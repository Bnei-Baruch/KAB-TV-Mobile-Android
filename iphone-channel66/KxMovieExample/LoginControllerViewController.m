//
//  LoginControllerViewController.m
//  kxmovie
//
//  Created by Igal Avraham on 12/16/12.
//
//

#import "LoginControllerViewController.h"
#import "MenuViewController.h"


@interface LoginControllerViewController () {
    NSString *mOriginalUrl;
}

@end

@implementation LoginControllerViewController
@synthesize mWebview, receivedData, mFromActionview;
- (id)initWithNibName:(NSString *)nibNameOrNil bundle:(NSBundle *)nibBundleOrNil
{
    NSLog(@"LoginControllerViewController :initWithNibName");
    self = [super initWithNibName:nibNameOrNil bundle:nibBundleOrNil];
    if (self) {
        // Custom initialization
    }
    mFromActionview = NO;
    return self;
}

- (void)viewDidLoad
{
    NSLog(@"LoginControllerViewController: viewDidLoad");
    [super viewDidLoad];
    // Do any additional setup after loading the view from its nib.
    
    //googleAnalytic
    self.screenName = @"Login Sviva Tova Screen";
    
    UIBarButtonItem *anotherButton = [[UIBarButtonItem alloc] initWithTitle:@"menu" style:UIBarButtonItemStylePlain target:self action:@selector(openMenu)];
    self.navigationItem.rightBarButtonItem = anotherButton;
    self.navigationItem.hidesBackButton = YES;
    
    mWebview.delegate = self;
    NSString *urlAddress = @"http://internet.kbb1.com";
    //NSString *urlAddress = @"http://www.google.com";
    
    //Create a URL object.
    NSURL *url = [NSURL URLWithString:[urlAddress stringByAddingPercentEscapesUsingEncoding:NSUTF8StringEncoding]];
    
    NSURLCache* theCache = [NSURLCache sharedURLCache];
    [theCache setMemoryCapacity:4 * 1024 * 1024];
    [theCache setDiskCapacity:512*1024];
    
    //URL Requst Object
    NSURLRequest *requestObj = [NSURLRequest requestWithURL:url
                                                cachePolicy:NSURLRequestReturnCacheDataElseLoad timeoutInterval:5];
    // NSURLRequest *requestObj = [NSURLRequest requestWithURL:url ];
    
    //Load the request in the UIWebView.
    [mWebview loadRequest:requestObj];
    //saving password and users
    //http://stackoverflow.com/questions/4772341/is-it-possible-for-a-uiwebview-to-save-and-autofill-previously-entered-form-valu
    
    NSHTTPCookie *cookie = [[NSHTTPCookie alloc] initWithProperties:[[NSDictionary alloc] initWithContentsOfFile:@"cookie"]];
    NSHTTPCookieStorage *cookieJar = [NSHTTPCookieStorage sharedHTTPCookieStorage];
    
    [cookieJar setCookie:cookie];
    
}

