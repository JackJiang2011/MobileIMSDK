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
//  ClientCoreSDK.h
//  MibileIMSDK4i_X (MobileIMSDK v3.0 at Summer 2017)
//
//  Created by JackJiang on 14/10/21.
//  Copyright (c) 2017年 52im.net. All rights reserved.
//

#import <Foundation/Foundation.h>
#import "ClientCoreSDK.h"
#import "ChatTransDataEvent.h"
#import "ChatBaseEvent.h"
#import "MessageQoSEvent.h"
#import "CompletionDefine.h"

@interface ClientCoreSDK : NSObject

@property (nonatomic, assign) BOOL localDeviceNetworkOk;
@property (nonatomic, assign) BOOL connectedToServer;
@property (nonatomic, assign) BOOL loginHasInit;
@property (nonatomic, retain) NSString *currentLoginUserId;
@property (nonatomic, retain) NSString *currentLoginToken;
@property (nonatomic, retain) NSString *currentLoginExtra;

@property (nonatomic, retain) id<ChatTransDataEvent> chatTransDataEvent;
@property (nonatomic, retain) id<ChatBaseEvent> chatBaseEvent;
@property (nonatomic, retain) id<MessageQoSEvent> messageQoSEvent;

+ (ClientCoreSDK *)sharedInstance;
+ (BOOL) isENABLED_DEBUG;
+ (void) setENABLED_DEBUG:(BOOL)enabledDebug;
+ (BOOL) isAutoReLogin;
+ (void) setAutoReLogin:(BOOL)arl;

- (BOOL) isInitialed;
- (void)initCore;
- (void) releaseCore;

@end
