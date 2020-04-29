package com.sa.service.server;

import java.util.Arrays;
import java.util.HashSet;

import com.sa.base.ConfManager;
import com.sa.base.ServerDataPool;
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

	private HashSet<String> getRole(Object option) {
		HashSet<String> roomRoles = new HashSet<>();
		/** 如果选项不为空 */
		if (null != option) {
			/** 分隔选项成数组 并 添加到房间规则中 */
			roomRoles.addAll(Arrays.asList(((String) option).split(",")));
		}

		return roomRoles;
	}

	@Override
	public void execPacket() {
		/** 获取房间禁言角色 */
		HashSet<String> roomRoles = getRole(this.getOption(1));
		String[] roomIds = this.getRoomId().split(",");
		if (null != roomIds && roomIds.length > 0) {
			for (String rId : roomIds) {
				/** 设置房间角色 */
				ServerDataPool.dataManager.setRoomRole(rId, roomRoles, ConfManager.getTalkEnable());
			}
		}
		ClientResponecBegin clientResponecBegin = new ClientResponecBegin(this.getPacketHead());
		clientResponecBegin.execPacket();
	}

	@Override
	public PacketType getPacketType() {
		return PacketType.ServerRequestcBegin;
	}

}
