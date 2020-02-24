package com.sa.client;

import java.util.HashMap;
import java.util.Map;

import com.sa.base.ConfManager;
import com.sa.base.ServerDataPool;
import com.sa.base.element.ChannelExtend;
import com.sa.net.Packet;
import com.sa.net.PacketManager;
import com.sa.service.server.ServerLogin;
import com.sa.util.HttpClientUtil;
import com.sa.util.StringUtil;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelPromise;

public class ClientTransportHandler extends ChannelInboundHandlerAdapter {
	public ClientTransportHandler() {
	}

	@Override
	public void channelActive(ChannelHandlerContext ctx) {
		// 此时通道已建立，保存通信人-通道信息，向通信人发送登陆消息
		Integer transactionId = (int) (1 + Math.random() * 100000000);
		String toUserId = StringUtil.subStringIp(ctx.channel().remoteAddress().toString());

		ServerLogin serverLogin = new ServerLogin(transactionId, "0", "0", toUserId, 0);

		// 缓存 用户-通道信息
		ServerDataPool.USER_CHANNEL_MAP.put(toUserId, ctx);
		// 缓存 通道-用户信息
		ServerDataPool.CHANNEL_USER_MAP.put(ctx, new ChannelExtend());

		serverLogin.execPacket();
	}

	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
		Packet packet = (Packet) msg;
		System.out.println(packet.toString());
		// packet.printPacket(ClientConfigs.CONSOLE_FLAG, "",
		// packet.toString());

		PacketManager.INSTANCE.execPacket(packet);
	}

	public void close(ChannelHandlerContext ctx, ChannelPromise promise) {
		System.err.println("TCP closed...");
		if (null != ctx) {
			ctx.close(promise);
		}
	}

	@Override
	public void channelInactive(ChannelHandlerContext ctx) throws Exception {
		String strLog = "服务端" + ctx.channel().remoteAddress() + "关闭channelInactive";
		if (null != ctx) {
			ctx.close();
		}
		String userId = StringUtil.subStringIp(ctx.channel().remoteAddress().toString());
		if (null != userId) {
			// 移除 用户-通道信息
			ServerDataPool.USER_CHANNEL_MAP.remove(userId);
			// 移除 通道-用户信息
			ServerDataPool.CHANNEL_USER_MAP.remove(ctx);
		}
		// TODO 通道关闭警告
		System.err.println(strLog);
		String url = ConfManager.getSendErrorMsgUrl();
		if (null != url && !"".equals(url)) {
			try {
				Map<String, String> params = new HashMap<>();
				params.put("msg", strLog);
				HttpClientUtil.post(url, params);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	public void disconnect(ChannelHandlerContext ctx, ChannelPromise promise) throws Exception {
		ctx.disconnect(promise);
		System.err.println("服务端关闭disconnect");
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
		System.err.println("服务端关闭exceptionCaught");
		// ctx.fireExceptionCaught(cause);
		cause.printStackTrace();
		if (null != ctx) {
			Channel channel = ctx.channel();
			if (channel.isActive()) {
				System.err.println("simpleclient" + channel.remoteAddress() + "异常");
			}
			// TODO 警告
			ctx.close();
		}
	}
}