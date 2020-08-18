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
 * ChatBaseEventImpl.java at 2017-5-1 21:08:44, code by Jack Jiang.
 * You can contact author with jack.jiang@52im.net or jb2011@163.com.
 */
package net.openmob.mobileimsdk.android.demo.event;

import java.util.Observer;

import net.openmob.mobileimsdk.android.demo.MainActivity;
import net.openmob.mobileimsdk.android.event.ChatBaseEvent;
import android.util.Log;

/**
 * 与IM服务器的连接事件在此ChatBaseEvent子类中实现即可。
 * 
 * @author Jack Jiang, 20170501
 * @version.1.1
 */
public class ChatBaseEventImpl implements ChatBaseEvent
{
	private final static String TAG = ChatBaseEventImpl.class.getSimpleName();
	
	private MainActivity mainGUI = null; 
	
	// 本Observer目前仅用于登陆时（因为登陆与收到服务端的登陆验证结果
	// 是异步的，所以有此观察者来完成收到验证后的处理）
	private Observer loginOkForLaunchObserver = null;
	
	@Override
	public void onLoginMessage(int dwErrorCode)
	{
		if (dwErrorCode == 0) 
		{
			Log.i(TAG, "【DEBUG_UI】IM服务器登录/重连成功！");
			
			// TODO 以下代码仅用于DEMO哦
			if(this.mainGUI != null)
			{
				this.mainGUI.refreshMyid();
				this.mainGUI.showIMInfo_green("IM服务器登录/重连成功,dwErrorCode="+dwErrorCode);
			}
		}
		else 
		{
			Log.e(TAG, "【DEBUG_UI】IM服务器登录/连接失败，错误代码：" + dwErrorCode);

			// TODO 以下代码仅用于DEMO哦
			if(this.mainGUI != null)
			{
				this.mainGUI.refreshMyid();
				this.mainGUI.showIMInfo_red("IM服务器登录/连接失败,code="+dwErrorCode);
			}
		}
		
		// 此观察者只有开启程序首次使用登陆界面时有用
		if(loginOkForLaunchObserver != null)
		{
			loginOkForLaunchObserver.update(null, dwErrorCode);
			loginOkForLaunchObserver = null;
		}
	}

	@Override
	public void onLinkCloseMessage(int dwErrorCode)
	{
		Log.e(TAG, "【DEBUG_UI】与IM服务器的网络连接出错关闭了，error：" + dwErrorCode);
		
		// TODO 以下代码仅用于DEMO哦
		if(this.mainGUI != null)
		{
			this.mainGUI.refreshMyid();
			this.mainGUI.showIMInfo_red("与IM服务器的连接已断开, 自动登陆/重连将启动! ("+dwErrorCode+")");
		}
	}
	
	public void setLoginOkForLaunchObserver(Observer loginOkForLaunchObserver)
	{
		this.loginOkForLaunchObserver = loginOkForLaunchObserver;
	}
	
	public ChatBaseEventImpl setMainGUI(MainActivity mainGUI)
	{
		this.mainGUI = mainGUI;
		return this;
	}
}
