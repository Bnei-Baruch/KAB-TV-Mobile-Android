//
//  MainViewController.m
//  kxmovie
//
//  Created by Kolyvan on 18.10.12.
//  Copyright (c) 2012 Konstantin Boukreev . All rights reserved.
//
//  https://github.com/kolyvan/kxmovie
//  this file is part of KxMovie
//  KxMovie is licenced under the LGPL v3, see lgpl-3.0.txt

#import "MainViewController.h"
#import "LoginControllerViewController.h"
#import "MenuViewController.h"
#import "Reachability.h"
#import "KxMovieExample-Swift.h"
#import <AVFoundation/AVFoundation.h>
#import "AppAuth.h"

@import GoogleMobileAds;
@import Firebase;

#ifndef AVURLAssetHTTPHeaderFieldsKey
#define AVURLAssetHTTPHeaderFieldsKey @"AVURLAssetHTTPHeaderFieldsKey"
#endif

//#import "AudioWebViewController.h"
#define kCYCAppDelegatePlayNotificationName @"playNotification"
#define kCYCAppDelegatePauseNotificationName @"pauseNotification"
@interface MainViewController () {
    NSArray *_localMovies;
    NSArray *_remoteMovies;
    NSArray *_localMoviesName;
}

@property (strong, nonatomic) UITableView *tableView;
@property(nonatomic, strong) GADBannerView *bannerView;
@property (nonatomic, strong) id timeObserver;
@property (nonatomic, strong) NSTimer *stallTimer;

@end



#pragma mark - Private definitions

@interface MainViewController (Private)
-(void)playFromURL:(NSURL *)URL;
@end


#pragma mark - implementation
@implementation MainViewController

@synthesize streamNames;
@synthesize svivaTovastreamNames;
@synthesize joinedStreamNames;

@synthesize mp;
@synthesize mpVC;



- (id)init
{
    self = [super init];
    if (self) {
        self.title = @"Bnei Baruch Kabbalah";
        
        // current SvivaTova links for testing:
        // http://streams.kab.tv/heb-special-fNgO58zb_medium.asx
        // http://streams.kab.tv/heb-special-fNgO58zb.asx
        // http://streams.kab.tv/rus-special-fNgO58zb_medium.asx
        // http://streams.kab.tv/rus-special-fNgO58zb.asx
        // http://streams.kab.tv/eng-special-fNgO58zb.asx
        // http://streams.kab.tv/eng-special-fNgO58zb_medium.asx
        // http://streams.kab.tv/spa-special-fNgO58zb.asx
        // http://streams.kab.tv/spa-special-fNgO58zb_medium.asx
        
        _remoteMovies = @[
            //            @"http://www.wowza.com/_h264/BigBuckBunny_175k.mov",
            //            // @"http://www.wowza.com/_h264/BigBuckBunny_115k.mov",
            //            @"rtsp://184.72.239.149/vod/mp4:BigBuckBunny_115k.mov",
            //            @"http://santai.tv/vod/test/test_format_1.3gp",
            //            @"http://santai.tv/vod/test/test_format_1.mp4",
            @"http://icecast.kab.tv/heb.mp3",
            @"http://icecast.kab.tv/radiozohar2014.mp3",
            @"http://icecast.kab.tv/newlife",
            @"http://edge1.il.kab.tv/rtplive/tv66-heb-medium.stream/playlist.m3u8",
            @"http://icecast.kab.tv/rus.mp3",
            @"http://edge1.il.kab.tv/rtplive/tv66-rus-medium.stream/playlist.m3u8",
            //@"http://icecast.kab.tv/live1-heb-574bcfd5.mp3"
            
            //@"rtsp://184.72.239.149/vod/mp4://BigBuckBunny_175k.mov",
            //@"http://santai.tv/vod/test/BigBuckBunny_175k.mov",
        ];
        self.streamNames = [[ NSMutableDictionary alloc]
                            init];
        self.joinedStreamNames = [[ NSMutableDictionary alloc]
                                  init];
        
        //        [self.streamNames setObject:@"ערוץ 66 - אודיו" forKey:@"http://icecast.kab.tv/heb.mp3"];
        //        [self.streamNames setObject:@"רדיו ערוץ 66" forKey:@"http://icecast.kab.tv/radiozohar2014.mp3"];
        //
        //        [self.streamNames setObject:@"ערוץ 66 - וידאו" forKey:@"http://edge1.il.kab.tv/rtplive/tv66-heb-medium.stream/playlist.m3u8"];
        //        [self.streamNames setObject:@"Канал 66 - Русском Аудио" forKey:@"http://icecast.kab.tv/rus.mp3"];
        //        [self.streamNames setObject:@"Канал 66 - Русском Видео" forKey:@"http://edge1.il.kab.tv/rtplive/tv66-rus-medium.stream/playlist.m3u8"];
        //
        //
        
        [self.streamNames setObject:@"http://icecast.kab.tv/heb.mp3"  forKey:@"ערוץ קבלה לעם - אודיו"];
        [self.streamNames setObject:@"http://icecast.kab.tv/radiozohar2014.mp3" forKey:@"רדיו קבלה לעם" ];
        
        
        [self.streamNames setObject:@"http://edge1.il.kab.tv/rtplive/tv66-heb-medium.stream/playlist.m3u8" forKey:@"ערוץ קבלה לעם - וידאו" ];
        [self.streamNames setObject:@"http://icecast.kab.tv/rus.mp3" forKey:@"Каббала - Русском Аудио" ];
        [self.streamNames setObject: @"http://edge1.il.kab.tv/rtplive/tv66-rus-medium.stream/playlist.m3u8" forKey:@"Каббала - Русском Видео"];
        
        NSMutableArray *keyChannel66 = [[NSMutableArray alloc]init];
        [keyChannel66 addObject:@"ערוץ קבלה לעם - אודיו"];
        [keyChannel66 addObject:@"ערוץ קבלה לעם - וידאו"];
        [keyChannel66 addObject:@"רדיו קבלה לעם"];
        
        
        [keyChannel66 addObject:@"Каббала - Русском Аудио"];
        [keyChannel66 addObject:@"Каббала - Русском Видео"];
        
        
        
        dataSource = [[NSMutableArray alloc ]initWithCapacity:5];
        
        NSMutableDictionary *sectionChannel66 = [[NSMutableDictionary alloc]init];
        [sectionChannel66 setValue:keyChannel66 forKey:@"קבלה לעם"];
        [dataSource addObject:sectionChannel66];
        
        //[self processSvivaTovaStreams];
        
    }
    
    UIBarButtonItem *anotherButton = [[UIBarButtonItem alloc] initWithTitle:@"menu" style:UIBarButtonItemStylePlain target:self action:@selector(openMenu)];
    self.navigationItem.rightBarButtonItem = anotherButton;
    self.navigationItem.hidesBackButton = YES;
    return self;
}

