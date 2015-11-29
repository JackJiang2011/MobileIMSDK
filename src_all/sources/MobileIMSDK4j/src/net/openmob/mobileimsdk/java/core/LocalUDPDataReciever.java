/*
 * Copyright (C) 2015 Jack Jiang The MobileIMSDK Project. 
 * All rights reserved.
 * Project URL:https://github.com/JackJiang2011/MobileIMSDK
 *  
 * openmob.net PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 * 
 * Archived at 2015-11-27 14:02:01, code by Jack Jiang.
 * You can contact author with jack.jiang@openmob.net or jb2011@163.com.
 */
package net.openmob.mobileimsdk.java.core;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.util.Observable;
import java.util.Observer;

import net.openmob.mobileimsdk.java.ClientCoreSDK;
import net.openmob.mobileimsdk.java.conf.ConfigEntity;
import net.openmob.mobileimsdk.java.utils.Log;
import net.openmob.mobileimsdk.server.protocal.Protocal;
import net.openmob.mobileimsdk.server.protocal.ProtocalFactory;
import net.openmob.mobileimsdk.server.protocal.ProtocalType;
import net.openmob.mobileimsdk.server.protocal.s.PErrorResponse;
import net.openmob.mobileimsdk.server.protocal.s.PLoginInfoResponse;

public class LocalUDPDataReciever
{
	private static final String TAG = LocalUDPDataReciever.class.getSimpleName();

	private Thread thread = null;

	private static LocalUDPDataReciever instance = null;
	private static MessageHandler messageHandler = null;

	public static LocalUDPDataReciever getInstance()
	{
		if (instance == null)
		{
			instance = new LocalUDPDataReciever();
			messageHandler = new MessageHandler();
		}
		return instance;
	}

	public void stop()
	{
		if (this.thread != null)
		{
			this.thread.interrupt();
			this.thread = null;
		}
	}

	public void startup()
	{
		stop();
		try
		{
			this.thread = new Thread(new Runnable()
			{
				public void run()
				{
					try
					{
						if (ClientCoreSDK.DEBUG) {
							Log.d(LocalUDPDataReciever.TAG, "【IMCORE】本地UDP端口侦听中，端口=" + ConfigEntity.localUDPPort + "...");
						}

						//开始侦听
						LocalUDPDataReciever.this.p2pListeningImpl();
					}
					catch (Exception eee)
					{
						Log.w(LocalUDPDataReciever.TAG, "【IMCORE】本地UDP监听停止了(socket被关闭了?)," + eee.getMessage(), eee);
					}
				}
			});
			this.thread.start();
		}
		catch (Exception e)
		{
			Log.w(TAG, "【IMCORE】本地UDPSocket监听开启时发生异常," + e.getMessage(), e);
		}
	}

	private void p2pListeningImpl() throws Exception
	{
		while (true)
		{
			byte[] data = new byte[1024];
			// 接收数据报的包
			DatagramPacket packet = new DatagramPacket(data, data.length);

			DatagramSocket localUDPSocket = LocalUDPSocketProvider.getInstance().getLocalUDPSocket();
			if ((localUDPSocket == null) || (localUDPSocket.isClosed())) {
				continue;
			}
			
			localUDPSocket.receive(packet);
			messageHandler.handleMessage(packet);
		}
	}

