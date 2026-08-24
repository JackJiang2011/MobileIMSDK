//
//  KickoutInfo.swift
//  SwiftMobileIMSDK
//
//  Created by fishbay on 2021/8/1.

//  向客户端发出的“被踢”指令包内容的DTO类

import Foundation

class KickoutInfo: HandyJSON {
    /// 被踢原因描述
    var code: KickoutCodeType = .unknown
    /// 被踢原因编码
    var reason: String?
    
    required init() {}
}
