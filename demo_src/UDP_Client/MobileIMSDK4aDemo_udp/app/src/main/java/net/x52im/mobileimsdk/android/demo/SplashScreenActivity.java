/*
 * Copyright (C) 2022  即时通讯网(52im.net) & Jack Jiang.
 * The MobileIMSDK_UDP (MobileIMSDK v6.x UDP版) Project. 
 * All rights reserved.
 * 
 * > Github地址：https://github.com/JackJiang2011/MobileIMSDK
 * > 文档地址：  http://www.52im.net/forum-89-1.html
 * > 技术社区：  http://www.52im.net/
 * > 技术交流群：185926912 (http://www.52im.net/topic-qqgroup.html)
 * > 作者公众号：“即时通讯技术圈】”，欢迎关注！
 * > 联系作者：  http://www.52im.net/thread-2792-1-1.html
 *  
 * "即时通讯网(52im.net) - 即时通讯开发者社区!" 推荐开源工程。
 * 
 * SplashScreenActivity.java at 2022-7-28 17:21:45, code by Jack Jiang.
 */
package net.x52im.mobileimsdk.android.demo;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;

import net.x52im.mobileimsdk.android.demo.R;

/**
 * 应用程序启动类：显示闪屏界面并跳转到主界面.
 * 
 * @author liux, Jack Jiang
 * @version 1.0
 */
public class SplashScreenActivity extends Activity {
	@Override
	public void onCreate(Bundle savedInstanceState) {
		// FIX: 以下代码是为了解决Android自level 1以来的[安装完成点击“Open”后导致的应用被重复启动]的Bug
		// @see https://code.google.com/p/android/issues/detail?id=52247
		// @see https://code.google.com/p/android/issues/detail?id=2373
		// @see https://code.google.com/p/android/issues/detail?id=26658
		// @see https://github.com/cleverua/android_startup_activity
		// @see http://stackoverflow.com/questions/4341600/how-to-prevent-multiple-instances-of-an-activity-when-it-is-launched-with-differ/
		// @see http://stackoverflow.com/questions/12111943/duplicate-activities-on-the-back-stack-after-initial-installation-of-apk
		// 加了以下代码还得确保Manifast里加上权限申请：“android.permission.GET_TASKS”
		if (!isTaskRoot()) {
		    final Intent intent = getIntent();
		    final String intentAction = intent.getAction();
		    if (intent.hasCategory(Intent.CATEGORY_LAUNCHER) && intentAction != null && intentAction.equals(Intent.ACTION_MAIN)) {
		        finish();
		    }
		}// FIX END

		// 沉浸式效果（系统状态栏将变的透明，界面从状态栏下方透过）
		setStatusBarTranslucent(this, true);

		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);  
		final View view = View.inflate(this, R.layout.splash_screen_activity_layout, null);
		setContentView(view);

		// 渐变展示启动屏
		AlphaAnimation aa = new AlphaAnimation(0.3f, 1.0f);
		aa.setDuration(2000);
		view.startAnimation(aa);
		aa.setAnimationListener(new AnimationListener() {
			@Override
			public void onAnimationEnd(Animation arg0) {
				redirectTo();
			}

			@Override
			public void onAnimationRepeat(Animation animation) {
			}

			@Override
			public void onAnimationStart(Animation animation) {
			}
		});
	}

	/**
	 * 跳转到...
	 */
	private void redirectTo() {
		Intent intent = new Intent(SplashScreenActivity.this, LoginActivity.class);
		startActivity(intent);
		finish();
	}

	/**
	 * 调用此方法，可以实现承浸式界面效果（比如用于显示图片、app启动时的闪屏界面
	 * 等，尤其刘海屏下的视觉效果会很棒）。
	 * <p>
	 * 注：承浸式界面效果由Android系统在android 4.4版及以上系统中提供。
	 *
	 * @param ac 要设置的界面
	 * @param translucent true 实现承浸式界面效果
	 */
	@TargetApi(Build.VERSION_CODES.KITKAT)
	public static void setStatusBarTranslucent(Activity ac, boolean translucent) {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
			ac.getWindow().setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS, WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
		}
	}
}