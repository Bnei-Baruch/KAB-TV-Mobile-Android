//
//  ProfileChecker 2.h
//  kxmovie
//
//  Created by shidur on 23/09/2025.
//


#import <Foundation/Foundation.h>
#import <UIKit/UIKit.h>

NS_ASSUME_NONNULL_BEGIN

@protocol PCUserSub <NSObject>
- (void)requestLogout;
-(void)loginSuccesful;
@end

@interface ProfileChecker : NSObject

/// Use a view controller that is currently visible to present alerts.
/// Kept weak to avoid retain cycles.
- (instancetype)initWithPresentingViewController:(UIViewController *)presenter;

/// Mirrors: checkUserProfileAndShowPopupIfInactive(userId, authToken, endSession)
- (void)checkUserProfileAndShowPopupIfInactiveForUserId:(NSString *)userId
                                              authToken:(nullable NSString *)authToken
                                             endSession:(id<PCUserSub>)endSession;

/// Mirrors: deleteAccount(authToken, endSession) -> Boolean (returns YES after starting the task)
- (BOOL)deleteAccountWithAuthToken:(NSString *)authToken
                        endSession:(id<PCUserSub>)endSession;

@end

NS_ASSUME_NONNULL_END
