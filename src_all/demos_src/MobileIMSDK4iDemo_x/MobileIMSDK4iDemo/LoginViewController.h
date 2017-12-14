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
//  LoginViewController.h
//  MibileIMSDK4iDemo_X (A demo for MobileIMSDK v3.0 at Summer 2017)
//
//  Created by JackJiang on 15/11/8.
//  Copyright © 2017年 52im.net. All rights reserved.
//

#import <UIKit/UIKit.h>

@interface LoginViewController : UIViewController<UIAlertViewDelegate>

/** 登陆名 */
@property (weak, nonatomic) IBOutlet UITextField *loginName;
/** 登陆密码 */
@property (weak, nonatomic) IBOutlet UITextField *loginPsw;

/** 服务器地址 */
@property (weak, nonatomic) IBOutlet UITextField *addrField;
/** 服务器端口 */
@property (weak, nonatomic) IBOutlet UITextField *portField;

/** Demo版本号 */
@property (weak, nonatomic) IBOutlet UILabel *versionView;

/*!
 *  @Author Jack Jiang, 14-11-08 15:11:43
 *
 *  登陆事件处理。
 *
 *  @param sender
 */
- (IBAction)signIn:(id)sender;

@end
