//
//  ViewController.m
//  kxmovie
//
//  Created by Igal Avraham on 5/21/13.
//
//
#import <SystemConfiguration/CaptiveNetwork.h>
#import "LangSelectorViewController.h"
#import "KxMovieViewController.h"
#import "AudioWebViewController.h"
@interface LangSelectorViewController ()

@end

@implementation LangSelectorViewController
@synthesize mFromActionview,mp,mpVC;

- (id)initWithStyle:(UITableViewStyle)style
{
    self = [super initWithStyle:style];
    if (self) {
        // Custom initialization
        langName = [[NSArray alloc]initWithObjects:@"English",@"Español",@"Français",@"Pycckий",@"Italiano",@"Duetsch",@"עברית",@"Türkçe", nil];
        
        locales = [[NSArray alloc]initWithObjects:@"eng",@"spa",@"fre",@"rus",@"ita",@"ger",@"heb",@"por", nil];
        
       
    }
    return self;
}

- (void)viewDidLoad
{
    [super viewDidLoad];

    // Uncomment the following line to preserve selection between presentations.
    // self.clearsSelectionOnViewWillAppear = NO;
 
    // Uncomment the following line to display an Edit button in the navigation bar for this view controller.
     //self.navigationItem.rightBarButtonItem = self.editButtonItem;
    //[self.navigationController. popViewControllerAnimated:NO];
    
    
}

//- (void) viewDidUnload
//{
//    self.audiocontroller =nil;
//    [super viewDidUnload];
//}

- (void) viewWillDisappear:(BOOL)animated
{
//    if(![audiocontroller isBeingPresented])
//        audiocontroller  = nil;
    [super viewWillDisappear:animated];
    
}

- (void)didReceiveMemoryWarning
{
    [super didReceiveMemoryWarning];
    // Dispose of any resources that can be recreated.
}

#pragma mark - Table view data source

- (NSInteger)numberOfSectionsInTableView:(UITableView *)tableView
{
#warning Potentially incomplete method implementation.
    // Return the number of sections.
    return 1;
}

- (NSInteger)tableView:(UITableView *)tableView numberOfRowsInSection:(NSInteger)section
{
#warning Incomplete method implementation.
    // Return the number of rows in the section.
    return [langName count];
    //number of languages
}

- (UITableViewCell *)tableView:(UITableView *)tableView cellForRowAtIndexPath:(NSIndexPath *)indexPath
{
    static NSString *CellIdentifier = @"Cell";
    UITableViewCell *cell = [tableView dequeueReusableCellWithIdentifier:CellIdentifier];
    if (cell == nil) {
        cell = [[UITableViewCell alloc] initWithStyle:UITableViewCellStyleDefault reuseIdentifier:CellIdentifier];
    }
    
    // Configure the cell...
    NSString *cellValue = [langName objectAtIndex:indexPath.row];
    cell.textLabel.text = cellValue;
    return cell;
}

/*
// Override to support conditional editing of the table view.
- (BOOL)tableView:(UITableView *)tableView canEditRowAtIndexPath:(NSIndexPath *)indexPath
{
    // Return NO if you do not want the specified item to be editable.
    return YES;
}
*/

/*
// Override to support editing the table view.
- (void)tableView:(UITableView *)tableView commitEditingStyle:(UITableViewCellEditingStyle)editingStyle forRowAtIndexPath:(NSIndexPath *)indexPath
{
    if (editingStyle == UITableViewCellEditingStyleDelete) {
        // Delete the row from the data source
        [tableView deleteRowsAtIndexPaths:@[indexPath] withRowAnimation:UITableViewRowAnimationFade];
    }   
    else if (editingStyle == UITableViewCellEditingStyleInsert) {
        // Create a new instance of the appropriate class, insert it into the array, and add a new row to the table view
    }   
}
*/

/*
// Override to support rearranging the table view.
- (void)tableView:(UITableView *)tableView moveRowAtIndexPath:(NSIndexPath *)fromIndexPath toIndexPath:(NSIndexPath *)toIndexPath
{
}
*/

/*
// Override to support conditional rearranging of the table view.
- (BOOL)tableView:(UITableView *)tableView canMoveRowAtIndexPath:(NSIndexPath *)indexPath
{
    // Return NO if you do not want the item to be re-orderable.
    return YES;
}
*/

