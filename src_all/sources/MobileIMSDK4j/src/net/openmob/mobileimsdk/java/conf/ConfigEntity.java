/*
 * Copyright (C) 2015 Jack Jiang The MobileIMSDK Project. 
 * All rights reserved.
 * Project URL:https://github.com/JackJiang2011/MobileIMSDK
 *  
 * openmob.net PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 * 
 * Archived at 2015-11-27 14:02:01, code by Jack Jiang.
 * You can contact author with jack.jiang@openmob.net or jb2011@163.com.
 */
package net.openmob.mobileimsdk.java.conf;

import net.openmob.mobileimsdk.java.core.KeepAliveDaemon;

public class ConfigEntity
{
	public static String appKey = null;

	public static String serverIP = "rbcore.openmob.net";

	public static int serverUDPPort = 7901;

	public static int localUDPPort = 0;

	public static void setSenseMode(SenseMode mode)
	{
		int keepAliveInterval = 0;
		int networkConnectionTimeout = 0;
		switch (mode)
		{
		case MODE_3S:
		{
			// 心跳间隔3秒
			keepAliveInterval = 3000;// 3s
			// 10秒后未收到服务端心跳反馈即认为连接已断开（相当于连续3 个心跳间隔后仍未收到服务端反馈）
			networkConnectionTimeout = 3000 * 3 + 1000;// 10s
			break;
		}
		case MODE_10S:
			// 心跳间隔10秒
			keepAliveInterval = 10000;// 10s
			// 10秒后未收到服务端心跳反馈即认为连接已断开（相当于连续2 个心跳间隔后仍未收到服务端反馈）
			networkConnectionTimeout = 10000 * 2 + 1000;// 21s
			break;
		case MODE_30S:
			// 心跳间隔30秒
			keepAliveInterval = 30000;// 30s
			// 10秒后未收到服务端心跳反馈即认为连接已断开（相当于连续2 个心跳间隔后仍未收到服务端反馈）
			networkConnectionTimeout = 30000 * 2 + 1000;// 61s
			break;
		case MODE_60S:
			// 心跳间隔60秒
			keepAliveInterval = 60000;// 60s
			// 10秒后未收到服务端心跳反馈即认为连接已断开（相当于连续2 个心跳间隔后仍未收到服务端反馈）
			networkConnectionTimeout = 60000 * 2 + 1000;// 121s
			break;
		case MODE_120S:
			// 心跳间隔120秒
			keepAliveInterval = 120000;// 120s
			// 10秒后未收到服务端心跳反馈即认为连接已断开（相当于连续2 个心跳间隔后仍未收到服务端反馈）
			networkConnectionTimeout = 120000 * 2 + 1000;// 241s
			break;
		}

		if(keepAliveInterval > 0)
		{
			// 设置Kepp alive心跳间隔
			KeepAliveDaemon.KEEP_ALIVE_INTERVAL = keepAliveInterval;
		}
		if(networkConnectionTimeout > 0)
		{
			// 设置与服务端掉线的超时时长
			KeepAliveDaemon.NETWORK_CONNECTION_TIME_OUT = networkConnectionTimeout;
		}
	}

	public static enum SenseMode
	{
		MODE_3S, 

		MODE_10S, 

		MODE_30S, 

		MODE_60S, 

		MODE_120S;
	}
}