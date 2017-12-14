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
 * QoS4ReciveDaemonC2B.java at 2017-12-9 11:24:33, code by Jack Jiang.
 * You can contact author with jack.jiang@52im.net or jb2011@163.com.
 */
package net.nettime.mobileimsdk.server.bridge;

import net.openmob.mobileimsdk.server.qos.QoS4ReciveDaemonRoot;

public class QoS4ReciveDaemonC2B extends QoS4ReciveDaemonRoot
{
	private static QoS4ReciveDaemonC2B instance = null;
	
	public static QoS4ReciveDaemonC2B getInstance()
	{
		if(instance == null)
			instance = new QoS4ReciveDaemonC2B();
		return instance;
	}
	
	public QoS4ReciveDaemonC2B()
	{
		super(5 * 1000  
			, 15 * 1000 
			, true
			, "-桥接QoS！");
	}
}