-(NSString *) getKeyDataFromUrl:(NSString *) url {
    NSLog(@"+-+- LoginControllerViewController: getKeyDataFromUrl");
    NSString *retData;
    
    NSURLRequest *theRequest =
    [NSURLRequest requestWithURL:[NSURL URLWithString:url]
                     cachePolicy:NSURLRequestReloadIgnoringLocalCacheData
                 timeoutInterval:10.0];
    
    
    // create the connection with the request
    // and start loading the data
    // note that the delegate for the NSURLConnection is self, so delegate methods must be defined in this file
    //            NSURLConnection *theConnection=[[NSURLConnection alloc] initWithRequest:theRequest delegate:self];
    NSURLResponse* response;
    NSError* error = nil;
    
    NSData* result =[NSURLConnection sendSynchronousRequest:theRequest returningResponse:&response error:&error];
    
    
    if(error == nil)
    {
        retData = [[NSString alloc] initWithData:result
                                        encoding:NSUTF8StringEncoding];
        
        NSLog(@"string-retData = %@", retData);
    }
    
    
    
    return retData;
}

#pragma mark - Table view delegate

- (void)tableView:(UITableView *)tableView didSelectRowAtIndexPath:(NSIndexPath *)indexPath
{
    // Navigation logic may go here. Create and push another view controller.
    /*
     <#DetailViewController#> *detailViewController = [[<#DetailViewController#> alloc] initWithNibName:@"<#Nib name#>" bundle:nil];
     // ...
     // Pass the selected object to the new view controller.
     [self.navigationController pushViewController:detailViewController animated:YES];
     */
    //NSRange isRange = [request.URL.absoluteString rangeOfString:@"mp3" options:NSCaseInsensitiveSearch];

    Reachability *reach = [Reachability reachabilityForInternetConnection];
    NetworkStatus status = [reach currentReachabilityStatus];
    //    UIAlertView *alert = [[UIAlertView alloc] initWithTitle:@"Reachability"
    //                                                    message:[self stringFromStatus:status] delegate:nil
    //                                          cancelButtonTitle:@"OK" otherButtonTitles:nil];
    //    [alert show];
    
    if(status == NotReachable)
    {
        [self dismissViewControllerAnimated:YES completion:nil];
        UIAlertView *alert = [[UIAlertView alloc] initWithTitle:@"Channel 66"
                                                        message:@"No Internet connection available" delegate:nil
                                              cancelButtonTitle:@"OK" otherButtonTitles:nil];
        [alert show];
        return;
    }

    
    NSString *url = @"http://kabbalahgroup.info/internet/events/render_event_response?locale=he&source=stream_container&type=update_presets&timestamp=2011-11-25+13:29:53+UTC&stream_preset_id=3&flash=true&wmv=true";
    NSString* keyData = [self getKeyDataFromUrl:url];
//    
//    if(isRange.location!=NSNotFound && !mFromActionview)
//    {
    if(!mFromActionview)
    {
        if ([self checkIsActive: keyData])
        {
            UIActionSheet *select = [[UIActionSheet alloc]initWithTitle:@"Stream type" delegate:self cancelButtonTitle:@"Cancel" destructiveButtonTitle:nil otherButtonTitles:@"Video",@"Audio", nil];
            select.accessibilityValue = [locales objectAtIndex:indexPath.row];
            [select showInView:self.tableView];
            mFromActionview = NO;
            
            
           // return false;
        }
        else { // is_active = false
            UIAlertView *noBrodMessage = [[UIAlertView alloc] initWithTitle: @"" message: @"Sorry No Broadcast"  delegate: self cancelButtonTitle: @"Ok" otherButtonTitles: nil];
            [noBrodMessage show];
         //   return false;
        }
        
        
   }

}

-(BOOL) checkIsActive: (NSString*) keyData {
    //check if special stream is active
    NSLog(@"+-+- LoginControllerViewController: checkIsActive");
    BOOL retVal=NO;
    
    
    NSString *strToFind = @"is_active";
    NSRange range = [keyData rangeOfString:strToFind];
    NSLog(@"range.length %d", range.length);
    NSLog(@"range.location %d", range.location);
    
    NSString *tempVal = [keyData substringWithRange:NSMakeRange(range.location + strToFind.length+2, 4)];
    
    if ([tempVal isEqualToString:@"true"]) {
        retVal = YES;
    } else {
        retVal = NO;
    }
    
    return retVal;
    
}


