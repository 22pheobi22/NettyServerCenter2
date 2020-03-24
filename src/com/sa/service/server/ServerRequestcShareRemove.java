package com.sa.service.server;

import com.sa.base.ServerDataPool;
import com.sa.net.Packet;
import com.sa.net.PacketType;
import com.sa.service.client.ClientResponecShareRemove;

public class ServerRequestcShareRemove extends Packet {
	public ServerRequestcShareRemove(){}

	@Override
	public void execPacket() {
		/** 获取序列 1 的选项 */
		String op1 = (String) this.getOption(1);
		String[] roomIds = this.getRoomId().split(",");
		if (null != roomIds && roomIds.length > 0) {
			for (String rId : roomIds) {
				/** 实例化共享删除 下行 并赋值 并 执行 */
				ClientResponecShareRemove clientResponecShareRemove = new ClientResponecShareRemove(this.getPacketHead(), this.getOptions());
				clientResponecShareRemove.setRoomId(rId);
				clientResponecShareRemove.execPacket();
				/** 删除共享 */
				ServerDataPool.dataManager.removeShare(rId, op1);
			}
		}
	}

	@Override
	public PacketType getPacketType() {
		return PacketType.ServerRequestcShareRemove;
	}

}
