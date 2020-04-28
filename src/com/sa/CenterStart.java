package com.sa;

import com.sa.base.CenterManager;
import com.sa.base.ServerDataPool;
import com.sa.thread.ActiveStandbySwitching;
import com.sa.thread.DeleteRedisDataSync;
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
		// 主备切换
		new Thread(new ActiveStandbySwitching()).start();
		// 启动清除redis数据线程
		new Thread(new DeleteRedisDataSync()).start();
	}

	private static void startNetty() {
		try {
			/**
			 * 中心启动时，校验自己的角色
			 * 第一次启动 redis中无数据 直接写入ip角色信息
			 */
			ServerDataPool.redisDataManager.setMasterSlave();
			
			new CenterManager().centerLinkServer();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	
}