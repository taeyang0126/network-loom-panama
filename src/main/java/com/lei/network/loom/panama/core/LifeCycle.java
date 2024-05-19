package com.lei.network.loom.panama.core;

/**
 * <p>
 * LifeCycle
 * </p>
 *
 * @author 伍磊
 */
public interface LifeCycle {

    void init();

    void exit() throws InterruptedException;

}
