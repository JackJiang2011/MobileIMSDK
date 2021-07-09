/*
 * Copyright (C) 2021  即时通讯网(52im.net) & Jack Jiang.
 * The MobileIMSDK_TCP (MobileIMSDK v6.x TCP版) Project. 
 * All rights reserved.
 * 
 * > Github地址：https://github.com/JackJiang2011/MobileIMSDK
 * > 文档地址：  http://www.52im.net/forum-89-1.html
 * > 技术社区：  http://www.52im.net/
 * > 技术交流群：215477170 (http://www.52im.net/topic-qqgroup.html)
 * > 作者公众号：“即时通讯技术圈】”，欢迎关注！
 * > 联系作者：  http://www.52im.net/thread-2792-1-1.html
 *  
 * "即时通讯网(52im.net) - 即时通讯开发者社区!" 推荐开源工程。
 * 
 * KeepAliveDaemon.java at 2021-7-6 15:43:17, code by Jack Jiang.
 */
package net.x52im.mobileimsdk.java.core;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Observer;
import java.util.concurrent.atomic.AtomicLong;

import javax.swing.Timer;

import net.x52im.mobileimsdk.java.ClientCoreSDK;
import net.x52im.mobileimsdk.java.utils.Log;

public class KeepAliveDaemon {
	
	private final static String TAG = KeepAliveDaemon.class.getSimpleName();
	
	private static KeepAliveDaemon instance = null;
	public static int KEEP_ALIVE_INTERVAL = 15000;
	public static int NETWORK_CONNECTION_TIME_OUT = KEEP_ALIVE_INTERVAL + 5000;

	private boolean keepAliveRunning = false;
	private AtomicLong lastGetKeepAliveResponseFromServerTimstamp = new AtomicLong(0);
	private Observer networkConnectionLostObserver = null;
	private boolean _excuting = false;
	private Timer timer = null;

	public static KeepAliveDaemon getInstance() {
		if (instance == null)
			instance = new KeepAliveDaemon();
		return instance;
	}

	private KeepAliveDaemon() {
		init();
	}

	private void init() {
		timer = new Timer(KEEP_ALIVE_INTERVAL, new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				run();
			}
		});
	}

	public void run() {
		if (!_excuting) {
			boolean willStop = false;
			_excuting = true;
			if (ClientCoreSDK.DEBUG)
				Log.i(TAG, "【IMCORE-TCP】心跳线程执行中...");
			int code = LocalDataSender.getInstance().sendKeepAlive();

			boolean isInitialedForKeepAlive = (lastGetKeepAliveResponseFromServerTimstamp.longValue() == 0);
			if (isInitialedForKeepAlive)
				lastGetKeepAliveResponseFromServerTimstamp.set(System.currentTimeMillis());

			if (!isInitialedForKeepAlive) {
				long now = System.currentTimeMillis();
				if (now - lastGetKeepAliveResponseFromServerTimstamp.longValue() >= NETWORK_CONNECTION_TIME_OUT) {
					notifyConnectionLost();
					willStop = true;
				}
			}

			_excuting = false;
			if (!willStop) {
				; // do nothing
			} else {
				timer.stop();
			}
		}
	}

	public void notifyConnectionLost() {
		stop();
		if (networkConnectionLostObserver != null)
			networkConnectionLostObserver.update(null, null);
	}

	public void stop() {
		if (timer != null)
			timer.stop();
		keepAliveRunning = false;
		lastGetKeepAliveResponseFromServerTimstamp.set(0);
	}

	public void start(boolean immediately) {
		stop();
		if (immediately)
			timer.setInitialDelay(0);
		else
			timer.setInitialDelay(KEEP_ALIVE_INTERVAL);
		timer.start();
		keepAliveRunning = true;
	}

	public boolean isKeepAliveRunning() {
		return keepAliveRunning;
	}

	public void updateGetKeepAliveResponseFromServerTimstamp() {
		lastGetKeepAliveResponseFromServerTimstamp.set(System.currentTimeMillis());
	}

	public void setNetworkConnectionLostObserver(Observer networkConnectionLostObserver) {
		this.networkConnectionLostObserver = networkConnectionLostObserver;
	}
}
