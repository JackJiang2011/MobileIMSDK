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
 * MessageQoSEventImpl.java at 2017-5-1 21:08:44, code by Jack Jiang.
 * You can contact author with jack.jiang@52im.net or jb2011@163.com.
 */
package net.openmob.mobileimsdk.android.demo.event;

import java.util.ArrayList;

import net.openmob.mobileimsdk.android.demo.MainActivity;
import net.openmob.mobileimsdk.android.event.MessageQoSEvent;
import net.openmob.mobileimsdk.server.protocal.Protocal;
import android.util.Log;

/**
 * 消息送达相关事件（由QoS机制通知上来的）在此MessageQoSEvent子类中实现即可。
 * 
 * @author Jack Jiang, 20170501
 * @version.1.1
 */
public class MessageQoSEventImpl implements MessageQoSEvent
{
	private final static String TAG = MessageQoSEventImpl.class.getSimpleName();
	
	private MainActivity mainGUI = null; 
	
	@Override
	public void messagesLost(ArrayList<Protocal> lostMessages)
	{
		Log.d(TAG, "【DEBUG_UI】收到系统的未实时送达事件通知，当前共有"+lostMessages.size()+"个包QoS保证机制结束，判定为【无法实时送达】！");
	
		if(this.mainGUI != null)
		{
			this.mainGUI.showIMInfo_brightred("[消息未成功送达]共"+lostMessages.size()+"条!(网络状况不佳或对方id不存在)");
		}
	}

	@Override
	public void messagesBeReceived(String theFingerPrint)
	{
		if(theFingerPrint != null)
		{
			Log.d(TAG, "【DEBUG_UI】收到对方已收到消息事件的通知，fp="+theFingerPrint);
			if(this.mainGUI != null)
			{
				this.mainGUI.showIMInfo_blue("[收到对方消息应答]fp="+theFingerPrint);
			}
		}
	}
	
	public MessageQoSEventImpl setMainGUI(MainActivity mainGUI)
	{
		this.mainGUI = mainGUI;
		return this;
	}
}
