/*
 * Copyright (C) 2024  即时通讯网(52im.net) & Jack Jiang.
 * The MobileIMSDK-鸿蒙Next客户端 Project. All rights reserved.
 *
 * 联系邮件：jack.jiang@52im.net
 * 联系微信：hellojackjiang
 * 联系QQ： 413980957
 * 技术社区：http://www.52im.net
 */

import { router } from '@kit.ArkUI';
import { common, ConfigurationConstant } from '@kit.AbilityKit';
import { BusinessError } from '@kit.BasicServicesKit';

import {LogUtils, Logger, LogLevel, ClientCoreSDK } from 'mobileimsdk/Index';
import PreferenceManager from './utils/PreferenceManager';

const TAG: string = '[SplashPage] ';

/**
 * 闪屏界面。
 *
 * @author huawei、Jack Jiang(http://www.52im.net/thread-2792-1-1.html)
 */
@Entry
@Component
struct SplashPage {
  private preferenceManager: PreferenceManager = PreferenceManager.getInstance();
  isAgreedPrivacy: boolean = router.getParams() ? (router.getParams() as Record<string, boolean>)['isAgreedPrivacy'] : false;

  /**
   * 生命周期中只被调用一次，类似于iOS中的viewDidLoad、Android中的onCreate.
   */
  aboutToAppear(): void {
    setTimeout(() => {
      this.checkAgreedPrivacy();
    }, 0);
  }

  /**
   * 初始化.
   */
  init(): void {
    setTimeout(() => {
      this.jump();
    }, 3000);
  }

  /**
   * 检查是否已确认过隐私提示（就是在SafePage中确认过的）。
   */
  checkAgreedPrivacy(): void {
    if (this.isAgreedPrivacy) {
      this.preferenceManager.setValue('isAgreedPrivacy', true).then(() => {
        ClientCoreSDK.Log.info(TAG+ 'Put the value of isAgreedPrivacy Successfully.');
        this.init();
      }).catch((err: BusinessError) => {
        ClientCoreSDK.Log.error(TAG+ 'Put the value of isAgreedPrivacy Failed, err: ' + err);
      });
    } else {
      this.preferenceManager.getValue<boolean>('isAgreedPrivacy').then((isAgreed: boolean | null) => {
        if (isAgreed) {
          this.init();
        } else {
          router.replaceUrl({ url: 'pages/SafePage' });
        }
      }).catch((err: BusinessError) => {
        ClientCoreSDK.Log.error(TAG+ 'check isAgreedPrivacy Failed, err: ' + err);
      });
    }
  }

  jump(): void {
    // 进入登录界面
    router.replaceUrl({ url: 'pages/LoginPage' });
  }

  build() {
    Column() {
      Column() {
        Column() {
          Image($r('app.media.ic_splash_graybg2'))
            .width(210)
            .aspectRatio(1)
            .objectFit(ImageFit.Contain)
            .transition(TransitionEffect.asymmetric(TransitionEffect.OPACITY.animation({ duration: 2000, curve: Curve.Ease }), TransitionEffect.OPACITY.animation({ duration: 0 })))
        }
        .width('100%')
        .aspectRatio(2 / 3)
        .backgroundImageSize({ width: '225%', height: '100%' })
        .backgroundImagePosition(Alignment.Center)
        .justifyContent(FlexAlign.Center)

        Column() {
          Row() {
            Text('MobileIMSDK')
              .fontFamily('HarmonyHeiTi')
              .fontColor('#000000')//'#FFFFFF')
              .fontSize(24)
              .fontWeight(500)

            Text('鸿蒙')
              .fontFamily('HarmonyHeiTi')
              .fontColor('#ffffff')
              .fontSize(12)
              .fontWeight(500)// .backgroundColor('#e50a59f7')
              .backgroundColor('#000000')
              .padding({ top: 4, bottom: 4, left: 6, right: 6 })
              .borderRadius(6)
              .margin({ left: 5 })
          }

          // Text('欢迎使用MobileIMSDK开源框架')
          Text('愉快地开发您自已的即时通讯应用')
            .fontFamily('HarmonyHeiTi')
            .fontSize(16)
            .fontColor($r('sys.color.ohos_id_color_text_secondary'))
            .opacity(0.7)
            .fontWeight(400)
            .margin({ top: 10 })
        }
        .transition(TransitionEffect.asymmetric(TransitionEffect.OPACITY.animation({ duration: 1000, curve: Curve.Ease }), TransitionEffect.OPACITY.animation({ duration: 0 })))
      }
      .padding({ bottom: '17%', top: 24 })
      .justifyContent(FlexAlign.SpaceBetween)
      .height('100%')
      .width('100%')
    }
    .height('100%')
    .width('100%')
    .backgroundColor('#ffffff')//'#0A59F7')
    // 适配流海屏（顶部状态栏透明并穿过，并透过底部上划操作区）
    .expandSafeArea([SafeAreaType.SYSTEM], [SafeAreaEdge.TOP, SafeAreaEdge.BOTTOM])
  }
}