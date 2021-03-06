//
//  QSBonusTableViewProvider.m
//  qingshow-ios-native
//
//  Created by wxy325 on 15/12/5.
//  Copyright © 2015年 QS. All rights reserved.
//

#import "QSBonusTableViewProvider.h"
#import "QSU15BonusListTableViewCell.h"

@implementation QSBonusTableViewProvider

@dynamic delegate;

- (void)registerCell
{
    [self.view registerNib:[UINib nibWithNibName:@"QSU15BonusListTableViewCell" bundle:nil] forCellReuseIdentifier:@"QSU15BonusListTableViewCell"];
}

- (CGFloat)tableView:(UITableView *)tableView heightForRowAtIndexPath:(NSIndexPath *)indexPath
{
    CGFloat rate = [UIScreen mainScreen].bounds.size.width / 320;
    return 60.f * rate;
}

- (UITableViewCell *)tableView:(UITableView *)tableView cellForRowAtIndexPath:(NSIndexPath *)indexPath {
    QSU15BonusListTableViewCell *cell = [tableView dequeueReusableCellWithIdentifier:@"QSU15BonusListTableViewCell"];
    [cell bindWithDict:self.resultArray[indexPath.row]];
    CGFloat rate = [UIScreen mainScreen].bounds.size.width / 320;
    cell.transform = CGAffineTransformMakeScale(rate, rate);
    return cell;
}
- (void)tableView:(UITableView *)tableView didSelectRowAtIndexPath:(NSIndexPath *)indexPath
{
    [tableView deselectRowAtIndexPath:indexPath animated:YES];
    NSDictionary *dict = self.resultArray[indexPath.row];
    if ([self.delegate respondsToSelector:@selector(provider:didTapBonus:)]) {
        [self.delegate provider:self didTapBonus:dict];
    }
}


@end
