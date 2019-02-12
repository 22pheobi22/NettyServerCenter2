package com.sa.service.client;

import io.netty.channel.ChannelHandlerContext;

import java.util.TreeMap;

import com.sa.base.ServerDataPool;
import com.sa.base.ServerManager;
import com.sa.net.Packet;
import com.sa.net.PacketHeadInfo;
import com.sa.net.PacketType;
import com.sa.util.Constant;

public class ClientLoginOut extends Packet {

	public ClientLoginOut() {}

	public ClientLoginOut(PacketHeadInfo packetHead) {
		this.setPacketHead(packetHead);
	}

	public ClientLoginOut(PacketHeadInfo packetHead, TreeMap<Integer, Object> options) {
		this.setPacketHead(packetHead);
		this.setOptions(options);
	}

	@Override
	public PacketType getPacketType() {
		return PacketType.ClientLoginOut;
	}

	@Override
	public void execPacket() {
		try {
			this.setToUserId(this.getFromUserId());
			ServerManager.INSTANCE.sendPacketTo(this, Constant.CONSOLE_CODE_S);

			/** 根据roomId 和 发信人id 移除房间内用户*/
			ServerDataPool.serverDataManager.removeRoomUser(this.getRoomId(), this.getFromUserId());
			ChannelHandlerContext ctx =  ServerDataPool.USER_CHANNEL_MAP.get(this.getFromUserId());
			ctx.close();

			noticeUser();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/** 通知房间内用户*/
	private void noticeUser() {
		ClientResponebRoomUser crru = new ClientResponebRoomUser(this.getPacketHead());
		crru.setOption(12, this.getFromUserId());

		crru.execPacket();
	}
}
