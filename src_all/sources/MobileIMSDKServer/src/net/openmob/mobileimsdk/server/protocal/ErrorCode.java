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

public abstract interface ErrorCode
{
  public static final int COMMON_CODE_OK = 0;
  public static final int COMMON_NO_LOGIN = 1;
  public static final int COMMON_UNKNOW_ERROR = 2;
  public static final int COMMON_DATA_SEND_FAILD = 3;
  public static final int COMMON_INVALID_PROTOCAL = 4;

  public static abstract interface ForC
  {
    public static final int BREOKEN_CONNECT_TO_SERVER = 201;
    public static final int BAD_CONNECT_TO_SERVER = 202;
    public static final int CLIENT_SDK_NO_INITIALED = 203;
    public static final int LOCAL_NETWORK_NOT_WORKING = 204;
    public static final int TO_SERVER_NET_INFO_NOT_SETUP = 205;
  }

  public static abstract interface ForS
  {
    public static final int RESPONSE_FOR_UNLOGIN = 301;
  }
}