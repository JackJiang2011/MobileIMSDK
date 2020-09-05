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
 * LocalDataReciever.java at 2020-8-8 15:58:02, code by Jack Jiang.
 */
package net.x52im.mobileimsdk.android.core;

import java.util.Observable;
import java.util.Observer;

import net.x52im.mobileimsdk.android.ClientCoreSDK;
import net.x52im.mobileimsdk.server.protocal.ErrorCode;
import net.x52im.mobileimsdk.server.protocal.Protocal;
import net.x52im.mobileimsdk.server.protocal.ProtocalFactory;
import net.x52im.mobileimsdk.server.protocal.ProtocalType;
import net.x52im.mobileimsdk.server.protocal.s.PErrorResponse;
import net.x52im.mobileimsdk.server.protocal.s.PLoginInfoResponse;

import android.util.Log;

import net.x52im.mobileimsdk.android.utils.MBThreadPoolExecutor;

public class LocalDataReciever {
    private final static String TAG = LocalDataReciever.class.getSimpleName();
    private static LocalDataReciever instance = null;

    public static LocalDataReciever getInstance() {
        if (instance == null)
            instance = new LocalDataReciever();

        return instance;
    }

    private LocalDataReciever() {
        init();
    }

    private void init() {
    }

    public void handleProtocal(final byte[] fullProtocalOfBody) {
        MBThreadPoolExecutor.runOnMainThread(() -> handleProtocalImpl(fullProtocalOfBody));
    }

    private void handleProtocalImpl(byte[] fullProtocalOfBody) {

        if (fullProtocalOfBody == null || fullProtocalOfBody.length == 0) {
            Log.d(TAG, "【IMCORE-TCP】无效的fullProtocalOfBody（null 或 .length == 0）！");
            return;
        }

        try {
            final Protocal pFromServer = ProtocalFactory.parse(fullProtocalOfBody, fullProtocalOfBody.length);

            if (pFromServer.isQoS()) {
                // # Bug FIX B20170620_001 START 【1/2】
                // # [Bug描述]：当服务端认证接口返回非0的code时，客记端会进入自动登陆尝试死循环。
                // # [Bug原因]：原因在于客户端收到服务端的响应包时，因服务端发过来的包需要QoS，客户端会先发送一
                //             个ACK包，那么此ACK包到达服务端后会因客户端“未登陆”而再次发送一“未登陆”错误信息
                //             包给客户端，客户端在收到此包后会触发自动登陆重试，进而进入死循环。
                // # [解决方法]：客户端判定当收到的是服务端的登陆响应包且code不等于0就不需要回ACK包给服务端。
                // # [此解决方法带来的服务端表现]：服务端会因客户端网络关闭而将响应包进行重传直到超时丢弃，但并不影响什么。
                if (pFromServer.getType() == ProtocalType.S.FROM_SERVER_TYPE_OF_RESPONSE$LOGIN
                        && ProtocalFactory.parsePLoginInfoResponse(pFromServer.getDataContent()).getCode() != 0) {
                    if (ClientCoreSDK.DEBUG)
                        Log.d(TAG, "【IMCORE-TCP】【BugFIX】这是服务端的登陆返回响应包，" +
                                "且服务端判定登陆失败(即code!=0)，本次无需发送ACK应答包！");
                }
                // # Bug FIX 20170620 END 【1/2】
                else {
                    if (QoS4ReciveDaemon.getInstance().hasRecieved(pFromServer.getFp())) {
                        if (ClientCoreSDK.DEBUG)
                            Log.d(TAG, "【IMCORE-TCP】【QoS机制】" + pFromServer.getFp() + "已经存在于发送列表中，这是重复包，通知应用层收到该包罗！");

                        QoS4ReciveDaemon.getInstance().addRecieved(pFromServer);
                        sendRecievedBack(pFromServer);

                        return;
                    }

                    QoS4ReciveDaemon.getInstance().addRecieved(pFromServer);
                    sendRecievedBack(pFromServer);
                }
            }

            switch (pFromServer.getType()) {
                case ProtocalType.C.FROM_CLIENT_TYPE_OF_COMMON$DATA: {
                    onRecievedCommonData(pFromServer);
                    break;
                }
                case ProtocalType.S.FROM_SERVER_TYPE_OF_RESPONSE$KEEP$ALIVE: {
                    onServerResponseKeepAlive();
                    break;
                }
                case ProtocalType.C.FROM_CLIENT_TYPE_OF_RECIVED: {
                    onMessageRecievedACK(pFromServer);
                    break;
                }
                case ProtocalType.S.FROM_SERVER_TYPE_OF_RESPONSE$LOGIN: {
                    onServerResponseLogined(pFromServer);
                    break;
                }
                case ProtocalType.S.FROM_SERVER_TYPE_OF_RESPONSE$FOR$ERROR: {
                    onServerResponseError(pFromServer);
                    break;
                }

                default:
                    Log.w(TAG, "【IMCORE-TCP】收到的服务端消息类型：" + pFromServer.getType() + "，但目前该类型客户端不支持解析和处理！");
                    break;
            }
        } catch (Exception e) {
            Log.w(TAG, "【IMCORE-TCP】处理消息的过程中发生了错误.", e);
        }
    }

    protected void onRecievedCommonData(Protocal pFromServer) {
//		Log.d(TAG, ">>>>>>>>>>>>>>>>>>>>>>>>>>>>收到"+pFromServer.getFrom()+"发过来的消息："+pFromServer.getDataContent()+".["+pFromServer.getTo()+"]");
        // 收到通用数据的回调
        if (ClientCoreSDK.getInstance().getChatMessageEvent() != null) {
            ClientCoreSDK.getInstance().getChatMessageEvent().onRecieveMessage(
                    pFromServer.getFp(), pFromServer.getFrom()
                    , pFromServer.getDataContent(), pFromServer.getTypeu());
        }
    }

