package com.sa.client;

import java.util.Set;

import com.sa.base.ConfManager;
import com.sa.base.ServerDataPool;

public class ClientStart {
	public static void main(String[] args) {
		//连接服务
		Set<String> ips = ServerDataPool.USER_CHANNEL_MAP.keySet();
		String[] address = ConfManager.getServerAddress();
		for (int i = 0; i < address.length; i++) {
			String[] addr = address[i].split(":");
			if(!ips.contains(addr[1])){
				new Thread(new ChatClient(addr[0], Integer.valueOf(addr[1]),true)).start();
			}
		}
	}
}
