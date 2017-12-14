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
 * ServerCoreHandler.java at 2017-5-2 15:49:27, code by Jack Jiang.
 * You can contact author with jack.jiang@52im.net or jb2011@163.com.
 */
package net.openmob.mobileimsdk.server;

import java.nio.ByteBuffer;

import net.openmob.mobileimsdk.server.event.MessageQoSEventListenerS2C;
import net.openmob.mobileimsdk.server.event.ServerEventListener;
import net.openmob.mobileimsdk.server.processor.BridgeProcessor;
import net.openmob.mobileimsdk.server.processor.LogicProcessor;
import net.openmob.mobileimsdk.server.processor.OnlineProcessor;
import net.openmob.mobileimsdk.server.protocal.Protocal;
import net.openmob.mobileimsdk.server.protocal.ProtocalType;
import net.openmob.mobileimsdk.server.utils.LocalSendHelper;
import net.openmob.mobileimsdk.server.utils.ServerToolKits;

import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.service.IoHandlerAdapter;
import org.apache.mina.core.session.IdleStatus;
import org.apache.mina.core.session.IoSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ServerCoreHandler extends IoHandlerAdapter 
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

    @Override
    public void exceptionCaught(IoSession session, Throwable cause) throws Exception 
    {
        logger.error("[IMCORE]exceptionCaught捕获到错了，原因是："+cause.getMessage(), cause);
        session.close(true);
    }

    @Override
    public void messageReceived(IoSession session, Object message)
            throws Exception 
    {
    	if (message instanceof IoBuffer) 
    	{
            IoBuffer buffer = (IoBuffer) message;
            Protocal pFromClient = ServerToolKits.fromIOBuffer(buffer);
            
            String remoteAddress = ServerToolKits.clientInfoToString(session);
            
            switch(pFromClient.getType())
            {
	        	case ProtocalType.C.FROM_CLIENT_TYPE_OF_RECIVED:
	        	{
	        		logger.info("[IMCORE]>> 收到客户端"+remoteAddress+"的ACK应答包发送请求.");
	        			
	        		if(!OnlineProcessor.isLogined(session))
	        		{
	        			LocalSendHelper.replyDataForUnlogined(session, pFromClient);
	        			return;
	        		}

	        		logicProcessor.processACK(pFromClient, remoteAddress);
	        		break;
	        	}
	        	case ProtocalType.C.FROM_CLIENT_TYPE_OF_COMMON$DATA:
	        	{
	        		logger.info("[IMCORE]>> 收到客户端"+remoteAddress+"的通用数据发送请求.");
	        		
	        		if(serverEventListener != null)
	        		{
	        			if(!OnlineProcessor.isLogined(session))
	        			{
	        				LocalSendHelper.replyDataForUnlogined(session, pFromClient);
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
	        			logger.warn("[IMCORE]>> 收到客户端"+remoteAddress+"的通用数据传输消息，但回调对象是null，回调无法继续.");
	        		}
	        		break;
	        	}
	        	case ProtocalType.C.FROM_CLIENT_TYPE_OF_KEEP$ALIVE:
	        	{
	        		if(!OnlineProcessor.isLogined(session))
	        		{
	        			LocalSendHelper.replyDataForUnlogined(session, pFromClient);
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
            		logger.info("[IMCORE]>> 收到客户端"+remoteAddress+"的退出登陆请求.");
            		session.close(true);
            		break;
            	}
	        	case ProtocalType.C.FROM_CLIENT_TYPE_OF_ECHO:
	        		pFromClient.setType(ProtocalType.S.FROM_SERVER_TYPE_OF_RESPONSE$ECHO);
	        		LocalSendHelper.sendData(session, pFromClient);
	        		break;
            	default:
            		logger.warn("[IMCORE]【注意】收到的客户端"+remoteAddress+"消息类型："+pFromClient.getType()+"，但目前该类型服务端不支持解析和处理！");
            		break;
            }
        }
    	else
    	{
    		logger.error("[IMCORE]【注意】收到了未知数据类型的用户消息(messageReceived), message.class="+message.getClass()
        			+ ", IoBuffer?"+(message instanceof IoBuffer)
        			+ ", ByteBuffer?"+(message instanceof ByteBuffer));
    	}
    }
    
    @Override
    public void sessionClosed(IoSession session) throws Exception 
    {
    	String user_id = OnlineProcessor.getUserIdFromSession(session);
    	IoSession sessionInOnlinelist = OnlineProcessor.getInstance().getOnlineSession(user_id);
    	
    	logger.info("[IMCORE]"+ServerToolKits.clientInfoToString(session)+"的会话已关闭(user_id="+user_id+")了...");
    	
    	// TODO just for DEBUG：以下代码仅作Debug之用，您随时可删除之！
    	{// DEBUG Start
    		
    		logger.info(".......... 【0】[当前正在被关闭的session] session.hashCode="+session.hashCode()
    			+", session.ip+port="+session.getRemoteAddress());
    		
    		if(sessionInOnlinelist != null)
    		{
    			logger.info(".......... 【1】[处于在线列表中的session] session.hashCode="+sessionInOnlinelist.hashCode()
    				+", session.ip+port="+sessionInOnlinelist.getRemoteAddress());
    		}
    	}// DEBUG END
    	
    	if(user_id != null)
    	{
    		//## Bug FIX: 20171211 START
    		if(sessionInOnlinelist != null && session != null && session == sessionInOnlinelist)
    		//## Bug FIX: 20171211 END
    		{
    			OnlineProcessor.getInstance().removeUser(user_id);
    			if(serverEventListener != null)
    				serverEventListener.onUserLogoutAction_CallBack(user_id, null, session);
    			else
    				logger.debug("[IMCORE]>> 会话"+ServerToolKits.clientInfoToString(session)
    						+"被系统close了，但回调对象是null，没有进行回调通知.");
    		}
    		else
    		{
    			logger.warn("[IMCORE]【2】【注意】会话"+ServerToolKits.clientInfoToString(session)
    					+"不在在线列表中，意味着它是被客户端弃用的，本次忽略这条关闭事件即可！");
    		}
    	}
    	else
    	{
    		logger.warn("[IMCORE]【注意】会话"+ServerToolKits.clientInfoToString(session)+"被系统close了，但它里面没有存放user_id，这个会话是何时建立的？");
    	}
    }

    @Override
    public void sessionCreated(IoSession session) throws Exception 
    {
    	logger.info("[IMCORE]与"+ServerToolKits.clientInfoToString(session)+"的会话建立(sessionCreated)了...");
    }

	@Override
	public void sessionOpened(IoSession session) throws Exception 
	{
		logger.info("[IMCORE]与"+ServerToolKits.clientInfoToString(session)+"的会话(sessionOpened)打开了...");
	}

    @Override
    public void sessionIdle(IoSession session, IdleStatus status) throws Exception 
    {
    	logger.info("[IMCORE]Session idle...");
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
