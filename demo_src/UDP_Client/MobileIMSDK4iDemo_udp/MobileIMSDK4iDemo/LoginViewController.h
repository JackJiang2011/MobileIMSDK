//  ----------------------------------------------------------------------
//  Copyright (C) 2021  即时通讯网(52im.net) & Jack Jiang.
//  The MobileIMSDK_UDP (MobileIMSDK v6.x UDP版) Project.
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

#import <UIKit/UIKit.h>

@interface LoginViewController : UIViewController

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
 *  @Author Jack Jiang
 *
 *  登陆事件处理。
 *
 *  @param sender
 */
- (IBAction)signIn:(id)sender;

@end
