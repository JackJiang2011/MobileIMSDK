/*
 * Copyright (C) 2022  即时通讯网(52im.net) & Jack Jiang.
 * The MobileIMSDK_UDP (MobileIMSDK v6.x UDP版) Project. 
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
 * LocalSocketProvider.java at 2022-7-28 17:23:40, code by Jack Jiang.
 */
package net.x52im.mobileimsdk.android.core;

import java.net.DatagramSocket;

import net.x52im.mobileimsdk.android.ClientCoreSDK;
import net.x52im.mobileimsdk.android.conf.ConfigEntity;

import android.util.Log;

public class LocalSocketProvider {
    private final static String TAG = LocalSocketProvider.class.getSimpleName();
    private static LocalSocketProvider instance = null;

    private DatagramSocket localSocket = null;

    public static LocalSocketProvider getInstance() {
        if (instance == null) {
            synchronized (LocalSocketProvider.class) {
                if (instance == null) {
                    instance = new LocalSocketProvider();
                }
            }
        }
        return instance;
    }

    private LocalSocketProvider() {
        //
    }

    public DatagramSocket resetLocalSocket() {
        try {
            closeLocalSocket();

//			if(ClientCoreSDK.DEBUG)
//				Log.d(TAG, "【IMCORE】new DatagramSocket()中...");

            localSocket = (ConfigEntity.localPort == 0 ?
                    new DatagramSocket() : new DatagramSocket(ConfigEntity.localPort));//_Utils.LOCAL_UDP_SEND$LISTENING_PORT);
            localSocket.setReuseAddress(true);

//			if(ClientCoreSDK.DEBUG)
//				Log.d(TAG, "【IMCORE】new DatagramSocket()已成功完成.");

            return localSocket;
        } catch (Exception e) {
            Log.w(TAG, "【IMCORE-UDP】localSocket创建时出错，原因是：" + e.getMessage(), e);
            closeLocalSocket();
            return null;
        }
    }

    private boolean isLocalSocketReady() {
        return localSocket != null && !localSocket.isClosed();
    }

    public DatagramSocket getLocalSocket() {
        if (isLocalSocketReady()) {
//			if(ClientCoreSDK.DEBUG)
//				Log.d(TAG, "【IMCORE-UDP】isLocalSocketReady()==true，直接返回本地socket引用哦。");
            return localSocket;
        } else {
//			if(ClientCoreSDK.DEBUG)
//				Log.d(TAG, "【IMCORE-UDP】isLocalSocketReady()==false，需要先resetLocalSocket()...");
            return resetLocalSocket();
        }
    }

    public void closeLocalSocket() {
        this.closeLocalSocket(true);
    }

    public void closeLocalSocket(boolean silent) {
        try {
            if (ClientCoreSDK.DEBUG && !silent)
                Log.d(TAG, "【IMCORE-UDP】正在closeLocalSocket()...");

            if (localSocket != null) {
                localSocket.close();
                localSocket = null;
            } else {
                if (!silent)
                    Log.d(TAG, "【IMCORE-UDP】Socket处于未初化状态（可能是您还未登陆），无需关闭。");
            }
        } catch (Exception e) {
            if (!silent)
                Log.w(TAG, "【IMCORE-UDP】lcloseLocalSocket时出错，原因是：" + e.getMessage(), e);
        }
    }
}
