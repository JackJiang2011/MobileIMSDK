/*
 * Copyright (C) 2016 即时通讯网(52im.net) The MobileIMSDK Project. 
 * All rights reserved.
 * Project URL:https://github.com/JackJiang2011/MobileIMSDK
 *  
 * 即时通讯网(52im.net) - 即时通讯技术社区! PROPRIETARY/CONFIDENTIAL.
 * Use is subject to license terms.
 * 
 * MessageQoSEventS2CListnerImpl.java at 2016-2-20 11:25:05, code by Jack Jiang.
 * You can contact author with jack.jiang@52im.net or jb2011@163.com.
 */
package net.openmob.mobileimsdk.server.demo;

import java.util.ArrayList;

import net.openmob.mobileimsdk.server.event.MessageQoSEventListenerS2C;
import net.openmob.mobileimsdk.server.protocal.Protocal;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MessageQoSEventS2CListnerImpl implements MessageQoSEventListenerS2C
{
	private static Logger logger = LoggerFactory.getLogger(MessageQoSEventS2CListnerImpl.class);  
	
	@Override
	public void messagesLost(ArrayList<Protocal> lostMessages)
	{
		logger.debug("【DEBUG_QoS_S2C事件】收到系统的未实时送达事件通知，当前共有"
						+lostMessages.size()+"个包QoS保证机制结束，判定为【无法实时送达】！");
	}

	@Override
	public void messagesBeReceived(String theFingerPrint)
	{
		if(theFingerPrint != null)
		{
			logger.debug("【DEBUG_QoS_S2C事件】收到对方已收到消息事件的通知，fp="+theFingerPrint);
		}
	}
}
