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

#import <Foundation/Foundation.h>
#import "Protocal.h"
#import "PLoginInfoResponse.h"
#import "PErrorResponse.h"
#import "PLoginInfo.h"
#import "PKickoutInfo.h"

@interface ProtocalFactory : NSObject


////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
#pragma mark - 协议解析相关方法
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

+ (id) parse:(NSData *)fullProtocalJASOnBytes;
+ (id) parse:(NSData *)fullProtocalJASOnBytes withClass:(Class)clazz;
+ (id) parseObject:(NSString *)dataContentJSONOfProtocal withClass:(Class)clazz;
+ (PLoginInfoResponse *) parsePLoginInfoResponse:(NSString *)dataContentOfProtocal;
+ (PErrorResponse *) parsePErrorResponse:(NSString *) dataContentOfProtocal;

+ (PKickoutInfo *)parsePKickoutInfo:(NSString *)dataContentOfProtocal;


////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
#pragma mark - 协议组装相关方法
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

+ (Protocal *) createPLoginoutInfo:(NSString *) user_id;
+ (Protocal *) createPLoginInfo:(PLoginInfo *)loginInfo;
+ (Protocal *) createPKeepAlive:(NSString *)from_user_id;
+ (Protocal *) createCommonData:(NSString *)dataContent fromUserId:(NSString *)from_user_id toUserId:(NSString *)to_user_id;
+ (Protocal *) createCommonData:(NSString *)dataContent fromUserId:(NSString *)from_user_id toUserId:(NSString *)to_user_id withTypeu:(int)typeu;
+ (Protocal *) createCommonData:(NSString *)dataContent fromUserId:(NSString *)from_user_id toUserId:(NSString *)to_user_id qos:(bool)QoS fp:(NSString *)fingerPrint withTypeu:(int)typeu;
+ (Protocal *) createRecivedBack:(NSString *)from_user_id toUserId:(NSString *)to_user_id withFingerPrint:(NSString *)recievedMessageFingerPrint;
+ (Protocal *) createRecivedBack:(NSString *)from_user_id toUserId:(NSString *)to_user_id withFingerPrint:(NSString *)recievedMessageFingerPrint andBridge:(bool)bridge;

+ (Protocal *) createPKickout:(nonnull NSString *)to_user_id code:(int)code reason:(nullable NSString *)reason;

@end

