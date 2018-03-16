package com.nhnent.eat.communication.netty;

/**
 * Created by NHNEnt on 2017-03-28.
 */
public interface PacketListener {
    void receivePacket(byte[] readBuf);

    void exceptionCaught(String exceptionMessage);
}
