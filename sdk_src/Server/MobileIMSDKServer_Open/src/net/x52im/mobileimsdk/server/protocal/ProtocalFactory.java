/*
 * Copyright (C) 2021  即时通讯网(52im.net) & Jack Jiang.
 * The MobileIMSDK v6.x Project. 
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
 * ProtocalFactory.java at 2021-6-29 10:15:36, code by Jack Jiang.
 */
package net.x52im.mobileimsdk.server.protocal;

import net.x52im.mobileimsdk.server.protocal.c.PKeepAlive;
import net.x52im.mobileimsdk.server.protocal.c.PLoginInfo;
import net.x52im.mobileimsdk.server.protocal.s.PErrorResponse;
import net.x52im.mobileimsdk.server.protocal.s.PKeepAliveResponse;
import net.x52im.mobileimsdk.server.protocal.s.PKickoutInfo;
import net.x52im.mobileimsdk.server.protocal.s.PLoginInfoResponse;

import com.google.gson.Gson;

public class ProtocalFactory
{
	private static String create(Object c)
	{
		return new Gson().toJson(c);
	}
	
	public static <T> T parse(byte[] fullProtocalJSONBytes, int len, Class<T> clazz)
	{
		return parse(CharsetHelper.getString(fullProtocalJSONBytes, len), clazz);
	}
	
	public static <T> T parse(String dataContentOfProtocal, Class<T> clazz)
	{
		return new Gson().fromJson(dataContentOfProtocal, clazz);
	}
	
	public static Protocal parse(byte[] fullProtocalJSONBytes, int len)
	{
		return parse(fullProtocalJSONBytes, len, Protocal.class);
	}
	
	public static Protocal createPKeepAliveResponse(String to_user_id)
	{
		return new Protocal(ProtocalType.S.FROM_SERVER_TYPE_OF_RESPONSE$KEEP$ALIVE, create(new PKeepAliveResponse()), "0", to_user_id);
	}
	
	public static PKeepAliveResponse parsePKeepAliveResponse(String dataContentOfProtocal)
	{
		return parse(dataContentOfProtocal, PKeepAliveResponse.class);
	}
	
	public static Protocal createPKeepAlive(String from_user_id)
	{
		return new Protocal(ProtocalType.C.FROM_CLIENT_TYPE_OF_KEEP$ALIVE, create(new PKeepAlive()), from_user_id, "0");
	}
	
	public static PKeepAlive parsePKeepAlive(String dataContentOfProtocal)
	{
		return parse(dataContentOfProtocal, PKeepAlive.class);
	}
	
	public static Protocal createPErrorResponse(int errorCode, String errorMsg, String user_id)
	{
		return new Protocal(ProtocalType.S.FROM_SERVER_TYPE_OF_RESPONSE$FOR$ERROR, create(new PErrorResponse(errorCode, errorMsg)), "0", user_id);
	}
	
	public static PErrorResponse parsePErrorResponse(String dataContentOfProtocal)
	{
		return parse(dataContentOfProtocal, PErrorResponse.class);
	}
	
	public static Protocal createPLoginoutInfo(String user_id)
	{
		return new Protocal(ProtocalType.C.FROM_CLIENT_TYPE_OF_LOGOUT, null, user_id, "0");
	}
	
	public static Protocal createPLoginInfo(PLoginInfo loginInfo)
	{
		return new Protocal(ProtocalType.C.FROM_CLIENT_TYPE_OF_LOGIN, create(loginInfo), loginInfo.getLoginUserId(), "0");
	}
	
	public static PLoginInfo parsePLoginInfo(String dataContentOfProtocal)
	{
		return parse(dataContentOfProtocal, PLoginInfo.class);
	}
	
	public static Protocal createPLoginInfoResponse(int code, long firstLoginTime, String user_id)
	{
		return new Protocal(ProtocalType.S.FROM_SERVER_TYPE_OF_RESPONSE$LOGIN, create(new PLoginInfoResponse(code, firstLoginTime)), "0", user_id, false, null); 
	}
	
	public static PLoginInfoResponse parsePLoginInfoResponse(String dataContentOfProtocal)
	{
		return parse(dataContentOfProtocal, PLoginInfoResponse.class);
	}
	
	public static Protocal createCommonData(String dataContent, String from_user_id, String to_user_id, boolean QoS, String fingerPrint)
	{
		return createCommonData(dataContent, from_user_id, to_user_id, QoS, fingerPrint, -1);
	}
	
	public static Protocal createCommonData(String dataContent, String from_user_id, String to_user_id, boolean QoS, String fingerPrint, int typeu)
	{
		return new Protocal(ProtocalType.C.FROM_CLIENT_TYPE_OF_COMMON$DATA, dataContent, from_user_id, to_user_id, QoS, fingerPrint, typeu);
	}
	
	public static Protocal createRecivedBack(String from_user_id, String to_user_id, String recievedMessageFingerPrint)
	{
		return createRecivedBack(from_user_id, to_user_id, recievedMessageFingerPrint, false);
	}
	
	public static Protocal createRecivedBack(String from_user_id, String to_user_id, String recievedMessageFingerPrint, boolean bridge)
	{
		Protocal p = new Protocal(ProtocalType.C.FROM_CLIENT_TYPE_OF_RECIVED, recievedMessageFingerPrint, from_user_id, to_user_id);
		p.setBridge(bridge);
		return p;
	}
	
	public static Protocal createPKickout(String to_user_id, int code, String reason)
	{
		return new Protocal(ProtocalType.S.FROM_SERVER_TYPE_OF_KICKOUT, create(new PKickoutInfo(code, reason)), "0", to_user_id);
	}
	
	public static PKickoutInfo parsePKickoutInfo(String dataContentOfProtocal)
	{
		return parse(dataContentOfProtocal, PKickoutInfo.class);
	}
}
