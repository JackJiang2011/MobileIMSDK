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
//  UDPUtils.h
//  MobileIMSDK4i
//
//  Created by JackJiang on 14/10/27.
//  Copyright (c) 2014年 openmob.net. All rights reserved.
//

#import <Foundation/Foundation.h>
#import "GCDAsyncUdpSocket.h"

/*!
 * 一个本地UDP消息发送工具类。
 *
 * @author Jack Jiang, 2014-10-27
 * @version 1.0
 * @since 2.1
 */
@interface UDPUtils : NSObject

/*!
 * 发送一条UDP消息。
 *
 * @param skt GCDAsyncUdpSocket对象引用
 * @param d 要发送的比特数组
 * @return true表示成功发出，否则表示发送失败
 * @see #send(DatagramSocket, DatagramPacket)
 */
+ (BOOL) send:(GCDAsyncUdpSocket *) skt withData:(NSData *)d;

@end
