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
import java.util.Observer;
import javax.swing.Timer;
import net.openmob.mobileimsdk.java.ClientCoreSDK;
import net.openmob.mobileimsdk.java.utils.Log;

public class KeepAliveDaemon
{
  private static final String TAG = KeepAliveDaemon.class.getSimpleName();

  public static int NETWORK_CONNECTION_TIME_OUT = 10000;

  public static int KEEP_ALIVE_INTERVAL = 3000;

  private boolean keepAliveRunning = false;

  private long lastGetKeepAliveResponseFromServerTimstamp = 0L;

  private static KeepAliveDaemon instance = null;

  private Observer networkConnectionLostObserver = null;

  private boolean _excuting = false;

  private Timer timer = null;

  public static KeepAliveDaemon getInstance()
  {
    if (instance == null)
      instance = new KeepAliveDaemon();
    return instance;
  }

  private KeepAliveDaemon()
  {
    init();
  }

  private void init()
  {
    this.timer = new Timer(KEEP_ALIVE_INTERVAL, new ActionListener()
    {
      public void actionPerformed(ActionEvent e) {
        KeepAliveDaemon.this.run();
      }
    });
  }

  public void run()
  {
    if (!this._excuting)
    {
      boolean willStop = false;

      this._excuting = true;
      if (ClientCoreSDK.DEBUG)
        Log.i(TAG, "【IMCORE】心跳线程执行中...");
      int code = LocalUDPDataSender.getInstance().sendKeepAlive();

      boolean isInitialedForKeepAlive = this.lastGetKeepAliveResponseFromServerTimstamp == 0L;
      if ((code == 0) && (this.lastGetKeepAliveResponseFromServerTimstamp == 0L)) {
        this.lastGetKeepAliveResponseFromServerTimstamp = System.currentTimeMillis();
      }

      if (!isInitialedForKeepAlive)
      {
        long now = System.currentTimeMillis();

        if (now - this.lastGetKeepAliveResponseFromServerTimstamp >= NETWORK_CONNECTION_TIME_OUT)
        {
          stop();

          if (this.networkConnectionLostObserver != null) {
            this.networkConnectionLostObserver.update(null, null);
          }
          willStop = true;
        }

      }

      this._excuting = false;
      if (willStop)
      {
        this.timer.stop();
      }
    }
  }

  public void stop()
  {
    if (this.timer != null) {
      this.timer.stop();
    }
    this.keepAliveRunning = false;

    this.lastGetKeepAliveResponseFromServerTimstamp = 0L;
  }

  public void start(boolean immediately)
  {
    stop();

    if (immediately)
      this.timer.setInitialDelay(0);
    else
      this.timer.setInitialDelay(KEEP_ALIVE_INTERVAL);
    this.timer.start();

    this.keepAliveRunning = true;
  }

  public boolean isKeepAliveRunning()
  {
    return this.keepAliveRunning;
  }

  public void updateGetKeepAliveResponseFromServerTimstamp()
  {
    this.lastGetKeepAliveResponseFromServerTimstamp = System.currentTimeMillis();
  }

  public void setNetworkConnectionLostObserver(Observer networkConnectionLostObserver)
  {
    this.networkConnectionLostObserver = networkConnectionLostObserver;
  }
}