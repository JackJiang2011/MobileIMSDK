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
//  AppDelegate.h
//  RainbowCore4i
//
//  Created by JackJiang on 14/10/21.
//  Copyright (c) 2014年 cngeeker.com. All rights reserved.
//

#import <UIKit/UIKit.h>

#define CurAppDelegate ((AppDelegate*)[[UIApplication sharedApplication] delegate])

@class ViewController;

@interface AppDelegate : UIResponder <UIApplicationDelegate>

@property (strong, nonatomic) UIWindow *window;
@property (strong, nonatomic) ViewController *viewController;

- (UIView *) getMainView;
- (ViewController *) getMainViewController;
- (void) setMyid:(int)myid;

@end

