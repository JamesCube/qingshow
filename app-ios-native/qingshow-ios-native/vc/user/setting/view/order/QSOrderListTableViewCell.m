//
//  QSOrderListTableViewCell.m
//  qingshow-ios-native
//
//  Created by wxy325 on 3/11/15.
//  Copyright (c) 2015 QS. All rights reserved.
//

#import "QSOrderListTableViewCell.h"
#import <QuartzCore/QuartzCore.h>
#import "QSTradeUtil.h"
#import "QSOrderUtil.h"
#import "QSItemUtil.h"
#import "QSTaobaoInfoUtil.h"
#import "QSCommonUtil.h"
#import "UIImageView+MKNetworkKitAdditions.h"

@interface QSOrderListTableViewCell ()

@property (strong, nonatomic) NSDictionary* tradeDict;

@property (assign, nonatomic) float skuLabelBaseY;
@end

@implementation QSOrderListTableViewCell

#pragma mark - Init Config
- (void)awakeFromNib {
    // Initialization code
//    [self configBtn:self.refundButton];
    [self configBtn:self.submitButton];
    self.selectionStyle = UITableViewCellSelectionStyleNone;
    self.skuLabelBaseY = self.sizeTextLabel.frame.origin.y;
}
- (void)configBtn:(UIButton*)btn
{
    UIColor* color = btn.titleLabel.textColor;
    btn.layer.borderColor = color.CGColor;
    btn.layer.borderWidth = 1.f;
    btn.layer.cornerRadius = 4.f;
    btn.layer.masksToBounds = YES;
}

- (void)setSelected:(BOOL)selected animated:(BOOL)animated {
    [super setSelected:selected animated:animated];

    // Configure the view for the selected state
}

#pragma mark - Binding
- (void)bindWithDict:(NSDictionary*)tradeDict
{

    self.tradeDict = tradeDict;
    NSArray* orderList = [QSTradeUtil getOrderArray:tradeDict];
    NSDictionary* orderDict = nil;
    if (orderList.count) {
        orderDict = orderList[0];
    }
    NSDictionary* itemDict = [QSOrderUtil getItemSnapshot:orderDict];
    NSDictionary* taobaoInfo = [QSItemUtil getTaobaoInfo:itemDict];
    
//    self.orderIdLabel.text = [QSCommonUtil getIdOrEmptyStr:tradeDict];
    self.stateLabel.text = [QSTradeUtil getStatusDesc:tradeDict];
    self.titleLabel.text = [QSItemUtil getItemName:itemDict];
    [self.itemImgView setImageFromURL:[QSItemUtil getFirstImagesUrl:itemDict]];
    
    self.priceLabel.text = [QSOrderUtil getPriceDesc:orderDict];
    self.quantityLabel.text = [QSOrderUtil getQuantityDesc:orderDict];

    NSString* sku = [QSOrderUtil getSkuId:orderDict];
    NSDictionary* skuDict = [QSTaobaoInfoUtil findSkusWithSkuId:sku taobaoInfo:taobaoInfo];

    float height = self.skuLabelBaseY;

    NSString* size = [QSTaobaoInfoUtil getSizeOfSku:skuDict];
    if (size && size.length) {
        self.sizeLabel.text = size;
        self.sizeTextLabel.hidden = NO;
        self.sizeLabel.hidden = NO;
        [self updateView:self.sizeTextLabel y:height];
        [self updateView:self.sizeLabel y:height];
        height += 20;
    } else {
        self.sizeTextLabel.hidden = YES;
        self.sizeLabel.hidden = YES;
    }
    
    NSString* color = [QSTaobaoInfoUtil getColorOfSku:skuDict];
    if (color && color.length) {
        self.colorLabel.text = color;
        self.colorTextLabel.hidden = NO;
        self.colorLabel.hidden = NO;
        [self updateView:self.colorTextLabel y:height];
        [self updateView:self.colorLabel y:height];
        height += 20;
    } else {
        self.colorTextLabel.hidden = YES;
        self.colorLabel.hidden = YES;
    }
    
    for (UIView* view in @[self.quantityLabel, self.quantityTextLabel, self.priceLabel, self.priceTextLabel]) {
        [self updateView:view y:height];
    }
    
    NSNumber* status = [QSTradeUtil getStatus:tradeDict];
    if (status.intValue == 0) {
        self.submitButton.hidden = NO;
        [self.submitButton setTitle:@"付款" forState:UIControlStateNormal];
    } else if (status.intValue < 5 && status.intValue > 0) {
        self.submitButton.hidden = NO;
        [self.submitButton setTitle:@"申请退货" forState:UIControlStateNormal];
    } else {
        self.submitButton.hidden = YES;
    }
}
- (void)updateView:(UIView*)view y:(float)y
{
    CGRect rect = view.frame;
    rect.origin.y = y;
    view.frame = rect;
}

#pragma mark - Getter And Setter
//- (void)setType:(QSOrderListTableViewCellType)type
//{
//    _type = type;
//    BOOL fHiddenLabel = NO;
//    switch (type) {
//        case QSOrderListTableViewCellTypeComplete:
//        {
//            self.stateLabel.text = @"交易完成";
//            fHiddenLabel = NO;
//
//            break;
//        }
//        case QSOrderListTableViewCellTypeWaiting:
//        {
//            self.stateLabel.text = @"待收货";
//            fHiddenLabel = YES;
//            break;
//        }
//        default:
//        {
//            break;
//        }
//    }
//    self.dateEndLabel.hidden = fHiddenLabel;
//    self.dateEndTextLabel.hidden = fHiddenLabel;
//    self.dateStartLabel.hidden = fHiddenLabel;
//    self.dateStartTextLabel.hidden = fHiddenLabel;
//    self.submitButton.hidden = !fHiddenLabel;
//    self.refundButton.hidden = !fHiddenLabel;
//}

#pragma mark - IBAction
- (IBAction)refundBtnPressed:(id)sender
{
    if ([self.delegate respondsToSelector:@selector(didClickRefundBtnForCell:)]) {
        [self.delegate didClickRefundBtnForCell:self];
    }
}
- (IBAction)submitBtnPressed:(id)sender
{
    int status = [QSTradeUtil getStatus:self.tradeDict].intValue;
    if (status == 0) {
        [self payBtnPressed];
    } else if (status > 0 && status < 5) {
        [self refundBtnPressed:sender];
    }
}
- (void)payBtnPressed
{
    if ([self.delegate respondsToSelector:@selector(didClickPayBtnForCell:)]) {
        [self.delegate didClickPayBtnForCell:self];
    }
}

@end