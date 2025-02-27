package com.meat.distributedlock.aop;

import com.meat.distributedlock.annotation.DistributedLock;
import com.meat.distributedlock.lock.DistributedLockTask;
import com.meat.distributedlock.lock.renew.DistributedLockToRenew;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

/**
 * @author tchstart
 * @data 2025-02-27
 */
@Component
@Aspect
@AllArgsConstructor
@Slf4j
public class DistributedLockAspect {

        private final RedisTemplate<String, Object> redisTemplate;

        private static final String defaultKey = "distributed:lock:aspect:";

        @Pointcut("@annotation(com.meat.distributedlock.annotation.DistributedLock)")
        public void distributedLock() {
        }


        @Around("distributedLock()")
        public void invoke(ProceedingJoinPoint joinPoint) throws Exception {
            String key = getKey(joinPoint);

            Boolean absent = redisTemplate.opsForValue().setIfAbsent(key, Thread.currentThread().getId() + "", 5, TimeUnit.MINUTES);

            if (Boolean.FALSE.equals(absent)) {
                DistributedLock distributedLock = getDistributedLockAnnotation(getMethod(joinPoint));
                if (Boolean.FALSE.equals(distributedLock.lockFail())) {
                    return;
                }
                throw new RuntimeException(distributedLock.failMessage());
            }

            try {
                // 生成任务
                addTask(key, joinPoint);

                // 执行业务
                joinPoint.proceed();
            } catch (Throwable e) {
                log.error("业务执行出错！");
            } finally {
                // 资源处理
                String value = (String) redisTemplate.opsForValue().get(key);
                if (value.equals(Thread.currentThread().getId() + "")) {
                    // 只删除自己线程加的锁，防止误删他人的锁，导致他人业务逻辑执行多次
                    // 如果删除出问题，就是key没有被删除掉
                    redisTemplate.delete(key);
                }
            }
        }

        private void addTask(String key, ProceedingJoinPoint joinPoint) {
            DistributedLock annotation = getDistributedLockAnnotation(getMethod(joinPoint));
            DistributedLockTask task = new DistributedLockTask();
            task.setKey(key);
            task.setExpiredTime(annotation.expiredTime());
            task.setMaxToRenewNum(annotation.maxToRenewNum());
            task.setNewToRenewNum(0);
            task.setNewUpdatedTime(LocalDateTime.now());
            task.setThread(Thread.currentThread());
            DistributedLockToRenew.taskList.add(task);
        }

        private String getKey(ProceedingJoinPoint joinPoint) {

            Method method = getMethod(joinPoint);
            if (Objects.isNull(method)) {
                return defaultKey + "default-key";
            }

            DistributedLock annotation = getDistributedLockAnnotation(method);
            if (Objects.isNull(annotation)) {
                return defaultKey + "default-key";
            }

            String key = annotation.key();
            String keyPrefix = defaultKey + method.getClass().getName() + ":" + method.getName();

            if (Objects.isNull(key)) {
                return keyPrefix;
            }


            return keyPrefix + ":" + replenishKey(key, joinPoint.getArgs());
        }

        private String replenishKey(String key, Object[] args) {
            if (Objects.isNull(args) || args.length == 0) {
                return key;
            }

            for (int i = 0; i < args.length; i++) {
                key = key.replace(String.format("{%s}", i + 1), args[i].toString());
            }
            return key;
        }

        private DistributedLock getDistributedLockAnnotation(Method method) {

            DistributedLock annotation = method.getAnnotation(DistributedLock.class);
            if (Objects.isNull(annotation)) {
                return null;
            }

            return annotation;
        }

        private Method getMethod(ProceedingJoinPoint joinPoint) {
            Method method = null;
            try {
                String methodName = joinPoint.getSignature().getName();
                method = joinPoint.getTarget().getClass().getMethod(methodName, ((MethodSignature) joinPoint.getSignature()).getParameterTypes());
            } catch (NoSuchMethodException e) {
                log.error("获取注解key失败，使用默认key");
                return null;
            }

            return method;
        }
}
