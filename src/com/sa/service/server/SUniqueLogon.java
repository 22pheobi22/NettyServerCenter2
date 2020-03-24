/**
 * 
 * 项目名称:[NettyServer]
 * 包:	 [com.sa.service.server]
 * 类名称: [UniqueLogon]
 * 类描述: [一句话描述该类的功能]
 * 创建人: [Y.P]
 * 创建时间:[2017年7月17日 下午3:03:21]
 * 修改人: [Y.P]
 * 修改时间:[2017年7月17日 下午3:03:21]
 * 修改备注:[说明本次修改内容]  
 * 版本:	 [v1.0]   
 * 
 */
package com.sa.service.server;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import com.sa.base.ConfManager;
import com.sa.base.Manager;
import com.sa.base.ServerDataPool;
import com.sa.base.element.ChannelExtend;
import com.sa.net.Packet;
import com.sa.net.PacketHeadInfo;
import com.sa.net.PacketType;
import com.sa.service.client.CUniqueLogon;
import com.sa.service.client.ClientLogin;
import com.sa.service.client.ClientResponebRoomUser;
import com.sa.util.Constant;

import io.netty.channel.ChannelHandlerContext;

public class SUniqueLogon extends Packet {
	public SUniqueLogon() {
	}

	public SUniqueLogon(PacketHeadInfo packetHead) {
		this.setPacketHead(packetHead);
	}

	@Override
	public void execPacket() {

		int code = 0;
		String msg = "成功";

		String fromServerIp = (String) this.getOption(100);
		/** 获取 用户 角色 */
		String role = (String) this.getOption(2);
		/** 格式化 用户角色 */
		HashSet<String> userRole = toRole(role);
		/** 判断用户是否登陆过 code=1未登录 code=0已登录 */
		Map<String, Object> checkUniqueLogonResult = checkUniqueLogon(userRole);

		if ("0".equals(String.valueOf(checkUniqueLogonResult.get("code")))) {
			/** 返回code=0 已登录 */
			// 已登录则注销上次登录--旧sever通道
			doLogonUngister((ChannelHandlerContext) checkUniqueLogonResult.get("result"));
		}
		// 獲取最新登錄服務IP
		if (null == fromServerIp || "".equals(fromServerIp)) {
			return;
		}
		// 新server通道
		ChannelHandlerContext context = ServerDataPool.USER_CHANNEL_MAP.get(fromServerIp);
		ChannelExtend ce = ServerDataPool.CHANNEL_USER_MAP.get(context);
		if (null == context || null == ce || null == ce.getConnBeginTime()) {
			return;
		}

		if (10093 != code) {
			/** 注册登录 */
			doLogin(context, ce, userRole, role);
		}

		/** 登录信息 下行 处理 */
		clientLogin(code, msg, role, context);

	}

	@Override
	public PacketType getPacketType() {
		return PacketType.SUniqueLogon;
	}

