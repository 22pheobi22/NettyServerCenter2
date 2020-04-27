/**
 *
 * 项目名称:[NettyServer]
 * 包:	 [com.sa.base]
 * 类名称: [ServerDataManager]
 * 类描述: [一句话描述该类的功能]
 * 创建人: [Y.P]
 * 创建时间:[2017年7月15日 上午11:15:22]
 * 修改人: [Y.P]
 * 修改时间:[2017年7月15日 上午11:15:22]
 * 修改备注:[说明本次修改内容]
 * 版本:	 [v1.0]
 *
 */
package com.sa.base;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.sa.base.element.Logs;
import com.sa.base.element.People;
import com.sa.base.element.Room;

import io.netty.channel.ChannelHandlerContext;

public class DataManager {
	
	/** 获取 房间 空余 时长 */
	public Integer getFreeRoom(String roomId) {
		if(ConfManager.getIsRedis()){
			return ServerDataPool.redisDataManager.getFreeRoom(roomId);
		}else{
			return ServerDataPool.serverDataManager.getFreeRoom(roomId);
		}
	}

	/**
	 * 获取共享
	 */
	public Object getShare(String roomId, String key) {
		if(ConfManager.getIsRedis()){
			return ServerDataPool.redisDataManager.getShare(roomId,key);
		}else{
			return ServerDataPool.serverDataManager.getShare(roomId,key);
		}
	}

	/**
	 * 获取共享
	 */
	public List<Object> getShareList(String roomId, String key) {
		if(ConfManager.getIsRedis()){
			return ServerDataPool.redisDataManager.getShareList(roomId,key);
		}else{
			return ServerDataPool.serverDataManager.getShareList(roomId,key);
		}
	}

	/**
	 * 设置共享
	 */
	public void setShare(String roomId, String key, String value, String type) {
		if(ConfManager.getIsRedis()){
			ServerDataPool.redisDataManager.setShare(roomId, key, value, type);
		}else{
			ServerDataPool.serverDataManager.setShare(roomId, key, value, type);
		}
	}

	/**
	 * 更新共享
	 */
	public int updateShare(String roomId, String key, String value, int index) {
		if(ConfManager.getIsRedis()){
			return ServerDataPool.redisDataManager.updateShare(roomId, key, value, index);
		}else{
			return ServerDataPool.serverDataManager.updateShare(roomId, key, value, index);
		}
	}

	/**
	 * 更新共享
	 */
	public int updateShare(String roomId, String key, String oldValue, String newValue) {
		if(ConfManager.getIsRedis()){
			return ServerDataPool.redisDataManager.updateShare(roomId, key, oldValue, newValue);
		}else{
			return ServerDataPool.serverDataManager.updateShare(roomId, key, oldValue, newValue);
		}
	}

	/**
	 * 移出共享
	 */
	public int removeShare(String roomId, String key, String value) {
		if(ConfManager.getIsRedis()){
			return ServerDataPool.redisDataManager.removeShare(roomId, key, value);
		}else{
			return ServerDataPool.serverDataManager.removeShare(roomId, key, value);
		}
	}

	/**
	 * 移出共享
	 */
	public void removeShare(String roomId, String key) {
		if(ConfManager.getIsRedis()){
			ServerDataPool.redisDataManager.removeShare(roomId, key);
		}else{
			ServerDataPool.serverDataManager.removeShare(roomId, key);
		}
	}

	/**
	 * 移出共享
	 */
	public int removeShare(String roomId, String key, int index, int len) {
		if(ConfManager.getIsRedis()){
			return ServerDataPool.redisDataManager.removeShare(roomId, key, index, len);
		}else{
			return ServerDataPool.serverDataManager.removeShare(roomId, key, index, len);
		}	
	}	

	public void removeShare(String roomId, String key, String[] arr) {
		if(ConfManager.getIsRedis()){
			ServerDataPool.redisDataManager.removeShare(roomId, key, arr);
		}else{
			ServerDataPool.serverDataManager.removeShare(roomId, key, arr);
		}	
	}

