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
 * LocalSendHelper.java at 2017-5-2 15:49:27, code by Jack Jiang.
 * You can contact author with jack.jiang@52im.net or jb2011@163.com.
 */
package net.openmob.mobileimsdk.server.utils;

import net.nettime.mobileimsdk.server.bridge.QoS4SendDaemonB2C;
import net.openmob.mobileimsdk.server.ServerCoreHandler;
import net.openmob.mobileimsdk.server.processor.OnlineProcessor;
import net.openmob.mobileimsdk.server.protocal.ErrorCode;
import net.openmob.mobileimsdk.server.protocal.Protocal;
import net.openmob.mobileimsdk.server.protocal.ProtocalFactory;
import net.openmob.mobileimsdk.server.qos.QoS4SendDaemonS2C;

import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.future.WriteFuture;
import org.apache.mina.core.session.IoSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LocalSendHelper
{
	private static Logger logger = LoggerFactory.getLogger(ServerCoreHandler.class);  
	
	public static boolean sendData(String to_user_id, String dataContent) throws Exception 
    {
    	return sendData(to_user_id, dataContent, true, null, -1);
    }
	
	public static boolean sendData(String to_user_id, String dataContent, int typeu) throws Exception 
    {
    	return sendData(to_user_id, dataContent, true, null, typeu);
    }
	
	public static boolean sendData(String to_user_id, String dataContent
			, boolean QoS, int typeu) throws Exception 
    {
    	return sendData(to_user_id, dataContent, QoS, null, typeu);
    }
	
	public static boolean sendData(String to_user_id, String dataContent
			, boolean QoS, String fingerPrint) throws Exception 
    {
    	return sendData(to_user_id, dataContent, QoS, fingerPrint, -1);
    }
	
	public static boolean sendData(String to_user_id, String dataContent
			, boolean QoS, String fingerPrint, int typeu) throws Exception 
    {
    	return sendData(ProtocalFactory.createCommonData(
    			dataContent, "0", to_user_id, QoS, fingerPrint, typeu));
    }
    
    public static boolean sendData(Protocal p) throws Exception 
    {
    	if(p != null)
    	{
    		if(!"0".equals(p.getTo()))
    			return sendData(OnlineProcessor.getInstance().getOnlineSession(p.getTo()), p);
    		else
    		{
    			logger.warn("[IMCORE]【注意】此Protocal对象中的接收方是服务器(user_id==0)（而此方法本来就是由Server调用，自已发自已不可能！），数据发送没有继续！"+p.toGsonString());
    			return false;
    		}
    	}
    	else
    		return false;
    }
    
    public static boolean sendData(IoSession session, Protocal p) throws Exception 
    {
		if(session == null)
		{
			logger.info("[IMCORE]toSession==null >> id="+p.getFrom()+"的用户尝试发给客户端"+p.getTo()
					+"的消息：str="+p.getDataContent()+"因接收方的id已不在线，此次实时发送没有继续(此消息应考虑作离线处理哦).");
		}
		else
		{
			if(session.isConnected())
			{
		    	if(p != null)
		    	{
		    		byte[] res = p.toBytes();
		    		
		    		IoBuffer buf = IoBuffer.wrap(res);  
		    		
		    		WriteFuture future = session.write(buf);  
		    		future.awaitUninterruptibly(100);
		    		if( future.isWritten() )
		    		{
		    			if("0".equals(p.getFrom()))
		    			{
		    				if(p.isQoS() && !QoS4SendDaemonS2C.getInstance().exist(p.getFp()))
		    					QoS4SendDaemonS2C.getInstance().put(p);
		    			}
		    			else if(p.isBridge())
		    			{
		    				if(p.isQoS() && !QoS4SendDaemonB2C.getInstance().exist(p.getFp()))
		    					QoS4SendDaemonB2C.getInstance().put(p);
		    			}
		    			
		    			return true;
		    		}
		    		else
		    			logger.warn("[IMCORE]给客户端："+ServerToolKits.clientInfoToString(session)+"的数据->"+p.toGsonString()+",发送失败！["+res.length+"](此消息应考虑作离线处理哦).");
		    	}
			}
			else
			{
				logger.warn("[IMCORE]toSession!=null但会话已经关闭 >> 客户端id="+p.getFrom()+"要发给客户端"+p.getTo()
						+"的实时消息：str="+p.getDataContent()+"没有继续(此消息应考虑作离线处理哦).");
			}
		}
		
		return false;
    }
    
	public static boolean replyDataForUnlogined(IoSession session, Protocal p) throws Exception
	{
		logger.warn("[IMCORE]>> 客户端"+ServerToolKits.clientInfoToString(session)+"尚未登陆，"+p.getDataContent()+"处理未继续.");
		return sendData(session, ProtocalFactory.createPErrorResponse(
				ErrorCode.ForS.RESPONSE_FOR_UNLOGIN, p.toGsonString(), "-1")); // 尚未登陆则user_id就不存在了,用-1表示吧，目前此情形下该参数无意义
	}

	public static boolean replyDelegateRecievedBack(IoSession session, Protocal pFromClient) throws Exception
	{
		if(pFromClient.isQoS() && pFromClient.getFp() != null)
		{
			Protocal receivedBackP = ProtocalFactory.createRecivedBack(
					pFromClient.getTo()
					, pFromClient.getFrom()
					, pFromClient.getFp());

			return sendData(session, receivedBackP);
		}
		else
		{
			logger.warn("[IMCORE]收到"+pFromClient.getFrom()
					+"发过来需要QoS的包，但它的指纹码却为null！无法发伪应答包哦！");
			return false;
		}
	}
}
