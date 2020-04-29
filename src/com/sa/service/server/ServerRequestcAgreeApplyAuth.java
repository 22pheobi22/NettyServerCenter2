/**
 * 
 * 项目名称:[NettyServer]
 * 包:	 [com.sa.service.server]
 * 类名称: [ServerRequestApplyAuth]
 * 类描述: [一句话描述该类的功能]
 * 创建人: [Y.P]
 * 创建时间:[2018年7月30日 上午10:05:23]
 * 修改人: [Y.P]
 * 修改时间:[2018年7月30日 上午10:05:23]
 * 修改备注:[说明本次修改内容]  
 * 版本:	 [v1.0]   
 * 
 */
package com.sa.service.server;

import com.sa.base.ServerDataPool;
import com.sa.net.Packet;
import com.sa.net.PacketType;
import com.sa.service.client.ClientResponecAgreeApplyAuth;

public class ServerRequestcAgreeApplyAuth extends Packet {
	public ServerRequestcAgreeApplyAuth() {
	}

	@Override
	public PacketType getPacketType() {
		return PacketType.ServerRequestcAgreeApplyAuth;
	}

	@Override
	public void execPacket() {
		String[] roomIds = this.getRoomId().split(",");
		if (null != roomIds && roomIds.length > 0) {
			for (String rId : roomIds) {
				/**
				 * option 1 : 权限CODE
				 * option 2 : 权限名称
				 * option 3 : 操作 (+：添加 -:删除)
				 * option 4 : 多人或单人权限标识（1、n）
				 */
				ServerDataPool.dataManager.setRoomUserDefAuth(rId,
						this.getToUserId(),
						(String) this.getOption(1),
						(String) this.getOption(3),
						(String) this.getOption(4));
			}
		}
		/** 实例化 开课 下行 并 赋值 并 执行 */
		ClientResponecAgreeApplyAuth clientResponecAgreeApplyAuth = new ClientResponecAgreeApplyAuth(
				this.getPacketHead(), this.getOptions());
		clientResponecAgreeApplyAuth.execPacket();
	}

}