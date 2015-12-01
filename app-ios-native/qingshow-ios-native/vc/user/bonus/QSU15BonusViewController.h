//
//  QSU15BonusViewController.h
//  qingshow-ios-native
//
//  Created by mhy on 15/8/31.
//  Copyright (c) 2015年 QS. All rights reserved.
//

#import <UIKit/UIKit.h>

@interface QSU15BonusViewController : UIViewController<UIAlertViewDelegate>
#pragma mark - Container
@property (weak, nonatomic) IBOutlet UIScrollView *containerView;

#pragma mark - Content
@property (strong, nonatomic) IBOutlet UIView *bonusContentView;
@property (weak, nonatomic) IBOutlet UILabel *allBonusLabel;
@property (weak, nonatomic) IBOutlet UILabel *currBonusLabel;
@property (weak, nonatomic) IBOutlet UIButton *withdrawBtn;
@property (weak, nonatomic) IBOutlet UIScrollView *scrollView;
@property (weak, nonatomic) IBOutlet UIButton *faqBtn;

#pragma mark - Faq Layer
@property (strong, nonatomic) IBOutlet UIView *faqLayer;
@property (weak, nonatomic) IBOutlet UIScrollView *faqContainerScrollView;
#pragma mark - Faq Content
@property (strong, nonatomic) IBOutlet UIImageView *faqContentImgView;



@property (strong,nonatomic)NSString *peopleId;
- (instancetype)initwithBonuesArray:(NSArray *)array;

@end
