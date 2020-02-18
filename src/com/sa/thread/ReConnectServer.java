package com.sa.thread;

import java.util.List;
import java.util.Set;

import com.sa.base.ConfManager;
import com.sa.base.ServerDataPool;
import com.sa.client.ChatClient;
import com.sa.util.JedisUtil;

public class ReConnectServer implements Runnable {

	@Override
	public void run() {
		while(true) {
			try {
				Thread.sleep(1000*60*1);
			} catch (Exception e) {
				e.printStackTrace();
			}
			try {
				boolean isMasterCenter= false;
				JedisUtil jedisUtil = new JedisUtil();
				List<String> hashValsAll = jedisUtil.getHashValsAll("centerRoleInfo");
				if(null!=hashValsAll){
					String masterCenterAddress = jedisUtil.getHash("centerRoleInfo", "master");
					if(null!=masterCenterAddress&&masterCenterAddress.equals(ConfManager.getCenterIp()+":"+ConfManager.getClientSoketServerPort())){
						isMasterCenter=true;
					}
				}
				if(isMasterCenter){
					//连接服务
					Set<String> ips = ServerDataPool.USER_CHANNEL_MAP.keySet();
					String[] address = ConfManager.getServerAddress();
					if(null!=address&&address.length>0){
						for (int i = 0; i < address.length; i++) {
							String[] addr = address[i].split(":");
							if(!ips.contains(addr[0])){
								new Thread(new ChatClient(addr[0], Integer.valueOf(addr[1]),true)).start();
							}
						}
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			try {
				Thread.sleep(1000*60*4);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
}