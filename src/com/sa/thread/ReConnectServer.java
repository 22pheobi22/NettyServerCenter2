package com.sa.thread;

import java.util.List;
import java.util.Set;

import com.sa.base.ConfManager;
import com.sa.base.ServerDataPool;
import com.sa.client.ChatClient;
import com.sa.util.JedisUtil;
import com.sa.util.ReadConf;

public class ReConnectServer implements Runnable {

	@Override
	public void run() {
//		while(true) {
//			try {
//				Thread.sleep(1000*30);
//			} catch (Exception e) {
//				e.printStackTrace();
//			}
//			try {
//				boolean isMasterCenter= false;
//				JedisUtil jedisUtil = new JedisUtil();
//				List<String> hashValsAll = jedisUtil.getHashValsAll("centerRoleInfo");
//				if(null!=hashValsAll){
//					String masterCenterAddress = jedisUtil.getHash("centerRoleInfo", "master");
//					if(null!=masterCenterAddress&&masterCenterAddress.equals(ConfManager.getCenterIp()+":"+ConfManager.getClientSoketServerPort())){
//						isMasterCenter=true;
//					}
//				}
//				if(isMasterCenter){
//					//连接服务
//					Set<String> ips = ServerDataPool.USER_CHANNEL_MAP.keySet();
//					for (String p : ips) {
//						System.out.println("已连接服务："+p);
//					}
//					new ReadConf().readFileByLines();
//					String[] address = ConfManager.getServerAddress();
//					if(null!=address&&address.length>0){
//						for (int i = 0; i < address.length; i++) {
//							System.out.println("配置服务："+address[i]);
//							String[] addr = address[i].split(":");
//							if(!ips.contains(addr[0])){
//								Thread centerToServer = new Thread(new ChatClient(addr[0], Integer.valueOf(addr[1]),true));
//								centerToServer.setName("centerToServer"+addr[0]);
//								centerToServer.start();
//								ServerDataPool.NAME_THREAD_MAP.put("centerToServer"+addr[0], centerToServer);
//							}
//						}
//					}
//				}
//			} catch (Exception e) {
//				e.printStackTrace();
//			}
//			try {
//				Thread.sleep(1000*30);
//			} catch (Exception e) {
//				e.printStackTrace();
//			}
//		}
	}
}
