//
//  QSU09TradeListViewController.m
//  qingshow-ios-native
//
//  Created by wxy325 on 3/10/15.
//  Copyright (c) 2015 QS. All rights reserved.
//

#import "QSU09TradeListViewController.h"
#import "QSU12RefundViewController.h"
#import "QSS11CreateTradeViewController.h"
#import "QSNetworkKit.h"
#import "WXApi.h"
#import "UIViewController+QSExtension.h"
#import "QSPaymentService.h"
#import "UIViewController+ShowHud.h"
#import "QSItemUtil.h"
#import "QSDateUtil.h"
#import "QSTradeUtil.h"
#import "QSPeopleUtil.h"
#import "QSS10ItemDetailViewController.h"
#import "QSAbstractRootViewController.h"
#import "QSUnreadManager.h"
#import "QSUserManager.h"
#import "QSNotificationHelper.h"

#define PAGE_ID @"U09 - 交易一览"
@interface QSU09TradeListViewController ()

@property (strong,nonatomic) NSDictionary *tradeDict;

@property (assign, nonatomic)BOOL isFirstLoad;


@end

@implementation QSU09TradeListViewController

#pragma mark - Init
- (instancetype)init
{
    self = [super initWithNibName:@"QSU09TradeListViewController" bundle:nil];
    if (self) {
        
    }
    return self;
}

