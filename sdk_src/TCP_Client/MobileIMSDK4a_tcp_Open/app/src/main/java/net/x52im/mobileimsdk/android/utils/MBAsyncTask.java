/*
 * Copyright (C) 2021  即时通讯网(52im.net) & Jack Jiang.
 * The MobileIMSDK_TCP (MobileIMSDK v6.x TCP版) Project. 
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
 * MBAsyncTask.java at 2021-7-1 15:08:17, code by Jack Jiang.
 */
package net.x52im.mobileimsdk.android.utils;

public abstract class MBAsyncTask {
    public void execute(final Object... params) {
        MBThreadPoolExecutor.runInBackground(() -> {
            final int code = doInBackground(params);
            MBThreadPoolExecutor.runOnMainThread(() -> { onPostExecute(code); });
        });
    }

    protected abstract Integer doInBackground(Object... params);

    protected void onPostExecute(Integer code) {
    }
}
