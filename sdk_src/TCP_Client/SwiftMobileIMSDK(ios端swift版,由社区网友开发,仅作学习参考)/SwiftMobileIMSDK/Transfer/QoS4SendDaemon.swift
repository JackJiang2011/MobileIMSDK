//
//  QoS4SendDaemon.swift
//  SwiftMobileIMSDK
//
//  Created by fishbay on 2021/8/8.

//  QoS机制中提供消息送达质量保证的守护线程

import Foundation

class QoS4SendDaemon {
    
    /// QoS质量保证线程心跳间隔（单位：毫秒），默认5000ms
    static var checkInterval: Int = 5000
    /// “刚刚”发出的消息阀值定义（单位：毫秒），默认3000毫秒
    static var messageJustNowTime: Int = 3000
    /// 一个包允许的最大重发次数，默认2次
    static var qosTryCount: Int = 2
    
    /// Hash表，因为本类中可能存在不同的线程同时remove或遍历之，注意同步问题
    /// 本对象中的key=指纹码，value=Protocal对象
    var sentMessage = [String: Protocol]()
    /// 本Hash表目前仅用于QoS重传判断是否是“刚刚”发出的消息之用，别无它用
    /// 本对象中的key=指纹码，value=该包发送出去时的时间戳
    var sendMessageTimestamp = [String: Int]()
    /// 当前线程是否正在执行中
    var running: Bool = false
    
    var executing: Bool = false
    var timer: Timer?
    
    /// 本属性仅作DEBUG之用：DEBUG事件观察者
    var debugObserver: ObserverCompletion?
    
    // 单例
    private static let instance: QoS4SendDaemon = QoS4SendDaemon()
    static func sharedInstance() -> QoS4SendDaemon {
        return instance
    }
    private init() {
        CAPrint("QoS4SendDaemon已经init了")
    }
    
    @objc func run() {
        if self.executing {
            return
        }
        
        // 极端情况下本次循环内可能执行时间超过了时间间隔，此处是防止在前一次还没有运行完的情况下又重复执行，从而出现无法预知的错误
        self.executing = true
        
        // 丢包列表
        var lostMessages = [Protocol]()
        
        if ClientCoreSDK.isEnableDebug() && self.sentMessage.count > 0 {
            CAPrint("【IMCORE-TCP】【QoS】====== 消息发送质量保证线程运行中, 当前需要处理的列表长度为 \(self.sentMessage.count) ...")
        }
        
        for key in self.sentMessage.keys {
            let message = self.sentMessage[key]
            if message != nil && message!.QoS {
                // 达到或超过了最大重试次数（判定丢包）
                if message!.getRetryCount() >= Self.qosTryCount {
                    if ClientCoreSDK.isEnableDebug() {
                        CAPrint("【IMCORE-TCP】【QoS】指纹为 \(message!.fp ?? "") 的消息包重传次数已达 \(message!.getRetryCount()) (最多 \(Self.qosTryCount) 次)上限，将判定为丢包")
                    }
                    
                    // 将这个包加入到丢包列表（该Protocal对象将是一个clone的全新对象而非原来的引用哦！）
                    let newMessage = message!.clone()
                    if newMessage != nil {
                        lostMessages.append(newMessage!)
                    }
                    
                    // 从列表中称除之
                    self.remove(fp: message!.fp ?? "")
                } else {
                    //  解决了无线网络延较大时，刚刚发出的消息在其应答包还在途中时被错误地进行重传
                    let lastMessageTimestamp = self.sendMessageTimestamp[key]
                    let delta = ToolKits.getTimeStampWithMillisecondInt() - (lastMessageTimestamp ?? 0)
                    // 该消息包是“刚刚”发出的，本次不需要重传它
                    if delta <= Self.messageJustNowTime {
                        if ClientCoreSDK.isEnableDebug() {
                            CAPrint("【IMCORE-TCP】【QoS】指纹为\(key)的包距\"刚刚\"发出才\(delta) ms(<=\(Self.messageJustNowTime) ms将被认定是\"刚刚\"), 本次不需要重传哦")
                        }
                    } else {
                        let sendCode = LocalDataSender.sharedInstance().sendCommonData(message: message)
                        // 已成功重传
                        if sendCode == .commonCodeOK {
                            // 重传次数+1
                            message!.increaseRetryCount()
                            
                            if ClientCoreSDK.isEnableDebug() {
                                CAPrint("【IMCORE-TCP】【QoS】指纹为\(message!.fp ?? "")的消息包已成功进行重传，此次之后重传次数已达\(message!.getRetryCount())(最多\(Self.qosTryCount)次)")
                            }
                        } else {
                            CAPrint("【IMCORE-TCP】【QoS】指纹为\(message!.fp ?? "")的消息包重传失败，它的重传次数之前已累计为\(message!.getRetryCount())(最多\(Self.qosTryCount)次)")
                        }
                    }
                }
            } else {
                // value值为null，从列表中去掉
                self.remove(fp: key)
            }
        }
        
        if lostMessages.count > 0 {
            // 通知观察者这些包丢包了（目标接收者没有收到）
            self.notifyMessageLost(lostMessage: lostMessages)
        }
        
        self.executing = false
        
        // form DEBUG
        self.debugObserver?(nil, 2)
    }
    
