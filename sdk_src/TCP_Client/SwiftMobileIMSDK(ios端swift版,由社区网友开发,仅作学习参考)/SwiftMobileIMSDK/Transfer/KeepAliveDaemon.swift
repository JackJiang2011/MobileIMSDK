//
//  KeepAliveDaemon.swift
//  SwiftMobileIMSDK
//
//  Created by fishbay on 2021/8/8.

//  用于保持与服务端通信活性的Keep alive独立线程

import Foundation

class KeepAliveDaemon {
    /// keep Alive 心跳发送时间间隔（单位：毫秒），默认15000秒（即15秒）
    static var keepAliveInterval: Int = 15000
    /// 收到服务端响应心跳包的超时时间（单位：毫秒），默认（15 * 1000 + 5000）＝ 20000 毫秒（即20秒）
    static var networkConnectionTimeout: Int = 20000
    /// 心跳包超时检查定时器的运行间隔时间（单位：毫秒），默认（2 * 1000）＝ 2000 毫秒（即2秒）
    static var networkConnectionTimeoutCheckInterval: Int = 2000
    
    /// 当前心跳线程是否正在执行中
    var keepAliveRunning: Bool = false
    /// 记录最近一次服务端的心跳响应包时间，时间上毫秒
    var lastGetKeepAliveResponseFromServerTimstamp: Int = 0
    /// 网络断开事件观察者
    var networkConnectionLostObserver: ObserverCompletion?
    /// 本属性仅作DEBUG之用：DEBUG事件观察者
    var debugObserver: ObserverCompletion?
    /// 心跳包发送定时器的本次执行是否还未完成（具体指的是一次完整的心跳发送逻辑的执行）
    var keepAliveTaskExecuting: Bool = false
    /// 心跳包发送定时器
    var keepAliveTimer: Timer?
    /// 心跳包响应超时检查的独立定时器
    var keepAliveTimeoutTimer: Timer?
    
    /// 单例
    private static let instance: KeepAliveDaemon = KeepAliveDaemon()
    static func sharedInstance() -> KeepAliveDaemon {
        return instance
    }
    private init() {
        CAPrint("KeepAliveDaemon已经init了")
    }
    
    /// 设置心跳间隔
    /// - Parameter keepAliveInterval: 心跳间隔
    static func setKeepAliveInterval(keepAliveInterval: Int) {
        Self.keepAliveInterval = keepAliveInterval
    }
    static func getKeepAliveInterval() -> Int {
        return Self.keepAliveInterval
    }
    
    /// 设置服务端响应心跳包的超时时间
    static func setNetworkConnectionTimeout(networkConnectionTimeout: Int) {
        Self.networkConnectionTimeout = networkConnectionTimeout
    }
    static func getNetworkConnectionTimeout() -> Int {
        return Self.networkConnectionTimeout
    }
    
    /// 发送心跳包
    @objc func doKeepAlive() {
        // 极端情况下本次循环内可能执行时间超过了时间间隔，此处是防止在前一次还没有运行完的情况下又重复过劲行，从而出现无法预知的错误
        if self.keepAliveTaskExecuting {
            return
        }
        
        self.keepAliveTaskExecuting = true
        
        if ClientCoreSDK.isEnableDebug() {
            CAPrint("【IMCORE-TCP】心跳包[发送]线程执行中...")
        }
        
        // 发送心跳包
        let result = LocalDataSender.sharedInstance().sendKeepAlive()
        if ClientCoreSDK.isEnableDebug() {
            CAPrint("【IMCORE-TCP】心跳包发送完成：\(result)")
        }
        
        self.debugObserver?(nil, 2)
        
        // 首先执行Keep Alive心跳包时，把此时的时间作为第1次收到服务响应的时间（初始化）
        if self.isInitialedForKeepAlive() {
            self.lastGetKeepAliveResponseFromServerTimstamp = ToolKits.getTimeStampWithMillisecondInt()
        }
        
        self.keepAliveTaskExecuting = false
    }
    