	/**
	 * 注销聊天室
	 */
	public Room removeRoom(String roomId) {
		if(ConfManager.getIsRedis()){
			return ServerDataPool.redisDataManager.removeRoom(roomId);
		}else{
			return ServerDataPool.serverDataManager.removeRoom(roomId);
		}
	}

	/**
	 * 移出聊天室
	 */
	public synchronized void removeRoomUser(String userId) {
		if(ConfManager.getIsRedis()){
			ServerDataPool.redisDataManager.removeRoomUser(userId);
		}else{
			ServerDataPool.serverDataManager.removeRoomUser(userId);
		}
	}

	/**
	 * 移出聊天室
	 */
	public synchronized People removeRoomUser(String roomId, String userId) {
		if(ConfManager.getIsRedis()){
			return ServerDataPool.redisDataManager.removeRoomUser(roomId, userId);
		}else{
			return ServerDataPool.serverDataManager.removeRoomUser(roomId, userId);
		}
	}

	/**
	 * 禁言
	 */
	public People notSpeakAuth(String roomId, String userId) {
		if(ConfManager.getIsRedis()){
			return ServerDataPool.redisDataManager.notSpeakAuth(roomId, userId);
		}else{
			return ServerDataPool.serverDataManager.notSpeakAuth(roomId, userId);
		}
	}

	/**
	 * 移出禁言
	 */
	public People speakAuth(String roomId, String userId) {
		if(ConfManager.getIsRedis()){
			return ServerDataPool.redisDataManager.speakAuth(roomId, userId);
		}else{
			return ServerDataPool.serverDataManager.speakAuth(roomId, userId);
		}
	}

	/**
	 * 移出房间权限
	 */
	public void removeRoomAuth(String roomId, HashSet<String> roomRoles) {
		if(ConfManager.getIsRedis()){
			ServerDataPool.redisDataManager.removeRoomAuth(roomId, roomRoles);
		}else{
			ServerDataPool.serverDataManager.removeRoomAuth(roomId, roomRoles);
		}
	}

	/**
	 * 设置房间禁言权限
	 *
	 * HashSet<String> roomRoles 房间被禁言角色
	 */
	public void setRoomRole(String roomId, HashSet<String> roomRoles, boolean notSpeak) {
		if(ConfManager.getIsRedis()){
			ServerDataPool.redisDataManager.setRoomRole(roomId, roomRoles, notSpeak);
		}else{
			ServerDataPool.serverDataManager.setRoomRole(roomId, roomRoles, notSpeak);
		}
	}

	/**
	 * 获取房间
	 */
	public Room getRoom(String roomId) {
		if(ConfManager.getIsRedis()){
			return ServerDataPool.redisDataManager.getRoom(roomId);
		}else{
			return ServerDataPool.serverDataManager.getRoom(roomId);
		}
	}

	public Set<String> getRooms() {
		if(ConfManager.getIsRedis()){
			return ServerDataPool.redisDataManager.getRooms();
		}else{
			return ServerDataPool.serverDataManager.getRooms();
		}
	}

	/**
	 * 创建房间 向房间内添加人员及人员角色、权限
	 */
	public synchronized void setRoomUser(String roomId, String userId, String name, String icon, String agoraId,
			HashSet<String> userRole, boolean notSpeak) {

		if(ConfManager.getIsRedis()){
			ServerDataPool.redisDataManager.setRoomUser(roomId, userId, name, icon, agoraId, userRole, notSpeak);
		}else{
			ServerDataPool.serverDataManager.setRoomUser(roomId, userId, name, icon, agoraId, userRole, notSpeak);
		}
	}

	/**
	 * 创建房间 向房间内添加人员及人员角色、权限
	 */
	public synchronized void setRoomUser(String roomId, String userId, String name, String icon,
			HashSet<String> userRole, boolean notSpeak) {

		if(ConfManager.getIsRedis()){
			ServerDataPool.redisDataManager.setRoomUser(roomId, userId, name, icon, userRole, notSpeak);
		}else{
			ServerDataPool.serverDataManager.setRoomUser(roomId, userId, name, icon, userRole, notSpeak);
		}
	}

