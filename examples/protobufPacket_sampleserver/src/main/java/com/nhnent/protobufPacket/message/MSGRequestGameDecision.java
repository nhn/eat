package com.nhnent.protobufPacket.message;

import com.google.protobuf.InvalidProtocolBufferException;
import static com.nhnent.protobufPacket.CommonUtil.*;
import com.nhnent.protobufPacket.Server;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tutorial.Basic;

import java.io.IOException;
import java.nio.ByteBuffer;

/**
 * Executed when current server receive the RequestGameDecision packet.
 */
public class MSGRequestGameDecision implements IMessage {

    ByteBuffer responseByteBuffer;
    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Override
    public void execute(Server server, byte[] packetBody) throws InvalidProtocolBufferException {

        Basic.RequestGameDecision requestGameDecision = Basic.RequestGameDecision.parseFrom(packetBody);

        server.getRpsGame().playGame(requestGameDecision.getDecision());


        // Response the game result <Packet - ResponseGameResult>
        Basic.ResponseGameResult.Builder responseGameResult_builder
                = Basic.ResponseGameResult.newBuilder();

        Basic.ResponseGameResult responseGameResult;

        responseGameResult_builder.setPlayerScore(server.getRpsGame().getCurrentPlayerScore());
        responseGameResult_builder.setPlayerCombo(server.getRpsGame().getCurrentPlayerCombo());
        responseGameResult_builder.setBotScore(server.getRpsGame().getCurrentBotScore());
        responseGameResult_builder.setBotCombo(server.getRpsGame().getCurrentBotCombo());

        responseGameResult_builder.setPlayerDecision(server.getRpsGame().getPlayerDecision());
        responseGameResult_builder.setBotDecision(server.getRpsGame().getBotDecision());

        switch (server.getRpsGame().getENUM_gameResult())
        {
            case WIN:
                responseGameResult_builder.setGameResult(Basic.ResponseGameResult.GameResult.WIN);
                break;
            case LOSE:
                responseGameResult_builder.setGameResult(Basic.ResponseGameResult.GameResult.LOSE);
                break;
            case DRAW:
                responseGameResult_builder.setGameResult(Basic.ResponseGameResult.GameResult.DRAW);
                break;
            default:
                responseGameResult_builder.setGameResult(null);
                break;
        }

        responseGameResult = responseGameResult_builder.build();

        Basic.Header basicHeader =
                getHeader("ResponseGameResult", responseGameResult.getSerializedSize());

        responseByteBuffer =
                ByteBuffer.allocate(basicHeader.getSerializedSize() + responseGameResult.getSerializedSize() + 1);

        responseByteBuffer.put((byte)basicHeader.getSerializedSize());
        responseByteBuffer.put(basicHeader.toByteArray());
        responseByteBuffer.put(responseGameResult.toByteArray());

        responseByteBuffer.rewind();

        try {
            server.writeChannel(responseByteBuffer);
        } catch (IOException e) {
            logger.error(ExceptionUtils.getStackTrace(e));
        }

        responseByteBuffer.clear();

        if(logger.isDebugEnabled()) {
            logger.debug("=========================================");
            logger.debug("[RPSGame] Turn : {}", server.getRpsGame().getTurn());
            logger.debug("[RPSGame] RESULT : {}", server.getRpsGame().getENUM_gameResult());
            logger.debug("-----------------------------------------");
            logger.debug("[RPSGame] PLAYER Decision : {} / Combo : {} / SCORE : {}",
                    server.getRpsGame().getPlayerDecision(),
                    server.getRpsGame().getCurrentPlayerCombo(),
                    server.getRpsGame().getCurrentPlayerScore());
            logger.debug("[RPSGame] BOT Decision : {} / Combo : {} / SCORE : {}",
                    server.getRpsGame().getBotDecision(),
                    server.getRpsGame().getCurrentBotCombo(),
                    server.getRpsGame().getCurrentBotScore());
            logger.debug("** ROCK == 0, PAPER == 1, SCISSOR == 2 **");
            logger.debug("=========================================");
        }
    }

}
