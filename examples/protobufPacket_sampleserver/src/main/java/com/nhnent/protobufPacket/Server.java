package com.nhnent.protobufPacket;

import com.nhnent.protobufPacket.message.IMessage;
import com.nhnent.protobufPacket.message.MSGRequestGameDecision;
import com.nhnent.protobufPacket.message.MSGRequestLogin;
import com.nhnent.protobufPacket.message.MSGRequestStartGame;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tutorial.Basic;

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

public class Server implements Runnable {

    private Selector selector;
    private HashMap<SocketChannel, String> dataMapper;
    private InetSocketAddress socketAddress;
    private final Logger logger = LoggerFactory.getLogger(getClass());

    private static final String USERID = "test123";
    private static final String PASSWORD = "pwd123";

    Basic.UserInfo userInfo;

    SocketChannel channel;

    // RPS Game
    Boolean isLogin = Boolean.FALSE;
    Boolean isGameStart = Boolean.FALSE;
    RPSGame rpsGame;

    /**
     * Initializer
     * @param address IP address
     * @param port Port number
     */
    public Server(String address, int port)
    {
        socketAddress = new InetSocketAddress(address, port);
        dataMapper = new HashMap<>();
    }

    /**
     * Run Server Socket
     */
    @Override
    public void run() {
        ServerSocketChannel socketChannel;
        try
        {
            selector = Selector.open();
            socketChannel = ServerSocketChannel.open();
            socketChannel.configureBlocking(false);

            socketChannel.socket().bind(socketAddress);
            socketChannel.register(selector, SelectionKey.OP_ACCEPT);
        }
        catch (IOException e)
        {
            logger.error(ExceptionUtils.getFullStackTrace(e));
        }

        logger.info("[SERVER] Server Started!");


        while(true)
        {
            try
            {
                selector.select();

                Iterator<?> keys = selector.selectedKeys().iterator();

                while (keys.hasNext())
                {
                    SelectionKey key = (SelectionKey) keys.next();
                    keys.remove();

                    if(!key.isValid()) continue;

                    if(key.isAcceptable())
                    {
                        accept(key);
                    }
                    else if(key.isReadable())
                    {
                        readData(key);
                    }
                }
            }
            catch (IOException | ClassNotFoundException e)
            {
                logger.error(ExceptionUtils.getStackTrace(e));
            }
        }

    }

    /**
     * Accept connection request from client
     * @param key key for client connection
     */
    private void accept(SelectionKey key)
    {
        ServerSocketChannel serverChannel = (ServerSocketChannel) key.channel();
        SocketChannel channel;

        try
        {
            channel = serverChannel.accept();
            channel.configureBlocking(false);
            Socket socket = channel.socket();
            SocketAddress remoteAddr = socket.getRemoteSocketAddress();
            logger.info("Connected to : " + remoteAddr);

            // register channel with selector for further IO
            dataMapper.put(channel, remoteAddr.toString());
            channel.register(this.selector, SelectionKey.OP_READ);

        }
        catch (IOException e)
        {
            logger.error(ExceptionUtils.getStackTrace(e));
        }
    }

    private void close(SelectionKey key) throws IOException {
        channel = (SocketChannel) key.channel();
        logger.info("client is disconnected");
        this.dataMapper.remove(channel);
        channel.close();
        key.cancel();
    }
    /**
     * Read data from client
     * @param key key for client connection
     * @throws IOException throw exception
     * @throws ClassNotFoundException throw exception
     */
    private void readData(SelectionKey key) throws IOException, ClassNotFoundException {
        channel = (SocketChannel) key.channel();
        ByteBuffer buffer = ByteBuffer.allocate(1024);
        int numRead = -1;

        try {
            numRead = channel.read(buffer);
        } catch (Exception e) {
            close(key);
            return;
        }

        if(numRead == -1)
        {
            close(key);
            return;
        }

        buffer.rewind();

        byte headerSize = buffer.get();             // read 1 byte for headerSize

        logger.debug("< HeaderSize > : {}", (int)headerSize);

        byte[] header = new byte[headerSize];

        buffer.get(header);                         // read header

        Basic.Header headerProto = Basic.Header.parseFrom(header);  // deserialize the Basic.Header

        logger.debug("[Header]MsgName : {}", headerProto.getMsgName());
        logger.debug("[Header]BodySize : {}", headerProto.getBodySize());

        int bodysize = headerProto.getBodySize();

        byte[] body = new byte[bodysize];

        buffer.get(body);

        IMessage currentMessage = null;

        // If, RequestLogin
        if(headerProto.getMsgName().equals("RequestLogin"))
            currentMessage = new MSGRequestLogin();

        //  If, RequestStartGame
        else if(headerProto.getMsgName().equals("RequestStartGame"))
            currentMessage = new MSGRequestStartGame();

        // If, RequestGameDecision
        else if(headerProto.getMsgName().equals("RequestGameDecision"))
            currentMessage = new MSGRequestGameDecision();


        // Execute command
        currentMessage.execute(this, body);

    }

    public SocketChannel getChannel() {
        return channel;
    }

    public void writeChannel(ByteBuffer byteBuffer) throws IOException {
        channel.write(byteBuffer);
    }

    public Boolean getLogin() {
        return isLogin;
    }

    public void setLogin(Boolean login) {
        isLogin = login;
    }

    public Boolean getGameStart() {
        return isGameStart;
    }

    public void setGameStart(Boolean gameStart) {
        isGameStart = gameStart;
    }

    public RPSGame getRpsGame() {
        return rpsGame;
    }

    public void setRpsGame(RPSGame rpsGame) {
        this.rpsGame = rpsGame;
    }

    public Basic.UserInfo getUserInfo(){
        return userInfo;
    }

    public void setUserInfo(Basic.UserInfo info)
    {
        userInfo = info;
    }

    public String getUserId()
    {
        return USERID;
    }

    public String getPassword()
    {
        return PASSWORD;
    }
}
