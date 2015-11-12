//
//  UIViewController+Ext.h
//  MobileIMSDK4iDemo
//
//  Created by JackJiang on 15/11/8.
//  Copyright © 2015年 cngeeker.com. All rights reserved.
//

#import <UIKit/UIKit.h>

@interface UIViewController (Ext)

- (IBAction)E_textFieldDidEndOnExit:(id)sender;

- (IBAction)E_clickBgToHideKeyboard:(id)sender;

- (void) E_showToastInfo:(NSString *)title withContent:(NSString *)content onParent:(UIView *)parentView;

@end
