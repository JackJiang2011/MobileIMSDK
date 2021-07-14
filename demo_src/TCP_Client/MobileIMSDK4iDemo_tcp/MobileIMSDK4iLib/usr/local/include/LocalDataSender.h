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
#import "PLoginInfo.h"

/*!
 * 数据发送处理实用类。
 * <p>
 * 本类是MobileIMSDK框架的唯一提供数据发送的公开实用类。
 *
 * @author Jack Jiang(http://www.52im.net/thread-2792-1-1.html)
 * @version 1.0
 * @since 2.1
 */
@interface LocalDataSender : NSObject

/// 获取本类的单例。使用单例访问本类的所有资源是唯一的合法途径。
+ (LocalDataSender *)sharedInstance;

/*!
 * 发送登陆信息，本方法同时会判断socket连接的建立情况（并在未连接的情况下首先尝试建立连接）。
 * <p>
 * 本方法中已经默认进行了核心库的初始化，因而使用本类完成登陆时，就无需单独调用初始化方法[ClientCoreSDK initCore]了。
 * <p>
 * ========================================【补充说明】===================================================
 * 登陆代码中，进行与服务端连接与否的检查，是登陆逻辑中特有的（其它正常发送时不需要有这种检查），因为正常的数据通信系统中，
 * 登陆验证是第一步，也是必须的一步，此步里进行连接检查（如果未连接就进行连接的发起）、身份认证等，此步正常结束后，才是一
 * 个通信系统能正常工作的开始，这是很合理的逻辑。必竟，首次的连接建立和身份认证，不可能由登陆之外的逻辑来实现，否则那就很
 * 奇怪了！ ==============================================================================================
 *
 * @warning 本库的启动入口就是登陆过程触发的，因而要使本库能正常工作，请确保首先进行登陆操作。
 * @param loginInfo 提交到服务端的的登陆信息，具体请见：http://docs.52im.net/extend/docs/api/mobileimsdk/server_tcp/net/x52im/mobileimsdk/server/protocal/c/PLoginInfo.html
 * @return 0表示数据发出成功（因整个socket操作都是GCD异步完成，理论上发出成功不意味着实际被成功收到哦），否则返回的是错误码
 * @see [LocalDataSender sendImpl_:(NSData *)]
 */
- (int) sendLogin:(PLoginInfo *)loginInfo;

/*!
 * 发送注销登陆信息.
 * <p>
 * 本方法调用后，除非再次进行登陆过程，否则核心库将处于初始未初始化状态。
 *
 * @warning 此方法的调用将被本库理解为退出库的使用，本方法将会额外调用资源释放方法 [ClientCoreSDK releaseCore]，以保证资源释放。
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
 * 通用数据发送方法。
 *
 * @param dataContentWidthStr 要发送的数据内容（字符串方式组织）
 * @param to_user_id 要发送到的目标用户id
 * @return 0表示数据发出成功，否则返回的是错误码
 * @see #sendCommonData(Protocal)
 * @see DataFactoryC.createCommonData(String, int, int)
 */
- (int) sendCommonDataWithStr:(NSString *)dataContentWidthStr toUserId:(NSString *)to_user_id;

/*!
 * 通用数据发送方法。
 *
 * @param dataContentWidthStr 要发送的数据内容（字符串方式组织）
 * @param to_user_id 要发送到的目标用户id
 * @return 0表示数据发出成功，否则返回的是错误码
 * @see #sendCommonData(Protocal)
 * @see DataFactoryC.createCommonData(String, int, int)
 */
- (int) sendCommonDataWithStr:(NSString *)dataContentWidthStr toUserId:(NSString *)to_user_id withTypeu:(int)typeu;

/*!
 * 通用数据发送方法（默认不需要Qos支持）。
 *
 * @param dataContentWidthStr 要发送的数据内容（字符串方式组织）
 * @param to_user_id 要发送到的目标用户id
 * @param QoS true表示需QoS机制支持，不则不需要
 * @param fingerPrint QoS机制中要用到的指纹码（即消息包唯一id），生成方法见 [Protocal:genFingerPrint]
 * @return 0表示数据发出成功，否则返回的是错误码
 * @see [sendCommonData:Protocal]
 * @see [DataFactoryC:createCommonData(String, int, int, boolean, String)]
 */
- (int) sendCommonDataWithStr:(NSString *)dataContentWidthStr toUserId:(NSString *)to_user_id qos:(BOOL)QoS fp:(NSString *)fingerPrint withTypeu:(int)typeu;

/*!
 * 通用数据发送的根方法。
 *
 * @param p 要发送的内容（MobileIMSDK框架的“协议”DTO对象组织形式）
 * @return 0表示数据发出成功，否则返回的是错误码
 * @see [LocalDataSender sendImpl_:(NSData *)]
 */
- (int) sendCommonData:(Protocal *)p;

@end
