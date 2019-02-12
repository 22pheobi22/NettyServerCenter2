package com.sa;

import com.sa.base.ConfManager;
import com.sa.transport.ClientSocketServcer;
import com.sa.util.ReadConf;

public class ServerStart {

	public static void main(String[] args) {
		initConf();

//		new Thread(new LogSync(ConfManager.getLogUrl(), ConfManager.getLogTime())).start();

//		new Thread(new HttpServer(ConfManager.getHttpPort())).start();

//		new Thread(new RoomCancelSync()).start();

//		if (!ConfManager.getIsCenter()) {
//			new Thread(new StatisticRoomInfoSync(ConfManager.getStatisticUrl(), ConfManager.getStatisticTime())).start();
//		} else {
//			new Thread(new ClientSocketServcer(ConfManager.getCenterPort())).start();
//		}

		startNetty();
	}

	private static void initConf() {
		new ReadConf().readFileByLines();
	}

	private static void startNetty() {
		try {
			new Thread(new ClientSocketServcer(ConfManager.getClientSoketServerPort())).start();
//			new WebSocketServer().bind(ConfManager.getWebSoketServerPort());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
