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
#import "ChatTransDataEvent.h"
#import "ChatBaseEvent.h"
#import "MessageQoSEvent.h"
#import "CompletionDefine.h"

@interface ClientCoreSDK : NSObject

//@property (nonatomic, assign) BOOL localDeviceNetworkOk;
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
