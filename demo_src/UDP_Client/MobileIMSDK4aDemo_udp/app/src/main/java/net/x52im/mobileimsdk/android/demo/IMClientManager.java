/*
 * Copyright (C) 2022  即时通讯网(52im.net) & Jack Jiang.
 * The MobileIMSDK_UDP (MobileIMSDK v6.x UDP版) Project. 
 * All rights reserved.
 * 
 * > Github地址：https://github.com/JackJiang2011/MobileIMSDK
 * > 文档地址：  http://www.52im.net/forum-89-1.html
 * > 技术社区：  http://www.52im.net/
 * > 技术交流群：185926912 (http://www.52im.net/topic-qqgroup.html)
 * > 作者公众号：“即时通讯技术圈】”，欢迎关注！
 * > 联系作者：  http://www.52im.net/thread-2792-1-1.html
 *  
 * "即时通讯网(52im.net) - 即时通讯开发者社区!" 推荐开源工程。
 * 
 * IMClientManager.java at 2022-7-28 17:21:45, code by Jack Jiang.
 */
package net.x52im.mobileimsdk.android.demo;

import net.x52im.mobileimsdk.android.ClientCoreSDK;
import net.x52im.mobileimsdk.android.conf.ConfigEntity;
import net.x52im.mobileimsdk.android.demo.event.ChatBaseEventImpl;
import net.x52im.mobileimsdk.android.demo.event.ChatMessageEventImpl;
import net.x52im.mobileimsdk.android.demo.event.MessageQoSEventImpl;

import android.content.Context;

/**
 * MobileIMSDK的管理类。
 * 正式的APP项目中，建议在Application中管理本类，确保SDK的生命周期同步于整个APP的生命周期。
 *
 * @author Jack Jiang(http://www.52im.net/thread-2792-1-1.html)
 */
public class IMClientManager {
	private static IMClientManager instance = null;
	
	/** MobileIMSDK是否已被初始化. true表示已初化完成，否则未初始化. */
	private boolean init = false;

	/** 基本连接状态事件监听器 */
	private ChatBaseEventImpl chatBaseListener = null;
	/** 数据接收事件监听器 */
	private ChatMessageEventImpl chatMessageListener = null;
	/** 消息送达保证事件监听器 */
	private MessageQoSEventImpl messageQoSListener = null;
	
	private Context context = null;

	public static IMClientManager getInstance(Context context) {
		if(instance == null)
			instance = new IMClientManager(context);
		return instance;
	}
	
	private IMClientManager(Context context) {
		this.context = context;
		initMobileIMSDK();
	}

	/**
	 * MobileIMSDK的初始化方法。正式的APP项目中，建议本方法在Application的子类中调用。
	 */
	public void initMobileIMSDK() {
		if(!init) {
			// 设置服务器ip和服务器端口
//			ConfigEntity.serverIP = "192.168.82.138";
//			ConfigEntity.serverPort = 7901;
	    
			// MobileIMSDK核心IM框架的敏感度模式设置
//			ConfigEntity.setSenseMode(SenseMode.MODE_10S);
	    
			// 开启/关闭DEBUG信息输出
//	    	ClientCoreSDK.DEBUG = false;
			
			// 【特别注意】请确保首先进行核心库的初始化（这是不同于iOS和Java端的地方)
			ClientCoreSDK.getInstance().init(this.context);
	    
			// 设置事件回调
			chatBaseListener = new ChatBaseEventImpl();
			chatMessageListener = new ChatMessageEventImpl();
			messageQoSListener = new MessageQoSEventImpl();
			ClientCoreSDK.getInstance().setChatBaseEvent(chatBaseListener);
			ClientCoreSDK.getInstance().setChatMessageEvent(chatMessageListener);
			ClientCoreSDK.getInstance().setMessageQoSEvent(messageQoSListener);
			
			init = true;
		}
	}

	/**
	 * MobileIMSDK的资源释放方法（退出SDK时使用）。
	 */
	public void release() {
		ClientCoreSDK.getInstance().release();
		resetInitFlag();
	}

	/**
	 * 重置init标识。
	 * <p>
	 * <b>重要说明：</b>不退出APP的情况下，重新登陆时记得调用一下本方法，不然再
	 * 次调用 {@link #initMobileIMSDK()} 时也不会重新初始化MobileIMSDK（
	 * 详见 {@link #initMobileIMSDK()}代码）而报 code=203错误！
	 */
	public void resetInitFlag() {
		init = false;
	}

	public ChatMessageEventImpl getChatMessageListener() {
		return chatMessageListener;
	}

	public ChatBaseEventImpl getChatBaseListener() {
		return chatBaseListener;
	}

	public MessageQoSEventImpl getMessageQoSListener() {
		return messageQoSListener;
	}
}
