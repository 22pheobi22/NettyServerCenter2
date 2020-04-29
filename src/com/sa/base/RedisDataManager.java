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

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import com.alibaba.fastjson.JSON;
import com.sa.base.element.ChatLog;
import com.sa.base.element.Logs;
import com.sa.base.element.People;
import com.sa.base.element.Room;
import com.sa.base.element.Share;
import com.sa.util.Constant;
import com.sa.util.JedisUtil;
import com.sa.util.StringUtil;

import io.netty.channel.ChannelHandlerContext;

public class RedisDataManager {
	private JedisUtil jedisUtil = new JedisUtil();

	private String ROOM_FREE_MAP_KEY = "ROOM_FREE_MAP";
	private String ROOM_INFO_MAP_KEY = "ROOM_INFO_MAP_";
	private String USER_SERVERIP_MAP_KEY = "USER_SERVERIP_MAP";
	private String SHARE_LIST_KEY = "SHARE_KEY_LIST";
	private String ROOM_SHARE_KEY = "ROOM_SHARE_";
	private final String CENTER_MASTER_SLAVE_INFO = "CENTER_MASTER_SLAVE_INFO";
	private final String CENTER_HEART_BEAT = "CENTER-HEART-BEAT-";
	private final String MASTER = "MASTER";
	private final String SLAVE = "SLAVE";

	/**
	 * 系统管理员
	 */
	private String IM_ADMIN_SYSTEM_SYS_USER = "im_admin_system_sys_user_";

	/** 获取 房间 空余 时长 */
	public Integer getFreeRoom(String roomId) {
		int freeNum = 0;

		if (null == jedisUtil.getHash(ROOM_FREE_MAP_KEY, roomId)
				|| "".equals(jedisUtil.getHash(ROOM_FREE_MAP_KEY, roomId))) {
			jedisUtil.setHash(ROOM_FREE_MAP_KEY, roomId, "0");
		} else {
			freeNum = Integer.valueOf(jedisUtil.getHash(ROOM_FREE_MAP_KEY, roomId));
		}
		return freeNum;
	}

	/**
	 * 获取共享
	 */
	/*
	 * public Map<String, Share> getShare(String roomId) { // 根据roomid获取房间信息
	 * Room room = this.getRoom(roomId);
	 * 
	 * return room.getShare(); }
	 */

	/**
	 * 获取type=1的共享
	 */
	public Object getShare(String roomId, String key) {
		// 根据roomid获取房间信息
		Room room = this.getRoom(roomId);

		Share share = room.getShare().get(key);
		if (null == share) {
			return null;
		}

		return share.getContent();
	}

	/**
	 * 获取type=n的共享
	 */
	public List<Object> getShareList(String roomId, String key) {
		List<String> rangeOfList = jedisUtil.getRangeOfList(ROOM_SHARE_KEY + roomId + "_" + key, 0, -1);
		if (rangeOfList.size() > 0) {
			List<Object> shareList = new ArrayList<>();
			shareList.addAll(rangeOfList);
			return shareList;
		}
		return null;
	}

	/**
	 * 设置共享
	 */
	public void setShare(String roomId, String key, String value, String type) {
		if ("n".equals(type)) {
			if (null != value) {
				String[] values = value.split("##");
				//for (String v : values) {
					jedisUtil.addEleIntoList(ROOM_SHARE_KEY + roomId+"_"+key, values);
				//}
				jedisUtil.addEleIntoSet(SHARE_LIST_KEY, key);
			}
		} else if ("1".equals(type)) {
			// 根据roomid获取房间信息
			Room room = this.getRoom(roomId);

			Map<String, Share> shareMap = room.getShare();

			Share share = shareMap.get(key);
			if (null == share) {
				share = new Share(type);
				shareMap.put(key, share);
			}
			share.setContent(value);
			jedisUtil.setString(ROOM_INFO_MAP_KEY + roomId, JSON.toJSONString(room));
		}
	}

	/**
	 * 更新key對應集合指定索引処值為新值
	 * @param roomId
	 * @param key
	 * @param value
	 * @param index
	 * @return
	 */
	public int updateShare(String roomId, String key, String value, int index) {
		int rs = 0;
		long len = jedisUtil.getLengthOfList(ROOM_SHARE_KEY + roomId + "_" + key);
		// key不存在或列表為空 則創建集合並添加元素到集合
		if (len == 0) {
			jedisUtil.addEleIntoList(ROOM_SHARE_KEY + roomId + "_" + key, value);
		} else if (index > len-1) {
			rs = -1;
			return rs;
		} else {
			jedisUtil.setEleOfListByIndex(ROOM_SHARE_KEY + roomId + "_" + key, index, value);
		}
		return rs;
	}