    /// 心跳包响应超时检查
    @objc func doTimeoutCheck() {
        if ClientCoreSDK.isEnableDebug() {
            CAPrint("【IMCORE-TCP】心跳[超时检查]线程执行中...")
        }
        
        // 首先执行Keep Alive心跳包时，把此时的时间作为第1次收到服务响应的时间（初始化）
        if !isInitialedForKeepAlive() {
            let now = ToolKits.getTimeStampWithMillisecondInt()
            // 当当前时间与最近一次服务端的心跳响应包时间间隔>= 20秒就判定当前与服务端的网络连接已断开
            if now - self.lastGetKeepAliveResponseFromServerTimstamp >= Self.networkConnectionTimeout {
                self.notifyConnectionLost()
            }
        }
    }
    
    /// 是否是首先执行Keep Alive心跳包发送
    /// - Returns: true - 是，false - 不是
    func isInitialedForKeepAlive() -> Bool {
        return self.lastGetKeepAliveResponseFromServerTimstamp == 0
    }
    
    /// 心跳线程算法已判定需要与服务器的“通信通道”断开，调用此方法将进入框架的“通信通道”断开处理逻辑
    func notifyConnectionLost() {
        // 先停止心跳线程
        self.stop()
        
        // 再通知“网络连接已断开”
        self.networkConnectionLostObserver?(nil, nil)
    }
    
    /// 无条件中断本线程的运行
    func stop() {
        // 关闭心跳超时检查定时器
        if self.keepAliveTimeoutTimer != nil {
            if self.keepAliveTimeoutTimer!.isValid {
                self.keepAliveTimeoutTimer!.invalidate()
            }
            
            self.keepAliveTimeoutTimer = nil
        }
        
        // 关闭心跳包发送定时器
        if self.keepAliveTimer != nil {
            if self.keepAliveTimer!.isValid {
                self.keepAliveTimer!.invalidate()
            }
            
            self.keepAliveTimer = nil
        }
        
        self.keepAliveRunning = false
        self.lastGetKeepAliveResponseFromServerTimstamp = 0
        
        // debug
        self.debugObserver?(nil, 0)
    }
    
    /// 启动线程
    /// - Parameter immediately:  true表示立即执行线程作业，否则直到执行间隔的到来才进行首次作业的执行
    func start(immediately: Bool) {
        self.stop()
        
        // 启动心跳包发送定时器
        self.keepAliveTimer = Timer.scheduledTimer(timeInterval: TimeInterval(Self.keepAliveInterval / 1000),
                                                   target: self,
                                                   selector: #selector(doKeepAlive),
                                                   userInfo: nil,
                                                   repeats: true)
        
        // 如果需要立即执行
        if immediately {
            self.keepAliveTimer!.fire()
        }
        
        // 启动心跳超时检查定时器
        self.keepAliveTimeoutTimer = Timer.scheduledTimer(timeInterval: TimeInterval(Self.networkConnectionTimeoutCheckInterval / 1000),
                                                          target: self,
                                                          selector: #selector(doTimeoutCheck),
                                                          userInfo: nil,
                                                          repeats: true)
        
        // 如果需要立即执行
        if immediately {
            self.keepAliveTimeoutTimer!.fire()
        }
        
        self.keepAliveRunning = true
        
        // debug
        self.debugObserver?(nil, 1)
    }
    
    /// 线程是否正在运行中
    /// - Returns: true表示是，否则线路处于停止状态
    func isKeepAliveRunning() -> Bool {
        return self.keepAliveRunning
    }
    
    /// 收到服务端反馈的心跳包时调用此方法：作用是更新服务端最背后的响应时间戳
    func updateLastGetKeepAliveResponseFromServerTimstamp() {
        self.lastGetKeepAliveResponseFromServerTimstamp = ToolKits.getTimeStampWithMillisecondInt()
    }
    
    /// 设置网络断开事件观察者
    /// - Parameter networkConnectionLostObserver: 网络事件观察者
    func setNetworkConnectionLostObserver(networkConnectionLostObserver: ObserverCompletion?) {
        self.networkConnectionLostObserver = networkConnectionLostObserver
    }
    
    /// Just for DEBUG
    func setDebugObserver(debugObserver: ObserverCompletion?) {
        self.debugObserver = debugObserver
    }
}
