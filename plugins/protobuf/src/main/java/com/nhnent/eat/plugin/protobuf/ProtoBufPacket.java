package com.nhnent.eat.plugin.protobuf;

import com.google.protobuf.DynamicMessage;
import com.google.protobuf.InvalidProtocolBufferException;
import com.google.protobuf.Message;
import com.nhnent.eat.common.PacketClassPool;
import com.nhnent.eat.entity.GeneratedPacketJson;
import com.nhnent.eat.entity.ScenarioUnit;
import com.nhnent.eat.packets.IStreamPacket;
import com.nhnent.eat.plugin.protobuf.config.ProtobufConfig;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import javafx.util.Pair;
import net.jpountz.lz4.LZ4Factory;
import net.jpountz.lz4.LZ4FastDecompressor;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.Queue;

import static com.nhnent.eat.common.CommonDefine.*;
import static com.nhnent.eat.handler.PacketJsonHandler.removeRedundant;
import static com.nhnent.eat.plugin.protobuf.ProtobufUtil.messageToJson;

public class ProtoBufPacket implements IStreamPacket {

    private final Logger logger = LoggerFactory.getLogger(getClass());
    private static final ProtoBufPacket obj = new ProtoBufPacket();

    public static IStreamPacket obj() {
        return obj;
    }

    @Override
    public void initSingletonInstance() {
        GenerateJAR.getInstance().initialize();
    }

    @Override
    public ScenarioUnit decodeScenarioHeader(String packetHeader, ScenarioUnit scenarioUnit) {

        final int indexOfPckName = 1;
        final int indexOfPckSubID = 2;
        final int indexOfPckServiceID = 3;

        //ex) #Response:[TUTORIAL]Basic.ResponseStartGame
        final int lengthOfContainPckName = 2;

        //ex) #Response:[TUTORIAL]Basic.ResponseStartGame:[subID]

        final int lengthOfContainPckSubID = 3;

        //ex) #Response:[TUTORIAL]Basic.ResponseStartGame:[subID]:[svcID]
        final int lengthOfContainPckServiceID = 4;

        String pckType;
        String pckName = EmptyString;
        String subId = EmptyString;
        String serviceId = EmptyString;

        String[] scenarioDefine = packetHeader.split(PckNameDelimiter);
        pckType = scenarioDefine[0].replace(PckDefDelimiter, EmptyString);

        if (scenarioDefine.length >= lengthOfContainPckName) {
            pckName = scenarioDefine[indexOfPckName];
        }
        if (scenarioDefine.length == lengthOfContainPckSubID) {
            subId = scenarioDefine[indexOfPckSubID];
        }
        if (scenarioDefine.length == lengthOfContainPckServiceID) {
            serviceId = scenarioDefine[indexOfPckServiceID];
        }

        scenarioUnit.packageName = pckName.split("]")[0].replace("[", "");
        scenarioUnit.name = pckName.split("]")[1];
        scenarioUnit.subId = subId;
        scenarioUnit.type = pckType;
        scenarioUnit.dest = serviceId;

        return scenarioUnit;
    }

    @Override
    public String packetToJson(Object packet)  {
        try {
            return messageToJson((Message) packet);
        } catch (InvalidProtocolBufferException e) {
            logger.error(ExceptionUtils.getStackTrace(e));
        }
        return null;
    }

    @Override
    public GeneratedPacketJson packetToJson(final String expect, final Object real) throws Exception {

        String expectJson;
        String realJson;

        expectJson = expect;
        realJson = messageToJson((Message) real);

        realJson = ProtobufDescPool.obj().parseReceivedBytePacket(expectJson, realJson);
        expectJson = ProtobufDescPool.obj().convertExpectedJsonToFormalJson(expectJson);

        expectJson = removeRedundant(expectJson);
        realJson = removeRedundant(realJson);

        GeneratedPacketJson result = new GeneratedPacketJson();
        result.expectJson = expectJson;
        result.realJson = realJson;
        return result;
    }

    @Override
    public byte[] jsonToPacket(ScenarioUnit scenarioUnit) {

        byte[] packet = null;
        Message.Builder msg;
        DynamicMessage message;
        String serviceID;

        try {
            String packageName = scenarioUnit.packageName;
            String packetName;
            if (scenarioUnit.name.contains(".")) {
                packetName = scenarioUnit.name.split("\\.")[1];
            } else {
                packetName = scenarioUnit.name;
            }
            String packetType = scenarioUnit.type;
            String jsonContents = scenarioUnit.json;

            msg = ProtobufDescPool.obj().JsonToMessage(packageName + "." + packetName, jsonContents);

            message = (DynamicMessage) msg.build();
            if(scenarioUnit.dest != EmptyString) {
                serviceID = scenarioUnit.dest;
            } else {
                serviceID = ProtobufConfig.obj().getProtobuf().getServiceId();
            }
            packet = Packet.generateTransferPacket(serviceID, packetType, "", message);
        } catch (Exception e) {
            logger.error(ExceptionUtils.getStackTrace(e));
        }

        return packet;
    }

