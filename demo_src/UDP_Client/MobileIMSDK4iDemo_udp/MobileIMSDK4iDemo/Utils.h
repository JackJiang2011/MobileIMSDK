//
//  ToolKits.h
//  MobileIMSDK4iDemo
//
//  Created by Jack Jiang on 2021/7/8.
//  Copyright © 2021 52im.net. All rights reserved.
//

#import <Foundation/Foundation.h>
#import <UIKit/UIKit.h>

@interface Utils : NSObject

/*!
 *  显示一个弹出提示框。因ios8及以上系统中UIAlertView已过时，本方法将使用UIAlertController实现同样的功能。
 *
 *  @param title 标题
 *  @param content 提示信息内容
 *  @param btnTitle 按钮上显示的文字
 *  @param parent 依赖的爷UIViewControler对象
 *  @param handler 点击确认时的block回调
 */
+ (void)showAlert:(NSString *_Nonnull)title content:(NSString *_Nonnull)content btnTitle:(NSString *_Nonnull)btnTitle parent:(UIViewController *_Nonnull)parent handler:(void (^ __nullable)(UIAlertAction * _Nonnull action))handler;

/*!
 *  显示一个确认提示框。因ios8及以上系统中UIAlertView已过时，本方法将使用UIAlertController实现同样的功能。
 *
 *  @param title 标题
 *  @param content 提示信息内容
 *  @param okBtnTitle 确认按钮上显示的文字
 *  @param cancelBtnTitle 取消按钮上显示的文字
 *  @param parent 依赖的爷UIViewControler对象
 *  @param okHandler 点击确认时的block回调
 *  @param cancelHandler 点击取消时的block回调
 */
+ (void)areYouSureAlert:(NSString *_Nonnull)title content:(NSString *_Nonnull)content okBtnTitle:(NSString *_Nullable)okBtnTitle cancelBtnTitle:(NSString *_Nullable)cancelBtnTitle parent:(UIViewController *_Nonnull)parent okHandler:(void (^ __nullable)(UIAlertAction * _Nullable action))okHandler cancelHandler:(void (^ __nullable)(UIAlertAction * _Nullable action))cancelHandler;

@end
