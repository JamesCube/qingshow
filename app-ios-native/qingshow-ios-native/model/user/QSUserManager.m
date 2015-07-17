//
//  QSUserManager.m
//  qingshow-ios-native
//
//  Created by wxy325 on 11/17/14.
//  Copyright (c) 2014 QS. All rights reserved.
//

#import "QSUserManager.h"

#define kLastClickMenuDateKey @"kLastClickMenuDateKey"
#define kGlobalFirstLaunchShowDueDate @"kGlobalFirstLaunchShowDueDate"
#define kGlobalFirstLaunchShowTitle @"kGlobalFirstLaunchShowTitle"
@interface QSUserManager ()

@property (strong, nonatomic) NSUserDefaults* userDefault;

@end

@implementation QSUserManager

@synthesize lastClickMenuDate = _lastClickMenuDate;
@synthesize globalFirstLaunchShowDueDate = _globalFirstLaunchShowDueDate;
@synthesize globalFirstLaunchTitle = _globalFirstLaunchTitle;

- (instancetype)init {
    self = [super init];
    if (self) {
        self.userDefault = [NSUserDefaults standardUserDefaults];
    }
    return self;
}

+ (QSUserManager*)shareUserManager
{
    static QSUserManager* s_userManager = nil;
    static dispatch_once_t onceToken;
    dispatch_once(&onceToken, ^{
        s_userManager = [[QSUserManager alloc] init];
        s_userManager.fIsLogined = YES;
    });
    return s_userManager;
}

- (NSDate*)lastClickMenuDate
{
    if (!_lastClickMenuDate) {
        double d = [self.userDefault doubleForKey:kLastClickMenuDateKey];
        
        _lastClickMenuDate = [[NSDate alloc] initWithTimeIntervalSince1970:d];;
    }
    return _lastClickMenuDate;
}
- (void)setLastClickMenuDate:(NSDate *)lastClickMenuDate
{
    
    _lastClickMenuDate = lastClickMenuDate;
    [self.userDefault setDouble:[lastClickMenuDate timeIntervalSince1970] forKey:kLastClickMenuDateKey];
    [self.userDefault synchronize];
}

- (NSDate*)globalFirstLaunchShowDueDate {
    if (!_globalFirstLaunchShowDueDate) {
        double d = [self.userDefault doubleForKey:kGlobalFirstLaunchShowDueDate];
        
        _globalFirstLaunchShowDueDate = [[NSDate alloc] initWithTimeIntervalSince1970:d];;
    }
    return _globalFirstLaunchShowDueDate;
}

- (void)setGlobalFirstLaunchShowDueDate:(NSDate *)globalFirstLaunchShowDueDate
{
    _globalFirstLaunchShowDueDate = globalFirstLaunchShowDueDate;
    [self.userDefault setDouble:[globalFirstLaunchShowDueDate timeIntervalSince1970] forKey:kGlobalFirstLaunchShowDueDate];
    [self.userDefault synchronize];
}

- (void)setGlobalFirstLaunchTitle:(NSString *)globalFirstLaunchTitle {
    _globalFirstLaunchTitle = globalFirstLaunchTitle;
    [self.userDefault setValue:globalFirstLaunchTitle forKey:kGlobalFirstLaunchShowTitle];
    [self.userDefault synchronize];
}

- (NSString*)globalFirstLaunchTitle{
    if (!_globalFirstLaunchTitle) {
        _globalFirstLaunchTitle = [self.userDefault valueForKey:kGlobalFirstLaunchShowTitle];
    }
    return _globalFirstLaunchTitle;
}
@end
