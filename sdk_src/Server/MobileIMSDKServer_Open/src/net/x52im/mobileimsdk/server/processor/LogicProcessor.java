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
 * LogicProcessor.java at 2021-8-4 21:24:14, code by Jack Jiang.
 */
package net.x52im.mobileimsdk.server.processor;

import io.netty.channel.Channel;
import net.x52im.mobileimsdk.server.ServerCoreHandler;
import net.x52im.mobileimsdk.server.network.Gateway;
import net.x52im.mobileimsdk.server.network.GatewayUDP;
import net.x52im.mobileimsdk.server.network.MBObserver;
import net.x52im.mobileimsdk.server.protocal.Protocal;
import net.x52im.mobileimsdk.server.protocal.ProtocalFactory;
import net.x52im.mobileimsdk.server.protocal.c.PLoginInfo;
import net.x52im.mobileimsdk.server.qos.QoS4ReciveDaemonC2S;
import net.x52im.mobileimsdk.server.qos.QoS4SendDaemonS2C;
import net.x52im.mobileimsdk.server.utils.GlobalSendHelper;
import net.x52im.mobileimsdk.server.utils.LocalSendHelper;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LogicProcessor
{
	private static Logger logger = LoggerFactory.getLogger(LogicProcessor.class);  
	private ServerCoreHandler serverCoreHandler = null;

	public LogicProcessor(ServerCoreHandler serverCoreHandler)
	{
		this.serverCoreHandler = serverCoreHandler;
	}
	
	public void processC2CMessage(BridgeProcessor bridgeProcessor,
			Channel session, Protocal pFromClient, String remoteAddress) throws Exception
	{
		GlobalSendHelper.sendDataC2C(bridgeProcessor, session, pFromClient, remoteAddress, this.serverCoreHandler);
	}
	
	public void processC2SMessage(Channel session, final Protocal pFromClient, String remoteAddress) throws Exception
	{
		if(pFromClient.isQoS())// && processedOK)
		{
			boolean hasRecieved = QoS4ReciveDaemonC2S.getInstance().hasRecieved(pFromClient.getFp());
			
			QoS4ReciveDaemonC2S.getInstance().addRecieved(pFromClient);
			LocalSendHelper.replyRecievedBack(session
					, pFromClient
					, new MBObserver(){
						@Override
						public void update(boolean receivedBackSendSucess, Object extraObj)
						{
							if(receivedBackSendSucess)
								logger.debug("[IMCORE-本机QoS！]【QoS_应答_C2S】向"+pFromClient.getFrom()+"发送"+pFromClient.getFp()
										+"的应答包成功了,from="+pFromClient.getTo()+".");
						}
					}
			);
		
			if(hasRecieved)
			{
				if(QoS4ReciveDaemonC2S.getInstance().isDebugable())
					logger.debug("[IMCORE-本机QoS！]【QoS机制】"+pFromClient.getFp()+"因已经存在于发送列表中，这是重复包，本次忽略通知业务处理层（只需要回复ACK就行了）！");
				
				return;
			}	
		}

		boolean processedOK = this.serverCoreHandler.getServerEventListener().onTransferMessage4C2S(pFromClient, session);
	}
	
	public void processACK(final Protocal pFromClient, final String remoteAddress) throws Exception
	{
		String theFingerPrint = pFromClient.getDataContent();
		logger.debug("[IMCORE-本机QoS！]【QoS机制_S2C】收到接收者"+pFromClient.getFrom()+"回过来的指纹为"+theFingerPrint+"的应答包.");

		if(this.serverCoreHandler.getServerMessageQoSEventListener() != null)
			this.serverCoreHandler.getServerMessageQoSEventListener().messagesBeReceived(theFingerPrint);

		QoS4SendDaemonS2C.getInstance().remove(theFingerPrint);
	}

	public void processLogin(final Channel session, final Protocal pFromClient, final String remoteAddress) throws Exception
	{
		final PLoginInfo loginInfo = ProtocalFactory.parsePLoginInfo(pFromClient.getDataContent());
		logger.info("[IMCORE-{}]>> 客户端"+remoteAddress+"发过来的登陆信息内容是：uid={}、token={}、firstLoginTime={}"
				, Gateway.$(session), loginInfo.getLoginUserId(), loginInfo.getLoginToken(), loginInfo.getFirstLoginTime());
		
		if(loginInfo == null || loginInfo.getLoginUserId() == null)
		{
			logger.warn("[IMCORE-{}]>> 收到客户端{}登陆信息，但loginInfo或loginInfo.getLoginUserId()是null，登陆无法继续[uid={}、token={}、firstLoginTime={}]！"
					, Gateway.$(session), remoteAddress, loginInfo, loginInfo.getLoginUserId(), loginInfo.getFirstLoginTime());
			
			if(!GatewayUDP.isUDPChannel(session))
				session.close();
			
			return;
		}
		
		if(serverCoreHandler.getServerEventListener() != null)
		{
			boolean alreadyLogined = OnlineProcessor.isLogined(session);//(_try_user_id != -1);
			if(alreadyLogined)
			{
				logger.debug("[IMCORE-{}]>> 【注意】客户端{}的会话正常且已经登陆过，而此时又重新登陆：uid={}、token={}、firstLoginTime={}"
        				, Gateway.$(session), remoteAddress, loginInfo.getLoginUserId(), loginInfo.getLoginToken(), loginInfo.getFirstLoginTime());
				processLoginSucessSend(session, loginInfo, remoteAddress);
			}
			else
			{
				int code = serverCoreHandler.getServerEventListener().onUserLoginVerify(
						loginInfo.getLoginUserId(), loginInfo.getLoginToken(), loginInfo.getExtra(), session);
				if(code == 0)
				{
					processLoginSucessSend(session, loginInfo, remoteAddress);
				}
				else
				{
					logger.warn("[IMCORE-{}]>> 客户端{}登陆失败【no】，马上返回失败信息，并关闭其会话。。。", Gateway.$(session), remoteAddress);
					
					MBObserver sendResultObserver = new MBObserver(){
						@Override
						public void update(boolean sendOK, Object extraObj)
						{
							logger.warn("[IMCORE-{}]>> 客户端{}登陆失败信息返回成功？{}（会话即将关闭）", Gateway.$(session), remoteAddress, sendOK);
							session.close();
						}
					};
					
					LocalSendHelper.sendData(session, ProtocalFactory.createPLoginInfoResponse(code, -1, "-1"), GatewayUDP.isUDPChannel(session)?null:sendResultObserver);
				}
			}
		}
		else
		{
			logger.warn("[IMCORE-{}]>> 收到客户端{}登陆信息，但回调对象是null，没有进行回调.", Gateway.$(session), remoteAddress);
		}
	}
	
	private void processLoginSucessSend(final Channel session, final PLoginInfo loginInfo, final String remoteAddress) throws Exception
	{
		final long firstLoginTimeFromClient = loginInfo.getFirstLoginTime();
		final boolean firstLogin = PLoginInfo.isFirstLogin(firstLoginTimeFromClient);//(firstLoginTimeFromClient <= 0);
		final long firstLoginTimeToClient = (firstLogin? System.currentTimeMillis() : firstLoginTimeFromClient);

		MBObserver sendResultObserver = new MBObserver(){
			@Override
			public void update(boolean __sendOK, Object extraObj)
			{
				if(__sendOK)
				{
					boolean putOK = OnlineProcessor.getInstance().putUser(loginInfo.getLoginUserId(), firstLoginTimeFromClient, session);
					if(putOK)
					{
						OnlineProcessor.setUserIdForChannel(session, loginInfo.getLoginUserId());
						OnlineProcessor.setFirstLoginTimeForChannel(session, firstLoginTimeToClient);
						serverCoreHandler.getServerEventListener().onUserLoginSucess(loginInfo.getLoginUserId(), loginInfo.getExtra(), session);
					}
				}
				else
					logger.warn("[IMCORE-{}]>> 发给客户端{}的登陆成功信息发送失败了【no】！", Gateway.$(session), remoteAddress);
				
			}
		};
		LocalSendHelper.sendData(session, ProtocalFactory.createPLoginInfoResponse(0, firstLoginTimeToClient, loginInfo.getLoginUserId()), sendResultObserver);
	}

	public void processKeepAlive(Channel session, Protocal pFromClient, String remoteAddress) throws Exception
	{
		String userId = OnlineProcessor.getUserIdFromChannel(session);
		if(userId != null){
			LocalSendHelper.sendData(ProtocalFactory.createPKeepAliveResponse(userId), null);
		}
		else{
			logger.warn("[IMCORE-{}]>> Server在回客户端{}的响应包时，调用getUserIdFromSession返回null，用户在这一瞬间掉线了？！", Gateway.$(session), remoteAddress);
		}
	}
}
