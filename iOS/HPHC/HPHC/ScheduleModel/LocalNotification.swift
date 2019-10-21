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

import UIKit
import UserNotifications

/**
 LocalNotification manages registration and refreshing notification for the activities and studies
 */
class LocalNotification: NSObject {
    
    static var studies: Array<Study> = [] //StudyList
    static var notificationList: Array<AppLocalNotification> = [] //NotificationList
    static var handler: ((Bool) -> ()) = {_ in }
    class func registerLocalNotificationForJoinedStudies(completionHandler: @escaping (Bool) -> ()) {
        
        studies = (Gateway.instance.studies?.filter({$0.userParticipateState.status == UserStudyStatus.StudyStatus.inProgress && $0.status == .Active}))!
        
        handler = completionHandler
        LocalNotification.registerForStudy()
        
    }
    
    class func registerForStudy() {
        
        if studies.count > 0 {
            
            let study = studies.first
            LocalNotification.registerForStudy(study: study!) { (done) in
                if done {
                    if (studies.count) > 0 {
                        studies.removeFirst()
                        LocalNotification.registerForStudy()
                    }
                    
                }
            }
            
        } else {
            handler(true)
        }
    }
    
    /**
     registerForStudy method registers all local notification for study and returns and completion handler with bool
     */
    class func registerForStudy(study: Study,completionHandler: @escaping (Bool) -> ()) {
        
        DBHandler.loadActivityListFromDatabase(studyId: study.studyId) { (activities) in
            if activities.count > 0 {
              
                LocalNotification.registerAllLocalNotificationFor(activities: activities, completionHandler: {
                    (done,_)   in
                    completionHandler(true)
                })
            } else {
                completionHandler(true)
            }
        }
    }
    
    /**
     registerAllLocalNotificationFor registers local notification for activities based on the frequency type and availability and returns a completion handler with status and array of localNotifications
     */
    class func registerAllLocalNotificationFor(activities: Array<Activity>,completionHandler: @escaping (Bool,Array<AppLocalNotification>) -> ()) {
        
        LocalNotification.notificationList.removeAll()
        
        let date = Date()
        for activity in activities {
            
            var runsBeforeToday: Array<ActivityRun> = []
            if activity.frequencyType == Frequency.One_Time && activity.endDate == nil {
                //runsBeforeToday = runs
                runsBeforeToday = activity.activityRuns
                
            } else {
                
                runsBeforeToday = activity.activityRuns.filter({$0.endDate >= date})
            }
            
            for run in runsBeforeToday {
                
                switch activity.frequencyType {
                case .One_Time:
                    
                    if run.endDate != nil {
                        let date = run.endDate.addingTimeInterval(-24*3600) // 24 hours before
                        let message = "The activity " + activity.name! + " will expire in 24 hours. Your participation is important. Please visit the study to complete it now."
                        
                        LocalNotification.composeRunNotification(startDate: date, endDate: run.endDate, message: message, run: run)
                    }
                    
                case .Daily:
                    if activity.frequencyRuns?.count == 1 {
                        
                        let date = run.startDate! // 24 hours before
                        let message = "A new run of the daily activity " + activity.name! + ", is now available. Your participation is important. Please visit the study to complete it now."
                        //let userInfo = [kStudyId: run.studyId,
                         //               kActivityId: run.activityId]
                        
                        LocalNotification.composeRunNotification(startDate: date, endDate: run.endDate, message: message, run: run)
                        
                    } else {
                        
                        let date = run.startDate! // 24 hours before
                        let message1 = "A new run of the daily activity " + activity.name!
                        let message2 = ", is now available and is valid until " + LocalNotification.timeFormatter.string(from: run.endDate!)
                        let messgge3 = ". Your participation is important. Please visit the study to complete it now."
                        let message = message1 + message2 + messgge3
//                        let userInfo = [kStudyId: run.studyId,
//                                        kActivityId: run.activityId]
                        
                        LocalNotification.composeRunNotification(startDate: date, endDate: run.endDate, message: message, run: run)
                    }
                    
                case .Weekly:
                    
                    //expiry notificaiton
                    let date = run.endDate.addingTimeInterval(-24*3600)
                    let message = "The current run of the weekly activity " + activity.name! + " will expire in 24 hours. Your participation is important. Please visit the study to complete it now."
//                    let userInfo = [kStudyId: run.studyId,
//                                    kActivityId: run.activityId]
                    
                    LocalNotification.composeRunNotification(startDate: date, endDate: run.endDate, message: message, run: run)
                    
                    //start notification
                    let startMessage = "A new run of the weekly activity " + activity.name! + ", is now available. Please visit the study to complete it now."
                    
                    LocalNotification.composeRunNotification(startDate: run.startDate!, endDate: run.endDate, message: startMessage, run: run)
                    
                case .Monthly:
                    
                    let date = run.endDate.addingTimeInterval(-72*3600)
                    let message = "The current run of the monthly activity " + activity.name! + " will expire in 3 days. Your participation is important. Please visit the study to complete it now."
//                    let userInfo = [kStudyId: run.studyId,
//                                    kActivityId: run.activityId]
                    LocalNotification.composeRunNotification(startDate: date, endDate: run.endDate, message: message, run: run)
                    
                    //start notification
                    let startMessage = "A new run of the monthly activity " + activity.name! + ", is now available. Please visit the study to complete it now."
                    LocalNotification.composeRunNotification(startDate: run.startDate!, endDate: run.endDate, message: startMessage, run: run)
                    
                case .Scheduled:
                    
                    let date = run.startDate! // 24 hours before
                    let endDate = LocalNotification.oneTimeFormatter.string(from: run.endDate!)
                    let message1 = "A new run of the scheduled activity " + activity.name!
                    let message2 = ", is now available and is valid until " + "\(endDate)"
                    let message3 = ". Your participation is important. Please visit the study to complete it now."
                    let message = message1 + message2 + message3
//                    let userInfo = [kStudyId: run.studyId,
//                                    kActivityId: run.activityId]
                    
                    LocalNotification.composeRunNotification(startDate: date, endDate: run.endDate, message: message, run: run)
                }
            }
        }
        completionHandler(true,LocalNotification.notificationList)
    }
    
