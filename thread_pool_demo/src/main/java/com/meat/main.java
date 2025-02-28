package com.meat;

import com.meat.reject.ThrowRejectHandle;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

/**
 * @author tchstart
 * @data 2025-02-28
 */
public class main {
    public static void main(String[] args) {
        ThreadPool threadPool = new ThreadPool(2,4,1, TimeUnit.SECONDS,new ArrayBlockingQueue(2),new ThrowRejectHandle());

        // 建5个Task
        for(int i = 0 ; i < 5 ; i++){
            threadPool.execute(() -> {
                try {
                    Thread.sleep(10000);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                System.out.println(Thread.currentThread().getName());
            });
        }

        System.out.println("主线程没有被阻塞");
    }
}
