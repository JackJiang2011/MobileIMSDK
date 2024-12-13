/*
 * Copyright (C) 2024  即时通讯网(52im.net) & Jack Jiang.
 * The MobileIMSDK-鸿蒙Next客户端 Project. All rights reserved.
 *
 * 联系邮件：jack.jiang@52im.net
 * 联系微信：hellojackjiang
 * 联系QQ： 413980957
 * 技术社区：http://www.52im.net
 */

import { LocalDataSender, ClientCoreSDK, ErrorCode, Protocal } from "mobileimsdk/Index";
import IMClientManager from "../../IMClientManager";
import Message from "../model/Message";
import ToolKits from "../utils/ToolKits";

const TAG: string = '[ChatInputView] '

/**
 * 聊天界面输入区组件。
 *
 * @author Jack Jiang(http://www.52im.net/thread-2792-1-1.html)
 */
@Component
export struct ChatInputView {
  // @Link message: string;

  @State messageInput: string = '';
  @State receiverInput: string = '';

  // 发送消息实现方法
  doSendMessage() {
    if(this.messageInput.length > 0 && this.receiverInput.length > 0) {
      if(this.receiverInput === ClientCoreSDK.getInstance().currentLoginInfo?.loginUserId) {
        ToolKits.showToast('不能给自已发送消息哦！');
        return;
      }

      // 先生成指纹码
      let fingerPring: string = Protocal.genFingerPrint();
      // 发送消息
      let code = LocalDataSender.getInstance().sendCommonDataPlain(this.messageInput, this.receiverInput, true, fingerPring);
      if(code === ErrorCode.COMMON_CODE_OK) {
        ClientCoreSDK.Log.info("数据已成功发出！");

        // 用于ui显示的消息对象
        let m: Message = Message.createChatMsgEntity_OUTGO_TEXT(this.messageInput, fingerPring);
        // 将消息对象放入全局消息数据缓存中（以备聊天ui列表中显示之）
        IMClientManager.getInstance().getMessageProvider().putMessage(m);
        // 清空输入框
        this.messageInput = '';
      } else {
        ToolKits.showToast("数据发送失败。错误码是："+code+"！");
      }
    } else {
      ToolKits.showToast('接收者id或发送内容为空，本次消息发送没有继续!');
      ClientCoreSDK.Log.error(TAG+ "msg.len="+this.messageInput.length+",receiverUid.len="+this.receiverInput.length)
    }
  }

  build() {

    Row() {
      TextInput({
        placeholder: "对方ID", text: $$this.receiverInput
      })
        .backgroundColor('#f5f7fa')
        .width(56)
        .height(34)
        // .onChange((msg: string) => {
        //   this.message = msg
        // })
        .fontSize(13)
        .borderRadius(8)
        .padding({ left: 6, right: 6 })
        .placeholderFont({ size: 14 })
        .placeholderColor('#a1a7af')

      TextInput({
        placeholder: "请输入聊天内容，按发送键提交", text: $$this.messageInput
      })
        .backgroundColor('#f5f7fa')
        .width(100)// .layoutWeight(8)
        .height(34)
        // .onChange((msg: string) => {
        //   this.message = msg
        // })
        .fontSize(13)
        .layoutWeight(1)
        .borderRadius(8)
        .margin({ left: 10, right: 10 })
        .padding({ left: 10, right: 10 })
        .placeholderFont({ size: 14 })
        .placeholderColor('#a1a7af')

      Button("提交", { type: ButtonType.Normal, stateEffect: true })
        .width(44)
        .height(34)
        .borderRadius(8)
        .padding(0)
        .fontSize(14)
        .backgroundColor('#00de7a')
        .fontWeight(500)// .margin(4)
        .onClick(() => {
          // let MsgIn: MsgInput = new MsgInput()
          // MsgIn.id = this.MsgArray.length.toString()
          // MsgIn.role = "user"
          // MsgIn.content = this.message
          // this.MsgArray.push(MsgIn)
          // this.httpData(this.MsgArray)

          // this.message = "";

          // ClientCoreSDK.Log.info('对方uid='+this.receiverInput+', 消息='+this.messageInput);

          // 发送消息
          this.doSendMessage();
        })
    }
    .height(54)
    .width('100%')
    .backgroundColor("#ffffff") //ffffff
    .padding({ left: 10, right: 10 })
    .alignItems(VerticalAlign.Center)

    // .shadow({
    //   radius: 30, // 模糊半径
    //   color: "#0d000000", // 阴影颜色
    //   offsetX: 0, // X轴偏移量
    //   offsetY: -5 // Y轴偏移量
    // })
  }
}