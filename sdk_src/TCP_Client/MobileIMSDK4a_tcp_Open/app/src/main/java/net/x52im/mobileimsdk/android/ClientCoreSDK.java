/*
 * Copyright (C) 2021  即时通讯网(52im.net) & Jack Jiang.
 * The MobileIMSDK_TCP (MobileIMSDK v6.x TCP版) Project. 
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
 * ClientCoreSDK.java at 2021-7-1 15:08:17, code by Jack Jiang.
 */
package net.x52im.mobileimsdk.android;

import net.x52im.mobileimsdk.android.core.AutoReLoginDaemon;
import net.x52im.mobileimsdk.android.core.KeepAliveDaemon;
import net.x52im.mobileimsdk.android.core.LocalDataReciever;
import net.x52im.mobileimsdk.android.core.LocalDataSender;
import net.x52im.mobileimsdk.android.core.LocalSocketProvider;
import net.x52im.mobileimsdk.android.core.QoS4ReciveDaemon;
import net.x52im.mobileimsdk.android.core.QoS4SendDaemon;
import net.x52im.mobileimsdk.android.event.ChatBaseEvent;
import net.x52im.mobileimsdk.android.event.ChatMessageEvent;
import net.x52im.mobileimsdk.android.event.MessageQoSEvent;
import net.x52im.mobileimsdk.server.protocal.c.PLoginInfo;

import android.app.Application;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

public class ClientCoreSDK {
    private final static String TAG = ClientCoreSDK.class.getSimpleName();

    public static boolean DEBUG = true;
    public static boolean autoReLogin = true;
    private static ClientCoreSDK instance = null;

    private boolean _init = false;

    //## 20200817备注：localDeviceNetworkOk字段的更新依赖于APP收到的系统的网络事件，而此事件在某些极端情况下
    //##              无法正常收到，进而当网络本身已恢复而此字段并未正确更新的情况下，会导致LocalDataSender中的send
    //##              数据方法在前置检查时，抛出 "ErrorCode.ForC.LOCAL_NETWORK_NOT_WORKING"错误而不会继续数据发送，
    //##              进而无法实现重连指令的发出，从而无法实现重连成功。有鉴于此，停用这个字段是更好的选择！
//	/**
//	 * 网络是否可用, true表示可用，否则表示不可用.
//	 * <p>
//	 * 本字段将在网络事件通知处理中被设置.
//	 * <p>
//	 * 注意：本类中的网络状态变更事件，尤其在网络由断变好之后，受Android系统广播机制的影响事件收到延迟在1~2秒，目
//	 * 前没有找到其它更优的替代方案，但从算法逻辑上讲不影响本核心类库的工作（仅影响核心库算法的构造难易度而已）！
//	 */
//	private boolean localDeviceNetworkOk = true;
    private boolean connectedToServer = true;
    private boolean loginHasInit = false;
    private PLoginInfo currentLoginInfo = null;

    private ChatBaseEvent chatBaseEvent = null;
    private ChatMessageEvent chatMessageEvent = null;
    private MessageQoSEvent messageQoSEvent = null;

    private Context context = null;

    public static ClientCoreSDK getInstance() {
        if (instance == null)
            instance = new ClientCoreSDK();
        return instance;
    }

    private ClientCoreSDK() {
//		init();
    }

    public void init(Context _context) {
        if (!_init) {
            if (_context == null)
                throw new IllegalArgumentException("context can't be null!");

            if (_context instanceof Application)
                this.context = _context;
            else {
                this.context = _context.getApplicationContext();
            }

            // Register for broadcasts when network status changed
            IntentFilter intentFilter = new IntentFilter();
            intentFilter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
            this.context.registerReceiver(networkConnectionStatusBroadcastReceiver, intentFilter);

            AutoReLoginDaemon.getInstance();
            KeepAliveDaemon.getInstance();
            LocalDataReciever.getInstance();
            QoS4ReciveDaemon.getInstance();
            QoS4SendDaemon.getInstance();

            //
            _init = true;
        }
    }

