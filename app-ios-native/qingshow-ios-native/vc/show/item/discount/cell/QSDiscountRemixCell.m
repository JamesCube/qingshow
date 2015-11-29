//
//  QSDiscountRemixCell.m
//  qingshow-ios-native
//
//  Created by wxy325 on 15/11/29.
//  Copyright (c) 2015年 QS. All rights reserved.
//

#import "QSDiscountRemixCell.h"
#import "UINib+QSExtension.h"

@implementation QSDiscountRemixCell

+ (instancetype)generateCell {
    return [UINib generateViewWithNibName:@"QSDiscountRemixCell"];
}

- (void)bindWithData:(NSDictionary *)itemDict {
    
}
- (CGFloat)getHeight:(NSDictionary *)itemDict {
    return 440.f;
}

#pragma mark - IBAction
- (IBAction)remixBtnPressed:(UIButton*)sender {
    if ([self.delegate respondsToSelector:@selector(discountCellRemixBtnPressed:)]) {
        [self.delegate discountCellRemixBtnPressed:self];
    }
}

- (IBAction)previousRemixBtnPressed:(UIButton*)sender {
    if ([self.delegate respondsToSelector:@selector(discountCellPreviousRemixBtnPressed:)]) {
        [self.delegate discountCellPreviousRemixBtnPressed:self];
    }
}

- (IBAction)nextRemixBtnPressed:(UIButton*)sender {
    if ([self.delegate respondsToSelector:@selector(discountCellNextRemixBtnPressed:)]) {
        [self.delegate discountCellNextRemixBtnPressed:self];
    }
}
@end
