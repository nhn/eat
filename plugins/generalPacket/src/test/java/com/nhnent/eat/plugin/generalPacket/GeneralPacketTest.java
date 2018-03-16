package com.nhnent.eat.plugin.generalPacket;

import javafx.util.Pair;
import org.junit.Test;

import java.util.Queue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;

import static org.junit.Assert.*;

public class GeneralPacketTest {
    @Test
    public void receivePacket() throws Exception {
        GeneralPacket generalPacket = new GeneralPacket();

//        Queue<Pair<String, byte[]>> packets = new
        BlockingQueue<Pair<String, byte[]>> packetQueue = new LinkedBlockingDeque<>();

        GeneralPacket packet ;
//        Header header;
//        Body body;

//        generalPacket.receivePacket(packetQueue, packet.toByte());

        Pair<String, byte[]> decodedPacket  = packetQueue.poll();

//        decodedPacket.getKey()  == header;
//        decodedPacket.getValue() == body.toByte()

    }

}