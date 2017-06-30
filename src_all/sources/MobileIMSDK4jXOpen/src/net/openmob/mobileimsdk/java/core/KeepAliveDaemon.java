/*
 * Copyright (C) 2017  即时通讯网(52im.net) & Jack Jiang.
 * The MobileIMSDK_X (MobileIMSDK v3.x) Project. 
 * All rights reserved.
 * 
 * > Github地址: https://github.com/JackJiang2011/MobileIMSDK
 * > 文档地址: http://www.52im.net/forum-89-1.html
 * > 即时通讯技术社区：http://www.52im.net/
 * > 即时通讯技术交流群：320837163 (http://www.52im.net/topic-qqgroup.html)
 *  
 * "即时通讯网(52im.net) - 即时通讯开发者社区!" 推荐开源工程。
 * 
 * KeepAliveDaemon.java at 2017-5-1 22:14:56, code by Jack Jiang.
 * You can contact author with jack.jiang@52im.net or jb2011@163.com.
 */
package net.openmob.mobileimsdk.java.core;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Observer;

import javax.swing.Timer;

import net.openmob.mobileimsdk.java.ClientCoreSDK;
import net.openmob.mobileimsdk.java.utils.Log;

public class KeepAliveDaemon
{
	private final static String TAG = KeepAliveDaemon.class.getSimpleName();
	
	private static KeepAliveDaemon instance = null;
	
	public static int NETWORK_CONNECTION_TIME_OUT = 10 * 1000;
	public static int KEEP_ALIVE_INTERVAL = 3000;//1000;
	
	private boolean keepAliveRunning = false;
	private long lastGetKeepAliveResponseFromServerTimstamp = 0;
	private Observer networkConnectionLostObserver = null;
	private boolean _excuting = false;
	private Timer timer = null;
	
	public static KeepAliveDaemon getInstance()
	{
		if(instance == null)
			instance = new KeepAliveDaemon();
		return instance;
	}
	
	private KeepAliveDaemon()
	{
		init();
	}
	
	private void init()
	{
		timer = new Timer(KEEP_ALIVE_INTERVAL, new ActionListener(){
			public void actionPerformed(ActionEvent e)
			{
				run();
			}
		});
	}
	
	public void run()
	{
		if(!_excuting)
		{
			boolean willStop = false;
			_excuting = true;
			if(ClientCoreSDK.DEBUG)
				Log.i(TAG, "【IMCORE】心跳线程执行中...");
			int code = LocalUDPDataSender.getInstance().sendKeepAlive();

			boolean isInitialedForKeepAlive = (lastGetKeepAliveResponseFromServerTimstamp == 0);
			if(code == 0 && lastGetKeepAliveResponseFromServerTimstamp == 0)
				lastGetKeepAliveResponseFromServerTimstamp = System.currentTimeMillis();

			if(!isInitialedForKeepAlive)
			{
				long now = System.currentTimeMillis();
				if(now - lastGetKeepAliveResponseFromServerTimstamp >= NETWORK_CONNECTION_TIME_OUT)
				{
					stop();
					if(networkConnectionLostObserver != null)
						networkConnectionLostObserver.update(null, null);
					willStop = true;
				}
			}

			_excuting = false;
			if(!willStop)
			{
				; // do nothing
			}
			else
			{
				timer.stop();
			}
		}
	}
	
	public void stop()
	{
		if(timer != null)
			timer.stop();
		keepAliveRunning = false;
		lastGetKeepAliveResponseFromServerTimstamp = 0;
	}
	
	public void start(boolean immediately)
	{
		stop();
		
		if(immediately)
			timer.setInitialDelay(0);
		else
			timer.setInitialDelay(KEEP_ALIVE_INTERVAL);
		timer.start();
		
		keepAliveRunning = true;
	}
	
	public boolean isKeepAliveRunning()
	{
		return keepAliveRunning;
	}
	
	public void updateGetKeepAliveResponseFromServerTimstamp()
	{
		lastGetKeepAliveResponseFromServerTimstamp = System.currentTimeMillis();
	}

	public void setNetworkConnectionLostObserver(Observer networkConnectionLostObserver)
	{
		this.networkConnectionLostObserver = networkConnectionLostObserver;
	}
}
