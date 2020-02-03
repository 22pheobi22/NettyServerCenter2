/**
 * 登出 上行
 */
package com.sa.service.server;

import java.util.Objects;

import com.sa.base.ConfManager;
import com.sa.base.Manager;
import com.sa.base.ServerDataPool;
import com.sa.base.element.People;
import com.sa.net.Packet;
import com.sa.net.PacketType;
import com.sa.service.client.ClientLoginOut;
import com.sa.util.Constant;

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
