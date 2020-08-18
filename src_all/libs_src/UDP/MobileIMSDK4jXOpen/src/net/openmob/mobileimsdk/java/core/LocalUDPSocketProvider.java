/*
 * Copyright (C) 2020  即时通讯网(52im.net) & Jack Jiang.
 * The MobileIMSDK_X_netty (MobileIMSDK v4.x MINA版) Project. 
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
 * LocalUDPSocketProvider.java at 2020-4-15 22:53:59, code by Jack Jiang.
 */
package net.openmob.mobileimsdk.java.core;

import java.net.DatagramSocket;

import net.openmob.mobileimsdk.java.ClientCoreSDK;
import net.openmob.mobileimsdk.java.conf.ConfigEntity;
import net.openmob.mobileimsdk.java.utils.Log;

public class LocalUDPSocketProvider
{
	private final static String TAG = LocalUDPSocketProvider.class.getSimpleName();
	
	private static LocalUDPSocketProvider instance= null;

	private DatagramSocket localUDPSocket = null;
	
	public static LocalUDPSocketProvider getInstance()
	{
		if(instance == null)
			instance = new LocalUDPSocketProvider();
		return instance;
	}
	
	private LocalUDPSocketProvider()
	{
		//
	}
	
	public DatagramSocket resetLocalUDPSocket()
	{
		try
		{
			closeLocalUDPSocket();
			
//			if(ClientCoreSDK.DEBUG)
//				Log.d(TAG, "【IMCORE】new DatagramSocket()中...");
			
			localUDPSocket = (ConfigEntity.localUDPPort == 0?
					new DatagramSocket():new DatagramSocket(ConfigEntity.localUDPPort));//_Utils.LOCAL_UDP_SEND$LISTENING_PORT);
			localUDPSocket.setReuseAddress(true);
			
//			if(ClientCoreSDK.DEBUG)
//				Log.d(TAG, "【IMCORE】new DatagramSocket()已成功完成.");
			
			return localUDPSocket;
		}
		catch (Exception e)
		{
			Log.w(TAG, "【IMCORE】localUDPSocket创建时出错，原因是："+e.getMessage(), e);
			closeLocalUDPSocket();
			return null;
		}
	}
	
	private boolean isLocalUDPSocketReady()
	{
		return localUDPSocket != null && !localUDPSocket.isClosed();
	}
	
	public DatagramSocket getLocalUDPSocket()
	{
		if(isLocalUDPSocketReady())
		{
//			if(ClientCoreSDK.DEBUG)
//				Log.d(TAG, "【IMCORE】isLocalUDPSocketReady()==true，直接返回本地socket引用哦。");
			return localUDPSocket;
		}
		else
		{
//			if(ClientCoreSDK.DEBUG)
//				Log.d(TAG, "【IMCORE】isLocalUDPSocketReady()==false，需要先resetLocalUDPSocket()...");
			return resetLocalUDPSocket();
		}
	}
	
	public void closeLocalUDPSocket()
    {
        this.closeLocalUDPSocket(true);
    }
	
	public void closeLocalUDPSocket(boolean silent)
	{
		try
		{
			if(ClientCoreSDK.DEBUG && !silent)
				Log.d(TAG, "【IMCORE】正在closeLocalUDPSocket()...");
			
			if(localUDPSocket != null)
			{
				localUDPSocket.close();
				localUDPSocket = null;
			}
			else
			{
				if(!silent)
					Log.d(TAG, "【IMCORE】Socket处于未初化状态（可能是您还未登陆），无需关闭。");
			}	
		}
		catch (Exception e)
		{
			if(!silent)
				Log.w(TAG, "【IMCORE】lcloseLocalUDPSocket时出错，原因是："+e.getMessage(), e);
		}
	}
}
