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
//  MessageQoSEventImpl.m
//  MibileIMSDK4iDemo_X (A demo for MobileIMSDK v3.0 at Summer 2017)
//
//  Created by JackJiang on 14/10/28.
//  Copyright (c) 2017年 52im.net. All rights reserved.
//

#import "MessageQoSEventImpl.h"
#import "AppDelegate.h"
#import "MainViewController.h"


/**
 * 消息送达相关事件（由QoS机制通知上来的）在此MessageQoSEvent子类中实现即可。
 *
 * @author Jack Jiang, 20170501
 * @version.1.1
 */
@implementation MessageQoSEventImpl

- (void) messagesLost:(NSMutableArray*)lostMessages
{
    NSLog(@"【DEBUG_UI】收到系统的未实时送达事件通知，当前共有%li个包QoS保证机制结束，判定为【无法实时送达】！", (unsigned long)[lostMessages count]);
    
    // UI显示
    [[CurAppDelegate getMainViewController] showIMInfo_brightred:[NSString stringWithFormat:@"[消息未成功送达]共%li条!(网络状况不佳或对方id不存在)", [lostMessages count]]];
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
