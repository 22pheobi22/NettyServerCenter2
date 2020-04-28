/**
 *
 * 项目名称:[NettyServer]
 * 包:	 [com.sa.service.server]
 * 类名称: [ServerRequestcGag]
 * 类描述: [一句话描述该类的功能]
 * 创建人: [Y.P]
 * 创建时间:[2017年7月4日 下午5:00:16]
 * 修改人: [Y.P]
 * 修改时间:[2017年7月4日 下午5:00:16]
 * 修改备注:[说明本次修改内容]
 * 版本:	 [v1.0]
 *
 */
package com.sa.service.server;

import java.util.Map;

import com.sa.base.ServerDataPool;
import com.sa.base.element.People;
import com.sa.base.element.Room;
import com.sa.net.Packet;
import com.sa.net.PacketType;
import com.sa.service.client.ClientResponecGag;

public class ServerRequestcGag extends Packet {
	public ServerRequestcGag() {
	}

	@Override
	public PacketType getPacketType() {
		return PacketType.ServerRequestcGag;
	}

	@Override
	public void execPacket() {
		String[] roomIds = this.getRoomId().split(",");
		if (null != roomIds && roomIds.length > 0) {
			for (String rId : roomIds) {
				if (null == this.getToUserId() || "".equals(this.getToUserId())) {
					all(rId);
				} else {
					one(this.getToUserId(), rId);
				}
			}
		}
		ClientResponecGag cr = new ClientResponecGag(this.getPacketHead());
		cr.execPacket();
	}

	private void one(String userId, String roomId) {
		/** 禁言 */
		ServerDataPool.dataManager.notSpeakAuth(roomId, userId);
	}

	private void all(String roomId) {
		Room room = ServerDataPool.dataManager.getRoom(roomId);

		for (Map.Entry<String, People> entry : room.getPeoples().entrySet()) {
			one(entry.getKey(), roomId);
		}
	}
}
