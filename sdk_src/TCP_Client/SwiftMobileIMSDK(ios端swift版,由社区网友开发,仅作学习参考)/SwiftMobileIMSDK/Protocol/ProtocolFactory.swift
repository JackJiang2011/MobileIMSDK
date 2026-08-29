//
//  ProtocolFactory.swift
//  SwiftMobileIMSDK
//
//  Created by fishbay on 2021/8/1.

//  MibileIMSDK框架的协议工厂类

import Foundation

class ProtocolFactory {
    
    /// 创建登陆信息
    /// - Parameter loginInfo: 登陆对象
    /// - Returns: 登陆的协议对象
    static func createLoginInfo(loginInfo: LoginInfo) -> Protocol {
        let message = Protocol()
        
        message.type = ProtocolType.clientLogin.rawValue
        message.dataContent = loginInfo.toJSONString(prettyPrint: true)
        message.from = loginInfo.loginUserId ?? "-1"
        message.to = "0"
        message.QoS = true
        message.fp = Protocol.getFingerPrint()
        
        return message
    }
    
    /// 创建退出登陆信息
    /// - Parameter userId: 用户id
    /// - Returns: 退出登陆的协议对象
    static func createLoginOutInfo(userId: String) -> Protocol {
        let message = Protocol()
        
        message.type = ProtocolType.clientLogout.rawValue
        message.from = userId
        message.to = "0"
        message.QoS = true
        message.fp = Protocol.getFingerPrint()
        
        return message
    }
    
    /// 创建通用信息
    /// - Parameters:
    ///   - dataContent: 数据内容
    ///   - fromUserId: 发送者
    ///   - toUserId: 接受者
    ///   - QoS: 质量保证
    ///   - fp: 指纹
    ///   - typeu: 应用层专用字段
    /// - Returns: 通用协议对象
    static func createCommonData(dataContent: String, fromUserId: String, toUserId: String, QoS: Bool?, fp: String?, typeu: Int?) -> Protocol {
        let message = Protocol()
        
        message.type = ProtocolType.clientCommonData.rawValue
        message.dataContent = dataContent
        message.from = fromUserId
        message.to = toUserId
        message.QoS = QoS ?? true
        message.fp = message.QoS ? Protocol.getFingerPrint() : nil
        message.typeu = typeu ?? -1
        
        return message
    }
    
    /// 创建接收反馈信息
    /// - Parameters:
    ///   - fromUserId: 发送者
    ///   - toUserId: 接收者
    ///   - QoS: 质量保证
    ///   - fp: 消息指纹
    ///   - bridge: 是否来自跨服器
    /// - Returns: 接收反馈的协议对象
    static func createReceivedBack(fromUserId: String?, toUserId: String?, QoS: Bool?, fp: String?, bridge: Bool?) -> Protocol {
        let message = Protocol()
        
        message.type = ProtocolType.clientReceived.rawValue
        message.from = fromUserId ?? "-1"
        message.to = toUserId ?? "-1"
        message.QoS = false
        message.fp = nil
        message.bridge = false
        message.typeu = -1
        
        return message
    }
    
    /// 创建心跳信息
    /// - Parameter fromUserId: 发送者
    /// - Returns: 心跳协议对象
    static func createKeepAlive(fromUserId: String) -> Protocol {
        let message = Protocol()
        
        message.type = ProtocolType.clientKeepAlive.rawValue
        message.dataContent = KeepAlive().toJSONString(prettyPrint: true)
        message.from = fromUserId
        message.to = "0"
        message.QoS = true
        message.fp = Protocol.getFingerPrint()
        
        return message
    }
    
    /// 创建被踢信息
    /// - Parameters:
    ///   - kickoutInfo: 被踢对象
    ///   - toUserId: 接收者
    /// - Returns: 被踢的协议对象
    static func createKickOut(kickoutInfo: KickoutInfo, toUserId: String) -> Protocol {
        let message = Protocol()
        
        message.type = ProtocolType.serverKickout.rawValue
        message.dataContent = kickoutInfo.toJSONString(prettyPrint: true)
        message.from = "0"
        message.to = toUserId
        message.QoS = true
        message.fp = Protocol.getFingerPrint()
        
        return message
    }
    
    /// 解析协议data数据
    /// - Parameter data: 协议data数据
    /// - Returns: 协议信息对象
    static func parse(data: Data?) -> Protocol? {
        if data == nil {
            CAPrint("待解析的数据是nil")
            return nil
        }
        
        let jsonString = String(data: data!, encoding: .utf8)
        if let message = Protocol.deserialize(from: jsonString) {
            return message
        }
        
        return nil
    }
    
    /// 解析登陆响应的json字符串数据
    /// - Parameter jsonString: 登陆响应json字符串
    /// - Returns: 登陆响应对象
    static func parseLoginInfoResponse(jsonString: String?) -> LoginInfoResponse? {
        if jsonString.isBlank {
            CAPrint("待解析的登陆响应数据是nil")
            return nil
        }
        
        if let loginInfoResponse = LoginInfoResponse.deserialize(from: jsonString) {
            return loginInfoResponse
        }
        
        return nil
    }
    
    /// 解析心跳响应的json字符串数据
    /// - Parameter jsonString: 心跳响应json字符串
    /// - Returns: 心跳响应对象
    static func parseKeepAliveResponse(jsonString: String?) -> KeepAliveResponse? {
        if jsonString.isBlank{
            CAPrint("待解析的心跳响应数据是nil")
            return nil
        }
        
        if let keepAliveResponse = KeepAliveResponse.deserialize(from: jsonString) {
            return keepAliveResponse
        }
        
        return nil
    }
    
    /// 解析错误响应的json字符串数据
    /// - Parameter jsonString: 错误响应json字符串
    /// - Returns: 错误响应对象
    static func parseErrorResponse(jsonString: String?) -> ErrorResponse? {
        if jsonString.isBlank {
            CAPrint("待解析的错误响应数据是nil")
            return nil
        }
        
        if let errorResponse = ErrorResponse.deserialize(from: jsonString) {
            return errorResponse
        }
        
        return nil
    }
    
    /// 解析被踢的json字符串数据
    /// - Parameter jsonString: 被踢的json字符串
    /// - Returns: 被踢对象
    static func parseKickoutInfo(jsonString: String?) -> KickoutInfo? {
        if jsonString.isBlank {
            CAPrint("待解析的被踢响应数据是nil")
            return nil
        }
        
        if let kickoutInfo = KickoutInfo.deserialize(from: jsonString) {
            return kickoutInfo
        }
        
        return nil
    }
}
