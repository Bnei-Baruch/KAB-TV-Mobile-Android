//
//  KeycloakController.m
//  KxMovieExample
//
//  Created by shidur on 25/05/2025.
//

#import "KeycloakController.h"



#import "AppAuth.h"
#import "AppDelegate.h"

typedef void (^PostRegistrationCallback)(OIDServiceConfiguration *configuration,
                                         OIDRegistrationResponse *registrationResponse);

/*! @brief The OIDC issuer from which the configuration will be discovered.
 */
static NSString *const kIssuer = @"https://accounts.kab.info/auth/realms/main/";

/*! @brief The OAuth client ID.
    @discussion For client configuration instructions, see the README.
        Set to nil to use dynamic registration with this example.
    @see https://github.com/openid/AppAuth-iOS/blob/master/Examples/Example-iOS_ObjC/README.md
 */
static NSString *const kClientID = @"kabbalahtv";

/*! @brief The OAuth redirect URI for the client @c kClientID.
    @discussion For client configuration instructions, see the README.
    @see https://github.com/openid/AppAuth-iOS/blob/master/Examples/Example-iOS_ObjC/README.md
 */
static NSString *const kRedirectURI = @"kabbalahtv.mobile://login-callback";

/*! @brief NSCoding key for the authState property.
 */
static NSString *const kAppAuthExampleAuthStateKey = @"authState";


@interface KeycloakController () <OIDAuthStateChangeDelegate, OIDAuthStateErrorDelegate>
@end


NS_ASSUME_NONNULL_BEGIN

@implementation KeycloakController


-(instancetype)initForLogout{
    if ((self = [super initWithNibName:nil bundle:nil])) {
        self.logout = true;
       }
       return self;
}

-(instancetype)init{
    if ((self = [super initWithNibName:nil bundle:nil])) {
        self.logout = false;
       }
       return self;
}

- (void)verifyConfig {
#if !defined(NS_BLOCK_ASSERTIONS)

  // The example needs to be configured with your own client details.
  // See: https://github.com/openid/AppAuth-iOS/blob/master/Examples/Example-iOS_ObjC/README.md

  NSAssert(![kIssuer isEqualToString:@"https://issuer.example.com"],
           @"Update kIssuer with your own issuer. "
            "Instructions: https://github.com/openid/AppAuth-iOS/blob/master/Examples/Example-iOS_ObjC/README.md");

  NSAssert(![kClientID isEqualToString:@"YOUR_CLIENT_ID"],
           @"Update kClientID with your own client ID. "
            "Instructions: https://github.com/openid/AppAuth-iOS/blob/master/Examples/Example-iOS_ObjC/README.md");

  NSAssert(![kRedirectURI isEqualToString:@"com.example.app:/oauth2redirect/example-provider"],
           @"Update kRedirectURI with your own redirect URI. "
            "Instructions: https://github.com/openid/AppAuth-iOS/blob/master/Examples/Example-iOS_ObjC/README.md");

  // verifies that the custom URI scheme has been updated in the Info.plist
  NSArray __unused* urlTypes =
      [[NSBundle mainBundle] objectForInfoDictionaryKey:@"CFBundleURLTypes"];
  NSAssert([urlTypes count] > 0, @"No custom URI scheme has been configured for the project.");
  NSArray *urlSchemes =
      [(NSDictionary *)[urlTypes objectAtIndex:0] objectForKey:@"CFBundleURLSchemes"];
  NSAssert([urlSchemes count] > 0, @"No custom URI scheme has been configured for the project.");
  NSString *urlScheme = [urlSchemes objectAtIndex:0];

  NSAssert(![urlScheme isEqualToString:@"com.example.app"],
           @"Configure the URI scheme in Info.plist (URL Types -> Item 0 -> URL Schemes -> Item 0) "
            "with the scheme of your redirect URI. Full instructions: "
            "https://github.com/openid/AppAuth-iOS/blob/master/Examples/Example-iOS_ObjC/README.md");

#endif // !defined(NS_BLOCK_ASSERTIONS)
}
- (void)saveState {
  // for production usage consider using the OS Keychain instead
  NSUserDefaults* userDefaults = [[NSUserDefaults alloc] initWithSuiteName:@"group.net.openid.appauth.Example"];
    NSData *archivedAuthState = [NSKeyedArchiver archivedDataWithRootObject:self._authState];
  [userDefaults setObject:archivedAuthState
                   forKey:kAppAuthExampleAuthStateKey];
  [userDefaults synchronize];
}

