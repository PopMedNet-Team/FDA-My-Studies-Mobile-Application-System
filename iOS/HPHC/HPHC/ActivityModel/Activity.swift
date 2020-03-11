/*
 License Agreement for FDA My Studies
Copyright © 2017-2019 Harvard Pilgrim Health Care Institute (HPHCI) and its Contributors. Permission is
hereby granted, free of charge, to any person obtaining a copy of this software and associated
documentation files (the &quot;Software&quot;), to deal in the Software without restriction, including without
limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the
Software, and to permit persons to whom the Software is furnished to do so, subject to the following
conditions:
The above copyright notice and this permission notice shall be included in all copies or substantial
portions of the Software.
Funding Source: Food and Drug Administration (“Funding Agency”) effective 18 September 2014 as
Contract no. HHSF22320140030I/HHSF22301006T (the “Prime Contract”).
THE SOFTWARE IS PROVIDED &quot;AS IS&quot;, WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED,
INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR
PURPOSE AND NON-INFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE
LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT
OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR
OTHER DEALINGS IN THE SOFTWARE.
 */

import Foundation
import ResearchKit

// MARK: Api Constants
let kActivityType = "type"
let kActivityInfoMetaData = "metadata"

let kActivityInfo = "info"

let kActivityResponseData = "data"

let kActivityStudyId = "studyId"
let kActivityActivityId = "qId"
let kActivityName = "name"



let kActivityConfiguration = "configuration"

let kActivityFrequency = "frequency"
let kActivityFrequencyRuns = "runs"
let kActivityManualAnchorRuns = "anchorRuns"
let kActivityFrequencyType = "type"

let kActivityStartTime = "startTime"
let kActivityEndTime = "endTime"

let kActivitySteps = "steps"

// schedule Api Keys

let kActivityLifetime = "lifetime"
let kActivityRunLifetime = "runLifetime"


//questionnaireConfiguration
let kActivityBranching = "branching"
let kActivityRandomization = "randomization"


let kActivityLastModified = "lastModified"
let kActivityTaskSubType = "taskSubType"

enum ActivityType: String {
    case Questionnaire = "questionnaire"
    case activeTask = "task"
}

enum Frequency: String {
    case One_Time = "One time"
    case Daily = "Daily"
    case Weekly = "Weekly"
    case Monthly = "Monthly"
    case Scheduled = "Manually Schedule"
    
    var description: String {
        switch self {
        case .One_Time:
            return "One Time"
        case .Daily:
            return "Daily"
        case .Weekly:
            return "Weekly"
        case .Monthly:
            return "Monthly"
        case .Scheduled:
            return "As Scheduled"
        
        }
    }
    
}
enum ActivityState: String {
    case active
    case deleted
}

enum ActivityScheduleType: String {
    case regular = "Regular"
    case anchorDate = "AnchorDate"
}

enum AnchorDateSourceType: String {
    case enrollmentDate = "EnrollmentDate"
    case activityResponse = "ActivityResponse"
}

/**
 Model Activity represents a Questionery or Active Task
*/
class Activity {
    
    var type: ActivityType?
    var actvityId: String? // Unique id of each activity
    
    var studyId: String?
    var name: String? //this will come in activity list used to display
    var shortName: String? //this will come in meta data
    var version: String?
    var state: String?
    var lastModified: Date?
    var userStatus: UserActivityStatus.ActivityStatus = .yetToJoin
    var startDate: Date?
    var endDate: Date?
    var branching: Bool?
    var randomization: Bool?
    
    var schedule: Schedule?
    var steps: Array<Dictionary<String,Any>>? = []
    var orkSteps: Array<ORKStep>? = [] //array of ORKSteps stores each step involved in Questionary
    var activitySteps: Array<ActivityStep>? = []
    
    var frequencyRuns: Array<Dictionary<String, Any>>? = []
    var anchorRuns: Array<Dictionary<String, Any>>? = [] // scheduled mannuly for anchor date
    var frequencyType: Frequency = .One_Time
    
