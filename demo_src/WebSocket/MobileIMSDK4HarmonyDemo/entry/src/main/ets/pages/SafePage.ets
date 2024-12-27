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
import { common } from '@kit.AbilityKit';
import ToolKits from './utils/ToolKits';

/**
 * 隐私提示和确认界面。
 *
 * @author huawei、Jack Jiang(http://www.52im.net/thread-2792-1-1.html)
 */
@Entry
@Component
struct SafePage {
  build() {
    GridRow({ columns: 4, gutter: { x: 24 }, }) {
      GridCol({ span: 4, offset: 0 }) {
        Column() {
          Image($r('app.media.startIcon1'))
            .width(96)
            .aspectRatio(1)
            .margin({ top: 80 })
            .border({width: 1, radius: 22, color: '#eceef1'})
          Text('欢迎使用')
            .fontSize(14)
            .fontColor($r('sys.color.ohos_id_color_text_secondary'))
            .lineHeight(19)
            .margin({ top: 24 })
          Row() {
            Text('MobileIMSDK')
              .fontSize(30)
              .fontColor($r('sys.color.ohos_id_color_text_primary'))
              .lineHeight(40)
              .fontWeight(FontWeight.Medium)
            Text('鸿蒙')
              .fontFamily('HarmonyHeiTi')
              .fontColor('#ffffff')
              .fontSize(13)
              .fontWeight(500)
              .backgroundColor('#000000')
              .padding({top:4, bottom:4, left: 6, right: 6})
              .borderRadius(6)
              .margin({left: 5})
          }
          Text('愉快的开发您自已的即时通讯应用程序')
            .fontSize(14)
            .fontColor($r('sys.color.ohos_id_color_text_secondary'))
            .lineHeight(19)
            .margin({ top: 4 })

          Blank()

          Image($r('app.media.ic_safe'))
            .height(24)
            .height(24)
            .margin({ bottom: 26 })

          Text() {
            Span('本应用仅作为MobileIMSDK的演示Demo，')
              .fontSize(10)
              .fontColor($r('sys.color.ohos_id_color_text_secondary'))
              .lineHeight(14)
            Span('它需要使用移动网络')
              .fontSize(10)
              .fontColor($r('sys.color.ohos_id_color_text_primary'))
              .fontWeight(FontWeight.Medium)
              .lineHeight(14)
            Span('，以便进行实时长连接通信。')
              .fontSize(10)
              .lineHeight(14)
            Span("它不会读取您的任何隐私数据，请放心体验。更多资料请前往")
              .fontSize(10)
              .fontColor($r('sys.color.ohos_id_color_text_secondary'))
              .lineHeight(14)
            Span(' 即时通讯网(52im.net) ')
              .fontSize(10)
              .fontColor('#0A59F7')
              .fontWeight(FontWeight.Medium)
              .lineHeight(14)
              .onClick(() => {
                router.pushUrl({ url: 'pages/PrivacyPage' })
              })
            Span('查阅，让即时通讯技术能更好地传播与分享。')
              .fontSize(10)
              .fontColor($r('sys.color.ohos_id_color_text_secondary'))
              .lineHeight(14)
          }
          .width('100%')

          Text("请阅读以上信息，点击下方\"同意\"按钮，表示您认可本应用相关权限的申请。")
            .fontSize(10)
            .fontColor($r('sys.color.ohos_id_color_text_secondary'))
            .lineHeight(14)
            .width('100%')
            .margin({ top: 4 })

          Row({ space: 12 }) {
            Button('取消')
              .backgroundColor('#0D000000')
              .fontColor('#0A59F7')
              .layoutWeight(1)
              .onClick(() => {
                let context: common.UIAbilityContext = getContext(this) as common.UIAbilityContext;
                // 退出APP
                ToolKits.exitSystem(context)
              })
            Button('同意')
              .backgroundColor('#0A59F7')
              .fontColor(Color.White)
              .layoutWeight(1)
              .onClick(() => {
                // 进入闪屏界面
                router.replaceUrl({ url: 'pages/SplashPage', params: { isAgreedPrivacy: true } })
              })
          }
          .width('100%')
          .justifyContent(FlexAlign.SpaceBetween)
          .margin({ top: 24, bottom: 45 })
        }
        .width('100%')
        .height('100%')
      }
    }
    .backgroundColor($r('sys.color.ohos_id_color_sub_background'))
    .padding({ left: 24, right: 24 })
    // 适配流海屏（顶部状态栏透明并穿过，并透过底部上划操作区）
    .expandSafeArea([SafeAreaType.SYSTEM], [SafeAreaEdge.TOP, SafeAreaEdge.BOTTOM])
  }
}