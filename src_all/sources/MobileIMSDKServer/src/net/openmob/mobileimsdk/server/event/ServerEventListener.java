/*
 * Copyright (C) 2016 即时通讯网(52im.net) The MobileIMSDK Project. 
 * All rights reserved.
 * Project URL:https://github.com/JackJiang2011/MobileIMSDK
 *  
 * 即时通讯网(52im.net) - 即时通讯技术社区! PROPRIETARY/CONFIDENTIAL.
 * Use is subject to license terms.
 * 
 * ServerEventListener.java at 2016-2-20 11:26:02, code by Jack Jiang.
 * You can contact author with jack.jiang@52im.net or jb2011@163.com.
 */
package net.openmob.mobileimsdk.server.event;

import org.apache.mina.core.session.IoSession;

public abstract interface ServerEventListener
{
  public abstract int onVerifyUserCallBack(String paramString1, String paramString2, String extra);

  public abstract void onUserLoginAction_CallBack(int paramInt, String paramString, IoSession paramIoSession);

  public abstract void onUserLogoutAction_CallBack(int paramInt, Object paramObject);

  public abstract boolean onTransBuffer_CallBack(int paramInt1, int paramInt2, String paramString1, String paramString2);

  public abstract void onTransBuffer_C2C_CallBack(int paramInt1, int paramInt2, String paramString);

  public abstract boolean onTransBuffer_C2C_RealTimeSendFaild_CallBack(int paramInt1, int paramInt2, String paramString1, String paramString2);
}