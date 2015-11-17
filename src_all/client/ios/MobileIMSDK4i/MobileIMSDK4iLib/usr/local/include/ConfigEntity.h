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
//  ConfigEntity.h
//  MobileIMSDK4i
//
//  Created by JackJiang on 14/10/22.
//  Copyright (c) 2014年 openmob.net. All rights reserved.
//

#import <Foundation/Foundation.h>

/*!
 * MobileIMSDK的全局参数控制类。
 *
 * @author Jack Jiang, 2014-10-22
 * @version 1.0
 * @since 2.1
 */
@interface ConfigEntity : NSObject

/*! 
 * @typedef SenseMode
 * MobileIMSDK即时通讯核心框架预设的敏感度模式.
 *
 * <p>
 * 对于客户端而言，此模式决定了用户与服务端网络会话的健康模式，原则上超敏感客户端的体验越好。
 *
 * <p>
 * <b>重要说明：</b><u>客户端本模式的设定必须要与服务端的模式设制保持一致</u>，否则
 * 可能因参数的不一致而导致IM算法的不匹配，进而出现不可预知的问题。
 *
 * @author Jack Jiang, 2015-09-07
 * @version 2.1
 */
typedef enum
{
    /*!
     * 此模式下：<br>
     * * KeepAlive心跳问隔为3秒；<br>
     * * 10秒后未收到服务端心跳反馈即认为连接已断开（相当于连续3 个心跳间隔后仍未收到服务端反馈）。
     */
    SenseMode3S,
    
    /*!
     * 此模式下：<br>
     * * KeepAlive心跳问隔为10秒；<br>
     * * 21秒后未收到服务端心跳反馈即认为连接已断开（相当于连续2 个心跳间隔后仍未收到服务端反馈）。
     */
    SenseMode10S,
    
    /*!
     * 此模式下：<br>
     * * KeepAlive心跳问隔为30秒；<br>
     * * 61秒后未收到服务端心跳反馈即认为连接已断开（相当于连续2 个心跳间隔后仍未收到服务端反馈）。
     */
    SenseMode30S,
    
    /*!
     * 此模式下：<br>
     * * KeepAlive心跳问隔为60秒；<br>
     * * 121秒后未收到服务端心跳反馈即认为连接已断开（相当于连续2 个心跳间隔后仍未收到服务端反馈）。
     */
    SenseMode60S,
    
    /*!
     * 此模式下：<br>
     * * KeepAlive心跳问隔为120秒；<br>
     * * 241秒后未收到服务端心跳反馈即认为连接已断开（相当于连续2 个心跳间隔后仍未收到服务端反馈）。
     */
    SenseMode120S
} SenseMode;

// OC中访问全局static变量需通过方法

/*!
 *  设置AppKey.
 *
 *  @param key AppKey字符串
 */
+ (void)registerWithAppKey:(NSString *)key;

/*!
 * 全局设置：服务端IP或域名。
 * @warning 如需设置本参数，请在登陆前调用，否则将不起效。
 *
 * @param sIp 服务器的ip地址或域名
 */
+ (void) setServerIp:(NSString*)sIp;

/*!
 *  返回设置的服务端IP或域名。
 *
 *  @return 服务端IP或域名
 */
+ (NSString *)getServerIp;

/*!
 * 全局设置：服务端UDP服务侦听端口号。
 * @warning 如需设置本参数，请在登陆前调用，否则将不起效。
 *
 * @param sPort 服务端的端口号
 */
+ (void) setServerPort:(int)sPort;

/*!
 *  返回设置的服务端端口号。
 *
 *  @return 服务端端口号
 */
+ (int) getServerPort;

/*!
 * 全局设置：本地UDP数据发送和侦听端口。默认是7801。
 *
 * <br>
 * 在什么场景下需要使用固定端口号呢？通常用于debug时，比如观察NAT网络下的外网端口分配情况。
 *
 * @warning 如需设置本参数，请在登陆前调用，否则将不起效。
 *
 * @param lPort 本地UDP数据发送和侦听端口号。<u>参数lPort=-1时表示不绑定固定port</u>（由系
 * 统自动分配，这意味着同时开启两个及以上本SDK的实例也不会出现端口占用冲突），否则使用指定端口
 */
+ (void) setLocalUdpSendAndListeningPort:(int)lPort;

+ (int) getLocalUdpSendAndListeningPort;

/*!
 * 设置MobileIMSDK即时通讯核心框架预设的敏感度模式。
 *
 * <p>
 * <b>重要说明：</b><u>客户端本模式的设定必须要与服务端的模式设制保持一致</u>，否则
 * 可能因参数的不一致而导致IM算法的不匹配，进而出现不可预知的问题。
 *
 * @warning 请在登陆前调用，否则将不起效.
 *
 * @param mode 框架预设的敏感度模式
 * @see SenseMode
 */
+ (void) setSenseMode:(SenseMode)mode;

@end