    var result: ActivityResult?

    var restortionData: Data? //stores the restortionData for current activity
    var totalRuns = 0
    var currentRunId = 1
    var compeltedRuns = 0
    var incompletedRuns = 0
    var activityRuns: Array<ActivityRun>! = []
    var currentRun: ActivityRun! = nil
    var userParticipationStatus: UserActivityStatus! = nil
    var taskSubType: String? = "" //used for active tasks
    var anchorDate:AnchorDate? = nil
    var schedulingType:ActivityScheduleType = .regular
    
     //Default Initializer
    init() {
       
        self.type = .Questionnaire
        
        self.actvityId = ""
        // info
        self.studyId = ""
        self.name = ""
        //self.version = "0"
        self.lastModified = nil
        self.userStatus = .yetToJoin
        self.startDate = nil
        self.endDate = nil
        
        self.shortName = ""
        self.taskSubType = ""
        
        // questionnaireConfigurations
        self.branching = false
        self.randomization = false
        
        // Steps
        self.steps = Array()
        
        self.schedule = nil
        self.result = nil
        self.restortionData = Data()
        self.orkSteps =  Array<ORKStep>()
        
        self.activitySteps = Array<ActivityStep>()
        
        self.frequencyRuns = Array<Dictionary<String, Any>>() // contains the runs of Activity
        self.frequencyType = .One_Time
    }
    
    // MARK:Initializer Methods
    init(studyId: String,infoDict: Dictionary<String,Any>) {
        
        self.studyId = studyId
        
        //Need to reCheck with actual dictionary when passed
        if Utilities.isValidObject(someObject: infoDict as AnyObject?) {
            
            if Utilities.isValidValue(someObject: infoDict[kActivityId] as AnyObject) {
                self.actvityId = (infoDict[kActivityId] as? String)!
            }
            
            if Utilities.isValidValue(someObject: infoDict[kActivityVersion] as AnyObject) {
                self.version = (infoDict[kActivityVersion] as? String)!
            }
            
            
            if Utilities.isValidValue(someObject: infoDict[kActivityTitle] as AnyObject) {
                self.name = (infoDict[kActivityTitle] as? String)!
            }
            if Utilities.isValidValue(someObject: infoDict["state"] as AnyObject) {
                self.state = (infoDict["state"] as? String)!
            }
            if Utilities.isValidValue(someObject: infoDict[kActivityBranching] as AnyObject ) {
                self.branching = (infoDict[kActivityBranching] as? Bool)!
            }
            if Utilities.isValidValue(someObject: infoDict[kActivityType] as AnyObject) {
                self.type = ActivityType(rawValue: (infoDict[kActivityType] as? String)!)
            }
            
            if Utilities.isValidValue(someObject: infoDict[kActivityStartTime] as AnyObject) {
                self.startDate =  Utilities.getDateFromStringWithOutTimezone(dateString: (infoDict[kActivityStartTime] as? String)!)
            }
            if Utilities.isValidValue(someObject: infoDict["schedulingType"] as AnyObject) {
                let scheduleValue = infoDict["schedulingType"] as? String ?? "Regular"
                self.schedulingType = ActivityScheduleType(rawValue: scheduleValue)! 
            }
            if Utilities.isValidValue(someObject: infoDict[kActivityEndTime] as AnyObject ) {
                self.endDate =  Utilities.getDateFromStringWithOutTimezone(dateString: (infoDict[kActivityEndTime] as? String)!)
            }
            
            if Utilities.isValidObject(someObject: infoDict[kActivityFrequency] as AnyObject?) {
                
                let frequencyDict: Dictionary = (infoDict[kActivityFrequency] as? Dictionary<String, Any>)!
                
                if Utilities.isValidObject(someObject: frequencyDict[kActivityFrequencyRuns] as AnyObject ) {
                    self.frequencyRuns =  frequencyDict[kActivityFrequencyRuns] as? Array<Dictionary<String,Any>>
                }
                if Utilities.isValidObject(someObject: frequencyDict[kActivityManualAnchorRuns] as AnyObject ) {
                    self.anchorRuns =  frequencyDict[kActivityManualAnchorRuns] as? Array<Dictionary<String,Any>>
                }
                
                if Utilities.isValidValue(someObject: frequencyDict[kActivityFrequencyType] as AnyObject ){
                    self.frequencyType =  Frequency(rawValue: (frequencyDict[kActivityFrequencyType] as? String)! )!
                }
                
            }
            
            //AnchorDate
            let anchorDateDetail = infoDict["anchorDate"] as? [String:Any]
            if (anchorDateDetail != nil && self.schedulingType == .anchorDate) {
                setActivityAvailability(anchorDateDetail ?? [:])
            }
            
            
            let currentUser = User.currentUser
            if let userActivityStatus = currentUser.participatedActivites.filter({$0.activityId == self.actvityId && $0.studyId == self.studyId}).first {
                self.userParticipationStatus = userActivityStatus
                
            } else {
                self.userParticipationStatus = UserActivityStatus()
            }
            
            if Utilities.isValidValue(someObject: infoDict[kActivityTaskSubType] as AnyObject ) {
                self.taskSubType =  (infoDict[kActivityTaskSubType] as? String)!
            }
           
            if self.startDate != nil && (self.schedulingType == .regular || self.anchorDate?.sourceType == "EnrollmentDate"){
                self.calculateActivityRuns(studyId: self.studyId!)
            }
        } else {
            Logger.sharedInstance.debug("infoDict is null:\(infoDict)")
        }
        
    }
    
