/*
 * Copyright (C) 2023  即时通讯网(52im.net) & Jack Jiang.
 * The MobileIMSDK_TCP (MobileIMSDK v6.4 TCP版) Project. 
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
 * LocalDataSender.java at 2023-9-21 15:32:54, code by Jack Jiang.
 */
package net.x52im.mobileimsdk.java.core;

import io.netty.channel.Channel;

import org.jdesktop.swingworker.SwingWorker;

import net.x52im.mobileimsdk.server.protocal.ProtocalFactory;
import net.x52im.mobileimsdk.server.protocal.ErrorCode;
import net.x52im.mobileimsdk.server.protocal.Protocal;
import net.x52im.mobileimsdk.server.protocal.c.PLoginInfo;
import net.x52im.mobileimsdk.java.ClientCoreSDK;
import net.x52im.mobileimsdk.java.utils.Log;
import net.x52im.mobileimsdk.java.utils.MBObserver;
import net.x52im.mobileimsdk.java.utils.TCPUtils;

public class LocalDataSender {
	private final static String TAG = LocalDataSender.class.getSimpleName();

	private static LocalDataSender instance = null;

	public static LocalDataSender getInstance() {
		if (instance == null) {
			synchronized (LocalDataSender.class) {
				if (instance == null) {
					instance = new LocalDataSender();
				}
			}
		}
		return instance;
	}

	private LocalDataSender() {
	}

	int sendLogin(final PLoginInfo loginInfo) {
		int codeForCheck = this.checkBeforeSend();
		if (codeForCheck != ErrorCode.COMMON_CODE_OK)
			return codeForCheck;

		if (!LocalSocketProvider.getInstance().isLocalSocketReady()) {
			if (ClientCoreSDK.DEBUG)
				Log.d(TAG, "【IMCORE-TCP】发送登陆指令时，socket连接未就绪，首先开始尝试发起连接（登陆指令将在连接成功后的回调中自动发出）。。。。");

			MBObserver connectionDoneObserver = new MBObserver() {
				public void update(boolean sucess, Object extraObj) {
					if (sucess)
						sendLoginImpl(loginInfo);
					else
						Log.w(TAG, "【IMCORE-TCP】[来自Netty的连接结果回调观察者通知]socket连接失败，本次登陆信息未成功发出！");
				}
			};
			LocalSocketProvider.getInstance().setConnectionDoneObserver(connectionDoneObserver);

			return LocalSocketProvider.getInstance().resetLocalSocket() != null ? ErrorCode.COMMON_CODE_OK : ErrorCode.ForC.BAD_CONNECT_TO_SERVER;
		} else {
			return this.sendLoginImpl(loginInfo);
		}
	}

	// 不推荐直接调用本方法实现“登陆”流程，请使用SendLoginAsync（此异步线程中包含发送登陆包之外的处理和逻辑）
	int sendLoginImpl(PLoginInfo loginInfo) {
		byte[] b = ProtocalFactory.createPLoginInfo(loginInfo).toBytes();
		int code = send(b, b.length);
		if (code == 0) {
			ClientCoreSDK.getInstance().setCurrentLoginInfo(loginInfo);
		}

		return code;
	}

	public int sendLoginout() {
		int code = ErrorCode.COMMON_CODE_OK;
		if (ClientCoreSDK.getInstance().isLoginHasInit()) {
			byte[] b = ProtocalFactory.createPLoginoutInfo(ClientCoreSDK.getInstance().getCurrentLoginInfo().getLoginUserId()).toBytes();
			code = send(b, b.length);
			if (code == 0) {
				// do nothing
			}
		}

		ClientCoreSDK.getInstance().release();

		return code;
	}

	int sendKeepAlive() {
		byte[] b = ProtocalFactory.createPKeepAlive(ClientCoreSDK.getInstance().getCurrentLoginInfo().getLoginUserId()).toBytes();
		return send(b, b.length);
	}

	public int sendCommonData(String dataContentWidthStr, String to_user_id) {
		return sendCommonData(dataContentWidthStr, to_user_id, -1);
	}

	public int sendCommonData(String dataContentWidthStr, String to_user_id, int typeu) {
		return sendCommonData(dataContentWidthStr, to_user_id, null, typeu);
	}

	public int sendCommonData(String dataContentWidthStr, String to_user_id,String fingerPrint, int typeu) {
		return sendCommonData(dataContentWidthStr, to_user_id, true, fingerPrint, typeu);
	}

