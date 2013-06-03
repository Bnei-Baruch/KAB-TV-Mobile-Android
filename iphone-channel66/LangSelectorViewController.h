//
//  ViewController.h
//  kxmovie
//
//  Created by Igal Avraham on 5/21/13.
//
//

#import <UIKit/UIKit.h>
#import "CXMLDocument.h"
#import "CXMLElement.h"
#import "GAITrackedViewController.h"
#import "AudioWebViewController.h"

@interface LangSelectorViewController : UITableViewController <UITableViewDataSource>

{
    NSArray * langName;
    NSArray * locales;
    Boolean mFromActionview;
   // AudioWebViewController *audiocontroller;
}

//@property (retain, nonatomic) AudioWebViewController *audiocontroller;
@property (nonatomic,assign) Boolean  mFromActionview;

- (void) dismissAudio;
@end