	/**
	 * 更新共享--替換集合共享中指定值為新值
	 * @param roomId
	 * @param key
	 * @param oldValue
	 * @param newValue
	 * @return
	 */
	public int updateShare(String roomId, String key, String oldValue, String newValue) {
		int rs = 0;
		//更新值等於被更新值 不做處理
		if (oldValue.equals(newValue)) {
			return rs;
		}
		//集合長度為0 不處理
		long length = jedisUtil.getLengthOfList(ROOM_SHARE_KEY + roomId + "_" + key);
		if(length==0){
			return -1;
		}
		jedisUtil.replaceEleInList(ROOM_SHARE_KEY + roomId + "_" + key, oldValue, newValue);
		return rs;
	}

	/**
	 * 移出key對應集合中指定值
	 */
	public int removeShare(String roomId, String key, String value) {
		long length = jedisUtil.getLengthOfList(ROOM_SHARE_KEY + roomId + "_" + key);
		if(0==length){
			return -1;
		}
		jedisUtil.removeEleFromList(ROOM_SHARE_KEY + roomId + "_" + key, 0, value);
		return 0;
	}

	/**
	 * 移出共享
	 */
	public void removeShare(String roomId, String key) {
		boolean b = jedisUtil.setContain(SHARE_LIST_KEY, key);
		if (b) {
			jedisUtil.delString(ROOM_SHARE_KEY + roomId + "_" + key);
		} else {
			// 根据roomid获取房间信息
			Room room = this.getRoom(roomId);

			room.getShare().remove(key);
			jedisUtil.setString(ROOM_INFO_MAP_KEY + roomId, JSON.toJSONString(room));
		}
	}

	/**
	 * 移出集合共享中key對應的指定索引処指定長度的内容
	 */
	public int removeShare(String roomId, String key, int index, int len) {
		int rs = 0;
		long length = jedisUtil.getLengthOfList(ROOM_SHARE_KEY + roomId + "_" + key);
		if (length == 0) {
			return -1;
		}
		if (index > length - 1) {
			return -2;
		}
		if ((index + len - 1) > length - 1) {
			return -3;
		}
		if (len < 0) {
			return -3;
		}
		//先將要移除元素設置成特殊值，再統一移除
		for (int i = 0; i < len; i++) {
			jedisUtil.setEleOfListByIndex(ROOM_SHARE_KEY + roomId + "_" + key, index + i, "@#￥");
		}
		jedisUtil.removeEleFromList(ROOM_SHARE_KEY + roomId + "_" + key, 0, "@#￥");
		return rs;
	}

	/**
	 * 移除key對應共享集合中指定索引對應的值
	 * @param roomId
	 * @param key
	 * @param arr
	 * @return
	 */
	public int removeShare(String roomId, String key, String[] arr) {
		int rs = 0;
		long length = jedisUtil.getLengthOfList(ROOM_SHARE_KEY + roomId + "_" + key);
		if (length == 0) {
			return -1;
		}
		Object[] index = sort_asc(arr);
		for (int i = index.length - 1; i >= 0; i--) {
			if (length > (int) index[i]) {
				jedisUtil.setEleOfListByIndex(ROOM_SHARE_KEY + roomId + "_" + key, (int) index[i], "@#￥");
			}
		}
		jedisUtil.removeEleFromList(ROOM_SHARE_KEY + roomId + "_" + key, 0, "@#￥");
		return rs;
	}

	private Object[] sort_asc(String[] arr) {
		Set<Integer> index = new TreeSet<Integer>();

		for (int i = 0; i < arr.length; i++) {
			if (null == arr[i] || "".equals(arr[i]))
				continue;

			index.add(Integer.parseInt(arr[i]));
		}
		return index.toArray();
	}

	/**
	 * 注销聊天室
	 */
	public Room removeRoom(String roomId) {
		Room room = this.getRoom(roomId);
		jedisUtil.delString(ROOM_INFO_MAP_KEY + roomId);
		return room;
	}

	/*
	 * public void removeRoomServerIp(String roomId) { // 根据roomid获取房间信息 Room
	 * room = this.getRoom(roomId);
	 * 
	 * Map<String, People> peoplesMap = room.getPeoples(); for (Entry<String,
	 * People> people : peoplesMap.entrySet()) { String userId =
	 * people.getKey(); jedisUtil.delHash(USER_SERVERIP_MAP_KEY, userId); } }
	 */

	/**
	 * 移出聊天室
	 */
	public synchronized void removeRoomUser(String userId) {
		// Set<String> keysAll = jedisUtil.getKeysAll(ROOM_INFO_MAP_KEY + "*");
		Set<String> keysAll = jedisUtil.scanKeys(ROOM_INFO_MAP_KEY + "*");
		// 便利房间信息Map
		for (String key : keysAll) {
			String roomId = key.replace(ROOM_INFO_MAP_KEY, "");
			String roomStr = jedisUtil.getString(key);
			if (null != roomStr) {
				Room room = JSON.parseObject(roomStr, Room.class);
				// 如果房间里有符合userid的人
				if (null != room.getPeoples().get(userId)) {
					// 将用户从聊天室踢出
					removeRoomUser(roomId, userId);
				}
			}
		}
	}

