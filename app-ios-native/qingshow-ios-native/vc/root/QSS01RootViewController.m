//
//  QSViewController.m
//  qingshow-ios-native
//
//  Created by wxy325 on 10/31/14.
//  Copyright (c) 2014 QS. All rights reserved.
//

#import "QSS01RootViewController.h"
#import "QSNetworkEngine.h"
#import "QSP01ModelListViewController.h"
#import "QSP03BrandListViewController.h"

#import "QSU02UserSettingViewController.h"
#import "QSS03ShowDetailViewController.h"
#import "QSU06LoginViewController.h"


@interface QSS01RootViewController ()

@property (strong, nonatomic) QSRootMenuView* menuView;
@property (assign, nonatomic) BOOL fIsShowMenu;
@property (strong, nonatomic) QSShowCollectionViewDelegateObj* delegateObj;
@property (assign, nonatomic) BOOL fISLogined;
@end

@implementation QSS01RootViewController
#pragma mark - 
- (id)init
{
    self = [self initWithNibName:@"QSS01RootViewController" bundle:nil];
    if (self) {
        
    }
    return self;
}
#pragma mark - Life Cycle
- (void)viewDidLoad
{
    [super viewDidLoad];
	// Do any additional setup after loading the view, typically from a nib.MyLayout *layout=[[MyLayout alloc]init];
    [self configDelegateObj];
    [self configNavBar];
    QSRootMenuView* menuView = [QSRootMenuView generateView];
    [self.menuContainer addSubview:menuView];
    self.menuView = menuView;
    self.fIsShowMenu = NO;
    self.fISLogined = NO;
    menuView.delegate = self;
    
    UIBarButtonItem *backButton = [[UIBarButtonItem alloc] initWithTitle:@" " style:UIBarButtonItemStyleDone target:nil action:nil];
    [[self navigationItem] setBackBarButtonItem:backButton];
}

- (void)didReceiveMemoryWarning
{
    [super didReceiveMemoryWarning];
    // Dispose of any resources that can be recreated.
}
#pragma mark - Network
- (void)configDelegateObj
{
    self.delegateObj = [[QSShowCollectionViewDelegateObj alloc] init];
    self.delegateObj.delegate = self;
    [self.delegateObj bindWithCollectionView:self.collectionView];
    self.delegateObj.networkBlock = ^MKNetworkOperation*(ArraySuccessBlock succeedBlock, ErrorBlock errorBlock, int page){
        return [SHARE_NW_ENGINE getChosenFeedingPage:page onSucceed:succeedBlock onError:errorBlock];
    };
    [self.delegateObj fetchDataOfPage:1];
}

#pragma mark - Configure View
- (void)configNavBar
{
    self.navigationController.navigationBar.tintColor = [UIColor colorWithRed:89.f/255.f green:86.f/255.f blue:86.f/255.f alpha:1.f];
    UIImageView* titleImageView = [[UIImageView alloc] initWithImage:[UIImage imageNamed:@"nav_btn_image_logo"]];
    self.navigationItem.titleView = titleImageView;
    
    UIBarButtonItem* menuItem = [[UIBarButtonItem alloc] initWithImage:[UIImage imageNamed:@"nav_btn_menu"] style:UIBarButtonItemStylePlain target:self action:@selector(menuButtonPressed)];
    self.navigationItem.leftBarButtonItem = menuItem;
    
    UIBarButtonItem* rightButtonItem;
    [SHARE_NW_ENGINE getLoginUserOnSucced: ^(NSDictionary* data, NSDictionary* metadata) {
        NSLog(@"complete");
        if (data != nil) {
            self.fISLogined = YES;
        } else {
            self.fISLogined = NO;
        }
    }
                                  onError: ^(NSError *error) {
                                      NSLog(@"Error");
                                  }];
    
    if (self.fISLogined) {
        rightButtonItem = [[UIBarButtonItem alloc] initWithImage:[UIImage imageNamed:@"nav_btn_account"] style:UIBarButtonItemStylePlain target:self action:@selector(accountButtonPressed)];
    } else {
        rightButtonItem = [[UIBarButtonItem alloc] initWithImage:[UIImage imageNamed:@"nav_btn_account"] style:UIBarButtonItemStylePlain target:self action:@selector(loginButtonPressed)];
    }
    self.navigationItem.rightBarButtonItem = rightButtonItem;
}

#pragma mark - IBAction
- (void)menuButtonPressed
{
    __weak QSS01RootViewController* weakSelf = self;
    if (self.fIsShowMenu)
    {
        [self.menuView hideMenuAnimationComple:^{
            weakSelf.menuContainer.hidden = YES;
        }];
    }
    else
    {
        weakSelf.menuContainer.hidden = NO;
        [self.menuView showMenuAnimationComple:^{
        }];
    }
    self.fIsShowMenu = !self.fIsShowMenu;
}
- (void)accountButtonPressed
{
    UIStoryboard *tableViewStoryboard = [UIStoryboard storyboardWithName:@"QSU02UserSetting"
                                                                  bundle:nil];
    QSU02UserSettingViewController *vc = [tableViewStoryboard
                                          instantiateViewControllerWithIdentifier:@"U02UserSetting"];
    //QSU02UserSettingViewController *vc = [[QSU02UserSettingViewController alloc]init];
    [self.navigationController pushViewController:vc animated:YES];
}

- (void) loginButtonPressed
{
    NSLog(@"GOTO LOGIN");
    
    UIViewController *vc = [[QSU06LoginViewController alloc]initWithNibName:@"QSU06LoginViewController" bundle:nil];
    [self.navigationController pushViewController:vc animated:YES];
}

#pragma mark - QSWaterFallCollectionViewCellDelegate
- (void)favorBtnPressed:(QSShowCollectionViewCell*)cell
{
    
    
}
#pragma mark - QSRootMenuViewDelegate
- (void)rootMenuItemPressedType:(int)type
{
    switch (type) {
        case 3:
        {
            UIViewController* vc = [[QSP01ModelListViewController alloc] init];
            [self.navigationController pushViewController:vc animated:YES];
            break;
        }
        case 9:
        {
            UIViewController* vc = [[QSP03BrandListViewController alloc] init];
            [self.navigationController pushViewController:vc animated:YES];
            break;
        }
        default:
            break;
    }


}

#pragma mark - QSShowWaterfallDelegateObjDelegate
- (void)scrollViewDidScroll:(UIScrollView *)scrollView
{
    if (self.fIsShowMenu)
    {
        __weak QSS01RootViewController* weakSelf = self;
        [self.menuView hideMenuAnimationComple:^{
            weakSelf.menuContainer.hidden = YES;
        }];
        self.fIsShowMenu = NO;
    }
}
- (void)didClickShow:(NSDictionary*)showDict
{
    UIViewController* vc = [[QSS03ShowDetailViewController alloc] init];
    [self.navigationController pushViewController:vc animated:YES];
}
@end
