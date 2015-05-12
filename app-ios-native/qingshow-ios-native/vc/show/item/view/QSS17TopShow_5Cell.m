//
//  QSS17TopShow_5Cell.m
//  qingshow-ios-native
//
//  Created by ching show on 15/5/7.
//  Copyright (c) 2015年 QS. All rights reserved.
//

#import "QSS17TopShow_5Cell.h"

@implementation QSS17TopShow_5Cell

- (void)awakeFromNib {
    // Initialization code
    self.backImage.transform = CGAffineTransformMakeRotation(M_1_PI / 4);
    self.sumLabel.transform = CGAffineTransformMakeRotation(-M_1_PI / 4);
}

- (void)setSelected:(BOOL)selected animated:(BOOL)animated {
    [super setSelected:selected animated:animated];

    // Configure the view for the selected state
}

@end
