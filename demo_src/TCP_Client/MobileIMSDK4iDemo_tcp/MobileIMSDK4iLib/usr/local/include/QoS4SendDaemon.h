//  ----------------------------------------------------------------------
//  Copyright (C) 2020  即时通讯网(52im.net) & Jack Jiang.
//  The MobileIMSDK_TCP (MobileIMSDK v5.x TCP版) Project.
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
#import "Protocal.h"
#import "CompletionDefine.h"

/*!
 * QoS机制中提供消息送达质量保证的守护线程。
 * <br>
 * 本类是QoS机制的核心，极端情况下将弥补因UDP协议天生的不可靠性而带来的丢包情况。
 * <p>
 * 当前MobileIMSDK的QoS机制支持全部的C2C、C2S、S2C共3种消息交互场景下的消息送达质量保证.
 * 
 * @warning 本线程的启停，目前属于MobileIMSDK算法的一部分，暂时无需也不建议由应用层自行调用。</b>
 *
 * @author Jack Jiang(http://www.52im.net/thread-2792-1-1.html)
 * @version 1.1
 * @since 2.1
 */
@interface QoS4SendDaemon : NSObject

/// 获取本类的单例。使用单例访问本类的所有资源是唯一的合法途径。
+ (QoS4SendDaemon *)sharedInstance;

/*!
 * 启动线程。
 * <p>
 * 无论本方法调用前线程是否已经在运行中，都会尝试首先调用 stop 方法，以便确保线程被启动前是真正处于停止状态，这也意味着可无害调用本方法。
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
 * 该包是否已存在于队列中。
 *
 * @param fingerPrint 消息包的特纹特征码（理论上是唯一的）
 * @return
 */
- (BOOL) exist:(NSString *)fingerPrint;

/*!
 * 推入一个消息包的指纹特征码.
 * <br><b>注意：</b>本方法只会将指纹码推入，而不是将整个Protocal对象放入列表中。
 *
 * @param p
 */
- (void) put:(Protocal *)p;

/*!
 * 移除一个消息包.
 * <p>
 * 此操作是在步异线程中完成，目的是尽一切可能避免可能存在的阻塞本类中的守望护线程.
 *
 * @param fingerPrint 消息包的特纹特征码（理论上是唯一的）
 * @return
 */
- (void) remove:(NSString *) fingerPrint;

/**
 * 清空缓存队列。
 * <p>
 * 调用此方法可以防止在APP不退出的情况下退出登陆MobileIMSDK时没有清除队列缓存，导致此时换用另一账号时发生数据交叉。
 *
 * @since 3.2, 20180103
 */
- (void) clear;

/*!
 * 队列大小.
 *
 * @return
 * @see HashMap#size()
 */
- (unsigned long) size;

/*!
 * Just for DEBUG.
 */
- (void) setDebugObserver:(ObserverCompletion)debugObserver;

@end
