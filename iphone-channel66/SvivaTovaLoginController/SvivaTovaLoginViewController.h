//
//  SvivaTovaLoginViewController.h
//  kxmovie
//
//  Created by Asher on 4/29/13.
//
//

#import <UIKit/UIKit.h>
#import "LangSelectorViewController.h"

@interface SvivaTovaLoginViewController : UIViewController {

    __weak IBOutlet UITextField *usernameTF;
    __weak IBOutlet UITextField *passwordTF;
    __weak IBOutlet UISwitch *rememberLoginDetailsSwitch;
    __weak IBOutlet UIButton *loginButton;
    __weak IBOutlet UIButton *dismissScreenButton;
    LangSelectorViewController *lang;
}
- (IBAction)cancelButtonClicked:(id)sender;
- (IBAction)loginButtonClicked:(id)sender;

@property (nonatomic,retain) LangSelectorViewController *lang;
@property (weak, nonatomic) IBOutlet UITextField *usernameTF;
@property (weak, nonatomic) IBOutlet UITextField *passwordTF;
@property (weak, nonatomic) IBOutlet UISwitch *rememberLoginDetailsSwitch;
@property (weak, nonatomic) IBOutlet UIButton *loginButton;
@property (weak, nonatomic) IBOutlet UIButton *dismissScreenButton;
@end
