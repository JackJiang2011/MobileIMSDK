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
package net.openmob.mobileimsdk.server.protocal.s;

public class PLoginInfoResponse
{
  private int code = 0;

  private int user_id = -1;

  public PLoginInfoResponse(int code, int user_id)
  {
    this.code = code;
    this.user_id = user_id;
  }

  public int getCode()
  {
    return this.code;
  }

  public void setCode(int code) {
    this.code = code;
  }

  public int getUser_id()
  {
    return this.user_id;
  }

  public void setUser_id(int user_id)
  {
    this.user_id = user_id;
  }
}