- (void)actionSheet:(UIActionSheet *)actionSheet clickedButtonAtIndex:(NSInteger)buttonIndex;
{
    NSLog(@"LoginControllerViewController: clickedButtonAtIndex");
    NSString *url = @"http://kabbalahgroup.info/internet/events/render_event_response?locale=he&source=stream_container&type=update_presets&timestamp=2011-11-25+13:29:53+UTC&stream_preset_id=3&flash=true&wmv=true";
    NSString* keyData = [self getKeyDataFromUrl:url];
    
     //parse for langugae and select from streams
    url = @"http://mobile.kbb1.com/kab_channel/sviva_tova/jsonresponseexample.json";
    NSDictionary* langjsonData = [self getJsonDataFromUrl:url];
    
    switch (buttonIndex) {
        case 0:
        {
            //video
            //http://mobile.kbb1.com/kab_channel/sviva_tova/jsonresponseexample.json
            //http://kabbalahgroup.info/internet/events/render_event_response?locale=he&source=stream_container&type=update_presets&timestamp=2011-11-25+13:29:53+UTC&stream_preset_id=3&flash=true&wmv=true
           
           
            
            if ([self checkIsActive: keyData])
            {
                if (langjsonData && keyData) {
                    
                    NSString *isHLS = [langjsonData objectForKey:@"HLSSupport"];
                    
                    if([isHLS isEqualToString:@"no"])
                    {
                    NSString *keyToReplace = [self getSecretKeyValueForAsxFile: keyData];
                    NSLog(@"langjsonData = %@, keyJsonData = %@", langjsonData, keyData);
                    NSMutableArray *locales = [[NSMutableArray alloc] init];
                    for( NSDictionary *locale in [langjsonData objectForKey:@"locale"])
                    {
                        [locales addObject:[[locale allKeys] objectAtIndex:0]];
                    }
                    NSLog(@"Video actionSheet.accessibilityValue = %@", actionSheet.accessibilityValue);
                    NSString *url_clicked = actionSheet.accessibilityValue;
                    NSString *asxUrlToPlay;
                    for(NSString *lang in locales)
                    {
                        NSRange range = [url_clicked rangeOfString:lang];
                        NSLog(@"range.length %d", range.length);
                        NSLog(@"range.location %d", range.location);
                        if(range.length  > 0)
                        {
                            //find the correct leng in the json
                            int index =  [locales indexOfObject:lang];
                            
                            NSArray *urls = [[[[[[[langjsonData objectForKey:@"locale"] objectAtIndex:index] objectForKey:lang]objectForKey:@"pages"] objectAtIndex:0] objectForKey:@"regular"] objectForKey:@"urls"];
                            
                            NSUserDefaults *userD = [[NSUserDefaults alloc] init];
                            NSString *strQ = [[userD objectForKey:@"quality"] lowercaseString];
                            
                            if (urls.count == 1) {
                                NSDictionary *tempUrls = [urls objectAtIndex:0];
                                asxUrlToPlay = [tempUrls objectForKey:@"url_value"];
                            } else {
                                //need to complete here
                                int minUrlLength=100;
                                for( NSDictionary *urlsT  in urls)
                                {
                                    NSString *asxUrlToPlayT = [urlsT objectForKey:@"url_value"];
                                    NSRange urlRange = [asxUrlToPlayT rangeOfString:strQ];
                                    NSLog(@"urlRange.length %d", urlRange.length);
                                    NSLog(@"urlRange.location %d", urlRange.location);
                                    
                                    
                                    if ([strQ isEqualToString:@"high"] ) {
                                        if ([asxUrlToPlayT length] < minUrlLength) {
                                            //find the shortest asx url
                                            minUrlLength = [asxUrlToPlayT length];
                                            asxUrlToPlay = asxUrlToPlayT;
                                        }
                                    } else {
                                        if (urlRange.length  > 0) {
                                            //find the match asx url
                                            asxUrlToPlay = asxUrlToPlayT;
                                            break;
                                        }
                                    }
                                    NSLog(@" urlsT = %@", urlsT);
                                }
                            }
                            
                            NSLog(@" asxUrlToPlay = %@", asxUrlToPlay);
                            asxUrlToPlay = [self replaceKeyForAsx:asxUrlToPlay: keyToReplace];
                            NSLog(@"url = %@", asxUrlToPlay);
                            break;
                        }
                    }
                    if (asxUrlToPlay) {
                        NSArray* streamUrls = [self parseAsxFile :asxUrlToPlay];
                        NSString *urlToPlay = [self checkForActiveUrl: streamUrls];
                        NSLog(@"urlToPlay = %@", urlToPlay );
                        if (urlToPlay) {
                            //googleAnalytic
                            NSString *analyticPrm = [NSString stringWithFormat:@"%@ - %@", @"sviva Tova Video", urlToPlay];
                            //self.trackedViewName = analyticPrm;
                            KxMovieViewController *vc = [KxMovieViewController movieViewControllerWithContentPath:urlToPlay];
                        
//                            [self.navigationController pushViewController:vc animated:YES completion:nil];
                            //[self.navigationController pushViewController:vc animated:YES];
                            [self presentModalViewController:vc animated:YES];
                            
                        }
                    } else { // else show message for unavailble stream
                        UIAlertView *noBrodMessage = [[UIAlertView alloc] initWithTitle: @"" message: @"Broadcast not avilable, please try later"  delegate: self cancelButtonTitle: @"Ok" otherButtonTitles: nil];
                        [noBrodMessage show];
                    }
                }
                else
                {
                    NSString *keyToReplace = [self getSecretKeyValueForAsxFile: keyData];
                    NSLog(@"langjsonData = %@, keyJsonData = %@", langjsonData, keyData);
                    NSMutableArray *locales = [[NSMutableArray alloc] init];
                    for( NSDictionary *locale in [langjsonData objectForKey:@"localeHLS"])
                    {
                        [locales addObject:[[locale allKeys] objectAtIndex:0]];
                    }
                    NSLog(@"Video actionSheet.accessibilityValue = %@", actionSheet.accessibilityValue);
                    NSString *url_clicked = actionSheet.accessibilityValue;
                    NSString *asxUrlToPlay;
                    for(NSString *lang in locales)
                    {
                        NSRange range = [url_clicked rangeOfString:lang];
                        NSLog(@"range.length %d", range.length);
                        NSLog(@"range.location %d", range.location);
                        if(range.length  > 0)
                        {
                            //find the correct leng in the json
                            int index =  [locales indexOfObject:lang];
                            
                            NSArray *urls = [[[[[[[langjsonData objectForKey:@"localeHLS"] objectAtIndex:index] objectForKey:lang]objectForKey:@"pages"] objectAtIndex:0] objectForKey:@"regular"] objectForKey:@"urls"];
                            
                            NSUserDefaults *userD = [[NSUserDefaults alloc] init];
                            NSString *strQ = [[userD objectForKey:@"quality"] lowercaseString];
                            
                            if (urls.count == 1) {
                                NSDictionary *tempUrls = [urls objectAtIndex:0];
                                asxUrlToPlay = [tempUrls objectForKey:@"url_value"];
                            } else {
                                //need to complete here
                                int minUrlLength=100;
                                for( NSDictionary *urlsT  in urls)
                                {
                                    NSString *asxUrlToPlayT = [urlsT objectForKey:@"url_value"];
                                    NSRange urlRange = [asxUrlToPlayT rangeOfString:strQ];
                                    NSLog(@"urlRange.length %d", urlRange.length);
                                    NSLog(@"urlRange.location %d", urlRange.location);
                                    
                                    
                                    if ([strQ isEqualToString:@"high"] ) {
                                        if([[urlsT objectForKey:@"url_quality"] isEqualToString:@"300k"])
                                            asxUrlToPlay = asxUrlToPlayT;
                                        
                                    } else {
                                        if([[urlsT objectForKey:@"url_quality"] isEqualToString:@"100k"])
                                            asxUrlToPlay = asxUrlToPlayT;
                                        }
                                
                                        NSLog(@" urlsT = %@", urlsT);
                                    }
                            
                                }
                            }
                            
                        
                        
                    }
                    if (asxUrlToPlay) {
                        
                        NSString *urlToPlay = asxUrlToPlay;
                        NSLog(@"urlToPlay = %@", urlToPlay );
                        if (urlToPlay) {
                            //googleAnalytic
                            NSString *analyticPrm = [NSString stringWithFormat:@"%@ - %@", @"sviva Tova Video", urlToPlay];
                            //self.trackedViewName = analyticPrm;
//                            KxMovieViewController *vc = [KxMovieViewController movieViewControllerWithContentPath:urlToPlay];
                            self.mpVC = [[MPMoviePlayerViewController alloc] init];// initWithContentURL:URL];
                            
                            
                            if (self.mpVC)
                            {
                                [[UIApplication sharedApplication] beginReceivingRemoteControlEvents];
                                self.mp = [mpVC moviePlayer];
                                self.mp.movieSourceType = MPMovieSourceTypeStreaming;
                                [self.mp setContentURL:[NSURL URLWithString:urlToPlay]];
                                [self.mp prepareToPlay];
                                self.mp.fullscreen = YES;
                                self.mp.allowsAirPlay =YES;
                                [self.mp prepareToPlay];
                                self.mp.useApplicationAudioSession = YES;// for audio playing in background. (3.x)
                                
                                // Register to receive a notification when the movie has finished playing
                                [[NSNotificationCenter defaultCenter] addObserver:self
                                                                         selector:@selector(moviePlayBackDidFinish:)
                                                                             name:MPMoviePlayerPlaybackDidFinishNotification
                                                                           object:nil];
                                
                                ////        CGRect viewInsetRect = CGRectInset ([self.view bounds],0.0, 0.0 );
                                ////        [[self.mpVC view] setFrame: viewInsetRect ];
                                ////        [self.view addSubview:self.mpVC.view];
                                [self presentMoviePlayerViewControllerAnimated:mpVC];
                                [self.mp play];
                            }
                            
                            //                            [self.navigationController pushViewController:vc animated:YES completion:nil];
                            //[self.navigationController pushViewController:vc animated:YES];
                           // [self presentModalViewController:vc animated:YES];
                            
                        }
                    } else { // else show message for unavailble stream
                        UIAlertView *noBrodMessage = [[UIAlertView alloc] initWithTitle: @"" message: @"Broadcast not avilable, please try later"  delegate: self cancelButtonTitle: @"Ok" otherButtonTitles: nil];
                        [noBrodMessage show];
                    }

                }
            } else { // is_active = false
                UIAlertView *noBrodMessage = [[UIAlertView alloc] initWithTitle: @"" message: @"Sorry No Broadcast"  delegate: self cancelButtonTitle: @"Ok" otherButtonTitles: nil];
                [noBrodMessage show];
            }
            NSLog(@"Video picked");
            break;
        }
        }
        case 1:
        {
            if(!mFromActionview)
            {
            if ([self checkIsActive: keyData] )
            {
                BOOL isTranslationMode = NO;
                //check if inside a wifi area then use urls for realtime translations otherwise use regualr urls
                NSDictionary *wifisupport =  [langjsonData valueForKey:@"TranslationWIFISupport"];
                NSArray * ssid = [wifisupport valueForKey:@"ssid"];
                NSArray * urls  = [wifisupport valueForKey:@"urls"];
               
                
                NSString *audio = [[NSString alloc]init];
                audio = @"http://icecast.kab.tv/live1-heb-574bcfd5.mp3";
                audio = [audio stringByReplacingOccurrencesOfString:@"heb" withString:actionSheet.accessibilityValue];
                
                
                for(NSString *ssidString in ssid)
                {
                    if([ssidString isEqualToString:[self currentWifiSSID]])
                    {
                    for(NSDictionary *url in urls)
                    {
                        
                    
                        NSString *urlvalue = [url objectForKey:actionSheet.accessibilityValue];
                        if(urlvalue==nil)
                            continue;
                        else
                        {
                           
                        audio =  urlvalue;
                        isTranslationMode = YES;
                        break;
                        }
                    }
                    }
                }
                
                //googleAnalytic
                NSString *analyticPrm = [NSString stringWithFormat:@"%@ - %@", @"sviva Tova Audio", actionSheet];
      //          self.trackedViewName = analyticPrm;
                //Audio
                mFromActionview = YES;
                
               // mWebview = [[UIWebView alloc]init];
                //[self.tableView addSubview:mWebview];
                
               // [mWebview loadRequest:[NSURLRequest requestWithURL:[NSURL URLWithString:audio]]];
                if(isTranslationMode)
                {
                KxMovieViewController *ac = [KxMovieViewController movieViewControllerWithContentPath:audio];
                   
                    [self.navigationController pushViewController:ac animated:YES];
                    [ac pause];
                    [ac play];
                
                        
                }
                else
                {
                AudioWebViewController *ac = [[AudioWebViewController alloc]init];
//                if(audiocontroller== nil)
//                    audiocontroller = [[AudioWebViewController alloc]init];
                    ac.delegate = self;
                    [ac setUrl:audio];
                    [self.navigationController pushViewController:ac animated:YES];

               }
               
                //[self presentViewController:vc animated:YES completion:^{[self loadDone];}];
                
                NSLog(@"Audio actionSheet.accessibilityValue = %@", actionSheet.accessibilityValue);
                NSLog(@"Audio picked");
            }
            else { // is_active = false
                UIAlertView *noBrodMessage = [[UIAlertView alloc] initWithTitle: @"" message: @"Sorry No Broadcast"  delegate: self cancelButtonTitle: @"Ok" otherButtonTitles: nil];
                [noBrodMessage show];
            }
                mFromActionview = NO;
            }
            
            
            break;
        }
        default:
            break;
    }
}

