package com.nhnent.eat.communication.netty.ws;

import co.paralleluniverse.fibers.SuspendExecution;
import co.paralleluniverse.strands.Strand;
import com.nhnent.eat.communication.netty.PacketListener;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.websocketx.*;
import io.netty.util.CharsetUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Arrays;

import static com.nhnent.eat.Main.*;

/**
 * @author Stephen Mallette (http://stephen.genoprime.com)
 */
public class WebSocketClientHandler extends SimpleChannelInboundHandler<Object> {
    private final Logger logger = LoggerFactory.getLogger(getClass());

    private final WebSocketClientHandshaker handshaker;
    private ChannelPromise handshakeFuture;

    private PacketListener packetListener = null;
    private ChannelHandlerContext ctx = null;

    public WebSocketClientHandler(final WebSocketClientHandshaker handshaker) {
        this.handshaker = handshaker;
    }

    public ChannelFuture handshakeFuture() {
        return handshakeFuture;
    }

    @Override
    public void handlerAdded(final ChannelHandlerContext ctx) throws Exception {
        handshakeFuture = ctx.newPromise();
    }

    @Override
    public void channelActive(final ChannelHandlerContext ctx) throws Exception {
        handshaker.handshake(ctx.channel());
        this.ctx = ctx;
    }

    @Override
    public void channelInactive(final ChannelHandlerContext ctx) throws Exception {
        //System.out.println("WebSocket Client disconnected!");
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Object msg) throws Exception {
        final Channel ch = ctx.channel();
        if (!handshaker.isHandshakeComplete()) {
            // web socket client connected
            handshaker.finishHandshake(ch, (FullHttpResponse) msg);
            handshakeFuture.setSuccess();
            return;
        }

        if (msg instanceof FullHttpResponse) {
            final FullHttpResponse response = (FullHttpResponse) msg;
            throw new Exception("Unexpected FullHttpResponse (getStatus=" + response.getStatus() + ", content="
                                        + response.content().toString(CharsetUtil.UTF_8) + ')');
        }

        final WebSocketFrame frame = (WebSocketFrame) msg;
        if (frame instanceof TextWebSocketFrame) {
            final TextWebSocketFrame textFrame = (TextWebSocketFrame) frame;
            // uncomment to print execute
            // logger.info(textFrame.text());
        } else if (frame instanceof PongWebSocketFrame) {
            // Nothing to do
        } else if (frame instanceof CloseWebSocketFrame)
            ch.close();
        else if (frame instanceof BinaryWebSocketFrame) {
            // uncomment to print execute
            // logger.info(frame.content().toString());

            ByteBuf buf= frame.content();
            if (packetListener != null) {
                byte[] bytes = new byte[buf.readableBytes()];
                buf.getBytes(buf.readerIndex(), bytes);
                if (logger.isDebugEnabled()) {
                    logger.debug("Received packet: {}", Arrays.toString(bytes));
                }
                packetListener.receivePacket(bytes);
            }
        }

    }

    @Override
    public void exceptionCaught(final ChannelHandlerContext ctx, final Throwable cause) throws Exception {
        cause.printStackTrace();

        if (!handshakeFuture.isDone()) {
            handshakeFuture.setFailure(cause);
        }

        ctx.close();
    }

    /**
     * Set Packet Listener
     *
     * @param packetListener PacketListener
     */
    public void setPacketListener(PacketListener packetListener) {
        this.packetListener = packetListener;
    }

    /**
     * Transfer packet to server
     *
     * @param sendPck Packet to transmit
     * @throws SuspendExecution     Throw exception
     * @throws InterruptedException Throw exception
     */
    public void transferPacket(final byte[] sendPck) throws InterruptedException, SuspendExecution {
        while (ctx == null) {
            Strand.sleep(100);
        }
        while (!handshaker.isHandshakeComplete()) {
            Strand.sleep(100);
            logger.debug("handshake is not completed");
        }
        printCountOfTransferPacket();
        if (logger.isDebugEnabled()) {
            logger.debug("Transferred packet: " + Arrays.toString(sendPck));
            logger.debug("Channel==>{}", ctx.channel().toString());
        }
        ByteBuf buffer = Unpooled.buffer(sendPck.length);
        buffer.writeBytes(sendPck);

        WebSocketFrame frame = new BinaryWebSocketFrame(buffer);

        ctx.writeAndFlush(frame);
    }

    /**
     * Print count of Transferred Packet
     */
    private void printCountOfTransferPacket() {
        transmitDataCountPerPeriod++;
        transmitDataTotalCount++;
        long now = System.currentTimeMillis();
        double elapsedTime = (now - lastTransmitTime) / 1000.0;
        NumberFormat formatter = new DecimalFormat("#0.00");
        if (elapsedTime > 10) {
            logger.info("Transferred packet count(during {} sec.) : {}", formatter.format(elapsedTime), transmitDataCountPerPeriod);
            transmitDataCountPerPeriod = 0;
            lastTransmitTime = System.currentTimeMillis();
        }
    }

    /**
     * close netty socket
     */
    public void close() {
        logger.debug("close");
        if (ctx != null) ctx.close();
    }
}

