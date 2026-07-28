//
//  ChatBaseProtocol.swift
//  SwiftMobileIMSDK
//
//  Created by fishbay on 2021/8/14.

//  MobileIMSDK的基础通信消息的回调事件接口

import Foundation

protocol ChatBaseProtocol {
    
    /// 本地用户的登陆结果回调事件通知
    /// - Parameter errorCode: 服务端反馈的登录结果：0 表示登陆成功，否则为服务端自定义的出错代码（按照约定通常为>=1025的数）
    func onLoginResponse(errorCode: Int?)
    
    /// 与服务端的通信断开的回调事件通知
    /// - Parameter errorCode: 本回调参数表示表示连接断开的原因，目前错误码没有太多意义，仅作保留字段，目前通常为-1
    func onLinkClose(errorCode: Int?)
    
    /// 本的用户被服务端踢出的回调事件通知
    /// - Parameter kickoutInfo: 被踢信息对象
    func onKickout(kickoutInfo: KickoutInfo?)
}
