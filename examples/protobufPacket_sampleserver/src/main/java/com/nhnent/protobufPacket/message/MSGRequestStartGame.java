package com.nhnent.protobufPacket.message;

import com.google.protobuf.InvalidProtocolBufferException;
import static com.nhnent.protobufPacket.CommonUtil.*;
import com.nhnent.protobufPacket.RPSGame;
import com.nhnent.protobufPacket.Server;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tutorial.Basic;

import java.io.IOException;
import java.nio.ByteBuffer;

/**
 * Executed when current server receive RequestStartGame packet.
 */
public class MSGRequestStartGame implements IMessage {

    ByteBuffer responseByteBuffer;
    private final Logger logger = LoggerFactory.getLogger(getClass());

    RPSGame rpsGame;

    @Override
    public void execute(Server server, byte[] packetBody) throws InvalidProtocolBufferException {

        server.setGameStart(Boolean.TRUE);

        Basic.RequestStartGame requestStartGame = Basic.RequestStartGame.parseFrom(packetBody);

        rpsGame = new RPSGame(server.getUserInfo().getUserId());

        rpsGame.setENUM_gameState(GAMESTATE.DECISION_MAKING);

        Basic.ResponseStartGame.Builder responseStartGame_builer = Basic.ResponseStartGame.newBuilder();
        Basic.ResponseStartGame responseStartGame;

        responseStartGame_builer.setResultType(Basic.ResultType.SUCCESS);
        responseStartGame = responseStartGame_builer.build();

        Basic.Header basicHeader =
                getHeader("ResponseStartGame", responseStartGame.getSerializedSize());

        responseByteBuffer =
                ByteBuffer.allocate(basicHeader.getSerializedSize() + responseStartGame.getSerializedSize() + 1);

        responseByteBuffer.put((byte)basicHeader.getSerializedSize());
        responseByteBuffer.put(basicHeader.toByteArray());
        responseByteBuffer.put(responseStartGame.toByteArray());

        responseByteBuffer.rewind();

        try {
            server.writeChannel(responseByteBuffer);
        } catch (IOException e) {
            logger.error(ExceptionUtils.getStackTrace(e));
        }

        responseByteBuffer.clear();

        logger.debug("[RPSGame] Game Start!");
        logger.debug("[RPSGame] PLAYER Score : {}", rpsGame.getCurrentPlayerScore());
        logger.debug("[RPSGame] BOT Score : {}", rpsGame.getCurrentBotScore());

        server.setRpsGame(rpsGame);
    }
}
