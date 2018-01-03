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
//  CharsetHelper.m
//  MibileIMSDK4i_X (MobileIMSDK v3.0 at Summer 2017)
//
//  Created by JackJiang on 14/10/22.
//  Copyright (c) 2017年 52im.net. All rights reserved.
//

#import "CharsetHelper.h"

@implementation CharsetHelper

+ (NSString *) getString:(NSData *)data
{
    return [[NSString alloc] initWithData:data encoding:NSUTF8StringEncoding];
}

+ (NSData *) getJSONBytesWithDictionary:(NSDictionary *)keyValuesForJASON
{
    NSError *error;
    NSData *jsonData = [NSJSONSerialization dataWithJSONObject:keyValuesForJASON options:NSJSONWritingPrettyPrinted error:&error];
    
    if(error != nil)
        NSLog(@"【IMCORE】将对象转成JSON数据时出错了：%@", error);
    return jsonData;
}

+ (NSData *) getBytesWithString:(NSString *)str
{
    return [str dataUsingEncoding:NSUTF8StringEncoding];
}

@end
