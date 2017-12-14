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
 * ChatTransDataEventImpl.java at 2017-5-1 21:08:44, code by Jack Jiang.
 * You can contact author with jack.jiang@52im.net or jb2011@163.com.
 */
package net.openmob.mobileimsdk.android.demo.event;

import net.openmob.mobileimsdk.android.demo.MainActivity;
import net.openmob.mobileimsdk.android.event.ChatTransDataEvent;
import net.openmob.mobileimsdk.server.protocal.ErrorCode;
import android.util.Log;
import android.widget.Toast;

/**
 * 与IM服务器的数据交互事件在此ChatTransDataEvent子类中实现即可。
 * 
 * @author Jack Jiang, 20170501
 * @version.1.1
 */
public class ChatTransDataEventImpl implements ChatTransDataEvent
{
	private final static String TAG = ChatTransDataEventImpl.class.getSimpleName();
	
	private MainActivity mainGUI = null; 
	
	@Override
	public void onTransBuffer(String fingerPrintOfProtocal, String userid, String dataContent, int typeu)
	{
		Log.d(TAG, "【DEBUG_UI】[typeu="+typeu+"]收到来自用户"+userid+"的消息:"+dataContent);
		
		if(mainGUI != null)
		{
			Toast.makeText(mainGUI, userid+"说："+dataContent, Toast.LENGTH_SHORT).show();
			this.mainGUI.showIMInfo_black(userid+"说："+dataContent);
		}
	}
	
	public ChatTransDataEventImpl setMainGUI(MainActivity mainGUI)
	{
		this.mainGUI = mainGUI;
		return this;
	}

	@Override
	public void onErrorResponse(int errorCode, String errorMsg)
	{
		Log.d(TAG, "【DEBUG_UI】收到服务端错误消息，errorCode="+errorCode+", errorMsg="+errorMsg);
		
		if(errorCode ==  ErrorCode.ForS.RESPONSE_FOR_UNLOGIN)
			this.mainGUI.showIMInfo_brightred("服务端会话已失效，自动登陆/重连将启动! ("+errorCode+")");
		else
			this.mainGUI.showIMInfo_red("Server反馈错误码："+errorCode+",errorMsg="+errorMsg);
	}
}
