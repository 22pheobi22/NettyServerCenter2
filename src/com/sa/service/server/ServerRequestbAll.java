/**
 *
 * 项目名称:[NettyServer]
 * 包:	 [com.sa.service]
 * 类名称: [ServerRequestbAll]
 * 类描述: [一句话描述该类的功能]
 * 创建人: [Y.P]
 * 创建时间:[2017年7月4日 上午11:43:57]
 * 修改人: [Y.P]
 * 修改时间:[2017年7月4日 上午11:43:57]
 * 修改备注:[说明本次修改内容]
 * 版本:	 [v1.0]
 *
 */
package com.sa.service.server;

import com.sa.net.Packet;
import com.sa.net.PacketType;
import com.sa.service.client.ClientResponebAll;

public class ServerRequestbAll extends Packet {
	public ServerRequestbAll() {
	}

	@Override
	public PacketType getPacketType() {
		return PacketType.ServerRequestbAll;
	}

	@Override
	public void execPacket() {
		/** 实例化 发送全体消息 下行 并执行 */
		ClientResponebAll clientResponebAll = new ClientResponebAll(this.getPacketHead(), this.getOptions());
		clientResponebAll.execPacket();
	}

}
