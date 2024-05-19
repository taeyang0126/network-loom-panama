package com.lei.network.loom.panama.core;

import com.lei.network.loom.panama.util.NativeUtil;

/**
 * <p>
 * PollerConfig
 * </p>
 *
 * @author 伍磊
 */
public final class PollerConfig {

    private int pollerCount = Math.max(NativeUtil.getCpuCores() >> 1, 4);

    public int getPollerCount() {
        return pollerCount;
    }

    public void setPollerCount(int pollerCount) {
        this.pollerCount = pollerCount;
    }
}
