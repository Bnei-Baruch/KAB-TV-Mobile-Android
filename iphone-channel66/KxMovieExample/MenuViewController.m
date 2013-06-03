//
//  MenuViewController.m
//  kxmovie
//
//  Created by Yuval Ovadia on 3/8/13.
//
//

#import "MenuViewController.h"
#import "LoginControllerViewController.h"
#import "MainViewController.h"
#import "ChooseQualityViewController.h"
#import "SvivaTovaLoginViewController.h"

@interface MenuViewController () {
NSArray *menu_details;
}

@property (strong, nonatomic) UITableView *tableView;
@end

@implementation MenuViewController
@synthesize fromWhere;

- (id)initWithNibName:(NSString *)nibNameOrNil bundle:(NSBundle *)nibBundleOrNil
{
    self = [super initWithNibName:nibNameOrNil bundle:nibBundleOrNil];
    if (self) {
        // Custom initialization
    }
    return self;
}


- (id)init
{
    NSLog(@"+-+- MenuViewController.m: init");
    self = [super init];
    if (self) {
        self.title = @"settings";
           }
    return self;
}

- (void)viewWillAppear:(BOOL)animated
{
    NSLog(@"+-+- MenuViewController.m: viewWillAppear");
    [super viewWillAppear:animated];
    //[self reloadMovies];
    [self.tableView reloadData];
}

- (void)loadView
{
    NSLog(@"+-+- MenuViewController.m: loadView");   
    if (fromWhere == 1) {
        menu_details = @[
        @"Sviva Tova",
        @"Choose Quality"
        ];
    } else {
        menu_details = @[
        @"Channel 66",
        @"Choose Quality"
        ];
    }
    
    
    self.view = [[UIView alloc] initWithFrame:[[UIScreen mainScreen] applicationFrame]];
    self.tableView = [[UITableView alloc] initWithFrame:self.view.bounds style:UITableViewStyleGrouped];
    self.tableView.backgroundColor = [UIColor whiteColor];
    //self.tableView.backgroundView = [[UIImageView alloc] initWithImage:image];
    self.tableView.autoresizingMask = UIViewAutoresizingFlexibleWidth | UIViewAutoresizingFlexibleTopMargin | UIViewAutoresizingFlexibleRightMargin | UIViewAutoresizingFlexibleLeftMargin | UIViewAutoresizingFlexibleHeight | UIViewAutoresizingFlexibleBottomMargin;
    self.tableView.delegate = self;
    self.tableView.dataSource = self;
    self.tableView.separatorStyle = UITableViewCellSeparatorStyleSingleLine;
    
    [self.view addSubview:self.tableView];
}

- (void)viewDidLoad
{
    NSLog(@"+-+- MenuViewController.m: viewDidLoad");
    [super viewDidLoad];
    // Do any additional setup after loading the view from its nib.
}

- (void)didReceiveMemoryWarning
{
    NSLog(@"+-+- MenuViewController.m: didReceiveMemoryWarning");
    [super didReceiveMemoryWarning];
    // Dispose of any resources that can be recreated.
}

-(void)openQualitySettings{
    NSLog(@"+-+- MenuViewController.m: openSvivaTova");
    
    //ChooseQualityViewController
    ChooseQualityViewController *quality = [[ChooseQualityViewController alloc]init];
    [self.navigationController pushViewController:quality animated:YES];
}
-(void)openSvivaTova{
    NSLog(@"+-+- MenuViewController.m: openSvivaTova");

//    LoginControllerViewController *web = [[LoginControllerViewController alloc]init];
//   [self.navigationController pushViewController:web animated:YES];
//    
    NSUserDefaults *defaults = [NSUserDefaults standardUserDefaults];
    if(!([defaults valueForKey:@"password"]!=NULL && [defaults valueForKey:@"username"]!=NULL))
    {
        SvivaTovaLoginViewController * login = [[SvivaTovaLoginViewController alloc]init];
        [self.navigationController pushViewController:login animated:YES];
    }
    else
    {
        LangSelectorViewController  *lang = [[LangSelectorViewController alloc]init];
        [self.navigationController pushViewController:lang animated:YES];
    }
    
}
-(void) openChannel66 {
    NSLog(@"+-+- MenuViewController.m: openChannel66");
    MainViewController *web = [[MainViewController alloc]init];
    
    
    [self.navigationController pushViewController:web animated:YES];
}
#pragma mark - Table view data source
- (NSInteger)tableView:(UITableView *)tableView numberOfRowsInSection:(NSInteger)section
{
    
    return [menu_details count];
}

