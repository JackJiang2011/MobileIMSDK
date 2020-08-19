//  ----------------------------------------------------------------------
//  Copyright (C) 2017  即时通讯网(52im.net) & Jack Jiang.
//  The MobileIMSDK_X (MobileIMSDK v3.x) Project.
//  All rights reserved.
//
//  > Github地址: https://github.com/JackJiang2011/MobileIMSDK
//  > 文档地址: http://www.52im.net/forum-89-1.html
//  > 即时通讯技术社区：http://www.52im.net/
//  > 即时通讯技术交流群：320837163 (http://www.52im.net/topic-qqgroup.html)
//
//  "即时通讯网(52im.net) - 即时通讯开发者社区!" 推荐开源工程。
//
//  如需联系作者，请发邮件至 jack.jiang@52im.net 或 jb2011@163.com.
//  ----------------------------------------------------------------------
//
//  ProtocalQoS4ReciveProvider.h
//  MibileIMSDK4i_X (MobileIMSDK v3.0 at Summer 2017)
//
//  Created by JackJiang on 14/10/23.
//  Copyright (c) 2017年 52im.net. All rights reserved.
//

#import <Foundation/Foundation.h>
#import "Protocal.h"
#import "CompletionDefine.h"

/*!
 * QoS机制中提供对已收到包进行有限生命周期存储并提供重复性判断的守护线程。
 * <p>
 * <b>原理是：</b>当收到需QoS机制支持消息包时，会把它的唯一特征码（即指纹id）
 * 存放于本类的“已收到”消息队列中，寿命约为 MESSAGES_VALID_TIME 指明
 * 的时间，每当 CHECH_INTERVAL 定时检查间隔到来时会对其存活期进行检查
 * ，超期将被移除，否则允许其继续存活。理论情况下，一个包的最大寿命不可能超过2
 * 倍的 CHECH_INTERVAL 时长。
 * <br>
 * <b><u>补充说明</u>：</b>“超期”即意味着对方要么已收到应答包（这是QoS机制正
 * 常情况下的表现）而无需再次重传、要么是已经达到QoS机制的重试极限而无可能再收
 * 到重复包（那么在本类列表中该表也就没有必要再记录了）。总之，“超期”是队列中
 * 这些消息包的正常生命周期的终止，无需过多解读。
 * <p>
 * <b>本类存在的意义在于：</b>极端情况下QoS机制中存在因网络丢包导致应答包的
 * 丢失而触发重传机制从而导致消息重复，而本类将维护一个有限时间段内收到的所有
 * 需要QoS支持的消息的指纹列表且提供“重复性”判断机制，从而保证应用层绝不会因为
 * QoS的重传机制而导致重复收到消息的情况。
 * <p>
 * 当前MobileIMSDK的QoS机制支持全部的C2C、C2S、S2C共3种消息交互场景下的
 * 消息送达质量保证.
 *
 * @warning 本线程的启停，目前属于MobileIMSDK算法的一部分，暂时无需也不建议由应用层自行调用。
 *
 * @author Jack Jiang, 2014-10-23
 * @version 1.0
 */
@interface ProtocalQoS4ReciveProvider : NSObject

/// 获取本类的单例。使用单例访问本类的所有资源是唯一的合法途径。
+ (ProtocalQoS4ReciveProvider *)sharedInstance;

/*!
 * 启动线程。
 * <p>
 * 无论本方法调用前线程是否已经在运行中，都会尝试首先调用 stop 方法，
 * 以便确保线程被启动前是真正处于停止状态，这也意味着可无害调用本方法。
 * 
 * @warning 本线程的启停，目前属于MobileIMSDK算法的一部分，暂时无需也不建议由应用层自行调用。
 *
 * @param immediately true表示立即执行线程作业，否则直到 AUTO_RE$LOGIN_INTERVAL
 * 执行间隔的到来才进行首次作业的执行
 */
- (void) startup:(BOOL)immediately;

/*!
 * 无条件中断本线程的运行。
 *
 * @warning 本线程的启停，目前属于MobileIMSDK算法的一部分，暂时无需也不建议由应用层自行调用。
 */
- (void) stop;

/*!
 * 线程是否正在运行中。
 *
 * @return true表示是，否则线路处于停止状态
 */
- (BOOL) isRunning;

/*!
 * 向列表中加入一个包的特征指纹。
 * <br>注意：本方法只会将指纹码推入，而不是将整个Protocal对象放入列表中。
 * 
 * @warning 本方法的调用，目前属于MobileIMSDK算法的一部分，暂时无需也不建议由应用层自行调用。
 *
 * @param p
 * @see #addRecieved(String)
 */
- (void) addRecieved:(Protocal *)p;

/*!
 * 向列表中加入一个包的特征指纹。
 *
 * @warning 本方法的调用，目前属于MobileIMSDK算法的一部分，暂时无需也不建议由应用层自行调用。
 *
 * @param fingerPrintOfProtocal 消息包的特纹特征码（理论上是唯一的）
 * @see #putImpl(String)
 */
- (void) addRecievedWithFingerPrint:(NSString *) fingerPrintOfProtocal;

/*!
 * 指定指纹码的Protocal是否已经收到过.
 * <p>
 * 此方法用于QoS机制中在防止因网络丢包导致对方未收到应答时而再次发送消息从而导致消息重复时的判断依赖.
 *
 * @param fingerPrintOfProtocal 消息包的特纹特征码（理论上是唯一的）
 * @return
 */
- (BOOL) hasRecieved:(NSString *) fingerPrintOfProtocal;

/*!
 * 当前“已收到消息”队列列表的大小.
 *
 * @return
 * @see NSDictionaty -> count:
 */
- (unsigned long) size;

/*!
 *  @Author Jack Jiang, 14-11-07 22:11:28
 *
 *  Just for DEBUG.
 */
- (void) setDebugObserver:(ObserverCompletion)debugObserver;

@end
