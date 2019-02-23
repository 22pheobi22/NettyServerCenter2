/**
 *
 * 项目名称：NettyServer
 * 类名称：AutoCancel
 * 类描述：
 * 创建人：Y.P
 * 创建时间：2019年2月23日 上午11:04:24
 * 修改人：Y.P
 * 修改时间：2019年2月23日 上午11:04:24
 * @version  1.0
 *
 */
package com.sa.thread;

import java.util.Map;

import com.sa.base.ServerDataPool;

import io.netty.channel.ChannelHandlerContext;

public class AutoCancelTempConnect implements Runnable {

	@Override
	public void run() {
		while(true) {
//			System.out.println("回收临时连接执行开始时间 - " + System.currentTimeMillis());
			try {
				Map<ChannelHandlerContext, Long> map = ServerDataPool.TEMP_CONN_MAP;
				
				for(Map.Entry<ChannelHandlerContext, Long> entry : map.entrySet()) {
					Long curr = System.currentTimeMillis();
					
					if (null == entry.getValue()) {
						map.remove(entry.getKey());
						continue;
					}

					Long tmp = curr - entry.getValue();
					if (tmp > 10000) {
						System.out.println("回收临时连接:"+ curr + "-" +entry.getValue() +"=" + tmp);
						map.remove(entry.getKey());

						ChannelHandlerContext ctx = entry.getKey();
						ctx.close();
					}
				}
			} catch(Exception e) {
				e.printStackTrace();
			}
			
			try {
				Thread.sleep(5000);
			} catch(Exception e) {
				e.printStackTrace();
			}
		}
	}

}
