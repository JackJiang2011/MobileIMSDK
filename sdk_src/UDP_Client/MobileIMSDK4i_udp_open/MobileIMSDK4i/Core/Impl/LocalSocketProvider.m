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

#import "LocalSocketProvider.h"
#import "MBGCDAsyncUdpSocket.h"
#import "ClientCoreSDK.h"
#import "ConfigEntity.h"
#import "ErrorCode.h"
#import "LocalDataReciever.h"
#import "CompletionDefine.h"


////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
#pragma mark - 私有API
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
@interface LocalSocketProvider ()

@property (nonatomic, retain) MBGCDAsyncUdpSocket *localSocket;
@property (nonatomic, copy) ConnectionCompletion connectionCompletionOnce_;

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

- (MBGCDAsyncUdpSocket *)resetLocalSocket
{
    [self closeLocalSocket];
    
    if([ClientCoreSDK isENABLED_DEBUG])
        NSLog(@"【IMCORE-UDP】new GCDAsyncUdpSocket中...");

    self.localSocket = [[MBGCDAsyncUdpSocket alloc] initWithDelegate:self delegateQueue:dispatch_get_main_queue()];

    int port = [ConfigEntity getLocalSendAndListeningPort];
    if (port < 0 || port > 65535)
        port = 0;
    NSError *error = nil;
    if (![self.localSocket bindToPort:port error:&error])
    {
        NSLog(@"【IMCORE-UDP】localSocket创建时出错，原因是 bindToPort: %@", error);
        return nil;
    }

    if (![self.localSocket beginReceiving:&error])
    {
        [self closeLocalSocket];
        
        NSLog(@"【IMCORE-UDP】localSocket创建时出错，原因是 beginReceiving: %@", error);
        return nil;
    }
    
    if([ClientCoreSDK isENABLED_DEBUG])
        NSLog(@"【IMCORE-UDP】localSocket创建已成功完成.");
    
    return self.localSocket;
}

- (int)tryConnectToHost:(NSError **)errPtr withSocket:(MBGCDAsyncUdpSocket *)skt completion:(ConnectionCompletion)finish
{
    if([ConfigEntity getServerIp] == nil)
    {
        if([ClientCoreSDK isENABLED_DEBUG])
            NSLog(@"【IMCORE-UDP】tryConnectToHost到目标主机%@:%d没有成功，ConfigEntity.server_ip==null!", [ConfigEntity getServerIp], [ConfigEntity getServerPort]);
        return ForC_TO_SERVER_NET_INFO_NOT_SETUP;
    }
    
    NSError *connectError = nil;
    if(finish != nil)
       [self setConnectionCompletionOnce_:finish];
    [skt connectToHost:[ConfigEntity getServerIp] onPort:[ConfigEntity getServerPort] error:&connectError];
    if(connectError != nil)
    {
        if([ClientCoreSDK isENABLED_DEBUG])
            NSLog(@"【IMCORE-UDP】localSocket尝试发出连接到目标主机%@:%d的动作时出错了：%@.(此前isConnected?%d)", [ConfigEntity getServerIp], [ConfigEntity getServerPort], connectError, [skt isConnected]);
        return ForC_BAD_CONNECT_TO_SERVER;
    }
    else
    {
        if([ClientCoreSDK isENABLED_DEBUG])
            NSLog(@"【IMCORE-UDP】localSocket尝试发出连接到目标主机%@:%d的动作成功了.(此前isConnected?%d)", [ConfigEntity getServerIp], [ConfigEntity getServerPort], [skt isConnected]);
        return COMMON_CODE_OK;
    }
}

- (BOOL) isLocalSocketReady
{
    return self.localSocket != nil && ![self.localSocket isClosed];
}

- (MBGCDAsyncUdpSocket *) getLocalSocket
{
    if([self isLocalSocketReady])
    {
        if([ClientCoreSDK isENABLED_DEBUG])
            NSLog(@"【IMCORE-UDP】isLocalSocketReady()==true，直接返回本地socket引用哦。");
        return self.localSocket;
    }
    else
    {
        if([ClientCoreSDK isENABLED_DEBUG])
            NSLog(@"【IMCORE-UDP】isLocalSocketReady()==false，需要先resetLocalSocket()...");
        return [self resetLocalSocket];
    }
}

- (void) closeLocalSocket
{
    if([ClientCoreSDK isENABLED_DEBUG])
        NSLog(@"【IMCORE-UDP】正在closeLocalSocket()...");
    if(self.localSocket != nil)
    {
        [self.localSocket close];
        self.localSocket = nil;
    }
    else
    {
        NSLog(@"【IMCORE-UDP】Socket处于未初化状态（可能是您还未登陆），无需关闭。");
    }
}

- (void) setConnectionObserver:(ConnectionCompletion)connObserver
{
    self.connectionCompletionOnce_ = connObserver;
}


////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
#pragma mark - GCDAsyncUdpSocketDelegate代码实现
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

- (void)udpSocket:(MBGCDAsyncUdpSocket *)sock didSendDataWithTag:(long)tag
{
    if([ClientCoreSDK isENABLED_DEBUG])
        NSLog(@"【UDP_SOCKET】tag为%li的NSData已成功发出.", tag);
}

- (void)udpSocket:(MBGCDAsyncUdpSocket *)sock didNotSendDataWithTag:(long)tag dueToError:(NSError *)error
{
    if([ClientCoreSDK isENABLED_DEBUG])
        NSLog(@"【UDP_SOCKET】tag为%li的NSData没有发送成功，原因是%@", tag, error);
}

- (void)udpSocket:(MBGCDAsyncUdpSocket *)sock didReceiveData:(NSData *)data
      fromAddress:(NSData *)address
withFilterContext:(id)filterContext
{
    NSString *msg = [[NSString alloc] initWithData:data encoding:NSUTF8StringEncoding];
    if (msg)
    {
        if([ClientCoreSDK isENABLED_DEBUG])
            NSLog(@"【UDP_SOCKET】RECV: %@", msg);
        
        [[LocalDataReciever sharedInstance] handleProtocal:data];
    }
    else
    {
        NSString *host = nil;
        uint16_t port = 0;
        [MBGCDAsyncUdpSocket getHost:&host port:&port fromAddress:address];
        
        if([ClientCoreSDK isENABLED_DEBUG])
            NSLog(@"【UDP_SOCKET】RECV: Unknown message from: %@:%hu", host, port);
    }
}

- (void)udpSocket:(MBGCDAsyncUdpSocket *)sock didConnectToAddress:(NSData *)address
{
    if([ClientCoreSDK isENABLED_DEBUG])
        NSLog(@"【UDP_SOCKET】成收到的了UDP的connect反馈, isCOnnected?%d", [sock isConnected]);
    if(self.connectionCompletionOnce_ != nil)
        self.connectionCompletionOnce_(YES);
}

- (void)udpSocket:(MBGCDAsyncUdpSocket *)sock didNotConnect:(NSError *)error
{
    if([ClientCoreSDK isENABLED_DEBUG])
        NSLog(@"【UDP_SOCKET】成收到的了UDP的connect反馈，但连接没有成功, isCOnnected?%d", [sock isConnected]);
    if(self.connectionCompletionOnce_ != nil)
        self.connectionCompletionOnce_(NO);
}

@end
