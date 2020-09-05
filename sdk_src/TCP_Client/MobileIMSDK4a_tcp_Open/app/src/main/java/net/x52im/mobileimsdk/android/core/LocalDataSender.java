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
 * LocalDataSender.java at 2020-8-8 15:58:02, code by Jack Jiang.
 */
package net.x52im.mobileimsdk.android.core;

import net.x52im.mobileimsdk.android.ClientCoreSDK;
import net.x52im.mobileimsdk.android.utils.MBObserver;
import net.x52im.mobileimsdk.android.utils.TCPUtils;
import net.x52im.mobileimsdk.server.protocal.ErrorCode;
import net.x52im.mobileimsdk.server.protocal.Protocal;
import net.x52im.mobileimsdk.server.protocal.ProtocalFactory;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import io.netty.channel.Channel;

public class LocalDataSender {
    private final static String TAG = LocalDataSender.class.getSimpleName();
    private static LocalDataSender instance = null;

    public static LocalDataSender getInstance() {
        if (instance == null)
            instance = new LocalDataSender();
        return instance;
    }

    private LocalDataSender() {
    }

    int sendLogin(String loginUserId, String loginToken, String extra) {

        int codeForCheck = this.checkBeforeSend();
        if (codeForCheck != ErrorCode.COMMON_CODE_OK)
            return codeForCheck;

        if (!LocalSocketProvider.getInstance().isLocalSocketReady()) {

            if (ClientCoreSDK.DEBUG)
                Log.d(TAG, "【IMCORE-TCP】发送登陆指令时，socket连接未就绪，首先开始尝试发起连接（登陆指令将在连接成功后的回调中自动发出）。。。。");

            MBObserver connectionDoneObserver = (sucess, extraObj) -> {
                if (sucess)
                    sendLoginImpl(loginUserId, loginToken, extra);
                else
                    Log.w(TAG, "【IMCORE-TCP】[来自Netty的连接结果回调观察者通知]socket连接失败，本次登陆信息未成功发出！");
            };
            LocalSocketProvider.getInstance().setConnectionDoneObserver(connectionDoneObserver);
            return LocalSocketProvider.getInstance().resetLocalSocket() != null
                    ? ErrorCode.COMMON_CODE_OK : ErrorCode.ForC.BAD_CONNECT_TO_SERVER;
        } else {
            return this.sendLoginImpl(loginUserId, loginToken, extra);
        }
    }

    int sendLoginImpl(String loginUserId, String loginToken, String extra) {
        byte[] b = ProtocalFactory.createPLoginInfo(loginUserId, loginToken, extra).toBytes();
        int code = send(b, b.length);
        if (code == 0) {
            ClientCoreSDK.getInstance().setCurrentLoginUserId(loginUserId);
            ClientCoreSDK.getInstance().setCurrentLoginToken(loginToken);
            ClientCoreSDK.getInstance().setCurrentLoginExtra(extra);
        }

        return code;
    }

    public int sendLoginout() {
        int code = ErrorCode.COMMON_CODE_OK;
        if (ClientCoreSDK.getInstance().isLoginHasInit()) {
            byte[] b = ProtocalFactory.createPLoginoutInfo(
                    ClientCoreSDK.getInstance().getCurrentLoginUserId()).toBytes();
            code = send(b, b.length);
            if (code == 0) {
                // do nothing
            }
        }
        ClientCoreSDK.getInstance().release();
        return code;
    }

    int sendKeepAlive() {
        byte[] b = ProtocalFactory.createPKeepAlive(ClientCoreSDK.getInstance().getCurrentLoginUserId()).toBytes();
        return send(b, b.length);
    }

    public int sendCommonData(String dataContentWidthStr, String to_user_id) {
        return sendCommonData(dataContentWidthStr, to_user_id, -1);
    }

    public int sendCommonData(String dataContentWidthStr, String to_user_id, int typeu) {
        return sendCommonData(dataContentWidthStr, to_user_id, null, typeu);
    }

    public int sendCommonData(String dataContentWidthStr, String to_user_id, String fingerPrint, int typeu) {
        return sendCommonData(dataContentWidthStr, to_user_id, true, fingerPrint, typeu);
    }

    public int sendCommonData(String dataContentWidthStr, String to_user_id, boolean QoS, String fingerPrint, int typeu) {
        return sendCommonData(ProtocalFactory.createCommonData(dataContentWidthStr
                , ClientCoreSDK.getInstance().getCurrentLoginUserId(), to_user_id, QoS, fingerPrint, typeu));
    }

