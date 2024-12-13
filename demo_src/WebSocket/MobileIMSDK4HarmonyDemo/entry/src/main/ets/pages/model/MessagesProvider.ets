/*
 * Copyright (C) 2024  即时通讯网(52im.net) & Jack Jiang.
 * The MobileIMSDK-鸿蒙Next客户端 Project. All rights reserved.
 *
 * 联系邮件：jack.jiang@52im.net
 * 联系微信：hellojackjiang
 * 联系QQ： 413980957
 * 技术社区：http://www.52im.net
 */

import { ClientCoreSDK, Protocal } from "mobileimsdk/Index";
import IMClientManager from "../../IMClientManager";
import MsgSendStatus from "../constants/MsgSendStatus";
import UIEvent from "../constants/UIEvent";
import Message from "./Message";

/**
 * 聊天消息的缓存数据管理提供者（集中管理所有的聊天消息和指令，消息来源为网络层通信数据包和本地发出的包
 * ，消息显示方式通过MessagesDataSource与UI界面进行解偶显示）。
 *
 * 代码参考自IM产品RainbowChat：http://www.52im.net/thread-19-1-1.html
 *
 * @author Jack Jiang(http://www.52im.net/thread-2792-1-1.html)
 */
export default class MessagesProvider {

  /** 聊天界面中，消息的显示时间间隔（单位：毫秒）：默认是2分钟内的消息只在第一条消息上显示时间，否则会再次显示时间 */
  // 参考资料：http://www.52im.net/thread-3008-1-1.html#40
  private static readonly CHATTING_MESSAGE_SHOW_TIME_INTERVAL: number = 2 * 60 * 1000;

  /** 真正的聊天软件中，此处应改造为<key=uid, value=Array<Message> >这样的Map集合，用于按uid分别存储与各好友的聊天消息 */
  private messages: Array<Message> = [];

  /**
   * 加入一条新消息。
   *
   * @param m 消息对象
   */
  putMessage(m: Message): void {
    // 以下代码用于判断并实现仿微信的只显示2分钟内聊天消息的时间标识（参考资料：http://www.52im.net/thread-3008-1-1.html#40）
    let previousMessage: Message | undefined = undefined;
    let messagesSize: number = this.messages.length;
    if (messagesSize > 0) {
      previousMessage = this.messages[messagesSize - 1];
    }
    MessagesProvider.setMessageShowTopTime(m, previousMessage);

    // 将此新消息对象放入数据模型(列表)
    this.messages.push(m);

    // 通知应用层更新ui
    IMClientManager.getInstance().getEmitter().emit(UIEvent.UIEVENT_messageAdded, this.messages.length - 1);
  }

  /**
   * 获得消息数据缓存集合。
   *
   * @returns 消息数据缓存集合
   */
  getMessages(): Array<Message> {
    return this.messages;
  }

  /**
   * 添加一条系统消息（显示在聊天列表中）。
   *
   * @param content 消息内容
   */
  addSystemMessage(content: string): void {
    let m: Message = Message.createChatMsgEntity_INCOME_SYSTEAMINFO('0', content, 0);
    this.putMessage(m);
  }

  /**
   * 更新指定指纹码的消息的发送状态（更新单条消息）。
   *
   * @param fingerPrint 消息指纹码（消息id）
   * @param sendStatus 发送状态，see {@link MsgSendStatus}
   */
  updateMessageSendStatus(fingerPrint: string, sendStatus: MsgSendStatus): void {
    // 遍历消息列表
    for(let i = 0; i < this.messages.length; i++) {
      let m = this.messages[i];
      // 对符合条件的消息对象进行消息发送状态的设置
      if(m && m.isOutgoing() && m.fingerPrintOfProtocal === fingerPrint) {
        // 更新状态
        m.sendStatus = sendStatus;
        // 通知应用层更新ui（参数就是消息所在索引）
        IMClientManager.getInstance().getEmitter().emit(UIEvent.UIEVENT_messageUpdate, i);
      }
    }
  }

  /**
   * 更新指定指纹码的消息的发送状态（更新多条消息）。
   * 目前用于QoS送达机制中告诉应用层有哪些原始消息报文未成功送达给对方。
   *
   * @param  protocals 原始消息报文对象数组，数组中Protocal对象指纹码（消息id）就是本次要更新的对象，这个数组目前来自于SDK的 EventType.onMessagesLost 事件通知
   * @param sendStatus 发送状态，see {@link MsgSendStatus}
   */
  updateMessagesSendStatus(protocals: Protocal[], sendStatus: MsgSendStatus): void {
    let updateIndexes: number[] = [];

    // 遍历消息列表
    // this.messages.forEach((m: Message) => {
    for(let mi = 0; mi < this.messages.length; mi++) {
      let m = this.messages[mi];
      for(let i = 0; i < protocals.length; i++){
        let p = protocals[i];
        // 对符合条件的消息对象进行消息发送状态的设置
        if(m && m.isOutgoing() && m.fingerPrintOfProtocal === p.fp) {
          // 更新状态
          m.sendStatus = sendStatus;
          // 加入已更新索引列表集合
          updateIndexes.push(mi);
          break;
        }
      }
    }

    if(updateIndexes.length > 0) {
      // 通知应用层更新ui（参数就是消息所在索引）
      IMClientManager.getInstance().getEmitter().emit(UIEvent.UIEVENT_messagesUpdate, updateIndexes);
    }
  }

  /**
   * 清空所有消息。
   */
  clear(): void {
    this.messages = [];
  }

  /**
   * 为当前的消息对象，设置是否显示消息时间标识。
   *
   * 此时间显示逻辑是与微信保持一致的：即只显示5分钟内聊天消息的时间标识，参考资料：http://www.52im.net/thread-3008-1-1.html#40
   *
   * @param theMessage 当前消息对象，不可为null
   * @param previousMessage 当前消息的自然时间的上一条消息，此消息可为空（此为空即表示当前消息就是消息集合中的第一条消息）
   */
  private static setMessageShowTopTime(theMessage: Message, previousMessage: Message | undefined): void {
    if(theMessage) {
      if(previousMessage === undefined) {
        theMessage.showTopTime = true;
        return;
      }

      // 以下代码用于判断并实现仿微信的只显示5分钟内聊天消息的时间标识（参考资料：http://www.52im.net/thread-3008-1-1.html#40）
      if(theMessage.date - previousMessage.date > MessagesProvider.CHATTING_MESSAGE_SHOW_TIME_INTERVAL)
        theMessage.showTopTime = true;
    }
  }
}