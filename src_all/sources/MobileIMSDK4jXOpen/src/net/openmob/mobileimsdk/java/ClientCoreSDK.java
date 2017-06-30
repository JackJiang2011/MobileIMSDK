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
 * ClientCoreSDK.java at 2017-5-1 22:14:56, code by Jack Jiang.
 * You can contact author with jack.jiang@52im.net or jb2011@163.com.
 */
package net.openmob.mobileimsdk.java;

import net.openmob.mobileimsdk.java.core.AutoReLoginDaemon;
import net.openmob.mobileimsdk.java.core.KeepAliveDaemon;
import net.openmob.mobileimsdk.java.core.LocalUDPDataReciever;
import net.openmob.mobileimsdk.java.core.LocalUDPSocketProvider;
import net.openmob.mobileimsdk.java.core.QoS4ReciveDaemon;
import net.openmob.mobileimsdk.java.core.QoS4SendDaemon;
import net.openmob.mobileimsdk.java.event.ChatBaseEvent;
import net.openmob.mobileimsdk.java.event.ChatTransDataEvent;
import net.openmob.mobileimsdk.java.event.MessageQoSEvent;

public class ClientCoreSDK
{
	private final static String TAG = ClientCoreSDK.class.getSimpleName();
	
	public static boolean DEBUG = true;
	public static boolean autoReLogin = true;
	
	private static ClientCoreSDK instance = null;
	
	private boolean _init = false;
	private boolean connectedToServer = true;
	private boolean loginHasInit = false;
	private String currentLoginUserId = null;
	private String currentLoginToken = null;
	private String currentLoginExtra = null;
	
	private ChatBaseEvent chatBaseEvent = null;
	private ChatTransDataEvent chatTransDataEvent = null;
	private MessageQoSEvent messageQoSEvent = null;
	
	public static ClientCoreSDK getInstance()
	{
		if(instance == null)
			instance = new ClientCoreSDK();
		return instance;
	}
	
	private ClientCoreSDK()
	{
	}
	
	public void init()
	{
		if(!_init)
		{
			_init = true;
		}
	}
	
	public void release()
	{
		LocalUDPSocketProvider.getInstance().closeLocalUDPSocket();
	    AutoReLoginDaemon.getInstance().stop(); // 2014-11-08 add by Jack Jiang
		QoS4SendDaemon.getInstance().stop();
		KeepAliveDaemon.getInstance().stop();
		LocalUDPDataReciever.getInstance().stop();
		QoS4ReciveDaemon.getInstance().stop();
		
		_init = false;
		
		this.setLoginHasInit(false);
		this.setConnectedToServer(false);
	}
	
	public String getCurrentLoginUserId()
	{
		return currentLoginUserId;
	}
	public ClientCoreSDK setCurrentLoginUserId(String currentLoginUserId)
	{
		this.currentLoginUserId = currentLoginUserId;
		return this;
	}
	
	public String getCurrentLoginToken()
	{
		return currentLoginToken;
	}
	public void setCurrentLoginToken(String currentLoginToken)
	{
		this.currentLoginToken = currentLoginToken;
	}
	
	public String getCurrentLoginExtra()
	{
		return currentLoginExtra;
	}
	public ClientCoreSDK setCurrentLoginExtra(String currentLoginExtra)
	{
		this.currentLoginExtra = currentLoginExtra;
		return this;
	}

	public boolean isLoginHasInit()
	{
		return loginHasInit;
	}
	public ClientCoreSDK setLoginHasInit(boolean loginHasInit)
	{
		this.loginHasInit = loginHasInit;
		return this;
	}
	
	public boolean isConnectedToServer()
	{
		return connectedToServer;
	}
	public void setConnectedToServer(boolean connectedToServer)
	{
		this.connectedToServer = connectedToServer;
	}

	public boolean isInitialed()
	{
		return this._init;
	}

	public void setChatBaseEvent(ChatBaseEvent chatBaseEvent)
	{
		this.chatBaseEvent = chatBaseEvent;
	}
	public ChatBaseEvent getChatBaseEvent()
	{
		return chatBaseEvent;
	}
	
	public void setChatTransDataEvent(ChatTransDataEvent chatTransDataEvent)
	{
		this.chatTransDataEvent = chatTransDataEvent;
	}
	public ChatTransDataEvent getChatTransDataEvent()
	{
		return chatTransDataEvent;
	}
	
	public void setMessageQoSEvent(MessageQoSEvent messageQoSEvent)
	{
		this.messageQoSEvent = messageQoSEvent;
	}
	public MessageQoSEvent getMessageQoSEvent()
	{
		return messageQoSEvent;
	}
}
