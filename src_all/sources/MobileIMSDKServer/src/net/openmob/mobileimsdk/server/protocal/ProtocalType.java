/*
 * Copyright (C) 2016 即时通讯网(52im.net) The MobileIMSDK Project. 
 * All rights reserved.
 * Project URL:https://github.com/JackJiang2011/MobileIMSDK
 *  
 * 即时通讯网(52im.net) - 即时通讯技术社区! PROPRIETARY/CONFIDENTIAL.
 * Use is subject to license terms.
 * 
 * ProtocalType.java at 2016-2-20 11:26:02, code by Jack Jiang.
 * You can contact author with jack.jiang@52im.net or jb2011@163.com.
 */
package net.openmob.mobileimsdk.server.protocal;

public interface ProtocalType
{
	interface C
	{
		int FROM_CLIENT_TYPE_OF_LOGIN = 0;
		int FROM_CLIENT_TYPE_OF_KEEP$ALIVE = 1;
		int FROM_CLIENT_TYPE_OF_COMMON$DATA = 2;
		int FROM_CLIENT_TYPE_OF_LOGOUT = 3;
		int FROM_CLIENT_TYPE_OF_RECIVED = 4;
		int FROM_CLIENT_TYPE_OF_ECHO = 5;
	}

	interface S
	{
		int FROM_SERVER_TYPE_OF_RESPONSE$LOGIN = 50;
		int FROM_SERVER_TYPE_OF_RESPONSE$KEEP$ALIVE = 51;
		int FROM_SERVER_TYPE_OF_RESPONSE$FOR$ERROR = 52;
		int FROM_SERVER_TYPE_OF_RESPONSE$ECHO = 53;
	}
}