    protected void onServerResponseKeepAlive() {
        if (ClientCoreSDK.DEBUG)
            Log.d(TAG, "【IMCORE-TCP】收到服务端回过来的Keep Alive心跳响应包.");
        KeepAliveDaemon.getInstance().updateGetKeepAliveResponseFromServerTimstamp();
    }

    protected void onMessageRecievedACK(Protocal pFromServer) {
        String theFingerPrint = pFromServer.getDataContent();
        if (ClientCoreSDK.DEBUG)
            Log.d(TAG, "【IMCORE-TCP】【QoS】收到" + pFromServer.getFrom() + "发过来的指纹为" + theFingerPrint + "的应答包.");

        if (ClientCoreSDK.getInstance().getMessageQoSEvent() != null)
            ClientCoreSDK.getInstance().getMessageQoSEvent().messagesBeReceived(theFingerPrint);

        QoS4SendDaemon.getInstance().remove(theFingerPrint);
    }

    protected void onServerResponseLogined(Protocal pFromServer) {
        PLoginInfoResponse loginInfoRes = ProtocalFactory.parsePLoginInfoResponse(pFromServer.getDataContent());
        if (loginInfoRes.getCode() == 0) {
            fireConnectedToServer();
        } else {
            Log.d(TAG, "【IMCORE-TCP】登陆验证失败，错误码=" + loginInfoRes.getCode() + "！");

//			// # Bug FIX B20170620_001 START 【2/2】
//			// 登陆失败后关闭网络监听是合理的作法
//			LocalUDPDataReciever.getInstance().stop();
//			// # Bug FIX B20170620_001 END 【2/2】

            LocalSocketProvider.getInstance().closeLocalSocket();
            ClientCoreSDK.getInstance().setConnectedToServer(false);
        }

        if (ClientCoreSDK.getInstance().getChatBaseEvent() != null) {
            ClientCoreSDK.getInstance().getChatBaseEvent().onLoginResponse(loginInfoRes.getCode());
        }
    }

    protected void onServerResponseError(Protocal pFromServer) {
        PErrorResponse errorRes = ProtocalFactory.parsePErrorResponse(pFromServer.getDataContent());
        if (errorRes.getErrorCode() == ErrorCode.ForS.RESPONSE_FOR_UNLOGIN) {
            ClientCoreSDK.getInstance().setLoginHasInit(false);

            Log.e(TAG, "【IMCORE-TCP】收到服务端的“尚未登陆”的错误消息，心跳线程将停止，请应用层重新登陆.");
            KeepAliveDaemon.getInstance().stop();
            AutoReLoginDaemon.getInstance().start(false);
        }

        if (ClientCoreSDK.getInstance().getChatMessageEvent() != null) {
            ClientCoreSDK.getInstance().getChatMessageEvent().onErrorResponse(
                    errorRes.getErrorCode(), errorRes.getErrorMsg());
        }
    }

    protected void fireConnectedToServer() {
        ClientCoreSDK.getInstance().setLoginHasInit(true);
        AutoReLoginDaemon.getInstance().stop();
        KeepAliveDaemon.getInstance().setNetworkConnectionLostObserver(new Observer() {
            @Override
            public void update(Observable observable, Object data) {
                fireDisconnectedToServer();
            }
        });
        // ** 2015-02-10 by Jack Jiang：收到登陆成功反馈后，无需立即就发起心跳，因为刚刚才与服务端
        // ** 成功通信了呢（刚收到服务器的登陆成功反馈），节省1次心跳，降低服务重启后的“雪崩”可能性
        KeepAliveDaemon.getInstance().start(false);

        QoS4SendDaemon.getInstance().startup(true);
        QoS4ReciveDaemon.getInstance().startup(true);
        ClientCoreSDK.getInstance().setConnectedToServer(true);
    }

    protected void fireDisconnectedToServer() {
        ClientCoreSDK.getInstance().setConnectedToServer(false);
//		QoS4SendDaemon.getInstance(context).stop();
        LocalSocketProvider.getInstance().closeLocalSocket();
        QoS4ReciveDaemon.getInstance().stop();
        ClientCoreSDK.getInstance().getChatBaseEvent().onLinkClose(-1);
        AutoReLoginDaemon.getInstance().start(true);// 建议：此参数可由true改为false，防止服务端重启等情况下，客户端立即重连等
    }

    private void sendRecievedBack(final Protocal pFromServer) {
        if (pFromServer.getFp() != null) {
            new LocalDataSender.SendCommonDataAsync(
                    ProtocalFactory.createRecivedBack(
                            pFromServer.getTo()
                            , pFromServer.getFrom()
                            , pFromServer.getFp()
                            , pFromServer.isBridge())) {
                @Override
                protected void onPostExecute(Integer code) {
                    if (ClientCoreSDK.DEBUG)
                        Log.d(TAG, "【IMCORE-TCP】【QoS】向" + pFromServer.getFrom() + "发送" + pFromServer.getFp() + "包的应答包成功,from=" + pFromServer.getTo() + "！");
                }
            }.execute();
        } else {
            Log.w(TAG, "【IMCORE-TCP】【QoS】收到" + pFromServer.getFrom() + "发过来需要QoS的包，但它的指纹码却为null！无法发应答包！");
        }
    }
}