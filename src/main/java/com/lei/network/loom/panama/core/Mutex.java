package com.lei.network.loom.panama.core;

import com.lei.network.loom.panama.constant.Constants;
import com.lei.network.loom.panama.exception.ExceptionType;
import com.lei.network.loom.panama.exception.FrameworkException;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public record Mutex(Lock lock) implements AutoCloseable {

    public Mutex {
        // 构造后的校验
        if (lock == null) {
            throw new FrameworkException(ExceptionType.CONTEXT, Constants.UNREACHED);
        }
    }

    public Mutex() {
        this(new ReentrantLock());
    }

    public Mutex acquire() {
        lock.lock();
        return this;
    }

    @Override
    public void close() {
        lock.unlock();
    }
}
