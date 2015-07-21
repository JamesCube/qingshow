//
//  QSWaterFallCollectionViewCell.m
//  qingshow-ios-native
//
//  Created by wxy325 on 10/31/14.
//  Copyright (c) 2014 QS. All rights reserved.
//

#import <QuartzCore/QuartzCore.h>

#import "QSShowCollectionViewCell.h"
#import "UIImageView+MKNetworkKitAdditions.h"
#import "ServerPath.h"
#import "DatabaseConstant.h"

#import "QSShowUtil.h"
#import "QSPeopleUtil.h"

@interface QSShowCollectionViewCell ()

- (void)updateLayoutWithData:(NSDictionary*)showData;


//Basic height
- (void)baseHeightSetup;
- (void)updateViewFrame:(UIView*)view withBase:(float)base imageHeight:(float)imgHeight;
@property (assign, nonatomic) float headIconImageViewBaseY;
@property (assign, nonatomic) float nameLabelBaseY;
@property (assign, nonatomic) float favorNumberLabelBaseY;
@property (assign, nonatomic) float favorButtonBaseY;
@property (assign, nonatomic) float modelTapViewBaseY;
@end


@implementation QSShowCollectionViewCell

#pragma mark - Life Cycle
- (void)awakeFromNib
{
    [super awakeFromNib];
    [self baseHeightSetup];
    self.headIconImageView.layer.cornerRadius = self.headIconImageView.frame.size.height / 2;
    self.headIconImageView.layer.masksToBounds = YES;
    self.headIconImageView.layer.borderColor = [UIColor whiteColor].CGColor;
    self.headIconImageView.layer.borderWidth = 1.f;
}

#pragma mark - Data
- (void)bindData:(NSDictionary*)showData
{
    //[self updateLayoutWithData:showData];

    
    
    
    //NSLog(@"showData = %@",showData);
    NSDictionary* modelDict = [QSShowUtil getPeopleFromShow:showData];
    if (modelDict) {
        self.nameLabel.hidden = NO;
        self.headIconImageView.hidden = NO;
        self.nameLabel.text = [QSPeopleUtil getNickname:modelDict];
        [self.headIconImageView setImageFromURL:[QSPeopleUtil getHeadIconUrl:modelDict]];
    } else {
        self.nameLabel.hidden = YES;
        self.headIconImageView.hidden = YES;
    }
    
    
    
    [self.backgroundImageView setImageFromURL:[QSShowUtil getCoverBackgroundUrl:showData]];
    [self.foregroundImageView setImageFromURL:[QSShowUtil getCoverForegroundUrl:showData]];
    [self.photoImageView setImageFromURL:[QSShowUtil getCoverUrl:showData] placeHolderImage:[UIImage imageNamed:@"root_cell_placehold_image1"] animation:NO];
    self.favorNumberLabel.text = [QSShowUtil getNumberLikeDescription:showData];
   // self.favorButton.selected = [QSShowUtil getIsLike:showData];
    
    
    
}

#pragma mark - Layout Update
- (void)baseHeightSetup
{
    float baseHeight = self.frame.size.height - 1;
    
    self.headIconImageViewBaseY = self.headIconImageView.frame.origin.y - baseHeight;
    self.nameLabelBaseY = self.nameLabel.frame.origin.y - baseHeight;
    self.favorNumberLabelBaseY = self.favorNumberLabel.frame.origin.y - baseHeight;
    //self.favorButtonBaseY = self.favorButton.frame.origin.y - baseHeight;
    self.modelTapViewBaseY = self.modelTapView.frame.origin.y - baseHeight;
}
- (void)updateLayoutWithData:(NSDictionary*)showData
{

    float height = [QSShowCollectionViewCell getImageHeightWithData:showData];
    CGRect photoRect = self.photoImageView.frame;
    photoRect.size.height = height;
    self.photoImageView.frame = photoRect;
    self.backgroundImageView.frame = self.photoImageView.frame;
    self.foregroundImageView.frame = self.photoImageView.frame;
    
    //layout other view
    [self updateViewFrame:self.headIconImageView withBase:self.headIconImageViewBaseY imageHeight:height];
    [self updateViewFrame:self.nameLabel withBase:self.nameLabelBaseY imageHeight:height];
    [self updateViewFrame:self.favorNumberLabel withBase:self.favorNumberLabelBaseY imageHeight:height];
    //[self updateViewFrame:self.favorButton withBase:self.favorButtonBaseY imageHeight:height];
    [self updateViewFrame:self.modelTapView withBase:self.modelTapViewBaseY imageHeight:height];
}

- (void)updateViewFrame:(UIView*)view withBase:(float)base imageHeight:(float)imgHeight
{
    CGRect rect = view.frame;
    rect.origin.y = base + imgHeight;
    view.frame = rect;
}

#pragma mark - Static Method
+ (float)getImageHeightWithData:(NSDictionary*)showData
{
    return ([UIScreen mainScreen].bounds.size.width - 4) / 2 / 9 * 16 + 2;
}

+ (float)getHeightWithData:(NSDictionary*)showData
{
    return [self getImageHeightWithData:showData];
}
+ (CGSize)getSizeWithData:(NSDictionary*)showData
{
    float height = [self getHeightWithData:showData];
    float widht = ([UIScreen mainScreen].bounds.size.width - 4) / 2;
    return CGSizeMake(widht, height);
}

#pragma mark - IBAction
- (IBAction)favorBtnPressed:(id)sender
{
    if ([self.delegate respondsToSelector:@selector(favorBtnPressed:)]) {
        [self.delegate favorBtnPressed:self];
    }
}
@end
