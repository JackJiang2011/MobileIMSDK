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
//  ProtocalQoS4ReciveProvider.m
//  MibileIMSDK4i_X (MobileIMSDK v3.0 at Summer 2017)
//
//  Created by JackJiang on 14/10/23.
//  Copyright (c) 2017年 52im.net. All rights reserved.
//

#import "QoS4ReciveDaemon.h"
#import "ClientCoreSDK.h"
#import "ToolKits.h"
#import "Protocal.h"
#import "NSMutableDictionary+Ext.h"


static int CHECK_INTERVAL = 5 * 60 * 1000;
static int MESSAGES_VALID_TIME = 10 * 60 * 1000;


@interface QoS4ReciveDaemon ()

@property (nonatomic, retain) NSMutableDictionary *recievedMessages;
@property (nonatomic, assign) BOOL running;
@property (nonatomic, assign) BOOL _excuting;
@property (nonatomic, retain) NSTimer *timer;
@property (nonatomic, copy) ObserverCompletion debugObserver_;

@end


@implementation QoS4ReciveDaemon

static QoS4ReciveDaemon *instance = nil;

+ (QoS4ReciveDaemon *)sharedInstance
{
    if (instance == nil)
    {
        instance = [[super allocWithZone:NULL] init];
    }
    return instance;
}

- (id)init
{
    if (![super init])
        return nil;
    
    NSLog(@"ProtocalQoS4ReciveProvider已经init了！");
    
    self.running = NO;
    self._excuting = NO;
    self.recievedMessages = [[NSMutableDictionary alloc] init];
    
    return self;
}

- (void) run
{
    if(!self._excuting)
    {
        self._excuting = YES;
        
        if([ClientCoreSDK isENABLED_DEBUG])
            NSLog(@"【IMCORE】【QoS接收方】++++++++++ START 暂存处理线程正在运行中，当前长度 %li", (unsigned long)[self.recievedMessages count]);

        NSArray *keyArr = [self.recievedMessages allKeys];
        for (NSString *key in keyArr)
        {
            NSNumber *objectValue = [self.recievedMessages objectForKey:key];
            long delta = [ToolKits getTimeStampWithMillisecond_l] - objectValue.longValue;
            if(delta >= MESSAGES_VALID_TIME)
            {
                if([ClientCoreSDK isENABLED_DEBUG])
                    NSLog(@"【IMCORE】【QoS接收方】指纹为%@的包已生存%li 毫秒(最大允许%d毫秒), 马上将删除之.", key, delta, MESSAGES_VALID_TIME);
                
                [self.recievedMessages removeObjectForKey:key];
            }
        }
    }

    if([ClientCoreSDK isENABLED_DEBUG])
        NSLog(@"【IMCORE】【QoS接收方】++++++++++ END 暂存处理线程正在运行中，当前长度 %li", (unsigned long)[self.recievedMessages count]);
    
    self._excuting = NO;
    
    // form DEBUG
    if(self.debugObserver_ != nil)
        self.debugObserver_(nil, [NSNumber numberWithInt:2]);
}

- (void) putImpl:(NSString *)fingerPrintOfProtocal
{
    if(fingerPrintOfProtocal != nil)
        [self.recievedMessages setValue:[NSNumber numberWithLong:[ToolKits getTimeStampWithMillisecond_l]] forKey:fingerPrintOfProtocal];
}

- (void) startup:(BOOL)immediately
{
    [self stop];

    if(self.recievedMessages != nil && [self.recievedMessages count] > 0)
    {
        NSArray *keyArr = [self.recievedMessages allKeys];
        for (NSString *key in keyArr)
        {
            [self putImpl:key];
        }
    }
    
    self.timer = [NSTimer scheduledTimerWithTimeInterval:CHECK_INTERVAL / 1000 target:self selector:@selector(run) userInfo:nil repeats:YES];
    if(immediately)
        [self.timer fire];

    self.running = YES;
    
    // form DEBUG
    if(self.debugObserver_ != nil)
        self.debugObserver_(nil, [NSNumber numberWithInt:1]);
}

- (void) stop
{
    if(self.timer != nil)
    {
        if([self.timer isValid])
           [self.timer invalidate];
        
        self.timer = nil;
    }
    self.running = NO;
    
    // form DEBUG
    if(self.debugObserver_ != nil)
        self.debugObserver_(nil, [NSNumber numberWithInt:0]);
}

- (BOOL) isRunning
{
    return self.running;
}

- (void) addRecieved:(Protocal *)p
{
    if(p != nil && p.QoS)
        [self addRecievedWithFingerPrint:p.fp];
}

- (void) addRecievedWithFingerPrint:(NSString *) fingerPrintOfProtocal
{
    if(fingerPrintOfProtocal == nil)
    {
        NSLog(@"【IMCORE】无效的 fingerPrintOfProtocal==null!");
        return;
    }
    
    if([self.recievedMessages containsKey:fingerPrintOfProtocal])
        NSLog(@"【IMCORE】【QoS接收方】指纹为 %@ 的消息已经存在于接收列表中，该消息重复了（原理可能是对方因未收到应答包而错误重传导致），更新收到时间戳哦.", fingerPrintOfProtocal);
    
    [self putImpl:fingerPrintOfProtocal];
}

- (BOOL) hasRecieved:(NSString *) fingerPrintOfProtocal
{
    return [self.recievedMessages containsKey:fingerPrintOfProtocal];
}

- (void) clear
{
    [self.recievedMessages removeAllObjects];
}

- (unsigned long) size
{
    return [self.recievedMessages count];
}

- (void) setDebugObserver:(ObserverCompletion)debugObserver
{
    self.debugObserver_ = debugObserver;
}


@end
