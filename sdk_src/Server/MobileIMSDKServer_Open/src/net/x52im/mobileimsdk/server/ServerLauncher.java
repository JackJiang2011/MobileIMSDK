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
 * ServerLauncher.java at 2022-7-12 16:35:58, code by Jack Jiang.
 */
package net.x52im.mobileimsdk.server;

import java.io.IOException;

import net.x52im.mobileimsdk.server.event.MessageQoSEventListenerS2C;
import net.x52im.mobileimsdk.server.event.ServerEventListener;
import net.x52im.mobileimsdk.server.network.Gateway;
import net.x52im.mobileimsdk.server.network.GatewayTCP;
import net.x52im.mobileimsdk.server.network.GatewayUDP;
import net.x52im.mobileimsdk.server.network.GatewayWebsocket;
import net.x52im.mobileimsdk.server.qos.QoS4ReciveDaemonC2S;
import net.x52im.mobileimsdk.server.qos.QoS4SendDaemonS2C;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class ServerLauncher 
{
	private static Logger logger = LoggerFactory.getLogger(ServerLauncher.class); 
	
	public static boolean serverTimestamp = false;
    public static boolean bridgeEnabled = false;
    public static int supportedGateways = 0;

    protected ServerCoreHandler serverCoreHandler = null; 
    private boolean running = false;
    
    private Gateway udp = null;
    private Gateway tcp = null;
    private Gateway ws = null;
    
    public ServerLauncher() throws IOException 
    {
    	// default do nothing
    }
    
    protected ServerCoreHandler initServerCoreHandler()
    {
    	return new ServerCoreHandler();
    }
    
    protected abstract void initListeners();
    
    protected void initGateways()
    {
    	if(Gateway.isSupportUDP(supportedGateways))
    	{
	    	udp = new GatewayUDP();
	    	udp.init(this.serverCoreHandler);
    	}
    	
    	if(Gateway.isSupportTCP(supportedGateways))
    	{
	    	tcp = new GatewayTCP();
	    	tcp.init(this.serverCoreHandler);
    	}
    	
    	if(Gateway.isSupportWebSocket(supportedGateways))
    	{
    		ws = new GatewayWebsocket();
    		ws.init(this.serverCoreHandler);
    	}
    }
    
    public void startup() throws Exception
    {	
    	if(!this.running)
    	{
    		serverCoreHandler = initServerCoreHandler();
    		initListeners();
    		initGateways();
    		QoS4ReciveDaemonC2S.getInstance().startup();
    		QoS4SendDaemonS2C.getInstance().startup(true).setServerLauncher(this);

    		if(ServerLauncher.bridgeEnabled){
//    			QoS4ReciveDaemonC2B.getInstance().startup();
//    			QoS4SendDaemonB2C.getInstance().startup(true).setServerLauncher(this);
    			serverCoreHandler.lazyStartupBridgeProcessor();

    			logger.info("[IMCORE] 配置项：已开启与MobileIMSDK Web的互通.");
    		}
    		else{
    			logger.info("[IMCORE] 配置项：未开启与MobileIMSDK Web的互通.");
    		}
    		
    		bind();
    		this.running = true;
    	}
    	else
    	{
    		logger.warn("[IMCORE] 基于MobileIMSDK的通信服务正在运行中，本次startup()失败，请先调用shutdown()后再试！");
    	}
    }
    
    protected void bind() throws Exception
    {
    	if(udp != null)
    		udp.bind();
    	if(tcp != null)
    		tcp.bind();
    	if(ws != null)
    		ws.bind();
    }

    public void shutdown()
    {
    	if(udp != null)
    		udp.shutdown();
    	if(tcp != null)
    		tcp.shutdown();
    	if(ws != null)
    		ws.shutdown();
    	
    	QoS4ReciveDaemonC2S.getInstance().stop();
    	QoS4SendDaemonS2C.getInstance().stop();
    	
    	this.running = false;
    }
    
    public ServerEventListener getServerEventListener()
	{
		return serverCoreHandler.getServerEventListener();
	}
	public void setServerEventListener(ServerEventListener serverEventListener)
	{
		this.serverCoreHandler.setServerEventListener(serverEventListener);
	}
	
	public MessageQoSEventListenerS2C getServerMessageQoSEventListener()
	{
		return serverCoreHandler.getServerMessageQoSEventListener();
	}
	public void setServerMessageQoSEventListener(MessageQoSEventListenerS2C serverMessageQoSEventListener)
	{
		this.serverCoreHandler.setServerMessageQoSEventListener(serverMessageQoSEventListener);
	}

	public ServerCoreHandler getServerCoreHandler()
	{
		return serverCoreHandler;
	}
	
    public boolean isRunning()
	{
		return running;
	}
	
//	public static void main(String[] args) throws IOException 
//    {
//        new ServerLauncher().startup();
//    }
}