    // MARK: Setter Methods
    
    // method to set  ActivityMetaData
    func setActivityMetaData(activityDict: Dictionary<String,Any>) {
        
        if Utilities.isValidObject(someObject: activityDict as AnyObject?) {
            
            if Utilities.isValidValue(someObject: activityDict[kActivityType] as AnyObject ) {
                self.type? =  ActivityType(rawValue: (activityDict[kActivityType] as? String)!)!
               
            }
            self.setInfo(infoDict: (activityDict[kActivityInfoMetaData] as? Dictionary<String,Any>)!)
            
            if Utilities.isValidObject(someObject: activityDict[kActivitySteps] as AnyObject?) {
                 self.setStepArray(stepArray: (activityDict[kActivitySteps] as? Array)! )
                
            } else {
                Logger.sharedInstance.debug("infoDict is null:\(String(describing: activityDict[kActivitySteps]))")
            }
        } else {
            Logger.sharedInstance.debug("infoDict is null:\(activityDict)")
        }
    }
    
    
    // method to set info part of activity from ActivityMetaData
    func setInfo(infoDict: Dictionary<String,Any>) {
        
        if Utilities.isValidObject(someObject: infoDict as AnyObject?) {
         
            if Utilities.isValidValue(someObject: infoDict["name"] as AnyObject ) {
                self.shortName =   infoDict["name"] as? String
            }
            
            if Utilities.isValidValue(someObject: infoDict[kActivityVersion] as AnyObject ) {
                self.version =  infoDict[kActivityVersion] as? String
            }
            if Utilities.isValidValue(someObject: infoDict[kActivityStartTime] as AnyObject ) {
                //self.startDate =  Utilities.getDateFromString(dateString: (infoDict[kActivityStartTime] as! String?)!)
            }
            if Utilities.isValidValue(someObject: infoDict[kActivityEndTime] as AnyObject ) {
                //self.endDate =   Utilities.getDateFromString(dateString: (infoDict[kActivityEndTime] as! String?)!)
            }
            if Utilities.isValidValue(someObject: infoDict[kActivityLastModified] as AnyObject ) {
                //self.lastModified =   Utilities.getDateFromString(dateString: (infoDict[kActivityLastModified] as! String?)!)
            }
            
        } else {
            Logger.sharedInstance.debug("infoDict is null:\(infoDict)")
        }
    }
    
