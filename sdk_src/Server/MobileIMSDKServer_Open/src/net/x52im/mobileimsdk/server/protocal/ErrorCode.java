/*
 * Copyright (C) 2022  即时通讯网(52im.net) & Jack Jiang.
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
 * ErrorCode.java at 2022-7-12 16:35:57, code by Jack Jiang.
 */
package net.x52im.mobileimsdk.server.protocal;

/**
 * 错误码常量表.<br>
 * <b>建议0~1024范围内的错误码作为Im核心框架保留，业务层请使用>1024的码表示！
 * 
 * @author Jack Jiang(http://www.52im.net/thread-2792-1-1.html)
 * @version 1.0
 */
public interface ErrorCode
{
	/** 一切正常 */
	int COMMON_CODE_OK = 0;
	/** 客户端尚未登陆 */
	int COMMON_NO_LOGIN = 1;
	/** 未知错误 */
	int COMMON_UNKNOW_ERROR = 2;
	
	/** 数据发送失败 */
	int COMMON_DATA_SEND_FAILD = 3;
	
	/** 无效的 {@link Protocal}对象 */
	int COMMON_INVALID_PROTOCAL = 4;
	
	/** 由客户端产生的错误码 */
	interface ForC
	{
		/** 与服务端的连接已断开 */
		int BREOKEN_CONNECT_TO_SERVER = 201;
		
		/** 与服务端的网络连接失败 */
		int BAD_CONNECT_TO_SERVER = 202;
		
		/** 客户端SDK尚未初始化 */
		int CLIENT_SDK_NO_INITIALED = 203;
		
		/** 本地网络不可用（未打开） */
		int LOCAL_NETWORK_NOT_WORKING = 204;
		
		/** 要连接的服务端网络参数未设置 */
		int TO_SERVER_NET_INFO_NOT_SETUP = 205;
	}
	
	/** 由服务端产生的错误码 */
	interface ForS
	{
		/** 客户端尚未登陆，请重新登陆 */
		int RESPONSE_FOR_UNLOGIN = 301;
	}
}
