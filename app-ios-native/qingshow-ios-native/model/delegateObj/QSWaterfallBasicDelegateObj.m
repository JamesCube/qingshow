//
//  QSWaterfallBasicDelegateObj.m
//  qingshow-ios-native
//
//  Created by wxy325 on 11/12/14.
//  Copyright (c) 2014 QS. All rights reserved.
//

#import "QSWaterfallBasicDelegateObj.h"
#import "MKNetworkOperation.h"
#import "QSMetadataUtil.h"
#import "NSNumber+QSExtension.h"
#import "QSAbstractScrollDelegateObj+Protect.h"

@interface QSWaterfallBasicDelegateObj ()

@end


@implementation QSWaterfallBasicDelegateObj

#pragma mark - Method To Be Override
- (void)registerCell
{
    return;
}

-(UICollectionViewCell *)collectionView:(UICollectionView *)collectionViews cellForItemAtIndexPath:(NSIndexPath *)indexPath
{
    return nil;
}

#pragma mark - Init

- (CGSize)collectionView:(UICollectionView *)collectionView layout:(UICollectionViewLayout*)collectionViewLayout sizeForItemAtIndexPath:(NSIndexPath *)indexPath
{
    return CGSizeZero;
}

- (void)dealloc
{
    self.view.dataSource = nil;
    self.view.delegate = nil;
}

#pragma mark - Config
- (void)refreshClickedData
{}

- (void)bindWithCollectionView:(UICollectionView *)collectionView
{
    _view = collectionView;
    self.view.dataSource = self;
    self.view.delegate = self;
    collectionView.alwaysBounceVertical = YES;
    collectionView.showsVerticalScrollIndicator = NO;
    
    QSWaterFallCollectionViewLayout* layout = [[QSWaterFallCollectionViewLayout alloc] init];
    self.view.collectionViewLayout = layout;
    self.view.translatesAutoresizingMaskIntoConstraints = NO;
    
    self.view.scrollEnabled=YES;
    self.view.backgroundColor=[UIColor colorWithRed:242.f/255.f green:242.f/255.f blue:242.f/255.f alpha:1.f];

    [self registerCell];
    
    UIRefreshControl* refreshControl = [[UIRefreshControl alloc] init];
    [refreshControl addTarget:self action:@selector(didPullRefreshControl:) forControlEvents:UIControlEventValueChanged];
    [collectionView addSubview:refreshControl];
}


#pragma mark - Network
- (MKNetworkOperation*)fetchDataOfPage:(int)page completion:(VoidBlock)block
{
    NSUInteger preCount = 0;
    if (page != 1) {
        preCount = self.resultArray.count;
    }
    
    return [self fetchDataOfPage:page viewRefreshBlock:^{
        if (page == 1) {
            [self.view reloadData];
        } else {
            NSMutableArray* indexPaths = [@[] mutableCopy];
            for (NSUInteger i = preCount; i < self.resultArray.count; i++){
                [indexPaths addObject:[NSIndexPath indexPathForItem:i inSection:0]];
            }
            [self.view insertItemsAtIndexPaths:indexPaths];
        }
    } completion:block];
}

#pragma mark - UICollecitonView Datasource And Delegate

//margin
-(UIEdgeInsets)collectionView:(UICollectionView *)collectionView layout:(UICollectionViewLayout *)collectionViewLayout insetForSectionAtIndex:(NSInteger)section
{
    return UIEdgeInsetsMake(10, 10, 10, 10);
}

-(void)collectionView:(UICollectionView *)collectionView didSelectItemAtIndexPath:(NSIndexPath *)indexPath
{
    [collectionView deselectItemAtIndexPath:indexPath animated:YES];
}


-(NSInteger)collectionView:(UICollectionView *)collectionView numberOfItemsInSection:(NSInteger)section
{
    return self.resultArray.count;
}



#pragma mark - Refresh Control
- (void)didPullRefreshControl:(UIRefreshControl*)refreshControl
{
    [self fetchDataOfPage:1 completion:^{
        [refreshControl endRefreshing];
    }];
}


- (NSString*)getTotalCountDesc
{
    if (!self.metadataDict) {
        return @"0";
    }
    long long filterCount = 0;
    for (NSDictionary* dict in self.resultArray) {
        if (self.filterBlock) {
            if (self.filterBlock(dict)){
                filterCount++;
            }
        }
    }
    long long t = [QSMetadataUtil getNumberTotal:self.metadataDict] - filterCount;
    t = t >= 0ll? t : 0ll;
    return @(t).kmbtStringValue;
}

@end

