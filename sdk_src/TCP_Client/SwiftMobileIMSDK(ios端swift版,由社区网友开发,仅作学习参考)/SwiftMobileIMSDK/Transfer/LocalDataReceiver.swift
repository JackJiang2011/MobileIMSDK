//
//  LocalDataReceiver.swift
//  SwiftMobileIMSDK
//
//  Created by fishbay on 2021/8/7.

//  数据接收辅助处理类

import Foundation

class LocalDataReceiver {
    
    /// 单例
    private static let instance = LocalDataReceiver()
    static func sharedInstance() -> LocalDataReceiver {
        return instance
    }
    private init() {
        CAPrint("LocalDataReceiver已经init了")
    }
    
    /// 解析收到的原始消息数据并按照MobileIMSDK定义的协议进行调度和处理
    /// - Parameter data: 收到的MobileIMSDK框架原始通信报文数据内容
    func handleProtocol(data: Data?) {
        if data == nil {
            return
        }
        
        let message = ProtocolFactory.parse(data: data!)
        if message == nil {
            return
        }
        
        // 如果该消息是需要QoS支持的包
        if message!.QoS {
            let loginResponse = ProtocolFactory.parseLoginInfoResponse(jsonString: message!.dataContent)
            let responseCode = loginResponse?.code ?? -1
            
            // 当服务端认证接口返回非0的code时，客记端会进入自动登陆尝试死循环
            if (message!.type == ProtocolType.serverLogin.rawValue) && (responseCode != 0) {
                if ClientCoreSDK.isEnableDebug() {
                    CAPrint("【IMCORE-TCP】【BugFIX】这是服务端的登陆返回响应包，且服务端判定登陆失败(即code!=0)，本次无需发送ACK应答包")
                }
            } else {
                // 且已经存在于接收列表中（及意味着可能是之前发给对方的应答包因网络或其它情况丢了，对方又因QoS机制重新发过来了）
                if QoS4ReceiveDaemon.sharedInstance().hasReceived(fp: message!.fp) {
                    if ClientCoreSDK.isEnableDebug() {
                        CAPrint("【IMCORE-TCP】【QoS机制】\(message!.fp ?? ""), 已经存在于发送列表中，这是重复包，通知应用层收到该包罗")
                    }
                    
                    // 【【C2C、C2S、S2C模式下的QoS机制2/4步：将收到的包存入QoS接收方暂存队列中（用于防重复）】】
                    QoS4ReceiveDaemon.sharedInstance().addReceived(message: message)
                    
                    // 【【C2C、C2S、S2C模式下的QoS机制3/4步：给发送者回一个“收到”应答包】】
                    self.sendRecievedBack(message: message)
                    
                    // 此包重复，不需要通知应用层收到该包了，直接返回
                    return
                }
                
                // 【【C2C、C2S、S2C模式下的QoS机制2/4步：将收到的包存入QoS接收方暂存队列中（用于防重复）】】
                QoS4ReceiveDaemon.sharedInstance().addReceived(message: message)
                
                // 【【C2C、C2S、S2C模式下的QoS机制3/4步：给发送者回一个“收到”应答包】】
                self.sendRecievedBack(message: message)
            }
        }
        
        let type = ProtocolType(rawValue: message!.type ?? -1)
        
        switch type {
        case .clientCommonData:
            self.onReceivedCommonData(message: message!)
            break
        case .serverKeepAlive:
            self.onServerResponseKeepAlive()
            break
        case .clientReceived:
            self.onMessageReceivedACK(message: message!)
            break
        case .serverLogin:
            self.onServerResponseLogin(message: message!)
            break
        case .serverError:
            self.onServerResponseError(message: message!)
            break
        case .serverKickout:
            self.onKickout(message: message!)
            break
        default:
            CAPrint("【IMCORE-TCP】收到的服务端消息类型：\(type ?? .unknown)，但目前该类型客户端不支持解析和处理")
            break
        }
    }
    
    /// 收到通用数据时的处理逻辑
    /// - Parameter message: 原始数据包
    func onReceivedCommonData(message: Protocol?) {
        if message == nil {
            return
        }
        
        // 收到通用数据的回调
        ClientCoreSDK.sharedInstance().chatMessageEvent?.onRecieveMessage(fp: message!.fp, userId: message!.from, dataContent: message!.dataContent, typeu: message!.typeu)
    }
    
