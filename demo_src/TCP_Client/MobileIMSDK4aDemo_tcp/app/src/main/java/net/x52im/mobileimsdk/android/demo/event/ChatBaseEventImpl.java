/*
 * Copyright (C) 2020  即时通讯网(52im.net) & Jack Jiang.
 * The MobileIMSDK_TCP (MobileIMSDK v5.x TCP版) Project. 
 * All rights reserved.
 * 
 * > Github地址：https://github.com/JackJiang2011/MobileIMSDK
 * > 文档地址：  http://www.52im.net/forum-89-1.html
 * > 技术社区：  http://www.52im.net/
 * > 技术交流群：320837163 (http://www.52im.net/topic-qqgroup.html)
 * > 作者公众号：“即时通讯技术圈】”，欢迎关注！
 * > 联系作者：  http://www.52im.net/thread-2792-1-1.html
 *  
 * "即时通讯网(52im.net) - 即时通讯开发者社区!" 推荐开源工程。
 * 
 * ChatBaseEventImpl.java at 2020-8-8 16:00:38, code by Jack Jiang.
 */
package net.x52im.mobileimsdk.android.demo.event;

import java.util.Observer;

import net.x52im.mobileimsdk.android.demo.MainActivity;
import net.x52im.mobileimsdk.android.event.ChatBaseEvent;

import android.util.Log;

/**
 * 与IM服务器的连接事件在此ChatBaseEvent子类中实现即可。
 *
 * @author Jack Jiang(http://www.52im.net/thread-2792-1-1.html)
 * @version 1.1
 */
public class ChatBaseEventImpl implements ChatBaseEvent
{
	private final static String TAG = ChatBaseEventImpl.class.getSimpleName();
	
	private MainActivity mainGUI = null;
	
	// 本Observer目前仅用于登陆时（因为登陆与收到服务端的登陆验证结果
	// 是异步的，所以有此观察者来完成收到验证后的处理）
	private Observer loginOkForLaunchObserver = null;

	/**
	 * 本地用户的登陆结果回调事件通知。
	 *
	 * @param errorCode 服务端反馈的登录结果：0 表示登陆成功，否则为服务端自定义的出错代码（按照约定通常为>=1025的数）
	 */
	@Override
	public void onLoginResponse(int errorCode)
	{
		if (errorCode == 0)
		{
			Log.i(TAG, "【DEBUG_UI】IM服务器登录/重连成功！");
			
			// TODO 以下代码仅用于DEMO哦
			if(this.mainGUI != null)
			{
				this.mainGUI.refreshMyid();
				this.mainGUI.showIMInfo_green("IM服务器登录/重连成功,errorCode="+errorCode);
			}
		}
		else 
		{
			Log.e(TAG, "【DEBUG_UI】IM服务器登录/连接失败，错误代码：" + errorCode);

			// TODO 以下代码仅用于DEMO哦
			if(this.mainGUI != null)
			{
				this.mainGUI.refreshMyid();
				this.mainGUI.showIMInfo_red("IM服务器登录/连接失败,code="+errorCode);
			}
		}
		
		// 此观察者只有开启程序首次使用登陆界面时有用
		if(loginOkForLaunchObserver != null)
		{
			loginOkForLaunchObserver.update(null, errorCode);
			loginOkForLaunchObserver = null;
		}
	}

	/**
	 * 与服务端的通信断开的回调事件通知。
	 * <br>
	 * 该消息只有在客户端连接服务器成功之后网络异常中断之时触发。<br>
	 * 导致与与服务端的通信断开的原因有（但不限于）：无线网络信号不稳定、WiFi与2G/3G/4G等同开情况下的网络切换、手机系统的省电策略等。
	 *
	 * @param errorCode 本回调参数表示表示连接断开的原因，目前错误码没有太多意义，仅作保留字段，目前通常为-1
	 */
	@Override
	public void onLinkClose(int errorCode)
	{
		Log.e(TAG, "【DEBUG_UI】与IM服务器的网络连接出错关闭了，error：" + errorCode);
		
		// TODO 以下代码仅用于DEMO哦
		if(this.mainGUI != null)
		{
			this.mainGUI.refreshMyid();
			this.mainGUI.showIMInfo_red("与IM服务器的连接已断开, 自动登陆/重连将启动! ("+errorCode+")");
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
