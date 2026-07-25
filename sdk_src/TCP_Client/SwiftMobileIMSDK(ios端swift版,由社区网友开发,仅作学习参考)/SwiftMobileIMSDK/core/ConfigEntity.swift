//
//  ConfigEntity.swift
//  SwiftMobileIMSDK
//
//  Created by fishbay on 2021/8/2.
//

import Foundation

class ConfigEntity {
    static var serverIP: String = "www.iconfidenceapp.com"
    static var serverPort: Int = 8901
    static var localSendAndListeningPort: Int = -1
    static var appKey: String?
    
    
    /// 设置appkey
    /// - Parameter key: appkey
    static func registerWithAppKey(key: String) {
        appKey = key
    }
    
    /// 设置服务器ip
    /// - Parameter serverIP: 服务器ip
    static func setServerIP(serverIP: String) {
        Self.serverIP = serverIP
    }
    static func getServerIP() -> String? {
        return Self.serverIP
    }
    
    /// 设置服务器端口
    /// - Parameter serverPort: 服务器端口
    static func setServerPort(serverPort: Int) {
        Self.serverPort = serverPort
    }
    static func getServerPort() -> Int? {
        return serverPort
    }
    
    /// 设置local的端口
    /// - Parameter lPort: local端口
    static func setLocalSendAndListeningPort(localPort: Int) {
        Self.localSendAndListeningPort = localPort
    }
    static func getLocalSendAndListeningPort() -> Int {
        return localSendAndListeningPort
    }
    
    /// 设置MobileIMSDK即时通讯核心框架预设的敏感度模式
    /// - Parameter mode: 模式
    static func setSenseMode(mode: SenseMode?) {
        var keepAliveInterval: Int = 0
        var networkConnectionTimeout: Int = 0
        
        switch mode {
        case .SenseMode3S:
            // 心跳间隔3秒
            keepAliveInterval = 3000
            // 5秒后未收到服务端心跳反馈即认为连接已断开（相当于连续2个心跳间隔(即算法最大6秒延迟后)后仍未收到服务端反馈）
            networkConnectionTimeout = keepAliveInterval + 2000
            break
        case .SenseMode5S:
            keepAliveInterval = 5000
            networkConnectionTimeout = keepAliveInterval + 3000
            break
        case .SenseMode10S:
            keepAliveInterval = 10000
            networkConnectionTimeout = keepAliveInterval + 5000
            break
        case .SenseMode15S:
            keepAliveInterval = 15000
            networkConnectionTimeout = keepAliveInterval + 5000
            break
        case .SenseMode30S:
            keepAliveInterval = 30000
            networkConnectionTimeout = keepAliveInterval + 5000
            break
        case .SenseMode60S:
            keepAliveInterval = 60000
            networkConnectionTimeout = keepAliveInterval + 5000
            break
        case .SenseMode120S:
            keepAliveInterval = 120000
            networkConnectionTimeout = keepAliveInterval + 5000
            break
            
        default: break
            
        }
        
        if keepAliveInterval > 0 {
            // 设置Kepp alive心跳间隔
            KeepAliveDaemon.setKeepAliveInterval(keepAliveInterval: keepAliveInterval)
        }
        
        if networkConnectionTimeout > 0 {
            // 设置与服务端掉线的超时时长
            KeepAliveDaemon.setNetworkConnectionTimeout(networkConnectionTimeout: networkConnectionTimeout)
        }
    }
}