- (void)loadView
{
    self.view = [[UIView alloc] initWithFrame:[[UIScreen mainScreen] applicationFrame]];
    self.tableView = [[UITableView alloc] initWithFrame:self.view.bounds style:UITableViewStyleGrouped];
    self.tableView.backgroundColor = [UIColor whiteColor];
    //self.tableView.backgroundView = [[UIImageView alloc] initWithImage:image];
    self.tableView.autoresizingMask = UIViewAutoresizingFlexibleWidth | UIViewAutoresizingFlexibleTopMargin | UIViewAutoresizingFlexibleRightMargin | UIViewAutoresizingFlexibleLeftMargin | UIViewAutoresizingFlexibleHeight | UIViewAutoresizingFlexibleBottomMargin;
    self.tableView.delegate = self;
    self.tableView.dataSource = self;
    self.tableView.separatorStyle = UITableViewCellSeparatorStyleSingleLine;
    
    [self.view addSubview:self.tableView];
}

- (void)addBannerViewToView:(UIView *)bannerView {
    bannerView.translatesAutoresizingMaskIntoConstraints = NO;
    [self.view addSubview:bannerView];
    [self.view addConstraints:@[
        [NSLayoutConstraint constraintWithItem:bannerView
                                     attribute:NSLayoutAttributeBottom
                                     relatedBy:NSLayoutRelationEqual
                                        toItem:self.bottomLayoutGuide
                                     attribute:NSLayoutAttributeTop
                                    multiplier:1
                                      constant:0],
        [NSLayoutConstraint constraintWithItem:bannerView
                                     attribute:NSLayoutAttributeCenterX
                                     relatedBy:NSLayoutRelationEqual
                                        toItem:self.view
                                     attribute:NSLayoutAttributeCenterX
                                    multiplier:1
                                      constant:0]
    ]];
}


/// Tells the delegate an ad request loaded an ad.
- (void)adViewDidReceiveAd:(GADBannerView *)adView {
    NSLog(@"adViewDidReceiveAd");
    [self addBannerViewToView:self.bannerView];
    
}

/// Tells the delegate an ad request failed.
- (void)adView:(GADBannerView *)adView
    didFailToReceiveAdWithError:(GADRequest *)error {
    NSLog(@"adView:didFailToReceiveAdWithError: %@", [error description]);
}

/// Tells the delegate that a full-screen view will be presented in response
/// to the user clicking on an ad.
- (void)adViewWillPresentScreen:(GADBannerView *)adView {
    NSLog(@"adViewWillPresentScreen");
}

/// Tells the delegate that the full-screen view will be dismissed.
- (void)adViewWillDismissScreen:(GADBannerView *)adView {
    NSLog(@"adViewWillDismissScreen");
}

