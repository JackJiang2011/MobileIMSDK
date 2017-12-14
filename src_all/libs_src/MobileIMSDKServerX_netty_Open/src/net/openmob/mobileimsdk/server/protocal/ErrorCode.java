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
 * ErrorCode.java at 2017-12-9 11:24:33, code by Jack Jiang.
 * You can contact author with jack.jiang@52im.net or jb2011@163.com.
 */
package net.openmob.mobileimsdk.server.protocal;

public interface ErrorCode
{
	int COMMON_CODE_OK = 0;
	int COMMON_NO_LOGIN = 1;
	int COMMON_UNKNOW_ERROR = 2;
	
	int COMMON_DATA_SEND_FAILD = 3;
	
	int COMMON_INVALID_PROTOCAL = 4;
	
	interface ForC
	{
		int BREOKEN_CONNECT_TO_SERVER = 201;
		int BAD_CONNECT_TO_SERVER = 202;
		int CLIENT_SDK_NO_INITIALED = 203;
		int LOCAL_NETWORK_NOT_WORKING = 204;
		int TO_SERVER_NET_INFO_NOT_SETUP = 205;
	}
	
	interface ForS
	{
		int RESPONSE_FOR_UNLOGIN = 301;
	}
}
