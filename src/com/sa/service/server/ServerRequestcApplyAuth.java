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
import com.sa.base.element.People;
import com.sa.net.Packet;
import com.sa.net.PacketType;
import com.sa.service.client.ClientResponecApplyAuth;

public class ServerRequestcApplyAuth extends Packet {
	public ServerRequestcApplyAuth() {
	}

	@Override
	public PacketType getPacketType() {
		return PacketType.ServerRequestcApplyAuth;
	}

	@Override
	public void execPacket() {
		String[] roomIds = this.getRoomId().split(",");
		if (null != roomIds && roomIds.length > 0) {
			for (String rId : roomIds) {
				/** 根据房间id 和 目标用户id 获取 人员信息 */
				People people = ServerDataPool.dataManager.getRoomUesr(rId, this.getToUserId());
				/** 如果人员信息不为空 */
				if (null != people) {
					/** 实例化一对一消息类型 下行 并 赋值 */
					ClientResponecApplyAuth clientResponebApplyAuth = new ClientResponecApplyAuth(this.getPacketHead(),
							this.getOptions());
					clientResponebApplyAuth.setRoomId(rId);
					/** 执行 一对一消息发送 下行 */
					clientResponebApplyAuth.execPacket();
					/** 如果有中心 并 中心ip不是 目标ip */
				}
			}
		}
	}

}