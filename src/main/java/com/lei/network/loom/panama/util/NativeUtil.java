package com.lei.network.loom.panama.util;

import com.lei.network.loom.panama.constant.Constants;
import com.lei.network.loom.panama.exception.ExceptionType;
import com.lei.network.loom.panama.exception.FrameworkException;
import com.lei.network.loom.panama.library.OsType;

import java.lang.foreign.Arena;
import java.lang.foreign.FunctionDescriptor;
import java.lang.foreign.Linker;
import java.lang.foreign.MemorySegment;
import java.lang.foreign.SymbolLookup;
import java.lang.foreign.ValueLayout;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.VarHandle;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

import static com.lei.network.loom.panama.constant.Constants.NUT;
import static com.lei.network.loom.panama.constant.Constants.UNREACHED;

/**
 * <p>
 * NativeUtil   <br/>
 * <ol>
 *     <li>构建一个用于读取和写入堆外内存的工具类</li>
 * </ol>
 * </p>
 *
 * @author 伍磊
 */
public final class NativeUtil {

    private NativeUtil() {
        throw new UnsupportedOperationException();
    }


    /**
     * 在C语言中，判断一个指针是否为空指针，一般会查看该指针是否为NULL，
     * 当一个空指针被从C语言中被返回到Java层时，MemorySegment对象会指向内存地址0，
     * 这是一个约定俗成的无效内存地址，我们只需要通过判断地址是否为0即可判断空指针
     */
    public static final MemorySegment NULL_POINTER = MemorySegment.ofAddress(0L);

    private static final int CPU_CORES = Runtime.getRuntime().availableProcessors();

    private static final String osName = System.getProperty("os.name").toLowerCase();
    private static final OsType osType = detectOsType();

    private static final long I_MAX = Integer.MAX_VALUE;
    private static final long I_MIN = Integer.MIN_VALUE;

    private static final Arena globalArena = Arena.global();

    private static final Linker linker = Linker.nativeLinker();

    private static final String libPath = System.getProperty("TENET_LIBRARY_PATH");
    private static final Map<String, SymbolLookup> libraryCache = new ConcurrentHashMap<>();

    private static String getDynamicLibraryName(String identifier) {
        return switch (osType) {
            case Windows -> STR."lib\{identifier}.dll";
            case Linux -> STR."lib\{identifier}.so";
            case MacOS -> STR."lib\{identifier}.dylib";
            default -> throw new FrameworkException(ExceptionType.NATIVE, "Unrecognized operating system");
        };
    }

    public static SymbolLookup loadLibrary(String identifier) {
        if(libPath == null) {
            throw new FrameworkException(ExceptionType.NATIVE, "Global libPath not found");
        }
        return libraryCache.computeIfAbsent(identifier, i -> SymbolLookup.libraryLookup(STR."\{libPath}/\{getDynamicLibraryName(i)}", Arena.global()));
    }


    public static MethodHandle methodHandle(SymbolLookup lookup, String methodName, FunctionDescriptor functionDescriptor, Linker.Option... options) {
        MemorySegment methodPointer = lookup.find(methodName)
                .orElseThrow(() -> new FrameworkException(ExceptionType.NATIVE, STR."Unable to load target native method : \{methodName}"));
        return linker.downcallHandle(methodPointer, functionDescriptor, options);
    }

    public static MethodHandle methodHandle(SymbolLookup lookup, List<String> methodNames, FunctionDescriptor functionDescriptor, Linker.Option... options) {
        for (String methodName : methodNames) {
            Optional<MemorySegment> methodPointer = lookup.find(methodName);
            if (methodPointer.isPresent()) {
                return linker.downcallHandle(methodPointer.get(), functionDescriptor, options);
            }
        }
        throw new FrameworkException(ExceptionType.NATIVE, STR."Unable to load target native method : \{methodNames}");
    }

    /**
     *  Safely cast long to int, throw an exception if overflow
     */
    public static int castInt(long l) {
        if(l < I_MIN || l > I_MAX) {
            throw new FrameworkException(ExceptionType.NATIVE, Constants.UNREACHED);
        }
        return (int) l;
    }

