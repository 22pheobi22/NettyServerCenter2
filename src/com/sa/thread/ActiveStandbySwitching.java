/**
 *
 * 项目名称：NettyServerCenter
 * 类名称：ActiveStandbySwitching
 * 类描述：
 * 创建人：Y.P
 * 创建时间：2020年4月28日 下午12:27:22
 * 修改人：Y.P
 * 修改时间：2020年4月28日 下午12:27:22
 * @version  1.0
 *
 */
package com.sa.thread;

import com.sa.base.CenterManager;
import com.sa.base.ConfManager;

public class ActiveStandbySwitching implements Runnable {

	@Override
	public void run() {
		long sleep = ConfManager.getCenterMasterOvertime();
		while(true) {
			try {
				Thread.sleep(sleep);
			} catch (Exception e) {
				e.printStackTrace();
			}

			try {
				new CenterManager().activeStandbySwitching();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
}
