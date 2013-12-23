//
//  AudioWebViewController.m
//  kxmovie
//
//  Created by Igal Avraham on 5/22/13.
//
//

#import "AudioWebViewController.h"
#import "LangSelectorViewController.h"

@interface AudioWebViewController ()

@end

@implementation AudioWebViewController
@synthesize mWebview,delegate,url;


- (id)initWithNibName:(NSString *)nibNameOrNil bundle:(NSBundle *)nibBundleOrNil
{
    self = [super initWithNibName:nibNameOrNil bundle:nibBundleOrNil];
    if (self) {
        // Custom initialization
       
        url = [[NSString alloc]init];
       
    }
    return self;
}

- (void)viewDidLoad
{
    [super viewDidLoad];
    //mWebview.delegate = self;
    // Do any additional setup after loading the view from its nib.
  //  [mWebview loadRequest:[NSURLRequest requestWithURL:[NSURL URLWithString:url]]];
    [[UIApplication sharedApplication] beginReceivingRemoteControlEvents];

}

-(void)viewDidUnload
{
    
}

- (void)didReceiveMemoryWarning
{
    [super didReceiveMemoryWarning];
    // Dispose of any resources that can be recreated.
}

- (void)setUrl:(NSString*)url1;
{
    url = url1;
    
}

- (void) viewDidAppear:(BOOL)animated {
    
    if(!mPlaying)
    {
     [mWebview loadRequest:[NSURLRequest requestWithURL:[NSURL URLWithString:url]]];
        mPlaying = YES;
    }
    else
    {
        mPlaying = NO;
        
       // [self ];
        [self dismissViewControllerAnimated:YES completion:nil];
        [self.delegate dismissAudio];
        //[self.parentViewController dismissViewControllerAnimated:YES completion:nil];
        mWebview = nil;
    }
}

- (BOOL)webView:(UIWebView *)webView shouldStartLoadWithRequest:(NSURLRequest *)request navigationType:(UIWebViewNavigationType)navigationType
{
    return YES;
}
- (void)webViewDidFinishLoad:(UIWebView *)webView
{
   // [self.navigationController popViewControllerAnimated:YES];
}

@end
