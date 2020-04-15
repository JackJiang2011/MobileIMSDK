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

#import "LocalUDPDataReciever.h"
#import "Protocal.h"
#import "LocalUDPDataSender.h"
#import "ProtocalFactory.h"
#import "ClientCoreSDK.h"
#import "ErrorCode.h"
#import "QoS4ReciveDaemon.h"
#import "ProtocalType.h"
#import "ChatTransDataEvent.h"
#import "KeepAliveDaemon.h"
#import "PLoginInfoResponse.h"
#import "AutoReLoginDaemon.h"
#import "QoS4SendDaemon.h"
#import "PErrorResponse.h"
#import "LocalUDPSocketProvider.h"

@implementation LocalUDPDataReciever

static LocalUDPDataReciever *instance = nil;

+ (LocalUDPDataReciever *)sharedInstance
{
    if (instance == nil)
    {
        instance = [[super allocWithZone:NULL] init];
    }
    return instance;
}

- (void) handleProtocal:(NSData *)originalProtocalJSONData
{
    if(originalProtocalJSONData == nil)
        return;
    
    Protocal *pFromServer =
        [ProtocalFactory parse:originalProtocalJSONData];
    if(pFromServer.QoS)
    {
        if(pFromServer.type == FROM_SERVER_TYPE_OF_RESPONSE_LOGIN
           && [ProtocalFactory parsePLoginInfoResponse:pFromServer.dataContent].code != 0)
        {
            if([ClientCoreSDK isENABLED_DEBUG])
                NSLog(@"【IMCORE】【BugFIX】这是服务端的登陆返回响应包，且服务端判定登陆失败(即code!=0)，本次无需发送ACK应答包！");
        }
        else
        {
            if([[QoS4ReciveDaemon sharedInstance] hasRecieved:pFromServer.fp])
            {
                if([ClientCoreSDK isENABLED_DEBUG])
                    NSLog(@"【IMCORE】【QoS机制】%@已经存在于发送列表中，这是重复包，通知应用层收到该包罗！", pFromServer.fp);

                [[QoS4ReciveDaemon sharedInstance] addRecieved:pFromServer];
                [self sendRecievedBack:pFromServer];

                return;
            }

            [[QoS4ReciveDaemon sharedInstance] addRecieved:pFromServer];
            [self sendRecievedBack:pFromServer];
        }
    }
    
    switch(pFromServer.type)
    {
        case FROM_CLIENT_TYPE_OF_COMMON_DATA:
        {
            if([ClientCoreSDK sharedInstance].chatTransDataEvent != nil)
            {
                [[ClientCoreSDK sharedInstance].chatTransDataEvent onTransBuffer:pFromServer.fp withUserId:pFromServer.from andContent:pFromServer.dataContent andTypeu:pFromServer.typeu];
            }
            break;
        }
        case FROM_SERVER_TYPE_OF_RESPONSE_KEEP_ALIVE:
        {
            if([ClientCoreSDK isENABLED_DEBUG])
                NSLog(@"【IMCORE】收到服务端回过来的Keep Alive心跳响应包.");
            [[KeepAliveDaemon sharedInstance] updateGetKeepAliveResponseFromServerTimstamp];
            break;
        }
        case FROM_CLIENT_TYPE_OF_RECIVED:
        {
            NSString *theFingerPrint = pFromServer.dataContent;
            if([ClientCoreSDK isENABLED_DEBUG])
                NSLog(@"【IMCORE】【QoS】收到%@发过来的指纹为%@的应答包.", pFromServer.from, theFingerPrint);
            
            if([ClientCoreSDK sharedInstance].messageQoSEvent != nil)
                [[ClientCoreSDK sharedInstance].messageQoSEvent messagesBeReceived:theFingerPrint];
            
            [[QoS4SendDaemon sharedInstance] remove:theFingerPrint];
            
            break;
        }
        case FROM_SERVER_TYPE_OF_RESPONSE_LOGIN:
        {
            PLoginInfoResponse *loginInfoRes = [ProtocalFactory parsePLoginInfoResponse:pFromServer.dataContent];
            if(loginInfoRes.code == 0)
            {
                [ClientCoreSDK sharedInstance].loginHasInit = YES;

                [[AutoReLoginDaemon sharedInstance] stop];
                
                ObserverCompletion observerBlock = ^(id observerble ,id data) {
                    [[QoS4SendDaemon sharedInstance] stop];
                    [ClientCoreSDK sharedInstance].connectedToServer = NO;
                    [[ClientCoreSDK sharedInstance].chatBaseEvent onLinkCloseMessage:-1];
                    [[AutoReLoginDaemon sharedInstance] start:YES];
                };

                [[KeepAliveDaemon sharedInstance] setNetworkConnectionLostObserver:observerBlock];
                [[KeepAliveDaemon sharedInstance] start:NO];
                [[QoS4ReciveDaemon sharedInstance] startup:YES];
                [[QoS4SendDaemon sharedInstance] startup:YES];
                [ClientCoreSDK sharedInstance].connectedToServer = YES;
            }
            else
            {
                [[LocalUDPSocketProvider sharedInstance] closeLocalUDPSocket];
                [ClientCoreSDK sharedInstance].connectedToServer = NO;
            }
            
            if([ClientCoreSDK sharedInstance].chatBaseEvent != nil)
            {
                [[ClientCoreSDK sharedInstance].chatBaseEvent onLoginMessage:loginInfoRes.code];
            }
            
            break;
        }
        case FROM_SERVER_TYPE_OF_RESPONSE_FOR_ERROR:
        {
            PErrorResponse *errorRes = [ProtocalFactory parsePErrorResponse:pFromServer.dataContent];
            if(errorRes.errorCode == ForS_RESPONSE_FOR_UNLOGIN)
            {
                [ClientCoreSDK sharedInstance].loginHasInit = NO;
                
                NSLog(@"【IMCORE】收到服务端的“尚未登陆”的错误消息，心跳线程将停止，请应用层重新登陆.");

                [[KeepAliveDaemon sharedInstance] stop];
                [[AutoReLoginDaemon sharedInstance] start:NO];
            }
            
            if([ClientCoreSDK sharedInstance].chatTransDataEvent != nil)
            {
                [[ClientCoreSDK sharedInstance].chatTransDataEvent onErrorResponse:errorRes.errorCode withErrorMsg:errorRes.errorMsg];
            }
            break;
        }
            
        default:
            NSLog(@"【IMCORE】收到的服务端消息类型：%d，但目前该类型客户端不支持解析和处理！", pFromServer.type);
            break;
    }
}

- (void) sendRecievedBack:(Protocal *)pFromServer
{
    if(pFromServer.fp != nil)
    {
        Protocal *p = [ProtocalFactory createRecivedBack:pFromServer.to toUserId:pFromServer.from withFingerPrint:pFromServer.fp andBridge:pFromServer.bridge];
        int sendCode = [[LocalUDPDataSender sharedInstance] sendCommonData:p];
        
        if(sendCode == COMMON_CODE_OK)
        {
            if([ClientCoreSDK isENABLED_DEBUG])
                NSLog(@"【IMCORE】【QoS】向%@发送%@包的应答包成功,from=%@ 【bridge?%d】！", pFromServer.from,pFromServer.fp, pFromServer.to, pFromServer.bridge);
        }
        else
        {
            if([ClientCoreSDK isENABLED_DEBUG])
                NSLog(@"【IMCORE】【QoS】向%@发送%@包的应答包失败了,错误码=%d！", pFromServer.from,pFromServer.fp, sendCode);
        }
    }
    else
    {
        NSLog(@"【IMCORE】【QoS】收到%@发过来需要QoS的包，但它的指纹码却为null！无法发应答包！", pFromServer.from);
    }
}

@end
