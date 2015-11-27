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
package net.openmob.mobileimsdk.android;

import android.app.Application;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;
import net.openmob.mobileimsdk.android.core.AutoReLoginDaemon;
import net.openmob.mobileimsdk.android.core.KeepAliveDaemon;
import net.openmob.mobileimsdk.android.core.LocalUDPDataReciever;
import net.openmob.mobileimsdk.android.core.LocalUDPSocketProvider;
import net.openmob.mobileimsdk.android.core.QoS4ReciveDaemon;
import net.openmob.mobileimsdk.android.core.QoS4SendDaemon;
import net.openmob.mobileimsdk.android.event.ChatBaseEvent;
import net.openmob.mobileimsdk.android.event.ChatTransDataEvent;
import net.openmob.mobileimsdk.android.event.MessageQoSEvent;

public class ClientCoreSDK
{
  private static final String TAG = ClientCoreSDK.class.getSimpleName();

  public static boolean DEBUG = true;

  public static boolean autoReLogin = true;

  private static ClientCoreSDK instance = null;

  private boolean _init = false;

  private boolean localDeviceNetworkOk = true;

  private boolean connectedToServer = true;

  private boolean loginHasInit = false;

  private int currentUserId = -1;

  private String currentLoginName = null;

  private String currentLoginPsw = null;

  private ChatBaseEvent chatBaseEvent = null;

  private ChatTransDataEvent chatTransDataEvent = null;

  private MessageQoSEvent messageQoSEvent = null;

  private Context context = null;

  private final BroadcastReceiver networkConnectionStatusBroadcastReceiver = new BroadcastReceiver()
  {
    public void onReceive(Context context, Intent intent)
    {
      ConnectivityManager connectMgr = (ConnectivityManager)context.getSystemService("connectivity");
      NetworkInfo mobNetInfo = connectMgr.getNetworkInfo(0);
      NetworkInfo wifiNetInfo = connectMgr.getNetworkInfo(1);
      if (((mobNetInfo == null) || (!mobNetInfo.isConnected())) && (
        (wifiNetInfo == null) || (!wifiNetInfo.isConnected())))
      {
        Log.e(ClientCoreSDK.TAG, "【IMCORE】【本地网络通知】检测本地网络连接断开了!");

        ClientCoreSDK.this.localDeviceNetworkOk = false;

        LocalUDPSocketProvider.getInstance().closeLocalUDPSocket();
      }
      else
      {
        if (ClientCoreSDK.DEBUG)
        {
          Log.e(ClientCoreSDK.TAG, "【IMCORE】【本地网络通知】检测本地网络已连接上了!");
        }

        ClientCoreSDK.this.localDeviceNetworkOk = true;

        LocalUDPSocketProvider.getInstance().closeLocalUDPSocket();
      }
    }
  };

  public static ClientCoreSDK getInstance()
  {
    if (instance == null)
      instance = new ClientCoreSDK();
    return instance;
  }

  public void init(Context _context)
  {
    if (!this._init)
    {
      if (_context == null) {
        throw new IllegalArgumentException("context can't be null!");
      }

      if ((_context instanceof Application)) {
        this.context = _context;
      }
      else
      {
        this.context = _context.getApplicationContext();
      }

      IntentFilter intentFilter = new IntentFilter();
      intentFilter.addAction("android.net.conn.CONNECTIVITY_CHANGE");
      this.context.registerReceiver(this.networkConnectionStatusBroadcastReceiver, intentFilter);

      this._init = true;
    }
  }

  public void release()
  {
    AutoReLoginDaemon.getInstance(this.context).stop();

    QoS4SendDaemon.getInstance(this.context).stop();

    KeepAliveDaemon.getInstance(this.context).stop();

    LocalUDPDataReciever.getInstance(this.context).stop();

    QoS4ReciveDaemon.getInstance(this.context).stop();

    LocalUDPSocketProvider.getInstance().closeLocalUDPSocket();
    try
    {
      this.context.unregisterReceiver(this.networkConnectionStatusBroadcastReceiver);
    }
    catch (Exception e)
    {
      Log.w(TAG, e.getMessage(), e);
    }

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

  public boolean isLocalDeviceNetworkOk()
  {
    return this.localDeviceNetworkOk;
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