package com.lei.network.loom.panama.library;

import com.lei.network.loom.panama.util.NativeUtil;

/**
 * 表示操作系统中的socket对象 <br/>
 * windows中是64位，linux是int类型，所以这里用了两个参数表示
 */
public record Socket(
        long longValue,
        int intValue
) {

    public Socket(int socket) {
        this(socket, socket);
    }

    public Socket(long socket) {
        this(socket, NativeUtil.castInt(socket));
    }

    @Override
    public int hashCode() {
        // 操作系统创建的socket值本身就是天然的优秀哈希散布，因为它们不会产生重复
        // 相比自动生成的哈希函数而言会具备更佳的性能
        return intValue;
    }
}