	public int sendCommonData(String dataContentWidthStr, String to_user_id,boolean QoS, String fingerPrint, int typeu) {
		return sendCommonData(ProtocalFactory.createCommonData(dataContentWidthStr, ClientCoreSDK.getInstance().getCurrentLoginInfo().getLoginUserId(), to_user_id, QoS, fingerPrint, typeu));
	}

	public int sendCommonData(Protocal p) {
		if (p != null) {
			byte[] b = p.toBytes();
			int code = send(b, b.length);
			if (code == 0) {
				if (p.isQoS() && !QoS4SendDaemon.getInstance().exist(p.getFp()))
					QoS4SendDaemon.getInstance().put(p);
			}
			return code;
		} else
			return ErrorCode.COMMON_INVALID_PROTOCAL;
	}

	public int send(byte[] fullProtocalBytes, int dataLen) {
		int codeForCheck = this.checkBeforeSend();
		if (codeForCheck != ErrorCode.COMMON_CODE_OK)
			return codeForCheck;
		//
		// if(!ClientCoreSDK.getInstance().isInitialed())
		// return ErrorCode.ForC.CLIENT_SDK_NO_INITIALED;

		Channel ds = LocalSocketProvider.getInstance().getLocalSocket();
		if (ds != null && ds.isActive()) {
			return TCPUtils.send(ds, fullProtocalBytes, dataLen) ? ErrorCode.COMMON_CODE_OK: ErrorCode.COMMON_DATA_SEND_FAILD;
		} else {
			Log.d(TAG, "【IMCORE-TCP】scocket未连接，无法发送，本条将被忽略（dataLen=" + dataLen + "）!");
			return ErrorCode.COMMON_CODE_OK;
		}
	}

	private int checkBeforeSend() {
		if (!ClientCoreSDK.getInstance().isInitialed())
			return ErrorCode.ForC.CLIENT_SDK_NO_INITIALED;
		return ErrorCode.COMMON_CODE_OK;
	}

	// ------------------------------------------------------------------------------------------
	// utilities class

	public static abstract class SendCommonDataAsync extends SwingWorker<Integer, Object> {
		protected Protocal p = null;

		public SendCommonDataAsync(String dataContentWidthStr, String to_user_id) {
			this(dataContentWidthStr, to_user_id, null, -1);
		}

		public SendCommonDataAsync(String dataContentWidthStr, String to_user_id, int typeu) {
			this(dataContentWidthStr, to_user_id, null, typeu);
		}

		public SendCommonDataAsync(String dataContentWidthStr, String to_user_id, String fingerPrint, int typeu) {
			this(ProtocalFactory.createCommonData(dataContentWidthStr, ClientCoreSDK.getInstance().getCurrentLoginInfo().getLoginUserId(), to_user_id, true, fingerPrint, typeu));
		}

		public SendCommonDataAsync(Protocal p) {
			if (p == null) {
				Log.w(TAG, "【IMCORE-TCP】无效的参数p==null!");
				return;
			}
			this.p = p;
		}

		@Override
		protected Integer doInBackground() {
			if (p != null)
				return LocalDataSender.getInstance().sendCommonData(p);
			return -1;
		}

		@Override
		protected void done() {
			int code = -1;
			try {
				code = get();
			} catch (Exception e) {
				Log.w(TAG, e.getMessage());
			}

			onPostExecute(code);
		}

		protected abstract void onPostExecute(Integer code);
	}

	public static class SendLoginDataAsync extends SwingWorker<Integer, Object> {
		
		protected PLoginInfo loginInfo = null;

		public SendLoginDataAsync(PLoginInfo loginInfo)
		{
			this.loginInfo = loginInfo;
			ClientCoreSDK.getInstance().init();
		}

		@Override
		protected Integer doInBackground() {
			int code = LocalDataSender.getInstance().sendLogin(this.loginInfo);
			return code;
		}

		@Override
		protected void done() {
			int code = -1;
			try {
				code = get();
			} catch (Exception e) {
				Log.w(TAG, e.getMessage());
			}

			onPostExecute(code);
		}

		protected void onPostExecute(Integer code) {
			if (code == 0) {
				// LocalUDPDataReciever.getInstance().startup();
			} else {
				Log.d(TAG, "【IMCORE-TCP】数据发送失败, 错误码是：" + code + "！");
			}

			fireAfterSendLogin(code);
		}

		protected void fireAfterSendLogin(int code) {
			// default do nothing
		}
	}
}
