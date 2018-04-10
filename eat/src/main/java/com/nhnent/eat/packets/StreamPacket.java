package com.nhnent.eat.packets;

import com.nhnent.eat.common.Util;
import com.nhnent.eat.entity.GeneratedPacketJson;
import com.nhnent.eat.entity.ScenarioUnit;
import javafx.util.Pair;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Queue;
import java.util.concurrent.ConcurrentHashMap;

public class StreamPacket {

    private final Logger logger = LoggerFactory.getLogger(getClass());
    private Class packetClass = null;
    private ConcurrentHashMap<String, Object> instances = new ConcurrentHashMap<>();
    private final String scenarioParserInstance = "scenarioParser";

    private static StreamPacket obj = new StreamPacket();

    public static StreamPacket obj() {
        return obj;
    }

    /**
     * Load class from JAR file
     *
     * @param pathToJar       path of JAR file
     * @param packetClassName class name to load
     */
    public void loadClass(final String pathToJar, final String packetClassName) {
        packetClass = Util.loadClassFromJarFile(pathToJar, packetClassName);
    }

    public void initialize(String userId) {
        Object instance;
        try {
            instance = packetClass.newInstance();
            instances.put(userId, instance);
        } catch (Exception e) {
            logger.error(ExceptionUtils.getStackTrace(e));
        }
    }

    public void initSingletonInstance() {
        try {
            Object instance = packetClass.newInstance();
            ((IStreamPacket) instance).initSingletonInstance();

            initialize(scenarioParserInstance);
        } catch (InstantiationException | IllegalAccessException e) {
            logger.error(ExceptionUtils.getStackTrace(e));
        }
    }

    public final ScenarioUnit decodeScenarioHeader(String headerString, ScenarioUnit scenarioUnit) {
        return ((IStreamPacket) instances.get(scenarioParserInstance)).decodeScenarioHeader(headerString, scenarioUnit);
    }

    public String packetToJson(String userId, Object packet) {
        return ((IStreamPacket) instances.get(userId)).packetToJson(packet);
    }

    public GeneratedPacketJson packetToJson(String userId, final String expect, final Object real) throws Exception {
        return ((IStreamPacket) instances.get(userId)).packetToJson(expect, real);
    }

    public byte[] jsonToPacket(String userId, ScenarioUnit scenarioUnit) {
        return ((IStreamPacket) instances.get(userId)).jsonToPacket(scenarioUnit);
    }

    public Object decodePacket(String userId, final Class<?> clsPck, byte[] data) {
        return ((IStreamPacket) instances.get(userId)).decodePacket(clsPck, data);
    }

    public byte[] getBodyPacket(String userId, byte[] packetBytes) {
        return ((IStreamPacket) instances.get(userId)).getBodyPacket(packetBytes);
    }

    public final void receivePacket(String userId, Queue<Pair<String, byte[]>> packets, final byte[] data)
            throws Exception {
        ((IStreamPacket) instances.get(userId)).receivePacket(packets, data);
    }

}
