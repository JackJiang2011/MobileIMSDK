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
 * LogicProcessor.java at 2017-5-2 15:49:27, code by Jack Jiang.
 * You can contact author with jack.jiang@52im.net or jb2011@163.com.
 */
package net.openmob.mobileimsdk.server.processor;

import net.nettime.mobileimsdk.server.bridge.QoS4SendDaemonB2C;
import net.openmob.mobileimsdk.server.ServerCoreHandler;
import net.openmob.mobileimsdk.server.protocal.Protocal;
import net.openmob.mobileimsdk.server.protocal.ProtocalFactory;
import net.openmob.mobileimsdk.server.protocal.c.PLoginInfo;
import net.openmob.mobileimsdk.server.qos.QoS4ReciveDaemonC2S;
import net.openmob.mobileimsdk.server.qos.QoS4SendDaemonS2C;
import net.openmob.mobileimsdk.server.utils.GlobalSendHelper;
import net.openmob.mobileimsdk.server.utils.LocalSendHelper;

import org.apache.mina.core.session.IoSession;
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
			IoSession session, Protocal pFromClient, String remoteAddress) throws Exception
	{
		GlobalSendHelper.sendDataC2C(bridgeProcessor, session, pFromClient
				, remoteAddress, this.serverCoreHandler);
	}
	
	public void processC2SMessage(IoSession session, Protocal pFromClient, String remoteAddress) throws Exception
	{
		if(pFromClient.isQoS())// && processedOK)
		{
			if(QoS4ReciveDaemonC2S.getInstance().hasRecieved(pFromClient.getFp()))
			{
				if(QoS4ReciveDaemonC2S.getInstance().isDebugable())
					logger.debug("[IMCORE-本机QoS！]【QoS机制】"+pFromClient.getFp()
							+"已经存在于发送列表中，这是重复包，通知业务处理层收到该包罗！");
				
				QoS4ReciveDaemonC2S.getInstance().addRecieved(pFromClient);
				boolean receivedBackSendSucess = LocalSendHelper.replyDelegateRecievedBack(session, pFromClient);
				if(receivedBackSendSucess)
					logger.debug("[IMCORE-本机QoS！]【QoS_应答_C2S】向"+pFromClient.getFrom()+"发送"+pFromClient.getFp()
							+"的应答包成功了,from="+pFromClient.getTo()+".");
				return;
			}
			
			QoS4ReciveDaemonC2S.getInstance().addRecieved(pFromClient);
			boolean receivedBackSendSucess = LocalSendHelper.replyDelegateRecievedBack(session, pFromClient);
			if(receivedBackSendSucess)
				logger.debug("[IMCORE-本机QoS！]【QoS_应答_C2S】向"+pFromClient.getFrom()+"发送"+pFromClient.getFp()
						+"的应答包成功了,from="+pFromClient.getTo()+".");
		}

//		boolean processedOK = this.serverCoreHandler.getServerEventListener().onTransBuffer_CallBack(
//				pFromClient.getTo(), pFromClient.getFrom(), pFromClient.getDataContent()
//				, pFromClient.getFp(), pFromClient.getTypeu(), session);
		boolean processedOK = this.serverCoreHandler.getServerEventListener().onTransBuffer_C2S_CallBack(pFromClient, session);
	}
	
	public void processACK(Protocal pFromClient, String remoteAddress) throws Exception
	{
		if("0".equals(pFromClient.getTo()))
		{
			String theFingerPrint = pFromClient.getDataContent();
			logger.debug("[IMCORE-本机QoS！]【QoS机制_S2C】收到接收者"+pFromClient.getFrom()+"回过来的指纹为"+theFingerPrint+"的应答包.");

			if(this.serverCoreHandler.getServerMessageQoSEventListener() != null)
				this.serverCoreHandler.getServerMessageQoSEventListener()
					.messagesBeReceived(theFingerPrint);

			QoS4SendDaemonS2C.getInstance().remove(theFingerPrint);
		}
		else
		{
			OnlineProcessor.getInstance().__printOnline();
			
			String theFingerPrint = pFromClient.getDataContent();

			boolean isBridge = pFromClient.isBridge();
			
			if(isBridge)
			{
				logger.debug("[IMCORE-桥接QoS！]【QoS机制_S2C】收到接收者"+pFromClient.getFrom()+"回过来的指纹为"+theFingerPrint+"的应答包.");
				QoS4SendDaemonB2C.getInstance().remove(theFingerPrint);
			}
			else
			{
				boolean sendOK = LocalSendHelper.sendData(pFromClient);
				logger.debug("[IMCORE-本机QoS！]【QoS机制_C2C】"+pFromClient.getFrom()+"发给"+pFromClient.getTo()
						+"的指纹为"+theFingerPrint+"的应答包已成功转发？"+sendOK);
			}
		}
	}
	
	public void processLogin(IoSession session, Protocal pFromClient, String remoteAddress) throws Exception
	{
		PLoginInfo loginInfo = ProtocalFactory.parsePLoginInfo(pFromClient.getDataContent());
		logger.info("[IMCORE]>> 客户端"+remoteAddress+"发过来的登陆信息内容是：loginInfo="
				+loginInfo.getLoginUserId()+"|getToken="+loginInfo.getLoginToken());
		
		if(loginInfo == null || loginInfo.getLoginUserId() == null)
		{
			logger.warn("[IMCORE]>> 收到客户端"+remoteAddress
					+"登陆信息，但loginInfo或loginInfo.getLoginUserId()是null，登陆无法继续[loginInfo="+loginInfo
					+",loginInfo.getLoginUserId()="+loginInfo.getLoginUserId()+"]！");
			return;
		}
		
		if(serverCoreHandler.getServerEventListener() != null)
		{
			boolean alreadyLogined = OnlineProcessor.isLogined(session);//(_try_user_id != -1);
			if(alreadyLogined)
			{
				logger.debug("[IMCORE]>> 【注意】客户端"+remoteAddress+"的会话正常且已经登陆过，而此时又重新登陆：getLoginName="
        				+loginInfo.getLoginUserId()+"|getLoginPsw="+loginInfo.getLoginToken());
				
				boolean sendOK = LocalSendHelper.sendData(session
						, ProtocalFactory.createPLoginInfoResponse(0, loginInfo.getLoginUserId()));
				if(sendOK)
				{
					session.setAttribute(OnlineProcessor.USER_ID_IN_SESSION_ATTRIBUTE, loginInfo.getLoginUserId());
					OnlineProcessor.getInstance().putUser(loginInfo.getLoginUserId(), session);
					
					serverCoreHandler.getServerEventListener().onUserLoginAction_CallBack(
							loginInfo.getLoginUserId(), loginInfo.getExtra(), session);
				}
				else
				{
					logger.warn("[IMCORE]>> 发给客户端"+remoteAddress+"的登陆成功信息发送失败了！");
				}
			}
			else
			{
				int code = serverCoreHandler.getServerEventListener().onVerifyUserCallBack(
						loginInfo.getLoginUserId(), loginInfo.getLoginToken(), loginInfo.getExtra(), session);
				if(code == 0)
				{
					boolean sendOK = LocalSendHelper.sendData(session
							, ProtocalFactory.createPLoginInfoResponse(code, loginInfo.getLoginUserId()));
					if(sendOK)
					{
						session.setAttribute(OnlineProcessor.USER_ID_IN_SESSION_ATTRIBUTE, loginInfo.getLoginUserId());
						OnlineProcessor.getInstance().putUser(loginInfo.getLoginUserId(), session);
						serverCoreHandler.getServerEventListener()
							.onUserLoginAction_CallBack(loginInfo.getLoginUserId(), loginInfo.getExtra(), session);
					}
					else
						logger.warn("[IMCORE]>> 发给客户端"+remoteAddress+"的登陆成功信息发送失败了！");
				}
				else
				{
					LocalSendHelper.sendData(session, ProtocalFactory.createPLoginInfoResponse(code, "-1"));
				}
			}
		}
		else
		{
			logger.warn("[IMCORE]>> 收到客户端"+remoteAddress+"登陆信息，但回调对象是null，没有进行回调.");
		}
	}

	public void processKeepAlive(IoSession session, Protocal pFromClient
			, String remoteAddress) throws Exception
	{
		String userId = OnlineProcessor.getUserIdFromSession(session);
		if(userId != null)
		{
			LocalSendHelper.sendData(ProtocalFactory.createPKeepAliveResponse(userId));
		}
		else
		{
			logger.warn("[IMCORE]>> Server在回客户端"+remoteAddress+"的响应包时，调用getUserIdFromSession返回null，用户在这一瞬间掉线了？！");
		}
	}
}
