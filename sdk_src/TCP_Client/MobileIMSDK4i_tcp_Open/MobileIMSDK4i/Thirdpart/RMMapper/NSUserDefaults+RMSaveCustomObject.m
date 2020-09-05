//
//  NSUserDefaults+RMSaveCustomObject.m
//  RMMapper
//
//  Created by Roomorama on 28/6/13.
//  Copyright (c) 2013 Roomorama. All rights reserved.
//

#import "NSUserDefaults+RMSaveCustomObject.h"

@implementation NSUserDefaults (RMSaveCustomObject)

-(void)rm_setCustomObject:(id)obj forKey:(NSString *)key {
    if ([obj respondsToSelector:@selector(encodeWithCoder:)] == NO) {
        NSLog(@"Error save object to NSUserDefaults. Object must respond to encodeWithCoder: message");
        return;
    }
    NSData *encodedObject = [NSKeyedArchiver archivedDataWithRootObject:obj];
    NSUserDefaults *defaults = [NSUserDefaults standardUserDefaults];
    [defaults setObject:encodedObject forKey:key];
    [defaults synchronize];
}

-(id)rm_customObjectForKey:(NSString *)key {
    NSUserDefaults *defaults = [NSUserDefaults standardUserDefaults];
    NSData *encodedObject = [defaults objectForKey:key];
    id obj = [NSKeyedUnarchiver unarchiveObjectWithData:encodedObject];
    return obj;
}

@end