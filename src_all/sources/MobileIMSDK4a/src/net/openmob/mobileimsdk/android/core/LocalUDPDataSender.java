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
package net.openmob.mobileimsdk.android.core;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import java.net.DatagramSocket;
import java.net.InetAddress;
import net.openmob.mobileimsdk.android.ClientCoreSDK;
import net.openmob.mobileimsdk.android.conf.ConfigEntity;
import net.openmob.mobileimsdk.android.utils.UDPUtils;
import net.openmob.mobileimsdk.server.protocal.CharsetHelper;
import net.openmob.mobileimsdk.server.protocal.Protocal;
import net.openmob.mobileimsdk.server.protocal.ProtocalFactory;

public class LocalUDPDataSender
{
  private static final String TAG = LocalUDPDataSender.class.getSimpleName();
  private static LocalUDPDataSender instance = null;

  private Context context = null;

  public static LocalUDPDataSender getInstance(Context context)
  {
    if (instance == null)
      instance = new LocalUDPDataSender(context);
    return instance;
  }

  private LocalUDPDataSender(Context context)
  {
    this.context = context;
  }

  int sendLogin(String loginName, String loginPsw)
  {
    byte[] b = ProtocalFactory.createPLoginInfo(loginName, loginPsw).toBytes();
    int code = send(b, b.length);

    if (code == 0)
    {
      ClientCoreSDK.getInstance().setCurrentLoginName(loginName);
      ClientCoreSDK.getInstance().setCurrentLoginPsw(loginPsw);
    }

    return code;
  }

  public int sendLoginout()
  {
    int code = 0;
    if (ClientCoreSDK.getInstance().isLoginHasInit())
    {
      byte[] b = ProtocalFactory.createPLoginoutInfo(ClientCoreSDK.getInstance().getCurrentUserId(), 
        ClientCoreSDK.getInstance().getCurrentLoginName()).toBytes();
      code = send(b, b.length);
    }

    ClientCoreSDK.getInstance().release();

    return code;
  }

  int sendKeepAlive()
  {
    byte[] b = ProtocalFactory.createPKeepAlive(ClientCoreSDK.getInstance().getCurrentUserId()).toBytes();
    return send(b, b.length);
  }

  public int sendCommonData(byte[] dataContent, int dataLen, int to_user_id)
  {
    return sendCommonData(CharsetHelper.getString(dataContent, dataLen), to_user_id, false, null);
  }

  public int sendCommonData(byte[] dataContent, int dataLen, int to_user_id, boolean QoS, String fingerPrint)
  {
    return sendCommonData(CharsetHelper.getString(dataContent, dataLen), to_user_id, QoS, fingerPrint);
  }

  public int sendCommonData(String dataContentWidthStr, int to_user_id)
  {
    return sendCommonData(ProtocalFactory.createCommonData(dataContentWidthStr, 
      ClientCoreSDK.getInstance().getCurrentUserId(), to_user_id));
  }

  public int sendCommonData(String dataContentWidthStr, int to_user_id, boolean QoS, String fingerPrint)
  {
    return sendCommonData(ProtocalFactory.createCommonData(dataContentWidthStr, 
      ClientCoreSDK.getInstance().getCurrentUserId(), to_user_id, QoS, fingerPrint));
  }

  public int sendCommonData(Protocal p)
  {
    if (p != null)
    {
      byte[] b = p.toBytes();
      int code = send(b, b.length);
      if (code == 0)
      {
        if ((p.isQoS()) && (!QoS4SendDaemon.getInstance(this.context).exist(p.getFp())))
          QoS4SendDaemon.getInstance(this.context).put(p);
      }
      return code;
    }

    return 4;
  }

  private int send(byte[] fullProtocalBytes, int dataLen)
  {
    if (!ClientCoreSDK.getInstance().isInitialed()) {
      return 203;
    }
    if (!ClientCoreSDK.getInstance().isLocalDeviceNetworkOk())
    {
      Log.e(TAG, "【IMCORE】本地网络不能工作，send数据没有继续!");
      return 204;
    }

    DatagramSocket ds = LocalUDPSocketProvider.getInstance().getLocalUDPSocket();

    if ((ds != null) && (!ds.isConnected()))
    {
      try
      {
        if (ConfigEntity.serverIP == null)
        {
          Log.w(TAG, "【IMCORE】send数据没有继续，原因是ConfigEntity.server_ip==null!");
          return 205;
        }

        ds.connect(InetAddress.getByName(ConfigEntity.serverIP), ConfigEntity.serverUDPPort);
      }
      catch (Exception e)
      {
        Log.w(TAG, "【IMCORE】send时出错，原因是：" + e.getMessage(), e);
        return 202;
      }
    }
    return UDPUtils.send(ds, fullProtocalBytes, dataLen) ? 0 : 3;
  }

  public static abstract class SendCommonDataAsync extends AsyncTask<Object, Integer, Integer>
  {
    protected Context context = null;
    protected Protocal p = null;

    public SendCommonDataAsync(Context context, byte[] dataContent, int dataLen, int to_user_id)
    {
      this(context, CharsetHelper.getString(dataContent, dataLen), to_user_id);
    }

    public SendCommonDataAsync(Context context, String dataContentWidthStr, int to_user_id, boolean QoS)
    {
      this(context, dataContentWidthStr, to_user_id, QoS, null);
    }

    public SendCommonDataAsync(Context context, String dataContentWidthStr, int to_user_id, boolean QoS, String fingerPrint)
    {
      this(context, 
        ProtocalFactory.createCommonData(dataContentWidthStr, 
        ClientCoreSDK.getInstance().getCurrentUserId(), to_user_id, QoS, fingerPrint));
    }

    public SendCommonDataAsync(Context context, String dataContentWidthStr, int to_user_id)
    {
      this(context, 
        ProtocalFactory.createCommonData(dataContentWidthStr, 
        ClientCoreSDK.getInstance().getCurrentUserId(), to_user_id));
    }

    public SendCommonDataAsync(Context context, Protocal p) {
      if (p == null)
      {
        Log.w(LocalUDPDataSender.TAG, "【IMCORE】无效的参数p==null!");
        return;
      }
      this.context = context;
      this.p = p;
    }

    protected Integer doInBackground(Object[] params)
    {
      if (this.p != null)
        return Integer.valueOf(LocalUDPDataSender.getInstance(this.context).sendCommonData(this.p));
      return Integer.valueOf(-1);
    }

    protected abstract void onPostExecute(Integer paramInteger);
  }

  public static abstract class SendLoginDataAsync extends AsyncTask<Object, Integer, Integer>
  {
    protected Context context = null;
    protected String loginName = null;
    protected String loginPsw = null;

    public SendLoginDataAsync(Context context, String loginName, String loginPsw)
    {
      this.context = context;
      this.loginName = loginName;
      this.loginPsw = loginPsw;
    }

    protected Integer doInBackground(Object[] params)
    {
      int code = LocalUDPDataSender.getInstance(this.context).sendLogin(this.loginName, this.loginPsw);
      return Integer.valueOf(code);
    }

    protected void onPostExecute(Integer code)
    {
      if (code.intValue() == 0)
      {
        LocalUDPDataReciever.getInstance(this.context).startup();
      }
      else
      {
        Log.d(LocalUDPDataSender.TAG, "【IMCORE】数据发送失败, 错误码是：" + code + "！");
      }

      fireAfterSendLogin(code.intValue());
    }

    protected void fireAfterSendLogin(int code)
    {
    }
  }
}