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

#import "ToolKits.h"
#import "CharsetHelper.h"
#import "RMMapper.h"

@implementation ToolKits


////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
#pragma mark - 其它实用方法
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

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


////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
#pragma mark - JSON转换相关方法
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

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
