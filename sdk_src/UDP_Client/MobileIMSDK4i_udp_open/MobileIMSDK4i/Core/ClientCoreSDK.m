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

#import "ClientCoreSDK.h"
#import "ChatMessageEvent.h"
#import "ChatBaseEvent.h"
#import "MessageQoSEvent.h"
#import "MBReachability.h"
#import "QoS4SendDaemon.h"
#import "KeepAliveDaemon.h"
#import "LocalDataReciever.h"
#import "LocalSocketProvider.h"
#import "QoS4ReciveDaemon.h"
#import "AutoReLoginDaemon.h"


////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
#pragma mark - 静态全局类变量
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

static BOOL ENABLED_DEBUG = NO;
static BOOL autoReLogin = YES;


////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
#pragma mark - 私有API
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
@interface ClientCoreSDK ()

@property (nonatomic) BOOL _init;
@property (nonatomic) MBReachability *internetReachability;

@end


////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
#pragma mark - 本类的代码实现
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
@implementation ClientCoreSDK

static ClientCoreSDK *instance = nil;

//------------------------------------------------------
#pragma mark - 静态方法

+ (ClientCoreSDK *)sharedInstance
{
    static dispatch_once_t onceToken;
    dispatch_once(&onceToken, ^{
        instance = [[super allocWithZone:NULL] init];
    });
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


//------------------------------------------------------
#pragma mark - 实例方法

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
        self.connectedToServer = NO;
        self.loginHasInit = NO;
        
        if(self.internetReachability == nil)
        {
            [[NSNotificationCenter defaultCenter] addObserver:self selector:@selector(reachabilityChanged:) name:kReachabilityChangedNotification object:nil];
            self.internetReachability = [MBReachability reachabilityForInternetConnection];
        }
        [self.internetReachability startNotifier];
        self._init = YES;
        
        NSLog(@"ClientCoreSDK已经完成initCore了！");
    }
}

- (void) releaseCore
{
    [[AutoReLoginDaemon sharedInstance] stop]; 
    [[QoS4SendDaemon sharedInstance] stop];
    [[KeepAliveDaemon sharedInstance] stop];
    [[QoS4ReciveDaemon sharedInstance] stop];
    [[LocalSocketProvider sharedInstance] closeLocalSocket];

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

- (void) saveFirstLoginTime:(long)firstLoginTime
{
    if(self.currentLoginInfo != nil)
        self.currentLoginInfo.firstLoginTime = firstLoginTime;
}

// @deprecated 本方法已弃用。请使用 [ClientCoreSDK sharedInstance].currentLoginInfo.loginUserId 替代之！
- (NSString *) currentLoginUserId
{
    return self.currentLoginInfo.loginUserId;
}

- (BOOL)internetReachable
{
    NetworkStatus netStatus = [self.internetReachability currentReachabilityStatus];
    return netStatus == ReachableViaWWAN || netStatus == ReachableViaWiFi;
}

- (void) reachabilityChanged:(NSNotification *)note
{
    MBReachability* reachability = [note object];
    NSParameterAssert([reachability isKindOfClass:[MBReachability class]]);
    
    NetworkStatus netStatus = [reachability currentReachabilityStatus];
    BOOL connectionRequired = [reachability connectionRequired];
    NSString* statusString = @"";
    
    switch (netStatus)
    {
        case NotReachable:
        {
            statusString = NSLocalizedString(@"【IMCORE-UDP】【本地网络通知】检测本地网络连接断开了!", @"Text field text for access is not available");
            connectionRequired = NO;
            
//          self.localDeviceNetworkOk = false;
            [[LocalSocketProvider sharedInstance] closeLocalSocket];
            
            break;
        }
            
        case ReachableViaWWAN: // 蜂窝网络、3G网络等
        case ReachableViaWiFi: // WIFI
        {
            int wifi = (netStatus == ReachableViaWiFi);
            statusString= [NSString stringWithFormat:NSLocalizedString(@"【IMCORE-UDP】【本地网络通知】检测本地网络已连接上了! WIFI? %d", @""), wifi?@"YES":@"NO"];
            
//          self.localDeviceNetworkOk = true;
            [[LocalSocketProvider sharedInstance] closeLocalSocket];
            
            break;
        }
    }
    
    if (connectionRequired)
    {
        NSString *connectionRequiredFormatString = NSLocalizedString(@"【IMCORE-UDP】%@, Connection Required", @"Concatenation of status string with connection requirement");
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
