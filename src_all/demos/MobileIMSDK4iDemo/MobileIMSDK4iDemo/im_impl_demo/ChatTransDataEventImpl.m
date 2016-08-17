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
//  ChatTransDataEventImpl.m
//  RainbowCore4i
//
//  Created by JackJiang on 14/10/28.
//  Copyright (c) 2014年 openmob.net. All rights reserved.
//

#import "ChatTransDataEventImpl.h"
#import "Toast+UIView.h"
#import "AppDelegate.h"
#import "MainViewController.h"

@implementation ChatTransDataEventImpl

- (void) onTransBuffer:(NSString *)fingerPrintOfProtocal withUserId:(int)dwUserid andContent:(NSString *)dataContent
{
    NSLog(@"【DEBUG_UI】收到来自用户%d的消息:%@", dwUserid, dataContent);
    
    // UI显示
    // Make toast with an image & title
    [[CurAppDelegate getMainView] makeToast:dataContent
                duration:3.0
                position:@"center"
                   title:[NSString stringWithFormat:@"%d说：", dwUserid]
                   image:[UIImage imageNamed:@"qzone_mark_img_myvoice.png"]];
    [[CurAppDelegate getMainViewController] showIMInfo_black:[NSString stringWithFormat:@"%d说：%@", dwUserid, dataContent]];
}

- (void) onErrorResponse:(int)errorCode withErrorMsg:(NSString *)errorMsg
{
    NSLog(@"【DEBUG_UI】收到服务端错误消息，errorCode=%d, errorMsg=%@", errorCode, errorMsg);
    
    // UI显示
    NSString *content = [NSString stringWithFormat:@"Server反馈错误码：%d,errorMsg=%@", errorCode, errorMsg];
    [[CurAppDelegate getMainViewController] showIMInfo_red:content];
}

@end
