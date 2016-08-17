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
//  OnLoginProgress.h
//  MobileIMSDK4iDemo
//
//  Created by JackJiang on 15/11/9.
//  Copyright © 2015年 openmob.net. All rights reserved.
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