	/**
	 * 获取人员列表
	 */
	public Map<String, People> getRoomUesrs(String roomId) {

		if(ConfManager.getIsRedis()){
			return ServerDataPool.redisDataManager.getRoomUesrs(roomId);
		}else{
			return ServerDataPool.serverDataManager.getRoomUesrs(roomId);
		}
	}

	/**
	 * 获取普通教师列表
	 */
	public Map<String, People> getRoomTeachers(String roomIds) {

		if(ConfManager.getIsRedis()){
			return ServerDataPool.redisDataManager.getRoomTeachers(roomIds);
		}else{
			return ServerDataPool.serverDataManager.getRoomTeachers(roomIds);
		}
	}

	/**
	 * 获取人员
	 */
	public People getRoomUesr(String roomId, String userId) {
		if(ConfManager.getIsRedis()){
			return ServerDataPool.redisDataManager.getRoomUesr(roomId, userId);
		}else{
			return ServerDataPool.serverDataManager.getRoomUesr(roomId, userId);
		}
	}

	/**
	 * 设置房间人员自定义权限
	 * 
	 * @param roomId
	 *            房间ID
	 * @param userId
	 *            用户ID
	 * @param roleCode
	 *            权限CODE
	 * @param flag
	 *            操作 (+：添加 -:删除)
	 * @param num
	 *            多人或单人权限标识（1、n）
	 */
	public void setRoomUserDefAuth(String roomId, String userId, String roleCode, String flag, String num) {

		if(ConfManager.getIsRedis()){
			ServerDataPool.redisDataManager.setRoomUserDefAuth(roomId, userId, roleCode, flag, num);
		}else{
			ServerDataPool.serverDataManager.setRoomUserDefAuth(roomId, userId, roleCode, flag, num);
		}
	}

	/**
	 * 获取人员角色
	 */
	public HashSet<String> getRoomUesrRole(String roomId, String userId) {
		if(ConfManager.getIsRedis()){
			return ServerDataPool.redisDataManager.getRoomUesrRole(roomId, userId);
		}else{
			return ServerDataPool.serverDataManager.getRoomUesrRole(roomId, userId);
		}
	}

	/**
	 * 获取人员权限
	 */
	public HashMap<String, Integer> getRoomUesrAuth(String roomId, String userId) {

		if(ConfManager.getIsRedis()){
			return ServerDataPool.redisDataManager.getRoomUesrAuth(roomId, userId);
		}else{
			return ServerDataPool.serverDataManager.getRoomUesrAuth(roomId, userId);
		}
	}

	/**
	 * 获取人员存在的房间
	 */
	public String getUserRoomNo(String userId) {
		if(ConfManager.getIsRedis()){
			return ServerDataPool.redisDataManager.getUserRoomNo(userId);
		}else{
			return ServerDataPool.serverDataManager.getUserRoomNo(userId);
		}	
	}

	/** 获取 房间id 和 每个房间人数 */
	public Map<String, Integer> getRoomInfo() {

		if(ConfManager.getIsRedis()){
			return ServerDataPool.redisDataManager.getRoomInfo();
		}else{
			return ServerDataPool.serverDataManager.getRoomInfo();
		}
	}

	/**遍历删除redis中所有房间信息 */
	public void removeRoomAll() {
		ServerDataPool.redisDataManager.removeRoomAll();
	}
	
	/**遍历删除redis中所有房间信息 */
	public void removeRoomShareList() {
		ServerDataPool.redisDataManager.removeRoomShareList();
	}
	
	/**
	 * 获取聊天记录列表 String roomId 房间id String chatKey 聊天记录key 时间+事务id 逗号分隔 int
	 * chatNum 获取聊天记录数量
	 */
	public List<Logs> getRoomChats(String roomId, String chatKey, int chatNum) {

		if(ConfManager.getIsRedis()){
			return ServerDataPool.redisDataManager.getRoomChats(roomId, chatKey, chatNum);
		}else{
			return ServerDataPool.serverDataManager.getRoomChats(roomId, chatKey, chatNum);
		}	
	}

