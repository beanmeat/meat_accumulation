package com.meat;

import com.meat.reject.RejectHanle;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

/**
 * @author tchstart
 * @data 2025-02-28
 */
public class ThreadPool {

    public BlockingQueue<Runnable> blockingQueue = new LinkedBlockingQueue<>();

    private int corePoolSize = 10;
    private int maxSize = 16;
    private int timeout = 1;
    private TimeUnit timeUnit = TimeUnit.SECONDS;
    private RejectHanle rejectHanle;

    public ThreadPool(int corePoolSize, int maxSize, int timeout, TimeUnit timeUnit,BlockingQueue blockingQueue,RejectHanle rejectHanle) {
        this.corePoolSize = corePoolSize;
        this.maxSize = maxSize;
        this.timeout = timeout;
        this.timeUnit = timeUnit;
        this.blockingQueue = blockingQueue;
        this.rejectHanle = rejectHanle;
    }

    List<Thread> coreList = new ArrayList<>();
    List<Thread> supportList = new ArrayList<>();

    public void execute(Runnable command){
        if (coreList.size() < corePoolSize) {
            Thread thread = new CoreThread();
            coreList.add(thread);
            thread.start();
        }
        if (blockingQueue.offer(command)) {
            return;
        }
        if (coreList.size() + supportList.size() < maxSize) {
            Thread thread = new SupportThread();
            supportList.add(thread);
            thread.start();
        }

        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        if (!blockingQueue.offer(command)) {
           // 阻塞策略
            rejectHanle.reject(command,this);
        }

    }

    class CoreThread extends Thread {
        @Override
        public void run() {
            while(true){
                try {
                    System.out.println("core拿到");
                    Runnable command = blockingQueue.take();;
                    command.run();
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }

            }
        }
    }

    class SupportThread extends Thread {
        @Override
        public void run() {
            while(true){
                try {
                    Runnable command = blockingQueue.poll(timeout, timeUnit);
                    System.out.println("support拿到");
                    if(command == null){
                        break;
                    }
                    command.run();
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
            System.out.println(Thread.currentThread().getName() + "线程结束了！");
        }
    }
}
