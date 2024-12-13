/*
 * Copyright (C) 2024  即时通讯网(52im.net) & Jack Jiang.
 * The MobileIMSDK-鸿蒙Next客户端 Project. All rights reserved.
 *
 * 联系邮件：jack.jiang@52im.net
 * 联系微信：hellojackjiang
 * 联系QQ： 413980957
 * 技术社区：http://www.52im.net
 */

import IMClientManager from "../../IMClientManager";
import { BasicDataSource } from "../common/LazyDataSource";
import Message from "./Message";

/**
 * 聊天界面（ChatPage.ets）中的List列表对应的数据源实现类（负责聊天消息数据的UI显示）。
 *
 * 注意：本类中的数据来源为全局消息缓存管理类 MessageProvider 中缓存消息集合的对象引用（浅拷贝），相当于共用同一个缓存，无需单
 * 独维护数据，实现了聊天消息数据的管理（MessageProvider负责）和UI界面显示（MessagesDataSource负责）的解偶。
 *
 * @author JackJang
 * @since 1.0
 */
export default class MessagesDataSource extends BasicDataSource<Message> {

  /** 引用全局消息缓存管理类 MessageProvider 中缓存消息集合（浅拷贝）*/
  private messages: Array<Message> = IMClientManager.getInstance().getMessageProvider().getMessages();

  notifyDataReload(): void {
    super.notifyDataReload();
  }

  totalCount(): number {
    return this.messages.length
  }

  getData(index: number): Message {
    return this.messages[index]
  }

  /**
   * 在List中使用LazyForEach时，响应式ui需要key变化才会更新，本方法就是按可变的内容计算key，从而在DataSource更新时，
   * 能让ui感知到，不然UI是不会刷新显示的。
   *
   * @param m 消息对象
   * @returns 计算出的key
   */
  static messageItemKey(m: Message) {
    // 优化点：给更新的消息对象加个最近更新时间戳，这应该是个更通用的key计算项，不然一旦变更内容多了，这个key的计算就不那么优雅了
    return m.msgType + '-' + m.fingerPrintOfProtocal + '-' + m.sendStatus;
  }
}