/*
 * Copyright (C) 2016 即时通讯网(52im.net) The MobileIMSDK Project. 
 * All rights reserved.
 * Project URL:https://github.com/JackJiang2011/MobileIMSDK
 *  
 * 即时通讯网(52im.net) - 即时通讯技术社区! PROPRIETARY/CONFIDENTIAL.
 * Use is subject to license terms.
 * 
 * PLoginInfoResponse.java at 2016-2-20 11:26:02, code by Jack Jiang.
 * You can contact author with jack.jiang@52im.net or jb2011@163.com.
 */
package net.openmob.mobileimsdk.server.protocal.s;

public class PLoginInfoResponse
{
	private int code = 0;
	private int user_id = -1;

	public PLoginInfoResponse(int code, int user_id)
	{
		this.code = code;
		this.user_id = user_id;
	}

	public int getCode()
	{
		return this.code;
	}

	public void setCode(int code) {
		this.code = code;
	}

	public int getUser_id()
	{
		return this.user_id;
	}

	public void setUser_id(int user_id)
	{
		this.user_id = user_id;
	}
}