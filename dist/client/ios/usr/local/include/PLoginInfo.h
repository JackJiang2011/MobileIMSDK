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
//  PLoginInfo.h
//  MobileIMSDK4i
//
//  Created by JackJiang on 14/10/22.
//  Copyright (c) 2014年 openmob.net. All rights reserved.
//

#import <Foundation/Foundation.h>

/*!
 * 登陆信息DTO类.
 *
 * @author Jack Jiang, 2014-10-22
 * @version 1.0
 */
@interface PLoginInfo : NSObject

/*! 登陆时提交的用户名：此用户名对框架来说可以随意，具体意义由上层逻辑决即可 */
@property (nonatomic, retain) NSString* loginName;

/*! 登陆时提交的密码：此密码对框架来说可以随意，具体意义由上层逻辑决即可 */
@property (nonatomic, retain) NSString* loginPsw;

/*!
 * 额外信息字符串。本字段目前为保留字段，供上层应用自行放置需要的内容。
 * @since 2.1.6 
 */
@property (nonatomic, retain) NSString* extra;


@end
