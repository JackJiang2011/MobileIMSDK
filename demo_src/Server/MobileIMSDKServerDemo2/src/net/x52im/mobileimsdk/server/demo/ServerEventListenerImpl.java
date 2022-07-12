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
 * ServerEventListenerImpl.java at 2022-7-12 16:35:42, code by Jack Jiang.
 */
package net.x52im.mobileimsdk.server.demo;

import io.netty.channel.Channel;
import net.x52im.mobileimsdk.server.event.ServerEventListener;
import net.x52im.mobileimsdk.server.processor.OnlineProcessor;
import net.x52im.mobileimsdk.server.protocal.Protocal;
import net.x52im.mobileimsdk.server.protocal.s.PKickoutInfo;

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
	 * 用户身份验证回调方法定义（即验证客户端连接的合法性，合法就允许正常能信，否则断开）.
	 * <p>
	 * 服务端的应用层可在本方法中实现用户登陆验证。
	 * <br>
	 * 注意：本回调在一种特殊情况下——即用户实际未退出登陆但再次发起来登陆包时，本回调是不会被调用的！
	 * <p>
	 * 根据MobileIMSDK的算法实现，本方法中用户验证通过（即方法返回值=0时）后
	 * ，将立即调用回调方法 {@link #onUserLoginSucess(int, String, IoSession)}。
	 * 否则会将验证结果（本方法返回值错误码通过客户端的 ChatBaseEvent.onLoginResponse(int userId, int errorCode)
	 * 方法进行回调）通知客户端）。
	 * 
	 * @param userId 传递过来的准一id，保证唯一就可以通信，可能是登陆用户名、也可能是任意不重复的id等，具体意义由业务层决定
	 * @param token 用于身份鉴别和合法性检查的token，它可能是登陆密码，也可能是通过前置单点登陆接口拿到的token等，具体意义由业务层决定
	 * @param extra 额外信息字符串。本字段目前为保留字段，供上层应用自行放置需要的内容
	 * @param session 此客户端连接对应的 netty “会话”
	 * @return 0 表示登陆验证通过，否则可以返回用户自已定义的错误码，错误码值应为：>=1025的整数
	 */
	@Override
	public int onUserLoginVerify(String userId, String token, String extra, Channel session)
	{
		logger.debug("【DEBUG_回调通知】正在调用回调方法：OnVerifyUserCallBack...(extra="+extra+")");
		return 0;
	}

	/**
	 * 用户登录验证成功后的回调方法定义（在业务上可理解为该用户的上线通知）.
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
	public void onUserLoginSucess(String userId, String extra, Channel session)
	{
		logger.debug("【IM_回调通知onUserLoginSucess】用户："+userId+" 上线了！");
	}

	/**
	 * 用户退出登录回调方法定义（可理解为下线通知回调）。
	 * <p>
	 * 服务端的应用层通常可在本方法中实现用户下线通知等。
	 * 
	 * @param userId 下线的用户user_id
	 * @param session 此客户端连接对应的 netty “会话”
	 * @param beKickoutCode 被踢原因编码，本参数当为-1时表示本次logout事件不是源自“被踢”，否则被踢原因编码请见 {@link PKickoutInfo}类中的常量定义
	 * @see {@link OnlineProcessor#setBeKickoutCodeForChannel(Channel, int)}
	 */
	@Override
	public void onUserLogout(String userId, Channel session, int beKickoutCode)
	{
		logger.debug("【DEBUG_回调通知onUserLogout】用户："+userId+" 离线了（beKickoutCode="+beKickoutCode+"）！");
	}
	
	/**
	 * 收到客户端发送给“服务端”的数据回调通知（即：消息路径为“C2S”的消息）前的处理逻辑。
	 * <p>
	 * <b>本方法的默认实现</b>：<font color="green">当开发者不需要本方法进行额外逻辑处理时，请直接返回true即可！</font>
	 * <p>
	 * <b>本方法的典型用途</b>：开发者可在本方法中实现如：用户聊天内容的鉴黄、过滤、篡改等等，把内容审读权限交给开发者，就看怎么用了。
	 * 
	 * @param p 消息/指令的完整协议包对象
	 * @param session 消息发送者的“会话”引用（也就是客户端的网络连接对象）
	 * @return true表示经过本方法后将正常进入 {@link #onTransferMessage4C2S(Protocal, Channel)}继续正常逻辑  ，false表示该条指令将不会继续处理（直接被丢弃）
	 * @see #onTransferMessage4C2S(Protocal, Channel)
	 * @since 6.2
	 */
	@Override
	public boolean onTransferMessage4C2CBefore(Protocal p, Channel session)
	{
		return true;
	}
	
	/**
	 * 收到客户端发送给“其它客户端”的数据回调通知（即：消息路径为“C2C”的消息）前的处理逻辑。
	 * <p>
	 * <b>本方法的默认实现</b>：<font color="green">当开发者不需要本方法进行额外逻辑处理时，请直接返回true即可！</font>
	 * <p>
	 * <b>本方法的典型用途</b>：开发者可在本方法中实现如：用户聊天内容的鉴黄、过滤、篡改等等，把内容审读权限交给开发者，就看怎么用了。
	 * 
	 * @param p 消息/指令的完整协议包对象
	 * @param session 消息发送者的“会话”引用（也就是客户端的网络连接对象）
	 * @return true表示经过本方法后将正常进入 {@link #onTransferMessage4C2C(Protocal)}继续正常逻辑  ，false表示该条指令将不会继续处理（直接被丢弃）
	 * @see #onTransferMessage4C2C(Protocal)
	 * @since 6.2
	 */
	@Override
	public boolean onTransferMessage4C2SBefore(Protocal p, Channel session)
	{
		return true;
	}

	/**
	 * 收到客户端发送给“服务端”的数据回调通知（即：消息路径为“C2S”的消息）.
	 * <p>
	 * MobileIMSDK在收到客户端向userId="0"(即接收目标是"服务器")的情况下通过
	 * 本方法的回调通知上层。
	 * <p>
	 * <b>本方法的典型用途</b>：开发者通常可在本方法中实现如：添加好友请求等需要服务端进行处理的业务。
	 * 
	 * @param p 消息/指令的完整协议包对象
	 * @param session 此客户端连接对应的 netty “会话”
	 * @return true表示本方法已成功处理完成，否则表示未处理成功。此返回值目前框架中并没有特殊意义，仅作保留吧
	 * @see Protocal
	 * @since 4.0
	 */
	@Override
	public boolean onTransferMessage4C2S(Protocal p, Channel session)
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
	 * 收到客户端发送给“其它客户端”的数据回调通知（即：消息路径为“C2C”的消息）.
	 * <p>
	 * <b>注意：</b>本方法当且仅当在数据被服务端成功实时发送（“实时”即意味着对方在线的情况下）出去后被回调调用.
	 * <p>
	 * <b>本方法的典型用途</b>：开发者可在本方法中可以实现用户聊天信息的收集，以便后期监控分析用户的行为等^_^。
	 * 开发者可以对本方法不作任何代码实现，也不会影响整个MobileIMSDK的运行，因为本回调并非关键逻辑，只是个普通消息传输结果的回调而已。
	 * <p>
	 * 提示：如果开启消息QoS保证，因重传机制，本回调中的消息理论上有重复的可能，请以参数 #fingerPrint
	 * 作为消息的唯一标识ID进行去重处理。
	 * 
	 * @param p 消息/指令的完整协议包对象
	 * @see Protocal
	 * @since 4.0
	 */
	@Override
	public void onTransferMessage4C2C(Protocal p)
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
	 * 服务端在进行消息发送时，当对方在线但实时发送失败、以及其它各种问题导致消息并没能正常发出时
	 * ，将无条件走本回调通知。
	 * 
	 * <p>
	 * <b>注意：</b>本方法当且仅当在数据被服务端<u>在线发送</u>失败后被回调调用.
	 * 
	 * <p>
	 * <b>举个例子：以下是一段典型的服务端消息/指令发送代码：</b>
	 * <pre style="border: 1px solid #eaeaea;background-color: #fff6ea;border-radius: 6px;">
	 * // 消息接收者的id（这个id由你自已定义，对于MobileIMSDK来说只要保证唯一性即可）
	 * String destinationUserId = "400069";
	 * 
	 * // 这是要发送的消息（"你好"是消息内容、“0”是消息发送者）
	 * final Protocal p = ProtocalFactory.createCommonData("你好", "0", destinationUserId, true, null, -1);
	 * 	
	 * // 对方在线的情况下，才需要实时发送，否则走离线处理逻辑
	 * if(OnlineProcessor.isOnline(destinationUserId)) {
	 *     // netty是异步通知数据发送结果的
	 *     MBObserver＜Object＞ resultObserver = new MBObserver＜Object＞(){
	 *         public void update(boolean sucess, Object extraObj) {
	 *             if(sucess){
	 *                 // 你的消息/指令实时发送成功，不需要额外处理了
	 *             }
	 *             else{
	 *                 //【1】TODO: 你的消息/指令实时发送失败，在这里实现离线消息处理逻辑！
	 *             }
	 *         }
	 *     };
	 * 		
	 *     //【2】开始实时消息/指令的发送
	 *     LocalSendHelper.sendData(p, resultObserver);
	 * }
	 * else{
	 *     //【3】TODO: 你的离线消息处理逻辑！
	 * }
	 * <br>
	 * <font color="#0000ff">如上代码所示：“【1】【3】”代码处，开发者可以自行明确地进行离线逻辑处理，“【2】”处如
	 * 果实时发送时出现任何问题，将会走本回调方法进行通知，框架正是通过此回调进一步确保消息可靠性保证的。</font>
	 * </pre>
	 * <p>
	 * 
	 * <p>
	 * <b>本方法的典型用途</b>：<br>
	 * 开发者可在本方法中实现离线消息的持久化存储（反正进到本回调通知的消息，就是应该被离线存储起来的）。
	 * 
	 * <p>
	 * <b>此方法存的意义何在？</b><br>
	 * 发生此种情况的场景可能是：对方确实不在线（那么此方法里就可以作为离线消息处理了）、或者在发送时判断对方是在线的
	 * 但服务端在发送时却没有成功（这种情况就可能是通信错误或对方非正常通出但尚未到达会话超时时限）。<br><u>应用层在
	 * 此方法里实现离线消息的处理即可！</u>
	 * 
	 * @param p 消息/指令的完整协议包对象
	 * @return true表示应用层已经处理了离线消息（如果该消息有QoS机制，则服务端将代为发送一条伪应答包
	 * （伪应答仅意味着不是接收方的实时应答，而只是存储到离线DB中，但在发送方看来也算是被对方收到，只是延
	 * 迟收到而已（离线消息嘛））），否则表示应用层没有处理（如果此消息有QoS机制，则发送方在QoS重传机制超时
	 * 后报出消息发送失败的提示）
	 * @see Protocal
	 * @see #onTransferMessage4C2C(Protocal)
	 * @since 4.0
	 */
	@Override
	public boolean onTransferMessage_RealTimeSendFaild(Protocal p)
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


	/**
	 * <b>注意：</b><font color="red">本回调仅用于与Web的互通模式下，默认情况下本方法可什么也不做，无任何影响。如你对此回调有疑问可跟Jack Jiang进行技术讨论！</font>
	 * {@inheritDoc}
	 * 
	 * @since 6.2
	 */
	@Override
	public void onTransferMessage4C2C_AfterBridge(Protocal p)
	{	
		// 默认本方法可
	}
}
