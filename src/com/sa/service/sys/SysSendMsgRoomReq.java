package com.sa.service.sys;

import com.sa.base.ServerDataPool;
import com.sa.net.Packet;
import com.sa.net.PacketType;
import com.sa.service.client.ClientResponebRoom;

/**
 * 房间内消息
 * @author zyh
 *
 * 2020年2月25日
 */
public class SysSendMsgRoomReq extends Packet {
	
	public SysSendMsgRoomReq(){}

	@Override
	public void execPacket() {
		String[] roomIds = this.getRoomId().split(",");
		if (null != roomIds && roomIds.length > 0) {
			for (String rId : roomIds) {
				ServerDataPool.dataManager.setRoomChats(rId,
						System.currentTimeMillis() + "," + this.getTransactionId(), this.getFromUserId(),
						(String) this.getOption(1));
				
				ClientResponebRoom newCrr = new ClientResponebRoom(this.getPacketHead(), this.getOptions());
				newCrr.setRoomId(rId);
				newCrr.execPacket();
			}
		}
		
		SysSendMsgRoomRes res=new SysSendMsgRoomRes();
		res.setPacketHead(this.getPacketHead());
		res.setOption(254, "房间内消息发送成功！");
		res.execPacket();
	}

	@Override
	public PacketType getPacketType() {
		return PacketType.SysSendMsgRoomReq;
	}

}
