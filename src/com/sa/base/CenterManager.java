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

import java.util.Set;

import com.sa.client.ChatClient;
import com.sa.client.MonitorClient;

public class CenterManager {
	private final String CENTER_LINK_SERVER_NAME = "CENTER-LINK-SERVER-";
	private final String CENTER_MASTER_SLAVE_MONITOR = "CENTER-MASTER-SLAVE-MONITOR-";
	private final String HEART_BEAT = "HEART-BEAT-";

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
		String master = ServerDataPool.redisDataManager.getCenterMaster();

		if (null != master && !"".equals(master)) {
			Set<String> keys = ServerDataPool.redisDataManager.getHeartBeats(HEART_BEAT + "-" + master + "-");

			if (null != keys) {
				int index = 0;
				long now = System.currentTimeMillis();
				for (String key : keys) {
					String value = ServerDataPool.redisDataManager.getHeartBeat(key);
					long time = Long.parseLong(value);
					if (now - time < ConfManager.getCenterMasterOvertime()) {
						index ++;
						break;
					}
				}
			
				if (0 == index) {
					
				}
			}
		}
	}
	
	public boolean checkServerLink() {
		return ServerDataPool.threadManager.check(CENTER_LINK_SERVER_NAME, 0);
	}

	public void heartBeat(String toUserId, String fromUserId) {
		String key = HEART_BEAT + toUserId + "-" + fromUserId;
		ServerDataPool.redisDataManager.heartBeat(key, System.currentTimeMillis());
	}
}
