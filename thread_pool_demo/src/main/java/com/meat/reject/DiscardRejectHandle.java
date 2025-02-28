package com.meat.reject;

import com.meat.ThreadPool;

/**
 * @author tchstart
 * @data 2025-02-28
 */
public class DiscardRejectHandle implements RejectHanle{
    @Override
    public void reject(Runnable rejectCommand, ThreadPool threadPool) {
        threadPool.blockingQueue.poll();
        threadPool.execute(rejectCommand);
    }
}
