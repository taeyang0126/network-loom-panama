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
 * Writer
 * </p>
 *
 * @author 伍磊
 */
public final class Writer {

    private static final Logger LOGGER = LoggerFactory.getLogger(Writer.class);
    private static final AtomicInteger counter = new AtomicInteger(0);
    private final Thread writerThread;
    private final Queue<WriterTask> writerTaskQueue = new MpscUnboundedAtomicArrayQueue<>(1024);

    public Writer(WriterConfig writerConfig) {
        this.writerThread = createWriterThread(writerConfig);
    }

    public Thread thread() {
        return writerThread;
    }

    public void submit(WriterTask writerTask) {
        if(writerTask == null || !writerTaskQueue.offer(writerTask)) {
            throw new FrameworkException(ExceptionType.NETWORK, Constants.UNREACHED);
        }
    }

    private Thread createWriterThread(WriterConfig writerConfig) {
        int sequence = counter.getAndIncrement();
        return Thread.ofPlatform().name(STR."writer-\{sequence}").unstarted(() -> {

        });
    }
}
