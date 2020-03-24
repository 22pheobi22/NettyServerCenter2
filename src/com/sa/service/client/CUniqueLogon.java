/**
 *
 * 项目名称:[NettyServer]
 * 包:	 [com.sa.service.client]
 * 类名称: [CUniqueLogon]
 * 类描述: [房间用户列表 下行]
 * 创建人: [Y.P]
 * 创建时间:[2017年7月17日 下午3:10:28]
 * 修改人: [Y.P]
 * 修改时间:[2017年7月17日 下午3:10:28]
 * 修改备注:[说明本次修改内容]
 * 版本:	 [v1.0]
 *
 */
package com.sa.service.client;

import com.sa.net.Packet;
import com.sa.net.PacketHeadInfo;
import com.sa.net.PacketType;

public class CUniqueLogon extends Packet {
	public CUniqueLogon() {
	}

	public CUniqueLogon(int transactionId, String roomId, String userId, int code) {
		super(transactionId, roomId, "0", userId, code);
	}

	public CUniqueLogon(PacketHeadInfo packetHead) {
		this.setPacketHead(packetHead);
	}

	@Override
	public void execPacket() {
		try {

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public PacketType getPacketType() {
		return PacketType.CUniqueLogon;
	}

}
