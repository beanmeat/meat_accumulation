package com.meat.reject;

import com.meat.ThreadPool;

/**
 * @author tchstart
 * @data 2025-02-28
 */
public interface RejectHanle {

    void reject(Runnable rejectCommand, ThreadPool threadPool);
}
