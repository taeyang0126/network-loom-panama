package com.lei.network.loom.panama.core;

import java.lang.foreign.MemorySegment;

/**
 * <p>
 * SentryPollerNode
 * </p>
 *
 * @author 伍磊
 */
public final class SentryPollerNode implements PollerNode {
    @Override
    public void onReadableEvent(MemorySegment reserved, int len) {

    }

    @Override
    public void onWritableEvent() {

    }
}
