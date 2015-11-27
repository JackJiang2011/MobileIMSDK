/*
 * Copyright (C) 2015 Jack Jiang The MobileIMSDK Project. 
 * All rights reserved.
 * Project URL:https://github.com/JackJiang2011/MobileIMSDK
 *  
 * openmob.net PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 * 
 * Archived at 2015-11-27 14:02:01, code by Jack Jiang.
 * You can contact author with jack.jiang@openmob.net or jb2011@163.com.
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

  public ServerLauncher()
    throws IOException
  {
  }

  public boolean isRunning()
  {
    return this.running;
  }

  public void shutdown()
  {
    if (this.acceptor != null) {
      this.acceptor.dispose();
    }

    QoS4ReciveDaemonC2S.getInstance().stop();

    QoS4SendDaemonS2C.getInstance().stop();

    this.running = false;
  }

  public void startup()
    throws IOException
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

    dcfg.setReceiveBufferSize(1024);
    dcfg.setSendBufferSize(1024);
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

  public static boolean sendData(int from_user_id, int to_user_id, String dataContent)
    throws Exception
  {
    return ServerCoreHandler.sendData(from_user_id, to_user_id, dataContent);
  }

  public static boolean sendData(int from_user_id, int to_user_id, String dataContent, boolean QoS)
    throws Exception
  {
    return ServerCoreHandler.sendData(from_user_id, to_user_id, dataContent, QoS);
  }

  public static boolean sendData(int from_user_id, int to_user_id, String dataContent, boolean QoS, String fingerPrint)
    throws Exception
  {
    return ServerCoreHandler.sendData(from_user_id, to_user_id, dataContent, 
      QoS, fingerPrint);
  }

  public static boolean sendData(Protocal p)
    throws Exception
  {
    return ServerCoreHandler.sendData(p);
  }

  public static boolean sendData(IoSession session, Protocal p)
    throws Exception
  {
    return ServerCoreHandler.sendData(session, p);
  }

  public static void setSenseMode(SenseMode mode)
  {
    int expire = 0;

    switch (mode)
    {
    case MODE_10S:
      expire = 10;
      break;
    case MODE_120S:
      expire = 21;
      break;
    case MODE_30S:
      expire = 62;
      break;
    case MODE_3S:
      expire = 122;
      break;
    case MODE_60S:
      expire = 242;
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
}