- (void)actionSheet:(UIActionSheet *)actionSheet clickedButtonAtIndex:(NSInteger)buttonIndex;
{
    NSLog(@"LoginControllerViewController: clickedButtonAtIndex");
     NSString *url = @"http://kabbalahgroup.info/internet/events/render_event_response?locale=he&source=stream_container&type=update_presets&timestamp=2011-11-25+13:29:53+UTC&stream_preset_id=3&flash=true&wmv=true";
    NSString* keyData = [self getKeyDataFromUrl:url];
    switch (buttonIndex) {
        case 0:
        {
            //video
            //http://mobile.kbb1.com/kab_channel/sviva_tova/jsonresponseexample.json
            //http://kabbalahgroup.info/internet/events/render_event_response?locale=he&source=stream_container&type=update_presets&timestamp=2011-11-25+13:29:53+UTC&stream_preset_id=3&flash=true&wmv=true
            //parse for langugae and select from streams
           url = @"http://mobile.kbb1.com/kab_channel/sviva_tova/jsonresponseexample.json";
            NSDictionary* langjsonData = [self getJsonDataFromUrl:url];
           
            
            if ([self checkIsActive: keyData])
            {
                if (langjsonData && keyData) {
                    NSString *keyToReplace = [self getKeyValueForAsxFile: keyData];
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
                        }
                    }
                    if (asxUrlToPlay) {
                        NSArray* streamUrls = [self parseAsxFile :asxUrlToPlay];
                        NSString *urlToPlay = [self checkForActiveUrl: streamUrls];
                        NSLog(@"urlToPlay = %@", urlToPlay );
                        if (urlToPlay) {
                            //googleAnalytic
                            NSString *analyticPrm = [NSString stringWithFormat:@"%@ - %@", @"sviva Tova Video", urlToPlay];
                            self.screenName = analyticPrm;
//                            KxMovieViewController *vc = [KxMovieViewController movieViewControllerWithContentPath:urlToPlay];
//                            [self presentViewController:vc animated:YES completion:nil];
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
        case 1:
        {
            if ([self checkIsActive: keyData])
            {
            //googleAnalytic
            NSString *analyticPrm = [NSString stringWithFormat:@"%@ - %@", @"sviva Tova Audio", actionSheet];
            self.screenName = analyticPrm;
            //Audio
            mFromActionview = YES;
            [mWebview loadRequest:[NSURLRequest requestWithURL:[NSURL URLWithString:actionSheet.accessibilityValue]]];
            
            NSLog(@"Audio actionSheet.accessibilityValue = %@", actionSheet.accessibilityValue);
            NSLog(@"Audio picked");
            }
            else { // is_active = false
                UIAlertView *noBrodMessage = [[UIAlertView alloc] initWithTitle: @"" message: @"Sorry No Broadcast"  delegate: self cancelButtonTitle: @"Ok" otherButtonTitles: nil];
                [noBrodMessage show];
            }

            
            break;
        }
        default:
            break;
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
-(NSString*) replaceKeyForAsx:(NSString*)currentUrl : (NSString*)keyValue {
    NSLog(@"+-+- LoginControllerViewController.m: replaceKeyForAsx");
    NSString *retAsxFile;
    
    NSString *oldKey = [self getKeyValueForAsxFile:currentUrl];
    
    retAsxFile = [currentUrl stringByReplacingOccurrencesOfString:oldKey
                                                withString:keyValue];
    NSLog(@"new asx file = %@", retAsxFile);
    
    return retAsxFile;
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

-(void) openMenu {
    
    MenuViewController *menu = [[MenuViewController alloc] init];
    menu.fromWhere = 2;
    [self.navigationController pushViewController:menu animated:YES];
    
    
}

#pragma mark webView methods



- (BOOL)webView:(UIWebView *)webView shouldStartLoadWithRequest:(NSURLRequest *)request navigationType:(UIWebViewNavigationType)navigationType
{
    NSLog(@"shouldStartLoadWithRequest");
    NSRange isRange = [request.URL.absoluteString rangeOfString:@"mp3" options:NSCaseInsensitiveSearch];
    NSLog(@"request = %@", request );
    NSUserDefaults *defaults = [NSUserDefaults standardUserDefaults];
    
    if(navigationType == UIWebViewNavigationTypeFormSubmitted){
        
        //grab the data from the page
        NSString *username = [webView stringByEvaluatingJavaScriptFromString: @"document.getElementById('user_email').value"];
        NSString *password = [webView stringByEvaluatingJavaScriptFromString: @"document.getElementById('user_password').value"];
                //store values locally
        [defaults setObject:username forKey:@"username"];
        [defaults setObject:password forKey:@"password"];
        [defaults synchronize];
    }
    //if([request.URL.absoluteString isEqualToString:@"http://icecast.kab.tv/live1-heb-574bcfd5.mp3"])
    NSString *url = @"http://kabbalahgroup.info/internet/events/render_event_response?locale=he&source=stream_container&type=update_presets&timestamp=2011-11-25+13:29:53+UTC&stream_preset_id=3&flash=true&wmv=true";
    NSString* keyData = [self getKeyDataFromUrl:url];
    
    if(isRange.location!=NSNotFound && !mFromActionview)
    {
        if ([self checkIsActive: keyData])
        {
        UIActionSheet *select = [[UIActionSheet alloc]initWithTitle:@"Stream type" delegate:self cancelButtonTitle:@"Cancel" destructiveButtonTitle:nil otherButtonTitles:@"Video",@"Audio", nil];
        select.accessibilityValue = request.URL.absoluteString;
        [select showInView:self.mWebview];
        mFromActionview = NO;
        
        
        return false;
        }
        else { // is_active = false
            UIAlertView *noBrodMessage = [[UIAlertView alloc] initWithTitle: @"" message: @"Sorry No Broadcast"  delegate: self cancelButtonTitle: @"Ok" otherButtonTitles: nil];
            [noBrodMessage show];
            return false;
        }

        
    }
    
    //http://kabbalahgroup.info/internet/en/mobile
    
    
    return true;
}

- (void)webViewDidFinishLoad:(UIWebView *)webView
{
    NSLog(@"webViewDidFinishLoad");
    NSLog(@"webView.request = %@", webView.request);
    //yuval
    if (mFromActionview) {
        mFromActionview = NO;
    }
    
    
    //yuval
    
    NSUserDefaults *defaults = [NSUserDefaults standardUserDefaults];
    NSString *username = [webView stringByEvaluatingJavaScriptFromString: @"document.getElementById('user_email').value"];
    NSString *password = [webView stringByEvaluatingJavaScriptFromString: @"document.getElementById('user_password').value"];
    if([username length]==0)
        {
            //get the user name and password saved
            username =    [defaults valueForKey:@"username"];
            password =    [defaults valueForKey:@"password"];
            if([username length] >0)
            {
                NSString*  jScriptString1 = [NSString  stringWithFormat:@"document.getElementById('user_email').value='%@'", username];
                //username is the id for username field in Login form
                
                NSString*  jScriptString2 = [NSString stringWithFormat:@"document.getElementById('user_password').value='%@'", password];
                //here password is the id for password field in Login Form
                //Now Call The Javascript for entring these Credential in login Form
                [webView stringByEvaluatingJavaScriptFromString:jScriptString1];
                
                [webView stringByEvaluatingJavaScriptFromString:jScriptString2];
                //Further if you want to submit login Form Automatically the you may use below line
                
                 [webView stringByEvaluatingJavaScriptFromString:@"document.forms['login_form'].submit();"];
                // here 'login_form' is the id name of LoginForm
            }
        }

}


- (void)webView:(UIWebView *)webView didFailLoadWithError:(NSError *)error
{
    NSLog(@"Error : %@",error);
}



#pragma mark NSURLConnection methods

- (void)connection:(NSURLConnection *)connection didReceiveResponse:(NSURLResponse *)response
{
    NSLog(@"didReceiveResponse");
    // This method is called when the server has determined that it
    // has enough information to create the NSURLResponse.
	
    // It can be called multiple times, for example in the case of a
    // redirect, so each time we reset the data.
	
    // receivedData is an instance variable declared elsewhere.
    
    [receivedData setLength:0];
}

- (void)connection:(NSURLConnection *)connection didReceiveData:(NSData *)data
{
     NSLog(@"didReceiveData");
    // Append the new data to receivedData.
    // receivedData is an instance variable declared elsewhere.
    
    [receivedData appendData:data];
}

- (void)connection:(NSURLConnection *)connection didFailWithError:(NSError *)error
{
    NSLog(@"didFailWithError");
    // release the connection, and the data object
    // receivedData is declared as a method instance elsewhere
    
   
    
    // inform the user
    UIAlertView *didFailWithErrorMessage = [[UIAlertView alloc] initWithTitle: @"NSURLConnection " message: @"didFailWithError"  delegate: self cancelButtonTitle: @"Ok" otherButtonTitles: nil];
    [didFailWithErrorMessage show];
    
    //inform the user
    NSLog(@"Connection failed! Error - %@ %@",
          [error localizedDescription],
          [[error userInfo] objectForKey:NSErrorFailingURLStringKey]);
}

- (void)connectionDidFinishLoading:(NSURLConnection *)connection
{
    NSLog(@"connectionDidFinishLoading");


    // do something with the data
    // receivedData is declared as an instance variable elsewhere
    // in this example, convert data (from plist) to a string and then to a dictionary
    
    NSString *dataString = [[NSString alloc] initWithData: receivedData  encoding:NSUTF8StringEncoding];
                            NSMutableDictionary *dictionary = [dataString propertyList];
    
    NSLog(@"dictionary = %@", dictionary);
                            
                            
                            //store data in singleton class for use by other parts of the program
//                            SingletonObject *mySharedObject = [SingletonObject sharedSingleton];
//                            mySharedObject.aDictionary = dictionary;
    
                            //alert the user
                            //alert the user
//                            NSString *message = [[NSString alloc] initWithFormat:@"Succeeded! Received %d bytes of data",[receivedData length]];
//                                                 NSLog(message);
//                                                 NSLog(@"Dictionary:\n%@",dictionary);
//                                                 UIAlertView *finishedLoadingMessage = [[UIAlertView alloc] initWithTitle: @"NSURLConnection " message:message  delegate: self cancelButtonTitle: @"Ok" otherButtonTitles: nil];
//                                                 [finishedLoadingMessage show];
//                                                 
//                                                 
                                                 // release the connection, and the data object
    
}

                                                 
                                                 

- (void) dumpCookies:(NSString *)msgOrNil {
    NSLog(@"dumpCookies");
    NSMutableString *cookieDescs    = [[NSMutableString alloc] init];
    NSHTTPCookie *cookie;
    NSHTTPCookieStorage *cookieJar = [NSHTTPCookieStorage sharedHTTPCookieStorage];
    for (cookie in [cookieJar cookies]) {
        [cookieDescs appendString:[self cookieDescription:cookie]];
    }
    NSLog(@"------ [Cookie Dump: %@] ---------\n%@", msgOrNil, cookieDescs);
    NSLog(@"----------------------------------");
}

- (NSString *) cookieDescription:(NSHTTPCookie *)cookie {
    NSLog(@"cookieDescription");
    if([[cookie name] isEqualToString:@"_simulator_session"])
       [[cookie properties] writeToFile:@"cookie" atomically:YES];
    NSMutableString *cDesc      = [[NSMutableString alloc] init];
    [cDesc appendString:@"[NSHTTPCookie]\n"];
    [cDesc appendFormat:@"  name            = %@\n",            [[cookie name] stringByReplacingPercentEscapesUsingEncoding:NSUTF8StringEncoding]];
    [cDesc appendFormat:@"  value           = %@\n",            [[cookie value] stringByReplacingPercentEscapesUsingEncoding:NSUTF8StringEncoding]];
    [cDesc appendFormat:@"  domain          = %@\n",            [cookie domain]];
    [cDesc appendFormat:@"  path            = %@\n",            [cookie path]];
    [cDesc appendFormat:@"  expiresDate     = %@\n",            [cookie expiresDate]];
    [cDesc appendFormat:@"  sessionOnly     = %d\n",            [cookie isSessionOnly]];
    [cDesc appendFormat:@"  secure          = %d\n",            [cookie isSecure]];
    [cDesc appendFormat:@"  comment         = %@\n",            [cookie comment]];
    [cDesc appendFormat:@"  commentURL      = %@\n",            [cookie commentURL]];
    [cDesc appendFormat:@"  version         = %u\n",            [cookie version]];
    
    //  [cDesc appendFormat:@"  portList        = %@\n",            [cookie portList]];
    //  [cDesc appendFormat:@"  properties      = %@\n",            [cookie properties]];
    
    return cDesc;
}

- (void) viewWillDisappear:(BOOL)animated
{
    NSLog(@"viewWillDisappear");
    [self dumpCookies:nil];
}

- (void)didReceiveMemoryWarning
{
    [super didReceiveMemoryWarning];
    // Dispose of any resources that can be recreated.
}



@end