    /**
     composeRunNotification creates a notification for the dates & message of ActivityRun specfied and saves to the notificationList
     */
    class func composeRunNotification(startDate: Date,endDate: Date,message: String,run: ActivityRun) {
        
        _ = [kStudyId: run.studyId,
                        kActivityId: run.activityId] as [String: String]
        
        //create App local notification object
        
        //let randomString = Utilities.randomString(length: 5)
        
        let notification = AppLocalNotification()
        notification.id =  String(run.runId) + run.activityId + run.studyId
        notification.message = message
        notification.activityId = run.activityId
        notification.title = ""
        notification.startDate = startDate
        notification.endDate = endDate
        notification.type = AppNotification.NotificationType.Study
        notification.subType = AppNotification.NotificationSubType.Activity
        notification.audience = Audience.Limited
        notification.studyId =  run.studyId //(Study.currentStudy?.studyId)!
        
        LocalNotification.notificationList.append(notification)
    }
    
    /**
     scheduleNotificationOn registers local notification
     */
    class func scheduleNotificationOn(date: Date,message: String,userInfo: Dictionary<String,Any> , id:String?){

        if date > Date() {
            print("NotificationMessage\(message) ** date \(date.description(with: Locale.current))" )
            let content = UNMutableNotificationContent()
            content.body = message
            content.userInfo = userInfo
            content.sound = UNNotificationSound.default
            content.badge = 1
            
            let timeInterval = date.timeIntervalSinceNow
            print("Time \(timeInterval)")
            let trigger = UNTimeIntervalNotificationTrigger(timeInterval: timeInterval, repeats: false)
            let id = id ?? Utilities.randomString(length: 10)
            let request = UNNotificationRequest(identifier: id, content: content, trigger: trigger)
            let center = UNUserNotificationCenter.current()
            center.add(request)

        }
        
    }
    
    /**
     removeLocalNotificationfor deletes the notification for studyId & activityId
     */
    class func removeLocalNotificationfor(studyId: String,activityid: String) {
        
        
        let notificationCenter = UNUserNotificationCenter.current()
        notificationCenter.getPendingNotificationRequests { (allNotificaiton) in
            
            var nIdentifers:[String] = []
            for notification in allNotificaiton {
                let userInfo = notification.content.userInfo
                if userInfo[kStudyId] != nil && userInfo[kActivityId] != nil {
                    if (userInfo[kStudyId] as! String == studyId && userInfo[kActivityId] as! String == activityid) {
                        nIdentifers.append(notification.identifier)
                    }
                }
            }
            notificationCenter.removePendingNotificationRequests(withIdentifiers: nIdentifers)
        }
        
    }
    