    @Override
    public Object decodePacket(final Class<?> clsPck, byte[] data) {
        Logger logger = LoggerFactory.getLogger("com.nhnent.eat.protobufHandler.ProtoBufPacket");
        try {
            return PacketClassPool.obj().callFunction(clsPck.getName(), clsPck, "parseFrom", data);
        } catch (Exception e) {
            logger.error(ExceptionUtils.getStackTrace(e));
        }
        return null;
    }

    @Override
    public byte[] getBodyPacket(byte[] packetBytes) {
        int headerSize = (int)packetBytes[0];
        int bodySize = packetBytes.length - 1 - headerSize;

        return Arrays.copyOfRange(packetBytes, headerSize, headerSize + bodySize);
    }

    private static final int DEFAULT_BUFFER_SIZE = 4096;
    private ByteBuf buffer = Unpooled.buffer(DEFAULT_BUFFER_SIZE);
    /**
     * Inspect packet and parse packet
     *  1. Read packet header
     *  2. If size of packet is lack to generated whole packet, it will wait next packet
     *  3. If 2 more packets are exist in the given packet, it will insert it to given queue
     * @param packets Queue of packets
     * @param data Original packet byte
     * @throws Exception Handle exception
     */
    @Override
    public final void receivePacket(Queue<Pair<String, byte[]>> packets, final byte[] data)
            throws Exception {
        String receivedPacketName;
        if (buffer.writableBytes() < data.length) {
            int copyCount = buffer.writerIndex() - buffer.readerIndex();
            buffer.resetWriterIndex();
            buffer.writeBytes(buffer, buffer.readerIndex(), copyCount);
            buffer.resetReaderIndex();

            if (buffer.writableBytes() < data.length) {
                int addCapacity = data.length - buffer.writableBytes();
                buffer.capacity(buffer.capacity() + addCapacity);
            }
        }

        buffer.writeBytes(data);
        while (buffer.readableBytes() != 0) {
            int headerSize = buffer.getByte(buffer.readerIndex());
            int streamLen = buffer.readableBytes();

            //If lack of header packet size
            if (streamLen < headerSize + 1) { // Add 1 byte for header size.
                break;
            }

            final String baseHeader = ProtobufConfig.obj().getProtobuf().getHeaderPackageClassName() + "$Header";
            final String baseHeaderBuilder = ProtobufConfig.obj().getProtobuf().getHeaderPackageClassName() + "$Header$Builder";
            PacketClassPool classes = PacketClassPool.obj();
            Object builder = classes.callFunction(baseHeader, null, "newBuilder", null);
            try {
                classes.callFunction(baseHeaderBuilder, builder, "mergeFrom",
                        buffer.array(),
                        buffer.readerIndex() + 1,
                        headerSize);
            } catch (Exception e) {
                logger.error("Failed to decode packet.\nheaderSize:{}\nData:{}",headerSize, buffer.array());
                throw e;
            }

            //Check and skip Ping packet
            String msgName = (String)classes.callFunction(baseHeaderBuilder, builder, "getMsgName",null);
            //logger.info("recv Msg Name : " + msgName);
            if(msgName.equals("Ping")) {
                logger.debug("received PING");
                buffer.readerIndex(buffer.readerIndex() + 1 + headerSize);
                continue;
            }

            boolean needToDecompress = false;
            int bodySize;
            int uncompressSize = (int) classes.callFunction(baseHeaderBuilder, builder, "getUncompressSize",null);

            logger.debug("msgName=>{}, uncompressSize=>{}", msgName, uncompressSize);

            bodySize = (int) classes.callFunction(baseHeaderBuilder, builder, "getBodySize", null);

            int packetSize = 1 + headerSize + bodySize;
            if(uncompressSize != 0) {
                //Need to decompress packet
                needToDecompress = true;
            }

            //If lack of packet size
            if (packetSize > streamLen) {
                break;
            }
            byte[] body;
            if(needToDecompress) {
                byte[] compressedBody = new byte[bodySize];

                //begin : code for lz4 decompression
                body = new byte[uncompressSize];
                System.arraycopy(buffer.array(), buffer.readerIndex() + 1 + headerSize, compressedBody, 0, bodySize);

                LZ4Factory factory = LZ4Factory.fastestInstance();
                LZ4FastDecompressor decompressor = factory.fastDecompressor();

                decompressor.decompress(compressedBody,body);
                //end : code for lz4 decompression

                //begin : code for snappy decompression
                //body = Snappy.uncompress(compressedBody);
                //end : code for snappy decompression
            } else {
                body = new byte[bodySize];
                System.arraycopy(buffer.array(), buffer.readerIndex() + 1 + headerSize, body, 0, bodySize);
            }

            if(headerSize != 0) {
                receivedPacketName = (String)classes.callFunction(baseHeaderBuilder, builder, "getMsgName", null);
                packets.add(new Pair<>(receivedPacketName, body));
            }
            buffer.readerIndex(buffer.readerIndex() + packetSize);
        }
    }
}
