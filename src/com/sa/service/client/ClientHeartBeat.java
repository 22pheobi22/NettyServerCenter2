package com.sa.service.client;

import com.sa.base.CenterManager;
import com.sa.net.Packet;
import com.sa.net.PacketType;
import com.sa.util.ByteBufUtil;

public class ClientHeartBeat extends Packet {
	public ClientHeartBeat() {
	}

	@Override
	public void writePacketBody(ByteBufUtil buf) {
	}

	@Override
	public void readPacketBody(ByteBufUtil buf) {
	}

	@Override
	public PacketType getPacketType() {
		return PacketType.ClientHeartBeat;
	}

	@Override
	public void execPacket() {
		String temp = String.valueOf(this.getOption(1));
		if ("false".equals(temp)) {
			new CenterManager().activeStandbySwitching();
		}
	}

}
