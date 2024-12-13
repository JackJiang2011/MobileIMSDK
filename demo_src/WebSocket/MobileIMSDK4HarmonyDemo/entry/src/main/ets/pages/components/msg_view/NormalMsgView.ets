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
import MsgType from "../../constants/MsgType";
import { MsgContentView_text } from "../msg_content_view/MsgContentView_text";
import MsgSendStatus from "../../constants/MsgSendStatus";
import ToolKits from "../../utils/ToolKits";

/**
 * 普通聊天消息UI组件。
 *
 * @author Jack Jiang(http://www.52im.net/thread-2792-1-1.html)
 */
@Component
export struct MsgView_normal {
  message?: Message;

  build() {
    Row() {
      if (this.message) {
        // 如果是收到的消息，则头像在最左边显示
        if(this.message.isOutgoing() === false) {
          this.AvatarImage(false)
        }

        // 消息气泡外层父布局
        Column() {
          // 昵称显示
          this.NicknameText(this.message.senderDisplayName, this.message.isOutgoing());

          // 特别说明：当此组件为Row()时，将导致文本超长时尾部挤出屏幕的问题，这或许是鸿蒙的bug，使用Flex()则能解决问题，参考资料：https://developer.huawei.com/consumer/cn/forum/topic/0204138991096490058
          Flex({direction: FlexDirection.Row, wrap: FlexWrap.NoWrap, justifyContent:(this.message.isOutgoing() ? FlexAlign.End : FlexAlign.Start)}) {

            // 消息状态图标的显示：只有“发出”的消息需要显示哦
            this.SendStatusIcon(this.message)

            // 消息内容子组件（按消息类型调用对应的子组件实现）
            if (this.message.msgType === MsgType.TYPE_TEXT) {
              // 注：设置layoutWeight(1)能解决超长挤出屏幕的问题，但占满所有空间，这不是预期想要的
              MsgContentView_text({ message: this.message })
            } else {
              // TODO: 更多消息类型待实现哦！！
              // TODO: 更多消息类型待实现哦！！
              // TODO: 更多消息类型待实现哦！！
            }
          }
        }
        .messageBubbleOutsideStyle(this.message.isOutgoing())

        // 如果是发出的消息，则头像在最右边显示
        if(this.message.isOutgoing() === true) {
          this.AvatarImage(true)
        }
      }
    }
    .width("100%").alignItems(VerticalAlign.Top)
  }

  @Builder
  NicknameText(nickname: string, isOutgoing: boolean) {
    Text(nickname)// .backgroundColor('#009900')
      .fontColor('#979ca6')
      .fontSize(11)
      .margin(isOutgoing ? { bottom: 4, right: 12 }: { bottom: 4, left: 12 })
      .visibility(isOutgoing ? Visibility.None : Visibility.Visible)
  }

  @Builder
  AvatarImage(isOutgoing: boolean) {
    Image($r(isOutgoing ? "app.media.default_avatar1": "app.media.default_avatar2"))
      .backgroundColor(Color.White)
      .objectFit(ImageFit.Fill)
      .width(40)
      .height(40)
      .borderRadius(20)
      .margin({ left: 8, right: 8 })
  }

  @Builder
  SendStatusIcon(message: Message) {
    // 只有“发出”的消息有消息状态ui的显示
    if (message.isOutgoing() === true) {
      // 发送失败
      if (message.sendStatus === MsgSendStatus.sendFaild) {
        Image($r('app.media.ic_warn'))
          .fillColor($r('sys.color.ohos_id_color_badge_red'))//'#ff453a')
          .width(19)
          .height(19)
          .margin({top:3, right: 7})
          .onClick(() => {
            ToolKits.showToast('本条消息没成功送达！')
          })
      }
      // 发送中
      else if (message.sendStatus === MsgSendStatus.sending) {
        Progress({ value: 0, total: 100, type: ProgressType.Ring })
          .color(Color.Grey).value(19).width(19)
          .style({strokeWidth: 2, scaleCount: 15, scaleWidth: 5, status: ProgressStatus.LOADING })
          .margin({top:5, right: 7})
          .onClick(() => {
            ToolKits.showToast('消息发送中...')
          })
      }
      // 发送成功
      else {
        // 啥也不显示
      }
    }
  }
}

// 消息气泡外层父布局的样式设置
@Extend(Column)
function messageBubbleOutsideStyle(isOutgoing: boolean) {
  .layoutWeight(1)
  .alignItems(isOutgoing ? HorizontalAlign.End : HorizontalAlign.Start)
  // 气泡末端距离手机两侧的间距，目的是让消息气泡的末端要空出一个头像+头像两边间距的空白距离，ui上好看一点
  .margin(isOutgoing ? { left: 40 + 8 + 8 } : { right: 40 + 8 + 8 })
}