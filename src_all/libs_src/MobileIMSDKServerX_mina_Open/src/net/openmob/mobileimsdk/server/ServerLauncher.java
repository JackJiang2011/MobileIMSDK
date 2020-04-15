/*
 * Copyright (C) 2020  即时通讯网(52im.net) & Jack Jiang.
 * The MobileIMSDK_X_netty (MobileIMSDK v4.x MINA版) Project. 
 * All rights reserved.
 * 
 * > Github地址：https://github.com/JackJiang2011/MobileIMSDK
 * > 文档地址：  http://www.52im.net/forum-89-1.html
 * > 技术社区：  http://www.52im.net/
 * > 技术交流群：320837163 (http://www.52im.net/topic-qqgroup.html)
 * > 作者公众号：“即时通讯技术圈】”，欢迎关注！
 * > 联系作者：  http://www.52im.net/thread-2792-1-1.html
 *  
 * "即时通讯网(52im.net) - 即时通讯开发者社区!" 推荐开源工程。
 * 
 * ServerLauncher.java at 2020-4-14 18:50:34, code by Jack Jiang.
 */
package net.openmob.mobileimsdk.server;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.concurrent.Executors;

import net.nettime.mobileimsdk.server.bridge.QoS4ReciveDaemonC2B;
import net.nettime.mobileimsdk.server.bridge.QoS4SendDaemonB2C;
import net.openmob.mobileimsdk.server.event.MessageQoSEventListenerS2C;
import net.openmob.mobileimsdk.server.event.ServerEventListener;
import net.openmob.mobileimsdk.server.qos.QoS4ReciveDaemonC2S;
import net.openmob.mobileimsdk.server.qos.QoS4SendDaemonS2C;

import org.apache.mina.core.filterchain.DefaultIoFilterChainBuilder;
import org.apache.mina.core.session.ExpiringSessionRecycler;
import org.apache.mina.filter.executor.ExecutorFilter;
import org.apache.mina.transport.socket.DatagramSessionConfig;
import org.apache.mina.transport.socket.nio.NioDatagramAcceptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class ServerLauncher 
{
	private static Logger logger = LoggerFactory.getLogger(ServerLauncher.class); 

	public static boolean debug = true;
	public static String appKey = null;
	public static int PORT = 7901;
	public static int SESION_RECYCLER_EXPIRE = 10;
	public static boolean bridgeEnabled = false;

	private boolean running = false;
	protected ServerCoreHandler serverCoreHandler = null; 
	private NioDatagramAcceptor acceptor = null;

	public ServerLauncher() throws IOException 
	{
	}

	public boolean isRunning()
	{
		return running;
	}

	public void startup() throws IOException
	{	
		if(!this.running)
		{
			serverCoreHandler = initServerCoreHandler();
			initListeners();
			acceptor = initAcceptor();
			initFilter(acceptor);
			initSessionConfig(acceptor);

			QoS4ReciveDaemonC2S.getInstance().startup();
			QoS4SendDaemonS2C.getInstance().startup(true).setServerLauncher(this);

			if(ServerLauncher.bridgeEnabled){
				QoS4ReciveDaemonC2B.getInstance().startup();
				QoS4SendDaemonB2C.getInstance().startup(true).setServerLauncher(this);
				serverCoreHandler.lazyStartupBridgeProcessor();
				logger.info("[IMCORE] 配置项：已开启与MobileIMSDK Web的互通.");
			}
			else{
				logger.info("[IMCORE] 配置项：未开启与MobileIMSDK Web的互通.");
			}

			acceptor.bind(new InetSocketAddress(PORT));

			this.running = true;

			logger.info("[IMCORE] 基于MobileIMSDK的UDP服务正在端口" + PORT+"上监听中...");
		}
		else
		{
			logger.warn("[IMCORE] 基于MobileIMSDK的UDP服务正在运行中" +
					"，本次startup()失败，请先调用shutdown()后再试！");
		}
    }
    
    public void shutdown()
    {
    	if(acceptor != null)
    		acceptor.dispose();
    	
    	QoS4ReciveDaemonC2S.getInstance().stop();
    	QoS4SendDaemonS2C.getInstance().stop();
    	
    	if(ServerLauncher.bridgeEnabled){
    		QoS4ReciveDaemonC2B.getInstance().stop();
    		QoS4SendDaemonB2C.getInstance().stop();
    	}
    	
    	this.running = false;
    }
    
    protected ServerCoreHandler initServerCoreHandler()
    {
    	return new ServerCoreHandler();
    }
    
    protected abstract void initListeners();
    
    protected NioDatagramAcceptor initAcceptor()
    {
    	NioDatagramAcceptor acceptor = new NioDatagramAcceptor();
    	acceptor.getFilterChain()
    		.addLast("threadPool", new ExecutorFilter(Executors.newCachedThreadPool())); 
    	
    	acceptor.setHandler(serverCoreHandler);
    	acceptor.setSessionRecycler(new ExpiringSessionRecycler(SESION_RECYCLER_EXPIRE));//15));//10));
    	
    	return acceptor;
    }
    
    protected void initFilter(NioDatagramAcceptor acceptor)
    {
    	DefaultIoFilterChainBuilder chain = acceptor.getFilterChain();
//      chain.addLast("logger", new LoggingFilter());
//		chain.addLast("myChin", new ProtocolCodecFilter(new ObjectSerializationCodecFactory()));  
    }
    
    protected void initSessionConfig(NioDatagramAcceptor acceptor)
    {
    	DatagramSessionConfig dcfg = acceptor.getSessionConfig();
    	dcfg.setReuseAddress(true);
//     	dcfg.setReadBufferSize(4096);    // 设置接收最大字节默认2048
    	dcfg.setReceiveBufferSize(1024); // 设置输入缓冲区的大小，调整到2048后性能反而降低
    	dcfg.setSendBufferSize(1024);    // 设置输出缓冲区的大小，调整到2048后性能反而降低
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
}
