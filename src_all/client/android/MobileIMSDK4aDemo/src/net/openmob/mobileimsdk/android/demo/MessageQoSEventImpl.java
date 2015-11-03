/*
 * Copyright (C) 2015 Jack Jiang The MobileIMSDK Project. 
 * All rights reserved.
 * Project URL:https://github.com/JackJiang2011/MobileIMSDK
 *  
 * openmob.net PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 * 
 * MessageQoSEventImpl.java at 2015-10-7 22:01:48, code by Jack Jiang.
 * You can contact author with jack.jiang@openmob.net or jb2011@163.com.
 */
package net.openmob.mobileimsdk.android.demo;

import java.util.ArrayList;

import net.openmob.mobileimsdk.android.event.MessageQoSEvent;
import net.openmob.mobileimsdk.server.protocal.Protocal;
import android.util.Log;

public class MessageQoSEventImpl implements MessageQoSEvent
{
	private final static String TAG = MessageQoSEventImpl.class.getSimpleName();
	
	private DemoMain ____temp = null; 
	
	@Override
	public void messagesLost(ArrayList<Protocal> lostMessages)
	{
		Log.d(TAG, "【DEBUG_UI】收到系统的未实时送达事件通知，当前共有"+lostMessages.size()+"个包QoS保证机制结束，判定为【无法实时送达】！");
	
		if(this.____temp != null)
		{
			this.____temp.showIMInfo_brightred("[消息未成功送达]共"+lostMessages.size()+"条!(网络状况不佳或对方id不存在)");
		}
	}

	@Override
	public void messagesBeReceived(String theFingerPrint)
	{
		if(theFingerPrint != null)
		{
			Log.d(TAG, "【DEBUG_UI】收到对方已收到消息事件的通知，fp="+theFingerPrint);
			if(this.____temp != null)
			{
				this.____temp.showIMInfo_blue("[收到对方消息应答]fp="+theFingerPrint);
			}
		}
	}
	
	public MessageQoSEventImpl set____temp(DemoMain ____temp)
	{
		this.____temp = ____temp;
		return this;
	}

	public DemoMain get____temp()
	{
		return ____temp;
	}
}
