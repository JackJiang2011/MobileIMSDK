//  ----------------------------------------------------------------------
//  Copyright (C) 2020  即时通讯网(52im.net) & Jack Jiang.
//  The MobileIMSDK_TCP (MobileIMSDK v5.x TCP版) Project.
//  All rights reserved.
//
//  > Github地址: https://github.com/JackJiang2011/MobileIMSDK
//  > 文档地址:    http://www.52im.net/forum-89-1.html
//  > 技术社区：   http://www.52im.net/
//  > 技术交流群： 320837163 (http://www.52im.net/topic-qqgroup.html)
//  > 作者公众号： “即时通讯技术圈】”，欢迎关注！
//  > 联系作者：   http://www.52im.net/thread-2792-1-1.html
//
//  "即时通讯网(52im.net) - 即时通讯开发者社区!" 推荐开源工程。
//  ----------------------------------------------------------------------

#import "ChatBaseEventImpl.h"
#import "AppDelegate.h"
#import "MainViewController.h"
#import "AutoReLoginDaemon.h"
#import "Utils.h"

/**
 * 与IM服务器的连接事件在此ChatBaseEvent子类中实现即可。
 *
 * @author Jack Jiang, 20170501
 * @version.1.1
 */
@implementation ChatBaseEventImpl

/*!
 * 本地用户的登陆结果回调事件通知。
 *
 * @param errorCode 服务端反馈的登录结果：0 表示登陆成功，否则为服务端自定义的出错代码（按照约定通常为>=1025的数）
 */
- (void) onLoginResponse:(int)errorCode
{
    if (errorCode == 0)
    {
        NSLog(@"【DEBUG_UI】IM服务器登录/连接成功！");
        
        // UI显示
        [CurAppDelegate refreshConnecteStatus];
        [[CurAppDelegate getMainViewController] showIMInfo_green:[NSString stringWithFormat:@"登录成功,errorCode=%d", errorCode]];
    }
    else
    {
        NSLog(@"【DEBUG_UI】IM服务器登录/连接失败，错误代码：%d", errorCode);
        
        // UI显示
        [[CurAppDelegate getMainViewController] showIMInfo_red:[NSString stringWithFormat:@"IM服务器登录/连接失败,code=%d", errorCode]];
    }
    
    // 此观察者只有开启程序首次使用登陆界面时有用
    if(self.loginOkForLaunchObserver != nil)
    {
        self.loginOkForLaunchObserver(nil, [NSNumber numberWithInt:errorCode]);
        
        //## Try bug FIX! 20160810：上方的observer作为block代码应是被异步执行，此处立即设置nil的话，实测
        //##                        中会遇到怎么也登陆不进去的问题（因为此observer已被过早的nil了！）
//      self.loginOkForLaunchObserver = nil;
    }
}

/*!
 * 与服务端的通信断开的回调事件通知。
 *
 * <br>
 * 该消息只有在客户端连接服务器成功之后网络异常中断之时触发。
 * 导致与与服务端的通信断开的原因有（但不限于）：无线网络信号不稳定、WiFi与2G/3G/4G等同开情况下的网络切换、手机系统的省电策略等。
 *
 * @param errorCode 本回调参数表示表示连接断开的原因，目前错误码没有太多意义，仅作保留字段，目前通常为-1
 */
- (void) onLinkClose:(int)errorCode
{
    NSLog(@"【DEBUG_UI】与IM服务器的网络连接出错关闭了，error：%d", errorCode);
    
    // UI显示
    [CurAppDelegate refreshConnecteStatus];
    [[CurAppDelegate getMainViewController] showIMInfo_red:[NSString stringWithFormat:@"与IM服务器的连接已断开! (%d)", errorCode]];
}

/**
 * 本的用户被服务端踢出的回调事件通知。
 *
 * @param kickoutInfo 被踢信息对象，{@link PKickoutInfo} 对象中的 code字段定义了被踢原因代码
 */
- (void) onKickout:(PKickoutInfo *)kickoutInfo
{
    NSLog(@"【DEBUG_UI】已收到服务端的\"被踢\"指令，kickoutInfo.code：%d", kickoutInfo.code);

    NSString *alertContent = @"";
    if(kickoutInfo.code == KICKOUT_FOR_DUPLICATE_LOGIN)
    {
        alertContent = @"账号已在其它地方登陆，当前会话已断开，请退出后重新登陆！";
    }
    else if(kickoutInfo.code == KICKOUT_FOR_ADMIN)
    {
        alertContent = @"已被管理员强行踢出聊天，当前会话已断开！";
    }
    else
    {
        alertContent = [NSString stringWithFormat:@"你已被踢出聊天，当前会话已断开（kickoutReason=%@）！", kickoutInfo.reason];
    }

    // 在信息列表中显示提示
    [[CurAppDelegate getMainViewController] showIMInfo_red:alertContent];
    // 跳出一个Alert提示
    [Utils showAlert:@"你被踢了" content:alertContent btnTitle:@"知道了！" parent:[CurAppDelegate getMainViewController] handler:^(UIAlertAction *action) {
        // 退出应用
        [[CurAppDelegate getMainViewController] signOut:nil];
    }];
}

@end
