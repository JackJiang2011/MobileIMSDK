/*
 * Copyright (C) 2022  即时通讯网(52im.net) & Jack Jiang.
 * The MobileIMSDK_TCP (MobileIMSDK v6.x TCP版) Project. 
 * All rights reserved.
 * 
 * > Github地址：https://github.com/JackJiang2011/MobileIMSDK
 * > 文档地址：  http://www.52im.net/forum-89-1.html
 * > 技术社区：  http://www.52im.net/
 * > 技术交流群：185926912 (http://www.52im.net/topic-qqgroup.html)
 * > 作者公众号：“即时通讯技术圈】”，欢迎关注！
 * > 联系作者：  http://www.52im.net/thread-2792-1-1.html
 *  
 * "即时通讯网(52im.net) - 即时通讯开发者社区!" 推荐开源工程。
 * 
 * KeepAliveDaemon.java at 2022-7-28 17:24:47, code by Jack Jiang.
 */
package net.x52im.mobileimsdk.android.core;

import java.util.Observer;
import java.util.concurrent.atomic.AtomicLong;

import net.x52im.mobileimsdk.android.ClientCoreSDK;
import net.x52im.mobileimsdk.android.utils.MBSimpleTimer;
import net.x52im.mobileimsdk.android.utils.MBThreadPoolExecutor;

import android.os.Handler;
import android.util.Log;

public class KeepAliveDaemon {

    private final static String TAG = KeepAliveDaemon.class.getSimpleName();

    private static KeepAliveDaemon instance = null;

    public static int KEEP_ALIVE_INTERVAL = 15000;//3000;//1000;
    public static int NETWORK_CONNECTION_TIME_OUT = KEEP_ALIVE_INTERVAL + 5000;//20 * 1000;//10 * 1000;
    public static int NETWORK_CONNECTION_TIME_OUT_CHECK_INTERVAL = 2 * 1000;

    private boolean keepAliveRunning = false;
    private final AtomicLong lastGetKeepAliveResponseFromServerTimstamp = new AtomicLong(0);
    private Observer networkConnectionLostObserver = null;

    private Handler keepAliveHandler = null;
    private Runnable keepAliveRunnable = null;
    private boolean keepAliveTaskExcuting = false;
    private boolean keepAliveWillStop = false;

    private MBSimpleTimer keepAliveTimeoutTimer = null;

    private boolean init = false;

    /** !本属性仅作DEBUG之用：DEBUG事件观察者 */
    private Observer debugObserver;

    public static KeepAliveDaemon getInstance() {
        if(instance == null)
            instance = new KeepAliveDaemon();
        return instance;
    }

    private KeepAliveDaemon() {
        init();
    }

    private void init() {
        if(init)
            return;

        keepAliveHandler = new Handler();
        keepAliveRunnable = () -> {
            if(!keepAliveTaskExcuting) {
                MBThreadPoolExecutor.runInBackground(() -> {
                    final int code = doKeepAlive();
                    MBThreadPoolExecutor.runOnMainThread(() -> onKeepAlive(code));
                });
            }
        };

        keepAliveTimeoutTimer = new MBSimpleTimer(NETWORK_CONNECTION_TIME_OUT_CHECK_INTERVAL){
            @Override
            protected void doAction(){
                if(ClientCoreSDK.DEBUG)
                    Log.i(TAG, "【IMCORE-TCP】心跳[超时检查]线程执行中...");

                doTimeoutCheck();
            }
        };
        keepAliveTimeoutTimer.init();

        init = true;
    }

    private int doKeepAlive() {
        keepAliveTaskExcuting = true;
        if(ClientCoreSDK.DEBUG)
            Log.i(TAG, "【IMCORE-TCP】心跳包[发送]线程执行中...");
        int code = LocalDataSender.getInstance().sendKeepAlive();

        return code;
    }

    private void onKeepAlive(int code) {
        // for DEBUG
        if(this.debugObserver != null)
            this.debugObserver.update(null, 2);

        boolean isInitialedForKeepAlive = isInitialedForKeepAlive();
        //## Bug FIX 20190513 v4.0.1 START
        if(isInitialedForKeepAlive)
            lastGetKeepAliveResponseFromServerTimstamp.set(System.currentTimeMillis());
        //## Bug FIX 20190513 v4.0.1 END

        keepAliveTaskExcuting = false;
        if(!keepAliveWillStop)
            keepAliveHandler.postDelayed(keepAliveRunnable, KEEP_ALIVE_INTERVAL);
    }

    private void doTimeoutCheck() {
        boolean isInitialedForKeepAlive = isInitialedForKeepAlive();
        if(!isInitialedForKeepAlive) {
            long now = System.currentTimeMillis();
            if(now - lastGetKeepAliveResponseFromServerTimstamp.longValue() >= NETWORK_CONNECTION_TIME_OUT) {
                if(ClientCoreSDK.DEBUG)
                    Log.w(TAG, "【IMCORE-TCP】心跳机制已判定网络断开，将进入断网通知和重连处理逻辑 ...");

                notifyConnectionLost();
                keepAliveWillStop = true;
            }
        }
    }

    private boolean isInitialedForKeepAlive() {
        return (lastGetKeepAliveResponseFromServerTimstamp.longValue() == 0);
    }

    public void notifyConnectionLost() {
        stop();
        if(networkConnectionLostObserver != null)
            networkConnectionLostObserver.update(null, null);
    }

    public void stop() {
        keepAliveTimeoutTimer.stop();

        keepAliveHandler.removeCallbacks(keepAliveRunnable);
        keepAliveRunning = false;
        keepAliveWillStop = false;
        lastGetKeepAliveResponseFromServerTimstamp.set(0);

        // for DEBUG
        if(this.debugObserver != null)
            this.debugObserver.update(null, 0);
    }

    public void start(boolean immediately) {
        stop();
        keepAliveHandler.postDelayed(keepAliveRunnable, immediately ? 0 : KEEP_ALIVE_INTERVAL);
        keepAliveRunning = true;
        keepAliveWillStop = false;

        keepAliveTimeoutTimer.start(immediately);

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