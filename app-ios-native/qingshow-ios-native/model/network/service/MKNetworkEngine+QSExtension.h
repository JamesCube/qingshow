//
//  QSNetworkEngine+Protect.h
//  qingshow-ios-native
//
//  Created by wxy325 on 12/10/14.
//  Copyright (c) 2014 QS. All rights reserved.
//

#import <Foundation/Foundation.h>

#import "MKNetworkEngine.h"

@interface MKNetworkEngine (QSExtension)

- (MKNetworkOperation*)startOperationWithPath:(NSString*)path
                                       method:(NSString*)method
                                     paramers:(NSDictionary*)paramDict
                                  onSucceeded:(OperationSucceedBlock)succeedBlock
                                      onError:(OperationErrorBlock)errorBlock;
- (MKNetworkOperation*)startOperationWithPathNoVersion:(NSString*)path
                                       method:(NSString*)method
                                     paramers:(NSDictionary*)paramDict
                                  onSucceeded:(OperationSucceedBlock)succeedBlock
                                      onError:(OperationErrorBlock)errorBlock;
- (MKNetworkOperation *)startOperationWithPath:(NSString *)path
                                        method:(NSString *)method
                                      paramers:(NSDictionary *)paramDict
                                       fileKey:(NSString *)fileKey
                                         image:(NSData *)image
                                   onSucceeded:(OperationSucceedBlock)succeedBlock
                                       onError:(OperationErrorBlock)errorBlock;
- (MKNetworkOperation *)startOperationWithPath:(NSString *)path
                                        method:(NSString *)method
                                      paramers:(NSDictionary *)paramDict
                                       fileKey:(NSString *)fileKey
                                      fileName:(NSString*)fileName
                                         image:(NSData *)image
                                   onSucceeded:(OperationSucceedBlock)succeedBlock
                                       onError:(OperationErrorBlock)errorBlock;

@end