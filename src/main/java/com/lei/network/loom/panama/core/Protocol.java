package com.lei.network.loom.panama.core;

import java.lang.foreign.MemorySegment;

/**
 * <p>
 * Protocol
 * 表示连接建立完成，可以进行数据读写
 * </p>
 *
 * @author 伍磊
 */
public interface Protocol {

    int onReadableEvent(MemorySegment reserved, int len);

    int onWritableEvent();

    int doWrite(MemorySegment data, int len);

    void doShutdown();

    void doClose();
}
