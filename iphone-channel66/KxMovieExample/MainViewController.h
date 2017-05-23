//
//  MainViewController.h
//  kxmovie
//
//  Created by Kolyvan on 18.10.12.
//  Copyright (c) 2012 Konstantin Boukreev . All rights reserved.
//
//  https://github.com/kolyvan/kxmovie
//  this file is part of KxMovie
//  KxMovie is licenced under the LGPL v3, see lgpl-3.0.txt

#import <UIKit/UIKit.h>
#import "GAITrackedViewController.h"

// for background audio playback &| apple's internal player:
#import <AVFoundation/AVFoundation.h>
#import <AudioToolbox/AudioToolbox.h>
#import <MediaPlayer/MediaPlayer.h>

@interface MainViewController : GAITrackedViewController<UITableViewDataSource, UITableViewDelegate,UIAlertViewDelegate> {
    NSMutableDictionary *streamNames;
     NSMutableDictionary *svivaTovastreamNames;
    NSMutableDictionary  *joinedStreamNames;

    // For Apple's audio player:
	MPMoviePlayerController *mp;
	MPMoviePlayerViewController *mpVC;
    
    NSMutableArray *dataSource;
}
@property (nonatomic, strong) MPMoviePlayerController *mp;
@property (nonatomic, strong) MPMoviePlayerViewController *mpVC;

@property(nonatomic,strong) NSMutableDictionary *streamNames;
@property(nonatomic,strong) NSMutableDictionary *svivaTovastreamNames;
@property(nonatomic,strong) NSMutableDictionary *joinedStreamNames;

-(void) startRadio;
@end
