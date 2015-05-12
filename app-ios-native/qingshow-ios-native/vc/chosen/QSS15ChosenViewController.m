//
//  QSS15ChosenViewController.m
//  qingshow-ios-native
//
//  Created by wxy325 on 5/6/15.
//  Copyright (c) 2015 QS. All rights reserved.
//

#define PAGE_ID @"S15"

#import "QSS15ChosenViewController.h"
#import "QSNetworkKit.h"
#import "QSChosenUtil.h"
#import "UIViewController+QSExtension.h"

@interface QSS15ChosenViewController ()

@property (strong, nonatomic) QSBigImageTableViewProvider* provider;

@end

@implementation QSS15ChosenViewController

#pragma mark - Init
- (instancetype)init {
    self = [super initWithNibName:@"QSS15ChosenViewController" bundle:nil];
    if (self) {
        
    }
    return self;
}

#pragma mark - Life Cycle
- (void)viewDidLoad {
    [super viewDidLoad];
    [self configProvider];

}

- (void)viewWillAppear:(BOOL)animated
{
    [super viewWillAppear:animated];
    [self.provider refreshClickedData];
    [MobClick beginLogPageView:PAGE_ID];
}

- (void)viewDidDisappear:(BOOL)animated
{
    [super viewDidDisappear:animated];
    [MobClick endLogPageView:PAGE_ID];
}

- (void)didReceiveMemoryWarning {
    [super didReceiveMemoryWarning];
    // Dispose of any resources that can be recreated.
}

#pragma mark -
- (void)configProvider {
    __weak QSS15ChosenViewController* weakSelf = self;
    self.provider = [[QSBigImageTableViewProvider alloc] init];
    self.provider.type = QSBigImageTableViewCellTypeChosen;
//    self.provider.hasPaging = NO;
    self.provider.delegate = self;
    [self.provider bindWithTableView:self.tableView];
    self.provider.networkBlock = ^MKNetworkOperation*(ArraySuccessBlock succeedBlock, ErrorBlock errorBlock, int page){
        return [SHARE_NW_ENGINE chosenFeedByType:ChosenTypeHome page:page onSucceed:succeedBlock onError:^(NSError *error) {
            if ([error.domain isEqualToString:NSURLErrorDomain] && error.code == -1009) {
                UIAlertView* a = [[UIAlertView alloc] initWithTitle:@"未连接网络或信号不好" message:nil delegate:weakSelf cancelButtonTitle:@"确定" otherButtonTitles: nil];
                [a show];
            } else {
                errorBlock(error);
            }
        }];
    };
    [self.provider fetchDataOfPage:1];
}

#pragma mark - QSBigImageTableViewProviderDelegate

- (void)didClickCell:(UITableViewCell*)cell ofData:(NSDictionary*)dict type:(QSBigImageTableViewCellType)type
{
    QSChosenRefType chosenType = [QSChosenUtil getChosenRefType:dict];
    NSDictionary* ref = [QSChosenUtil getRef:dict];
    switch (chosenType) {
        case QSChosenRefTypeItem:
        {
            [self showItemDetailViewController:ref];
            break;
        }
        case QSChosenRefTypePreview:
        {
            [self showPreviewDetailViewController:ref];
            break;
        }
        case QSChosenRefTypeShow:
        {
            [self showShowDetailViewController:ref];
            break;
        }
        default:
            break;
    }
}

- (void)clickLikeOfDict:(NSDictionary*)dict type:(QSBigImageTableViewCellType)type
{
    QSChosenRefType chosenType = [QSChosenUtil getChosenRefType:dict];
    NSDictionary* ref = [QSChosenUtil getRef:dict];
    if (chosenType == QSChosenRefTypeShow) {
        [SHARE_NW_ENGINE handleShowLike:ref onSucceed:^(BOOL f) {
            [self.provider rebindData:dict];
        } onError:^(NSError *error) {
            [self handleError:error];
        }];
    } else if (chosenType == QSChosenRefTypeItem) {
        [SHARE_NW_ENGINE handleItemLike:ref onSucceed:^(BOOL f) {
            [self.provider rebindData:dict];
        } onError:^(NSError *error) {
            [self handleError:error];
        }];
    }
    [self.provider rebindData:ref];
    
}
@end
