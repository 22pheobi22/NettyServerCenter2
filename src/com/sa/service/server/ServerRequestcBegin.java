package com.sa.service.server;

import java.util.Map;

import com.sa.base.ConfManager;
import com.sa.base.ServerManager;
import com.sa.net.Packet;
import com.sa.net.PacketType;
import com.sa.service.client.ClientMsgReceipt;
import com.sa.service.client.ClientResponecBegin;
import com.sa.service.permission.Permission;
import com.sa.util.Constant;
/**
 * 开课
 *
 */
public class ServerRequestcBegin extends Packet {
	public ServerRequestcBegin(){}

	@Override
	public void execPacket() {
		/** 校验用户角色*/
		Map<String, Object> result = Permission.INSTANCE.checkUserRole(this.getRoomId(), this.getFromUserId(), Constant.ROLE_TEACHER);
		/** 如果校验成功*/
		if (0 == ((Integer) result.get("code"))) {
			/** 如果有中心 并 目标IP不是中心IP*/
			if (ConfManager.getIsCenter() && !ConfManager.getCenterIp().equals(this.getRemoteIp())) {
				/** 消息转发到中心*/
				ServerManager.INSTANCE.sendPacketToCenter(this, Constant.CONSOLE_CODE_TS);
			} else {
//				HashSet<String> roomRoles = getRole(this.getOption(1));
//				ServerDataPool.serverDataManager.setRoomRole(this.getRoomId(), roomRoles, ConfManager.getRoomChat());
				/** 实例化 开课 下行 并 赋值 并 执行*/
				new ClientResponecBegin(this.getPacketHead()).execPacket();
			}
		}
		/** 实例化消息回执 并 赋值 并 执行*/
		new ClientMsgReceipt(this.getPacketHead(), result).execPacket();
	}

	@Override
	public PacketType getPacketType() {
		return PacketType.ServerRequestcBegin;
	}

}