- (void)clearLocalSessionOnly {
  // Forget AppAuth state (local sign-out)
  self.authState = nil;

  // OPTIONAL: revoke tokens if your OP supports RFC 7009
  // POST to https://issuer/oauth/revoke with token & client auth

  // Notify UI you’re signed out...
}

- (void)logoutCall {
    
    NSURL *issuer = [NSURL URLWithString:kIssuer];
  // 1) Discover config (includes end_session_endpoint if supported)
  [OIDAuthorizationService discoverServiceConfigurationForIssuer:issuer
                                                      completion:^(OIDServiceConfiguration * _Nullable configuration, NSError * _Nullable error) {
      if (!configuration || error) {
          NSLog(@"Discovery failed: %@", error);
          [self clearLocalSessionOnly];
          return;
      }
      
      // Need the last ID Token as id_token_hint
      NSString *idToken = self._authState.lastTokenResponse.idToken;
      if (!idToken) { // fall back to local clear if we don't have one
          [self clearLocalSessionOnly];
          return;
      }
      
      // Your *registered* post-logout redirect URI (must be allowed by the OP)
      NSURL *postLogout = [NSURL URLWithString:@"kabbalahtv.mobile://login-callback"];
      
      OIDEndSessionRequest *req =
      [[OIDEndSessionRequest alloc] initWithConfiguration:configuration
                                              idTokenHint:idToken
                                    postLogoutRedirectURL:postLogout
                                                    state:nil
                                     additionalParameters:nil];
      
      id<OIDExternalUserAgent> agent =
        [[OIDExternalUserAgentIOS alloc] initWithPresentingViewController:self];
      
      // Present the end-session flow (uses ASWebAuthenticationSession / SFSafariViewController)
      self.configuration =
      [OIDAuthorizationService presentEndSessionRequest:req externalUserAgent: agent
                                               callback:^(OIDEndSessionResponse * _Nullable endSessionResponse, NSError * _Nullable error) {
          
          
          if(error)
          {
              [self.navigationController popToRootViewControllerAnimated:false];
          }
          else
          {
              // Regardless of network outcome, clear local auth
              [self clearLocalSessionOnly];
              NSUserDefaults *userD = [[NSUserDefaults alloc] init];
              [userD setObject:@"0" forKey:@"isLogin"];
              [userD synchronize];
              
              [self setAuthState:nil];
              
              [self.navigationController popToRootViewControllerAnimated:FALSE];
          }
          }
          ];
      
  }];
}

/*! @brief Loads the @c OIDAuthState from @c NSUSerDefaults.
 */
- (void)loadState {
  // loads OIDAuthState from NSUSerDefaults
  NSUserDefaults* userDefaults = [[NSUserDefaults alloc] initWithSuiteName:@"group.net.openid.appauth.Example"];
  NSData *archivedAuthState = [userDefaults objectForKey:kAppAuthExampleAuthStateKey];
  OIDAuthState *authState = [NSKeyedUnarchiver unarchiveObjectWithData:archivedAuthState];
    if(self.logout == true)
    {
        [self setAuthState:authState];
        [self logoutCall];
       
        [self dismissViewControllerAnimated:false completion:^{
            
        }];
        
    }
    else{
        [self setAuthState:authState];
        [self authWithAutoCodeExchange];
    }
    
}

-(void)viewDidLoad{
    [super viewDidLoad];
    
    [self loadState];
}

- (void)setAuthState:(nullable OIDAuthState *)authState {
  if (self._authState == authState) {
    return;
  }
  self._authState = authState;
  self._authState.stateChangeDelegate = self;
  [self stateChanged];
}

- (void)stateChanged {
  [self saveState];
}

- (void)didChangeState:(OIDAuthState *)state {
  [self stateChanged];
}

- (void)authState:(OIDAuthState *)state didEncounterAuthorizationError:(nonnull NSError *)error {
  //[self logMessage:@"Received authorization error: %@", error];
}

