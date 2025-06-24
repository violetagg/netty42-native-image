package org.example;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.HttpObject;
import io.netty.handler.codec.http.LastHttpContent;

import java.util.concurrent.CountDownLatch;

public class HttpClientHandler extends SimpleChannelInboundHandler<HttpObject> {

	final CountDownLatch latch;

	HttpClientHandler(CountDownLatch latch) {
		this.latch = latch;
	}

	@Override
	public void channelRead0(ChannelHandlerContext ctx, HttpObject msg) {
		if (msg instanceof LastHttpContent) {
			latch.countDown();
			ctx.close();
		}
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
		ctx.close();
		ctx.fireExceptionCaught(cause);
	}
}
