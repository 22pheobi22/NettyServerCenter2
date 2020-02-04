/**
 *
 * 项目名称:[NettyServer]
 * 包:	 [com.sa.service.server]
 * 类名称: [ServerRequestbRoomUser]
 * 类描述: [房间内用户列表]
 * 创建人: [Y.P]
 * 创建时间:[2017年7月4日 下午4:59:34]
 * 修改人: [Y.P]
 * 修改时间:[2017年7月4日 下午4:59:34]
 * 修改备注:[]
 * 版本:	 [v1.0]
 *
 */
package com.sa.service.server;

import com.sa.net.Packet;
import com.sa.net.PacketType;

public class ServerRequestbRoomTeacher extends Packet {
	public ServerRequestbRoomTeacher() {
	}

	@Override
	public PacketType getPacketType() {
		return PacketType.ServerRequestbRoomTeacher;
	}

	@Override
	public void execPacket() {
	}
}
