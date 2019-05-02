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
//  UDPUtils.m
//  MibileIMSDK4i_X (MobileIMSDK v3.0 at Summer 2017)
//
//  Created by JackJiang on 14/10/27.
//  Copyright (c) 2017年 52im.net. All rights reserved.
//

#import "UDPUtils.h"
#import "MBGCDAsyncUdpSocket.h"

@implementation UDPUtils

+ (BOOL) send:(MBGCDAsyncUdpSocket *) skt withData:(NSData *)d
{
    BOOL sendSucess = YES;
    if(skt != nil && d != nil)
    {
        if([skt isConnected])
        {
            [skt sendData:d withTimeout:-1 tag:0];
        }
    }
    else
    {
        NSLog(@"【IMCORE】在send()UDP数据报时没有成功执行，原因是：skt==null || d == null!");
    }
    
    return sendSucess;
}

@end
