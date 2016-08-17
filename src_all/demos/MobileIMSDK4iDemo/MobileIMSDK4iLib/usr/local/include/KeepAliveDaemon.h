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
//  KeepAliveDaemon.h
//  MobileIMSDK4i
//
//  Created by JackJiang on 14/10/24.
//  Copyright (c) 2014年 openmob.net. All rights reserved.
//

#import <Foundation/Foundation.h>
#import "CompletionDefine.h"

/*!
 * 用于保持与服务端通信活性的Keep alive独立线程。
 * <p>
 * <b>Keep alive的目的有2个：</b>
 * <br>
 * 1、<u>防止NAT路由算法导致的端口老化</u>：
 * <br>
 * <code>
 * 路由器的NAT路由算法存在所谓的“端口老化”概念
 * （理论上NAT算法中UDP端口老化时间为300S，但这不是标准，而且中高端路由器
 * 可由网络管理员自行设定此值），Keep alive机制可确保在端口老化时间到来前
 * 重置老化时间，进而实现端口“保活”的目的，否则端口老化导致的后果是服务器
 * 将向客户端发送的数据将被路由器抛弃。
 * </code>
 * <br>
 * 2、<u>即时探测由于网络状态的变动而导致的通信中断</u>（进而自动触发自动治愈机制）：
 * <br>
 * <code>
 * 此种情况可的原因有（但不限于）：无线网络信号不稳定、WiFi与2G/3G/4G等同开情
 * 况下的网络切换、手机系统的省电策略等。
 * </code>
 * <p>
 * <b>本线程的启停，目前属于MobileIMSDK算法的一部分，暂时无需也不建议由应用层自行调用。</b>
 *
 * @author Jack Jiang, 2014-10-24
 * @version 1.0
 */
@interface KeepAliveDaemon : NSObject

/// 获取本类的单例。使用单例访问本类的所有资源是唯一的合法途径。
+ (KeepAliveDaemon *)sharedInstance;

/*!
 *  @Author Jack Jiang, 14-10-31 15:10:11
 *
 *  Keep Alive 心跳时间间隔（单位：毫秒），默认3000毫秒.
 * <p>
 * 心跳间隔越短则保持会话活性的健康度更佳，但将使得在大量客户端连接情况下服务端因此而增加负载，
 * 且手机将消耗更多电量和流量，所以此间隔需要权衡（建议为：>=1秒 且 < 300秒）！
 * <p>
 * 说明：此参数用于设定客户端发送到服务端的心跳间隔，心跳包的作用是用来保持与服务端的会话活性（
 * 更准确的说是为了避免客户端因路由器的NAT算法而导致UDP端口老化）.
 * <p>
 * 参定此参数的同时，也需要相应设置服务端的ServerLauncher.SESION_RECYCLER_EXPIRE参数。 *
 *  @param keepAliveTimeWithMils
 */
+ (void) setKEEP_ALIVE_INTERVAL:(int)keepAliveTimeWithMils;

/*!
 *  @Author Jack Jiang, 14-10-31 15:10:16
 *
 *  返回当前设置的Keep Alive 心跳时间间隔（单位：毫秒）.
 *
 *  @return
 */
+ (int) getKEEP_ALIVE_INTERVAL;

/*!
 *  @Author Jack Jiang, 14-10-31 15:10:20
 *
 *  设置收到服务端响应心跳包的超时间时间（单位：毫秒），默认（3000 * 3 + 1000）＝ 10000 毫秒.
 *  <p>
 *  超过这个时间客户端将判定与服务端的网络连接已断开（此间隔建议为(KEEP_ALIVE_INTERVAL * 3) + 1 秒），
 *  没有上限，但不可太长，否则将不能即时反映出与服务器端的连接断开（比如掉掉线时），请从
 *  能忍受的反应时长和即时性上做出权衡。
 *  <p>
 *  本参数除与{@link KeepAliveDaemon#KEEP_ALIVE_INTERVAL}有关联外，不受其它设置影响。
 *
 *  @param networkConnectionTimeout
 */
+ (void) setNETWORK_CONNECTION_TIME_OUT:(int)networkConnectionTimeout;

/*!
 *  @Author Jack Jiang, 14-10-31 15:10:23
 *
 *  服务端响应心跳包的超时间时间（单位：毫秒）.
 *
 *  @return 
 */
+ (int) getNETWORK_CONNECTION_TIME_OUT;

/*!
 * 无条件中断本线程的运行。
 * <p>
 * <b>本线程的启停，目前属于MobileIMSDK算法的一部分，暂时无需也不建议由应用层自行调用。</b>
 */
- (void) stop;

/*!
 * 启动线程。
 * <p>
 * 无论本方法调用前线程是否已经在运行中，都会尝试首先调用 {@link #stop()}方法，
 * 以便确保线程被启动前是真正处于停止状态，这也意味着可无害调用本方法。
 * <p>
 * <b>本线程的启停，目前属于MobileIMSDK算法的一部分，暂时无需也不建议由应用层自行调用。</b>
 *
 * @param immediately true表示立即执行线程作业，否则直到 {@link #AUTO_RE$LOGIN_INTERVAL}
 * 执行间隔的到来才进行首次作业的执行
 */
- (void) start:(BOOL)immediately;

/**
 * 线程是否正在运行中。
 *
 * @return true表示是，否则线路处于停止状态
 */
- (BOOL) isKeepAliveRunning;

/**
 * 收到服务端反馈的心跳包时调用此方法：作用是更新服务端最背后的响应时间戳.
 * <p>
 * <b>本方法的调用，目前属于MobileIMSDK算法的一部分，暂时无需也不建议由应用层自行调用。</b>
 */
- (void) updateGetKeepAliveResponseFromServerTimstamp;

/**
 * 设置网络断开事件观察者.
 * <p>
 * <b>本方法的调用，目前属于MobileIMSDK算法的一部分，暂时无需也不建议由应用层自行调用。</b>
 *
 * @param networkConnectionLostObserver
 */
- (void) setNetworkConnectionLostObserver:(ObserverCompletion)networkConnLostObserver;

/*!
 *  @Author Jack Jiang, 14-11-07 22:11:28
 *
 *  Just for DEBUG.
 */
- (void) setDebugObserver:(ObserverCompletion)debugObserver;


@end
