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
 * 应用层的各种通知事件类型，这些事件主要用于通知UI界面的更新等。
 *
 * @author Jack Jiang(http://www.52im.net/thread-2792-1-1.html)
 */
export default class UIEvent {
  /** UI事件：登录或掉线重连完成 */
  static readonly UIEVENT_onIMAfterLoginComplete: string = "uievent.onIMAfterLoginComplete";
  /** UI事件：掉线了 */
  static readonly UIEVENT_onIMDisconnected: string = "uievent.onIMDisconnected";
  /** UI事件：心跳了一次（发出的） */
  static readonly UIEVENT_onIMPing: string = "uievent.onIMPing";
  /** UI事件：心跳了一次（收到的应答包） */
  static readonly UIEVENT_onIMPong: string = "uievent.onIMPong";
  /** UI事件：你被踢了 */
  static readonly UIEVENT_onKickout: string = "uievent.onKickout";

  /** UI事件：增加了聊天消息 */
  static readonly UIEVENT_messageAdded = 'uievent.messageAdded';
  /** UI事件：更新了聊天消息（单条聊天消息） */
  static readonly UIEVENT_messageUpdate = 'uievent.messageUpdate';
  /** UI事件：更新了聊天消息（多条聊天消息） */
  static readonly UIEVENT_messagesUpdate = 'uievent.messagesUpdate';
}