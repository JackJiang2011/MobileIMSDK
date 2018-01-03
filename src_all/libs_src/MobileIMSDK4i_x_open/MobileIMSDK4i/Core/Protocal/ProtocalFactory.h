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
//  ProtocalFactory.h
//  MibileIMSDK4i_X (MobileIMSDK v3.0 at Summer 2017)
//
//  Created by JackJiang on 14/10/23.
//  Copyright (c) 2017年 52im.net. All rights reserved.
//

#import <Foundation/Foundation.h>
#import "Protocal.h"
#import "PLoginInfoResponse.h"
#import "PErrorResponse.h"

@interface ProtocalFactory : NSObject

+ (id) parse:(NSData *)fullProtocalJASOnBytes;
+ (id) parse:(NSData *)fullProtocalJASOnBytes withClass:(Class)clazz;
+ (id) parseObject:(NSString *)dataContentJSONOfProtocal withClass:(Class)clazz;
+ (PLoginInfoResponse *) parsePLoginInfoResponse:(NSString *)dataContentOfProtocal;
+ (PErrorResponse *) parsePErrorResponse:(NSString *) dataContentOfProtocal;


+ (Protocal *) createPLoginoutInfo:(NSString *) user_id;
+ (Protocal *) createPLoginInfo:(NSString *)loginUserId withToken:(NSString *)loginToken andExtra:(NSString *)extra;
+ (Protocal *) createPKeepAlive:(NSString *)from_user_id;
+ (Protocal *) createCommonData:(NSString *)dataContent fromUserId:(NSString *)from_user_id toUserId:(NSString *)to_user_id;
+ (Protocal *) createCommonData:(NSString *)dataContent fromUserId:(NSString *)from_user_id toUserId:(NSString *)to_user_id withTypeu:(int)typeu;
+ (Protocal *) createCommonData:(NSString *)dataContent fromUserId:(NSString *)from_user_id toUserId:(NSString *)to_user_id qos:(bool)QoS fp:(NSString *)fingerPrint withTypeu:(int)typeu;
+ (Protocal *) createRecivedBack:(NSString *)from_user_id toUserId:(NSString *)to_user_id withFingerPrint:(NSString *)recievedMessageFingerPrint;
+ (Protocal *) createRecivedBack:(NSString *)from_user_id toUserId:(NSString *)to_user_id withFingerPrint:(NSString *)recievedMessageFingerPrint andBridge:(bool)bridge;

@end