/// Tells the delegate that the full-screen view has been dismissed.
- (void)adViewDidDismissScreen:(GADBannerView *)adView {
    NSLog(@"adViewDidDismissScreen");
}

/// Tells the delegate that a user click will open another app (such as
/// the App Store), backgrounding the current app.
- (void)adViewWillLeaveApplication:(GADBannerView *)adView {
    NSLog(@"adViewWillLeaveApplication");
}


- (void)viewDidLoad
{
    [super viewDidLoad];
    //GADMobileAds.sharedInstance.requestConfiguration.testDeviceIdentifiers = @[ kGADSimulatorID ];
    
    self.bannerView = [[GADBannerView alloc]
                       initWithAdSize:kGADAdSizeSmartBannerPortrait];
    
    
    [self addBannerViewToView:self.bannerView];
    
    NSString *language = [[NSLocale preferredLanguages] objectAtIndex:0];
    NSDictionary *languageDic = [NSLocale componentsFromLocaleIdentifier:language];
    NSString *languageCode = [languageDic objectForKey:@"kCFLocaleLanguageCodeKey"];
    
  
    
  
    
    if([languageCode isEqualToString:@"he"])
    {
        self.bannerView.adUnitID = @"ca-app-pub-4525606414173317/2693563718";
    }
    else
    if([languageCode isEqualToString:@"ru"])
       {
           self.bannerView.adUnitID = @"ca-app-pub-4525606414173317/8589173632";
       }
    else
    if([languageCode isEqualToString:@"es"])
       {
           self.bannerView.adUnitID = @"ca-app-pub-4525606414173317/6996604953";
       }
    else{
    
          self.bannerView.adUnitID = @"ca-app-pub-4525606414173317/5369642604";
      }
    
    self.bannerView.rootViewController = self;
    [self.bannerView loadRequest:[GADRequest request]];
    self.bannerView.delegate = self;
    
    
    
    
    //for audio playing in background:
    AVAudioSession *audioSession = [AVAudioSession sharedInstance];
    NSError *setCategoryError = nil;
    [audioSession setCategory:AVAudioSessionCategoryPlayback error:&setCategoryError];
    if (setCategoryError) {
        NSLog(@"setCategoryError");
    }
    
    NSError *activationError = nil;
    [audioSession setActive:YES error:&activationError];
    if (activationError) {
        NSLog(@"activationError");
    }
    
    if ([[UIApplication sharedApplication] respondsToSelector:@selector(beginReceivingRemoteControlEvents) ]) {
        [[UIApplication sharedApplication] beginReceivingRemoteControlEvents];
        [[AVAudioSession sharedInstance] setCategory:AVAudioSessionCategoryPlayback error:nil];
        [[AVAudioSession sharedInstance] setActive: YES error: nil];
    }
    
    if(SYSTEM_VERSION_GREATER_THAN_OR_EQUAL_TO(@"11.0")){
        //NOTE: this is the only way that I find to make this work on IOS 11 its seems to be that togglePlayPauseCommand is not working anymore
        MPRemoteCommandCenter* commandCenter = [MPRemoteCommandCenter sharedCommandCenter];
        [commandCenter.playCommand addTargetWithHandler:^MPRemoteCommandHandlerStatus(MPRemoteCommandEvent * _Nonnull event) {
            [[NSNotificationCenter defaultCenter] postNotificationName:kCYCAppDelegatePlayNotificationName object:nil];
            return MPRemoteCommandHandlerStatusSuccess;
        }];
        
        [commandCenter.pauseCommand addTargetWithHandler:^MPRemoteCommandHandlerStatus(MPRemoteCommandEvent * _Nonnull event) {
            [[NSNotificationCenter defaultCenter] postNotificationName:kCYCAppDelegatePauseNotificationName object:nil];
            return MPRemoteCommandHandlerStatusSuccess;
        }];
        
        [[NSNotificationCenter defaultCenter] addObserverForName:@"RemotePlayCommandNotification" object:nil queue:nil usingBlock:^(NSNotification * _Nonnull note) {
            
            NSLog(@"Clicked the play button.");
        }];
        
        [[NSNotificationCenter defaultCenter] addObserverForName:@"RemotePauseCommandNotification" object:nil queue:nil usingBlock:^(NSNotification * _Nonnull note) {
            
            NSLog(@"Clicked the pause button.");
        }];
    }
    
    
    
}

- (void)didReceiveMemoryWarning
{
    [super didReceiveMemoryWarning];
}

- (BOOL)shouldAutorotateToInterfaceOrientation:(UIInterfaceOrientation)interfaceOrientation
{
    return (interfaceOrientation != UIInterfaceOrientationPortraitUpsideDown);
}

- (void)viewWillAppear:(BOOL)animated
{
    [super viewWillAppear:animated];
    [self processSvivaTovaStreams];
    [self reloadMovies];
    [self.tableView reloadData];
}

