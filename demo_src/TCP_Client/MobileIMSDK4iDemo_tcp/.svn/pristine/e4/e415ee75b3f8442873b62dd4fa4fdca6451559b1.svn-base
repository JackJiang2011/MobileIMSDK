//  ----------------------------------------------------------------------
//  Copyright (C) 2020  即时通讯网(52im.net) & Jack Jiang.
//  The MobileIMSDK_TCP (MobileIMSDK v5.x TCP版) Project.
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
#import "MBGCDAsyncSocket.h"

/*!
 * 一个本地TCP消息发送工具类。
 *
 * @author Jack Jiang(http://www.52im.net/thread-2792-1-1.html)
 * @version 1.0
 * @since 2.1
 */
@interface TCPUtils : NSObject

/*!
 * 发送一条tcp消息。
 *
 * @param skt GCDAsyncSocket对象引用
 * @param d 要发送的比特数组
 * @return true表示成功发出，否则表示发送失败
 * @see #send(DatagramSocket, DatagramPacket)
 */
+ (BOOL) send:(MBGCDAsyncSocket *) skt withData:(NSData *)d;

@end
