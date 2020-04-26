package com.sa;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.sa.base.ConfManager;
import com.sa.base.ServerDataManager;
import com.sa.base.ServerDataPool;
import com.sa.client.ChatClient;
import com.sa.thread.AutoCancelTempConnect;
import com.sa.thread.DeleteRedisDataSync;
import com.sa.thread.ReConnectServer;
import com.sa.transport.ClientSocketServcer;
import com.sa.util.JedisUtil;
import com.sa.util.ReadConf;

public class CenterStart {

	public static void main(String[] args) {
		//初始化配置
		initConf();
		//開啓自動回收臨時鏈接綫程
		new Thread(new AutoCancelTempConnect()).start();
		//啓動netty服務端
		startNetty();
		//启动清除redis数据线程
		new Thread(new DeleteRedisDataSync()).start();
		//连接新增服务线程
		new Thread(new ReConnectServer()).start();
	}

	private static void initConf() {
		new ReadConf().readFileByLines();
	}

	private static void startNetty() {
		try {
			new Thread(new ClientSocketServcer(ConfManager.getClientSoketServerPort())).start();
			//Thread.sleep(5000);
			//中心启动时，校验自己的角色
			//第一次启动 redis中无数据 直接写入ip角色信息
			boolean isMasterCenter= false;
			JedisUtil jedisUtil = new JedisUtil();
			List<String> hashValsAll = jedisUtil.getHashValsAll("centerRoleInfo");
			if(hashValsAll==null||hashValsAll.size()<=0){
				Map<String,String> centerRoleMap = new HashMap<>();
				centerRoleMap.put("master", ConfManager.getCenterIp()+":"+ConfManager.getClientSoketServerPort());
				centerRoleMap.put("slave", ConfManager.getCenterIpAnother()+":"+ConfManager.getCenterPortAnother());
				jedisUtil.setHashMulti("centerRoleInfo", centerRoleMap);
				isMasterCenter = true;
			}else{
				String masterCenterAddress = jedisUtil.getHash("centerRoleInfo", "master");
				if(null!=masterCenterAddress&&masterCenterAddress.equals(ConfManager.getCenterIp()+":"+ConfManager.getClientSoketServerPort())){
					isMasterCenter=true;
				}
			}
			
			//若是主，读取服务列表，主动连接服务
			if(isMasterCenter){
				//连接服务 
				String[] address = ConfManager.getServerAddress();
				if(null!=address&&address.length>0){
					for (int i = 0; i < address.length; i++) {
						String[] addr = address[i].split(":");
						Thread centerToServer = new Thread(new ChatClient(addr[0], Integer.valueOf(addr[1]),isMasterCenter));
						centerToServer.setName("centerToServer"+addr[0]);
						centerToServer.start();
						//存储线程信息
						ServerDataPool.NAME_THREAD_MAP.put("centerToServer"+addr[0], centerToServer);
					}
				}
			}else{
			//若是备，监测主
			Thread centerToCenter = new Thread(new ChatClient(ConfManager.getCenterIpAnother(), ConfManager.getCenterPortAnother(),isMasterCenter));
			centerToCenter.setName("centerToCenter");
			centerToCenter.start();
			//存储线程信息
			ServerDataPool.NAME_THREAD_MAP.put("centerToCenter", centerToCenter);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}