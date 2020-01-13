package com.sa;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.sa.base.ConfManager;
import com.sa.client.ChatClient;
import com.sa.thread.AutoCancelTempConnect;
import com.sa.thread.MongoLogSync;
import com.sa.thread.RoomCancelSync;
import com.sa.transport.ClientSocketServcer;
import com.sa.transport.WebSocketServer;
import com.sa.util.JedisUtil;
import com.sa.util.ReadConf;

public class ServerStart {

	public static void main(String[] args) {
		initConf();
		
		new Thread(new AutoCancelTempConnect()).start();

		/** 是否 启用 mongodb*/
		boolean mongodbEnable = ConfManager.getMongodbEnable();
		if(mongodbEnable){
			new Thread(new MongoLogSync(ConfManager.getMongoIp(), ConfManager.getMongoPort(), ConfManager.getMongoNettyLogDBName(),ConfManager.getMongoNettyLogTableName(),ConfManager.getMongoNettyLogUserName(),ConfManager.getMongoNettyLogPassword(), ConfManager.getLogTime(),false)).start();
		}

		new Thread(new RoomCancelSync()).start();

/*		new Thread(new HttpServer(ConfManager.getHttpPort())).start();
		new Thread(new LogSync(ConfManager.getLogUrl(), ConfManager.getLogTime())).start();
		if (!ConfManager.getIsCenter()) {
			new Thread(new StatisticRoomInfoSync(ConfManager.getStatisticUrl(), ConfManager.getStatisticTime())).start();
		} else {
			new Thread(new ClientSocketServcer(ConfManager.getCenterPort())).start();
		}*/

		startNetty();

	}

	private static void initConf() {
		new ReadConf().readFileByLines();
	}

	private static void startNetty() {
		try {
			new Thread(new ClientSocketServcer(ConfManager.getClientSoketServerPort())).start();
			new Thread(new WebSocketServer(ConfManager.getWebSoketServerPort())).start();
			//Thread.sleep(5000);
			//中心启动时，校验自己的角色
			//第一次启动 redis中无数据 直接写入ip角色信息
			boolean isMasterCenter= false;
			JedisUtil jedisUtil = new JedisUtil();
			//JedisPoolUtil jedisPoolUtil = new JedisPoolUtil();
			//Jedis jedis = jedisPoolUtil.getJedis();
			List<String> hashValsAll = jedisUtil.getHashValsAll("centerRoleInfo");
			if(hashValsAll==null||hashValsAll.size()<=0){
				Map<String,String> centerRoleMap = new HashMap<>();
				centerRoleMap.put("master", ConfManager.getCenterIp()+":"+ConfManager.getClientSoketServerPort());
				centerRoleMap.put("slave", ConfManager.getCenterIpAnother()+":"+ConfManager.getCenterPortAnother());
				//jedis.hmset("centerRoleInfo", centerRoleMap);
				jedisUtil.setHashMulti("centerRoleInfo", centerRoleMap);
				//Jedis jedis = jedisPool.getJedis();
				isMasterCenter = true;
			}else{
				String masterCenterAddress = jedisUtil.getHash("centerRoleInfo", "master");
				if(null!=masterCenterAddress&&masterCenterAddress.equals(ConfManager.getCenterIp()+":"+ConfManager.getClientSoketServerPort())){
					isMasterCenter=true;
				}
			}
			
			//若是主，读取服务列表，主动连接服务
			if(isMasterCenter){
				String[] address = ConfManager.getServerAddress();
				for (int i = 0; i < address.length; i++) {
					String[] addr = address[i].split(":");
					new Thread(new ChatClient(addr[0], Integer.valueOf(addr[1]),isMasterCenter)).start();
				}
			}else{
			//若是备，监测主
			new Thread(new ChatClient(ConfManager.getCenterIpAnother(), ConfManager.getCenterPortAnother(),isMasterCenter)).start();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
