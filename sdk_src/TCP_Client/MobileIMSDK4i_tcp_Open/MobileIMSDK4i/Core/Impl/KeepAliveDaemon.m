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

#import "KeepAliveDaemon.h"
#import "ToolKits.h"
#import "LocalDataSender.h"
#import "ClientCoreSDK.h"


////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
#pragma mark - 静态全局类变量
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

static int KEEP_ALIVE_INTERVAL = 15000;//3000;
static int NETWORK_CONNECTION_TIME_OUT = 20 * 1000;//10 * 1000;

static int NETWORK_CONNECTION_TIME_OUT_CHECK_INTERVAL = 2 * 1000;

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
#pragma mark - 私有API
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

@interface KeepAliveDaemon ()

@property (nonatomic, assign) BOOL keepAliveRunning;
@property (nonatomic, assign) long lastGetKeepAliveResponseFromServerTimstamp;

@property (nonatomic, copy) ObserverCompletion networkConnectionLostObserver_;
@property (nonatomic, copy) ObserverCompletion debugObserver_;

@property (nonatomic, assign) BOOL keepAliveTaskExcuting;
@property (nonatomic, retain) NSTimer *keepAliveTimer;

@property (nonatomic, retain) NSTimer *keepAliveTimeoutTimer;

@end


////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
#pragma mark - 本类的代码实现
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

@implementation KeepAliveDaemon

static KeepAliveDaemon *instance = nil;


//-----------------------------------------------------------------------------------
#pragma mark - 外部可调用的静态方法

+ (KeepAliveDaemon *)sharedInstance
{
    static dispatch_once_t onceToken;
    dispatch_once(&onceToken, ^{
        instance = [[super allocWithZone:NULL] init];
    });
    return instance;
}

+ (void) setKEEP_ALIVE_INTERVAL:(int)keepAliveTimeWithMils
{
    KEEP_ALIVE_INTERVAL = keepAliveTimeWithMils;
}
+ (int) getKEEP_ALIVE_INTERVAL
{
    return KEEP_ALIVE_INTERVAL;
}

+ (void) setNETWORK_CONNECTION_TIME_OUT:(int)networkConnectionTimeout
{
    NETWORK_CONNECTION_TIME_OUT = networkConnectionTimeout;
}
+ (int) getNETWORK_CONNECTION_TIME_OUT
{
    return NETWORK_CONNECTION_TIME_OUT;
}


//-----------------------------------------------------------------------------------
#pragma mark - 仅内部可调用的方法

- (id)init
{
    if (![super init])
        return nil;
    
    NSLog(@"KeepAliveDaemon已经init了！");
    
    self.keepAliveRunning = NO;
    self.lastGetKeepAliveResponseFromServerTimstamp = 0;
    self.keepAliveTaskExcuting = NO;
    
    return self;
}

- (void) doKeepAlive
{
    if(!self.keepAliveTaskExcuting)
    {
        self.keepAliveTaskExcuting = true;
        
        if([ClientCoreSDK isENABLED_DEBUG])
            NSLog(@"【IMCORE-TCP】心跳包[发送]线程执行中...");
        
        [[LocalDataSender sharedInstance] sendKeepAlive];
        
        // form DEBUG
        if(self.debugObserver_ != nil)
            self.debugObserver_(nil, [NSNumber numberWithInt:2]);
        
        BOOL isInitialedForKeepAlive = [self isInitialedForKeepAlive];
        //## Bug FIX 20190513 v4.0.1 START
        //## 解决极端情况下手机网络断开时，无法进入下面的"断开"通知流程
        if(isInitialedForKeepAlive)
            self.lastGetKeepAliveResponseFromServerTimstamp = [ToolKits getTimeStampWithMillisecond_l];
        //## Bug FIX 20190513 v4.0.1 END
        
        self.keepAliveTaskExcuting = NO;
    }
}

- (void) doTimeoutCheck
{
	if([ClientCoreSDK isENABLED_DEBUG])
        NSLog(@"【IMCORE-TCP】心跳[超时检查]线程执行中...");
		
    BOOL isInitialedForKeepAlive = [self isInitialedForKeepAlive];
    if(!isInitialedForKeepAlive)
    {
        long now = [ToolKits getTimeStampWithMillisecond_l];
        if(now - self.lastGetKeepAliveResponseFromServerTimstamp >= NETWORK_CONNECTION_TIME_OUT)
        {
            [self notifyConnectionLost];
        }
    }
}

- (BOOL) isInitialedForKeepAlive
{
    return (self.lastGetKeepAliveResponseFromServerTimstamp == 0);
}


//-----------------------------------------------------------------------------------
#pragma mark - 外部可调用的类方法

- (void)notifyConnectionLost
{
    [self stop];
    if(self.networkConnectionLostObserver_ != nil)
        self.networkConnectionLostObserver_(nil, nil);
}

- (void) stop
{
    if(self.keepAliveTimeoutTimer != nil)
    {
        if([self.keepAliveTimeoutTimer isValid])
            [self.keepAliveTimeoutTimer invalidate];
        
        self.keepAliveTimeoutTimer = nil;
    }
    
    if(self.keepAliveTimer != nil)
    {
        if([self.keepAliveTimer isValid])
            [self.keepAliveTimer invalidate];
        
        self.keepAliveTimer = nil;
    }
    self.keepAliveRunning = NO;
    self.lastGetKeepAliveResponseFromServerTimstamp = 0;
    
    // for DEBUG
    if(self.debugObserver_ != nil)
        self.debugObserver_(nil, [NSNumber numberWithInt:0]);
}

- (void) start:(BOOL)immediately
{
    [self stop];
    
    self.keepAliveTimer = [NSTimer scheduledTimerWithTimeInterval:KEEP_ALIVE_INTERVAL / 1000 target:self selector:@selector(doKeepAlive) userInfo:nil repeats:YES];
    if(immediately)
       [self.keepAliveTimer fire];
    
    self.keepAliveTimeoutTimer = [NSTimer scheduledTimerWithTimeInterval:NETWORK_CONNECTION_TIME_OUT_CHECK_INTERVAL / 1000 target:self selector:@selector(doTimeoutCheck) userInfo:nil repeats:YES];
    if(immediately)
       [self.keepAliveTimeoutTimer fire];

    self.keepAliveRunning = YES;
    
    // form DEBUG
    if(self.debugObserver_ != nil)
        self.debugObserver_(nil, [NSNumber numberWithInt:1]);
}

- (BOOL) isKeepAliveRunning
{
    return self.keepAliveRunning;
}

- (void) updateGetKeepAliveResponseFromServerTimstamp
{
    self.lastGetKeepAliveResponseFromServerTimstamp = [ToolKits getTimeStampWithMillisecond_l];//System.currentTimeMillis();
}

- (void) setNetworkConnectionLostObserver:(ObserverCompletion)networkConnLostObserver
{
    self.networkConnectionLostObserver_ = networkConnLostObserver;
}

- (void) setDebugObserver:(ObserverCompletion)debugObserver
{
    self.debugObserver_ = debugObserver;
}

@end
