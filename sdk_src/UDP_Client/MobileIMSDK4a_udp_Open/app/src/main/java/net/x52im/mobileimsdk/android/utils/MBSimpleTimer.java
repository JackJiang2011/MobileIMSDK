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
 * MBSimpleTimer.java at 2022-7-28 17:23:39, code by Jack Jiang.
 */
package net.x52im.mobileimsdk.android.utils;

import android.os.Handler;
import android.util.Log;

public abstract class MBSimpleTimer {

    private Handler handler = null;
    private Runnable runnable = null;
    private int interval = -1;

    public MBSimpleTimer(int interval){
        this.interval = interval;
    }

    public void init() {
        handler = new Handler();
        runnable = () -> {
            try {
                doAction();
            } catch (Exception e) {
                Log.w(MBSimpleTimer.class.getSimpleName(), e);
            }

            handler.postDelayed(runnable, interval);
        };
    }

    protected abstract void doAction();

    public void start(boolean immediately) {
        stop();
        onStart();
        handler.postDelayed(runnable, immediately ? 0 : interval);
    }

    protected void onStart(){
        // default do nothing
    }

    public void stop() {
        handler.removeCallbacks(runnable);
        onStop();
    }

    protected void onStop(){
        // default do nothing
    }
}