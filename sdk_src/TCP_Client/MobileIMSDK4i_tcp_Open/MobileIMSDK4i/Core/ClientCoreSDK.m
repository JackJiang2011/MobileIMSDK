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
    
//    NSLog(@"ClientCoreSDK已经init了！");
    
//    // 内部变量初始化
//    [self initCore];
    
    return self;
}

- (void)initCore
{
    if(!self._init)
    {
        // 变量初始化
//      self.localDeviceNetworkOk = NO;
        self.connectedToServer = NO;
        self.loginHasInit = NO;
        
        if(self.internetReachability == nil)
        {
            /*
             Observe the kNetworkReachabilityChangedNotification. When that notification is posted, the method reachabilityChanged will be called.
             */
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
    self.connectedToServer = NO;// 2021-07-09 add by Jack Jiang
    
    [[AutoReLoginDaemon sharedInstance] stop]; // 2014-11-08 add by Jack Jiang
    [[QoS4SendDaemon sharedInstance] stop];
    [[KeepAliveDaemon sharedInstance] stop];
//  [[LocalUDPDataReciever sharedInstance] stop];
    [[QoS4ReciveDaemon sharedInstance] stop];
    [[LocalSocketProvider sharedInstance] closeLocalSocket];

    //## Bug FIX: 20180103 by Jack Jiang START
    [[QoS4SendDaemon sharedInstance] clear];
    [[QoS4ReciveDaemon sharedInstance] clear];
    //## Bug FIX: 20180103 by Jack Jiang END

    [self.internetReachability stopNotifier];
    
    self._init = NO;
    self.loginHasInit = NO;
 // self.connectedToServer = NO;
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

/*
 * Called by Reachability whenever status changes.
 */
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
            statusString = NSLocalizedString(@"【IMCORE-TCP】【本地网络通知】检测本地网络连接断开了!", @"Text field text for access is not available");
            /* Minor interface detail- connectionRequired may return YES even when the host is unreachable. We cover that up here... */
            connectionRequired = NO;
            
//          self.localDeviceNetworkOk = false;
            [[LocalSocketProvider sharedInstance] closeLocalSocket];
            
            break;
        }
            
        case ReachableViaWWAN: // 蜂窝网络、3G网络等
        case ReachableViaWiFi: // WIFI
        {
            int wifi = (netStatus == ReachableViaWiFi);
            statusString= [NSString stringWithFormat:NSLocalizedString(@"【IMCORE-TCP】【本地网络通知】检测本地网络已连接上了! WIFI? %@", @""), wifi?@"YES":@"NO"];
            
//          self.localDeviceNetworkOk = true;
            [[LocalSocketProvider sharedInstance] closeLocalSocket];
            
            break;
        }
    }
    
    if (connectionRequired)
    {
        NSString *connectionRequiredFormatString = NSLocalizedString(@"【IMCORE-TCP】%@, Connection Required", @"Concatenation of status string with connection requirement");
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
