//
//  AppDelegate.h
//  kxmovieapp
//
//  Created by Kolyvan on 11.10.12.
//  Copyright (c) 2012 Konstantin Boukreev . All rights reserved.
//
//  https://github.com/kolyvan/kxmovie
//  this file is part of KxMovie
//  KxMovie is licenced under the LGPL v3, see lgpl-3.0.txt

#import <UIKit/UIKit.h>


@class KxMovieViewController;

@interface AppDelegate : UIResponder <UIApplicationDelegate>

@property (strong, nonatomic) UIWindow *window;
@property (readonly, strong) NSPersistentContainer *persistentContainer;
@property (strong,nonatomic) NSManagedObjectContext * _context;
@property (strong,nonatomic) NSManagedObjectContext * managedObjectContext;
@property (strong,nonatomic) UIViewController * vc;
- (void)saveContext;
@end
