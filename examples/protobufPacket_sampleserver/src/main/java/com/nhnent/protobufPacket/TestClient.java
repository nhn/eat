package com.nhnent.protobufPacket;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tutorial.Basic;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

/**
 * You can also test the sample server by execute this test client without EAT.
 */
public class TestClient {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    public void startClient() throws IOException, InterruptedException, ClassNotFoundException {

        InetSocketAddress hostAddress = new InetSocketAddress("localhost", 31203);
        SocketChannel client = SocketChannel.open(hostAddress);

        logger.info("[Client] Client started!");

        requestLogin(client);

        Thread.sleep(10000);


        logger.info("Client close");
        client.close();
    }


    public void requestLogin(SocketChannel client) throws IOException, ClassNotFoundException {
        ByteBuffer buffer = ByteBuffer.allocate(1024);

        Basic.RequestLogin.Builder requestLogin_builder = Basic.RequestLogin.newBuilder();
        Basic.RequestLogin requestLogin;

        Basic.Header.Builder basicHeader_builder = Basic.Header.newBuilder();
        Basic.Header basicHeader;

        requestLogin_builder.setUserId("test123");
        requestLogin_builder.setPassword("1234");

        requestLogin = requestLogin_builder.build();

        basicHeader_builder.setMsgName("RequestLogin");
        basicHeader_builder.setBodySize(requestLogin.getSerializedSize());

        basicHeader = basicHeader_builder.build();

        buffer.put((byte)basicHeader.getSerializedSize());
        buffer.put(basicHeader.toByteArray());
        buffer.put(requestLogin.toByteArray());

        buffer.rewind();

        client.write(buffer);

        buffer.clear();

        // response
        client.read(buffer);

        buffer.rewind();

        byte headerSize = buffer.get();             // read 1 byte for headerSize

        logger.info("[Client] < HeaderSize > : {}", (int)headerSize);

        byte[] header = new byte[headerSize];

        buffer.get(header);                         // read header

        Basic.Header headerProto = Basic.Header.parseFrom(header);  // deserialize the Basic.Header

        logger.debug("[Client - Received Header]MsgName : {}", headerProto.getMsgName());
        logger.debug("[Client - Received Header]BodySize : {}", headerProto.getBodySize());

        byte[] body = new byte[headerProto.getBodySize()];

        buffer.get(body);

        Basic.ResponseLogin responseLogin = Basic.ResponseLogin.parseFrom(body);

        logger.debug("[Client - Received Body] UserInfo : {}", responseLogin.getResultUserInfo());
        logger.debug("[Client - Received Body] ResultType : {}", responseLogin.getResultType());


    }

}
