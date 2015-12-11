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
//  LocalUDPDataSender.h
//  MobileIMSDK4i
//
//  Created by JackJiang on 14/10/27.
//  Copyright (c) 2014年 openmob.net. All rights reserved.
//

#import <Foundation/Foundation.h>
#import "Protocal.h"

/*!
 * 数据发送处理实用类。
 * <p>
 * 本类是MobileIMSDK框架的唯一提供数据发送的公开实用类。
 *
 * @author Jack Jiang, 2014-10-27
 * @version 1.0
 * @since 2.1
 */
@interface LocalUDPDataSender : NSObject

/// 获取本类的单例。使用单例访问本类的所有资源是唯一的合法途径。
+ (LocalUDPDataSender *)sharedInstance;

/*!
 * 发送登陆信息(默认extra字段值为nil哦).
 * <p>
 * 本方法中已经默认进行了核心库的初始化，因而使用本类完成登陆时，就无需单独
 * 调用初始化方法[ClientCoreSDK initCore]了。
 *
 * @warning 本库的启动入口就是登陆过程触发的，因而要使本库能正常工作，
 * 请确保首先进行登陆操作。
 * @param loginName 登陆时提交的用户名：此用户名对框架来说可以随意，具体意义由上层逻辑决即可
 * @param loginPsw 登陆时提交的密码：此密码对框架来说可以随意，具体意义由上层逻辑决即可
 * @return 0表示数据发出成功，否则返回的是错误码
 * @see [LocalUDPDataSender sendLogin:withPassword:andExtra:]
 */
- (int) sendLogin:(NSString *)loginName withPassword:(NSString *)loginPsw;

/*!
 * 发送登陆信息.
 * <p>
 * 本方法中已经默认进行了核心库的初始化，因而使用本类完成登陆时，就无需单独
 * 调用初始化方法[ClientCoreSDK initCore]了。
 *
 * @warning 本库的启动入口就是登陆过程触发的，因而要使本库能正常工作，
 * 请确保首先进行登陆操作。
 * @param loginName 登陆时提交的用户名：此用户名对框架来说可以随意，具体意义由上层逻辑决即可
 * @param loginPsw 登陆时提交的密码：此密码对框架来说可以随意，具体意义由上层逻辑决即可
 * @param extra 额外信息字符串，可为null。本字段目前为保留字段，供上层应用自行放置需要的内容
 * @return 0表示数据发出成功，否则返回的是错误码
 * @see [LocalUDPDataSender sendImpl_:(NSData *)]
 */
- (int) sendLogin:(NSString *)loginName withPassword:(NSString *)loginPsw andExtra:(NSString *)extra;

/*!
 * 发送注销登陆信息.
 * <p>
 * 本方法调用后，除非再次进行登陆过程，否则核心库将处于初始未初始化状态。
 *
 * @warning 此方法的调用将被本库理解为退出库的使用，本方法将会额外调
 * 用资源释放方法 [ClientCoreSDK releaseCore]，以保证资源释放。
 * @return 0表示数据发出成功，否则返回的是错误码
 * @see [LocalUDPDataSender sendImpl_:(NSData *)]
 */
- (int) sendLoginout;

/*!
 * 发送Keep Alive心跳包.
 *
 * @return 0表示数据发出成功，否则返回的是错误码
 * @see [LocalUDPDataSender sendImpl_:(NSData *)]
 */
- (int) sendKeepAlive;

/*!
 * 通用数据发送方法（默认不需要Qos支持）。
 *
 * @param dataContent byte数组组织的数据内容
 * @param dataLen byte数组长度
 * @param to_user_id 要发送到的目标用户id
 * @return 0表示数据发出成功，否则返回的是错误码
 * @see #sendCommonData(String, int, boolean, String)
 */
- (int) sendCommonData:(NSData *)dataContent toUserId:(int)to_user_id;

/*!
 * 通用数据发送方法。
 *
 * @param dataContent byte数组组织的数据内容
 * @param dataLen byte数组长度
 * @param to_user_id 要发送到的目标用户id
 * @param QoS true表示需QoS机制支持，不则不需要
 * @param fingerPrint QoS机制中要用到的指纹码（即消息包唯一id），生成方法见 {@link Protocal#genFingerPrint()}
 * @return 0表示数据发出成功，否则返回的是错误码
 * @see #sendCommonData(String, int, boolean, String)
 */
- (int) sendCommonData:(NSData *)dataContent toUserId:(int)to_user_id qos:(BOOL)QoS fp:(NSString *)fingerPrint;

/*!
 * 通用数据发送方法。
 *
 * @param dataContentWidthStr 要发送的数据内容（字符串方式组织）
 * @param to_user_id 要发送到的目标用户id
 * @return 0表示数据发出成功，否则返回的是错误码
 * @see #sendCommonData(Protocal)
 * @see DataFactoryC.createCommonData(String, int, int)
 */
- (int) sendCommonDataWithStr:(NSString *)dataContentWidthStr toUserId:(int)to_user_id;

/*!
 * 通用数据发送方法（默认不需要Qos支持）。
 *
 * @param dataContentWidthStr 要发送的数据内容（字符串方式组织）
 * @param to_user_id 要发送到的目标用户id
 * @return 0表示数据发出成功，否则返回的是错误码
 * @see #sendCommonData(Protocal)
 * @see DataFactoryC#createCommonData(String, int, int, boolean, String)
 */
- (int) sendCommonDataWithStr:(NSString *)dataContentWidthStr toUserId:(int)to_user_id qos:(BOOL)QoS fp:(NSString *)fingerPrint;

/*!
 * 通用数据发送的根方法。
 *
 * @param p 要发送的内容（MobileIMSDK框架的“协议”DTO对象组织形式）
 * @return 0表示数据发出成功，否则返回的是错误码
 * @see [LocalUDPDataSender sendImpl_:(NSData *)]
 */
- (int) sendCommonData:(Protocal *)p;

@end