	/**
	 * 移出聊天室
	 */
	public synchronized People removeRoomUser(String roomId, String userId) {

		// 根据roomid获取房间信息
		Room room = this.getRoom(roomId);

		// 获取根据userid被成功删除的人员信息
		People people = room.getPeoples().remove(userId);
		// 如果人员信息不为空
		if (null != people) {
			// 设置房间内人数-1
			room.setPeopleNum(room.getPeopleNum() - 1);
			jedisUtil.setString(ROOM_INFO_MAP_KEY + roomId, JSON.toJSONString(room));
		}

		this.print("removeRoomUser");

		return people;
	}

	/**
	 * 禁言
	 */
	public People notSpeakAuth(String roomId, String userId) {

		// 根据roomid获取房间信息
		Room room = this.getRoom(roomId);

		// 根据userId获取用户信息
		People people = room.getPeoples().get(userId);
		// 如果用户不为空
		if (null != people) {
			// 设置禁言
			people.getAuth().put(Constant.AUTH_SPEAK, 0);
			room.getPeoples().put(userId, people);

			room.getNotSpeakPeoples().put(userId, 0);
			jedisUtil.setString(ROOM_INFO_MAP_KEY + roomId, JSON.toJSONString(room));

			System.out.println("userId:" + userId + "==auth:" + people.getAuth().get(Constant.AUTH_SPEAK));
		}
		return people;
	}

	/**
	 * 移出禁言
	 */
	public People speakAuth(String roomId, String userId) {

		// 根据roomid获取房间信息
		Room room = this.getRoom(roomId);
		// 根据userId获取用户信息
		People people = room.getPeoples().get(userId);
		// 如果人员信息不为空
		if (null != people) {
			// 设置用户禁言状态
			people.getAuth().put(Constant.AUTH_SPEAK, 1);
			room.getNotSpeakPeoples().remove(userId);
			jedisUtil.setString(ROOM_INFO_MAP_KEY + roomId, JSON.toJSONString(room));
		}

		return people;
	}

	/**
	 * 移出房间权限
	 */
	public void removeRoomAuth(String roomId, HashSet<String> roomRoles) {

		// 根据roomid获取房间信息
		Room room = this.getRoom(roomId);
		// 遍历房间内人员信息Map
		for (Map.Entry<String, People> entry : room.getPeoples().entrySet()) {
			// 获取人员角色
			HashSet<String> roleHS = entry.getValue().getRole();
			// 遍历角色集合
			for (Iterator<?> it = roleHS.iterator(); it.hasNext();) {
				// 如果人员角色符合房间禁言规则
				if (roomRoles.contains(it.next())) {
					// 解除禁言
					entry.getValue().getAuth().remove(Constant.AUTH_SPEAK);
					room.getNotSpeakPeoples().remove(entry.getKey());
				}
			}
		}
		// 删除禁止操作角色权限
		room.getNotDoRole().removeAll(roomRoles);
		jedisUtil.setString(ROOM_INFO_MAP_KEY + roomId, JSON.toJSONString(room));
	}

	/**
	 * 设置房间禁言权限
	 *
	 * HashSet<String> roomRoles 房间被禁言角色
	 */
	public void setRoomRole(String roomId, HashSet<String> roomRoles, boolean notSpeak) {

		// 根据roomid获取房间信息
		Room room = this.getRoom(roomId);

		// 设置初始化禁止操作角色
		room.setNotDoRole(roomRoles);
		// 设置初始化禁止操作角色
		room.getNotDoRole().add("role");
		// 遍历房间内人员信息
		for (Map.Entry<String, People> entry : room.getPeoples().entrySet()) {
			// 获取人员角色
			HashSet<String> peopleRole = entry.getValue().getRole();

			boolean flag = true;
			// 遍历用户角色
			for (Iterator<?> it = peopleRole.iterator(); it.hasNext();) {
				// 如果人员角色包含禁言角色
				if (roomRoles.contains(it.next())) {
					// 设置禁言
					entry.getValue().getAuth().put(Constant.AUTH_SPEAK, 0);
					room.getNotSpeakPeoples().put(entry.getKey(), 0);
					flag = false;
				}
			}

			if (flag || !notSpeak) {
				entry.getValue().getAuth().put(Constant.AUTH_SPEAK, 1);
				room.getNotSpeakPeoples().remove(entry.getKey());
			}
		}
		jedisUtil.setString(ROOM_INFO_MAP_KEY + roomId, JSON.toJSONString(room));

	}

