package com.sa.service.manager;

import com.sa.base.ServerDataPool;
import com.sa.base.ServerManager;
import com.sa.base.element.ChannelExtend;
import com.sa.service.client.ClientLogin;
import com.sa.service.server.ServerLogin;
import com.sa.util.Constant;
import com.sa.util.StringUtil;

import io.netty.channel.ChannelHandlerContext;

public enum LoginManager {

	INSTANCE;

	//中心此登錄只有备中心使用
	public void login(ChannelHandlerContext context, ServerLogin loginPact) {
		
		String strIp = context.channel().remoteAddress().toString();
		strIp = StringUtil.subStringIp(strIp);

		int code = 0;
		String msg = "成功";
		/** 获取 临时通道 状态*/
		ChannelExtend ce = ServerDataPool.TEMP_CONN_MAP.get(context);
		/** 如果为空 */
		if (null == ce || null == ce.getConnBeginTime()) {
			return;
		}

		/** 将 发信人id 和 通道信息 放入 临时通道缓存 */
		ServerDataPool.TEMP_CONN_MAP2.put(loginPact.getFromUserId(), context);

		/** 将用户信息注册 */
		ServerManager.INSTANCE.addOnlineContext(loginPact.getRoomId(), loginPact.getFromUserId(),context, ce.getChannelType());
		/** 登录信息 下行 处理 */
		
		clientLogin(loginPact, code, msg, "", context);

		/** 删除 缓存通道 */
		ServerDataPool.TEMP_CONN_MAP2.remove(loginPact.getFromUserId());
		ServerDataPool.TEMP_CONN_MAP.remove(context);
	}


	private void clientLogin(ServerLogin loginPact, int code, String msg, String role, ChannelHandlerContext context) {
		ClientLogin cl = new ClientLogin(loginPact.getPacketHead());

		cl.setToUserId(loginPact.getFromUserId());
		cl.setStatus(code);
		cl.setOption(1, loginPact.getOption(1));
		cl.setOption(2, role);
		cl.setOption(254, msg);

		try {
			ServerManager.INSTANCE.sendPacketTo(cl, context, Constant.CONSOLE_CODE_S);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