- (void)doClientRegistration:(OIDServiceConfiguration *)configuration
                    callback:(PostRegistrationCallback)callback {
    NSURL *redirectURI = [NSURL URLWithString:kRedirectURI];

    OIDRegistrationRequest *request =
        [[OIDRegistrationRequest alloc] initWithConfiguration:configuration
                                                 redirectURIs:@[ redirectURI ]
                                                responseTypes:nil
                                                   grantTypes:nil
                                                  subjectType:nil
                                      tokenEndpointAuthMethod:@"client_secret_post"
                                           initialAccessToken:nil
                                         additionalParameters:nil];

  // performs registration request
  //  [self logMessage:@"Initiating registration request"];

    [OIDAuthorizationService performRegistrationRequest:request
        completion:^(OIDRegistrationResponse *_Nullable regResp, NSError *_Nullable error) {
      if (regResp) {
        [self setAuthState:[[OIDAuthState alloc] initWithRegistrationResponse:regResp]];
        [self logMessage:@"Got registration response: [%@]", regResp];
        callback(configuration, regResp);
      } else {
        [self logMessage:@"Registration error: %@", [error localizedDescription]];
        [self setAuthState:nil];
      }
    }];
}

- (void)authWithAutoCodeExchange {
  [self verifyConfig];

  NSURL *issuer = [NSURL URLWithString:kIssuer];

  [self logMessage:@"Fetching configuration for issuer: %@", issuer];

  // discovers endpoints
  [OIDAuthorizationService discoverServiceConfigurationForIssuer:issuer
      completion:^(OIDServiceConfiguration *_Nullable configuration, NSError *_Nullable error) {
    if (!configuration) {
      [self logMessage:@"Error retrieving discovery document: %@", [error localizedDescription]];
      [self setAuthState:nil];
      return;
    }

    [self logMessage:@"Got configuration: %@", configuration];

    if (!kClientID) {
      [self doClientRegistration:configuration
                        callback:^(OIDServiceConfiguration *configuration,
                                   OIDRegistrationResponse *registrationResponse) {
        [self doAuthWithAutoCodeExchange:configuration
                                clientID:registrationResponse.clientID
                            clientSecret:registrationResponse.clientSecret];
      }];
    } else {
      [self doAuthWithAutoCodeExchange:configuration clientID:kClientID clientSecret:nil];
    }
   }];
}


- (void)doAuthWithAutoCodeExchange:(OIDServiceConfiguration *)configuration
                          clientID:(NSString *)clientID
                      clientSecret:(NSString *)clientSecret {
  NSURL *redirectURI = [NSURL URLWithString:kRedirectURI];
  // builds authentication request
  OIDAuthorizationRequest *request =
      [[OIDAuthorizationRequest alloc] initWithConfiguration:configuration
                                                    clientId:clientID
                                                clientSecret:clientSecret
                                                      scopes:@[ OIDScopeOpenID, OIDScopeProfile ]
                                                 redirectURL:redirectURI
                                                responseType:OIDResponseTypeCode
                                        additionalParameters:nil];
  // performs authentication request
  AppDelegate *appDelegate = (AppDelegate *) [UIApplication sharedApplication].delegate;
  [self logMessage:@"Initiating authorization request with scope: %@", request.scope];

  appDelegate.currentAuthorizationFlow =
      [OIDAuthState authStateByPresentingAuthorizationRequest:request
          presentingViewController:self
                          callback:^(OIDAuthState *_Nullable authState, NSError *_Nullable error) {
            if (authState) {
              [self setAuthState:authState];
              [self logMessage:@"Got authorization tokens. Access token: %@",
                               authState.lastTokenResponse.accessToken];
                [self userinfo];
            } else {
              [self logMessage:@"Authorization error: %@", [error localizedDescription]];
              [self setAuthState:nil];
                [self.navigationController popToRootViewControllerAnimated:FALSE];
            }
          }];
}

