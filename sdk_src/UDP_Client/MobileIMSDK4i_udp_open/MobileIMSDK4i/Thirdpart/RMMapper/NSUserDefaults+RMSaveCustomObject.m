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
    
//    NSData *encodedObject = [NSKeyedArchiver archivedDataWithRootObject:obj];
//    NSUserDefaults *defaults = [NSUserDefaults standardUserDefaults];
//    [defaults setObject:encodedObject forKey:key];
//    [defaults synchronize];
    
    NSError *error = nil;
    // 或者YES，如果你的对象支持并需要安全编码
    BOOL requiresSecureCoding = NO;
    // 使用iOS 12以后的新方法进行归档
    NSData *archivedData = [NSKeyedArchiver archivedDataWithRootObject:obj
                                                    requiringSecureCoding:requiresSecureCoding
                                                                  error:&error];
    if (error) {
        NSLog(@"Error during archiving: %@", error.localizedDescription);
    } else {
        // 成功归档后的处理
        NSUserDefaults *defaults = [NSUserDefaults standardUserDefaults];
        [defaults setObject:archivedData forKey:key];
        [defaults synchronize];
    }
}

-(id)rm_customObjectForKey:(NSString *)key withClass:(Class)clazz {
    NSUserDefaults *defaults = [NSUserDefaults standardUserDefaults];
    NSData *encodedObject = [defaults objectForKey:key];
    
//    id obj = [NSKeyedUnarchiver unarchiveObjectWithData:encodedObject];
//    return obj;
    
    NSError *error = nil;
    // 使用iOS 12以后的新方法进行反归档
    id myObject = [NSKeyedUnarchiver unarchivedObjectOfClass:clazz fromData:encodedObject error:&error];

    if (error) {
        NSLog(@"Error during unarchiving: %@", error.localizedDescription);
    } else if (myObject) {
        // 成功解档后的处理
        return myObject;
    } else {
        NSLog(@"Unable to unarchive object.");
    }
    
    return nil;
}

@end
