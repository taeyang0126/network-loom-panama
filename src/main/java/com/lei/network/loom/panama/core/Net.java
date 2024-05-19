package com.lei.network.loom.panama.core;

import com.lei.network.loom.panama.constant.Constants;
import com.lei.network.loom.panama.exception.ExceptionType;
import com.lei.network.loom.panama.exception.FrameworkException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.IntStream;

import static com.lei.network.loom.panama.constant.Constants.INITIAL;
import static com.lei.network.loom.panama.constant.Constants.RUNNING;

/**
 * <p>
 * Net
 * </p>
 *
 * @author 伍磊
 */
public class Net extends AbstractLifeCycle {

    private static final Logger log = LoggerFactory.getLogger(Net.class);

    private static final AtomicBoolean instanceFlag = new AtomicBoolean(false);
    private final State state = new State(INITIAL);

    private final Thread netThread;
    private final List<Poller> pollers;
    private final List<Writer> writers;

    public Net(NetConfig netConfig, PollerConfig pollerConfig, WriterConfig writerConfig) {
        if (netConfig == null || pollerConfig == null || writerConfig == null) {
            throw new NullPointerException();
        }

        // 类似于单例模式
        if (!instanceFlag.compareAndSet(false, true)) {
            throw new FrameworkException(ExceptionType.NETWORK, Constants.UNREACHED);
        }

        int pollerCount = pollerConfig.getPollerCount();
        if (pollerCount <= 0) {
            throw new FrameworkException(ExceptionType.NETWORK, "Poller instances cannot be zero");
        }
        int writerCount = writerConfig.getWriterCount();
        if (writerCount <= 0) {
            throw new FrameworkException(ExceptionType.NETWORK, "Writer instances cannot be zero");
        }

        this.pollers = IntStream.range(0, pollerCount).mapToObj(_ -> new Poller(pollerConfig)).toList();
        this.writers = IntStream.range(0, writerCount).mapToObj(_ -> new Writer(writerConfig)).toList();
        this.netThread = createNetThread(netConfig);
    }

    @Override
    protected void doInit() {
        try (Mutex _ = state.withMutex()) {

        }
    }

    @Override
    protected void doExit() throws InterruptedException {
        try (Mutex _ = state.withMutex()) {

        }
    }

    public void addListener(ListenerConfig listenerConfig) {
        try (Mutex _ = state.withMutex()) {
            int current = state.get();
            if (current > RUNNING) {
                throw new RuntimeException(Constants.UNREACHED);
            }
        }
    }

    private Thread createNetThread(NetConfig netConfig) {
        return Thread.ofPlatform().unstarted(() -> {

        });
    }
}
