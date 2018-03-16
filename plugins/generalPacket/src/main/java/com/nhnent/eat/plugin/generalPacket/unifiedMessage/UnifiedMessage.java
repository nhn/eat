package com.nhnent.eat.plugin.generalPacket.unifiedMessage;

/**
 * Created by NHNEnt on 2017-03-19.
 */
public class UnifiedMessage {
    private UnitMessage[] unitMessage;
    private OrderedMessage[] orderedMessage;

    public UnitMessage[] getUnitMessage() {
        return unitMessage;
    }
    public void setUnitMessage(UnitMessage[] unitMessage) {
        this.unitMessage = unitMessage;
    }

    public OrderedMessage[] getOrderedMessage() {
        return orderedMessage;
    }

    public void setOrderedMessage(OrderedMessage[] orderedMessage) {
        this.orderedMessage = orderedMessage;
    }
}