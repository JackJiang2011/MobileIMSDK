/*
 * Copyright (C) 2020  即时通讯网(52im.net) & Jack Jiang.
 * The MobileIMSDK_X_netty (MobileIMSDK v4.x Netty版) Project. 
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
 * QoS4SendDaemonB2C.java at 2020-4-14 17:24:14, code by Jack Jiang.
 */
package net.nettime.mobileimsdk.server.bridge;

import net.openmob.mobileimsdk.server.qos.QoS4SendDaemonRoot;

public class QoS4SendDaemonB2C extends QoS4SendDaemonRoot
{
	private static QoS4SendDaemonB2C instance = null;
	
	public static QoS4SendDaemonB2C getInstance()
	{
		if(instance == null)
			instance = new QoS4SendDaemonB2C();
		return instance;
	}
	
	private QoS4SendDaemonB2C()
	{
		super(3000    
			, 2 * 1000 
			, -1       
			, true
			, "-桥接QoS！");
	}
}
