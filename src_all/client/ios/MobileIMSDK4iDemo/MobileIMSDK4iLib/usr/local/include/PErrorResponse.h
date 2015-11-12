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
//  PErrorResponse.h
//  MobileIMSDK4i
//
//  Created by JackJiang on 14/10/22.
//  Copyright (c) 2014年 openmob.net. All rights reserved.
//

#import <Foundation/Foundation.h>

/**
 * 错误信息DTO类。
 *
 * @author Jack Jiang, 2014-10-22
 * @version 1.0
 */
@interface PErrorResponse : NSObject

@property (nonatomic, assign) int errorCode;
@property (nonatomic, retain) NSString* errorMsg;

@end
