//
//  SvivaTovaLoginController.m
//  kxmovie
//
//  Created by Asher on 4/29/13.
//
//

#import "SvivaTovaLoginController.h"

#define kSuccesfulLoginIndicator @"allow_archived_broadcasts"
#define kFailedLoginIndicator @"אימייל או סיסמא שגויים"
#define kSvivaTovaLoginURL @"http://kabbalahgroup.info/internet/he/users/login"
#define kSvivaTovaLoginURLrest @"http://kabbalahgroup.info/internet/api/v1/tokens.json"
@class FBSDKAccessToken;

@implementation SvivaTovaLoginController

@synthesize strReply,allHeaderFields,recievedData;
@synthesize serverCookies;
@synthesize loginRequestResponseTarget;
@synthesize loginSuccesResponseSelector;
@synthesize loginfailedResponseSelector;


- (void)loginWithUsername:(NSString *)username
              andPassword:(NSString *)password
          andLocalization:(NSString *)localization
                   target:(NSObject *)target
            successAction:(SEL)successSEL
               failAction:(SEL)failSEL {

    
    if (target && successSEL) {
        self.loginRequestResponseTarget = target;
        self.loginSuccesResponseSelector = successSEL;
    }
    else {
        LogErr(@"Unable to request service since target Or success Selector are missing");
    }
    if (failSEL) {
        self.loginfailedResponseSelector = failSEL;
    } else {
        LogErr(@"Unable to request service since target Or success Selector are missing");
    }
    
    
    
	NSHTTPCookieStorage *cookieStorage = [NSHTTPCookieStorage sharedHTTPCookieStorage];
	[cookieStorage setCookieAcceptPolicy:NSHTTPCookieAcceptPolicyAlways];
	
	NSArray *mcookies = [cookieStorage cookies];
	for (NSString *c in mcookies) {
		//LogDebug(@"new cookie: = %@",c);
	}
	
	
	NSHTTPURLResponse* response;
	NSData* dataReply;
	NSURL* url = [NSURL URLWithString:kSvivaTovaLoginURLrest];
	
    
    
    
    /*
     GET /internet/he/users/login HTTP/1.1
     Host: kabbalahgroup.info
     Connection: keep-alive
     Accept: text/html,application/xhtml+xml,application/xml;q=0.9,* / *;q=0.8
     User-Agent: Mozilla/5.0 (Macintosh; Intel Mac OS X 10_8_3) AppleWebKit/537.31 (KHTML, like Gecko) Chrome/26.0.1410.65 Safari/537.31
     Accept-Encoding: gzip,deflate,sdch
     Accept-Language: en-US,en;q=0.8
     Accept-Charset: ISO-8859-1,utf-8;q=0.7,*;q=0.3
     If-None-Match: "d6446e8ac6a98aaee5a6e74a706fa81e"
     */
	NSMutableURLRequest *req = [NSMutableURLRequest requestWithURL:url];
	[req setHTTPMethod:@"POST"];
    [req setValue:@"application/json" forHTTPHeaderField:@"Content-Type"];
//	[req setValue:@"keep-alive" forHTTPHeaderField:@"Connection"];
//    [req setValue:@"gzip,deflate,sdch" forHTTPHeaderField:@"Accept-Encoding"];
//	[req setValue:@"kabbalahgroup.info" forHTTPHeaderField:@"Host"];
//	
//	[req setValue: @"Mozilla/5.0 (iPhone; U; CPU iPhone OS 3_0 like Mac OS X; en-us) AppleWebKit/528.18 (KHTML, like Gecko) Version/4.0 Mobile/7A341 Safari/528.16" forHTTPHeaderField: @"User-Agent"];
	
    NSDictionary * body = [[NSDictionary alloc]initWithObjectsAndKeys:password,@"password",username,@"email",nil];
    
    NSError *error;
    NSData *postdata = [NSJSONSerialization dataWithJSONObject:body options:0 error:&error];
    [req setHTTPBody:postdata];
	LogDebug(@"%@",req);
    
    
    
    [UIApplication sharedApplication].networkActivityIndicatorVisible = YES;
    
	
	//dataReply = [NSURLConnection sendSynchronousRequest:req returningResponse:&response error:&error];
	//strReply = [[NSString alloc]initWithData:dataReply encoding:NSUTF8StringEncoding];
	//LogDebug(@"%@",strReply);
    
    
    NSURLConnection* conn = [NSURLConnection connectionWithRequest:req delegate:self];

    if (conn){
        recievedData = [NSMutableData new];
        LogWarn(@"Connection to server succeded!");
    }
    else{
        LogErr(@"Can't download data from server!");
        // TODO: return error here.
    }
    
    
    
//	allHeaderFields = (NSMutableDictionary*)[ response allHeaderFields];
//	for (id key in allHeaderFields) {
//		LogDebug(@"key: %@, value: %@", key, [allHeaderFields objectForKey:key]);
//	}
//	LogDebug(@"%d" ,[response statusCode]);
//	statusCode = [response statusCode];
//	
//	/*
//     allHeaderFields = (NSMutableDictionary*)[ response allHeaderFields];
//     for (NSHTTPCookie *cookie in cookieStorage.cookies) {
//      NSString *tmp = [NSString stringWithFormat:@"%@=%@; ",[cookie valueForKey:@"name"],[cookie valueForKey:@"value"] ];
//      LogDebug(@"cookie saved: %@ ",tmp);
//     }
//     */
//    
//    
//    // Post login data to SvivaTova & try to login:
//    [self postLoginDataWithUsername:username andPassword:password andLocalization:localization];

}


