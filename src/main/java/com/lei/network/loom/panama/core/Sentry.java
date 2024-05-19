package com.lei.network.loom.panama.core;

import java.lang.foreign.MemorySegment;

/**
 * <p>
 * Sentry
 * 代表连接认证阶段，即连接尚未完成建立的阶段
 * </p>
 *
 * @author 伍磊
 */
public interface Sentry {

    int onReadableEvent(MemorySegment reserved, int len);

    int onWritableEvent();

    Protocol toProtocol();

    void doClose();

}
