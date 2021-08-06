/*
 * Copyright (C) 2021  即时通讯网(52im.net) & Jack Jiang.
 * The MobileIMSDK4j_udp (MobileIMSDK4j v6.1 UDP版) Project. 
 * All rights reserved.
 * 
 * > Github地址：https://github.com/JackJiang2011/MobileIMSDK
 * > 文档地址：  http://www.52im.net/forum-89-1.html
 * > 技术社区：  http://www.52im.net/
 * > 技术交流群：320837163 (http://www.52im.net/topic-qqgroup.html)
 * > 作者公众号：“即时通讯技术圈】”，欢迎关注！
 * > 联系作者：  http://www.52im.net/thread-2792-1-1.html
 *  
 * "即时通讯网(52im.net) - 即时通讯开发者社区!" 推荐开源工程。
 * 
 * AutoReLoginDaemon.java at 2021-8-4 21:37:03, code by Jack Jiang.
 */
package net.x52im.mobileimsdk.java.core;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.Timer;

import net.x52im.mobileimsdk.java.ClientCoreSDK;
import net.x52im.mobileimsdk.java.utils.Log;

import org.jdesktop.swingworker.SwingWorker;

public class AutoReLoginDaemon {
	private final static String TAG = AutoReLoginDaemon.class.getSimpleName();

	private static AutoReLoginDaemon instance = null;

	public static int AUTO_RE$LOGIN_INTERVAL = 2000;

	private boolean autoReLoginRunning = false;
	private boolean _excuting = false;
	private Timer timer = null;

	public static AutoReLoginDaemon getInstance() {
		if (instance == null)
			instance = new AutoReLoginDaemon();
		return instance;
	}

	private AutoReLoginDaemon() {
		init();
	}

	private void init() {
		timer = new Timer(AUTO_RE$LOGIN_INTERVAL, new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				run();
			}
		});
	}

	public void run() {
		if (!_excuting) {
			_excuting = true;
			if (ClientCoreSDK.DEBUG)
				Log.p(TAG, "【IMCORE-UDP】自动重新登陆线程执行中, autoReLogin?"+ ClientCoreSDK.autoReLogin + "...");
			int code = -1;
			if (ClientCoreSDK.autoReLogin) {
				LocalSocketProvider.getInstance().closeLocalSocket();
				code = LocalDataSender.getInstance().sendLogin(ClientCoreSDK.getInstance().getCurrentLoginInfo());

			}

			if (code == 0) {
				LocalDataReciever.getInstance().startup();
			}

			_excuting = false;
		}
	}

	public void stop() {
		if (timer != null)
			timer.stop();
		autoReLoginRunning = false;
	}

	public void start(boolean immediately) {
		stop();

		if (immediately)
			timer.setInitialDelay(0);
		else
			timer.setInitialDelay(AUTO_RE$LOGIN_INTERVAL);
		timer.start();

		autoReLoginRunning = true;
	}

	public boolean isautoReLoginRunning() {
		return autoReLoginRunning;
	}
}
