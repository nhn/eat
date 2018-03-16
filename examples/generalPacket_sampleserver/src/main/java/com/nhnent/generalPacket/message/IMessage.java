package com.nhnent.generalPacket.message;

import java.nio.channels.SocketChannel;

/**
 * Interface of a Message
 */
public interface IMessage {

    public abstract void execute(SocketChannel channel, Object message);

}
