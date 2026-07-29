//
//  ChatMessageEvent.swift
//  SwiftMobileIMSDK
//
//  Created by fishbay on 2021/8/2.

//  与IM服务器的数据交互事件在此ChatTransDataEvent子类中实现即可

import Foundation

class ChatMessageEvent: ChatMessageProtocol {
    
    /// 收到普通消息的回调事件通知
    /// - Parameters:
    ///   - fp: 当该消息需要QoS支持时本回调参数为该消息的特征指纹码，否则为null
    ///   - userId: 消息的发送者id（RainbowCore框架中规定发送者id=“0”即表示是由服务端主动发过的，否则表示的是其它客户端发过来的消息）
    ///   - dataContent: 消息内容的文本表示形式
    ///   - typeu: 未定义
    func onRecieveMessage(fp: String?, userId: String?, dataContent: String?, typeu: Int?) {
        CAPrint("【DEBUG_UI】\(typeu ?? 0)收到来自用户\(userId ?? "")的消息：\(dataContent ?? "")")
        
        NotificationCenter.default.post(name: .MyNotificationUpdateChatList, object: MessageData(content: "\(userId ?? "")说\(dataContent ?? "")", color: .black))
    }
    
    /// 服务端反馈的出错信息回调事件通知
    /// - Parameters:
    ///   - errorCode: 错误码，定义在常量表 ErrorCode 中有关服务端错误码的定义
    ///   - errorMsg: 描述错误内容的文本信息
    func onErrorResponse(errorCode: ErrorCode?, errorMsg: String?) {
        CAPrint("【DEBUG_UI】收到服务端错误消息，errorCode=\(errorCode ?? .unknown), errorMsg=\(errorMsg ?? "")")
        
        if errorCode == ErrorCode.noLoginFromResponse {
            NotificationCenter.default.post(name: .MyNotificationUpdateChatList, object: MessageData(content: "服务端会话已失效，自动登陆/重连启动!\(errorCode!)", color: .magenta))
        } else {
            NotificationCenter.default.post(name: .MyNotificationUpdateChatList, object: MessageData(content: "Server反馈错误码：\(errorCode!),errorMsg=\(errorMsg!)", color: .red))
        }
    }
}
