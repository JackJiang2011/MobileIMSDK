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
//  Copyright (c) 2014年 cngeeker.com. All rights reserved.
//

#import <Foundation/Foundation.h>
#import "ChatBaseEvent.h"
#import "CompletionDefine.h"

@interface ChatBaseEventImpl : NSObject <ChatBaseEvent>

/** !本属性仅作DEBUG之用：DEBUG事件观察者 */
@property (nonatomic, copy) ObserverCompletion debugObserver;// block代码块一定要用copy属性，否则报错！

@end