    public void release() {
        ClientCoreSDK.getInstance().setConnectedToServer(false);// Bug FIX:  2021-02-23 add by Jack Jiang
        LocalSocketProvider.getInstance().closeLocalSocket();
        AutoReLoginDaemon.getInstance().stop(); // 2014-11-08 add by Jack Jiang
        QoS4SendDaemon.getInstance().stop();
        KeepAliveDaemon.getInstance().stop();
//		LocalUDPDataReciever.getInstance().stop();
        QoS4ReciveDaemon.getInstance().stop();

        //## Bug FIX: 20180103 by Jack Jiang START
        QoS4SendDaemon.getInstance().clear();
        QoS4ReciveDaemon.getInstance().clear();
        //## Bug FIX: 20180103 by Jack Jiang END

        try {
            context.unregisterReceiver(networkConnectionStatusBroadcastReceiver);
        } catch (Exception e) {
            Log.i(TAG, "还未注册android网络事件广播的监听器，本次取消注册已被正常忽略哦.");
        }

        _init = false;
        this.setLoginHasInit(false);
        this.setConnectedToServer(false);
    }

    public void setCurrentLoginInfo(PLoginInfo currentLoginInfo) {
		this.currentLoginInfo = currentLoginInfo;
	}

    public PLoginInfo getCurrentLoginInfo() {
		return this.currentLoginInfo;
	}

	public void saveFirstLoginTime(long firstLoginTime) {
		if(this.currentLoginInfo != null)
			this.currentLoginInfo.setFirstLoginTime(firstLoginTime);
	}

    @Deprecated
	public String getCurrentLoginUserId()
	{
		return this.currentLoginInfo.getLoginUserId();
	}

    @Deprecated
	public String getCurrentLoginToken()
	{
		return this.currentLoginInfo.getLoginToken();
	}

    @Deprecated
	public String getCurrentLoginExtra()
	{
		return this.currentLoginInfo.getExtra();
	}

    public boolean isLoginHasInit() {
        return loginHasInit;
    }

    public ClientCoreSDK setLoginHasInit(boolean loginHasInit) {
        this.loginHasInit = loginHasInit;
        return this;
    }

    public boolean isConnectedToServer() {
        return connectedToServer;
    }

    public void setConnectedToServer(boolean connectedToServer) {
        this.connectedToServer = connectedToServer;
    }

    public boolean isInitialed() {
        return this._init;
    }

    public void setChatBaseEvent(ChatBaseEvent chatBaseEvent) {
        this.chatBaseEvent = chatBaseEvent;
    }

    public ChatBaseEvent getChatBaseEvent() {
        return chatBaseEvent;
    }

    public void setChatMessageEvent(ChatMessageEvent chatMessageEvent) {
        this.chatMessageEvent = chatMessageEvent;
    }

    public ChatMessageEvent getChatMessageEvent() {
        return chatMessageEvent;
    }

    public void setMessageQoSEvent(MessageQoSEvent messageQoSEvent) {
        this.messageQoSEvent = messageQoSEvent;
    }

    public MessageQoSEvent getMessageQoSEvent() {
        return messageQoSEvent;
    }

    //--------------------------------------------------------------------------------------- inner class

    private final BroadcastReceiver networkConnectionStatusBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            ConnectivityManager connectMgr = (ConnectivityManager) context.getSystemService(context.CONNECTIVITY_SERVICE);
            NetworkInfo mobNetInfo = connectMgr.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
            NetworkInfo wifiNetInfo = connectMgr.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
            NetworkInfo ethernetInfo = connectMgr.getNetworkInfo(9);
            if (!(mobNetInfo != null && mobNetInfo.isConnected())
                    && !(wifiNetInfo != null && wifiNetInfo.isConnected())
                    // ## Bug FIX 20170228: 解决当Android系统用有线网连接时没有判断此网事件的问题
                    && !(ethernetInfo != null && ethernetInfo.isConnected())
			) {
                Log.w(TAG, "【IMCORE-TCP】【本地网络通知】检测本地网络连接断开了!");
                LocalSocketProvider.getInstance().closeLocalSocket();
            } else {
                if (ClientCoreSDK.DEBUG)
                    Log.i(TAG, "【IMCORE-TCP】【本地网络通知】检测本地网络已连接上了!");
                LocalSocketProvider.getInstance().closeLocalSocket();
            }
        }
    };
}