-(void) moviePlayBackDidFinish:(NSNotification*)notification {
    [[NSNotificationCenter defaultCenter] removeObserver:self
                                                    name:MPMoviePlayerPlaybackDidFinishNotification
                                                  object:nil];
    
    NSError *error = [[notification userInfo] objectForKey:@"error"];
    if (error) {
        NSLog(@"Did finish with error: %@", error);
        
    }
    
    self.mp = nil;
    self.mpVC = nil;
}
- (void) dismissAudio
{
    
   // [audiocontroller dismissViewControllerAnimated:YES completion:nil];
    
    
}
-(void)loadDone
{
    NSLog(@"Load done");
}

-(NSDictionary *) getJsonDataFromUrl:(NSString *) url {
    NSLog(@"+-+- LoginControllerViewController: getJsonDataFromUrl");
    NSDictionary *retData;
    
    NSURLRequest *theRequest =
    [NSURLRequest requestWithURL:[NSURL URLWithString:url]
                     cachePolicy:NSURLRequestReloadIgnoringLocalCacheData
                 timeoutInterval:10.0];
    
    
    // create the connection with the request
    // and start loading the data
    // note that the delegate for the NSURLConnection is self, so delegate methods must be defined in this file
    //            NSURLConnection *theConnection=[[NSURLConnection alloc] initWithRequest:theRequest delegate:self];
    NSURLResponse* response;
    NSError* error = nil;
    
    NSData* result =[NSURLConnection sendSynchronousRequest:theRequest returningResponse:&response error:&error];
    
    
    if(error == nil)
    {
        NSError* jsonError = nil;
        retData = [NSJSONSerialization  JSONObjectWithData:result options:nil error:&jsonError];
        
        NSLog(@"json =  %@", retData);
    }
    
    
    
    return retData;
}

