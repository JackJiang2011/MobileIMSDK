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

#import "AutoReLoginDaemon.h"
#import "ClientCoreSDK.h"
#import "LocalDataSender.h"
#import "LocalDataReciever.h"

static int AUTO_RE_LOGIN_INTERVAL = 2000;


@interface AutoReLoginDaemon ()

@property (nonatomic, assign) BOOL autoReLoginRunning;
@property (nonatomic, assign) BOOL _excuting;
@property (nonatomic, retain) NSTimer *timer;
@property (nonatomic, copy) ObserverCompletion debugObserver_;// block代码块一定要用copy属性，否则报错！

@end


@implementation AutoReLoginDaemon

static AutoReLoginDaemon *instance = nil;

+ (AutoReLoginDaemon *)sharedInstance
{
    if (instance == nil)
    {
        instance = [[super allocWithZone:NULL] init];
    }
    return instance;
}

+ (void) setAUTO_RE_LOGIN_INTERVAL:(int)autoReLoginInterval
{
    AUTO_RE_LOGIN_INTERVAL = autoReLoginInterval;
}
+ (int) getAUTO_RE_LOGIN_INTERVAL
{
    return AUTO_RE_LOGIN_INTERVAL;
}


- (id)init
{
    if (![super init])
        return nil;
    
    NSLog(@"AutoReLoginDaemon已经init了！");
    
    self.autoReLoginRunning = NO;
    self._excuting = NO;
    
    return self;
}

- (void) run
{
    if(!self._excuting)
    {
        self._excuting = YES;
        if([ClientCoreSDK isENABLED_DEBUG])
            NSLog(@"【IMCORE-UDP】自动重新登陆线程执行中, autoReLogin? %d...", [ClientCoreSDK isAutoReLogin]);
        int code = -1;

        if([ClientCoreSDK isAutoReLogin])
        {
            NSString *curLoginUserId = [ClientCoreSDK sharedInstance].currentLoginUserId;
            NSString *curLoginToken = [ClientCoreSDK sharedInstance].currentLoginToken;
            NSString *curLoginExtra = [ClientCoreSDK sharedInstance].currentLoginExtra;
            code = [[LocalDataSender sharedInstance] sendLogin:curLoginUserId withToken:curLoginToken andExtra:curLoginExtra];
            
            // form DEBUG
            if(self.debugObserver_ != nil)
                self.debugObserver_(nil, [NSNumber numberWithInt:2]);
        }
        
        if(code == 0)
        {
            if([ClientCoreSDK isENABLED_DEBUG])
                 NSLog(@"【IMCORE-UDP】自动重新登陆数据包已发出(iOS上无需自已启动UDP接收线程, GCDAsyncUDPTask自行解决了).");
        }
        
        //
        self._excuting = NO;
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
    self.autoReLoginRunning = NO;
    
    // form DEBUG
    if(self.debugObserver_ != nil)
        self.debugObserver_(nil, [NSNumber numberWithInt:0]);
}

- (void) start:(BOOL)immediately
{
    [self stop];
    
    self.timer = [NSTimer scheduledTimerWithTimeInterval:AUTO_RE_LOGIN_INTERVAL / 1000
                                                  target:self
                                                selector:@selector(run)
                                                userInfo:nil
                                                 repeats:YES];
    if(immediately)
        [self.timer fire];
    self.autoReLoginRunning = YES;
    
    // form DEBUG
    if(self.debugObserver_ != nil)
        self.debugObserver_(nil, [NSNumber numberWithInt:1]);
}

- (BOOL) isAutoReLoginRunning
{
    return self.autoReLoginRunning;
}

- (void) setDebugObserver:(ObserverCompletion)debugObserver
{
    self.debugObserver_ = debugObserver;
}

@end
