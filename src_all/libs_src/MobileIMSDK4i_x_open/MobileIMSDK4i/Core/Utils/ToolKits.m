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
//  ToolKits.m
//  MibileIMSDK4i_X (MobileIMSDK v3.0 at Summer 2017)
//
//  Created by JackJiang on 14/10/22.
//  Copyright (c) 2017年 52im.net. All rights reserved.
//

#import "ToolKits.h"
#import "CharsetHelper.h"
#import "RMMapper.h"

@implementation ToolKits

+ (NSString *) generateUUID
{
    NSString *uuid = [[NSUUID UUID] UUIDString];
    return uuid;
}

+ (NSTimeInterval) getTimeStampWithMillisecond
{
    NSDate* dat = [NSDate dateWithTimeIntervalSinceNow:0];
    NSTimeInterval a = [dat timeIntervalSince1970] * 1000;
    return a;
}

+ (long) getTimeStampWithMillisecond_l
{
    return [[NSNumber numberWithDouble:[ToolKits getTimeStampWithMillisecond]] longValue];
}

+ (NSString *) toJSONString:(NSData *)datas
{
    NSString *jsonStr = [CharsetHelper getString:datas];
    return jsonStr;
}
+ (NSData *) toJSONBytesWithDictionary:(NSDictionary *)dic
{
    NSData *jsonData = [CharsetHelper getJSONBytesWithDictionary:dic];
    return jsonData;
}

+ (NSMutableDictionary *) toMutableDictionary:(id)obj
{
    NSMutableDictionary *dic = [RMMapper mutableDictionaryForObject:obj];
    return dic;
}

+ (NSDictionary *) fromJSONBytesToDictionary:(NSData *)jsonBytes
{
    return [NSJSONSerialization JSONObjectWithData:jsonBytes options:0 error:nil];
}
+ (id) fromDictionaryToObject:(NSDictionary *)dic withClass:(Class)clazz
{
    return[RMMapper objectWithClass:clazz fromDictionary:dic];
}


@end
