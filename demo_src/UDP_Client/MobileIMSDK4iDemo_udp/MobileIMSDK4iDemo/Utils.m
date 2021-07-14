//
//  ToolKits.m
//  MobileIMSDK4iDemo
//
//  Created by Jack Jiang on 2021/7/8.
//  Copyright © 2021 52im.net. All rights reserved.
//

#import "Utils.h"

@implementation Utils

+ (void)showAlert:(NSString *)title content:(NSString *)content btnTitle:(NSString *)btnTitle parent:(UIViewController *_Nonnull)parent handler:(void (^ __nullable)(UIAlertAction *action))handler
{
    UIAlertController* alert = [UIAlertController alertControllerWithTitle:title
                                                                   message:content
                                                            preferredStyle:UIAlertControllerStyleAlert];
    
    UIAlertAction* defaultAction = [UIAlertAction actionWithTitle:btnTitle style:UIAlertActionStyleDefault handler:handler];
    
    [alert addAction:defaultAction];
    [parent presentViewController:alert animated:YES completion:nil];
}

+ (void)areYouSureAlert:(NSString *)title content:(NSString *)content okBtnTitle:(NSString *)okBtnTitle cancelBtnTitle:(NSString *)cancelBtnTitle parent:(UIViewController *)parent okHandler:(void (^ __nullable)(UIAlertAction *action))okHandler cancelHandler:(void (^ __nullable)(UIAlertAction *action))cancelHandler
{
    UIAlertController* alert = [UIAlertController alertControllerWithTitle:title
                                                                   message:content
                                                            preferredStyle:UIAlertControllerStyleAlert];
    
    UIAlertAction* defaultAction = [UIAlertAction actionWithTitle:okBtnTitle style:UIAlertActionStyleDefault handler:okHandler];
    UIAlertAction* cencelAction = [UIAlertAction actionWithTitle:cancelBtnTitle style:UIAlertActionStyleCancel handler:cancelHandler];
    
    [alert addAction:defaultAction];
    [alert addAction:cencelAction];
    [parent presentViewController:alert animated:YES completion:nil];
}

@end
