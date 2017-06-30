/*
 * Copyright (C) 2017  即时通讯网(52im.net) & Jack Jiang.
 * The MobileIMSDK_X (MobileIMSDK v3.x) Project. 
 * All rights reserved.
 * 
 * > Github地址: https://github.com/JackJiang2011/MobileIMSDK
 * > 文档地址: http://www.52im.net/forum-89-1.html
 * > 即时通讯技术社区：http://www.52im.net/
 * > 即时通讯技术交流群：320837163 (http://www.52im.net/topic-qqgroup.html)
 *  
 * "即时通讯网(52im.net) - 即时通讯开发者社区!" 推荐开源工程。
 * 
 * ServerToolKits.java at 2017-5-2 15:49:28, code by Jack Jiang.
 * You can contact author with jack.jiang@52im.net or jb2011@163.com.
 */
package net.openmob.mobileimsdk.server.utils;

import java.net.SocketAddress;

import net.openmob.mobileimsdk.server.ServerCoreHandler;
import net.openmob.mobileimsdk.server.ServerLauncher;
import net.openmob.mobileimsdk.server.processor.OnlineProcessor;
import net.openmob.mobileimsdk.server.protocal.CharsetHelper;
import net.openmob.mobileimsdk.server.protocal.Protocal;
import net.openmob.mobileimsdk.server.protocal.ProtocalFactory;

import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.session.IoSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ServerToolKits
{
	private static Logger logger = LoggerFactory.getLogger(ServerCoreHandler.class);  
	
    public static void setSenseMode(SenseMode mode)
    {
    	int expire = 0;
    	
    	switch(mode)
    	{
    		case MODE_3S:
    			// 误叛容忍度为丢3个包
    			expire = 3 * 3 + 1;
    			break;
    		case MODE_10S:
    			// 误叛容忍度为丢2个包
    			expire = 10 * 2 + 1;
        		break;
    		case MODE_30S:
    			// 误叛容忍度为丢2个包
    			expire = 30 * 2 + 2;
        		break;
    		case MODE_60S:
    			// 误叛容忍度为丢2个包
    			expire = 60 * 2 + 2;
        		break;
    		case MODE_120S:
    			// 误叛容忍度为丢2个包
    			expire = 120 * 2 + 2;
        		break;
    	}
    	
    	if(expire > 0)
    		ServerLauncher.SESION_RECYCLER_EXPIRE = expire;
    }
    
	public static String clientInfoToString(IoSession session)
	{
		SocketAddress remoteAddress = session.getRemoteAddress();
		String s1 = remoteAddress.toString();
		StringBuilder sb = new StringBuilder()
		.append("{uid:")
		.append(OnlineProcessor.getUserIdFromSession(session))
		.append("}")
		.append(s1);
		return sb.toString();
	}
	
	public static String fromIOBuffer_JSON(IoBuffer buffer) throws Exception 
	{
		String jsonStr = buffer.getString(CharsetHelper.decoder);
//		logger.debug("[IMCORE]>> 【收到数据长度】"+jsonStr.length()+", 内容："+jsonStr+".");
		return jsonStr;
	}
	
	public static Protocal fromIOBuffer(IoBuffer buffer) throws Exception 
	{
//		return new Gson().fromJson(fromIOBuffer_JSON(buffer), Protocal.class);
		return ProtocalFactory.parse(fromIOBuffer_JSON(buffer), Protocal.class);
	}
    
    public enum SenseMode
    {
    	/** 
    	 * 对应于客户端的3秒心跳模式：此模式的用户非正常掉线超时时长为“3 * 3 + 1”秒。
    	 * <p>
    	 * 客户端心跳丢包容忍度为3个包。此模式为当前所有预设模式中体验最好，但
    	 * 客户端可能会大幅提升耗电量和心跳包的总流量。 
    	 */
    	MODE_3S,
    	
    	/** 
    	 * 对应于客户端的10秒心跳模式：此模式的用户非正常掉线超时时长为“10 * 2 + 1”秒。 
    	 * <p>
    	 * 客户端心跳丢包容忍度为2个包。
    	 */
    	MODE_10S,
    	
    	/** 
    	 * 对应于客户端的30秒心跳模式：此模式的用户非正常掉线超时时长为“30 * 2 + 2”秒。
    	 * <p>
    	 * 客户端心跳丢包容忍度为2个包。
    	 */
    	MODE_30S,
    	
    	/** 
    	 * 对应于客户端的60秒心跳模式：此模式的用户非正常掉线超时时长为“60 * 2 + 2”秒。
    	 * <p>
    	 * 客户端心跳丢包容忍度为2个包。
    	 */
    	MODE_60S,
    	
    	/** 
    	 * 对应于客户端的120秒心跳模式：此模式的用户非正常掉线超时时长为“120 * 2 + 2”秒。 
    	 * <p>
    	 * 客户端心跳丢包容忍度为2个包。
    	 */
    	MODE_120S
    }
}
