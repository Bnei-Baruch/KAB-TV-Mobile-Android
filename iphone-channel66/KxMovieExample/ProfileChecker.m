//
//  ProfileChecker 2.h
//  kxmovie
//
//  Created by shidur on 23/09/2025.
//


#import "ProfileChecker.h"

@interface ProfileChecker ()
@property (nonatomic, weak) UIViewController *presenter;
@property (nonatomic, weak, nullable) id<PCUserSub> endSession;
@end

@implementation ProfileChecker

- (instancetype)initWithPresentingViewController:(UIViewController *)presenter {
    self = [super init];
    if (self) {
        _presenter = presenter;
    }
    return self;
}

- (void)checkUserProfileAndShowPopupIfInactiveForUserId:(NSString *)userId
                                              authToken:(nullable NSString *)authToken
                                             endSession:(id<PCUserSub>)endSession
{
    self.endSession = endSession;

    dispatch_async(dispatch_get_global_queue(QOS_CLASS_USER_INITIATED, 0), ^{
        BOOL isActive = [self isUserActiveSynchronousWithUserId:userId authToken:authToken];

        dispatch_async(dispatch_get_main_queue(), ^{
            if (!isActive) {
                [self showInactiveProfilePopup];
            } else {
                NSLog(@"User %@ is active.", userId);
                if ([self.endSession respondsToSelector:@selector(loginSuccesful)]) {
                    [self.endSession loginSuccesful];
                }
            }
        });
    });
}

#pragma mark - Private

- (BOOL)isUserActiveSynchronousWithUserId:(NSString *)userId authToken:(nullable NSString *)authToken {
    // Build URL
    NSString *urlString = [NSString stringWithFormat:@"https://api.kli.one/profile/v1/profile/%@/short",
                                                     [userId stringByAddingPercentEncodingWithAllowedCharacters:[NSCharacterSet URLPathAllowedCharacterSet]]];
    NSURL *url = [NSURL URLWithString:urlString];
    if (!url) {
        NSLog(@"API Error: invalid URL");
        return NO;
    }

    // Build request
    NSMutableURLRequest *req = [NSMutableURLRequest requestWithURL:url];
    req.HTTPMethod = @"GET";
    if (authToken.length > 0) {
        [req setValue:[NSString stringWithFormat:@"Bearer %@", authToken] forHTTPHeaderField:@"Authorization"];
    }
    // [req setValue:@"YOUR_API_KEY" forHTTPHeaderField:@"X-Api-Key"]; // if needed

    __block NSData *respData = nil;
    __block NSURLResponse *resp = nil;
    __block NSError *err = nil;

    dispatch_semaphore_t sema = dispatch_semaphore_create(0);

    [[[NSURLSession sharedSession] dataTaskWithRequest:req
                                     completionHandler:^(NSData * _Nullable data,
                                                         NSURLResponse * _Nullable response,
                                                         NSError * _Nullable error)
    {
        respData = data;
        resp = response;
        err = error;
        dispatch_semaphore_signal(sema);
    }] resume];

    // Wait for completion (since Kotlin used synchronous call inside a background thread)
    dispatch_semaphore_wait(sema, DISPATCH_TIME_FOREVER);

    if (err) {
        NSLog(@"Network Error: %@", err.localizedDescription);
        return NO;
    }

    NSHTTPURLResponse *http = (NSHTTPURLResponse *)resp;
    if (![http isKindOfClass:[NSHTTPURLResponse class]] || http.statusCode < 200 || http.statusCode >= 300) {
        NSLog(@"API Error: %ld - %@", (long)http.statusCode, http.description);
        return NO;
    }

    if (respData.length == 0) {
        NSLog(@"API Error: Empty response body");
        return NO;
    }

    // Parse JSON and extract "active" (accept bool or "true"/"false", at top level or inside "data")
    id json = [NSJSONSerialization JSONObjectWithData:respData options:0 error:&err];
    if (err || !json) {
        NSLog(@"API Error: Failed to parse JSON response - %@", err.localizedDescription);
        return NO;
    }

    // Helper to interpret an "active" value
    BOOL (^parseActive)(id _Nullable) = ^BOOL(id _Nullable v) {
        if (!v || v == [NSNull null]) return NO;
        if ([v isKindOfClass:[NSNumber class]]) {
            return [(NSNumber *)v boolValue];
        }
        if ([v isKindOfClass:[NSString class]]) {
            NSString *s = [(NSString *)v lowercaseString];
            if ([s isEqualToString:@"false"]) return NO;
            if ([s isEqualToString:@"true"])  return YES;
            // Any non-"false" string treated as active per Kotlin logic
            return YES;
        }
        NSLog(@"API Warning: 'active' field is not a boolean or expected string.");
        return NO;
    };

    // Try top-level "active"
    if ([json isKindOfClass:[NSDictionary class]]) {
        NSDictionary *dict = (NSDictionary *)json;

        id activeTop = dict[@"active"];
        if (activeTop != nil) {
            return parseActive(activeTop);
        }

        // Try nested under "data"
        id dataObj = dict[@"data"];
        if ([dataObj isKindOfClass:[NSDictionary class]]) {
            id activeNested = ((NSDictionary *)dataObj)[@"active"];
            if (activeNested != nil) {
                return parseActive(activeNested);
            }
        }

        NSLog(@"API Warning: 'data' object or 'active' field not found in response.");
        return NO;
    }

    NSLog(@"API Error: JSON root is not an object.");
    return NO;
}

