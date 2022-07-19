/*
 * Copyright (C) 2022  即时通讯网(52im.net) & Jack Jiang.
 * The MobileIMSDK_UDP (MobileIMSDK v6.x UDP版) Project. 
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
 * MessageQoSEventImpl.java at 2022-7-16 17:02:12, code by Jack Jiang.
 */
package net.x52im.mobileimsdk.java.demo.event;

import java.util.ArrayList;

import net.x52im.mobileimsdk.java.demo.MainGUI;
import net.x52im.mobileimsdk.java.event.MessageQoSEvent;
import net.x52im.mobileimsdk.java.utils.Log;
import net.x52im.mobileimsdk.server.protocal.Protocal;

/**
 * 消息送达相关事件（由QoS机制通知上来的）在此MessageQoSEvent子类中实现即可。
 * 
 * @author Jack Jiang, 20170501
 * @version.1.1
 */
public class MessageQoSEventImpl implements MessageQoSEvent
{
	private final static String TAG = MessageQoSEventImpl.class.getSimpleName();
	
	private MainGUI mainGUI = null; 
	
	/**
	 * 消息未送达的回调事件通知.
	 *
	 * @param lostMessages 由MobileIMSDK QoS算法判定出来的未送达消息列表（此列表中的Protocal对象是原对象的
	 *                        clone（即原对象的深拷贝），请放心使用哦），应用层可通过指纹特征码找到原消息并可
	 *                        以UI上将其标记为”发送失败“以便即时告之用户
	 * @see net.x52im.mobileimsdk.server.protocal.Protocal
	 */
	@Override
	public void messagesLost(ArrayList<Protocal> lostMessages)
	{
		Log.d(TAG, "【DEBUG_UI】收到系统的未实时送达事件通知，当前共有"+lostMessages.size()+"个包QoS保证机制结束，判定为【无法实时送达】！");
		
		if(this.mainGUI != null)
		{
			this.mainGUI.showIMInfo_brightred("[消息未成功送达]共"+lostMessages.size()+"条!(网络状况不佳或对方id不存在)");
		}
	}

	/**
	 * 消息已被对方收到的回调事件通知.
	 * <p>
	 * <b>目前，判定消息被对方收到是有两种可能：</b><br>
	 * <ul>
	 * <li>1) 对方确实是在线并且实时收到了；</li>
	 * <li>2) 对方不在线或者服务端转发过程中出错了，由服务端进行离线存储成功后的反馈（此种情况严格来讲不能算是“已被
	 * 		收到”，但对于应用层来说，离线存储了的消息原则上就是已送达了的消息：因为用户下次登陆时肯定能通过HTTP协议取到）。</li>
	 * </ul>
	 *
	 * @param theFingerPrint 已被收到的消息的指纹特征码（唯一ID），应用层可据此ID来找到原先已发生的消息并可在
	 *                          UI是将其标记为”已送达“或”已读“以便提升用户体验
	 * @see net.x52im.mobileimsdk.server.protocal.Protocal
	 */
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
	
	public MessageQoSEventImpl setMainGUI(MainGUI mainGUI)
	{
		this.mainGUI = mainGUI;
		return this;
	}
}
