package com.lei.network.loom.panama.core;

import java.lang.foreign.MemorySegment;

/**
 * <p>
 * PollerNode
 * </p>
 *
 * @author 伍磊
 */
public sealed interface PollerNode permits SentryPollerNode, ProtocolPollerNode {

    /**
     * 可读事件的处理回调
     * @param reserved
     * @param len
     */
    void onReadableEvent(MemorySegment reserved, int len);

    /**
     * 可写事件的处理回调
     */
    void onWritableEvent();

}
