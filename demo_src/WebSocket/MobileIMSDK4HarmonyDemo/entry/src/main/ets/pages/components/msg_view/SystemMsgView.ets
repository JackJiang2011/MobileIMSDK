/*
 * Copyright (C) 2024  即时通讯网(52im.net) & Jack Jiang.
 * The MobileIMSDK-鸿蒙Next客户端 Project. All rights reserved.
 *
 * 联系邮件：jack.jiang@52im.net
 * 联系微信：hellojackjiang
 * 联系QQ： 413980957
 * 技术社区：http://www.52im.net
 */

import Message from "../../model/Message";

/**
 * 系统消息UI组件。
 *
 * @author Jack Jiang(http://www.52im.net/thread-2792-1-1.html)
 */
@Preview
@Component
export struct MsgView_system {
  message?: Message;

  build() {
    if(this.message) {
      Row() {
        Text(this.message.content)
          .fontColor('#979ca6')
          .backgroundColor('#e5e7ec')
          .fontSize(13)
          .padding({
            top: 5,
            left: 15,
            bottom: 5,
            right: 15
          })
          .borderRadius(12)
          .textAlign(TextAlign.Center)
      }
      .width("100%")
      .justifyContent(FlexAlign.Center)
    }
  }
}