-(NSString *) getSecretKeyValueForAsxFile :(NSString*) stringData {
    //get current key for .asx url
    NSLog(@"+-+- LoginControllerViewController: getKeyValueForAsxFile");
    NSString *retKey;
    NSString *strToFind = @"\"secret_word\":\"";
    NSRange range = [stringData rangeOfString:strToFind];
    NSLog(@"range.length %d", range.length);
    NSLog(@"range.location %d", range.location);
    
    retKey = [stringData substringWithRange:NSMakeRange(range.location + strToFind.length, 8)];
    
    return retKey;
    
}

-(NSString *) getKeyValueForAsxFile :(NSString*) stringData {
    //get current key for .asx url
    NSLog(@"+-+- LoginControllerViewController: getKeyValueForAsxFile");
    NSString *retKey;
    NSString *strToFind = @"special-";
    NSRange range = [stringData rangeOfString:strToFind];
    NSLog(@"range.length %d", range.length);
    NSLog(@"range.location %d", range.location);
    
    retKey = [stringData substringWithRange:NSMakeRange(range.location + strToFind.length, 8)];
    
    return retKey;
    
}

-(NSString*) replaceKeyForAsx:(NSString*)currentUrl : (NSString*)keyValue {
    NSLog(@"+-+- LoginControllerViewController.m: replaceKeyForAsx");
    NSString *retAsxFile;
    
    NSString *oldKey = [self getKeyValueForAsxFile:currentUrl];
    
    retAsxFile = [currentUrl stringByReplacingOccurrencesOfString:oldKey
                                                       withString:keyValue];
    NSLog(@"new asx file = %@", retAsxFile);
    
    return retAsxFile;
}

