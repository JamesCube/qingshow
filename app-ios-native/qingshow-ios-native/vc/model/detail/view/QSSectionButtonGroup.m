//
//  QSSectionButtonGroup.m
//  qingshow-ios-native
//
//  Created by wxy325 on 11/5/14.
//  Copyright (c) 2014 QS. All rights reserved.
//

#import "QSSectionButtonGroup.h"

@interface QSSectionButtonGroup ()
- (void)configView;

@end

@implementation QSSectionButtonGroup

- (id)init
{
    self = [self initWithFrame:CGRectMake(0, 0, 320, 45)];
    if (self) {
        [self configView];
        [self updateLayout];
    }
    return self;
}
#pragma mark - Layout
- (void)configView
{
    self.buttonGroup = @[
                         [QSSectionNumberTextButton generateView],
                         [QSSectionNumberTextButton generateView],
                         [QSSectionNumberTextButton generateView]
                         ];
    self.singleButton = [QSSectionImageTextButton generateView];
    for (QSSectionButtonBase* btn in self.buttonGroup){
        UITapGestureRecognizer* ges = [[UITapGestureRecognizer alloc] initWithTarget:self action:@selector(groupButtonPressed:)];
        [btn addGestureRecognizer:ges];
        [self addSubview:btn];
    }
    
    UITapGestureRecognizer* ges = [[UITapGestureRecognizer alloc] initWithTarget:self action:@selector(singleButtonPressed:)];
    [self.singleButton addGestureRecognizer:ges];
    [self addSubview:self.singleButton];
}
- (void)layoutSubviews
{
    [super layoutSubviews];
    [self updateLayout];
}
- (void)updateLayout
{
    CGSize size = self.frame.size;
    float width = size.width / (self.buttonGroup.count + 1);
    float height = size.height;
    for (int i = 0; i < self.buttonGroup.count; i++) {
        QSSectionButtonBase* btn = self.buttonGroup[i];
        btn.frame = CGRectMake(i * width, 0, width - 1, height);
    }
    self.singleButton.frame = CGRectMake(self.buttonGroup.count * width, 0, width, height);
}
#pragma mark - Gesture
- (void)groupButtonPressed:(UIGestureRecognizer*)ges
{
    int i = 0;
    for (QSSectionButtonBase* btn in self.buttonGroup)
    {
        btn.selected = ges.view == btn;
        if (btn.selected && [self.delegate respondsToSelector:@selector(groupButtonPressed:)]) {
            [self.delegate groupButtonPressed:i];
        }
        ++i;
    }
}

- (void)singleButtonPressed:(UIGestureRecognizer*)ges
{
    if ([self.delegate respondsToSelector:@selector(singleButtonPressed)])
    {
        [self.delegate singleButtonPressed];
    }
}
#pragma mark - Config Text
- (void)setTitle:(NSString*)title atIndex:(int)index
{
    if (index < self.buttonGroup.count) {
        QSSectionButtonBase* btn = self.buttonGroup[index];
        btn.textLabel.text = title;
    }
    else
    {
        self.singleButton.textLabel.text = title;
    }
}

- (void)setNumber:(NSString*)numberStr atIndex:(int)index
{
    QSSectionNumberTextButton* btn = self.buttonGroup[index];
    btn.numberLabel.text = numberStr;
}
- (void)setSelect:(int)index
{
    for (int i = 0; i < self.buttonGroup.count; i++) {
        QSSectionButtonBase* btn = self.buttonGroup[i];
        btn.selected = i == index;
    }
}
@end