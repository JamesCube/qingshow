//
//  QSS20MatcherViewController.m
//  qingshow-ios-native
//
//  Created by wxy325 on 6/21/15.
//  Copyright (c) 2015 QS. All rights reserved.
//

#import "QSS20MatcherViewController.h"
#import "QSS21CategorySelectorVC.h"
#import "QSS23MatcherPreviewViewController.h"

#import "QSAbstractRootViewController.h"
#import "QSNetworkKit.h"
#import "QSCommonUtil.h"
#import "QSCategoryUtil.h"
#import "UIViewController+QSExtension.h"
#import "UIViewController+ShowHud.h"
#import "UIView+ScreenShot.h"
#import "QSS03ShowDetailViewController.h"

#import "NSArray+QSExtension.h"

@interface QSS20MatcherViewController ()

@property (strong, nonatomic) QSMatcherItemSelectionView* itemSelectionView;
@property (strong, nonatomic) QSMatcherCanvasView* canvasView;

@property (strong, nonatomic) NSMutableDictionary* cateIdToProvider;

@property (strong, nonatomic) NSString* selectedCateId;
@property (strong, nonatomic) NSArray* allCategories;
@end

@implementation QSS20MatcherViewController

#pragma mark - Init
- (instancetype)init {
    self = [super initWithNibName:@"QSS20MatcherViewController" bundle:nil];
    if (self) {
        
    }
    return self;
}

#pragma mark - Life Cycle
- (void)viewDidLoad {
    [super viewDidLoad];
    
    self.submitButton.layer.cornerRadius = 4.f;
    self.submitButton.layer.masksToBounds = YES;
    self.categorySelectionButton.layer.borderColor = [UIColor whiteColor].CGColor;
    self.categorySelectionButton.layer.borderWidth = 1.f;
    self.categorySelectionButton.layer.cornerRadius = 4.f;
    self.categorySelectionButton.layer.masksToBounds = YES;
    // Do any additional setup after loading the view from its nib.
    self.itemSelectionView = [QSMatcherItemSelectionView generateView];
    self.itemSelectionView.frame = self.itemSelectionContainer.bounds;
    [self.itemSelectionContainer addSubview:self.itemSelectionView];
    [self.itemSelectionView reloadData];
    
    
    self.canvasView = [QSMatcherCanvasView generateView];
    self.canvasView.delegate = self;
    self.canvasView.frame = self.canvasContainer.bounds;
    [self.canvasContainer addSubview:self.canvasView];
    
    
    [SHARE_NW_ENGINE matcherQueryCategoriesOnSucceed:^(NSArray *array, NSDictionary *metadata) {
        self.allCategories = array;
        NSMutableArray* selectedCategories = [@[] mutableCopy];
        
        __block int maxRow = -1;
        __block int maxColumn = -1;
        
        DicBlock updateMaxRowAndColumn = ^(NSDictionary* dict){
            NSNumber* n = [QSCategoryUtil getMathchInfoRow:dict];
            if (n && n.intValue > maxRow) {
                maxRow = n.intValue;
            }
            n = [QSCategoryUtil getMatchInfoColumn:dict];
            if (n && n.intValue > maxColumn) {
                maxColumn = n.intValue;
            }
        };
        
        for (NSDictionary* category in array) {
            if ([QSCategoryUtil getDefaultOnCanvas:category]) {
                [selectedCategories addObject:category];
                updateMaxRowAndColumn(category);
                
            }
            NSArray* childrens = [QSCategoryUtil getChildren:category];
            for (NSDictionary* c in childrens) {
                if ([QSCategoryUtil getDefaultOnCanvas:c]) {
                    [selectedCategories addObject:c];
                    updateMaxRowAndColumn(c);
                }
            }
        }
        
        if (maxRow >= 0) {
            self.canvasView.maxRow = maxRow;
        }
        
        if (maxColumn >= 0) {
            self.canvasView.maxColumn = maxColumn;
        }
        
        [self updateCategory:selectedCategories];
    } onError:nil];
    self.cateIdToProvider = [@{} mutableCopy];
}



- (void)viewWillAppear:(BOOL)animated {
    [super viewWillAppear:animated];
    self.navigationController.navigationBarHidden = YES;
}

- (void)didReceiveMemoryWarning {
    [super didReceiveMemoryWarning];
    // Dispose of any resources that can be recreated.
}

#pragma mark - IBAction
- (IBAction)categorySelectedBtnPressed:(id)sender {
    NSArray* categoryArray = [[self.cateIdToProvider allValues] mapUsingBlock:^id(QSMatcherItemsProvider* p) {
        return p.categoryDict;
    }];
    QSS21CategorySelectorVC* vc = [[QSS21CategorySelectorVC alloc] initWithCategories:self.allCategories selectedCategories:categoryArray];
    vc.delegate = self;
    [self.navigationController pushViewController:vc animated:YES];
}

