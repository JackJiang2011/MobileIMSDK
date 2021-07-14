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
 * 一个增加了containsKey方法的NSMutableDictionary catlog实现。
 *
 * @author Jack Jiang(http://www.52im.net/thread-2792-1-1.html)
 * @since 2.1
 */
@interface NSMutableDictionary (Ext)

/*!
 *  是否包含指定key所对应的对象。
 *
 *  @param key 要进行判断的key值
 *  @return true表示是列表中忆包含此key，否则表示尚未包含
 */
- (BOOL) containsKey:(NSString *)key;

@end
