/**
 *
 * 项目名称：NettyServerCenter
 * 类名称：ServerThreadBO
 * 类描述：
 * 创建人：Y.P
 * 创建时间：2020年4月27日 下午3:21:30
 * 修改人：Y.P
 * 修改时间：2020年4月27日 下午3:21:30
 * @version  1.0
 *
 */
package com.sa.base.element;

public class ServerThreadBO {
	public Thread thread;
	public int reconnectTimes = 0;
	
	public ServerThreadBO(){}
	
	public ServerThreadBO(Thread thread) {
		this.thread = thread;
		this.reconnectTimes = 0;
	}
}
