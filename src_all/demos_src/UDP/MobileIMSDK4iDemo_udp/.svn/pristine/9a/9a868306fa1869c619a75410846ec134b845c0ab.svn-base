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
//  IMClientManager.h
//  MobileIMSDK4iDemo
//
//  Created by JackJiang on 15/11/8.
//  Copyright © 2015年 openmob.net. All rights reserved.
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
