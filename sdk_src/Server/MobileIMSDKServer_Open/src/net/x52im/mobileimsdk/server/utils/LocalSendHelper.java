/*
 * Copyright (C) 2022  即时通讯网(52im.net) & Jack Jiang.
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
 * LocalSendHelper.java at 2022-7-12 16:35:57, code by Jack Jiang.
 */
package net.x52im.mobileimsdk.server.utils;

import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import net.x52im.mobileimsdk.server.ServerCoreHandler;
import net.x52im.mobileimsdk.server.ServerLauncher;
import net.x52im.mobileimsdk.server.network.Gateway;
import net.x52im.mobileimsdk.server.network.GatewayUDP;
import net.x52im.mobileimsdk.server.network.MBObserver;
import net.x52im.mobileimsdk.server.processor.OnlineProcessor;
import net.x52im.mobileimsdk.server.protocal.ErrorCode;
import net.x52im.mobileimsdk.server.protocal.Protocal;
import net.x52im.mobileimsdk.server.protocal.ProtocalFactory;
import net.x52im.mobileimsdk.server.protocal.s.PKickoutInfo;
import net.x52im.mobileimsdk.server.qos.QoS4SendDaemonS2C;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LocalSendHelper
{
	private static Logger logger = LoggerFactory.getLogger(ServerCoreHandler.class);  
	
	public static void sendData(String to_user_id, String dataContent, MBObserver resultObserver) throws Exception 
    {
    	sendData(to_user_id, dataContent, true, null, -1, resultObserver);
    }
	
	public static void sendData(String to_user_id, String dataContent, int typeu, MBObserver resultObserver) throws Exception 
    {
    	sendData(to_user_id, dataContent, true, null, typeu, resultObserver);
    }
	
	public static void sendData(String to_user_id, String dataContent, boolean QoS, int typeu, MBObserver resultObserver) throws Exception 
    {
    	sendData(to_user_id, dataContent, QoS, null, typeu, resultObserver);
    }
	
	public static void sendData(String to_user_id, String dataContent, boolean QoS, String fingerPrint, MBObserver resultObserver) throws Exception 
    {
    	sendData(to_user_id, dataContent, QoS, fingerPrint, -1, resultObserver);
    }
	
	public static void sendData(String to_user_id, String dataContent, boolean QoS, String fingerPrint, int typeu, MBObserver resultObserver) throws Exception 
    {
    	sendData(ProtocalFactory.createCommonData(dataContent, "0", to_user_id, QoS, fingerPrint, typeu), resultObserver);
    }
    
    public static void sendData(Protocal p, MBObserver resultObserver) throws Exception 
    {
    	if(p != null)
    	{
    		if(!"0".equals(p.getTo()))
    			sendData(OnlineProcessor.getInstance().getOnlineSession(p.getTo()), p, resultObserver);
    		else
    		{
    			logger.warn("[IMCORE]【注意】此Protocal对象中的接收方是服务器(user_id==0)（而此方法本来就是由Server调用，自已发自已不可能！），数据发送没有继续！"+p.toGsonString());
    			if(resultObserver != null)
    				resultObserver.update(false, null);
    		}
    	}
    	else
    	{
    		if(resultObserver != null)
    			resultObserver.update(false, null);
    	}
    }
    
    public static void sendData(final Channel session, final Protocal p, final MBObserver resultObserver) throws Exception 
    {
		if(session == null)
		{
			logger.info("[IMCORE-{}]toSession==null >> id={}的用户尝试发给客户端{}的消息：str={}因接收方的id已不在线，此次实时发送没有继续(此消息应考虑作离线处理哦)."
					, Gateway.$(session), p.getFrom(), p.getTo(), p.getDataContent());
		}
		else
		{
			if(session.isActive())
			{
		    	if(p != null)
		    	{
		    		if(ServerLauncher.serverTimestamp)
		    			p.setSm(Protocal.genServerTimestamp());
		    		
		    		Object to = null;
		    		if(Gateway.isWebSocketChannel(session)){
		    			final String res = p.toGsonString();
		    			to = new TextWebSocketFrame(res);
		    		}
		    		else{
		    			final byte[] res = p.toBytes();
		    			to = Unpooled.copiedBuffer(res);
		    		}
		    		ChannelFuture cf = session.writeAndFlush(to);//.sync();
		    		
		    		cf.addListener(new ChannelFutureListener() {
		    	         public void operationComplete(ChannelFuture future) {
		    	        	 if( future.isSuccess()){
		    	        		 if(p.isQoS() && !QoS4SendDaemonS2C.getInstance().exist(p.getFp()))
		    	        			 QoS4SendDaemonS2C.getInstance().put(p);
		    	        	 }
		    	        	 else{
		    	        		 logger.warn("[IMCORE-{}]给客户端：{}的数据->{},发送失败(此消息应考虑作离线处理哦)."
		    	        				 , Gateway.$(session), ServerToolKits.clientInfoToString(session), p.toGsonString());
		    	        	 }

		 		    		if(resultObserver != null)
	 		    				resultObserver.update(future.isSuccess(), null);
		    	         }
		    	    });
		    		
		    		// ## Bug FIX: 20171226 by JS, 上述数据的发送结果直接通过ChannelFutureListener就能知道，
		    		//            如果此处不return，则会走到最后的resultObserver.update(false, null);，就会
		    		//            出现一个发送方法的结果回调先是失败（错误地走到下面去了），一个是成功（真正的listener结果）
		    		return;
		    		// ## Bug FIX: 20171226 by JS END
		    	}
		    	else
		    	{
		    		logger.warn("[IMCORE-{}]客户端id={}要发给客户端{}的实时消息：str={}没有继续(此消息应考虑作离线处理哦)."
							, Gateway.$(session), p.getFrom(), p.getTo(), p.getDataContent());
		    	}
			}
		}
		
		if(resultObserver != null)
			resultObserver.update(false, null);
    }
    
	public static void replyDataForUnlogined(final Channel session, Protocal p, MBObserver resultObserver) throws Exception
	{
		logger.warn("[IMCORE-{}]>> 客户端{}尚未登陆，{}处理未继续."
				, Gateway.$(session), ServerToolKits.clientInfoToString(session), p.getDataContent());
		
		if(resultObserver == null)
		{
			resultObserver = new MBObserver(){
				@Override
				public void update(boolean sendOK, Object extraObj){
					logger.warn("[IMCORE-{}]>> 客户端{}未登陆，服务端反馈发送成功？{}（会话即将关闭）"
							, Gateway.$(session), ServerToolKits.clientInfoToString(session), sendOK);
					
					if(!GatewayUDP.isUDPChannel(session))
						session.close();
				}
			};
		}
		
		Protocal perror = ProtocalFactory.createPErrorResponse(ErrorCode.ForS.RESPONSE_FOR_UNLOGIN, p.toGsonString(), "-1"); // 尚未登陆则user_id就不存在了,用-1表示吧，目前此情形下该参数无意义
		sendData(session, perror, resultObserver);
	}

	public static void replyRecievedBack(Channel session, Protocal pFromClient, MBObserver resultObserver) throws Exception
	{
		if(pFromClient.isQoS() && pFromClient.getFp() != null)
		{
			Protocal receivedBackP = ProtocalFactory.createRecivedBack(pFromClient.getTo(), pFromClient.getFrom(), pFromClient.getFp());
			sendData(session, receivedBackP, resultObserver);
		}
		else
		{
			logger.warn("[IMCORE-{}]收到{}发过来需要QoS的包，但它的指纹码却为null！无法发伪应答包哦！", Gateway.$(session), pFromClient.getFrom());
			
			if(resultObserver != null)
				resultObserver.update(false, null);
		}
	}
	
	public static void sendKickout(final Channel sessionBeKick, String to_user_id, final int code, String reason) throws Exception
	{
		MBObserver sendResultObserver = new MBObserver(){
			@Override
			public void update(boolean sendOK, Object extraObj){
				logger.warn("[IMCORE-{}]>> 客户端{}的被踢指令发送成功？{}（会话即将关闭）", Gateway.$(sessionBeKick), ServerToolKits.clientInfoToString(sessionBeKick), sendOK);
				if(!GatewayUDP.isUDPChannel(sessionBeKick)){
					OnlineProcessor.setBeKickoutCodeForChannel(sessionBeKick, code);
					sessionBeKick.close();
				}
			}
		};
		LocalSendHelper.sendData(sessionBeKick, ProtocalFactory.createPKickout(to_user_id, code, reason), sendResultObserver);
	}
}
