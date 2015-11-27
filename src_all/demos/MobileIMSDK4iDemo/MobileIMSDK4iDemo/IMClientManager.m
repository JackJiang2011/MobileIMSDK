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
//  IMClientManager.m
//  MobileIMSDK4iDemo
//
//  Created by JackJiang on 15/11/8.
//  Copyright © 2015年 openmob.net. All rights reserved.
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
