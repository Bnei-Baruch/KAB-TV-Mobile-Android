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
#import "KxMovieViewController.h"
#import "LoginControllerViewController.h"
#import "MenuViewController.h"
#import "Reachability.h"
//#import "AudioWebViewController.h"

@interface MainViewController () {
    NSArray *_localMovies;
    NSArray *_remoteMovies;
    NSArray *_localMoviesName;
}

@property (strong, nonatomic) UITableView *tableView;
@end



#pragma mark - Private definitions

@interface MainViewController (Private)
-(void)playFromURL:(NSURL *)URL;
@end


#pragma mark - implementation
@implementation MainViewController

@synthesize streamNames;
@synthesize mp;
@synthesize mpVC;



- (id)init
{
    self = [super init];
    if (self) {
        self.title = @"Channel 66";
    
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
        @"mmst://wms1.il.kab.tv/heb",
        @"http://icecast.kab.tv/rus.mp3",
        @"mmst://wms1.il.kab.tv/rus",
        //@"http://icecast.kab.tv/live1-heb-574bcfd5.mp3"
        
            //@"rtsp://184.72.239.149/vod/mp4://BigBuckBunny_175k.mov",
            //@"http://santai.tv/vod/test/BigBuckBunny_175k.mov",
        ];
        self.streamNames = [[ NSMutableDictionary alloc]
         init];
        [self.streamNames setObject:@"ערוץ 66 - אודיו" forKey:@"http://icecast.kab.tv/heb.mp3"];
        [self.streamNames setObject:@"ערוץ 66 - וידאו" forKey:@"mmst://wms1.il.kab.tv/heb"];
        [self.streamNames setObject:@"Канал 66 - Русском Аудио" forKey:@"http://icecast.kab.tv/rus.mp3"];
        [self.streamNames setObject:@"Канал 66 - Русском Видео" forKey:@"mmst://wms1.il.kab.tv/rus"];
        
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

- (void)viewDidLoad
{
    [super viewDidLoad];

    
    
    
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
    return 1;
}

- (NSString *)tableView:(UITableView *)tableView titleForHeaderInSection:(NSInteger)section
{
    switch (section) {
        case 0:     return @"Channel 66";
        //case 1:     return @"Extra";
    }
    return @"";
}

- (NSInteger)tableView:(UITableView *)tableView numberOfRowsInSection:(NSInteger)section
{
    switch (section) {
        case 0:     return _remoteMovies.count;
        //case 1:     return _localMovies.count;
    }
    return 0;
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
    
    NSString *path;
    
    if (indexPath.section == 0) {
        
        path = _remoteMovies[indexPath.row];
        cell.textLabel.text = [self.streamNames objectForKey:path];
        
    } 
    
    return cell;
}

#pragma mark - Table view delegate

- (void)tableView:(UITableView *)tableView didSelectRowAtIndexPath:(NSIndexPath *)indexPath
{
    Reachability *reach = [Reachability reachabilityForInternetConnection];
    NetworkStatus status = [reach currentReachabilityStatus];
    
    if(status == NotReachable)
    {
        [self dismissViewControllerAnimated:YES completion:nil];
        UIAlertView *alert = [[UIAlertView alloc] initWithTitle:@"Channel 66"
                                                        message:@"No Internet connection available" delegate:nil
                                              cancelButtonTitle:@"OK" otherButtonTitles:nil];
        [alert show];
        return;
    }
    
    
    NSString *path;
    path = _remoteMovies[indexPath.row];
    NSRange range = [path rangeOfString:@"mp3"];
    Boolean video = [path rangeOfString:@"mp3"].length==0;
    Boolean quality = [[[NSUserDefaults standardUserDefaults] valueForKey:@"quality"] isEqualToString:@"Medium"];
    if (indexPath.section == 0) {
        
        
        if( video && quality)
            path = [path stringByAppendingString:@"_medium"];
        
        
    }
    NSString *analyticPrm = [NSString stringWithFormat:@"%@ - %@", @"Channel 66", path];
    //googleAnalytic
    self.trackedViewName = analyticPrm;
    if(video)
    {
        KxMovieViewController *vc = [KxMovieViewController movieViewControllerWithContentPath:path];
        [self presentViewController:vc animated:YES completion:nil];
    }
    else
    {
        //AudioWebViewController *vc =[[AudioWebViewController alloc]init];
        //[vc setUrl:path];
        [self playFromURL:[NSURL URLWithString:path]]; // to save memory
        //[self presentViewController:vc animated:YES completion:nil];
    }
    
    //[self.navigationController pushViewController:vc animated:YES];
}

/* OLD:
#pragma mark - Table view delegate

- (void)tableView:(UITableView *)tableView didSelectRowAtIndexPath:(NSIndexPath *)indexPath
{
    
    
    NSString *path;
    
    if (indexPath.section == 0) {
        
        path = _remoteMovies[indexPath.row];
        
        // TODO: this is temp, should build a wise mechanism:
        NSRange isAudioStream = [path rangeOfString:@"mp3"]; // use localization
        NSRange isHebrewStream = [path rangeOfString:@"heb"]; // use localization
        NSRange isRussiantream = [path rangeOfString:@"rus"]; // use localization
        
        if (isAudioStream.length && isHebrewStream.length) {
            [self playFromURL:[NSURL URLWithString:path]];
            return;
        }
        else if (isAudioStream.length && isRussiantream.length) {
            [self playFromURL:[NSURL URLWithString:path]];
            return;
        }
        
        
        
        //unused: NSRange range = [path rangeOfString:@"mp3"];
        Boolean video = [path rangeOfString:@"mp3"].length==0;
        if ([[[NSUserDefaults standardUserDefaults] valueForKey:@"quality"] isKindOfClass:[NSString class]]) {
            Boolean quality = [[[NSUserDefaults standardUserDefaults] valueForKey:@"quality"] isEqualToString:@"Medium"];
            //if( video && quality)
            //path = [path stringByAppendingString:@"_medium"];
        }
        
        
    }
    NSString *analyticPrm = [NSString stringWithFormat:@"%@ - %@", @"Channel 66", path];
    //googleAnalytic
    self.trackedViewName = analyticPrm;
    
    KxMovieViewController *vc = [KxMovieViewController movieViewControllerWithContentPath:path];
    [self presentViewController:vc animated:YES completion:nil];
    //[self.navigationController pushViewController:vc animated:YES];
}
*/

- (void)textFieldDidEndEditing:(UITextField *)textField
{
    NSLog(@"textFieldDidEndEditing");
}

-(void) openMenu {
    
    MenuViewController *menu = [[MenuViewController alloc] init];
    menu.fromWhere = 1;
    [self.navigationController pushViewController:menu animated:YES];
    
    
}


#pragma mark - Audio playback using Apple's player to save resources


-(void)playFromURL:(NSURL *)URL {
    
    
	self.mpVC = [[MPMoviePlayerViewController alloc] initWithContentURL:URL];
	if (self.mpVC)
	{
		self.mp = [mpVC moviePlayer];
        
        self.mp.useApplicationAudioSession = NO;// for audio playing in background. (3.x)
        
		// Register to receive a notification when the movie has finished playing
		[[NSNotificationCenter defaultCenter] addObserver:self
												 selector:@selector(moviePlayBackDidFinish:)
													 name:MPMoviePlayerPlaybackDidFinishNotification
												   object:nil];
        
        
		[self presentMoviePlayerViewControllerAnimated:mpVC];
		[self.mp play];
        
        
        
	}
}

-(void) moviePlayBackDidFinish:(NSNotification*)notification {
    [[NSNotificationCenter defaultCenter] removeObserver:self
                                                    name:MPMoviePlayerPlaybackDidFinishNotification
                                                  object:nil];
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

@end