	private static class MessageHandler
	{
		public void handleMessage(DatagramPacket p)
		{
			DatagramPacket packet = p;
			if (packet == null) {
				return;
			}

			try
			{
				Protocal pFromServer = 
						ProtocalFactory.parse(packet.getData(), packet.getLength());

				if (pFromServer.isQoS())
				{
					if (QoS4ReciveDaemon.getInstance().hasRecieved(pFromServer.getFp()))
					{
						if (ClientCoreSDK.DEBUG) {
							Log.d(LocalUDPDataReciever.TAG, "【IMCORE】【QoS机制】" + pFromServer.getFp() + "已经存在于发送列表中，这是重复包，通知应用层收到该包罗！");
						}

						QoS4ReciveDaemon.getInstance().addRecieved(pFromServer);
						sendRecievedBack(pFromServer);
						return;
					}

					QoS4ReciveDaemon.getInstance().addRecieved(pFromServer);
					sendRecievedBack(pFromServer);
				}

				switch (pFromServer.getType())
				{
					case ProtocalType.C.FROM_CLIENT_TYPE_OF_COMMON$DATA:
					{
						if (ClientCoreSDK.getInstance().getChatTransDataEvent() == null)
							break;
						ClientCoreSDK.getInstance().getChatTransDataEvent().onTransBuffer(
								pFromServer.getFp(), pFromServer.getFrom(), pFromServer.getDataContent());
	
						break;
					}
					case ProtocalType.S.FROM_SERVER_TYPE_OF_RESPONSE$KEEP$ALIVE:
					{
						if (ClientCoreSDK.DEBUG) {
							Log.p(LocalUDPDataReciever.TAG, "【IMCORE】收到服务端回过来的Keep Alive心跳响应包.");
						}
						KeepAliveDaemon.getInstance().updateGetKeepAliveResponseFromServerTimstamp();
						break;
					}
					case ProtocalType.C.FROM_CLIENT_TYPE_OF_RECIVED:
					{
						String theFingerPrint = pFromServer.getDataContent();
						if (ClientCoreSDK.DEBUG) {
							Log.i(LocalUDPDataReciever.TAG, "【IMCORE】【QoS】收到" + pFromServer.getFrom() + "发过来的指纹为" + theFingerPrint + "的应答包.");
						}
	
						if (ClientCoreSDK.getInstance().getMessageQoSEvent() != null) {
							ClientCoreSDK.getInstance().getMessageQoSEvent().messagesBeReceived(theFingerPrint);
						}
	
						QoS4SendDaemon.getInstance().remove(theFingerPrint);
						break;
					}
					case ProtocalType.S.FROM_SERVER_TYPE_OF_RESPONSE$LOGIN:
					{
						PLoginInfoResponse loginInfoRes = 
								ProtocalFactory.parsePLoginInfoResponse(pFromServer.getDataContent());
	
						if (loginInfoRes.getCode() == 0)
						{
							ClientCoreSDK.getInstance()
								.setLoginHasInit(true)
								.setCurrentUserId(loginInfoRes.getUser_id());
							AutoReLoginDaemon.getInstance().stop();
							KeepAliveDaemon.getInstance().setNetworkConnectionLostObserver(new Observer()
							{
								public void update(Observable observable, Object data)
								{
									QoS4SendDaemon.getInstance().stop();
									QoS4ReciveDaemon.getInstance().stop();
									ClientCoreSDK.getInstance().setConnectedToServer(false);
									ClientCoreSDK.getInstance().setCurrentUserId(-1);
									ClientCoreSDK.getInstance().getChatBaseEvent().onLinkCloseMessage(-1);
									AutoReLoginDaemon.getInstance().start(true);
								}
							});
							
							KeepAliveDaemon.getInstance().start(false);
							QoS4SendDaemon.getInstance().startup(true);
							QoS4ReciveDaemon.getInstance().startup(true);
							ClientCoreSDK.getInstance().setConnectedToServer(true);
						}
						else
						{
							ClientCoreSDK.getInstance().setConnectedToServer(false);
							ClientCoreSDK.getInstance().setCurrentUserId(-1);
						}
	
						// TODO FOR DEBUG
						System.out.println("【注意：：：】登陆成功，User_id()=" + loginInfoRes.getUser_id() + ", getChatBaseEvent=" + ClientCoreSDK.getInstance().getChatBaseEvent());
	
						if (ClientCoreSDK.getInstance().getChatBaseEvent() == null)
							break;
						ClientCoreSDK.getInstance().getChatBaseEvent().onLoginMessage(
								loginInfoRes.getUser_id(), loginInfoRes.getCode());
						break;
					}
					case ProtocalType.S.FROM_SERVER_TYPE_OF_RESPONSE$FOR$ERROR:
					{
						PErrorResponse errorRes = ProtocalFactory.parsePErrorResponse(pFromServer.getDataContent());
	
						if (errorRes.getErrorCode() == 301)
						{
							ClientCoreSDK.getInstance().setLoginHasInit(false);
	
							Log.e(LocalUDPDataReciever.TAG, "【IMCORE】收到服务端的“尚未登陆”的错误消息，心跳线程将停止，请应用层重新登陆.");
	
							KeepAliveDaemon.getInstance().stop();
							AutoReLoginDaemon.getInstance().start(false);
						}
	
						if (ClientCoreSDK.getInstance().getChatTransDataEvent() == null)
							break;
						ClientCoreSDK.getInstance().getChatTransDataEvent().onErrorResponse(
								errorRes.getErrorCode(), errorRes.getErrorMsg());
	
						break;
					}
					default:
						Log.w(LocalUDPDataReciever.TAG, "【IMCORE】收到的服务端消息类型：" + pFromServer.getType() + "，但目前该类型客户端不支持解析和处理！");
				}
			}
			catch (Exception e)
			{
				Log.w(LocalUDPDataReciever.TAG, "【IMCORE】处理消息的过程中发生了错误.", e);
			}
		}

		private void sendRecievedBack(final Protocal pFromServer)
		{
			if(pFromServer.getFp() != null)
			{
				new LocalUDPDataSender.SendCommonDataAsync(
						ProtocalFactory.createRecivedBack(
								pFromServer.getTo()
								, pFromServer.getFrom()
								, pFromServer.getFp())){
					@Override
					protected void onPostExecute(Integer code)
					{
						if(ClientCoreSDK.DEBUG)
							Log.d(TAG, "【IMCORE】【QoS】向"+pFromServer.getFrom()+"发送"+pFromServer.getFp()+"包的应答包成功,from="+pFromServer.getTo()+"！");
					}
				}.execute();
			}
			else
			{
				Log.w(TAG, "【IMCORE】【QoS】收到"+pFromServer.getFrom()+"发过来需要QoS的包，但它的指纹码却为null！无法发应答包！");
			}
		}
	}
}