- (void) reloadMovies
{
    NSMutableArray *ma = [NSMutableArray array];
    NSFileManager *fm = [[NSFileManager alloc] init];
    NSString *folder = [NSSearchPathForDirectoriesInDomains(NSDocumentDirectory,
                                                            NSUserDomainMask,
                                                            YES) lastObject];
    NSArray *contents = [fm contentsOfDirectoryAtPath:folder error:nil];
    
    for (NSString *filename in contents) {
        
        if (filename.length > 0 &&
            [filename characterAtIndex:0] != '.') {
            
            NSString *path = [folder stringByAppendingPathComponent:filename];
            NSDictionary *attr = [fm attributesOfItemAtPath:path error:nil];
            if (attr) {
                id fileType = [attr valueForKey:NSFileType];
                if ([fileType isEqual: NSFileTypeRegular]) {
                    
                    NSString *ext = path.pathExtension.lowercaseString;
                    
                    if ([ext isEqualToString:@"mp3"] ||
                        [ext isEqualToString:@"caff"]||
                        [ext isEqualToString:@"aiff"]||
                        [ext isEqualToString:@"ogg"] ||
                        [ext isEqualToString:@"wma"] ||
                        [ext isEqualToString:@"m4a"] ||
                        [ext isEqualToString:@"m4v"] ||
                        [ext isEqualToString:@"3gp"] ||
                        [ext isEqualToString:@"mp4"] ||
                        [ext isEqualToString:@"mov"] ||
                        [ext isEqualToString:@"avi"] ||
                        [ext isEqualToString:@"mkv"] ||
                        [ext isEqualToString:@"mpeg"]||
                        [ext isEqualToString:@"mpg"] ||
                        [ext isEqualToString:@"flv"] ||
                        [ext isEqualToString:@"vob"]) {
                        
                        [ma addObject:path];
                    }
                }
            }
        }
    }
    
    //_localMovies = [ma copy];
    NSUserDefaults *defaults = [NSUserDefaults standardUserDefaults];
    if([[defaults objectForKey:@"activated"] isEqualToString:@"yes"])
    {
        //yuval - the user is loged in
        
    }
    else
    {
        _localMovies = [[NSArray alloc] initWithObjects:@"Login", nil];
    }
}

#pragma mark - Table view data source

- (NSInteger)numberOfSectionsInTableView:(UITableView *)tableView
{
    //    NSUserDefaults *userD = [[NSUserDefaults alloc] init];
    //
    //    if (![@"1" isEqualToString:[userD objectForKey:@"isLogin"]])
    //    return 1;
    //    else
    //        return 2;
    
    return [dataSource count];
}

- (NSString *)tableView:(UITableView *)tableView titleForHeaderInSection:(NSInteger)section
{
    //    switch (section) {
    //            //check if login is done then show sviva tovs first
    //        case 0:
    //        {
    //            NSUserDefaults *userD = [[NSUserDefaults alloc] init];
    //
    //            if (![@"1" isEqualToString:[userD objectForKey:@"isLogin"]])
    //                return @"Channel 66";
    //            else
    //                return [@"Sviva Tova" stringByAppendingString:@" Hebrew"];
    //            break;
    //
    ////            case 0:
    ////                break;
    //        }
    //        case 1:
    //                return @"Channel 66";
    //                break;
    //            }
    //        //case 1:     return @"Extra";
    //
    //    return @"";
    
    
    NSString *title =    [[[(NSMutableDictionary*)[dataSource objectAtIndex:section] keyEnumerator]allObjects]objectAtIndex:0];
    return title;
    
}

- (NSInteger)tableView:(UITableView *)tableView numberOfRowsInSection:(NSInteger)section
{
    
    //if app activated then show the sviva tova with selected language on activation
    //    switch (section) {
    //        case 0:     3; //2 video types quality and 1 audio
    //
    //        case 1:     return _remoteMovies.count;;
    //    }
    
    //    NSUserDefaults *userD = [[NSUserDefaults alloc] init];
    //
    //    switch (section) {
    //
    //
    //           case 0:
    //            if (![@"1" isEqualToString:[userD objectForKey:@"isLogin"]])
    //                return _remoteMovies.count;
    //
    //            else
    //                3;
    //            break;
    //
    //
    //        case 1:
    //                if (![@"1" isEqualToString:[userD objectForKey:@"isLogin"]])
    //
    //                return 0;
    //            else
    //                return _remoteMovies.count;
    //            break;
    //    }
    //    return 0;
    
    return [(NSArray*)[[(NSDictionary*)[dataSource objectAtIndex:section] allValues]objectAtIndex:0] count];
}

