package org.example;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpClientCodec;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;

import java.util.concurrent.CountDownLatch;

public class HttpClientInitializer extends ChannelInitializer<SocketChannel> {

	final CountDownLatch latch;

	HttpClientInitializer(CountDownLatch latch) {
		this.latch = latch;
	}

	@Override
	public void initChannel(SocketChannel ch) {
		ch.pipeline().addLast(new LoggingHandler(LogLevel.DEBUG), new HttpClientCodec(), new HttpClientHandler(latch));
	}
}