    // method to set Configration
    func setConfiguration(configurationDict: Dictionary<String,Any>)  {
        
        if Utilities.isValidObject(someObject: configurationDict as AnyObject?) {
            if Utilities.isValidValue(someObject: configurationDict[kActivityBranching] as AnyObject) {
                self.branching =   configurationDict[kActivityBranching] as? Bool
            }
            if Utilities.isValidValue(someObject: configurationDict[kActivityRandomization] as AnyObject) {
                self.randomization =   configurationDict[kActivityId] as? Bool
            }
        } else {
            Logger.sharedInstance.debug("configurationDict is null:\(configurationDict)")
        }
    }
    
    func setActivityAvailability(_ availability:[String:Any]) {

        self.anchorDate = AnchorDate.init(availability)
        //Issue: Crash with joining date for first time
        if self.anchorDate?.sourceType == "EnrollmentDate" {
            var enrollmentDate = Study.currentStudy?.userParticipateState.joiningDate
            
            //update start date
            var startDateStringEnrollment =  Utilities.formatterShort?.string(from: enrollmentDate!)
            let startTimeEnrollment =  "00:00:00"
            startDateStringEnrollment = (startDateStringEnrollment ?? "") + " " + startTimeEnrollment
            enrollmentDate = Utilities.findDateFromString(dateString: startDateStringEnrollment ?? "")
            
            self.anchorDate?.anchorDateValue = enrollmentDate
            let lifeTime = self.updateLifeTime(self.anchorDate!, frequency: self.frequencyType)
           
            //update start date
            var startDateString =  Utilities.formatterShort?.string(from: lifeTime.0!)
            let startTime =  (self.anchorDate?.startTime == nil) ? "00:00:00" : (self.anchorDate?.startTime)!
            startDateString = (startDateString ?? "") + " " + startTime
            let startdate = Utilities.findDateFromString(dateString: startDateString ?? "")
            
            //update end date
            var endDateString =  Utilities.formatterShort?.string(from: lifeTime.1!)
            let endTime =  (self.anchorDate?.endTime == nil) ? "00:00:00" : (self.anchorDate?.endTime)!
            endDateString = (endDateString ?? "") + " " + endTime
            let endDate = Utilities.findDateFromString(dateString: endDateString ?? "")
            
            self.startDate = startdate
            self.endDate = endDate
        }
        
    }
    
     //method to set step array
    func setStepArray(stepArray: Array<Dictionary<String,Any>>) {
       
        if Utilities.isValidObject(someObject: stepArray as AnyObject?){
            self.steps? = stepArray
        } else {
            Logger.sharedInstance.debug("stepArray is null:\(stepArray)")
        }
    }
    
    func setORKSteps(orkStepArray: [ORKStep])  {
        if Utilities.isValidObject(someObject: orkStepArray as AnyObject?) {
            self.orkSteps = orkStepArray
        } else {
            Logger.sharedInstance.debug("stepArray is null:\(orkStepArray)")
        }
        
    }
    
    //method to set step array
    func setActivityStepArray(stepArray: Array<ActivityStep>) {
        
        if Utilities.isValidObject(someObject: stepArray as AnyObject?) {
            self.activitySteps? = stepArray
        } else {
            Logger.sharedInstance.debug("stepArray is null:\(stepArray)")
        }
    }
    
    func calculateActivityRuns(studyId: String) {
        
        Schedule().getRunsForActivity(activity: self, handler: { (runs) in
            if runs.count > 0 {
                self.activityRuns = runs
            }
        })
    }
    
    func getRestortionData() -> Data {
        return self.restortionData!
    }
    func setRestortionData(restortionData: Data)  {
        self.restortionData = restortionData
    }
    
