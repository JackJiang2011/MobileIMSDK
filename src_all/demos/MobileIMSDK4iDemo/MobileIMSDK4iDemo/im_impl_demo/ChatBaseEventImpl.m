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
//  ChatBaseEventImpl.m
//  RainbowCore4i
//
//  Created by JackJiang on 14/10/28.
//  Copyright (c) 2014年 openmob.net. All rights reserved.
//

#import "ChatBaseEventImpl.h"
#import "AppDelegate.h"
#import "MainViewController.h"

@implementation ChatBaseEventImpl

- (void) onLoginMessage:(int) dwUserId withErrorCode:(int)dwErrorCode
{
    if (dwErrorCode == 0)
    {
        NSLog(@"【DEBUG_UI】登录成功，当前分配的user_id=%d！", dwUserId);
        
        // UI显示
        [CurAppDelegate refreshMyid];
        [[CurAppDelegate getMainViewController] showIMInfo_green:[NSString stringWithFormat:@"登录成功,id=%d", dwUserId]];
    }
    else
    {
        NSLog(@"【DEBUG_UI】登录失败，错误代码：%d", dwErrorCode);
        
        // UI显示
        [[CurAppDelegate getMainViewController] showIMInfo_red:[NSString stringWithFormat:@"登录失败,code=%d", dwErrorCode]];
    }
    
    // 此观察者只有开启程序首次使用登陆界面时有用
    if(self.loginOkForLaunchObserver != nil)
    {
        self.loginOkForLaunchObserver(nil, [NSNumber numberWithInt:dwErrorCode]);
        self.loginOkForLaunchObserver = nil;
    }
}

- (void) onLinkCloseMessage:(int)dwErrorCode
{
    NSLog(@"【DEBUG_UI】网络连接出错关闭了，error：%d", dwErrorCode);
    
    // UI显示
    [[CurAppDelegate getMainViewController] showIMInfo_red:[NSString stringWithFormat:@"服务器连接已断开,error=%d", dwErrorCode]];
}


@end
