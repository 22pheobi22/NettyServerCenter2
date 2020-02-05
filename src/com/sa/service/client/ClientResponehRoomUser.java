package com.sa.service.client;

import com.sa.net.Packet;
import com.sa.net.PacketHeadInfo;
import com.sa.net.PacketType;

public class ClientResponehRoomUser extends Packet {

	/**
	 *
	 */
	public ClientResponehRoomUser() {
	}

	/**
	 * @param transactionId
	 * @param roomId
	 * @param fromUserId
	 * @param toUserId
	 * @param status
	 */
	public ClientResponehRoomUser(PacketHeadInfo packetHead) {
		this.setPacketHead(packetHead);
	}

	@Override
	public void execPacket() {
	}

	@Override
	public PacketType getPacketType() {
		return PacketType.ClientResponehRoomUser;
	}

}