    func updateLifeTime(_ anchorDate:AnchorDate, frequency:Frequency) -> (Date?,Date?){
        guard let date = anchorDate.anchorDateValue else {
            return (nil,nil)
        }
        
        return updateLifeTime(date, frequency: frequency)
        
    }
    
    func updateLifeTime(_ date:Date, frequency:Frequency) -> (Date?,Date?) {
        
        var startDate:Date!
        var endDate:Date!
        
        switch frequency {
        case .One_Time:
            
            let startDateInterval = TimeInterval(60*60*24*(self.anchorDate?.startDays)!)
            let endDateInterval = TimeInterval(60*60*24*(self.anchorDate?.endDays)!)
            
            startDate = date.addingTimeInterval(startDateInterval)
            endDate = date.addingTimeInterval(endDateInterval)
            
        case .Daily:
            
            let startDateInterval = TimeInterval(60*60*24*(self.anchorDate?.startDays)!)
            let endDateInterval = TimeInterval(60*60*24*(self.anchorDate?.repeatInterval)!)
            startDate = date.addingTimeInterval(startDateInterval)
            endDate = startDate.addingTimeInterval(endDateInterval)
            
        case .Weekly:
           
            let startDateInterval = TimeInterval(60*60*24*(self.anchorDate?.startDays)!)
            let endDateInterval = TimeInterval(60*60*24*7*(self.anchorDate?.repeatInterval)!)
            startDate = date.addingTimeInterval(startDateInterval)
            endDate = startDate.addingTimeInterval(endDateInterval)
        case .Monthly:
            
            let startDateInterval = TimeInterval(60*60*24*(self.anchorDate?.startDays)!)
            //let endDateInterval = TimeInterval(-1)
            startDate = date.addingTimeInterval(startDateInterval)
            let calender = Calendar.current
            endDate = calender.date(byAdding: .month, value: (self.anchorDate?.repeatInterval)!, to: startDate)
            //endDate = endDate.addingTimeInterval(endDateInterval)
        case .Scheduled:
            
            let startDateInterval = TimeInterval(60*60*24*(self.anchorDate?.startDays)!)
            let endDateInterval = TimeInterval(60*60*24*(self.anchorDate?.endDays)!)
            
            startDate = date.addingTimeInterval(startDateInterval)
            endDate = date.addingTimeInterval(endDateInterval)
            
        }
        
        return (startDate,endDate)
    }
    
}


class AnchorDate {
    
    var sourceType:String?
    var sourceActivityId:String?
    var sourceKey:String?
    var sourceFormKey:String?
    var startDays:Int = 0
    var startTime:String?
    var endDays:Int = 0
    var repeatInterval:Int = 0
    var endTime:String?
    var anchorDateValue:Date?
    
    init() {
        
    }
    init(_ anchorDateDetail:[String:Any]) {
        
        self.sourceType = anchorDateDetail["sourceType"] as? String
        self.sourceActivityId = anchorDateDetail["sourceActivityId"] as? String
        self.sourceKey = anchorDateDetail["sourceKey"] as? String
        self.sourceFormKey = anchorDateDetail["sourceFormKey"] as? String
        
        let anchorStart = anchorDateDetail["start"] as? [String:Any]
        self.startDays = anchorStart?["anchorDays"] as? Int ?? 0
        self.startTime = anchorStart?["time"] as? String
        
        let anchorEnd = anchorDateDetail["end"] as? [String:Any]
        self.endDays = anchorEnd?["anchorDays"] as? Int ?? 0
        self.endTime = anchorEnd?["time"] as? String
        self.repeatInterval = anchorEnd?["repeatInterval"] as? Int ?? 0
    
    }
    
    func updateUserResponseAnchorDate(_ response:[String:Any]) {
        //let results = response["results"] as! Array<Dictionary<String,Any>> //[[String : Any]]
        //"studyId == %@",studyId
        //let anchorDateResult = results.filter("key == %@","self.sourceKey")
    }
}		

