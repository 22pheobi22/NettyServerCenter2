/**
 *
 * 项目名称:[NettyServer]
 * 包:	 [com.sa.service]
 * 类名称: [ServerRequestbRoom]
 * 类描述: [一句话描述该类的功能]
 * 创建人: [Y.P]
 * 创建时间:[2017年7月4日 上午11:43:47]
 * 修改人: [Y.P]
 * 修改时间:[2017年7月4日 上午11:43:47]
 * 修改备注:[说明本次修改内容]
 * 版本:	 [v1.0]
 *
 */
package com.sa.service.server;

import com.sa.base.ServerDataPool;
import com.sa.net.Packet;
import com.sa.net.PacketType;
import com.sa.service.client.ClientResponebRoom;

public class ServerRequestbRoom extends Packet {
	public ServerRequestbRoom() {
	}

	@Override
	public PacketType getPacketType() {
		return PacketType.ServerRequestbRoom;
	}

	@Override
	public void execPacket() {

		String[] roomIds = this.getRoomId().split(",");

		if (null != roomIds && roomIds.length > 0) {
			for (String rId : roomIds) {
				/*ServerDataPool.dataManager.setRoomChats(rId, System.currentTimeMillis() + "," + this.getTransactionId(),
						this.getFromUserId(), (String) this.getOption(1));*/
				
				ClientResponebRoom newCrr = new ClientResponebRoom(this.getPacketHead(), this.getOptions());
				newCrr.setRoomId(rId);
				newCrr.execPacket(); 
			}
		}
	}
}
