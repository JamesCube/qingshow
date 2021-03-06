//
//  QSMatcherTableViewCell.h
//  qingshow-ios-native
//
//  Created by wxy325 on 15/11/20.
//  Copyright (c) 2015年 QS. All rights reserved.
//

#import <UIKit/UIKit.h>


#define QSMatcherTableViewCellId @"QSMatcherTableViewCellId"

@class QSMatcherTableViewCell;
@protocol QSMatcherTableViewCellDelegate <NSObject>

- (void)cell:(QSMatcherTableViewCell*)cell didClickUser:(NSDictionary*)dict;
- (void)cell:(QSMatcherTableViewCell*)cell didClickStickyShow:(NSDictionary*)dict;
@end

@interface QSMatcherTableViewCell : UITableViewCell

@property (weak, nonatomic) IBOutlet UILabel* timeLabel;
@property (weak, nonatomic) IBOutlet UILabel* dateLabel;
@property (weak, nonatomic) IBOutlet UIImageView* eyeImgView;
@property (weak, nonatomic) IBOutlet UIImageView* userHeadImgView;
@property (weak, nonatomic) IBOutlet UILabel* numLabel;
@property (weak, nonatomic) IBOutlet UIView* usersContainer;
@property (strong, nonatomic) IBOutletCollection(UIImageView) NSArray *showImgViews;
@property (strong, nonatomic) IBOutletCollection(UIImageView) NSArray *showForegroundImgViews;
@property (strong, nonatomic) IBOutletCollection(UIView) NSArray *showBackgroundViews;
@property (weak, nonatomic) NSObject<QSMatcherTableViewCellDelegate>* delegate;
@property (weak, nonatomic) IBOutlet UIImageView* rankImgView;

@property (weak, nonatomic) IBOutlet UIImageView* stickyImageView;

@property (weak, nonatomic) IBOutlet UIView* bottomContainer;

@property (strong, nonatomic) IBOutlet UIView* userHeadContainer;
@property (strong, nonatomic) IBOutlet UIView* showContainer;
@property (strong, nonatomic) IBOutlet UIView* stickyContainer;
@property (strong, nonatomic) IBOutlet UIImageView* bottomContainerBackground;

- (void)bindWithDict:(NSDictionary*)dict;
+ (CGFloat)getHeightWithDict:(NSDictionary*)dict;
@end
