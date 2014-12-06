//
//  QSShowUtil.h
//  qingshow-ios-native
//
//  Created by wxy325 on 11/23/14.
//  Copyright (c) 2014 QS. All rights reserved.
//

#import <Foundation/Foundation.h>

@interface QSShowUtil : NSObject

+ (NSArray*)getShowVideoPreviewUrlArray:(NSDictionary*)dict;
+ (NSArray*)getItemsImageUrlArrayFromShow:(NSDictionary*)dict;
+ (NSDictionary*)getItemFromShow:(NSDictionary*)showDict AtIndex:(int)index;
+ (NSArray*)getItems:(NSDictionary*)showDict;
+ (NSDictionary*)getPeopleFromShow:(NSDictionary*)showDict;

+ (NSString*)getNumberCommentsDescription:(NSDictionary*)showDict;
+ (NSString*)getNumberLikeDescription:(NSDictionary*)showDict;
+ (BOOL)getIsLike:(NSDictionary*)showDict;
+ (void)setIsLike:(BOOL)isLike show:(NSDictionary*)showDict;

@end
