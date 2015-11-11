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
#import "QSG01ItemWebViewController.h"
#import "QSAbstractRootViewController.h"
#import "QSUnreadManager.h"
#import "QSUserManager.h"

#define PAGE_ID @"U09 - 交易一览"
@interface QSU09TradeListViewController ()

@property (strong,nonatomic) NSDictionary *tradeDict;

@property (strong, nonatomic) QSS12NewTradeExpectableViewController* s11NotiVc;

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
    [self didClickExpectablePriceBtnOfOrder:tradeDict];
//    [SHARE_PAYMENT_SERVICE sharedForTrade:tradeDict onSucceed:^(NSDictionary* d){
//        [self.provider reloadData];
//        QSS11CreateTradeViewController* vc = [[QSS11CreateTradeViewController alloc] initWithDict:d];
//        QSBackBarItem *backItem = [[QSBackBarItem alloc]initWithActionVC:self];
//        vc.navigationItem.leftBarButtonItem = backItem;
//        vc.menuProvider = self.menuProvider;
//        [self.navigationController pushViewController:vc animated:YES];
//    } onError:^(NSError *error) {
//        [self handleError:error];
//    }];
}
- (void)didClickExchangeBtnOfOrder:(NSDictionary *)orderDic
{
    NSString *company = [QSTradeUtil getTradeLogisticCompany:orderDic];
    NSString *trackingId = [QSTradeUtil getTradeLogisticId:orderDic];
    NSString *str = [NSString stringWithFormat:@"物流公司：%@\n物流单号：%@",company,trackingId];
    UIAlertView *alert = [[UIAlertView alloc]initWithTitle:@"物流信息" message:str delegate:self cancelButtonTitle:@"确定" otherButtonTitles:nil, nil];
    [alert show];
}
- (void)didClickReceiveBtnOfOrder:(NSDictionary *)tradeDict
{
    _tradeDict = tradeDict;
    NSDictionary *dic = tradeDict;

    NSDictionary* itemDict = [QSTradeUtil getItemSnapshot:dic];
    NSString *title = [QSItemUtil getItemName:itemDict];
    NSDate *date = [NSDate date];
    NSString *dateStr = [NSString stringWithFormat:@"收货时间  %@",[QSDateUtil buildStringFromDate:date]];
    UIAlertView *alert = [[UIAlertView alloc]initWithTitle:title message:dateStr delegate:self cancelButtonTitle:@"取消" otherButtonTitles:@"确认", nil];
    alert.delegate = self;
    alert.tag = 101;
    [alert show];
    
}
- (void)didClickCancelBtnOfOrder:(NSDictionary *)orderDic
{
    _tradeDict = orderDic;
    UIAlertView *alert = [[UIAlertView alloc]initWithTitle:nil message:@"确认取消订单？" delegate:self cancelButtonTitle:@"取消" otherButtonTitles:@"确认", nil];
    alert.delegate = self;
    alert.tag = 102;
    [alert show];
}

- (void)didClickExpectablePriceBtnOfOrder:(NSDictionary *)orderDict {
    [self showTradeNotiViewOfTradeId:orderDict];
}
- (void)didClickToWebPage:(NSDictionary *)orderDic
{
    NSDictionary *itemDic = [QSTradeUtil getItemDic:orderDic];
    NSString *itemId = [QSItemUtil getItemId:itemDic];
    __weak QSU09TradeListViewController *weakSelf = self;
    [SHARE_NW_ENGINE getItemWithId:itemId onSucceed:^(NSDictionary *item, NSDictionary *metadata) {
        if (item) {
            QSG01ItemWebViewController *vc = [[QSG01ItemWebViewController alloc]initWithItem:item peopleId:[QSTradeUtil getPromoterId:orderDic]];
//            vc.isDisCountBtnHidden = YES;
            [weakSelf.navigationController pushViewController:vc animated:YES];
        }
    } onError:^(NSError *error) {
        
    }];

}
#pragma mark - UIAlertViewDelegate
- (void)alertView:(UIAlertView *)alertView clickedButtonAtIndex:(NSInteger)buttonIndex
{
    if (alertView.tag == 101) {
        if (buttonIndex == 1) {
            __weak QSU09TradeListViewController *weakSelf = self;
            [SHARE_NW_ENGINE changeTrade:_tradeDict status:5 info:nil onSucceed:^(NSDictionary* tradeDict){
                [weakSelf showTextHud:@"收货成功！"];
            } onError:nil];
        }
        else
        {
            [alertView dismissWithClickedButtonIndex:0 animated:YES];
        }

    }
    else if(alertView.tag == 102){
        if (buttonIndex == 1) {
            __weak QSU09TradeListViewController *weakSelf = self;
            [SHARE_NW_ENGINE changeTrade:_tradeDict status:18 info:nil onSucceed:^(NSDictionary* dict){
                [[QSUnreadManager getInstance] clearTradeUnreadId:[QSEntityUtil getIdOrEmptyStr:_tradeDict]];
                if ([QSTradeUtil getStatus:_tradeDict].intValue == 0) {
                    [weakSelf showTextHud:@"已取消订单"];
                }else{
                    [weakSelf showTextHud:@"已取消订单" afterCustomDelay:2.f];
                }
                [weakSelf changeValueOfSegment:0];
                [weakSelf.provider reloadData];
            }onError:nil];

        }
        else
        {
            [alertView dismissWithClickedButtonIndex:0 animated:YES];
        }
    }
}


#pragma mark -
- (void)showTradeNotiViewOfTradeId:(NSDictionary*)tradeDict
{
    [[QSUnreadManager getInstance] clearTradeUnreadId:[QSEntityUtil getIdOrEmptyStr:tradeDict]];
    self.s11NotiVc = [[QSS12NewTradeExpectableViewController alloc] initWithDict:tradeDict];
    self.s11NotiVc.delelgate = self;
    self.s11NotiVc.view.frame = self.navigationController.view.bounds;
    [self.navigationController.view addSubview:self.s11NotiVc.view];
}
    
- (void)didClickClose:(QSS12NewTradeExpectableViewController*)vc {
    [self.s11NotiVc.view removeFromSuperview];
    self.s11NotiVc = nil;
}
- (void)didClickPay:(QSS12NewTradeExpectableViewController*)vc {
    NSDictionary* tradeDict = vc.tradeDict;

    [SHARE_PAYMENT_SERVICE sharedForTrade:tradeDict onSucceed:^(NSDictionary* d){
        [self didClickClose:vc];
        QSS11CreateTradeViewController* v = [[QSS11CreateTradeViewController alloc] initWithDict:d];
        v.menuProvider = self.menuProvider;
        [self.navigationController pushViewController:v animated:YES];
    } onError:^(NSError *error) {
        [vc handleError:error];
    }];
    
}

- (void)triggerChangeToSegmentIndex:(int)index {
    self.headerView.segmentControl.selectedSegmentIndex = index;
    [self changeValueOfSegment:index];
}
- (void)handleNetworkError:(NSError *)error {
    [self handleError:error];
}
@end
