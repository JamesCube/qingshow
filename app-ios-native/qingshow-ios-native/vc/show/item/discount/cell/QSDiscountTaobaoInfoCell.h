//
//  QSDiscountTaobaoInfoCell.h
//  qingshow-ios-native
//
//  Created by wxy325 on 7/30/15.
//  Copyright (c) 2015 QS. All rights reserved.
//

#import <UIKit/UIKit.h>
#import "QSAbstractDiscountTableViewCell.h"
@interface QSDiscountTaobaoInfoCell : QSAbstractDiscountTableViewCell

@property (assign, nonatomic) int infoIndex;

@property (weak, nonatomic) IBOutlet UILabel* titleLabel;
@property (weak, nonatomic) IBOutlet UIView* lineView;
- (NSString*)getResult;
@end