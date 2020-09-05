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
//  IMClientManager.h
//  MibileIMSDK4iDemo_X (A demo for MobileIMSDK v3.0 at Summer 2017)
//
//  Created by JackJiang on 15/11/8.
//  Copyright © 2017年 52im.net. All rights reserved.
//

#import <Foundation/Foundation.h>
#import "ChatBaseEventImpl.h"
#import "ChatTransDataEventImpl.h"
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

- (void)initMobileIMSDK;

- (void)releaseMobileIMSDK;

- (ChatTransDataEventImpl *) getTransDataListener;
- (ChatBaseEventImpl *) getBaseEventListener;
- (MessageQoSEventImpl *) getMessageQoSListener;

@end
