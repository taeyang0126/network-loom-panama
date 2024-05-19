package com.lei.network.loom.panama.buffer;

import com.lei.network.loom.panama.exception.ExceptionType;
import com.lei.network.loom.panama.exception.FrameworkException;
import com.lei.network.loom.panama.util.NativeUtil;

import java.lang.foreign.MemorySegment;
import java.lang.foreign.ValueLayout;
import java.nio.charset.StandardCharsets;

import static com.lei.network.loom.panama.constant.Constants.EMPTY_BYTES;
import static com.lei.network.loom.panama.constant.Constants.NUT;

/**
 * <p>
 * 只读内存
 * </p>
 *
 * @author 伍磊
 */
public final class ReadBuffer {

    private final MemorySegment segment;
    private final long size;
    private long readIndex;

    public ReadBuffer(MemorySegment memorySegment) {
        this.segment = memorySegment;
        this.size = memorySegment.byteSize();
        this.readIndex = 0L;
    }

    public long size() {
        return size;
    }

    public long readIndex() {
        return readIndex;
    }

    public void setReadIndex(long index) {
        if (index < 0 || index >= size) {
            throw new FrameworkException(ExceptionType.NATIVE, "ReadIndex out of bound");
        }
        readIndex = index;
    }

    public byte readByte() {
        long nextIndex = readIndex + 1;
        if (nextIndex > size) {
            throw new FrameworkException(ExceptionType.NATIVE, "read index overflow");
        }
        byte b = NativeUtil.getByte(segment, readIndex);
        readIndex = nextIndex;
        return b;
    }

    public byte[] readBytes(int count) {
        long nextIndex = readIndex + count;
        if (nextIndex > size) {
            throw new FrameworkException(ExceptionType.NATIVE, "read index overflow");
        }
        // 切片返回一个新的内存区域
        byte[] result = segment.asSlice(readIndex, count).toArray(ValueLayout.JAVA_BYTE);
        readIndex = nextIndex;
        return result;
    }

    public short readShort() {
        long nextIndex = readIndex + ValueLayout.JAVA_SHORT.byteSize();
        if (nextIndex > size) {
            throw new FrameworkException(ExceptionType.NATIVE, "read index overflow");
        }
        short result = NativeUtil.getShort(segment, readIndex);
        readIndex = nextIndex;
        return result;
    }

    public int readInt() {
        long nextIndex = readIndex + ValueLayout.JAVA_INT.byteSize();
        if (nextIndex > size) {
            throw new FrameworkException(ExceptionType.NATIVE, "read index overflow");
        }
        int result = NativeUtil.getInt(segment, readIndex);
        readIndex = nextIndex;
        return result;
    }

    public long readLong() {
        long nextIndex = readIndex + ValueLayout.JAVA_LONG.byteSize();
        if (nextIndex > size) {
            throw new FrameworkException(ExceptionType.NATIVE, "read index overflow");
        }
        long result = NativeUtil.getLong(segment, readIndex);
        readIndex = nextIndex;
        return result;
    }

    public MemorySegment readSegment(long count) {
        long nextIndex = readIndex + count;
        if(nextIndex > size) {
            throw new FrameworkException(ExceptionType.NATIVE, "read index overflow");
        }
        MemorySegment result = segment.asSlice(readIndex, count);
        readIndex = nextIndex;
        return result;
    }

    /**
     * 获取堆内存中的 {@link MemorySegment} 对象，如果是堆外的，则进行拷贝
     */
    public MemorySegment readHeapSegment(long count) {
        MemorySegment m = readSegment(count);
        if (m.isNative()) {
            long len = m.byteSize();
            byte[] bytes = new byte[(int) len];
            MemorySegment h = MemorySegment.ofArray(bytes);
            MemorySegment.copy(m, 0, h, 0, len);
            return h;
        }
        return m;
    }

    /**
     * 读取字节直到某个分隔符
     */
    public byte[] readUntil(byte... separators) {
        for (long cur = readIndex; cur <= size - separators.length; cur++) {
            if (NativeUtil.matches(segment, cur, separators)) {
                byte[] result = cur == readIndex ? EMPTY_BYTES : segment.asSlice(readIndex, cur - readIndex).toArray(ValueLayout.JAVA_BYTE);
                readIndex = cur + separators.length;
                return result;
            }
        }
        return null;
    }

    public String readCStr() {
        byte[] bytes = readUntil(NUT);
        if(bytes == null || bytes.length == 0) {
            return null;
        }
        return new String(bytes, StandardCharsets.UTF_8);
    }

}
