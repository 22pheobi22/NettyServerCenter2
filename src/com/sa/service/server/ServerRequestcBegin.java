package com.sa.service.server;

import com.sa.net.Packet;
import com.sa.net.PacketType;
import com.sa.service.client.ClientResponecBegin;

/**
 * 开课
 *
 */
public class ServerRequestcBegin extends Packet {
	public ServerRequestcBegin() {
	}

	@Override
	public void execPacket() {
		ClientResponecBegin clientResponecBegin = new ClientResponecBegin(this.getPacketHead());
		clientResponecBegin.execPacket();
	}

	@Override
	public PacketType getPacketType() {
		return PacketType.ServerRequestcBegin;
	}

}
