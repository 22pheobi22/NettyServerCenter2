/**
 *
 * 项目名称:[NettyServer]
 * 包:	 [com.sa.service.client]
 * 类名称: [ClientResponecBegin]
 * 类描述: [一句话描述该类的功能]
 * 创建人: [Y.P]
 * 创建时间:[2017年7月16日 下午5:16:17]
 * 修改人: [Y.P]
 * 修改时间:[2017年7月16日 下午5:16:17]
 * 修改备注:[说明本次修改内容]
 * 版本:	 [v1.0]
 *
 */
package com.sa.service.client;

import com.sa.base.Manager;
import com.sa.net.Packet;
import com.sa.net.PacketHeadInfo;
import com.sa.net.PacketType;
import com.sa.util.Constant;

public class ClientResponecBegin extends Packet {
	public ClientResponecBegin() {
	}

	public ClientResponecBegin(PacketHeadInfo packetHead) {
		this.setPacketHead(packetHead);
	}


	@Override
	public void execPacket() {
		try {
			Manager.INSTANCE.sendPacketToRoomServerExpectSourse(this, Constant.CONSOLE_CODE_S, this.getFromUserId());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public PacketType getPacketType() {
		return PacketType.ClientResponecBegin;
	}

}
