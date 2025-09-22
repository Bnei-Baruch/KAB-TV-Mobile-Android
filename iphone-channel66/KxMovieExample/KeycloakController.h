//
//  KeycloakController.h
//  KxMovieExample
//
//  Created by shidur on 25/05/2025.
//

#import <Foundation/Foundation.h>
#import "AppAuth.h"
#import "LangSelectorViewController.h"

NS_ASSUME_NONNULL_BEGIN

@interface KeycloakController : UIViewController







- (instancetype)initForLogout:(bool)logout;




@property (nonatomic, copy) NSURL *authorizationEndpoint; //=[NSURL URLWithString:@"https://accounts.google.com/o/oauth2/v2/auth"];
@property (nonatomic, copy) NSURL *tokenEndpoint;// = [NSURL URLWithString:@"https://www.googleapis.com/oauth2/v4/token"];

@property (nonatomic, strong) OIDServiceConfiguration *configuration;// =[[OIDServiceConfiguration alloc]initWithAuthorizationEndpoint:authorizationEndpoint tokenEndpoint:tokenEndpoint];

@property (nonatomic, strong) OIDAuthState *_authState;

@property (nonatomic, strong)  LangSelectorViewController *lang;

@property  bool logout;

@end

NS_ASSUME_NONNULL_END
