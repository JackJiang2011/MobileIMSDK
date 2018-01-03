#import <Foundation/Foundation.h>

@interface RMMapper : NSObject

/**
 Answer from http://stackoverflow.com/questions/754824/get-an-object-attributes-list-in-objective-c/13000074#13000074
 
 Return dictionary of property name and type from a class.
 Useful for Key-Value Coding.
 */
+ (NSDictionary *)propertiesForClass:(Class)cls;

/** Populate existing object with values from dictionary
 */
+ (id) populateObject:(id)obj fromDictionary:(NSDictionary*)dict;
+ (id) populateObject:(id)obj fromDictionary:(NSDictionary*)dict exclude:(NSArray*)excludeArray;


/** Create a new object with given class and populate it with value from dictionary
 */
+ (id) objectWithClass:(Class)cls fromDictionary:(NSDictionary*)dict;

/** Convert an object to a dictionary
 */
+ (NSDictionary*) dictionaryForObject:(id)obj;
+ (NSDictionary*) dictionaryForObject:(id)obj include:(NSArray*)includeArray;
+ (NSMutableDictionary*) mutableDictionaryForObject:(id)obj;
+ (NSMutableDictionary*) mutableDictionaryForObject:(id)obj include:(NSArray*)includeArray;


/** Convert an array of dict to array of object with predefined class
 */
+ (NSArray*) arrayOfClass:(Class)cls fromArrayOfDictionary:(NSArray*)array;
+ (NSMutableArray*) mutableArrayOfClass:(Class)cls fromArrayOfDictionary:(NSArray*)array;

@end
