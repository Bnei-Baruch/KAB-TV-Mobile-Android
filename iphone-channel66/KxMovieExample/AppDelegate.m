//
//  AppDelegate.m
//  kxmovieapp
//
//  Created by Kolyvan on 11.10.12.
//  Copyright (c) 2012 Konstantin Boukreev . All rights reserved.
//
//  https://github.com/kolyvan/kxmovie
//  this file is part of KxMovie
//  KxMovie is licenced under the LGPL v3, see lgpl-3.0.txt
//#define TESTING 0
#import "AppDelegate.h"
#import "MainViewController.h"
#import "GAI.h"
#ifdef TESTING
#import "TestFlight.h"
#endif
#import "Parse/Parse.h"

@implementation AppDelegate

- (BOOL)application:(UIApplication *)application didFinishLaunchingWithOptions:(NSDictionary *)launchOptions
{
    self.window = [[UIWindow alloc] initWithFrame:[[UIScreen mainScreen] bounds]];
    
    // Optional: automatically send uncaught exceptions to Google Analytics.
    [GAI sharedInstance].trackUncaughtExceptions = YES;
    // Optional: set Google Analytics dispatch interval to e.g. 20 seconds.
    [GAI sharedInstance].dispatchInterval = 20;
    // Optional: set debug to YES for extra debugging information.
    //[GAI sharedInstance].debug = YES;
    // Create tracker instance.
    id<GAITracker> tracker = [[GAI sharedInstance] trackerWithTrackingId:@"UA-36608494-1"];
    

#ifdef TESTING
    [TestFlight setDeviceIdentifier:[[UIDevice currentDevice] uniqueIdentifier]];

    
    [TestFlight takeOff:@"8d2caf14-8df9-4937-a9d7-3e91b0a5465b"];
#endif    
    //
    NSUserDefaults *userD = [[NSUserDefaults alloc] init];
    if (![userD objectForKey:@"quality"]) {
        [userD setObject:@"Medium" forKey:@"quality"];
        [userD synchronize];
    }
   
    
    UIViewController * vc = [[MainViewController alloc] init];
    UITabBarController *tabBarController = [[UITabBarController alloc] init];
    tabBarController.viewControllers = @[
        [[UINavigationController alloc] initWithRootViewController:vc],
    ];
    
    self.window.rootViewController = tabBarController;
    [self.window makeKeyAndVisible];
    
    
    [Parse enableLocalDatastore];
    //channel 66
    //[Parse setApplicationId:@"dmSTSXcOcBxITZBioUAmC7HXps0OCUteMJEklSCD" clientKey:@"b0gN0SoJgOmQ51fkQoNb9B7bNEIF2agc9SYhFG7U"];
    // test channle 66
    [Parse setApplicationId:@"KZGRjYuBEwh6vubjJBRzscvVixyLC8fWg9YqAwVS" clientKey:@"H3JqHHIKrd8xN44weGfAsWmUeCJQdqh8bPR8H4M6"];
    //testchannel2
    
    //[Parse setApplicationId:@"ayoTJHpHAVbwWEprqxzQeYpYCIaxz98HY19DbQiF" clientKey:@"imLHqDJYiH6S3iPtZ3gw1yilsXna8wHM0oSiGktp"];
    
   
    if ([application respondsToSelector:@selector(isRegisteredForRemoteNotifications)])
    {
        // iOS 8 Notifications
        [application registerUserNotificationSettings:[UIUserNotificationSettings settingsForTypes:(UIUserNotificationTypeSound | UIUserNotificationTypeAlert | UIUserNotificationTypeBadge) categories:nil]];
        
        [application registerForRemoteNotifications];
    }
    else
    {
        // iOS < 8 Notifications
        [application registerForRemoteNotificationTypes:
         (UIRemoteNotificationTypeBadge | UIRemoteNotificationTypeAlert | UIRemoteNotificationTypeSound)];
    }
    
    if (!launchOptions[UIApplicationLaunchOptionsRemoteNotificationKey]) {
        
    }
    
    
        return YES;
}

