//
//  QSMatchCollectionViewProvider.h
//  qingshow-ios-native
//
//  Created by mhy on 15/6/18.
//  Copyright (c) 2015年 QS. All rights reserved.
//

#import "QSWaterfallBasicProvider.h"
#import "QSMatchShowsCell.h"
#import "QSU01MatchCollectionViewCell.h"
@protocol QSMatchCollectionViewProviderDelegate <QSAbstractScrollProviderDelegate>

- (void)didSelectedCellInCollectionView:(id)sender;
- (void)didClickHeaderImgView:(id)sender;

@end
typedef enum : NSUInteger {
    U01Type = 1,
    S01Type,
} MatchCellProviderType;

@interface QSMatchCollectionViewProvider : QSWaterfallBasicProvider<QSMatchShowCellDelegate>

@property(nonatomic,assign)NSObject<QSMatchCollectionViewProviderDelegate>* delegate;
@property(nonatomic,assign)NSInteger type;

@end