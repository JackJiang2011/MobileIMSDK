/*
 * Copyright (C) 2016 即时通讯网(52im.net) The MobileIMSDK Project. 
 * All rights reserved.
 * Project URL:https://github.com/JackJiang2011/MobileIMSDK
 *  
 * 即时通讯网(52im.net) - 即时通讯技术社区! PROPRIETARY/CONFIDENTIAL.
 * Use is subject to license terms.
 * 
 * ServerCoreHandler.java at 2016-2-20 11:26:02, code by Jack Jiang.
 * You can contact author with jack.jiang@52im.net or jb2011@163.com.
 */
package net.openmob.mobileimsdk.server;

import java.net.SocketAddress;
import java.nio.ByteBuffer;
import net.openmob.mobileimsdk.server.event.MessageQoSEventListenerS2C;
import net.openmob.mobileimsdk.server.event.ServerEventListener;
import net.openmob.mobileimsdk.server.processor.UserProcessor;
import net.openmob.mobileimsdk.server.protocal.CharsetHelper;
import net.openmob.mobileimsdk.server.protocal.Protocal;
import net.openmob.mobileimsdk.server.protocal.ProtocalFactory;
import net.openmob.mobileimsdk.server.protocal.ProtocalType;
import net.openmob.mobileimsdk.server.protocal.c.PLoginInfo;
import net.openmob.mobileimsdk.server.qos.QoS4ReciveDaemonC2S;
import net.openmob.mobileimsdk.server.qos.QoS4SendDaemonS2C;
import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.future.WriteFuture;
import org.apache.mina.core.service.IoHandlerAdapter;
import org.apache.mina.core.session.IdleStatus;
import org.apache.mina.core.session.IoSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ServerCoreHandler extends IoHandlerAdapter
{
	private static Logger logger = LoggerFactory.getLogger(ServerCoreHandler.class);

	// 服务端事件回调实现
	private ServerEventListener serverEventListener = null;
	
	// QoS机制下的S2C模式中，由服务端主动发起消息的QoS事件回调实现
	private MessageQoSEventListenerS2C serverMessageQoSEventListener = null;

	@Override
    public void exceptionCaught(IoSession session, Throwable cause)
            throws Exception 
    {
        logger.error("[IMCORE]exceptionCaught捕获到错了，原因是："+cause.getMessage(), cause);
        session.close(true);
    }

	public void messageReceived(IoSession session, Object message) throws Exception
	{
		if ((message instanceof IoBuffer))
		{
			IoBuffer buffer = (IoBuffer)message;
			Protocal pFromClient = fromIOBuffer(buffer);

			String remoteAddress = clientInfoToString(session);
			switch (pFromClient.getType())
			{
				case ProtocalType.C.FROM_CLIENT_TYPE_OF_RECIVED:
				case ProtocalType.C.FROM_CLIENT_TYPE_OF_COMMON$DATA:
				{
					logger.info(">> 收到客户端" + remoteAddress + "的通用数据发送请求.");
	
					// 开始回调
					if (this.serverEventListener != null)
					{
						if (!UserProcessor.isLogined(session))
						{
							replyDataForUnlogined(session, pFromClient);
							return;
						}
	
						// 【C2S数据】客户端发给服务端的消息
						if (pFromClient.getTo() == 0)
						{
							if(pFromClient.getType() == ProtocalType.C.FROM_CLIENT_TYPE_OF_RECIVED)
							{
								String theFingerPrint = pFromClient.getDataContent();
								logger.debug("【IMCORE】【QoS机制_S2C】收到" + pFromClient.getFrom() + "发过来的指纹为" + theFingerPrint + "的应答包.");
	
								if (this.serverMessageQoSEventListener != null) {
									this.serverMessageQoSEventListener.messagesBeReceived(theFingerPrint);
								}
	
								QoS4SendDaemonS2C.getInstance().remove(theFingerPrint);
								break;
							}
	
							if (pFromClient.isQoS())
							{
								if (QoS4ReciveDaemonC2S.getInstance().hasRecieved(pFromClient.getFp()))
								{
									if (QoS4ReciveDaemonC2S.DEBUG) {
										logger.debug("【IMCORE】【QoS机制】" + pFromClient.getFp() + 
												"已经存在于发送列表中，这是重复包，通知业务处理层收到该包罗！");
									}
	
									QoS4ReciveDaemonC2S.getInstance().addRecieved(pFromClient);
	
									boolean receivedBackSendSucess = replyDelegateRecievedBack(session, pFromClient);
									if (receivedBackSendSucess) {
										logger.debug("【QoS_应答_C2S】向" + pFromClient.getFrom() + "发送" + pFromClient.getFp() + 
												"的应答包成功了,from=" + pFromClient.getTo() + ".");
									}
	
									return;
								}
	
								QoS4ReciveDaemonC2S.getInstance().addRecieved(pFromClient);
								boolean receivedBackSendSucess = replyDelegateRecievedBack(session, pFromClient);
								if (receivedBackSendSucess) {
									logger.debug("【QoS_应答_C2S】向" + pFromClient.getFrom() + "发送" + pFromClient.getFp() + 
											"的应答包成功了,from=" + pFromClient.getTo() + ".");
								}
							}
	
							boolean receivedBackSendSucess = this.serverEventListener.onTransBuffer_CallBack(
									pFromClient.getTo(), pFromClient.getFrom(), pFromClient.getDataContent(), pFromClient.getFp());
							break;
						}
	
						// TODO DEBUG
						UserProcessor.getInstance().__printOnline();
	
						boolean sendOK = sendData(pFromClient);
						if (sendOK)
						{
							this.serverEventListener.onTransBuffer_C2C_CallBack(
									pFromClient.getTo(), pFromClient.getFrom(), pFromClient.getDataContent());
							break;
						}
	
						logger.info("[IMCORE]>> 客户端" + remoteAddress + "的通用数据尝试实时发送没有成功，将交给应用层进行离线存储哦...");
	
						boolean offlineProcessedOK = this.serverEventListener
								.onTransBuffer_C2C_RealTimeSendFaild_CallBack(pFromClient.getTo(), 
										pFromClient.getFrom(), pFromClient.getDataContent(), pFromClient.getFp());
	
						if ((pFromClient.isQoS()) && (offlineProcessedOK))
						{
							boolean receivedBackSendSucess = replyDelegateRecievedBack(session, pFromClient);
							if (!receivedBackSendSucess) break;
							logger.debug("【QoS_伪应答_C2S】向" + pFromClient.getFrom() + "发送" + pFromClient.getFp() + 
									"的伪应答包成功,from=" + pFromClient.getTo() + ".");
							break;
						}
	
						logger.warn("[IMCORE]>> 客户端" + remoteAddress + "的通用数据传输消息尝试实时发送没有成功，但上层应用层没有成" + 
								"功(或者完全没有)进行离线存储，此消息将被服务端丢弃！");
	
						break;
					}
	
					logger.warn("[IMCORE]>> 收到客户端" + remoteAddress + "的通用数据传输消息，但回调对象是null，回调无法继续.");
					break;
				}
				case ProtocalType.C.FROM_CLIENT_TYPE_OF_KEEP$ALIVE:
				{
					if (!UserProcessor.isLogined(session))
					{
						replyDataForUnlogined(session, pFromClient);
						return;
					}
	
					sendData(ProtocalFactory.createPKeepAliveResponse(UserProcessor.getUserIdFromSession(session)));
					break;
				}
				case ProtocalType.C.FROM_CLIENT_TYPE_OF_LOGIN:
				{
					PLoginInfo loginInfo = ProtocalFactory.parsePLoginInfo(pFromClient.getDataContent());
					logger.info("[IMCORE]>> 客户端" + remoteAddress + "发过来的登陆信息内容是：getLoginName=" + 
							loginInfo.getLoginName() + "|getLoginPsw=" + loginInfo.getLoginPsw());
	
					if (this.serverEventListener != null)
					{
						int _try_user_id = UserProcessor.getUserIdFromSession(session);
	
						boolean alreadyLogined = _try_user_id != -1;
	
						if (alreadyLogined)
						{
							logger.debug("[IMCORE]>> 【注意】客户端" + remoteAddress + "的会话正常且已经登陆过，而此时又重新登陆：getLoginName=" + 
									loginInfo.getLoginName() + "|getLoginPsw=" + loginInfo.getLoginPsw());
	
							boolean sendOK = sendData(session, ProtocalFactory.createPLoginInfoResponse(0, _try_user_id));
							if (sendOK)
							{
								// 将用户登陆成功后的id暂存到会话对象中备用
	    						session.setAttribute(UserProcessor.USER_ID_IN_SESSION_ATTRIBUTE, _try_user_id);
	    						// 将用户登陆成功后的登陆名暂存到会话对象中备用
	    						session.setAttribute(UserProcessor.LOGIN_NAME_IN_SESSION_ATTRIBUTE, loginInfo.getLoginName());
	    						// 将用户信息放入到在线列表中（理论上：每一个存放在在线列表中的session都对应了user_id）
	    						UserProcessor.getInstance().putUser(_try_user_id, session, loginInfo.getLoginName());
								
	    						this.serverEventListener.onUserLoginAction_CallBack(_try_user_id, loginInfo.getLoginName(), session);
								break;
							}
	
							logger.warn("[IMCORE]>> 发给客户端" + remoteAddress + "的登陆成功信息发送失败了！");
							break;
						}
	
						int code = this.serverEventListener.onVerifyUserCallBack(loginInfo.getLoginName(), loginInfo.getLoginPsw(), loginInfo.getExtra());
						if (code == 0)
						{
							int user_id = getNextUserId(loginInfo);
	
							boolean sendOK = sendData(session, ProtocalFactory.createPLoginInfoResponse(code, user_id));
							if (sendOK)
							{
								// 将用户登陆成功后的id暂存到会话对象中备用
	    						session.setAttribute(UserProcessor.USER_ID_IN_SESSION_ATTRIBUTE, user_id);
	    						// 将用户登陆成功后的登陆名暂存到会话对象中备用
	    						session.setAttribute(UserProcessor.LOGIN_NAME_IN_SESSION_ATTRIBUTE, loginInfo.getLoginName());
	    						// 将用户信息放入到在线列表中（理论上：每一个存放在在线列表中的session都对应了user_id）
	    						UserProcessor.getInstance().putUser(user_id, session, loginInfo.getLoginName());
	    						
								this.serverEventListener.onUserLoginAction_CallBack(user_id, loginInfo.getLoginName(), session);
	
								break;
							}
							logger.warn("[IMCORE]>> 发给客户端" + remoteAddress + "的登陆成功信息发送失败了！");
							break;
						}
	
						sendData(session, ProtocalFactory.createPLoginInfoResponse(code, -1));
						break;
					}
	
					logger.warn("[IMCORE]>> 收到客户端" + remoteAddress + "登陆信息，但回调对象是null，没有进行回调.");
					break;
				}
				case ProtocalType.C.FROM_CLIENT_TYPE_OF_LOGOUT:
				{
					logger.info("[IMCORE]>> 收到客户端" + remoteAddress + "的退出登陆请求.");
	
					session.close(true);
					break;
				}
				// FIXME: 以下代码建议仅用于Debug时，否则存在恶意DDoS攻击的可能！
				// 【收到客户端发过来的ECHO指令（目前回显指令仅用于C2S时开发人员的网络测试，别无他用】
//				case ProtocalType.C.FROM_CLIENT_TYPE_OF_ECHO:
//				{
//					pFromClient.setType(53);
//					sendData(session, pFromClient);
//					break;
//				}
				default:
					logger.warn("[IMCORE]【注意】收到的客户端" + remoteAddress + "消息类型：" + pFromClient.getType() + "，但目前该类型服务端不支持解析和处理！");
					break;
			}
		} 
		else 
		{
			logger.error("[IMCORE]【注意】收到了未知数据类型的用户消息(messageReceived), message.class=" + message.getClass() + 
					", IoBuffer?" + (message instanceof IoBuffer) + 
					", ByteBuffer?" + (message instanceof ByteBuffer));
		}
	}

	protected int getNextUserId(PLoginInfo loginInfo)
	{
		return UserProcessor.nextUserId(loginInfo);
	}

	protected boolean replyDataForUnlogined(IoSession session, Protocal p) throws Exception
	{
		logger.warn("[IMCORE]>> 客户端" + clientInfoToString(session) + "尚未登陆，" + p.getDataContent() + "处理未继续.");

		return sendData(session, ProtocalFactory.createPErrorResponse(
				301, p.toGsonString(), -1));
	}

	protected boolean replyDelegateRecievedBack(IoSession session, Protocal pFromClient) throws Exception
	{
		if ((pFromClient.isQoS()) && (pFromClient.getFp() != null))
		{
			Protocal receivedBackP = ProtocalFactory.createRecivedBack(
					pFromClient.getTo(), 
					pFromClient.getFrom(), 
					pFromClient.getFp());

			return sendData(session, receivedBackP);
		}

		logger.warn("[IMCORE]收到" + pFromClient.getFrom() + 
				"发过来需要QoS的包，但它的指纹码却为null！无法发伪应答包哦！");
		return false;
	}

	public void sessionClosed(IoSession session) throws Exception
	{
		int user_id = UserProcessor.getUserIdFromSession(session);
		String loginName = UserProcessor.getLoginNameFromSession(session);
		logger.info("[IMCORE]与" + clientInfoToString(session) + "的会话关闭(user_id=" + user_id + ",loginName=" + loginName + ")了...");
		if (user_id != -1)
		{
			UserProcessor.getInstance().removeUser(user_id);

			if (this.serverEventListener != null)
			{
				this.serverEventListener.onUserLogoutAction_CallBack(user_id, null);
			}
			else logger.debug("[IMCORE]>> 客户端" + clientInfoToString(session) + "的会话被系统close了，但回调对象是null，没有进行回调.");
		}
		else
		{
			logger.warn("[IMCORE]【注意】客户端" + clientInfoToString(session) + "的会话被系统close了，但它里面没有存放user_id，这个会话是何时建立的？");
		}
	}

	public void sessionCreated(IoSession session) throws Exception
	{
		logger.info("[IMCORE]与" + clientInfoToString(session) + "的会话建立(sessionCreated)了...");
	}

	public void sessionIdle(IoSession session, IdleStatus status) throws Exception
	{
		logger.info("[IMCORE]Session idle...");
	}

	public void sessionOpened(IoSession session) throws Exception
	{
		logger.info("[IMCORE]与" + clientInfoToString(session) + "的会话(sessionOpened)打开了...");
	}

	ServerEventListener getServerEventListener()
	{
		return this.serverEventListener;
	}

	void setServerEventListener(ServerEventListener serverEventListener) {
		this.serverEventListener = serverEventListener;
	}

	MessageQoSEventListenerS2C getServerMessageQoSEventListener()
	{
		return this.serverMessageQoSEventListener;
	}

	void setServerMessageQoSEventListener(MessageQoSEventListenerS2C serverMessageQoSEventListener)
	{
		this.serverMessageQoSEventListener = serverMessageQoSEventListener;
	}

	static boolean sendData(int from_user_id, int to_user_id, String dataContent) throws Exception
	{
		return sendData(from_user_id, to_user_id, dataContent, false);
	}

	static boolean sendData(int from_user_id, int to_user_id, String dataContent, boolean QoS) throws Exception
	{
		return sendData(from_user_id, to_user_id, dataContent, QoS, null);
	}

	static boolean sendData(int from_user_id, int to_user_id, String dataContent, boolean QoS, String fingerPrint) throws Exception
	{
		return sendData(ProtocalFactory.createCommonData(dataContent, from_user_id, to_user_id, QoS, fingerPrint));
	}

	static boolean sendData(Protocal p) throws Exception
	{
		if (p != null)
		{
			if (p.getTo() != 0) {
				return sendData(UserProcessor.getInstance().getSession(p.getTo()), p);
			}

			logger.warn("[IMCORE]【注意】此Protocal对象中的接收方是服务器(user_id==0)，数据发送没有继续！" + p.toGsonString());
			return false;
		}

		return false;
	}

	static boolean sendData(IoSession session, Protocal p) throws Exception
	{
		if (session == null)
		{
			logger.info("[IMCORE]toSession==null >> id=" + p.getFrom() + "的用户尝试发给客户端" + p.getTo() + 
					"的消息：str=" + p.getDataContent() + "因接收方的id已不在线，此次实时发送没有继续(此消息可考虑作离线处理哦).");
		}
		else if (session.isConnected())
		{
			if (p != null)
			{
				byte[] res = p.toBytes();
				IoBuffer buf = IoBuffer.wrap(res);
				WriteFuture future = session.write(buf);
				future.awaitUninterruptibly(100L);
				// The message has been written successfully
				if (future.isWritten())
				{
					if (p.getFrom() == 0)
					{
						if ((p.isQoS()) && (!QoS4SendDaemonS2C.getInstance().exist(p.getFp()))) {
							QoS4SendDaemonS2C.getInstance().put(p);
						}
					}
					return true;
				}

				logger.warn("[IMCORE]给客户端：" + clientInfoToString(session) + "的数据->" + p.toGsonString() + ",发送失败！[" + res.length + "](此消息可考虑作离线处理哦).");
			}
		}
		else
		{
			logger.warn("[IMCORE]toSession!=null但会话已经关闭 >> 客户端id=" + p.getFrom() + "要发给客户端" + p.getTo() + 
					"的实时消息：str=" + p.getDataContent() + "没有继续(此消息可考虑作离线处理哦).");
		}

		return false;
	}

	public static String clientInfoToString(IoSession session)
	{
//		InetSocketAddress remoteAddress = (InetSocketAddress)session.getRemoteAddress();
		SocketAddress remoteAddress = session.getRemoteAddress();
//		return "["+remoteAddress.getHostName()+":"+remoteAddress.getPort()+"]";
		
//		long t1 = System.currentTimeMillis();
		String s1 = remoteAddress.toString();
//		System.out.println("【1】计算Ip地址耗时："+((System.currentTimeMillis()-t1)));
		
//		t1 = System.currentTimeMillis();
//		String s2 = remoteAddress.getPort()+"";
//		System.out.println("【3】计算Ip地址耗时："+((System.currentTimeMillis()-t1)));
//		
//		t1 = System.currentTimeMillis();
//		String str = "["+s1+":"+s2+"]";
//		System.out.println("【3】计算Ip地址耗时："+((System.currentTimeMillis()-t1)));
		
		StringBuilder sb = new StringBuilder()
		.append("{uid:")
		.append(UserProcessor.getLoginNameFromSession(session))
		.append(",cid:")
		.append(UserProcessor.getUserIdFromSession(session))
		.append("}")
		.append(s1);
		
		return sb.toString();
	}

	public static String fromIOBuffer_JSON(IoBuffer buffer) throws Exception
	{
		String jsonStr = buffer.getString(CharsetHelper.decoder);
		return jsonStr;
	}

	public static Protocal fromIOBuffer(IoBuffer buffer) throws Exception
	{
		return (Protocal)ProtocalFactory.parse(fromIOBuffer_JSON(buffer), Protocal.class);
	}
}