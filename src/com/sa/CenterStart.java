package com.sa;

import com.sa.base.CenterManager;
import com.sa.base.ConfManager;
import com.sa.base.ServerDataPool;
import com.sa.thread.AutoCancelTempConnect;
import com.sa.thread.DeleteRedisDataSync;
import com.sa.thread.ReConnectServer;
import com.sa.transport.ClientSocketServcer;
import com.sa.util.ReadConf;

public class CenterStart {

	public static void main(String[] args) {
		// 初始化配置
		initConf();
		
		// 启动中心
		startNetty();

		// 启动辅助服务
		auxiliaryService();
	}

	private static void initConf() {
		new ReadConf().readFileByLines();
	}

	private static void auxiliaryService() {
		// 开启自动回收临时连接线程
		new Thread(new AutoCancelTempConnect()).start();
		// 启动清除redis数据线程
		new Thread(new DeleteRedisDataSync()).start();
		// 连接新增服务线程
		new Thread(new ReConnectServer()).start();
	}

	private static void startNetty() {
		try {
			// TODO:启动主备监控服务
			new Thread(new ClientSocketServcer(ConfManager.getClientSoketServerPort())).start();

			/**
			 * 中心启动时，校验自己的角色
			 * 第一次启动 redis中无数据 直接写入ip角色信息
			 */
			boolean isMasterCenter = ServerDataPool.redisDataManager.setMasterSlave();
			
			CenterManager centerManager = new CenterManager();
			//若是主，读取服务列表，主动连接服务
			if(isMasterCenter) { // 如果是中心，启动中心连接服务
				centerManager.centerLinkServer();
			}else{
				//若是备，监测主
				centerManager.monitorLinkCenter();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	
}