- (UITableViewCell *)tableView:(UITableView *)tableView cellForRowAtIndexPath:(NSIndexPath *)indexPath
{
    static NSString *cellIdentifier = @"Cell";
    UITableViewCell *cell = [self.tableView dequeueReusableCellWithIdentifier:cellIdentifier];
    if (cell == nil) {
        cell = [[UITableViewCell alloc] initWithStyle:UITableViewCellStyleDefault
                                      reuseIdentifier:cellIdentifier];
        cell.accessoryType = UITableViewCellAccessoryDisclosureIndicator;
    }
    
    //    NSString *path;
    //
    //     NSUserDefaults *userD = [[NSUserDefaults alloc] init];
    //
    //    //if activated then put sviva tova as first menu
    //
    //    if (![@"1" isEqualToString:[userD objectForKey:@"isLogin"]])
    //    {
    //    if (indexPath.section == 0) {
    //
    ////        path = 'get the url from urls saved from language selector'
    ////        cell.textLabel.text = 'get the description from urls saved from language selector';
    ////
    //    }
    //    if (indexPath.section == 1) {
    //
    //        path = _remoteMovies[indexPath.row];
    //        cell.textLabel.text = [self.streamNames objectForKey:path];
    //
    //    }
    //    }
    //    else if (indexPath.section == 0) {
    //
    //        path = _remoteMovies[indexPath.row];
    //        cell.textLabel.text = [self.streamNames objectForKey:path];
    //
    //    }
    
    cell.textLabel.text = [(NSMutableArray*)[[(NSDictionary*)[dataSource objectAtIndex:indexPath.section] allValues] objectAtIndex:0] objectAtIndex:indexPath.row];
    return cell;
}


-(void) startRadio
{
    NSIndexPath *indexPath;
    if([self.tableView numberOfSections]>1)
        indexPath = [NSIndexPath indexPathForRow:1 inSection:1];
    else
        indexPath = [NSIndexPath indexPathForRow:1 inSection:0];
    
    
    
    [self.tableView selectRowAtIndexPath:indexPath
                                animated:YES
                          scrollPosition:UITableViewScrollPositionNone];
    [self tableView:self.tableView didSelectRowAtIndexPath:indexPath];
}

#pragma mark - Table view delegate

- (void)tableView:(UITableView *)tableView didSelectRowAtIndexPath:(NSIndexPath *)indexPath
{
    Reachability *reach = [Reachability reachabilityForInternetConnection];
    NetworkStatus status = [reach currentReachabilityStatus];
    
    if(status == NotReachable)
    {
        [self dismissViewControllerAnimated:YES completion:nil];
        UIAlertView *alert = [[UIAlertView alloc] initWithTitle:@"קבלה לעם"
                                                        message:@"No Internet connection available" delegate:nil
                                              cancelButtonTitle:@"OK" otherButtonTitles:nil];
        [alert show];
        return;
    }
    
    
    NSString *path;
    path = _remoteMovies[indexPath.row];
    NSArray *key = [[[[dataSource objectAtIndex:indexPath.section] allValues]objectAtIndex:0] objectAtIndex:indexPath.row];
    path = [self.joinedStreamNames valueForKey:key];
    NSRange range = [path rangeOfString:@"mp3"];
    Boolean video = [path rangeOfString:@"mp3"].length==0;
    Boolean quality = [[[NSUserDefaults standardUserDefaults] valueForKey:@"quality"] isEqualToString:@"Medium"];
    //if activated then play the url form sviva tova section which is 0
    
    
    
    if (indexPath.section == 0) {
        
        
        if( video && !quality)
            path = [path stringByReplacingOccurrencesOfString:@"medium" withString:@"high"];
        //
        
    }
    NSString *analyticPrm = [NSString stringWithFormat:@"%@ - %@", @"קבלה לעם", path];
    //googleAnalytic
    //  self.screenName = analyticPrm;
    path = [path stringByReplacingOccurrencesOfString:@"http" withString:@"https"];
    [self playURL:[NSURL URLWithString:path]]; // to save memory
}


- (void)textFieldDidEndEditing:(UITextField *)textField
{
    NSLog(@"textFieldDidEndEditing");
}


