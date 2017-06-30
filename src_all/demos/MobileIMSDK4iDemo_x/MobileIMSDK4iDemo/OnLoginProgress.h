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
//  OnLoginProgress.h
//  MibileIMSDK4iDemo_X (A demo for MobileIMSDK v3.0 at Summer 2017)
//
//  Created by JackJiang on 15/11/9.
//  Copyright © 2017年 52im.net. All rights reserved.
//

#import <UIKit/UIKit.h>
#import <Foundation/Foundation.h>
#import "CompletionDefine.h"

@interface OnLoginProgress : NSObject

/** 登陆超时回调（观察者） */
@property (nonatomic, copy) ObserverCompletion onLoginTimeoutObserver;// block代码块一定要用copy属性，否则报错！

/**
 * 显示进度提示.
 *
 * @param show YES表示马上显示，NO表示取消显示
 */
- (void)showProgressing:(BOOL)show onParent:(UIView *)view;

@end
