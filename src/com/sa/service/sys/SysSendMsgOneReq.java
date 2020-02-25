package com.sa.service.sys;

import com.sa.base.ServerDataPool;
import com.sa.base.element.People;
import com.sa.net.Packet;
import com.sa.net.PacketType;
import com.sa.service.client.ClientResponebOne;

/**
 * 1对1消息
 * @author zyh
 *
 * 2020年2月25日
 */
public class SysSendMsgOneReq extends Packet {
	
	public SysSendMsgOneReq(){}

	@Override
	public void execPacket() {
		String[] roomIds = this.getRoomId().split(",");
		if (roomIds != null && roomIds.length > 0) {
			for (String rId : roomIds) {
				/** 根据房间id 和 目标用户id 获取 人员信息 */
				People people = ServerDataPool.dataManager.getRoomUesr(rId, this.getToUserId());
				/** 实例化一对一消息类型 下行 并 赋值 */
				ClientResponebOne clientResponebOne = new ClientResponebOne(this.getPacketHead(),
						this.getOptions());
				/** 如果人员信息不为空 */
				if (null != people) {
					// 设置房间id为目标房间id
					clientResponebOne.setRoomId(rId);
					/** 执行 一对一消息发送 下行 */
					clientResponebOne.execPacket();
				}
			}
		}
		
		SysSendMsgOneRes res=new SysSendMsgOneRes();
		res.setPacketHead(this.getPacketHead());
		res.setOption(254, "1v1消息发送成功！");
		res.execPacket();
	}

	@Override
	public PacketType getPacketType() {
		return PacketType.SysSendMsgOneReq;
	}

}
