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
package net.openmob.mobileimsdk.java.conf;

public class ConfigEntity
{
  public static String appKey = null;

  public static String serverIP = "rbcore.openmob.net";

  public static int serverUDPPort = 7901;

  public static int localUDPPort = 0;

  public static void setSenseMode(SenseMode mode)
  {
    int keepAliveInterval = 0;
    int networkConnectionTimeout = 0;
    switch (mode)
    {
    case MODE_10S:
      keepAliveInterval = 3000;

      networkConnectionTimeout = 10000;
      break;
    case MODE_120S:
      keepAliveInterval = 10000;

      networkConnectionTimeout = 21000;
      break;
    case MODE_30S:
      keepAliveInterval = 30000;

      networkConnectionTimeout = 61000;
      break;
    case MODE_3S:
      keepAliveInterval = 60000;

      networkConnectionTimeout = 121000;
      break;
    case MODE_60S:
      keepAliveInterval = 120000;

      networkConnectionTimeout = 241000;
    }

    if (keepAliveInterval > 0)
    {
      net.openmob.mobileimsdk.java.core.KeepAliveDaemon.KEEP_ALIVE_INTERVAL = keepAliveInterval;
    }
    if (networkConnectionTimeout > 0)
    {
      net.openmob.mobileimsdk.java.core.KeepAliveDaemon.NETWORK_CONNECTION_TIME_OUT = networkConnectionTimeout;
    }
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