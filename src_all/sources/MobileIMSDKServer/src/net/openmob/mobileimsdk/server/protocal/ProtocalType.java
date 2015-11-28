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
package net.openmob.mobileimsdk.server.protocal;

public abstract interface ProtocalType
{
  public static abstract interface C
  {
    public static final int FROM_CLIENT_TYPE_OF_LOGIN = 0;
    public static final int FROM_CLIENT_TYPE_OF_KEEP$ALIVE = 1;
    public static final int FROM_CLIENT_TYPE_OF_COMMON$DATA = 2;
    public static final int FROM_CLIENT_TYPE_OF_LOGOUT = 3;
    public static final int FROM_CLIENT_TYPE_OF_RECIVED = 4;
    public static final int FROM_CLIENT_TYPE_OF_ECHO = 5;
  }

  public static abstract interface S
  {
    public static final int FROM_SERVER_TYPE_OF_RESPONSE$LOGIN = 50;
    public static final int FROM_SERVER_TYPE_OF_RESPONSE$KEEP$ALIVE = 51;
    public static final int FROM_SERVER_TYPE_OF_RESPONSE$FOR$ERROR = 52;
    public static final int FROM_SERVER_TYPE_OF_RESPONSE$ECHO = 53;
  }
}