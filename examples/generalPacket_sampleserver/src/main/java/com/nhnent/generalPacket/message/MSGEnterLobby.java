package com.nhnent.generalPacket.message;

import com.nhnent.generalPacket.packets.EnterLobby;
import com.nhnent.generalPacket.packets.EnterLobbyResponse;
import com.nhnent.generalPacket.packets.MessageEnvelop;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

import static com.nhnent.generalPacket.CommonUtil.serialize;

/**
 * Executed when current server receive EnterLobby packet.
 */
public class MSGEnterLobby implements IMessage {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Override
    public void execute(SocketChannel channel, Object message) {

        EnterLobby enterLobby = (EnterLobby)message;
        logger.info("[SERVER][EnterLobby] gameChannel:{}", enterLobby.getGameChannel());

        EnterLobbyResponse enterLobbyResponse = new EnterLobbyResponse();
        enterLobbyResponse.setCode(100);
        enterLobbyResponse.setDescription("Welcome to Lobby");

        MessageEnvelop sendMessage = new MessageEnvelop();
        sendMessage.setMessageName("EnterLobbyResponse");
        sendMessage.setMessage(enterLobbyResponse);

        try {
            channel.write(ByteBuffer.wrap(serialize(sendMessage)));
        } catch (IOException e) {
            logger.error(ExceptionUtils.getStackTrace(e));
        }
    }
}