	/**
	 * 获取房间
	 */
	public Room getRoom(String roomId) {
		Room room = null;
		// 根据roomId获取房间信息
		String roomStr = jedisUtil.getString(ROOM_INFO_MAP_KEY + roomId);
		// 如果没有该房间
		if (null == roomStr || "".equals(roomStr)) {
			// 实例化一个房间
			room = new Room();
			// 创建房间
			roomStr = JSON.toJSONString(room);
			jedisUtil.setString(ROOM_INFO_MAP_KEY + roomId, roomStr);
		} else {
			room = JSON.parseObject(roomStr, Room.class);
		}

		return room;
	}

	public Set<String> getRooms() {

		Set<String> keys = new HashSet<>();

		// Set<String> keysAll = jedisUtil.getKeysAll(ROOM_INFO_MAP_KEY + "*");
		Set<String> keysAll = jedisUtil.scanKeys(ROOM_INFO_MAP_KEY + "*");
		for (String key : keysAll) {
			String roomId = key.replace(ROOM_INFO_MAP_KEY, "");
			String roomStr = jedisUtil.getString(key);
			if (null != roomStr) {
				Room room = JSON.parseObject(roomStr, Room.class);
				keys.add(roomId + "[" + room.getPeopleNum() + "]");
			}
		}
		return keys;
	}

	/**
	 * 创建房间 向房间内添加人员及人员角色、权限
	 */
	public synchronized void setRoomUser(String roomId, String userId, String name, String icon, String agoraId,
			HashSet<String> userRole, boolean notSpeak) {

		// 根据roomid获取房间信息
		Room room = this.getRoom(roomId);
		// 获取房间内人员信息Map
		Map<String, People> peopleMap = room.getPeoples();
		// 根据userId获取用户信息
		People people = peopleMap.get(userId);
		// 如果人员信息为空
		if (null == people) {
			// 实例化人员信息
			people = new People();
		}
		// 设置说话权限
		people.setIcon(icon);
		people.setName(name);
		people.setAgoraId(agoraId);
		people.getAuth().put(Constant.AUTH_SPEAK, 1);
		if (null != room.getNotSpeakPeoples().get(userId)) {
			people.getAuth().put(Constant.AUTH_SPEAK, 0);
		}
		// 获取房间禁言角色
		HashSet<String> notDoRole = room.getNotDoRole();
		// 如果禁言角色为空
		if (null == notDoRole) {
			// 如果禁止发言
			if (notSpeak) {
				// 设置用户为禁止发言
				people.getAuth().put(Constant.AUTH_SPEAK, 0);
				room.getNotSpeakPeoples().put(userId, 0);
			}
		} else {
			// 遍历用户角色
			for (Iterator<?> it = userRole.iterator(); it.hasNext();) {
				// 如果禁言角色包含用户角色
				if (notDoRole.contains(it.next())) {
					// 设置用户为禁止发言
					people.getAuth().put(Constant.AUTH_SPEAK, 0);
					room.getNotSpeakPeoples().put(userId, 0);
				}
			}
		}

		// 将用户角色添加到人员角色信息中
		people.getRole().addAll(userRole);
		// 向房间内添加用户及相关信息
		peopleMap.put(userId, people);
		// 设置房间用户数量增量+1
		room.setPeopleNum(room.getPeopleNum() + 1);

		// 复杂对象转json存储到redis
		jedisUtil.setString(ROOM_INFO_MAP_KEY + roomId, JSON.toJSONString(room));
		this.print("setRoomUser");
	}

	/**
	 * 创建房间 向房间内添加人员及人员角色、权限
	 */
	public synchronized void setRoomUser(String roomId, String userId, String name, String icon,
			HashSet<String> userRole, boolean notSpeak) {

		// 根据roomid获取房间信息
		Room room = this.getRoom(roomId);
		// 获取房间内人员信息Map
		Map<String, People> peopleMap = room.getPeoples();
		// 根据userId获取用户信息
		People people = peopleMap.get(userId);
		// 如果人员信息为空
		if (null == people) {
			// 实例化人员信息
			people = new People();
		}
		// 设置说话权限
		people.setIcon(icon);
		people.setName(name);
		people.getAuth().put(Constant.AUTH_SPEAK, 1);
		if (null != room.getNotSpeakPeoples().get(userId)) {
			people.getAuth().put(Constant.AUTH_SPEAK, 0);
		}
		// 获取房间禁言角色
		HashSet<String> notDoRole = room.getNotDoRole();
		// 如果禁言角色为空
		if (null == notDoRole) {
			// 如果禁止发言
			if (notSpeak) {
				// 设置用户为禁止发言
				people.getAuth().put(Constant.AUTH_SPEAK, 0);
				room.getNotSpeakPeoples().put(userId, 0);
			}
		} else {
			// 遍历用户角色
			for (Iterator<?> it = userRole.iterator(); it.hasNext();) {
				// 如果禁言角色包含用户角色
				if (notDoRole.contains(it.next())) {
					// 设置用户为禁止发言
					people.getAuth().put(Constant.AUTH_SPEAK, 0);
					room.getNotSpeakPeoples().put(userId, 0);
				}
			}
		}

		// 将用户角色添加到人员角色信息中
		people.getRole().addAll(userRole);
		// 向房间内添加用户及相关信息
		peopleMap.put(userId, people);
		// 设置房间用户数量增量+1
		room.setPeopleNum(room.getPeopleNum() + 1);

		// 复杂对象转json存储到redis
		jedisUtil.setString(ROOM_INFO_MAP_KEY + roomId, JSON.toJSONString(room));
		this.print("setRoomUser");
	}

