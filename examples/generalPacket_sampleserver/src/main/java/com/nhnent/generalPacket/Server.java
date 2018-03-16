package com.nhnent.generalPacket;

import com.nhnent.generalPacket.message.IMessage;
import com.nhnent.generalPacket.message.MSGEnterLobby;
import com.nhnent.generalPacket.message.MSGLogin;
import com.nhnent.generalPacket.packets.*;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.HashMap;
import java.util.Iterator;

import static com.nhnent.generalPacket.CommonUtil.deserialize;

public class Server implements Runnable {

    private Selector selector;private HashMap<SocketChannel,String> dataMapper;
    private InetSocketAddress socketAddress;
    private final Logger logger = LoggerFactory.getLogger(getClass());

    /**
     * Initializer
     * @param address IP address
     * @param port Port no
     */
    public Server(String address, int port) {
        socketAddress = new InetSocketAddress(address, port);
        dataMapper = new HashMap<>();
    }

    /**
     * Run Server Socket
     */
    @Override
    public void run() {
        try {
            selector = Selector.open();
            ServerSocketChannel socketChannel = ServerSocketChannel.open();
            socketChannel.configureBlocking(false);

            socketChannel.socket().bind(socketAddress);
            socketChannel.register(selector, SelectionKey.OP_ACCEPT);

        } catch (IOException e) {
            logger.error(ExceptionUtils.getStackTrace(e));
        }

        logger.info("[SERVER] Server Started!");

        while (true) {
            try {
                selector.select();

                Iterator<?> keys = selector.selectedKeys().iterator();

                while (keys.hasNext()) {

                    SelectionKey key = (SelectionKey) keys.next();
                    keys.remove();

                    if (!key.isValid()) {
                        continue;
                    }

                    if (key.isAcceptable()) {
                        accept(key);
                    }

                    else if (key.isReadable()) {
                        readData(key);
                    }
                }

            } catch (IOException|ClassNotFoundException e) {
                logger.error(ExceptionUtils.getStackTrace(e));
            }
        }

    }

    /**
     * Accept connection request from client
     * @param key key for client connection
     */
    private void accept(SelectionKey key) {
        ServerSocketChannel serverChannel = (ServerSocketChannel) key.channel();
        SocketChannel channel;

        try {
            channel = serverChannel.accept();
            channel.configureBlocking(false);
            Socket socket = channel.socket();
            SocketAddress remoteAddr = socket.getRemoteSocketAddress();
            logger.info("Connected to: " + remoteAddr);

            // register channel with selector for further IO
            dataMapper.put(channel, remoteAddr.toString());
            channel.register(this.selector, SelectionKey.OP_READ);

        } catch (IOException e) {
            logger.error(ExceptionUtils.getStackTrace(e));
        }

    }

    /**
     * Read data from client
     * @param key key for client connection
     * @throws IOException throw exception
     * @throws ClassNotFoundException throw exception
     */
    private void readData(SelectionKey key) throws IOException, ClassNotFoundException {

        SocketChannel channel = (SocketChannel) key.channel();
        ByteBuffer buffer = ByteBuffer.allocate(1024);
        int numRead = -1;

        try {
            numRead = channel.read(buffer);

            if (numRead == -1) {
                this.dataMapper.remove(channel);
                Socket socket = channel.socket();
                SocketAddress remoteAddr = socket.getRemoteSocketAddress();
                channel.close();
                key.cancel();
                return;
            }

        } catch (IOException e) {
            logger.error(ExceptionUtils.getStackTrace(e));
        }

        MessageEnvelop recvMessage = (MessageEnvelop)deserialize(buffer.array());

        IMessage currentMessage = null;

        logger.info("[SERVER]Receive Packet: {}", recvMessage.getMessageName());

        // If, Login Request
        if(recvMessage.getMessageName().equals("Login"))
            currentMessage = new MSGLogin();

        // If, EnterLobby Request
        else if(recvMessage.getMessageName().equals("EnterLobby"))
            currentMessage = new MSGEnterLobby();

        // Execute the current message
        currentMessage.execute(channel, recvMessage.getMessage());
    }
}
