//
//  Messages+CoreDataProperties.swift
//  kxmovie
//
//  Created by Igal Avraham on 21/10/2016.
//
//

import Foundation
import CoreData


extension Messages {

    @nonobjc public class func fetchRequest() -> NSFetchRequest<Messages> {
        return NSFetchRequest<Messages>(entityName: "Messages");
    }

    @NSManaged public var date: NSDate?
    @NSManaged public var text: String?

}
