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

import java.net.DatagramSocket;
import java.net.InetAddress;
import net.openmob.mobileimsdk.java.ClientCoreSDK;
import net.openmob.mobileimsdk.java.conf.ConfigEntity;
import net.openmob.mobileimsdk.java.utils.Log;
import net.openmob.mobileimsdk.java.utils.UDPUtils;
import net.openmob.mobileimsdk.server.protocal.CharsetHelper;
import net.openmob.mobileimsdk.server.protocal.ErrorCode;
import net.openmob.mobileimsdk.server.protocal.Protocal;
import net.openmob.mobileimsdk.server.protocal.ProtocalFactory;
import org.jdesktop.swingworker.SwingWorker;

public class LocalUDPDataSender
{
	private static final String TAG = LocalUDPDataSender.class.getSimpleName();
	private static LocalUDPDataSender instance = null;

	public static LocalUDPDataSender getInstance()
	{
		if (instance == null)
			instance = new LocalUDPDataSender();
		return instance;
	}

	int sendLogin(String loginName, String loginPsw)
	{
		byte[] b = ProtocalFactory.createPLoginInfo(loginName, loginPsw).toBytes();
		int code = send(b, b.length);
		// 登陆信息成功发出时就把登陆名存下来
		if(code == 0)
		{
			ClientCoreSDK.getInstance().setCurrentLoginName(loginName);
			ClientCoreSDK.getInstance().setCurrentLoginPsw(loginPsw);
		}
		
		return code;
	}

	public int sendLoginout()
	{
		int code = ErrorCode.COMMON_CODE_OK;
		if(ClientCoreSDK.getInstance().isLoginHasInit())
		{
			byte[] b = ProtocalFactory.createPLoginoutInfo(ClientCoreSDK.getInstance().getCurrentUserId()
					, ClientCoreSDK.getInstance().getCurrentLoginName()).toBytes();
			code = send(b, b.length);
			// 登出信息成功发出时
			if(code == 0)
			{
	//			// 发出退出登陆的消息同时也关闭心跳线程
	//			KeepAliveDaemon.getInstance(context).stop();
	//			// 重置登陆标识
	//			ClientCoreSDK.getInstance().setLoginHasInit(false);
			}
		}
		
		// 释放SDK资源
		ClientCoreSDK.getInstance().release();
		
		return code;
	}

	int sendKeepAlive()
	{
		byte[] b = ProtocalFactory.createPKeepAlive(ClientCoreSDK.getInstance().getCurrentUserId()).toBytes();
		return send(b, b.length);
	}

	public int sendCommonData(byte[] dataContent, int dataLen, int to_user_id)
	{
		return sendCommonData(CharsetHelper.getString(dataContent, dataLen), to_user_id, false, null);
	}

	public int sendCommonData(byte[] dataContent, int dataLen, int to_user_id, boolean QoS, String fingerPrint)
	{
		return sendCommonData(CharsetHelper.getString(dataContent, dataLen), to_user_id, QoS, fingerPrint);
	}

	public int sendCommonData(String dataContentWidthStr, int to_user_id)
	{
		return sendCommonData(ProtocalFactory.createCommonData(dataContentWidthStr, 
				ClientCoreSDK.getInstance().getCurrentUserId(), to_user_id));
	}

	public int sendCommonData(String dataContentWidthStr, int to_user_id, boolean QoS, String fingerPrint)
	{
		return sendCommonData(ProtocalFactory.createCommonData(dataContentWidthStr, 
				ClientCoreSDK.getInstance().getCurrentUserId(), to_user_id, QoS, fingerPrint));
	}

	public int sendCommonData(Protocal p)
	{
		if (p != null)
		{
			byte[] b = p.toBytes();
			int code = send(b, b.length);
			if (code == 0)
			{
				if ((p.isQoS()) && (!QoS4SendDaemon.getInstance().exist(p.getFp())))
					QoS4SendDaemon.getInstance().put(p);
			}
			return code;
		}

		return ErrorCode.COMMON_INVALID_PROTOCAL;
	}

