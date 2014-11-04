//
//  QSModelListViewController.m
//  qingshow-ios-native
//
//  Created by wxy325 on 11/3/14.
//  Copyright (c) 2014 QS. All rights reserved.
//

#import "QSModelListViewController.h"


@interface QSModelListViewController ()

@end

@implementation QSModelListViewController

- (id)init
{
    self = [self initWithNibName:@"QSModelListViewController" bundle:nil];
    if (self) {
        
    }
    return self;
}

#pragma mark - Life Cycle
- (void)viewDidLoad
{
    [super viewDidLoad];
    // Do any additional setup after loading the view.
    [self.tableView registerNib:[UINib nibWithNibName:@"QSModelListTableViewCell" bundle:nil] forCellReuseIdentifier:@"QSModelListTableViewCell"];
    self.navigationItem.title = @"人气达人";

}

- (void)didReceiveMemoryWarning
{
    [super didReceiveMemoryWarning];
    // Dispose of any resources that can be recreated.
}

/*
#pragma mark - Navigation

// In a storyboard-based application, you will often want to do a little preparation before navigation
- (void)prepareForSegue:(UIStoryboardSegue *)segue sender:(id)sender
{
    // Get the new view controller using [segue destinationViewController].
    // Pass the selected object to the new view controller.
}
*/

#pragma mark - UITableView DataSource
- (NSInteger)tableView:(UITableView *)tableView numberOfRowsInSection:(NSInteger)section
{
    return 10;
}
- (UITableViewCell *)tableView:(UITableView *)tableView cellForRowAtIndexPath:(NSIndexPath *)indexPath
{
    QSModelListTableViewCell* cell = (QSModelListTableViewCell*)[tableView dequeueReusableCellWithIdentifier:@"QSModelListTableViewCell" forIndexPath:indexPath];
    cell.delegate = self;

    
    return cell;
}


#pragma mark - UITableView Delegate
- (CGFloat)tableView:(UITableView *)tableView heightForRowAtIndexPath:(NSIndexPath *)indexPath
{
    return 62.f;
}
#pragma mark - QSModelListTableViewCellDelegate
- (void)favorBtnPressed:(QSModelListTableViewCell *)cell
{
    
}


@end
