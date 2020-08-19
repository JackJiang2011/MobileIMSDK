/*
 * Copyright (C) 2020  即时通讯网(52im.net) & Jack Jiang.
 * The MobileIMSDK_UDP (MobileIMSDK v5.x UDP版) Project. 
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
 * KeepAliveDaemon.java at 2020-8-18 15:45:30, code by Jack Jiang.
 */
package net.openmob.mobileimsdk.android.core;

import java.util.Observer;

import net.openmob.mobileimsdk.android.ClientCoreSDK;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Handler;
import android.util.Log;

public class KeepAliveDaemon
{
	private final static String TAG = KeepAliveDaemon.class.getSimpleName();
	
	private static KeepAliveDaemon instance = null;
	
	public static int NETWORK_CONNECTION_TIME_OUT = 10 * 1000;
	public static int KEEP_ALIVE_INTERVAL = 3000;//1000;

	private boolean keepAliveRunning = false;
	private long lastGetKeepAliveResponseFromServerTimstamp = 0;
	private Observer networkConnectionLostObserver = null;

    private Handler handler = null;
    private Runnable runnable = null;
	private boolean _excuting = false;
    private boolean init = false;

	private Context context = null;
	
	public static KeepAliveDaemon getInstance(Context context)
	{
		if(instance == null)
			instance = new KeepAliveDaemon(context);
		return instance;
	}
	
	private KeepAliveDaemon(Context context)
	{
		this.context = context;
		init();
	}
	
	private void init()
	{
        if(init)
            return;

		handler = new Handler();
		runnable = new Runnable(){
			@Override
			public void run()
			{
				if(!_excuting)
				{
					new AsyncTask<Object, Integer, Integer>()
					{
						private boolean willStop = false;
						
						@Override
						protected Integer doInBackground(Object... params)
						{
							_excuting = true;
							if(ClientCoreSDK.DEBUG)
								Log.d(TAG, "【IMCORE】心跳线程执行中...");
							int code = LocalUDPDataSender.getInstance(context).sendKeepAlive();
							
							return code;
						}

						@Override
						protected void onPostExecute(Integer code)
						{
							boolean isInitialedForKeepAlive = (lastGetKeepAliveResponseFromServerTimstamp == 0);
							if(isInitialedForKeepAlive)
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
								handler.postDelayed(runnable, KEEP_ALIVE_INTERVAL);
						}
					}.execute();
				}
			}
		};
	}
	
	public void stop()
	{
		handler.removeCallbacks(runnable);
		keepAliveRunning = false;
		lastGetKeepAliveResponseFromServerTimstamp = 0;
	}
	
	public void start(boolean immediately)
	{
		stop();
		
		handler.postDelayed(runnable, immediately ? 0 : KEEP_ALIVE_INTERVAL);
		keepAliveRunning = true;
	}
	
	public boolean isKeepAliveRunning()
	{
		return keepAliveRunning;
	}

    public boolean isInit()
    {
        return init;
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
