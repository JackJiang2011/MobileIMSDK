/*
 * Copyright (C) 2020  即时通讯网(52im.net) & Jack Jiang.
 * The MobileIMSDK_TCP (MobileIMSDK v5.x TCP版) Project.
 * All rights reserved.
 *
 * > Github地址：https://github.com/JackJiang2011/MobileIMSDK
 * > 文档地址：  http://www.52im.net/forum-89-1.html
 * > 技术社区：  http://www.52im.net/
 * > 技术交流群：320837163 (http://www.52im.net/topic-qqgroup.html)
 * > 作者公众号：“即时通讯技术圈】”，欢迎关注！
 * > 联系作者：  http://www.52im.net/thread-2792-1-1.html
 *
 * "即时通讯网(52im.net) - 即时通讯开发者社区!" 推荐开源工程。
 *
 * AutoReLoginDaemon.java at 2020-8-8 15:58:02, code by Jack Jiang.
 */
package net.x52im.mobileimsdk.android.core;

import net.x52im.mobileimsdk.android.ClientCoreSDK;
import net.x52im.mobileimsdk.android.conf.ConfigEntity;
import net.x52im.mobileimsdk.android.utils.MBThreadPoolExecutor;

import android.os.Handler;
import android.util.Log;

import java.util.Observer;

public class AutoReLoginDaemon {
    private final static String TAG = AutoReLoginDaemon.class.getSimpleName();
    private static AutoReLoginDaemon instance = null;
    public static int AUTO_RE$LOGIN_INTERVAL = 5000;//2000;

    private Handler handler = null;
    private Runnable runnable = null;
    private boolean autoReLoginRunning = false;
    private boolean _excuting = false;
    private boolean init = false;

    /** !本属性仅作DEBUG之用：DEBUG事件观察者 */
    private Observer debugObserver;

    public static AutoReLoginDaemon getInstance() {
        if (instance == null)
            instance = new AutoReLoginDaemon();
        return instance;
    }

    private AutoReLoginDaemon() {
        init();
    }

    private void init() {
        if (init)
            return;

        handler = new Handler();
        runnable = () -> {
            if (!_excuting) {
                // 在独立线程中执行doSendLogin()发送登陆请求，完成后在主线程中执行onSendLogin()
                MBThreadPoolExecutor.runInBackground(() -> {
                    final int code = doSendLogin();
                    MBThreadPoolExecutor.runOnMainThread(() -> onSendLogin(code));
                });
            }
        };

        init = true;
    }

    private int doSendLogin() {
        _excuting = true;
        if (ClientCoreSDK.DEBUG)
            Log.d(TAG, "【IMCORE-TCP】自动重新登陆线程执行中, autoReLogin?" + ClientCoreSDK.autoReLogin + "...");
        int code = -1;
        if (ClientCoreSDK.autoReLogin) {
            code = LocalDataSender.getInstance().sendLogin(
                    ClientCoreSDK.getInstance().getCurrentLoginUserId()
                    , ClientCoreSDK.getInstance().getCurrentLoginToken()
                    , ClientCoreSDK.getInstance().getCurrentLoginExtra());
        }
        return code;
    }

    private void onSendLogin(int result) {
        // for DEBUG
        if(this.debugObserver != null)
            this.debugObserver.update(null, 2);

        if (result == 0) {
//			LocalUDPDataReciever.getInstance().startup();
        }

        _excuting = false;
        handler.postDelayed(runnable, AUTO_RE$LOGIN_INTERVAL);
    }

    public void stop() {
        handler.removeCallbacks(runnable);
        autoReLoginRunning = false;

        // for DEBUG
        if(this.debugObserver != null)
            this.debugObserver.update(null, 0);
    }

    public void start(boolean immediately) {
        stop();
        handler.postDelayed(runnable, immediately ? 0 : AUTO_RE$LOGIN_INTERVAL);
        autoReLoginRunning = true;

        // for DEBUG
        if(this.debugObserver != null)
            this.debugObserver.update(null, 1);
    }

    public boolean isAutoReLoginRunning() {
        return autoReLoginRunning;
    }

    public boolean isInit() {
        return init;
    }

    /**
     * !本方法仅用于DEBUG，开发者无需关注！
     *
     * @return DEBUG事件观察者
     */
    public Observer getDebugObserver() {
        return debugObserver;
    }

    /**
     * !本方法仅用于DEBUG，开发者无需关注！
     *
     * @param debugObserver DEBUG事件观察者
     */
    public void setDebugObserver(Observer debugObserver) {
        this.debugObserver = debugObserver;
    }
}
