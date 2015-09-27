//
//  QSU17ResetPswStep1ViewController.m
//  qingshow-ios-native
//
//  Created by mhy on 15/9/27.
//  Copyright (c) 2015年 QS. All rights reserved.
//

#import "QSU17ResetPswStep1ViewController.h"
#import "QSNetworkKit.h"
#import "UIViewController+ShowHud.h"
#import "QSU18ResetPswStep2ViewController.h"
@interface QSU17ResetPswStep1ViewController ()
@property (weak, nonatomic) IBOutlet UIButton *nextStepBtn;
@property (weak, nonatomic) IBOutlet UIButton *getCodeBtn;
@property (weak, nonatomic) IBOutlet UITextField *phoneTextField;
@property (weak, nonatomic) IBOutlet UITextField *codeTextField;


@property (strong,nonatomic)NSTimer *timer;
@end

@implementation QSU17ResetPswStep1ViewController

- (void)viewDidLoad {
    [super viewDidLoad];
    // Do any additional setup after loading the view from its nib.
    [self configUI];
}
- (void)configUI
{
    //self.view.backgroundColor = [UIColor colorWithRed:0.949 green:0.588 blue:0.643 alpha:1.000];
    self.nextStepBtn.layer.cornerRadius = self.nextStepBtn.bounds.size.width / 8;
    self.getCodeBtn.layer.cornerRadius  =self.getCodeBtn.bounds.size.width / 8;

}
- (IBAction)getCodeBtnPressed:(id)sender {
    NSString *phoneStr = self.phoneTextField.text;
    if (phoneStr.length == 11) {
        [SHARE_NW_ENGINE getTestNumberWithMobileNumber:phoneStr onSucceed:^{
            [self showTextHud:@"已成功发送验证码"];
            [self setTimer];
        } onError:^(NSError *error) {
            if (error.code == 1031) {
                [self showTextHud:@"已超过每日发送次数"];
            }else{
                [self showTextHud:@"手机号码不正确或已被注册"];
            }
        }];

    }else{
        [self showTextHud:@"请正确填写手机号码"];
    }
    
}

- (void)setTimer
{
    _timer = [NSTimer scheduledTimerWithTimeInterval:1 target:self selector:@selector(timerRun) userInfo:nil repeats:YES];
    [_timer fire];
    
    self.getCodeBtn.userInteractionEnabled = NO;
}
- (void)timerRun
{
    static int num = 60;
    [self.getCodeBtn setTitle:[NSString stringWithFormat:@"%d秒后可重发",num] forState:UIControlStateNormal];
    num -= 1;
    if (num < 1) {
        [_timer invalidate];
        _timer = nil;
        num = 60;
        [self.getCodeBtn setTitle:@"获取验证码" forState:UIControlStateNormal];
        self.getCodeBtn.userInteractionEnabled = YES;
    }
    
}
- (IBAction)nextStepBtnPressed:(id)sender {
    NSString *PhoneStr = self.phoneTextField.text;
    NSString *codeStr = self.codeTextField.text;
    __weak QSU17ResetPswStep1ViewController *weakSelf = self;
    [SHARE_NW_ENGINE resetPassWord:PhoneStr coed:codeStr onSucceed:^{
        QSU18ResetPswStep2ViewController *vc = [[QSU18ResetPswStep2ViewController alloc]init];
        vc.mobile = PhoneStr;
        [self.navigationController pushViewController:vc animated:YES];
    } onError:^(NSError *error) {
        [weakSelf showTextHud:@"验证码或者手机号不正确"];
    }];
}

- (IBAction)backBtnPressed:(id)sender {
    [self.navigationController popToRootViewControllerAnimated:YES];
}

- (void)didReceiveMemoryWarning {
    [super didReceiveMemoryWarning];
    // Dispose of any resources that can be recreated.
}

/*
#pragma mark - Navigation

// In a storyboard-based application, you will often want to do a little preparation before navigation
- (void)prepareForSegue:(UIStoryboardSegue *)segue sender:(id)sender {
    // Get the new view controller using [segue destinationViewController].
    // Pass the selected object to the new view controller.
}
*/

@end