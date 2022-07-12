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
 * GlobalSendHelper.java at 2022-7-12 16:35:57, code by Jack Jiang.
 */
package net.x52im.mobileimsdk.server.utils;

import io.netty.channel.Channel;
import net.x52im.mobileimsdk.server.ServerCoreHandler;
import net.x52im.mobileimsdk.server.ServerLauncher;
import net.x52im.mobileimsdk.server.network.Gateway;
import net.x52im.mobileimsdk.server.network.MBObserver;
import net.x52im.mobileimsdk.server.processor.BridgeProcessor;
import net.x52im.mobileimsdk.server.processor.OnlineProcessor;
import net.x52im.mobileimsdk.server.protocal.Protocal;
import net.x52im.mobileimsdk.server.qos.QoS4ReciveDaemonC2S;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GlobalSendHelper
{
	private static Logger logger = LoggerFactory.getLogger(ServerCoreHandler.class);  

	public static void sendDataC2C(final BridgeProcessor bridgeProcessor
			, final Channel session, final Protocal pFromClient, final String remoteAddress
			, final ServerCoreHandler serverCoreHandler) throws Exception
	{
		// TODO just for DEBUG
		OnlineProcessor.getInstance().__printOnline();

		boolean needDelegateACK = false;
		if(ServerLauncher.bridgeEnabled && !OnlineProcessor.isOnline(pFromClient.getTo()))
		{
			logger.debug("[IMCORE-{}<C2C>-桥接↑]>> 客户端{}不在线，数据[from:{},fp:{},to:{},content:{}] 将通过MQ直发Web服务端" +
					"（彼时在线则通过web实时发送、否则通过Web端进行离线存储）【第一阶段APP+WEB跨机通信算法】！"
					, Gateway.$(session), pFromClient.getTo(), pFromClient.getFrom(), pFromClient.getFp()
					, pFromClient.getTo(), pFromClient.getDataContent());

			if(pFromClient.isQoS() && QoS4ReciveDaemonC2S.getInstance().hasRecieved(pFromClient.getFp()))
			{
				needDelegateACK = true;
			}
			else
			{
				boolean toMQ = bridgeProcessor.publish(pFromClient.toGsonString());
				if(toMQ)
				{
					logger.debug("[IMCORE-{}<C2C>-桥接↑]>> 客户端{}的数据已跨机器送出成功【OK】。(数据[from:{}"+
							",fp:{},to:{},content:{}]【第一阶段APP+WEB跨机通信算法】)"
							, Gateway.$(session), remoteAddress, pFromClient.getFrom(), pFromClient.getFp()
							, pFromClient.getTo(), pFromClient.getDataContent());

					if(pFromClient.isQoS())
						needDelegateACK = true;
				}
				else
				{
					logger.debug("[IMCORE-{}<C2C>-桥接↑]>> 客户端{}的数据已跨机器送出失败，将作离线处理了【NO】。(数据[from:{},fp:{},to:{},content:{}]【第一阶段APP+WEB跨机通信算法】)"
							, Gateway.$(session), remoteAddress, pFromClient.getFrom(), pFromClient.getFp()
							, pFromClient.getTo(), pFromClient.getDataContent());

					boolean offlineProcessedOK = serverCoreHandler.getServerEventListener().onTransferMessage_RealTimeSendFaild(pFromClient);
					if(pFromClient.isQoS() && offlineProcessedOK)
					{
						needDelegateACK = true;
					}
					else
					{
						logger.warn("[IMCORE-{}<C2C>-桥接↑]>> 客户端{}的通用数据传输消息尝试实时发送没有成功，但上层应用层没有成" +
								"功(或者完全没有)进行离线存储，此消息将被服务端丢弃【第一阶段APP+WEB跨机通信算法】！"
								, Gateway.$(session), remoteAddress);
					}
				}
				
				serverCoreHandler.getServerEventListener().onTransferMessage4C2C_AfterBridge(pFromClient);
			}

			if(needDelegateACK)
			{
				MBObserver resultObserver = new MBObserver(){
					@Override
					public void update(boolean receivedBackSendSucess, Object extraObj)
					{
						if(receivedBackSendSucess)
							logger.debug("[IMCORE-{}<C2C>-桥接↑]【QoS_伪应答_C2S】向{}发送{}的伪应答包成功,伪装from自：{}【第一阶段APP+WEB跨机通信算法】."
									, Gateway.$(session), pFromClient.getFrom(), pFromClient.getFp(), pFromClient.getTo());
					}
				};
				
				LocalSendHelper.replyRecievedBack(session, pFromClient, resultObserver);
			}

			QoS4ReciveDaemonC2S.getInstance().addRecieved(pFromClient);
		}
		else
		{
			MBObserver resultObserver = new MBObserver(){
				@Override
				public void update(boolean sendOK, Object extraObj)
				{
					boolean needAck = false;
					
					if(sendOK)
					{
						needAck = true;
						serverCoreHandler.getServerEventListener().onTransferMessage4C2C(pFromClient);
					}
					else
					{
						logger.info("[IMCORE-{}<C2C>]>> 客户端{}的通用数据尝试实时发送没有成功，将交给应用层进行离线存储哦..."
								, Gateway.$(session), remoteAddress);

						boolean offlineProcessedOK = serverCoreHandler.getServerEventListener().onTransferMessage_RealTimeSendFaild(pFromClient);
						if(pFromClient.isQoS() && offlineProcessedOK)
						{
							needAck = true;
						}
						else
						{
							logger.warn("[IMCORE-{}<C2C>]>> 客户端{}的通用数据传输消息尝试实时发送没有成功，但上层应用层没有成功(或者完全没有)进行离线存储，此消息已被服务端丢弃！", Gateway.$(session), remoteAddress);
						}
					}
					
					if(needAck)
					{
						try
						{
							MBObserver retObserver = new MBObserver(){
								@Override
								public void update(boolean sucess, Object extraObj)
								{
									if(sucess)
									{
										logger.debug("[IMCORE-{}<C2C>]【QoS_伪应答_C2S】向{}发送{}的应答包成功,from={}."
												, Gateway.$(session), pFromClient.getFrom(), pFromClient.getFp(), pFromClient.getTo());
									}
								}
							};
							
							LocalSendHelper.replyRecievedBack(session, pFromClient, retObserver);
						}
						catch (Exception e)
						{
							logger.warn(e.getMessage(), e);
						}
					}
				}
			};
			
			LocalSendHelper.sendData(pFromClient, resultObserver);
		}
	}
	
	public static void sendDataS2C(BridgeProcessor bridgeProcessor, Protocal pFromClient, final MBObserver resultObserver) throws Exception
	{
		// TODO just for DEBUG
		OnlineProcessor.getInstance().__printOnline();
		
		boolean sucess = false;

		if(ServerLauncher.bridgeEnabled && !OnlineProcessor.isOnline(pFromClient.getTo()))
		{
			logger.debug("[IMCORE<S2C>-桥接↑]>> 客户端{}不在线，数据[from:{},fp:{},to:{},content:{}] 将通过MQ直发Web服务端（彼时在线则通过web实时发送、否则通过Web端进行离线存储）【第一阶段APP+WEB跨机通信算法】！"
					,pFromClient.getTo(), pFromClient.getFrom(), pFromClient.getFp(), pFromClient.getTo(), pFromClient.getDataContent());

			boolean toMQ = bridgeProcessor.publish(pFromClient.toGsonString());
			if(toMQ)
			{
				logger.debug("[IMCORE<S2C>-桥接↑]>> 服务端的数据已跨机器送出成功【OK】。(数据[from:{},fp:{},to:{},content:{}]【第一阶段APP+WEB跨机通信算法】)"
						, pFromClient.getFrom(), pFromClient.getFp(), pFromClient.getTo(), pFromClient.getDataContent());
				sucess = true;
			}
			else
			{
				logger.error("[IMCORE<S2C>-桥接↑]>> 服务端的数据已跨机器送出失败，请通知管理员检查MQ中间件是否正常工作【NO】。(数据[from:"+pFromClient.getFrom()
						+",fp:{},to:{},content:{}]【第一阶段APP+WEB跨机通信算法】)"
						, pFromClient.getFp(), pFromClient.getTo(), pFromClient.getDataContent());

			}
		}
		else
		{
			LocalSendHelper.sendData(pFromClient, new MBObserver(){
				@Override
				public void update(boolean _sendSucess, Object extraObj)
				{
					if(_sendSucess)
						_sendSucess = true;
					else
						logger.warn("[IMCORE]>> 服务端的通用数据传输消息尝试实时发送没有成功，但上层应用层没有成功，请应用层自行决定此条消息的发送【NO】！");
					
					if(resultObserver != null)
						resultObserver.update(_sendSucess, null);
				}
			});
			
			return;
		}
		
		if(resultObserver != null)
			resultObserver.update(sucess, null);
	}
}
