package com.lei.network.loom.panama.buffer;

/**
 * <p>
 * WriteBufferPolicy
 * </p>
 *
 * @author 伍磊
 */
public interface WriteBufferPolicy {

    /**
     * 扩容
     * @param writeBuffer   writeBuffer
     * @param nextIndex 该次写入后的writeIndex值，我们需要将当前WriteBuffer扩容到至少可容纳nextIndex的数据大小
     */
    void resize(WriteBuffer writeBuffer, long nextIndex);

    void close(WriteBuffer writeBuffer);
}
