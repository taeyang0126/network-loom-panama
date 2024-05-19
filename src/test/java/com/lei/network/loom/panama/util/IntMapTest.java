package com.lei.network.loom.panama.util;

import com.lei.network.loom.panama.JmhTest;
import com.lei.network.loom.panama.library.Socket;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.Param;
import org.openjdk.jmh.infra.Blackhole;
import org.openjdk.jmh.runner.RunnerException;

import java.util.HashMap;
import java.util.Map;

import static com.lei.network.loom.panama.constant.Constants.KB;

/**
 * <p>
 * IntMapTest
 * </p>
 *
 * @author 伍磊
 */
public class IntMapTest extends JmhTest {

    private static final Object o = new Object();
    @Param({"5", "20", "100", "1000", "10000"})
    private int size;

    @Benchmark
    public void testSocketMap(Blackhole bh) {
        Map<Socket, Object> m = new HashMap<>(KB);
        for(int i = 0; i < size; i++) {
            Socket socket = new Socket(i);
            m.put(socket, o);
            bh.consume(m.get(socket));
        }
    }

    @Benchmark
    public void testHashMap(Blackhole bh) {
        Map<Integer, Object> m = new HashMap<>(KB);
        for(int i = 0; i < size; i++) {
            m.put(i, o);
            bh.consume(m.get(i));
        }
    }

    @Benchmark
    public void testIntMap(Blackhole bh) {
        IntMap<Object> m = new IntMap<>(KB);
        for(int i = 0; i < size; i++) {
            m.put(i, o);
            bh.consume(m.get(i));
        }
    }

    public static void main(String[] args) throws RunnerException {
        runTest(IntMapTest.class);
    }
}
