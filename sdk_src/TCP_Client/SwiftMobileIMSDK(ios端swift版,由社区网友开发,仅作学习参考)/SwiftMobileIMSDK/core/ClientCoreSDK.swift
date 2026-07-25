//
//  ClientCoreSDK.swift
//  SwiftMobileIMSDK
//
//  Created by fishbay on 2021/8/1.
//

import Foundation

class ClientCoreSDK: HandyJSON {
    /// debug
    static var enableDebug = false
    /// 是否在登陆成功后掉线时自动重新登陆线程中实质性发起登陆请求
    static var autoReLogin = true
    
    /// 是否已成功连接到服务器（当然，前提是已成功发起过登陆请求后）
    var connectedToServer: Bool = false
    /// 当且仅当用户从登陆界面成功登陆后设置本字段为true，系统退出（登陆）时设置为false
    var loginHasInit: Bool = false
    /// 保存提交到服务端的用户登陆信息，可能是登陆用户名、任意不重复的id等，具体意义由业务层决定
    var currentLoginInfo: LoginInfo?
    /// 是否初始化
    var initial: Bool = false
    /// 网络状态检测类
    var internetReachability: Reachability?
    
    /// 事件回调
    var chatMessageEvent: ChatMessageEvent?
    var chatBaseEvent: ChatBaseEvent?
    var messageQoSEvent: MessageQoSEvent?
    
    /// 单例
    private static let instance: ClientCoreSDK = ClientCoreSDK()
    static func sharedInstance() -> ClientCoreSDK {
        return instance
    }
    required init() {}
    
    /// 设置调试
    /// - Parameter enableDebug: 调试标志
    static func setEnableDebug(enableDebug: Bool) {
        Self.enableDebug = enableDebug
    }
    static func isEnableDebug() -> Bool {
        return enableDebug
    }
    
    /// 设置自动重新登陆
    /// - Parameter autoReLogin: 自动重新登陆
    static func setAutoReLogin(autoReLogin: Bool) {
        Self.autoReLogin = autoReLogin
    }
    static func isAutoReLogin() -> Bool {
        return autoReLogin
    }
    
    /// 初始化核心库
    func initCore() {
        if self.initial {
            // 已初始化，直接返回
            return
        }
        
        self.connectedToServer = false
        self.loginHasInit = false
        
        if self.internetReachability == nil {
            do {
                let reachability = try Reachability()
                NotificationCenter.default.addObserver(self, selector: #selector(reachabilityChanged(notification:)), name: .reachabilityChanged, object: reachability)
                self.internetReachability = reachability
            } catch {
                CAPrint("ClientCoreSDK 创建Reachability对象失败：\(error)")
            }
        }
        
        do {
            try self.internetReachability!.startNotifier()
        } catch {
            CAPrint("ClientCoreSDK本地网络通知启动失败：\(error)")
        }
        
        self.initial = true
        
        CAPrint("ClientCoreSDK已经完成initCore了")
    }
    
    /// 监控网络状态变化
    /// - Parameter notification: 通知对象
    @objc func reachabilityChanged(notification: Notification) {
        
        let reachability = notification.object as! Reachability
        
        switch reachability.connection {
        case .unavailable:
            CAPrint("【IMCORE-TCP】【本地网络通知】检测本地网络连接断开了!")
            LocalSocketProvider.sharedInstance().closeLocalSocket()
            break
        case .wifi:
            CAPrint("【IMCORE-TCP】【本地网络通知】检测本地网络已连接上了! 【wifi】")
            if self.getLoginHasInit() ?? false {
                LocalSocketProvider.sharedInstance().closeLocalSocket()
            }
            
            break
        case .cellular:
            CAPrint("【IMCORE-TCP】【本地网络通知】检测本地网络已连接上了! 【Cellular】")
            if self.getLoginHasInit() ?? false {
                LocalSocketProvider.sharedInstance().closeLocalSocket()
            }
            break
        default:
            break
        }
    }
    
    /// 释放MobileIMSDK框架资源统一方法
    func releaseCore() {
        // 设置是否正常连接（登陆）到服务器的标识
        self.connectedToServer = false
        
        // 尝试停掉掉线重连线程（如果线程正在运行的话）
        AutoReLoginDaemon.sharedInstance().stop()
        
        // 尝试停掉QoS质量保证（发送）心跳线程
        QoS4SendDaemon.sharedInstance().stop()
        
        // 尝试停掉Keep Alive心跳线程
        KeepAliveDaemon.sharedInstance().stop()
        
        // 尝试停掉QoS质量保证（接收防重复机制）心跳线程
        QoS4ReceiveDaemon.sharedInstance().stop()
        
        // 尝试关闭本地Socket
        LocalSocketProvider.sharedInstance().closeLocalSocket()
        
        // 并清除QoS发送队列缓存：防止不退出APP时切换另一账号后qos的缓存队列未清空
        QoS4SendDaemon.sharedInstance().clear()
        
        // 并清除QoS接收队列缓存：防止不退出APP时切换另一账号后qos的缓存队列未清空
        QoS4ReceiveDaemon.sharedInstance().clear()
        
        // 取消注册网络事件的监听
        self.internetReachability?.stopNotifier()
        NotificationCenter.default.removeObserver(self, name: .reachabilityChanged, object: self.internetReachability)
        
        self.initial = false
        self.loginHasInit = false
    }
    
    /// 获取是否初始化状态
    /// - Returns: true - 已经出事后，false - 未初始化
    func isInitialed() -> Bool {
        return self.initial
    }
    
    /// 保存第一次登陆时间
    /// - Parameter firstLoginTime: 第一次登陆时间
    func saveFirstLoginTime(firstLoginTime: Int) {
        self.currentLoginInfo?.firstLoginTime = firstLoginTime
    }
    
    /// 网络是否连接
    /// - Returns: true - 已连接，false - 未连接
    func isInternetReachable() -> Bool {
        let netState = internetReachability?.connection
        return netState == .cellular || netState == .wifi
    }
    
    /// 是否登陆
    /// - Parameter loginHasInit: 是否登陆
    func setLoginHasInit(loginHasInit: Bool?) {
        self.loginHasInit = loginHasInit ?? false
    }
    func getLoginHasInit() -> Bool? {
        return self.loginHasInit
    }
    
    /// 设置当前登陆用户
    /// - Parameter loginInfo: 登陆用户信息
    func setCurrentLoginInfo(loginInfo: LoginInfo) {
        self.currentLoginInfo = loginInfo
    }
    func getCurrentLoginInfo() -> LoginInfo? {
        return self.currentLoginInfo
    }
    
    /// 设置基础事件回调
    /// - Parameter chatBaseEvent: 回调对象
    func setChatBaseEvent(chatBaseEvent: ChatBaseEvent) {
        self.chatBaseEvent = chatBaseEvent
    }
    func getChatBaseEvent() -> ChatBaseEvent? {
        return self.chatBaseEvent
    }
    
    /// 设置数据事件回调
    /// - Parameter chatMessageEvent: 回调对象
    func setChatMessageEvent(chatMessageEvent: ChatMessageEvent) {
        self.chatMessageEvent = chatMessageEvent
    }
    func getChatMessageEvent() -> ChatMessageEvent? {
        return self.chatMessageEvent
    }
    
    /// 设置质量事件回调
    /// - Parameter messageQoSEvent: 回调对象
    func setMessageQoSEvent(messageQoSEvent: MessageQoSEvent) {
        self.messageQoSEvent = messageQoSEvent
    }
    func getMessageQoSEvent() -> MessageQoSEvent? {
        return self.messageQoSEvent
    }
}

