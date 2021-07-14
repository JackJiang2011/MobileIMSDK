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
#import "MBGCDAsyncSocket.h"
#import "CompletionDefine.h"

/*!
 * 本地 TCP Socket 实例封装实用类。
 * <br>
 * 本类提供存取本地TCP Socket通信对象引用的方便方法，封装了Socket有效性判断以及异常处理等，以便确保调用者通过
 * 方法 {@link #getLocalUDPSocket()}拿到的Socket对象是健康有效的。
 * <p>
 * 依据作者对MobileIMSDK API的设计理念，本类将以单例的形式提供给调用者使用。
 *
 * @author Jack Jiang(http://www.52im.net/thread-2792-1-1.html)
 * @version 1.0
 * @since 1.0
 */
@interface LocalSocketProvider : NSObject <GCDAsyncSocketDelegate>

/// 获取本类的单例。使用单例访问本类的所有资源是唯一的合法途径。
+ (LocalSocketProvider *)sharedInstance;

/*!
 * 重置并新建一个全新的Socket对象。
 *
 * @return 新建的全新Socket对象引用
 * @see GCDAsyncSocket
 * @see [ConfigEntity getLocalPort]
 */
- (MBGCDAsyncSocket *)resetLocalSocket;

/*!
 * 获得本地Socket的实例引用.
 * <p>
 * 本方法内封装了Socket有效性判断以及异常处理等，以便确保调用者通过本方法拿到的Socket对象是健康有效的。
 *
 * @return 如果该实例正常则返回它的引用，否则返回null
 * @see [LocalSocketProvider isLocalSocketReady]
 * @see [LocalSocketProvider resetLocalSocket]
 */
- (MBGCDAsyncSocket *) getLocalSocket;

/*!
 * 设置连接完成后将被通知的观察者。如果设置本参数，则将在连接完成后调用1次，调用完成后置null。
 * <p>
 * 设置本观察者的目的，是因为socket连接的过程是异步完成，有时希望在连接完成时就能立即执行想要的逻辑，那么设置本观察者即可（在某次
 * 连接最终完成前，本参数的设置都会覆盖之前的设置，因为只有这一个观察者可以用哦）。
 *
 * @param networkConnectionLostObserver 观察者对象
 */
- (void) setConnectionObserver:(ConnectionCompletion)connObserver;

/*!
 *  尝试连接指定的socket.
 *  <p>
 *  因为GCDAsyncSocket是异步的，所以连接的结果，会在 delegate中进行回调通知。
 *
 *  @param errPtr 本参数为Error的地址，本方法执行返回时如有错误产生则不为空，否则为nil
 *  @param finish 连接结果回调
 *
 *  @return 0 表示connect的意图是否成功发出（实际上真正连接是通过异步的delegate方法回来的，不在此方法考虑之列），否则表示错误码
 *  @see GCDAsyncSocket, ConnectionCompletion
 */
- (int)tryConnectToHost:(NSError **)errPtr withSocket:(MBGCDAsyncSocket *)skt completion:(ConnectionCompletion)finish;

/*!
 * 本类中的Socket对象是否是健康的。
 *
 * @return true表示是健康的，否则不是
 */
- (BOOL) isLocalSocketReady;

/*!
 * 强制关闭本地Socket侦听。
 * <p>
 * 一旦调用本方法后，再次调用[LocalSocketProvider getLocalSocket]将会返回一个全新的Socket对象引用。
 * <p>
 * <b>本方法通常在两个场景下被调用：</b><br>
 * 1) 真正需要关闭Socket时（如所在的APP通出时）；<br>
 * 2) 当调用者检测到网络发生变动后希望重置以便获得健康的Socket引用对象时。
 *
 * @see [GCDAsyncSocket close]
 */
- (void) closeLocalSocket;

@end
