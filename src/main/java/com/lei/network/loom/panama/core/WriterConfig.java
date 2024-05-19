package com.lei.network.loom.panama.core;

import com.lei.network.loom.panama.util.NativeUtil;

/**
 * <p>
 * WriterConfig
 * </p>
 *
 * @author 伍磊
 */
public final class WriterConfig {

    private int writerCount = Math.max(NativeUtil.getCpuCores() >> 1, 4);

    public int getWriterCount() {
        return writerCount;
    }

    public void setWriterCount(int writerCount) {
        this.writerCount = writerCount;
    }
}
