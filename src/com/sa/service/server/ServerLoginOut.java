/**
 * 登出 上行
 */
package com.sa.service.server;

import com.sa.net.Packet;
import com.sa.net.PacketType;
import com.sa.service.client.ClientLoginOut;

public class ServerLoginOut extends Packet{
	public ServerLoginOut(){}

	@Override
	public PacketType getPacketType() {
		return PacketType.ServerLoginOut;
	}

	@Override
	public void execPacket() {
		/** 实例化登出 下行 并执行 */
		ClientLoginOut clientLoginOut = new ClientLoginOut(this.getPacketHead(), this.getOptions());
		clientLoginOut.execPacket();
	}

}
