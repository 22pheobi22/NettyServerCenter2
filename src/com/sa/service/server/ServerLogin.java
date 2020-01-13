/**
 * 登陆 上行
 */
package com.sa.service.server;

import com.sa.base.ServerDataPool;
import com.sa.net.Packet;
import com.sa.net.PacketType;
import com.sa.service.client.ClientMsgReceipt;

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
		// 用户登录成功
		ClientMsgReceipt clientMsgReceipt = new ClientMsgReceipt(this.getPacketHead());
		clientMsgReceipt.setOptions(this.getOptions());

		clientMsgReceipt.execPacket();
	}
	
	public void centerExecPacket() {
		ChannelHandlerContext channelContext = ServerDataPool.USER_CHANNEL_MAP.get(this.getToUserId());

		if (null == channelContext) return;

		channelContext.writeAndFlush(this);
	}
}
