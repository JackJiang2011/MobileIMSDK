//
//  UIViewController+Ext.m
//  MobileIMSDK4iDemo
//
//  Created by JackJiang on 15/11/8.
//  Copyright © 2015年 cngeeker.com. All rights reserved.
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
