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
 * GlobalSendHelper.java at 2017-5-2 15:49:28, code by Jack Jiang.
 * You can contact author with jack.jiang@52im.net or jb2011@163.com.
 */
package net.openmob.mobileimsdk.server.utils;

import org.apache.mina.core.session.IoSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.nettime.mobileimsdk.server.bridge.QoS4ReciveDaemonC2B;
import net.openmob.mobileimsdk.server.ServerCoreHandler;
import net.openmob.mobileimsdk.server.ServerLauncher;
import net.openmob.mobileimsdk.server.processor.BridgeProcessor;
import net.openmob.mobileimsdk.server.processor.OnlineProcessor;
import net.openmob.mobileimsdk.server.protocal.Protocal;

public class GlobalSendHelper
{
	private static Logger logger = LoggerFactory.getLogger(ServerCoreHandler.class);  

	public static void sendDataC2C(BridgeProcessor bridgeProcessor
			, IoSession session, Protocal pFromClient, String remoteAddress
			, ServerCoreHandler serverCoreHandler) throws Exception
	{
		// TODO just for DEBUG
		OnlineProcessor.getInstance().__printOnline();

		boolean needDelegateACK = false;

		if(ServerLauncher.bridgeEnabled && !OnlineProcessor.isOnline(pFromClient.getTo()))
		{
			logger.debug("[IMCORE<C2C>-桥接↑]>> 客户端"+pFromClient.getTo()+"不在线，数据[from:"+pFromClient.getFrom()
					+",fp:"+pFromClient.getFp()+"to:"+pFromClient.getTo()+",content:"+pFromClient.getDataContent()
					+"] 将通过MQ直发Web服务端（彼时在线则通过web实时发送、否则通过Web端进"
					+"行离线存储）【第一阶段APP+WEB跨机通信算法】！");

			if(pFromClient.isQoS()
					&& QoS4ReciveDaemonC2B.getInstance().hasRecieved(pFromClient.getFp()))
			{
				needDelegateACK = true;
			}
			else
			{
				boolean toMQ = bridgeProcessor.publish(pFromClient.toGsonString());
				if(toMQ)
				{
					logger.debug("[IMCORE<C2C>-桥接↑]>> 客户端"+remoteAddress+"的数据已跨机器送出成功【OK】。(数据[from:"+pFromClient.getFrom()
							+",fp:"+pFromClient.getFp()+",to:"+pFromClient.getTo()+",content:"+pFromClient.getDataContent()
							+"]【第一阶段APP+WEB跨机通信算法】)");

					if(pFromClient.isQoS())
						needDelegateACK = true;
				}
				else
				{
					logger.debug("[IMCORE<C2C>-桥接↑]>> 客户端"+remoteAddress+"的数据已跨机器送出失败，将作离线处理了【NO】。(数据[from:"+pFromClient.getFrom()
							+",fp:"+pFromClient.getFp()+"to:"+pFromClient.getTo()+",content:"+pFromClient.getDataContent()
							+"]【第一阶段APP+WEB跨机通信算法】)");

					boolean offlineProcessedOK = serverCoreHandler.getServerEventListener()
							.onTransBuffer_C2C_RealTimeSendFaild_CallBack(pFromClient.getTo()
									, pFromClient.getFrom(), pFromClient.getDataContent(), pFromClient.getFp(), pFromClient.getTypeu());
					if(pFromClient.isQoS() && offlineProcessedOK)
					{
						needDelegateACK = true;
					}
					else
					{
						logger.warn("[IMCORE<C2C>-桥接↑]>> 客户端"+remoteAddress+"的通用数据传输消息尝试实时发送没有成功，但上层应用层没有成" +
								"功(或者完全没有)进行离线存储，此消息将被服务端丢弃【第一阶段APP+WEB跨机通信算法】！");
					}
				}
			}

			if(needDelegateACK)
			{
				boolean receivedBackSendSucess = LocalSendHelper.replyDelegateRecievedBack(session, pFromClient);
				if(receivedBackSendSucess)
					logger.debug("[IMCORE<C2C>-桥接↑]【QoS_伪应答_C2S】向"+pFromClient.getFrom()+"发送"+pFromClient.getFp()
							+"的伪应答包成功,伪装from自："+pFromClient.getTo()+"【第一阶段APP+WEB跨机通信算法】.");
			}

			QoS4ReciveDaemonC2B.getInstance().addRecieved(pFromClient);
		}
		else
		{
			boolean sendOK = LocalSendHelper.sendData(pFromClient);
			if(sendOK)
			{
				serverCoreHandler.getServerEventListener().onTransBuffer_C2C_CallBack(
						pFromClient.getTo(), pFromClient.getFrom()
						, pFromClient.getDataContent(), pFromClient.getFp(), pFromClient.getTypeu());
			}
			else
			{
				logger.info("[IMCORE<C2C>]>> 客户端"+remoteAddress+"的通用数据尝试实时发送没有成功，将交给应用层进行离线存储哦...");

				boolean offlineProcessedOK = serverCoreHandler.getServerEventListener()
						.onTransBuffer_C2C_RealTimeSendFaild_CallBack(pFromClient.getTo()
								, pFromClient.getFrom(), pFromClient.getDataContent(), pFromClient.getFp(), pFromClient.getTypeu());
				if(pFromClient.isQoS() && offlineProcessedOK)
				{
					boolean receivedBackSendSucess = LocalSendHelper.replyDelegateRecievedBack(session, pFromClient);
					if(receivedBackSendSucess)
						logger.debug("[IMCORE<C2C>]【QoS_伪应答_C2S】向"+pFromClient.getFrom()+"发送"+pFromClient.getFp()
								+"的伪应答包成功,from="+pFromClient.getTo()+".");
				}
				else
				{
					logger.warn("[IMCORE<C2C>]>> 客户端"+remoteAddress+"的通用数据传输消息尝试实时发送没有成功，但上层应用层没有成" +
							"功(或者完全没有)进行离线存储，此消息将被服务端丢弃！");
				}
			}
		}
	}
	
