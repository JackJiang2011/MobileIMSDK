//
//  ProtocolType.swift
//  Confidence
//
//  Created by fishbay on 2021/7/31.

//  客户端协议类型

import Foundation

/// 协议类型
enum ProtocolType: Int, HandyJSONEnum {
    /// 未知
    case unknown = -1
    /// 客户端登陆
    case clientLogin = 0
    /// 客户端心跳包
    case clientKeepAlive = 1
    /// 客户端发送通用数据
    case clientCommonData = 2
    /// 客户端退出登陆
    case clientLogout = 3
    /// 客户端QoS保证机制中的消息应答包
    case clientReceived = 4
    /// 客户端C2S时的回显指令（此指令目前仅用于测试时）
    case clientEcho = 5
    
    
    /// 服务端响应客户端的登陆
    case serverLogin = 50
    /// 服务端响应客户端的心跳包
    case serverKeepAlive = 51
    /// 服务端反馈给客户端的错误信息
    case serverError = 52
    /// 服务端反馈回显指令给客户端
    case serverEcho = 53
    /// 服务端向客户端发出“被踢”指令
    case serverKickout = 54
}