-(NSString *) checkForActiveUrl: (NSArray *) streamUrls {
    NSLog(@"+-+- LoginControllerViewController.m: checkForActiveUrl");
    NSString *retUrl;
    
    
    NSLog(@"streamUrls = %@", streamUrls);
    for (NSDictionary *streamUrl in streamUrls) {
        NSLog(@"%@", streamUrl);
        NSString *sUrl = [streamUrl objectForKey:@"href"];
        
        
        retUrl = [sUrl stringByReplacingOccurrencesOfString:@"mms:" withString:@"mmst:"];
        //take the correct stream
        NSRange range = [retUrl rangeOfString:@"nl"];
        if(range.length  > 0) {
            NSLog(@"retUrl = %@", retUrl);
            break;
        }
    }
    
    
    //retUrl = @"mmst://wms1.il.kab.tv/heb-special-9hNYBs4Q";
    //retUrl = @"mmst://wms1.uk.kab.tv/heb-special-9hNYBs4Q";
    //retUrl = @"mmst://wms1.nl.kab.tv/heb-special-9hNYBs4Q";
    //    retUrl = @"mmst://wms1.il.kab.tv/heb";
    //retUrl = @"mmst://wms1.il.kab.tv/heb-special-9hNYBs4Q";
    //retUrl =   @"mms://wms1.uk.kab.tv/heb-special-9hNYBs4Q_medium";
    return retUrl;
}

