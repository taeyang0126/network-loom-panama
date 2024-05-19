package com.lei.network.loom.panama.core;

import java.util.concurrent.atomic.AtomicInteger;

import static com.lei.network.loom.panama.constant.Constants.INITIAL;
import static com.lei.network.loom.panama.constant.Constants.RUNNING;
import static com.lei.network.loom.panama.constant.Constants.STOPPED;
import static com.lei.network.loom.panama.constant.Constants.UNREACHED;

/**
 * <p>
 * AbstractLifeCycle
 * </p>
 *
 * @author 伍磊
 */
public abstract class AbstractLifeCycle implements LifeCycle {

    private final AtomicInteger state = new AtomicInteger(INITIAL);

    protected abstract void doInit();

    protected abstract void doExit() throws InterruptedException;

    @Override
    public void init() {
        if (!state.compareAndSet(INITIAL, RUNNING)) {
            throw new RuntimeException(UNREACHED);
        }
        doInit();
    }

    @Override
    public void exit() throws InterruptedException {
        if (!state.compareAndSet(RUNNING, STOPPED)) {
            throw new RuntimeException(UNREACHED);
        }
        doExit();
    }

}
