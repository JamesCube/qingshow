//
//  QSS03ShowDetailViewController.h
//  qingshow-ios-native
//
//  Created by wxy325 on 11/10/14.
//  Copyright (c) 2014 QS. All rights reserved.
//

#import <UIKit/UIKit.h>

@interface QSS03ShowDetailViewController : UIViewController

@property (weak, nonatomic) IBOutlet UIScrollView *containerScrollView;
@property (strong, nonatomic) IBOutlet UIView *contentView;

- (id)init;

@end
