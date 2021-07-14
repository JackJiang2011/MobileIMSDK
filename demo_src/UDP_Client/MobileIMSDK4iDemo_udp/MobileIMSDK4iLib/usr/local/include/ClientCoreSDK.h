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
#import "ClientCoreSDK.h"
#import "ChatMessageEvent.h"
#import "ChatBaseEvent.h"
#import "MessageQoSEvent.h"
#import "CompletionDefine.h"
#import "PLoginInfo.h"

/*!
 * MobileIMSDK核心框架的核心入口类。
 * <br>
 * 本类主要提供一些全局参数的读取和设置。
 *
 * @author Jack Jiang(http://www.52im.net/thread-2792-1-1.html)
 * @version 2.1
 */
@interface ClientCoreSDK : NSObject

//## 受制于iOS没有精确的网络事件通知机制，本字段的维护在当前的方案下，会不精确（比如模拟器下，电脑断开和连接时，模拟器小概率能收到断开事件，但无法收到已连接事件），所以干脆去掉这个鸡肋字段了
///*!
// * 本地硬件网络是否可用, true表示可用，否则表示不可用.
// * <p>
// * 本字段将在网络事件通知处理中被设置.
// *
// * @warning 本类中的网络状态变更事件通知实现类虽是Apple官方公布，但并不是正式API的一部分，未来或许有更可靠的实现方案，但目前并不影响本核心类库算法的构造和实现。
// */
//@property (nonatomic, assign) BOOL localDeviceNetworkOk;

/*!
 * 是否已成功连接到服务器（当然，前提是已成功发起过登陆请求后）.
 * <p>
 * 此“成功”意味着可以正常与服务端通信（可以近似理解为Socket正常建立、身份认证正常通过），“不成功”意味着不能与服务端通信.
 * <br>
 * 不成功的因素有很多：比如网络不可用、网络状况很差导致的掉线、心跳超时等.
 * <p>
 * <b>本参数是整个MobileIMSDK框架中唯一可作为判断与MobileIMSDK服务器的通信是否正常的准确依据。</b>
 * <p>
 * 本参数将在收到服务端的登陆请求反馈后被设置为true，在与服务端的通信无法正常完成时被设置为false。
 * <br>
 * <u>那么MobileIMSDK如何判断与服务端的通信是否正常呢？</u> 判断方法如下：
 * <ul>
 * <li>1）登陆请求被正常反馈即意味着通信正常（包括首次登陆时和断掉后的自动重新时）；</li>
 * <li>2）首次登陆或断线后自动重连时登陆请求被发出后，没有收到服务端反馈时即意味着不正常；</li>
 * <li>3）与服务端通信正常后，在规定的超时时间内没有收到心跳包的反馈后即意味着与服务端的通信又中断了（即所谓的掉线）。</li>
 * </ul>
 *
 * <br>
 * <b>本参数由框架自动设置。</b>
 */
@property (nonatomic, assign) BOOL connectedToServer;

/*!
 * 当且仅当用户从登陆界面成功登陆后设置本字段为true，系统退出（登陆）时设置为false。<br>
 * <b>本参数由框架自动设置。</b>
 */
@property (nonatomic, assign) BOOL loginHasInit;

/*!
 * 保存提交到服务端的用户登陆信息，可能是登陆用户名、任意不重复的id等，具体意义由业务层决定。
 * <p>
 * 本字段在登陆信息成功发出后就会被设置，将在掉线后自动重连时使用。<br>
 * 因不保证服务端正确收到和处理了该用户的登陆信息，所以本字段因只在{@link #connectedToServer}==true 时才有意义.<br>
 * <b>本参数由框架自动设置。</b>
 */
@property (nonatomic, retain) PLoginInfo *currentLoginInfo;

/*! 框架基础通信消息的回调事件（如：登陆成功事件通知、掉线事件通知） */
@property (nonatomic, retain) id<ChatMessageEvent> chatMessageEvent;
/*! 通用数据通信消息的回调事件（如：收到聊天数据事件通知、服务端返回的错误信息事件通知） */
@property (nonatomic, retain) id<ChatBaseEvent> chatBaseEvent;
/*! QoS质量保证机制的回调事件（如：消息未成功发送的通知、消息已被对方成功收到的通知） */
@property (nonatomic, retain) id<MessageQoSEvent> messageQoSEvent;


