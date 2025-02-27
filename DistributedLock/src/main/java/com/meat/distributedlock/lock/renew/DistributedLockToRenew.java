package com.meat.distributedlock.lock.renew;

import com.alibaba.fastjson.JSON;
import com.meat.distributedlock.lock.DistributedLockTask;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;

@Slf4j
@Component
public class DistributedLockToRenew {

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    /**
     * 这把锁，用来对 taskList 进行上锁，使其添加、迭代删除时只能串行
     */
    private ReentrantLock taskListLock = new ReentrantLock();

    public static final List<DistributedLockTask> taskList = new ArrayList<>();

    private ScheduledExecutorService taskExecutorService;

    {
        // Runtime.getRuntime().availableProcessors()) 返回可用处理器的Java虚拟机的数量
        taskExecutorService = Executors.newScheduledThreadPool(Runtime.getRuntime().availableProcessors());

        taskExecutorService.scheduleAtFixedRate(() -> {
            try {
                // 原始版本
                // scanningTaskV1();
                // 优化版本
                scanningTaskV2();
            } catch (Exception e) {
                //错误处理
                log.error("执行扫描Task逻辑出错：", e);
            }

        }, 1, 4, TimeUnit.SECONDS);

    }

    /**
     * 统一对外添加任务方法，加锁
     * @param task
     */
    public void addTask(DistributedLockTask task) {
        taskListLock.lock();
        try {
            taskList.add(task);
        }finally {
            taskListLock.unlock();
        }
    }

    private void scanningTaskV2() {
        if (CollectionUtils.isEmpty(taskList)) {
            return;
        }

        // 需要删除的任务，暂存这个集合中
        List<DistributedLockTask> removeTask = new ArrayList<>();

        // 获取任务副本
        List<DistributedLockTask> copyTaskList = new ArrayList<>(taskList);

        for (DistributedLockTask task : copyTaskList) {
            try {
                // 判断 Redis 中是否存在 key
                if (Boolean.FALSE.equals(redisTemplate.hasKey(task.getKey()))) {
                    removeTask.add(task);
                    continue;
                }

                // 判断是否达到最大续约次数
                if (Boolean.TRUE.equals(task.isMaxToRenewNum(null))) {
                    // 把耗时任务中断，排除业务为何执行如此之久
                    task.getThread().interrupt();
                    removeTask.add(task);
                    continue;
                }

                // 是否到达续约时间
                if (Boolean.FALSE.equals(task.isToRenewTime(null))) {
                    continue;
                }

                log.info("开始续约任务：key:{}", task.getKey());
                // 开始续约
                redisTemplate.expire(task.getKey(), task.getExpiredTime(), TimeUnit.SECONDS);
                task.setNewToRenewNum(task.getNewToRenewNum() + 1);
                task.setNewUpdatedTime(LocalDateTime.now());
            } catch (Exception e) {
                //错误处理
                log.error("处理任务出错：{}，", JSON.toJSONString(task), e);
            }
        }

        if (CollectionUtils.isEmpty(removeTask)) {
            return;
        }

        // 加锁，删除 taskList 中需要移除的任务
        taskListLock.lock();
        try {
            taskList.removeAll(removeTask);
        }finally {
            taskListLock.unlock();
        }
    }

    private void scanningTaskV1() {
        if (CollectionUtils.isEmpty(taskList)) {
            return;
        }

        Iterator<DistributedLockTask> iterator = taskList.iterator();
        while (iterator.hasNext()) {
            DistributedLockTask task = iterator.next();
            try {
                // 判断 Redis 中是否存在 key
                if (Boolean.FALSE.equals(redisTemplate.hasKey(task.getKey()))) {
                    iterator.remove();
                    continue;
                }

                // 判断是否达到最大续约次数
                if (Boolean.TRUE.equals(task.isMaxToRenewNum(null))) {
                    // 把耗时任务中断，排除业务为何执行如此之久
                    task.getThread().interrupt();
                    iterator.remove();
                    continue;
                }

                // 是否到达续约时间
                if (Boolean.FALSE.equals(task.isToRenewTime(null))) {
                    continue;
                }

                log.info("开始续约任务：key:{}", task.getKey());
                // 开始续约
                redisTemplate.expire(task.getKey(), task.getExpiredTime(), TimeUnit.SECONDS);
                task.setNewToRenewNum(task.getNewToRenewNum() + 1);
                task.setNewUpdatedTime(LocalDateTime.now());
            } catch (Exception e) {
                //错误处理
                log.error("处理任务出错：{}，", JSON.toJSONString(task), e);
            }

        }

    }
}