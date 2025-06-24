package org.example;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpServerCodec;

import java.util.concurrent.CountDownLatch;

public class HttpServerInitializer extends ChannelInitializer<SocketChannel> {

	final CountDownLatch latch;

	HttpServerInitializer(CountDownLatch serverChannelLatch) {
		this.latch = serverChannelLatch;
	}

	@Override
	public void initChannel(SocketChannel ch) {
		ch.pipeline().addLast(new HttpServerCodec(), new HttpServerHandler());

		latch.countDown();
	}
}