- (void)showInactiveProfilePopup {
    if (!self.presenter) {
        NSLog(@"UI Error: presenter is nil, cannot show alert");
        if ([self.endSession respondsToSelector:@selector(requestLogout)]) {
            [self.endSession requestLogout];
        }
        return;
    }

    UIAlertController *alert = [UIAlertController alertControllerWithTitle:@"User permissions"
                                                                   message:@"Please contact help@kli.one"
                                                            preferredStyle:UIAlertControllerStyleAlert];

    __weak typeof(self) weakSelf = self;
    UIAlertAction *ok = [UIAlertAction actionWithTitle:@"OK"
                                                 style:UIAlertActionStyleDefault
                                               handler:^(__unused UIAlertAction * _Nonnull action) {
        __strong typeof(self) self_ = weakSelf;
//        [self_ presenterDismissIfNeededAndLogout];
        id<PCUserSub> endSession = self.endSession;
        if ([endSession respondsToSelector:@selector(requestLogout)]) {
            [endSession requestLogout];
        }
    }];

    [alert addAction:ok];
    alert.modalPresentationStyle = UIModalPresentationOverFullScreen;

    // Present on main thread just to be safe (we already call from main)
    dispatch_async(dispatch_get_main_queue(), ^{
        [self.presenter presentViewController:alert animated:YES completion:nil];
    });
}

- (void)presenterDismissIfNeededAndLogout {
    // No dialog object to dismiss explicitly like Android; just call delegate.
    id<PCUserSub> endSession = self.endSession;
    if ([endSession respondsToSelector:@selector(requestLogout)]) {
        [endSession requestLogout];
    }
}

- (BOOL)deleteAccountWithAuthToken:(NSString *)authToken
                        endSession:(id<PCUserSub>)endSession
{
    self.endSession = endSession;

    NSURL *url = [NSURL URLWithString:@"https://acc.kab.sh/api/self_remove"];
    if (!url) { return YES; }

    NSMutableURLRequest *req = [NSMutableURLRequest requestWithURL:url];
    req.HTTPMethod = @"GET"; // Match Kotlin (no body). Change to DELETE/POST if API expects that.
    [req setValue:[NSString stringWithFormat:@"Bearer %@", authToken] forHTTPHeaderField:@"Authorization"];

    dispatch_async(dispatch_get_global_queue(QOS_CLASS_USER_INITIATED, 0), ^{
        [[[NSURLSession sharedSession] dataTaskWithRequest:req
                                         completionHandler:^(NSData * _Nullable data,
                                                             NSURLResponse * _Nullable response,
                                                             NSError * _Nullable error)
        {
            if (error) {
                NSLog(@"Network Error (Delete Account): %@", error.localizedDescription);
                if ([self.endSession respondsToSelector:@selector(requestLogout)]) {
                    [self.endSession requestLogout];
                }
                return;
            }

            NSHTTPURLResponse *http = (NSHTTPURLResponse *)response;
            if (http.statusCode < 200 || http.statusCode >= 300) {
                NSLog(@"API Error: %ld - %@", (long)http.statusCode, http.description);
                if ([self.endSession respondsToSelector:@selector(requestLogout)]) {
                    [self.endSession requestLogout];
                }
                return;
            }

            NSString *bodyStr = (data ? [[NSString alloc] initWithData:data encoding:NSUTF8StringEncoding] : @"");
            NSLog(@"API Response: %@", bodyStr);
            if ([self.endSession respondsToSelector:@selector(requestLogout)]) {
                [self.endSession requestLogout];
            }
        }] resume];
    });

    // Mirrors Kotlin: returns immediately true after starting the task
    return YES;
}

@end
