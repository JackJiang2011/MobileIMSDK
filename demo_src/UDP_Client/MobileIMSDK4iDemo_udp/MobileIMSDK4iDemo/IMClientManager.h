//  ----------------------------------------------------------------------
//  Copyright (C) 2021  即时通讯网(52im.net) & Jack Jiang.
//  The MobileIMSDK_UDP (MobileIMSDK v6.x UDP版) Project.
//  All rights reserved.
//
//  > Github地址: https://github.com/JackJiang2011/MobileIMSDK
//  > 文档地址:    http://www.52im.net/forum-89-1.html
//  > 技术社区：   http://www.52im.net/
//  > 技术交流群： 215477170 (http://www.52im.net/topic-qqgroup.html)
//  > 作者公众号： “即时通讯技术圈】”，欢迎关注！
//  > 联系作者：   http://www.52im.net/thread-2792-1-1.html
//
//  "即时通讯网(52im.net) - 即时通讯开发者社区!" 推荐开源工程。
//  ----------------------------------------------------------------------

#import <Foundation/Foundation.h>
#import "ChatBaseEventImpl.h"
#import "ChatMessageEventImpl.h"
#import "MessageQoSEventImpl.h"

@interface IMClientManager : NSObject

/*!
 * 取得本类实例的唯一公开方法。
 * <p>
 * 本类目前在APP运行中是以单例的形式存活，请一定注意这一点哦。
 *
 * @return
 */
+ (IMClientManager *)sharedInstance;

/*!
 * MobileIMSDK的初始化方法。
 */
- (void)initMobileIMSDK;

/*!
 * MobileIMSDK的资源释放方法（退出SDK时使用）。
 */
- (void)releaseMobileIMSDK;


/**
 * 重置init标识。
 * <p>
 * <b>重要说明：</b>不退出APP的情况下，重新登陆时记得调用一下本方法，不然再
 * 次调用 {@link #initMobileIMSDK()} 时也不会重新初始化MobileIMSDK（
 * 详见 {@link #initMobileIMSDK()}代码）而报 code=203错误！
 *
 */
- (void)resetInitFlag;

- (ChatMessageEventImpl *) getTransDataListener;
- (ChatBaseEventImpl *) getBaseEventListener;
- (MessageQoSEventImpl *) getMessageQoSListener;

@end
