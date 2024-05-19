package com.lei.network.loom.panama.handler;

import com.lei.network.loom.panama.coder.Decoder;
import com.lei.network.loom.panama.coder.Encoder;
import com.lei.network.loom.panama.core.Loc;
import com.lei.network.loom.panama.core.Poller;
import com.lei.network.loom.panama.core.Writer;
import com.lei.network.loom.panama.library.Socket;

/**
 * <p>
 * Channel  <br/>
 * 一个Channel对象代表着为一条已经成功建立的TCP连接所分配的所有资源
 * 每个Channel都具备其特有的Socket对象，Encoder编码器，Decoder解码器，
 * Handler用于处理业务数据，并与指定的Poller和Writer实例进行绑定，由其处理Channel上所有相关的读写事件，
 * Loc表示对端的IP地址，既可能是客户端地址也可能是服务端地址，
 * Channel接口是我们对通用读写场景下的TCP连接的统一抽象
 * </p>
 *
 * @author 伍磊
 */
public sealed interface Channel permits ChannelImpl {

    Socket socket();

    Encoder encoder();

    Decoder decoder();

    Handler handler();

    Poller poller();

    Writer writer();

    Loc loc();

}
