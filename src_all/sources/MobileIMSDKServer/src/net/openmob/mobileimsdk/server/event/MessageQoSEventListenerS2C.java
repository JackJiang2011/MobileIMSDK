/*
 * Copyright (C) 2016 即时通讯网(52im.net) The MobileIMSDK Project. 
 * All rights reserved.
 * Project URL:https://github.com/JackJiang2011/MobileIMSDK
 *  
 * 即时通讯网(52im.net) - 即时通讯技术社区! PROPRIETARY/CONFIDENTIAL.
 * Use is subject to license terms.
 * 
 * MessageQoSEventListenerS2C.java at 2016-2-20 11:26:02, code by Jack Jiang.
 * You can contact author with jack.jiang@52im.net or jb2011@163.com.
 */
package net.openmob.mobileimsdk.server.event;

import java.util.ArrayList;
import net.openmob.mobileimsdk.server.protocal.Protocal;

public abstract interface MessageQoSEventListenerS2C
{
  public abstract void messagesLost(ArrayList<Protocal> paramArrayList);

  public abstract void messagesBeReceived(String paramString);
}