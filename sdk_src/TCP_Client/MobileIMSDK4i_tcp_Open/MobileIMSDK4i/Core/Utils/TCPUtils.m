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

#import "TCPUtils.h"
#import "TCPFrameCodec.h"

@implementation TCPUtils

+ (BOOL) send:(MBGCDAsyncSocket *) skt withData:(NSData *)d
{
    BOOL sendSucess = YES;
    if(skt != nil && d != nil)
    {
        if([skt isConnected])
        {
            NSData *frame = [TCPFrameCodec encodeFrame:d];
            if(frame != nil)
                [skt writeData:frame withTimeout:-1 tag:999];
            else
                NSLog(@"【IMCORE】要发送的数据编码后frame=nil，本次发送将被忽略(要发送的原始数据为：%@)!", d);
        }
        else
        {
            NSLog(@"【IMCORE】[skt isConnected]=NO，本次发送将被忽略(要发送的原始数据为：%@)!", d);
        }
    }
    else
    {
        NSLog(@"【IMCORE】在send()UDP数据报时没有成功执行，原因是：skt==null || d == null!");
    }
    
    return sendSucess;
}

@end
