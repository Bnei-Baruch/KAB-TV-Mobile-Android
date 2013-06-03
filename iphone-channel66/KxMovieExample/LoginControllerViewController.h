//
//  LoginControllerViewController.h
//  kxmovie
//
//  Created by Igal Avraham on 12/16/12.
//
//

#import <UIKit/UIKit.h>
#import "CXMLDocument.h"
#import "CXMLElement.h"
#import "GAITrackedViewController.h"


@interface LoginControllerViewController : GAITrackedViewController <UIActionSheetDelegate, UIWebViewDelegate>
{
   IBOutlet UIWebView *mWebview;
    Boolean mFromActionview;
    NSDictionary *jsonData;
    NSMutableData *receivedData;
    
}

@property(nonatomic,retain) NSMutableData *receivedData;
@property (nonatomic,assign) Boolean mFromActionview;
@property (nonatomic,retain) UIWebView *mWebview;
@end
