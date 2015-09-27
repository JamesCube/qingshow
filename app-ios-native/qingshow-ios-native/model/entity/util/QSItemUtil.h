//
//  QSItemUtil.h
//  qingshow-ios-native
//
//  Created by wxy325 on 11/23/14.
//  Copyright (c) 2014 QS. All rights reserved.
//

#import <Foundation/Foundation.h>

@interface QSItemUtil : NSObject

+ (NSURL*)getThumbnail:(NSDictionary*)itemDict;
+ (NSURL*)getShopUrl:(NSDictionary*)itemDict;

+ (NSString*)getItemName:(NSDictionary*)item;
+ (NSNumber*)getPrice:(NSDictionary*)itemDict;
+ (NSString*)getPriceDesc:(NSDictionary*)item;
+ (NSNumber*)getPromoPrice:(NSDictionary*)itemDict;
+ (NSString*)getPromoPriceDesc:(NSDictionary*)itemDict;
+ (NSString*)getReturnInfoAddr:(NSDictionary*)itemDict;
+ (NSString*)getReturnInfoCompany:(NSDictionary*)itemDict;
+ (NSString*)getReturnInfoPhone:(NSDictionary*)itemDict;

+ (NSString *)getReturnAddrFromDic:(NSDictionary *)dict;
+ (NSString *)getReturnNameFromDic:(NSDictionary *)dict;
+ (NSString *)getReturnPhoneFromDic:(NSDictionary *)dict;
+ (NSMutableAttributedString *)getAttrbuteStr:(NSString *)str;
+ (NSNumber *)getExpectablePrice:(NSDictionary *)dict;
+ (NSDictionary*)getCategoryRef:(NSDictionary*)itemDict;
+ (NSString*)getCategoryStr:(NSDictionary*)itemDict;
+ (NSArray*)getSkuProperties:(NSDictionary*)itemDict;
+ (NSDictionary*)getSkuTable:(NSDictionary*)itemDict;
+ (NSString*)getKeyValueForSkuTableFromeSkuProperties:(NSArray*)skuArray;
+ (int)getFirstValueFromSkuTableWithkey:(NSString*)key itemDic:(NSDictionary*)itemDic;
+ (NSNumber*)getMinExpectionPrice:(NSDictionary*)itemDict;
+ (BOOL)getReadOnly:(NSDictionary*)itemDict;
+ (BOOL)getDelist:(NSDictionary *)itemDict;
+ (NSString*)getItemId:(NSDictionary *)itemDict;
+ (NSArray*)getMatchSkuKeysForItem:(NSDictionary*)itemDict skuKeys:(NSArray*)skuKeys;
@end
