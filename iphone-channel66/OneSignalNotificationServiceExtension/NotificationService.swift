import UserNotifications
import CoreData
import OneSignal


class NotificationService: UNNotificationServiceExtension {
    
    var contentHandler: ((UNNotificationContent) -> Void)?
    var receivedRequest: UNNotificationRequest!
    var bestAttemptContent: UNMutableNotificationContent?
    
    override func didReceive(_ request: UNNotificationRequest, withContentHandler contentHandler: @escaping (UNNotificationContent) -> Void) {
        self.receivedRequest = request;
        self.contentHandler = contentHandler
        bestAttemptContent = (request.content.mutableCopy() as? UNMutableNotificationContent)
        
        if let bestAttemptContent = bestAttemptContent {
            OneSignal.didReceiveNotificationExtensionRequest(self.receivedRequest, with: self.bestAttemptContent)
            contentHandler(bestAttemptContent)
            
            ////////////
            //var context = DataStore.getInstance().managedObjectContext;
//            var context2 = PortDelegate.persistentContainer;
//            
//            // Create a new managed object
//            NSManagedObject *messages = [NSEntityDescription insertNewObjectForEntityForName:@"Messages" inManagedObjectContext:context];
//            [messages setValue:[payload body] forKey:@"text"];
//            NSDateFormatter *dateFormatter=[[NSDateFormatter alloc] init];
//            [dateFormatter setDateFormat:@"yyyy-MM-dd HH:mm:ss"];
//            
//            [messages setValue:[NSDate date] forKey:@"date"];
//            [messages willSave];
//            
//            NSError *error = nil;
//            // Save the object to persistent store
//            if (![context save:&error]) {
//                NSLog(@"Can't Save! %@ %@", error, [error localizedDescription]);
//            }
        }
    }
    
    override func serviceExtensionTimeWillExpire() {
        // Called just before the extension will be terminated by the system.
        // Use this as an opportunity to deliver your "best attempt" at modified content, otherwise the original push payload will be used.
        if let contentHandler = contentHandler, let bestAttemptContent =  bestAttemptContent {
            OneSignal.serviceExtensionTimeWillExpireRequest(self.receivedRequest, with: self.bestAttemptContent)
            contentHandler(bestAttemptContent)
        }
    }
//    func handleContent(UNNotificationContent content)
//    {
//
//    }
    
}
