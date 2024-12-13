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
import ToolKits from "../../utils/ToolKits";

/**
 * 聊天消息时间显示组件。
 *
 * @author Jack Jiang(http://www.52im.net/thread-2792-1-1.html)
 */
@Component
export struct MsgTimeView {
  message?: Message;

  build() {
    // 需要显示消息时间的才显示，否则不需要显示
    if (this.message?.showTopTime) {
      Text(ToolKits.getTimeStringAutoShort2(this.message.date, true, true))
        .fontSize(12)
        .fontColor('#979ca6')
        .margin({bottom: 13})
    }
  }
}

