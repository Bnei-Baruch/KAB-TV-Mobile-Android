//
//  MenuViewController.h
//  kxmovie
//
//  Created by Yuval Ovadia on 3/8/13.
//
//

#import <UIKit/UIKit.h>

@interface MenuViewController : UIViewController <UITableViewDataSource, UITableViewDelegate, UIAlertViewDelegate> {
    int fromWhere;
}


@property int fromWhere;

@end
