//  ----------------------------------------------------------------------
//  Copyright (C) 2020  即时通讯网(52im.net) & Jack Jiang.
//  The MobileIMSDK_X (MobileIMSDK v4.x) Project.
//  All rights reserved.
//
//  > Github地址: https://github.com/JackJiang2011/MobileIMSDK
//  > 文档地址:    http://www.52im.net/forum-89-1.html
//  > 技术社区：   http://www.52im.net/
//  > 技术交流群： 320837163 (http://www.52im.net/topic-qqgroup.html)
//  > 作者公众号： “即时通讯技术圈】”，欢迎关注！
//  > 联系作者：   http://www.52im.net/thread-2792-1-1.html
//
//  "即时通讯网(52im.net) - 即时通讯开发者社区!" 推荐开源工程。
//  ----------------------------------------------------------------------

#import <Foundation/Foundation.h>
#import "PKickoutInfo.h"

/*! @protocol ChatBaseEvent
 * MobileIMSDK的基础通信消息的回调事件接口（如：登陆成功事件通知、掉线事件通知等）。
 * <br>
 * 实现此接口后，通过 [ClientCoreSDK setChatBaseEvent:]方法设置之，可实现回调事件的通知和处理。
 *
 * @author Jack Jiang(http://www.52im.net/thread-2792-1-1.html)
 * @version 1.0
 * @see ClientCoreSDK
 */
@protocol ChatBaseEvent <NSObject>

/*!
 * 本地用户的登陆结果回调事件通知。
 *
 * @param errorCode 服务端反馈的登录结果：0 表示登陆成功，否则为服务端自定义的出错代码（按照约定通常为>=1025的数）
 */
- (void) onLoginResponse:(int) errorCode;

/*!
 * 与服务端的通信断开的回调事件通知。
 *
 * <br>
 * 该消息只有在客户端连接服务器成功之后网络异常中断之时触发。
 * 导致与与服务端的通信断开的原因有（但不限于）：无线网络信号不稳定、WiFi与2G/3G/4G等同开情
 * 况下的网络切换、手机系统的省电策略等。
 *
 * @param errorCode 本回调参数表示表示连接断开的原因，目前错误码没有太多意义，仅作保留字段，目前通常为-1
 */
- (void) onLinkClose:(int)errorCode;

/*!
 * 本的用户被服务端踢出的回调事件通知。
 *
 * @param kickoutInfo 被踢信息对象，{@link PKickoutInfo} 对象中的 code字段定义了被踢原因代码
 */
- (void) onKickout:(PKickoutInfo *)kickoutInfo;

@end
