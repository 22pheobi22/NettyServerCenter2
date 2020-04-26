package com.sa.service.server;

import java.util.HashMap;
import java.util.Map;

import com.sa.base.ConfManager;
import com.sa.base.ServerDataPool;
import com.sa.client.ChatClient;
import com.sa.net.Packet;
import com.sa.net.PacketType;
import com.sa.util.JedisUtil;

import io.netty.channel.ChannelHandlerContext;

public class ServerHeartBeat extends Packet{

	public ServerHeartBeat() {
		this.setTransactionId(0);
		this.setRoomId("0");
		this.setFromUserId("0");
		this.setToUserId("0");
		this.setOption(1, "checkServer");
		this.setStatus(0);
	}

	@Override
	public PacketType getPacketType() {
		return PacketType.ServerHearBeat;
	}

	@Override
	public void execPacket() {
		System.err.println("收到客户端心跳事件！");
		//中心监测到已与服务无连接  通知备机进行主备切换
		//关闭现有中心链接
		ChannelHandlerContext context = ServerDataPool.USER_CHANNEL_MAP.get(ConfManager.getCenterIpAnother());
		context.close();

		//1.去redis将自己改为主
		Map<String,String> centerRoleMap = new HashMap<>();
		centerRoleMap.put("master", ConfManager.getCenterIp()+":"+ConfManager.getClientSoketServerPort());
		centerRoleMap.put("slave", ConfManager.getCenterIpAnother()+":"+ConfManager.getCenterPortAnother());
		JedisUtil jedisUtil = new JedisUtil();
		jedisUtil.setHashMulti("centerRoleInfo", centerRoleMap);
		boolean isMaster = true;
		//2.主动连接服务
        String[] address = ConfManager.getServerAddress();
        for (int i = 0; i < address.length; i++) {
        	String[] addr = address[i].split(":");
        	Thread centerToServer = new Thread(new ChatClient(addr[0], Integer.valueOf(addr[1]),isMaster));
        	centerToServer.setName("centerToServer"+addr[0]);
        	centerToServer.start();
        	//3.存储主与服务连接线程信息
        	ServerDataPool.NAME_THREAD_MAP.put("centerToServer"+addr[0], centerToServer);
       }
        //4.关闭并删除原备主连接线程
        Thread centerToCenterThread = ServerDataPool.NAME_THREAD_MAP.get("centerToCenter");
        if(null!=centerToCenterThread){
        	centerToCenterThread.interrupt();
        	ServerDataPool.NAME_THREAD_MAP.remove("centerToCenter");
        }
	}

	@Override
	public String toString() {
		return null;
	}

}
