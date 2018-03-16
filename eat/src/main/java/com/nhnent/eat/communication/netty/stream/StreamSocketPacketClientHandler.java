package com.nhnent.eat.communication.netty.stream;

import co.paralleluniverse.fibers.SuspendExecution;
import co.paralleluniverse.strands.Strand;
import com.nhnent.eat.communication.netty.PacketListener;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.timeout.ReadTimeoutException;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Arrays;

import static com.nhnent.eat.Main.*;


/**
 * Netty Packet client handler
 */
public class StreamSocketPacketClientHandler extends ChannelInboundHandlerAdapter {
    private final Logger logger = LoggerFactory.getLogger(getClass());
    private ChannelHandlerContext ctx = null;
    private PacketListener packetListener = null;

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        logger.debug("Channel is activated. {}", ctx.channel().toString());
        this.ctx = ctx;
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        logger.debug("Receive Packet==============================");
        ByteBuf buf = (ByteBuf) msg;
        try {
            if (packetListener != null) {
                byte[] bytes = new byte[buf.readableBytes()];
                buf.getBytes(buf.readerIndex(), bytes);
//                if (logger.isDebugEnabled()) {
                    logger.debug("Received packet: {}", Arrays.toString(bytes));
//                }
                packetListener.receivePacket(bytes);
            }
        } catch (Exception e) {
            logger.error(ExceptionUtils.getStackTrace(e));
        } finally {
            buf.release();
        }
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) {
        logger.debug("channelReadComplete");
        ctx.flush();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        if (cause instanceof ReadTimeoutException) {
            packetListener.exceptionCaught("Timeout");
        } else {
            super.exceptionCaught(ctx, cause);
            cause.printStackTrace();
            ctx.close();
        }
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
        printCountOfTransferPacket();
        if (logger.isDebugEnabled()) {
            logger.debug("Transferred packet: " + Arrays.toString(sendPck));
            logger.debug("Channel==>{}", ctx.channel().toString());
        }
        ByteBuf buffer = Unpooled.buffer(sendPck.length);
        buffer.writeBytes(sendPck);
        ctx.writeAndFlush(buffer);
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
