/*
 * Copyright (C) 2016 即时通讯网(52im.net) The MobileIMSDK Project. 
 * All rights reserved.
 * Project URL:https://github.com/JackJiang2011/MobileIMSDK
 *  
 * 即时通讯网(52im.net) - 即时通讯技术社区! PROPRIETARY/CONFIDENTIAL.
 * Use is subject to license terms.
 * 
 * LocalUDPDataSender.java at 2016-2-20 11:25:50, code by Jack Jiang.
 * You can contact author with jack.jiang@52im.net or jb2011@163.com.
 */
package net.openmob.mobileimsdk.android.core;

import java.net.DatagramSocket;
import java.net.InetAddress;

import net.openmob.mobileimsdk.android.ClientCoreSDK;
import net.openmob.mobileimsdk.android.conf.ConfigEntity;
import net.openmob.mobileimsdk.android.utils.UDPUtils;
import net.openmob.mobileimsdk.server.protocal.CharsetHelper;
import net.openmob.mobileimsdk.server.protocal.ErrorCode;
import net.openmob.mobileimsdk.server.protocal.Protocal;
import net.openmob.mobileimsdk.server.protocal.ProtocalFactory;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

public class LocalUDPDataSender
{
	private static final String TAG = LocalUDPDataSender.class.getSimpleName();
	private static LocalUDPDataSender instance = null;

	private Context context = null;

	public static LocalUDPDataSender getInstance(Context context)
	{
		if (instance == null)
			instance = new LocalUDPDataSender(context);
		return instance;
	}

	private LocalUDPDataSender(Context context)
	{
		this.context = context;
	}

	int sendLogin(String loginName, String loginPsw, String extra)
	{
		byte[] b = ProtocalFactory.createPLoginInfo(loginName, loginPsw, extra).toBytes();
		int code = send(b, b.length);
		// 登陆信息成功发出时就把登陆名存下来
		if(code == 0)
		{
			ClientCoreSDK.getInstance().setCurrentLoginName(loginName);
			ClientCoreSDK.getInstance().setCurrentLoginPsw(loginPsw);
			ClientCoreSDK.getInstance().setCurrentLoginExtra(extra);
		}
		
		return code;
	}

