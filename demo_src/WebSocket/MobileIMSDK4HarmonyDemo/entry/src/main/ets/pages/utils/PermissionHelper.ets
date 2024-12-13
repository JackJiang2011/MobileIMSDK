/*
 * Copyright (C) 2024  即时通讯网(52im.net) & Jack Jiang.
 * The MobileIMSDK-鸿蒙Next客户端 Project. All rights reserved.
 *
 * 联系邮件：jack.jiang@52im.net
 * 联系微信：hellojackjiang
 * 联系QQ： 413980957
 * 技术社区：http://www.52im.net
 */

import abilityAccessCtrl, { Permissions } from '@ohos.abilityAccessCtrl'
import { BusinessError } from '@kit.BasicServicesKit';
import { bundleManager, common } from '@kit.AbilityKit';
import { Logger, ClientCoreSDK } from 'mobileimsdk/Index';

const TAG: string = '[PermissionHelper]';
const Log :Logger = ClientCoreSDK.Log

async function checkAccessToken(permission: Permissions): Promise<abilityAccessCtrl.GrantStatus> {
    let atManager: abilityAccessCtrl.AtManager = abilityAccessCtrl.createAtManager();
    let grantStatus: abilityAccessCtrl.GrantStatus = abilityAccessCtrl.GrantStatus.PERMISSION_DENIED;

    // Obtains the accessTokenID of an application.
    let tokenId: number = 0;
    try {
        let bundleInfo: bundleManager.BundleInfo =
            await bundleManager.getBundleInfoForSelf(bundleManager.BundleFlag.GET_BUNDLE_INFO_WITH_APPLICATION);
        let appInfo: bundleManager.ApplicationInfo = bundleInfo.appInfo;
        tokenId = appInfo.accessTokenId;
    } catch (error) {
        let err: BusinessError = error as BusinessError;
        Log.error(TAG+ `Failed to get bundle info for self. Code is ${err.code}, message is ${err.message}`);
    }
    // Check whether the app is granted with the permission.
    try {
        grantStatus = await atManager.checkAccessToken(tokenId, permission);
    } catch (error) {
        let err: BusinessError = error as BusinessError;
        Log.error(TAG+ `Failed to check access token. Code is ${err.code}, message is ${err.message}`);
    }
    return grantStatus;
}

/**
 * 检查权限并返回未授权权限的数组集合。
 *
 * @param context
 * @param permissions
 * @returns 未授权的权限集合（如果全部已获授权则返回的是空数组）
 */
export async function checkPermissions(context: Context, permissions: Permissions[]): Promise<Permissions[]> {
    let ungrantedPermissions: Permissions[] = [];
    for (let i = 0; i < permissions.length; i++) {
        let grantStatus: abilityAccessCtrl.GrantStatus = await checkAccessToken(permissions[i]);
        if (grantStatus !== abilityAccessCtrl.GrantStatus.PERMISSION_GRANTED) {
            ungrantedPermissions.push(permissions[i])
        }
    }
    return ungrantedPermissions
}

export async function checkPermission(context: Context, permission: Permissions): Promise<boolean> {
    let grantStatus: abilityAccessCtrl.GrantStatus = await checkAccessToken(permission);
    if (grantStatus !== abilityAccessCtrl.GrantStatus.PERMISSION_GRANTED) {
        return false
    }
    return true
}

export async function requestPermissions(context: Context, permissions: Permissions[]): Promise<void> {
    let atManager = abilityAccessCtrl.createAtManager();
    await atManager.requestPermissionsFromUser(context, permissions).then((data) => {
        console.info(`Request permissions succeed, data is: ${data}`);
    }).catch((error: Error) => {
        console.error(`Request permissions failed, error is: ${error}`);
    });
}
