//
//  QSCommonUtil.m
//  qingshow-ios-native
//
//  Created by wxy325 on 12/10/14.
//  Copyright (c) 2014 QS. All rights reserved.
//

#import "QSCommonUtil.h"

@implementation QSCommonUtil
+ (BOOL)checkIsNil:(id)obj
{
    return !obj || [obj isKindOfClass:[NSNull class]];
}
+ (BOOL)checkIsDict:(id)obj
{
    if ([self checkIsNil:obj]) {
        return NO;
    }
    return [obj isKindOfClass:[NSDictionary class]];
}
+ (BOOL)checkIsArray:(id)obj
{
    if ([self checkIsNil:obj]) {
        return NO;
    }
    return [obj isKindOfClass:[NSArray class]];
}
+ (NSString*)getIdOrEmptyStr:(NSDictionary*)dict
{
    if (![self checkIsDict:dict]) {
        return @"";
    }
    NSString* idString = dict[@"_id"];
    if ([self checkIsNil:idString]) {
        return @"";
    }
    return idString;
}
+ (NSString *)getCommentsStr:(NSDictionary *)dict
{
    if (![self checkIsDict:dict]) {
        return nil;
    }
    NSString *commentsStr = dict[@"_comments"];
    if ([self checkIsNil:commentsStr]) {
        return @"";
    }
    return commentsStr;
}

+ (id)getValue:(NSDictionary*)dict key:(NSString*)key class:(Class)c {
    if ([self checkIsNil:dict]) {
        return nil;
    }
    id v = dict[key];
    if ([self checkIsNil:v] || ![v isKindOfClass:c]) {
        return nil;
    } else {
        return v;
    }
}

+ (NSString*)getStringValue:(NSDictionary*)dict key:(NSString*)key {
    return [self getValue:dict key:key class:[NSString class]];
}

+ (NSArray*)getArrayValue:(NSDictionary*)dict key:(NSString*)key {
    return [self getValue:dict key:key class:[NSArray class]];
}
@end