    /**
     removeLocalNotificationfor deletes notification based on studyId
     */
    class func removeLocalNotificationfor(studyId: String) {
        
        let notificationCenter = UNUserNotificationCenter.current()
        notificationCenter.getPendingNotificationRequests { (allNotificaiton) in
            
            var nIdentifers:[String] = []
            for notification in allNotificaiton {
                let userInfo = notification.content.userInfo
                if userInfo[kStudyId] != nil {
                    if (userInfo[kStudyId] as! String == studyId) {
                        nIdentifers.append(notification.identifier)
                    }
                }
            }
            notificationCenter.removePendingNotificationRequests(withIdentifiers: nIdentifers)
        }
        
    }
    
    /**
     cancelAllLocalNotification removes all local notification registered
     */
    class func cancelAllLocalNotification() {
        
        UNUserNotificationCenter.current().removeAllPendingNotificationRequests()
        //UIApplication.shared.cancelAllLocalNotifications()
    }
    
    
    class func registerReopenAppNotification() {
        
        let userInfo = ["registerApp": "mystudies",
                        ]
        //let date = Date().addingTimeInterval(60*60*24*14)
        let date = Date().addingTimeInterval(60)
        
        
        var infoDict: NSDictionary?
        if let path = Bundle.main.path(forResource: "Info", ofType: "plist") {
            infoDict = NSDictionary(contentsOfFile: path)
        }
        let navTitle = infoDict!["ProductTitleName"] as! String
        
        let message = "It’s been a while since you visited the \(navTitle) app. Please consider continuing your participation in any studies in which you’re enrolled."
        
    
        
        let notification = UILocalNotification()
        notification.fireDate = date
        notification.alertBody = message
        notification.alertAction = "Ok"
        notification.soundName = UILocalNotificationDefaultSoundName
        notification.userInfo = userInfo
        notification.repeatInterval = NSCalendar.Unit.day
        UIApplication.shared.scheduleLocalNotification(notification)
        
    }
    
    class func removeReopenAppNotification() {
        
        let allNotificaiton = UIApplication.shared.scheduledLocalNotifications
        
        for notification in allNotificaiton! {
            let userInfo = notification.userInfo
            if userInfo?["registerApp"] != nil {
                if (userInfo!["registerApp"] as! String == "mystudies") {
                    UIApplication.shared.cancelLocalNotification(notification)
                }
            }
        }
    }
    
    /**
     refreshAllLocalNotification cancels existing notifications and reschedules the top 50 notification from local notifications list
     */
    
    class func refreshAllLocalNotification() {
        
        //Fetch top 50 notifications
        DBHandler.getRecentLocalNotification {(localNotifications) in
            
            if localNotifications.count > 0 {
                //Cancel All Local Notifications
                LocalNotification.cancelAllLocalNotification()
                
                LocalNotification.scheduledNotificaiton()
                for notification in localNotifications {
                    
                    //Generate User Info
                    let userInfo = [kStudyId: notification.studyId!,
                                    kActivityId: notification.activityId!]
                    
                    //Reschedule top 50 Local Notifications
                    LocalNotification.scheduleNotificationOn(date: notification.startDate!, message: notification.message!, userInfo: userInfo,id: notification.id)
                    
                }
                LocalNotification.scheduledNotificaiton()
            }
        }
    }
    
    class func scheduledNotificaiton() {
        let center = UNUserNotificationCenter.current()
        center.getPendingNotificationRequests(completionHandler: { requests in
            print(requests)
            
        })
    }
    
    private static let timeFormatter: DateFormatter = {
        let formatter = DateFormatter()
        formatter.dateFormat = "hh:mma"
        return formatter
    }()
    private static let oneTimeFormatter: DateFormatter = {
        let formatter = DateFormatter()
        formatter.dateFormat = "hh:mma, MMM dd YYYY"
        return formatter
    }()
    
}

class NotificationHandler: NSObject {
    var studyId: String! = ""
    var activityId: String! = ""
    var appOpenFromNotification = false
    static var instance = NotificationHandler()
}
