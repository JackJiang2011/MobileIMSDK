/*
 * Copyright (C) 2020  即时通讯网(52im.net) & Jack Jiang.
 * The MobileIMSDK_UDP (MobileIMSDK v5.x UDP版) Project.
 * All rights reserved.
 *
 * > Github地址：https://github.com/JackJiang2011/MobileIMSDK
 * > 文档地址：  http://www.52im.net/forum-89-1.html
 * > 技术社区：  http://www.52im.net/
 * > 技术交流群：215477170 (http://www.52im.net/topic-qqgroup.html)
 * > 作者公众号：“即时通讯技术圈】”，欢迎关注！
 * > 联系作者：  http://www.52im.net/thread-2792-1-1.html
 *
 * "即时通讯网(52im.net) - 即时通讯开发者社区!" 推荐开源工程。
 *
 * KeepAliveDaemon.java at 2020-8-18 15:40:04, code by Jack Jiang.
 */
package net.x52im.mobileimsdk.android.core;

import java.util.Observer;

import net.x52im.mobileimsdk.android.ClientCoreSDK;
import net.x52im.mobileimsdk.android.utils.MBThreadPoolExecutor;

import android.os.Handler;
import android.util.Log;

public class KeepAliveDaemon {
    private final static String TAG = KeepAliveDaemon.class.getSimpleName();

    private static KeepAliveDaemon instance = null;
    public static int NETWORK_CONNECTION_TIME_OUT = 10 * 1000;
    public static int KEEP_ALIVE_INTERVAL = 3000;//1000;

    private boolean keepAliveRunning = false;
    private long lastGetKeepAliveResponseFromServerTimstamp = 0;
    private Observer networkConnectionLostObserver = null;

    private Handler handler = null;
    private Runnable runnable = null;
    private boolean _excuting = false;
    private boolean _willStop = false;
    private boolean init = false;

    public static KeepAliveDaemon getInstance() {
        if (instance == null)
            instance = new KeepAliveDaemon();
        return instance;
    }

    private KeepAliveDaemon() {
        init();
    }

    private void init() {
        if (init)
            return;

        handler = new Handler();
        runnable = () -> {
            if (!_excuting) {
                _willStop = false;
                // 在独立线程中执行doKeepALive()发送心跳指令，完成后在主线程中执行onKeepAlive()
                MBThreadPoolExecutor.runInBackground(() -> {
                    final int code = doKeepAlive();
                    MBThreadPoolExecutor.runOnMainThread(() -> onKeepAlive(code));
                });
            }
        };

        init = true;
    }

    private int doKeepAlive() {
        _excuting = true;
        if (ClientCoreSDK.DEBUG)
            Log.d(TAG, "【IMCORE-UDP】心跳线程执行中...");
        int code = LocalDataSender.getInstance().sendKeepAlive();
        return code;
    }

    private void onKeepAlive(int code) {
        boolean isInitialedForKeepAlive = (lastGetKeepAliveResponseFromServerTimstamp == 0);
        //## Bug FIX 20190513 v4.0.1 START
        //## 解决极端情况下手机网络断开时，无法进入下面的"断开"通知流程
//		if(code == 0 && lastGetKeepAliveResponseFromServerTimstamp == 0)
        if (isInitialedForKeepAlive)
            lastGetKeepAliveResponseFromServerTimstamp = System.currentTimeMillis();
        //## Bug FIX 20190513 v4.0.1 END

        if (!isInitialedForKeepAlive) {
            long now = System.currentTimeMillis();
            if (now - lastGetKeepAliveResponseFromServerTimstamp >= NETWORK_CONNECTION_TIME_OUT) {
                stop();

                if (networkConnectionLostObserver != null)
                    networkConnectionLostObserver.update(null, null);

                _willStop = true;
            }
        }

        _excuting = false;
        if (!_willStop)
            handler.postDelayed(runnable, KEEP_ALIVE_INTERVAL);
    }

    public void stop() {
        handler.removeCallbacks(runnable);
        keepAliveRunning = false;
        lastGetKeepAliveResponseFromServerTimstamp = 0;
    }

    public void start(boolean immediately) {
        stop();
        handler.postDelayed(runnable, immediately ? 0 : KEEP_ALIVE_INTERVAL);
        keepAliveRunning = true;
    }

    public boolean isKeepAliveRunning() {
        return keepAliveRunning;
    }

    public boolean isInit() {
        return init;
    }

    public void updateGetKeepAliveResponseFromServerTimstamp() {
        lastGetKeepAliveResponseFromServerTimstamp = System.currentTimeMillis();
    }

    public void setNetworkConnectionLostObserver(Observer networkConnectionLostObserver) {
        this.networkConnectionLostObserver = networkConnectionLostObserver;
    }
}
