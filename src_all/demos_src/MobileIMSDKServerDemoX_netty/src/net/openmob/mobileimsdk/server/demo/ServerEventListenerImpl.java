/*
 * Copyright (C) 2020  即时通讯网(52im.net) & Jack Jiang.
 * The MobileIMSDK_X_netty (MobileIMSDK v4.x Netty版) Project. 
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
 * ServerEventListenerImpl.java at 2020-4-14 23:20:49, code by Jack Jiang.
 */
package net.openmob.mobileimsdk.server.demo;

import io.netty.channel.Channel;
import net.openmob.mobileimsdk.server.event.ServerEventListener;
import net.openmob.mobileimsdk.server.protocal.Protocal;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 与客服端的所有数据交互事件在此ServerEventListener子类中实现即可。
 * 
 * @author Jack Jiang
 * @version 1.0
 * @since 3.1
 */
public class ServerEventListenerImpl implements ServerEventListener
{
	private static Logger logger = LoggerFactory.getLogger(ServerEventListenerImpl.class);  
	
	/**
	 * 用户身份验证回调方法定义.
	 * <p>
	 * 服务端的应用层可在本方法中实现用户登陆验证。
	 * <br>
	 * 注意：本回调在一种特殊情况下——即用户实际未退出登陆但再次发起来登陆包时，本回调是不会被调用的！
	 * <p>
	 * 根据MobileIMSDK的算法实现，本方法中用户验证通过（即方法返回值=0时）后
	 * ，将立即调用回调方法 {@link #onUserLoginAction_CallBack(int, String, IoSession)}。
	 * 否则会将验证结果（本方法返回值错误码通过客户端的 ChatBaseEvent.onLoginMessage(int dwUserId, int dwErrorCode)
	 * 方法进行回调）通知客户端）。
	 * 
	 * @param userId 传递过来的准一id，保证唯一就可以通信，可能是登陆用户名、也可能是任意不重复的id等，具体意义由业务层决定
	 * @param token 用于身份鉴别和合法性检查的token，它可能是登陆密码，也可能是通过前置单点登陆接口拿到的token等，具体意义由业务层决定
	 * @param extra 额外信息字符串。本字段目前为保留字段，供上层应用自行放置需要的内容
	 * @param session 此客户端连接对应的 netty “会话”
	 * @return 0 表示登陆验证通过，否则可以返回用户自已定义的错误码，错误码值应为：>=1025的整数
	 */
	@Override
	public int onVerifyUserCallBack(String userId, String token, String extra, Channel session)
	{
		logger.debug("【DEBUG_回调通知】正在调用回调方法：OnVerifyUserCallBack...(extra="+extra+")");
		return 0;
	}

	/**
	 * 用户登录验证成功后的回调方法定义（可理解为上线通知回调）.
	 * <p>
	 * 服务端的应用层通常可在本方法中实现用户上线通知等。
	 * <br>
	 * 注意：本回调在一种特殊情况下——即用户实际未退出登陆但再次发起来登陆包时，回调也是一定会被调用。
	 * 
	 * @param userId 传递过来的准一id，保证唯一就可以通信，可能是登陆用户名、也可能是任意不重复的id等，具体意义由业务层决定
	 * @param extra 额外信息字符串。本字段目前为保留字段，供上层应用自行放置需要的内容。为了丰富应用层处理的手段，在本回调中也把此字段传进来了
	 * @param session 此客户端连接对应的 netty “会话”
	 */
	@Override
	public void onUserLoginAction_CallBack(String userId, String extra, Channel session)
	{
		logger.debug("【IM_回调通知OnUserLoginAction_CallBack】用户："+userId+" 上线了！");
	}

	/**
	 * 用户退出登录回调方法定义（可理解为下线通知回调）。
	 * <p>
	 * 服务端的应用层通常可在本方法中实现用户下线通知等。
	 * 
	 * @param userId 下线的用户user_id
	 * @param obj
	 * @param session 此客户端连接对应的 netty “会话”
	 */
	@Override
	public void onUserLogoutAction_CallBack(String userId, Object obj, Channel session)
	{
		logger.debug("【DEBUG_回调通知OnUserLogoutAction_CallBack】用户："+userId+" 离线了！");
	}

	/**
	 * 通用数据回调方法定义（客户端发给服务端的（即接收user_id="0"））.
	 * <p>
	 * MobileIMSDK在收到客户端向user_id=0(即接收目标是服务器)的情况下通过
	 * 本方法的回调通知上层。上层通常可在本方法中实现如：添加好友请求等业务实现。
	 * 
	 * <p style="background:#fbf5ee;border-radius:4px;">
	 * <b><font color="#ff0000">【版本兼容性说明】</font></b>本方法用于替代v3.x中的以下方法：<br>
	 * <code>public boolean onTransBuffer_CallBack(String userId, String from_user_id
	 * 			, String dataContent, String fingerPrint, int typeu, Channel session);
	 * </code>
	 * 
	 * @param userId 接收方的user_id（本方法接收的是发给服务端的消息，所以此参数的值肯定==0）
	 * @param from_user_id 发送方的user_id
	 * @param dataContent 数据内容（文本形式）
	 * @param session 此客户端连接对应的 netty “会话”
	 * @return true表示本方法已成功处理完成，否则表示未处理成功。此返回值目前框架中并没有特殊意义，仅作保留吧
	 * @since 4.0
	 */
	@Override
	public boolean onTransBuffer_C2S_CallBack(Protocal p, Channel session)
	{
		// 接收者uid
		String userId = p.getTo();
		// 发送者uid
		String from_user_id = p.getFrom();
		// 消息或指令内容
		String dataContent = p.getDataContent();
		// 消息或指令指纹码（即唯一ID）
		String fingerPrint = p.getFp();
		// 【重要】用户定义的消息或指令协议类型（开发者可据此类型来区分具体的消息或指令）
		int typeu = p.getTypeu();
				
		logger.debug("【DEBUG_回调通知】[typeu="+typeu+"]收到了客户端"+from_user_id+"发给服务端的消息：str="+dataContent);
		return true;
	}

