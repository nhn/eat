package com.nhnent.eat.communication.netty.ws;

import com.nhnent.eat.common.Config.Config;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.net.ssl.SSLException;
import java.io.File;

public class SslHandler {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    public static SslContext initSSL() {
        Logger logger = LoggerFactory.getLogger("com.nhnent.eat.communication.netty.ws.SslHandler");

        SslContext sslContext =
                getSslContext(Config.obj().getServer().getSsl().getKeyCertChainPath(),
                              Config.obj().getServer().getSsl().getPrivateKeyPath(),
                              Config.obj().getServer().getSsl().getKeyPassword()
                );

        if(sslContext !=null){
            logger.info("use ssl");
        }
        return sslContext;
    }

    private static SslContext getSslContext(String keyCertChain, String privateKey, String keyPassword){
        SslContext sslCtx =  null;
        Logger logger = LoggerFactory.getLogger("com.nhnent.eat.communication.netty.ws.SslHandler");

        if(!keyCertChain.isEmpty() && !privateKey.isEmpty()){
            File crtFile = new File(keyCertChain);
            File privateKeyFile = new File(privateKey);


            try {
                //sslCtx = SslContext.newServerContext(crtFile, pkFile,"1111");
                if(keyPassword.isEmpty()){
                    sslCtx = SslContextBuilder.forServer(crtFile, privateKeyFile).build();
                }else{
                    sslCtx = SslContextBuilder.forServer(crtFile,privateKeyFile,keyPassword).build();
                }


            } catch (SSLException e) {
                logger.error(ExceptionUtils.getStackTrace(e));
            }
        }

        return  sslCtx;
    }
}
