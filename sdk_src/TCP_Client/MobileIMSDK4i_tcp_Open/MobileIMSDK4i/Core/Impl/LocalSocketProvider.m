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

#import "LocalSocketProvider.h"
#import "ClientCoreSDK.h"
#import "ConfigEntity.h"
#import "ErrorCode.h"
#import "LocalDataReciever.h"
#import "CompletionDefine.h"
#import "TCPFrameCodec.h"
#import "KeepAliveDaemon.h"

#define TCP_TAG_FIXED_LENGTH_HEADER       990
#define TCP_TAG_RESPONSE_BODY             991

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
#pragma mark - 私有API
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
@interface LocalSocketProvider ()

@property (nonatomic, retain) MBGCDAsyncSocket *localSocket;
@property (nonatomic, copy) ConnectionCompletion connectionCompletionOnce_;// block代码块一定要用copy属性，否则报错！

@end


////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
#pragma mark - 本类的代码实现
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
@implementation LocalSocketProvider

static LocalSocketProvider *instance = nil;

+ (LocalSocketProvider *)sharedInstance
{
    static dispatch_once_t onceToken;
    dispatch_once(&onceToken, ^{
        instance = [[super allocWithZone:NULL] init];
    });
    return instance;
}

- (MBGCDAsyncSocket *)resetLocalSocket
{
    [self closeLocalSocket];
    
    if([ClientCoreSDK isENABLED_DEBUG])
        NSLog(@"【IMCORE-TCP】new GCDAsyncSocket中...");
    
    // ** Setup our socket.
    // The socket will invoke our delegate methods using the usual delegate paradigm.
    // However, it will invoke the delegate methods on a specified GCD delegate dispatch queue.
    //
    // Now we can configure the delegate dispatch queues however we want.
    // We could simply use the main dispatch queue, so the delegate methods are invoked on the main thread.
    // Or we could use a dedicated dispatch queue, which could be helpful if we were doing a lot of processing.
    //
    // The best approach for your application will depend upon convenience, requirements and performance.
    //
    // For this simple example, we're just going to use the main thread.
    self.localSocket = [[MBGCDAsyncSocket alloc] initWithDelegate:self delegateQueue:dispatch_get_main_queue()];
    
    return self.localSocket;
}

- (int)tryConnectToHost:(NSError **)errPtr withSocket:(MBGCDAsyncSocket *)skt completion:(ConnectionCompletion)finish
{
    if([ConfigEntity getServerIp] == nil)
    {
        if([ClientCoreSDK isENABLED_DEBUG])
            NSLog(@"【IMCORE-TCP】tryConnectToHost到目标主机%@:%d没有成功，ConfigEntity.server_ip==null!", [ConfigEntity getServerIp], [ConfigEntity getServerPort]);
        return ForC_TO_SERVER_NET_INFO_NOT_SETUP;
    }
    
    NSError *connectError = nil;
    if(finish != nil)
       [self setConnectionCompletionOnce_:finish];
    
    if(![skt connectToHost:[ConfigEntity getServerIp] onPort:[ConfigEntity getServerPort] error:&connectError])
    {
        if([ClientCoreSDK isENABLED_DEBUG])
            NSLog(@"【IMCORE-TCP】localTCPSocket尝试发出连接到目标主机%@:%d的动作时出错了：%@.", [ConfigEntity getServerIp], [ConfigEntity getServerPort], connectError);
        return ForC_BAD_CONNECT_TO_SERVER;
    }
    else
    {
        if([ClientCoreSDK isENABLED_DEBUG])
            NSLog(@"【IMCORE-TCP】localTCPSocket尝试发出连接到目标主机%@:%d的动作成功了.", [ConfigEntity getServerIp], [ConfigEntity getServerPort]);
        return COMMON_CODE_OK;
    }
}

- (BOOL) isLocalSocketReady
{
    return self.localSocket != nil && [self.localSocket isConnected];
}

- (MBGCDAsyncSocket *) getLocalSocket
{
    if([self isLocalSocketReady])
    {
        if([ClientCoreSDK isENABLED_DEBUG])
            NSLog(@"【IMCORE-TCP】isLocalSocketReady()==true，直接返回本地socket引用哦。");
        return self.localSocket;
    }
    else
    {
        if([ClientCoreSDK isENABLED_DEBUG])
            NSLog(@"【IMCORE-TCP】isLocalSocketReady()==false，需要先resetLocalUDPSocket()...");
        return [self resetLocalSocket];
    }
}

- (void) closeLocalSocket
{
    if([ClientCoreSDK isENABLED_DEBUG])
        NSLog(@"【IMCORE-TCP】正在closeLocalSocket()...");
    
    if(self.localSocket != nil)
    {
        [self.localSocket disconnect];
        self.localSocket = nil;
    }
    else
    {
        NSLog(@"【IMCORE-TCP】Socket处于未初化状态（可能是您还未登陆），无需关闭。");
    }
}

