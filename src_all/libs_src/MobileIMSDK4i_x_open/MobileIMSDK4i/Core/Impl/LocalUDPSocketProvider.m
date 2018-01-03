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
//  LocalUDPSocketProvider.m
//  MibileIMSDK4i_X (MobileIMSDK v3.0 at Summer 2017)
//
//  Created by JackJiang on 14/10/22.
//  Copyright (c) 2017年 52im.net. All rights reserved.
//

#import "LocalUDPSocketProvider.h"
#import "GCDAsyncUdpSocket.h"
#import "ClientCoreSDK.h"
#import "ConfigEntity.h"
#import "ErrorCode.h"
#import "LocalUDPDataReciever.h"
#import "CompletionDefine.h"


@interface LocalUDPSocketProvider ()

@property (nonatomic, retain) GCDAsyncUdpSocket *localUDPSocket;
@property (nonatomic, copy) ConnectionCompletion connectionCompletionOnce_;

@end


@implementation LocalUDPSocketProvider

static LocalUDPSocketProvider *instance = nil;

+ (LocalUDPSocketProvider *)sharedInstance
{
    if (instance == nil)
    {
        instance = [[super allocWithZone:NULL] init];
    }
    return instance;
}

- (GCDAsyncUdpSocket *)resetLocalUDPSocket
{
    [self closeLocalUDPSocket];
    
    if([ClientCoreSDK isENABLED_DEBUG])
        NSLog(@"【IMCORE】new GCDAsyncUdpSocket中...");

    self.localUDPSocket = [[GCDAsyncUdpSocket alloc] initWithDelegate:self delegateQueue:dispatch_get_main_queue()];

    int port = [ConfigEntity getLocalUdpSendAndListeningPort];
    if (port < 0 || port > 65535)
        port = 0;
    NSError *error = nil;
    if (![self.localUDPSocket bindToPort:port error:&error])
    {
        NSLog(@"【IMCORE】localUDPSocket创建时出错，原因是 bindToPort: %@", error);
        return nil;
    }

    if (![self.localUDPSocket beginReceiving:&error])
    {
        [self closeLocalUDPSocket];
        
        NSLog(@"【IMCORE】localUDPSocket创建时出错，原因是 beginReceiving: %@", error);
        return nil;
    }
    
    if([ClientCoreSDK isENABLED_DEBUG])
        NSLog(@"【IMCORE】localUDPSocket创建已成功完成.");
    
    return self.localUDPSocket;
}

- (int)tryConnectToHost:(NSError **)errPtr withSocket:(GCDAsyncUdpSocket *)skt completion:(ConnectionCompletion)finish
{
    if([ConfigEntity getServerIp] == nil)
    {
        if([ClientCoreSDK isENABLED_DEBUG])
            NSLog(@"【IMCORE】tryConnectToHost到目标主机%@:%d没有成功，ConfigEntity.server_ip==null!", [ConfigEntity getServerIp], [ConfigEntity getServerPort]);
        return ForC_TO_SERVER_NET_INFO_NOT_SETUP;
    }
    
    NSError *connectError = nil;
    if(finish != nil)
       [self setConnectionCompletionOnce_:finish];
    [skt connectToHost:[ConfigEntity getServerIp] onPort:[ConfigEntity getServerPort] error:&connectError];
    if(connectError != nil)
    {
        if([ClientCoreSDK isENABLED_DEBUG])
            NSLog(@"【IMCORE】localUDPSocket尝试发出连接到目标主机%@:%d的动作时出错了：%@.(此前isConnected?%d)", [ConfigEntity getServerIp], [ConfigEntity getServerPort], connectError, [skt isConnected]);
        return ForC_BAD_CONNECT_TO_SERVER;
    }
    else
    {
        if([ClientCoreSDK isENABLED_DEBUG])
            NSLog(@"【IMCORE】localUDPSocket尝试发出连接到目标主机%@:%d的动作成功了.(此前isConnected?%d)", [ConfigEntity getServerIp], [ConfigEntity getServerPort], [skt isConnected]);
        return COMMON_CODE_OK;
    }
}

