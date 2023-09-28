/*
 * Copyright (C) 2023  即时通讯网(52im.net) & Jack Jiang.
 * The MobileIMSDK_TCP (MobileIMSDK v6.4 TCP版) Project. 
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
 * KeepAliveDaemon.java at 2023-9-21 15:32:54, code by Jack Jiang.
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
	public static int NETWORK_CONNECTION_TIME_OUT_CHECK_INTERVAL = 2 * 1000;
	
	private boolean keepAliveRunning = false;
	private AtomicLong lastGetKeepAliveResponseFromServerTimstamp = new AtomicLong(0);
	private Observer networkConnectionLostObserver = null;
	
	private boolean keepAliveTaskExcuting = false;
	private boolean keepAliveWillStop = false;
	private Timer keepAliveTimer = null;
	
	private Timer keepAliveTimeoutTimer = null;

	public static KeepAliveDaemon getInstance() {
		if (instance == null) {
			synchronized (KeepAliveDaemon.class) {
				if (instance == null) {
					instance = new KeepAliveDaemon();
				}
			}
		}
		return instance;
	}

	private KeepAliveDaemon() {
		init();
	}

	private void init() {
		keepAliveTimer = new Timer(KEEP_ALIVE_INTERVAL, new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				doKeepAlive();
			}
		});
		
		keepAliveTimeoutTimer = new Timer(NETWORK_CONNECTION_TIME_OUT_CHECK_INTERVAL, new ActionListener(){
			public void actionPerformed(ActionEvent e){
				if(ClientCoreSDK.DEBUG)
					Log.i(TAG, "【IMCORE-TCP】心跳[超时检查]线程执行中...");

				doTimeoutCheck();
			}
		});
	}

	private void doKeepAlive() {
		if (!keepAliveTaskExcuting) {
//			boolean willStop = false;
			keepAliveTaskExcuting = true;
			if(ClientCoreSDK.DEBUG)
				Log.i(TAG, "【IMCORE-TCP】心跳包[发送]线程执行中...");
			int code = LocalDataSender.getInstance().sendKeepAlive();

			boolean isInitialedForKeepAlive = isInitialedForKeepAlive();
			if (isInitialedForKeepAlive)
				lastGetKeepAliveResponseFromServerTimstamp.set(System.currentTimeMillis());

			keepAliveTaskExcuting = false;
			if (!keepAliveWillStop) {
				; // do nothing
			} else {
				keepAliveTimer.stop();
			}
		}
	}

	private void doTimeoutCheck(){
		boolean isInitialedForKeepAlive = isInitialedForKeepAlive();
		if(!isInitialedForKeepAlive){
			long now = System.currentTimeMillis();

			// TODO: just for debug
//			if(ClientCoreSDK.DEBUG)
//				Log.i(TAG, ">>>> t1="+now+", t2="+lastGetKeepAliveResponseFromServerTimstamp+" -> 差："+(now - lastGetKeepAliveResponseFromServerTimstamp.longValue()));

			if(now - lastGetKeepAliveResponseFromServerTimstamp.longValue() >= NETWORK_CONNECTION_TIME_OUT)	{
				notifyConnectionLost();
				keepAliveWillStop = true;
			}
		}		
	}
	
	private boolean isInitialedForKeepAlive(){
		return (lastGetKeepAliveResponseFromServerTimstamp.longValue() == 0);
	}

	public void notifyConnectionLost() {
		stop();
		if (networkConnectionLostObserver != null)
			networkConnectionLostObserver.update(null, null);
	}

	public void stop() {
		if(keepAliveTimeoutTimer != null)
			keepAliveTimeoutTimer.stop();
		
		if (keepAliveTimer != null)
			keepAliveTimer.stop();
		
		keepAliveRunning = false;
		keepAliveWillStop = false;
		lastGetKeepAliveResponseFromServerTimstamp.set(0);
	}

	public void start(boolean immediately) {
		stop();
		
		if (immediately)
			keepAliveTimer.setInitialDelay(0);
		else
			keepAliveTimer.setInitialDelay(KEEP_ALIVE_INTERVAL);
		keepAliveTimer.start();
		
		if(immediately)
			keepAliveTimeoutTimer.setInitialDelay(0);
		else
			keepAliveTimeoutTimer.setInitialDelay(NETWORK_CONNECTION_TIME_OUT_CHECK_INTERVAL);
		keepAliveTimeoutTimer.start();
		
		keepAliveRunning = true;
		keepAliveWillStop = false;
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
