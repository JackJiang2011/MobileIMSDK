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
 * PLoginInfoResponse.java at 2021-6-29 10:15:36, code by Jack Jiang.
 */
package net.x52im.mobileimsdk.server.protocal.s;

public class PLoginInfoResponse
{
	protected int code = 0;
	
	protected long firstLoginTime = 0;
	public PLoginInfoResponse(int code, long firstLoginTime)
	{
		this.code = code;
		this.firstLoginTime = firstLoginTime;
	}

	public int getCode()
	{
		return code;
	}
	public void setCode(int code)
	{
		this.code = code;
	}

	public long getFirstLoginTime()
	{
		return firstLoginTime;
	}

	public void setFirstLoginTime(long firstLoginTime)
	{
		this.firstLoginTime = firstLoginTime;
	}
}
