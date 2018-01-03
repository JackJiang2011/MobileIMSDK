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
//  Protocal.h
//  MibileIMSDK4i_X (MobileIMSDK v3.0 at Summer 2017)
//
//  Created by JackJiang on 14/10/22.
//  Copyright (c) 2017年 52im.net. All rights reserved.
//

#import <Foundation/Foundation.h>

@interface Protocal : NSObject

@property (nonatomic, assign)  bool bridge;
@property (nonatomic, assign)  int type;
@property (nonatomic, retain)  NSString *dataContent;
@property (nonatomic, retain)  NSString *from;
@property (nonatomic, retain)  NSString *to;
@property (nonatomic, retain)  NSString *fp;
@property (nonatomic, assign)  bool QoS;
@property (nonatomic, assign)  int typeu;

- (int) getRetryCount;
- (void) increaseRetryCount;
- (NSString *) toGsonString;
- (NSData *) toBytes;
- (Protocal *) clone;

+ (Protocal *) initWithType:(int)type content:(NSString *)dataContent from:(NSString *)from to:(NSString *)to;
+ (Protocal *) initWithType:(int)type content:(NSString *)dataContent from:(NSString *)from to:(NSString *)to tu:(int)typeu;
+ (Protocal *) initWithType:(int)type content:(NSString *)dataContent from:(NSString *)from to:(NSString *)to qos:(bool)QoS fp:(NSString *)fingerPrint tu:(int)typeu;
+ (Protocal *) initWithType:(int)type content:(NSString *)dataContent from:(NSString *)from to:(NSString *)to qos:(bool)QoS fp:(NSString *)fingerPrint bg:(bool)bridge tu:(int)typeu;
+ (NSString *) genFingerPrint;

@end
