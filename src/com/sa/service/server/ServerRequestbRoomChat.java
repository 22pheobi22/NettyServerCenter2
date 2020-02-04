package com.sa.service.server;

import com.sa.net.Packet;
import com.sa.net.PacketType;

public class ServerRequestbRoomChat extends Packet {
	public ServerRequestbRoomChat() {
	}

	@Override
	public void execPacket() {
		
	}

	@Override
	public PacketType getPacketType() {
		return PacketType.ServerRequestbRoomChat;
	}

}
