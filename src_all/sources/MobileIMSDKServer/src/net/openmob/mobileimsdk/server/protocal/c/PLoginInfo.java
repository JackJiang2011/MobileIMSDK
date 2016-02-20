/*
 * Copyright (C) 2016 即时通讯网(52im.net) The MobileIMSDK Project. 
 * All rights reserved.
 * Project URL:https://github.com/JackJiang2011/MobileIMSDK
 *  
 * 即时通讯网(52im.net) - 即时通讯技术社区! PROPRIETARY/CONFIDENTIAL.
 * Use is subject to license terms.
 * 
 * PLoginInfo.java at 2016-2-20 11:26:02, code by Jack Jiang.
 * You can contact author with jack.jiang@52im.net or jb2011@163.com.
 */
package net.openmob.mobileimsdk.server.protocal.c;

public class PLoginInfo
{
	private String loginName = null;
	private String loginPsw = null;
	private String extra = null;

	public PLoginInfo(String loginName, String loginPsw)
	{
		this(loginName, loginPsw, null);
	}
	
	public PLoginInfo(String loginName, String loginPsw, String extra)
	{
		this.loginName = loginName;
		this.loginPsw = loginPsw;
		this.extra = extra;
	}

	public String getLoginName()
	{
		return this.loginName;
	}

	public void setLoginName(String loginName)
	{
		this.loginName = loginName;
	}

	public String getLoginPsw()
	{
		return this.loginPsw;
	}

	public void setLoginPsw(String loginPsw)
	{
		this.loginPsw = loginPsw;
	}
	
	public String getExtra()
	{
		return extra;
	}
	
	public void setExtra(String extra)
	{
		this.extra = extra;
	}
}