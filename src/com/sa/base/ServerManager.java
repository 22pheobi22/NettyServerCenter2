package com.sa.base;

import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;

import com.sa.base.element.ChannelExtend;
import com.sa.base.element.People;
import com.sa.net.Packet;
import com.sa.net.PacketType;
import com.sa.net.codec.PacketBinEncoder;
import com.sa.util.Constant;
import com.sa.util.StringUtil;

import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.websocketx.BinaryWebSocketFrame;

public enum ServerManager {

	INSTANCE;

	/** 向通道写消息并发送*/
	private void writeAndFlush(ChannelHandlerContext ctx, Packet pact) throws Exception {
		ChannelExtend ce = ServerDataPool.CHANNEL_USER_MAP.get(ctx);
		if (null == ce) {
			ce = ServerDataPool.TEMP_CONN_MAP.get(ctx);
		}

		if (null != ce) {
			if (0 == ce.getChannelType()) {
				//System.out.println("【ctx:"+ctx+"】【pack:"+pact+"】");
				ctx.writeAndFlush(pact);
			} else if (1 == ce.getChannelType()) {
				// 将数据包封成二进制包
				BinaryWebSocketFrame binaryWebSocketFrame = new PacketBinEncoder().encode(pact);
		
				// 把包放进通道并发送
				ctx.writeAndFlush(binaryWebSocketFrame);
			} else {
				System.out.println("未知类型连接");
			}
		} else {
			System.out.println("通道拓展信息不存在");
		}
	}

	/** 向单一用户发送数据包*/
	public void sendPacketTo(Packet pact, String consoleHead) throws Exception {
		// 如果数据包为空 或 数据接收者是中心 返回
		if(pact == null || "0".equals(pact.getToUserId())) return;

		// 获取缓存的 用户-通道信息
		Map<String, ChannelHandlerContext> contextMap  = ServerDataPool.USER_CHANNEL_MAP;
		// 空则返回
		if(StringUtil.isEmpty(contextMap)) return;

		// 获取接收用户通道
		ChannelHandlerContext targetContext = contextMap.get(pact.getToUserId());
		// 空则返回
		if(targetContext == null) return;

		// 在控制台打印消息头
		pact.printPacket(ConfManager.getConsoleFlag(), consoleHead, ConfManager.getFileLogFlag(), ConfManager.getFileLogPath());
		// 缓存消息日志
//		this.log(pact);

		// 给接收用户发送数据包
		writeAndFlush(targetContext, pact);
	}
	
	/** 向中心发送数据包*/
	public void sendPacketToCenter(Packet pact, String consoleHead) {
		// 获取服务端和用户的消息通道
		Map<String, ChannelHandlerContext> contextMap  = ServerDataPool.USER_CHANNEL_MAP;
		// 如果空则返回
		if(StringUtil.isEmpty(contextMap)) return;
		// 获取服务端和中心的管道
		ChannelHandlerContext targetContext = contextMap.get("0");
		// 如果空则返回
		if(targetContext == null) return;
		// 在控制台打印消息
		pact.printPacket(ConfManager.getConsoleFlag(), consoleHead, ConfManager.getFileLogFlag(), ConfManager.getFileLogPath());
		// 记录操作日志
//		this.log(pact);
		// 向通道写消息
		targetContext.writeAndFlush(pact);
	}

	/**
	 *  向所有在线用户发送数据包
	 * @throws Exception
	 */
	public void sendPacketToRoomAllUsers(Packet pact, String consoleHead) throws Exception{
		// 如果数据包为空 则返回
		if(pact == null ) return;

		// 获取房间内所有用户信息
		Map<String, People> roomUsers = ServerDataPool.serverDataManager.getRoomUesrs(pact.getRoomId());
		// 如果房间内没有用户 则返回
		if (null == roomUsers || 0 == roomUsers.size()) return;

		// 在控制台打印消息头
		pact.printPacket(ConfManager.getConsoleFlag(), consoleHead, ConfManager.getFileLogFlag(), ConfManager.getFileLogPath());
		// 缓存消息日志
//		this.log(pact);

		// 遍历用户map
		for (Map.Entry<String, People> entry : roomUsers.entrySet()) {
			if ("0".equals(entry.getKey())) {
				continue;
			}
			// 获取用户通道
			ChannelHandlerContext ctx = ServerDataPool.USER_CHANNEL_MAP.get(entry.getKey());
			if(null!=ctx){
				// 向通道写数据并发送
				writeAndFlush(ctx, pact);
			}
		}
	}

