package net.x52im.mobileimsdk.android.demo.utils;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.util.Log;

import java.text.MessageFormat;

import androidx.core.app.NotificationCompat;

/**
 * 本类代码参考自全功能IM产品RainbowChat：http://www.52im.net/thread-19-1-1.html
 *
 * @author JackJiang
 * @since 6.5
 */
public class ToolKits {

    private final static String TAG = ToolKits.class.getSimpleName();

    /**
     * 资源字符串国际化。
     *
     * @param c 上下文
     * @param messageFormatPatternResId 资源字符串，字符串内容通常形如："视频会话发送失败 t0 {0}({1},{2})"
     * @param arguments 对应于messageFormatPatternResId中的占位符
     * @return 返回格式化后的字符串
     */
    public static String i18n(Context c, int messageFormatPatternResId, Object... arguments){
        String messageFormatPattern = c.getString(messageFormatPatternResId);
        return MessageFormat.format(messageFormatPattern, arguments);
    }

    /**
     * 获取App的名称。
     *
     * @param context 上下文
     *
     * @return 名称
     */
    public static String getAppName(Context context) {
        PackageManager pm = context.getPackageManager();
        //获取包信息
        try {
            PackageInfo packageInfo = pm.getPackageInfo(context.getPackageName(), 0);
            //获取应用 信息
            ApplicationInfo applicationInfo = packageInfo.applicationInfo;
            //获取albelRes
            int labelRes = applicationInfo.labelRes;
            //返回App的名称
            return context.getResources().getString(labelRes);
        } catch (PackageManager.NameNotFoundException e) {
            Log.w(TAG, e);
        }

        return null;
    }

    /**
     * 返回NotificationManager对象。
     *
     * @param context 上下文
     * @return NotificationManager对象引用
     */
    public static NotificationManager getNotificationManager(Context context) {
        NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        // 以下代码，解决在Android 8及以上代码中，无法正常显示Notification或报"Bad notification for startForeground"等问题
        NotificationChannel notificationChannel = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            notificationChannel = new NotificationChannel("default_1", "Default Channel", NotificationManager.IMPORTANCE_HIGH);
            notificationChannel.enableLights(true);

            notificationChannel.setLightColor(Color.RED);
            notificationChannel.setShowBadge(true);
            // 锁屏显示通知
            notificationChannel.setLockscreenVisibility(Notification.VISIBILITY_PUBLIC);

            manager.createNotificationChannel(notificationChannel);
        }

        return manager;
    }

    /**
     * 兼容地方法创建Notification方法。
     *
     * @param context 上下文
     * @param pendingIntent 点击通知要打开的Activity的Intent
     * @param title 通知标题
     * @param text 通知内容
     * @param iconId 通知图标
     * @return 新的Notification对象
     */
    public static Notification createNotification(Context context, PendingIntent pendingIntent, String title, String text, int iconId) {
        // 创建一个Notification Builder，使用NotificationCompat可以更好的兼容Android各系统版本，
        // 有关Android Notitication的兼容性、详细设置等，参见：https://www.cnblogs.com/travellife/p/Android-Notification-xiang-jie.html
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, "default_1")
                .setContentTitle(title)
                .setContentText(text)
                .setContentIntent(pendingIntent)
                // 设置显示在手机最上边的状态栏的图标
                .setSmallIcon(iconId)
                // 设置为重要通知（否则在小米这类用机里，会默认放到"不重要的通知"，太恶心）
                .setPriority(NotificationCompat.PRIORITY_MAX)
                // 根据类型设置通知的Catlog（便于Android系统进行分类管理）
                .setCategory(NotificationCompat.CATEGORY_MESSAGE);

        // 通知的显示等级（Android5.0开始，通知可以显示在锁屏上）：
        // - VISIBILITY_PRIVATE : 显示基本信息，如通知的图标，但隐藏通知的全部内容
        // - VISIBILITY_PUBLIC : 显示通知的全部内容
        // - VISIBILITY_SECRET : 不显示任何内容，包括图标
        builder.setVisibility(NotificationCompat.VISIBILITY_PUBLIC);

        // 创建一个Notification
        Notification notification = builder.build();

        return notification;
    }

    /**
     * 显示一个通用的信息Notification（比如聊天消息通知）。
     *
     * @param notification_uniqe_ident_id 通知id，全局准一即可
     * @param context 上下文
     * @param destActivityIntent 点击通知要打开的Activity的Intent
     * @param icon_res_id 通知图标
     * @param tickerText 通知内容折叠显示时的要显示的内容
     * @param infoContentTitle 通知内容的标题
     * @param infoContentText 通知内容
     * @param cancelIfExist true表示如果该notification_uniqe_ident_id标识的Notivication存在的话
     * 		直接更新，否则先退出(清除)原先的Notivication再发一个新的。如果不退出原有的Notivication
     * 		则收到同一个Notive之后就不会有响铃等提醒（只是更新通知时间而已）
     * @param useDefaultNotificationSound 是否使用系统提示音
     * @param silent 是否静辜
     * @return NotificationManager对象引用
     */
    public static NotificationManager addNotificaction(
            int notification_uniqe_ident_id
            , Context context
            , Intent destActivityIntent
            , int icon_res_id
            , String tickerText, String infoContentTitle, String infoContentText
            , boolean cancelIfExist
            , boolean useDefaultNotificationSound, boolean silent)
    {
        NotificationManager manager = ToolKits.getNotificationManager(context);

        // 只有在声音模式打开时才会真正的给个系统提示（否则会有系统震动、声音等），否则无法实现真正的静音哦！
        if(!silent)
        {
            if(cancelIfExist)
                manager.cancel(notification_uniqe_ident_id);

            PendingIntent pendingIntent = PendingIntent.getActivity(
                    context, 0, destActivityIntent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);//PendingIntent.FLAG_ONE_SHOT);

            // 创建一个Notification
            Notification notification = ToolKits.createNotification(context, pendingIntent, infoContentTitle, infoContentText, icon_res_id);

            // 当当前的notification被放到状态栏上的时候，提示内容
            notification.tickerText = tickerText;
            // 点击后自动退出
            notification.flags = Notification.FLAG_AUTO_CANCEL;

            if(useDefaultNotificationSound)
                // 添加声音提示
                notification.defaults = Notification.DEFAULT_ALL;


            // 各种属性意义，请见：https://www.jianshu.com/p/a87e95d30164
            notification.defaults |= Notification.DEFAULT_VIBRATE; // 添加默认震动提醒
            notification.defaults |= Notification.DEFAULT_LIGHTS;  // 默认三色灯提醒

            manager.notify(notification_uniqe_ident_id, notification);
        }

        return manager;
    }
}