-(NSArray *)parseAsxFile: (NSString *) asxUrl {
    NSLog(@"+-+- LoginControllerViewController.m: parseAsxFile");
    
    //asxUrl = @"http://streams.kab.tv/heb.asx";
    //asxUrl = @"http://streams.kab.tv/heb-special-9hNYBs4Q.asx";
    //asxUrl = @"http://streams.kab.tv/heb-special-9hNYBs4Q_medium.asx";
    
    NSMutableArray *res = [[NSMutableArray alloc] init];
    NSURL *url = [NSURL URLWithString: asxUrl];
    
    /* have TouchXML parse it into a CXMLDocument */
    CXMLDocument *document = [[CXMLDocument alloc] initWithContentsOfURL:url options:0 error:nil];
    
    NSArray *nodes = NULL;
    //  searching for piglet nodes
    nodes = [document nodesForXPath:@"//ref" error:nil];
    
    for (CXMLElement *node in nodes) {
        NSMutableDictionary *item = [[NSMutableDictionary alloc] init];
        int counter;
        for(counter = 0; counter < [node childCount]; counter++) {
            //  common procedure: dictionary with keys/values from XML node
            [item setObject:[[node childAtIndex:counter] stringValue] forKey:[[node childAtIndex:counter] name]];
        }
        
        //  and here it is - attributeForName! Simple as that.
        [item setObject:[[node attributeForName:@"href"] stringValue] forKey:@"href"];  // <------ this magical arrow is pointing to the area of interest
        
        [res addObject:item];
    }
    
    //   and we print our results
    NSLog(@"%@", res);
    
    
    
    //retVal = @"mmst://wms1.il.kab.tv/heb";
    return res;
}



- (id)fetchSSIDInfo
{
    NSArray *ifs = (__bridge id)CNCopySupportedInterfaces();
    NSLog(@"%s: Supported interfaces: %@", __func__, ifs);
    id info = nil;
    for (NSString *ifnam in ifs) {
        info = (__bridge id)CNCopyCurrentNetworkInfo((__bridge CFStringRef)ifnam);
        NSLog(@"%s: %@ => %@", __func__, ifnam, info);
        if (info && [info count]) {
            break;
        }
        
    }
 
    return info;
}

- (NSString *)currentWifiSSID {
    // Does not work on the simulator.
    NSString *ssid = nil;
    NSArray *ifs = (__bridge_transfer id)CNCopySupportedInterfaces();
    for (NSString *ifnam in ifs) {
        NSDictionary *info = (__bridge_transfer id)CNCopyCurrentNetworkInfo((__bridge CFStringRef)ifnam);
        if (info[@"SSID"]) {
            ssid = info[@"SSID"];
        }
    }
    return ssid;
}

@end
