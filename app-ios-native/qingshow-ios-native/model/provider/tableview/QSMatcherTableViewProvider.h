//
//  QSMatcherTableViewProvider.h
//  qingshow-ios-native
//
//  Created by wxy325 on 15/11/20.
//  Copyright (c) 2015年 QS. All rights reserved.
//

#import "QSTableViewBasicProvider.h"
@class QSMatcherTableViewProvider;

@protocol QSMatcherTableViewProviderDelegate <QSAbstractScrollProviderDelegate>

- (void)provider:(QSMatcherTableViewProvider*)provider didClickDate:(NSDate*)date;

@end

@interface QSMatcherTableViewProvider : QSTableViewBasicProvider

@property (weak, nonatomic) NSObject<QSMatcherTableViewProviderDelegate>* delegate;

@end
