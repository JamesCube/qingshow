//
//  QSRootContentViewController.h
//  qingshow-ios-native
//
//  Created by wxy325 on 5/20/15.
//  Copyright (c) 2015 QS. All rights reserved.
//

#import <UIKit/UIKit.h>
#import "QSIRootContentViewController.h"

@interface QSRootContentViewController : UIViewController <QSIRootContentViewController>

- (void)handleUnreadChange:(NSNotification*)noti;
@property (strong, nonatomic) UITapGestureRecognizer* showVersionTapGesture;

@end
