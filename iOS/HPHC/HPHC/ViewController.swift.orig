//
//  ViewController.swift
//  FDA
//
//  Created by Arun Kumar on 2/2/17.
//  Copyright © 2017 BTC. All rights reserved.
//

import UIKit
import ResearchKit

<<<<<<< HEAD
let user = User()
let activitybuilder:ActivityBuilder? = ActivityBuilder()
=======
let user = User.currentUser

>>>>>>> suri_develop

let resourceArray : Array<Any>? = nil
class ViewController: UIViewController,ORKTaskViewControllerDelegate {
    
    
    override func viewDidLoad() {
        super.viewDidLoad()
        // Do any additional setup after loading the view, typically from a nib.
        
        self.userProfile()
        self.setPrefereneces()
        self.addResources()
        
       // self.buildTask()
        
        user.bookmarkStudy(studyId: "121")
        
        user.updateStudyStatus(studyId: "121", status:.yetToJoin)
        
        user.bookmarkActivity(studyId: "121", activityId: "151")
        
        print(user.getStudyStatus(studyId: "121").description)
        
    }

    
    
    //MARK:ORKTaskViewController Delegate
    
    
    func taskViewControllerSupportsSaveAndRestore(_ taskViewController: ORKTaskViewController) -> Bool {
        return true
    }
    
    public func taskViewController(_ taskViewController: ORKTaskViewController, didFinishWith reason: ORKTaskViewControllerFinishReason, error: Error?) {
        
        var taskResult:Any?
        
        switch reason {
            
        case ORKTaskViewControllerFinishReason.completed:
            print("completed")
            taskResult = taskViewController.result
        case ORKTaskViewControllerFinishReason.failed:
            print("failed")
            taskResult = taskViewController.result
        case ORKTaskViewControllerFinishReason.discarded:
            print("discarded")
            taskResult = taskViewController.result
        case ORKTaskViewControllerFinishReason.saved:
            print("saved")
            taskResult = taskViewController.restorationData
             activitybuilder?.activity?.restortionData = taskViewController.restorationData
        }
        
        
       
        activitybuilder?.actvityResult?.initWithORKTaskResult(taskResult: taskViewController.result)
        
       
        
        taskViewController.dismiss(animated: true, completion: nil)
        
    }
    
    func taskViewController(_ taskViewController: ORKTaskViewController, stepViewControllerWillAppear stepViewController: ORKStepViewController) {
        
        
    }
    
    //MARK:StepViewController Delegate
    public func stepViewController(_ stepViewController: ORKStepViewController, didFinishWith direction: ORKStepViewControllerNavigationDirection){
        
    }
    
    public func stepViewControllerResultDidChange(_ stepViewController: ORKStepViewController){
        
    }
    public func stepViewControllerDidFail(_ stepViewController: ORKStepViewController, withError error: Error?){
        
    }
    
   
    //MARK: methods
    
    func buildTask()  {
        
        let filePath  = Bundle.main.path(forResource: "ActiveTask", ofType: "json")
        
        let data = NSData(contentsOfFile: filePath!)
        
        
        do {
            let dataDict = try JSONSerialization.jsonObject(with: data! as Data, options: []) as? Dictionary<String,Any>
            
            if  Utilities.isValidObject(someObject: dataDict as AnyObject?) && (dataDict?.count)! > 0 {
                
               
                let task:ORKTask?
                let taskViewController:ORKTaskViewController?
                
                if Utilities.isValidObject(someObject: dataDict?["Result"] as? Dictionary<String, Any> as AnyObject?){
                
                
                activitybuilder?.initActivityWithDict(dict: dataDict?["Result"] as! Dictionary<String, Any>)
                
                    
                    
                task = activitybuilder?.createTask()
                
                
                taskViewController = ORKTaskViewController(task:task, taskRun: nil)
                taskViewController?.delegate = self
                taskViewController?.outputDirectory = FileManager.default.urls(for: .documentDirectory, in: .userDomainMask).first!
                present(taskViewController!, animated: true, completion: nil)
                }
            }
            
            // use anyObj here
        } catch {
            print("json error: \(error.localizedDescription)")
        }

       
        
        
    }
    
    
   
    
    
    func addResources()  {
        
        if let path = Bundle.main.path(forResource: "Resources", ofType: "plist") {
            
           if let responseArray = NSArray(contentsOfFile: path) {
            
            if Utilities.isValidObject(someObject: responseArray) {
            
                for i in 0 ..< responseArray.count {
                    
                    if Utilities.isValidObject(someObject:responseArray[i] as AnyObject? )  {
                        let resource:Resource? = Resource()
                        
                        resource?.setResource(dict:(responseArray[i] as? NSDictionary)! )
                        responseArray.adding(resource as Any)
                    }
                    
                }
            }
            
            }
            
            if let dict = NSDictionary(contentsOfFile: path) as? [String:Any] {
                user.setUser(dict:dict as NSDictionary)
                
                Logger.sharedInstance.debug(dict)
                Logger.sharedInstance.info(dict)
                Logger.sharedInstance.error(dict)
                
                
            }
        }
        
    }

    
    
    func userProfile()  {
        
        if let path = Bundle.main.path(forResource: "UserProfile", ofType: "plist") {
            if let dict = NSDictionary(contentsOfFile: path) as? [String:Any] {
                user.setUser(dict:dict as NSDictionary)
            
                Logger.sharedInstance.debug(dict)
                Logger.sharedInstance.info(dict)
                Logger.sharedInstance.error(dict)
              
                
            }
        }
        
    }
    
    func setPrefereneces()  {
        
        if let path = Bundle.main.path(forResource: "UserPreferences", ofType: "plist") {
            if let dict = NSDictionary(contentsOfFile: path) as? [String:Any] {
                user.setUser(dict:dict as NSDictionary)
                
                //studies
                let studies = dict[kStudies] as! Array<Dictionary<String, Any>>
                
                for study in studies {
                    let participatedStudy = UserStudyStatus(detail: study)
                    user.participatedStudies.append(participatedStudy)
                }
                
                //activities
                let activites = dict[kActivites]  as! Array<Dictionary<String, Any>>
                for activity in activites {
                    let participatedActivity = UserActivityStatus(detail: activity)
                    user.participatedActivites.append(participatedActivity)
                }
                
            }
        }
        
    }

    
    
    

    
    
    override func didReceiveMemoryWarning() {
        super.didReceiveMemoryWarning()
        // Dispose of any resources that can be recreated.
    }


}