- (BOOL) isLocalUDPSocketReady
{
    return self.localUDPSocket != nil && ![self.localUDPSocket isClosed];
}

- (GCDAsyncUdpSocket *) getLocalUDPSocket
{
    if([self isLocalUDPSocketReady])
    {
        if([ClientCoreSDK isENABLED_DEBUG])
            NSLog(@"【IMCORE】isLocalUDPSocketReady()==true，直接返回本地socket引用哦。");
        return self.localUDPSocket;
    }
    else
    {
        if([ClientCoreSDK isENABLED_DEBUG])
            NSLog(@"【IMCORE】isLocalUDPSocketReady()==false，需要先resetLocalUDPSocket()...");
        return [self resetLocalUDPSocket];
    }
}

- (void) closeLocalUDPSocket
{
    if([ClientCoreSDK isENABLED_DEBUG])
        NSLog(@"【IMCORE】正在closeLocalUDPSocket()...");
    if(self.localUDPSocket != nil)
    {
        [self.localUDPSocket close];
        self.localUDPSocket = nil;
    }
    else
    {
        NSLog(@"【IMCORE】Socket处于未初化状态（可能是您还未登陆），无需关闭。");
    }
}

- (void) setConnectionObserver:(ConnectionCompletion)connObserver
{
    self.connectionCompletionOnce_ = connObserver;
}


- (void)udpSocket:(GCDAsyncUdpSocket *)sock didSendDataWithTag:(long)tag
{
    if([ClientCoreSDK isENABLED_DEBUG])
        NSLog(@"【UDP_SOCKET】tag为%li的NSData已成功发出.", tag);
}

- (void)udpSocket:(GCDAsyncUdpSocket *)sock didNotSendDataWithTag:(long)tag dueToError:(NSError *)error
{
    if([ClientCoreSDK isENABLED_DEBUG])
        NSLog(@"【UDP_SOCKET】tag为%li的NSData没有发送成功，原因是%@", tag, error);
}

- (void)udpSocket:(GCDAsyncUdpSocket *)sock didReceiveData:(NSData *)data
      fromAddress:(NSData *)address
withFilterContext:(id)filterContext
{
    NSString *msg = [[NSString alloc] initWithData:data encoding:NSUTF8StringEncoding];
    if (msg)
    {
        if([ClientCoreSDK isENABLED_DEBUG])
            NSLog(@"【UDP_SOCKET】RECV: %@", msg);
        
        [[LocalUDPDataReciever sharedInstance] handleProtocal:data];
    }
    else
    {
        NSString *host = nil;
        uint16_t port = 0;
        [GCDAsyncUdpSocket getHost:&host port:&port fromAddress:address];
        
        if([ClientCoreSDK isENABLED_DEBUG])
            NSLog(@"【UDP_SOCKET】RECV: Unknown message from: %@:%hu", host, port);
    }
}

- (void)udpSocket:(GCDAsyncUdpSocket *)sock didConnectToAddress:(NSData *)address
{
    if([ClientCoreSDK isENABLED_DEBUG])
        NSLog(@"【UDP_SOCKET】成收到的了UDP的connect反馈, isCOnnected?%d", [sock isConnected]);
    if(self.connectionCompletionOnce_ != nil)
        self.connectionCompletionOnce_(YES);
}

- (void)udpSocket:(GCDAsyncUdpSocket *)sock didNotConnect:(NSError *)error
{
    if([ClientCoreSDK isENABLED_DEBUG])
        NSLog(@"【UDP_SOCKET】成收到的了UDP的connect反馈，但连接没有成功, isCOnnected?%d", [sock isConnected]);
    if(self.connectionCompletionOnce_ != nil)
        self.connectionCompletionOnce_(NO);
}

@end
