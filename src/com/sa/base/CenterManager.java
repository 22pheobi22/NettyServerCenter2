/**
 *
 * 项目名称：NettyServerCenter
 * 类名称：CenterManager
 * 类描述：
 * 创建人：Y.P
 * 创建时间：2020年4月27日 下午2:30:16
 * 修改人：Y.P
 * 修改时间：2020年4月27日 下午2:30:16
 * @version  1.0
 *
 */
package com.sa.base;

import com.sa.client.ChatClient;
import com.sa.client.MonitorClient;

public class CenterManager {
	private String CENTER_LINK_SERVER_NAME = "CENTER-LINK-SERVER-";
	private String CENTER_MASTER_SLAVE_MONITOR = "CENTER-MASTER-SLAVE-MONITOR-";

	public void monitorLinkCenter() {
		String key = CENTER_MASTER_SLAVE_MONITOR + ConfManager.getCenterIpAnother();

		MonitorClient monitorClient = new MonitorClient(key, ConfManager.getCenterIpAnother(), ConfManager.getCenterPortAnother());
		start(key, monitorClient);
	}

	public void centerLinkServer() {
		//连接服务 
		String[] address = ConfManager.getServerAddress();

		if(null != address && address.length > 0) {
			for (int i = 0; i < address.length; i++) {
				String[] addr = address[i].split(":");

				String key = CENTER_LINK_SERVER_NAME +addr[0];
				ChatClient chatClient = new ChatClient(key, addr[0], Integer.valueOf(addr[1]));
				start(key, chatClient);
			}
		}
	}

	public void start(String key, Runnable client) {
		
		Thread thread = new Thread(client);
		thread.setName(key);
		thread.start();

		//存储线程信息
		ServerDataPool.threadManager.set(key, thread);
	}

	/**
	 * 中心主备切换
	 */
	public void activeStandbySwitching() {
		// TODO: 改redis中主信息
		
		
		// 启动连接server服务
		this.centerLinkServer();
		
		// TODO:关闭原监控通道
		// 关闭监控线程 
		String key = CENTER_MASTER_SLAVE_MONITOR+ConfManager.getCenterIpAnother();
		ServerDataPool.threadManager.close(key);
	}
	
	public boolean checkServerLink() {
		return ServerDataPool.threadManager.check(CENTER_LINK_SERVER_NAME, 0);
	}
}
