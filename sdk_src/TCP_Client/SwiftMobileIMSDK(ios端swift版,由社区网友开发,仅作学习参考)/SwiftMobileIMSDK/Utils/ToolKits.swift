//
//  ToolKits.swift
//  SwiftMobileIMSDK
//
//  Created by fishbay on 2021/7/28.

//  实用工具类

import UIKit

class ToolKits {
    
    /// 生成uuid
    /// - Returns: uuid
    static func generateUUID() -> String {
        return UUID.init().uuidString
    }

    /// 系统时间戳（单位：毫秒），浮点表示
    static func getTimeStampWithMillisecond() -> TimeInterval {
        let time = Date.init(timeIntervalSinceNow: 0)
        return time.timeIntervalSince1970 * 1000
    }
    
    /// 返回系统时间戳（单位：毫秒），Int表示
    static func getTimeStampWithMillisecondInt() -> Int {
        return Int(getTimeStampWithMillisecond())
    }
}


