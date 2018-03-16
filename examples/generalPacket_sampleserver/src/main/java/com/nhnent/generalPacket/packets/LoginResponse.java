package com.nhnent.generalPacket.packets;

import java.io.Serializable;

public class LoginResponse implements Serializable {
    private int code;
    private String description;

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }
}
