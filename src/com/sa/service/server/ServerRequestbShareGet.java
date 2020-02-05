/**
 *
 * 项目名称:[NettyServer]
 * 包:	 [com.sa.service.server]
 * 类名称: [ServerRequestcGetShare]
 * 类描述: [一句话描述该类的功能]
 * 创建人: [Y.P]
 * 创建时间:[2017年7月11日 下午5:52:27]
 * 修改人: [Y.P]
 * 修改时间:[2017年7月11日 下午5:52:27]
 * 修改备注:[说明本次修改内容]
 * 版本:	 [v1.0]
 *
 */
package com.sa.service.server;

import com.sa.net.Packet;
import com.sa.net.PacketType;

public class ServerRequestbShareGet extends Packet {
	public ServerRequestbShareGet(){}

	@Override
	public void execPacket() {
	}

	@Override
	public PacketType getPacketType() {
		return PacketType.ServerRequestbShareGet;
	}

}
