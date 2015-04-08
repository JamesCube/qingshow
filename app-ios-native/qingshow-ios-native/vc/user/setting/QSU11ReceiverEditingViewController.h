//
//  QSU11LocationEditingViewController.h
//  qingshow-ios-native
//
//  Created by wxy325 on 3/14/15.
//  Copyright (c) 2015 QS. All rights reserved.
//

#import <UIKit/UIKit.h>
#import "QSProvinceSelectionTableViewController.h"

@interface QSU11ReceiverEditingViewController : UIViewController<UITableViewDataSource, UITableViewDelegate, QSProvinceSelectionTableViewControllerDelegate>

#pragma mark - Cell
@property (strong, nonatomic) IBOutlet UITableViewCell *nameCell;
@property (strong, nonatomic) IBOutlet UITableViewCell *phoneCell;
@property (strong, nonatomic) IBOutlet UITableViewCell *locationCell;
@property (strong, nonatomic) IBOutlet UITableViewCell *detailLocationCell;


#pragma mark - Label
@property (strong, nonatomic) IBOutlet UITextField *nameTextField;
@property (strong, nonatomic) IBOutlet UITextField *phoneTextField;
@property (strong, nonatomic) IBOutlet UILabel *localLabel;
@property (strong, nonatomic) IBOutlet UITextField *detailLocationTextField;


@property (weak, nonatomic) IBOutlet UITableView *tableView;

- (instancetype)initWithDict:(NSDictionary*)dict;
@property (weak, nonatomic) IBOutlet UIPickerView *provincePicker;

@end
