package com.lei.network.loom.panama.constant;

import java.lang.foreign.ValueLayout;

/**
 * <p>
 * Constants
 * </p>
 *
 * @author 伍磊
 */
public interface Constants {

    String TENET = "tenet";

    // 用于表示在程序流程中，完全不应该触及的分支
    String UNREACHED = "Shouldn't be reached";

    // 表示C风格字符串的结尾标志
    byte NUT = (byte) '\0';

    // 作为默认情况下的空byte数组使用，因为会在未查找到任何字节数据的情况下，会经常的使用到该结构，没有必要每次都新建一个空的byte数组来增加GC的负担
    byte[] EMPTY_BYTES = new byte[0];

    // 常用类型size大小
    long BYTE_SIZE = 1;
    long SHORT_SIZE = ValueLayout.JAVA_SHORT.byteSize();
    long INT_SIZE = ValueLayout.JAVA_INT.byteSize();
    long LONG_SIZE = ValueLayout.JAVA_LONG.byteSize();
    long FLOAT_SIZE = ValueLayout.JAVA_FLOAT.byteSize();
    long DOUBLE_SIZE = ValueLayout.JAVA_DOUBLE.byteSize();

    // 状态
    int INITIAL = 0;

    int STARTING = 1;

    int RUNNING = 2;

    int CLOSING = 3;

    int STOPPED = 4;

    // 大小
    int KB = 1024;
    int MB = 1024 * KB;
    int GB = 1024 * MB;

    /**
     * 网络
     * Integer.MIN_VALUE是一个非常特别的负数，他在二进制的形式里，除了第一位符号位为1之外，其他的所有位均为0
     * 这使得我们只要将特定位移的值与之进行或运算，就可以得到一个负数形式的，可用于位运算的常量
     * 引入位运算是为了在后面判断具体的状态位时，可以更加的方便，而将常量转化为负数的原因是避免和recv()函数和send()函数的返回值发生冲突
     */
    int NET_NONE = Integer.MIN_VALUE;
    // 代表当前的状态不需要进行变更，可以将该返回值直接无视
    int NET_IGNORED = Integer.MIN_VALUE | 1;
    // 代表当前Sentry可以被升级为Protocol
    int NET_UPDATE = Integer.MIN_VALUE | (1 << 2);
    int NET_W = Integer.MIN_VALUE | (1 << 4); // register write only
    // 示如果当前没有注册该事件，则添加注册，否则保持原状
    int NET_PW = Integer.MIN_VALUE | (1 << 6); // register write if possible
    int NET_R = Integer.MIN_VALUE | (1 << 8); // register read only
    int NET_PR = Integer.MIN_VALUE | (1 << 10); // register read if possible
    int NET_RW = NET_R | NET_W; // register read and write
    // 表示除了可读事件和可写事件以外的其他事件，比如epoll中默认注册的EPOLL_ERR和EPOLL_HUP等
    int NET_OTHER = Integer.MIN_VALUE | (1 << 20);

}
