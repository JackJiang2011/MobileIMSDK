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
//  ConfigEntity.m
//  MibileIMSDK4i_X (MobileIMSDK v3.0 at Summer 2017)
//
//  Created by JackJiang on 14/10/22.
//  Copyright (c) 2017年 52im.net. All rights reserved.
//

#import "ConfigEntity.h"
#import "KeepAliveDaemon.h"

static NSString *serverIp = @"openmob.net";
static int serverPort = 7901;
static int localUdpSendAndListeningPort = 7801;
static NSString *appKey = nil;

@implementation ConfigEntity

+ (void)registerWithAppKey:(NSString *)key
{
    appKey = key;
}

+ (void) setServerIp:(NSString*)sIp
{
    serverIp = sIp;
}
+ (NSString *)getServerIp
{
    return serverIp;
}

+ (void) setServerPort:(int)sPort
{
    serverPort = sPort;
}
+ (int) getServerPort
{
    return serverPort;
}

+ (void) setLocalUdpSendAndListeningPort:(int)lPort
{
    localUdpSendAndListeningPort = lPort;
}
+ (int) getLocalUdpSendAndListeningPort
{
    return localUdpSendAndListeningPort;
}

+ (void) setSenseMode:(SenseMode)mode
{
    int keepAliveInterval = 0;
    int networkConnectionTimeout = 0;
    
    switch(mode)
    {
        case SenseMode3S:
            keepAliveInterval = 3000;
            networkConnectionTimeout = 3000 * 3 + 1000;
            break;
        case SenseMode10S:
            keepAliveInterval = 10000;
            networkConnectionTimeout = 10000 * 2 + 1000;
            break;
        case SenseMode30S:
            keepAliveInterval = 30000;
            networkConnectionTimeout = 30000 * 2 + 1000;
            break;
        case SenseMode60S:
            keepAliveInterval = 60000;
            networkConnectionTimeout = 60000 * 2 + 1000;
            break;
        case SenseMode120S:
            keepAliveInterval = 120000;
            networkConnectionTimeout = 120000 * 2 + 1000;
            break;
    }
    
    if(keepAliveInterval > 0)
    {
        [KeepAliveDaemon setKEEP_ALIVE_INTERVAL:keepAliveInterval];
    }
    
    if(networkConnectionTimeout > 0)
    {
        [KeepAliveDaemon setNETWORK_CONNECTION_TIME_OUT:networkConnectionTimeout];
    }
}


@end
