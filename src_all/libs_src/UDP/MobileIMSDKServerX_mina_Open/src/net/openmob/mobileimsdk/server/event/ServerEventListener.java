/*
 * Copyright (C) 2020  即时通讯网(52im.net) & Jack Jiang.
 * The MobileIMSDK_X_netty (MobileIMSDK v4.x MINA版) Project. 
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
 * ServerEventListener.java at 2020-4-14 18:50:34, code by Jack Jiang.
 */
package net.openmob.mobileimsdk.server.event;

import net.openmob.mobileimsdk.server.protocal.Protocal;

import org.apache.mina.core.session.IoSession;

public interface ServerEventListener
{
	public int onVerifyUserCallBack(String userId, String token, String extra, IoSession session);
	
	public void onUserLoginAction_CallBack(String userId, String extra, IoSession session);
	
	public void onUserLogoutAction_CallBack(String userId, Object obj, IoSession session);
	
//	public boolean onTransBuffer_CallBack(String userId, String from_user_id
//			, String dataContent, String fingerPrint, int typeu, IoSession session);
	public boolean onTransBuffer_C2S_CallBack(Protocal p, IoSession session);
	
//	public void onTransBuffer_C2C_CallBack(String userId, String from_user_id
//			, String dataContent, String fingerPrint, int typeu);
	public void onTransBuffer_C2C_CallBack(Protocal p);
	
//	public boolean onTransBuffer_C2C_RealTimeSendFaild_CallBack(String userId
//			, String from_user_id, String dataContent
//			, String fingerPrint, int typeu);
	public boolean onTransBuffer_C2C_RealTimeSendFaild_CallBack(Protocal p);
}
