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
