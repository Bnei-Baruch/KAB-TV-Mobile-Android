//
//  SvivaTovaLoginController.h
//  kxmovie
//
//  Created by Asher on 4/29/13.
//
//

#import <Foundation/Foundation.h>

@interface SvivaTovaLoginController : NSObject {
    
    NSString* strReply;
    NSMutableDictionary* allHeaderFields;
    NSMutableData	*recievedData;
    int		  statusCode;
    
    NSString *serverCookies;
    
    NSObject *loginRequestResponseTarget;
    SEL loginSuccesResponseSelector;
    SEL loginfailedResponseSelector;
}

- (void)loginWithUsername:(NSString *)username
              andPassword:(NSString *)password
          andLocalization:(NSString *)localization
                   target:(NSObject *)target
            successAction:(SEL)successSEL
               failAction:(SEL)failSEL;

- (void) postLoginDataWithUsername:(NSString *)username
                       andPassword:(NSString *)password
                   andLocalization:(NSString *)localization;


@property (strong) 	NSMutableData*	  recievedData;
@property (strong)  NSMutableDictionary* allHeaderFields;
@property (strong) 	NSString* strReply;
@property (strong) 	NSString *serverCookies;
@property (strong) 	NSObject *loginRequestResponseTarget;
@property 	SEL loginSuccesResponseSelector;
@property 	SEL loginfailedResponseSelector;

@end