- (void) processSvivaTovaStreams
{
    if([dataSource count]>1)
        [dataSource removeObjectAtIndex:0];
    [joinedStreamNames removeAllObjects];
    NSUserDefaults *userD = [[NSUserDefaults alloc] init];
    NSDictionary *svivaStreams = [userD objectForKey:@"currentSvivaTovaData"];
    if(svivaStreams !=nil && [[userD objectForKey:@"isLogin"] isEqual:@"1"] )
    {
        
        
        //            if(![[NSUserDefaults standardUserDefaults] boolForKey:@"Registered"])
        //            {
        //                RegisterViewController *reg = [[RegisterViewController alloc]init];
        //
        //                [self.navigationController presentModalViewController:reg animated:YES];
        //
        //            }
        
        
        NSDictionary *regular = [svivaStreams objectForKey:@"regular"];
        NSArray *urls = [regular objectForKey:@"urls"];
        svivaTovastreamNames = [[NSMutableDictionary alloc]init];
        NSMutableArray *sectionSviva = [[NSMutableArray alloc]init];
        for(int i =0;i<urls.count;i++)
        {
            NSDictionary *urlData = [urls objectAtIndex:i];
            [svivaTovastreamNames setObject:[urlData objectForKey:@"url_value"] forKey: [urlData objectForKey:@"url_quality_name"]];
            [sectionSviva addObject:[urlData objectForKey:@"url_quality_name"]];
            
            
        }
        NSMutableDictionary *svivaTova = [[NSMutableDictionary alloc]init];
        [svivaTova setObject:sectionSviva forKey:[regular valueForKey:@"description"]];
        if([dataSource count]>1)
            [dataSource removeObjectAtIndex:0];
        [dataSource insertObject:svivaTova atIndex:0];
        
        [self.joinedStreamNames addEntriesFromDictionary:self.svivaTovastreamNames];
        [self.joinedStreamNames addEntriesFromDictionary:self.streamNames];
        
    }
    else
        [self.joinedStreamNames addEntriesFromDictionary:self.streamNames];
    
    [self.tableView reloadData];
    
}

-(void) openMenu {
    
    MenuViewController *menu = [[MenuViewController alloc] init];
    menu.fromWhere = 1;
    [self.navigationController pushViewController:menu animated:YES];
    
    
}

- (void) play
{
    [self.mp play];
}

- (void) pause
{
    [self.mp pause];
}

#pragma mark - Audio playback using Apple's player to save resources


-(void)playFromURL:(NSURL *)URL {
    
    [FIRAnalytics logEventWithName:@"play"
                       parameters:@{
                           @"name": [URL.absoluteString containsString:@"mp3"]?@"Audio":@"Video",
                                    @"URL": URL.absoluteString
                                    }];
    self.mpVC = [[AVPlayerViewController alloc] init];
    
    if (self.mpVC)
    {
        self.mpVC.player = [AVPlayer playerWithURL:URL];
        
        
        // Register to receive a notification when the movie has finished playing
        [[NSNotificationCenter defaultCenter] addObserver:self
                                                 selector:@selector(moviePlayBackDidFinish:)
                                                     name:AVPlayerItemFailedToPlayToEndTimeErrorKey
                                                   object:nil];
        
        
        [self presentViewController:mpVC animated:YES completion:^{
            [mpVC.player play];
        }];
        
        
    }
}

-(void) moviePlayBackDidFinish:(NSNotification*)notification {
    [[NSNotificationCenter defaultCenter] removeObserver:self
                                                    name:AVPlayerItemFailedToPlayToEndTimeErrorKey
                                                  object:nil];
    NSError *error = [[notification userInfo] objectForKey:@"error"];
    if (error) {
        NSLog(@"Did finish with error: %@", error);
        UIAlertView *noBrodMessage = [[UIAlertView alloc] initWithTitle: @"" message: @"Sorry No Broadcast"  delegate: self cancelButtonTitle: @"Ok" otherButtonTitles: nil];
        [noBrodMessage show];
        
    }
    self.mp = nil;
    self.mpVC = nil;
}



/*tmp4test
 #pragma mark - Shake Gesture
 -(BOOL)canBecomeFirstResponder {
 return YES;
 }
 
 - (void)motionEnded:(UIEventSubtype)motion withEvent:(UIEvent *)event {
 NSLog(@"{motion ended event ");
 if (motion == UIEventSubtypeMotionShake) {
 NSLog(@"{shaken state ");
 [self resignFirstResponder];  // deny double shake gesture
 
 //SvivaTovaLoginViewController *svivaTovaLoginViewController = [[SvivaTovaLoginViewController alloc] init];
 //[self presentModalViewController:svivaTovaLoginViewController animated:YES];
 
 }
 else {
 NSLog(@"{not shaken state ");
 }
 }
 */
- (void)playerViewController:(AVPlayerViewController *)playerViewController
restoreUserInterfaceForPictureInPictureStopWithCompletionHandler:(void (^)(BOOL restored))completionHandler {

    // If your player VC isn’t visible, present it so playback continues in-app
    if (self.presentedViewController != playerViewController) {
        [self presentViewController:playerViewController animated:YES completion:^{
            completionHandler(YES); // UI restored
        }];
    } else {
        completionHandler(YES);
    }
}