	/** 设置消息缓存 */
	public synchronized void setRoomChats(String roomId, String chatKey, String userId, String msg) {

		if(ConfManager.getIsRedis()){
			ServerDataPool.redisDataManager.setRoomChats(roomId, chatKey, userId, msg);
		}else{
			ServerDataPool.serverDataManager.setRoomChats(roomId, chatKey, userId, msg);
		}	
	}

	/** 清除历史消息记录 */
	public void cleanLogs(String roomId) {

		if(ConfManager.getIsRedis()){
			ServerDataPool.redisDataManager.cleanLogs(roomId);
		}else{
			ServerDataPool.serverDataManager.cleanLogs(roomId);
		}
	}

	public void print(String method) {
	}

	public void setFreeRoom(String roomId, int freeNum) {

		if(ConfManager.getIsRedis()){
			ServerDataPool.redisDataManager.setFreeRoom(roomId,freeNum);
		}else{
			ServerDataPool.serverDataManager.setFreeRoom(roomId,freeNum);
		}	
	}

	public void cancelFreeRoom(String roomId) {

		if(ConfManager.getIsRedis()){
			ServerDataPool.redisDataManager.cancelFreeRoom(roomId);
		}else{
			ServerDataPool.serverDataManager.cancelFreeRoom(roomId);
		}	
	}

	///////////////////////////////////////////////////////////////////////////////
	/** 刪除用戶id-ip信息 */
	public void delUserServer(String userId) {
		if(ConfManager.getIsRedis()){
			ServerDataPool.redisDataManager.delUserServer(userId);
		}else{
			//ServerDataPool.serverDataManager.delUserServer(userId);
		}
	}

	/** 获取用户所在服务器通道 */
	public ChannelHandlerContext getUserServerChannel(String userId) {
		if(ConfManager.getIsRedis()){
			return ServerDataPool.redisDataManager.getUserServerChannel(userId);
		}else{
			//ServerDataPool.serverDataManager.getUserServerChannel(userId);
		}
		return null;
	}

	/** 获取用户所在服务器ip */
	public String getUserServerIp(String userId) {
		if(ConfManager.getIsRedis()){
			return ServerDataPool.redisDataManager.getUserServerIp(userId);
		}else{
			//ServerDataPool.serverDataManager.getUserServerIp(userId);
		}
		return null;
	}

	/** 根據用戶id--获取用户所在房间其他用户所在服务ip列表--若不包含发信者ip则除外 */
	public List<ChannelHandlerContext> getChannelListOfUserRoomByUserId(String userId) {
		if(ConfManager.getIsRedis()){
			return ServerDataPool.redisDataManager.getChannelListOfUserRoomByUserId(userId);
		}else{
			//ServerDataPool.serverDataManager.getChannelListOfUserRoomByUserId(userId);
		}
		return null;
	}

	/** 根據用戶id--获取用户所在房间其他用户所在服务ip列表--若不包含发信者ip则除外 */
	public Set<String> getServerListOfUserRoomByUserId(String userId) {
		if(ConfManager.getIsRedis()){
			return ServerDataPool.redisDataManager.getServerListOfUserRoomByUserId(userId);
		}else{
			//ServerDataPool.serverDataManager.getServerListOfUserRoomByUserId(userId);
		}
		return null;
	}

	/** 根據房間id--获取用户所在房间其他用户所在服务ip列表--若不包含发信者ip则除外 */
	public Set<String> getServerListOfUserRoomByRoomId(String roomId, String userId) {
		if(ConfManager.getIsRedis()){
			return ServerDataPool.redisDataManager.getServerListOfUserRoomByRoomId(roomId, userId);
		}else{
			//ServerDataPool.serverDataManager.getServerListOfUserRoomByRoomId(roomId, userId);
		}
		return null;
	}
}