    /// 收到服务端的心跳响应包时的处理逻辑
    func onServerResponseKeepAlive() {
        if ClientCoreSDK.isEnableDebug() {
            CAPrint("【IMCORE-TCP】收到服务端回过来的Keep Alive心跳响应包")
        }
        
        // 更新服务端的最新响应时间（该时间将作为计算网络是否断开的依据）
        KeepAliveDaemon.sharedInstance().updateLastGetKeepAliveResponseFromServerTimstamp()
    }
    
    /// 收到ACK消息应答时的处理逻辑
    /// - Parameter message: 原始数据包
    func onMessageReceivedACK(message: Protocol?) {
        if message == nil {
            return
        }
        
        // 应答包的消息内容即为之前收到包的指纹id
        let fp = message!.fp ?? "nil"
        
        if ClientCoreSDK.isEnableDebug() {
            CAPrint("【IMCORE-TCP】【QoS】收到\(message!.from)发过来的指纹为\(fp)的应答包")
        }
        
        // 将收到的应答事件通知事件处理者
        ClientCoreSDK.sharedInstance().messageQoSEvent?.messagesBeReceived(fp: fp)
        
        // 【【C2C或C2S模式下的QoS机制4/4步：收到应答包时将包从发送QoS队列中删除】】
        QoS4SendDaemon.sharedInstance().remove(fp: fp)
    }
    
    /// 收到登陆响应包的处理逻辑
    /// - Parameter message: 原始数据包
    func onServerResponseLogin(message: Protocol?) {
        if message == nil {
            return
        }
        
        // 解析服务端反馈过来的登陆消息
        let loginInfo = ProtocolFactory.parseLoginInfoResponse(jsonString: message!.dataContent)
        if loginInfo == nil {
            return
        }
        
        // 登陆成功了
        if loginInfo!.code == 0 {
            // loginHasInit字段未设置时即表示这是首次"成功登陆"
            if !ClientCoreSDK.sharedInstance().getLoginHasInit()! {
                ClientCoreSDK.sharedInstance().saveFirstLoginTime(firstLoginTime: loginInfo!.firstLoginTime)
            }
            
            // 登陆成功的其它处理逻辑
            self.fireConnectedToServer()
        } else {
            // 登陆失败后关闭网络监听是合理的作法
            LocalSocketProvider.sharedInstance().closeLocalSocket()
            
            // 设置中否正常连接（登陆）到服务器的标识（注意：在要event事件通知前设置哦，因为应用中是在event中处理状态的）
            ClientCoreSDK.sharedInstance().connectedToServer = false
        }
        
        // 用户登陆认证情况通知回调
        ClientCoreSDK.sharedInstance().chatBaseEvent?.onLoginResponse(errorCode: loginInfo!.code)
    }
    
    /// 收到服务端的错误信息包时的处理逻辑
    /// - Parameter message: 原始数据包
    func onServerResponseError(message: Protocol?) {
        if message == nil {
            return
        }
        
        // 解析服务端反馈过来的消息
        let errorResponse = ProtocolFactory.parseErrorResponse(jsonString: message!.dataContent)
        if errorResponse == nil {
            return
        }
        
        // 收到的如果是“尚未登陆”的错误消息，则意味着该用户的socket会话可能是非法的，接着就该中止心跳并启动重连机制
        if errorResponse!.errorCode == .noLoginFromResponse {
            ClientCoreSDK.sharedInstance().setLoginHasInit(loginHasInit: false)
            
            CAPrint("【IMCORE-TCP】收到服务端的“尚未登陆”的错误消息，心跳线程将停止，请应用层重新登陆")
            
            // 停止心跳
            KeepAliveDaemon.sharedInstance().stop()
            
            // 此时尝试延迟开启自动登陆线程哦
            AutoReLoginDaemon.sharedInstance().start(immediately: false)
        }
        
        // 收到错误响应消息的回调
        ClientCoreSDK.sharedInstance().chatMessageEvent?.onErrorResponse(errorCode: errorResponse!.errorCode, errorMsg: errorResponse!.errorDesc!)
    }
    
