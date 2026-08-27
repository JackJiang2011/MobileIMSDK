//
//  Protocol.swift
//  SwiftMobileIMSDK
//
//  Created by fishbay on 2021/8/1.

//  协议报文对象

import Foundation

class Protocol: HandyJSON {
    /// 是否来自跨服务器的消息：true表示是、否则不是。本字段是为跨服务器或集群准备的
    var bridge: Bool = false
    
    /// 协议类型
    var type: Int?
    
    /// 协议数据内容
    var dataContent: String?
    
    /// 消息发出方的id（当用户登陆时，此值可不设置），为“-1”表示未设定、为“0”表示来自Server
    var from: String = "-1"
    
    /// 消息接收方的id（当用户退出时，此值可不设置），为“-1”表示未设定、为“0”表示发给Server
    var to: String = "-1"
    
    /// true表示本包需要进行QoS质量保证，否则不需要
    var QoS: Bool = false
    
    /// 用于QoS消息包的质量保证时作为消息的指纹特征码
    var fp: String?
    
    /// 应用层专用字段——用于应用层存放聊天、推送等场景下的消息类型，值为-1时表示未定义
    var typeu: Int = -1
    
    /// 本字段仅用于客户端QoS时：表示丢包重试次数。即本字段不应被传给消息接收者
    private var retryCount: Int = 0
    
    required init() {}
    
    /// 重试次数+1
    func increaseRetryCount() {
        self.retryCount += 1
    }
    func getRetryCount() -> Int {
        return self.retryCount
    }
    
    /// 获取消息的data数据
    /// - Returns: data数据
    func toBytes() -> Data? {
        if self.toJSONString() == nil {
            CAPrint("被clone对象的json字符串是nil")
            return nil
        }
        
        let jsonString = self.toJSONString(prettyPrint: true)!
        let data = jsonString.data(using: .utf8)
        
        return data
    }
    
    /// 克隆本对象
    /// - Returns: 新的对象
    func clone() -> Protocol? {
        if self.toJSONString() == nil {
            CAPrint("被clone对象的json字符串是nil")
            return nil
        }
        
        let jsonString = self.toJSONString(prettyPrint: true)!
        return Protocol.deserialize(from: jsonString)
    }
    
    /// 返回QoS需要的消息包的指纹特征码
    /// - Returns: 指纹
    static func getFingerPrint() -> String {
        return ToolKits.generateUUID()
    }
    
}
