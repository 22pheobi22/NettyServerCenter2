/**
 *
 * 项目名称：CommunicationManage
 * 类名称：SysCloseRoomsReq
 * 类描述：
 * 创建人：Y.P
 * 创建时间：2019年2月12日 下午3:36:20
 * 修改人：Y.P
 * 修改时间：2019年2月12日 下午3:36:20
 * @version  1.0
 *
 */
package com.sa.service.sys;

import com.sa.net.Packet;
import com.sa.net.PacketType;
import com.sa.service.client.ClientResponecRoomRemove;
import com.sa.util.Constant;

public class SysCloseRoomReq extends Packet {

	public SysCloseRoomReq(){
	}

	@Override
	public void execPacket() {
		/*String roomId = (String) this.getOption(1);
		*//** 删除 房间 消息 缓存 *//*
		ServerDataPool.dataManager.cleanLogs(roomId);
		*//** 删除 房间 缓存 *//*
		ServerDataPool.dataManager.removeRoom(roomId);
		*//** 删除 房间 空闲 计数 缓存 *//*
		ServerDataPool.dataManager.cancelFreeRoom(roomId);*/
		
		String[] roomIds = this.getRoomId().split(",");
		if (null != roomIds && roomIds.length > 0) {
			for (String rId : roomIds) {
				/** 实例化 删除房间 下行*/
				ClientResponecRoomRemove clientResponecRoomRemove = new ClientResponecRoomRemove(this.getPacketHead());
				clientResponecRoomRemove.setStatus(20046);
				clientResponecRoomRemove.setOption(1, Constant.PROMPT_CODE_20046);
				clientResponecRoomRemove.setRoomId(rId);
				clientResponecRoomRemove.execPacket();
			}
		}

		SysCloseRoomRes sysCloseRoomRes = new SysCloseRoomRes();
		sysCloseRoomRes.setPacketHead(this.getPacketHead());
		sysCloseRoomRes.setOptions(this.getOptions());
		sysCloseRoomRes.setOption(254, "关闭房间成功");
		sysCloseRoomRes.execPacket();
	}

	@Override
	public PacketType getPacketType() {
		return PacketType.SysCloseRoomReq;
	}

}