	/**
	 * 获取人员列表
	 */
	public Map<String, People> getRoomUesrs(String roomId) {

		// 根据roomid获取房间信息
		Room room = this.getRoom(roomId);

		this.print("getRoomUesrs");

		return room.getPeoples();
	}

	/**
	 * 获取普通教师列表
	 */
	public Map<String, People> getRoomTeachers(String roomIds) {

		Map<String, People> teachers = new HashMap<String, People>();
		if (null == roomIds || "".equals(roomIds)) {
			return teachers;
		}

		String[] rooms = roomIds.split(",");
		for (String roomId : rooms) {
			// 根据roomid获取房间信息
			Room room = this.getRoom(roomId);
			Map<String, People> peoples = room.getPeoples();

			for (Entry<String, People> entry : peoples.entrySet()) {
				if (entry.getValue().getRole().contains(Constant.ROLE_TEACHER)) {
					entry.getValue().setUserId(entry.getKey());
					teachers.put(roomId, entry.getValue());

					break;
				}
			}
		}
		return teachers;
	}

	/**
	 * 获取人员
	 */
	public People getRoomUesr(String roomId, String userId) {
		// 获取房间内人员信息Map
		Map<String, People> peopleMap = getRoomUesrs(roomId);
		// 根据userId获取用户信息
		People people = peopleMap.get(userId);

		return people;
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

		// 根据roomid获取房间信息
		Room room = this.getRoom(roomId);

		// 获取房间内人员信息Map
		Map<String, People> peopleMap = getRoomUesrs(roomId);

		// 根据userId获取用户信息
		People people = peopleMap.get(userId);
		// 如果人员信息不为空
		if (null != people) {
			if ("+".equals(flag)) {
				if ("1".equals(num)) {
					for (Entry<String, People> entry : peopleMap.entrySet()) {
						entry.getValue().getAuth().remove(roleCode);
					}
				}

				people.getAuth().put(roleCode, 1);
			} else if ("-".equals(flag)) {
				people.getAuth().remove(roleCode);
			}
			room.getPeoples().put(userId, people);
			jedisUtil.setString(ROOM_INFO_MAP_KEY + roomId, JSON.toJSONString(room));

		}
	}

	/**
	 * 获取人员角色
	 */
	public HashSet<String> getRoomUesrRole(String roomId, String userId) {
		// 获取房间内人员信息Map
		Map<String, People> peopleMap = getRoomUesrs(roomId);
		// 根据userId获取用户信息
		People people = peopleMap.get(userId);
		// 如果人员信息不为空
		if (null != people) {
			return people.getRole();
		}

		return null;
	}

	/**
	 * 获取人员权限
	 */
	public HashMap<String, Integer> getRoomUesrAuth(String roomId, String userId) {

		// 根据roomid获取房间信息
		Room room = this.getRoom(roomId);
		// 获取房间内人员信息Map
		Map<String, People> peopleMap = room.getPeoples();
		// 根据userId获取用户信息
		People people = peopleMap.get(userId);
		// 如果人员信息不为空
		if (null != people) {
			return people.getAuth();
		}

		return null;
	}

	/**
	 * 获取人员存在的房间
	 */
	public String getUserRoomNo(String userId) {
		String roomId = "";

		// Set<String> keysAll = jedisUtil.getKeysAll(ROOM_INFO_MAP_KEY + "*");
		Set<String> keysAll = jedisUtil.scanKeys(ROOM_INFO_MAP_KEY + "*");
		for (String key : keysAll) {
			String roomStr = jedisUtil.getString(key);
			if (null != roomStr) {
				Room room = JSON.parseObject(roomStr, Room.class);
				// 如果如果用户信息不为空
				if (null != room.getPeoples().get(userId)) {
					roomId += (key.replace(ROOM_INFO_MAP_KEY, "") + ",");
				}
			}
		}

		return roomId.equals("") ? null : roomId;
	}

