package net.x52im.mobileimsdk.android.demo.permission2;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.hjq.permissions.OnPermissionCallback;
import com.hjq.permissions.Permission;
import com.hjq.permissions.XXPermissions;

import net.x52im.mobileimsdk.android.demo.R;

import java.text.MessageFormat;
import java.util.Arrays;
import java.util.List;
import java.util.Observer;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;

/**
 * *** 本类的实现参考自全功能IM产品RainbowChat：http://www.52im.net/thread-19-1-1.html ***
 *
 * 用于支持最新Android 14 及以上版本的动态权限管理器。
 *
 * @author Jack Jiang
 * @since 7.2
 */
public class PermissionManager
{
    private final static String TAG = PermissionManager.class.getSimpleName();

    /**
     * 请求 POST_NOTIFICATIONS 权限。
     * 注：Android 13开始，新增了通知权限，详情参见：https://mp.weixin.qq.com/s/BQ57pzaffB8vPyGuJtc37w 。
     *
     * @param activity 调用时的主类
     * @param obsOnGranted 已同意权限或已拥有此权限时的观察者，不可为null
     * @param obsOnDenied 已拒绝权限或无此权限时的观察者，可为null（为null时将使用默认观察者，主要用于信息提示）
     * @since 9.0
     */
    public static void requestPermission_POST_NOTIFICATIONS(final Context activity
            , final Observer obsOnGranted, Observer obsOnDenied)
    {
        // 本次要动态申请的权限
        final String[] pemmisions = {Permission.POST_NOTIFICATIONS};

        // 如果拒绝或没有获取到权限时的观察者没有设置，则给个默认的（主要用于信息提示）
        if(obsOnDenied == null)
        {
            obsOnDenied = (o, permissionNamesObj) -> {
                final List<String> permissionNames = (List<String>)permissionNamesObj;
                String message = MessageFormat.format(activity.getResources().getString(R.string.rb_permission_fail_to_cancel), permissionNames);
                Log.w(TAG, "[动态权限onDenied]"+message);
                Toast.makeText(activity, message, Toast.LENGTH_LONG).show();
//                WidgetUtils.showToastLong(activity, message, WidgetUtils.ToastType.WARN);
            };
        }

        // 开始动态权限申请（用于targetSDKVersion>=23，即Android6.0及以上系统时）
        PermissionManager.requestPermission(activity
                // 权限获取成功或已经取得此权限时
                , obsOnGranted
                // 用户拒绝或权限获取失败时
                , obsOnDenied
                , false
                , pemmisions);
    }

    /**
     * 请求权限。
     *
     * @param activity 调用时的主类
     * @param obsOnGranted 已同意权限或已拥有此权限时的观察者，不可为null
     * @param obsOnDenied 已拒绝权限或无此权限时的观察者，可为null（为null时将使用默认观察者，主要用于信息提示）
     * @param showSettingIfNever true表示当正在获取已被用户永久拒绝的权限时自动跳到系统权限设置界面，否则不跳转
     * @param permissions 要请求的权限，数组形式
     * @see com.hjq.permissions.Permission
     */
    public static void requestPermission(final Context activity, final Observer obsOnGranted
            , final Observer obsOnDenied, final boolean showSettingIfNever, String... permissions) {
        final List<String> permissionAllNames = PermissionNameConvert.permissionsToNames(activity, Arrays.asList(permissions));

        Log.i(TAG, "[动态权限requestPermission]权限"+ Arrays.toString(permissionAllNames.toArray())+"正在请求中。。。");

        XXPermissions.with(activity)
                // 申请权限
                .permission(permissions)
                .interceptor(new PermissionInterceptor())
                .request(new OnPermissionCallback() {

                    /**
                     * 有权限被同意授予时回调
                     *
                     * @param permissions 请求成功的权限组
                     * @param allGranted 是否全部授予了
                     */
                    @Override
                    public void onGranted(@NonNull List<String> permissions, boolean allGranted) {
                        if (!allGranted) {
                            Log.i(TAG, "[动态权限onGranted]【部分】权限"+ Arrays.toString(permissions.toArray())+"获取成功，本次将忽略且不通知应用层的obsOnGranted！");
                            return;
                        }

                        Log.i(TAG, "[动态权限onGranted]【全部】权限"+ Arrays.toString(permissions.toArray())+"获取成功。");
                        final List<String> permissionNames = PermissionNameConvert.permissionsToNames(activity, permissions);//Permission.transformText(activity, permissions);
                        if (obsOnGranted != null)
                            obsOnGranted.update(null, permissionNames);
                    }

                    /**
                     * 有权限被拒绝授予时回调
                     *
                     * @param permissions 请求失败的权限组
                     * @param doNotAskAgain  是否勾选了不再询问选项
                     */
                    @Override
                    public void onDenied(@NonNull List<String> permissions, boolean doNotAskAgain) {
                        Log.e(TAG, "[动态权限onDenied]权限"+Arrays.toString(permissions.toArray())+"获取失败！");

                        List<String> permissionNames = PermissionNameConvert.permissionsToNames(activity, permissions);//Permission.transformText(activity, permissions);
                        if(obsOnDenied != null)
                            obsOnDenied.update(null, permissionNames);

                        // 显示系统权限设置
                        if(doNotAskAgain && showSettingIfNever){
                            if(activity instanceof Activity) {
                                showSettingDialog((Activity)activity, permissions);
                            } else {
                                Log.e(TAG, "[动态权限onDenied]权限"+Arrays.toString(permissions.toArray())+"获取失败，且显示系统权限设置对话框也失败了，原因是上下文对象不是Activity！！");
                            }
                        }
                    }
                });
    }

    /**
     * 显示授权对话框
     */
    public static void showSettingDialog(Activity activity, List<String> permissions) {
        new AlertDialog.Builder(activity)
                .setTitle(R.string.common_permission_alert)
                .setMessage(PermissionNameConvert.getPermissionNames(activity, permissions))
                .setPositiveButton(R.string.rb_permission_setting_btn_go, (dialog, which) -> {
                    dialog.dismiss();
                    XXPermissions.startPermissionActivity(activity, permissions);
                })
                .setNegativeButton("取消", null)
                .show();
    }
}
