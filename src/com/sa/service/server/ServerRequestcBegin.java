package com.sa.service.server;

import com.sa.net.Packet;
import com.sa.net.PacketType;
import com.sa.service.client.ClientResponecBegin;
/**
 * 开课
 *
 */
public class ServerRequestcBegin extends Packet {
	public ServerRequestcBegin(){}

	@Override
	public void execPacket() {
		String[] roomIds = this.getRoomId().split(",");
			if (null != roomIds && roomIds.length > 0) {
				for (String rId : roomIds) {
					/** 实例化 开课 下行 并 赋值 并 执行*/
					ClientResponecBegin clientResponecBegin = new ClientResponecBegin(this.getPacketHead());
					clientResponecBegin.setRoomId(rId);
					clientResponecBegin.execPacket();
				}
			}
	}

	@Override
	public PacketType getPacketType() {
		return PacketType.ServerRequestcBegin;
	}

}
