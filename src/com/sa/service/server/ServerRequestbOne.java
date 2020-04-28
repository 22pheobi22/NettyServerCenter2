/**
 *
 * 项目名称:[NettyServer]
 * 包:	 [com.sa.service]
 * 类名称: [ServerRequestOne]
 * 类描述: [一句话描述该类的功能]
 * 创建人: [Y.P]
 * 创建时间:[2017年7月4日 上午11:32:11]
 * 修改人: [Y.P]
 * 修改时间:[2017年7月4日 上午11:32:11]
 * 修改备注:[说明本次修改内容]
 * 版本:	 [v1.0]
 *
 */
package com.sa.service.server;

import com.sa.net.Packet;
import com.sa.net.PacketType;
import com.sa.service.client.ClientResponebOne;

public class ServerRequestbOne extends Packet {
	public ServerRequestbOne() {
	}

	@Override
	public PacketType getPacketType() {
		return PacketType.ServerRequestbOne;
	}

	@Override
	public void execPacket() {
		/** 实例化一对一消息类型 下行 并 赋值 */
		ClientResponebOne clientResponebOne = new ClientResponebOne(this.getPacketHead(), this.getOptions());
		/** 执行 一对一消息发送 下行 */
		clientResponebOne.execPacket();
	}
}
