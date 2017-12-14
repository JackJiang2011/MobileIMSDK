/*
 * Copyright (C) 2017  即时通讯网(52im.net) & Jack Jiang.
 * The MobileIMSDK_X_netty (MobileIMSDK v3.x Netty版) Project. 
 * All rights reserved.
 * 
 * > Github地址: https://github.com/JackJiang2011/MobileIMSDK
 * > 文档地址: http://www.52im.net/forum-89-1.html
 * > 即时通讯技术社区：http://www.52im.net/
 * > 即时通讯技术交流群：320837163 (http://www.52im.net/topic-qqgroup.html)
 *  
 * "即时通讯网(52im.net) - 即时通讯开发者社区!" 推荐开源工程。
 * 
 * MessageQoSEventS2CListnerImpl.java at 2017-12-9 12:47:43, code by Jack Jiang.
 * You can contact author with jack.jiang@52im.net or jb2011@163.com.
 */
package net.openmob.mobileimsdk.server.demo;

import java.util.ArrayList;

import net.openmob.mobileimsdk.server.event.MessageQoSEventListenerS2C;
import net.openmob.mobileimsdk.server.protocal.Protocal;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 由开发者通过服务端消息发送接口发出的消息的消息送达相关事件（由S2C模式（即Server to Client）
 * 下QoS机制通知上来的）在此MessageQoSEventListenerS2C子类中实现即可。
 * 
 * @author Jack Jiang, 2017-12-08
 * @version 1.0
 * @since 3.1
 */
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
