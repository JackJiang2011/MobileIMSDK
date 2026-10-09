//
//  TCPUtils.swift
//  SwiftMobileIMSDK
//
//  Created by fishbay on 2021/7/28.

//  一个本地TCP消息发送工具类

import Foundation

class TCPUtils {
    
    
    /// 发送数据
    /// - Parameters:
    ///   - socket: socket对象
    ///   - data: 要发送的数据
    /// - Returns: true-发送成功，false-发送失败
    static func send(socket: GCDAsyncSocket?, data: Data?) -> Bool {
        if socket == nil || data == nil {
            CAPrint("【IMCORE】在send()数据报时没有成功执行，原因是：skt==null || d == null")
            return false
        }
        
        var success: Bool = true
        
        if socket!.isConnected {
            // 编码成“帧”，解决tcp传输时的半包、粘包问题
            let frame = TCPFrameCodec.encodeFrame(bodyData: data)
            
            if frame != nil {
                socket!.write(frame, withTimeout: -1, tag: 999)
            } else {
                CAPrint("【IMCORE】要发送的数据编码后frame=nil，本次发送将被忽略(要发送的原始数据为：\(data!)")
                success = false
            }
        } else {
            CAPrint("【IMCORE】[skt isConnected]=false，本次发送将被忽略(要发送的原始数据为：\(data!))")
            success = false
        }
        
        return success
    }
}


