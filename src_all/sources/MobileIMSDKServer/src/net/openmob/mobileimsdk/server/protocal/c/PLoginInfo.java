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
package net.openmob.mobileimsdk.server.protocal.c;

public class PLoginInfo
{
  private String loginName = null;
  private String loginPsw = null;

  public PLoginInfo(String loginName, String loginPsw)
  {
    this.loginName = loginName;
    this.loginPsw = loginPsw;
  }

  public String getLoginName()
  {
    return this.loginName;
  }

  public void setLoginName(String loginName)
  {
    this.loginName = loginName;
  }

  public String getLoginPsw()
  {
    return this.loginPsw;
  }

  public void setLoginPsw(String loginPsw)
  {
    this.loginPsw = loginPsw;
  }
}