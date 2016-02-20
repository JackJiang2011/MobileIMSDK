/*
 * Copyright (C) 2016 即时通讯网(52im.net) The MobileIMSDK Project. 
 * All rights reserved.
 * Project URL:https://github.com/JackJiang2011/MobileIMSDK
 *  
 * 即时通讯网(52im.net) - 即时通讯技术社区! PROPRIETARY/CONFIDENTIAL.
 * Use is subject to license terms.
 * 
 * QoS4ReciveDaemon.java at 2016-2-20 11:25:50, code by Jack Jiang.
 * You can contact author with jack.jiang@52im.net or jb2011@163.com.
 */
package net.openmob.mobileimsdk.android.core;

import android.content.Context;
import android.os.Handler;
import android.util.Log;
import java.util.concurrent.ConcurrentHashMap;
import net.openmob.mobileimsdk.android.ClientCoreSDK;
import net.openmob.mobileimsdk.server.protocal.Protocal;

public class QoS4ReciveDaemon
{
	private static final String TAG = QoS4ReciveDaemon.class.getSimpleName();
	public static final int CHECH_INTERVAL = 300000;
	public static final int MESSAGES_VALID_TIME = 600000;
	private ConcurrentHashMap<String, Long> recievedMessages = new ConcurrentHashMap();

	private Handler handler = null;
	private Runnable runnable = null;

	private boolean running = false;

	private boolean _excuting = false;

	private Context context = null;

	private static QoS4ReciveDaemon instance = null;

	public static QoS4ReciveDaemon getInstance(Context context)
	{
		if (instance == null) {
			instance = new QoS4ReciveDaemon(context);
		}

		return instance;
	}

	public QoS4ReciveDaemon(Context context)
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
				if (!QoS4ReciveDaemon.this._excuting)
				{
					QoS4ReciveDaemon.this._excuting = true;

					if (ClientCoreSDK.DEBUG) {
						Log.d(QoS4ReciveDaemon.TAG, "【IMCORE】【QoS接收方】++++++++++ START 暂存处理线程正在运行中，当前长度" + QoS4ReciveDaemon.this.recievedMessages.size() + ".");
					}

					for (String key : QoS4ReciveDaemon.this.recievedMessages.keySet())
					{
						long delta = System.currentTimeMillis() - ((Long)QoS4ReciveDaemon.this.recievedMessages.get(key)).longValue();

						if (delta < MESSAGES_VALID_TIME)
							continue;
						if (ClientCoreSDK.DEBUG)
							Log.d(QoS4ReciveDaemon.TAG, "【IMCORE】【QoS接收方】指纹为" + key + "的包已生存" + delta + 
									"ms(最大允许" + MESSAGES_VALID_TIME + "ms), 马上将删除之.");
						QoS4ReciveDaemon.this.recievedMessages.remove(key);
					}

				}

				if (ClientCoreSDK.DEBUG) {
					Log.d(QoS4ReciveDaemon.TAG, "【IMCORE】【QoS接收方】++++++++++ END 暂存处理线程正在运行中，当前长度" + QoS4ReciveDaemon.this.recievedMessages.size() + ".");
				}

				QoS4ReciveDaemon.this._excuting = false;

				QoS4ReciveDaemon.this.handler.postDelayed(QoS4ReciveDaemon.this.runnable, CHECH_INTERVAL);
			}
		};
	}

	public void startup(boolean immediately)
	{
		stop();

		if ((this.recievedMessages != null) && (this.recievedMessages.size() > 0))
		{
			for (String key : this.recievedMessages.keySet())
			{
				putImpl(key);
			}

		}

		this.handler.postDelayed(this.runnable, immediately ? 0 : CHECH_INTERVAL);

		this.running = true;
	}

	public void stop()
	{
		this.handler.removeCallbacks(this.runnable);

		this.running = false;
	}

	public boolean isRunning()
	{
		return this.running;
	}

	public void addRecieved(Protocal p)
	{
		if ((p != null) && (p.isQoS()))
			addRecieved(p.getFp());
	}

	public void addRecieved(String fingerPrintOfProtocal)
	{
		if (fingerPrintOfProtocal == null)
		{
			Log.w(TAG, "【IMCORE】无效的 fingerPrintOfProtocal==null!");
			return;
		}

		if (this.recievedMessages.containsKey(fingerPrintOfProtocal)) {
			Log.w(TAG, "【IMCORE】【QoS接收方】指纹为" + fingerPrintOfProtocal + 
					"的消息已经存在于接收列表中，该消息重复了（原理可能是对方因未收到应答包而错误重传导致），更新收到时间戳哦.");
		}

		putImpl(fingerPrintOfProtocal);
	}

	private void putImpl(String fingerPrintOfProtocal)
	{
		if (fingerPrintOfProtocal != null)
			this.recievedMessages.put(fingerPrintOfProtocal, Long.valueOf(System.currentTimeMillis()));
	}

	public boolean hasRecieved(String fingerPrintOfProtocal)
	{
		return this.recievedMessages.containsKey(fingerPrintOfProtocal);
	}

	public int size()
	{
		return this.recievedMessages.size();
	}
}