- (void)doAuthWithoutCodeExchange:(OIDServiceConfiguration *)configuration
                         clientID:(NSString *)clientID
                     clientSecret:(NSString *)clientSecret {
  NSURL *redirectURI = [NSURL URLWithString:kRedirectURI];

  // builds authentication request
  OIDAuthorizationRequest *request =
      [[OIDAuthorizationRequest alloc] initWithConfiguration:configuration
                                                    clientId:clientID
                                                clientSecret:clientSecret
                                                      scopes:@[ OIDScopeOpenID, OIDScopeProfile ]
                                                 redirectURL:redirectURI
                                                responseType:OIDResponseTypeCode
                                        additionalParameters:nil];
  // performs authentication request
  AppDelegate *appDelegate = (AppDelegate *) [UIApplication sharedApplication].delegate;
//  [self logMessage:@"Initiating authorization request %@", request];
  appDelegate.currentAuthorizationFlow =
      [OIDAuthorizationService presentAuthorizationRequest:request
          presentingViewController:self
                          callback:^(OIDAuthorizationResponse *_Nullable authorizationResponse,
                                     NSError *_Nullable error) {
        if (authorizationResponse) {
          OIDAuthState *authState =
              [[OIDAuthState alloc] initWithAuthorizationResponse:authorizationResponse];
          [self setAuthState:authState];

          [self logMessage:@"Authorization response with code: %@",
                           authorizationResponse.authorizationCode];
          // could just call [self tokenExchange:nil] directly, but will let the user initiate it.
        } else {
          [self logMessage:@"Authorization error: %@", [error localizedDescription]];
        }
      }];
}

- (IBAction)authWithAutoCodeExchange:(nullable id)sender {
  [self verifyConfig];

  NSURL *issuer = [NSURL URLWithString:kIssuer];

  [self logMessage:@"Fetching configuration for issuer: %@", issuer];

  // discovers endpoints
  [OIDAuthorizationService discoverServiceConfigurationForIssuer:issuer
      completion:^(OIDServiceConfiguration *_Nullable configuration, NSError *_Nullable error) {
    if (!configuration) {
      [self logMessage:@"Error retrieving discovery document: %@", [error localizedDescription]];
      [self setAuthState:nil];
      return;
    }

    [self logMessage:@"Got configuration: %@", configuration];

    if (!kClientID) {
      [self doClientRegistration:configuration
                        callback:^(OIDServiceConfiguration *configuration,
                                   OIDRegistrationResponse *registrationResponse) {
        [self doAuthWithAutoCodeExchange:configuration
                                clientID:registrationResponse.clientID
                            clientSecret:registrationResponse.clientSecret];
      }];
    } else {
      [self doAuthWithAutoCodeExchange:configuration clientID:kClientID clientSecret:nil];
    }
   }];
}

