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

    // 用于表示在程序流程中，完全不应该触及的分支
    String UNREACHED = "Shouldn't be reached";

    // 表示C风格字符串的结尾标志
    byte NUT = (byte) '\0';

    // 作为默认情况下的空byte数组使用，因为会在未查找到任何字节数据的情况下，会经常的使用到该结构，没有必要每次都新建一个空的byte数组来增加GC的负担
    byte[] EMPTY_BYTES = new byte[0];

    long BYTE_SIZE = 1;
    long SHORT_SIZE = ValueLayout.JAVA_SHORT.byteSize();
    long INT_SIZE = ValueLayout.JAVA_INT.byteSize();
    long LONG_SIZE = ValueLayout.JAVA_LONG.byteSize();
    long FLOAT_SIZE = ValueLayout.JAVA_FLOAT.byteSize();
    long DOUBLE_SIZE = ValueLayout.JAVA_DOUBLE.byteSize();

}
