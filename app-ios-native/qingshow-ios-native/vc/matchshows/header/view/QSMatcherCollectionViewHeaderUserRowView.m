//
//  QSMatcherCollectionViewHeaderUserRowView.m
//  qingshow-ios-native
//
//  Created by wxy325 on 15/11/24.
//  Copyright © 2015年 QS. All rights reserved.
//

#import "QSMatcherCollectionViewHeaderUserRowView.h"
#import "QSMatcherCollectionViewHeaderUserView.h"
#import "QSPeopleUtil.h"
#import "UIImageView+MKNetworkKitAdditions.h"
#import "NSArray+QSExtension.h"

#define HEAD_NUMBER 8
#define SPACE_X 1.f

@interface QSMatcherCollectionViewHeaderUserRowView()
@property (strong, nonatomic) NSMutableArray* headerViews;
@property (strong, nonatomic) NSArray* userArray;
@end

@implementation QSMatcherCollectionViewHeaderUserRowView
- (instancetype)init {
    self = [super init];
    if (self) {
        self.alignCenter = NO;
        self.headerViews = [@[] mutableCopy];
        for (int i = 0; i < HEAD_NUMBER; i++) {
            UIView* v = [QSMatcherCollectionViewHeaderUserView generateView];
            v.userInteractionEnabled = YES;
            UITapGestureRecognizer* ges = [[UITapGestureRecognizer alloc] initWithTarget:self action:@selector(didTapHead:)];
            [v addGestureRecognizer:ges];
            [self.headerViews addObject:v];
            [self addSubview:v];
        }
    }
    return self;
}
- (void)layoutSubviews {
    [super layoutSubviews];
    CGFloat initX = 0;
    CGSize containerSize = self.bounds.size;
    CGFloat blockWidth = containerSize.width / HEAD_NUMBER;
    CGFloat headRadius = blockWidth - SPACE_X;
    CGFloat rate = headRadius / 50.f;
    CGFloat centerY = containerSize.height / 2;
    
    if (self.alignCenter) {
        initX = (containerSize.width - blockWidth * self.userArray.count) / 2;
    }


    for (int i = 0; i < self.headerViews.count; i++) {
        UIView* headerView = self.headerViews[i];
        headerView.transform = CGAffineTransformMakeScale(1, 1);
        headerView.frame = CGRectMake(0, 0, 50, 50);
        headerView.transform = CGAffineTransformMakeScale(rate, rate);
//        headerView.bounds = CGRectMake(0, 0, headRadius, headRadius);
        headerView.center = CGPointMake(initX + i * blockWidth + headRadius / 2, centerY);
    }
    
}

- (void)bindWithUsers:(NSArray*)users {
    users = [users filteredArrayUsingBlock:^BOOL(id u) {
        return ![QSEntityUtil checkIsNil:u];
    }];
    self.userArray = users;
    
    for (int i = 0; i < self.headerViews.count; i++) {
        QSMatcherCollectionViewHeaderUserView* imgView = self.headerViews[i];
        if (i < users.count) {
            NSDictionary* u = users[i];
            imgView.hidden = NO;
            NSURL* url = [QSPeopleUtil getHeadIconUrl:u type:QSImageNameType50];
            [imgView.headerImgView setImageFromURL:url];
            imgView.iconImgView.image = [QSPeopleUtil rankImgView:u];
        } else {
            imgView.hidden = YES;
        }
    }
    [self layoutIfNeeded];
}

- (void)didTapHead:(UITapGestureRecognizer*)ges {
    UIView* v = ges.view;
    NSUInteger i = [self.headerViews indexOfObject:v];
    if (i != NSNotFound) {
        if ([self.delegate respondsToSelector:@selector(userRowView:didClickIndex:)]) {
            [self.delegate userRowView:self didClickIndex:i];
        }
    }
    
}

@end
