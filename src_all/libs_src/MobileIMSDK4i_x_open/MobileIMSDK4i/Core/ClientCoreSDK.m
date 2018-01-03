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
//  ClientCoreSDK.m
//  MibileIMSDK4i_X (MobileIMSDK v3.0 at Summer 2017)
//
//  Created by JackJiang on 14/10/21.
//  Copyright (c) 2017年 52im.net. All rights reserved.
//

#import "ClientCoreSDK.h"
#import "ChatTransDataEvent.h"
#import "ChatBaseEvent.h"
#import "MessageQoSEvent.h"
#import "Reachability.h"
#import "QoS4SendDaemon.h"
#import "KeepAliveDaemon.h"
#import "LocalUDPDataReciever.h"
#import "LocalUDPSocketProvider.h"
#import "QoS4ReciveDaemon.h"
#import "AutoReLoginDaemon.h"


static BOOL ENABLED_DEBUG = NO;
static BOOL autoReLogin = YES;


@interface ClientCoreSDK ()

@property (nonatomic) BOOL _init;
@property (nonatomic) Reachability *internetReachability;

@end


@implementation ClientCoreSDK

static ClientCoreSDK *instance = nil;

+ (ClientCoreSDK *)sharedInstance
{
    if (instance == nil)
    {
        instance = [[super allocWithZone:NULL] init];
    }
    return instance;
}

+ (BOOL) isENABLED_DEBUG
{
    return ENABLED_DEBUG;
}
+ (void) setENABLED_DEBUG:(BOOL)enabledDebug
{
    ENABLED_DEBUG = enabledDebug;
}

+ (BOOL) isAutoReLogin
{
    return autoReLogin;
}
+ (void) setAutoReLogin:(BOOL)arl
{
    autoReLogin = arl;
}

- (id)init
{
    if (![super init])
        return nil;
    
    return self;
}

- (void)initCore
{
    if(!self._init)
    {
        self.localDeviceNetworkOk = NO;
        self.connectedToServer = NO;
        self.loginHasInit = NO;
        
        if(self.internetReachability == nil)
        {
            [[NSNotificationCenter defaultCenter] addObserver:self selector:@selector(reachabilityChanged:) name:kReachabilityChangedNotification object:nil];
            self.internetReachability = [Reachability reachabilityForInternetConnection];
        }
        [self.internetReachability startNotifier];
        self.localDeviceNetworkOk = [self internetReachable];
        self._init = YES;
        
        NSLog(@"ClientCoreSDK已经完成initCore了！");
    }
}

- (void) releaseCore
{
    [[AutoReLoginDaemon sharedInstance] stop]; 
    [[QoS4ReciveDaemon sharedInstance] stop];
    [[KeepAliveDaemon sharedInstance] stop];
    [[QoS4SendDaemon sharedInstance] stop];
    [[LocalUDPSocketProvider sharedInstance] closeLocalUDPSocket];

    [[QoS4SendDaemon sharedInstance] clear];
    [[QoS4ReciveDaemon sharedInstance] clear];

    [self.internetReachability stopNotifier];
    
    self._init = NO;
    self.loginHasInit = NO;
    self.connectedToServer = NO;
}


- (BOOL) isInitialed
{
    return self._init;
}

- (BOOL)internetReachable
{
    NetworkStatus netStatus = [self.internetReachability currentReachabilityStatus];
    return netStatus == ReachableViaWWAN || netStatus == ReachableViaWiFi;
}

- (void) reachabilityChanged:(NSNotification *)note
{
    Reachability* reachability = [note object];
    NSParameterAssert([reachability isKindOfClass:[Reachability class]]);
    
    NetworkStatus netStatus = [reachability currentReachabilityStatus];
    BOOL connectionRequired = [reachability connectionRequired];
    NSString* statusString = @"";
    
    switch (netStatus)
    {
        case NotReachable:
        {
            statusString = NSLocalizedString(@"【IMCORE】【本地网络通知】检测本地网络连接断开了!", @"Text field text for access is not available");
            connectionRequired = NO;
            
            self.localDeviceNetworkOk = false;
            [[LocalUDPSocketProvider sharedInstance] closeLocalUDPSocket];
            
            break;
        }
            
        case ReachableViaWWAN: // 蜂窝网络、3G网络等
        case ReachableViaWiFi: // WIFI
        {
            int wifi = (netStatus == ReachableViaWiFi);
            statusString= [NSString stringWithFormat:NSLocalizedString(@"【IMCORE】【本地网络通知】检测本地网络已连接上了! WIFI? %d", @""), wifi?@"YES":@"NO"];
            
            self.localDeviceNetworkOk = true;
            [[LocalUDPSocketProvider sharedInstance] closeLocalUDPSocket];
            
            break;
        }
    }
    
    if (connectionRequired)
    {
        NSString *connectionRequiredFormatString = NSLocalizedString(@"【IMCORE】%@, Connection Required", @"Concatenation of status string with connection requirement");
        statusString = [NSString stringWithFormat:connectionRequiredFormatString, statusString];
    }
    
    if(ENABLED_DEBUG)
        NSLog(@"%@", statusString);
}

- (void)dealloc
{
    [[NSNotificationCenter defaultCenter] removeObserver:self name:kReachabilityChangedNotification object:nil];
}

@end
