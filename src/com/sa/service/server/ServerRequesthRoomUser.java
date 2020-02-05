package com.sa.service.server;

import com.sa.net.Packet;
import com.sa.net.PacketType;

public class ServerRequesthRoomUser extends Packet {

	/**
	 *
	 */
	public ServerRequesthRoomUser() {}

	/**
	 * @param transactionId
	 * @param roomId
	 * @param fromUserId
	 * @param toUserId
	 * @param status
	 */
	public ServerRequesthRoomUser(Integer transactionId, String roomId, String fromUserId, String toUserId, Integer status) {
		this.setTransactionId(transactionId);
		this.setRoomId(roomId);
		this.setFromUserId(fromUserId);
		this.setToUserId(toUserId);
		this.setStatus(status);
	}

	@Override
	public void execPacket() {

	}

	@Override
	public PacketType getPacketType() {
		return PacketType.ServerRequesthRoomUser;
	}

}
