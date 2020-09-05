/*
 * Copyright (C) 2020  即时通讯网(52im.net) & Jack Jiang.
 * The MobileIMSDK v5.x Project. 
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
 * Gateway.java at 2020-8-22 16:00:59, code by Jack Jiang.
 */
package net.x52im.mobileimsdk.server.network;

import io.netty.channel.Channel;
import io.netty.channel.socket.nio.NioSocketChannel;
import net.x52im.mobileimsdk.server.ServerCoreHandler;
import net.x52im.mobileimsdk.server.network.udp.MBUDPChannel;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class Gateway
{
	private static Logger logger = LoggerFactory.getLogger(ServerCoreHandler.class);  
	
	public static final int SUPPORT_UDP = 0x0001; // 即2进制：0000 0001
	public static final int SUPPORT_TCP = 0x0002; // 即2进制：0000 0010
	
	public abstract void init(ServerCoreHandler serverCoreHandler);
	public abstract void bind() throws Exception;
	public abstract void shutdown();
	
	public static boolean isSupportUDP(int support)
	{
		// 位运算
		return (support & SUPPORT_UDP) == SUPPORT_UDP;
	}
	
	public static boolean isSupportTCP(int support)
	{
		// 位运算
		return (support & SUPPORT_TCP) == SUPPORT_TCP;
	}

	public static boolean isTCPChannel(Channel c)
	{
		return (c != null && c instanceof NioSocketChannel);
	}

	public static boolean isUDPChannel(Channel c)
	{
		return (c != null && c instanceof MBUDPChannel);
	}
	
	public static String $(Channel c)
	{
		return getGatewayFlag(c);
	}
	
	public static String getGatewayFlag(Channel c)
	{
//		logger.info(">>>>>> c.class="+c.getClass().getName());
		if(Gateway.isUDPChannel(c))
			return "udp";
		else 
			return "tcp";
	}
}
