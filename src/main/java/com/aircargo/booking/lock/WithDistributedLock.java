package com.aircargo.booking.lock;

import java.lang.annotation.*;
import java.util.concurrent.TimeUnit;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface WithDistributedLock {
    String key();
    long waitTime() default 5;               // time to wait for the lock
    long leaseTime() default 15;             // auto-release
    TimeUnit unit() default TimeUnit.SECONDS;
}