    /// 收到服务端发回来过的“重复登录被踢”消息时的处理逻辑
    /// - Parameter message: 原始数据包
    func onKickout(message: Protocol?) {
        if message == nil {
            return
        }
        
        if ClientCoreSDK.isEnableDebug() {
            CAPrint("【IMCORE-TCP】收到服务端发过来的“被踢”指令")
        }
        
        // 收到被踢指令后，主动断开本地连接等一系列通信资源等释放动作
        ClientCoreSDK.sharedInstance().releaseCore()
        
        // 解析服务端发过来的被踢指令消息内容
        let kickoutInfo = ProtocolFactory.parseKickoutInfo(jsonString: message!.dataContent)
        
        // 给应用层发出“被踢”事件通知
        ClientCoreSDK.sharedInstance().chatBaseEvent?.onKickout(kickoutInfo: kickoutInfo)
        
        // 给应用层发出“网络已断开”事件通知
        ClientCoreSDK.sharedInstance().chatBaseEvent?.onLinkClose(errorCode: -1)
    }
    
    /// 登陆服务端成功后的处理逻辑
    func fireConnectedToServer() {
        // 记录用户登陆信息（因为此处不太好记录用户登陆名和密码，所以登陆名和密码现在是在登陆消息发出时就记录了）
        ClientCoreSDK.sharedInstance().setLoginHasInit(loginHasInit: true)
        
        // 尝试关闭自动重新登陆线程
        AutoReLoginDaemon.sharedInstance().stop()
        
        weak var weakself = self
        let observerBlock = { (observerble: Any?, data: Any?) -> Void in
            weakself!.fireDisconnectedToServer()
        }
        
        // 立即开启Keepalive心跳线程
        KeepAliveDaemon.sharedInstance().setNetworkConnectionLostObserver(networkConnectionLostObserver: observerBlock)
        
        // 成功通信了呢（刚收到服务器的登陆成功反馈），节省1次心跳，降低服务重启后的“雪崩”可能性
        KeepAliveDaemon.sharedInstance().start(immediately: false)
        
        // 启动QoS机制之发送列表重视机制
        QoS4SendDaemon.sharedInstance().startup(immediately: true)
        
        // 启动QoS机制之接收列表防重复机制
        QoS4ReceiveDaemon.sharedInstance().startup(immediately: true)
        
        // 设置中否正常连接（登陆）到服务器的标识（注意：在要event事件通知前设置哦，因为应用中是在event中处理状态的）
        ClientCoreSDK.sharedInstance().connectedToServer = true
    }
    
    /// 与服务端断开连接后的处理逻辑
    func fireDisconnectedToServer() {
        // 设置中否正常连接（登陆）到服务器的标
        ClientCoreSDK.sharedInstance().connectedToServer = false
        
        // 尝试关闭本地Socket（确保已经变“坏”的socket被重置）
        LocalSocketProvider.sharedInstance().closeLocalSocket()
        
        // 掉线时关闭QoS机制之接收列表防重复机制
        QoS4ReceiveDaemon.sharedInstance().stop()
        
        // 通知回调实现类
        ClientCoreSDK.sharedInstance().chatBaseEvent?.onLinkClose(errorCode: -1)
        
        // 网络断开后即刻开启自动重新登陆线程从而尝试重新登陆
        AutoReLoginDaemon.sharedInstance().start(immediately: false)
    }
    
    /// 发送消息应答指令
    /// - Parameter message: 原始数据包
    func sendRecievedBack(message: Protocol?) {
        if message == nil {
            return
        }
        
        if message!.fp == nil {
            CAPrint("【IMCORE-TCP】【QoS】收到\(message!.from)发过来需要QoS的包，但它的指纹码却为null！无法发应答包")
            return
        }
        
        let receiveBackMessage = ProtocolFactory.createReceivedBack(fromUserId: message!.to, toUserId: message!.from, QoS: false, fp: nil, bridge: message!.bridge)
        
        let sendCode = LocalDataSender.sharedInstance().sendCommonData(message: receiveBackMessage)
        if sendCode == .commonCodeOK {
            if ClientCoreSDK.isEnableDebug() {
                CAPrint("【IMCORE-TCP】【QoS】向\(message!.from)发送\(message!.fp ?? "")包的应答包成功,from=\(message!.to) 【bridge?\(message!.bridge)】")
            }
        } else {
            if ClientCoreSDK.isEnableDebug() {
                CAPrint("【IMCORE-TCP】【QoS】向\(message!.from)发送\(message!.fp ?? "")包的应答包失败了,错误码=\(sendCode ?? .unknown)")
            }
        }
    }
}
