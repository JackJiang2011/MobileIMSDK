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

#import "ProtocalFactory.h"
#import "ToolKits.h"
#import "CharsetHelper.h"
#import "Protocal.h"
#import "ProtocalType.h"
#import "PKeepAliveResponse.h"
#import "PKeepAlive.h"
#import "PLoginInfo.h"
#import "PLoginInfoResponse.h"
#import "PErrorResponse.h"

@implementation ProtocalFactory


#pragma mark - 以下方法为本类内部使用

+ (NSString *) create:(id)protocalDataContentObj
{
    return[ToolKits toJSONString:[ToolKits toJSONBytesWithDictionary:[ToolKits toMutableDictionary:protocalDataContentObj]]];
}


////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
#pragma mark - 协议解析相关方法（可外部调用）
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

+ (id) parse:(NSData *)fullProtocalJASOnBytes
{
    return [ProtocalFactory parse:fullProtocalJASOnBytes withClass:Protocal.class];
}
+ (id) parse:(NSData *)fullProtocalJASOnBytes withClass:(Class)clazz
{
    return [ToolKits fromDictionaryToObject:
            [ToolKits fromJSONBytesToDictionary:fullProtocalJASOnBytes] withClass:clazz];
}
+ (id) parseObject:(NSString *)dataContentJSONOfProtocal withClass:(Class)clazz
{
    return [ToolKits fromDictionaryToObject:
            [ToolKits fromJSONBytesToDictionary:
             [CharsetHelper getBytesWithString:dataContentJSONOfProtocal]] withClass:clazz];
}

+ (PLoginInfoResponse *) parsePLoginInfoResponse:(NSString *)dataContentOfProtocal
{
    return [ProtocalFactory parseObject:dataContentOfProtocal withClass:[PLoginInfoResponse class]];
}

+ (PKeepAliveResponse *) parsePKeepAliveResponse:(NSString *)dataContentOfProtocal
{
    return [ProtocalFactory parseObject:dataContentOfProtocal withClass:[PKeepAliveResponse class]];
}

+ (PErrorResponse *) parsePErrorResponse:(NSString *) dataContentOfProtocal
{
    return [ProtocalFactory parseObject:dataContentOfProtocal withClass:[PErrorResponse class]];
}

+ (PKickoutInfo *)parsePKickoutInfo:(NSString *)dataContentOfProtocal
{
    return [ProtocalFactory parseObject:dataContentOfProtocal withClass:[PKickoutInfo class]];
}


////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
#pragma mark - 协议组装相关方法（可外部调用）
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

+ (Protocal *) createPLoginoutInfo:(NSString *) user_id
{
    NSString *dataContent = nil; // 空JSON对象
    return [Protocal initWithType:FROM_CLIENT_TYPE_OF_LOGOUT content:dataContent from:user_id to:@"0"];
}

+ (Protocal *) createPLoginInfo:(PLoginInfo *)loginInfo//(NSString *)loginUserId withToken:(NSString *)loginToken andExtra:(NSString *)extra
{
    NSString *dataContent = [ProtocalFactory create:loginInfo];
    return [Protocal initWithType:FROM_CLIENT_TYPE_OF_LOGIN content:dataContent from:loginInfo.loginUserId to:@"0"];
}

+ (Protocal *) createCommonData:(NSString *)dataContent fromUserId:(NSString *)from_user_id toUserId:(NSString *)to_user_id
{
    return [ProtocalFactory createCommonData:dataContent fromUserId:from_user_id toUserId:to_user_id qos:YES fp:nil withTypeu:-1];
}
+ (Protocal *) createCommonData:(NSString *)dataContent fromUserId:(NSString *)from_user_id toUserId:(NSString *)to_user_id withTypeu:(int)typeu
{
    return [ProtocalFactory createCommonData:dataContent fromUserId:from_user_id toUserId:to_user_id qos:YES fp:nil withTypeu:typeu];
}
+ (Protocal *) createCommonData:(NSString *)dataContent fromUserId:(NSString *)from_user_id toUserId:(NSString *)to_user_id qos:(bool)QoS fp:(NSString *)fingerPrint withTypeu:(int)typeu
{
    return [Protocal initWithType:FROM_CLIENT_TYPE_OF_COMMON_DATA content:dataContent from:from_user_id to:to_user_id qos:QoS fp:fingerPrint tu:typeu];
}

+ (Protocal *) createRecivedBack:(NSString *)from_user_id toUserId:(NSString *)to_user_id withFingerPrint:(NSString *)recievedMessageFingerPrint
{
    return [ProtocalFactory createRecivedBack:from_user_id toUserId:to_user_id withFingerPrint:recievedMessageFingerPrint andBridge:NO];
}

+ (Protocal *) createRecivedBack:(NSString *)from_user_id toUserId:(NSString *)to_user_id withFingerPrint:(NSString *)recievedMessageFingerPrint andBridge:(bool)bridge
{
    return [Protocal initWithType:FROM_CLIENT_TYPE_OF_RECIVED content:recievedMessageFingerPrint from:from_user_id to:to_user_id qos:NO fp:nil bg:bridge tu:-1];
}

+ (Protocal *) createPKeepAlive:(NSString *)from_user_id
{
    NSString *dataContent = [ProtocalFactory create:[[PKeepAlive alloc] init]];
    return [Protocal initWithType:FROM_CLIENT_TYPE_OF_KEEP_ALIVE content:dataContent from:from_user_id to:@"0"];
}

+ (Protocal *) createPKickout:(nonnull NSString *)to_user_id code:(int)code reason:(nullable NSString *)reason
{
    PKickoutInfo *ki = [[PKickoutInfo alloc] init];
    ki.code = code;
    ki.reason = reason;

    return [Protocal initWithType:FROM_SERVER_TYPE_OF_KICKOUT content:[ProtocalFactory create:ki] from:@"0" to:to_user_id];
}

@end
