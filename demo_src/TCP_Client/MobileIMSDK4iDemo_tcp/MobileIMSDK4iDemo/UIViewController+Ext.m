//  ----------------------------------------------------------------------
//  Copyright (C) 2021  即时通讯网(52im.net) & Jack Jiang.
//  The MobileIMSDK_TCP (MobileIMSDK v6.x TCP版) Project.
//  All rights reserved.
//
//  > Github地址: https://github.com/JackJiang2011/MobileIMSDK
//  > 文档地址:    http://www.52im.net/forum-89-1.html
//  > 技术社区：   http://www.52im.net/
//  > 技术交流群： 215477170 (http://www.52im.net/topic-qqgroup.html)
//  > 作者公众号： “即时通讯技术圈】”，欢迎关注！
//  > 联系作者：   http://www.52im.net/thread-2792-1-1.html
//
//  "即时通讯网(52im.net) - 即时通讯开发者社区!" 推荐开源工程。
//  ----------------------------------------------------------------------

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
                position:@"top"
                   title:title
                   image:[UIImage imageNamed:@"info.png"]];
}

@end
