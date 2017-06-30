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
 * QoS4SendDaemonS2C.java at 2017-5-2 15:49:27, code by Jack Jiang.
 * You can contact author with jack.jiang@52im.net or jb2011@163.com.
 */
package net.openmob.mobileimsdk.server.qos;

public class QoS4SendDaemonS2C extends QoS4SendDaemonRoot
{
	private static QoS4SendDaemonS2C instance = null;
	
	public static QoS4SendDaemonS2C getInstance()
	{
		if(instance == null)
			instance = new QoS4SendDaemonS2C();
		return instance;
	}
	
	private QoS4SendDaemonS2C()
	{
		super(0 // QoS质量保证线程心跳间隔（单位：毫秒），本参数<=0表示使用父类的默认值
			, 0 // “刚刚”发出的消息阀值定义（单位：毫秒），本参数<=0表示使用父类的默认值
			, -1// 一个包允许的最大重发次数，本参数<0表示使用父类的默认值
			, true
			, "-本机QoS");
	}
}