	/**
	 * 通道数据回调函数定义（客户端发给客户端的（即接收方user_id不为“0”的情况））.
	 * <p>
	 * <b>注意：</b>本方法当且仅当在数据被服务端成功在线发送出去后被回调调用.
	 * <p>
	 * 上层通常可在本方法中实现用户聊天信息的收集，以便后期监控分析用户的行为等^_^。
	 * <p>
	 * 提示：如果开启消息QoS保证，因重传机制，本回调中的消息理论上有重复的可能，请以参数 #fingerPrint
	 * 作为消息的唯一标识ID进行去重处理。
	 * 
	 * <p style="background:#fbf5ee;border-radius:4px;">
	 * <b><font color="#ff0000">【版本兼容性说明】</font></b>本方法用于替代v3.x中的以下方法：<br>
	 * <code>public void onTransBuffer_C2C_CallBack(String userId, String from_user_id
	 *			, String dataContent, String fingerPrint, int typeu);
	 * 
	 * @param userId 接收方的user_id（本方法接收的是客户端发给客户端的，所以此参数的值肯定>0）
	 * @param from_user_id 发送方的user_id
	 * @param dataContent
	 * @since 4.0
	 */
	@Override
	public void onTransBuffer_C2C_CallBack(Protocal p)
	{
		// 接收者uid
		String userId = p.getTo();
		// 发送者uid
		String from_user_id = p.getFrom();
		// 消息或指令内容
		String dataContent = p.getDataContent();
		// 消息或指令指纹码（即唯一ID）
		String fingerPrint = p.getFp();
		// 【重要】用户定义的消息或指令协议类型（开发者可据此类型来区分具体的消息或指令）
		int typeu = p.getTypeu();
				
		logger.debug("【DEBUG_回调通知】[typeu="+typeu+"]收到了客户端"+from_user_id+"发给客户端"+userId+"的消息：str="+dataContent);
	}

	/**
	 * 通用数据实时发送失败后的回调函数定义（客户端发给客户端的（即接收方user_id不为“0”的情况））.
	 * <p>
	 * 注意：本方法当且仅当在数据被服务端<u>在线发送</u>失败后被回调调用.
	 * <p>
	 * <b>此方法存的意义何在？</b><br>
	 * 发生此种情况的场景可能是：对方确实不在线（那么此方法里就可以作为离线消息处理了）、
	 * 或者在发送时判断对方是在线的但服务端在发送时却没有成功（这种情况就可能是通信错误
	 * 或对方非正常通出但尚未到达会话超时时限）。<br><u>应用层在此方法里实现离线消息的处理即可！</u>
	 * 
	 * <p style="background:#fbf5ee;border-radius:4px;">
	 * <b><font color="#ff0000">【版本兼容性说明】</font></b>本方法用于替代v3.x中的以下方法：<br>
	 * <code>public boolean onTransBuffer_C2C_RealTimeSendFaild_CallBack(String userId
	 *			, String from_user_id, String dataContent, String fingerPrint, int typeu);
	 * </code>
	 * 
	 * @param userId 接收方的user_id（本方法接收的是客户端发给客户端的，所以此参数的值肯定>0），此id在本方法中不一定保证有意义
	 * @param from_user_id 发送方的user_id
	 * @param dataContent 消息内容
	 * @param fingerPrint 该消息对应的指纹（如果该消息有QoS保证机制的话），用于在QoS重要机制下服务端离线存储时防止重复存储哦
	 * @return true表示应用层已经处理了离线消息（如果该消息有QoS机制，则服务端将代为发送一条伪应答包
	 * （伪应答仅意味着不是接收方的实时应答，而只是存储到离线DB中，但在发送方看来也算是被对方收到，只是延
	 * 迟收到而已（离线消息嘛））），否则表示应用层没有处理（如果此消息有QoS机制，则发送方在QoS重传机制超时
	 * 后报出消息发送失败的提示）
	 * @see #onTransBuffer_C2C_CallBack(Protocal)
	 * @since 4.0
	 */
	@Override
	public boolean onTransBuffer_C2C_RealTimeSendFaild_CallBack(Protocal p)
	{
		// 接收者uid
		String userId = p.getTo();
		// 发送者uid
		String from_user_id = p.getFrom();
		// 消息或指令内容
		String dataContent = p.getDataContent();
		// 消息或指令指纹码（即唯一ID）
		String fingerPrint = p.getFp();
		// 【重要】用户定义的消息或指令协议类型（开发者可据此类型来区分具体的消息或指令）
		int typeu = p.getTypeu();

		logger.debug("【DEBUG_回调通知】[typeu="+typeu+"]客户端"+from_user_id+"发给客户端"+userId+"的消息：str="+dataContent
				+"，因实时发送没有成功，需要上层应用作离线处理哦，否则此消息将被丢弃.");
		return false;
	}
}
