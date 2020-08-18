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
 * AutoReLoginDaemon.java at 2017-5-1 21:06:42, code by Jack Jiang.
 * You can contact author with jack.jiang@52im.net or jb2011@163.com.
 */
package net.openmob.mobileimsdk.android.core;

import net.openmob.mobileimsdk.android.ClientCoreSDK;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Handler;
import android.util.Log;

public class AutoReLoginDaemon
{
	private final static String TAG = AutoReLoginDaemon.class.getSimpleName();
	
	private static AutoReLoginDaemon instance = null;
	
	public static int AUTO_RE$LOGIN_INTERVAL = 2000;

	private Handler handler = null;
	private Runnable runnable = null;
	private boolean autoReLoginRunning = false;
	private boolean _excuting = false;
	private Context context = null;
	
	public static AutoReLoginDaemon getInstance(Context context)
	{
		if(instance == null)
			instance = new AutoReLoginDaemon(context);
		return instance;
	}
	
	private AutoReLoginDaemon(Context context)
	{
		this.context = context;
		init();
	}
	
	private void init()
	{
		handler = new Handler();
		runnable = new Runnable(){
			@Override
			public void run()
			{
				if(!_excuting)
				{
					new AsyncTask<Object, Integer, Integer>(){
						@Override
						protected Integer doInBackground(Object... params)
						{
							_excuting = true;
							if(ClientCoreSDK.DEBUG)
								Log.d(TAG, "【IMCORE】自动重新登陆线程执行中, autoReLogin?"+ClientCoreSDK.autoReLogin+"...");
							int code = -1;
							
							if(ClientCoreSDK.autoReLogin)
							{
								code = LocalUDPDataSender.getInstance(context).sendLogin(
										ClientCoreSDK.getInstance().getCurrentLoginUserId()
										, ClientCoreSDK.getInstance().getCurrentLoginToken()
										, ClientCoreSDK.getInstance().getCurrentLoginExtra());
							}
							return code;
						}

						@Override
						protected void onPostExecute(Integer result)
						{
							if(result == 0)
							{
								LocalUDPDataReciever.getInstance(context).startup();
							}

							_excuting = false;
							handler.postDelayed(runnable, AUTO_RE$LOGIN_INTERVAL);
						}
					}.execute();
				}
			}
		};
	}
	
	public void stop()
	{
		handler.removeCallbacks(runnable);
		autoReLoginRunning = false;
	}
	
	public void start(boolean immediately)
	{
		stop();
		
		handler.postDelayed(runnable, immediately ? 0 : AUTO_RE$LOGIN_INTERVAL);
		autoReLoginRunning = true;
	}
	
	public boolean isAutoReLoginRunning()
	{
		return autoReLoginRunning;
	}
}
