/*
 * Copyright (C) 2020  即时通讯网(52im.net) & Jack Jiang.
 * The MobileIMSDK_X_netty (MobileIMSDK v4.x MINA版) Project. 
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
 * ProtocalFactory.java at 2020-4-14 18:50:34, code by Jack Jiang.
 */
package net.openmob.mobileimsdk.server.protocal;

import net.openmob.mobileimsdk.server.protocal.c.PKeepAlive;
import net.openmob.mobileimsdk.server.protocal.c.PLoginInfo;
import net.openmob.mobileimsdk.server.protocal.s.PErrorResponse;
import net.openmob.mobileimsdk.server.protocal.s.PKeepAliveResponse;
import net.openmob.mobileimsdk.server.protocal.s.PLoginInfoResponse;

import com.google.gson.Gson;

public class ProtocalFactory
{
	private static String create(Object c)
	{
		return new Gson().toJson(c);
	}
	
//	public static <T> T parse(byte[] fullProtocalJASOnBytes, int len, Class<T> clazz)
//	{
//		return parse(CharsetHelper.getString(fullProtocalJASOnBytes, len), clazz);
//	}
	
	public static <T> T parse(String dataContentOfProtocal, Class<T> clazz)
	{
		return new Gson().fromJson(dataContentOfProtocal, clazz);
	}
	
//	public static Protocal parse(byte[] fullProtocalJASOnBytes, int len)
//	{
//		return parse(fullProtocalJASOnBytes, len, Protocal.class);
//	}
	
	public static Protocal createPKeepAliveResponse(String to_user_id)
	{
		return new Protocal(ProtocalType.S.FROM_SERVER_TYPE_OF_RESPONSE$KEEP$ALIVE
				, create(new PKeepAliveResponse()), "0", to_user_id);
	}
	
	public static PKeepAliveResponse parsePKeepAliveResponse(String dataContentOfProtocal)
	{
		return parse(dataContentOfProtocal, PKeepAliveResponse.class);
	}
	
	public static Protocal createPKeepAlive(String from_user_id)
	{
		return new Protocal(ProtocalType.C.FROM_CLIENT_TYPE_OF_KEEP$ALIVE
				, create(new PKeepAlive()), from_user_id, "0");
	}
	
	public static PKeepAlive parsePKeepAlive(String dataContentOfProtocal)
	{
		return parse(dataContentOfProtocal, PKeepAlive.class);
	}
	
	public static Protocal createPErrorResponse(int errorCode, String errorMsg, String user_id)
	{
		return new Protocal(ProtocalType.S.FROM_SERVER_TYPE_OF_RESPONSE$FOR$ERROR
				, create(new PErrorResponse(errorCode, errorMsg)), "0", user_id);
	}
	
	public static PErrorResponse parsePErrorResponse(String dataContentOfProtocal)
	{
		return parse(dataContentOfProtocal, PErrorResponse.class);
	}
	
	public static Protocal createPLoginoutInfo(String user_id)
	{
		return new Protocal(ProtocalType.C.FROM_CLIENT_TYPE_OF_LOGOUT
				, null
				, user_id, "0");
	}
	
	public static Protocal createPLoginInfo(String userId, String token, String extra)
	{
		return new Protocal(ProtocalType.C.FROM_CLIENT_TYPE_OF_LOGIN
				, create(new PLoginInfo(userId, token, extra))
//					, "-1"
					, userId
					, "0");
	}
	
	public static PLoginInfo parsePLoginInfo(String dataContentOfProtocal)
	{
		return parse(dataContentOfProtocal, PLoginInfo.class);
	}
	
	public static Protocal createPLoginInfoResponse(int code
			, String user_id)
	{
		return new Protocal(ProtocalType.S.FROM_SERVER_TYPE_OF_RESPONSE$LOGIN
				, create(new PLoginInfoResponse(code
						))
				, "0"
				, user_id // changed -1 to user_id: modified by Jack Jiang 20150911 -> 目的是让登陆响应包能正常支持QoS机制
				, true, Protocal.genFingerPrint()// add QoS support by Jack Jiang 20150911
				); 
	}
	
	public static PLoginInfoResponse parsePLoginInfoResponse(String dataContentOfProtocal)
	{
		return parse(dataContentOfProtocal, PLoginInfoResponse.class);
	}
	
	public static Protocal createCommonData(String dataContent, String from_user_id, String to_user_id
			, boolean QoS, String fingerPrint)
	{
		return createCommonData(dataContent, from_user_id, to_user_id, QoS, fingerPrint, -1);
	}
	
	public static Protocal createCommonData(String dataContent, String from_user_id, String to_user_id
			, boolean QoS, String fingerPrint, int typeu)
	{
		return new Protocal(ProtocalType.C.FROM_CLIENT_TYPE_OF_COMMON$DATA
				, dataContent, from_user_id, to_user_id, QoS, fingerPrint, typeu);
	}
	
	public static Protocal createRecivedBack(String from_user_id, String to_user_id
			, String recievedMessageFingerPrint)
	{
		return createRecivedBack(from_user_id, to_user_id, recievedMessageFingerPrint, false);
	}
	
	public static Protocal createRecivedBack(String from_user_id, String to_user_id
			, String recievedMessageFingerPrint, boolean bridge)
	{
		Protocal p = new Protocal(ProtocalType.C.FROM_CLIENT_TYPE_OF_RECIVED
				, recievedMessageFingerPrint, from_user_id, to_user_id);// 该包当然不需要QoS支持！
		p.setBridge(bridge);
		return p;
	}
}
