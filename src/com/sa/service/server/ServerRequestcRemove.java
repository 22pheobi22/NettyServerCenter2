/**
 *
 * 项目名称:[NettyServer]
 * 包:	 [com.sa.service.server]
 * 类名称: [ServerRequestcRemove]
 * 类描述: [一句话描述该类的功能]
 * 创建人: [Y.P]
 * 创建时间:[2017年7月4日 下午5:00:33]
 * 修改人: [Y.P]
 * 修改时间:[2017年7月4日 下午5:00:33]
 * 修改备注:[说明本次修改内容]
 * 版本:	 [v1.0]
 *
 */
package com.sa.service.server;

import com.sa.base.ServerDataPool;
import com.sa.base.element.People;
import com.sa.net.Packet;
import com.sa.net.PacketType;
import com.sa.service.client.ClientResponecRemove;

public class ServerRequestcRemove extends Packet {
	public ServerRequestcRemove() {
	}

	/**
	 * @param transactionId
	 * @param roomId
	 * @param fromUserId
	 * @param toUserId
	 * @param status
	 */
	public ServerRequestcRemove(Integer transactionId, String roomId, String fromUserId, String toUserId,
			Integer status) {
		this.setTransactionId(transactionId);
		this.setRoomId(roomId);
		this.setFromUserId(fromUserId);
		this.setToUserId(toUserId);
		this.setStatus(status);
	}

	@Override
	public PacketType getPacketType() {
		return PacketType.ServerRequestcRemove;
	}

	@Override
	public void execPacket() {
		String[] roomIds = this.getRoomId().split(",");
		if (null != roomIds && roomIds.length > 0) {
			for (String rId : roomIds) {
				/** 发送被迫下线通知*/
				//offline();
				/** 移除用户*/
				ServerDataPool.dataManager.removeRoomUser(rId, this.getToUserId());
				/** 通知被踢用户*/
				//noticeUser();
			}
		}
		ClientResponecRemove clientResponecRemove = new ClientResponecRemove(this.getPacketHead(),
				this.getOptions());
		clientResponecRemove.execPacket();
		/**该用户是否还存在于其他房间*/
		String userRoomNo = ServerDataPool.dataManager.getUserRoomNo(this.getToUserId());
		if(null==userRoomNo||"".equals(userRoomNo)){
			//若不存在  发送踢人下行消息到服务 关闭该服务上用户通道
			//Manager.INSTANCE.sendPacketTo(this, Constant.CONSOLE_CODE_S);
			//移除user-ip信息
			ServerDataPool.dataManager.delUserServer(this.getToUserId());
		}
	}

}
