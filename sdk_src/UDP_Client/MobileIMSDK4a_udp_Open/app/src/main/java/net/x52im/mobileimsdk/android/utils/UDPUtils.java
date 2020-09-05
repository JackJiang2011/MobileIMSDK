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
 * UDPUtils.java at 2020-8-18 15:40:04, code by Jack Jiang.
 */
package net.x52im.mobileimsdk.android.utils;

import java.net.DatagramPacket;
import java.net.DatagramSocket;

import android.util.Log;

public class UDPUtils {
    private final static String TAG = UDPUtils.class.getSimpleName();

    public static boolean send(DatagramSocket skt, byte[] d, int dataLen) {
        if (skt != null && d != null) {
            try {
                return send(skt, new DatagramPacket(d, dataLen));
            } catch (Exception e) {
                Log.e(TAG, "【IMCORE-UDP】send方法中》》发送UDP数据报文时出错了：remoteIp=" + skt.getInetAddress()
                        + ", remotePort=" + skt.getPort() + ".原因是：" + e.getMessage(), e);
                return false;
            }
        } else {
            Log.e(TAG, "【IMCORE-UDP】send方法中》》无效的参数：skt=" + skt);//
            return false;
        }
    }

    public synchronized static boolean send(DatagramSocket skt, DatagramPacket p) {
        boolean sendSucess = true;
        if (skt != null && p != null) {
            if (skt.isConnected()) {
                try {
                    skt.send(p);
                } catch (Exception e) {
                    sendSucess = false;
                    Log.e(TAG, "【IMCORE-UDP】send方法中》》发送UDP数据报文时出错了，原因是：" + e.getMessage(), e);
                }
            }
        } else {
            Log.w(TAG, "【IMCORE-UDP】在send()UDP数据报时没有成功执行，原因是：skt==null || p == null!");
        }

        return sendSucess;
    }
}
