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
				/** 实例化 开课 下行 并 赋值 并 执行 */
				ClientResponecAgreeApplyAuth clientResponecAgreeApplyAuth = new ClientResponecAgreeApplyAuth(
						this.getPacketHead(), this.getOptions());
				clientResponecAgreeApplyAuth.setRoomId(rId);
				clientResponecAgreeApplyAuth.execPacket();
			}
		}
	}

}