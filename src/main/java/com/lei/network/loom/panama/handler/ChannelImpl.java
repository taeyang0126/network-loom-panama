package com.lei.network.loom.panama.handler;

import com.lei.network.loom.panama.coder.Decoder;
import com.lei.network.loom.panama.coder.Encoder;
import com.lei.network.loom.panama.core.Loc;
import com.lei.network.loom.panama.core.Poller;
import com.lei.network.loom.panama.core.Writer;
import com.lei.network.loom.panama.library.Socket;

/**
 * <p>
 * ChannelImpl
 * </p>
 *
 * @author 伍磊
 */
public record ChannelImpl(
        Socket socket,
        Encoder encoder,
        Decoder decoder,
        Handler handler,
        Poller poller,
        Writer writer,
        Loc loc
) implements Channel {
}
