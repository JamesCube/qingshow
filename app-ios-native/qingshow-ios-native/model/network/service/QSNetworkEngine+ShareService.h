//
//  QSNetworkEngine+ShareService.h
//  qingshow-ios-native
//
//  Created by mhy on 15/10/13.
//  Copyright (c) 2015年 QS. All rights reserved.
//

#import <Foundation/Foundation.h>
#import "QSNetworkEngine.h"

@interface QSNetworkEngine (ShareService)

- (MKNetworkOperation *)shareCreateShow:(NSString *)showId
                              onSucceed:(StringBlock)succeedBlock
                                onError:(ErrorBlock)errorBlock;

- (MKNetworkOperation *)shareCreateTrade:(NSString *)tradeId
                              onSucceed:(StringBlock)succeedBlock
                                onError:(ErrorBlock)errorBlock;

- (MKNetworkOperation *)shareCreateBonus:(NSString *)peopleId
                              onSucceed:(StringBlock)succeedBlock
                                onError:(ErrorBlock)errorBlock;
@end
