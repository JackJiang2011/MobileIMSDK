//
//  ErrorResponse.swift
//  SwiftMobileIMSDK
//
//  Created by fishbay on 2021/8/1.

//  错误信息DTO类

import Foundation

class ErrorResponse: HandyJSON {
    var errorCode: ErrorCode = .unknown
    var errorDesc: String?
    
    required init() {}
}
