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
//  IMClientManager.m
//  MibileIMSDK4iDemo_X (A demo for MobileIMSDK v3.0 at Summer 2017)
//
//  Created by JackJiang on 15/11/8.
//  Copyright © 2017年 52im.net. All rights reserved.
//

#import "IMClientManager.h"
#import "ClientCoreSDK.h"
#import "ConfigEntity.h"


///////////////////////////////////////////////////////////////////////////////////////////
#pragma mark - 私有API
///////////////////////////////////////////////////////////////////////////////////////////

@interface IMClientManager ()

/* MobileIMSDK是否已被初始化. true表示已初化完成，否则未初始化. */
@property (nonatomic) BOOL _init;
//
@property (strong, nonatomic) ChatBaseEventImpl *baseEventListener;
//
@property (strong, nonatomic) ChatTransDataEventImpl *transDataListener;
//
@property (strong, nonatomic) MessageQoSEventImpl *messageQoSListener;

@end


///////////////////////////////////////////////////////////////////////////////////////////
#pragma mark - 本类的代码实现
///////////////////////////////////////////////////////////////////////////////////////////

@implementation IMClientManager

// 本类的单例对象
static IMClientManager *instance = nil;

+ (IMClientManager *)sharedInstance
{
    if (instance == nil)
    {
        instance = [[super allocWithZone:NULL] init];
    }
    return instance;
}

/*
 *  重写init实例方法实现。
 *
 *  @return
 *  @see [NSObject init:]
 */
- (id)init
{
    if (![super init])
        return nil;
    
    [self initMobileIMSDK];
    
    return self;
}

- (void)initMobileIMSDK
{
    if(!self._init)
    {
        // 设置AppKey
        [ConfigEntity registerWithAppKey:@"5418023dfd98c579b6001741"];
        
        // 设置服务器ip和服务器端口
//      [ConfigEntity setServerIp:@"rbcore.openmob.net"];
//      [ConfigEntity setServerPort:7901];
        
        // 使用以下代码表示不绑定固定port（由系统自动分配），否则使用默认的7801端口
//      [ConfigEntity setLocalUdpSendAndListeningPort:-1];
        
        // RainbowCore核心IM框架的敏感度模式设置
//      [ConfigEntity setSenseMode:SenseMode10S];
        
        // 开启DEBUG信息输出
        [ClientCoreSDK setENABLED_DEBUG:YES];
        
        // 设置事件回调
        self.baseEventListener = [[ChatBaseEventImpl alloc] init];
        self.transDataListener = [[ChatTransDataEventImpl alloc] init];
        self.messageQoSListener = [[MessageQoSEventImpl alloc] init];
        [ClientCoreSDK sharedInstance].chatBaseEvent = self.baseEventListener;
        [ClientCoreSDK sharedInstance].chatTransDataEvent = self.transDataListener;
        [ClientCoreSDK sharedInstance].messageQoSEvent = self.messageQoSListener;
        
        self._init = YES;
    }
}

- (void)releaseMobileIMSDK
{
    [[ClientCoreSDK sharedInstance] releaseCore];
}

- (ChatTransDataEventImpl *) getTransDataListener
{
    return self.transDataListener;
}
- (ChatBaseEventImpl *) getBaseEventListener
{
    return self.baseEventListener;
}
- (MessageQoSEventImpl *) getMessageQoSListener
{
    return self.messageQoSListener;
}

@end
