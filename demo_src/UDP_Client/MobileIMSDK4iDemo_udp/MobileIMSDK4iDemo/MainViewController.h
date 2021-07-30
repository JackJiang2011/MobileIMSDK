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


@interface MainViewController : UIViewController<UITableViewDelegate, UITableViewDataSource>

/** 通信状态图标 */
@property (weak, nonatomic) IBOutlet UIImageView *connectStatusIcon;
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
 *  发送消息事件处理。
 *
 *  @param sender
 */
- (IBAction)send:(id)sender;

/*!
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


