package com.lei.network.loom.panama.coder;

import com.lei.network.loom.panama.buffer.WriteBuffer;

/**
 * <p>
 * Encoder
 * </p>
 *
 * @author 伍磊
 */
@FunctionalInterface
public interface Encoder {

    /**
     * 要将该Object对象，采用网络协议所规定的格式，序列化成字节流后，写入至WriteBuffer中
     */
    void encode(WriteBuffer writeBuffer, Object o);

}
