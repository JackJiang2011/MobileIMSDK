//
//  IMClientManager.swift
//  SwiftMobileIMSDK
//
//  Created by fishbay on 2021/8/14.
//

import Foundation

class IMClientManager {
    /// MobileIMSDK是否已被初始化. true表示已初化完成，否则未初始化
    var initital: Bool = false

    var baseEventListener: ChatBaseProtocol?
    var messageEventListener: ChatMessageProtocol?
    var messageQoSEventListener: MessageQoSProtocol?
    
    // 单例
    private static let instance = IMClientManager()
    static func sharedInstance() -> IMClientManager {
        return instance
    }
    private init() {
    }
    
    func initMobileSDK() {
        if !self.initital {
            // 设置appkey
            ConfigEntity.registerWithAppKey(key: "123456789")
            
            // MobileIMSDK核心IM框架的敏感度模式设置
            ConfigEntity.setSenseMode(mode: .SenseMode5S)
            
            // 开启DEBUG信息输出
            ClientCoreSDK.setEnableDebug(enableDebug: true)
            
            // 设置事件回调
            self.baseEventListener = ChatBaseEvent()
            ClientCoreSDK.sharedInstance().chatBaseEvent = self.baseEventListener as? ChatBaseEvent
            
            self.messageEventListener = ChatMessageEvent()
            ClientCoreSDK.sharedInstance().chatMessageEvent = self.messageEventListener as? ChatMessageEvent
            
            self.messageQoSEventListener = MessageQoSEvent()
            ClientCoreSDK.sharedInstance().messageQoSEvent = self.messageEventListener as? MessageQoSEvent
            
            self.initital = true
        }
    }
    
    func releaseMobileSDK() {
        ClientCoreSDK.sharedInstance().releaseCore()
        
        self.initital = false
    }
    
    
}
