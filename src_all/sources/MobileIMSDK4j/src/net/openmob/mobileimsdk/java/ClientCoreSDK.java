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
package net.openmob.mobileimsdk.java;

import net.openmob.mobileimsdk.java.core.AutoReLoginDaemon;
import net.openmob.mobileimsdk.java.core.KeepAliveDaemon;
import net.openmob.mobileimsdk.java.core.LocalUDPDataReciever;
import net.openmob.mobileimsdk.java.core.LocalUDPSocketProvider;
import net.openmob.mobileimsdk.java.core.QoS4ReciveDaemon;
import net.openmob.mobileimsdk.java.core.QoS4SendDaemon;
import net.openmob.mobileimsdk.java.event.ChatBaseEvent;
import net.openmob.mobileimsdk.java.event.ChatTransDataEvent;
import net.openmob.mobileimsdk.java.event.MessageQoSEvent;

public class ClientCoreSDK
{
  private static final String TAG = ClientCoreSDK.class.getSimpleName();

  public static boolean DEBUG = true;

  public static boolean autoReLogin = true;

  private static ClientCoreSDK instance = null;

  private boolean _init = false;

  private boolean connectedToServer = true;

  private boolean loginHasInit = false;

  private int currentUserId = -1;

  private String currentLoginName = null;

  private String currentLoginPsw = null;

  private ChatBaseEvent chatBaseEvent = null;

  private ChatTransDataEvent chatTransDataEvent = null;

  private MessageQoSEvent messageQoSEvent = null;

  public static ClientCoreSDK getInstance()
  {
    if (instance == null)
      instance = new ClientCoreSDK();
    return instance;
  }

  public void init()
  {
    if (!this._init)
    {
      this._init = true;
    }
  }

  public void release()
  {
    AutoReLoginDaemon.getInstance().stop();

    QoS4SendDaemon.getInstance().stop();

    KeepAliveDaemon.getInstance().stop();

    LocalUDPDataReciever.getInstance().stop();

    QoS4ReciveDaemon.getInstance().stop();

    LocalUDPSocketProvider.getInstance().closeLocalUDPSocket();

    this._init = false;

    setLoginHasInit(false);
    setConnectedToServer(false);
  }

  public int getCurrentUserId()
  {
    return this.currentUserId;
  }

  public ClientCoreSDK setCurrentUserId(int currentUserId)
  {
    this.currentUserId = currentUserId;
    return this;
  }

  public String getCurrentLoginName()
  {
    return this.currentLoginName;
  }

  public ClientCoreSDK setCurrentLoginName(String currentLoginName)
  {
    this.currentLoginName = currentLoginName;
    return this;
  }

  public String getCurrentLoginPsw()
  {
    return this.currentLoginPsw;
  }

  public void setCurrentLoginPsw(String currentLoginPsw)
  {
    this.currentLoginPsw = currentLoginPsw;
  }

  public boolean isLoginHasInit()
  {
    return this.loginHasInit;
  }

  public ClientCoreSDK setLoginHasInit(boolean loginHasInit)
  {
    this.loginHasInit = loginHasInit;

    return this;
  }

  public boolean isConnectedToServer()
  {
    return this.connectedToServer;
  }

  public void setConnectedToServer(boolean connectedToServer)
  {
    this.connectedToServer = connectedToServer;
  }

  public boolean isInitialed()
  {
    return this._init;
  }

  public void setChatBaseEvent(ChatBaseEvent chatBaseEvent)
  {
    this.chatBaseEvent = chatBaseEvent;
  }

  public ChatBaseEvent getChatBaseEvent()
  {
    return this.chatBaseEvent;
  }

  public void setChatTransDataEvent(ChatTransDataEvent chatTransDataEvent)
  {
    this.chatTransDataEvent = chatTransDataEvent;
  }

  public ChatTransDataEvent getChatTransDataEvent()
  {
    return this.chatTransDataEvent;
  }

  public void setMessageQoSEvent(MessageQoSEvent messageQoSEvent)
  {
    this.messageQoSEvent = messageQoSEvent;
  }

  public MessageQoSEvent getMessageQoSEvent()
  {
    return this.messageQoSEvent;
  }
}