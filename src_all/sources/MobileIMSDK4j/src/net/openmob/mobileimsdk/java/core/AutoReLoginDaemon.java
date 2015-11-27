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
package net.openmob.mobileimsdk.java.core;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.Timer;
import net.openmob.mobileimsdk.java.ClientCoreSDK;
import net.openmob.mobileimsdk.java.utils.Log;

public class AutoReLoginDaemon
{
  private static final String TAG = AutoReLoginDaemon.class.getSimpleName();

  public static int AUTO_RE$LOGIN_INTERVAL = 2000;

  private boolean autoReLoginRunning = false;

  private boolean _excuting = false;

  private Timer timer = null;

  private static AutoReLoginDaemon instance = null;

  public static AutoReLoginDaemon getInstance()
  {
    if (instance == null)
      instance = new AutoReLoginDaemon();
    return instance;
  }

  private AutoReLoginDaemon()
  {
    init();
  }

  private void init()
  {
    this.timer = new Timer(AUTO_RE$LOGIN_INTERVAL, new ActionListener()
    {
      public void actionPerformed(ActionEvent e) {
        AutoReLoginDaemon.this.run();
      }
    });
  }

  public void run() {
    if (!this._excuting)
    {
      this._excuting = true;
      if (ClientCoreSDK.DEBUG)
        Log.d(TAG, "【IMCORE】自动重新登陆线程执行中, autoReLogin?" + ClientCoreSDK.autoReLogin + "...");
      int code = -1;

      if (ClientCoreSDK.autoReLogin)
      {
        LocalUDPSocketProvider.getInstance().closeLocalUDPSocket();

        code = LocalUDPDataSender.getInstance().sendLogin(
          ClientCoreSDK.getInstance().getCurrentLoginName(), ClientCoreSDK.getInstance().getCurrentLoginPsw());
      }

      if (code == 0)
      {
        LocalUDPDataReciever.getInstance().startup();
      }

      this._excuting = false;
    }
  }

  public void stop()
  {
    if (this.timer != null) {
      this.timer.stop();
    }
    this.autoReLoginRunning = false;
  }

  public void start(boolean immediately)
  {
    stop();

    if (immediately)
      this.timer.setInitialDelay(0);
    else
      this.timer.setInitialDelay(AUTO_RE$LOGIN_INTERVAL);
    this.timer.start();

    this.autoReLoginRunning = true;
  }

  public boolean isautoReLoginRunning()
  {
    return this.autoReLoginRunning;
  }
}