package com.nhnent.generalPacket.packets;

import java.io.Serializable;

public class EnterLobby implements Serializable{
    private String gameChannel;

    public String getGameChannel() {
        return gameChannel;
    }

    public void setGameChannel(String gameChannel) {
        this.gameChannel = gameChannel;
    }
}
