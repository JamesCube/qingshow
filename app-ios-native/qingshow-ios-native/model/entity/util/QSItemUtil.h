//
//  QSItemUtil.h
//  qingshow-ios-native
//
//  Created by wxy325 on 11/23/14.
//  Copyright (c) 2014 QS. All rights reserved.
//

#import <Foundation/Foundation.h>

@interface QSItemUtil : NSObject

+ (NSArray*)getImagesUrl:(NSDictionary*)itemDict;
+ (NSURL*)getFirstImagesUrl:(NSDictionary*)itemDict;
+ (NSDictionary*)getImageMetadata:(NSDictionary*)itemDict;
+ (NSString*)getItemTypeName:(NSDictionary*)itemDict;
+ (NSURL*)getShopUrl:(NSDictionary*)itemDict;

+ (NSString*)getItemName:(NSDictionary*)item;
+ (BOOL)hasDiscountInfo:(NSDictionary*)item;
+ (NSString*)getPrice:(NSDictionary*)item;
+ (NSString*)getPriceAfterDiscount:(NSDictionary*)itemDict;

+ (NSString*)getImageDesc:(NSDictionary*)itemDict atIndex:(int)index;

+ (NSDictionary*)getTaobaoInfo:(NSDictionary*)item;

+ (NSURL*)getSizeExplanation:(NSDictionary*)item;
+ (NSString*)getVideoPath:(NSDictionary*)item;
+ (NSDictionary*)getBrand:(NSDictionary*)item;

+ (BOOL)getIsLike:(NSDictionary*)itemDict;
+ (void)setIsLike:(BOOL)isLike item:(NSDictionary*)itemDict;
+ (void)addNumberLike:(long long)num forItem:(NSDictionary*)itemDict;
+ (NSString*)getNumberLikeDescription:(NSDictionary*)itemDict;
@end
