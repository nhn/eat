package com.nhnent.eat.packets;

import com.nhnent.eat.entity.GeneratedPacketJson;
import javafx.util.Pair;

import java.util.Queue;

public interface IStreamPacket {

    /**
     * Initialize singleton instance.
     * If stream packet class needs initialize singleton instance,
     * We can put code within this function
     */
    void initSingletonInstance();

    /**
     * Decode packet(which is consisted of byte array) with given class
     * @param clsPck Class of packet
     * @param data packet byte
     * @return decoded packet instance
     */
    Object decodePacket(final Class<?> clsPck, byte[] data);

    /**
     * Get body packet from given packet(which is consist of Header and Body)
     * @param packetBytes Whole packet
     * @return Extracted Body Packet
     */
    byte[] getBodyPacket(byte[] packetBytes);

    /**
     * Generate Packet json from given packet
     * @param packet packet instance
     * @return generated json string
     */
    String packetToJson(Object packet);

    /**
     * Generate Packet json(for both Expected and Real)
     * @param expect expected receiving packet
     * @param real received packet
     * @return generated json(for both Expected and Real)
     * @throws Exception throw exception
     */
    GeneratedPacketJson packetToJson(final String expect, final Object real) throws Exception;

    /**
     * Generate packet byte with given information
     * @param packetType packet type
     * @param packageName package name of packet
     * @param packetName packet class name
     * @param jsonContents json string of packet
     * @return generated packet byte
     */
    byte[] jsonToPacket(String packetType, String packageName, String packetName, String jsonContents);


    /**
     * Receive packet bytes and decode and put it to packet queue
     * @param packets packet queue
     * @param data received packet bytes
     * @throws Exception throw exception
     */
    void receivePacket(Queue<Pair<String, byte[]>> packets, final byte[] data) throws Exception;
}