- (void)playURL:(NSURL *)url {
    
    NSUserDefaults* userDefaults = [[NSUserDefaults alloc] initWithSuiteName:@"group.net.openid.appauth.Example"];
    NSData *archivedAuthState = [userDefaults objectForKey:@"authState"];
    OIDAuthState *authState = [NSKeyedUnarchiver unarchiveObjectWithData:archivedAuthState];
    
    
    NSDictionary *hdr = @{ @"Authorization": authState.lastTokenResponse.accessToken };
    AVURLAsset *asset = [AVURLAsset URLAssetWithURL:url
                                            options:@{ AVURLAssetHTTPHeaderFieldsKey: hdr }];
    
    
    [asset.resourceLoader setDelegate:self queue:dispatch_get_main_queue()];
    
    
    AVPlayerItem *item = [AVPlayerItem playerItemWithAsset:asset];

    // Observe item + player
    [[NSNotificationCenter defaultCenter] addObserver:self
                                             selector:@selector(itemEnded:)
                                                 name:AVPlayerItemDidPlayToEndTimeNotification
                                               object:item];
    [[NSNotificationCenter defaultCenter] addObserver:self
                                             selector:@selector(itemFailedToEnd:)
                                                 name:AVPlayerItemFailedToPlayToEndTimeNotification
                                               object:item];
    [[NSNotificationCenter defaultCenter] addObserver:self
                                             selector:@selector(itemStalled:)
                                                 name:AVPlayerItemPlaybackStalledNotification
                                               object:item];

    [item addObserver:self forKeyPath:@"status"
              options:NSKeyValueObservingOptionNew | NSKeyValueObservingOptionInitial
              context:NULL];

    self.mp = [AVPlayer playerWithPlayerItem:item];
    if (@available(iOS 10.0, *)) {
        // If you don’t want AVPlayer to wait forever to “minimize stalls”
        self.mp.automaticallyWaitsToMinimizeStalling = NO;
    }

    AVPlayerViewController *vc = [AVPlayerViewController new];
    vc.player = self.mp;
    vc.modalPresentationStyle = UIModalPresentationFullScreen;
    vc.showsPlaybackControls = YES;
    self.mpVC = vc;
    self.mpVC.delegate = self;

    if (@available(iOS 12.0, *)) {
        vc.entersFullScreenWhenPlaybackBegins = YES;
        vc.exitsFullScreenWhenPlaybackEnds = YES; // works only when the item actually ends
    }

    [self presentViewController:vc animated:YES completion:^{
        [self.mp play];
    }];
    

    // Optional: watch timeControlStatus to detect “waiting” states
    if (@available(iOS 10.0, *)) {
        [self.mp addObserver:self forKeyPath:@"timeControlStatus"
                         options:NSKeyValueObservingOptionNew context:NULL];
    }
}

#pragma mark - Notifications

- (void)itemEnded:(NSNotification *)n {
    [self cleanupAndDismiss:@"ended"];
}

- (void)itemFailedToEnd:(NSNotification *)n {
    NSError *err = n.userInfo[AVPlayerItemFailedToPlayToEndTimeErrorKey];
    NSLog(@"Playback failed to end: %@", err);
    [self cleanupAndDismiss:@"failed"];
}

- (void)itemStalled:(NSNotification *)n {
    NSLog(@"Playback stalled");
    // 1) Why is the player waiting?
    if (@available(iOS 10.0, *)) {
        NSLog(@"timeControlStatus=%ld reason=%@", (long)self.mp.timeControlStatus,
              self.mp.reasonForWaitingToPlay); // e.g. AVPlayerWaitingToMinimizeStallsReason
    }

    // 2) Buffer health
    AVPlayerItem *item = self.mp.currentItem;
    NSArray *ranges = item.loadedTimeRanges;
    if (ranges.count) {
        CMTimeRange r = [ranges.firstObject CMTimeRangeValue];
        Float64 cur = CMTimeGetSeconds(item.currentTime);
        Float64 end = CMTimeGetSeconds(CMTimeRangeGetEnd(r));
        NSLog(@"buffered=%.2fs likely=%d empty=%d full=%d",
              end - cur,
              item.playbackLikelyToKeepUp,
              item.playbackBufferEmpty,
              item.playbackBufferFull);
    }

    // 3) Network access log (tells you stalls, bitrates, errors per segment)
    AVPlayerItemAccessLogEvent *e = item.accessLog.events.lastObject;
    NSLog(@"observedBitrate=%.0f indicatedBitrate=%.0f stalls=%ld transfers=%ld",
          e.observedBitrate, e.indicatedBitrate,
          (long)e.numberOfStalls, (long)e.numberOfServerAddressChanges);

    // 4) Errors (404/403, key failures, etc.)
    for (AVPlayerItemErrorLogEvent *errEvt in item.errorLog.events) {
        NSLog(@"error: %@", errEvt.errorComment);
    }
    
    AVPlayerItemErrorLogEvent *err = self.mp.currentItem.errorLog.events.lastObject;
    NSLog(@"fail URL=%@ status=%ld domain=%@ comment=%@",
          err.URI, (long)err.errorStatusCode, err.errorDomain, err.errorComment);
    [self startStallTimer]; // treat as “ended” if we’re stuck too long
}

