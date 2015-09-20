//
//  QSModelBadgeView.m
//  qingshow-ios-native
//
//  Created by wxy325 on 11/5/14.
//  Copyright (c) 2014 QS. All rights reserved.
//

#import "QSBadgeView.h"
#import "UIImageView+MKNetworkKitAdditions.h"
#import <QuartzCore/QuartzCore.h>
#import "QSPeopleUtil.h"
#import "UINib+QSExtension.h"

@interface QSBadgeView ()

@property (weak, nonatomic) IBOutlet UIImageView *backgroundImageView;
@property (weak, nonatomic) IBOutlet UIImageView *iconImageView;

@property (weak, nonatomic) IBOutlet UILabel *nameLabel;
@property (weak, nonatomic) IBOutlet UILabel *statusLabel;


@end

@implementation QSBadgeView

#pragma mark - Static Method
+ (QSBadgeView*)generateView
{
    return [UINib generateViewWithNibName:@"QSBadgeView"];
}

#pragma mark - Life Cycle
- (void)awakeFromNib
{
    self.iconImageView.layer.cornerRadius = self.iconImageView.frame.size.height / 2;
    self.iconImageView.layer.masksToBounds = YES;
    
    CGRect rect = self.frame;
    rect.size.width = [UIScreen mainScreen].bounds.size.width;
    self.frame = rect;
    self.btnGroup = [[QSBadgeBtnGroup alloc] initWithTypes:
  @[
    @(QSBadgeButtonTypeMatcher),
    @(QSBadgeButtonTypeRecommend),
    @(QSBadgeButtonTypeFavor),
    @(QSBadgeButtonTypeFollowing),
    @(QSBadgeButtonTypeFollower)
    ]];
    self.btnGroup.frame = self.btnsContainer.bounds;
    [self.btnsContainer addSubview:self.btnGroup];
}


#pragma mark - Binding
- (void)bindWithPeopleDict:(NSDictionary*)peopleDict
{
    self.iconImageView.layer.cornerRadius = self.iconImageView.frame.size.height / 2;
    self.iconImageView.layer.masksToBounds = YES;
    self.iconImageView.layer.borderColor = [UIColor whiteColor].CGColor;
    self.iconImageView.layer.borderWidth = 2.f;
    self.nameLabel.font = NEWFONT;
    self.nameLabel.text = [QSPeopleUtil getNickname:peopleDict];
    NSMutableString* statusStr = [@"" mutableCopy];
    NSString* height = [QSPeopleUtil getHeight:peopleDict];
    if (height && height.length) {
        [statusStr appendFormat:@"%@cm ", height];
    }
    NSString* weight = [QSPeopleUtil getWeight:peopleDict];
    if (weight && weight.length) {
        [statusStr appendFormat:@"%@kg ", weight];
    }
    self.statusLabel.text = statusStr;
    float bonus = 0;
    for (NSDictionary *dic in [QSPeopleUtil getBonusList:peopleDict]) {
        bonus += [QSPeopleUtil getMoneyFromBonusDict:dic].floatValue;
    }
    self.bonusLabel.text = [NSString stringWithFormat:@"佣金:￥%.2f",bonus];
    if([QSPeopleUtil getHeadIconUrl:peopleDict])
    {
    [self.iconImageView setImageFromURL:[QSPeopleUtil getHeadIconUrl:peopleDict type:QSImageNameType200] placeHolderImage:[UIImage imageNamed:@"user_head_default.jpg"] animation:YES];
    [self.backgroundImageView setImageFromURL:[QSPeopleUtil getBackgroundUrl:peopleDict] placeHolderImage:[UIImage imageNamed:@"user_bg_default.jpg"] animation:YES];
    }
    self.followBtn.selected = [QSPeopleUtil getPeopleIsFollowed:peopleDict];
}

- (void)layoutSubviews {
    [super layoutSubviews];
    self.btnGroup.frame = self.btnsContainer.bounds;
}

- (UIView*)hitTest:(CGPoint)point withEvent:(UIEvent *)event {
    UIView* originView = [super hitTest:point withEvent:event];
    
    if ([self.btnsContainer pointInside:[self convertPoint:point toView:self.btnsContainer] withEvent:event] || originView == self.followBtn) {
        return originView;
    } else {
        if (self.touchDelegateView) {
            return self.touchDelegateView;
        } else {
            return originView;
        }
    }
}
- (BOOL)canBecomeFirstResponder {
    return NO;
}
@end
