/*
 * Copyright (C) 2020  即时通讯网(52im.net) & Jack Jiang.
 * The MobileIMSDK_UDP (MobileIMSDK v5.x UDP版) Project.
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
 * LocalUDPDataReciever.java at 2020-8-18 15:40:04, code by Jack Jiang.
 */
package net.x52im.mobileimsdk.android.core;

import java.net.DatagramPacket;
import java.net.DatagramSocket;

import net.x52im.mobileimsdk.android.ClientCoreSDK;
import net.x52im.mobileimsdk.android.conf.ConfigEntity;

import android.os.Message;
import android.util.Log;

public class LocalDataReciever {
    private final static String TAG = LocalDataReciever.class.getSimpleName();
    private static LocalDataReciever instance = null;

    private LocalDataHandler messageHandler = null;
    private Thread thread = null;
    private boolean init = false;

    public static LocalDataReciever getInstance() {
        if (instance == null)
            instance = new LocalDataReciever();
        return instance;
    }

    private LocalDataReciever() {
        init();
    }

    private void init() {
        if (init)
            return;

        messageHandler = new LocalDataHandler();
        init = true;
    }

    public void stop() {
        if (thread != null) {
            thread.interrupt();
            thread = null;
        }
    }

    public void startup() {
        stop();

        try {
            thread = new Thread(() -> {
                try {
                    if (ClientCoreSDK.DEBUG)
                        Log.d(TAG, "【IMCORE-UDP】本地UDP端口侦听中，端口=" + ConfigEntity.localPort + "...");

                    udpListeningImpl();
                } catch (Exception eee) {
                    Log.w(TAG, "【IMCORE-UDP】本地UDP监听停止了(socket被关闭了?)：" + eee.getMessage() + "，应该是用户退出登陆或网络断开了。");
                }
            });
            thread.start();
        } catch (Exception e) {
            Log.w(TAG, "【IMCORE-UDP】本地UDPSocket监听开启时发生异常," + e.getMessage(), e);
        }
    }

    public boolean isInit() {
        return init;
    }

    private void udpListeningImpl() throws Exception {
        while (true) {
            byte[] data = new byte[1024];
            DatagramPacket packet = new DatagramPacket(data, data.length);
            DatagramSocket localUDPSocket = LocalSocketProvider.getInstance().getLocalSocket();
            if (localUDPSocket != null && !localUDPSocket.isClosed()) {
                localUDPSocket.receive(packet);

                Message m = Message.obtain();
                m.obj = packet;
                messageHandler.sendMessage(m);
            }
        }
    }
}
