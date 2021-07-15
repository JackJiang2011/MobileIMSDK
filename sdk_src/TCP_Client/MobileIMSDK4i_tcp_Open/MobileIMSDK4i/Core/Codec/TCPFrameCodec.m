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

#import "TCPFrameCodec.h"


////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
#pragma mark - 静态全局类变量
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

static int TCP_FRAME_FIXED_HEADER_LENGTH = 4;    // 4 bytes
static int TCP_FRAME_MAX_BODY_LENGTH = 6 * 1024; // 6K bytes


////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
#pragma mark - 静态公开方法
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

@implementation TCPFrameCodec

+ (NSData *)encodeFrame:(NSData *)bodyDataOfFrame
{
    if(bodyDataOfFrame != nil && [bodyDataOfFrame length] > 0)
    {
        int bodyLength = CFSwapInt32HostToBig((int)[bodyDataOfFrame length]);
        NSData *headerData = [NSData dataWithBytes:&bodyLength length:TCP_FRAME_FIXED_HEADER_LENGTH];

        NSMutableData *frame = [NSMutableData dataWithData:headerData];
        [frame appendData:bodyDataOfFrame];
        
        return frame;
    }
    
    return nil;
}

+ (int)decodeBodyLength:(NSData *)headerDataOfFrame
{
    int bodyLength = 0;
    if(headerDataOfFrame != nil && [headerDataOfFrame length] > 0)
    {
        [headerDataOfFrame getBytes:&bodyLength length:TCP_FRAME_FIXED_HEADER_LENGTH];
        bodyLength = CFSwapInt32BigToHost(bodyLength);
    }
   
    return bodyLength;
}

+ (void)setTCP_FRAME_FIXED_HEADER_LENGTH:(int)l
{
    TCP_FRAME_FIXED_HEADER_LENGTH = l;
}

+ (int)getTCP_FRAME_FIXED_HEADER_LENGTH
{
    return TCP_FRAME_FIXED_HEADER_LENGTH;
}

+ (void)setTCP_FRAME_MAX_BODY_LENGTH:(int)l
{
    TCP_FRAME_MAX_BODY_LENGTH = l;
}
+ (int)getTCP_FRAME_MAX_BODY_LENGTH
{
    return TCP_FRAME_MAX_BODY_LENGTH;
}


@end
