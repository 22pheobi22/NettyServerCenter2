/**
 *
 * 项目名称:[NettyServer]
 * 包:	 [com.sa.service.client]
 * 类名称: [ClientResponecRemove]
 * 类描述: [一句话描述该类的功能]
 * 创建人: [Y.P]
 * 创建时间:[2017年7月18日 上午10:55:50]
 * 修改人: [Y.P]
 * 修改时间:[2017年7月18日 上午10:55:50]
 * 修改备注:[说明本次修改内容]
 * 版本:	 [v1.0]
 *
 */
package com.sa.service.client;

import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

import com.sa.base.Manager;
import com.sa.base.ServerDataPool;
import com.sa.net.Packet;
import com.sa.net.PacketHeadInfo;
import com.sa.net.PacketType;
import com.sa.util.Constant;

public class ClientResponecRemove extends Packet {
	public ClientResponecRemove() {
	}

	public ClientResponecRemove(PacketHeadInfo packetHead, TreeMap<Integer, Object> options) {
		this.setOptions(options);
		this.setPacketHead(packetHead);
	}

	@Override
	public void execPacket() {
		try {
			Manager.INSTANCE.sendPacketTo(this, Constant.CONSOLE_CODE_S);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public PacketType getPacketType() {
		return PacketType.ClientResponecRemove;
	}

	private void offline() {
		Map<String, Object> result2 = new HashMap<>();

		result2.put("code", 10097);
		result2.put("msg", Constant.ERR_CODE_10097);

		ClientMsgReceipt clientMsgReceipt = new ClientMsgReceipt(this.getPacketHead(), result2);
		clientMsgReceipt.setToUserId(this.getToUserId());
		clientMsgReceipt.execPacket();
	}

	private void noticeUser() {
		ClientResponebRoomUser crru = new ClientResponebRoomUser(this.getPacketHead());
		crru.setOption(12, this.getToUserId());

		crru.execPacket();
	}
}
