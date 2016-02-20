/*
 * Copyright (C) 2016 即时通讯网(52im.net) The MobileIMSDK Project. 
 * All rights reserved.
 * Project URL:https://github.com/JackJiang2011/MobileIMSDK
 *  
 * 即时通讯网(52im.net) - 即时通讯技术社区! PROPRIETARY/CONFIDENTIAL.
 * Use is subject to license terms.
 * 
 * ServerLauncher.java at 2016-2-20 11:26:02, code by Jack Jiang.
 * You can contact author with jack.jiang@52im.net or jb2011@163.com.
 */
package net.openmob.mobileimsdk.server;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.concurrent.Executors;
import net.openmob.mobileimsdk.server.event.MessageQoSEventListenerS2C;
import net.openmob.mobileimsdk.server.event.ServerEventListener;
import net.openmob.mobileimsdk.server.protocal.Protocal;
import net.openmob.mobileimsdk.server.qos.QoS4ReciveDaemonC2S;
import net.openmob.mobileimsdk.server.qos.QoS4SendDaemonS2C;
import org.apache.mina.core.filterchain.DefaultIoFilterChainBuilder;
import org.apache.mina.core.session.ExpiringSessionRecycler;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.executor.ExecutorFilter;
import org.apache.mina.transport.socket.DatagramSessionConfig;
import org.apache.mina.transport.socket.nio.NioDatagramAcceptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class ServerLauncher
{
	private static Logger logger = LoggerFactory.getLogger(ServerLauncher.class);

	public static String appKey = null;
	public static int PORT = 7901;
	public static int SESION_RECYCLER_EXPIRE = 10;

	private boolean running = false;
	protected ServerCoreHandler serverCoreHandler = null;
	private NioDatagramAcceptor acceptor = null;

	public ServerLauncher() throws IOException
	{
	}

	public boolean isRunning()
	{
		return this.running;
	}

	public void shutdown()
	{
		// ** 取消服务端网络监听
    	if(acceptor != null)
    		acceptor.dispose();
    	
    	// ** 停止QoS机制（目前服务端只支持C2S模式的QoS）下的防重复检查线程
    	QoS4ReciveDaemonC2S.getInstance().stop();
    	// ** 停止服务端对S2C模式下QoS机制的丢包重传和离线通知线程
    	QoS4SendDaemonS2C.getInstance().stop();
    	
    	// ** 设置启动标识
    	this.running = false;
	}

	public void startup() throws IOException
	{
		this.serverCoreHandler = initServerCoreHandler();
		initListeners();
		this.acceptor = initAcceptor();
		initFilter(this.acceptor);
		initSessionConfig(this.acceptor);
		QoS4ReciveDaemonC2S.getInstance().startup();
		QoS4SendDaemonS2C.getInstance().startup(true).setServerLauncher(this);
		this.acceptor.bind(new InetSocketAddress(PORT));

		this.running = true;
		logger.info("[IMCORE]UDP服务器正在端口" + PORT + "上监听中...");
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
		acceptor.setHandler(this.serverCoreHandler);
		acceptor.setSessionRecycler(new ExpiringSessionRecycler(SESION_RECYCLER_EXPIRE));
		return acceptor;
	}

	protected void initFilter(NioDatagramAcceptor acceptor)
	{
		DefaultIoFilterChainBuilder chain = acceptor.getFilterChain();
	}

	protected void initSessionConfig(NioDatagramAcceptor acceptor)
	{
		DatagramSessionConfig dcfg = acceptor.getSessionConfig();
		dcfg.setReuseAddress(true);
//     	dcfg.setReadBufferSize(4096);//设置接收最大字节默认2048
    	dcfg.setReceiveBufferSize(1024);//设置输入缓冲区的大小，调整到2048后性能反而降低
    	dcfg.setSendBufferSize(1024);//1024//设置输出缓冲区的大小，调整到2048后性能反而降低
	}

	public ServerEventListener getServerEventListener()
	{
		return this.serverCoreHandler.getServerEventListener();
	}

	public void setServerEventListener(ServerEventListener serverEventListener)
	{
		this.serverCoreHandler.setServerEventListener(serverEventListener);
	}

	public MessageQoSEventListenerS2C getServerMessageQoSEventListener()
	{
		return this.serverCoreHandler.getServerMessageQoSEventListener();
	}

	public void setServerMessageQoSEventListener(MessageQoSEventListenerS2C serverMessageQoSEventListener)
	{
		this.serverCoreHandler.setServerMessageQoSEventListener(serverMessageQoSEventListener);
	}

	public static boolean sendData(int from_user_id, int to_user_id, String dataContent) throws Exception
	{
		return ServerCoreHandler.sendData(from_user_id, to_user_id, dataContent);
	}

	public static boolean sendData(int from_user_id, int to_user_id, String dataContent, boolean QoS) throws Exception
	{
		return ServerCoreHandler.sendData(from_user_id, to_user_id, dataContent, QoS);
	}

	public static boolean sendData(int from_user_id, int to_user_id
			, String dataContent, boolean QoS, String fingerPrint) throws Exception
	{
		return ServerCoreHandler.sendData(from_user_id, to_user_id, dataContent, 
				QoS, fingerPrint);
	}

	public static boolean sendData(Protocal p) throws Exception
	{
		return ServerCoreHandler.sendData(p);
	}

	public static boolean sendData(IoSession session, Protocal p) throws Exception
	{
		return ServerCoreHandler.sendData(session, p);
	}

	public static void setSenseMode(SenseMode mode)
	{
		int expire = 0;

		switch (mode)
		{
			case MODE_3S:
				// 误叛容忍度为丢3个包
				expire = 3 * 3 + 1;
				break;
			case MODE_10S:
				// 误叛容忍度为丢2个包
				expire = 10 * 2 + 1;
	    		break;
			case MODE_30S:
				// 误叛容忍度为丢2个包
				expire = 30 * 2 + 2;
	    		break;
			case MODE_60S:
				// 误叛容忍度为丢2个包
				expire = 60 * 2 + 2;
	    		break;
			case MODE_120S:
				// 误叛容忍度为丢2个包
				expire = 120 * 2 + 2;
	    		break;
		}

		if (expire > 0)
			SESION_RECYCLER_EXPIRE = expire;
	}

	public static enum SenseMode
	{
		MODE_3S, 

		MODE_10S, 

		MODE_30S, 

		MODE_60S, 

		MODE_120S;
	}
	
//	public static void main(String[] args) throws IOException 
//  {
//      new ServerLauncher().startup();
//  }
}