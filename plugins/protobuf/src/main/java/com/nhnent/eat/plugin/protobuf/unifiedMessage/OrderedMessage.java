package com.nhnent.eat.plugin.protobuf.unifiedMessage;

/**
 * Created by NHNEnt on 2017-03-19.
 */
public class OrderedMessage {
    private String packetName;
    private String packetBytes;
    private int priority;

    public String getPacketName() {
        return packetName;
    }

    public void setPacketName(String packetName) {
        this.packetName = packetName;
    }

    public String getPacketBytes() {
        return packetBytes;
    }

    public void setPacketBytes(String packetBytes) {
        this.packetBytes = packetBytes;
    }

    public int getPriority() {
        return priority;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }
}
