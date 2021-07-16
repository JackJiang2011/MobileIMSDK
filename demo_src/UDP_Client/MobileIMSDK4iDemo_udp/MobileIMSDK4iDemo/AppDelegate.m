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

#import "AppDelegate.h"
#import "MainViewController.h"
#import "ClientCoreSDK.h"
#import "ToolKits.h"
#import "PErrorResponse.h"
#import "CharsetHelper.h"
#import "Protocal.h"
#import "ClientCoreSDK.h"
#import "ChatMessageEventImpl.h"
#import "ChatBaseEventImpl.h"
#import "MessageQoSEventImpl.h"
#import "KeepAliveDaemon.h"
#import "AutoReLoginDaemon.h"
#import "ConfigEntity.h"
#import "LoginViewController.h"
#import "IMClientManager.h"

@interface AppDelegate ()

@end

@implementation AppDelegate

- (BOOL)application:(UIApplication *)application didFinishLaunchingWithOptions:(NSDictionary *)launchOptions {
    
    // init MobileIMSDK first
    // 提示：在不退出APP的情况下退出登陆后再重新登陆时，请确保调用本方法一次，不然会报code=203错误哦！
    [[IMClientManager sharedInstance] initMobileIMSDK];
    
    self.window = [[UIWindow alloc] initWithFrame:[[UIScreen mainScreen] bounds]];
    
    LoginViewController *loginViewController = [[LoginViewController alloc] initWithNibName:@"LoginViewController"  bundle:nil];
    UINavigationController* nav = [[UINavigationController alloc] initWithRootViewController:loginViewController];
    self.window.rootViewController = nav;
    
    [self.window makeKeyAndVisible];

    return YES;
}

-(void)switchToMainViewController
{
    if (self.viewController == nil)
        self.viewController	= [[MainViewController alloc] initWithNibName:@"MainViewController" bundle:nil];
    
    UINavigationController  *navRoot = [[UINavigationController alloc] initWithRootViewController:self.viewController];
    self.window.rootViewController = navRoot;
}

- (UIView *) getMainView
{
    return self.viewController.view;
}

- (MainViewController *) getMainViewController
{
    return self.viewController;
}

- (void) refreshConnecteStatus
{
    [self.viewController refreshConnecteStatus];
}

- (void)applicationWillResignActive:(UIApplication *)application {
    // Sent when the application is about to move from active to inactive state. This can occur for certain types of temporary interruptions (such as an incoming phone call or SMS message) or when the user quits the application and it begins the transition to the background state.
    // Use this method to pause ongoing tasks, disable timers, and throttle down OpenGL ES frame rates. Games should use this method to pause the game.
}

- (void)applicationDidEnterBackground:(UIApplication *)application {
    // Use this method to release shared resources, save user data, invalidate timers, and store enough application state information to restore your application to its current state in case it is terminated later.
    // If your application supports background execution, this method is called instead of applicationWillTerminate: when the user quits.
}

- (void)applicationWillEnterForeground:(UIApplication *)application {
    // Called as part of the transition from the background to the inactive state; here you can undo many of the changes made on entering the background.
}

- (void)applicationDidBecomeActive:(UIApplication *)application {
    // Restart any tasks that were paused (or not yet started) while the application was inactive. If the application was previously in the background, optionally refresh the user interface.
}

- (void)applicationWillTerminate:(UIApplication *)application {
    // Called when the application is about to terminate. Save data if appropriate. See also applicationDidEnterBackground:.
    
    // 释放IM核心占用的资源
    [[ClientCoreSDK sharedInstance] releaseCore];
}

@end
