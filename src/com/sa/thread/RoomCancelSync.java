package com.sa.thread;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.alibaba.fastjson.JSONObject;
import com.sa.base.ConfManager;
import com.sa.base.ServerDataPool;
import com.sa.base.element.Room;
import com.sa.base.element.Share;
import com.sa.service.client.ClientResponecRoomRemove;
import com.sa.util.Constant;
import com.sa.util.HttpClientUtil;
import com.sa.util.JedisUtil;

public class RoomCancelSync implements Runnable {

	@Override
	public void run() {
		while(true) {
			//角色判断  主中心才回收
			boolean isMasterCenter= false;
			JedisUtil jedisUtil = new JedisUtil();
			//List<String> hashValsAll = jedisUtil.getHashValsAll("centerRoleInfo");
			//if(null!=hashValsAll){
				String masterCenterAddress = jedisUtil.getHash("CENTER_MASTER_SLAVE_INFO", "MASTER");
				if(null!=masterCenterAddress&&masterCenterAddress.equals(ConfManager.getCenterIp())){
					isMasterCenter=true;
				}
			//}
			if(isMasterCenter){
				
				Map<String, Integer> roomInfo = ServerDataPool.dataManager.getRoomInfo();
				for (Map.Entry<String, Integer> room : roomInfo.entrySet()) {
					String roomId = room.getKey();	// 房间id
//				int personNum = room.getValue();// 房间人数
//				if (0 == personNum) {			// 如果房间人数是0
					// 空闲时长，每个数代表5分钟
					int freeNum = ServerDataPool.dataManager.getFreeRoom(roomId);
					if (freeNum >= 18) {
//						System.out.println("RoomCancelSync roomId = " + roomId);
						Boolean closeLession = closeLession(roomId);
						closeLession=true;
						if (closeLession) {
							System.out.println("RoomCancelSync roomId = " + roomId + "remove");
							// 销毁房间
							/** 删除 房间 消息 缓存 */
							//ServerDataPool.dataManager.cleanLogs(roomId);
							/** 删除 房间 缓存 */
							//Room removeRoom = ServerDataPool.dataManager.removeRoom(roomId);
							
							ClientResponecRoomRemove clientResponecRoomRemove = new ClientResponecRoomRemove();
							clientResponecRoomRemove.setStatus(20046);
							clientResponecRoomRemove.setOption(1, Constant.PROMPT_CODE_20046);
							clientResponecRoomRemove.setRoomId(roomId);
							clientResponecRoomRemove.setFromUserId("0");
							clientResponecRoomRemove.execPacket();
							
							/** 删除 房间 空闲 计数 缓存 */
							//ServerDataPool.dataManager.cancelFreeRoom(roomId);
						}
					} else {
						// 空余时长➕1
						ServerDataPool.dataManager.setFreeRoom(roomId,freeNum);
					}
//				} else {
//					ServerDataPool.dataManager.cancelFreeRoom(roomId);
//				}
				}
			}
			try {
//				Thread.sleep(1000*15);
				//Thread.sleep(1000*60*10);
				Thread.sleep(1000*60*3);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	private Boolean closeLession(String roomId) {
		String statNum = null;

		Room room = ServerDataPool.dataManager.getRoom(roomId);
		Map<String, Share> shareMap = room.getShare();
		if (null != shareMap) {
			Share share = shareMap.get("starcount");
			if (null != share) {
				statNum = (String) share.getContent();

				if (null != statNum && !"".equals(statNum)) {
					// System.out.println(statNum);
					statNum = statNum.replaceAll("\\{", "").replaceAll("\\}", "").replaceAll("\"", "").replaceAll(":", "-1-");
				}
			}
		}

		Boolean rs = false;
		String url = ConfManager.getStatSaveUrl();
		if (null != url && !"".equals(url)) {
			if (null == statNum) {
				statNum="";
			}

			try {
				Map<String, String> params = new HashMap<>();
				params.put("class_id", roomId);
				params.put("status", statNum);
				params.put("visit", "netty");
				
				String str = HttpClientUtil.post(url, params);
			
				JSONObject jsonObject = JSONObject.parseObject(str);
				if (null != jsonObject) {
					JSONObject object = (JSONObject) jsonObject.get("data");
					if (null != object) {
						String closeLession = (String) object.get("time_after_class_end_time");
						if (null != closeLession && !"".equals(closeLession)) {
							rs = Boolean.valueOf(closeLession);
						}
					}
				}
			} catch(Exception e) {
				e.printStackTrace();
			}
		}

		return rs;
	}
}