- (void)loginWithFBToken:(NSString *)token
          andLocalization:(NSString *)localization
                   target:(NSObject *)target
            successAction:(SEL)successSEL
               failAction:(SEL)failSEL {
    
    
    if (target && successSEL) {
        self.loginRequestResponseTarget = target;
        self.loginSuccesResponseSelector = successSEL;
    }
    else {
        LogErr(@"Unable to request service since target Or success Selector are missing");
    }
    if (failSEL) {
        self.loginfailedResponseSelector = failSEL;
    } else {
        LogErr(@"Unable to request service since target Or success Selector are missing");
    }
    
    
    
    NSHTTPCookieStorage *cookieStorage = [NSHTTPCookieStorage sharedHTTPCookieStorage];
    [cookieStorage setCookieAcceptPolicy:NSHTTPCookieAcceptPolicyAlways];
    
    NSArray *mcookies = [cookieStorage cookies];
    for (NSString *c in mcookies) {
        //LogDebug(@"new cookie: = %@",c);
    }
    
    
    NSHTTPURLResponse* response;
    NSData* dataReply;
    NSURL* url = [NSURL URLWithString:kSvivaTovaLoginURLrest];
    
    
    
    
    /*
     GET /internet/he/users/login HTTP/1.1
     Host: kabbalahgroup.info
     Connection: keep-alive
     Accept: text/html,application/xhtml+xml,application/xml;q=0.9,* / *;q=0.8
     User-Agent: Mozilla/5.0 (Macintosh; Intel Mac OS X 10_8_3) AppleWebKit/537.31 (KHTML, like Gecko) Chrome/26.0.1410.65 Safari/537.31
     Accept-Encoding: gzip,deflate,sdch
     Accept-Language: en-US,en;q=0.8
     Accept-Charset: ISO-8859-1,utf-8;q=0.7,*;q=0.3
     If-None-Match: "d6446e8ac6a98aaee5a6e74a706fa81e"
     */
    NSMutableURLRequest *req = [NSMutableURLRequest requestWithURL:url];
    [req setHTTPMethod:@"POST"];
    [req setValue:@"application/json" forHTTPHeaderField:@"Content-Type"];
    //	[req setValue:@"keep-alive" forHTTPHeaderField:@"Connection"];
    //    [req setValue:@"gzip,deflate,sdch" forHTTPHeaderField:@"Accept-Encoding"];
    //	[req setValue:@"kabbalahgroup.info" forHTTPHeaderField:@"Host"];
    //
    //	[req setValue: @"Mozilla/5.0 (iPhone; U; CPU iPhone OS 3_0 like Mac OS X; en-us) AppleWebKit/528.18 (KHTML, like Gecko) Version/4.0 Mobile/7A341 Safari/528.16" forHTTPHeaderField: @"User-Agent"];
    
    NSDictionary * body = [[NSDictionary alloc]initWithObjectsAndKeys:token ,@"fb_token",nil];
    
    NSError *error;
    NSData *postdata = [NSJSONSerialization dataWithJSONObject:body options:0 error:&error];
    [req setHTTPBody:postdata];
    LogDebug(@"%@",req);
    
    
    
    [UIApplication sharedApplication].networkActivityIndicatorVisible = YES;
    
    
    //dataReply = [NSURLConnection sendSynchronousRequest:req returningResponse:&response error:&error];
    //strReply = [[NSString alloc]initWithData:dataReply encoding:NSUTF8StringEncoding];
    //LogDebug(@"%@",strReply);
    
    
    NSURLConnection* conn = [NSURLConnection connectionWithRequest:req delegate:self];
    
    if (conn){
        recievedData = [NSMutableData new];
        LogWarn(@"Connection to server succeded!");
    }
    else{
        LogErr(@"Can't download data from server!");
        // TODO: return error here.
    }
    
    
    
    //	allHeaderFields = (NSMutableDictionary*)[ response allHeaderFields];
    //	for (id key in allHeaderFields) {
    //		LogDebug(@"key: %@, value: %@", key, [allHeaderFields objectForKey:key]);
    //	}
    //	LogDebug(@"%d" ,[response statusCode]);
    //	statusCode = [response statusCode];
    //
    //	/*
    //     allHeaderFields = (NSMutableDictionary*)[ response allHeaderFields];
    //     for (NSHTTPCookie *cookie in cookieStorage.cookies) {
    //      NSString *tmp = [NSString stringWithFormat:@"%@=%@; ",[cookie valueForKey:@"name"],[cookie valueForKey:@"value"] ];
    //      LogDebug(@"cookie saved: %@ ",tmp);
    //     }
    //     */
    //
    //
    //    // Post login data to SvivaTova & try to login:
    //    [self postLoginDataWithUsername:username andPassword:password andLocalization:localization];
    
}