- (void) setConnectionObserver:(ConnectionCompletion)connObserver
{
    self.connectionCompletionOnce_ = connObserver;
}


////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
#pragma mark - GCDAsyncSocketDelegate代码实现
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

- (void)socket:(MBGCDAsyncSocket *)sock didWriteDataWithTag:(long)tag
{
    // You could add checks here
    if([ClientCoreSDK isENABLED_DEBUG])
        NSLog(@"【IMCORE-TCP-SOCKET】tag为%li的数据已成功Write完成.", tag);
}

- (void)socket:(MBGCDAsyncSocket *)socket didReadData:(NSData *)data withTag:(long)tag
{
    if([ClientCoreSDK isENABLED_DEBUG])
        NSLog(@"【IMCORE-TCP-SOCKET】RECV【原始帧】：%@", data);
    
    if (tag == TCP_TAG_FIXED_LENGTH_HEADER)
    {
        int bodyLength = [TCPFrameCodec decodeBodyLength:data];
        if(bodyLength <= 0 || bodyLength > [TCPFrameCodec getTCP_FRAME_MAX_BODY_LENGTH])
        {
            if([ClientCoreSDK isENABLED_DEBUG])
                NSLog(@"【IMCORE-TCP-SOCKET】【CAUTION】【原始帧-头】中实际解析出的bodyLength=%d (而SDK中最大允许长度为>0 && <= %d)，它是不合法的，将断开本次scoket连接！", bodyLength, [TCPFrameCodec getTCP_FRAME_MAX_BODY_LENGTH]);
            [socket disconnect];
        }
        else
        {
            if([ClientCoreSDK isENABLED_DEBUG])
                NSLog(@"【IMCORE-TCP-SOCKET】已正常从【原始帧-头】中解码出bodyLength=%d，马上开始正式读取Body数据。。。", bodyLength);
            [socket readDataToLength:bodyLength withTimeout:-1 tag:TCP_TAG_RESPONSE_BODY];
        }
    }
    else if (tag == TCP_TAG_RESPONSE_BODY)
    {
        NSString *msg = [[NSString alloc] initWithData:data encoding:NSUTF8StringEncoding];
        
        if([ClientCoreSDK isENABLED_DEBUG])
            NSLog(@"【IMCORE-TCP-SOCKET】已正常从【原始帧-体】中解码出msg=%@", msg);
        [[LocalDataReciever sharedInstance] handleProtocal:data];
        [socket readDataToLength:[TCPFrameCodec getTCP_FRAME_FIXED_HEADER_LENGTH] withTimeout:-1 tag:TCP_TAG_FIXED_LENGTH_HEADER];
    }
    else
    {
        if([ClientCoreSDK isENABLED_DEBUG])
            NSLog(@"【IMCORE-TCP-SOCKET】RECV: 未知的socket:didReadData tag=%ld，它是不合法的，将断开本次scoket连接！", tag);
        [socket disconnect];
    }
}

- (void)socket:(MBGCDAsyncSocket *)socket didConnectToHost:(NSString *)host port:(uint16_t)port
{
    if([ClientCoreSDK isENABLED_DEBUG])
        NSLog(@"【IMCORE-TCP-SOCKET】成收到的了TCP的connect反馈, isConnected?%d", [socket isConnected]);

    if(self.connectionCompletionOnce_ != nil)
        self.connectionCompletionOnce_(YES);
    
    [socket readDataToLength:[TCPFrameCodec getTCP_FRAME_FIXED_HEADER_LENGTH] withTimeout:-1 tag:TCP_TAG_FIXED_LENGTH_HEADER];
}

- (void)socketDidDisconnect:(MBGCDAsyncSocket *)sock withError:(nullable NSError *)err
{
    if([ClientCoreSDK isENABLED_DEBUG])
    {
        BOOL hasError = (err != nil);
        NSLog(@"【IMCORE-TCP-SOCKET】连接已断开%@，socket.isConnected?%d，ClientCoreSDK.connectedToServer?%d，error=%@", hasError?@"【请关注错误信息】":@"", [sock isConnected], [ClientCoreSDK sharedInstance].connectedToServer, err);
    }
    
    if([ClientCoreSDK sharedInstance].connectedToServer)
    {
        if([ClientCoreSDK isENABLED_DEBUG])
            NSLog(@"【IMCORE-TCP-SOCKET】连接已断开，立即提前进入框架的“通信通道”断开处理逻辑(而不是等心跳线程探测到，那就已经比较迟了)......");
        [[KeepAliveDaemon sharedInstance] notifyConnectionLost];
    }
}

@end
