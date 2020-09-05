/*
 * Copyright (C) 2020  即时通讯网(52im.net) & Jack Jiang.
 * The MobileIMSDK_TCP (MobileIMSDK v5.x TCP版) Project. 
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
 * ConfigEntity.java at 2020-8-6 14:24:51, code by Jack Jiang.
 */
package net.x52im.mobileimsdk.java.conf;

import net.x52im.mobileimsdk.java.core.KeepAliveDaemon;

public class ConfigEntity {
	public static String appKey = null;
	public static String serverIP = "192.168.0.190";// "rbcore.52im.net";
	public static int serverPort = 8901;
	public static int localUDPPort = 0;// 7801;

	public static void setSenseMode(SenseMode mode) {
		int keepAliveInterval = 0;
		int networkConnectionTimeout = 0;
		switch (mode) {
		case MODE_3S: {
			keepAliveInterval = 3000;// 3s
			networkConnectionTimeout = keepAliveInterval * 1 + 2000;// 5s
			break;
		}
		case MODE_10S:
			keepAliveInterval = 10000;// 10s
			networkConnectionTimeout = keepAliveInterval * 1 + 5000;// 15s
			break;
		case MODE_15S:
			keepAliveInterval = 15000;// 15s
			networkConnectionTimeout = keepAliveInterval * 1 + 5000;// 20s
			break;
		case MODE_30S:
			keepAliveInterval = 30000;// 30s
			networkConnectionTimeout = keepAliveInterval * 1 + 5000;// 35s
			break;
		case MODE_60S:
			keepAliveInterval = 60000;// 60s
			networkConnectionTimeout = keepAliveInterval * 1 + 5000;// 65s
			break;
		case MODE_120S:
			keepAliveInterval = 120000;// 120s
			networkConnectionTimeout = keepAliveInterval * 1 + 5000;// 125s
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
		 * * 5秒后未收到服务端心跳反馈即认为连接已断开（相当于连续2个心跳间隔(即算法最大6秒延迟后)后仍未收到服务端反馈）。
		 */
		MODE_3S,

		/**
		 * 此模式下：<br>
		 * * KeepAlive心跳问隔为10秒；<br>
		 * * 15秒后未收到服务端心跳反馈即认为连接已断开（相当于连续2个心跳间隔(即算法最大20秒延迟后)后仍未收到服务端反馈）。
		 */
		MODE_10S,

		/**
		 * 此模式下：<br>
		 * * KeepAlive心跳问隔为15秒；<br>
		 * * 20秒后未收到服务端心跳反馈即认为连接已断开（相当于连续2个心跳间隔(即算法最大30秒延迟后)后仍未收到服务端反馈）。
		 * 
		 * @since 5.0
		 */
		MODE_15S,

		/**
		 * 此模式下：<br>
		 * * KeepAlive心跳问隔为30秒；<br>
		 * * 35秒后未收到服务端心跳反馈即认为连接已断开（相当于连续2个心跳间隔(即算法最大60秒延迟后)后仍未收到服务端反馈）。
		 */
		MODE_30S,

		/**
		 * 此模式下：<br>
		 * * KeepAlive心跳问隔为60秒；<br>
		 * * 65秒后未收到服务端心跳反馈即认为连接已断开（相当于连续2个心跳间隔(即算法最大120秒延迟后)后仍未收到服务端反馈）。
		 */
		MODE_60S,

		/**
		 * 此模式下：<br>
		 * * KeepAlive心跳问隔为120秒；<br>
		 * * 125秒后未收到服务端心跳反馈即认为连接已断开（相当于连续2个心跳间隔(即算法最大240秒延迟后)后仍未收到服务端反馈）。
		 */
		MODE_120S
	}
}
