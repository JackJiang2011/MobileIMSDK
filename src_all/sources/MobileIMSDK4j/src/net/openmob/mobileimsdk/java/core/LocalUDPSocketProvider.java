/*
 * Copyright (C) 2016 即时通讯网(52im.net) The MobileIMSDK Project. 
 * All rights reserved.
 * Project URL:https://github.com/JackJiang2011/MobileIMSDK
 *  
 * 即时通讯网(52im.net) - 即时通讯技术社区! PROPRIETARY/CONFIDENTIAL.
 * Use is subject to license terms.
 * 
 * LocalUDPSocketProvider.java at 2016-2-20 11:25:57, code by Jack Jiang.
 * You can contact author with jack.jiang@52im.net or jb2011@163.com.
 */
package net.openmob.mobileimsdk.java.core;

import java.net.DatagramSocket;
import net.openmob.mobileimsdk.java.ClientCoreSDK;
import net.openmob.mobileimsdk.java.conf.ConfigEntity;
import net.openmob.mobileimsdk.java.utils.Log;

public class LocalUDPSocketProvider
{
	private static final String TAG = LocalUDPSocketProvider.class.getSimpleName();

	private DatagramSocket localUDPSocket = null;

	private static LocalUDPSocketProvider instance = null;

	public static LocalUDPSocketProvider getInstance()
	{
		if (instance == null)
			instance = new LocalUDPSocketProvider();
		return instance;
	}

	private DatagramSocket resetLocalUDPSocket()
	{
		try
		{
			closeLocalUDPSocket();
			if (ClientCoreSDK.DEBUG)
				Log.d(TAG, "【IMCORE】new DatagramSocket()中...");
			this.localUDPSocket = (ConfigEntity.localUDPPort == 0 ? 
					new DatagramSocket() : new DatagramSocket(ConfigEntity.localUDPPort));
			this.localUDPSocket.setReuseAddress(true);
			if (ClientCoreSDK.DEBUG) {
				Log.d(TAG, "【IMCORE】new DatagramSocket()已成功完成.");
			}

			return this.localUDPSocket;
		}
		catch (Exception e)
		{
			Log.w(TAG, "【IMCORE】localUDPSocket创建时出错，原因是：" + e.getMessage(), e);

			closeLocalUDPSocket();
		}
		
		return null;
	}

	private boolean isLocalUDPSocketReady()
	{
		return (this.localUDPSocket != null) && (!this.localUDPSocket.isClosed());
	}

	public DatagramSocket getLocalUDPSocket()
	{
		if (isLocalUDPSocketReady())
		{
			if (ClientCoreSDK.DEBUG)
				Log.d(TAG, "【IMCORE】isLocalUDPSocketReady()==true，直接返回本地socket引用哦。");
			return this.localUDPSocket;
		}

		if (ClientCoreSDK.DEBUG)
			Log.d(TAG, "【IMCORE】isLocalUDPSocketReady()==false，需要先resetLocalUDPSocket()...");
		return resetLocalUDPSocket();
	}

	public void closeLocalUDPSocket()
	{
		try
		{
			if (ClientCoreSDK.DEBUG)
				Log.d(TAG, "【IMCORE】正在closeLocalUDPSocket()...");
			if (this.localUDPSocket != null)
			{
				this.localUDPSocket.close();
				this.localUDPSocket = null;
			}
			else
			{
				Log.d(TAG, "【IMCORE】Socket处于未初化状态（可能是您还未登陆），无需关闭。");
			}
		}
		catch (Exception e)
		{
			Log.w(TAG, "【IMCORE】lcloseLocalUDPSocket时出错，原因是：" + e.getMessage(), e);
		}
	}
}