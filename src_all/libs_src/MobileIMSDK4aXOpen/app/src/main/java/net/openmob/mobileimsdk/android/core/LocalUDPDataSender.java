/*
 * Copyright (C) 2020  即时通讯网(52im.net) & Jack Jiang.
 * The MobileIMSDK_X_netty (MobileIMSDK v4.x) Project.
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
 * Created at 2020-4-14 23:22:29, code by Jack Jiang.
 */
package net.openmob.mobileimsdk.android.core;

import java.net.DatagramSocket;
import java.net.InetAddress;

import net.openmob.mobileimsdk.android.ClientCoreSDK;
import net.openmob.mobileimsdk.android.conf.ConfigEntity;
import net.openmob.mobileimsdk.android.utils.UDPUtils;
import net.openmob.mobileimsdk.server.protocal.ErrorCode;
import net.openmob.mobileimsdk.server.protocal.Protocal;
import net.openmob.mobileimsdk.server.protocal.ProtocalFactory;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

public class LocalUDPDataSender
{
	private final static String TAG = LocalUDPDataSender.class.getSimpleName();
	private static LocalUDPDataSender instance = null;
	
	private Context context = null;
	
	public static LocalUDPDataSender getInstance(Context context)
	{
		if(instance == null)
			instance = new LocalUDPDataSender(context);
		return instance;
	}
	
	private LocalUDPDataSender(Context context)
	{
		this.context = context;
	}
	
	int sendLogin(String loginUserId, String loginToken, String extra)
	{
		byte[] b = ProtocalFactory.createPLoginInfo(loginUserId, loginToken, extra).toBytes();
		int code = send(b, b.length);
		if(code == 0)
		{
			ClientCoreSDK.getInstance().setCurrentLoginUserId(loginUserId);
			ClientCoreSDK.getInstance().setCurrentLoginToken(loginToken);
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
					ClientCoreSDK.getInstance().getCurrentLoginUserId()).toBytes();
			code = send(b, b.length);
			if(code == 0)
			{
				// do nothing
			}
		}
		ClientCoreSDK.getInstance().release();
		return code;
	}
	
	int sendKeepAlive()
	{
		byte[] b = ProtocalFactory.createPKeepAlive(
				ClientCoreSDK.getInstance().getCurrentLoginUserId()).toBytes();
		return send(b, b.length);
	}
	
	public int sendCommonData(String dataContentWidthStr, String to_user_id)
	{
		return sendCommonData(dataContentWidthStr, to_user_id, -1);
	}
	public int sendCommonData(String dataContentWidthStr, String to_user_id, int typeu)
	{
		return sendCommonData(dataContentWidthStr, to_user_id, null, typeu);
	}
	public int sendCommonData(String dataContentWidthStr, String to_user_id
			, String fingerPrint, int typeu)
	{
		return sendCommonData(dataContentWidthStr, to_user_id, true, fingerPrint, typeu);
	}
	public int sendCommonData(String dataContentWidthStr, String to_user_id
			, boolean QoS, String fingerPrint, int typeu)
	{
		return sendCommonData(ProtocalFactory.createCommonData(dataContentWidthStr
				, ClientCoreSDK.getInstance().getCurrentLoginUserId(), to_user_id, QoS, fingerPrint, typeu));
	}
	
	public int sendCommonData(Protocal p)
	{
		if(p != null)
		{
			byte[] b = p.toBytes();
			int code = send(b, b.length);
			if(code == 0)
			{
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
	
	public static abstract class SendCommonDataAsync extends AsyncTask<Object, Integer, Integer>
	{
		protected Context context = null;
		protected Protocal p = null;
		
		public SendCommonDataAsync(Context context, String dataContentWidthStr, String to_user_id)
		{
			this(context, dataContentWidthStr, to_user_id, null, -1);
		}

		public SendCommonDataAsync(Context context, String dataContentWidthStr, String to_user_id
				, int typeu)
		{
			this(context, dataContentWidthStr, to_user_id, null, typeu);
		}
		
		public SendCommonDataAsync(Context context, String dataContentWidthStr, String to_user_id
				, String fingerPrint, int typeu)
		{
			this(context, ProtocalFactory.createCommonData(dataContentWidthStr
					, ClientCoreSDK.getInstance().getCurrentLoginUserId(), to_user_id
					, true, fingerPrint, typeu));
		}

		public SendCommonDataAsync(Context context, Protocal p)
		{
			if(p == null)
			{
				Log.w(TAG, "【IMCORE】无效的参数p==null!");
				return;
			}
			this.context = context;
			this.p = p;
		}

		@Override
		protected Integer doInBackground(Object... params)
		{
			if(p != null)
				return LocalUDPDataSender.getInstance(context).sendCommonData(p);//dataContentWidthStr, to_user_id);
			return -1;
		}

		@Override
		protected abstract void onPostExecute(Integer code);
	}
	
	public static abstract class SendLoginDataAsync extends AsyncTask<Object, Integer, Integer>
	{
		protected Context context = null;
		protected String loginUserId = null;
		protected String loginToken = null;
		protected String extra = null;

		public SendLoginDataAsync(Context context
				, String loginUserId, String loginToken)
		{
			this(context, loginUserId, loginToken, null);
		}
		public SendLoginDataAsync(Context context
				, String loginUserId, String loginToken, String extra)
		{
			this.context = context;
			this.loginUserId = loginUserId;
			this.loginToken = loginToken;
			this.extra = extra;
			
//			ClientCoreSDK.getInstance().init(context);
		}

		@Override
		protected Integer doInBackground(Object... params)
		{
			int code = LocalUDPDataSender.getInstance(context).sendLogin(loginUserId, loginToken, this.extra);
			return code;
		}

		@Override
		protected void onPostExecute(Integer code)
		{
			if(code == 0)
			{
				LocalUDPDataReciever.getInstance(context).startup();
			}
			else
			{
				Log.d(TAG, "【IMCORE】数据发送失败, 错误码是："+code+"！");
			}
			
			fireAfterSendLogin(code);
		}
		
		protected void fireAfterSendLogin(int code)
		{
			// default do nothing
		}
	}
}
