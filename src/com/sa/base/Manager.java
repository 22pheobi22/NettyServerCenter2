package com.sa.base;

import java.util.HashSet;
import java.util.Map;

import com.sa.base.element.ChannelExtend;
import com.sa.net.Packet;
import com.sa.net.PacketType;
import com.sa.net.codec.PacketBinEncoder;
import com.sa.util.Constant;

import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.websocketx.BinaryWebSocketFrame;

public enum Manager {

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
		if(ConfManager.getIsRedis()){
			RedisManager.INSTANCE.sendPacketTo(pact, consoleHead);
		}else{
			ServerManager.INSTANCE.sendPacketTo(pact, consoleHead);
		}
	}
	/** 向中心发送数据包*/
	public void sendPacketToCenter(Packet pact, String consoleHead) {
		/*if(ConfManager.getIsRedis()){
			RedisManager.INSTANCE.sendPacketToCenter(pact, consoleHead);
		}else{*/
			ServerManager.INSTANCE.sendPacketToCenter(pact, consoleHead);
		//}
	}

	/**
	 *  向所有在线用户发送数据包
	 * @throws Exception
	 */
	public void sendPacketToRoomAllUsers(Packet pact, String consoleHead) throws Exception{
		if(ConfManager.getIsRedis()){
			RedisManager.INSTANCE.sendPacketToRoomAllUsers(pact, consoleHead);
		}else{
			ServerManager.INSTANCE.sendPacketToRoomAllUsers(pact, consoleHead);
		}
	}

	/**
	 *  向所有在线用户发送数据包
	 * @throws Exception
	 */
	public void sendPacketToAllUsers(Packet pact, String consoleHead) throws Exception{
		/*if(ConfManager.getIsRedis()){
			RedisManager.INSTANCE.sendPacketToAllUsers(pact, consoleHead);
		}else{*/
			ServerManager.INSTANCE.sendPacketToAllUsers(pact, consoleHead);
		//}
	}

	/**
	 *  向单一在线用户发送数据包
	 * @throws Exception
	 */
	public void sendPacketTo(Packet pact,ChannelHandlerContext targetContext, String consoleHead) throws Exception {
		// 如果数据包为空 或者 接收用户的通道为空 则返回
		if(pact == null || targetContext == null) return;

		// 在控制台打印消息头
		pact.printPacket(ConfManager.getConsoleFlag(), consoleHead, ConfManager.getFileLogFlag(), ConfManager.getFileLogPath());
		// 缓存消息日志
//		this.log(pact);

		// 将数据包写进通道并发送
		writeAndFlush(targetContext, pact);
	}

	/** 根据用户id获取在线通道*/
	public ChannelHandlerContext getOnlineContextBy(String userId){
		return ServerDataPool.USER_CHANNEL_MAP.get(userId);
	}

	/** 获取所有在线的用户通道*/
	public Map<String, ChannelHandlerContext> getAllOnlineContext(){
		return ServerDataPool.USER_CHANNEL_MAP;
	}
	
	/**
	 * 登录、注册、上线、绑定--非中心
	 */
	public void addOnlineContext(String roomId, String userId, String name, String icon, String agoraId, HashSet<String> userRole, boolean notSpeak, ChannelHandlerContext context, int channelType){
		if(ConfManager.getIsRedis()){
			RedisManager.INSTANCE.addOnlineContext(roomId, userId, name, icon, agoraId,userRole, notSpeak, context, channelType);
		}else{
			ServerManager.INSTANCE.addOnlineContext(roomId, userId, name, icon, agoraId,userRole, notSpeak, context, channelType);
		}
	}
	
	/**
	 * 登录、注册、上线、绑定--中心
	 */
	public void addOnlineContext(String userId, ChannelHandlerContext context, int channelType){
		ServerManager.INSTANCE.addOnlineContext(userId, context, channelType);
	}
	
	public void ungisterUserInfo(String userId) {
		RedisManager.INSTANCE.ungisterUserInfo(userId);
	}

	/**
	 *   注销用户通信渠道
	 */
	public void ungisterUserId(String userId) {
		if(ConfManager.getIsRedis()){
			RedisManager.INSTANCE.ungisterUserId(userId);
		}else{
			ServerManager.INSTANCE.ungisterUserId(userId);
		}
	}

	/**
	 *   注销用户通信渠道
	 */
	public void ungisterUserContext(ChannelHandlerContext context) {
		if(ConfManager.getIsRedis()){
			RedisManager.INSTANCE.ungisterUserContext(context);
		}else{
			ServerManager.INSTANCE.ungisterUserContext(context);
		}
	}

	/** 想全体用户发送消息*/
	public void sendPacketToAllUsers(Packet pact, String consoleHead, String fromUserId) {
		/*if(ConfManager.getIsRedis()){
			RedisManager.INSTANCE.sendPacketToAllUsers(pact,consoleHead,fromUserId);
		}else{*/
			ServerManager.INSTANCE.sendPacketToAllUsers(pact,consoleHead,fromUserId);
		//}
	}

	/**
	 *  向房间内除发信人外所有用户所在服务发送数据包
	 * @throws Exception
	 */
	public void sendPacketToRoomAllUsers(Packet pact, String consoleHead, String fromUserId) throws Exception{
		if(ConfManager.getIsRedis()){
			RedisManager.INSTANCE.sendPacketToRoomAllUsers(pact, consoleHead, fromUserId);
		}else{
			ServerManager.INSTANCE.sendPacketToRoomAllUsers(pact, consoleHead, fromUserId);
		}
	}
	
	/**發送消息到除中心及推送消息的服務器外的其他服務器*/
	public void sendPacketToServerExpectSourse(Packet pact, String consoleHead, String fromUserId) throws Exception{
		RedisManager.INSTANCE.sendPacketToServerExpectSourse(pact, consoleHead, fromUserId);
	}
	/**發送房間内消息--發送消息到処中心，推送消息的服務外的其他房間内學院所在服務*/
	public void sendPacketToRoomServerExpectSourse(Packet pact, String consoleHead, String fromUserId) throws Exception{
		RedisManager.INSTANCE.sendPacketToRoomServerExpectSourse(pact, consoleHead, fromUserId);
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
