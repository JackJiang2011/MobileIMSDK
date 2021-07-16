//
//  PKickoutInfo.h
//  MobileIMSDK4i
//
//  Created by Jack Jiang on 2021/7/8.
//  Copyright © 2021 cngeeker.com. All rights reserved.
//

#import <Foundation/Foundation.h>


#define KICKOUT_FOR_DUPLICATE_LOGIN  1
#define KICKOUT_FOR_ADMIN            2


@interface PKickoutInfo : NSObject

@property (nonatomic, assign) int code;
@property (nonatomic, retain) NSString*reason;

@end

