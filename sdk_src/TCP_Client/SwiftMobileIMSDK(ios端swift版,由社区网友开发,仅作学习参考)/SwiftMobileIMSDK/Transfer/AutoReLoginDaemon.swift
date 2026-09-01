//
//  AutoReLoginDaemon.swift
//  SwiftMobileIMSDK
//
//  Created by fishbay on 2021/8/2.

//  与服务端通信中断后的自动登陆（重连）独立线程

import Foundation

class AutoReLoginDaemon {
    
    /// 自动重新登陆时间间隔（单位：毫秒），默认2000毫秒
    static var autoReLoginInterval: Int = 2000
    
    /// 当前心跳线程是否正在执行中
    var autoReLoginRunning: Bool = false
    
    var executing: Bool = false
    var timer: Timer?
    
    /// 本属性仅作DEBUG之用：DEBUG事件观察者
    var debugObserver: ObserverCompletion?
    
    /// 单例
    private static let instance = AutoReLoginDaemon()
    static func sharedInstance() -> AutoReLoginDaemon {
        return instance
    }
    /// 禁止外部通过构造方法创建对象
    private init() {
        CAPrint("AutoReLoginDaemon已经init了")
    }
    
    /// 设置自动重新登陆的间隔时间
    /// - Parameter autoReLoginInterval: 间隔时间
    static func setAutoReLoginInterval(autoReLoginInterval: Int) {
        Self.autoReLoginInterval = autoReLoginInterval
    }
    static func getAutoReLoginInterval() -> Int {
        return autoReLoginInterval
    }
    
    @objc func run() {
        if self.executing {
            return
        }
        
        self.executing = true
        
        let isAutoReLogin = ClientCoreSDK.isAutoReLogin()
        let isSocketReady = LocalSocketProvider.sharedInstance().isLocalSocketReady()
        
        if ClientCoreSDK.isEnableDebug() {
            CAPrint("【IMCORE-TCP】自动重新登陆线程执行中, autoReLogin ? \(isAutoReLogin), socketReady ? \(isSocketReady) ...")
        }
        
        var code: ErrorCode = .unknown
        
        // 是否允许自动重新登陆哦
        if isAutoReLogin {
            code = LocalDataSender.sharedInstance().sendLogin(loginInfo: ClientCoreSDK.sharedInstance().getCurrentLoginInfo())
            
            // for DEBUG
            self.debugObserver?(nil, 2)
        }
        
        if code == .commonCodeOK {
            if ClientCoreSDK.isEnableDebug() {
                CAPrint("【IMCORE-TCP】自动重新登陆数据包已发出")
            }
        }
        
        self.executing = false
    }
    
    func stop() {
        if self.timer != nil {
            if self.timer!.isValid {
                self.timer!.invalidate()
            }
            
            self.timer = nil
        }
        
        self.autoReLoginRunning = false
        
        // for DEBUG
        self.debugObserver?(nil, 0)
    }
    
    func start(immediately: Bool) {
        self.stop()
        
        // 执行延迟的单位是秒
        self.timer = Timer.scheduledTimer(timeInterval: TimeInterval(Self.autoReLoginInterval / 1000),
                                          target: self,
                                          selector: #selector(run),
                                          userInfo: nil,
                                          repeats: true)
        // 如果需要立即执行
        if immediately {
            self.timer?.fire()
        }
        
        self.autoReLoginRunning = true
        
        // for DEBUG
        self.debugObserver?(nil, 1)

    }
    
    /// 设置debug回调
    func setDebugObserver(debugObserver: @escaping ObserverCompletion) {
        self.debugObserver = debugObserver
    }
    
    func isAutoReLoginRunning() -> Bool {
        return self.autoReLoginRunning
    }
    
}