#pragma mark - Life Cycle
- (void)viewDidLoad {
    [super viewDidLoad];
    _isFirstLoad = YES;
    // Do any additional setup after loading the view from its nib.
    [self configProvider];
    [self configView];
    [self.navigationController.navigationBar setTitleTextAttributes:
     @{NSFontAttributeName:NAVNEWFONT,
       NSForegroundColorAttributeName:[UIColor blackColor]}];
    if ([UIScreen mainScreen].bounds.size.width == 414) {
        self.view.transform = CGAffineTransformMakeScale(1.3, 1.3);
    }
    [[NSNotificationCenter defaultCenter] addObserver:self selector:@selector(handleWillEnterForeground:) name:UIApplicationWillEnterForegroundNotification object:nil];
    
    if ([[QSUnreadManager getInstance] shouldShowTradeUnreadOfType:QSUnreadTradeTypeTradeShipped]) {
        [self triggerChangeToSegmentIndex:1];
    }
    
}
- (void)viewWillAppear:(BOOL)animated
{
    [super viewWillAppear:animated];
    [self.provider reloadData];
    self.navigationController.navigationBarHidden = NO;
    [MobClick beginLogPageView:PAGE_ID];

    NSDictionary* u = [QSUserManager shareUserManager].userInfo;
    if (!u || [QSPeopleUtil getPeopleRole:u] == QSPeopleRoleGuest) {
        [self.menuProvider triggerToShowVc:QSRootMenuItemMeida];
    }
    
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
- (void)dealloc {
    [[NSNotificationCenter defaultCenter] removeObserver:self];
}
- (void)handleWillEnterForeground:(NSNotification*)noti {
    [self.provider reloadData];
}
- (void)handleUnreadChange:(NSNotification*)noti {
    [super handleUnreadChange:noti];
    [self.provider.view reloadData];
}

- (void)willPresentAlertView:(UIAlertView *)alertView
{
    for (UIView *view in alertView.subviews) {
        if ([view isKindOfClass:[UILabel class]]) {
            UILabel *label = (UILabel *)view;
            if ([label.text isEqualToString:alertView.message]) {
                label.font = NEWFONT;
            }
        }
    }
}

- (void)configView {
    self.automaticallyAdjustsScrollViewInsets = NO;
    self.title = @"我的订单";
    _headerView = [QSTradeListHeaderView makeView];
    _headerView.delegate = self;
//    headerView.segmentControl.selectedSegmentIndex = 1;
//    [self changeValueOfSegment:1];
    self.tableView.tableHeaderView = _headerView;
    self.tableView.backgroundColor = [UIColor colorWithRed:204.f/255.f green:204.f/255.f blue:204.f/255.f alpha:1.f];
    
    [self hideNaviBackBtnTitle];
}
- (void)configProvider
{
    self.provider = [[QSTradeListTableViewProvider alloc] init];
    [self.provider bindWithTableView:self.tableView];
    __weak QSU09TradeListViewController *weakSelf = self;
    self.provider.networkBlock = ^MKNetworkOperation*(ArraySuccessBlock succeedBlock, ErrorBlock errorBlock, int page){
        return [SHARE_NW_ENGINE queryPhase:page phases:@"0" onSucceed:succeedBlock onError:^(NSError *error){
                if (error.code == 1009 && page == 1  && _isFirstLoad == YES) {
                    weakSelf.headerView.segmentControl.selectedSegmentIndex = 1;
                    [weakSelf changeValueOfSegment:1];
                }else(errorBlock(error));
        }];
    };
    _isFirstLoad = NO;
    self.provider.delegate = self;
}

#pragma mark - QSTradeListHeaderViewDelegate
- (void)changeValueOfSegment:(NSInteger)value
{
    if (value == 1) {
        self.provider.networkBlock = ^MKNetworkOperation*(ArraySuccessBlock succeedBlock, ErrorBlock errorBlock, int page){
            return [SHARE_NW_ENGINE queryPhase:page phases:@"1,2" onSucceed:succeedBlock onError:errorBlock];
        };
        [self.provider reloadData];
    }
    else if(value == 0)
    {
        self.provider.networkBlock = ^MKNetworkOperation*(ArraySuccessBlock succeedBlock, ErrorBlock errorBlock, int page){
            return [SHARE_NW_ENGINE queryPhase:page phases:@"0" onSucceed:succeedBlock onError:errorBlock];
        };
        [self.provider reloadData];
    }
}
- (void)didTapPhone:(NSString*)phoneNumber {
    if (!phoneNumber.length) {
        return;
    }
    NSString* str = [[NSString alloc] initWithFormat:@"telprompt://%@",phoneNumber];
    [[UIApplication sharedApplication] openURL:[NSURL URLWithString:str]];
}

#pragma mark - QSTradeListTableViewProviderDelegate
- (void)didClickOrder:(NSDictionary *)orderDict {
}
- (void)didClickRefundBtnOfOrder:(NSDictionary*)tradeDict
{
    
    QSU12RefundViewController* vc = [[QSU12RefundViewController alloc] initWithDict:tradeDict actionVC:self];
    QSBackBarItem *backItem = [[QSBackBarItem alloc]initWithActionVC:self];
    vc.navigationItem.leftBarButtonItem = backItem;
    [self.navigationController pushViewController:vc animated:YES];
}

- (void)didClickPayBtnOfOrder:(NSDictionary *)tradeDict
{
#warning TODO
    [self showTradeNotiViewOfTradeId:tradeDict];
}

- (void)didClickLogisticBtnOfOrder:(NSDictionary *)orderDic
{
    NSString *company = [QSTradeUtil getTradeLogisticCompany:orderDic];
    NSString *trackingId = [QSTradeUtil getTradeLogisticId:orderDic];
    NSString *str = [NSString stringWithFormat:@"物流公司：%@\n物流单号：%@",company,trackingId];
    UIAlertView *alert = [[UIAlertView alloc]initWithTitle:@"物流信息" message:str delegate:self cancelButtonTitle:@"确定" otherButtonTitles:nil, nil];
    [alert show];
}

- (void)didClickToWebPage:(NSDictionary *)orderDic
{
    NSDictionary *itemDic = [QSTradeUtil getItemDic:orderDic];
    NSString *itemId = [QSItemUtil getItemId:itemDic];
    __weak QSU09TradeListViewController *weakSelf = self;
    [SHARE_NW_ENGINE getItemWithId:itemId onSucceed:^(NSDictionary *item, NSDictionary *metadata) {
        if (item) {
            QSS10ItemDetailViewController *vc = [[QSS10ItemDetailViewController alloc]initWithItem:item];
//            vc.isDisCountBtnHidden = YES;
            [weakSelf.navigationController pushViewController:vc animated:YES];
        }
    } onError:^(NSError *error) {
        
    }];

}

#pragma mark -
- (void)showTradeNotiViewOfTradeId:(NSDictionary*)tradeDict
{
    [[QSUnreadManager getInstance] clearTradeUnreadId:[QSEntityUtil getIdOrEmptyStr:tradeDict]];
    [QSNotificationHelper postShowTradeExpectablePriceChangeVcNotiWithTradeDict:tradeDict];
}

- (void)triggerChangeToSegmentIndex:(int)index {
    self.headerView.segmentControl.selectedSegmentIndex = index;
    [self changeValueOfSegment:index];
}
- (void)handleNetworkError:(NSError *)error {
    [self handleError:error];
}
@end
