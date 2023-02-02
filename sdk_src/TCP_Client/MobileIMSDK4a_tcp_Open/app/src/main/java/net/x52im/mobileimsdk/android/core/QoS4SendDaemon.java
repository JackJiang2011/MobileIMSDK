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
 * QoS4SendDaemon.java at 2022-7-28 17:24:47, code by Jack Jiang.
 */
package net.x52im.mobileimsdk.android.core;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Observer;
import java.util.concurrent.ConcurrentHashMap;

import net.x52im.mobileimsdk.android.ClientCoreSDK;
import net.x52im.mobileimsdk.android.utils.MBThreadPoolExecutor;
import net.x52im.mobileimsdk.server.protocal.Protocal;

import android.os.Handler;
import android.util.Log;

public class QoS4SendDaemon {
    private final static String TAG = QoS4SendDaemon.class.getSimpleName();

    private static QoS4SendDaemon instance = null;
    public final static int CHECH_INTERVAL = 5000;
    public final static int MESSAGES_JUST$NOW_TIME = 3 * 1000;
    public final static int QOS_TRY_COUNT = 2;// since 3.0 (20160918): 为了降低服务端负载，本参数由原3调整为2

    private ConcurrentHashMap<String, Protocal> sentMessages = new ConcurrentHashMap<>();
    private ConcurrentHashMap<String, Long> sendMessagesTimestamp = new ConcurrentHashMap<>();
    private Handler handler = null;
    private Runnable runnable = null;
    private boolean running = false;
    private boolean _excuting = false;
    private boolean init = false;

    /** !本属性仅作DEBUG之用：DEBUG事件观察者 */
    private Observer debugObserver;

    public static QoS4SendDaemon getInstance() {
        if (instance == null) {
            synchronized (QoS4SendDaemon.class) {
                if (instance == null) {
                    instance = new QoS4SendDaemon();
                }
            }
        }
        return instance;
    }

    private QoS4SendDaemon() {
        init();
    }

    private void init() {
        if (init)
            return;

        handler = new Handler();
        runnable = () -> {
            if (!_excuting) {
                final ArrayList<Protocal> lostMessages = new ArrayList<>();
                MBThreadPoolExecutor.runInBackground(() -> {
                    final ArrayList<Protocal> ret = doRetryCheck(lostMessages);
                    MBThreadPoolExecutor.runOnMainThread(() -> onRetryCheck(ret));
                });
            }
        };

        init = true;
    }

    private ArrayList<Protocal> doRetryCheck(ArrayList<Protocal> lostMessages) {
        _excuting = true;
        try {
            if (ClientCoreSDK.DEBUG && sentMessages.size() > 0)
                Log.d(TAG, "【IMCORE-TCP】【QoS】====== 消息发送质量保证线程运行中, 当前需要处理的列表长度为" + sentMessages.size() + "...");

            for (String key : sentMessages.keySet()) {
                final Protocal p = sentMessages.get(key);
                if (p != null && p.isQoS()) {
                    if (p.getRetryCount() >= QOS_TRY_COUNT) {
                        if (ClientCoreSDK.DEBUG)
                            Log.d(TAG, "【IMCORE-TCP】【QoS】指纹为" + p.getFp()
                                    + "的消息包重传次数已达" + p.getRetryCount() + "(最多" + QOS_TRY_COUNT + "次)上限，将判定为丢包！");

                        lostMessages.add((Protocal) p.clone());
                        remove(p.getFp());
                    } else {
                        //### 2015103 Bug Fix: 解决了无线网络延较大时，刚刚发出的消息在其应答包还在途中时被错误地进行重传
                        Long sendMessageTimestamp = sendMessagesTimestamp.get(key);
                        long delta = System.currentTimeMillis() - (sendMessageTimestamp == null ? 0 : sendMessageTimestamp);
                        if (delta <= MESSAGES_JUST$NOW_TIME) {
                            if (ClientCoreSDK.DEBUG)
                                Log.w(TAG, "【IMCORE-TCP】【QoS】指纹为" + key + "的包距\"刚刚\"发出才" + delta
                                        + "ms(<=" + MESSAGES_JUST$NOW_TIME + "ms将被认定是\"刚刚\"), 本次不需要重传哦.");
                        }
                        //### 2015103 Bug Fix END
                        else {
                            new LocalDataSender.SendCommonDataAsync(p) {
                                @Override
                                protected void onPostExecute(Integer code) {
                                    p.increaseRetryCount();
                                    if (code == 0) {
                                        if (ClientCoreSDK.DEBUG)
                                            Log.d(TAG, "【IMCORE-TCP】【QoS】指纹为" + p.getFp() + "的消息包已成功进行重传，此次之后重传次数已达" + p.getRetryCount() + "(最多" + QOS_TRY_COUNT + "次).");
                                    } else {
                                        Log.w(TAG, "【IMCORE-TCP】【QoS】指纹为" + p.getFp() + "的消息包重传失败，它的重传次数之前已累计为" + p.getRetryCount() + "(最多" + QOS_TRY_COUNT + "次).");
                                    }
                                }
                            }.execute();
                        }
                    }
                } else {
                    remove(key);
                }
            }
        } catch (Exception eee) {
            Log.w(TAG, "【IMCORE-TCP】【QoS】消息发送质量保证线程运行时发生异常," + eee.getMessage(), eee);
        }

        return lostMessages;
    }

    private void onRetryCheck(ArrayList<Protocal> al) {
        // for DEBUG
        if(this.debugObserver != null)
            this.debugObserver.update(null, 2);

        if (al != null && al.size() > 0)
            notifyMessageLost(al);

        _excuting = false;
        handler.postDelayed(runnable, CHECH_INTERVAL);
    }

    protected void notifyMessageLost(ArrayList<Protocal> lostMessages) {
        if (ClientCoreSDK.getInstance().getMessageQoSEvent() != null)
            ClientCoreSDK.getInstance().getMessageQoSEvent().messagesLost(lostMessages);
    }

    public void startup(boolean immediately) {
        stop();
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

    boolean exist(String fingerPrint) {
        return sentMessages.get(fingerPrint) != null;
    }

    public void put(Protocal p) {
        if (p == null) {
            Log.w(TAG, "Invalid arg p==null.");
            return;
        }
        if (p.getFp() == null) {
            Log.w(TAG, "Invalid arg p.getFp() == null.");
            return;
        }

        if (!p.isQoS()) {
            Log.w(TAG, "This protocal is not QoS pkg, ignore it!");
            return;
        }

        if (sentMessages.get(p.getFp()) != null)
            Log.w(TAG, "【IMCORE-TCP】【QoS】指纹为" + p.getFp() + "的消息已经放入了发送质量保证队列，该消息为何会重复？（生成的指纹码重复？还是重复put？）");

        sentMessages.put(p.getFp(), p);
        sendMessagesTimestamp.put(p.getFp(), System.currentTimeMillis());
    }

    public void remove(final String fingerPrint) {
        sendMessagesTimestamp.remove(fingerPrint);
        Object removedObj = sentMessages.remove(fingerPrint);
        Log.w(TAG, "【IMCORE-TCP】【QoS】指纹为" + fingerPrint + "的消息已成功从发送质量保证队列中移除(可能是收到接收方的应答也可能是达到了重传的次数上限)，重试次数="
                + (removedObj != null ? ((Protocal) removedObj).getRetryCount() : "none呵呵."));
    }

    public void clear() {
        this.sentMessages.clear();
        this.sendMessagesTimestamp.clear();
    }

    public int size() {
        return sentMessages.size();
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