	public int sendLoginout()
	{
		int code = ErrorCode.COMMON_CODE_OK;
		if(ClientCoreSDK.getInstance().isLoginHasInit())
		{
			byte[] b = ProtocalFactory.createPLoginoutInfo(
					ClientCoreSDK.getInstance().getCurrentUserId()
					, ClientCoreSDK.getInstance().getCurrentLoginName()).toBytes();
			code = send(b, b.length);
			// 登出信息成功发出时
			if(code == 0)
			{
//				// 发出退出登陆的消息同时也关闭心跳线程
//				KeepAliveDaemon.getInstance(context).stop();
//				// 重置登陆标识
//				ClientCoreSDK.getInstance().setLoginHasInit(false);
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
		return sendCommonData(
				CharsetHelper.getString(dataContent, dataLen), to_user_id, false, null);
	}

	public int sendCommonData(byte[] dataContent, int dataLen, int to_user_id, boolean QoS, String fingerPrint)
	{
		return sendCommonData(
				CharsetHelper.getString(dataContent, dataLen), to_user_id, QoS, fingerPrint);
	}

	public int sendCommonData(String dataContentWidthStr, int to_user_id)
	{
		return sendCommonData(ProtocalFactory.createCommonData(dataContentWidthStr, 
				ClientCoreSDK.getInstance().getCurrentUserId(), to_user_id));
	}

	public int sendCommonData(String dataContentWidthStr
			, int to_user_id, boolean QoS, String fingerPrint)
	{
		return sendCommonData(ProtocalFactory.createCommonData(dataContentWidthStr, 
				ClientCoreSDK.getInstance().getCurrentUserId(), to_user_id, QoS, fingerPrint));
	}

	public int sendCommonData(Protocal p)
	{
		if(p != null)
		{
			byte[] b = p.toBytes();
			int code = send(b, b.length);
			if(code == 0)
			{
				// 【【C2C或C2S模式下的QoS机制1/4步：将包加入到发送QoS队列中】】
				// 如果需要进行QoS质量保证，则把它放入质量保证队列中供处理(已在存在于列
				// 表中就不用再加了，已经存在则意味当前发送的这个是重传包哦)
				if(p.isQoS() && !QoS4SendDaemon.getInstance(context).exist(p.getFp()))
					QoS4SendDaemon.getInstance(context).put(p);
			}
			return code;
		}
		else
			return ErrorCode.COMMON_INVALID_PROTOCAL;
	}

	private int send(byte[] fullProtocalBytes, int dataLen)
	{
		if(!ClientCoreSDK.getInstance().isInitialed())
			return ErrorCode.ForC.CLIENT_SDK_NO_INITIALED;
		
		if(!ClientCoreSDK.getInstance().isLocalDeviceNetworkOk())
		{
			Log.e(TAG, "【IMCORE】本地网络不能工作，send数据没有继续!");
			return ErrorCode.ForC.LOCAL_NETWORK_NOT_WORKING;
		}
		
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
				
				// 即刻连接上服务端（如果不connect，即使在DataProgram中设置了远程id和地址则服务端MINA也收不到，跟普通的服
				// 务端UDP貌似不太一样，普通UDP时客户端无需先connect可以直接send设置好远程ip和端口的DataPragramPackage）
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

	public static abstract class SendCommonDataAsync extends AsyncTask<Object, Integer, Integer>
	{
		protected Context context = null;
		protected Protocal p = null;

		public SendCommonDataAsync(Context context, byte[] dataContent, int dataLen, int to_user_id)
		{
			this(context, CharsetHelper.getString(dataContent, dataLen), to_user_id);
		}

		public SendCommonDataAsync(Context context, String dataContentWidthStr, int to_user_id, boolean QoS)
		{
			this(context, dataContentWidthStr, to_user_id, QoS, null);
		}

		public SendCommonDataAsync(Context context, String dataContentWidthStr, int to_user_id, boolean QoS, String fingerPrint)
		{
			this(context, 
					ProtocalFactory.createCommonData(dataContentWidthStr, 
							ClientCoreSDK.getInstance().getCurrentUserId(), to_user_id, QoS, fingerPrint));
		}

		public SendCommonDataAsync(Context context, String dataContentWidthStr, int to_user_id)
		{
			this(context, 
					ProtocalFactory.createCommonData(dataContentWidthStr, 
							ClientCoreSDK.getInstance().getCurrentUserId(), to_user_id));
		}

		public SendCommonDataAsync(Context context, Protocal p) {
			if (p == null)
			{
				Log.w(LocalUDPDataSender.TAG, "【IMCORE】无效的参数p==null!");
				return;
			}
			this.context = context;
			this.p = p;
		}

		protected Integer doInBackground(Object[] params)
		{
			if (this.p != null)
				return Integer.valueOf(LocalUDPDataSender.getInstance(this.context).sendCommonData(this.p));
			return Integer.valueOf(-1);
		}

		protected abstract void onPostExecute(Integer paramInteger);
	}

	public static abstract class SendLoginDataAsync extends AsyncTask<Object, Integer, Integer>
	{
		protected Context context = null;
		protected String loginName = null;
		protected String loginPsw = null;
		protected String extra = null;

		public SendLoginDataAsync(Context context, String loginName, String loginPsw)
		{
			this(context, loginName, loginPsw, null);
		}
		
		public SendLoginDataAsync(Context context, String loginName, String loginPsw, String extra)
		{
			this.context = context;
			this.loginName = loginName;
			this.loginPsw = loginPsw;
			this.extra = extra;
		}

		protected Integer doInBackground(Object[] params)
		{
			int code = LocalUDPDataSender.getInstance(this.context)
					.sendLogin(this.loginName, this.loginPsw, this.extra);
			return Integer.valueOf(code);
		}

		protected void onPostExecute(Integer code)
		{
			if (code.intValue() == 0)
			{
				LocalUDPDataReciever.getInstance(this.context).startup();
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