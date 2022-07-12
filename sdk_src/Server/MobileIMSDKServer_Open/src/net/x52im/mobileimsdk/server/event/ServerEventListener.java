/*
 * Copyright (C) 2022  即时通讯网(52im.net) & Jack Jiang.
 * The MobileIMSDK v6.1 Project. 
 * All rights reserved.
 * 
 * > Github地址：https://github.com/JackJiang2011/MobileIMSDK
 * > 文档地址：  http://www.52im.net/forum-89-1.html
 * > 技术社区：  http://www.52im.net/
 * > 技术交流群：320837163 (http://www.52im.net/topic-qqgroup.html)
 * > 作者公众号：“【即时通讯技术圈】”，欢迎关注！
 * > 联系作者：  http://www.52im.net/thread-2792-1-1.html
 *  
 * "即时通讯网(52im.net) - 即时通讯开发者社区!" 推荐开源工程。
 * 
 * ServerEventListener.java at 2022-7-12 16:35:57, code by Jack Jiang.
 */
package net.x52im.mobileimsdk.server.event;

import io.netty.channel.Channel;
import net.x52im.mobileimsdk.server.protocal.Protocal;

public interface ServerEventListener
{
	int onUserLoginVerify(String userId, String token, String extra, Channel session);
	void onUserLoginSucess(String userId, String extra, Channel session);
	void onUserLogout(String userId, Channel session, int beKickoutCode);
	
	boolean onTransferMessage4C2SBefore(Protocal p, Channel session);
	boolean onTransferMessage4C2CBefore(Protocal p, Channel session);
	
	boolean onTransferMessage4C2S(Protocal p, Channel session);
	void onTransferMessage4C2C(Protocal p);
	boolean onTransferMessage_RealTimeSendFaild(Protocal p);
	void onTransferMessage4C2C_AfterBridge(Protocal p);
}
