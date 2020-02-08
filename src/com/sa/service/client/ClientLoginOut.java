package com.sa.service.client;

import java.util.TreeMap;

import com.sa.base.ConfManager;
import com.sa.base.Manager;
import com.sa.base.ServerDataPool;
import com.sa.net.Packet;
import com.sa.net.PacketHeadInfo;
import com.sa.net.PacketType;
import com.sa.util.Constant;

import io.netty.channel.ChannelHandlerContext;

public class ClientLoginOut extends Packet {

	public ClientLoginOut() {}

	public ClientLoginOut(PacketHeadInfo packetHead) {
		this.setPacketHead(packetHead);
	}

	public ClientLoginOut(PacketHeadInfo packetHead, TreeMap<Integer, Object> options) {
		this.setPacketHead(packetHead);
		this.setOptions(options);
	}

	@Override
	public PacketType getPacketType() {
		return PacketType.ClientLoginOut;
	}

	@Override
	public void execPacket() {
		try {
			if(!ConfManager.getCenterId().equals(this.getFromUserId())){
				if(null!=this.getRoomId()){
					String[] roomIds = this.getRoomId().split(",");
					if(roomIds!=null&&roomIds.length>0){
						for (String rId : roomIds) {
							/** 根据roomId 和 发信人id 移除房间内用户*/
							ServerDataPool.dataManager.removeRoomUser(rId, this.getFromUserId());
							//重新设置房间id为要处理房间的id  下边通知其他人要用
							this.setRoomId(rId);
							noticeUser();
						}
					}
					//刪除user-ip信息
					ServerDataPool.dataManager.delUserServer(this.getFromUserId());
				}
			}else{
				this.setToUserId(this.getFromUserId());
				Manager.INSTANCE.sendPacketTo(this, Constant.CONSOLE_CODE_S);

				ChannelHandlerContext ctx =  ServerDataPool.USER_CHANNEL_MAP.get(this.getFromUserId());
				if(null!=ctx){
					ctx.close();
				}
				ServerDataPool.CHANNEL_USER_MAP.remove(ctx);
				ServerDataPool.USER_CHANNEL_MAP.remove(this.getFromUserId());
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/** 通知房间内用户*/
	private void noticeUser() {
		ClientResponebRoomUser crru = new ClientResponebRoomUser(this.getPacketHead());
		crru.setOption(12, this.getFromUserId());

		crru.execPacket();
	}
}