    public int sendCommonData(Protocal p) {
        if (p != null) {
            byte[] b = p.toBytes();
            int code = send(b, b.length);
            if (code == 0) {
                if (p.isQoS() && !QoS4SendDaemon.getInstance().exist(p.getFp()))
                    QoS4SendDaemon.getInstance().put(p);
            }
            return code;
        } else
            return ErrorCode.COMMON_INVALID_PROTOCAL;
    }

    private int send(byte[] fullProtocalBytes, int dataLen) {

        int codeForCheck = this.checkBeforeSend();
        if (codeForCheck != ErrorCode.COMMON_CODE_OK)
            return codeForCheck;

        Channel ds = LocalSocketProvider.getInstance().getLocalSocket();
        if (ds != null && ds.isActive()) {// && [ClientCoreSDK sharedInstance].connectedToServer)
            return TCPUtils.send(ds, fullProtocalBytes, dataLen) ? ErrorCode.COMMON_CODE_OK : ErrorCode.COMMON_DATA_SEND_FAILD;
        } else {
            Log.d(TAG, "【IMCORE-TCP】scocket未连接，无法发送，本条将被忽略（dataLen=" + dataLen + "）!");
            return ErrorCode.COMMON_CODE_OK;
        }
    }

    private int checkBeforeSend() {
        if (!ClientCoreSDK.getInstance().isInitialed())
            return ErrorCode.ForC.CLIENT_SDK_NO_INITIALED;

//		if(!ClientCoreSDK.getInstance().isLocalDeviceNetworkOk()) {
//			Log.e(TAG, "【IMCORE-TCP】本地网络不能工作，send数据没有继续!");
//			return ErrorCode.ForC.LOCAL_NETWORK_NOT_WORKING;
//		}

        return ErrorCode.COMMON_CODE_OK;
    }

    //------------------------------------------------------------------------------------------ utilities class

    public static abstract class SendCommonDataAsync extends AsyncTask<Object, Integer, Integer> {
        protected Protocal p = null;

        public SendCommonDataAsync(String dataContentWidthStr, String to_user_id) {
            this(dataContentWidthStr, to_user_id, null, -1);
        }

        public SendCommonDataAsync(String dataContentWidthStr, String to_user_id, int typeu) {
            this(dataContentWidthStr, to_user_id, null, typeu);
        }

        public SendCommonDataAsync(String dataContentWidthStr, String to_user_id, String fingerPrint, int typeu) {
            this(ProtocalFactory.createCommonData(dataContentWidthStr
                    , ClientCoreSDK.getInstance().getCurrentLoginUserId(), to_user_id, true, fingerPrint, typeu));
        }

        public SendCommonDataAsync(Protocal p) {
            if (p == null) {
                Log.w(TAG, "【IMCORE-TCP】无效的参数p==null!");
                return;
            }
            this.p = p;
        }

        @Override
        protected Integer doInBackground(Object... params) {
            if (p != null)
                return LocalDataSender.getInstance().sendCommonData(p);//dataContentWidthStr, to_user_id);
            return -1;
        }

        @Override
        protected abstract void onPostExecute(Integer code);
    }

    public static abstract class SendLoginDataAsync extends AsyncTask<Object, Integer, Integer> {
        protected String loginUserId = null;
        protected String loginToken = null;
        protected String extra = null;

        public SendLoginDataAsync(String loginUserId, String loginToken) {
            this(loginUserId, loginToken, null);
        }

        public SendLoginDataAsync(String loginUserId, String loginToken, String extra) {
            this.loginUserId = loginUserId;
            this.loginToken = loginToken;
            this.extra = extra;

//			//### Bug Fix 2015-11-07 by Jack Jiang
//			// 确保首先进行核心库的初始化（此方法多次调用是无害的，但必须要
//			// 保证在使用IM核心库的任何实质方法前调用（初始化）1次））
//			ClientCoreSDK.getInstance().init(context);
        }

        @Override
        protected Integer doInBackground(Object... params) {
            int code = LocalDataSender.getInstance().sendLogin(loginUserId, loginToken, this.extra);
            return code;
        }

        @Override
        protected void onPostExecute(Integer code) {
            if (code == 0) {
//				LocalUDPDataReciever.getInstance().startup();
            } else {
                Log.d(TAG, "【IMCORE-TCP】数据发送失败, 错误码是：" + code + "！");
            }

            fireAfterSendLogin(code);
        }

        protected void fireAfterSendLogin(int code) {
            // default do nothing
        }
    }
}
