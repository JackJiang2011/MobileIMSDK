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
 * QoS4ReciveDaemon.java at 2022-7-28 17:24:47, code by Jack Jiang.
 */
package net.x52im.mobileimsdk.android.core;

import java.util.ArrayList;
import java.util.Observer;
import java.util.concurrent.ConcurrentHashMap;

import net.x52im.mobileimsdk.android.ClientCoreSDK;
import net.x52im.mobileimsdk.server.protocal.Protocal;

import android.os.Handler;
import android.util.Log;

public class QoS4ReciveDaemon {
    private final static String TAG = QoS4ReciveDaemon.class.getSimpleName();
    private static QoS4ReciveDaemon instance = null;
    public final static int CHECH_INTERVAL = 5 * 60 * 1000;      // 5分钟
    public final static int MESSAGES_VALID_TIME = 10 * 60 * 1000;// 10分钟

    private ConcurrentHashMap<String, Long> recievedMessages = new ConcurrentHashMap<String, Long>();
    private Handler handler = null;
    private Runnable runnable = null;
    private boolean running = false;
    private boolean _excuting = false;
    private boolean init = false;

    /** !本属性仅作DEBUG之用：DEBUG事件观察者 */
    private Observer debugObserver;

    public static QoS4ReciveDaemon getInstance() {
        if (instance == null)
            instance = new QoS4ReciveDaemon();
        return instance;
    }

    public QoS4ReciveDaemon() {
        init();
    }

    private void init() {
        if (init)
            return;

        handler = new Handler();
        runnable = () -> {
            if (!_excuting) {
                _excuting = true;
                if (ClientCoreSDK.DEBUG)
                    Log.d(TAG, "【IMCORE-TCP】【QoS接收方】+++++ START 暂存处理线程正在运行中，当前长度" + recievedMessages.size() + ".");

                for (String key : recievedMessages.keySet()) {
                    Long recievedTime = recievedMessages.get(key);
                    long delta = System.currentTimeMillis() - (recievedTime == null ? 0 : recievedTime);
                    if (delta >= MESSAGES_VALID_TIME) {
                        if (ClientCoreSDK.DEBUG)
                            Log.d(TAG, "【IMCORE-TCP】【QoS接收方】指纹为" + key + "的包已生存" + delta + "ms(最大允许" + MESSAGES_VALID_TIME + "ms), 马上将删除之.");
                        recievedMessages.remove(key);
                    }
                }
            }

            if (ClientCoreSDK.DEBUG)
                Log.d(TAG, "【IMCORE-TCP】【QoS接收方】+++++ END 暂存处理线程正在运行中，当前长度" + recievedMessages.size() + ".");

            // for DEBUG
            if(this.debugObserver != null)
                this.debugObserver.update(null, 2);

            _excuting = false;
            handler.postDelayed(runnable, CHECH_INTERVAL);
        };

        init = true;
    }

    public void startup(boolean immediately) {
        stop();
        if (recievedMessages != null && recievedMessages.size() > 0) {
            for (String key : recievedMessages.keySet()) {
                putImpl(key);
            }
        }

        handler.postDelayed(runnable, immediately ? 0 : CHECH_INTERVAL);
        running = true;

        // for DEBUG
        if(this.debugObserver != null)
            this.debugObserver.update(null, 1);
    }

    public void stop() {
        handler.removeCallbacks(runnable);
        running = false;

        // for DEBUG
        if(this.debugObserver != null)
            this.debugObserver.update(null, 0);
    }

    public boolean isRunning() {
        return running;
    }

    public boolean isInit() {
        return init;
    }

    public void addRecieved(Protocal p) {
        if (p != null && p.isQoS())
            addRecieved(p.getFp());
    }

    public void addRecieved(String fingerPrintOfProtocal) {
        if (fingerPrintOfProtocal == null) {
            Log.w(TAG, "【IMCORE-TCP】无效的 fingerPrintOfProtocal==null!");
            return;
        }

        if (recievedMessages.containsKey(fingerPrintOfProtocal))
            Log.w(TAG, "【IMCORE-TCP】【QoS接收方】指纹为" + fingerPrintOfProtocal
                    + "的消息已经存在于接收列表中，该消息重复了（原理可能是对方因未收到应答包而错误重传导致），更新收到时间戳哦.");

        putImpl(fingerPrintOfProtocal);
    }

    private void putImpl(String fingerPrintOfProtocal) {
        if (fingerPrintOfProtocal != null)
            recievedMessages.put(fingerPrintOfProtocal, System.currentTimeMillis());
    }

    public boolean hasRecieved(String fingerPrintOfProtocal) {
        return recievedMessages.containsKey(fingerPrintOfProtocal);
    }

    public void clear() {
        this.recievedMessages.clear();
    }

    public int size() {
        return recievedMessages.size();
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
