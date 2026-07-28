//
//  ChatMessageProtocol.swift
//  SwiftMobileIMSDK
//
//  Created by fishbay on 2021/8/14.

//  MobileIMSDK的通用数据通信消息的回调事件接口

import Foundation

protocol ChatMessageProtocol {
    
    /// 收到普通消息的回调事件通知
    /// - Parameters:
    ///   - fp: 当该消息需要QoS支持时本回调参数为该消息的特征指纹码，否则为null
    ///   - userId: 消息的发送者id（RainbowCore框架中规定发送者id=“0”即表示是由服务端主动发过的，否则表示的是其它客户端发过来的消息）
    ///   - dataContent: 消息内容的文本表示形式
    ///   - typeu: 未定义
    func onRecieveMessage(fp: String?, userId: String?, dataContent: String?, typeu: Int?)
    
    /// 服务端反馈的出错信息回调事件通知
    /// - Parameters:
    ///   - errorCode: 错误码，定义在常量表 ErrorCode 中有关服务端错误码的定义
    ///   - errorMsg: 描述错误内容的文本信息
    func onErrorResponse(errorCode: ErrorCode?, errorMsg: String?)
}
