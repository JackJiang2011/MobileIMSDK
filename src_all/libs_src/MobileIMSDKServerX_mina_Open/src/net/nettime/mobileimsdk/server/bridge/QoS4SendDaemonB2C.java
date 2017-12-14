/*
 * Copyright (C) 2017  即时通讯网(52im.net) & Jack Jiang.
 * The MobileIMSDK_X (MobileIMSDK v3.x) Project. 
 * All rights reserved.
 * 
 * > Github地址: https://github.com/JackJiang2011/MobileIMSDK
 * > 文档地址: http://www.52im.net/forum-89-1.html
 * > 即时通讯技术社区：http://www.52im.net/
 * > 即时通讯技术交流群：320837163 (http://www.52im.net/topic-qqgroup.html)
 *  
 * "即时通讯网(52im.net) - 即时通讯开发者社区!" 推荐开源工程。
 * 
 * QoS4SendDaemonB2C.java at 2017-5-2 15:49:28, code by Jack Jiang.
 * You can contact author with jack.jiang@52im.net or jb2011@163.com.
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
