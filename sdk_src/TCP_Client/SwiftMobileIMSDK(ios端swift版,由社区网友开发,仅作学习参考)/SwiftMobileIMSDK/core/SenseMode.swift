//
//  SenseMode.swift
//  SwiftMobileIMSDK
//
//  Created by fishbay on 2021/8/2.

//  MobileIMSDK即时通讯核心框架预设的敏感度模式

import Foundation

enum SenseMode {
    /*!
     * * KeepAlive心跳问隔为3秒；<br>
     * * 5秒后未收到服务端心跳反馈即认为连接已断开（相当于连续2个心跳间隔(即算法最大6秒延迟后)后仍未收到服务端反馈）。
     */
    case SenseMode3S
    /*！
     * * KeepAlive心跳问隔为5秒；<br>
     * * 8秒后未收到服务端心跳反馈即认为连接已断开（相当于连续1个心跳间隔+3秒链路延迟容忍时间后仍未收到服务端反馈）。
     */
    case SenseMode5S
    /*!
     * * KeepAlive心跳问隔为10秒；<br>
     * * 15秒后未收到服务端心跳反馈即认为连接已断开（相当于连续2个心跳间隔(即算法最大20秒延迟后)后仍未收到服务端反馈）。
     */
    case SenseMode10S
    /*!
     * * KeepAlive心跳问隔为15秒；<br>
     * * 20秒后未收到服务端心跳反馈即认为连接已断开（相当于连续2个心跳间隔(即算法最大30秒延迟后)后仍未收到服务端反馈）。
     */
    case SenseMode15S
    /*!
     * * KeepAlive心跳问隔为30秒；<br>
     * * 35秒后未收到服务端心跳反馈即认为连接已断开（相当于连续2个心跳间隔(即算法最大60秒延迟后)后仍未收到服务端反馈）。
     */
    case SenseMode30S
    /*!
     * * KeepAlive心跳问隔为60秒；<br>
     * * 65秒后未收到服务端心跳反馈即认为连接已断开（相当于连续2个心跳间隔(即算法最大120秒延迟后)后仍未收到服务端反馈）。
     */
    case SenseMode60S
    /*!
     * * KeepAlive心跳问隔为120秒；<br>
     * *125秒后未收到服务端心跳反馈即认为连接已断开（相当于连续2个心跳间隔(即算法最大240秒延迟后)后仍未收到服务端反馈）。
     */
    case SenseMode120S
}