	/**
	 *  向所有在线用户发送数据包
	 * @throws Exception
	 */
	public void sendPacketToAllUsers(Packet pact, String consoleHead) throws Exception{
		// 如果数据包为空 则返回
		if(pact == null ) return;

		// 在控制台打印消息头
		pact.printPacket(ConfManager.getConsoleFlag(), consoleHead, ConfManager.getFileLogFlag(), ConfManager.getFileLogPath());
		// 缓存消息日志
//		this.log(pact);

		// 遍历用户-通道map
		for (Entry<String, ChannelHandlerContext> ctx : ServerDataPool.USER_CHANNEL_MAP.entrySet()) {
			// 将数据包发送给所有用户  中心除外
			if (!ConfManager.getCenterId().equals(ctx.getKey())) {
				// 发消息
				writeAndFlush(ctx.getValue(), pact);
//				ctx.getValue().writeAndFlush(pact);
			}
		}
	}
	
	/**
	 *  向所有在线用户发送数据包
	 * @throws Exception
	 */
	public void sendPacketToAllUsersExpectSourceServer(Packet pact, String consoleHead) throws Exception{
		// 如果数据包为空 则返回
		if(pact == null ) return;

		// 在控制台打印消息头
		pact.printPacket(ConfManager.getConsoleFlag(), consoleHead, ConfManager.getFileLogFlag(), ConfManager.getFileLogPath());
		// 缓存消息日志
//		this.log(pact);
		//消息来源服务ip
		String sourceServerIp = ServerDataPool.dataManager.getUserServerIp(pact.getFromUserId());
		//获取并遍历中心和源服务外的serverchannel
		Collection<ChannelHandlerContext> channels = ServerDataPool.USER_CHANNEL_MAP.values();
		channels.remove(ConfManager.getCenterId());
		channels.remove(sourceServerIp);
		
		for (ChannelHandlerContext ctx : channels) {
			// 发消息
			writeAndFlush(ctx, pact);
		}
	}

	/**
	 * 登录、注册、上线、绑定
	 */
	public void addOnlineContext(String roomId, String userId, String name, String icon, String agoraId, HashSet<String> userRole, boolean notSpeak, ChannelHandlerContext context, int channelType){
		// 如果通道为空 则抛出空指针错误
		if(context == null){
			// 抛出通道为空的异常
			throw new NullPointerException("context is null");
		}
		// 缓存 通道-用户信息
		ServerDataPool.CHANNEL_USER_MAP.put(context, new ChannelExtend(userId, channelType));
		// 缓存 用户-通道信息
		ServerDataPool.USER_CHANNEL_MAP.put(userId,context);

//		System.out.println(ServerDataPool.USER_CHANNEL_MAP.size() + "U C" + ServerDataPool.CHANNEL_USER_MAP.size());

		// 如果用户不是中心
		if (!ConfManager.getCenterId().equals(userId)) {
			// 将用户信息缓存
			String[] roomIds = roomId.split(",");
			if(roomIds!=null&&roomIds.length>0){
				//循环保存房间用户信息
				for (String rId : roomIds) {
					ServerDataPool.serverDataManager.setRoomUser(rId, userId, name, icon, agoraId, userRole, notSpeak);
				}
			}
		}

	}
	
	/**
	 * 登录、注册、上线、绑定--中心
	 */
	public void addOnlineContext(String userId,ChannelHandlerContext context, int channelType){
		// 如果通道为空 则抛出空指针错误
		if(context == null){
			// 抛出通道为空的异常
			throw new NullPointerException("context is null");
		}
		
		// 缓存 用户-通道信息
		ServerDataPool.USER_CHANNEL_MAP.put(userId,context);
		// 缓存 通道-用户信息
		ServerDataPool.CHANNEL_USER_MAP.put(context, new ChannelExtend(userId, channelType));

		/** 删除 缓存通道 */
		ServerDataPool.TEMP_CONN_MAP2.remove(userId);
		ServerDataPool.TEMP_CONN_MAP.remove(context);
	}

