//
//  QSU15BonusListTableViewCell.m
//  qingshow-ios-native
//
//  Created by mhy on 15/8/31.
//  Copyright (c) 2015年 QS. All rights reserved.
//

#import "QSU15BonusListTableViewCell.h"
#import "QSPeopleUtil.h"
#import "QSDateUtil.h"
#import "UIImageView+MKNetworkKitAdditions.h"
@implementation QSU15BonusListTableViewCell

- (void)awakeFromNib {
    // Initialization code
}

- (void)bindWithDict:(NSDictionary *)dict
{
    self.nameLabel.text = [QSPeopleUtil getNoteFromBonusDict:dict];
    NSString *dateStr = [QSPeopleUtil getCreateFromBonusDict:dict];
    NSDate *date = [QSDateUtil buildDateFromResponseString:dateStr];
    self.dateLabel.text = [QSDateUtil buildStringFromDate:date];
    NSNumber* money = [QSPeopleUtil getMoneyFromBonusDict:dict];
    double m = 0.0;
    if (money) {
        m = money.doubleValue;
    }
    self.priceLabel.text = [NSString stringWithFormat:@"￥%.2f",m];
    [self.headerImgView setImageFromURL:[NSURL URLWithString:[QSPeopleUtil getIconFromBonusDict:dict]]];
}
- (void)setSelected:(BOOL)selected animated:(BOOL)animated {
    [super setSelected:selected animated:animated];

    // Configure the view for the selected state
}

@end