//------------------------------------------------------
#pragma mark - 静态方法

/*!
 * 取得本类实例的唯一公开方法。
 * <p>
 * 依据作者对MobileIMSDK API的设计理念，本类目前在整个框架运行中
 * 是以单例的形式存活。
 *
 * @return
 */
+ (ClientCoreSDK *)sharedInstance;

/*!
 *  返回MobileIMSDK框架的日志输出开关量值。
 *
 *  @return true表示开启MobileIMSDK Debug信息在控制台下的输出，否则关闭。默认为NO
 */
+ (BOOL) isENABLED_DEBUG;

/*!
 *  设置MobileIMSDK框架的日志输出开关量。
 *
 *  @param enabledDebug true表示开启MobileIMSDK Debug信息在控制台下的输出，否则关闭。默认为NO
 */
+ (void) setENABLED_DEBUG:(BOOL)enabledDebug;

/*!
 * 是否在登陆成功后掉线时自动重新登陆线程中实质性发起登陆请求。
 * <p>
 * 什么样的场景下，需要设置本参数为false？比如：上层应用可以在自已的节电逻辑中控制当网络长时断开时就不需要实质性发起登陆请求了，因为 网络请求是非常耗电的。
 * <p>
 * <b>本参数的设置将实时生效。</b>
 *
 * @return true表示将在线程运行周期中正常发起，否则不发起（即关闭实质性的重新登陆请求）
 */
+ (BOOL) isAutoReLogin;

/*!
 * 设置是否在登陆成功后掉线时自动重新登陆线程中实质性发起登陆请求。
 * <p>
 * 什么样的场景下，需要设置本参数为false？比如：上层应用可以在自已的节电逻辑中控制当网络长时断开时就不需要实质性发起登陆请求了，因为 网络请求是非常耗电的。
 * <p>
 * <b>本参数的设置将实时生效。</b>
 *
 * @param arl true表示将在线程运行周期中正常发起，否则不发起（即关闭实质性的重新登陆请求）
 */
+ (void) setAutoReLogin:(BOOL)arl;


//------------------------------------------------------
#pragma mark - 实例方法

/*!
 * MobileIMSDK的核心框架是否已经初始化.
 * <br>
 * 当调用 @link initCore @/link 方法后本字段将被置为true，调用@link  releaseCore @/link时将被重新置为false.
 * <br>
 * <b>本参数由框架自动设置。</b>
 * 
 * @return true表示已初始化完成，否则没有初始化完成
 */
- (BOOL) isInitialed;

/*!
 * 初始化核心库.
 * <p>
 * 本方法被调用后， @link isInitialed @/link 将返回true，否则返回false。
 * <p>
 * <b>本方法无需调用者自行调用</b>，它将在发送登陆路请求后（即调用[LocalDataSender sendLogin:(NSString *)loginName withPassword:(NSString *)loginPsw] 时）被自动调用。
 */
- (void)initCore;

/*!
 * 释放MobileIMSDK框架资源统一方法。
 * <p>
 * 本方法建议在退出登陆（或退出APP时）时调用。调用时将尝试关闭所有
 * MobileIMSDK框架的后台守护线程并同设置核心框架init==false、
 * loginHasInit==false、connectedToServer==false。
 *
 * @see AutoReLoginDaemon.stop()
 * @see QoS4SendDaemon.stop()
 * @see KeepAliveDaemon.stop()
 * @see LocalDataReciever.stop()
 * @see QoS4ReciveDaemon.stop()
 * @see LocalSocketProvider.closeLocalSocket()
 */
- (void) releaseCore;

/*!
 * 用于首次登陆成功后，保存服务端返回的登陆时间（此时间将会随着完整登陆信息，在掉线重连时再次提交到服务端，服务端
 * 可据此时间精确判定在多端、网络复杂性的情况下的互踢逻辑）。
 *
 * @param firstLoginTime 服务端返回的首次登陆时间（此时间为形如“1624333553769”的Java时间戳）
 * @since 6.0
 */
- (void) saveFirstLoginTime:(long)firstLoginTime;

/*!
 *  @deprecated 本方法已弃用。请使用 [ClientCoreSDK sharedInstance].currentLoginInfo.loginUserId 替代之！
 */
- (NSString *) currentLoginUserId;

@end
