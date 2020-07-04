//
//  SvivaTovaLoginViewController.m
//  kxmovie
//
//  Created by Asher on 4/29/13.
//
//

#import "SvivaTovaLoginViewController.h"
#import "SvivaTovaLoginController.h"
#import "LangSelectorViewController.h"
#import <FBSDKCoreKit/FBSDKAccessToken.h>
#import "LangSelectorViewController.h"
#import <FBSDKCoreKit/FBSDKProfile.h>
#import <FBSDKCoreKit/FBSDKGraphRequest.h>
#import <FBSDKCoreKit/FBSDKSettings.h>
@import Firebase;

#define kActivityViewTagId 8439
#define kLoadingViewTagId 1993


#pragma mark - Private definitions
@interface SvivaTovaLoginViewController (PrivateMethods)

- (void) loginSuccesful;
- (void) loginFailed;
- (void) loginInProgress;
- (void) loginProgressEnded;
- (BOOL) isValidEmailAddress:(NSString *)emailAddr;

@end





#pragma mark - implementation
    
@implementation SvivaTovaLoginViewController

@synthesize usernameTF;
@synthesize passwordTF;
@synthesize rememberLoginDetailsSwitch;
@synthesize loginButton;
@synthesize dismissScreenButton;
@synthesize lang;


#pragma mark - UITextField Delegates

- (BOOL)textFieldShouldEndEditing:(UITextField *)textField {
    
	if ([textField isEqual:passwordTF]) {
        // nothing to validate.
	}
	else if ([textField isEqual:usernameTF]) {
        
        if (![self isValidEmailAddress:usernameTF.text]) {
            
            // Invalid email address:
            
            // TODO: Localization:
            
            // TODO: verify all users uses email for login!
            
            UIAlertView *loginFailedAlertView = [[UIAlertView alloc] initWithTitle:@"Username is not an email"
                                                                           message:@"שם משתמש לא תקין"
                                                                          delegate:nil
                                                                 cancelButtonTitle:nil
                                                                 otherButtonTitles:@"חזור", nil];
            [loginFailedAlertView show];
            
            LogWarn(@"username is not a valid email address");
            
            [usernameTF becomeFirstResponder];
            
            return NO;
            
        }

        
	}
	return YES;
}



- (BOOL)textFieldShouldReturn:(UITextField *)textField {
	
	NSString *username_str = nil;
	NSString *password_str = nil;
	
	username_str = [usernameTF.text stringByTrimmingCharactersInSet:[NSCharacterSet whitespaceCharacterSet]];
	password_str = [passwordTF.text stringByTrimmingCharactersInSet:[NSCharacterSet whitespaceCharacterSet]];
	
    if (!username_str.length || !password_str.length) {
        
		if (textField == usernameTF) [passwordTF becomeFirstResponder];
		if (textField == passwordTF) [usernameTF becomeFirstResponder];
        
        return NO;
        
	} else {
        
        if (![self isValidEmailAddress:username_str]) {
            
            // Invalid email address:

            // TODO: Localization:

            // TODO: verify all users uses email for login!
            
            UIAlertView *loginFailedAlertView = [[UIAlertView alloc] initWithTitle:@"Username is not an email"
                                                                           message:@"שם משתמש לא תקין"
                                                                          delegate:nil
                                                                 cancelButtonTitle:nil
                                                                 otherButtonTitles:@"חזור", nil];
            [loginFailedAlertView show];
            
            LogWarn(@"username is not a valid email address");
            
            [usernameTF becomeFirstResponder];
            
            return NO;
            
        }
        else {
            
            [textField resignFirstResponder];
            
            // All ok,  can try to log-in:
            [self loginButtonClicked:nil];
            
        }

	}
	
	return	 YES;
}




#pragma mark - Default

- (id)initWithNibName:(NSString *)nibNameOrNil bundle:(NSBundle *)nibBundleOrNil
{
    self = [super initWithNibName:nibNameOrNil bundle:nibBundleOrNil];
    if (self) {
        // Custom initialization
    }
    return self;
}

