//
//  LoginInfo.swift
//  SwiftMobileIMSDK
//
//  Created by fishbay on 2021/8/1.

//  登陆信息DTO类

import Foundation

class LoginInfo: HandyJSON {
    /// 登陆时提交的准一id，保证唯一就可以通信，可能是登陆用户名、也可能是任意不重复的id等，具体意义由业务层决定
    var loginUserId: String?
    /// 登陆时提交到服务端用于身份鉴别和合法性检查的token，它可能是登陆密码，也可能是通过前置单点登陆接口拿到的token等，具体意义由业务层决定
    var loginToken: String?
    /// 额外信息字符串。本字段目前为保留字段，供上层应用自行放置需要的内容
    var extra: String?
    /// 客户端首次登陆时间
    var firstLoginTime: Int = 0
    
    required init() {}
}
