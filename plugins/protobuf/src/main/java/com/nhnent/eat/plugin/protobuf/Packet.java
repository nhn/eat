package com.nhnent.eat.plugin.protobuf;

import com.google.protobuf.DynamicMessage;
import com.nhnent.eat.common.PacketClassPool;
import com.nhnent.eat.entity.ScenarioUnitType;
import com.nhnent.eat.plugin.protobuf.config.ProtobufConfig;

import java.nio.ByteBuffer;

/**
 * Created by NHNEnt on 2016-12-23.
 */
class Packet {

    private static Integer seqNo = 0;
    /**
     *
     * @param destination Destination of packet(Game, Chatting or and so on.)
     * @param type Type of scenario unit
     * @param msgName Name of packet
     * @param subId SubID which for 1 player
     * @param bodySize Size of body
     * @return Generated packet header
     * @throws Exception If reflection function is not found, it will raise exception
     */
    private static byte[] generateHeader(final String destination, final String type, final String msgName,
                                         final String subId, final int bodySize) throws Exception {

        final String baseHeader = ProtobufConfig.obj().getProtobuf().getHeaderPackageClassName() + "$Header";
        final String baseHeaderBuilder = ProtobufConfig.obj().getProtobuf().getHeaderPackageClassName() + "$Header$Builder";
        PacketClassPool classes = PacketClassPool.obj();

        Object builder = classes.callFunction(baseHeader, null, "newBuilder", null);

        classes.callFunction(baseHeaderBuilder, builder, "setSubid", subId);
        if (type.equals(ScenarioUnitType.Request)) {
            classes.callFunction(baseHeaderBuilder, builder, "setSeq",++seqNo);
        } else {
            classes.callFunction(baseHeaderBuilder, builder, "setSeq",0);
        }
        classes.callFunction(baseHeaderBuilder, builder, "setMsgName",msgName);
        classes.callFunction(baseHeaderBuilder, builder, "setBodySize",bodySize);
        classes.callFunction(baseHeaderBuilder, builder, "setService",destination);
        Object header = classes.callFunction(baseHeaderBuilder, builder, "build", null);
        return (byte[])classes.callFunction(baseHeader, header, "toByteArray", null);
    }

    /**
     *
     * @param destination Destination of packet(Game, Chatting or and so on.)
     * @param type Type of scenario unit
     * @param subId SubID which for 1 player
     * @param message Packet body which formatted GeneratedMessage(defined in Protocol Buffer)
     * @return Generated packet
     * @throws Exception If reflection function is not found, it will raise exception
     */
    public static byte[] generateTransferPacket(final String destination, final String type,
                                          final String subId, final DynamicMessage message) throws Exception {
        byte[] rawBody = message.toByteArray();
        byte[] header = generateHeader(destination, type, message.getDescriptorForType().getName(), subId, rawBody.length);
        int packetSize = 1 + header.length + rawBody.length;

        ByteBuffer buffer = ByteBuffer.allocate(packetSize);
        buffer.put((byte) header.length);
        buffer.put(header);
        buffer.put(rawBody);

        return buffer.array();
    }
}
