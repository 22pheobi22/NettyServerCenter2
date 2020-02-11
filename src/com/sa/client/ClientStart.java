package com.sa.client;

import com.sa.base.ConfManager;
import com.sa.util.ReadConf;

public class ClientStart {
	public static void main(String[] args) {
		// 连接服务
		new ReadConf().readFileByLines();
		String[] address = ConfManager.getAddAddress();
		for (int i = 0; i < address.length; i++) {
			String[] addr = address[i].split(":");
			new Thread(new ChatClient(addr[0], Integer.valueOf(addr[1]), true)).start();
		}
	}
}
