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
 * 聊天消息类型常量定义。
 *
 * 代码参考自IM产品RainbowChat：http://www.52im.net/thread-19-1-1.html
 *
 * 特别注意：本类中定义的常量切勿重复，否则将影响聊天列表UI的正常展现！
 *
 * @author Jack Jiang(http://www.52im.net/thread-2792-1-1.html)
 * @since 2.0
 */
export default class MsgType {

  /** 聊天消息类型之：普通文字消息 */
  static readonly TYPE_TEXT: number         = 0;
  /** 聊天消息类型之：图片消息（即消息内容就是存放于服务端的磁盘图片文件名） */
  static readonly TYPE_IMAGE: number        = 1;
  /** 聊天消息类型之：语音留言消息（即消息内容就是存放于服务端的语音留言文件名） */
  static readonly TYPE_VOICE: number        = 2;

  /** 聊天消息类型之：系统消息或提示信息（此类消息通常由服务器即f="0"的用户发出）. */
  static readonly TYPE_SYSTEAM$INFO: number = 90;



  // others .......
}
