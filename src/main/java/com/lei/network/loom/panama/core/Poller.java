package com.lei.network.loom.panama.core;

import com.lei.network.loom.panama.constant.Constants;
import com.lei.network.loom.panama.exception.ExceptionType;
import com.lei.network.loom.panama.exception.FrameworkException;
import org.jctools.queues.atomic.MpscUnboundedAtomicArrayQueue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Queue;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * <p>
 * Poller
 * </p>
 *
 * @author 伍磊
 */
public final class Poller {

    private static final Logger log = LoggerFactory.getLogger(Poller.class);
    private static final AtomicInteger counter = new AtomicInteger(0);

    private final Thread pollerThread;
    private final Queue<PollerTask> readerTaskQueue = new MpscUnboundedAtomicArrayQueue<>(1024);

    public Poller(PollerConfig pollerConfig) {
        this.pollerThread = createPollerThread(pollerConfig);
    }

    public Thread thread() {
        return pollerThread;
    }

    public void submit(PollerTask pollerTask) {
        if (pollerTask == null || !readerTaskQueue.offer(pollerTask)) {
            throw new FrameworkException(ExceptionType.NETWORK, Constants.UNREACHED);
        }
    }


    private Thread createPollerThread(PollerConfig pollerConfig) {
        int sequence = counter.getAndIncrement();
        return Thread.ofPlatform().name(STR."poller-\{sequence}").unstarted(() -> {
            for (; ; ) {
                // 多路复用监听

                // 队列事件处理
            }
        });
    }


}
