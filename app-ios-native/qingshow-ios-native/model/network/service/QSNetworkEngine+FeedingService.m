//
//  QSNetworkEngine+FeedingService.m
//  qingshow-ios-native
//
//  Created by wxy325 on 12/7/14.
//  Copyright (c) 2014 QS. All rights reserved.
//

#import "QSNetworkEngine+FeedingService.h"
#import "NSArray+QSExtension.h"
#import "QSNetworkEngine+Protect.h"
#import "QSCommonUtil.h"

//Path
#define PATH_FEEDING_CHOSEN @"feeding/chosen"
#define PATH_FEEDING_BY_MODEL @"feeding/byModel"
#define PATH_FEEDING_HOT @"feeding/hot"
#define PATH_FEEDING_BY_TAGS @"feeding/byTags"
#define PATH_FEEDING_STUDIO @"feeding/studio"
#define PATH_FEEDING_LIKE @"feeding/like"
#define PATH_FEEDING_RECOMMENDATION @"feeding/recommendation"
#define PATH_FEEDING_BY_BRAND @"feeding/byBrand"
#define PATH_FEEDING_BY_BRAND_DISCOUNT @"feeding/byBrandDiscount"
#define PATH_FEEDING_BY_TOPIC @"feeding/byTopic"



@interface QSNetworkEngine (Private)

- (MKNetworkOperation*)getFeedingPath:(NSString*)path
                           otherParam:(NSDictionary*)paramDict
                                 page:(int)page
                            onSucceed:(ArraySuccessBlock)succeedBlock
                              onError:(ErrorBlock)errorBlock;

@end

@implementation QSNetworkEngine(FeedingService)

#pragma mark - Private 
- (MKNetworkOperation*)getFeedingPath:(NSString*)path
                           otherParam:(NSDictionary*)paramDict
                                 page:(int)page
                            onSucceed:(ArraySuccessBlock)succeedBlock
                              onError:(ErrorBlock)errorBlock
{
    NSMutableDictionary* param = [paramDict mutableCopy];
    if (!param) {
        param = [@{} mutableCopy];
    }
    param[@"pageNo"] = @(page);
    if (![[param allKeys] containsObject:@"pageSize"]) {
        param[@"pageSize"] = @20;
    }


    
    return [self startOperationWithPath:path
                                 method:@"GET"
                               paramers:param
                            onSucceeded:^(MKNetworkOperation *completedOperation)
            {
               
                NSDictionary* retDict = completedOperation.responseJSON;
                if (succeedBlock) {
                    NSArray* shows = retDict[@"data"][@"shows"];
                    succeedBlock([shows deepMutableCopy], retDict[@"metadata"]);
                }
            }
                                onError:^(MKNetworkOperation *completedOperation, NSError *error)
            {
                if (errorBlock) {
                    errorBlock(error);
                }
            }];
    
}

#pragma mark - Feeding
- (MKNetworkOperation*)getChosenFeedingType:(int)type
                                       page:(int)page
                                  onSucceed:(ArraySuccessBlock)succeedBlock
                                    onError:(ErrorBlock)errorBlock
{
    return [self getFeedingPath:PATH_FEEDING_CHOSEN otherParam:@{@"type" : @(type)} page:page onSucceed:succeedBlock onError:errorBlock];
}
- (MKNetworkOperation*)getLikeFeedingUser:(NSDictionary*)userDict
                                     page:(int)page
                                onSucceed:(ArraySuccessBlock)succeedBlock
                                  onError:(ErrorBlock)errorBlock
{
    NSMutableDictionary* paramDict = [@{@"pageSize" : @100} mutableCopy];
    if (userDict) {
        paramDict[@"_id"] = userDict[@"_id"];
    }
    return [self getFeedingPath:PATH_FEEDING_LIKE otherParam:paramDict page:page onSucceed:succeedBlock onError:errorBlock];
}
- (MKNetworkOperation*)getRecommendationFeedingPage:(int)page
                                          onSucceed:(ArraySuccessBlock)succeedBlock
                                            onError:(ErrorBlock)errorBlock
{
    return [self getFeedingPath:PATH_FEEDING_RECOMMENDATION otherParam:nil page:page onSucceed:succeedBlock onError:errorBlock];
}


- (MKNetworkOperation*)getCategoryFeeding:(int)type
                                     page:(int)page
                                onSucceed:(ArraySuccessBlock)succeedBlock
                                  onError:(ErrorBlock)errorBlock
{
    NSString* path = nil;
    switch (type) {
        case 1:
            return [self getChosenFeedingType:1 page:page onSucceed:succeedBlock onError:errorBlock];
            break;
        case 2:
            path = PATH_FEEDING_HOT;
            break;
        case 8:
            path = PATH_FEEDING_STUDIO;
            break;
        default:
            break;
    }
    return [self getFeedingPath:path otherParam:nil page:page onSucceed:succeedBlock onError:errorBlock];
}


- (MKNetworkOperation*)getFeedByModel:(NSString*)modelId
                                 page:(int)page
                            onSucceed:(ArraySuccessBlock)succeedBlock
                              onError:(ErrorBlock)errorBlock
{
    return [self getFeedingPath:PATH_FEEDING_BY_MODEL otherParam:@{@"_id" : modelId} page:page onSucceed:succeedBlock onError:errorBlock];
}
- (MKNetworkOperation*)feedingByBrand:(NSDictionary*)brandDict
                                 page:(int)page
                            onSucceed:(ArraySuccessBlock)succeedBlock
                              onError:(ErrorBlock)errorBlock
{
    
    return [self getFeedingPath:PATH_FEEDING_BY_BRAND otherParam:@{@"_id" : [QSCommonUtil getIdOrEmptyStr:brandDict]} page:page onSucceed:succeedBlock onError:errorBlock];
}

- (MKNetworkOperation*)feedingByBrandDiscount:(NSDictionary*)brandDict
                                         page:(int)page
                                    onSucceed:(ArraySuccessBlock)succeedBlock
                                      onError:(ErrorBlock)errorBlock
{
    return [self getFeedingPath:PATH_FEEDING_BY_BRAND_DISCOUNT otherParam:@{@"_id" : [QSCommonUtil getIdOrEmptyStr:brandDict]} page:page onSucceed:succeedBlock onError:errorBlock];
}

- (MKNetworkOperation*)feedingByTopic:(NSDictionary*)topicDic
                                 page:(int)page
                            onSucceed:(ArraySuccessBlock)succeedBlock
                              onError:(ErrorBlock)errorBlock
{
    return [self getFeedingPath:PATH_FEEDING_BY_TOPIC otherParam:@{@"_id" : [QSCommonUtil getIdOrEmptyStr:topicDic]} page:page onSucceed:succeedBlock onError:errorBlock];
}

- (MKNetworkOperation *)hotFeedingByOnSucceed:(ArraySuccessBlock)succeedBlock onError:(ErrorBlock)errorBlock
{
    return [self startOperationWithPath:PATH_FEEDING_HOT method:nil paramers:nil onSucceeded:^(MKNetworkOperation *completedOperation) {
        if (succeedBlock) {
            NSDictionary *topShows = completedOperation.responseJSON;
            NSArray *topShowsArray = topShows[@"data"][@"shows"];
            succeedBlock([topShowsArray deepMutableCopy], topShows[@"metadata"]);
        }
        
        
    } onError:^(MKNetworkOperation *completedOperation, NSError *error) {
        if (errorBlock) {
            errorBlock(error);
        }
    }];
}

@end
