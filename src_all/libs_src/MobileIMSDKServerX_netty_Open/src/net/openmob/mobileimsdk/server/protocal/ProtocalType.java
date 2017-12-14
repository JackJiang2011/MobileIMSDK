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
 * ProtocalType.java at 2017-12-9 11:24:33, code by Jack Jiang.
 * You can contact author with jack.jiang@52im.net or jb2011@163.com.
 */
package net.openmob.mobileimsdk.server.protocal;

public interface ProtocalType
{
	//------------------------------------------------------- from client
	public interface C
	{
		int FROM_CLIENT_TYPE_OF_LOGIN = 0;
		int FROM_CLIENT_TYPE_OF_KEEP$ALIVE = 1;
		int FROM_CLIENT_TYPE_OF_COMMON$DATA = 2;
		int FROM_CLIENT_TYPE_OF_LOGOUT = 3;
		
		int FROM_CLIENT_TYPE_OF_RECIVED = 4;
		
		int FROM_CLIENT_TYPE_OF_ECHO = 5;
	}
	
	//------------------------------------------------------- from server
	public interface S
	{
		int FROM_SERVER_TYPE_OF_RESPONSE$LOGIN = 50;
		int FROM_SERVER_TYPE_OF_RESPONSE$KEEP$ALIVE = 51;
		
		int FROM_SERVER_TYPE_OF_RESPONSE$FOR$ERROR = 52;
		
		int FROM_SERVER_TYPE_OF_RESPONSE$ECHO = 53;
	}
}
