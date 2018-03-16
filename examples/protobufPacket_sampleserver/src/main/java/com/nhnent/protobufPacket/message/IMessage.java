package com.nhnent.protobufPacket.message;

import com.google.protobuf.InvalidProtocolBufferException;
import com.nhnent.protobufPacket.Server;

/**
 * Interface of a Message
 */
public interface IMessage {

    public abstract void execute(Server server, byte[] packetBody) throws InvalidProtocolBufferException;
}