	/** 获取 房间id 和 每个房间人数 */
	public Map<String, Integer> getRoomInfo() {

		// Set<String> keysAll = jedisUtil.getKeysAll(ROOM_INFO_MAP_KEY + "*");
		Set<String> keysAll = jedisUtil.scanKeys(ROOM_INFO_MAP_KEY + "*");
		Map<String, Integer> roomInfoMap = new HashMap<String, Integer>();
		for (String key : keysAll) {
			String roomId = key.replace(ROOM_INFO_MAP_KEY, "");
			String roomStr = jedisUtil.getString(key);
			if (null != roomStr) {
				Room room = JSON.parseObject(roomStr, Room.class);
				int peopleNum = room.getPeopleNum();

				roomInfoMap.put(roomId, peopleNum);
			}
		}
		return roomInfoMap;
	}

	/** 遍历删除redis中所有房间信息 */
	public void removeRoomAll() {

		Set<String> keysAll = jedisUtil.scanKeys(ROOM_INFO_MAP_KEY + "*");
		for (String key : keysAll) {
			jedisUtil.delString(key);
		}
	}
	
	/** 遍历删除redis中所有集合共享消息 */
	public void removeRoomShareList() {

		Set<String> keysAll = jedisUtil.scanKeys(ROOM_SHARE_KEY + "*");
		for (String key : keysAll) {
			jedisUtil.delString(key);
		}
	}

	/**
	 * 获取聊天记录列表 String roomId 房间id String chatKey 聊天记录key 时间+事务id 逗号分隔 int
	 * chatNum 获取聊天记录数量
	 */
	public List<Logs> getRoomChats(String roomId, String chatKey, int chatNum) {

		// 根据roomid获取房间信息
		Room room = this.getRoom(roomId);
		boolean check = true;

		ChatLog chatLog = room.getChatLog();

		List<Logs> logsList = new ArrayList<Logs>();

		TreeMap<String, Long> logPostion = chatLog.getLogPostion();

		if (null == logPostion) {
			check = false;
		}

		if (check) {
			Long index = (long) chatLog.getLogsList().size();
			if (!chatKey.equals("0")) { // 第一次获取历史消息
				if (null == logPostion.get(chatKey)) {
					check = false;
				} else {
					index = logPostion.get(chatKey);
				}
			}

			// if (check) {
			// for (int i = index; i > (index - chatNum) && size>0 && i>=0; i--)
			// {
			// if (0 <= i && null != chatLog.getLogsList().get(i)) {
			// logsList.add(chatLog.getLogsList().get(i));
			// }
			// }
			// }
			if (check) {
				long begin = 0;
				if (index - chatNum < 0) {
					begin = 0;
				} else {
					begin = index - chatNum;
				}

				for (long i = begin; i < index; i++) {
					if (0 <= i && null != chatLog.getLogsList().get((int) i)) {
						logsList.add(chatLog.getLogsList().get((int) i));
					}
				}
			}
		}

		return logsList;
	}

	/** 设置消息缓存 */
	public synchronized void setRoomChats(String roomId, String chatKey, String userId, String msg) {

		// 根据roomid获取房间信息
		Room room = this.getRoom(roomId);

		ChatLog chatLog = room.getChatLog();

		if (null == chatLog) {
			chatLog = new ChatLog();
			room.setChatLog(chatLog);
		}

		List<Logs> logsList = chatLog.getLogsList();

		if (null == logsList) {
			logsList = new ArrayList<Logs>();
			chatLog.setLogsList(logsList);
		}

		TreeMap<String, Long> logsPostion = chatLog.getLogPostion();

		if (null == logsPostion) {
			logsPostion = new TreeMap<String, Long>();
			chatLog.setLogPostion(logsPostion);
		}

		try {
			logsPostion.put(chatKey, (long) logsList.size());
		} catch (Exception e) {
			System.out
					.println("chatKey = " + chatKey + "\r\nlogsList = " + (null == logsList ? "null" : logsList.size())
							+ "\r\nlogsPostion = " + (null == logsPostion ? "null" : logsPostion.size()));
			e.printStackTrace();
		}

		People people = this.getRoomUesr(roomId, userId);
		if (Objects.isNull(people)) {
			people = getSystemUserByAccount(userId);
			if (Objects.isNull(people)) {
				return;
			}
		}

		Long sendTime = Long.parseLong(chatKey.substring(0, chatKey.indexOf(",")));

		int transactionId = Integer.parseInt(chatKey.substring(chatKey.indexOf(",") + 1));

		Logs logs = new Logs(userId, people.getIcon(), people.getName(), msg, sendTime, transactionId);

		logsList.add(logs);

		jedisUtil.setString(ROOM_INFO_MAP_KEY + roomId, JSON.toJSONString(room));
	}

