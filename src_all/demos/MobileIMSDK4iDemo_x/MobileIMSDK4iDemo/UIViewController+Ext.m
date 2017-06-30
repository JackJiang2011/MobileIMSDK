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
//  UIViewController+Ext.m
//  MibileIMSDK4iDemo_X (A demo for MobileIMSDK v3.0 at Summer 2017)
//
//  Created by JackJiang on 15/11/8.
//  Copyright © 2017年 52im.net. All rights reserved.
//

#import "UIViewController+Ext.h"
#import "Toast+UIView.h"

@implementation UIViewController (Ext)

- (IBAction)E_textFieldDidEndOnExit:(id)sender
{
    // 隐藏键盘
    [sender resignFirstResponder];
}

- (IBAction)E_clickBgToHideKeyboard:(id)sender
{
    NSLog(@"点击了背景！");
    
    // 以下代码实现隐藏键盘(iOS 6及更老的系统很有用)
    [[UIApplication sharedApplication] sendAction:@selector(resignFirstResponder) to:nil from:nil forEvent:nil];
}

- (void) E_showToastInfo:(NSString *)title withContent:(NSString *)content onParent:(UIView *)parentView
{
    // Make toast with an image & title
    // 本方法来自 Toast+UIView catlog实现
    [parentView makeToast:content
                duration:3.0
                position:@"bottom"
                   title:title
                   image:[UIImage imageNamed:@"info.png"]];
}

@end
