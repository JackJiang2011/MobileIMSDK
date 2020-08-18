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
 * ServerEventListener.java at 2017-5-2 15:49:28, code by Jack Jiang.
 * You can contact author with jack.jiang@52im.net or jb2011@163.com.
 */
package net.openmob.mobileimsdk.server.event;

import org.apache.mina.core.session.IoSession;

public interface ServerEventListener
{
	public int onVerifyUserCallBack(String userId, String token, String extra, IoSession session);
	public void onUserLoginAction_CallBack(String userId, String extra, IoSession session);
	public void onUserLogoutAction_CallBack(String userId, Object obj, IoSession session);
	public boolean onTransBuffer_CallBack(String userId, String from_user_id
			, String dataContent, String fingerPrint, int typeu, IoSession session);
	public void onTransBuffer_C2C_CallBack(String userId, String from_user_id
			, String dataContent, String fingerPrint, int typeu);
	public boolean onTransBuffer_C2C_RealTimeSendFaild_CallBack(String userId
			, String from_user_id, String dataContent
			, String fingerPrint, int typeu);
}
