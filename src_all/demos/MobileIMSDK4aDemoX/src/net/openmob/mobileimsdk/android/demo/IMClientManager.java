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
 * IMClientManager.java at 2017-5-1 21:08:44, code by Jack Jiang.
 * You can contact author with jack.jiang@52im.net or jb2011@163.com.
 */
package net.openmob.mobileimsdk.android.demo;

import net.openmob.mobileimsdk.android.ClientCoreSDK;
import net.openmob.mobileimsdk.android.conf.ConfigEntity;
import net.openmob.mobileimsdk.android.demo.event.ChatBaseEventImpl;
import net.openmob.mobileimsdk.android.demo.event.ChatTransDataEventImpl;
import net.openmob.mobileimsdk.android.demo.event.MessageQoSEventImpl;
import android.content.Context;

public class IMClientManager
{
	private static String TAG = IMClientManager.class.getSimpleName();
	
	private static IMClientManager instance = null;
	
	/** MobileIMSDK是否已被初始化. true表示已初化完成，否则未初始化. */
	private boolean init = false;
	
	// 
	private ChatBaseEventImpl baseEventListener = null;
	//
	private ChatTransDataEventImpl transDataListener = null;
	//
	private MessageQoSEventImpl messageQoSListener = null;
	
	private Context context = null;

	public static IMClientManager getInstance(Context context)
	{
		if(instance == null)
			instance = new IMClientManager(context);
		return instance;
	}
	
	private IMClientManager(Context context)
	{
		this.context = context;
		initMobileIMSDK();
	}

	public void initMobileIMSDK()
	{
		if(!init)
		{
			// 设置AppKey
			ConfigEntity.appKey = "5418023dfd98c579b6001741";
			
			// 设置服务器ip和服务器端口
//			ConfigEntity.serverIP = "192.168.82.138";
//			ConfigEntity.serverIP = "rbcore.openmob.net";
//			ConfigEntity.serverUDPPort = 7901;
	    
			// MobileIMSDK核心IM框架的敏感度模式设置
//			ConfigEntity.setSenseMode(SenseMode.MODE_10S);
	    
			// 开启/关闭DEBUG信息输出
//	    	ClientCoreSDK.DEBUG = false;
			
			// 【特别注意】请确保首先进行核心库的初始化（这是不同于iOS和Java端的地方)
			ClientCoreSDK.getInstance().init(this.context);
	    
			// 设置事件回调
			baseEventListener = new ChatBaseEventImpl();
			transDataListener = new ChatTransDataEventImpl();
			messageQoSListener = new MessageQoSEventImpl();
			ClientCoreSDK.getInstance().setChatBaseEvent(baseEventListener);
			ClientCoreSDK.getInstance().setChatTransDataEvent(transDataListener);
			ClientCoreSDK.getInstance().setMessageQoSEvent(messageQoSListener);
			
			init = true;
		}
	}

	public void release()
	{
		ClientCoreSDK.getInstance().release();
	}

	public ChatTransDataEventImpl getTransDataListener()
	{
		return transDataListener;
	}
	public ChatBaseEventImpl getBaseEventListener()
	{
		return baseEventListener;
	}
	public MessageQoSEventImpl getMessageQoSListener()
	{
		return messageQoSListener;
	}
}
