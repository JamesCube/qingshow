//
//  QSAbstractScrollDelegateObj.m
//  qingshow-ios-native
//
//  Created by wxy325 on 1/25/15.
//  Copyright (c) 2015 QS. All rights reserved.
//

#import "QSAbstractListViewProvider.h"
#import "QSAbstractListViewProvider+Protect.h"
#import "MKNetworkOperation.h"
#import "QSMetadataUtil.h"
#import "NSNumber+QSExtension.h"

@interface QSAbstractListViewProvider ()

@property (strong, nonatomic) MKNetworkOperation* refreshOperation;
@property (strong, nonatomic) MKNetworkOperation* loadMoreOperation;
@property (assign, nonatomic) BOOL fIsAll;
@property (strong, nonatomic) UIRefreshControl* refreshControl;
@end

@implementation QSAbstractListViewProvider

- (id)init
{
    self = [super init];
    if (self) {
        self.resultArray = [@[] mutableCopy];
        
        self.fIsAll = NO;
        self.loadMoreOperation = nil;
        self.refreshOperation = nil;
        self.currentPage = 1;
        self.hasRefreshControl = YES;
    }
    return self;
}

#pragma mark - Network
- (MKNetworkOperation*)fetchDataOfPage:(int)page completion:(VoidBlock)block
{
    return [self fetchDataOfPage:page viewRefreshBlock:nil completion:block];
}

- (void)reloadData
{
    [self fetchDataOfPage:1];
}

- (MKNetworkOperation*)fetchDataOfPage:(int)page
{
    return [self fetchDataOfPage:page completion:nil];
}

- (MKNetworkOperation*)fetchDataOfPage:(int)page
                      viewRefreshBlock:(VoidBlock)refreshBlock
                            completion:(VoidBlock)block
{
    MKNetworkOperation* op = self.networkBlock(^(NSArray *showArray, NSDictionary *metadata) {
        self.metadataDict = metadata;
        if (page == 1) {
            [self.resultArray removeAllObjects];
            self.refreshOperation = nil;
            self.currentPage = 1;
        }
        
        [self.resultArray addObjectsFromArray:showArray];
        if (refreshBlock) {
            refreshBlock();
        }
        
        
        if (block) {
            block();
        }
    }, ^(NSError *error) {
        if (error.code == 1009) {
            self.fIsAll = YES;
        }
        if ([self.delegate respondsToSelector:@selector(handleNetworkError:)]) {
            [self.delegate handleNetworkError:error];
        }
        if (block) {
            block();
        }
    }, page);
    
    if (page == 1) {
        [self.loadMoreOperation cancel];
        self.loadMoreOperation = nil;
        self.fIsAll = NO;
        self.refreshOperation = op;
    }
    return op;
}

#pragma mark - Scroll View
- (void)scrollViewDidScroll:(UIScrollView *)scrollView
{
    if ([self.delegate respondsToSelector:@selector(scrollViewDidScroll:)]) {
        [self.delegate scrollViewDidScroll:scrollView];
    }
    
    if (self.fIsAll || self.refreshOperation || self.loadMoreOperation || ! self.resultArray.count) {
        return;
    }
    
    if (scrollView.contentOffset.y + scrollView.frame.size.height >= scrollView.contentSize.height) {
        self.loadMoreOperation = [self fetchDataOfPage:self.currentPage + 1 completion:^{
            self.currentPage++;
            self.loadMoreOperation = nil;
        }];
    }
}

- (void)scrollViewWillBeginDragging:(UIScrollView *)scrollView
{
    if ([self.delegate respondsToSelector:@selector(scrollViewWillBeginDragging:)]) {
        [self.delegate scrollViewWillBeginDragging:scrollView];
    }
}

#pragma mark -
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

#pragma mark - Refresh Control
- (void)addRefreshControl
{
    if (self.hasRefreshControl) {
        if (self.refreshControl) {
            [self.refreshControl removeFromSuperview];
            self.refreshControl = nil;
        }
        self.refreshControl = [[UIRefreshControl alloc] init];
        [self.refreshControl addTarget:self action:@selector(didPullRefreshControl:) forControlEvents:UIControlEventValueChanged];
        [self.view addSubview:self.refreshControl];
    }
}
- (void)didPullRefreshControl:(UIRefreshControl*)refreshControl
{
    [self fetchDataOfPage:1 completion:^{
        [refreshControl endRefreshing];
    }];
}
- (void)refreshWithAnimation
{
    [self.refreshControl beginRefreshing];
    [self.view scrollRectToVisible:CGRectMake(0, -self.refreshControl.frame.size.height, 1, 1) animated:YES];
    [self fetchDataOfPage:1 completion:^{
        [self.refreshControl endRefreshing];
    }];
}
@end
