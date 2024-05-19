package com.lei.network.loom.panama.library;

import com.lei.network.loom.panama.constant.Constants;
import com.lei.network.loom.panama.exception.ExceptionType;
import com.lei.network.loom.panama.exception.FrameworkException;
import com.lei.network.loom.panama.util.NativeUtil;

import java.lang.foreign.MemorySegment;

/**
 * 多路复用类
 * 针对Windows，Linux， macOS三个操作系统而言，都可以统一的使用Mux对象对其进行建模，
 * 虽然我们实际上只会使用到Mux对象中的某一个成员，其它的成员会被置为空，这很类似于C语言中联合体的概念，
 * 但在Java中并没有联合体相关的实现，因此我们直接将其定义为一个Record类，在具体操作系统的实现中直接使用其对应的参数即可
 */
public record Mux(
        // windows多路复用是指针类型
        MemorySegment winHandle,
        // linux下是int类型的普通文件描述符
        int epfd,
        // macos下是int类型的普通文件描述符
        int kqfd
) {

    public static Mux win(MemorySegment winHandle) {
        return new Mux(winHandle, Integer.MIN_VALUE, Integer.MIN_VALUE);
    }

    public static Mux linux(int epfd) {
        return new Mux(NativeUtil.NULL_POINTER, epfd, Integer.MIN_VALUE);
    }

    public static Mux mac(int epfd) {
        return new Mux(NativeUtil.NULL_POINTER, Integer.MIN_VALUE, epfd);
    }

    @Override
    public String toString() {
        if(winHandle != NativeUtil.NULL_POINTER) {
            return String.valueOf(winHandle.address());
        }else if(epfd != Integer.MIN_VALUE) {
            return String.valueOf(epfd);
        }else if(kqfd != Integer.MIN_VALUE) {
            return String.valueOf(kqfd);
        }else {
            throw new FrameworkException(ExceptionType.NETWORK, Constants.UNREACHED);
        }
    }
}
