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
//  ChatBaseEventImpl.h
//  RainbowCore4i
//
//  Created by JackJiang on 14/10/28.
//  Copyright (c) 2014年 openmob.net. All rights reserved.
//

#import <Foundation/Foundation.h>
#import "ChatBaseEvent.h"
#import "CompletionDefine.h"

@interface ChatBaseEventImpl : NSObject <ChatBaseEvent>

/** 本Observer目前仅用于登陆时（因为登陆与收到服务端的登陆验证结果是异步的，所以有此观察者来完成收到验证后的处理）*/
@property (nonatomic, copy) ObserverCompletion loginOkForLaunchObserver;// block代码块一定要用copy属性，否则报错！

@end
