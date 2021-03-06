//
//  QSHookHelper.m
//  qingshow-ios-native
//
//  Created by wxy325 on 15/9/12.
//  Copyright (c) 2015年 QS. All rights reserved.
//

#import "QSHookHelper.h"
#import <Foundation/Foundation.h>
#import <objc/runtime.h>
#import "QSHookDictionary.h"
#import "QSHookArray.h"
#import "Aspects.h"
@interface QSHookHelper()

+ (void)hookDictionary;

@end

@implementation QSHookHelper

+ (void)registerHooker {
    [self hookDictionary];
    [self hookArray];
}

+ (void)hookDictionary {
    //switch method of NSDictionary to filter nil when init
    Method method1 = class_getClassMethod([NSDictionary class], @selector(dictionaryWithObjects:forKeys:count:));
    Method method2 = class_getClassMethod([NSDictionary class], @selector(hookDictionaryWithObjects:forKeys:count:));
    method_exchangeImplementations(method1, method2);
    
    method1 = class_getInstanceMethod([NSDictionary class], @selector(initWithObjects:forKeys:count:));
    method2 = class_getInstanceMethod([NSDictionary class], @selector(initHookWithObjects:forKeys:count:));
    method_exchangeImplementations(method1, method2);
}

+ (void)hookArray {
    //switch method of NSArray to filter nil when init
    Method method1 = class_getClassMethod([NSArray class], @selector(arrayWithObjects:count:));
    Method method2 = class_getClassMethod([NSArray class], @selector(hookArrayWithObjects:count:));
    method_exchangeImplementations(method1, method2);
    
    method1 = class_getInstanceMethod([NSArray class], @selector(initWithObjects:count:));
    method2 = class_getInstanceMethod([NSArray class], @selector(initHookWithObjects:count:));
    method_exchangeImplementations(method1, method2);
}

@end
