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
 * QoS4ReciveDaemon.java at 2017-5-1 21:06:41, code by Jack Jiang.
 * You can contact author with jack.jiang@52im.net or jb2011@163.com.
 */
package net.openmob.mobileimsdk.android.core;

import java.util.concurrent.ConcurrentHashMap;

import net.openmob.mobileimsdk.android.ClientCoreSDK;
import net.openmob.mobileimsdk.server.protocal.Protocal;
import android.content.Context;
import android.os.Handler;
import android.util.Log;

public class QoS4ReciveDaemon
{
	private final static String TAG = QoS4ReciveDaemon.class.getSimpleName();
	
	private static QoS4ReciveDaemon instance = null;
	
	public final static int CHECH_INTERVAL = 5 * 60 * 1000;      // 5分钟
	public final static int MESSAGES_VALID_TIME = 10 * 60 * 1000;// 10分钟
	
	private ConcurrentHashMap<String, Long> recievedMessages = new ConcurrentHashMap<String, Long>();
	private Handler handler = null;
	private Runnable runnable = null;
	private boolean running = false;
	private boolean _excuting = false;
	private Context context = null;
	
	public static QoS4ReciveDaemon getInstance(Context context)
	{
		if(instance == null)
			instance = new QoS4ReciveDaemon(context);
		
		return instance;
	}
	
	public QoS4ReciveDaemon(Context context)
	{
		this.context = context;
		init();
	}
	
	private void init()
	{
		handler = new Handler();
		runnable = new Runnable()
		{
			@Override
			public void run()
			{
				if(!_excuting)
				{
					_excuting = true;
					
					if(ClientCoreSDK.DEBUG)
						Log.d(TAG, "【IMCORE】【QoS接收方】++++++++++ START 暂存处理线程正在运行中，当前长度"+recievedMessages.size()+".");
					
					for(String key : recievedMessages.keySet())
					{
						long delta = System.currentTimeMillis() - recievedMessages.get(key);
						if(delta >= MESSAGES_VALID_TIME)
						{
							if(ClientCoreSDK.DEBUG)
								Log.d(TAG, "【IMCORE】【QoS接收方】指纹为"+key+"的包已生存"+delta
									+"ms(最大允许"+MESSAGES_VALID_TIME+"ms), 马上将删除之.");
							recievedMessages.remove(key);
						}
					}
				}

				if(ClientCoreSDK.DEBUG)
					Log.d(TAG, "【IMCORE】【QoS接收方】++++++++++ END 暂存处理线程正在运行中，当前长度"+recievedMessages.size()+".");
				
				_excuting = false;
				handler.postDelayed(runnable, CHECH_INTERVAL);
			}
		};
	}
	
	public void startup(boolean immediately)
	{
		stop();
		
		if(recievedMessages != null && recievedMessages.size() > 0)
		{
			for(String key : recievedMessages.keySet())
			{
				putImpl(key);
			}
		}
		
		handler.postDelayed(runnable, immediately ? 0 : CHECH_INTERVAL);
		running = true;
	}
	
	public void stop()
	{
		handler.removeCallbacks(runnable);
		running = false;
	}
	
	public boolean isRunning()
	{
		return running;
	}
	
	public void addRecieved(Protocal p)
	{
		if(p != null && p.isQoS())
			addRecieved(p.getFp());
	}
	public void addRecieved(String fingerPrintOfProtocal)
	{
		if(fingerPrintOfProtocal == null)
		{
			Log.w(TAG, "【IMCORE】无效的 fingerPrintOfProtocal==null!");
			return;
		}
		
		if(recievedMessages.containsKey(fingerPrintOfProtocal))
			Log.w(TAG, "【IMCORE】【QoS接收方】指纹为"+fingerPrintOfProtocal
					+"的消息已经存在于接收列表中，该消息重复了（原理可能是对方因未收到应答包而错误重传导致），更新收到时间戳哦.");
		
		putImpl(fingerPrintOfProtocal);
	}
	
	private void putImpl(String fingerPrintOfProtocal)
	{
		if(fingerPrintOfProtocal != null)
			recievedMessages.put(fingerPrintOfProtocal, System.currentTimeMillis());
	}
	
	public boolean hasRecieved(String fingerPrintOfProtocal)
	{
		return recievedMessages.containsKey(fingerPrintOfProtocal);
	}
	
	public int size()
	{
		return recievedMessages.size();
	}
}
