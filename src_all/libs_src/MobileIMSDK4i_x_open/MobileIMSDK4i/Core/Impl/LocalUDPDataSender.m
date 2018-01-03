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
//  LocalUDPDataSender.m
//  MibileIMSDK4i_X (MobileIMSDK v3.0 at Summer 2017)
//
//  Created by JackJiang on 14/10/27.
//  Copyright (c) 2017年 52im.net. All rights reserved.
//

#import "LocalUDPDataSender.h"
#import "ProtocalFactory.h"
#import "ClientCoreSDK.h"
#import "KeepAliveDaemon.h"
#import "CharsetHelper.h"
#import "QoS4SendDaemon.h"
#import "ErrorCode.h"
#import "GCDAsyncUdpSocket.h"
#import "LocalUDPSocketProvider.h"
#import "ConfigEntity.h"
#import "UDPUtils.h"
#import "CompletionDefine.h"

@implementation LocalUDPDataSender

static LocalUDPDataSender *instance = nil;


- (int) sendImpl_:(NSData *)fullProtocalBytes
{
    if(![[ClientCoreSDK sharedInstance] isInitialed])
        return ForC_CLIENT_SDK_NO_INITIALED;
    
    if(![ClientCoreSDK sharedInstance].localDeviceNetworkOk)
    {
        NSLog(@"【IMCORE】本地网络不能工作，send数据没有继续!");
        return ForC_LOCAL_NETWORK_NOT_WORKING;
    }

    GCDAsyncUdpSocket *ds = [[LocalUDPSocketProvider sharedInstance] getLocalUDPSocket];
    if(ds != nil && ![ds isConnected])
    {
        ConnectionCompletion observerBlock = ^(BOOL connectResult) {
            if(connectResult)
                [UDPUtils send:ds withData:fullProtocalBytes];
            else
            {
            }
        };
        [[LocalUDPSocketProvider sharedInstance] setConnectionObserver:observerBlock];

        NSError *connectError = nil;
        int connectCode = [[LocalUDPSocketProvider sharedInstance] tryConnectToHost:&connectError withSocket:ds completion:observerBlock];
        if(connectCode != COMMON_CODE_OK)
            return connectCode;
        else
            return COMMON_CODE_OK;
    }
    else
    {
        return [UDPUtils send:ds withData:fullProtocalBytes] ? COMMON_CODE_OK : COMMON_DATA_SEND_FAILD;
    }
}

- (void) putToQoS:(Protocal *)p
{
    if(p.QoS && ![[QoS4ReciveDaemon sharedInstance] exist:p.fp])
        [[QoS4ReciveDaemon sharedInstance] put:p];
}

+ (LocalUDPDataSender *)sharedInstance
{
    if (instance == nil)
    {
        instance = [[super allocWithZone:NULL] init];
    }
    return instance;
}

- (int) sendLogin:(NSString *)loginUserId withToken:(NSString *)loginToken
{
    return [self sendLogin:loginUserId withToken:loginToken andExtra:nil];
}

- (int) sendLogin:(NSString *)loginUserId withToken:(NSString *)loginToken andExtra:(NSString *)extra
{
    [[ClientCoreSDK sharedInstance] initCore];
    
    NSData *b = [[ProtocalFactory createPLoginInfo:loginUserId withToken:loginToken andExtra:extra] toBytes];
    int code = [self sendImpl_:b];
    if(code == 0)
    {
        [[ClientCoreSDK sharedInstance] setCurrentLoginUserId:loginUserId];
        [[ClientCoreSDK sharedInstance] setCurrentLoginToken:loginToken];
        [[ClientCoreSDK sharedInstance] setCurrentLoginExtra:extra];
    }
    
    return code;
}

- (int) sendLoginout
{
    int code = COMMON_CODE_OK;
    if([ClientCoreSDK sharedInstance].loginHasInit)
    {
        NSString *loginUserId = [ClientCoreSDK sharedInstance].currentLoginUserId;
        NSData *b = [[ProtocalFactory createPLoginoutInfo:loginUserId] toBytes];
        code = [self sendImpl_:b];
        if(code == 0)
        {
            [[KeepAliveDaemon sharedInstance] stop];
            [[ClientCoreSDK sharedInstance] setLoginHasInit:NO];
        }
    }
    
    [[ClientCoreSDK sharedInstance] releaseCore];
    
    return code;
}

- (int) sendKeepAlive
{
    NSString *currentLoginUserId = [[ClientCoreSDK sharedInstance] currentLoginUserId];
    NSData *b = [[ProtocalFactory createPKeepAlive: currentLoginUserId] toBytes];
    return [self sendImpl_:b];
}

- (int) sendCommonDataWithStr:(NSString *)dataContentWidthStr toUserId:(NSString *)to_user_id
{
    return [self sendCommonDataWithStr:dataContentWidthStr toUserId:to_user_id withTypeu:-1];
}

- (int) sendCommonDataWithStr:(NSString *)dataContentWidthStr toUserId:(NSString *)to_user_id withTypeu:(int)typeu
{
    NSString *currentLoginUserId = [[ClientCoreSDK sharedInstance] currentLoginUserId];
    Protocal *p = [ProtocalFactory createCommonData:dataContentWidthStr fromUserId:currentLoginUserId toUserId:to_user_id withTypeu:typeu];
    return [self sendCommonData:p];
}

- (int) sendCommonDataWithStr:(NSString *)dataContentWidthStr toUserId:(NSString *)to_user_id qos:(BOOL)QoS fp:(NSString *)fingerPrint withTypeu:(int)typeu
{
    NSString *currentLoginUserId = [[ClientCoreSDK sharedInstance] currentLoginUserId];
    Protocal *p = [ProtocalFactory createCommonData:dataContentWidthStr fromUserId:currentLoginUserId toUserId:to_user_id qos:QoS fp:fingerPrint withTypeu:typeu];
    return [self sendCommonData:p];
}

- (int) sendCommonData:(Protocal *)p
{
    @synchronized(self)
    {
        if(p != nil)
        {
            NSData *b = [p toBytes];
            int code = [self sendImpl_:b];
            if(code == 0)
            {
                [self putToQoS:p];
            }
            return code;
        }
        else
            return COMMON_INVALID_PROTOCAL;
    }
}

@end
