package com.sa.thread;

import java.util.Map;

import com.sa.base.ServerDataPool;

public class RoomCancelSync implements Runnable {

	@Override
	public void run() {
		while(true) {
			Map<String, Integer> roomInfo = ServerDataPool.serverDataManager.getRoomInfo();
			for (Map.Entry<String, Integer> room : roomInfo.entrySet()) {
				String roomId = room.getKey();	// 房间id
				int personNum = room.getValue();// 房间人数
				if (0 == personNum) {			// 如果房间人数是0
					// 空闲时长，每个数代表5分钟
					int freeNum = ServerDataPool.serverDataManager.getFreeRoom(roomId);
					if (freeNum >= 12) {
						// 销毁房间
						/** 删除 房间 消息 缓存 */
						ServerDataPool.serverDataManager.cleanLogs(roomId);
						/** 删除 房间 缓存 */
						ServerDataPool.serverDataManager.removeRoom(roomId);
						/** 删除 房间 空闲 计数 缓存 */
						ServerDataPool.serverDataManager.cancelFreeRoom(roomId);
					} else {
						// 空余时长➕1
						ServerDataPool.serverDataManager.setFreeRoom(roomId,freeNum);
					}
				} else {
					ServerDataPool.serverDataManager.cancelFreeRoom(roomId);
				}
			}
			try {
//				Thread.sleep(1000*15);
				Thread.sleep(1000*60*5);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

}
