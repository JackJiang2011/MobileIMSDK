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
//  CompletionDefine.h
//  MobileIMSDK4i
//
//  Created by JackJiang on 14/10/27.
//  Copyright (c) 2014年 openmob.net. All rights reserved.
//

#import <Foundation/Foundation.h>

/*!
 * 本接口中定义了一些用于回调的block类型。
 *
 * @author Jack Jiang,2014-10-29
 * @since 2.1
 */
@interface CompletionDefine : NSObject

/*!
 *  @Author Jack Jiang, 14-10-29 19:10:17
 *
 *  通用回调，应用场景是模拟Java中的Obsrver观察者模式。
 *
 *  @param observerble 此参数通常为nil，字段意义可自行定义
 *  @param arg1        通常为回调时的数据（字段意义可自行定义），可为nil
 */
typedef void (^ObserverCompletion)(id observerble ,id arg1);

/*!
 *  @Author Jack Jiang, 14-10-29 19:10:41
 *
 *  UDP Socket连接结果回调。
 *
 *  @param connectRsult YES表示连接成功，NO否则表示连接失败
 */
typedef void (^ConnectionCompletion)(BOOL connectRsult);

@end