- (void) postLoginDataWithUsername:(NSString *)username
                       andPassword:(NSString *)password
                   andLocalization:(NSString *)localization {
    
	NSHTTPCookieStorage *cookieStorage = [NSHTTPCookieStorage sharedHTTPCookieStorage];
	[cookieStorage setCookieAcceptPolicy:NSHTTPCookieAcceptPolicyAlways];
	
	
	NSMutableArray *params = [NSMutableArray arrayWithCapacity:0];
	
    [params addObject:@"utf8=%E2%9C%93"];
    [params addObject:@"authenticity_token=F78FFKLAanNNgR131e2Oh1XST99TYp+jjTcvjyGxqs4="];
    [params addObject:[NSString stringWithFormat:@"user[email]=%@",username]];
    [params addObject:[NSString stringWithFormat:@"user[password]=%@",password]];
    [params addObject:@"user[remember_me]=0"];
    [params addObject:@"commit=כניסה למערכת"];
    
	
	LogDebug(@"%@",params);
    
	NSData *body = [[params componentsJoinedByString:@"&"] dataUsingEncoding:NSUTF8StringEncoding];
	
	NSMutableURLRequest *req = [NSMutableURLRequest requestWithURL:[NSURL URLWithString:kSvivaTovaLoginURL] cachePolicy:NSURLRequestUseProtocolCachePolicy timeoutInterval:60.0];
	
    
	[req setHTTPMethod:@"POST"];
	[req setValue:@"application/x-www-form-urlencoded" forHTTPHeaderField:@"Content-Type"];
	[req setValue:kSvivaTovaLoginURL forHTTPHeaderField:@"Referer"];
	[req setValue:@"keep-alive" forHTTPHeaderField:@"Connection"];
    [req setValue:@"gzip,deflate,sdch" forHTTPHeaderField:@"Accept-Encoding"];
	[req setValue:@"application/x-www-form-urlencoded" forHTTPHeaderField:@"Content-Type"];
	[req setValue:@"kabbalahgroup.info" forHTTPHeaderField:@"Host"];
	[req setValue: @"Mozilla/5.0 (iPhone; U; CPU iPhone OS 3_0 like Mac OS X; en-us) AppleWebKit/528.18 (KHTML, like Gecko) Version/4.0 Mobile/7A341 Safari/528.16" forHTTPHeaderField: @"User-Agent"];
	[req setValue:self.serverCookies forHTTPHeaderField:@"Cookie"];
	[req setValue:[NSString stringWithFormat:@"%d", body.length] forHTTPHeaderField:@"Content-Length"];
	[req setHTTPBody:body];
	
	LogDebug(@"postLoginData: %@",req);
	
	
	NSURLConnection* conn = [NSURLConnection connectionWithRequest:req delegate:self];
	
	
	if (conn){
		recievedData = [NSMutableData new];
		LogWarn(@"Connection to server succeded!");
	}
	else{
		LogErr(@"Can't download data from server!");
        // TODO: return error here.
	}
	
	
	
	
	
}