- (IBAction)menuBtnPressed:(id)sender {
    [self.menuProvider didClickMenuBtn];
}

- (IBAction)previewButtonPressed:(id)sender {
    //图片需要显示70%以上才能提交
    if (![self.canvasView checkRate:0.7f]) {
        [self showErrorHudWithText:@"图片被遮挡太多，请调整"];
        return;
    }
    
    NSArray* items = [[self.cateIdToProvider allValues] mapUsingBlock:^id(QSMatcherItemsProvider* p) {
        if (p.selectIndex < p.resultArray.count) {
            return p.resultArray[p.selectIndex];
        } else {
            return [NSNull null];
        }
        
    }];
    items = [items filteredArrayUsingBlock:^BOOL(NSDictionary* itemDict) {
        return ![QSCommonUtil checkIsNil:itemDict];
    }];
    
    UIImage* snapshot = [self.canvasView submitView];
    
    UIViewController* vc = [[QSS23MatcherPreviewViewController alloc] initWithItems:items coverImages:snapshot menuProvider:self.menuProvider];
    [self.navigationController pushViewController:vc animated:YES];
}




- (void)setSelectedCateId:(NSString *)selectedCateId {
    if ([_selectedCateId isEqualToString:selectedCateId]) {
        return;
    }
    _selectedCateId = selectedCateId;
    QSMatcherItemsProvider* provider = self.cateIdToProvider[selectedCateId];
    self.itemSelectionView.datasource = provider;
    self.itemSelectionView.delegate = provider;
    self.itemSelectionView.selectIndex = provider.selectIndex;
    [self.itemSelectionView reloadData];
    [self.itemSelectionView offsetToZero:YES];
}

#pragma mark canvas
- (void)canvasView:(QSMatcherCanvasView*)view didTapCategory:(NSDictionary*)categoryDict {
    self.selectedCateId = [QSCommonUtil getIdOrEmptyStr:categoryDict];
    
}
- (void)canvasView:(QSMatcherCanvasView *)view didRemoveCategory:(NSDictionary *)categoryDict {
    NSString* categoryID = [QSCommonUtil getIdOrEmptyStr:categoryDict];
    [self.cateIdToProvider removeObjectForKey:categoryID];
    if ([self.selectedCateId isEqualToString:categoryID]) {
        self.selectedCateId = nil;
    }
}

#pragma mark - Category Selection
- (void)didSelectCategories:(NSArray*)categoryArray {
    [self updateCategory:categoryArray];
}

- (void)updateCategory:(NSArray*)array {
    //Update View
    [self.canvasView bindWithCategory:array];
    
    NSArray* newCategoryIds = [array mapUsingBlock:^id(NSDictionary* dict) {
        return [QSCommonUtil getIdOrEmptyStr:dict];
    }];
    NSArray* oldCategoryIds = [self.cateIdToProvider allKeys];
    //Remove Old Provider
    [oldCategoryIds enumerateObjectsUsingBlock:^(NSString* oldKey, NSUInteger idx, BOOL *stop) {
        if ([newCategoryIds indexOfObject:oldKey] == NSNotFound) {
            [self.cateIdToProvider removeObjectForKey:oldKey];
        }
    }];
    
    //Add New Provider
    for (NSDictionary* categoryDict in array) {
        __block NSString* cateId = [QSCommonUtil getIdOrEmptyStr:categoryDict];
        if ([oldCategoryIds indexOfObject:cateId] == NSNotFound) {
            QSMatcherItemsProvider* provider = [[QSMatcherItemsProvider alloc] initWithCategory:categoryDict];
            provider.delegate = self;
            self.cateIdToProvider[cateId] = provider;
            [provider reloadData];
        }
    }
    
    //Set Default Selected Provider
    if ([self.cateIdToProvider allKeys].count) {
        self.selectedCateId = [self.cateIdToProvider allKeys][0];
    }
}

#pragma mark - QSMatcherItemsProviderDelegate
- (void)matcherItemProvider:(QSMatcherItemsProvider*)provider ofCategory:(NSDictionary*)categoryDict didSelectItem:(NSDictionary*)itemDict{
    [self.canvasView setItem:itemDict forCategory:categoryDict];
}
- (void)matcherItemProvider:(QSMatcherItemsProvider*)provider didFinishNetworkLoading:(NSDictionary*)categoryDict {
    if ([[QSCommonUtil getIdOrEmptyStr:categoryDict] isEqualToString:self.selectedCateId]) {
        [self.itemSelectionView reloadData];
    }
}
@end
