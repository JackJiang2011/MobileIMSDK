//  ----------------------------------------------------------------------
//  Copyright (C) 2015 Jack Jiang The MobileIMSDK Project.
//  All rights reserved.
//  Project URL:  https://github.com/JackJiang2011/MobileIMSDK
//
//  openmob.net PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
//
//  You can contact author with jack.jiang@openmob.net or jb2011@163.com.
//  ----------------------------------------------------------------------
//
//  ChatBaseEvent.h
//  MobileIMSDK4i
//
//  Created by JackJiang on 14/10/21.
//  Copyright (c) 2014年 openmob.net. All rights reserved.
//

#import <Foundation/Foundation.h>

/*! @protocol ChatBaseEvent
 * MobileIMSDK的基础通信消息的回调事件接口（如：登陆成功事件
 * 通知、掉线事件通知等）。
 * <br>
 * 实现此接口后，通过 [ClientCoreSDK setChatBaseEvent:]方法设置之，
 * 可实现回调事件的通知和处理。
 *
 * @author Jack Jiang, 2014-10-21
 * @version 1.0
 * @see ClientCoreSDK
 */
@protocol ChatBaseEvent <NSObject>

/*!
 * 本地用户的登陆结果回调事件通知。
 *
 * @param dwUserId 当回调参数dwErrorCode=0时，本回调参数值表示登陆成功后服务端分配的用户id，否则本回调参数值无意义
 * @param dwErrorCode 服务端反馈的登录结果：0 表示登陆成功，否则为服务端自定义的出错代码（按照约定通常为>=1025的数）
 */
- (void) onLoginMessage:(int)dwUserId withErrorCode:(int) dwErrorCode;

/*!
 * 与服务端的通信断开的回调事件通知。
 *
 * <br>
 * 该消息只有在客户端连接服务器成功之后网络异常中断之时触发。
 * 导致与与服务端的通信断开的原因有（但不限于）：无线网络信号不稳定、WiFi与2G/3G/4G等同开情
 * 况下的网络切换、手机系统的省电策略等。
 *
 * @param dwErrorCode 本回调参数表示表示连接断开的原因，目前错误码没有太多意义，仅作保留字段，目前通常为-1
 */
- (void) onLinkCloseMessage:(int)dwErrorCode;

@end
