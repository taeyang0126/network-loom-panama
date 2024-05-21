package com.lei.network.loom.panama.core;

import com.lei.network.loom.panama.constant.Constants;
import com.lei.network.loom.panama.exception.ExceptionType;
import com.lei.network.loom.panama.exception.FrameworkException;
import com.lei.network.loom.panama.handler.Channel;
import com.lei.network.loom.panama.util.IntMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.foreign.MemorySegment;

/**
 * <p>
 * ProtocolPollerNode
 * </p>
 *
 * @author 伍磊
 */
public final class ProtocolPollerNode implements PollerNode {

    private static final Logger log = LoggerFactory.getLogger(ProtocolPollerNode.class);

    private final IntMap<PollerNode> nodeMap;

    private final Channel channel;

    private final Protocol protocol;

    private final State channelState;

    public ProtocolPollerNode(IntMap<PollerNode> nodeMap, Channel channel, Protocol protocol, State channelState) {
        this.nodeMap = nodeMap;
        this.channel = channel;
        this.protocol = protocol;
        this.channelState = channelState;
    }
    @Override
    public void onReadableEvent(MemorySegment reserved, int len) {
        int r;
        try{
            r = protocol.onReadableEvent(reserved, len);
        }catch (FrameworkException e) {
            log.error("Exception thrown in protocolPollerNode when invoking onReadableEvent()", e);
            close();
            return ;
        }
        if(r >= 0) {
            handleReceived(reserved, len, r);
        }else {
            handleEvent(r);
        }
    }

    @Override
    public void onWritableEvent() {
        int r;
        try{
            r = protocol.onWritableEvent();
        }catch (FrameworkException e) {
            log.error("Exception thrown in protocolPollerNode when invoking onWritableEvent()", e);
            close();
            return ;
        }
        if(r < 0) {
            handleEvent(r);
        }
    }

    private void handleEvent(int r) {
        if(r == Constants.NET_W || r == Constants.NET_R || r == Constants.NET_RW) {
            ctl(r);
        }else if(r != Constants.NET_IGNORED) {
            throw new FrameworkException(ExceptionType.NETWORK, Constants.UNREACHED);
        }
    }

    private void ctl(int expected) {
        // TODO 修改多路复用的状态
    }

    private void handleReceived(MemorySegment segment, int len, int received) {
        // TODO 处理接收到的数据
    }

    private void close() {
        if(nodeMap.remove(channel.socket().intValue(), this)) {
            // TODO 释放相关所有资源，关闭连接
        }
    }

    private void closeProtocol() {
        try{
            protocol.doClose();
        }catch (RuntimeException e) {
            log.error("Failed to close protocol from poller", e);
        }
    }
}