    public static OsType ostype() {
        return osType;
    }

    public static int getCpuCores() {
        return CPU_CORES;
    }

    public static boolean checkNullPointer(MemorySegment memorySegment) {
        return memorySegment == null || memorySegment.address() == 0L;
    }

    /**
     * Java官方提供的高效访问堆外内存的方式是借助VarHandle类进行完成的
     * MethodHandle与VarHandle类均为Java语言自JDK9以后引入的新的反射调用机制，其中MethodHandle类可以被理解成函数指针，用于高效的访问函数，VarHandle类可以被理解成对象指针，用于高效的访问变量。
     * 这两个类主要用于替换Java语言中陈旧的反射机制，并且不局限于访问Java中创建的方法与字段，还能够以统一的API实现对于Native方法与堆外内存的访问
     */
    // 创建了一个以Java中byte数组类型的布局进行内存访问的VarHandle对象，然后我们就可以使用该VarHandle用这种指定的内存布局形式去访问MemorySegment所指向的内存区域，
    // 使用get()方法和set()方法进行取值与赋值，就如同我们在操作一个byte[]数组对象一样
    private static final VarHandle byteHandler = MethodHandles.memorySegmentViewVarHandle(ValueLayout.JAVA_BYTE);

    public static byte getByte(MemorySegment memorySegment, long index) {
        return (byte) byteHandler.get(memorySegment, index);
    }

    public static void setByte(MemorySegment memorySegment, long index, byte value) {
        byteHandler.set(memorySegment, index, value);
    }

    // 定义Short，Int，Long等类型的访问模型时，我们均使用了UNALIGNED的布局，这意味着在试图读取或写入数据时，Java会采用非对齐的方式进行操纵
    // 在c语言的结构体中，各个成员会存在字节对齐的情况，确保每个成员的地址是其大小的整数倍。
    // 为了满足对齐要求，编译器可能会在结构体或变量的成员之间插入填充字节，以确保每个成员都按照其大小和对齐要求放置在正确的位置上，从而让CPU能够高效地访问数据
    /**
     * 在进行网络数据的读写时，TCP字节流中的数据是紧凑排列的，可能需要从任意位置读取任意字长的数据，
     * 因此对于所有超过1个字节的类型访问，我们可以统一在NativeUtil中将其定义为UNALIGNED形式。
     * 注意非对齐的访问只允许使用普通的get()和set()方法，如果需要对堆外内存使用compareAndSet()等原子操作，那么必须使用对齐的内存访问模式
     */
    private static final VarHandle shortHandle = MethodHandles.memorySegmentViewVarHandle(ValueLayout.JAVA_SHORT_UNALIGNED);
    private static final VarHandle intHandle = MethodHandles.memorySegmentViewVarHandle(ValueLayout.JAVA_INT_UNALIGNED);
    private static final VarHandle longHandle = MethodHandles.memorySegmentViewVarHandle(ValueLayout.JAVA_LONG_UNALIGNED);

    public static short getShort(MemorySegment memorySegment, long index) {
        return (short) shortHandle.get(memorySegment, index);
    }

    public static void setShort(MemorySegment memorySegment, long index, short value) {
        shortHandle.set(memorySegment, index, value);
    }

    public static int getInt(MemorySegment memorySegment, long index) {
        return (int) intHandle.get(memorySegment, index);
    }

    public static void setInt(MemorySegment memorySegment, long index, int value) {
        intHandle.set(memorySegment, index, value);
    }

    public static long getLong(MemorySegment memorySegment, long index) {
        return (long) longHandle.get(memorySegment, index);
    }

    public static void setLong(MemorySegment memorySegment, long index, long value) {
        longHandle.set(memorySegment, index, value);
    }

    /**
     * 字符串相关
     * C语言的字符串可以认为是一个以\0结尾的Java中的byte数组，而在Java中字符串是以String类的形式表示的
     * String底层依旧使用一个byte[]数组进行驱动，但是我们目前还不能够直接访问到这个byte[]数组
     * （在JDK22中，存在一个非常重要的优化，使得在编码方式兼容的情况下，直接使用String类中的byte数组进行拷贝，而不需要每次重新创建一个额外的数组，这会大幅度的提升与C语言中字符串交互场景下的性能）。
     * 在MemorySegment中已经提供了简便的访问C语言字符串的方式，但其每次调用时，都会先遍历字符串以获取其长度然后再进行拷贝，
     * 有时候我们可能事先就已经知道了字符串长度值，所以可以省略该步骤
     */
    public static String getStr(MemorySegment memorySegment) {
        return getStr(memorySegment, 0);
    }

