/**
 * 登陆 上行
 */
package com.sa.service.server;

import com.sa.base.ServerDataPool;
import com.sa.net.Packet;
import com.sa.net.PacketType;

import io.netty.channel.ChannelHandlerContext;

public class ServerLogin extends Packet {
	public ServerLogin(){}

	public ServerLogin(Integer transactionId, String roomId, String fromUserId, String toUserId, Integer status){
		super(transactionId, roomId, fromUserId, toUserId, status);
	}
	
	@Override
	public PacketType getPacketType() {
		return PacketType.ServerLogin;
	}

	@Override
	public void execPacket() {
		ChannelHandlerContext channelContext = ServerDataPool.USER_CHANNEL_MAP.get(this.getToUserId());

		if (null == channelContext) return;

		channelContext.writeAndFlush(this);
	}
}
