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
package net.openmob.mobileimsdk.android.demo;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;

/**
 * 应用程序启动类：显示闪屏界面并跳转到主界面.
 * 
 * @author liux, Jack Jiang
 * @version 1.0
 * @created 2012-3-21
 */
public class SplashScreenActivity extends Activity
{
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		// FIX: 以下代码是为了解决Android自level 1以来的[安装完成点击“Open”后导致的应用被重复启动]的Bug
		// @see https://code.google.com/p/android/issues/detail?id=52247
		// @see https://code.google.com/p/android/issues/detail?id=2373
		// @see https://code.google.com/p/android/issues/detail?id=26658
		// @see https://github.com/cleverua/android_startup_activity
		// @see http://stackoverflow.com/questions/4341600/how-to-prevent-multiple-instances-of-an-activity-when-it-is-launched-with-differ/
		// @see http://stackoverflow.com/questions/12111943/duplicate-activities-on-the-back-stack-after-initial-installation-of-apk
		// 加了以下代码还得确保Manifast里加上权限申请：“android.permission.GET_TASKS”
		if (!isTaskRoot()) 
		{// FIX START: by Jack Jiang 2013-11-07
		    final Intent intent = getIntent();
		    final String intentAction = intent.getAction();
		    if (intent.hasCategory(Intent.CATEGORY_LAUNCHER) &&
		            intentAction != null && intentAction.equals(Intent.ACTION_MAIN)) {
		        finish();
		    }
		}// FIX END
		
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);  
		final View view = View.inflate(this, R.layout.splash_screen_activity_layout, null);
		setContentView(view);

		// 渐变展示启动屏
		AlphaAnimation aa = new AlphaAnimation(0.3f, 1.0f);
		aa.setDuration(2000);
		view.startAnimation(aa);
		aa.setAnimationListener(new AnimationListener()
		{
			@Override
			public void onAnimationEnd(Animation arg0)
			{
				redirectTo();
			}

			@Override
			public void onAnimationRepeat(Animation animation)
			{
			}

			@Override
			public void onAnimationStart(Animation animation)
			{
			}
		});
	}

	/**
	 * 跳转到...
	 */
	private void redirectTo()
	{
		Intent intent = new Intent(SplashScreenActivity.this, LoginActivity.class);
		startActivity(intent);
		finish();
	}
}