/*
 * Copyright (C) 2017  即时通讯网(52im.net) & Jack Jiang.
 * The MobileIMSDK_X (MobileIMSDK v3.x) Project. 
 * All rights reserved.
 * 
 * > Github地址: https://github.com/JackJiang2011/MobileIMSDK
 * > 文档地址: http://www.52im.net/forum-89-1.html
 * > 即时通讯技术社区：http://www.52im.net/
 * > 即时通讯技术交流群：320837163 (http://www.52im.net/topic-qqgroup.html)
 *  
 * "即时通讯网(52im.net) - 即时通讯开发者社区!" 推荐开源工程。
 * 
 * LocalUDPDataReciever.java at 2017-5-1 22:14:56, code by Jack Jiang.
 * You can contact author with jack.jiang@52im.net or jb2011@163.com.
 */
package net.openmob.mobileimsdk.java.core;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketTimeoutException;
import java.util.Observable;
import java.util.Observer;

import net.openmob.mobileimsdk.java.ClientCoreSDK;
import net.openmob.mobileimsdk.java.conf.ConfigEntity;
import net.openmob.mobileimsdk.java.utils.Log;
import net.openmob.mobileimsdk.server.protocal.ErrorCode;
import net.openmob.mobileimsdk.server.protocal.Protocal;
import net.openmob.mobileimsdk.server.protocal.ProtocalFactory;
import net.openmob.mobileimsdk.server.protocal.ProtocalType;
import net.openmob.mobileimsdk.server.protocal.s.PErrorResponse;
import net.openmob.mobileimsdk.server.protocal.s.PLoginInfoResponse;

public class LocalUDPDataReciever
{
	private final static String TAG = LocalUDPDataReciever.class.getSimpleName();
	
	private static LocalUDPDataReciever instance = null;
	private static MessageHandler messageHandler = null;
	
	private Thread thread = null;
	
	public static LocalUDPDataReciever getInstance()
	{
		if(instance == null)
		{
			instance = new LocalUDPDataReciever();
			messageHandler = new MessageHandler();
		}
		return instance;
	}
	
	private LocalUDPDataReciever()
	{
	}
	
	public void stop()
	{
		if(thread != null)
		{
			thread.interrupt();
			thread = null;
		}
	}
	
	public void startup()
	{
		stop();
		
		try
		{
			thread = new Thread(new Runnable()
			{
				public void run()
				{
					try
					{
						if(ClientCoreSDK.DEBUG)
							Log.d(TAG, "【IMCORE】本地UDP端口侦听中，端口="+ConfigEntity.localUDPPort+"...");

						udpListeningImpl();
					}
					catch (Exception eee)
					{
						Log.w(TAG, "【IMCORE】本地UDP监听停止了(socket被关闭了?)："
                                +eee.getMessage()+"，应该是用户退出登陆或网络断开了。");
					}
				}
			});
			thread.start();
		}
		catch (Exception e)
		{
			Log.w(TAG, "【IMCORE】本地UDPSocket监听开启时发生异常,"+e.getMessage(), e);
		}
	}

	private void udpListeningImpl() throws Exception
	{
		while (true)
		{
			byte[] data = new byte[1024];
			DatagramPacket packet = new DatagramPacket(data, data.length);
			DatagramSocket localUDPSocket = LocalUDPSocketProvider.getInstance().getLocalUDPSocket();
			if (localUDPSocket != null && !localUDPSocket.isClosed())
			{
				/** ## Bug FIX 20190326 [Bug 1, Bug 2] - STAT
			        ## [Bug 20190326_1 描述: 因socket未被释放，导致监听线程无法退出，从而导致OOM的发生]
			        ## [Bug 20190326_2 描述: 因socket未被释放，导致虽手机网络物理连接正常(如WiFi)，但不能宽带上网时，导致宽带恢上网后仍不能重连成功]*/
				localUDPSocket.setSoTimeout(KeepAliveDaemon.NETWORK_CONNECTION_TIME_OUT + KeepAliveDaemon.KEEP_ALIVE_INTERVAL);
				/** ## Bug FIX 20190326[1,2] - STAT */
			
				try{
					localUDPSocket.receive(packet);
					messageHandler.handleMessage(packet);
				}
				catch(SocketTimeoutException e){
					Log.i(TAG, "【IMCORE】【udpListeningImpl】localUDPSocket.receive() 抛出了异常" +
							"SocketTimeoutException，应该是socket被close了，本次网络监听阻塞已被解除，socket" +
                            "监听线程也将正常结束生命周期。。。("+(thread == null ?"":thread.getId())+")");

					/** ## Bug FIX 20190326 [Bug 2] - STAT */
					try{
						if(localUDPSocket != null) {
							localUDPSocket.close();
							localUDPSocket = null;
						}
					}catch(Exception ee){
						Log.i(TAG, "【IMCORE】【udpListeningImpl】IN localUDPSocket.close(), cause="+ee.getMessage());
					}
					/** ## Bug FIX 20190326 [Bug 2] - END */
				}
			}
		}
	}
	
	private static class MessageHandler
	{
		public MessageHandler()
		{
		}
		