- (void)connection:(NSURLConnection *)connection didReceiveResponse:(NSURLResponse *)response {
    
    //LogDebug(@"did recieve response");
	//LogDebug(@"%d" ,[(NSHTTPURLResponse*)response statusCode]);
	allHeaderFields = (NSMutableDictionary*)[(NSHTTPURLResponse*)response allHeaderFields];
	
	for (id key in allHeaderFields) {
		LogDebug(@"key: %@, value: %@", key, [allHeaderFields objectForKey:key]);
	}
	
	
    
	NSHTTPURLResponse *httpResponse = (NSHTTPURLResponse *) response;
	NSHTTPCookieStorage *cookieStorage = [NSHTTPCookieStorage sharedHTTPCookieStorage];
	[cookieStorage setCookieAcceptPolicy:NSHTTPCookieAcceptPolicyAlways];
	allHeaderFields = (NSMutableDictionary*)[ httpResponse allHeaderFields];
	if (!self.serverCookies) self.serverCookies = [[NSString alloc] init];
    
	for (NSHTTPCookie *cookie in [cookieStorage cookies]) {
		if (![[cookie valueForKey:@"name"] isEqualToString:@"DSSIGNIN"]) {
            LogDebug(@"cookied: %@",cookie);
            NSString *tmp = [NSString stringWithFormat:@"%@=%@; ",[cookie valueForKey:@"name"],[cookie valueForKey:@"value"] ];
            self.serverCookies = [self.serverCookies stringByAppendingString:tmp];
            LogDebug(@"cookied: %@ ",tmp);
		}
	}
	LogDebug(@"Server Cookie: inherited serverCookie = %@",self.serverCookies);
}


