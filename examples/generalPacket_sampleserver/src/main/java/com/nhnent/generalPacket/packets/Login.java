package com.nhnent.generalPacket.packets;

import java.io.Serializable;

public class Login implements Serializable {
    private String id;
    private String gameId;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getGameId() {
        return gameId;
    }

    public void setGameId(String gameId) {
        this.gameId = gameId;
    }
}
