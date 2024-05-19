package com.lei.network.loom.panama.coder;

import com.lei.network.loom.panama.buffer.ReadBuffer;

import java.util.List;

/**
 * <p>
 * Decoder
 * </p>
 *
 * @author 伍磊
 */
@FunctionalInterface
public interface Decoder {

    /**
     * 从给定参数readBuffer中完成数据的读取，将其反序列化为Java类对象，并添加至entityList中，
     * 之所以使用一个List来存放解码后的实体，是因为针对于单个ReadBuffer也是很可能解析出多个消息体对象的
     *
     * 允许 decode()方法不向entityList中添加任何元素，当在网络框架内部检测到entityList没有变化时，意味着当前的数据流不足以进行解析，
     * 还需要继续接受更多数据才可以，那么我们需要将当前ReadBuffer中未读的部分先缓冲在内存里面，等待下一次读取
     */
    void decode(ReadBuffer readBuffer, List<Object> entityList);

}
