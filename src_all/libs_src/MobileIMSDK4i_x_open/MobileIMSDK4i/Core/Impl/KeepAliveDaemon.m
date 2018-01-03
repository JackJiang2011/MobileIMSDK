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
//  KeepAliveDaemon.m
//  MibileIMSDK4i_X (MobileIMSDK v3.0 at Summer 2017)
//
//  Created by JackJiang on 14/10/24.
//  Copyright (c) 2017年 52im.net. All rights reserved.
//

#import "KeepAliveDaemon.h"
#import "ToolKits.h"
#import "LocalUDPDataSender.h"
#import "ClientCoreSDK.h"


static int NETWORK_CONNECTION_TIME_OUT = 10 * 1000;
static int KEEP_ALIVE_INTERVAL = 3000;


@interface KeepAliveDaemon ()

@property (nonatomic, assign) BOOL keepAliveRunning;
@property (nonatomic, assign) long lastGetKeepAliveResponseFromServerTimstamp;
@property (nonatomic, copy) ObserverCompletion networkConnectionLostObserver_;
@property (nonatomic, copy) ObserverCompletion debugObserver_;
@property (nonatomic, assign) BOOL _excuting;
@property (nonatomic, retain) NSTimer *timer;

@end


@implementation KeepAliveDaemon

static KeepAliveDaemon *instance = nil;

+ (KeepAliveDaemon *)sharedInstance
{
    if (instance == nil)
    {
        instance = [[super allocWithZone:NULL] init];
    }
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


- (id)init
{
    if (![super init])
        return nil;
    
    NSLog(@"KeepAliveDaemon已经init了！");
    
    self.keepAliveRunning = NO;
    self.lastGetKeepAliveResponseFromServerTimstamp = 0;
    self._excuting = NO;
    
    return self;
}

- (void) run
{
    if(!self._excuting)
    {
        BOOL willStop = NO;
        self._excuting = true;
        if([ClientCoreSDK isENABLED_DEBUG])
            NSLog(@"【IMCORE】心跳线程执行中...");
        int code = [[LocalUDPDataSender sharedInstance] sendKeepAlive];
        
        // form DEBUG
        if(self.debugObserver_ != nil)
            self.debugObserver_(nil, [NSNumber numberWithInt:2]);
        
        BOOL isInitialedForKeepAlive = (self.lastGetKeepAliveResponseFromServerTimstamp == 0);
        if(code == 0 && self.lastGetKeepAliveResponseFromServerTimstamp == 0)
            self.lastGetKeepAliveResponseFromServerTimstamp = [ToolKits getTimeStampWithMillisecond_l];
        
        if(!isInitialedForKeepAlive)
        {
            long now = [ToolKits getTimeStampWithMillisecond_l];
            if(now - self.lastGetKeepAliveResponseFromServerTimstamp >= NETWORK_CONNECTION_TIME_OUT)
            {
                [self stop];
                if(self.networkConnectionLostObserver_ != nil)
                    self.networkConnectionLostObserver_(nil, nil);
                
                willStop = YES;
            }
        }
        
        self._excuting = NO;
        if(!willStop)
        {
            ;
        }
        else
        {
            [self stop];
        }
    }
}

- (void) stop
{
    if(self.timer != nil)
    {
        if([self.timer isValid])
            [self.timer invalidate];
        
        self.timer = nil;
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
    
    self.timer = [NSTimer scheduledTimerWithTimeInterval:KEEP_ALIVE_INTERVAL / 1000 target:self selector:@selector(run) userInfo:nil repeats:YES];
    if(immediately)
       [self.timer fire];
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
    self.lastGetKeepAliveResponseFromServerTimstamp = [ToolKits getTimeStampWithMillisecond_l];
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