- (void)viewDidLoad
{
    [super viewDidLoad];
    // Do any additional setup after loading the view from its nib.
    
    NSUserDefaults *defaults = [NSUserDefaults standardUserDefaults];
    self.usernameTF.text =    [defaults valueForKey:@"username"];
    self.passwordTF.text =    [defaults valueForKey:@"password"];
    
   
        _mLoginButton.readPermissions =
    @[@"public_profile", @"email", @"user_friends"];
    _mLoginButton.delegate = self;
    [_mLoginButton setLoginBehavior:FBSDKLoginBehaviorWeb];
   
}



-(void)deleteAllKeysForSecClass:(CFTypeRef)secClass {
    NSMutableDictionary* dict = [NSMutableDictionary dictionary];
    [dict setObject:(__bridge id)secClass forKey:(__bridge id)kSecClass];
    OSStatus result = SecItemDelete((__bridge CFDictionaryRef) dict);
    NSAssert(result == noErr || result == errSecItemNotFound, @"Error deleting keychain data (%ld)", result);
}

- (void)loginButtonDidLogOut:(FBSDKLoginButton *)loginButton;
{
   
    
   
}

- (void)  loginButton:(FBSDKLoginButton *)loginButton
didCompleteWithResult:(FBSDKLoginManagerLoginResult *)result
                error:(NSError *)error;
{

    if(result.token!=NULL)
    {

        [self loginwithFacebook:[result.token tokenString]];
    }
}

#pragma mark - Memory managment

- (void)didReceiveMemoryWarning
{
    [super didReceiveMemoryWarning];
    // Dispose of any resources that can be recreated.
}

- (void)viewDidUnload {
    usernameTF = nil;
    [self setPasswordTF:nil];
    usernameTF = nil;
    passwordTF = nil;
    [self setUsernameTF:nil];
    rememberLoginDetailsSwitch = nil;
    [self setRememberLoginDetailsSwitch:nil];
    [self setLoginButton:nil];
    loginButton = nil;
    dismissScreenButton = nil;
    [self setDismissScreenButton:nil];
    [super viewDidUnload];
}

#pragma helper functions

- (void)loginwithFacebook:(NSString*)token {
    
    SvivaTovaLoginController *svivaTovaLoginController = [[SvivaTovaLoginController alloc] init];
    
    
   
    
    
    
        [svivaTovaLoginController loginWithFBToken:token
                                    andLocalization:nil
                                             target:self
                                      successAction:@selector(loginSuccesful)
                                         failAction:@selector(loginFailed)];
        [self loginInProgress];
        
       
}


#pragma mark - Screen transition animation

- (UIModalTransitionStyle)modalTransitionStyle {
	return UIModalTransitionStyleFlipHorizontal;
}



#pragma mark - Buttons actions

- (IBAction)cancelButtonClicked:(id)sender {
    [self dismissModalViewControllerAnimated:YES];
}

- (IBAction)loginButtonClicked:(id)sender {
    
    SvivaTovaLoginController *svivaTovaLoginController = [[SvivaTovaLoginController alloc] init];
    
    NSString *user = usernameTF.text;
    NSString *pwd = passwordTF.text;

    
    if (![self isValidEmailAddress:user]) {

        // valid email addr:

        // TODO: Localization:
        
        UIAlertView *loginFailedAlertView = [[UIAlertView alloc] initWithTitle:@"Username is not an email"
                                                                       message:@"שם משתמש לא תקין"
                                                                      delegate:nil
                                                             cancelButtonTitle:nil
                                                             otherButtonTitles:@"חזור", nil];
        
        [loginFailedAlertView show];

        LogWarn(@"username is not a valid email address");
        
    }
    
    
    
    if (user.length && pwd.length) {

        //LogDebug(@"Logging in...");
    
        
        [svivaTovaLoginController loginWithUsername:user
                                        andPassword:pwd
                                    andLocalization:nil
                                             target:self
                                      successAction:@selector(loginSuccesful)
                                         failAction:@selector(loginFailed)];
        [self loginInProgress];
        
    }
    else {
        
        // TODO: Localization:
        
        UIAlertView *loginFailedAlertView = [[UIAlertView alloc] initWithTitle:@"Unable to login"
                                                                       message:@"אנא הזן את כל הפרטים"
                                                                      delegate:nil
                                                             cancelButtonTitle:nil
                                                             otherButtonTitles:@"חזור", nil];
        [loginFailedAlertView show];

        LogWarn(@"username or password are missing");
        
        [usernameTF becomeFirstResponder];
        
    }
    
}



