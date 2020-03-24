/**
 *
 * 项目名称:[NettyServer]
 * 包:	 [com.sa.service.server]
 * 类名称: [ServerRequestcRoomRemove]
 * 类描述: [一句话描述该类的功能]
 * 创建人: [Y.P]
 * 创建时间:[2017年7月11日 下午5:51:23]
 * 修改人: [Y.P]
 * 修改时间:[2017年7月11日 下午5:51:23]
 * 修改备注:[说明本次修改内容]
 * 版本:	 [v1.0]
 *
 */
package com.sa.service.server;

import com.sa.net.Packet;
import com.sa.net.PacketType;
import com.sa.service.client.ClientResponecRoomRemove;
import com.sa.util.Constant;

public class ServerRequestcRoomRemove extends Packet {
	public ServerRequestcRoomRemove() {
	}

	/**
	 * @param transactionId
	 * @param roomId
	 * @param fromUserId
	 * @param toUserId
	 * @param status
	 */
	public ServerRequestcRoomRemove(Integer transactionId, String roomId, String fromUserId, String toUserId,
			Integer status) {
		this.setTransactionId(transactionId);
		this.setRoomId(roomId);
		this.setFromUserId(fromUserId);
		this.setToUserId(toUserId);
		this.setStatus(status);
	}

	@Override
	public PacketType getPacketType() {
		return PacketType.ServerRequestcRoomRemove;
	}

	@Override
	public void execPacket() {
		String[] roomIds = this.getRoomId().split(",");
		if (null != roomIds && roomIds.length > 0) {
			for (String rId : roomIds) {
				/** 实例化 删除房间 下行 */
				ClientResponecRoomRemove clientResponecRoomRemove = new ClientResponecRoomRemove(this.getPacketHead());
				clientResponecRoomRemove.setStatus(20046);
				clientResponecRoomRemove.setOption(1, Constant.PROMPT_CODE_20046);
				clientResponecRoomRemove.setRoomId(rId);
				clientResponecRoomRemove.execPacket();
			}
		}
	}

}
