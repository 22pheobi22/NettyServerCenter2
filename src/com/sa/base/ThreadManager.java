/**
 *
 * 项目名称：NettyServerCenter
 * 类名称：ThreadManager
 * 类描述：
 * 创建人：Y.P
 * 创建时间：2020年4月27日 下午3:25:11
 * 修改人：Y.P
 * 修改时间：2020年4月27日 下午3:25:11
 * @version  1.0
 *
 */
package com.sa.base;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.sa.base.element.ServerThreadBO;

public class ThreadManager {
	private Map<String, ServerThreadBO> NAME_THREAD_MAP = new ConcurrentHashMap<>();

	public void setNum(String key, int num) {
		ServerThreadBO serverThreadBO = this.get(key);

		if (null != serverThreadBO) {
			serverThreadBO.reconnectTimes = num;
		} else {
			System.out.println("ThreadManager.setNum key=" + key + " 的线程不存在");
		}
	}

	public void set(String key, Thread t) {
		ServerThreadBO serverThreadBO = new ServerThreadBO(t);

		this.NAME_THREAD_MAP.put(key, serverThreadBO);
	}
	
	public ServerThreadBO get(String key) {
		return this.NAME_THREAD_MAP.get(key);
	}

	public void close(String key) {
		ServerThreadBO serverThreadBO = this.get(key);
		
		if (null != serverThreadBO && null != serverThreadBO.thread) {
			try {
				serverThreadBO.thread.interrupt();
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				this.NAME_THREAD_MAP.remove(key);
			}
		}
	}

	public int getCountNum() {
		return this.NAME_THREAD_MAP.size();
	}
	
	public int getCountNum(String key) {
		int num = 0;

		for(String k : this.NAME_THREAD_MAP.keySet()) {
			if (k.indexOf(key) != -1) {
				num++;
			}
		}
		return num;
	}

	public boolean check(String key, int countNum) {
		int num = 0;
		for (Map.Entry<String, ServerThreadBO> entry : this.NAME_THREAD_MAP.entrySet()) {
			if (entry.getKey().indexOf(key) != -1) {
				if (entry.getValue().reconnectTimes < ConfManager.getMaxReconnectTimes()) {
					num ++;
				}
			}
		}

		if (0 == num) {
			return false;
		}
		return true;
	}
}