	public static boolean sendDataS2C(BridgeProcessor bridgeProcessor, Protocal pFromClient) throws Exception
	{
		// TODO just for DEBUG
		OnlineProcessor.getInstance().__printOnline();
		
		boolean sucess = false;

		if(!OnlineProcessor.isOnline(pFromClient.getTo()))
		{
			logger.debug("[IMCORE<S2C>-桥接↑]>> 客户端"+pFromClient.getTo()+"不在线，数据[from:"+pFromClient.getFrom()
					+",fp:"+pFromClient.getFp()+"to:"+pFromClient.getTo()+",content:"+pFromClient.getDataContent()
					+"] 将通过MQ直发Web服务端（彼时在线则通过web实时发送、否则通过Web端进"
					+"行离线存储）【第一阶段APP+WEB跨机通信算法】！");

				boolean toMQ = bridgeProcessor.publish(pFromClient.toGsonString());
				if(toMQ)
				{
					logger.debug("[IMCORE<S2C>-桥接↑]>> 服务端的数据已跨机器送出成功【OK】。(数据[from:"+pFromClient.getFrom()
							+",fp:"+pFromClient.getFp()+",to:"+pFromClient.getTo()+",content:"+pFromClient.getDataContent()
							+"]【第一阶段APP+WEB跨机通信算法】)");
					sucess = true;
				}
				else
				{
					logger.error("[IMCORE<S2C>-桥接↑]>> 服务端的数据已跨机器送出失败，请通知管理员检查MQ中间件是否正常工作【NO】。(数据[from:"+pFromClient.getFrom()
							+",fp:"+pFromClient.getFp()+"to:"+pFromClient.getTo()+",content:"+pFromClient.getDataContent()
							+"]【第一阶段APP+WEB跨机通信算法】)");
				}
		}
		else
		{
			boolean sendOK = LocalSendHelper.sendData(pFromClient);
			if(sendOK)
				sucess = true;
			else
				logger.warn("[IMCORE]>> 服务端的通用数据传输消息尝试实时发送没有成功，但上层应用层没有成" +
							"功，请应用层自行决定此条消息的发送【NO】！");
		}
		
		return sucess;
	}
}
