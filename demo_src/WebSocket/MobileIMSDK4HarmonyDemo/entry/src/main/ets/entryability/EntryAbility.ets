/*
 * Copyright (C) 2024  即时通讯网(52im.net) & Jack Jiang.
 * The MobileIMSDK-鸿蒙Next客户端 Project. All rights reserved.
 *
 * 联系邮件：jack.jiang@52im.net
 * 联系微信：hellojackjiang
 * 联系QQ： 413980957
 * 技术社区：http://www.52im.net
 */

import { AbilityConstant, common, UIAbility, Want } from '@kit.AbilityKit';
import { window } from '@kit.ArkUI';
import IMClientManager from '../IMClientManager';
import { ClientCoreSDK } from "mobileimsdk";

const TAG: string = '[EntryAbility] '

/**
 * 入口Ability实现类。
 *
 * @author Jack Jiang(http://www.52im.net/thread-2792-1-1.html)
 */
export default class EntryAbility extends UIAbility {
  onCreate(want: Want, launchParam: AbilityConstant.LaunchParam): void {
    ClientCoreSDK.Log.debug(TAG+ 'Ability onCreate');

    // init MobileIMSDK first
    // 提示：在不退出APP的情况下退出登陆后再重新登陆时，请确保调用本方法一次，不然会报code=203错误哦！
    IMClientManager.getInstance().initMobileIMSDK(this.context.getApplicationContext(), this.context.eventHub);
  }

  onDestroy(): void {
    ClientCoreSDK.Log.debug(TAG+ 'Ability onDestroy');

    // 释放IM核心占用的资源
    IMClientManager.getInstance().releaseMobileIMSDK();
  }

  onWindowStageCreate(windowStage: window.WindowStage): void {
    // Main window is created, set main page for this ability
    ClientCoreSDK.Log.debug(TAG+ 'Ability onWindowStageCreate');

    AppStorage.setOrCreate<window.WindowStage>('windowStage', windowStage);
    AppStorage.setOrCreate<window.Window>('window', windowStage.getMainWindowSync());// 获取应用主窗口
    AppStorage.setOrCreate<common.UIAbilityContext>('uiAbilityContext', this.context);

    windowStage.loadContent('pages/SplashPage', (err) => {
      if (err.code) {
        ClientCoreSDK.Log.error(TAG+ 'Failed to load the content. Cause: %{public}s'+ (JSON.stringify(err) ?? ''));
        return;
      }
      ClientCoreSDK.Log.debug(TAG+ 'Succeeded in loading the content.');
    });
  }

  onWindowStageDestroy(): void {
    // Main window is destroyed, release UI related resources
    ClientCoreSDK.Log.debug(TAG+ 'Ability onWindowStageDestroy');
  }

  onForeground(): void {
    // Ability has brought to foreground
    ClientCoreSDK.Log.debug(TAG+ 'Ability onForeground');
  }

  onBackground(): void {
    // Ability has back to background
    ClientCoreSDK.Log.debug(TAG+ 'Ability onBackground');
  }
}
