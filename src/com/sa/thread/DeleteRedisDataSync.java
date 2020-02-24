package com.sa.thread;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import com.sa.base.ConfManager;
import com.sa.base.ServerDataPool;
import com.sa.util.DateUtil;
import com.sa.util.JedisUtil;

public class DeleteRedisDataSync implements Runnable {

	private String ROOM_FREE_MAP_KEY = "ROOM_FREE_MAP";
	private String USER_SERVERIP_MAP_KEY = "USER_SERVERIP_MAP";

	@Override
	public void run() {
		while(true) {
			//计算延时时间
			Long delayTime = getDelayTime();
			if(null!=delayTime){
				try {
					System.out.println("delay:"+delayTime);
					Thread.sleep(delayTime);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				//角色判断  主中心才回收
				boolean isMasterCenter= false;
				JedisUtil jedisUtil = new JedisUtil();
				List<String> hashValsAll = jedisUtil.getHashValsAll("centerRoleInfo");
				if(null!=hashValsAll){
					String masterCenterAddress = jedisUtil.getHash("centerRoleInfo", "master");
					if(null!=masterCenterAddress&&masterCenterAddress.equals(ConfManager.getCenterIp()+":"+ConfManager.getClientSoketServerPort())){
						isMasterCenter=true;
					}
					if(isMasterCenter){
						//遍历删除redis中所有房间信息
						ServerDataPool.dataManager.removeRoomAll();
						//删除redis中房间空闲时长信息
						jedisUtil.delHash(ROOM_FREE_MAP_KEY);
						//删除redis种中主备信息
						jedisUtil.delHash("centerRoleInfo");
						//删除redis中用户IP信息
						jedisUtil.delHash(USER_SERVERIP_MAP_KEY);
					}
				}
				try {
					Thread.sleep(1000);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}else{
				try {
					Thread.sleep(1000*60*3);
					System.err.println("reids数据清除线程异常");
					//TODO 发警告
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	}
	private Long getDelayTime(){
		Long delayTime = null;
		try {
			DateTimeFormatter fmt = DateTimeFormatter.ofPattern(DateUtil.DATE_FORMAT_08);
			LocalTime localTime = LocalTime.parse(ConfManager.getDelRedisTime(),fmt);
			LocalDate localDate = LocalDate.now();
			LocalDateTime localDateTime = LocalDateTime.of(localDate, localTime);
			ZoneId zone = ZoneId.systemDefault();
		    Instant instant = localDateTime.atZone(zone).toInstant();
			Date date = Date.from(instant);
			Calendar cal = Calendar.getInstance();
			cal.setTime(date);
			cal.getTimeInMillis();
			if(System.currentTimeMillis()<=cal.getTimeInMillis()){
				//如果当前日期小于等于执行时间
				delayTime=cal.getTimeInMillis()-System.currentTimeMillis();
			}else{
				//如果当前日期大于等于执行时间
				cal.add(cal.DATE, 1);
				delayTime=cal.getTimeInMillis()-System.currentTimeMillis();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return delayTime;
	}
}