package com.lei.network.loom.panama.buffer;

import com.lei.network.loom.panama.exception.ExceptionType;
import com.lei.network.loom.panama.exception.FrameworkException;
import com.lei.network.loom.panama.util.NativeUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.foreign.Arena;
import java.lang.foreign.MemorySegment;
import java.lang.foreign.ValueLayout;
import java.nio.charset.StandardCharsets;

import static com.lei.network.loom.panama.constant.Constants.BYTE_SIZE;
import static com.lei.network.loom.panama.constant.Constants.INT_SIZE;
import static com.lei.network.loom.panama.constant.Constants.LONG_SIZE;
import static com.lei.network.loom.panama.constant.Constants.NUT;
import static com.lei.network.loom.panama.constant.Constants.SHORT_SIZE;

/**
 * <p>
 * WriteBuffer
 * </p>
 *
 * @author 伍磊
 */
public final class WriteBuffer implements AutoCloseable {

    private static final Logger LOGGER = LoggerFactory.getLogger(WriteBuffer.class);

    private static final int DEFAULT_HEAP_BUFFER_SIZE = 32;
    private MemorySegment segment;
    private long size;
    private long writeIndex;
    private final WriteBufferPolicy policy;

    WriteBuffer(MemorySegment segment, WriteBufferPolicy policy) {
        this.segment = segment;
        this.size = segment.byteSize();
        this.writeIndex = 0;
        this.policy = policy;
    }

    public static WriteBuffer newDefaultWriteBuffer(Arena arena, long size) {
        MemorySegment memorySegment = arena.allocateArray(ValueLayout.JAVA_BYTE, size);
        return new WriteBuffer(memorySegment, new DefaultWriteBufferPolicy(arena));
    }

    public static WriteBuffer newFixedWriteBuffer(Arena arena, long size) {
        MemorySegment memorySegment = arena.allocateArray(ValueLayout.JAVA_BYTE, size);
        return new WriteBuffer(memorySegment, new FixedWriteBufferPolicy(arena));
    }

    public static WriteBuffer newReservedWriteBuffer(MemorySegment memorySegment) {
        return new WriteBuffer(memorySegment, new ReservedWriteBufferPolicy());
    }

    public static WriteBuffer newHeapWriteBuffer(int size) {
        byte[] data = new byte[size];
        return new WriteBuffer(MemorySegment.ofArray(data), new HeapWriteBufferPolicy(data));
    }

    public static WriteBuffer newHeapWriteBuffer() {
        return newHeapWriteBuffer(DEFAULT_HEAP_BUFFER_SIZE);
    }

    public long size() {
        return size;
    }

    public long writeIndex() {
        return writeIndex;
    }

    public void resize(long nextIndex) {
        if (nextIndex < 0) {
            throw new FrameworkException(ExceptionType.NATIVE, "Index overflow");
        } else if (nextIndex > size) {
            long oldSize = size();
            policy.resize(this, nextIndex);
            LOGGER.debug("[WriterBuffer]-[{}] resize success, oldSize:{}, newSize:{}", segment, oldSize, size());
        }
    }

    public void writeByte(byte b) {
        long nextIndex = writeIndex + BYTE_SIZE;
        resize(nextIndex);
        NativeUtil.setByte(segment, writeIndex, b);
        writeIndex = nextIndex;
    }

    public void writeBytes(byte[] b, int off, int len) {
        long nextIndex = writeIndex + b.length;
        resize(nextIndex);
        MemorySegment.copy(MemorySegment.ofArray(b), off, segment, writeIndex, len);
        writeIndex = nextIndex;
    }

    public void writeBytes(byte... bytes) {
        long nextIndex = writeIndex + bytes.length;
        resize(nextIndex);
        MemorySegment.copy(MemorySegment.ofArray(bytes), 0, segment, writeIndex, bytes.length);
        writeIndex = nextIndex;
    }

    public void writeShort(short value) {
        long nextIndex = writeIndex + SHORT_SIZE;
        resize(nextIndex);
        NativeUtil.setShort(segment, writeIndex, value);
        writeIndex = nextIndex;
    }

    public void writeInt(int i) {
        long nextIndex = writeIndex + INT_SIZE;
        resize(nextIndex);
        NativeUtil.setInt(segment, writeIndex, i);
        writeIndex = nextIndex;
    }

    public void writeLong(long l) {
        long nextIndex = writeIndex + LONG_SIZE;
        resize(nextIndex);
        NativeUtil.setLong(segment, writeIndex, l);
        writeIndex = nextIndex;
    }

    public void writeCStr(String str) {
        writeCStr(str, true);
    }

    /**
     * 写一个字符串，需要注意结尾需要加上 '\0'
     *
     * @param str         字符串
     * @param rollbackNut 若writeIndex前一个字符是'\0'，writeIndex是否需要往回一位 true-是 false-否
     */
    public void writeCStr(String str, boolean rollbackNut) {
        // 这里添加一个特殊的处理
        // 针对重复添加字符串的场景，需要判断 writeIndex 前一个字符是否是 '\0'，如果是，writeIndex 往前走一位
        if (rollbackNut && writeIndex > 0) {
            if (NativeUtil.getByte(segment, writeIndex - 1) == NUT) {
                writeIndex = writeIndex - 1;
            }
        }

        MemorySegment m = MemorySegment.ofArray(str.getBytes(StandardCharsets.UTF_8));
        long len = m.byteSize();
        // 加了 '\0'
        long nextIndex = writeIndex + len + BYTE_SIZE;
        resize(nextIndex);
        MemorySegment.copy(m, 0, segment, writeIndex, len);
        NativeUtil.setByte(segment, writeIndex + len, NUT);
        writeIndex = nextIndex;
    }