- (void)connection:(NSURLConnection *)connection didReceiveData:(NSData *)data {
    
    [recievedData appendData:data];
    
	NSString *strReplyy = [[NSString alloc]initWithData:recievedData encoding:NSUTF8StringEncoding];
	LogDebug(@"%@",strReplyy);
	//	LogDebug(@"data  = %@",[[NSString alloc]initWithData:data encoding:NSUTF8StringEncoding]);
	
    NSRange succesLoginStrRange = [strReplyy rangeOfString:kSuccesfulLoginIndicator];
    
    NSError *jsonError;
    NSData *objectData = [strReplyy dataUsingEncoding:NSUTF8StringEncoding];
    NSDictionary *json = [NSJSONSerialization JSONObjectWithData:objectData
                                                         options:NSJSONReadingMutableContainers
                                                           error:&jsonError];
    
    if([json valueForKey:kSuccesfulLoginIndicator])
    {
    if(succesLoginStrRange.length !=  0){
        LogWarn(@"\n\n\n\n\n\t\t\t********************************LOGIN SUCCESS ! ! !**************************************\n\n\n\n");
        
        if (self.loginSuccesResponseSelector && self.loginRequestResponseTarget) {
            if ([self.loginRequestResponseTarget respondsToSelector:self.loginSuccesResponseSelector]) {
                [self.loginRequestResponseTarget performSelector:self.loginSuccesResponseSelector withObject:nil];
            }
        }
    }
    }
else
{
   
        LogErr(@"\n\n\n\n\n\t\t\t********************************LOGIN FAILIURE ! ! !**************************************\n\n\n\n");
        if (self.loginfailedResponseSelector && self.loginRequestResponseTarget) {
            if ([self.loginRequestResponseTarget respondsToSelector:self.loginfailedResponseSelector]) {
                [self.loginRequestResponseTarget performSelector:self.loginfailedResponseSelector withObject:nil];
            }
        }
    
}
    
}


- (void)connection:(NSURLConnection *)connection didFailWithError:(NSError *)error {
    LogDebug(@"");
	
	UIAlertView* alert = [[UIAlertView alloc]init];
	alert.title = @"הבקשה נכשלה";
	alert.message = @"אין אפשרות ליצור קשר עם השרת";
	[alert addButtonWithTitle:@"אישור"];
	[alert show];
	//[self reset] - tomorrow..
    
    [UIApplication sharedApplication].networkActivityIndicatorVisible = NO;
    if (self.loginfailedResponseSelector && self.loginRequestResponseTarget) {
        if ([self.loginRequestResponseTarget respondsToSelector:self.loginfailedResponseSelector]) {
            [self.loginRequestResponseTarget performSelector:self.loginfailedResponseSelector withObject:nil];
        }
    }
}


- (void)connectionDidFinishLoading:(NSURLConnection *)connection {
    
    LogDebug(@"%u", [recievedData length]);
    
	NSString *errDetectionReply = [[NSString alloc]initWithData: recievedData encoding:NSUTF8StringEncoding];
	NSRange errDetectionRange = [errDetectionReply rangeOfString:@"משהו לא בסדר"];

	if(errDetectionRange.length !=  0) {
		UIAlertView *alert = [[UIAlertView alloc] init];
		alert.title = @"משהו לא בסדר";
		[alert addButtonWithTitle:@"אישור"];
		[alert show];
        
		return;
	}
	
	//POST login details:
	NSString *strReplyy = [[NSString alloc]initWithData: recievedData encoding:NSUTF8StringEncoding];
	LogDebug(@"%@",strReplyy);
    
    /*
	NSRange range = [strReplyy rangeOfString:@"<title>סימן מזהה לדף המתאים</title>"];
	if(range.length !=  0) {
		LogDebug(@"\n\n\n\n\n\t\t\t********************************LOGIN PAGE DETECTED TRYING TO LOG IN**************************************\n\n\n\n");
	}
    */

    [UIApplication sharedApplication].networkActivityIndicatorVisible = NO;
    
}



- (NSURLRequest *)connection:(NSURLConnection *)connection willSendRequest:(NSURLRequest *)request redirectResponse:(NSURLResponse *)redirectResponse

{
	
	LogDebug(@"%@ - REDIRECTED...",request);
	NSHTTPURLResponse *httpResponse = (NSHTTPURLResponse *) redirectResponse;
	statusCode = [httpResponse statusCode];
	LogDebug(@"statusCode: %d",statusCode);
	LogDebug(@"httpResponse: %@",httpResponse);
    return request;
	
}


































@end
