package com.nhnent.protobufPacket;

import tutorial.Basic;

public class CommonUtil {

    public enum LOGINRESULT {
        SUCCESS,
        FAIL
    }

    public enum GAMESTATE {
        BEFORE_GAMESTART,
        DECISION_MAKING,
        RESULT
    }

    public enum GAMERESULT {
        WIN,
        LOSE,
        DRAW,
        NONE
    }

    /**
     * Generates a packet header
     * @param messageName message name of body
     * @param bodySize body size
     * @return packet header which defined by protobuf (basic.proto)
     */
    public static Basic.Header getHeader(String messageName, int bodySize)
    {
        Basic.Header.Builder basicHeader_builder = Basic.Header.newBuilder();

        basicHeader_builder.setMsgName(messageName);
        basicHeader_builder.setBodySize(bodySize);

        return basicHeader_builder.build();
    }
}
