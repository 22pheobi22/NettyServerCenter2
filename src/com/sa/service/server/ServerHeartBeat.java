package com.sa.service.server;

import com.sa.base.CenterManager;
import com.sa.base.ServerDataPool;
import com.sa.net.Packet;
import com.sa.net.PacketType;
import com.sa.service.client.ClientHeartBeat;

import io.netty.channel.ChannelHandlerContext;

public class ServerHeartBeat extends Packet{

	public ServerHeartBeat() {
		this.setTransactionId(0);
		this.setRoomId("0");
		this.setFromUserId("0");
		this.setToUserId("0");
		this.setStatus(0);

		this.setOption(1, "check");
	}

	@Override
	public PacketType getPacketType() {
		return PacketType.ServerHearBeat;
	}

	@Override
	public void execPacket() {
		ClientHeartBeat clientHeartBeat = new ClientHeartBeat();

		clientHeartBeat.setPacketHead(this.getPacketHead());
		boolean check = new CenterManager().checkServerLink();

		if (!check) {
			clientHeartBeat.setOption(1, String.valueOf(check));
			ChannelHandlerContext bakCtx = ServerDataPool.USER_CHANNEL_MAP.get("0");
			bakCtx.writeAndFlush(clientHeartBeat);
		}
	}

}
