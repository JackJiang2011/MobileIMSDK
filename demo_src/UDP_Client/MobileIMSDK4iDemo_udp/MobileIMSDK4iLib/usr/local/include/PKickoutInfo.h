//
//  PKickoutInfo.h
//  MobileIMSDK4i
//
//  Created by Jack Jiang on 2021/7/8.
//  Copyright © 2021 cngeeker.com. All rights reserved.
//

#import <Foundation/Foundation.h>


/*! 被踢原因编码：因重复登陆被踢 */
#define KICKOUT_FOR_DUPLICATE_LOGIN  1
/*! 被踢原因编码：被管理员强行踢出 */
#define KICKOUT_FOR_ADMIN            2


/*!
 * 向客户端发出的“被踢”指令包内容的DTO类。
 *
 * @author Jack Jiang(http://www.52im.net/thread-2792-1-1.html)
 * @since 6.0
 */
@interface PKickoutInfo : NSObject

/** 被踢原因编码 */
@property (nonatomic, assign) int code;

/** 被踢原因描述 */
@property (nonatomic, retain) NSString*reason;

@end