	/** 清除历史消息记录 */
	public void cleanLogs(String roomId) {

		// 根据roomid获取房间信息
		Room room = this.getRoom(roomId);

		if (null != room) {
			ChatLog chatLog = room.getChatLog();
			if (null != chatLog) {
				if (null != chatLog.getLogsList()) {
					chatLog.getLogsList().clear();
				}
				if (null != chatLog.getLogPostion()) {
					chatLog.getLogPostion().clear();
				}
			}
			jedisUtil.setString(ROOM_INFO_MAP_KEY + roomId, JSON.toJSONString(room));
		}
	}

	/**
	 * 同一用户不能访问方式的数量
	 */
	public int getRoomTheSameUserCannotAccessNum(String roomId, String userId) {
		/*
		 * if (userId.endsWith("APP")) { userId = userId.replace("APP", ""); }
		 */

		Map<String, People> peoples = this.getRoomUesrs(roomId);
		int num = 0;
		for (Map.Entry<String, People> people : peoples.entrySet()) {
			if (people.equals(userId)) {
				num++;
			}
		}

		return num;
	}

	public void print(String method) {
		// for (Entry<String, Room> entry : ROOM_INFO_MAP.entrySet()) {
		// System.out.println(method + "\tROOM ID : " + entry.getKey());
		//
		// for(Iterator<?>
		// it=entry.getValue().getNotDoRole().iterator();it.hasNext();) {
		// System.out.print(it.next() + " ");
		// }
		//
		// System.out.println("people num : " +
		// entry.getValue().getPeopleNum());
		//
		// for (Entry<String, People> p :
		// entry.getValue().getPeoples().entrySet()) {
		// System.out.println(p.getKey() + "\t" + p.getValue().getName());
		// }
		//
		// System.out.println("chat num : " +
		// entry.getValue().getChatLog().getLogsList().size());
		// }
		// System.out.println("------------------------------------------------------------------------");
	}

	public void setFreeRoom(String roomId, int freeNum) {

		freeNum++;
		jedisUtil.setHash(ROOM_FREE_MAP_KEY, roomId, freeNum + "");
	}

	public void cancelFreeRoom(String roomId) {

		jedisUtil.delHash(ROOM_FREE_MAP_KEY, roomId);
	}

	///////////////////////////////////////////////////////////////////////////////
	/** 刪除用戶id-ip信息 */
	public void delUserServer(String userId) {
		jedisUtil.delHash(USER_SERVERIP_MAP_KEY, userId);
	}

	/** 获取用户所在服务器通道 */
	public ChannelHandlerContext getUserServerChannel(String userId) {
		String userServerIp = getUserServerIp(userId);
		if (null != userServerIp) {
			return ServerDataPool.USER_CHANNEL_MAP.get(userServerIp);
		}
		return null;
	}

	/** 获取用户所在服务器ip */
	public String getUserServerIp(String userId) {
		String userServerIp = null;
		userServerIp = jedisUtil.getHash(USER_SERVERIP_MAP_KEY, userId);
		return userServerIp;
	}

	/** 根據用戶id--获取用户所在房间其他用户所在服务ip列表--若不包含发信者ip则除外 */
	public Set<ChannelHandlerContext> getChannelListOfUserRoomByUserId(String userId) {
		Set<ChannelHandlerContext> channelList = new HashSet<>();
		Set<String> serverIpList = getServerListOfUserRoomByUserId(userId);
		for (String serverIp : serverIpList) {
			ChannelHandlerContext ctx = ServerDataPool.USER_CHANNEL_MAP.get(serverIp);
			if (null != ctx) {
				channelList.add(ctx);
			}
		}
		return channelList;
	}
	
	/** 根據用戶id--获取用户所在房间其他用户所在服务ip列表--发信者ip除外 */
	public Set<ChannelHandlerContext> getChannelListOfRoomByUserId(String userId) {
		Set<ChannelHandlerContext> channelList = new HashSet<>();
		Set<String> serverIpList = getServerListOfUserRoomByUserId(userId);
		for (String serverIp : serverIpList) {
			ChannelHandlerContext ctx = ServerDataPool.USER_CHANNEL_MAP.get(serverIp);
			if (null != ctx) {
				channelList.add(ctx);
			}
		}
		ChannelHandlerContext uChannel = getUserServerChannel(userId);
		channelList.remove(uChannel);
		return channelList;
	}

	/** 根據用戶id--获取用户所在房间其他用户所在服务ip列表--若不包含发信者ip则除外 */
	public Set<String> getServerListOfUserRoomByUserId(String userId) {
		String userRoomNo = getUserRoomNo(userId);
		return getServerListOfUserRoomByRoomId(userRoomNo, userId);
	}

