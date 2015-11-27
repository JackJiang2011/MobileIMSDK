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
package net.openmob.mobileimsdk.android.event;

import java.util.ArrayList;
import net.openmob.mobileimsdk.server.protocal.Protocal;

public abstract interface MessageQoSEvent
{
  public abstract void messagesLost(ArrayList<Protocal> paramArrayList);

  public abstract void messagesBeReceived(String paramString);
}