package com.nhnent.eat.communication.netty;

import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.handler.timeout.IdleStateHandler;
import io.netty.handler.timeout.ReadTimeoutException;

import java.util.concurrent.TimeUnit;

class ReadTimeoutWithNoClose extends IdleStateHandler {

    /**
     * Creates a new instance.
     *
     * @param timeoutSeconds read timeout in seconds
     */
    public ReadTimeoutWithNoClose(int timeoutSeconds) {
        this(timeoutSeconds, TimeUnit.SECONDS);
    }

    /**
     * Creates a new instance.
     *
     * @param timeout read timeout
     * @param unit    the {@link TimeUnit} of {@code timeout}
     */
    private ReadTimeoutWithNoClose(long timeout, TimeUnit unit) {
        super(timeout, 0, 0, unit);
    }

    @Override
    protected final void channelIdle(ChannelHandlerContext ctx, IdleStateEvent evt) throws Exception {
        assert evt.state() == IdleState.READER_IDLE;
        readTimedOut(ctx);
    }

    /**
     * Is called when a read timeout was detected.
     */
    private void readTimedOut(ChannelHandlerContext ctx) throws Exception {
        ctx.fireExceptionCaught(ReadTimeoutException.INSTANCE);
    }
}