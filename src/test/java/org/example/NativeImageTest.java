package org.example;

import io.netty.bootstrap.Bootstrap;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.MultiThreadIoEventLoopGroup;
import io.netty.channel.nio.NioIoHandler;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.http.DefaultHttpRequest;
import io.netty.handler.codec.http.HttpMethod;
import io.netty.handler.codec.http.HttpVersion;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.util.NetUtil;
import org.junit.jupiter.api.Test;

import java.net.InetSocketAddress;
import java.util.concurrent.CountDownLatch;

import static java.util.concurrent.TimeUnit.SECONDS;
import static org.junit.jupiter.api.Assertions.assertTrue;

class NativeImageTest {

	@Test
	void test1() throws InterruptedException {
		ServerBootstrap sb = new ServerBootstrap();
		Bootstrap cb = new Bootstrap();
		try {
			CountDownLatch serverChannelLatch = new CountDownLatch(1);
			sb.group(new MultiThreadIoEventLoopGroup(NioIoHandler.newFactory()))
					.channel(NioServerSocketChannel.class)
					.handler(new LoggingHandler(LogLevel.DEBUG))
					.childHandler(new HttpServerInitializer(serverChannelLatch));

			CountDownLatch responseReceivedLatch = new CountDownLatch(1);
			cb.group(new MultiThreadIoEventLoopGroup(NioIoHandler.newFactory()))
					.channel(NioSocketChannel.class)
					.handler(new HttpClientInitializer(responseReceivedLatch));

			Channel serverChannel = sb.bind(new InetSocketAddress(0)).sync().channel();
			int port = ((InetSocketAddress) serverChannel.localAddress()).getPort();

			ChannelFuture ccf = cb.connect(new InetSocketAddress(NetUtil.LOCALHOST, port));
			assertTrue(ccf.awaitUninterruptibly().isSuccess());
			Channel clientChannel = ccf.channel();
			assertTrue(serverChannelLatch.await(5, SECONDS));
			clientChannel.writeAndFlush(new DefaultHttpRequest(HttpVersion.HTTP_1_1, HttpMethod.GET, "/"));
			assertTrue(responseReceivedLatch.await(5, SECONDS));
		}
		finally {
			sb.config().group().shutdownGracefully();
			sb.config().childGroup().shutdownGracefully();
			cb.config().group().shutdownGracefully();
		}
	}
}
