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
 * ServerCoreHandler.java at 2017-12-9 11:24:34, code by Jack Jiang.
 * You can contact author with jack.jiang@52im.net or jb2011@163.com.
 */
package net.openmob.mobileimsdk.server;

import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import net.openmob.mobileimsdk.server.event.MessageQoSEventListenerS2C;
import net.openmob.mobileimsdk.server.event.ServerEventListener;
import net.openmob.mobileimsdk.server.processor.BridgeProcessor;
import net.openmob.mobileimsdk.server.processor.LogicProcessor;
import net.openmob.mobileimsdk.server.processor.OnlineProcessor;
import net.openmob.mobileimsdk.server.protocal.Protocal;
import net.openmob.mobileimsdk.server.protocal.ProtocalType;
import net.openmob.mobileimsdk.server.utils.LocalSendHelper;
import net.openmob.mobileimsdk.server.utils.ServerToolKits;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ServerCoreHandler
{
	private static Logger logger = LoggerFactory.getLogger(ServerCoreHandler.class);  
	
	protected ServerEventListener serverEventListener = null;
	protected MessageQoSEventListenerS2C serverMessageQoSEventListener = null;
	
	protected LogicProcessor logicProcessor = null;
	
	protected BridgeProcessor bridgeProcessor = null;
	
    public ServerCoreHandler()
    {
    	logicProcessor = this.createLogicProcessor();
    	
    	if(ServerLauncher.bridgeEnabled)
    		bridgeProcessor = this.createBridgeProcessor();
    }
    
    protected LogicProcessor createLogicProcessor()
    {
    	return new LogicProcessor(this);
    }
    
    protected BridgeProcessor createBridgeProcessor()
    {
    	BridgeProcessor bp = new BridgeProcessor(){
			protected void realtimeC2CSuccessCallback(Protocal p){
				serverEventListener.onTransBuffer_C2C_CallBack(
						p.getTo(), p.getFrom(), p.getDataContent(), p.getFp(), p.getTypeu());
			}

			@Override
			protected boolean offlineC2CProcessCallback(Protocal p){
				return serverEventListener.onTransBuffer_C2C_RealTimeSendFaild_CallBack(
						p.getTo(), p.getFrom(), p.getDataContent(), p.getFp(), p.getTypeu());
			}
    	};
    	return bp;
    }
    
    public void lazyStartupBridgeProcessor()
    {
    	if(ServerLauncher.bridgeEnabled && bridgeProcessor != null)
    	{
    		bridgeProcessor.start();
    	}
    }

    public void exceptionCaught(Channel session, Throwable cause) throws Exception 
    {
        logger.error("[IMCORE-netty]exceptionCaught捕获到错了，原因是："+cause.getMessage(), cause);
        session.close();
    }

    public void messageReceived(Channel session, ByteBuf bytebuf) throws Exception 
    {
    	Protocal pFromClient = ServerToolKits.fromIOBuffer(bytebuf);

    	String remoteAddress = ServerToolKits.clientInfoToString(session);

    	logger.info("---------------------------------------------------------");
    	logger.info("[IMCORE-netty] << 收到客户端"+remoteAddress+"的消息:::"+pFromClient.toGsonString());

    	switch(pFromClient.getType())
    	{
	    	case ProtocalType.C.FROM_CLIENT_TYPE_OF_RECIVED:
	    	{
	    		logger.info("[IMCORE-netty]<< 收到客户端"+remoteAddress+"的ACK应答包发送请求.");
	
	    		if(!OnlineProcessor.isLogined(session))
	    		{
	    			LocalSendHelper.replyDataForUnlogined(session, pFromClient, null);
	    			return;
	    		}
	
	    		logicProcessor.processACK(pFromClient, remoteAddress);
	    		break;
	    	}
	    	case ProtocalType.C.FROM_CLIENT_TYPE_OF_COMMON$DATA:
	    	{
	    		logger.info("[IMCORE-netty]<< 收到客户端"+remoteAddress+"的通用数据发送请求.");
	
	    		if(serverEventListener != null)
	    		{
	    			if(!OnlineProcessor.isLogined(session))
	    			{
	    				LocalSendHelper.replyDataForUnlogined(session, pFromClient, null);
	    				return;
	    			}
	
	    			if("0".equals(pFromClient.getTo()))
	    				logicProcessor.processC2SMessage(session, pFromClient, remoteAddress);
	    			else
	    				logicProcessor.processC2CMessage(bridgeProcessor, session
	    						, pFromClient, remoteAddress);
	    		}
	    		else
	    		{
	    			logger.warn("[IMCORE-netty]<< 收到客户端"+remoteAddress+"的通用数据传输消息，但回调对象是null，回调无法继续.");
	    		}
	    		break;
	    	}
	    	case ProtocalType.C.FROM_CLIENT_TYPE_OF_KEEP$ALIVE:
	    	{
	    		if(!OnlineProcessor.isLogined(session))
	    		{
	    			LocalSendHelper.replyDataForUnlogined(session, pFromClient, null);
	    			return;
	    		}
	    		else
	    			logicProcessor.processKeepAlive(session, pFromClient, remoteAddress);
	
	    		break;
	    	}
	    	case ProtocalType.C.FROM_CLIENT_TYPE_OF_LOGIN:
	    	{
	    		logicProcessor.processLogin(session, pFromClient, remoteAddress);
	    		break;
	    	}
	    	case ProtocalType.C.FROM_CLIENT_TYPE_OF_LOGOUT:
	    	{
	    		logger.info("[IMCORE-netty]<< 收到客户端"+remoteAddress+"的退出登陆请求.");
	    		session.close();
	    		break;
	    	}
	    	case ProtocalType.C.FROM_CLIENT_TYPE_OF_ECHO:
	    	{
	    		pFromClient.setType(ProtocalType.S.FROM_SERVER_TYPE_OF_RESPONSE$ECHO);
	    		LocalSendHelper.sendData(session, pFromClient, null);
	    		break;
	    	}
	    	default:
	    	{
	    		logger.warn("[IMCORE-netty]【注意】收到的客户端"+remoteAddress+"消息类型："+pFromClient.getType()+"，但目前该类型服务端不支持解析和处理！");
	    		break;
	    	}
    	}
    }
    
    public void sessionClosed(Channel session) throws Exception 
    {
    	String user_id = OnlineProcessor.getUserIdFromSession(session);
    	
    	Channel sessionInOnlinelist = OnlineProcessor.getInstance().getOnlineSession(user_id);
    	
    	logger.info("[IMCORE-netty]"+ServerToolKits.clientInfoToString(session)+"的会话已关闭(user_id="+user_id+")了...");
    	
    	if(user_id != null)
    	{
    		if(sessionInOnlinelist != null && session != null && session == sessionInOnlinelist)
    		{
    			OnlineProcessor.getInstance().removeUser(user_id);

    			if(serverEventListener != null)
    				serverEventListener.onUserLogoutAction_CallBack(user_id, null, session);
    			else
    				logger.debug("[IMCORE-netty]>> 会话"+ServerToolKits.clientInfoToString(session)
    						+"被系统close了，但回调对象是null，没有进行回调通知.");
    		}
    		else
    		{
    			logger.warn("[IMCORE-netty]【2】【注意】会话"+ServerToolKits.clientInfoToString(session)
    					+"不在在线列表中，意味着它是被客户端弃用的，本次忽略这条关闭事件即可！");
    		}
    	}
    	else
    	{
    		logger.warn("[IMCORE-netty]【注意】会话"+ServerToolKits.clientInfoToString(session)+"被系统close了，但它里面没有存放user_id，这个会话是何时建立的？");
    	}
    }

    public void sessionCreated(Channel session) throws Exception 
    {
    	logger.info("[IMCORE-netty]与"+ServerToolKits.clientInfoToString(session)+"的会话建立(channelActive)了...");
    }

    public ServerEventListener getServerEventListener()
	{
		return serverEventListener;
	}
	void setServerEventListener(ServerEventListener serverEventListener)
	{
		this.serverEventListener = serverEventListener;
	}
	
	public MessageQoSEventListenerS2C getServerMessageQoSEventListener()
	{
		return serverMessageQoSEventListener;
	}

	void setServerMessageQoSEventListener(MessageQoSEventListenerS2C serverMessageQoSEventListener)
	{
		this.serverMessageQoSEventListener = serverMessageQoSEventListener;
	}

	public BridgeProcessor getBridgeProcessor()
	{
		return bridgeProcessor;
	}
}
