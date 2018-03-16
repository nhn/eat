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
 * Executed when current server receive the RequestLogin packet.
 */
public class MSGRequestLogin implements IMessage{

    ByteBuffer responseByteBuffer;
    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Override
    public void execute(Server server, byte[] packetBody) throws InvalidProtocolBufferException {

        Basic.UserInfo.Builder userInfo_builder = Basic.UserInfo.newBuilder();
        Basic.UserInfo userInfo;

        LOGINRESULT loginRes;

        // <Protobuf Packet - RequestLogin> - parsing the body packet
        Basic.RequestLogin requestLogin = null;

        requestLogin = Basic.RequestLogin.parseFrom(packetBody);


        if(requestLogin.getUserId().equals(server.getUserId())
                && requestLogin.getPassword().equals(server.getPassword())) {
            loginRes = LOGINRESULT.SUCCESS;     // Login Success

            userInfo_builder.setUserId(requestLogin.getUserId());
            userInfo_builder.setUserName(requestLogin.getUserId()+"_name");

            logger.debug("[SERVER] Login Success");
        }
        else {
            loginRes = LOGINRESULT.FAIL;        // Login Fail

            userInfo_builder.setUserId("Invalid_UserID");
            userInfo_builder.setUserName("Invalid_UserName");

            logger.debug("[SERVER] Invalid User");
        }

        userInfo = userInfo_builder.build();

        logger.debug("[SERVER] UserID : {}", userInfo.getUserId());
        logger.debug("[SERVER] UserName : {}", userInfo.getUserName());

        Basic.ResponseLogin.Builder responseLogin_builer = Basic.ResponseLogin.newBuilder();
        Basic.ResponseLogin responseLogin;

        responseLogin_builer.setResultUserInfo(userInfo);

        if(loginRes == LOGINRESULT.SUCCESS)
            responseLogin_builer.setResultType(Basic.ResultType.SUCCESS);
        else
            responseLogin_builer.setResultType(Basic.ResultType.FAIL);

        responseLogin = responseLogin_builer.build();

        Basic.Header basicHeader =
                getHeader("ResponseLogin", responseLogin.getSerializedSize());

        responseByteBuffer =
                ByteBuffer.allocate(basicHeader.getSerializedSize() + responseLogin.getSerializedSize() + 1);

        responseByteBuffer.put((byte)basicHeader.getSerializedSize());
        responseByteBuffer.put(basicHeader.toByteArray());
        responseByteBuffer.put(responseLogin.toByteArray());

        responseByteBuffer.rewind();

        try {
            server.writeChannel(responseByteBuffer);
        } catch (IOException e) {
            logger.error(ExceptionUtils.getStackTrace(e));
        }

        responseByteBuffer.clear();

        server.setLogin(Boolean.TRUE);
        server.setUserInfo(userInfo);
    }

}
