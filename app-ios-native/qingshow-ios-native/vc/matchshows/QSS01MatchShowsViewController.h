//
//  QSS01MatchShowsViewController.h
//  qingshow-ios-native
//
//  Created by mhy on 15/6/18.
//  Copyright (c) 2015年 QS. All rights reserved.
//

#import "QSRootContentViewController.h"
#import "QSS11NewTradeNotifyViewController.h"
#import <UIKit/UIKit.h>

@interface QSS01MatchShowsViewController : QSRootContentViewController <QSS11NewTradeNotifyViewControllerDelegate>

@property (weak, nonatomic) IBOutlet UICollectionView *collectionView;
@property (weak, nonatomic) IBOutlet UIButton *backToTopbtn;

- (IBAction)backToTopBtnPressed:(id)sender;

- (void)showTradeNotiViewOfTradeId:(NSString*)tradeId actualPrice:(NSNumber*)actualPrice;

@end