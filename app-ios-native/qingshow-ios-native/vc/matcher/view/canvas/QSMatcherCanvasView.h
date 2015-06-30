//
//  QSMatcherCanvasView.h
//  qingshow-ios-native
//
//  Created by wxy325 on 6/24/15.
//  Copyright (c) 2015 QS. All rights reserved.
//

#import <UIKit/UIKit.h>


@class QSMatcherCanvasView;

@protocol QSMatcherCanvasViewDelegate <NSObject>

- (void)canvasView:(QSMatcherCanvasView*)view didTapCategory:(NSDictionary*)categoryDict;
- (void)canvasView:(QSMatcherCanvasView*)view didRemoveCategory:(NSDictionary*)categoryDict;

@end

@interface QSMatcherCanvasView : UIView <UIGestureRecognizerDelegate>

+ (instancetype)generateView;
- (void)bindWithCategory:(NSArray*)categoryArray;
- (void)setItem:(NSDictionary*)itemDict forCategory:(NSDictionary*)category;
- (void)setItem:(NSDictionary *)itemDict forCategoryId:(NSString *)categoryId;

- (UIImage*)submitView;


@property (weak, nonatomic) NSObject<QSMatcherCanvasViewDelegate>* delegate;

@property (assign, nonatomic) int maxRow;
@property (assign, nonatomic) int maxColumn;
@end
