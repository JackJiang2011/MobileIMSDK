//
//  NSObject+TDCopy.m
//  Roomorama
//
//  Created by Roomorama on 27/12/12.
//  Copyright (c) 2012 Roomorama. All rights reserved.
//

#import "NSObject+RMCopyable.h"
#import "RMMapper.h"


@implementation NSObject (RMCopyable)

-(id)copyWithZone:(NSZone *)zone {
    typeof(self) copiedObj = [[[self class] allocWithZone:zone] init];
    if (copiedObj) {
        NSDictionary* properties = [RMMapper propertiesForClass:[self class]];
        for (NSString* key in properties) {
            id val = [self valueForKey:key];
            [copiedObj setValue:val forKey:key];
        }
    }
    return copiedObj;
}


@end