	private int send(byte[] fullProtocalBytes, int dataLen)
	{
		if(!ClientCoreSDK.getInstance().isInitialed())
			return ErrorCode.ForC.CLIENT_SDK_NO_INITIALED;
		
		DatagramSocket ds = LocalUDPSocketProvider.getInstance().getLocalUDPSocket();
		// 如果Socket没有连接上服务端
		if(ds != null && !ds.isConnected())
		{
			try
			{
				if(ConfigEntity.serverIP == null)
				{
					Log.w(TAG, "【IMCORE】send数据没有继续，原因是ConfigEntity.server_ip==null!");
					return ErrorCode.ForC.TO_SERVER_NET_INFO_NOT_SETUP;
				}
				
				ds.connect(InetAddress.getByName(ConfigEntity.serverIP), ConfigEntity.serverUDPPort);
			}
			catch (Exception e)
			{
				Log.w(TAG, "【IMCORE】send时出错，原因是："+e.getMessage(), e);
				return ErrorCode.ForC.BAD_CONNECT_TO_SERVER;
			}
		}
		return UDPUtils.send(ds, fullProtocalBytes, dataLen) ? ErrorCode.COMMON_CODE_OK : ErrorCode.COMMON_DATA_SEND_FAILD;
	}

	public static abstract class SendCommonDataAsync extends SwingWorker<Integer, Object>
	{
		protected Protocal p = null;

		public SendCommonDataAsync(byte[] dataContent, int dataLen, int to_user_id)
		{
			this(CharsetHelper.getString(dataContent, dataLen), to_user_id);
		}

		public SendCommonDataAsync(String dataContentWidthStr, int to_user_id, boolean QoS)
		{
			this(dataContentWidthStr, to_user_id, QoS, null);
		}

		public SendCommonDataAsync(String dataContentWidthStr, int to_user_id, boolean QoS, String fingerPrint)
		{
			this(ProtocalFactory.createCommonData(dataContentWidthStr, 
					ClientCoreSDK.getInstance().getCurrentUserId(), to_user_id, QoS, fingerPrint));
		}

		public SendCommonDataAsync(String dataContentWidthStr, int to_user_id)
		{
			this(ProtocalFactory.createCommonData(dataContentWidthStr, 
					ClientCoreSDK.getInstance().getCurrentUserId(), to_user_id));
		}

		public SendCommonDataAsync(Protocal p) {
			if (p == null)
			{
				Log.w(LocalUDPDataSender.TAG, "【IMCORE】无效的参数p==null!");
				return;
			}
			this.p = p;
		}

		protected Integer doInBackground()
		{
			if (this.p != null)
				return Integer.valueOf(LocalUDPDataSender.getInstance().sendCommonData(this.p));
			return Integer.valueOf(-1);
		}

		protected void done()
		{
			int code = -1;
			try
			{
				code = ((Integer)get()).intValue();
			}
			catch (Exception e)
			{
				Log.w(LocalUDPDataSender.TAG, e.getMessage());
			}

			onPostExecute(Integer.valueOf(code));
		}

		protected abstract void onPostExecute(Integer paramInteger);
	}

	public static class SendLoginDataAsync extends SwingWorker<Integer, Object>
	{
		protected String loginName = null;
		protected String loginPsw = null;

		public SendLoginDataAsync(String loginName, String loginPsw)
		{
			this.loginName = loginName;
			this.loginPsw = loginPsw;

			ClientCoreSDK.getInstance().init();
		}

		protected Integer doInBackground()
		{
			int code = LocalUDPDataSender.getInstance().sendLogin(this.loginName, this.loginPsw);
			return Integer.valueOf(code);
		}

		protected void done()
		{
			int code = -1;
			try
			{
				code = ((Integer)get()).intValue();
			}
			catch (Exception e)
			{
				Log.w(LocalUDPDataSender.TAG, e.getMessage());
			}

			onPostExecute(Integer.valueOf(code));
		}

		protected void onPostExecute(Integer code)
		{
			if (code.intValue() == 0)
			{
				LocalUDPDataReciever.getInstance().startup();
			}
			else
			{
				Log.d(LocalUDPDataSender.TAG, "【IMCORE】数据发送失败, 错误码是：" + code + "！");
			}

			fireAfterSendLogin(code.intValue());
		}

		protected void fireAfterSendLogin(int code)
		{
			// default do nothing
		}
	}
}