    /// 将未送达信息反馈给消息监听者
    /// - Parameter lostMessage: 已被判定为“消息未送达”的消息列表
    func notifyMessageLost(lostMessage: [Protocol]) {
        ClientCoreSDK.sharedInstance().messageQoSEvent?.messagesLost(lostMessage: lostMessage)
    }
    
    /// 启动线程
    /// - Parameter immediately: true表示立即执行线程作业，否则直到执行间隔的到来才进行首次作业的执行
    func startup(immediately: Bool) {
        self.stop()
        
        // 执行延迟的单位是秒
        self.timer = Timer.scheduledTimer(timeInterval: TimeInterval(Self.checkInterval / 1000),
                                          target: self,
                                          selector: #selector(run),
                                          userInfo: nil,
                                          repeats: true)
        // 如果需要立即执行
        if immediately {
            self.timer!.fire()
        }
        
        self.running = true
        
        // debug
        self.debugObserver?(nil, 1)
    }
    
    /// 无条件中断本线程的运行
    func stop() {
        if self.timer != nil {
            if self.timer!.isValid {
                self.timer!.invalidate()
            }
            
            self.timer = nil
        }
        
        self.running = false
        
        // debug
        self.debugObserver?(nil, 0)
    }
    
    /// 线程是否正在运行中
    /// - Returns: true表示是，否则线路处于停止状态
    func isRunning() -> Bool {
        return self.running
    }
    
    /// 该包是否已存在于队列中
    /// - Parameter fp: 消息包的特纹特征码
    /// - Returns: true - 存在，false - 不存在
    func exist(fp: String?) -> Bool {
        if fp == nil {
            return false
        }
        
        return self.sentMessage.keys.contains(fp!)
    }
    
    /// 推入一个消息包的指纹特征码
    /// - Parameter message: 消息对象
    func put(message: Protocol?) {
        if message == nil {
            CAPrint("Invalid arg p==null")
            return
        }
        
        if message!.fp == nil {
            CAPrint("Invalid arg p.getFp() == null")
            return
        }
        
        if !message!.QoS {
            CAPrint("This protocal is not QoS pkg, ignore it")
            return
        }
        
        // 如果列表中已经存则仅提示（用于debug）
        if self.sentMessage.keys.contains(message!.fp!) {
            CAPrint("【IMCORE-TCP】【QoS】指纹为 \(message!.fp!) 的消息已经放入了发送质量保证队列，该消息为何会重复？（生成的指纹码重复？还是重复put？）")
        }
        
        // 保存消息
        self.sentMessage[message!.fp!] = message
        
        // 同时保存时间戳
        self.sendMessageTimestamp[message!.fp!] = ToolKits.getTimeStampWithMillisecondInt()
    }
    
    /// 移除一个消息包
    /// - Parameter fp: 消息包的特纹特征码
    func remove(fp: String?) {
        if fp == nil {
            return
        }
        
        if self.sentMessage.keys.contains(fp!) {
            self.sendMessageTimestamp.removeValue(forKey: fp!)
            self.sentMessage.removeValue(forKey: fp!)
            
            CAPrint("【IMCORE-TCP】【QoS】指纹为\(fp!)的消息已成功从发送质量保证队列中移除(可能是收到接收方的应答也可能是达到了重传的次数上限)，重试次数=\(self.sentMessage[fp!]?.getRetryCount() ?? 0)")
        } else {
            CAPrint("【IMCORE-TCP】【QoS】指纹为\(fp!)的消息已成功从发送质量保证队列中移除(可能是收到接收方的应答也可能是达到了重传的次数上限)，重试次数=none呵呵")
        }
    }
    
    /// 清空缓存队列
    func clear() {
        self.sentMessage.removeAll()
        self.sendMessageTimestamp.removeAll()
    }
    
    /// Just for DEBUG
    func setDebugObserver(debugObserver: @escaping ObserverCompletion) {
        self.debugObserver = debugObserver
    }
}
