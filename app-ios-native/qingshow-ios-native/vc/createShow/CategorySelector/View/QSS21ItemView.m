//
//  QSS21ItemView.m
//  qingshow-ios-native
//
//  Created by 刘少毅 on 15/6/29.
//  Copyright (c) 2015年 QS. All rights reserved.
//

#import "QSS21ItemView.h"
#import "UIImageView+MKNetworkKitAdditions.h"
#import "QSCategoryUtil.h"

@implementation QSS21ItemView

- (void)setSubViewsValueWith:(NSDictionary *)selectedDic
{
    NSString *itemName =[QSCategoryUtil getName:self.itemDic];
    self.titleLabel.text = itemName;
    
    BOOL enable = [QSCategoryUtil getMatchEnabled:self.itemDic];
    NSString *imgUrl = [[QSCategoryUtil getIconUrl:self.itemDic] absoluteString];
    NSRange range = NSMakeRange(0, imgUrl.length-1);
    if ([imgUrl hasSuffix:@".png"]) {
        range = [imgUrl rangeOfString:@".png"];
    }
    NSString *rangeStr = [imgUrl substringToIndex:range.location];
    NSString *imgSelectedUrl = [NSString stringWithFormat:@"%@_selected.png",rangeStr];
    NSString *imgUnSelectedUrl = [NSString stringWithFormat:@"%@_normal.png",rangeStr];
    NSString *imgDisableUrl = [NSString stringWithFormat:@"%@_disabled.png",rangeStr];
    if (self.itemDic == selectedDic) {
        [self.imgView setImageFromURL:[NSURL URLWithString:imgSelectedUrl] placeHolderImage:[UIImage imageNamed:@"selectedHoderImg"]];
    }else if(!enable)
    {
        self.userInteractionEnabled = NO;
        [self.imgView setImageFromURL:[NSURL URLWithString:imgDisableUrl] placeHolderImage:[UIImage imageNamed:@"hoderImg"]];
    }
    else  {
        //缓存selectedimage
        UIImageView *cashView = [[UIImageView alloc] init];
        [cashView setImageFromURL:[NSURL URLWithString:imgSelectedUrl]];
        
        [self.imgView setImageFromURL:[NSURL URLWithString:imgUnSelectedUrl] placeHolderImage:[UIImage imageNamed:@"hoderImg"]];
    }
}
- (void)setLastCellItem:(NSArray *)array
{
    NSString *itemName = [QSCategoryUtil getName:self.itemDic];
    self.titleLabel.text = itemName;
    BOOL enable = [QSCategoryUtil getMatchEnabled:self.itemDic];
    NSString *imgUrl = [[QSCategoryUtil getIconUrl:self.itemDic] absoluteString];
    NSRange range = NSMakeRange(0, imgUrl.length-1);
    if ([imgUrl hasSuffix:@".png"]) {
        range = [imgUrl rangeOfString:@".png"];
    }
    NSString *rangeStr = [imgUrl substringToIndex:range.location];
    NSString *imgSelectedUrl = [NSString stringWithFormat:@"%@_selected.png",rangeStr];
    NSString *imgUnSelectedUrl = [NSString stringWithFormat:@"%@_normal.png",rangeStr];
    NSString *imgDisableUrl = [NSString stringWithFormat:@"%@_disabled.png",rangeStr];

    if ([array containsObject:self.itemDic]) {
        [self.imgView setImageFromURL:[NSURL URLWithString:imgSelectedUrl] placeHolderImage:[UIImage imageNamed:@"selectedHoderImg"]];
    }else if(!enable)
    {
        self.userInteractionEnabled = NO;
        [self.imgView setImageFromURL:[NSURL URLWithString:imgDisableUrl] placeHolderImage:[UIImage imageNamed:@"hoderImg"]];
    }
    else  {
        //缓存selectedimage
        UIImageView *cashView = [[UIImageView alloc] init];
        [cashView setImageFromURL:[NSURL URLWithString:imgSelectedUrl]];
        
        [self.imgView setImageFromURL:[NSURL URLWithString:imgUnSelectedUrl] placeHolderImage:[UIImage imageNamed:@"hoderImg"]];
    }

}
@end
