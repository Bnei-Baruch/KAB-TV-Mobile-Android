//
//  UserInfoController.m
//  KxMovieExample
//
//  Created by shidur on 15/09/2025.
//

#import "UserInfoController.h"
#import "KeycloakController.h"


NS_ASSUME_NONNULL_BEGIN

@implementation UserInfoController


-(void)viewDidLoad{
    
    [super viewDidLoad];
    id  user = [[NSUserDefaults standardUserDefaults] objectForKey:@"userData"];
    [self logMessage:@"Success: %@", user];
   
        self.title = @"User Info";
        self.view.backgroundColor = [UIColor systemBackgroundColor];

        // Create fields
        self.nameField = [self makeTextFieldWithPlaceholder:[user objectForKey:@"given_name"]];
        self.surnameField = [self makeTextFieldWithPlaceholder:[user objectForKey:@"family_name"]];
        self.emailField = [self makeTextFieldWithPlaceholder:[user objectForKey:@"email"]];
//        self.emailField.keyboardType = UIKeyboardTypeEmailAddress;
//        self.emailField.autocapitalizationType = UITextAutocapitalizationTypeNone;

        // Red clickable text (button styled as link)
        self.actionLinkButton = [UIButton buttonWithType:UIButtonTypeSystem];
        [self.actionLinkButton setTitle:@"Delete account" forState:UIControlStateNormal];
        [self.actionLinkButton setTitleColor:[UIColor systemRedColor] forState:UIControlStateNormal];
        self.actionLinkButton.titleLabel.font = [UIFont systemFontOfSize:16 weight:UIFontWeightSemibold];
        self.actionLinkButton.translatesAutoresizingMaskIntoConstraints = NO;
        [self.actionLinkButton addTarget:self action:@selector(didTapLink) forControlEvents:UIControlEventTouchUpInside];

        // Optional: underline the title to look like a link
        NSMutableAttributedString *title = [[NSMutableAttributedString alloc] initWithString:@"Delete account"];
        [title addAttribute:NSUnderlineStyleAttributeName value:@(NSUnderlineStyleSingle) range:NSMakeRange(0, title.length)];
        [title addAttribute:NSForegroundColorAttributeName value:[UIColor systemRedColor] range:NSMakeRange(0, title.length)];
        [self.actionLinkButton setAttributedTitle:title forState:UIControlStateNormal];

        // Stack for clean layout
        UIStackView *stack = [[UIStackView alloc] initWithArrangedSubviews:@[
            [self labeledRowWithText:@"Name" field:self.nameField],
            [self labeledRowWithText:@"Surname" field:self.surnameField],
            [self labeledRowWithText:@"Email" field:self.emailField],
            self.actionLinkButton
        ]];
        stack.axis = UILayoutConstraintAxisVertical;
        stack.spacing = 16.0;
        stack.translatesAutoresizingMaskIntoConstraints = NO;

        [self.view addSubview:stack];

        UILayoutGuide *guide = self.view.safeAreaLayoutGuide;
        [NSLayoutConstraint activateConstraints:@[
            [stack.leadingAnchor constraintEqualToAnchor:guide.leadingAnchor constant:20],
            [stack.trailingAnchor constraintEqualToAnchor:guide.trailingAnchor constant:-20],
            [stack.topAnchor constraintEqualToAnchor:guide.topAnchor constant:24],
        ]];
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

#pragma mark - UI Helpers

- (UILabel *)makeTextFieldWithPlaceholder:(NSString *)placeholder {
    UILabel *tf = [[UILabel alloc] initWithFrame:CGRectZero];
    tf.text = placeholder;
    tf.translatesAutoresizingMaskIntoConstraints = NO;
    return tf;
}

- (UIView *)labeledRowWithText:(NSString *)text field:(UILabel *)field {
    UILabel *label = [[UILabel alloc] initWithFrame:CGRectZero];
    label.text = text;
    label.font = [UIFont systemFontOfSize:15 weight:UIFontWeightRegular];
    label.translatesAutoresizingMaskIntoConstraints = NO;

    // Make the label prefer to keep its intrinsic width, and the text field stretch
    [label setContentHuggingPriority:UILayoutPriorityRequired
                             forAxis:UILayoutConstraintAxisHorizontal];
    [label setContentCompressionResistancePriority:UILayoutPriorityRequired
                                           forAxis:UILayoutConstraintAxisHorizontal];

    UIStackView *row = [[UIStackView alloc] initWithArrangedSubviews:@[label, field]];
    row.axis = UILayoutConstraintAxisHorizontal;
    row.spacing = 12.0;
    row.alignment = UIStackViewAlignmentFill;
    row.translatesAutoresizingMaskIntoConstraints = NO;

    [field.widthAnchor constraintGreaterThanOrEqualToConstant:120].active = YES;

    return row;
}
#pragma mark - Actions

- (void)didTapLink {
    UIAlertController *alert = [UIAlertController alertControllerWithTitle:@"Account deletion"
                                                                   message:@"Are you sure ?"
                                                            preferredStyle:UIAlertControllerStyleAlert];
    [alert addAction:[UIAlertAction actionWithTitle:@"OK" style:UIAlertActionStyleDefault  handler:^(UIAlertAction * _Nonnull action) {
        [self.navigationController dismissViewControllerAnimated:YES
                                                      completion:^{
            
            KeycloakController  *key = [[KeycloakController alloc]init];
            key.logout = true;
            [self.navigationController pushViewController:key animated:YES];
            
        }];
       
        
        
    }]];
    [alert addAction:[UIAlertAction actionWithTitle:@"Cancel" style:UIAlertActionStyleDefault  handler:^(UIAlertAction * _Nonnull action) {
       
        
    }]];
    [self presentViewController:alert animated:YES completion:nil];
}

#pragma mark - UITextFieldDelegate (optional quality-of-life)

- (BOOL)textFieldShouldReturn:(UITextField *)textField {
    if (textField == self.nameField) {
        [self.surnameField becomeFirstResponder];
    } else if (textField == self.surnameField) {
        [self.emailField becomeFirstResponder];
    } else {
        [textField resignFirstResponder];
    }
    return YES;
}

@end

NS_ASSUME_NONNULL_END