	/** 根據房間id--获取用户所在房间其他用户所在服务ip列表--若不包含发信者ip则除外 */
	public Set<String> getServerListOfUserRoomByRoomId(String roomId, String userId) {
		Set<String> serverList = new HashSet<>();
		String[] roomIds = roomId.split(",");
		if (null != roomIds && roomIds.length > 0) {
			for (int i = 0; i < roomIds.length; i++) {
				Map<String, People> roomUesrs = getRoomUesrs(roomIds[i]);
				for (Entry<String, People> roomUser : roomUesrs.entrySet()) {
					if (null != userId && !userId.equals(roomUser.getKey())) {
						serverList.add(getUserServerIp(roomUser.getKey()));
					}
				}
			}
		}
		return serverList;
	}
	/**獲取除中心及用戶所在服務外 其他服務通道*/
	public Collection<ChannelHandlerContext> listOtherServerChannlByUserId(String userId) {
		ChannelHandlerContext uChannel = getUserServerChannel(userId);
		ChannelHandlerContext cChannel = getUserServerChannel(ConfManager.getCenterId());
		Collection<ChannelHandlerContext> values = ServerDataPool.USER_CHANNEL_MAP.values();
		values.remove(uChannel);
		values.remove(cChannel);
		return values;
	}

	/**
	 * 获取系统管理员
	 * 
	 * @param account
	 * @return
	 */
	public People getSystemUserByAccount(String account) {

		String key = USER_SERVERIP_MAP_KEY + account;
		String userStr = jedisUtil.getString(key);
		if (userStr == null || userStr.equals("")) {
			return null;
		}

		Map<String, Object> map = JSON.parseObject(userStr, Map.class);
		if (map == null || map.size() == 0) {
			return null;
		}

		String name = (String) map.get("name");

		People people = new People();
		people.setUserId(account);
		people.setName(name);
		people.setIcon("");

		return people;
	}
	
	/**
	 * 判断且设置中心主备信息
	 * @return true：本机本服务是主中心；false：本机本服务是备中心
	 */
	public void setMasterSlave() {
		Map<String, String> hashValsAll = jedisUtil.getHashAll(CENTER_MASTER_SLAVE_INFO);

		if(null == hashValsAll ||  0 >= hashValsAll.size()) {
			hashValsAll = new HashMap<>();
			hashValsAll.put(MASTER, ConfManager.getCenterIp());

			jedisUtil.setHashMulti(CENTER_MASTER_SLAVE_INFO, hashValsAll);
		} else {
			int index = 0;
			long now = System.currentTimeMillis();
			String master = hashValsAll.get(MASTER);
			Set<String> keys = getHeartBeats(CENTER_HEART_BEAT + master + "-");
			for (String key : keys) {
				String value = getHeartBeat(key);
				long time = Long.parseLong(value);
				if (now - time < ConfManager.getCenterMasterOvertime()) {
					index++;
					break;
				}
			}

			if (0 == index) {
				hashValsAll.put(MASTER, ConfManager.getCenterIp());
				jedisUtil.setHashMulti(CENTER_MASTER_SLAVE_INFO, hashValsAll);
			} else {
				hashValsAll.put(SLAVE, ConfManager.getCenterIp());
				jedisUtil.setHashMulti(CENTER_MASTER_SLAVE_INFO, hashValsAll);
			}
		}
	}
	/**更改redis種主備角色信息*/
	public void modifyMasterSlave(String masterAddr,String slaveAddr) {
		Map<String,String> centerRoleMap = new HashMap<>();
		centerRoleMap.put("master", masterAddr);
		centerRoleMap.put("slave", slaveAddr);
		jedisUtil.setHashMulti(CENTER_MASTER_SLAVE_INFO, centerRoleMap);
	}
	
	public String getCenterMaster() {
		return jedisUtil.getHash(CENTER_MASTER_SLAVE_INFO, MASTER);
	}

	public void modifyMasterSlave() {
		Map<String,String> centerInfoMap = jedisUtil.getHashAll(CENTER_MASTER_SLAVE_INFO);

		Map<String,String> tempMap = new HashMap<>();
		tempMap.put(MASTER, centerInfoMap.get(SLAVE));
		tempMap.put(SLAVE, centerInfoMap.get(MASTER));

		jedisUtil.setHashMulti(CENTER_MASTER_SLAVE_INFO, tempMap);
	}

	public void heartBeat(String key, long currentTimeMillis) {
		jedisUtil.setString(key, String.valueOf(currentTimeMillis));
	}

	public Set<String> getHeartBeats(String key) {
		return jedisUtil.scanKeys(key);
	}
	
	public String getHeartBeat(String key) {
		return jedisUtil.getString(key);
	}

	/**校驗發送和目標用戶是否在同一服務器*/
	public boolean checkSourceAndTargetServer(String fromUserId, String toUserId) {
		boolean b =false;
		if(!StringUtil.isEmpty(fromUserId)&&!StringUtil.isEmpty(toUserId)){
			String fromServerIp = getUserServerIp(fromUserId);
			String toServerIp = getUserServerIp(fromUserId);
			if(fromServerIp.equals(toServerIp)){
				b=true;
			}
		}
		return b;
	}
}