package com.sa.service.permission;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import com.sa.base.ServerDataPool;
import com.sa.util.Constant;

public enum Permission {

	INSTANCE;

	/*
	 * 设置消息
	 * Integer code 结果类型
	 * String msg 消息内容
	 */
	private Map<String, Object> setResult(Integer code, String msg) {
		Map<String, Object> map = new HashMap<>();

		map.put("code", code);
		map.put("msg", msg);

		return map;
	}

	/*
	 * 判断用户的权限是否开启
	 * String roomId	房间ID
	 * String userId	用户ID
	 * String auth		被判断权限
	 */
	public Map<String, Object> checkUserAuth(String roomId, String userId, String auth) {
		Integer code = 0;
		String msg = "";

		HashMap<String, Integer> hm = ServerDataPool.serverDataManager.getRoomUesrAuth(roomId, userId);
		if (null == hm) {
			return setResult(10099, Constant.ERR_CODE_10099);
		}

		if (null == hm.get(auth) || hm.get(auth) == 0) {
			code = 10080;
			msg = Constant.ERR_CODE_10080;
		}

		return setResult(code, msg);
	}

	/*
	 * 判断用户角色是否存在
	 * String roomId	房间ID
	 * String userId	用户ID
	 * String role		被判断角色
	 */
	public Map<String, Object> checkUserRole(String roomId, String userId, String role) {
		Integer code = 0;
		String msg = "";
		/** 获取用户角色*/
		HashSet<String> hs = ServerDataPool.serverDataManager.getRoomUesrRole(roomId, userId);
		/** 如果角色为空*/
		if (null == hs) {
			/** 设置消息*/
			return setResult(10099, Constant.ERR_CODE_10099);
		}
		/** 如果角色不包含用户角色*/
		if (!hs.contains(role)) {
			code = 10081;
			msg = Constant.ERR_CODE_10081;
		}

		return setResult(code, msg);
	}
}
