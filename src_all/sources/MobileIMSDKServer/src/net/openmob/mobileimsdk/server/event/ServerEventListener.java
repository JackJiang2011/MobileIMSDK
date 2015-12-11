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