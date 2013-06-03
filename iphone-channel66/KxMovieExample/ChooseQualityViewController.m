//
//  ChooseQualityViewController.m
//  kxmovie
//
//  Created by Yuval Ovadia on 3/8/13.
//
//

#import "ChooseQualityViewController.h"

@interface ChooseQualityViewController () {
    NSArray *seetings_details;
}

@property (strong, nonatomic) UITableView *tableView;

@end

@implementation ChooseQualityViewController

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
    NSLog(@"+-+- ChooseQualityViewController: init");
    self = [super init];
    if (self) {
        self.title = @"Choose quality";
    }
    return self;
}

- (void)viewWillAppear:(BOOL)animated
{
    NSLog(@"+-+- ChooseQualityViewController: viewWillAppear");
    [super viewWillAppear:animated];
    //[self reloadMovies];
    [self.tableView reloadData];
}

- (void)loadView
{
    NSLog(@"+-+- ChooseQualityViewController.m: loadView");
        seetings_details = @[
        @"Medium",
        @"High"
        ];
    
    
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
    NSLog(@"+-+- ChooseQualityViewController.m: viewDidLoad");
    [super viewDidLoad];
    // Do any additional setup after loading the view from its nib.
}

- (void)didReceiveMemoryWarning
{
    [super didReceiveMemoryWarning];
    // Dispose of any resources that can be recreated.
}

#pragma mark - Table view data source
- (NSInteger)tableView:(UITableView *)tableView numberOfRowsInSection:(NSInteger)section
{
    NSLog(@"+-+- ChooseQualityViewController.m: numberOfRowsInSection");
    return [seetings_details count];
}

- (UITableViewCell *)tableView:(UITableView *)tableView cellForRowAtIndexPath:(NSIndexPath *)indexPath
{
    NSLog(@"+-+- ChooseQualityViewController.m: cellForRowAtIndexPath");
    static NSString *cellIdentifier = @"Cell";
    UITableViewCell *cell = [self.tableView dequeueReusableCellWithIdentifier:cellIdentifier];
    if (cell == nil) {
        cell = [[UITableViewCell alloc] initWithStyle:UITableViewCellStyleDefault
                                      reuseIdentifier:cellIdentifier];
        
    }
    
    cell.textLabel.text = seetings_details[indexPath.row];
    
    NSUserDefaults *userD = [[NSUserDefaults alloc] init];
    NSString *str2 = [[userD objectForKey:@"quality"] lowercaseString];
    
    NSString *str1 = [cell.textLabel.text lowercaseString];
 
    if ([str1 isEqualToString:str2] ) {
        cell.accessoryType = UITableViewCellAccessoryCheckmark;
    } else {
        cell.accessoryType = UITableViewCellAccessoryNone;
    }
    
//    UIImage *indicatorImage = [UIImage imageNamed:@"indicator.png"];
//    cell.accessoryView =
//    [[UIImageView alloc]
//      initWithImage:indicatorImage];

    
    
    return cell;
}

- (void)tableView:(UITableView *)tableView didSelectRowAtIndexPath:(NSIndexPath *)indexPath
{
    NSLog(@"+-+- ChooseQualityViewController.m: didSelectRowAtIndexPath");

    //UITableViewCell* cell = [tableView cellForRowAtIndexPath:indexPath];
    NSUserDefaults *userD = [[NSUserDefaults alloc] init];
    switch (indexPath.row) {
        case 0:
            [userD setObject:@"Medium" forKey:@"quality"];
            break;
        case 1:
            [userD setObject:@"High" forKey:@"quality"];
            break;
        default:
            break;
    }
    [userD synchronize];
    [self.tableView reloadData];
    //[self refreshData];
}
-(void) refreshData {
    [self.tableView reloadData];
}
@end
