//
//  QSS17ViewController.h
//  qingshow-ios-native
//
//  Created by mhy on 15/5/20.
//  Copyright (c) 2015年 QS. All rights reserved.
//

#import <UIKit/UIKit.h>
#import "QSTableViewBasicProvider.h"
#import "QSShareViewController.h"
@interface QSS17ViewController : UIViewController<QSShareViewControllerDelegate>

@property (weak, nonatomic) IBOutlet UITableView *topShowTableView;

@end