	/**
	 *   注销用户通信渠道
	 */
	public void ungisterUserId(String userId) {
		// 如果用户id不为空
		if(userId  != null) {
			// 获取用户通道信息
			ChannelHandlerContext ctx = ServerDataPool.USER_CHANNEL_MAP.get(userId);

			// 如果通道不为空
			if (null == ctx) {
				return;
			}
			System.out.println("用户【 "+userId + " 】注销");
			// 删除通道-用户缓存
			ServerDataPool.CHANNEL_USER_MAP.remove(ctx);
			// 删除用户-通道缓存
			ServerDataPool.USER_CHANNEL_MAP.remove(userId);

			// 如果不是中心用户id
			if (!ConfManager.getCenterId().equals(userId)) {
				// 删除房间内该用户信息
				ServerDataPool.serverDataManager.removeRoomUser(userId);
			}
			if(null!=ctx){
				// 通道关闭
				ctx.close();
			}
		}
	}

	/**
	 *   注销用户通信渠道
	 */
	public void ungisterUserContext(ChannelHandlerContext context) {
		// 如果通道不为空
		if(context  != null) {
			// 根据通道获取用户id
			ChannelExtend ce = ServerDataPool.CHANNEL_USER_MAP.get(context);
			// 如果用户id为空 则返回
			if (null == ce || null == ce.getUserId()) {
				return;
			}

			// 注销用户
			ungisterUserId(ce.getUserId());
		}
	}

	/** 想全体用户发送消息*/
	public void sendPacketToAllUsers(Packet pact, String consoleHead, String fromUserId) {
		// 如果消息为空 则返回
		if(pact == null ) return;
		// 在控制台打印消息
		pact.printPacket(ConfManager.getConsoleFlag(), consoleHead, ConfManager.getFileLogFlag(), ConfManager.getFileLogPath());
		// 记录操作日志
//		this.log(pact);
		// 遍历用户通道Map
		for (Entry<String, ChannelHandlerContext> ctx : ServerDataPool.USER_CHANNEL_MAP.entrySet()) {
			// 如果不是发信人且不是中心
			if (!ConfManager.getCenterId().equals(ctx.getKey()) && !fromUserId.equals(ctx.getKey())) {
				// 发送消息
				ctx.getValue().writeAndFlush(pact);
			}
		}
	}

	/**
	 *  向房间内所有用户发送数据包
	 * @throws Exception
	 */
	public void sendPacketToRoomAllUsers(Packet pact, String consoleHead, String fromUserId) throws Exception{
		// 如果数据包为空 则返回
		if(pact == null ) return;

		// 获取房间内所有用户信息
		Map<String, People> roomUsers = ServerDataPool.serverDataManager.getRoomUesrs(pact.getRoomId());
		// 如果房间内没有用户 则返回
		if (null == roomUsers || 0 == roomUsers.size()) return;

		// 在控制台打印消息头
		pact.printPacket(ConfManager.getConsoleFlag(), consoleHead, ConfManager.getFileLogFlag(), ConfManager.getFileLogPath());

//		this.log(pact);

		// 遍历用户map
		for (Map.Entry<String, People> entry : roomUsers.entrySet()) {
			// 如果当前遍历出来的用户是发消息的用户，则不发送并继续遍历
			if (entry.getKey().equals(fromUserId) || "0".equals(entry.getKey())) {
				continue;
			}

//			System.out.println(entry.getKey() + " " + pact.getPacketType());

			// 获取用户通道
			ChannelHandlerContext ctx = ServerDataPool.USER_CHANNEL_MAP.get(entry.getKey());
			// 如果通道不为空
			if (null == ctx) {
				continue;
			}

			// 向通道写数据并发送
			writeAndFlush(ctx, pact);
		}
	}

	public synchronized void log(Packet packet) {
		Boolean consoleFlag = ConfManager.getConsoleFlag();
		Boolean fileFlag = ConfManager.getFileLogFlag();
		String fileLogPath = ConfManager.getFileLogPath();

		packet.printPacket(consoleFlag, Constant.CONSOLE_CODE_R, fileFlag, fileLogPath);

		// 缓存消息日志
		if (packet.getPacketType() != PacketType.ServerHearBeat && packet.getPacketType() != PacketType.ServerLogin){
			ServerDataPool.log.put(System.currentTimeMillis()+ConfManager.getLogKeySplit()+packet.getTransactionId(), packet);
		}
	}
}
