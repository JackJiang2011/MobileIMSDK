/*
 * Copyright (C) 2023  即时通讯网(52im.net) & Jack Jiang.
 * The MobileIMSDK v6.4 Project. 
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
 * QoS4SendDaemonS2C.java at 2023-9-21 15:24:55, code by Jack Jiang.
 */
package net.x52im.mobileimsdk.server.qos;

public class QoS4SendDaemonS2C extends QoS4SendDaemonRoot
{
	private static volatile QoS4SendDaemonS2C instance = null;
	
	public static QoS4SendDaemonS2C getInstance()
	{
		if (instance == null) {
			synchronized (QoS4SendDaemonS2C.class) {
				if (instance == null) {
					instance = new QoS4SendDaemonS2C();
				}
			}
		}
		return instance;
	}
	
	private QoS4SendDaemonS2C()
	{
		super(0, 0 , -1, true, "-本机QoS");
	}
}
