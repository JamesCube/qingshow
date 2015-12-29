//
//  QSMatcherTableViewProvider.m
//  qingshow-ios-native
//
//  Created by wxy325 on 15/11/20.
//  Copyright (c) 2015年 QS. All rights reserved.
//

#import "QSMatcherTableViewProvider.h"
#import "QSMatcherTableViewCell.h"
#import "QSMatchStickyTableViewCell.h"
#import "NSDictionary+QSExtension.h"
#import "QSDateUtil.h"
#define w ([UIScreen mainScreen].bounds.size.width)
#define h ([UIScreen mainScreen].bounds.size.height)

@interface QSMatcherTableViewProvider() <QSMatcherTableViewCellDelegate>

@end

@implementation QSMatcherTableViewProvider
@dynamic delegate;

- (void)registerCell
{
    [self.view registerNib:[UINib nibWithNibName:@"QSMatcherTableViewCell" bundle:nil] forCellReuseIdentifier:QSMatcherTableViewCellId];
    [self.view registerNib:[UINib nibWithNibName:@"QSMatchStickyTableViewCell" bundle:nil] forCellReuseIdentifier:QSMatchStickyTableViewCellIdentifier];
}

#pragma mark - Table View
- (UITableViewCell *)tableView:(UITableView *)tableView cellForRowAtIndexPath:(NSIndexPath *)indexPath
{
    NSDictionary* dict = self.resultArray[indexPath.row];
    NSArray* topOwners = [dict arrayValueForKeyPath:@"data.topOwners"];
    if (topOwners.count) {
        QSMatcherTableViewCell* cell = [tableView dequeueReusableCellWithIdentifier:QSMatcherTableViewCellId forIndexPath:indexPath];
        
        [cell bindWithDict:dict];
        cell.delegate = self;
        cell.contentView.transform = CGAffineTransformMakeScale(w / 320, w / 320);
        return cell;
    } else {
        QSMatchStickyTableViewCell* cell = [tableView dequeueReusableCellWithIdentifier:QSMatchStickyTableViewCellIdentifier forIndexPath:indexPath];
        [cell bindWithDict:dict];
        cell.contentView.transform = CGAffineTransformMakeScale(w / 320, w / 320);
        return cell;
    }
}
- (CGFloat)tableView:(UITableView *)tableView heightForRowAtIndexPath:(NSIndexPath *)indexPath
{
    NSDictionary* dict = self.resultArray[indexPath.row];

    NSString* stickyUrl = [dict stringValueForKeyPath:@"data.stickyShow.stickyCover"];
    NSArray* topOwners = [dict arrayValueForKeyPath:@"data.topOwners"];
    if (topOwners.count) {
        if (stickyUrl) {
            return (QSMatcherTableViewCellHeight + QSMatcherTableViewCellStickyImageHeight) * w / 320;
        } else {
            return QSMatcherTableViewCellHeight * w / 320;
        }
    } else {
        return QSMatchStickyTableViewCellHeight * w / 320;
    }
}

- (void)tableView:(UITableView *)tableView didSelectRowAtIndexPath:(NSIndexPath *)indexPath
{
    NSDictionary* dict = self.resultArray[indexPath.row];
    NSArray* topOwners = [dict arrayValueForKeyPath:@"data.topOwners"];
    NSDictionary* stickyShow = [dict dictValueForKeyPath:@"data.stickyShow"];
    if (topOwners.count) {
        NSNumber* hour = [dict numberValueForKeyPath:@"hour"];
        NSDate* date = [dict dateValueForKeyPath:@"date"];
        NSString* dateStr = [QSDateUtil buildDateStringFromDate:date];
        if (!dateStr) {
            return;
        }
        
        NSDate* retDate = [date dateByAddingTimeInterval:hour.intValue * 60 * 60];
        if ([self.delegate respondsToSelector:@selector(provider:didClickDate:)]) {
            [self.delegate provider:self didClickDate:retDate];
        }
    } else {
        if ([self.delegate respondsToSelector:@selector(provider:didClickShow:)]) {
            [self.delegate provider:self didClickShow:stickyShow];
        }
    }
}

- (void)cell:(QSMatcherTableViewCell*)cell didClickUser:(NSDictionary*)dict {
    if ([self.delegate respondsToSelector:@selector(provider:didClickPeople:)]) {
        [self.delegate provider:self didClickPeople:dict];
    }
}
- (void)cell:(QSMatcherTableViewCell*)cell didClickStickyShow:(NSDictionary*)dict {
    if ([self.delegate respondsToSelector:@selector(provider:didClickShow:)]) {
        [self.delegate provider:self didClickShow:dict];
    }
}
@end
