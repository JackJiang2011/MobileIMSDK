/*
 * Copyright (C) 2023  即时通讯网(52im.net) & Jack Jiang.
 * The MobileIMSDK v6.4 Project. 
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
 * ServerCoreHandler.java at 2023-9-21 15:24:55, code by Jack Jiang.
 */
package net.x52im.mobileimsdk.server;

import io.netty.channel.Channel;
import net.x52im.mobileimsdk.server.event.MessageQoSEventListenerS2C;
import net.x52im.mobileimsdk.server.event.ServerEventListener;
import net.x52im.mobileimsdk.server.network.Gateway;
import net.x52im.mobileimsdk.server.processor.BridgeProcessor;
import net.x52im.mobileimsdk.server.processor.LogicProcessor;
import net.x52im.mobileimsdk.server.processor.OnlineProcessor;
import net.x52im.mobileimsdk.server.protocal.Protocal;
import net.x52im.mobileimsdk.server.protocal.ProtocalType;
import net.x52im.mobileimsdk.server.utils.LocalSendHelper;
import net.x52im.mobileimsdk.server.utils.ServerToolKits;

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
				serverEventListener.onTransferMessage4C2C(p);
			}

			@Override
			protected boolean offlineC2CProcessCallback(Protocal p){
				return serverEventListener.onTransferMessage_RealTimeSendFaild(p);
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
        logger.debug("[IMCORE-"+Gateway.$(session)+"]此客户端的Channel抛出了exceptionCaught，原因是："
        		+cause.getMessage()+"，可以提前close掉了哦！", cause);
        session.close();
    }

    public void messageReceived(Channel session, Protocal pFromClient) throws Exception     
	{
    	String remoteAddress = ServerToolKits.clientInfoToString(session);
    	
    	switch(pFromClient.getType())
    	{
	    	case ProtocalType.C.FROM_CLIENT_TYPE_OF_RECIVED:
	    	{
	    		logger.info("[IMCORE-{}]<< 收到客户端{}的ACK应答包发送请求.", Gateway.$(session), remoteAddress);
	
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
	    		logger.info("[IMCORE-{}]<< 收到客户端{}的通用数据发送请求.", Gateway.$(session), remoteAddress);
	
	    		if(serverEventListener != null)
	    		{
	    			if(!OnlineProcessor.isLogined(session))
	    			{
	    				LocalSendHelper.replyDataForUnlogined(session, pFromClient, null);
	    				return;
	    			}
	    			
	    			if("0".equals(pFromClient.getTo())){
	    				if(serverEventListener.onTransferMessage4C2SBefore(pFromClient, session)){
	    					logicProcessor.processC2SMessage(session, pFromClient, remoteAddress);
	    				}
	    			} else{
	    				if(serverEventListener.onTransferMessage4C2CBefore(pFromClient, session)){
	    					logicProcessor.processC2CMessage(bridgeProcessor, session, pFromClient, remoteAddress);
	    				}
	    			}
	    		}
	    		else
	    		{
	    			logger.warn("[IMCORE-{}]<< 收到客户端{}的通用数据传输消息，但回调对象是null，回调无法继续.", Gateway.$(session), remoteAddress);
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
	    		logger.info("[IMCORE-{}]<< 收到客户端{}的退出登陆请求.", Gateway.$(session), remoteAddress);
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
	    		logger.warn("[IMCORE-{}]【注意】收到的客户端{}消息类型：{}，但目前该类型服务端不支持解析和处理！"
	    				, Gateway.$(session), remoteAddress, pFromClient.getType());
	    		break;
	    	}
    	}
    }
    
    public void sessionClosed(Channel session) throws Exception 
    {
    	String user_id = OnlineProcessor.getUserIdFromChannel(session);
    	
    	if(user_id != null)
    	{
	    	Channel sessionInOnlinelist = OnlineProcessor.getInstance().getOnlineSession(user_id);
	    	
	    	logger.info("[IMCORE-{}]{}的会话已关闭(user_id={}, firstLoginTime={})了..."
	    			, Gateway.$(session), ServerToolKits.clientInfoToString(session), user_id, OnlineProcessor.getFirstLoginTimeFromChannel(session));
	    	
	    	// TODO just for DEBUG：以下代码仅作Debug之用，您随时可删除之！
	    	{// DEBUG Start
	    		
	    		logger.info(".......... 【0】[当前正在被关闭的session] session.hashCode={}, session.ip+port={}"
	    				, session.hashCode(), session.remoteAddress());
	    		
	    		if(sessionInOnlinelist != null)
	    		{
	    			logger.info(".......... 【1】[处于在线列表中的session] session.hashCode={}, session.ip+port="
	    					, sessionInOnlinelist.hashCode(), sessionInOnlinelist.remoteAddress());
	    		}
	    	}// DEBUG END
    	
    		//## Bug FIX: 20171211 START
    		if(sessionInOnlinelist != null && session != null && session == sessionInOnlinelist)
    		//## Bug FIX: 20171211 END
    		{
    			int beKickoutCode = OnlineProcessor.getBeKickoutCodeFromChannel(session);
				
    			OnlineProcessor.removeAttributesForChannel(session);
    			OnlineProcessor.getInstance().removeUser(user_id);

    			if(serverEventListener != null)
    				serverEventListener.onUserLogout(user_id, session, beKickoutCode);
    			else
    				logger.debug("[IMCORE-{}]>> 会话{}被系统close了，但回调对象是null，没有进行回调通知."
    						, Gateway.$(session), ServerToolKits.clientInfoToString(session));
    		}
    		else
    		{
    			logger.warn("[IMCORE-{}]【2】【注意】会话{}不在在线列表中，意味着它是被客户端弃用/或被服务端强踢，本次忽略这条关闭事件即可！"
    					, Gateway.$(session), ServerToolKits.clientInfoToString(session));
    		}
    	}
    	else
    	{
    		logger.warn("[IMCORE-{}]【注意】会话{}被系统close了，但它里面没有存放user_id，它很可能是没有成功合法认证而被提前关闭，从而正常释放资源。"
    				, Gateway.$(session), ServerToolKits.clientInfoToString(session));
    	}
    }

    public void sessionCreated(Channel session) throws Exception 
    {
    	logger.info("[IMCORE-{}]与{}的会话建立(channelActive)了...", Gateway.$(session), ServerToolKits.clientInfoToString(session));
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
