/*
 * Copyright (C) 2022  即时通讯网(52im.net) & Jack Jiang.
 * The MobileIMSDK v6.x Project. 
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
 * MessageQoSEventS2CListnerImpl.java at 2022-7-12 16:35:42, code by Jack Jiang.
 */
package net.x52im.mobileimsdk.server.demo;

import java.util.ArrayList;

import net.x52im.mobileimsdk.server.event.MessageQoSEventListenerS2C;
import net.x52im.mobileimsdk.server.protocal.Protocal;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * MobileIMSDK的服务端QoS消息送达保证机制的事件监听器实现类。
 * <p>
 * <b>当前QoS机制支持全部的C2C、C2S、S2C共3种消息交互场景下的消息送达质量保证：<</b>>
 * <ur>
 * <li>1) Client to Server(C2S)：即由某客户端主动发起，消息最终接收者是服务端，此模式下：重发由C保证、ACK应答由S发回；</li>
 * <li>2) Server to Client(S2C)：即由服务端主动发起，消息最终接收者是某客户端，此模式下：重发由S保证、ACK应答由C发回；</li>
 * <li>2) Client to Client(C2C)：即由客户端主动发起，消息最终接收者是另一客户端。此模式对于QoS机制来说，相当于C2S+S2C两程路径。</li>
 * </ul>
 * <p>
 * TCP理论上能从底层保证数据的可靠性，但应用层的代码和场景中存在网络本身和网络之外的各种不可靠性，
 * MobileIMSDK中的QoS送达保证机制，将加强TCP的可靠性，确保消息，无法从哪一个层面和维度，都会给
 * 开发者提供两种结果：要么明确被送达（即收到ACK应答包，见 {@link #messagesBeReceived(String)}）
 * 、要行明确未被送达（见 {@link #messagesLost(ArrayList)}）。从理论上，保证消息的百分百送达率。
 * 
 * @author Jack Jiang
 * @version 1.0
 * @since 3.1
 * @see net.x52im.mobileimsdk.server.qos.QoS4SendDaemonS2C
 * @see net.x52im.mobileimsdk.server.qos.QoS4ReciveDaemonC2S
 * @see MessageQoSEventListenerS2C
 */
public class MessageQoSEventS2CListnerImpl implements MessageQoSEventListenerS2C
{
	private static Logger logger = LoggerFactory.getLogger(MessageQoSEventS2CListnerImpl.class);  
	
	/**
	 * 消息未送达的回调事件通知.
	 * 
	 * @param lostMessages 由MobileIMSDK QoS算法判定出来的未送达消息列表（此列表
	 * 中的Protocal对象是原对象的clone（即原对象的深拷贝），请放心使用哦），应用层
	 * 可通过指纹特征码找到原消息并可以UI上将其标记为”发送失败“以便即时告之用户
	 */
	@Override
	public void messagesLost(ArrayList<Protocal> lostMessages)
	{
		logger.debug("【DEBUG_QoS_S2C事件】收到系统的未实时送达事件通知，当前共有"
						+lostMessages.size()+"个包QoS保证机制结束，判定为【无法实时送达】！");
	}

	/**
	 * 消息已被对方收到的回调事件通知.
	 * <p>
	 * <b>目前，判定消息被对方收到是有两种可能：</b><br>
	 * 1) 对方确实是在线并且实时收到了；<br>
	 * 2) 对方不在线或者服务端转发过程中出错了，由服务端进行离线存储成功后的反馈
	 * （此种情况严格来讲不能算是“已被收到”，但对于应用层来说，离线存储了的消息
	 * 原则上就是已送达了的消息：因为用户下次登陆时肯定能通过HTTP协议取到）。
	 * 
	 * @param theFingerPrint 已被收到的消息的指纹特征码（唯一ID），应用层可据此ID
	 * 来找到原先已发生的消息并可在UI是将其标记为”已送达“或”已读“以便提升用户体验
	 */
	@Override
	public void messagesBeReceived(String theFingerPrint)
	{
		if(theFingerPrint != null)
		{
			logger.debug("【DEBUG_QoS_S2C事件】收到对方已收到消息事件的通知，fp="+theFingerPrint);
		}
	}
}
