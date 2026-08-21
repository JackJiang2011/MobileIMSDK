//
//  KickoutCodeType.swift
//  SwiftMobileIMSDK
//
//  Created by fishbay on 2021/8/11.

//  被踢错误码

import Foundation

enum KickoutCodeType: Int {
    /// 未知
    case unknown = -1
    /// 重复登陆被踢
    case duplicateLogin = 1
    /// 被管理员强行踢出
    case adminKickout = 2
}
