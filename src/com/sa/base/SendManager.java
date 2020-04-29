/**
 *
 * 项目名称：NettyServerCenter
 * 类名称：SendManager
 * 类描述：
 * 创建人：Y.P
 * 创建时间：2020年4月29日 下午5:21:31
 * 修改人：Y.P
 * 修改时间：2020年4月29日 下午5:21:31
 * @version  1.0
 *
 */
package com.sa.base;

import com.sa.base.element.ChannelExtend;
import com.sa.net.Packet;
import com.sa.net.codec.PacketBinEncoder;

import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.websocketx.BinaryWebSocketFrame;

public class SendManager {
	/** 向通道写消息并发送*/
	public void writeAndFlush(ChannelHandlerContext ctx, Packet pact) throws Exception {
		String master = ServerDataPool.redisDataManager.getCenterMaster();

		if (ConfManager.getCenterId().equals(master)) {
			ChannelExtend ce = ServerDataPool.CHANNEL_USER_MAP.get(ctx);
			if (null == ce) {
				ce = ServerDataPool.TEMP_CONN_MAP.get(ctx);
			}

			if (null != ce) {
				if (0 == ce.getChannelType()) {
					//System.out.println("【ctx:"+ctx+"】【pack:"+pact+"】");
					ctx.writeAndFlush(pact);
				} else if (1 == ce.getChannelType()) {
					// 将数据包封成二进制包
					BinaryWebSocketFrame binaryWebSocketFrame = new PacketBinEncoder().encode(pact);
			
					// 把包放进通道并发送
					ctx.writeAndFlush(binaryWebSocketFrame);
				} else {
					System.out.println("未知类型连接");
				}
			} else {
				System.out.println("通道拓展信息不存在");
			}
		}
	}
}
