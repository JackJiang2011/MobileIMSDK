/*
 * Copyright (C) 2024  即时通讯网(52im.net) & Jack Jiang.
 * The MobileIMSDK-鸿蒙Next客户端 Project. All rights reserved.
 *
 * 联系邮件：jack.jiang@52im.net
 * 联系微信：hellojackjiang
 * 联系QQ： 413980957
 * 技术社区：http://www.52im.net
 */

/**
 * 消息的发送状态常量.
 *
 * 代码参考自IM产品RainbowChat：http://www.52im.net/thread-19-1-1.html
 *
 * @author Jack Jiang(http://www.52im.net/thread-2792-1-1.html)
 */
export default class MsgSendStatus {
  /** 消息发送中 */
  static readonly sending: number    = 0;
  /** 消息已被对方收到（我方已收到QoS应答包） */
  static readonly beReceived: number = 1;
  /** 消息发送失败（在QoS超时重传的时间内未收到应答包） */
  static readonly sendFaild: number  = 2;
}