package com.meat.distributedlock.annotation;

import java.lang.annotation.*;

/**
 * DistributedLock
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD})
@Documented
public @interface DistributedLock {

    /**
     * 如果字符串中存在{1}，{2}等占位符
     * 则程序会获取对应方法中的参数顺序值进行填充
     * 获取不到则不处理
     */
    String key() default "distributedLock";

    /**
     * 加锁失败，是否抛错，默认 false，不抛错
     */
    boolean lockFail() default false;

    /**
     * 加锁失败，抛错的提示信息
     */
    String failMessage() default "";

    long expiredTime() default 30L;

    int maxToRenewNum() default 30;

}