- (void)application:(UIApplication *)application
didRegisterForRemoteNotificationsWithDeviceToken:(NSData *)newDeviceToken
{
    // Store the deviceToken in the current installation and save it to Parse.
    PFInstallation *currentInstallation = [PFInstallation currentInstallation];
    //[currentInstallation addUniqueObject:@"RABASH" forKey:@"channels"];
    [currentInstallation setDeviceTokenFromData:newDeviceToken];
    [currentInstallation saveInBackground];
}

- (void) application:(UIApplication *)application didFailToRegisterForRemoteNotificationsWithError:(NSError *)error
{
    NSLog(@"failed");
}

- (void)applicationWillResignActive:(UIApplication *)application
{
    // Sent when the application is about to move from active to inactive state. This can occur for certain types of temporary interruptions (such as an incoming phone call or SMS message) or when the user quits the application and it begins the transition to the background state.
    // Use this method to pause ongoing tasks, disable timers, and throttle down OpenGL ES frame rates. Games should use this method to pause the game.
    NSArray* allCookies = [[NSHTTPCookieStorage sharedHTTPCookieStorage] cookiesForURL:[NSURL URLWithString:@"kabbalahgroup.info"]];
    for (NSHTTPCookie *cookie in allCookies) {
        if ([cookie.name isEqualToString:@""]) {
            NSMutableDictionary* cookieDictionary = [NSMutableDictionary dictionaryWithDictionary:[[NSUserDefaults standardUserDefaults] dictionaryForKey:@"kabbala"]];
            [cookieDictionary setValue:cookie.properties forKey:@"kabbalahgroup.info"];
            [[NSUserDefaults standardUserDefaults] setObject:cookieDictionary forKey:@"cookie"];
        }
    }

}

- (void)application:(UIApplication *)application
didReceiveRemoteNotification:(NSDictionary *)userInfo {
    [PFPush handlePush:userInfo];
    
    NSDictionary *apsInfo = [userInfo objectForKey:@"aps"];
    if( [apsInfo objectForKey:@"alert"] != NULL)
    {
        PFObject *messagesObject = [PFObject objectWithClassName:@"messages"];
        messagesObject[@"text"] = [apsInfo objectForKey:@"alert"];
        messagesObject[@"date"] = [NSDate date];
        [messagesObject pinInBackground];
    }
}



- (void)applicationDidEnterBackground:(UIApplication *)application
{
    // Use this method to release shared resources, save user data, invalidate timers, and store enough application state information to restore your application to its current state in case it is terminated later. 
    // If your application supports background execution, this method is called instead of applicationWillTerminate: when the user quits.
    
    NSLog(@"applicationDidEnterBackground");
    
    //id audioManager = [KxAudioManager audioManager];
    //[audioManager deactivateAudioSession];
    
    
    
    __block UIBackgroundTaskIdentifier task = 0;
    task=[application beginBackgroundTaskWithExpirationHandler:^{
        NSLog(@"Expiration handler called %f",[application backgroundTimeRemaining]);
        [application endBackgroundTask:task];
        task=UIBackgroundTaskInvalid;
    }];
}

- (void)applicationWillEnterForeground:(UIApplication *)application
{
    // Called as part of the transition from the background to the inactive state; here you can undo many of the changes made on entering the background.
}

- (void)applicationDidBecomeActive:(UIApplication *)application
{
    // Restart any tasks that were paused (or not yet started) while the application was inactive. If the application was previously in the background, optionally refresh the user interface.
    NSDictionary* cookieDictionary = [[NSUserDefaults standardUserDefaults] dictionaryForKey:@"cookie"];
    NSDictionary* cookieProperties = [cookieDictionary valueForKey:@"kabbala"];
    if (cookieProperties != nil) {
        NSHTTPCookie* cookie = [NSHTTPCookie cookieWithProperties:cookieProperties];
        NSArray* cookieArray = [NSArray arrayWithObject:cookie];
        [[NSHTTPCookieStorage sharedHTTPCookieStorage] setCookies:cookieArray forURL:[NSURL URLWithString:@"kabbalahgroup.info"] mainDocumentURL:nil];
    }
}

- (void)applicationWillTerminate:(UIApplication *)application
{
    // Called when the application is about to terminate. Save data if appropriate. See also applicationDidEnterBackground:.
    
   // [[NSHTTPCookieStorage sharedHTTPCookieStorage] setCookies:cookies forURL:url mainDocumentURL:nil]; // where cookies is the unarchived array of cookies
  
  }

@end
