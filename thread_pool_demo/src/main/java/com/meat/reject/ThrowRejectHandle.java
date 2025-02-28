package com.meat.reject;

import com.meat.ThreadPool;

/**
 * @author tchstart
 * @data 2025-02-28
 */
public class ThrowRejectHandle implements RejectHanle{
    @Override
    public void reject(Runnable rejectCommand, ThreadPool threadPool) {
        throw new RuntimeException("阻塞队列满了");
    }
}