- (UITableViewCell *)tableView:(UITableView *)tableView cellForRowAtIndexPath:(NSIndexPath *)indexPath
{
    static NSString *cellIdentifier = @"Cell";
    UITableViewCell *cell = [self.tableView dequeueReusableCellWithIdentifier:cellIdentifier];
    if (cell == nil) {
        cell = [[UITableViewCell alloc] initWithStyle:UITableViewCellStyleDefault
                                      reuseIdentifier:cellIdentifier];
        cell.accessoryType = UITableViewCellAccessoryDisclosureIndicator;
    }
    
    cell.textLabel.text = menu_details[indexPath.row];

    return cell;
}

#pragma mark - Table view delegate

- (void)tableView:(UITableView *)tableView didSelectRowAtIndexPath:(NSIndexPath *)indexPath
{
        switch (indexPath.row) {
            case 0:
                if (fromWhere == 1) {
                    NSUserDefaults *userD = [[NSUserDefaults alloc] init];
                    if ([@"1" isEqualToString:[userD objectForKey:@"isLogin"]]) {
                        [self openSvivaTova];
                    } else {
                        [self showAlertWithText];
                    }
                } else {
                    [self openChannel66];
                }
                break;
            case 1:
                NSLog(@"Choose quality");
                [self openQualitySettings];
                break;
            default:
                break;
        }
//    }
}
    -(void)showAlertWithText {
        
        UIAlertView *passwordAlert = [[UIAlertView alloc] initWithTitle:@"Enter Pin" message:@"\n\n\n"
                                                               delegate:self cancelButtonTitle:NSLocalizedString(@"Cancel",nil) otherButtonTitles:NSLocalizedString(@"OK",nil), nil];
        passwordAlert.tag = 1;
        
        UILabel *passwordLabel = [[UILabel alloc] initWithFrame:CGRectMake(12,40,260,25)];
        passwordLabel.font = [UIFont systemFontOfSize:16];
        passwordLabel.textColor = [UIColor whiteColor];
        passwordLabel.backgroundColor = [UIColor clearColor];
        passwordLabel.shadowColor = [UIColor blackColor];
        passwordLabel.shadowOffset = CGSizeMake(0,-1);
        passwordLabel.textAlignment = UITextAlignmentCenter;
        passwordLabel.text = @"";
        //[passwordAlert addSubview:passwordLabel];
        
        UIImageView *passwordImage = [[UIImageView alloc] initWithImage:[UIImage imageWithContentsOfFile:[[NSBundle mainBundle] pathForResource:@"passwordfield" ofType:@"png"]]];
        passwordImage.frame = CGRectMake(11,79,262,31);
        //[passwordAlert addSubview:passwordImage];
        
        UITextField *passwordField = [[UITextField alloc] initWithFrame:CGRectMake(16,83,252,25)];
        passwordField.font = [UIFont systemFontOfSize:18];
        passwordField.backgroundColor = [UIColor whiteColor];
        passwordField.secureTextEntry = YES;
        passwordField.keyboardAppearance = UIKeyboardAppearanceAlert;
        //passwordField.delegate = self;
        [passwordField becomeFirstResponder];
        passwordField.borderStyle = UITextBorderStyleRoundedRect;
        passwordField.tag = 100;
        [passwordAlert addSubview:passwordField];
        
        
        
        //[passwordAlert setTransform:CGAffineTransformMakeTranslation(0,109)];
        [passwordAlert show];
        
}

- (void)alertView:(UIAlertView *)alertView didDismissWithButtonIndex:(NSInteger)buttonIndex {
    NSLog(@"didDismissWithButtonIndex");
    
    //get the textField from the UIalertView
    UITextField *getTextView = (UITextField*)[alertView viewWithTag:100];
    NSLog(@"key pressed = %d, getTextView = %@", buttonIndex, getTextView.text);
    
    if (buttonIndex == 1 && ([@"arvut" caseInsensitiveCompare:getTextView.text] == NSOrderedSame ) ) {
        [self openSvivaTova];
        
        NSUserDefaults *userD = [[NSUserDefaults alloc] init];
        [userD setObject:@"1" forKey:@"isLogin"];
        [userD synchronize];
    }
    
    
    
}
@end
