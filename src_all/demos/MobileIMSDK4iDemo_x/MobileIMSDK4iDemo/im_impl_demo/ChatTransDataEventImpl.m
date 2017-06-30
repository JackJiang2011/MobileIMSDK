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
//  ChatTransDataEventImpl.m
//  MibileIMSDK4iDemo_X (A demo for MobileIMSDK v3.0 at Summer 2017)
//
//  Created by JackJiang on 14/10/28.
//  Copyright (c) 2017年 52im.net. All rights reserved.
//

#import "ChatTransDataEventImpl.h"
#import "Toast+UIView.h"
#import "AppDelegate.h"
#import "MainViewController.h"


/**
 * 与IM服务器的数据交互事件在此ChatTransDataEvent子类中实现即可。
 *
 * @author Jack Jiang, 20170501
 * @version.1.1
 */
@implementation ChatTransDataEventImpl

- (void) onTransBuffer:(NSString *)fingerPrintOfProtocal withUserId:(NSString *)dwUserid andContent:(NSString *)dataContent andTypeu:(int)typeu
{
    NSLog(@"【DEBUG_UI】[%d]收到来自用户%@的消息:%@", typeu, dwUserid, dataContent);
    
    // UI显示
    // Make toast with an image & title
    [[CurAppDelegate getMainView] makeToast:dataContent
                duration:3.0
                position:@"center"
                   title:[NSString stringWithFormat:@"%@说：", dwUserid]
                   image:[UIImage imageNamed:@"qzone_mark_img_myvoice.png"]];
    [[CurAppDelegate getMainViewController] showIMInfo_black:[NSString stringWithFormat:@"%@说：%@", dwUserid, dataContent]];
}

- (void) onErrorResponse:(int)errorCode withErrorMsg:(NSString *)errorMsg
{
    NSLog(@"【DEBUG_UI】收到服务端错误消息，errorCode=%d, errorMsg=%@", errorCode, errorMsg);
    
    // UI显示
    NSString *content = [NSString stringWithFormat:@"Server反馈错误码：%d,errorMsg=%@", errorCode, errorMsg];
    [[CurAppDelegate getMainViewController] showIMInfo_red:content];
}

@end
