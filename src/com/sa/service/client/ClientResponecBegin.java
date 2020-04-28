/**
 *
 * 项目名称:[NettyServer]
 * 包:	 [com.sa.service.client]
 * 类名称: [ClientResponecBegin]
 * 类描述: [一句话描述该类的功能]
 * 创建人: [Y.P]
 * 创建时间:[2017年7月16日 下午5:16:17]
 * 修改人: [Y.P]
 * 修改时间:[2017年7月16日 下午5:16:17]
 * 修改备注:[说明本次修改内容]
 * 版本:	 [v1.0]
 *
 */
package com.sa.service.client;

import java.util.Arrays;
import java.util.HashSet;

import com.sa.base.ConfManager;
import com.sa.base.Manager;
import com.sa.base.ServerDataPool;
import com.sa.net.Packet;
import com.sa.net.PacketHeadInfo;
import com.sa.net.PacketType;
import com.sa.util.Constant;

public class ClientResponecBegin extends Packet {
	public ClientResponecBegin() {
	}

	public ClientResponecBegin(PacketHeadInfo packetHead) {
		this.setPacketHead(packetHead);
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
		try {
			/** 获取房间禁言角色 */
			HashSet<String> roomRoles = getRole(this.getOption(1));
			String[] roomIds = this.getRoomId().split(",");
			if (null != roomIds && roomIds.length > 0) {
				for (String rId : roomIds) {
					/** 设置房间角色 */
					ServerDataPool.dataManager.setRoomRole(rId, roomRoles, ConfManager.getTalkEnable());
				}
			}
			Manager.INSTANCE.sendPacketToRoomServerExpectSourse(this, Constant.CONSOLE_CODE_S, this.getFromUserId());
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	@Override
	public PacketType getPacketType() {
		return PacketType.ClientResponecBegin;
	}

}
