//
//  QSS03ShowDetailViewController.h
//  qingshow-ios-native
//
//  Created by wxy325 on 11/10/14.
//  Copyright (c) 2014 QS. All rights reserved.
//

#import <UIKit/UIKit.h>

@interface QSS03ShowDetailViewController : UIViewController

#pragma mark - IBOutlet
@property (weak, nonatomic) IBOutlet UIScrollView *containerScrollView;
@property (strong, nonatomic) IBOutlet UIView *contentView;

@property (weak, nonatomic) IBOutlet UIView *showContainer;


@property (weak, nonatomic) IBOutlet UIImageView *headIconImageView;
@property (weak, nonatomic) IBOutlet UILabel *nameLabel;
@property (weak, nonatomic) IBOutlet UILabel *detailLabel;
@property (weak, nonatomic) IBOutlet UILabel *contentLabel;
@property (weak, nonatomic) IBOutlet UIView *itemContainer;
@property (weak, nonatomic) IBOutlet UIButton *commentBtn;
@property (weak, nonatomic) IBOutlet UIButton *favorBtn;

@property (weak, nonatomic) IBOutlet UILabel *favorNumberLabel;

#pragma mark - Init
- (id)init;

#pragma mark - IBAction
- (IBAction)playBtnPressed:(id)sender;
- (IBAction)commentBtnPressed:(id)sender;
- (IBAction)shareBtnPressed:(id)sender;
- (IBAction)favorBtnPressed:(id)sender;

@end
