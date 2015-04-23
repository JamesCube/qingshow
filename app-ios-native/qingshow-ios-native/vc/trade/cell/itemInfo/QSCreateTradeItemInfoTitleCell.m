//
//  QSCreateTradeItemInfoTitleCell.m
//  qingshow-ios-native
//
//  Created by wxy325 on 3/16/15.
//  Copyright (c) 2015 QS. All rights reserved.
//

#import "QSCreateTradeItemInfoTitleCell.h"
#import "QSItemUtil.h"
#import "QSTaobaoInfoUtil.h"

@implementation QSCreateTradeItemInfoTitleCell

/*
// Only override drawRect: if you perform custom drawing.
// An empty implementation adversely affects performance during animation.
- (void)drawRect:(CGRect)rect {
    // Drawing code
}
*/

- (void)bindWithDict:(NSDictionary*)dict
{
    self.titleLabel.text = [QSItemUtil getItemName:dict];
    [self updateWithItemPrice:dict];
}

- (void)updateWithSize:(NSString*)sizeSku color:(NSString*)colorSku item:(NSDictionary*)itemDict
{
    NSDictionary* taobaoInfo = [QSItemUtil getTaobaoInfo:itemDict];
    NSString* price = [QSTaobaoInfoUtil getPriceOfSize:sizeSku color:colorSku taobaoInfo:taobaoInfo];
    NSString* prom_price = [QSTaobaoInfoUtil getPromoPriceOfSize:sizeSku color:colorSku taobaoInfo:taobaoInfo];
    
    if (!prom_price) {
        [self updateWithItemPrice:itemDict];
        return;
    }
    self.priceAfterDiscountLabel.text = prom_price ;
    if ([price isEqualToString:prom_price]) {
        self.priceLabel.hidden = YES;
        self.priceTextLabel.hidden = YES;
    } else {
        self.priceLabel.hidden = NO;
        self.priceTextLabel.hidden = NO;
        self.priceLabel.text =  price;
    }
}
- (void)updateWithItemPrice:(NSDictionary*)itemDict {
    if ([QSItemUtil hasDiscountInfo:itemDict]) {
        self.priceTextLabel.hidden = NO;
        self.priceLabel.hidden = NO;
        self.priceAfterDiscountLabel.text = [QSItemUtil getPriceAfterDiscount:itemDict];
        self.priceLabel.text = [QSItemUtil getPrice:itemDict];
        [self.priceAfterDiscountLabel sizeToFit];
        [self.priceLabel sizeToFit];
    } else {
        self.priceTextLabel.hidden = YES;
        self.priceLabel.hidden = YES;
        self.priceLabel.text = @"";
        self.priceAfterDiscountLabel.text = [QSItemUtil getPrice:itemDict];
        [self.priceLabel sizeToFit];
        [self.priceAfterDiscountLabel sizeToFit];
        
    }
}
@end