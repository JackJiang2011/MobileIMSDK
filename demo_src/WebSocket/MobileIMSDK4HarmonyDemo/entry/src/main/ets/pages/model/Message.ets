/*
 * Copyright (C) 2024  即时通讯网(52im.net) & Jack Jiang.
 * The MobileIMSDK-鸿蒙Next客户端 Project. All rights reserved.
 *
 * 联系邮件：jack.jiang@52im.net
 * 联系微信：hellojackjiang
 * 联系QQ： 413980957
 * 技术社区：http://www.52im.net
 */

import { ClientCoreSDK, Protocal, Utils } from "mobileimsdk/Index";
import ToolKits from "../utils/ToolKits";
import MsgType from "../constants/MsgType";
import MsgSendStatus from "../constants/MsgSendStatus";

/**
 * 聊天消息列表中的每个聊天消息数据的封装对象（本对像仅用于各聊天界面中ListView的UI显示时，不会用作别的地方）。
 *
 * 代码参考自IM产品RainbowChat：http://www.52im.net/thread-19-1-1.html
 *
 * @author Jack Jiang(http://www.52im.net/thread-2792-1-1.html)
 */
export default class Message {

  /** 消息发送者的id（"我"发出的消息，则此id就是本地用户的uid） */
  senderId: string = '' ;
  /** 消息发送者的昵称（用于显示） */
  senderDisplayName: string = '';
  /** 消息时间戳 */
  date: number = 0;
  /** 消息内容（注意：此消息内容可能并非扁平文本，可能是对象的JSON字串，以不同消息定义的封装对象为准）。*/
  content: string = '';

  /** 消息类型 */
  msgType: number = MsgType.TYPE_TEXT;
  /** 消息所对应的原始协议包指纹，目前只聊天消息有意义（群聊通知这种系统消息无意义） */
  fingerPrintOfProtocal: string = '';
  /** 消息所对应的群聊发送者发出的原始包协议包指纹，目前只在收到的消息对象中有意义，且仅用于群聊消息时作为消息"撤回"功能的匹配依据 */
  fingerPrintOfParent: string = '';

  /** 辅助UI显示字段：消息从网络发送的当前状态. 本字段仅针对发送的消息（而非收到的消息哦） */
  sendStatus: MsgSendStatus = MsgSendStatus.sending;

  /**
   * 辅助UI显示字段：是否显示消息气泡上方的时间。
   * 参照微信的逻辑：http://www.52im.net/thread-3008-1-1.html#40，超过2分钟的聊天消息才会在上方显示时间。
   * <p>
   * 本参数不来自消息数据本身，仅在消息载入内存时自动计算得出，用于聊天界面中显示顶部的消息时间之用。
   */
  showTopTime: boolean = false;

  constructor(senderId: string, senderDisplayName: string, date: number, content: string, msgType: number) {
    this.senderId = senderId;
    this.senderDisplayName = senderDisplayName;
    this.date = (date <= 0 ? Utils.getCurrentUTCTimestamp() : date);
    this.content = content;
    this.msgType = msgType;
  }

  /** 是否"我"发出的消息 */
  isOutgoing(): boolean {
    return Message.isOutgoing(this.senderId);
  }

  /** 是否"我"发出的消息 */
  static isOutgoing(senderId: string): boolean {
    if (senderId && senderId == (ClientCoreSDK.getInstance().currentLoginInfo?.loginUserId)) {
      return true;
    } else {
      return false;
    }
  }

  /**
   * 构建一条发出的消息对象。
   *
   * @param message 消息内容
   * @param fingerPrint 消息ID
   * @returns 新的消息对象
   */
  static createChatMsgEntity_OUTGO_TEXT(message: string, fingerPrint: string): Message {
    let localUid = ToolKits.getLocalUserUid();
    if(!localUid) {
      ClientCoreSDK.Log.error('无法读取本地用户uid，还没有登录？');
    }

    let cm: Message =  new Message(localUid ? localUid : '', "我", Utils.getCurrentUTCTimestamp(), message, MsgType.TYPE_TEXT);
    cm.fingerPrintOfProtocal = fingerPrint;
    return cm;
  }

  /**
   * 构建一条收到的消息对象。
   *
   * @param senderId 发送者uid
   * @param nickName 发送者昵称
   * @param message 消息内容
   * @param time 消息时间戳，0表示使用当前系统时间戳
   * @returns 新的消息对象
   */
  static createChatMsgEntity_INCOME_TEXT(senderId: string, nickName: string, message: string, time: number): Message {
    return new Message(senderId, nickName, time, message, MsgType.TYPE_TEXT);
  }

  /**
   * 构建一条系统消息对象。
   *
   * @param senderId 单聊请填uid、群聊请用gid
   * @param message 消息内容
   * @param time 消息时间戳，0表示使用当前系统时间戳
   * @returns 新的消息对象
   */
  static createChatMsgEntity_INCOME_SYSTEAMINFO(senderId: string, message: string, time: number): Message {
    // 生成一个fp吧，这个只用于LayzyForEach时方便ui刷新逻辑时使用，别无他用！
    return new Message(senderId, Protocal.genFingerPrint(), time, message, MsgType.TYPE_SYSTEAM$INFO);
  }
}