    public void writeSegment(MemorySegment memorySegment) {
        long len = memorySegment.byteSize();
        long nextIndex = writeIndex + len;
        resize(nextIndex);
        MemorySegment.copy(memorySegment, 0, segment, writeIndex, len);
        writeIndex = nextIndex;
    }

    public void setByte(long index, byte value) {
        if (index + BYTE_SIZE > writeIndex) {
            throw new RuntimeException("Index out of bound");
        }
        NativeUtil.setByte(segment, index, value);
    }

    public void setInt(long index, int value) {
        if (index + INT_SIZE > writeIndex) {
            throw new RuntimeException("Index out of bound");
        }
        NativeUtil.setInt(segment, index, value);
    }

    public void setLong(long index, long value) {
        if (index + LONG_SIZE > writeIndex) {
            throw new RuntimeException("Index out of bound");
        }
        NativeUtil.setLong(segment, index, value);
    }

    public MemorySegment content() {
        return writeIndex == size ? segment : segment.asSlice(0L, writeIndex);
    }

    /**
     * 指定的offset索引处截断内存并返回一个新的WriteBuffer，新的WriteBuffer可以继续沿用旧WriteBuffer中的policy对象而不受影响
     */
    public WriteBuffer truncate(long offset) {
        if (offset > writeIndex) {
            throw new FrameworkException(ExceptionType.NATIVE, "Truncate index overflow");
        }
        WriteBuffer w = new WriteBuffer(segment.asSlice(offset, size - offset), policy);
        w.writeIndex = writeIndex - offset;
        return w;
    }

    @Override
    public void close() {
        policy.close(this);
    }

    /**
     * 堆内存写入策略
     */
    static final class HeapWriteBufferPolicy implements WriteBufferPolicy {

        private byte[] data;

        public HeapWriteBufferPolicy(byte[] data) {
            this.data = data;
        }

        @Override
        public void resize(WriteBuffer writeBuffer, long nextIndex) {
            if (nextIndex > Integer.MAX_VALUE) {
                throw new FrameworkException(ExceptionType.NATIVE, "Heap writeBuffer size overflow");
            }

            int newLen = Math.max((int) nextIndex, data.length << 1);
            if (newLen < 0) {
                throw new FrameworkException(ExceptionType.NATIVE, "MemorySize overflow");
            }

            byte[] newData = new byte[newLen];
            System.arraycopy(data, 0, newData, 0, (int) writeBuffer.writeIndex);
            data = newData;
            writeBuffer.segment = MemorySegment.ofArray(newData);
            writeBuffer.size = newLen;
        }

        @Override
        public void close(WriteBuffer writeBuffer) {
            // No external close operation needed for heapWriteBuffer
        }
    }

    /**
     * 固定的写入 buffer， 不允许扩容
     *
     * @param arena
     */
    record FixedWriteBufferPolicy(Arena arena) implements WriteBufferPolicy {

        @Override
        public void resize(WriteBuffer writeBuffer, long nextIndex) {
            throw new FrameworkException(ExceptionType.NATIVE, "Current writeBuffer shouldn't be resized");
        }

        @Override
        public void close(WriteBuffer writeBuffer) {
            arena.close();
        }
    }

    /**
     * 堆外内存扩容策略
     * 所有的内存都由同一个固定的Arena进行释放，在关闭WriteBuffer时，只需要关闭该Arena即可释放掉所有的堆外内存
     */
    record DefaultWriteBufferPolicy(Arena arena) implements WriteBufferPolicy {

        @Override
        public void resize(WriteBuffer writeBuffer, long nextIndex) {
            long newLen = Math.max(nextIndex, writeBuffer.size() << 1);
            if (newLen < 0) {
                throw new FrameworkException(ExceptionType.NATIVE, "MemorySize overflow");
            }
            MemorySegment newSegment = arena.allocateArray(ValueLayout.JAVA_BYTE, newLen);
            MemorySegment.copy(writeBuffer.segment, 0, newSegment, 0, writeBuffer.writeIndex);
            writeBuffer.segment = newSegment;
            writeBuffer.size = newLen;
        }

        @Override
        public void close(WriteBuffer writeBuffer) {
            arena.close();
        }
    }

    /**
     * 此种策略会接收一个 MemorySegment 对象，这个对象不在 WriteBuffer 中产生，所以开发者需要自己管理 MemorySegment 内存
     *
     * 在ReservedWriteBufferPolicy策略中，情况有所不同，
     * ReservedWriteBufferPolicy会认为初始化的内存是一块预留的内存，
     * 它的生命周期并不在WriteBuffer的管辖范围之内，开发者应该自行管理预留内存的生命周期，
     * ReservedWriteBufferPolicy中的Arena对象只负责管理扩容内存的申请与释放即可
     */
    static final class ReservedWriteBufferPolicy implements WriteBufferPolicy {

        private Arena arena = null;

        @Override
        public void resize(WriteBuffer writeBuffer, long nextIndex) {
            long newLen = Math.max(nextIndex, writeBuffer.size() << 1);
            if (newLen < 0) {
                throw new FrameworkException(ExceptionType.NATIVE, "MemorySize overflow");
            }
            if (arena == null) {
                arena = Arena.ofConfined();
            }

            MemorySegment newSegment = arena.allocateArray(ValueLayout.JAVA_BYTE, newLen);
            MemorySegment.copy(writeBuffer.segment, 0, newSegment, 0, writeBuffer.writeIndex);
            writeBuffer.segment = newSegment;
            writeBuffer.size = newLen;
        }

        @Override
        public void close(WriteBuffer writeBuffer) {
            if (arena != null) {
                arena.close();
            }
        }
    }
}
