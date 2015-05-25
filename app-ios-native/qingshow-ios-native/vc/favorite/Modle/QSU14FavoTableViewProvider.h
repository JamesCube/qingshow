//
//  QSU14FavoTableVIewProvider.h
//  qingshow-ios-native
//
//  Created by 刘少毅 on 15/5/25.
//  Copyright (c) 2015年 QS. All rights reserved.
//

#import "QSTableViewBasicProvider.h"
@class QSU14FavoTableViewProvider;
@protocol QSU14FavoTableViewProviderDelegate <QSAbstractScrollProviderDelegate>

@optional
- (void)didSelectionShow:(NSDictionary*)showDict ofProvider:(QSU14FavoTableViewProvider*)provider;

@end

@interface QSU14FavoTableViewProvider : QSTableViewBasicProvider

@property (nonatomic, strong)NSArray *dataArray;
@property (weak, nonatomic) NSObject<QSU14FavoTableViewProviderDelegate>* delegate;

- (id)initwithArray:(NSArray *)array;

@end
