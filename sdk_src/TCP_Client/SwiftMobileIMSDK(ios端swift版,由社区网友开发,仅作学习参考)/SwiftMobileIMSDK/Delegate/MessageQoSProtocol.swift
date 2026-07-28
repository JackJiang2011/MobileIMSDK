//
//  MessageQoSProtocol.swift
//  SwiftMobileIMSDK
//
//  Created by fishbay on 2021/8/14.

//  MobileIMSDK的QoS质量保证机制的回调事件接口

import Foundation

protocol MessageQoSProtocol {
    
    /// 消息未送达的回调事件通知
    /// - Parameter lostMessage: 由MobileIMSDK QoS算法判定出来的未送达消息列表
    func messagesLost(lostMessage: [Any]?)
    
    /// 消息已被对方收到的回调事件通知
    /// - Parameter fingerPrint: 消息的指纹特征码
    func messagesBeReceived(fp: String?)
}
