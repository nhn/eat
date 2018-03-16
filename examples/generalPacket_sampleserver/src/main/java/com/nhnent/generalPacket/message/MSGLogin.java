package com.nhnent.generalPacket.message;

import com.nhnent.generalPacket.packets.Login;
import com.nhnent.generalPacket.packets.LoginResponse;
import com.nhnent.generalPacket.packets.MessageEnvelop;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

import static com.nhnent.generalPacket.CommonUtil.serialize;

/**
 * Executed when current server receive Login packet.
 */
public class MSGLogin implements IMessage {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Override
    public void execute(SocketChannel channel, Object message) {

        Login login = (Login)message;
        logger.info("[SERVER][Login] id:{}, gameId:{}", login.getId(), login.getGameId());

        LoginResponse loginResponse = new LoginResponse();
        loginResponse.setCode(100);
        loginResponse.setDescription("Welcome to Game");

        MessageEnvelop sendMessage = new MessageEnvelop();
        sendMessage.setMessageName("LoginResponse");
        sendMessage.setMessage(loginResponse);

        try {
            channel.write(ByteBuffer.wrap(serialize(sendMessage)));
        } catch (IOException e) {
            logger.error(ExceptionUtils.getStackTrace(e));
        }
    }
}
