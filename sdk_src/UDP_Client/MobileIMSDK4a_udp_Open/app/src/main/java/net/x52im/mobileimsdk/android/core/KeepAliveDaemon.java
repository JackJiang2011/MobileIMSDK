/*
 * Copyright (C) 2021  即时通讯网(52im.net) & Jack Jiang.
 * The MobileIMSDK_UDP (MobileIMSDK v6.x UDP版) Project. 
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
 * KeepAliveDaemon.java at 2021-7-2 22:38:44, code by Jack Jiang.
 */
package net.x52im.mobileimsdk.android.core;

import java.util.Observer;
import java.util.concurrent.atomic.AtomicLong;

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
    private AtomicLong lastGetKeepAliveResponseFromServerTimstamp = new AtomicLong(0);
    private Observer networkConnectionLostObserver = null;

    private Handler handler = null;
    private Runnable runnable = null;
    private boolean _excuting = false;
    private boolean _willStop = false;
    private boolean init = false;

    /** !本属性仅作DEBUG之用：DEBUG事件观察者 */
    private Observer debugObserver;

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
        // for DEBUG
        if(this.debugObserver != null)
            this.debugObserver.update(null, 2);

        boolean isInitialedForKeepAlive = (lastGetKeepAliveResponseFromServerTimstamp.longValue() == 0);
        //## Bug FIX 20190513 v4.0.1 START
        if (isInitialedForKeepAlive)
            lastGetKeepAliveResponseFromServerTimstamp.set(System.currentTimeMillis());
        //## Bug FIX 20190513 v4.0.1 END

        if (!isInitialedForKeepAlive) {
            long now = System.currentTimeMillis();
            if (now - lastGetKeepAliveResponseFromServerTimstamp.longValue() >= NETWORK_CONNECTION_TIME_OUT) {
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
        lastGetKeepAliveResponseFromServerTimstamp.set(0);

        // for DEBUG
        if(this.debugObserver != null)
            this.debugObserver.update(null, 0);
    }

    public void start(boolean immediately) {
        stop();
        handler.postDelayed(runnable, immediately ? 0 : KEEP_ALIVE_INTERVAL);
        keepAliveRunning = true;

        // for DEBUG
        if(this.debugObserver != null)
            this.debugObserver.update(null, 1);
    }

    public boolean isKeepAliveRunning() {
        return keepAliveRunning;
    }

    public boolean isInit() {
        return init;
    }

    public void updateGetKeepAliveResponseFromServerTimstamp() {
        lastGetKeepAliveResponseFromServerTimstamp.set(System.currentTimeMillis());
    }

    public void setNetworkConnectionLostObserver(Observer networkConnectionLostObserver) {
        this.networkConnectionLostObserver = networkConnectionLostObserver;
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
