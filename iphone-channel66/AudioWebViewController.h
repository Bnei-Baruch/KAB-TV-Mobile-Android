//
//  AudioWebViewController.h
//  kxmovie
//
//  Created by Igal Avraham on 5/22/13.
//
//

#import <UIKit/UIKit.h>
#import "Reachability.h"
@interface AudioWebViewController : UIViewController <UIWebViewDelegate>

{
    IBOutlet UIWebView *mWebview;
    NSString * url;
    Boolean mPlaying;
    id __unsafe_unretained delegate;

    
}

@property (unsafe_unretained) id delegate;

@property (retain,nonatomic) IBOutlet UIWebView *mWebview;
@property (retain,nonatomic)  NSString *url;

- (void)setUrl:(NSString*)url;

@end
