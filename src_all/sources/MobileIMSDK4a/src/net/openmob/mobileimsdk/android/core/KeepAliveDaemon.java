/*
 * Copyright (C) 2016 即时通讯网(52im.net) The MobileIMSDK Project. 
 * All rights reserved.
 * Project URL:https://github.com/JackJiang2011/MobileIMSDK
 *  
 * 即时通讯网(52im.net) - 即时通讯技术社区! PROPRIETARY/CONFIDENTIAL.
 * Use is subject to license terms.
 * 
 * KeepAliveDaemon.java at 2016-2-20 11:25:50, code by Jack Jiang.
 * You can contact author with jack.jiang@52im.net or jb2011@163.com.
 */
package net.openmob.mobileimsdk.android.core;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Handler;
import android.util.Log;
import java.util.Observer;
import net.openmob.mobileimsdk.android.ClientCoreSDK;

public class KeepAliveDaemon
{
	private static final String TAG = KeepAliveDaemon.class.getSimpleName();

	public static int NETWORK_CONNECTION_TIME_OUT = 10000;

	public static int KEEP_ALIVE_INTERVAL = 3000;

	private Handler handler = null;
	private Runnable runnable = null;
	private boolean keepAliveRunning = false;
	private long lastGetKeepAliveResponseFromServerTimstamp = 0L;

	private Observer networkConnectionLostObserver = null;
	private boolean _excuting = false;
	private Context context = null;

	private static KeepAliveDaemon instance = null;
	
	public static KeepAliveDaemon getInstance(Context context)
	{
		if (instance == null)
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
		this.handler = new Handler();
		this.runnable = new Runnable()
		{
			public void run()
			{
				// 极端情况下本次循环内可能执行时间超过了时间间隔，此处是防止在前一
				// 次还没有运行完的情况下又重复过劲行，从而出现无法预知的错误
				if (!KeepAliveDaemon.this._excuting)
				{
					new AsyncTask()
					{
						private boolean willStop = false;

						protected Integer doInBackground(Object[] params)
						{
							KeepAliveDaemon.this._excuting = true;
							if (ClientCoreSDK.DEBUG)
								Log.d(KeepAliveDaemon.TAG, "【IMCORE】心跳线程执行中...");
							int code = LocalUDPDataSender.getInstance(KeepAliveDaemon.this.context).sendKeepAlive();

							return Integer.valueOf(code);
						}

						protected void onPostExecute(Integer code)
						{
							boolean isInitialedForKeepAlive = 
									KeepAliveDaemon.this.lastGetKeepAliveResponseFromServerTimstamp == 0L;
							if ((code.intValue() == 0) 
									&& (KeepAliveDaemon.this.lastGetKeepAliveResponseFromServerTimstamp == 0L)) {
								KeepAliveDaemon.this.lastGetKeepAliveResponseFromServerTimstamp = System.currentTimeMillis();
							}

							if (!isInitialedForKeepAlive)
							{
								long now = System.currentTimeMillis();

								// 当当前时间与最近一次服务端的心跳响应包时间间隔>= 10秒就判定当前与服务端的网络连接已断开
								if (now - KeepAliveDaemon.this.lastGetKeepAliveResponseFromServerTimstamp 
										>= KeepAliveDaemon.NETWORK_CONNECTION_TIME_OUT)
								{
									KeepAliveDaemon.this.stop();

									if (KeepAliveDaemon.this.networkConnectionLostObserver != null) {
										KeepAliveDaemon.this.networkConnectionLostObserver.update(null, null);
									}
									this.willStop = true;
								}
							}

							KeepAliveDaemon.this._excuting = false;
							if (!this.willStop)
							{
								// 开始下一个心跳循环
								KeepAliveDaemon.this.handler.postDelayed(
										KeepAliveDaemon.this.runnable
										, KeepAliveDaemon.KEEP_ALIVE_INTERVAL);
							}
						}
					}
					.execute(new Object[0]);
				}
			}
		};
	}

	public void stop()
	{
		this.handler.removeCallbacks(this.runnable);
		this.keepAliveRunning = false;
		this.lastGetKeepAliveResponseFromServerTimstamp = 0L;
	}

	public void start(boolean immediately)
	{
		stop();

		this.handler.postDelayed(this.runnable, immediately ? 0 : KEEP_ALIVE_INTERVAL);
		this.keepAliveRunning = true;
	}

	public boolean isKeepAliveRunning()
	{
		return this.keepAliveRunning;
	}

	public void updateGetKeepAliveResponseFromServerTimstamp()
	{
		this.lastGetKeepAliveResponseFromServerTimstamp = System.currentTimeMillis();
	}

	public void setNetworkConnectionLostObserver(Observer networkConnectionLostObserver)
	{
		this.networkConnectionLostObserver = networkConnectionLostObserver;
	}
}