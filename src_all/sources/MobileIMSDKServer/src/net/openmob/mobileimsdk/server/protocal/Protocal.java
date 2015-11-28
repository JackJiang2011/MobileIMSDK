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

import com.google.gson.Gson;
import java.util.UUID;

public class Protocal
{
  private int type = 0;

  private String dataContent = null;

  private int from = -1;

  private int to = -1;

  private String fp = null;

  private boolean QoS = false;

  private transient int retryCount = 0;

  public Protocal(int type, String dataContent, int from, int to)
  {
    this(type, dataContent, from, to, false, null);
  }

  public Protocal(int type, String dataContent, int from, int to, boolean QoS, String fingerPrint)
  {
    this.type = type;
    this.dataContent = dataContent;
    this.from = from;
    this.to = to;
    this.QoS = QoS;

    if ((QoS) && (fingerPrint == null))
      this.fp = genFingerPrint();
    else
      this.fp = fingerPrint;
  }

  public int getType()
  {
    return this.type;
  }

  public void setType(int type)
  {
    this.type = type;
  }

  public String getDataContent()
  {
    return this.dataContent;
  }

  public void setDataContent(String dataContent)
  {
    this.dataContent = dataContent;
  }

  public int getFrom()
  {
    return this.from;
  }

  public void setFrom(int from)
  {
    this.from = from;
  }

  public int getTo()
  {
    return this.to;
  }

  public void setTo(int to)
  {
    this.to = to;
  }

  public String getFp()
  {
    return this.fp;
  }

  public int getRetryCount()
  {
    return this.retryCount;
  }

  public void increaseRetryCount()
  {
    this.retryCount += 1;
  }

  public boolean isQoS()
  {
    return this.QoS;
  }

  public String toGsonString()
  {
    return new Gson().toJson(this);
  }

  public byte[] toBytes()
  {
    return CharsetHelper.getBytes(toGsonString());
  }

  public Object clone()
  {
    Protocal cloneP = new Protocal(getType(), 
      getDataContent(), getFrom(), getTo(), isQoS(), getFp());
    return cloneP;
  }

  public static String genFingerPrint()
  {
    return UUID.randomUUID().toString();
  }
}