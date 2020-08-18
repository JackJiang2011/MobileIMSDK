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
 * QoS4ReciveDaemonC2B.java at 2017-5-2 15:49:28, code by Jack Jiang.
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
		super(5 * 1000  //【1】检查线程执行间隔（单位：毫秒），本参数<=0表示使用父类的默认值
			, 15 * 1000 //【2】一个消息放到在列表中（用于判定重复时使用）的生存时长（单位：毫秒），本参数<=0表示使用父类的默认值
			, true
			, "-桥接QoS！");
	}
}