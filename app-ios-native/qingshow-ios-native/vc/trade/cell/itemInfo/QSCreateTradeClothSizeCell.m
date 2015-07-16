//
//  QSCreateTableViewSizeCell.m
//  qingshow-ios-native
//
//  Created by mhy on 15/5/26.
//  Copyright (c) 2015年 QS. All rights reserved.
//

#import "QSCreateTradeClothSizeCell.h"
#import "UIImageView+MKNetworkKitAdditions.h"
#import "QSItemUtil.h"
@implementation QSCreateTradeClothSizeCell

/*
// Only override drawRect: if you perform custom drawing.
// An empty implementation adversely affects performance during animation.
- (void)drawRect:(CGRect)rect {
    // Drawing code
}
*/
- (void)awakeFromNib {
    self.shoulderOrHiplineTextField.delegate = self;
    self.bustCircleOrWaistlineTextField.delegate = self;
}
- (void)bindWithDict:(NSDictionary *)dict
{
    QSItemCategory category = [QSItemUtil getItemCategory:dict];
    if (category == QSItemCategoryClothSize) {
        self.titleLabel.text = @"上身数值";
        self.label1.text = @"胸围：";
        self.bustCircleOrWaistlineTextField.placeholder = @"70";
        self.label2.text = @"肩宽：";
        self.shoulderOrHiplineTextField.placeholder = @"30";
        self.bodyPartImgView.image = [UIImage imageNamed:@"category_shangyi"] ;
    }
    else if(category == QSItemCategoryPant)
    {
        self.titleLabel.text = @"下身数值";
        self.label1.text = @"腰围：";
        self.bustCircleOrWaistlineTextField.placeholder = @"70";
        self.label2.text = @"臀围：";
        self.shoulderOrHiplineTextField.placeholder = @"30";
        self.bodyPartImgView.image = [UIImage imageNamed:@"category_pants"];
    }
}
- (CGFloat)getHeightWithDict:(NSDictionary *)dict
{
    return 184.0f ;
}
- (CGFloat)getBustCircleOrWaistline
{
    return [self.bustCircleOrWaistlineTextField.text floatValue];
}
- (CGFloat)getShoulderOrHipline
{
    return [self.shoulderOrHiplineTextField.text floatValue];
}
- (void)hideKeyboard {
    [self.bustCircleOrWaistlineTextField resignFirstResponder];
    [self.shoulderOrHiplineTextField resignFirstResponder];
}

- (BOOL)textFieldShouldReturn:(UITextField *)textField {
    [self hideKeyboard];
    return YES;
}
@end
