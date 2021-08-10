/*
 * Copyright (C) 2021  即时通讯网(52im.net) & Jack Jiang.
 * The MobileIMSDK v6.1 Project. 
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
 * ProtocalType.java at 2021-8-4 21:24:15, code by Jack Jiang.
 */
package net.x52im.mobileimsdk.server.protocal;

/**
 * MobileIMSDK核心框架级的协议类型.
 * <p>
 * 这些协议类型由框架算法决定其意义和用途，不建议用户自行使用，用户
 * 自定义协议类型请参见 {@link Protocal} 类中的 typeu 字段。
 * 
 * @author Jack Jiang(http://www.52im.net/thread-2792-1-1.html)
 * @version 1.0
 */
public interface ProtocalType
{
	//------------------------------------------------------- from client
	public interface C
	{
		/** 由客户端发出 - 协议类型：客户端登陆 */
		int FROM_CLIENT_TYPE_OF_LOGIN = 0;
		/** 由客户端发出 - 协议类型：心跳包 */
		int FROM_CLIENT_TYPE_OF_KEEP$ALIVE = 1;
		/** 由客户端发出 - 协议类型：发送通用数据 */
		int FROM_CLIENT_TYPE_OF_COMMON$DATA = 2;
		/** 由客户端发出 - 协议类型：客户端退出登陆 */
		int FROM_CLIENT_TYPE_OF_LOGOUT = 3;
		
		/** 由客户端发出 - 协议类型：QoS保证机制中的消息应答包（目前只支持客户端间的QoS机制哦） */
		int FROM_CLIENT_TYPE_OF_RECIVED = 4;
		
		/** 由客户端发出 - 协议类型：C2S时的回显指令（此指令目前仅用于测试时） */
		int FROM_CLIENT_TYPE_OF_ECHO = 5;
	}
	
	//------------------------------------------------------- from server
	public interface S
	{
		/** 由服务端发出 - 协议类型：响应客户端的登陆 */
		int FROM_SERVER_TYPE_OF_RESPONSE$LOGIN = 50;
		/** 由服务端发出 - 协议类型：响应客户端的心跳包 */
		int FROM_SERVER_TYPE_OF_RESPONSE$KEEP$ALIVE = 51;
		
		/** 由服务端发出 - 协议类型：反馈给客户端的错误信息 */
		int FROM_SERVER_TYPE_OF_RESPONSE$FOR$ERROR = 52;
		
		/** 由服务端发出 - 协议类型：反馈回显指令给客户端 */
		int FROM_SERVER_TYPE_OF_RESPONSE$ECHO = 53;
		
		/** 由服务端发出 - 协议类型：向客户端发出“被踢”指令 */
		int FROM_SERVER_TYPE_OF_KICKOUT = 54;
	}
}
