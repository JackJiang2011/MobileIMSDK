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
 * Gateway.java at 2023-9-21 15:24:55, code by Jack Jiang.
 */
package net.x52im.mobileimsdk.server.network;

import io.netty.channel.Channel;
import io.netty.util.AttributeKey;
import net.x52im.mobileimsdk.server.ServerCoreHandler;

public abstract class Gateway
{
	public final static String SOCKET_TYPE_IN_CHANNEL_ATTRIBUTE = "__socket_type__";
	public static final AttributeKey<Integer> SOCKET_TYPE_IN_CHANNEL_ATTRIBUTE_ATTR = AttributeKey.newInstance(SOCKET_TYPE_IN_CHANNEL_ATTRIBUTE);
	
	public static final int SOCKET_TYPE_UDP       = 0x0001; 
	public static final int SOCKET_TYPE_TCP       = 0x0002; 
	public static final int SOCKET_TYPE_WEBSOCKET = 0x0004; 
	
	public abstract void init(ServerCoreHandler serverCoreHandler);
	public abstract void bind() throws Exception;
	public abstract void shutdown();
	
	public static void setSocketType(Channel c, int socketType)
	{
		c.attr(SOCKET_TYPE_IN_CHANNEL_ATTRIBUTE_ATTR).set(socketType);
	}
	
	public static void removeSocketType(Channel c)
	{
		c.attr(SOCKET_TYPE_IN_CHANNEL_ATTRIBUTE_ATTR).set(null);
	}
	
	public static int getSocketType(Channel c)
	{
		Integer socketType = c.attr(SOCKET_TYPE_IN_CHANNEL_ATTRIBUTE_ATTR).get();
		if(socketType != null)
			return socketType.intValue();
		return -1;
	}
	
	public static boolean isSupportUDP(int support)
	{
		// 位运算
		return (support & SOCKET_TYPE_UDP) == SOCKET_TYPE_UDP;
	}
	
	public static boolean isSupportTCP(int support)
	{
		// 位运算
		return (support & SOCKET_TYPE_TCP) == SOCKET_TYPE_TCP;
	}
	
	public static boolean isSupportWebSocket(int support)
	{
		// 位运算
		return (support & SOCKET_TYPE_WEBSOCKET) == SOCKET_TYPE_WEBSOCKET;
	}

	public static boolean isTCPChannel(Channel c)
	{
//		return (c != null && c instanceof NioSocketChannel);
		return (c != null && getSocketType(c) == SOCKET_TYPE_TCP);
	}

	public static boolean isUDPChannel(Channel c)
	{
//		return (c != null && c instanceof MBUDPChannel);
		return (c != null && getSocketType(c) == SOCKET_TYPE_UDP);
	}
	
	public static boolean isWebSocketChannel(Channel c)
	{
		return (c != null && getSocketType(c) == SOCKET_TYPE_WEBSOCKET);
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
		else if(Gateway.isTCPChannel(c))
			return "tcp";
		else if(Gateway.isWebSocketChannel(c))
			return "websocket";
		else 
			return "unknow";
	}
}