		public void handleMessage(DatagramPacket p)
		{
			DatagramPacket packet = p;
			if(packet == null)
				return;
			
			try
			{
				final Protocal pFromServer = 
						ProtocalFactory.parse(packet.getData(), packet.getLength());
				
				if(pFromServer.isQoS())
				{
					if(pFromServer.getType() == ProtocalType.S.FROM_SERVER_TYPE_OF_RESPONSE$LOGIN
							&& ProtocalFactory.parsePLoginInfoResponse(pFromServer.getDataContent()).getCode() != 0)
					{
						if(ClientCoreSDK.DEBUG)
							Log.d(TAG, "【IMCORE】【BugFIX】这是服务端的登陆返回响应包，" +
									"且服务端判定登陆失败(即code!=0)，本次无需发送ACK应答包！");
					}
					else
					{
						if(QoS4ReciveDaemon.getInstance().hasRecieved(pFromServer.getFp()))
						{
							if(ClientCoreSDK.DEBUG)
								Log.d(TAG, "【IMCORE】【QoS机制】"+pFromServer.getFp()+"已经存在于发送列表中，这是重复包，通知应用层收到该包罗！");

							QoS4ReciveDaemon.getInstance().addRecieved(pFromServer);
							sendRecievedBack(pFromServer);
							return;
						}

						QoS4ReciveDaemon.getInstance().addRecieved(pFromServer);
						sendRecievedBack(pFromServer);
					}
				}
				
				switch(pFromServer.getType())
				{
					case ProtocalType.C.FROM_CLIENT_TYPE_OF_COMMON$DATA:
					{
						if(ClientCoreSDK.getInstance().getChatTransDataEvent() != null)
						{
							ClientCoreSDK.getInstance().getChatTransDataEvent().onTransBuffer(
									pFromServer.getFp(), pFromServer.getFrom()
									, pFromServer.getDataContent(), pFromServer.getTypeu());
						}
						break;
					}
					case ProtocalType.S.FROM_SERVER_TYPE_OF_RESPONSE$KEEP$ALIVE:
					{
						if(ClientCoreSDK.DEBUG)
							Log.p(TAG, "【IMCORE】收到服务端回过来的Keep Alive心跳响应包.");
						KeepAliveDaemon.getInstance().updateGetKeepAliveResponseFromServerTimstamp();
						break;
					}
					case ProtocalType.C.FROM_CLIENT_TYPE_OF_RECIVED:
					{
						String theFingerPrint = pFromServer.getDataContent();
						if(ClientCoreSDK.DEBUG)
							Log.i(TAG, "【IMCORE】【QoS】收到"+pFromServer.getFrom()+"发过来的指纹为"+theFingerPrint+"的应答包.");
						
						if(ClientCoreSDK.getInstance().getMessageQoSEvent() != null)
							ClientCoreSDK.getInstance().getMessageQoSEvent().messagesBeReceived(theFingerPrint);

						QoS4SendDaemon.getInstance().remove(theFingerPrint);
						break;
					}
					case ProtocalType.S.FROM_SERVER_TYPE_OF_RESPONSE$LOGIN:
					{
						PLoginInfoResponse loginInfoRes = ProtocalFactory.parsePLoginInfoResponse(pFromServer.getDataContent());
						if(loginInfoRes.getCode() == 0)
						{
							ClientCoreSDK.getInstance().setLoginHasInit(true);
							AutoReLoginDaemon.getInstance().stop();
							KeepAliveDaemon.getInstance().setNetworkConnectionLostObserver(new Observer(){
								public void update(Observable observable, Object data)
								{
//									QoS4SendDaemon.getInstance().stop();
									/** ## Bug FIX 20190326 [Bug 1] - STAT
							            ## [Bug 20190326_1 描述: 因socket未被释放，导致监听线程无法退出，从而导致OOM的发生] */
									LocalUDPSocketProvider.getInstance().closeLocalUDPSocket();
									/** ## Bug FIX 20190326 [Bug 1] - END */
									QoS4ReciveDaemon.getInstance().stop();
									ClientCoreSDK.getInstance().setConnectedToServer(false);
//									LocalUDPSocketProvider.getInstance().closeLocalUDPSocket();
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
							LocalUDPDataReciever.getInstance().stop();
							ClientCoreSDK.getInstance().setConnectedToServer(false);
						}
							
						if(ClientCoreSDK.getInstance().getChatBaseEvent() != null)
						{
							ClientCoreSDK.getInstance().getChatBaseEvent().onLoginMessage(
								loginInfoRes.getCode());
						}
						
						break;
					}
					case ProtocalType.S.FROM_SERVER_TYPE_OF_RESPONSE$FOR$ERROR:
					{
						PErrorResponse errorRes = ProtocalFactory.parsePErrorResponse(pFromServer.getDataContent());
						if(errorRes.getErrorCode() == ErrorCode.ForS.RESPONSE_FOR_UNLOGIN)
						{
							ClientCoreSDK.getInstance().setLoginHasInit(false);
							Log.e(TAG, "【IMCORE】收到服务端的“尚未登陆”的错误消息，心跳线程将停止，请应用层重新登陆.");
							KeepAliveDaemon.getInstance().stop();
							AutoReLoginDaemon.getInstance().start(false);
						}
						
						if(ClientCoreSDK.getInstance().getChatTransDataEvent() != null)
						{
							ClientCoreSDK.getInstance().getChatTransDataEvent().onErrorResponse(
									errorRes.getErrorCode(), errorRes.getErrorMsg());
						}
						break;
					}
					
					default:
						Log.w(TAG, "【IMCORE】收到的服务端消息类型："+pFromServer.getType()+"，但目前该类型客户端不支持解析和处理！");
						break;
				}
			}
			catch (Exception e)
			{
				Log.w(TAG, "【IMCORE】处理消息的过程中发生了错误.", e);
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
								, pFromServer.getFp()
								// since 3.0
								, pFromServer.isBridge())){
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
