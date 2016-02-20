/*
 * Copyright (C) 2016 即时通讯网(52im.net) The MobileIMSDK Project. 
 * All rights reserved.
 * Project URL:https://github.com/JackJiang2011/MobileIMSDK
 *  
 * 即时通讯网(52im.net) - 即时通讯技术社区! PROPRIETARY/CONFIDENTIAL.
 * Use is subject to license terms.
 * 
 * ProtocalFactory.java at 2016-2-20 11:26:02, code by Jack Jiang.
 * You can contact author with jack.jiang@52im.net or jb2011@163.com.
 */
package net.openmob.mobileimsdk.server.protocal;

import com.google.gson.Gson;
import net.openmob.mobileimsdk.server.protocal.c.PKeepAlive;
import net.openmob.mobileimsdk.server.protocal.c.PLoginInfo;
import net.openmob.mobileimsdk.server.protocal.s.PErrorResponse;
import net.openmob.mobileimsdk.server.protocal.s.PKeepAliveResponse;
import net.openmob.mobileimsdk.server.protocal.s.PLoginInfoResponse;

public class ProtocalFactory
{
	private static String create(Object c)
	{
		return new Gson().toJson(c);
	}

	public static <T> T parse(byte[] fullProtocalJASOnBytes, int len, Class<T> clazz)
	{
		return parse(CharsetHelper.getString(fullProtocalJASOnBytes, len), clazz);
	}

	public static <T> T parse(String dataContentOfProtocal, Class<T> clazz)
	{
		return new Gson().fromJson(dataContentOfProtocal, clazz);
	}

	public static Protocal parse(byte[] fullProtocalJASOnBytes, int len)
	{
		return (Protocal)parse(fullProtocalJASOnBytes, len, Protocal.class);
	}

	public static Protocal createPKeepAliveResponse(int to_user_id)
	{
		return new Protocal(ProtocalType.S.FROM_SERVER_TYPE_OF_RESPONSE$KEEP$ALIVE, 
				create(new PKeepAliveResponse()), 0, to_user_id);
	}

	public static PKeepAliveResponse parsePKeepAliveResponse(String dataContentOfProtocal)
	{
		return (PKeepAliveResponse)parse(dataContentOfProtocal, PKeepAliveResponse.class);
	}

	public static Protocal createPKeepAlive(int from_user_id)
	{
		return new Protocal(ProtocalType.C.FROM_CLIENT_TYPE_OF_KEEP$ALIVE, 
				create(new PKeepAlive()), from_user_id, 0);
	}

	public static PKeepAlive parsePKeepAlive(String dataContentOfProtocal)
	{
		return (PKeepAlive)parse(dataContentOfProtocal, PKeepAlive.class);
	}

	public static Protocal createPErrorResponse(int errorCode, String errorMsg, int user_id)
	{
		return new Protocal(ProtocalType.S.FROM_SERVER_TYPE_OF_RESPONSE$FOR$ERROR, 
				create(new PErrorResponse(errorCode, errorMsg)), 0, user_id);
	}

	public static PErrorResponse parsePErrorResponse(String dataContentOfProtocal)
	{
		return (PErrorResponse)parse(dataContentOfProtocal, PErrorResponse.class);
	}

	public static Protocal createPLoginoutInfo(int user_id, String loginName)
	{
		return new Protocal(ProtocalType.C.FROM_CLIENT_TYPE_OF_LOGOUT
//				, create(new PLogoutInfo(user_id, loginName))
				, null
				, user_id, 0);
	}

	public static Protocal createPLoginInfo(String loginName, String loginPsw, String extra)
	{
		return new Protocal(ProtocalType.C.FROM_CLIENT_TYPE_OF_LOGIN
				, create(new PLoginInfo(loginName, loginPsw, extra)), -1, 0);
	}

	public static PLoginInfo parsePLoginInfo(String dataContentOfProtocal)
	{
		return (PLoginInfo)parse(dataContentOfProtocal, PLoginInfo.class);
	}

	public static Protocal createPLoginInfoResponse(int code, int user_id)
	{
		return new Protocal(ProtocalType.S.FROM_SERVER_TYPE_OF_RESPONSE$LOGIN, 
				create(new PLoginInfoResponse(code, user_id)), 
				0, 
				user_id, 
				true, Protocal.genFingerPrint());
	}

	public static PLoginInfoResponse parsePLoginInfoResponse(String dataContentOfProtocal)
	{
		return (PLoginInfoResponse)parse(dataContentOfProtocal, PLoginInfoResponse.class);
	}

	public static Protocal createCommonData(String dataContent, int from_user_id, int to_user_id, boolean QoS, String fingerPrint)
	{
		return new Protocal(ProtocalType.C.FROM_CLIENT_TYPE_OF_COMMON$DATA, 
				dataContent, from_user_id, to_user_id, QoS, fingerPrint);
	}

	public static Protocal createCommonData(String dataContent, int from_user_id, int to_user_id)
	{
		return new Protocal(ProtocalType.C.FROM_CLIENT_TYPE_OF_COMMON$DATA, 
				dataContent, from_user_id, to_user_id);
	}

	public static Protocal createRecivedBack(int from_user_id, int to_user_id, String recievedMessageFingerPrint)
	{
		return new Protocal(ProtocalType.C.FROM_CLIENT_TYPE_OF_RECIVED
				, recievedMessageFingerPrint, from_user_id, to_user_id);// 该包当然不需要QoS支持！
	}
}