	private void clientLogin(int code, String msg, String role, ChannelHandlerContext context) {
		ClientLogin cl = new ClientLogin(this.getPacketHead());

		cl.setToUserId(this.getFromUserId());
		cl.setStatus(code);
		cl.setOption(1, this.getOption(1));
		cl.setOption(2, role);
		cl.setOption(254, msg);

		try {
			Manager.INSTANCE.sendPacketTo(cl, context, Constant.CONSOLE_CODE_S);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/*
	 * 格式化 用户角色
	 */
	private HashSet<String> toRole(String role) {
		HashSet<String> userRole = new HashSet<String>();

		if ("1".equals(role)) {
			userRole.add(Constant.ROLE_TEACHER);
		} else if ("2".equals(role)) {
			userRole.add(Constant.ROLE_ASSISTANT);
		} else if ("3".equals(role)) {
			userRole.add(Constant.ROLE_STUDENT);
		} else if ("4".equals(role)) {
			userRole.add(Constant.ROLE_AUDIENCE);
		} else if ("5".equals(role)) {

		} else if ("0".equals(role)) {
			userRole.add(Constant.ROLE_PARENT_TEACHER);
		} else {
			userRole.add(role);
		}

		return userRole;
	}

	/** 校验 单点登录 */
	public Map<String, Object> checkUniqueLogon(HashSet<String> userRole) {
		Map<String, Object> map = new HashMap<>();
		map.put("code", 1);
		map.put("result", null);

		/** 根据 用户id 获取 用户通道 */
		ChannelHandlerContext temp = ServerDataPool.dataManager.getUserServerChannel(this.getFromUserId());
		/** 如果 用户通道 不为空 */
		if (null != temp) {
			map.put("code", 0);
			map.put("result", temp);
		}
		return map;
	}

	private void doLogonUngister(ChannelHandlerContext temp) {
		/** 发送 消息回执 */// 给原通道服务器
		CUniqueLogon cl = new CUniqueLogon(this.getTransactionId(), this.getRoomId(), this.getFromUserId(), 10098);
		cl.setFromUserId(this.getFromUserId());
		cl.setOption(254, Constant.ERR_CODE_10098);
		try {
			Manager.INSTANCE.sendPacketTo(cl, temp, Constant.CONSOLE_CODE_S);
		} catch (Exception e) {
			e.printStackTrace();
		}
		// 给房间用户发消息 通知用户注销
		noticeUserUngister(cl);

		// 注銷用戶信息
		Manager.INSTANCE.ungisterUserInfo(this.getFromUserId());
	}

	/** 通知房间内用户 */
	private void noticeUserUngister(CUniqueLogon cl) {
		String[] roomIds = null;
		// 向用户原来所在房间发减员消息
		String roomNo = ServerDataPool.dataManager.getUserRoomNo(cl.getFromUserId());
		if (null != roomNo) {
			roomIds = roomNo.split(",");
		}
		if (roomIds != null && roomIds.length > 0) {
			// 循环通知每个房间的用户
			for (String rId : roomIds) {
				ClientResponebRoomUser crru = new ClientResponebRoomUser(cl.getPacketHead());
				crru.setFromUserId(this.getFromUserId());
				crru.setStatus(0);
				crru.setOption(12, cl.getToUserId());
				crru.setRoomId(rId);
				crru.execPacket();
			}
		}
	}

	private void doLogin(ChannelHandlerContext context, ChannelExtend ce, HashSet<String> userRole, String role) {
		/** 注册用户上线信息 */
		Manager.INSTANCE.addOnlineContext(this.getRoomId(), this.getFromUserId(), (String) this.getOption(3),
				(String) this.getOption(4), (String) this.getOption(5), userRole, ConfManager.getTalkEnable(), context,
				ce.getChannelType());

		// 通知所在服务器做登录处理
		CUniqueLogon cl = new CUniqueLogon(this.getTransactionId(), this.getRoomId(), this.getFromUserId(), 0);
		cl.setFromUserId(this.getFromUserId());
		try {
			Manager.INSTANCE.sendPacketTo(cl, context, Constant.CONSOLE_CODE_S);
		} catch (Exception e) {
			e.printStackTrace();
		}

		// 给房间用户发消息 通知用户注册
		noticeUserRegister(userRole, role);
	}

	/** 通知房间内用户 */
	private void noticeUserRegister(HashSet<String> userRole, String role) {
		String[] roomIds = this.getRoomId().split(",");
		if (roomIds != null && roomIds.length > 0) {
			for (String rId : roomIds) {
				/** 实例化 房间用户列表 下行 */
				ClientResponebRoomUser crru = new ClientResponebRoomUser(this.getPacketHead());
				crru.setOption(11,
						"{\"id\":\"" + this.getFromUserId() + "\",\"name\":\"" + this.getOption(3) + "\",\"icon\":\""
								+ this.getOption(4) + "\",\"role\":[\"" + role + "\"],\"agoraId\":\""
								+ this.getOption(5) + "\"}");
				crru.setRoomId(rId);
				crru.execPacket();
			}
		}
	}
}
