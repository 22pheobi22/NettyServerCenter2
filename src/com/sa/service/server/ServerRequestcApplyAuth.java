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
		ClientResponecApplyAuth clientResponebApplyAuth = new ClientResponecApplyAuth(this.getPacketHead(),
				this.getOptions());
		clientResponebApplyAuth.execPacket();
	}

}