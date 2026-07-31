//
//  Notification+Extension.swift
//  SwiftMobileIMSDK
//
//  Created by fishbay on 2021/8/22.
//

import Foundation

extension Notification.Name {
    // 更新对话消息
    static let MyNotificationUpdateChatList = Notification.Name(rawValue: "UpdateChatList")
    
    // 刷新网络连接状态
    static let MyNotificationRefreshConnectStatus = Notification.Name(rawValue: "RefreshConnectStatus")
    
    // 被踢了
    static let MyNotificationKickout = Notification.Name(rawValue: "Kickout")
}
