package com.nhnent.generalPacket.packets;

import java.io.Serializable;

public class MessageEnvelop implements Serializable {
    private String messageName;
    private Object message;

    public String getMessageName() {
        return messageName;
    }

    public void setMessageName(String messageName) {
        this.messageName = messageName;
    }

    public Object getMessage() {
        return message;
    }

    public void setMessage(Object message) {
        this.message = message;
    }
}
