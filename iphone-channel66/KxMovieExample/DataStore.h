//
//  DataStore.h
//  kxmovie
//
//  Created by Igal Avraham on 23/10/2016.
//
//

#ifndef DataStore_h
#define DataStore_h

@interface DataStore:NSObject


@property (strong,nonatomic) NSManagedObjectContext * managedObjectContext;

+(DataStore*)getInstance;
@end


#endif /* DataStore_h */