#pragma mark - KVO

- (void)observeValueForKeyPath:(NSString *)keyPath
                      ofObject:(id)obj
                        change:(NSDictionary<NSKeyValueChangeKey,id> *)change
                       context:(void *)context {
    if ([keyPath isEqualToString:@"status"]) {
        AVPlayerItemStatus st = ((AVPlayerItem *)obj).status;
        if (st == AVPlayerItemStatusFailed) {
            NSLog(@"Item error: %@", ((AVPlayerItem *)obj).error);
            [self cleanupAndDismiss:@"item-status-failed"];
        }
    }
    if (@available(iOS 10.0, *)) {
        if ([keyPath isEqualToString:@"timeControlStatus"]) {
            switch (self.mp.timeControlStatus) {
                case AVPlayerTimeControlStatusPlaying:
                    [self invalidateStallTimer];
                    break;
                case AVPlayerTimeControlStatusPaused:
                    // user paused or we paused; no action
                    break;
                case AVPlayerTimeControlStatusWaitingToPlayAtSpecifiedRate:
                    NSLog(@"Waiting reason: %@", self.mp.reasonForWaitingToPlay);
                    [self startStallTimer];
                    break;
            }
        }
    }
}

#pragma mark - Stall handling

- (void)startStallTimer {
    [self invalidateStallTimer];
    self.stallTimer = [NSTimer scheduledTimerWithTimeInterval:12.0
                                                       target:self
                                                     selector:@selector(stallTimeout)
                                                     userInfo:nil
                                                      repeats:NO];
}

- (void)invalidateStallTimer {
    [self.stallTimer invalidate];
    self.stallTimer = nil;
}

- (void)stallTimeout {
    NSLog(@"Stall timeout → dismissing player");
    [self cleanupAndDismiss:@"stall-timeout"];
}

#pragma mark - Cleanup & dismiss

- (void)cleanupAndDismiss:(NSString *)reason {
    NSLog(@"Dismissing player due to: %@", reason);
    [self invalidateStallTimer];
    [[NSNotificationCenter defaultCenter] removeObserver:self];
    @try { [self.mp removeObserver:self forKeyPath:@"timeControlStatus"]; } @catch (...) {}
    @try { [self.mp.currentItem removeObserver:self forKeyPath:@"status"]; } @catch (...) {}

    [self.mpVC dismissViewControllerAnimated:YES completion:^{
        self.mpVC = nil;
    }];
}


#pragma mark - AVAssetResourceLoaderDelegate

- (BOOL)resourceLoader:(AVAssetResourceLoader *)loader
shouldWaitForLoadingOfRequestedResource:(AVAssetResourceLoadingRequest *)loadingRequest {

    
    NSUserDefaults* userDefaults = [[NSUserDefaults alloc] initWithSuiteName:@"group.net.openid.appauth.Example"];
    NSData *archivedAuthState = [userDefaults objectForKey:@"authState"];
    OIDAuthState *authState = [NSKeyedUnarchiver unarchiveObjectWithData:archivedAuthState];
    // Restore original https scheme
    NSURLComponents *c = [NSURLComponents componentsWithURL:loadingRequest.request.URL resolvingAgainstBaseURL:NO];
    c.scheme = @"https";
    NSURL *realURL = c.URL;

    NSMutableURLRequest *req = [NSMutableURLRequest requestWithURL:realURL];
    [req setValue:[NSString stringWithFormat:@"Bearer %@", authState.lastTokenResponse.accessToken] forHTTPHeaderField:@"Authorization"];

    NSURLSessionDataTask *task = [[NSURLSession sharedSession] dataTaskWithRequest:req
                                                                 completionHandler:^(NSData *data, NSURLResponse *response, NSError *error) {
        if (error) {
            [loadingRequest finishLoadingWithError:error];
            return;
        }

        // Fill out content info if available (helps with HLS keys/segments)
        if ([response isKindOfClass:[NSHTTPURLResponse class]]) {
            NSHTTPURLResponse *http = (NSHTTPURLResponse *)response;
            loadingRequest.response = http;
            loadingRequest.contentInformationRequest.contentType = http.MIMEType;
            NSNumber *len = http.allHeaderFields[@"Content-Length"];
            if (len) loadingRequest.contentInformationRequest.contentLength = len.longLongValue;
        }

        // Provide data
        [loadingRequest.dataRequest respondWithData:data];
        [loadingRequest finishLoading];
    }];

    [task resume];
    return YES; // we’ll call finishLoading/finishLoadingWithError when done
}

- (void)resourceLoader:(AVAssetResourceLoader *)loader didCancelLoadingRequest:(AVAssetResourceLoadingRequest *)loadingRequest {
    // Handle cancellation if you track tasks
}


@end
