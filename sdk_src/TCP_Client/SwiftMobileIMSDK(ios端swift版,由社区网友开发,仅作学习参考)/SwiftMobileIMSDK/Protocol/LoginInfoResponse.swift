//
//  LoginInfoResponse.swift
//  SwiftMobileIMSDK
//
//  Created by fishbay on 2021/8/1.

//  服务端反馈的用户登陆结果数据封装类

import Foundation

class LoginInfoResponse: HandyJSON {
    /// 错误码：0表示认证成功，否则是用户自定的错误码（该码应该是>1024的整数）
    var code: Int = 0
    /// 客户端首次登陆时间
    var firstLoginTime: Int = 0
    
    required init() {}
}
