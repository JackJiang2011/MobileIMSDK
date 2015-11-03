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
//  AutoReLoginDaemon.h
//  MobileIMSDK4i
//
//  Created by JackJiang on 14/10/24.
//  Copyright (c) 2014年 openmob.net. All rights reserved.
//

#import <Foundation/Foundation.h>
#import "CompletionDefine.h"

/*!
 * 与服务端通信中断后的自动登陆（重连）独立线程。
 * <p>
 * 鉴于无线网络的不可靠性和特殊性，移动端的即时通讯经常存在网络通信断断续续的
 * 状况，可能的原因有（但不限于）：无线网络信号不稳定、WiFi与2G/3G/4G等同开情
 * 况下的网络切换、手机系统的省电策略等。这就使得即时通信框架拥有对上层透明且健
 * 壮的健康度探测和自动治愈机制非常有必要。
 * <p>
 * 本类的存在使得MobileIMSDK框架拥有通信自动治愈的能力。
 * <p>
 * <b>注意：</b>自动登陆（重连）只可能发生在登陆成功后与服务端的网络通信断开时。
 * <p>
 * <b>本线程的启停，目前属于MobileIMSDK算法的一部分，暂时无需也不建议由应用层自行调用。</b>
 *
 * @author Jack Jiang, 2014-10-24
 * @version 1.0
 */
@interface AutoReLoginDaemon : NSObject

/// 获取本类的单例。使用单例访问本类的所有资源是唯一的合法途径。
+ (AutoReLoginDaemon *)sharedInstance;

/*!
 *  @Author Jack Jiang, 14-10-31 15:10:24
 *
 * 设置自动重新登陆时间间隔（单位：毫秒），默认2000毫秒。
 * <p>
 * 此参数只会影响断线后与服务器连接的即时性，不受任何配置参数
 * 的影响。请基于重连（重登陆）即时性和手机能耗上作出权衡。
 * <p>
 * 除非对MobileIMSDK的整个即时通讯算法非常了解，否则请勿尝试单独设置本参数。如
 * 需调整心跳频率请见 {@link [ConfigEntity.setSenseMode:SenseMode]}。
 *
 *  @param autoReLoginInterval
 */
+ (void) setAUTO_RE_LOGIN_INTERVAL:(int)autoReLoginInterval;

/*!
 *  @Author Jack Jiang, 14-10-31 15:10:28
 *
 *  获取自动重新登陆时间间隔（单位：毫秒）.
 *
 *  @return
 */
+ (int) getAUTO_RE_LOGIN_INTERVAL;

/*!
 * 无条件中断本线程的运行。
 *
 * @warning 本线程的启停，目前属于MobileIMSDK算法的一部分，暂时无需也不建议由应用层自行调用。
 */
- (void) stop;

/*!
 * 启动线程。
 * <p>
 * 无论本方法调用前线程是否已经在运行中，都会尝试首先调用 @link stop @/link 方法，
 * 以便确保线程被启动前是真正处于停止状态，这也意味着可无害调用本方法。
 * <p>
 *
 * @param immediately true表示立即执行线程作业，否则直到 {@link #AUTO_RE$LOGIN_INTERVAL}
 * 执行间隔的到来才进行首次作业的执行
 */
- (void) start:(BOOL)immediately;

/*!
 * 线程是否正在运行中。
 *
 * @return true表示是，否则线路处于停止状态
 */
- (BOOL) isautoReLoginRunning;

/*!
 *  @Author Jack Jiang, 14-11-07 22:11:28
 *
 *  Just for DEBUG.
 */
- (void) setDebugObserver:(ObserverCompletion)debugObserver;

@end
