package com.lei.network.loom.panama.handler;

/**
 * <p>
 * Handler
 * </p>
 *
 * @author 伍磊
 */
public interface Handler {

    /**
     * 触发连接成功建立事件
     * @param channel   channel
     */
    void onConnected(Channel channel);

    /**
     * decode 后接收到的对象 data，此方法来用实现具体的业务逻辑
     * @param channel   channel
     * @param data      读取的对象
     */
    void onRecv(Channel channel, Object data);

    /**
     * 连接关闭前的释放动作
     * 可配置在onShutdown()方法中去发送一条消息显式通知对方我即将下线，方便对端释放相关资源，简化业务场景
     * @param channel
     */
    void onShutdown(Channel channel);

    /**
     * onRemoved()方法会在TCP连接已经彻底关闭后触发
     * 若业务方认为连接不应该在此刻断开，可以在这个方法体中发起重连
     * @param channel
     */
    void onRemoved(Channel channel);

}
