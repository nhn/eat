package com.nhnent.eat.plugin.generalPacket;

import com.google.gson.Gson;
import com.nhnent.eat.common.PacketClassPool;
import com.nhnent.eat.entity.GeneratedPacketJson;
import com.nhnent.eat.entity.ScenarioUnit;
import com.nhnent.eat.packets.IStreamPacket;
import com.nhnent.generalPacket.packets.MessageEnvelop;
import javafx.util.Pair;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Queue;

import static com.nhnent.eat.common.CommonDefine.*;
import static com.nhnent.eat.handler.PacketJsonHandler.removeRedundant;
import static com.nhnent.eat.plugin.generalPacket.CommonUtil.deserialize;
import static com.nhnent.eat.plugin.generalPacket.CommonUtil.serialize;


public class GeneralPacket implements IStreamPacket {

    private final Logger logger = LoggerFactory.getLogger(getClass());
    private static GeneralPacket obj = null;

    private Gson gson = null;

    public static GeneralPacket obj() {
        if(obj == null) {
            obj = new GeneralPacket();
        }
        return obj;
    }

    @Override
    public void initSingletonInstance() {

    }


    @Override
    public ScenarioUnit decodeScenarioHeader(String packetHeader, ScenarioUnit scenarioUnit) {

        final int indexOfPckName = 1;
        final int indexOfPckSubID = 2;
        final int lengthOfContainPckName = 2;
        final int lengthOfContainPckSubID = 3;

        String pckType;
        String pckName = EmptyString;
        String subId = EmptyString;

        String[] scenarioDefine = packetHeader.split(PckNameDelimiter);
        pckType = scenarioDefine[0].replace(PckDefDelimiter, EmptyString);

        if (scenarioDefine.length >= lengthOfContainPckName) {
            pckName = scenarioDefine[indexOfPckName];
        }
        if (scenarioDefine.length == lengthOfContainPckSubID) {
            subId = scenarioDefine[indexOfPckSubID];
        }

        scenarioUnit.packageName = pckName.split("]")[0].replace("[", "");
        scenarioUnit.name = pckName.split("]")[1];
        scenarioUnit.subId = subId;
        scenarioUnit.type = pckType;

        return scenarioUnit;
    }

    // Json 형태로 decode
    @Override
    public Object decodePacket(Class<?> clsPck, byte[] data) {
        Object decodedPacket = null;
        try {
            decodedPacket = deserialize(data);
        } catch (IOException | ClassNotFoundException e) {
            logger.error(ExceptionUtils.getStackTrace(e));
        }
        return decodedPacket;
    }

    @Override
    public byte[] getBodyPacket(byte[] packetBytes) {
        return packetBytes;
    }

    @Override
    public String packetToJson(Object packet) {
        gson = new Gson();
        return gson.toJson(packet);
    }

    @Override
    public GeneratedPacketJson packetToJson(String expect, Object real) throws Exception {

        gson = new Gson();
        String expectJson = expect;
        String realJson = gson.toJson(real);

        expectJson = removeRedundant(expectJson);
        realJson = removeRedundant(realJson);

        GeneratedPacketJson result = new GeneratedPacketJson();
        result.expectJson = expectJson;
        result.realJson = realJson;

        return result;
    }

    @Override
    public byte[] jsonToPacket(ScenarioUnit scenarioUnit) {

        gson = new Gson();

        String packageName = scenarioUnit.packageName;
        String packetName;
        if (scenarioUnit.name.contains(".")) {
            packetName = scenarioUnit.name.split("\\.")[1];
        } else {
            packetName = scenarioUnit.name;
        }
        String jsonContents = scenarioUnit.json;

        // Find the class.
        Class targetClass =
                PacketClassPool.obj().findClassByName(packageName + "." + packetName);

        // Deserialize the jsonContents using targetClass.
        Object targetObj = gson.fromJson(jsonContents, targetClass);

        MessageEnvelop messageEnvelop = new MessageEnvelop();

        messageEnvelop.setMessageName(packetName);
        messageEnvelop.setMessage(targetObj);

        ByteBuffer buffer = null;
        try {

            buffer = ByteBuffer.allocate(serialize(messageEnvelop).length);
            buffer.put(serialize(messageEnvelop));

        } catch (IOException e) {
            logger.error(ExceptionUtils.getStackTrace(e));
        }

        return buffer.array();
    }

    @Override
    public void receivePacket(Queue<Pair<String, byte[]>> packets, byte[] data) throws Exception {

        MessageEnvelop recvMessage = (MessageEnvelop)deserialize(data);

        packets.add(new Pair<>(
                    recvMessage.getMessageName(),
                    serialize(recvMessage.getMessage())));
        return;
    }
}
