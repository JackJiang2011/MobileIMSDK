//
//  MessageQoSEvent.swift
//  SwiftMobileIMSDK
//
//  Created by fishbay on 2021/8/2.

//  消息送达相关事件（由QoS机制通知上来的）在此MessageQoSEvent子类中实现即可

import Foundation

class MessageQoSEvent: MessageQoSProtocol {
    
    /// 消息未送达的回调事件通知
    /// - Parameter lostMessage: 由MobileIMSDK QoS算法判定出来的未送达消息列表
    func messagesLost(lostMessage: [Any]?) {
        CAPrint("【DEBUG_UI】收到系统的未实时送达事件通知，当前共有\(lostMessage?.count ?? 0)个包QoS保证机制结束，判定为【无法实时送达】！")
        
        NotificationCenter.default.post(name: .MyNotificationUpdateChatList,
                                        object: MessageData(content: "[消息未成功送达]共\(lostMessage?.count ?? 0)条!(网络状况不佳或对方id不存在)", color: .magenta))
    }
    
    /// 消息已被对方收到的回调事件通知
    /// - Parameter fingerPrint: 消息的指纹特征码
    func messagesBeReceived(fp: String?) {
        if fp == nil {
            return
        }
        
        CAPrint("【DEBUG_UI】收到对方已收到消息事件的通知，fp=\(fp ?? "")")
        
        NotificationCenter.default.post(name: .MyNotificationUpdateChatList,
                                        object: MessageData(content: "[收到应答]\(fp!)", color: .blue))
    }
}
