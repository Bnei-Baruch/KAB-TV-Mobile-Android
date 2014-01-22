//
//  ViewController.h
//  kxmovie
//
//  Created by Igal Avraham on 5/21/13.
//
//

#import <UIKit/UIKit.h>
#import "CXMLDocument.h"
#import "CXMLElement.h"
#import "GAITrackedViewController.h"
#import "AudioWebViewController.h"
#import <AVFoundation/AVFoundation.h>
#import <AudioToolbox/AudioToolbox.h>
#import <MediaPlayer/MediaPlayer.h>

@interface LangSelectorViewController : UITableViewController <UITableViewDataSource>

{
    NSArray * langName;
    NSArray * locales;
    Boolean mFromActionview;
   // AudioWebViewController *audiocontroller;
    MPMoviePlayerController *mp;
	MPMoviePlayerViewController *mpVC;
}
@property (nonatomic, strong) MPMoviePlayerController *mp;
@property (nonatomic, strong) MPMoviePlayerViewController *mpVC;


//@property (retain, nonatomic) AudioWebViewController *audiocontroller;
@property (nonatomic,assign) Boolean  mFromActionview;

- (void) dismissAudio;
@end