- (void)userinfo {
  NSURL *userinfoEndpoint =
      self._authState.lastAuthorizationResponse.request.configuration.discoveryDocument.userinfoEndpoint;
  if (!userinfoEndpoint) {
    [self logMessage:@"Userinfo endpoint not declared in discovery document"];
    return;
  }
  NSString *currentAccessToken = self._authState.lastTokenResponse.accessToken;

  [self logMessage:@"Performing userinfo request"];

  [self._authState performActionWithFreshTokens:^(NSString *_Nonnull accessToken,
                                             NSString *_Nonnull idToken,
                                             NSError *_Nullable error) {
    if (error) {
      [self logMessage:@"Error fetching fresh tokens: %@", [error localizedDescription]];
      return;
    }

    // log whether a token refresh occurred
    if (![currentAccessToken isEqual:accessToken]) {
      [self logMessage:@"Access token was refreshed automatically (%@ to %@)",
                         currentAccessToken,
                         accessToken];
    } else {
      [self logMessage:@"Access token was fresh and not updated [%@]", accessToken];
    }

    // creates request to the userinfo endpoint, with access token in the Authorization header
    NSMutableURLRequest *request = [NSMutableURLRequest requestWithURL:userinfoEndpoint];
    NSString *authorizationHeaderValue = [NSString stringWithFormat:@"Bearer %@", accessToken];
    [request addValue:authorizationHeaderValue forHTTPHeaderField:@"Authorization"];

    NSURLSessionConfiguration *configuration =
        [NSURLSessionConfiguration defaultSessionConfiguration];
    NSURLSession *session = [NSURLSession sessionWithConfiguration:configuration
                                                          delegate:nil
                                                     delegateQueue:nil];

    // performs HTTP request
    NSURLSessionDataTask *postDataTask =
        [session dataTaskWithRequest:request
                   completionHandler:^(NSData *_Nullable data,
                                       NSURLResponse *_Nullable response,
                                       NSError *_Nullable error) {
      dispatch_async(dispatch_get_main_queue(), ^() {
        if (error) {
          [self logMessage:@"HTTP request failed %@", error];
          return;
        }
        if (![response isKindOfClass:[NSHTTPURLResponse class]]) {
          [self logMessage:@"Non-HTTP response"];
          return;
        }

        NSHTTPURLResponse *httpResponse = (NSHTTPURLResponse *)response;
        id jsonDictionaryOrArray =
            [NSJSONSerialization JSONObjectWithData:data options:0 error:NULL];

        if (httpResponse.statusCode != 200) {
          // server replied with an error
          NSString *responseText = [[NSString alloc] initWithData:data
                                                         encoding:NSUTF8StringEncoding];
          if (httpResponse.statusCode == 401) {
            // "401 Unauthorized" generally indicates there is an issue with the authorization
            // grant. Puts OIDAuthState into an error state.
            NSError *oauthError =
                [OIDErrorUtilities resourceServerAuthorizationErrorWithCode:0
                                                              errorResponse:jsonDictionaryOrArray
                                                            underlyingError:error];
            [self._authState updateWithAuthorizationError:oauthError];
            // log error
            [self logMessage:@"Authorization Error (%@). Response: %@", oauthError, responseText];
          } else {
            [self logMessage:@"HTTP: %d. Response: %@",
                             (int)httpResponse.statusCode,
                             responseText];
          }
          return;
        }

        // success response
        [self logMessage:@"Success: %@", jsonDictionaryOrArray];
          [[NSUserDefaults standardUserDefaults] setObject:jsonDictionaryOrArray forKey:@"userData"];
          [self loginSuccesful];
          
      });
    }];

    [postDataTask resume];
  }];
}

-(void) loginFalied{
    [self.navigationController popToRootViewControllerAnimated:false];
}
- (void) loginSuccesful {
    
//    [FIRAnalytics logEventWithName:@"login"
//                          parameters:@{
//                              @"name": @"success"
//                                       }];
//   //LogDebug(@"");
//    [self loginProgressEnded];
//    //[self dismissModalViewControllerAnimated:YES];
//    //[self.navigationController popViewControllerAnimated:YES];
        if(self.lang == nil )
    {
        
               self.lang = [[LangSelectorViewController alloc]init];
        [self.navigationController pushViewController:self.lang animated:YES];
        
        NSMutableArray *navigationStack = [[NSMutableArray alloc] initWithArray:
                                           self.navigationController.viewControllers];
        [navigationStack removeObjectAtIndex:[navigationStack count] - 2];
        self.navigationController.viewControllers = navigationStack;
        
        NSUserDefaults *userD = [[NSUserDefaults alloc] init];
        [userD setObject:@"1" forKey:@"isLogin"];
        [userD synchronize];
    
    [self dismissViewControllerAnimated:false completion:^{
        
    }];


        
    }

//    NSUserDefaults *defaults = [NSUserDefaults standardUserDefaults];
//    [defaults setObject:self.usernameTF.text forKey:@"username"];
//    [defaults setObject:self.passwordTF.text forKey:@"password"];
//    [defaults synchronize];
 
    
}
- (void)logMessage:(NSString *)format, ... NS_FORMAT_FUNCTION(1,2) {
  // gets message as string
  va_list argp;
  va_start(argp, format);
  NSString *log = [[NSString alloc] initWithFormat:format arguments:argp];
  va_end(argp);

  // outputs to stdout
  NSLog(@"%@", log);

  // appends to output log
  NSDateFormatter *dateFormatter = [[NSDateFormatter alloc] init];
  dateFormatter.dateFormat = @"hh:mm:ss";
  NSString *dateString = [dateFormatter stringFromDate:[NSDate date]];
//  _logTextView.text = [NSString stringWithFormat:@"%@%@%@: %@",
//                                                 _logTextView.text,
//                                                 ([_logTextView.text length] > 0) ? @"\n" : @"",
//                                                 dateString,
//                                                 log];
}


@end

NS_ASSUME_NONNULL_END
