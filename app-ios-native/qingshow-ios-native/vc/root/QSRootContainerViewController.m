//
//  QSRootContainerViewController.m
//  qingshow-ios-native
//
//  Created by wxy325 on 5/20/15.
//  Copyright (c) 2015 QS. All rights reserved.
//

#import "QSRootContainerViewController.h"
#import <QuartzCore/QuartzCore.h>
#import "QSU01UserDetailViewController.h"
#import "QSU02UserSettingViewController.h"
#import "QSNavigationController.h"
#import "QSU07RegisterViewController.h"
#import "QSUserManager.h"
#import "QSS20MatcherViewController.h"
#import "QSS01MatchShowsViewController.h"

#import "QSU09OrderListViewController.h"
#import "QST01ShowTradeViewController.h"
#import "NSDictionary+QSExtension.h"

#import "QSRootContentViewController.h"
#import "QSPnsHandler.h"

@interface QSRootContainerViewController ()


@property (strong, nonatomic) QSPnsHandler* pnsHandler;

@end

@implementation QSRootContainerViewController

- (instancetype)init {
    self = [super init];
    if (self){
        self.pnsHandler = [[QSPnsHandler alloc] initWithRootVc:self];
    }
    return self;
}

#pragma mark - Life Cycle
- (void)viewDidLoad {
    [super viewDidLoad];
    // Do any additional setup after loading the view.

    [self.navigationController.navigationBar
     setTitleTextAttributes:@{
                              NSFontAttributeName:NAVNEWFONT,
                              NSForegroundColorAttributeName:[UIColor blackColor]
                              }];

}

- (void)viewWillAppear:(BOOL)animated
{
    [super viewWillAppear:animated];
    self.navigationController.navigationBarHidden = YES;
    
    if (![QSUserManager shareUserManager].userInfo) {
        [self.menuView triggerItemTypePressed:QSRootMenuItemMeida];
    }
}
- (void)viewDidLayoutSubviews {
    [super viewDidLayoutSubviews];
    self.contentNavVc.view.frame = self.view.bounds;
}

- (void)didReceiveMemoryWarning {
    [super didReceiveMemoryWarning];
    // Dispose of any resources that can be recreated.
}




#pragma mark - QSRootMenuViewDelegate
- (void)rootMenuViewDidTapBlankView
{
    [self hideMenu];
}
- (void)rootMenuItemPressedType:(QSRootMenuItemType)type oldType:(QSRootMenuItemType)oldType
{
    [super rootMenuItemPressedType:type oldType:oldType];
    [self hideMenu];
    if (oldType != type) {
        UIViewController<QSIRootContentViewController>* vc = nil;
        switch (type) {
            case QSRootMenuItemMy: {
                QSU01UserDetailViewController* u01Vc = [[QSU01UserDetailViewController alloc] initWithCurrentUser];
                u01Vc.menuProvider = self;
                vc = u01Vc;
                break;
            }
            case QSRootMenuItemMeida: {
                QSS01MatchShowsViewController * matcherShowVc = [[QSS01MatchShowsViewController alloc] init];
                matcherShowVc.menuProvider = self;
                vc = matcherShowVc;
                break;
            }
            case QSRootMenuItemSetting: {
                QSU02UserSettingViewController *settingVc = [[QSU02UserSettingViewController alloc]init];
                settingVc.menuProvider = self;
                vc = settingVc;
                break;
            }
            case QSRootMenuItemMatcher: {
                QSS20MatcherViewController* matcherVc = [[QSS20MatcherViewController alloc] init];
                matcherVc.menuProvider = self;
                vc = matcherVc;
                break;
            }
            case QSRootMenuItemDiscount: {
                QSU09OrderListViewController* orderListVc = [[QSU09OrderListViewController alloc] init];
                orderListVc.menuProvider = self;
                vc = orderListVc;
                break;
            }
            case QSRootMenuItemShowTrade:{
                QST01ShowTradeViewController *t01VC = [[QST01ShowTradeViewController alloc]init];
                t01VC.menuProvider = self;
                vc = t01VC;
                break;
            }
            default:{
                break;
            }
        }
        [self showVc:vc];
        
        if ((![vc isKindOfClass:[QSS01MatchShowsViewController class]]) && (![vc isKindOfClass:[QST01ShowTradeViewController class]])) {
            [self showRegisterVc];
        }
    } else {
        if ([self.contentNavVc isKindOfClass:[UINavigationController class]]) {
            [((UINavigationController*)self.contentNavVc) popToRootViewControllerAnimated:NO];
        }
    }
    
    
}

- (void)showVc:(UIViewController<QSIRootContentViewController>*)vc{
    if (!vc) {
        return;
    }
    
    UINavigationController* nav = [[QSNavigationController alloc] initWithRootViewController:vc];
    nav.navigationBar.translucent = NO;
    self.contentVc = vc;
    [self.contentNavVc.view removeFromSuperview];
    [self.contentNavVc willMoveToParentViewController:nil];
    [self.contentNavVc removeFromParentViewController];

    [self.view addSubview:nav.view];
    [self addChildViewController:nav];
    

    [nav didMoveToParentViewController:self];    //Call after transition
    self.contentNavVc = nav;
}
- (UIViewController*)showRegisterVc {
    UIViewController* vc = nil;
    if (![QSUserManager shareUserManager].userInfo) {
        vc = [[QSU07RegisterViewController alloc] init];
        [self.navigationController pushViewController:vc animated:YES];
    }
    return vc;
}
- (UIViewController*)showDefaultVc {
    [self.menuView triggerItemTypePressed:QSRootMenuItemMeida];
    return self.contentVc;
}

- (UIViewController*)triggerToShowVc:(QSRootMenuItemType)type {
    [self.menuView triggerItemTypePressed:type];
    return self.contentVc;
}

@end
