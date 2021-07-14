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

/*!
 * 登陆信息DTO类.
 *
 * @author Jack Jiang(http://www.52im.net/thread-2792-1-1.html)
 * @version 1.0
 */
@interface PLoginInfo : NSObject

/*! 登陆时提交的准一id，保证唯一就可以通信，可能是登陆用户名、也可能是任意不重复的id等，具体意义由业务层决定 */
@property (nonatomic, retain) NSString* loginUserId;

/*! 登陆时提交到服务端用于身份鉴别和合法性检查的token，它可能是登陆密码，也可能是通过前置单点登陆接口拿到的token等，具体意义由业务层决定 */
@property (nonatomic, retain) NSString* loginToken;

/*!
 * 额外信息字符串。本字段目前为保留字段，供上层应用自行放置需要的内容。
 * @since 2.1.6 
 */
@property (nonatomic, retain) NSString* extra;

/*!
 * 客户端首次登陆时间（此时间由服务端在客户端首次登陆时返回的登陆信息中提供，客户端后绪在
 * 掉重连时带上本字段，以便服务端用于多端互踢判定逻辑中使用）。此值不设置则默认应置为0。
 * <p>
 * 此时间由服务端提供，且直到客户端主动登陆，此时间不会被更新或重置（重连时也不会重置）。
 * <p>
 * 此时间目前的唯一用途：用于多端登陆时互踢的依据，防止在客户端未收到服务端“踢出”指令的
 * 情况下，再次自动重连过来（通过此时间就可以判断出此客户端登陆时间之后又有新的端登陆，从
 * 而拒绝此次重连，防止后登陆的端被之前这个“老”的端在它的网络恢复后错误地挤出“新”登陆的）。
 * <p>
 * 本次互踢思路，请见我在此帖中的回复：<a href="http://www.52im.net/thread-2879-1-1.html">http://www.52im.net/thread-2879-1-1.html</a>
 *
 * @since 6.0
 */
@property (nonatomic, assign) long firstLoginTime;


@end
