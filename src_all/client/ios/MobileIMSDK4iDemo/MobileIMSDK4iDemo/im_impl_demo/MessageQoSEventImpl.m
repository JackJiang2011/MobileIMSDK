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
//  MessageQoSEventImpl.m
//  RainbowCore4i
//
//  Created by JackJiang on 14/10/28.
//  Copyright (c) 2014年 cngeeker.com. All rights reserved.
//

#import "MessageQoSEventImpl.h"
#import "AppDelegate.h"
#import "MainViewController.h"

@implementation MessageQoSEventImpl

- (void) messagesLost:(NSArray*)lostMessages
{
    NSLog(@"【DEBUG_UI】收到系统的未实时送达事件通知，当前共有%li个包QoS保证机制结束，判定为【无法实时送达】！", (unsigned long)[lostMessages count]);
    
    // UI显示
    [[CurAppDelegate getMainViewController] showIMInfo_brightred:[NSString stringWithFormat:@"[消息未成功送达]共%d条!(网络状况不佳或对方id不存在)", [lostMessages count]]];
}

- (void) messagesBeReceived:(NSString *)theFingerPrint
{
    if(theFingerPrint != nil)
    {
        NSLog(@"【DEBUG_UI】收到对方已收到消息事件的通知，fp=%@", theFingerPrint);
        
        // UI显示
        [[CurAppDelegate getMainViewController] showIMInfo_blue:[NSString stringWithFormat:@"[收到应答]%@", theFingerPrint]];
    }
}

@end
