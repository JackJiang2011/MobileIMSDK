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

#import "LocalDataSender.h"
#import "ProtocalFactory.h"
#import "ClientCoreSDK.h"
#import "KeepAliveDaemon.h"
#import "CharsetHelper.h"
#import "QoS4SendDaemon.h"
#import "ErrorCode.h"
#import "MBGCDAsyncSocket.h"
#import "LocalSocketProvider.h"
#import "ConfigEntity.h"
#import "TCPUtils.h"
#import "CompletionDefine.h"

@implementation LocalDataSender

static LocalDataSender *instance = nil;

//-----------------------------------------------------------------------------------
#pragma mark - 仅内部可调用的方法

- (int) checkBeforeSend
{
    if(![[ClientCoreSDK sharedInstance] isInitialed])
        return ForC_CLIENT_SDK_NO_INITIALED;
    
    //## Bug FIX：20200811 by JackJiang
//    if(![ClientCoreSDK sharedInstance].localDeviceNetworkOk)
//    {
//        NSLog(@"【IMCORE-TCP】本地网络不能工作，send数据没有继续!");
//        return ForC_LOCAL_NETWORK_NOT_WORKING;
//    }
    //## Bug FIX：END
    
    return COMMON_CODE_OK;
}

- (int) sendImpl_:(NSData *)fullProtocalBytes
{
    int codeForCheck = [self checkBeforeSend];
    if(codeForCheck != COMMON_CODE_OK)
        return codeForCheck;

    MBGCDAsyncSocket *ds = [[LocalSocketProvider sharedInstance] getLocalSocket];
    if(ds != nil && [ds isConnected])// && [ClientCoreSDK sharedInstance].connectedToServer)
    {
        return [TCPUtils send:ds withData:fullProtocalBytes] ? COMMON_CODE_OK : COMMON_DATA_SEND_FAILD;
    }
    else
    {
        NSLog(@"【IMCORE-TCP】scocket未连接，无法发送，本条将被忽略（data=%@）!", fullProtocalBytes);
        return COMMON_DATA_SEND_FAILD;//COMMON_CODE_OK;
    }
}

- (void) putToQoS:(Protocal *)p
{
    if(p.QoS && ![[QoS4SendDaemon sharedInstance] exist:p.fp])
        [[QoS4SendDaemon sharedInstance] put:p];
}


//-----------------------------------------------------------------------------------
#pragma mark - 外部可调用的方法

+ (LocalDataSender *)sharedInstance
{
    static dispatch_once_t onceToken;
    dispatch_once(&onceToken, ^{
        instance = [[super allocWithZone:NULL] init];
    });
    return instance;
}

- (int) sendLogin:(PLoginInfo *)loginInfo
{
    [[ClientCoreSDK sharedInstance] initCore];
    
    int codeForCheck = [self checkBeforeSend];
    if(codeForCheck != COMMON_CODE_OK)
        return codeForCheck;
    
    MBGCDAsyncSocket *ds = [[LocalSocketProvider sharedInstance] getLocalSocket];
    if(![[LocalSocketProvider sharedInstance] isLocalSocketReady])
    {
        ConnectionCompletion observerBlock = ^(BOOL connectResult) {
            if(connectResult)
                [self sendLoginImpl:loginInfo];
            else
               NSLog(@"【IMCORE-TCP】[来自GCDAsyncSocket的连接结果回调通知]socket连接失败，本次登陆信息未成功发出！");
        };
        [[LocalSocketProvider sharedInstance] setConnectionObserver:observerBlock];

        NSError *connectError = nil;
        int connectCode = [[LocalSocketProvider sharedInstance] tryConnectToHost:&connectError withSocket:ds completion:observerBlock];
        if(connectCode != COMMON_CODE_OK)
            return connectCode;
        else
            return COMMON_CODE_OK;
    }
    else
    {
        return [self sendLoginImpl:loginInfo];
    }
}

- (int)sendLoginImpl:(PLoginInfo *)loginInfo
{
    NSData *b = [[ProtocalFactory createPLoginInfo:loginInfo] toBytes];
    
    int code = [self sendImpl_:b];
    if(code == 0)
    {
        [[ClientCoreSDK sharedInstance] setCurrentLoginInfo:loginInfo];
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
