package net.x52im.mobileimsdk.android.demo.permission2;

import android.content.Context;

import net.x52im.mobileimsdk.android.demo.R;

import java.util.List;

import androidx.annotation.NonNull;

/**
 * *** 本类的实现参考自全功能IM产品RainbowChat：http://www.52im.net/thread-19-1-1.html ***
 *
 * Android 6.0及以上版本的动态框架权限描述转换器。
 *
 * @author Android 轮子哥
 */
public final class PermissionDescriptionConvert {

    /**
     * 获取权限描述
     */
   public static String getPermissionDescription(Context context, List<String> permissions) {
       StringBuilder stringBuilder = new StringBuilder();
       List<String> permissionNames = PermissionNameConvert.permissionsToNames(context, permissions);
       for (String permissionName : permissionNames) {
           stringBuilder.append(permissionName)
               .append(context.getString(R.string.common_permission_colon))
               .append(permissionsToDescription(context, permissionName))
               .append("\n");
       }
       return stringBuilder.toString().trim();
   }

   /**
    * 将权限名称列表转换成对应权限描述
    */
   @NonNull
   public static String permissionsToDescription(Context context, String permissionName) {
       String ret = "用于"+permissionName+"业务";

       // 请根据权限名称转换成对应权限说明
       if(permissionName != null){
           if(permissionName.equals(context.getString(R.string.common_permission_camera))) {
               ret = "用于图片消息、视频聊天、修改头像";
           } else if(permissionName.equals(context.getString(R.string.common_permission_image_and_video))) {
               ret = "用于图片/短视频消息、视频聊天";
           } else if(permissionName.equals(context.getString(R.string.common_permission_music_and_audio))) {
               ret = "用于语音消息、语音聊天";
           } else if(permissionName.equals(context.getString(R.string.common_permission_post_notifications))) {
               ret = "用于接收后台聊天消息推送";
           } else if(permissionName.equals(context.getString(R.string.common_permission_storage))) {
               ret = "用于文件消息";
           }
       }

       return ret;
   }
}