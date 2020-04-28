package com.sa.client;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.concurrent.ScheduledFuture;

import java.util.concurrent.TimeUnit;

import com.sa.service.server.ServerHeartBeat;

public class HeartBeatHandler extends ChannelInboundHandlerAdapter{

	private String host = "HeartBeatHandler";
	private volatile ScheduledFuture<?> heartBeatScheduler;

	public HeartBeatHandler() {}
	public HeartBeatHandler(String host) {
		this.host = host;
	}

	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg)
			throws Exception{
		if(heartBeatScheduler == null){
			System.err.println("init heartBeatScheduler---------");
			heartBeatScheduler = ctx.executor().scheduleAtFixedRate(
					new HeartBeatTask(ctx, host), 0, 5000, TimeUnit.MILLISECONDS);
		}
		ctx.fireChannelRead(msg);

	}

	private class HeartBeatTask implements Runnable{
		private final ChannelHandlerContext ctx;
		private String host;

		public HeartBeatTask(final ChannelHandlerContext ctx, String host){
			this.ctx = ctx;
			this.host = host;

		}
		@Override
		public void run() {
			this.ctx.writeAndFlush(new ServerHeartBeat(this.host));
		}

	}
}
