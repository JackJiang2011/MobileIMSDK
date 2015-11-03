/*
 * Copyright (C) 2015 Jack Jiang The MobileIMSDK Project. 
 * All rights reserved.
 * Project URL:https://github.com/JackJiang2011/MobileIMSDK
 *  
 * openmob.net PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 * 
 * ChatBaseEventImpl.java at 2015-10-7 22:01:48, code by Jack Jiang.
 * You can contact author with jack.jiang@openmob.net or jb2011@163.com.
 */
package net.openmob.mobileimsdk.android.demo;

import net.openmob.mobileimsdk.android.event.ChatBaseEvent;
import android.util.Log;

public class ChatBaseEventImpl implements ChatBaseEvent
{
	private final static String TAG = ChatBaseEventImpl.class.getSimpleName();
	
	private DemoMain ____temp = null; 
	
	@Override
	public void onLoginMessage(int dwUserId, int dwErrorCode)
	{
		if (dwErrorCode == 0) 
		{
//			Log.i(TAG, "【DEBUG_UI】登录成功，当前分配的user_id=！"+dwUserId);
			
			// TODO 以下代码仅用于DEMO哦
			if(this.____temp != null)
			{
				this.____temp.setMyid(dwUserId);
				this.____temp.showIMInfo_green("登录成功,id="+dwUserId);
			}
		}
		else 
		{
			Log.e(TAG, "【DEBUG_UI】登录失败，错误代码：" + dwErrorCode);
			this.____temp.showIMInfo_red("登录失败,code="+dwErrorCode);
		}
	}

	@Override
	public void onLinkCloseMessage(int dwErrorCode)
	{
		Log.e(TAG, "【DEBUG_UI】网络连接出错关闭了，error：" + dwErrorCode);
		
		// TODO 以下代码仅用于DEMO哦
		if(this.____temp != null)
		{
			this.____temp.setMyid(-1);
			this.____temp.showIMInfo_red("服务器连接已断开,error="+dwErrorCode);
		}
	}
	
	public ChatBaseEventImpl set____temp(DemoMain ____temp)
	{
		this.____temp = ____temp;
		return this;
	}
	public DemoMain get____temp()
	{
		return ____temp;
	}

}
