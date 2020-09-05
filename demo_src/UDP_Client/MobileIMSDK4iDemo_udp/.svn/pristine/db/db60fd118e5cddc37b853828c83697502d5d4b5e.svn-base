//  ----------------------------------------------------------------------
//  Copyright (C) 2015 Jack Jiang The MobileIMSDK Project.
//  All rights reserved.
//  Project URL:  https://github.com/JackJiang2011/MobileIMSDK
//
//  openmob.net PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
//
//  You can contact author with jack.jiang@openmob.net or jb2011@163.com.
//  ----------------------------------------------------------------------
//
//  CharsetHelper.h
//  MobileIMSDK4i
//
//  Created by JackJiang on 14/10/22.
//  Copyright (c) 2014年 openmob.net. All rights reserved.
//

#import <Foundation/Foundation.h>

/*!
 * 数据交互的编解码实现类。
 *
 * @author Jack Jiang, 2014-10-22
 * @version 1.0
 */
@interface CharsetHelper : NSObject

/*!
 * 将byte数组按UTF-8编码组织成字符串并返回.
 *
 * @param data
 * @return 成功解码完成则返回字符串，否则返回nil
 */
+ (NSString *) getString:(NSData *)data;

/*!
 * 将key-values对象转换成JSON表示的byte数组（以便网络传输待场景下）.
 *
 * @param keyValuesForJASON
 * @return 如果JSON转换成功则返回JSON表示的byte数组，否则返回nil
 */
+ (NSData *) getJSONBytesWithDictionary:(NSDictionary *)keyValuesForJASON;

/*!
 *  将字符串按UTF-8编码成byte数组。
 *
 *  @param str 字符串
 *
 *  @return 编码后的byte数组结果
 */
+ (NSData *) getBytesWithString:(NSString *)str;

@end
