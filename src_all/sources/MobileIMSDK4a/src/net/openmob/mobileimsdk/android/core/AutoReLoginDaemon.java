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
package net.openmob.mobileimsdk.android.core;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Handler;
import android.util.Log;
import net.openmob.mobileimsdk.android.ClientCoreSDK;

public class AutoReLoginDaemon
{
  private static final String TAG = AutoReLoginDaemon.class.getSimpleName();

  public static int AUTO_RE$LOGIN_INTERVAL = 2000;

  private Handler handler = null;
  private Runnable runnable = null;

  private boolean autoReLoginRunning = false;

  private boolean _excuting = false;

  private static AutoReLoginDaemon instance = null;

  private Context context = null;

  public static AutoReLoginDaemon getInstance(Context context)
  {
    if (instance == null)
      instance = new AutoReLoginDaemon(context);
    return instance;
  }

  private AutoReLoginDaemon(Context context)
  {
    this.context = context;
    init();
  }

  private void init()
  {
    this.handler = new Handler();
    this.runnable = new Runnable()
    {
      public void run()
      {
        if (!AutoReLoginDaemon.this._excuting)
        {
          new AsyncTask()
          {
            protected Integer doInBackground(Object[] params)
            {
              AutoReLoginDaemon.this._excuting = true;
              if (ClientCoreSDK.DEBUG)
                Log.d(AutoReLoginDaemon.TAG, "【IMCORE】自动重新登陆线程执行中, autoReLogin?" + ClientCoreSDK.autoReLogin + "...");
              int code = -1;

              if (ClientCoreSDK.autoReLogin)
              {
                code = LocalUDPDataSender.getInstance(AutoReLoginDaemon.this.context).sendLogin(
                  ClientCoreSDK.getInstance().getCurrentLoginName(), ClientCoreSDK.getInstance().getCurrentLoginPsw());
              }
              return Integer.valueOf(code);
            }

            protected void onPostExecute(Integer result)
            {
              if (result.intValue() == 0)
              {
                LocalUDPDataReciever.getInstance(AutoReLoginDaemon.this.context).startup();
              }

              AutoReLoginDaemon.this._excuting = false;

              AutoReLoginDaemon.this.handler.postDelayed(AutoReLoginDaemon.this.runnable, AutoReLoginDaemon.AUTO_RE$LOGIN_INTERVAL);
            }
          }
          .execute(new Object[0]);
        }
      }
    };
  }

  public void stop()
  {
    this.handler.removeCallbacks(this.runnable);

    this.autoReLoginRunning = false;
  }

  public void start(boolean immediately)
  {
    stop();

    this.handler.postDelayed(this.runnable, immediately ? 0 : AUTO_RE$LOGIN_INTERVAL);

    this.autoReLoginRunning = true;
  }

  public boolean isautoReLoginRunning()
  {
    return this.autoReLoginRunning;
  }
}