    // 定义了两套C风格字符串的访问模型，相较于JDK官方所直接提供的MemorySegment.getUtf8String()方法而言会更加的灵活一些，
    // 对于已知内存长度的字符串，可以提前分配好byte数组，逐个进行拷贝，对于未知内存长度的字符串，一直读取直到\0为止，然后调用MemorySegment.copy()的方法进行数组拷贝，
    // 该方法类似于System.arraycopy()方法，比起自己实现的逐字节拷贝，使用系统提供的范围拷贝会是更加高效的，如果未能读取到合法的C风格字符串则抛出异常
    public static String getStr(MemorySegment ptr, int maxLength) {
        if (maxLength > 0) {    // 表示已经知道了长度
            byte[] bytes = new byte[maxLength];
            for (int i = 0; i < maxLength; i++) {
                byte b = getByte(ptr, i);
                if (b == NUT) {
                    return new String(bytes, 0, i, StandardCharsets.UTF_8);
                } else {
                    bytes[i] = b;
                }
            }
        } else {    // 表示不知道字符串的长度，必须要遍历
            for (int i = 0; i < Integer.MAX_VALUE; i++) {
                byte b = getByte(ptr, i);
                if (b == NUT) {
                    // 到了字符串的末尾，进行拷贝
                    byte[] bytes = new byte[i];
                    MemorySegment.copy(ptr, ValueLayout.JAVA_BYTE, 0, bytes, 0, i);
                    return new String(bytes, 0, i, StandardCharsets.UTF_8);
                }
            }
        }
        throw new FrameworkException(ExceptionType.NATIVE, UNREACHED);
    }

    public static MemorySegment allocateStr(Arena arena, String  str) {
        return arena.allocateUtf8String(str);
    }

    // JDK中默认提供的Arena.allocateUtf8String()方法已经足够满足我们日常分配普通字符串的要求，
    // 不过还有一些情况是需要我们多预留一些内存空间给字符串的，而不是完全的一对一的模型，因此这里我们定义了第两种指定具体内存长度的分配模式，
    // 更适合在需要指定内存大小且留有余量的情况下进行使用
    public static MemorySegment allocateStr(Arena arena, String str, int len) {
        MemorySegment strSegment = MemorySegment.ofArray(str.getBytes(StandardCharsets.UTF_8));
        long size = strSegment.byteSize();
        if(len < size + 1) {
            throw new RuntimeException("String out of range");
        }
        MemorySegment memorySegment = arena.allocateArray(ValueLayout.JAVA_BYTE, len);
        MemorySegment.copy(strSegment, 0, memorySegment, 0, size);
        // 设置 size 位置上的结尾符号
        setByte(memorySegment, size, NUT);
        return memorySegment;
    }

    // 字节数组匹配，判断m从offset开始的字节数组是否与bytes一致
    public static boolean matches(MemorySegment m, long offset, byte[] bytes) {
        long mSize = m.byteSize();
        if (offset >= mSize) {
            throw new FrameworkException(ExceptionType.NATIVE, "read index overflow");
        }

        if ((offset + bytes.length) > mSize) {
            throw new FrameworkException(ExceptionType.NATIVE, "read index overflow");
        }

        // 比较每个字节
        for (int index = 0; index < bytes.length; index++) {
            if (getByte(m, offset + index) != bytes[index]) {
                return false;
            }
        }

        return true;
    }

    private static OsType detectOsType() {
        if(osName.contains("windows")) {
            return OsType.Windows;
        }else if(osName.contains("linux")) {
            return OsType.Linux;
        }else if(osName.contains("mac") && osName.contains("os")) {
            return OsType.MacOS;
        }else {
            return OsType.Unknown;
        }
    }


}
