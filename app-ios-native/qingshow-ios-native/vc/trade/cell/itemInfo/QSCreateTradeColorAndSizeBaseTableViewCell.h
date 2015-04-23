//
//  QSCreateTradeColorAndSizeBaseTableViewCell.h
//  qingshow-ios-native
//
//  Created by wxy325 on 3/20/15.
//  Copyright (c) 2015 QS. All rights reserved.
//

#import <UIKit/UIKit.h>
#import "QSCreateTradeTableViewCellBase.h"
@class QSTradeSelectButton;
@class QSCreateTradeColorAndSizeBaseTableViewCell;

@interface QSCreateTradeColorAndSizeBaseTableViewCell : QSCreateTradeTableViewCellBase

@property (strong, nonatomic) NSArray* skusArray;
@property (strong, nonatomic) NSMutableArray* btnArray;
@property (strong, nonatomic) QSTradeSelectButton* currentSelectBtn;


- (void)btnPressed:(QSTradeSelectButton*)btn;
- (void)enableAllBtn;
@end