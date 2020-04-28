package com.sa.service.server;

import com.sa.base.ConfManager;
import com.sa.net.Packet;
import com.sa.net.PacketType;

public class ServerHeartBeat extends Packet{

	public ServerHeartBeat(String host) {
		this.setTransactionId(0);
		this.setRoomId("0");
		this.setFromUserId(ConfManager.getCenterIp());
		this.setToUserId(host);
		this.setStatus(0);
	}
		

	@Override
	public PacketType getPacketType() {
		return PacketType.ServerHearBeat;
	}

	@Override
	public void execPacket() {}

}
