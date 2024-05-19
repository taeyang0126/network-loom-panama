package com.lei.network.loom.panama;

import com.lei.network.loom.panama.buffer.ReadBuffer;
import com.lei.network.loom.panama.util.NativeUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.foreign.Arena;
import java.lang.foreign.MemorySegment;
import java.lang.foreign.ValueLayout;

/**
 * <p>
 * Main
 * </p>
 *
 * @author 伍磊
 */
public class Main {
    private static final Logger log = LoggerFactory.getLogger(Main.class);

    public static void main(String[] args) {

        try(Arena arena = Arena.ofConfined()) {
            MemorySegment memorySegment = NativeUtil.allocateStr(arena, "hello world~");
            ReadBuffer readBuffer = new ReadBuffer(memorySegment);
            log.info("Accessing readBuffer : {}", readBuffer.readCStr());
        }

        // memorySegmentTest();

        // testNativeUtil();

        // logTest();
    }

    private static void memorySegmentTest() {
        try (Arena arena = Arena.ofConfined()) {
            // 由 area 分配的是堆外的内存
            MemorySegment memorySegment = arena.allocate(ValueLayout.JAVA_INT_UNALIGNED);
            memorySegment.set(ValueLayout.JAVA_INT_UNALIGNED, 0, 123);
            log.info("memorySegment is native: {}, value: {}"
                    , memorySegment.isNative(), memorySegment.get(ValueLayout.JAVA_INT, 0L));

            // MemorySegment 自己创建的是堆内的内存
            MemorySegment heapMemorySegment = MemorySegment.ofArray(new int[]{1997, 2003});
            long l = heapMemorySegment.byteSize();
            System.out.println(l);
            System.out.println(NativeUtil.getByte(heapMemorySegment, l - 1));
            log.info("heapMemorySegment is native: {}, value: {}"
                    , heapMemorySegment.isNative(), heapMemorySegment.get(ValueLayout.JAVA_INT, 4L));
        }
    }

    private static void testNativeUtil() {
        try (Arena arena = Arena.ofConfined()) {
            // 堆外分配一个Int类型的内存区域
            MemorySegment memorySegment = arena.allocate(ValueLayout.JAVA_INT);
            // 向这个内存区域写入一个数字
            NativeUtil.setInt(memorySegment, 0L, 123);
            // 读取
            log.info("Accessing native memory : {}", NativeUtil.getInt(memorySegment, 0L));
        }
    }

    private static void logTest() {
        try(Arena arena = Arena.ofConfined()) {
            MemorySegment memorySegment = arena.allocate(ValueLayout.JAVA_INT);
            log.debug("debug");
            log.info("no error");
            log.warn("warn");
            log.error("error");
        }
    }
}
