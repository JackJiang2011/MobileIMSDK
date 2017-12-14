/*
 * Copyright (C) 2017  即时通讯网(52im.net) & Jack Jiang.
 * The MobileIMSDK_X_netty (MobileIMSDK v3.x Netty版) Project. 
 * All rights reserved.
 * 
 * > Github地址: https://github.com/JackJiang2011/MobileIMSDK
 * > 文档地址: http://www.52im.net/forum-89-1.html
 * > 即时通讯技术社区：http://www.52im.net/
 * > 即时通讯技术交流群：320837163 (http://www.52im.net/topic-qqgroup.html)
 *  
 * "即时通讯网(52im.net) - 即时通讯开发者社区!" 推荐开源工程。
 * 
 * Protocal.java at 2017-12-9 11:24:33, code by Jack Jiang.
 * You can contact author with jack.jiang@52im.net or jb2011@163.com.
 */
package net.openmob.mobileimsdk.server.protocal;

import java.util.UUID;

import com.google.gson.Gson;

public class Protocal
{
	private boolean bridge = false;
	private int type = 0;
	private String dataContent = null;
	private String from = "-1";
	private String to = "-1";
	private String fp = null;
	private boolean QoS = false;
	private int typeu = -1;
	private transient int retryCount = 0;

	public Protocal(int type, String dataContent, String from, String to)
	{
		this(type, dataContent, from, to, -1);
	}
	
	public Protocal(int type, String dataContent, String from, String to, int typeu)
	{
		this(type, dataContent, from, to, false, null, typeu);
	}
	
	public Protocal(int type, String dataContent, String from, String to
			, boolean QoS, String fingerPrint)
	{
		this(type, dataContent, from, to, QoS, fingerPrint, -1);
	}
	
	public Protocal(int type, String dataContent, String from, String to
			, boolean QoS, String fingerPrint, int typeu)
	{
		this.type = type;
		this.dataContent = dataContent;
		this.from = from;
		this.to = to;
		this.QoS = QoS;
		this.typeu = typeu;
		
		if(QoS && fingerPrint == null)
			fp = Protocal.genFingerPrint();
		else
			fp = fingerPrint;
	}
	
	public int getType()
	{
		return this.type;
	}
	public void setType(int type)
	{
		this.type = type;
	}
	
	public String getDataContent()
	{
		return this.dataContent;
	}
	public void setDataContent(String dataContent)
	{
		this.dataContent = dataContent;
	}
	
	public String getFrom()
	{
		return this.from;
	}
	public void setFrom(String from)
	{
		this.from = from;
	}

	public String getTo()
	{
		return this.to;
	}
	public void setTo(String to)
	{
		this.to = to;
	}
	
	public String getFp()
	{
		return this.fp;
	}

	public int getRetryCount()
	{
		return this.retryCount;
	}
	public void increaseRetryCount()
	{
		this.retryCount += 1;
	}
	
	public boolean isQoS()
	{
		return QoS;
	}
	public void setQoS(boolean qoS)
	{
		this.QoS = qoS;
	}
	
	public boolean isBridge()
	{
		return bridge;
	}
	public void setBridge(boolean bridge)
	{
		this.bridge = bridge;
	}
	
	public int getTypeu()
	{
		return typeu;
	}
	public void setTypeu(int typeu)
	{
		this.typeu = typeu;
	}
	
	public String toGsonString()
	{
		return new Gson().toJson(this);
	}
	
	public byte[] toBytes()
	{
		return CharsetHelper.getBytes(toGsonString());
	}
	
	@Override
	public Object clone()
	{
		Protocal cloneP = new Protocal(this.getType()
				, this.getDataContent(), this.getFrom(), this.getTo(), this.isQoS(), this.getFp());
		cloneP.setBridge(this.bridge); // since 3.0
		cloneP.setTypeu(this.typeu);   // since 3.0
		return cloneP;
	}
	
	public static String genFingerPrint()
	{
		return UUID.randomUUID().toString();
	}
}
