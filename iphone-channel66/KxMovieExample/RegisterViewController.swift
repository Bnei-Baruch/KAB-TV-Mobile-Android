//
//  RegisterViewController.swift
//  KxMovieExample
//
//  Created by Igal Avraham on 11/12/2018.
//

import UIKit
import OneSignal

class RegisterViewController: UIViewController {

    @IBOutlet weak var name : UITextField?;
    @IBOutlet weak var group : UITextField?;
    @IBOutlet weak var email : UITextField?;
    @IBOutlet weak var number : UITextField?;
    @IBOutlet weak var gender : UISegmentedControl?;
    @IBOutlet weak var register : UIButton?;
    
    
    @IBAction func registerMessages(_ sender: UITapGestureRecognizer) {
        NSLog("test");
        var data:[String:String] = [:]
        data["name"] = name?.text;
        data["email"] = email?.text;
        data["number"] = number?.text;
        data["group"] = group?.text;
        let genderInt  = gender?.selectedSegmentIndex;
        var genderStr = "";
        if (genderInt == 0)
        {
         genderStr = "Female"
        }
        else
        {
            genderStr = "Male"
        }
        
        data["gender"] = genderStr;
        data["timezone"] =  TimeZone.current.abbreviation();
     
        do
        {
        let jsonData = try JSONSerialization.data(withJSONObject: data, options: .prettyPrinted)
            let decoded = try JSONSerialization.jsonObject(with: jsonData, options: [])
            // here "decoded" is of type `Any`, decoded from JSON data
            
            // you can now cast it with the right type
            if let dictFromJSON = decoded as? [String:String] {
                // use dictFromJSON
                OneSignal.sendTags(dictFromJSON)
                let defaults = UserDefaults.standard
                defaults.set(true, forKey:"Registered")
                OneSignal.setSubscription(true);
              self.dismiss(animated: true, completion: nil)
            }
            
    } catch {
    print(error.localizedDescription)
    }
        
    }
    
    override func viewDidLoad() {
        super.viewDidLoad()

        // Do any additional setup after loading the view.
    }


    /*
    // MARK: - Navigation

    // In a storyboard-based application, you will often want to do a little preparation before navigation
    override func prepare(for segue: UIStoryboardSegue, sender: Any?) {
        // Get the new view controller using segue.destination.
        // Pass the selected object to the new view controller.
    }
    */

}
