//
//  DataStore.m
//  kxmovie
//
//  Created by Igal Avraham on 23/10/2016.
//
//

#import <Foundation/Foundation.h>
#import "DataStore.h"


@implementation DataStore : NSObject

static DataStore *instance=nil;

+(DataStore *)getInstance
{
    @synchronized(self)
    {
        if(instance==nil)
        {
            instance= [DataStore new];
            [instance setupManagedObjectContext];
        }
    }
    return instance;
}
- (void)setupManagedObjectContext
{
    NSURL *nsurl =[[NSBundle mainBundle] URLForResource:@"Message" withExtension:@"momd"];
    
    NSManagedObjectModel * managedObjectModel = [[NSManagedObjectModel alloc]initWithContentsOfURL:nsurl];
    self.managedObjectContext =
    [[NSManagedObjectContext alloc] initWithConcurrencyType:NSMainQueueConcurrencyType];
    self.managedObjectContext.persistentStoreCoordinator =
    [[NSPersistentStoreCoordinator alloc] initWithManagedObjectModel:managedObjectModel];
    NSError* error;
    [self.managedObjectContext.persistentStoreCoordinator
     addPersistentStoreWithType:NSSQLiteStoreType
     configuration:nil
     URL:[self storeURL]
     options:nil
     error:&error];
    if (error) {
        NSLog(@"error: %@", error);
    }
    self.managedObjectContext.undoManager = [[NSUndoManager alloc] init];
}
- (NSURL*)storeURL
{
    NSURL* documentsDirectory = [[NSFileManager defaultManager] URLForDirectory:NSDocumentDirectory inDomain:NSUserDomainMask appropriateForURL:nil create:YES error:NULL];
    return [documentsDirectory URLByAppendingPathComponent:@"db.sqlite"];
}
@end

