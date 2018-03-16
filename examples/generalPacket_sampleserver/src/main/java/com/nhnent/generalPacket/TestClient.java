package com.nhnent.generalPacket;

import com.nhnent.generalPacket.packets.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

import static com.nhnent.generalPacket.CommonUtil.deserialize;
import static com.nhnent.generalPacket.CommonUtil.serialize;

/**
 * You can also test the sample server by execute this test client without EAT.
 */
public class TestClient {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    public void startClient() throws IOException, InterruptedException, ClassNotFoundException {

        InetSocketAddress hostAddress = new InetSocketAddress("localhost", 31203);
        SocketChannel client = SocketChannel.open(hostAddress);

        logger.info("[CLIENT] Client Started!");

        sendLogin(client);

        sendEnterLobby(client);

        //Sleep to waiting for receive packet
        Thread.sleep(10000);

        //Close client socket
        client.close();
    }

    /**
     * Send Login, and receive LoginResponse
     * @param client SocketChannel for client
     * @throws IOException throw exception
     * @throws ClassNotFoundException throw exception
     */
    public void sendLogin(SocketChannel client) throws IOException, ClassNotFoundException {
        ByteBuffer buffer = ByteBuffer.allocate(1024);
        MessageEnvelop recvMessage;
        MessageEnvelop message = new MessageEnvelop();

        buffer.clear();
        Login login = new Login();
        login.setId("foo");
        login.setGameId("go");
        message.setMessageName("Login");
        message.setMessage(login);
        client.write(ByteBuffer.wrap(serialize(message)));

        client.read(buffer);
        recvMessage = (MessageEnvelop)deserialize(buffer.array());
        LoginResponse loginResponse = (LoginResponse)recvMessage.getMessage();
        logger.info("[CLIENT][LoginResponse] code:{}, desc:{}",
                loginResponse.getCode(), loginResponse.getDescription());
    }

    /**
     * Send EnterLobby, and receive EnterLobbyResponse
     * @param client SocketChannel for client
     * @throws IOException throw exception
     * @throws ClassNotFoundException throw exception
     */
    private void sendEnterLobby(SocketChannel client) throws IOException, ClassNotFoundException {
        ByteBuffer buffer = ByteBuffer.allocate(1024);
        MessageEnvelop recvMessage;
        MessageEnvelop message = new MessageEnvelop();

        buffer.clear();
        EnterLobby enterLobby = new EnterLobby();
        enterLobby.setGameChannel("100");
        message.setMessageName("EnterLobby");
        message.setMessage(enterLobby);
        client.write(ByteBuffer.wrap(serialize(message)));

        client.read(buffer);
        recvMessage = (MessageEnvelop)deserialize(buffer.array());
        EnterLobbyResponse enterLobbyResponse = (EnterLobbyResponse)recvMessage.getMessage();
        logger.info("[CLIENT][EnterLobbyResponse] code:{}, desc:{}",
                enterLobbyResponse.getCode(), enterLobbyResponse.getDescription());
    }
}
