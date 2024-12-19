/*
 * Copyright (C) 2024  即时通讯网(52im.net) & Jack Jiang.
 * The MobileIMSDK-鸿蒙Next客户端 Project. All rights reserved.
 *
 * 联系邮件：jack.jiang@52im.net
 * 联系微信：hellojackjiang
 * 联系QQ： 413980957
 * 技术社区：http://www.52im.net
 */

import { ClientCoreSDK } from "mobileimsdk/Index";
import ToolKits from "../utils/ToolKits";

/**
 * 聊天界面的自定义标题栏组件。
 *
 * @author Jack Jiang(http://www.52im.net/thread-2792-1-1.html)
 */
@Preview
@Component
export struct CustomeTitleBar {

  // 连接状态：false 连接断开、true 连接正常
  @Prop connectStatus: boolean;

  @Prop keepAliveStatus: boolean;

  // 返回按钮图片
  backImg: Resource = $r("app.media.widget_title_btn_back_normal");

  build() {

    Column() {
      Row() {
        Row() {
          Text('MobileIMSDK')
            .textAlign(TextAlign.Start)
            .fontSize(22)
            .maxLines(1)
            .fontColor(Color.Black)
            .fontWeight(500)
            .ellipsisMode(EllipsisMode.CENTER)
            .textOverflow({overflow: TextOverflow.Ellipsis})

          Text('鸿蒙')
            .fontFamily('HarmonyHeiTi')
            .fontColor(Color.White)
            .fontSize(11)
            .fontWeight(400)
              // .backgroundColor('#e50a59f7')
            .backgroundColor(Color.Black)
            .padding({top:3, bottom:3, left: 4, right: 4})
            .borderRadius(5)
            .margin({left: 3})

          Text('v2.0')
            .fontSize(10)
            .fontColor('#999')
            .margin({left: 5})
        }
        .layoutWeight(1)

        Row() {
          // Image($r(this.connectStatus ? (this.keepAliveStatus ? 'app.media.ic_net_status_light': 'app.media.ic_net_status_ok') : 'app.media.ic_net_status_warn'))
          //   .width(12)
          //   .height(12)
          //   .objectFit(ImageFit.Fill)
          //   .margin({right: 5})

          Text()
            .width(12)
            .height(12)
            .borderRadius(6)
            .margin({right: 5})
            .backgroundColor(this.connectStatus ? (this.keepAliveStatus ? '#f1f515': '#00de7a') : '#ff8c6f')// '#00de7a'
            .border({width: 1, color: (this.connectStatus ? (this.keepAliveStatus ? '#d1d505': '#00c86e') : '#f26c4f'), radius: 6})
            .shadow({
              radius: 15, // 模糊半径
              color: this.keepAliveStatus ? "#59f1f515" : '#00ffffff', // 阴影颜色
              offsetX: 0, // X轴偏移量
              offsetY: 15 // Y轴偏移量
              })

          Text('UID:')
            .textAlign(TextAlign.Start)
            .fontSize(13)
            .maxLines(1)
            .fontColor(Color.Black)
            .fontWeight(500)
            .margin({right: 2})

          Text(ClientCoreSDK.getInstance().currentLoginInfo?.loginUserId)
            // .width(40)
            .constraintSize({maxWidth: 42})
            .fontSize(13)
            .fontColor('#505256')
            .maxLines(2)
            .ellipsisMode(EllipsisMode.CENTER)
            .textOverflow({overflow: TextOverflow.Ellipsis})
        }
      }
      .height(58)
      .padding({left: 10, right: 10})

      this.BottomLine()

      // if(!this.connectStatus) {
        Row() {
          LoadingProgress().width(20).height(20).margin({ right: 8 }).color('#ff6434')
          // Image($r('app.media.ic_disconnect_info2'))
          //   .width(14)
          //   .height(14)
          //   .objectFit(ImageFit.Fill)
          //   .margin({right: 8})
          Text('网络不给力，正在努力恢复中…')
            .maxLines(1)
            .ellipsisMode(EllipsisMode.CENTER)
            .textOverflow({ overflow: TextOverflow.Ellipsis })
            .fontColor('#ff6434')
            .fontSize(14)
        }
        .width('100%')
        .height(40)
        .backgroundColor('#fff0eb')
        .justifyContent(FlexAlign.Center)
        .visibility(this.connectStatus ? Visibility.None : Visibility.Visible)
        .animation({
          duration: 250,
          curve: Curve.EaseInOut,
          iterations: 1,
          playMode: PlayMode.Normal
        })
      // }
    }
    .width('100%')
    .backgroundColor(ToolKits.chat_page_title_background_color)//'#2e3746')
    // .linearGradient({
    //   direction:GradientDirection.Right,// 渐变方向
    //   colors:[['#2e3746',0],['#0e1a26',1]]
    // })
    // .shadow({
    //   radius: 6, // 模糊半径
    //   color: "#ffff0000", // 阴影颜色
    //   offsetX: 0, // X轴偏移量
    //   offsetY: 10 // Y轴偏移量
    // })
  }


  @Builder
  BottomLine() {
    Line()
      .height('1px')
      .width('100%')
      .backgroundColor('#dbdbdb')
  }
}
