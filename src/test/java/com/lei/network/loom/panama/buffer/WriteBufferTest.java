package com.lei.network.loom.panama.buffer;

import com.lei.network.loom.panama.util.NativeUtil;
import org.junit.jupiter.api.Test;

import java.lang.foreign.Arena;
import java.lang.foreign.MemorySegment;
import java.lang.foreign.ValueLayout;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * <p>
 * WriteBufferTest
 * </p>
 *
 * @author 伍磊
 */
public class WriteBufferTest {

    // 测试堆内存
    @Test
    public void test_HeapWriteBufferPolicy() {
        try(Arena arena = Arena.ofConfined()) {
            // 堆外分配4个byte的内存
            MemorySegment memorySegment = arena.allocate(4);
            // 写入一个int类型的数字
            int num = 2024;
            NativeUtil.setInt(memorySegment, 0L, num);

            // 获取到底层的字节数组
            byte[] data = memorySegment.toArray(ValueLayout.JAVA_BYTE);
            assertNotNull(data);
            assertEquals(4, data.length);
            assertTrue(memorySegment.isNative());

            // 封装policy
            WriteBufferPolicy writeBufferPolicy = new WriteBuffer.HeapWriteBufferPolicy(data);

            try (WriteBuffer writeBuffer = new WriteBuffer(memorySegment, writeBufferPolicy)) {
                writeBuffer.writeInt(num);
                writeBuffer.writeInt(2023);

                MemorySegment content = writeBuffer.content();
                assertEquals(num, NativeUtil.getInt(content, 0L));
                assertEquals(2023, NativeUtil.getInt(content, 4L));
                assertFalse(content.isNative());
            } catch (Exception e) {
                throw new RuntimeException(e);
            }

        }
    }

    @Test
    public void test_DefaultWriteBufferPolicy() {
        try(Arena arena = Arena.ofConfined()) {
            MemorySegment memorySegment = arena.allocate("hello".getBytes().length);

            WriteBufferPolicy writeBufferPolicy = new WriteBuffer.DefaultWriteBufferPolicy(arena);
            WriteBuffer writeBuffer = new WriteBuffer(memorySegment, writeBufferPolicy);
            assertEquals(5, writeBuffer.size());
            assertEquals(0, writeBuffer.writeIndex());

            // 写入 hello，由于c语言字符串结尾需要 '\0'
            // 所以这里会扩容
            writeBuffer.writeCStr("hello");
            assertNotEquals(5, writeBuffer.size());
            assertEquals(10, writeBuffer.size());

            // 写入 world
            writeBuffer.writeCStr(" world~");

            System.out.println(STR."writeIndex:\{writeBuffer.writeIndex()}, size:\{writeBuffer.size()}");

            MemorySegment content = writeBuffer.content();
            assertEquals("hello world~", NativeUtil.getStr(content));
        }
    }

    @Test
    public void test_default() {
        try(WriteBuffer writeBuffer = WriteBuffer.newDefaultWriteBuffer(Arena.ofConfined(), 4)) {
            writeBuffer.writeInt(1);
            writeBuffer.writeInt(2);
            writeBuffer.writeInt(3);

            MemorySegment content = writeBuffer.content();
            assertEquals(1, NativeUtil.getInt(content, 0L));
            assertEquals(2, NativeUtil.getInt(content, 4L));
            assertEquals(3, NativeUtil.getInt(content, 8L));

            ReadBuffer readBuffer = new ReadBuffer(content);
            assertEquals(1, readBuffer.readInt());
            assertEquals(2, readBuffer.readInt());
            assertEquals(3, readBuffer.readInt());
        }
    }
}
