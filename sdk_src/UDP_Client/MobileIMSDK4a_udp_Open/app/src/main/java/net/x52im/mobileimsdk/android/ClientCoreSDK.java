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
 * ClientCoreSDK.java at 2021-7-2 22:38:44, code by Jack Jiang.
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
                //### Bug FIX: 2015-11-07 by Jack Jiang
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
        LocalSocketProvider.getInstance().closeLocalSocket();
        AutoReLoginDaemon.getInstance().stop(); // 2014-11-08 add by Jack Jiang
        QoS4SendDaemon.getInstance().stop();
        KeepAliveDaemon.getInstance().stop();
        LocalDataReciever.getInstance().stop();
        QoS4ReciveDaemon.getInstance().stop();

        //## Bug FIX: 20180103 by Jack Jiang START
        QoS4SendDaemon.getInstance().clear();
        QoS4ReciveDaemon.getInstance().clear();
        //## Bug FIX: END

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

    public PLoginInfo getCurrentLoginInfo()
	{
		return this.currentLoginInfo;
	}

	public void saveFirstLoginTime(long firstLoginTime) {
		if(this.currentLoginInfo != null)
			this.currentLoginInfo.setFirstLoginTime(firstLoginTime);
	}

	@Deprecated
	public String getCurrentLoginUserId() {
		return this.currentLoginInfo.getLoginUserId();
	}
	
    @Deprecated
	public String getCurrentLoginToken() {
		return this.currentLoginInfo.getLoginToken();
	}

    @Deprecated
	public String getCurrentLoginExtra() {
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

    // The BroadcastReceiver that listens for discovered devices and changes the title when discovery is finished
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
                    && !(ethernetInfo != null && ethernetInfo.isConnected())) {
//				if(ClientCoreSDK.DEBUG)
                Log.e(TAG, "【IMCORE-UDP】【本地网络通知】检测本地网络连接断开了!");

                LocalSocketProvider.getInstance().closeLocalSocket();
            } else {
                if (ClientCoreSDK.DEBUG)
                    Log.e(TAG, "【IMCORE-UDP】【本地网络通知】检测本地网络已连接上了!");

                LocalSocketProvider.getInstance().closeLocalSocket();
            }
        }
    };
}
