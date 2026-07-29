//
//  ChatBaseEvent.swift
//  SwiftMobileIMSDK
//
//  Created by fishbay on 2021/8/2.

//  与IM服务器的连接事件在此ChatBaseEvent子类中实现即可

import Foundation

class ChatBaseEvent: ChatBaseProtocol {
    
    /// 本Observer目前仅用于登陆时（因为登陆与收到服务端的登陆验证结果是异步的，所以有此观察者来完成收到验证后的处理）
    var loginOkForLaunchObserver: ObserverCompletion?
    
    /// 本地用户的登陆结果回调事件通知
    /// - Parameter errorCode: 服务端反馈的登录结果：0 表示登陆成功，否则为服务端自定义的出错代码（按照约定通常为>=1025的数）
    func onLoginResponse(errorCode: Int?) {
        if errorCode == nil {
            return
        }
        
        if errorCode == ErrorCode.commonCodeOK.rawValue {
            CAPrint("【DEBUG_UI】IM服务器登录/连接成功！")
            
            NotificationCenter.default.post(name: .MyNotificationRefreshConnectStatus, object: nil)
            NotificationCenter.default.post(name: .MyNotificationUpdateChatList, object: MessageData(content: "登录成功", color: .green))
            
        } else {
            CAPrint("【DEBUG_UI】IM服务器登录/连接失败，错误代码：\(errorCode!)")
            
            NotificationCenter.default.post(name: .MyNotificationUpdateChatList, object: MessageData(content: "IM服务器登录/连接失败,code=\(errorCode!)", color: .red))
        }
        
        /// 此观察者只有开启程序首次使用登陆界面时有用
        self.loginOkForLaunchObserver?(nil, errorCode)
    }
    
    /// 与服务端的通信断开的回调事件通知
    /// - Parameter errorCode: 本回调参数表示表示连接断开的原因，目前错误码没有太多意义，仅作保留字段，目前通常为-1
    func onLinkClose(errorCode: Int?) {
        if errorCode == nil {
            return
        }
        
        CAPrint("【DEBUG_UI】与IM服务器的网络连接出错关闭了，error：\(errorCode!)")
        
        NotificationCenter.default.post(name: .MyNotificationRefreshConnectStatus, object: nil)
        NotificationCenter.default.post(name: .MyNotificationUpdateChatList, object: MessageData(content: "与IM服务器的连接已断开!\(errorCode!)", color: .green))
    }
    
    /// 本的用户被服务端踢出的回调事件通知
    /// - Parameter kickoutInfo: 被踢信息对象
    func onKickout(kickoutInfo: KickoutInfo?) {
        if kickoutInfo == nil {
            return
        }
        
        CAPrint("【DEBUG_UI】已收到服务端的\"被踢\"指令，kickoutInfo.code：\(kickoutInfo!.code)")
        
        var alertContent: String
        if kickoutInfo!.code == .duplicateLogin {
            alertContent = "账号已在其它地方登陆，当前会话已断开，请退出后重新登陆！"
        } else if kickoutInfo!.code == .adminKickout {
            alertContent = "已被管理员强行踢出聊天，当前会话已断开！"
        } else {
            alertContent = "你已被踢出聊天，当前会话已断开 kickoutReason：\(kickoutInfo!.reason!)"
        }
        
        NotificationCenter.default.post(name: .MyNotificationUpdateChatList, object: MessageData(content: alertContent, color: .red))
        
        // 跳出一个Alert提示
        PopupDialogUtils.showDialog(target: SceneDelegate.shared!.window!.rootViewController!, title: "你被踢了", subtitle: "", successTitle: "知道了") {
            NotificationCenter.default.post(name: .MyNotificationKickout, object: nil)
        }
    }
    
    func setLoginOkForLaunchObserver(loginOkForLaunchObserver: ObserverCompletion?) {
        self.loginOkForLaunchObserver = loginOkForLaunchObserver
    }
}