// TODO: handle multiple errors. the current assumption is that the only non-reachability error is wrong username/password:

#pragma mark - Login request status

- (void) loginSuccesful {
    
    [FIRAnalytics logEventWithName:@"login"
                          parameters:@{
                              @"name": @"success"
                                       }];
   //LogDebug(@"");
    [self loginProgressEnded];
    //[self dismissModalViewControllerAnimated:YES];
    //[self.navigationController popViewControllerAnimated:YES];
        if(lang == nil )
    {
        
               lang = [[LangSelectorViewController alloc]init];
        [self.navigationController pushViewController:lang animated:YES];
        
        NSMutableArray *navigationStack = [[NSMutableArray alloc] initWithArray:
                                           self.navigationController.viewControllers];
        [navigationStack removeObjectAtIndex:[navigationStack count] - 2];
        self.navigationController.viewControllers = navigationStack;
        
        NSUserDefaults *userD = [[NSUserDefaults alloc] init];
        [userD setObject:@"1" forKey:@"isLogin"];
        [userD synchronize];


        
    }

    NSUserDefaults *defaults = [NSUserDefaults standardUserDefaults];
    [defaults setObject:self.usernameTF.text forKey:@"username"];
    [defaults setObject:self.passwordTF.text forKey:@"password"];
    [defaults synchronize];
 
    
}


- (void) loginFailed {

    //LogDebug(@"");
    [FIRAnalytics logEventWithName:@"login"
    parameters:@{
        @"name": @"failed"
                 }];
    [self loginProgressEnded];
    
    FBSDKLoginManager *manager = [[FBSDKLoginManager alloc] init];
    [manager logOut];
    // TODO: Localization:
    
    UIAlertView *loginFailedAlertView = [[UIAlertView alloc] initWithTitle:@"Login failed!"
                                                                   message:@"Wrong username or password"
                                                                  delegate:nil
                                                         cancelButtonTitle:nil
                                                         otherButtonTitles:@"חזור", nil];
    
    [loginFailedAlertView show];
    
}


#pragma mark - Login request screen handling

- (void) loginInProgress {
    
    [self.view setUserInteractionEnabled:NO];
    
    UIView *overlayingView = [[UIView alloc] initWithFrame:self.view.bounds];
    overlayingView.backgroundColor = [UIColor darkGrayColor];
    overlayingView.alpha = 0.7;
    overlayingView.tag = kLoadingViewTagId;
    [self.view addSubview:overlayingView];
    
    
    UIActivityIndicatorView *loginInProgressActivityIntidicator = [[UIActivityIndicatorView alloc] initWithActivityIndicatorStyle:UIActivityIndicatorViewStyleWhiteLarge];
    loginInProgressActivityIntidicator.tag = kActivityViewTagId;
    loginInProgressActivityIntidicator.center = self.view.center;
    [overlayingView addSubview:loginInProgressActivityIntidicator];
    [loginInProgressActivityIntidicator startAnimating];
    
}

- (void) loginProgressEnded {
    
    [self.view setUserInteractionEnabled:YES];
    
    UIActivityIndicatorView *loginInProgressActivityIntidicator = (UIActivityIndicatorView *)[self.view viewWithTag:kActivityViewTagId];
    [loginInProgressActivityIntidicator stopAnimating];
    [loginInProgressActivityIntidicator removeFromSuperview];
    
    UIView *overlayingView = [self.view viewWithTag:kLoadingViewTagId]; 
    [overlayingView removeFromSuperview];
    
}



#pragma mark - Email verification

- (BOOL) isValidEmailAddress:(NSString *)emailAddr {
    
    NSString *emailRegex = @"[A-Z0-9a-z._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,4}";
    
    NSPredicate *emailPredicate = [NSPredicate predicateWithFormat:@"SELF MATCHES %@", emailRegex];
    
    if ([emailPredicate evaluateWithObject:emailAddr]) {
        return YES;
    }
    else {
        return NO;
    }
    
}






@end
