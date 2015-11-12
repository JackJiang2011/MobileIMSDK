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
//  LoginViewController.h
//  MobileIMSDK4iDemo
//
//  Created by JackJiang on 15/11/8.
//  Copyright © 2015年 cngeeker.com. All rights reserved.
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
