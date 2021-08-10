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
 * PLoginInfo.java at 2021-8-4 21:24:15, code by Jack Jiang.
 */
package net.x52im.mobileimsdk.server.protocal.c;

public class PLoginInfo
{
	protected String loginUserId = null;
	protected String loginToken = null;
	protected String extra = null;
	protected long firstLoginTime = 0;
	
	public PLoginInfo(String loginUserId, String loginToken)
	{
		this(loginUserId, loginToken, null);
	}
	
	public PLoginInfo(String loginUserId, String loginToken, String extra)
	{
		this.loginUserId = loginUserId;
		this.loginToken = loginToken;
		this.extra = extra;
	}
	
	public String getLoginUserId()
	{
		return loginUserId;
	}

	public void setLoginUserId(String loginUserId)
	{
		this.loginUserId = loginUserId;
	}

	public String getLoginToken()
	{
		return loginToken;
	}

	public void setLoginToken(String loginToken)
	{
		this.loginToken = loginToken;
	}

	public String getExtra()
	{
		return extra;
	}
	
	public void setExtra(String extra)
	{
		this.extra = extra;
	}

	public long getFirstLoginTime()
	{
		return firstLoginTime;
	}

	public void setFirstLoginTime(long firstLoginTime)
	{
		this.firstLoginTime = firstLoginTime;
	}
	
	public static boolean isFirstLogin(long firstLoginTime)
	{
		return firstLoginTime <= 0;
	}
}
