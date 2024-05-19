package com.lei.network.loom.panama.library;

/**
 * <p>
 * SocketConfig
 * </p>
 *
 * @author 伍磊
 */
public final class SocketConfig {

    private boolean reuseAddr = true;

    private boolean keepAlive = false;

    private boolean tcpNoDelay = true;

    // 针对于IPV4协议栈和IPV6协议栈的一个设定，我们在设置一个服务端socket时，是可以选择将其绑定在一个IPV4地址上还是IPV6地址上
    // PV6地址是提供了对IPV4地址的兼容的，我们选取的Windows，Linux，macOS这三个操作系统都支持在开启IPV6服务端socket监听连接时，同时支持IPV4连接和IPV6连接，这种兼容性机制使得在过渡期间可以逐步采用IPv6，而不会完全割裂与仍然使用IPv4的网络的连接
    // 如果我们强制设定了一个IPV6_V6ONLY的选项，那么在IPV6地址监听的服务端socket将不允许建立IPV4协议发起的连接，我们在配置类中默认将其设定为false，也就是默认在使用IPV6的情况下，开启IPV6与IPV4兼容的协议栈
    private boolean ipv6Only = false;

    public boolean isReuseAddr() {
        return reuseAddr;
    }

    public void setReuseAddr(boolean reuseAddr) {
        this.reuseAddr = reuseAddr;
    }

    public boolean isKeepAlive() {
        return keepAlive;
    }

    public void setKeepAlive(boolean keepAlive) {
        this.keepAlive = keepAlive;
    }

    public boolean isTcpNoDelay() {
        return tcpNoDelay;
    }

    public void setTcpNoDelay(boolean tcpNoDelay) {
        this.tcpNoDelay = tcpNoDelay;
    }

    public boolean isIpv6Only() {
        return ipv6Only;
    }

    public void setIpv6Only(boolean ipv6Only) {
        this.ipv6Only = ipv6Only;
    }
}
