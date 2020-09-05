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
//  ViewController.h
//  MibileIMSDK4iDemo_X (A demo for MobileIMSDK v3.0 at Summer 2017)
//
//  Created by JackJiang on 14/10/21.
//  Copyright (c) 2017年 52im.net. All rights reserved.
//

#import <UIKit/UIKit.h>


@interface MainViewController : UIViewController<UITableViewDelegate, UITableViewDataSource>

/** 通信状态显示 */
@property (weak, nonatomic) IBOutlet UILabel *connectStatus;
/** 我的登陆账号 */
@property (weak, nonatomic) IBOutlet UILabel *myUserId;
/** 要发送消息的对方id */
@property (weak, nonatomic) IBOutlet UITextField *friendId;

/** 要发送的消息 */
@property (weak, nonatomic) IBOutlet UITextField *messageField;

@property (weak, nonatomic) IBOutlet UITableView *tableView;

/*!
 *  @Author Jack Jiang, 14-11-08 15:11:53
 *
 *  退出登陆事件处理。
 *
 *  @param sender
 */
- (IBAction)signOut:(id)sender;

/*!
 *  @Author Jack Jiang, 14-11-08 15:11:07
 *
 *  发送消息事件处理。
 *
 *  @param sender
 */
- (IBAction)send:(id)sender;

/*!
 *  @Author Jack Jiang, 14-11-08 15:11:26
 *
 *  刷新本地的user_id显示.
 *
 *  @param myid
 */
- (void) refreshConnecteStatus;

- (void) showIMInfo_black:(NSString*)txt;
- (void) showIMInfo_blue:(NSString*)txt;
- (void) showIMInfo_brightred:(NSString*)txt;
- (void) showIMInfo_red:(NSString*)txt;
- (void) showIMInfo_green:(NSString*)txt;


//----------------------------------------- for debug START
@property (weak, nonatomic) IBOutlet UIImageView *iviewAutoRelogin;
@property (weak, nonatomic) IBOutlet UIImageView *iviewKeepAlive;

@property (weak, nonatomic) IBOutlet UIImageView *iviewQoSSend;
@property (weak, nonatomic) IBOutlet UIImageView *iviewQoSReceive;
//----------------------------------------- for debug END

@end


