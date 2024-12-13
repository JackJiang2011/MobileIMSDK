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
 * 普通聊天消息内容组件（文本消息）。
 *
 * @author Jack Jiang(http://www.52im.net/thread-2792-1-1.html)
 */
@Component
export struct MsgContentView_text {
  // 消息数据对象
  message?: Message;

  build() {
    if(this.message) {
      Row() {
        Text(this.message.content)
          .enableDataDetector(true)
          .dataDetectorConfig({
            types: [TextDataDetectorType.URL, TextDataDetectorType.PHONE_NUMBER, TextDataDetectorType.EMAIL]
          })
          .fontSize(14)
      }
      // 消息气泡四周的空白
      .padding(this.message.isOutgoing() ? { top: 14, bottom: 14, left: 14, right: 16 } : { top: 14, bottom: 14, left: 16, right: 14 })
      // 气泡图片的不可拉伸区
      .backgroundImageResizable(this.message.isOutgoing() ? { slice: { top: 21, left: 14, bottom: 14, right: 18, } } : {slice: {top: 21,left: 18,bottom: 14,right: 14, } })
      // 气泡背景图
      .backgroundImage($r(this.message.isOutgoing() ? "app.media.chatto_bg_normal": "app.media.chatfrom_bg_normal"))
      // 背景图要填满组件
      .backgroundImageSize({ width: '100%', height: '100%' })
    }
  }
}