//
//  QSG02WelcomeViewController.m
//  qingshow-ios-native
//
//  Created by wxy325 on 5/8/15.
//  Copyright (c) 2015 QS. All rights reserved.
//

#import "QSG02WelcomeViewController.h"
//#import "QSS01RootViewController.h"
//#import "QSAppDelegate.h"

#define w ([UIScreen mainScreen].bounds.size.width)
#define h ([UIScreen mainScreen].bounds.size.height)
@interface QSG02WelcomeViewController ()

@end

@implementation QSG02WelcomeViewController
- (instancetype)init
{
    self = [super initWithNibName:@"QSG02WelcomeViewController" bundle:nil];
    if (self) {
        
    }
    return self;
}

#pragma mark - Life Cycle
- (void)viewDidLoad {
    [super viewDidLoad];
    // Do any additional setup after loading the view from its nib.
    self.view.frame = [UIScreen mainScreen].bounds;
    _loginBtn.frame = CGRectMake(w/5*4, h/10*9, 60, 30);
    _welcomeSCV.frame = CGRectMake(0, 0, w, h);
    _welcomeSCV.contentSize = CGSizeMake(w*3, h);
    _welcomeSCV.pagingEnabled = YES;
    _welcomeSCV.alpha = 1.0f;
    [self addPhotosToSVC];
}

//- (void)dealloc
//{
//    NSLog(@"%s dealloc", __func__);
//}

- (void)addPhotosToSVC
{
    UIImage *img01 = nil;
    UIImage *img02 = nil;
    UIImage *img03 = nil;
    unsigned int  num = 0;
    if (self.view.bounds.size.width == 320) {
        if (self.view.bounds.size.height == 568) {
            num = 568;
        }
        else{
             num = 480;
        }
    }
    else if(self.view.bounds.size.width == 375){
        num = 667;
    }
    else if(self.view.bounds.size.width == 414)
    {
        num = 736;
    }
    NSLog(@"w = %f,h = %f",w,h);
    
    NSString *imgName01 = [NSString stringWithFormat:@"welcome1_%d",num];
    NSString *imgName02 = [NSString stringWithFormat:@"welcome2_%d",num];
    NSString *imgName03 = [NSString stringWithFormat:@"welcome3_%d",num];
    img01 = [UIImage imageNamed:imgName01];
    img02 = [UIImage imageNamed:imgName02];
    img03 = [UIImage imageNamed:imgName03];
    
    for (int i = 0;  i < 3; i ++) {
        UIImageView *imageView = [[UIImageView alloc]initWithFrame:CGRectMake(w*i, 0, w, h)];
        [_welcomeSCV addSubview:imageView];
        if (i == 0) {
            imageView.image = img01;
        }
        else if(i == 1)
        {
            imageView.image = img02;
        }
        else if(i == 2)
        {
            imageView.image = img03;
        }
    }

}

- (void)didReceiveMemoryWarning {
    [super didReceiveMemoryWarning];
    // Dispose of any resources that can be recreated.
}

#define ISFIRSTLOGIN @"isFirstLogin"
#pragma mark - IBAction
- (IBAction)skipBtnPressed:(id)sender {
//    NSUserDefaults *defaults = [NSUserDefaults standardUserDefaults];
//    [defaults setObject:@"YES" forKey:ISFIRSTLOGIN];
//    [defaults synchronize];
//    
//    QSAppDelegate *delegate = (QSAppDelegate *)[UIApplication sharedApplication].delegate;
//    delegate.window.rootViewController = [[QSS01RootViewController alloc] init];
    
    if ([self.delegate respondsToSelector:@selector(dismissWelcomePage:)]) {
        [self.delegate dismissWelcomePage:self];
    }
}

@end
