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

#import "QoS4ReciveDaemon.h"
#import "ClientCoreSDK.h"
#import "ToolKits.h"
#import "Protocal.h"
#import "NSMutableDictionary+Ext.h"


////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
#pragma mark - 静态全局类变量
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

static int CHECK_INTERVAL = 5 * 60 * 1000; // 5分钟
static int MESSAGES_VALID_TIME = 10 * 60 * 1000;// 10分钟


////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
#pragma mark - 私有API
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

@interface QoS4ReciveDaemon ()

@property (nonatomic, retain) NSMutableDictionary *recievedMessages;
@property (nonatomic, assign) BOOL running;
@property (nonatomic, assign) BOOL _excuting;
@property (nonatomic, retain) NSTimer *timer;
/* !本属性仅作DEBUG之用：DEBUG事件观察者 */
@property (nonatomic, copy) ObserverCompletion debugObserver_;// block代码块一定要用copy属性，否则报错！

@end

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
#pragma mark - 本类的代码实现
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

@implementation QoS4ReciveDaemon

static QoS4ReciveDaemon *instance = nil;

+ (QoS4ReciveDaemon *)sharedInstance
{
    static dispatch_once_t onceToken;
    dispatch_once(&onceToken, ^{
        instance = [[super allocWithZone:NULL] init];
    });
    return instance;
}


//-----------------------------------------------------------------------------------
#pragma mark - 仅内部可调用的方法

- (id)init
{
    if (![super init])
        return nil;
    
    NSLog(@"QoS4ReciveDaemon已经init了！");
    
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
            NSLog(@"【IMCORE-TCP】【QoS接收方】+++++ START 暂存处理线程正在运行中，当前长度 %li", (unsigned long)[self.recievedMessages count]);

        NSArray *keyArr = [self.recievedMessages allKeys];//只要增加这一个就可以解决问题了，相对比较方便
        for (NSString *key in keyArr)//修改dic为keyArr，这样枚举的时候使用的是keyArr，修改的是另一个
        {
            NSNumber *objectValue = [self.recievedMessages objectForKey:key];
            long delta = [ToolKits getTimeStampWithMillisecond_l] - (objectValue == nil ? 0: objectValue.longValue);
            if(delta >= MESSAGES_VALID_TIME)
            {
                if([ClientCoreSDK isENABLED_DEBUG])
                    NSLog(@"【IMCORE-TCP】【QoS接收方】指纹为%@的包已生存%li 毫秒(最大允许%d毫秒), 马上将删除之.", key, delta, MESSAGES_VALID_TIME);
                
                [self.recievedMessages removeObjectForKey:key];
            }
        }
    }

    if([ClientCoreSDK isENABLED_DEBUG])
        NSLog(@"【IMCORE-TCP】【QoS接收方】+++++ END 暂存处理线程正在运行中，当前长度 %li", (unsigned long)[self.recievedMessages count]);
    
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


//-----------------------------------------------------------------------------------
#pragma mark - 外部可调用的方法

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
        NSLog(@"【IMCORE-TCP】无效的 fingerPrintOfProtocal==null!");
        return;
    }
    
    if([self.recievedMessages containsKey:fingerPrintOfProtocal])
        NSLog(@"【IMCORE-TCP】【QoS接收方】指纹为 %@ 的消息已经存在于接收列表中，该消息重复了（原理可能是对方因未收到应答包而错误重传导致），更新收到时间戳哦.", fingerPrintOfProtocal);
    
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
