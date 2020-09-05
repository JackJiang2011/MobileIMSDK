//
//  NSUserDefaults+RMSaveCustomObject.h
//  RMMapper
//
//  Created by Roomorama on 28/6/13.
//  Copyright (c) 2013 Roomorama. All rights reserved.
//

#import <Foundation/Foundation.h>

@interface NSUserDefaults (RMSaveCustomObject)


/** Save to NSUserDefaults. The obj must be Archivable. Otherwise it would not be savable.
 The easiest way to make object archivable is to import NSObject+RMArchivable.h
 
 Answer from http://stackoverflow.com/questions/2315948/how-to-store-custom-objects-in-nsuserdefaults/2315972#2315972
 */
-(void) rm_setCustomObject:(id)obj forKey:(NSString*)key;


/** Load to NSUserDefaults
 */
-(id) rm_customObjectForKey:(NSString*)key;

@end
