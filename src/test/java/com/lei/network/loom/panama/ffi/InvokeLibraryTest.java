package com.lei.network.loom.panama.ffi;

import java.io.File;
import java.lang.foreign.Arena;
import java.lang.foreign.FunctionDescriptor;
import java.lang.foreign.Linker;
import java.lang.foreign.MemorySegment;
import java.lang.foreign.SymbolLookup;
import java.lang.foreign.ValueLayout;
import java.lang.invoke.MethodHandle;

/**
 * <p>
 * FFI 调用外部函数接口 <br/>
 * 因为 project Panama 目前处于 preview 阶段，所以必须在编译和执行阶段都添加参数 --enable-preview
 * <ol>
 *     <li>java compiler 配置编译选项为 --enable-preview </li>
 *     <li> vm options 配置 --enable-preview --enable-native-access=ALL-UNNAMED </li>
 * </ol>
 * </p>
 *
 * @author 伍磊
 */
public class InvokeLibraryTest {

    public static void main(String[] args) {
        // 1. 将动态链接库文件加载为一个 SymbolLookup 对象
        // Arena.global() 代表的是该动态链接库的生命作用域，通常都会使用全局global作用域，这样动态链接库只有在JVM退出的时候才会被自动卸载
        SymbolLookup symbolLookup = SymbolLookup.libraryLookup(new File("c/hello.dylib").toPath(), Arena.global());
        // 2. 从该动态链接库中，找到 hello() 的函数，并且规定这个函数的返回值为Void
        MemorySegment func = symbolLookup.find("hello").orElseThrow(() -> new RuntimeException("Failed to load dynamic library"));
        // ValueLayout.ADDRESS 对应 c 语言中的指针类型
        // 函数名和函数的参数类型以及返回类型可以完全锁定一个函数，而不用担心重载导致的冲突问题，这样就获取到了该hello()函数的MethodHandle对象
        // MethodHandle 可以被理解为一个函数指针，记载了该函数在内存中的一个具体地址，后续所有的对该函数的调用，都可以通过 MethodHandle.invokeExact()方法来实现
        MethodHandle handle = Linker.nativeLinker().downcallHandle(func, FunctionDescriptor.ofVoid(ValueLayout.ADDRESS));

        // 使用 project Panama 提供的堆外内存机制，Arena 对象维护着一块堆外内存的生命周期，可以通过Arena来进行不同内存布局的堆外内存的分配，当Arena对象被关闭时，通过Arena分配的所有内存也会被释放
        try (Arena arena = Arena.ofConfined()) {
            // 分配了一个 UTF8格式的，以C风格'\0'作为结尾的字符串，并返回一个指向该字符串的指针
            // c 语言中的指针在java中表现为 MemorySegment 对象
            MemorySegment memorySegment = arena.allocateUtf8String("world");
            // 将该MemorySegment传递给对应函数MethodHandle.invokeExact()，这样就完成了一次完整的，从Java语言到C语言的函数调用
            handle.invokeExact(memorySegment);
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }
}
