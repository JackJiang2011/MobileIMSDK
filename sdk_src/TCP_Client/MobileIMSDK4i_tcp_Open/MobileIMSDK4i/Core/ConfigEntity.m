//  ----------------------------------------------------------------------
//  Copyright (C) 2021  即时通讯网(52im.net) & Jack Jiang.
//  The MobileIMSDK_TCP (MobileIMSDK v6.x TCP版) Project.
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

#import "ConfigEntity.h"
#import "KeepAliveDaemon.h"

static NSString *serverIp = @"rbcore.52im.net";
static int serverPort = 8901;
static int localSendAndListeningPort = -1;//7801;
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

+ (void) setLocalSendAndListeningPort:(int)lPort
{
    localSendAndListeningPort = lPort;
}
+ (int) getLocalSendAndListeningPort
{
    return localSendAndListeningPort;
}

+ (void) setSenseMode:(SenseMode)mode
{
    int keepAliveInterval = 0;
    int networkConnectionTimeout = 0;
    
    switch(mode)
    {
        case SenseMode3S:
            keepAliveInterval = 3000;// 3s
            networkConnectionTimeout = keepAliveInterval * 1 + 2000;// 5s
            break;
        case SenseMode5S:
            keepAliveInterval = 5000;// 5s
            networkConnectionTimeout = keepAliveInterval * 1 + 3000;// 8s
            break;
        case SenseMode10S:
            keepAliveInterval = 10000;// 10s
            networkConnectionTimeout = keepAliveInterval * 1 + 5000;// 15s
            break;
        case SenseMode15S:
            keepAliveInterval = 15000;// 15s
            networkConnectionTimeout = keepAliveInterval * 1 + 5000;// 20s
            break;
        case SenseMode30S:
            keepAliveInterval = 30000;// 30s
            networkConnectionTimeout = keepAliveInterval * 1 + 5000;// 35s
            break;
        case SenseMode60S:
            keepAliveInterval = 60000;// 60s
            networkConnectionTimeout = keepAliveInterval * 1 + 5000;// 65s
            break;
        case SenseMode120S:
            keepAliveInterval = 120000;// 120s
            networkConnectionTimeout = keepAliveInterval * 1 + 5000;// 125s
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
