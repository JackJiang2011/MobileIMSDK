/*
 * Copyright (C) 2021  即时通讯网(52im.net) & Jack Jiang.
 * The MobileIMSDK4j_udp (MobileIMSDK4j v6.x UDP版) Project. 
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
 * ConfigEntity.java at 2021-7-6 15:54:43, code by Jack Jiang.
 */
package net.x52im.mobileimsdk.java.conf;

import net.x52im.mobileimsdk.java.core.KeepAliveDaemon;

public class ConfigEntity {
	public static String serverIP = "rbcore.52im.net";
	public static int serverPort = 7901;
	public static int localPort = 0;

	public static void setSenseMode(SenseMode mode) {
		int keepAliveInterval = 0;
		int networkConnectionTimeout = 0;
		switch (mode) {
		case MODE_3S: {
			keepAliveInterval = 3000;// 3s
			networkConnectionTimeout = 3000 * 3 + 1000;// 10s
			break;
		}
		case MODE_10S:
			keepAliveInterval = 10000;// 10s
			networkConnectionTimeout = 10000 * 2 + 1000;// 21s
			break;
		case MODE_30S:
			keepAliveInterval = 30000;// 30s
			networkConnectionTimeout = 30000 * 2 + 1000;// 61s
			break;
		case MODE_60S:
			keepAliveInterval = 60000;// 60s
			networkConnectionTimeout = 60000 * 2 + 1000;// 121s
			break;
		case MODE_120S:
			keepAliveInterval = 120000;// 120s
			networkConnectionTimeout = 120000 * 2 + 1000;// 241s
			break;
		}

		if (keepAliveInterval > 0) {
			KeepAliveDaemon.KEEP_ALIVE_INTERVAL = keepAliveInterval;
		}
		if (networkConnectionTimeout > 0) {
			KeepAliveDaemon.NETWORK_CONNECTION_TIME_OUT = networkConnectionTimeout;
		}
	}

	public enum SenseMode {
		/**
		 * 此模式下：<br>
		 * * KeepAlive心跳问隔为3秒；<br>
		 * * 10秒后未收到服务端心跳反馈即认为连接已断开（相当于连续3 个心跳间隔后仍未收到服务端反馈）。
		 */
		MODE_3S,

		/**
		 * 此模式下：<br>
		 * * KeepAlive心跳问隔为10秒；<br>
		 * * 21秒后未收到服务端心跳反馈即认为连接已断开（相当于连续2 个心跳间隔后仍未收到服务端反馈）。
		 */
		MODE_10S,

		/**
		 * 此模式下：<br>
		 * * KeepAlive心跳问隔为30秒；<br>
		 * * 61秒后未收到服务端心跳反馈即认为连接已断开（相当于连续2 个心跳间隔后仍未收到服务端反馈）。
		 */
		MODE_30S,

		/**
		 * 此模式下：<br>
		 * * KeepAlive心跳问隔为60秒；<br>
		 * * 121秒后未收到服务端心跳反馈即认为连接已断开（相当于连续2 个心跳间隔后仍未收到服务端反馈）。
		 */
		MODE_60S,

		/**
		 * 此模式下：<br>
		 * * KeepAlive心跳问隔为120秒；<br>
		 * * 241秒后未收到服务端心跳反馈即认为连接已断开（相当于连续2 个心跳间隔后仍未收到服务端反馈）。
		 */
		MODE_120S
	}
}
