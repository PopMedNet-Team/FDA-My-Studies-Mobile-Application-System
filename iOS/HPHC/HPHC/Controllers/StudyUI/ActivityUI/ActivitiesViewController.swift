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
import UIKit
import ResearchKit
import IQKeyboardManagerSwift

let kActivities = "activities"

let kActivityUnwindToStudyListIdentifier = "unwindeToStudyListIdentier"
let kActivityAbondonedAlertMessage = "You missed the previous run of this activity. Please wait till the next run becomes available. Run timings are given on the Activities list screen."

enum ActivityAvailabilityStatus:Int{
    case current
    case upcoming
    case past
}

class ActivitiesViewController : UIViewController{
    
    @IBOutlet var tableView : UITableView?
    @IBOutlet var labelNoNetworkAvailable: UILabel?
    
    var tableViewSections: Array<Dictionary<String,Any>>! = []
    var lastFetelKickIdentifer: String = ""  //TEMP
    var selectedIndexPath: IndexPath? = nil
    var isAnchorDateSet: Bool = false
    var taskControllerPresented = false
    var refreshControl: UIRefreshControl? //To fetch the updated Activities
    
    var allActivityList: Array<Dictionary<String,Any>>! = []
    var selectedFilter: ActivityFilterType? //Holds the applied FilterTypes
    
    private var managedResult: [String: Any] = [:]
    
    let labkeyResponseFetch = ResponseDataFetch()
    
    // MARK:- Viewcontroller Lifecycle
    fileprivate func presentUpdatedConsent() {
        let appDelegate = (UIApplication.shared.delegate as? AppDelegate)!
        appDelegate.checkConsentStatus(controller: self)
    }
    
    override func viewDidLoad() {
        super.viewDidLoad()
        
        selectedFilter = ActivityFilterType.all
        
        self.tableView?.estimatedRowHeight = 126
        self.tableView?.rowHeight = UITableView.automaticDimension
        
        self.tabBarController?.delegate = self
        
        self.navigationItem.title = NSLocalizedString("STUDY ACTIVITIES", comment: "")
        self.tableView?.sectionHeaderHeight = 30
        
        self.navigationController?.navigationItem.rightBarButtonItem?.tintColor = UIColor.gray
        
        if (Study.currentStudy?.studyId) != nil {
            if StudyUpdates.studyConsentUpdated {
                NotificationHandler.instance.activityId = ""
                presentUpdatedConsent()
            }
        }
        
        //create refresh control for pull to refresh
        refreshControl = UIRefreshControl()
        refreshControl?.attributedTitle = NSAttributedString(string: "Pull to refresh")
        refreshControl?.addTarget(self, action: #selector(refresh(sender:)), for: UIControl.Event.valueChanged)
        tableView?.addSubview(refreshControl!)
    }
    
    override func viewWillAppear(_ animated: Bool) {
        super.viewWillAppear(animated)
        
        self.navigationController?.interactivePopGestureRecognizer?.isEnabled = false
        UIApplication.shared.statusBarStyle = .default
        
        if Utilities.isStandaloneApp() {
            self.setNavigationBarItem()
        }
        else {
            self.addHomeButton()
        }
       
        
        if !taskControllerPresented {
            taskControllerPresented = false
            self.checkForActivitiesUpdates()
        }
        
        if tableViewSections.count == 0 {
            self.tableView?.isHidden = true
            self.labelNoNetworkAvailable?.isHidden = false
            
        } else {
            self.tableView?.isHidden = false
            self.labelNoNetworkAvailable?.isHidden = true
        }
        
        
    }
    
    override func viewDidAppear(_ animated: Bool) {
        super.viewDidAppear(animated)
        
        let message =  "The study " + (Study.currentStudy?.name!)! + " is 100 percent complete. Thank you for your participation."
       // UIUtilities.showAlertWithMessage(alertMessage: message)
        
    }
    
    // MARK: Helper Methods
    
    func getLabkeyResponse() {
        
        let ud = UserDefaults.standard
        let key = "LabKeyResponse" + (Study.currentStudy?.studyId)!
        if !(ud.bool(forKey: key)){
            labkeyResponseFetch.checkUpdates()
        }
    }
    
    func checkForActivitiesUpdates(){
        
        if StudyUpdates.studyActivitiesUpdated {
            
            self.sendRequestToGetActivityStates()
            
            //udpate status to false so notification can be registered again
            Study.currentStudy?.activitiesLocalNotificationUpdated = false
            DBHandler.updateLocalNotificaitonUpdated(studyId: (Study.currentStudy?.studyId)!,status: false)
            
        } else {
            
            if self.refreshControl != nil && (self.refreshControl?.isRefreshing)!{
                self.refreshControl?.endRefreshing()
            }
            //self.loadActivitiesFromDatabase()
            self.fetchActivityAnchorDateResponseFromLabkey()
        }
    }
    
    /**
     checkIfFetelKickCountRunning method verifies whether if FetalKick Task is Still running and calculate the time difference.
     */
    func checkIfFetelKickCountRunning(){
        
        let ud = UserDefaults.standard
        
        if (ud.bool(forKey: "FKC") && ud.object(forKey: kFetalKickStartTimeStamp) != nil) {
            
            //let studyId = (ud.object(forKey: kFetalkickStudyId)  as? String)!
            let activityId = (ud.object(forKey: kFetalKickActivityId)  as? String)!
            let activity  = Study.currentStudy?.activities?.filter({$0.actvityId == activityId}).last
            
            Study.updateCurrentActivity(activity: activity!)
            //check in database
            DBHandler.loadActivityMetaData(activity: activity!, completionHandler: { (found) in
                
                if found {
                    self.createActivity()
                }
                
            })
            
        } else {
            //check if user navigated from notification
            
            if NotificationHandler.instance.activityId.count > 0 {
                
                let activityId = NotificationHandler.instance.activityId
                
                let rowDetail = tableViewSections[0]
                let activities = (rowDetail["activities"] as? Array<Activity>)!
                let index = activities.index(where: {$0.actvityId == activityId})
                let ip = IndexPath.init(row: index!, section: 0)
                self.selectedIndexPath = ip
                self.tableView?.selectRow(at: ip, animated: true, scrollPosition: .middle)
                self.tableView?.delegate?.tableView!(self.tableView!, didSelectRowAt: ip)
                
                NotificationHandler.instance.activityId = ""
            }
        }
    }
    
    /**
     RegisterNotificationForAnchorDate method sets the notification for the available resource
     */
    
    func registerNotificationForAnchorDate(){
        
        DBHandler.getResourcesWithAnchorDateAvailable(studyId: (Study.currentStudy?.studyId)!) { (resourcesList) in
            if resourcesList.count > 0 {
                let todayDate = Date()
                for resource in resourcesList {
                    
                    if resource.startDate == nil && resource.endDate == nil {
                        
                        let anchorDateObject = Study.currentStudy?.anchorDate
                        //Fetch AnchorData based availablity
                        if(anchorDateObject != nil && (anchorDateObject?.isAnchorDateAvailable())!) {
                            
                            let anchorDate = Study.currentStudy?.anchorDate?.date?.startOfDay
                            
                            if anchorDate != nil {
                                
                                //also anchor date condition
                                let startDateInterval = TimeInterval(60*60*24*(resource.anchorDateStartDays))
                                
                                let endDateInterval = TimeInterval(60*60*24*(resource.anchorDateEndDays))
                                
                                let startAnchorDate = anchorDate?.addingTimeInterval(startDateInterval)
                                var endAnchorDate = anchorDate?.addingTimeInterval(endDateInterval)
                                
                                endAnchorDate = endAnchorDate?.endOfDay
                                let startDateResult = (startAnchorDate?.compare(todayDate))! as ComparisonResult
                                //let endDateResult = (endAnchorDate?.compare(todayDate))! as ComparisonResult
                                self.isAnchorDateSet = false
                                
                                if startDateResult == .orderedDescending {
                                    //upcoming
                                    let notfiId = resource.resourceId! + (Study.currentStudy?.studyId)!
                                    DBHandler.isNotificationSetFor(notification: notfiId
                                        , completionHandler: { (found) in
                                            if !found {
                                                
                                                //Create AppLocalNotification
                                                let notification = AppLocalNotification()
                                                notification.id = resource.resourceId! + (Study.currentStudy?.studyId)!
                                                notification.message = resource.notificationMessage
                                                notification.title = "New Resource Available"
                                                notification.startDate = startAnchorDate
                                                notification.endDate = endAnchorDate
                                                notification.type = AppNotification.NotificationType.Study
                                                notification.subType = AppNotification.NotificationSubType.Resource
                                                notification.audience = Audience.Limited
                                                notification.studyId = (Study.currentStudy?.studyId)!
                                                //notification.activityId = Study.currentActivity?.actvityId
                                                
                                                //Save Notification to Database
                                                DBHandler.saveLocalNotification(notification: notification)
                                                
                                                //register notification
                                                var notificationDate = startAnchorDate?.startOfDay
                                                notificationDate = notificationDate?.addingTimeInterval(43200)
                                                let message = resource.notificationMessage
                                                let userInfo = ["studyId": (Study.currentStudy?.studyId)!,
                                                                "type": "resource"];
                                                LocalNotification.scheduleNotificationOn(date: notificationDate!, message: message!, userInfo: userInfo, id: notification.id)
                                            }
                                    })
                                }
                            }
                        }
                    }
                }
            }
        }
    }
    
    
    // MARK:- Button Actions
    
    /**
     Home Button Clicked
     @param sender    Accepts any kind of object
     */
    @IBAction func homeButtonAction(_ sender: AnyObject){
        self.performSegue(withIdentifier: kActivityUnwindToStudyListIdentifier, sender: self)
    }
    
    @IBAction func filterButtonAction(_ sender: AnyObject){
        let frame = self.view.frame
        
        if self.selectedFilter == nil {
            self.selectedFilter = ActivityFilterType.all
        }
        //create and load FilterView
        let view = ActivityFilterView.instanceFromNib(frame: frame , selectedIndex: self.selectedFilter!)
        view.delegate = self
        self.tabBarController?.view.addSubview(view)
    }
    
    // MARK: Helper Methods
    func checkForDashBoardInfo(){
        
        DBHandler.loadStatisticsForStudy(studyId: (Study.currentStudy?.studyId)!) { (statiticsList) in
            
            if statiticsList.count != 0 {
                //Do Nothing
            } else {
                self.sendRequestToGetDashboardInfo()
            }
        }
    }
    
    
    @objc func refresh(sender:AnyObject) {
        
        Logger.sharedInstance.info("Request for study Updated...")
        WCPServices().getStudyUpdates(study: Study.currentStudy!, delegate: self)
        //self.sendRequesToGetActivityList()
    }
    
    
    // MARK:-
    
    func fetchActivityAnchorDateResponseFromLabkey() {
        
        AnchorDateHandler().fetchActivityAnchorDateResponseFromLabkey { (status) in
            print("Finished 1")
            //if status {
                //DispatchQueue.main.async {
                    self.loadActivitiesFromDatabase()
                //}
                
            //}
            
        }
        print("Finished 0")
    }
    
    /**
     Used to load the Actif=vities data from database
     */
    func loadActivitiesFromDatabase(){
        
        if DBHandler.isActivitiesEmpty((Study.currentStudy?.studyId)!) {
            self.sendRequestToGetActivityStates()
        }
        else {
            
            DBHandler.loadActivityListFromDatabase(studyId: (Study.currentStudy?.studyId)!) { (activities) in
                if activities.count > 0 {
                    Study.currentStudy?.activities = activities
                    
                    self.handleActivityListResponse()
                    
                    
                } else {
                    
                    //self.sendRequestToGetActivityStates()
                }
            }
        }
    }
    
    
    /**
     Used to create an activity using ORKTaskViewController
     */
    func createActivity(){
        
        //Disable Custom KeyPad with toolbars
//        IQKeyboardManager.sharedManager().enable = false
        IQKeyboardManager.shared.enableAutoToolbar = false
        
        if Utilities.isValidObject(someObject: Study.currentActivity?.steps as AnyObject?){
            
            //Create ActivityBuilder instance
            ActivityBuilder.currentActivityBuilder = ActivityBuilder()
            ActivityBuilder.currentActivityBuilder.initWithActivity(activity: Study.currentActivity! )
        }
        
        let task: ORKTask?
        let taskViewController: ORKTaskViewController?
        
        task = ActivityBuilder.currentActivityBuilder.createTask()
        
        if task != nil {
            
            //check if restorationData is available
            if Study.currentActivity?.currentRun.restortionData != nil {
                let restoredData = Study.currentActivity?.currentRun.restortionData
                
                //let result: ORKResult?
                taskViewController = ORKTaskViewController(task: task, restorationData: restoredData, delegate: self)
            } else {
                
                taskViewController = ORKTaskViewController(task: task, taskRun: nil)
                taskViewController?.outputDirectory = FileManager.default.urls(for: .documentDirectory, in: .userDomainMask).first!
            }
            
            taskViewController?.showsProgressInNavigationBar = true
            
            taskViewController?.title = "Activity"
            
            //Customize appearance of TaskViewController
            UIView.appearance(whenContainedInInstancesOf: [ORKTaskViewController.self]).tintColor = kUIColorForSubmitButtonBackground
            
            taskViewController?.delegate = self
            UIApplication.shared.statusBarStyle = .default
            taskControllerPresented = true
            taskViewController?.modalPresentationStyle = .fullScreen
            present(taskViewController!, animated: true, completion: nil)
            
        } else {
            //Task creation failed
            UIUtilities.showAlertMessage(kAlertMessageText, errorMessage: NSLocalizedString("Invalid Data!", comment: ""), errorAlertActionTitle: NSLocalizedString("OK", comment: ""), viewControllerUsed: self)
        }
        
    }
    
    
    /**
     Used to get Activity Availability Status
     @param activity    Accepts data from Activity class
     @return ActivityAvailabilityStatus
     */
    func getActivityAvailabilityStatus(activity: Activity) -> ActivityAvailabilityStatus {
        
        var todayDate = Date().utcDate()
        
        let difference = UserDefaults.standard.value(forKey: "offset") as? Int
        if difference != nil {
            todayDate = todayDate.addingTimeInterval(TimeInterval(difference!))
        }
        
        if activity.startDate != nil && activity.endDate != nil {
            
            let startDateResult = (activity.startDate?.compare(todayDate))! as ComparisonResult
            let endDateResult = (activity.endDate?.compare(todayDate))! as ComparisonResult
            
            if startDateResult == .orderedAscending && endDateResult == .orderedDescending{
                return .current
                
            } else if startDateResult == .orderedDescending {
                return .upcoming
                
            } else if endDateResult == .orderedAscending {
                return .past
            }
        } else if activity.startDate != nil {
            
            let startDateResult = (activity.startDate?.compare(todayDate))! as ComparisonResult
            
            if startDateResult == .orderedAscending{
                return .current
                
            } else if startDateResult == .orderedDescending {
                return .upcoming
            }
        }
        return .current
    }
    
    
    /**
     Used to handle Activity list response
     */
    func handleActivityListResponse(){
        
        tableViewSections = []
        allActivityList = []
        let activities = Study.currentStudy?.activities
        
        var currentActivities: Array<Activity> = []
        var upcomingActivities: Array<Activity> = []
        var pastActivities: Array<Activity> = []
        
        var isInActiveActivitiesAreAvailable: Bool! = false
        for activity in activities! {
            
            if activity.state == "active" || activity.state == nil {
                
                let status =  self.getActivityAvailabilityStatus(activity: activity)
                switch status {
                case .current:
                    currentActivities.append(activity)
                case .upcoming:
                    upcomingActivities.append(activity)
                case .past:
                    pastActivities.append(activity)
                }
            } else {
                
                isInActiveActivitiesAreAvailable = true
                DBHandler.deleteDBLocalNotification(activityId: activity.actvityId!,studyId: activity.studyId!)
            }
        }
        
        if isInActiveActivitiesAreAvailable {
            LocalNotification.refreshAllLocalNotification()
        }
        
        //sort as per start date
        currentActivities.sort(by: {$0.startDate?.compare($1.startDate!) == .orderedAscending})
        upcomingActivities.sort(by: {$0.startDate?.compare($1.startDate!) == .orderedAscending})
        pastActivities.sort(by: {$0.startDate?.compare($1.startDate!) == .orderedAscending})
        
        let  sortedCurrentActivities =  currentActivities.sorted(by: { (activity1: Activity, activity2: Activity) -> Bool in
            
            return (activity1.userParticipationStatus.status.sortIndex < activity2.userParticipationStatus.status.sortIndex)
        })
        
        
        let currentDetails = ["title": "CURRENT","activities": sortedCurrentActivities] as [String : Any]
        let upcomingDetails = ["title": "UPCOMING","activities": upcomingActivities] as [String : Any]
        let pastDetails = ["title": "PAST","activities": pastActivities] as [String : Any]
        
        allActivityList.append(currentDetails)
        allActivityList.append(upcomingDetails)
        allActivityList.append(pastDetails)
        
        tableViewSections = allActivityList
        
        if self.selectedFilter == .tasks || self.selectedFilter == .surveys {
            
            let filterType:ActivityType! =  (selectedFilter == .surveys ? .Questionnaire : .activeTask)
            self.updateSectionArray(activityType: filterType)
        }
        
        DispatchQueue.main.async {
            
            self.tableView?.reloadData()
            self.tableView?.isHidden = false
            self.labelNoNetworkAvailable?.isHidden = true
            self.updateCompletionAdherence()
        }
        
        
        
        
        if (User.currentUser.settings?.localNotifications)! {
            print("localNotifications enabled")
            if !(Study.currentStudy?.activitiesLocalNotificationUpdated)! {
                print("Registerig Notification")
                //Register LocalNotifications
                LocalNotification.registerAllLocalNotificationFor(activities: (Study.currentStudy?.activities)!) { (finished,notificationlist) in
                    print("Notification set sucessfully")
                    Study.currentStudy?.activitiesLocalNotificationUpdated = true
                    DBHandler.saveRegisteredLocaNotification(notificationList: notificationlist)
                    DBHandler.updateLocalNotificaitonUpdated(studyId: (Study.currentStudy?.studyId)!,status: true)
                    LocalNotification.refreshAllLocalNotification()
                }
                
            }
        }
        
        self.checkIfFetelKickCountRunning()
        
        Logger.sharedInstance.info("Activities Displayed to user")
    }
    
    /**
     Used to update Activity Run Status
     @param status    Accepts data from UserActivityStatus class and ActivityStatus enum
     */
    func updateActivityRunStuatus(status: UserActivityStatus.ActivityStatus){
        
        let activity = Study.currentActivity!
        
        let activityStatus = User.currentUser.updateActivityStatus(studyId: activity.studyId!, activityId: activity.actvityId!,runId: String(activity.currentRunId), status: status)
        activityStatus.compeltedRuns = activity.compeltedRuns
        activityStatus.incompletedRuns = activity.incompletedRuns
        activityStatus.totalRuns = activity.totalRuns
        activityStatus.activityVersion = activity.version
        
        //Update participationStatus to server
        UserServices().updateUserActivityParticipatedStatus(studyId: activity.studyId!, activityStatus: activityStatus, delegate: self)
        
        //Update participationStatus to DB
        DBHandler.updateActivityParticipationStatus(activity: activity)
        
        if status == .completed {
            self.updateCompletionAdherence()
        }
        
    }
    
    /**
     updateCompletionAdherence, calculates the Completion & Adherence based on following criteria
     completion = ((totalCompletedRuns + totalIncompletedRuns) * 100) / (totalRuns)
     adherence =  (totalCompletedRuns*100) / (totalCompletedRuns + totalIncompletedRuns)
     
     and alerts the user about the study Completion Status
     
     */
    func updateCompletionAdherence() {
        
        var totalRuns = 0
        var totalCompletedRuns = 0
        var totalIncompletedRuns = 0
        let activities = Study.currentStudy?.activities //.filter({$0.state == "active"})
        
        //Calculate Runs
        for activity in activities! {
            totalRuns += activity.totalRuns
            totalIncompletedRuns += activity.incompletedRuns
            totalCompletedRuns += activity.compeltedRuns
            
        }
        
        
        Study.currentStudy?.totalCompleteRuns = totalCompletedRuns
        Study.currentStudy?.totalIncompleteRuns = totalIncompletedRuns
        //Calculate Completion & Adherence
        let completion = ceil( Double(self.divide(lhs: (totalCompletedRuns + totalIncompletedRuns)*100, rhs: totalRuns)) )
        let adherence = ceil (Double(self.divide(lhs: totalCompletedRuns*100, rhs: (totalCompletedRuns + totalIncompletedRuns))))
        
        let studyid = (Study.currentStudy?.studyId)!
        
        let status = User.currentUser.udpateCompletionAndAdherence(studyId: studyid, completion: Int(completion), adherence: Int(adherence))
        
        //Update to server
        UserServices().updateCompletionAdherence(studyStauts: status, delegate: self)
        //Update Local DB
        DBHandler.updateStudyParticipationStatus(study: Study.currentStudy!)
        
        
        //Compose Alert based on Completion
        let halfCompletionKey = "50pcShown"  + (Study.currentStudy?.studyId)!
        let fullCompletionKey = "100pcShown"  + (Study.currentStudy?.studyId)!
        let missedKey = "totalMissed"  + (Study.currentStudy?.studyId)!
        
        let ud = UserDefaults.standard
        if completion > 50 && completion < 100 {
            
            if !(ud.bool(forKey: halfCompletionKey)) {
                let message =  "The study " + (Study.currentStudy?.name!)! + " is now 50 percent complete. We look forward to your continued participation as the study progresses."
                //UIUtilities.showAlertWithMessage(alertMessage: message)
                ud.set(true, forKey: halfCompletionKey)
                
            }
            
        }
        
        if completion == 100 {
            
            if !(ud.bool(forKey: fullCompletionKey)) {
                let message =  "The study " + (Study.currentStudy?.name!)! + " is 100 percent complete. Thank you for your participation."
                UIUtilities.showAlertWithMessage(alertMessage: message)
                ud.set(true, forKey: fullCompletionKey)
                
            }
        }
        
        //Alerts User about Completion
        if ud.object(forKey: missedKey) == nil {
            ud.set(totalIncompletedRuns, forKey: missedKey)
            
        } else {
            let previousMissed = (ud.object(forKey: missedKey) as? Int)!
            ud.set(totalIncompletedRuns, forKey: missedKey)
            if previousMissed < totalIncompletedRuns {
                //show alert
                
                let message = "We noticed you missed an activity in " + (Study.currentStudy?.name!)! + " today. That’s ok! We know you’re busy, but we encourage you to complete study activities before they expire."
                UIUtilities.showAlertWithMessage(alertMessage: message)
            }
        }
        
        ud.synchronize()
        
    }
    
    func divide(lhs: Int, rhs: Int) -> Int {
        if rhs == 0 {
            return 0
        }
        return lhs/rhs
    }
    
    /**
     Used to update Activity Status To InProgress
     */
    func updateActivityStatusToInProgress(){
        self.updateActivityRunStuatus(status: .inProgress)
    }
    
    
    /**
     Used to update Activity Status To Complete
     */
    func updateActivityStatusToComplete(){
        self.updateActivityRunStuatus(status: .completed)
    }
    
    //save completed staus in database
    func updateRunStatusToComplete(){
        
        let activity = Study.currentActivity!
        activity.compeltedRuns += 1
        DBHandler.updateRunToComplete(runId: activity.currentRunId, activityId: activity.actvityId!, studyId: activity.studyId!)
        self.updateActivityStatusToComplete()
    }
    
    /**
     Update Run Status based on Run Id
     */
    func updateRunStatusForRunId(runId:Int){
        
        let activity = Study.currentActivity!
        activity.compeltedRuns += 1
        DBHandler.updateRunToComplete(runId: runId, activityId: activity.actvityId!, studyId: activity.studyId!)
        
        //update run count information
        let incompleteRuns = activity.currentRunId - activity.compeltedRuns
        activity.incompletedRuns = (incompleteRuns < 0) ? 0 : incompleteRuns
        if activity.currentRun == nil {
            //Do Nothing
            
        } else {
            //Status is not completed
            if activity.userParticipationStatus.status != UserActivityStatus.ActivityStatus.completed {
                
                var incompleteRuns = activity.currentRunId - activity.compeltedRuns
                incompleteRuns -= 1
                activity.incompletedRuns = (incompleteRuns < 0) ? 0 : incompleteRuns
            }
            
        }
        
        let activityStatus = User.currentUser.updateActivityStatus(studyId: activity.studyId!, activityId: activity.actvityId!,runId: String(runId), status: .completed)
        activityStatus.compeltedRuns = activity.compeltedRuns
        activityStatus.incompletedRuns = activity.incompletedRuns
        activityStatus.totalRuns = activity.totalRuns
        activityStatus.activityVersion = activity.version
        
        //Update User Participation Status to server
        UserServices().updateUserActivityParticipatedStatus(studyId: activity.studyId!, activityStatus: activityStatus, delegate: self)
        
        //Update User Participation Status to DB
        DBHandler.updateActivityParticipationStatus(activity: activity)
        
        self.updateCompletionAdherence()
        self.tableView?.reloadData()
        
    }
    
    /**
     Handler for studyUpdateResponse
     */
    func handleStudyUpdatesResponse() {
        
        Study.currentStudy?.newVersion = StudyUpdates.studyVersion
        DBHandler.updateMetaDataToUpdateForStudy(study: Study.currentStudy!, updateDetails: nil)
        
        //Consent Updated
        if StudyUpdates.studyConsentUpdated {
            presentUpdatedConsent()
            
        } else if StudyUpdates.studyInfoUpdated {
            WCPServices().getStudyInformation(studyId: (Study.currentStudy?.studyId)!, delegate: self)
            
        } else {
            self.checkForActivitiesUpdates()
        }
        
    }
    
    // MARK: Api Calls
    
    /**
     Used to send Request To Get ActivityStates
     */
    func sendRequestToGetActivityStates(){
        UserServices().getUserActivityState(studyId: (Study.currentStudy?.studyId)!, delegate: self)
    }
    
    
    /**
     Used to send Request To Get ActivityList
     */
    func sendRequesToGetActivityList(){
        WCPServices().getStudyActivityList(studyId: (Study.currentStudy?.studyId)!, delegate: self)
    }
    
    func sendRequestToGetDashboardInfo(){
        WCPServices().getStudyDashboardInfo(studyId: (Study.currentStudy?.studyId)!, delegate: self)
    }
    func sendRequestToGetResourcesInfo(){
        WCPServices().getResourcesForStudy(studyId:(Study.currentStudy?.studyId)!, delegate: self)
    }
    
}


// MARK:- TableView Datasource
extension ActivitiesViewController: UITableViewDataSource{
    
    func numberOfSections(in tableView: UITableView) -> Int {
        return tableViewSections.count
    }
    
    private func tableView(tableView: UITableView, heightForHeaderInSection section: Int) -> CGFloat {
        return 30
    }
    
    func tableView(_ tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
        
        let rowDetail = tableViewSections[section]
        let activities = (rowDetail["activities"] as? Array<Activity>)!
        if activities.count == 0 {
            return 1
        }
        return activities.count
    }
    
    func tableView(_ tableView: UITableView, viewForHeaderInSection section: Int) -> UIView? {
        let view = UIView.init(frame: CGRect(x: 0, y: 0, width: tableView.frame.width, height: 30))
        view.backgroundColor = kBackgroundTableViewColor
        
        let dayData = tableViewSections[section]
        
        let statusText = (dayData["title"] as? String)!
        
        let label = UILabel.init(frame: CGRect(x: 18, y: 0, width: view.frame.size.width, height: view.frame.size.height))
        label.textAlignment = NSTextAlignment.natural
        label.text = statusText
        label.font = UIFont.boldSystemFont(ofSize: 14)
        label.translatesAutoresizingMaskIntoConstraints = true
        label.textColor = kGreyColor
        view.addSubview(label)
        
        return view
    }
    
    func tableView(_ tableView: UITableView, cellForRowAt indexPath: IndexPath) -> UITableViewCell {
        
        let rowDetail = tableViewSections[indexPath.section]
        let activities = (rowDetail["activities"] as? Array<Activity>)!
        
        if activities.count == 0 {
            
            let cell = tableView.dequeueReusableCell(withIdentifier: "noData", for: indexPath)
            cell.isUserInteractionEnabled = false
            return cell
        } else {
            var cell = (tableView.dequeueReusableCell(withIdentifier: kActivitiesTableViewCell, for: indexPath) as? ActivitiesTableViewCell)!
            cell.delegate = self
            
            //Cell Data Setup
            cell.backgroundColor = UIColor.clear
            //kActivitiesTableViewScheduledCell
            let availabilityStatus = ActivityAvailabilityStatus(rawValue: indexPath.section)
            
            let activity = activities[indexPath.row]
            
            //check for scheduled frequency
            if activity.frequencyType == .Scheduled {
                
                cell = (tableView.dequeueReusableCell(withIdentifier: kActivitiesTableViewScheduledCell, for: indexPath) as? ActivitiesTableViewCell)!
                cell.delegate = self
            }
            //Set Cell data
            cell.populateCellDataWithActivity(activity: activity, availablityStatus: availabilityStatus!)
            
            return cell
        }
    }
}


// MARK:- TableView Delegates
extension ActivitiesViewController: UITableViewDelegate{
    
    func tableView(_ tableView: UITableView, didSelectRowAt indexPath: IndexPath) {
        tableView.deselectRow(at: indexPath, animated: true)
        
        let availabilityStatus = ActivityAvailabilityStatus(rawValue: indexPath.section)!
        
        switch availabilityStatus {
        case .current:
            
            let rowDetail = tableViewSections[indexPath.section]
            let activities = (rowDetail["activities"] as? Array<Activity>)!
            
            let activity = activities[indexPath.row]
            //Check for activity run status & if run is available
            if activity.currentRun != nil {
                if activity.userParticipationStatus != nil {
                    let activityRunParticipationStatus = activity.userParticipationStatus
                    if activityRunParticipationStatus?.status == .yetToJoin || activityRunParticipationStatus?.status == .inProgress
                        
                    {
                        Study.updateCurrentActivity(activity: activities[indexPath.row])
                        
                        //Following to be commented
                        //self.createActivity()
                        Logger.sharedInstance.info("Activity Fetching from db")
                        //check in database
                        DBHandler.loadActivityMetaData(activity: activities[indexPath.row], completionHandler: { (found) in
                            if found {
                                self.createActivity()
                            } else {
                                
                                //Fetch ActivityMetaData from Server
                                WCPServices().getStudyActivityMetadata(studyId: (Study.currentStudy?.studyId)! , activityId: (Study.currentActivity?.actvityId)!, activityVersion: (Study.currentActivity?.version)!, delegate: self)
                            }
                        })
                        
                        self.updateActivityStatusToInProgress()
                        self.selectedIndexPath = indexPath
                        
                    } else {
                        debugPrint("run is completed")
                    }
                }
                
            } else if activity.userParticipationStatus?.status == .abandoned {
                debugPrint("run not available")
                UIUtilities.showAlertWithMessage(alertMessage: NSLocalizedString(kActivityAbondonedAlertMessage, comment: ""))
            }
            
        case .upcoming,.past: break
            
        }
    }
}

// MARK:- ActivitiesCell Delegate
extension ActivitiesViewController: ActivitiesCellDelegate{
    
    func activityCell(cell: ActivitiesTableViewCell, activity: Activity) {
        
        let frame = self.view.frame
        //frame.size.height += 114
        
        let view = ActivitySchedules.instanceFromNib(frame: frame, activity: activity)
        self.tabBarController?.view.addSubview(view)
        
    }
}

// MARK:- ActivityFilterDelegate
extension ActivitiesViewController: ActivityFilterViewDelegate{
    func setSelectedFilter(selectedIndex: ActivityFilterType) {
        
        // current filter is not same as existing filter
        if self.selectedFilter != selectedIndex {
            
            // currently filter type is all so no need to fetch all activities
            if self.selectedFilter == .all{
                
                let filterType: ActivityType! =  (selectedIndex == .surveys ? .Questionnaire : .activeTask)
                self.updateSectionArray(activityType: filterType)
                
            } else {// existing filterType is either Task or Surveys
                
                //load all the sections from scratch
                self.tableViewSections = []
                self.tableViewSections = allActivityList
                
                // applying the new filter Type
                if selectedIndex == .surveys || selectedIndex == .tasks{
                    let filterType:ActivityType! =  (selectedIndex == .surveys ? .Questionnaire : .activeTask)
                    self.updateSectionArray(activityType: filterType)
                }
            }
            self.selectedFilter = selectedIndex
            self.tableView?.reloadData()
        } else {
            //current and newly selected filter types are same
        }
    }
    
    func updateSectionArray(activityType: ActivityType)  {
        
        var updatedSectionArray: Array<Dictionary<String,Any>>! = []
        for section in tableViewSections {
            let activities = (section[kActivities] as? Array<Activity>)!
            var sectionDict: Dictionary<String,Any>! = section
            sectionDict[kActivities] = activities.filter({$0.type == activityType
            })
            
            updatedSectionArray.append(sectionDict)
        }
        tableViewSections = []
        tableViewSections = updatedSectionArray
    }
    
    
}


// MARK:- Webservice Delegates
extension ActivitiesViewController: NMWebServiceDelegate {
    
    func startedRequest(_ manager: NetworkManager, requestName: NSString) {
        Logger.sharedInstance.info(" START requestname : \(requestName)")
        
        if (requestName as String == RegistrationMethods.updateStudyState.method.methodName) ||  (requestName as String == RegistrationMethods.updateActivityState.method.methodName) ||
            (requestName as String == WCPMethods.studyDashboard.method.methodName) || (requestName as String == WCPMethods.resources.method.methodName){
        } else {
            self.addProgressIndicator()
        }
    }
    
    
    
    func finishedRequest(_ manager: NetworkManager, requestName: NSString, response: AnyObject?) {
        Logger.sharedInstance.info("requestname : \(requestName) Response : \(String(describing:response))")
        
        if requestName as String == RegistrationMethods.activityState.method.methodName {
            self.sendRequesToGetActivityList()
        } else if requestName as String == WCPMethods.activityList.method.methodName {
            
            //get DashboardInfo
            self.sendRequestToGetDashboardInfo()
            
            self.fetchActivityAnchorDateResponseFromLabkey()
           // self.loadActivitiesFromDatabase()
            
            if self.refreshControl != nil && (self.refreshControl?.isRefreshing)!{
                self.refreshControl?.endRefreshing()
            }
            
            StudyUpdates.studyActivitiesUpdated = false
            //Update StudymetaData for Study
            DBHandler.updateMetaDataToUpdateForStudy(study: Study.currentStudy!, updateDetails: nil)
            
            
            
        } else if requestName as String == WCPMethods.activity.method.methodName {
            self.removeProgressIndicator()
            self.createActivity()
            
        } else if requestName as String == WCPMethods.studyDashboard.method.methodName {
            self.sendRequestToGetResourcesInfo()
            self.getLabkeyResponse()
            
        } else if requestName as String == WCPMethods.resources.method.methodName {
            self.removeProgressIndicator()
            
        } else if requestName as String == ResponseMethods.processResponse.method.methodName {
            self.removeProgressIndicator()
            //self.updateRunStatusToComplete()
            self.checkForActivitiesUpdates()
            
        } else if requestName as String == WCPMethods.studyUpdates.method.methodName {
            
            Logger.sharedInstance.info("Handling response for study updates...")
            if Study.currentStudy?.version == StudyUpdates.studyVersion {
                
                self.loadActivitiesFromDatabase()
                self.removeProgressIndicator()
                if self.refreshControl != nil && (self.refreshControl?.isRefreshing)! {
                    self.refreshControl?.endRefreshing()
                }
            } else {
                self.handleStudyUpdatesResponse()
            }
            
        } else if requestName as String == WCPMethods.studyInfo.method.methodName {
            
            StudyUpdates.studyInfoUpdated = false
            DBHandler.updateMetaDataToUpdateForStudy(study: Study.currentStudy!, updateDetails: nil)
            
            self.checkForActivitiesUpdates()
        }
    }
    
    func failedRequest(_ manager: NetworkManager, requestName: NSString, error: NSError) {
        Logger.sharedInstance.info("requestname : \(requestName)")
        self.removeProgressIndicator()
        
        if self.refreshControl != nil && (self.refreshControl?.isRefreshing)! {
            self.refreshControl?.endRefreshing()
        }
        if error.code == 403 { //unauthorized
            UIUtilities.showAlertMessageWithActionHandler(kErrorTitle, message: error.localizedDescription, buttonTitle: kTitleOk, viewControllerUsed: self, action: {
                self.fdaSlideMenuController()?.navigateToHomeAfterUnauthorizedAccess()
            })
        } else {
            if requestName as String == RegistrationMethods.activityState.method.methodName {
                //self.sendRequesToGetActivityList()
                if (error.code != NoNetworkErrorCode) {
                    self.loadActivitiesFromDatabase()
                } else {
                    
                    self.tableView?.isHidden = true
                    self.labelNoNetworkAvailable?.isHidden = false
                    
                    UIUtilities.showAlertWithTitleAndMessage(title: NSLocalizedString(kErrorTitle, comment: "") as NSString, message: error.localizedDescription as NSString)
                }
            } else if requestName as String == ResponseMethods.processResponse.method.methodName {
                
                if (error.code == NoNetworkErrorCode) {
                    //Users are notified when their responses don’t get submitted due to network issues and are notified that the responses will be automatically submitted once the app has network available again.
                    //                UIUtilities.showAlertWithMessage(alertMessage: "Your responses don’t get submitted due to network issue, but we have saved it locally, we will automatically submit once the app has network available again.")
                    //
                    //                if error.code == CouldNotConnectToServerCode {
                    //                    UIUtilities.showAlertWithMessage(alertMessage: "Your responses don’t get submitted due to connectiviy with our server, but we have saved it locally, we will automatically submit once the app has network available again.")
                    //                }
                    //let data = ActivityBuilder.currentActivityBuilder.actvityResult?.getResultDictionary()
                    //DBHandler.saveResponseDataFor(activity: Study.currentActivity!, toBeSynced: true, data: data!)
                    
                }
                
            }
            else if requestName as String == RegistrationMethods.updateStudyState.method.methodName {
                
            }
            else {
                if error.code != 300 {
                    UIUtilities.showAlertWithTitleAndMessage(title: NSLocalizedString(kErrorTitle, comment: "") as NSString, message: error.localizedDescription as NSString)
                }
                
            }
        }
    }
}

// MARK:- ORKTaskViewController Delegate
extension ActivitiesViewController: ORKTaskViewControllerDelegate{
    
    func taskViewControllerSupportsSaveAndRestore(_ taskViewController: ORKTaskViewController) -> Bool {
        return true
    }
    
    /// This method will update the result for other choices for each step
    fileprivate func updateResultForChoiceQuestions(_ taskViewController: ORKTaskViewController) {
        if let results = taskViewController.result.results as? [ORKStepResult]{
            
            for result in results {
                if let choiceResult = result.results?.first as? ORKChoiceQuestionResult, let answers = choiceResult.answer as? [Any] {
                    var selectedChoices: [Any] = []
                    
                    var otherChoiceDict = answers.filter({$0 as? JSONDictionary != nil}).first as? JSONDictionary
                    let otherValueKey = "otherValue"
                    if let otherValue = otherChoiceDict?[otherValueKey] as? String {
                        otherChoiceDict?.removeValue(forKey: otherValueKey)
                        answers.forEach { (value) in
                            if let value = value as? String {
                                if value != otherValue {
                                    selectedChoices.append(value)
                                }
                            } else {
                                selectedChoices.append(otherChoiceDict!)
                            }
                        }
                        choiceResult.answer = selectedChoices
                    }
                    
                }
            }
        }
    }
    
    public func taskViewController(_ taskViewController: ORKTaskViewController, didFinishWith reason: ORKTaskViewControllerFinishReason, error: Error?) {
        
        //Enable Custom Keypad with toolbar
        // IQKeyboardManager.sharedManager().enable = true
        IQKeyboardManager.shared.enableAutoToolbar = true
        
        self.managedResult.removeAll()
        
        updateResultForChoiceQuestions(taskViewController)
        
        var taskResult: Any?
        
        switch reason {
            
        case ORKTaskViewControllerFinishReason.completed:
            print("completed")
            taskResult = taskViewController.result
//            let ud = UserDefaults.standard
//            if ud.bool(forKey: "FKC") {
//
//                let runid = (ud.object(forKey: "FetalKickCounterRunid") as? Int)!
//
//                if Study.currentActivity?.currentRun.runId != runid {
//                    //runid is changed
//                    self.updateRunStatusForRunId(runId: runid)
//                } else {
//                    self.updateRunStatusToComplete()
//                }
//            } else {
//
//                self.updateRunStatusToComplete()
//            }
            
        case ORKTaskViewControllerFinishReason.failed:
            print("failed")
            taskResult = taskViewController.result
        case ORKTaskViewControllerFinishReason.discarded:
            print("discarded")
            
            let study = Study.currentStudy
            let activity = Study.currentActivity
            activity?.currentRun.restortionData = nil
            DBHandler.updateActivityRestortionDataFor(activity: activity!, studyId: (study?.studyId)!, restortionData:nil)
            
            
            let ud = UserDefaults.standard
            ud.removeObject(forKey: "FKC")
            ud.removeObject(forKey: kFetalKickActivityId)
            ud.removeObject(forKey: kFetalKickCounterValue)
            ud.removeObject(forKey: kFetalKickStartTimeStamp)
            ud.removeObject(forKey: kFetalkickStudyId)
            ud.removeObject(forKey: kFetalKickCounterRunId)
            ud.synchronize()
            
            self.checkForActivitiesUpdates()
            
        case ORKTaskViewControllerFinishReason.saved:
            print("saved")
            taskResult = taskViewController.restorationData
            
            if taskViewController.task?.identifier == "ConsentTask" {
                //Do Nothing
            } else {
                ActivityBuilder.currentActivityBuilder.activity?.restortionData = taskViewController.restorationData
            }
            
            self.checkForActivitiesUpdates()
            
        @unknown default:
            break
        }
        
        
        let activityId = Study.currentActivity?.actvityId
        let studyId = Study.currentStudy?.studyId
        var response:[String:Any]? = nil
        
        if  taskViewController.task?.identifier == "ConsentTask" {
            consentbuilder?.consentResult?.initWithORKTaskResult(taskResult:taskViewController.result )
        } else {
            
            if reason == ORKTaskViewControllerFinishReason.completed {
                
                ActivityBuilder.currentActivityBuilder.actvityResult?.initWithORKTaskResult(taskResult: taskViewController.result)
                
                response = ActivityBuilder.currentActivityBuilder.actvityResult?.getResultDictionary()
                
                Study.currentActivity?.userStatus = .completed
                
                if ActivityBuilder.currentActivityBuilder.actvityResult?.type == ActivityType.activeTask {
                    
                    
                    if  (taskViewController.result.results?.count)! > 0 {
                        
                        let orkStepResult:ORKStepResult? = (taskViewController.result.results?[1] as? ORKStepResult)!
                        
                        if (orkStepResult?.results?.count)! > 0 {
                            
                            let activeTaskResultType =  ActiveStepType(rawValue:(ActivityBuilder.currentActivityBuilder.activity?.activitySteps?.first?.resultType as? String)!)
                            
                            switch activeTaskResultType! {
                                
                            case .fetalKickCounter:
                                
                                let fetalKickResult:FetalKickCounterTaskResult? = orkStepResult?.results?.first as? FetalKickCounterTaskResult
                                
                                //let study = Study.currentStudy
                                let activity = Study.currentActivity
                                
                                //Create the stats for FetalKick
                                if fetalKickResult != nil {
                                    
                                    let value = Float((fetalKickResult?.duration)!)/60
                                    let kickcount = Float((fetalKickResult?.totalKickCount)!)
                                    let dict = ActivityBuilder.currentActivityBuilder.activity?.steps?.first!
                                    let key =   (dict?[kActivityStepKey] as? String)!
                                    
                                    //Save Stats to DB
                                    DBHandler.saveStatisticsDataFor(activityId: (activity?.actvityId)!, key: key, data: value,fkDuration: Int(kickcount), date: Date())
                                    
                                    let ud = UserDefaults.standard
                                    ud.removeObject(forKey: "FKC")
                                    ud.removeObject(forKey: kFetalKickActivityId)
                                    ud.removeObject(forKey: kFetalKickCounterValue)
                                    ud.removeObject(forKey: kFetalKickStartTimeStamp)
                                    ud.removeObject(forKey: kFetalkickStudyId)
                                    ud.removeObject(forKey: kFetalKickCounterRunId)
                                    ud.synchronize()
                                    
                                }
                            case .spatialSpanMemoryStep:
                                let activity = Study.currentActivity
                                
                                //Create stats for SpatialSpanMemoryStep
                                let spatialSpanResult: ORKSpatialSpanMemoryResult? = orkStepResult?.results?.first as? ORKSpatialSpanMemoryResult
                                
                                //get score
                                let scores = Float((spatialSpanResult?.score)!)
                                let keyScore = "Score"
                                //Save Stats to DB
                                DBHandler.saveStatisticsDataFor(activityId: (activity?.actvityId)!, key: keyScore , data: Float(scores),fkDuration: Int(0), date: Date())
                                
                                //get numberOfFailures
                                let numberOfFailures = Float((spatialSpanResult?.numberOfFailures)!)
                                let keyNumberOfFailures = "NumberofFailures"
                                //Save Stats to DB
                                DBHandler.saveStatisticsDataFor(activityId: (activity?.actvityId)!, key: keyNumberOfFailures , data: Float(numberOfFailures),fkDuration: Int(0), date: Date())
                                
                                
                                //get number of Games
                                let numberOfGames = Float((spatialSpanResult?.numberOfGames)!)
                                let keyNumberOfGames = "NumberofGames"
                                //Save Stats to DB
                                DBHandler.saveStatisticsDataFor(activityId: (activity?.actvityId)!, key: keyNumberOfGames , data: Float(numberOfGames),fkDuration: Int(0), date: Date())
                                
                                break
                            case .towerOfHanoi:
                                
                                //Create Stats for TowersOfHonoi
                                let activity = Study.currentActivity
                                let tohResult: ORKTowerOfHanoiResult? = orkStepResult?.results?.first as? ORKTowerOfHanoiResult
                                let key =  ActivityBuilder.currentActivityBuilder.activity?.steps?.first![kActivityStepKey] as? String
                                
                                let numberOfMoves = tohResult?.moves?.count
                                
                                //Save Stats to DB
                                DBHandler.saveStatisticsDataFor(activityId: (activity?.actvityId)!, key: key! , data: Float(numberOfMoves!),fkDuration: Int(0), date: Date())
                                
                                break
                            default:break
                            }
                        }
                    }
                }
                
                
                
                //send response to labkey
                LabKeyServices().processResponse(responseData:response!, delegate: self)
            }
        }
        taskViewController.dismiss(animated: true, completion: {
           
            if reason == ORKTaskViewControllerFinishReason.completed {
                
                
                let ud = UserDefaults.standard
                if ud.bool(forKey: "FKC") {
                    
                    let runid = (ud.object(forKey: "FetalKickCounterRunid") as? Int)!
                    
                    if Study.currentActivity?.currentRun.runId != runid {
                        //runid is changed
                        self.updateRunStatusForRunId(runId: runid)
                    } else {
                        self.updateRunStatusToComplete()
                    }
                } else {
                    
                    self.updateRunStatusToComplete()
                }

                let lifeTimeUpdated = DBHandler.updateTargetActivityAnchorDateDetail(studyId: studyId!, activityId: activityId!, response: response!)
                if lifeTimeUpdated {
                    self.loadActivitiesFromDatabase()
                }
                else {
                    self.tableView?.reloadData()
                }
            }
            else {
                self.tableView?.reloadData()
            }
           
        })
    }
    
    func taskViewController(_ taskViewController: ORKTaskViewController, stepViewControllerWillAppear stepViewController: ORKStepViewController) {
        
        if (taskViewController.result.results?.count)! > 1 {
            
            if activityBuilder?.actvityResult?.result?.count == taskViewController.result.results?.count {
                activityBuilder?.actvityResult?.result?.removeLast()
            } else {
                
                let study = Study.currentStudy
                let activity = Study.currentActivity
                
                if activity?.type != .activeTask{
                    
                    //Update RestortionData for Activity in DB
                    DBHandler.updateActivityRestortionDataFor(activity: activity!, studyId: (study?.studyId)!, restortionData: taskViewController.restorationData!)
                    activity?.currentRun.restortionData = taskViewController.restorationData!
                }
                
                
                let orkStepResult: ORKStepResult? = taskViewController.result.results?[(taskViewController.result.results?.count)! - 2] as! ORKStepResult?
                let activityStepResult: ActivityStepResult? = ActivityStepResult()
                if (activity?.activitySteps?.count )! > 0 {
                    
                    let activityStepArray = activity?.activitySteps?.filter({$0.key == orkStepResult?.identifier
                    })
                    if (activityStepArray?.count)! > 0 {
                        activityStepResult?.step  = activityStepArray?.first
                    }
                }
                activityStepResult?.initWithORKStepResult(stepResult: orkStepResult! as ORKStepResult , activityType: (ActivityBuilder.currentActivityBuilder.actvityResult?.type)!)
                
               // let dictionary = activityStepResult?.getActivityStepResultDict()
                
                
                //check for anchor date
                if study?.anchorDate != nil && study?.anchorDate?.anchorDateActivityId == activity?.actvityId {
                    
                    if (study?.anchorDate?.anchorDateQuestionKey)! ==  (activityStepResult?.key)!{
                        if let value1 = activityStepResult?.value as? String {
                            isAnchorDateSet = true
                            study?.anchorDate?.setAnchorDateFromQuestion(date: value1)
                        }
                    }
                }
                
                //save data for stats
                if ActivityBuilder.currentActivityBuilder.actvityResult?.type == .Questionnaire {
                    
                    if let value1 = activityStepResult?.value as? NSNumber {
                        let value = value1.floatValue
                        DBHandler.saveStatisticsDataFor(activityId: (activity?.actvityId)!, key: (activityStepResult?.key)!, data: value,fkDuration: 0,date: Date())
                    }
                }
                
                let ud = UserDefaults.standard
                
                let activityId:String? = ud.value(forKey:"FetalKickActivityId" ) as! String?
                //go forward if fetal kick task is running
                if activity?.type == .activeTask
                    && ud.bool(forKey: "FKC")
                    && activityId != nil
                    && activityId == Study.currentActivity?.actvityId
                    && (stepViewController is ORKInstructionStepViewController)  {
                    
                    DispatchQueue.main.asyncAfter(deadline: .now()) {
                        stepViewController.goForward()
                    }
                }
                
                //disable back button
                if stepViewController is FetalKickCounterStepViewController{
                    stepViewController.backButtonItem = nil
                }
            }
        }
    }
    
    
    // MARK:- StepViewController Delegate
    public func stepViewController(_ stepViewController: ORKStepViewController, didFinishWith direction: ORKStepViewControllerNavigationDirection){
        
    }
    
    public func stepViewControllerResultDidChange(_ stepViewController: ORKStepViewController){
        
    }
    
    public func stepViewControllerDidFail(_ stepViewController: ORKStepViewController, withError error: Error?){
        
    }
    
    func taskViewController(_ taskViewController: ORKTaskViewController, viewControllerFor step: ORKStep) -> ORKStepViewController? {
        
        
        if let result = taskViewController.result.stepResult(forStepIdentifier: step.identifier) {
            self.managedResult[step.identifier] = result
        }
        
        if let step = step as? QuestionStep, step.answerFormat?.isKind(of: ORKTextChoiceAnswerFormat.self) ?? false {
            
            var textChoiceQuestionController :TextChoiceQuestionController
            
            var result = taskViewController.result.result(forIdentifier: step.identifier)
            result = ( result == nil ) ? self.managedResult[step.identifier] as? ORKStepResult : result
            
            if let result = result  {
                textChoiceQuestionController = TextChoiceQuestionController(step: step, result: result)
            } else {
                textChoiceQuestionController = TextChoiceQuestionController(step: step)
            }
            
            
            return textChoiceQuestionController
        }
        
        
        let storyboard = UIStoryboard.init(name: "FetalKickCounter", bundle: nil)
        
        if step is FetalKickCounterStep {
            
            let ttController = (storyboard.instantiateViewController(withIdentifier: "FetalKickCounterStepViewController") as? FetalKickCounterStepViewController)!
            ttController.step = step
            return ttController
        } else if  step is FetalKickIntroStep {
            
            let ttController = (storyboard.instantiateViewController(withIdentifier: "FetalKickIntroStepViewControllerIdentifier") as? FetalKickIntroStepViewController)!
            ttController.step = step
            return ttController
        } else {
            return nil
        }
        
    }
    
    
    func taskViewController(_ taskViewController: ORKTaskViewController, didChange result: ORKTaskResult) {
        
        // Saving the TextChoiceQuestionController result to publish it later.
        if taskViewController.currentStepViewController?.isKind(of: TextChoiceQuestionController.self) ?? false {
            if let result = result.stepResult(forStepIdentifier: taskViewController.currentStepViewController?.step?.identifier ?? "") {
                self.managedResult[result.identifier] = result
            }
        }
    }
    
    
}

extension ActivitiesViewController:UITabBarControllerDelegate{
    
    func tabBarController(_ tabBarController: UITabBarController, didSelect viewController: UIViewController) {
        let tabBarIndex = tabBarController.selectedIndex
        if tabBarIndex == 0 {
            //do your stuff
            self.checkForActivitiesUpdates()
        }
    }
    
    
}

// MARK:- ActivitySchedules Class
class ActivitySchedules:UIView,UITableViewDelegate,UITableViewDataSource{
    
    @IBOutlet var tableview: UITableView?
    @IBOutlet var buttonCancel: UIButton!
    @IBOutlet var heightLayoutConstraint: NSLayoutConstraint!
    
    var activity:Activity!
    
    required init?(coder aDecoder: NSCoder) {
        super.init(coder: aDecoder)
        //fatalError("init(coder:) has not been implemented")
    }
    
    class func instanceFromNib(frame:CGRect , activity:Activity) -> ActivitySchedules {
        let view = (UINib(nibName: "ActivitySchedules", bundle: nil).instantiate(withOwner: nil, options: nil)[0] as? ActivitySchedules)!
        view.frame = frame
        view.activity = activity
        view.tableview?.register(UITableViewCell.self, forCellReuseIdentifier: "Cell")
        view.tableview?.delegate = view
        view.tableview?.dataSource = view
        let height = (activity.activityRuns.count*44) + 104
        let maxViewHeight = Int(UIScreen.main.bounds.size.height - 67)
        view.heightLayoutConstraint.constant = CGFloat((height > maxViewHeight) ? maxViewHeight: height)
        view.layoutIfNeeded()
        
        return view
    }
    
    
    // MARK:- Button Action
    @IBAction func buttonCancelClicked(_:UIButton) {
        self.removeFromSuperview()
    }
    
    // MARK: Tableview Delegates
    func numberOfSections(in tableView: UITableView) -> Int {
        return 1
    }
    
    func tableView(_ tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
        return self.activity.activityRuns.count
    }
    
    func tableView(_ tableView: UITableView, cellForRowAt indexPath: IndexPath) -> UITableViewCell {
        
        let cell = tableView.dequeueReusableCell(withIdentifier: "Cell", for: indexPath)
        cell.textLabel?.font = UIFont(name: "HelveticaNeue-Light", size: 13)
        let activityRun = self.activity.activityRuns[indexPath.row]
        cell.textLabel?.text = ActivitySchedules.formatter.string(from: activityRun.startDate) + " - "
            + ActivitySchedules.formatter.string(from: activityRun.endDate)
        
        if activityRun.runId == self.activity.currentRunId {
            cell.textLabel?.textColor = kBlueColor
            
        } else if activityRun.runId < self.activity.currentRunId {
            cell.textLabel?.textColor = UIColor.gray
        }
        cell.textLabel?.textAlignment = .center
        return cell
    }
    
    private static let formatter: DateFormatter = {
        let formatter = DateFormatter()
        formatter.dateFormat = "hh:mma, MMM dd YYYY"
        //formatter.timeZone = TimeZone.init(abbreviation:"GMT")
        return formatter
    }()
}


class ResponseDataFetch:NMWebServiceDelegate{
    
    var dataSourceKeysForLabkey: Array<Dictionary<String,String>> = []
    
     static let labkeyDateFormatter: DateFormatter = {
        //2017/06/13 18:12:13
        let formatter = DateFormatter()
        formatter.timeZone = TimeZone.init(identifier: "America/New_York")
        formatter.dateFormat = "YYYY-MM-dd HH:mm:ss.SSS"
        
        return formatter
    }()
    
    public static let localDateFormatter: DateFormatter = {
        //2017/06/13 18:12:13
        let formatter = DateFormatter()
        formatter.timeZone = TimeZone.current
        formatter.dateFormat = "YYYY/MM/dd HH:mm:ss"
        
        return formatter
    }()
    
    
    init() {
        
    }
    
    // MARK: Helper Methods
    func checkUpdates() {
        if StudyUpdates.studyActivitiesUpdated {
            self.sendRequestToGetDashboardInfo()
            
        } else {
            
            //Load Stats List from DB
            DBHandler.loadStatisticsForStudy(studyId: (Study.currentStudy?.studyId)!) { (statiticsList) in
                
                if statiticsList.count != 0 {
                    StudyDashboard.instance.statistics = statiticsList
                    self.getDataKeysForCurrentStudy()
                    let appDelegate = (UIApplication.shared.delegate as? AppDelegate)!
                    appDelegate.addAndRemoveProgress(add: true)
                    
                } else {
                    //Fetch DashboardInfo
                    self.sendRequestToGetDashboardInfo()
                }
            }
        }
    }
    
    func sendRequestToGetDashboardInfo(){
        WCPServices().getStudyDashboardInfo(studyId: (Study.currentStudy?.studyId)!, delegate: self)
    }
    
    func handleExecuteSQLResponse(){
        
        if !self.dataSourceKeysForLabkey.isEmpty {
            self.dataSourceKeysForLabkey.removeFirst()
        }
         self.sendRequestToGetDashboardResponse()
    }
    
    func getDataKeysForCurrentStudy(){
        
        DBHandler.getDataSourceKeyForActivity(studyId: (Study.currentStudy?.studyId)!) { (activityKeys) in
            print(activityKeys)
            if activityKeys.count > 0 {
                self.dataSourceKeysForLabkey = activityKeys
                print("dashboardResponse Called: ")
                
                //GetDashboardResponse from server
                self.sendRequestToGetDashboardResponse()
            }
        }
        
    }
    
    func sendRequestToGetDashboardResponse() {
        
        if self.dataSourceKeysForLabkey.count != 0 {
            let details = self.dataSourceKeysForLabkey.first
            let activityId = details?["activityId"]
            var tableName = activityId
            let activity = Study.currentStudy?.activities.filter({$0.actvityId == activityId}).first
            var keys = details?["keys"]
            if activity?.type == ActivityType.activeTask {
                if activity?.taskSubType == "fetalKickCounter" {
                    //                keys = "\"count\",duration,FetalKickId"
                    //                tableName = activityId!+activityId!
                    
                    keys = "\"count\",duration"
                    tableName = activityId!+activityId!
                } else if activity?.taskSubType == "towerOfHanoi" {
                    keys = "numberOfMoves"
                    tableName = activityId!+activityId!
                } else if activity?.taskSubType == "spatialSpanMemory" {
                    keys = "NumberofGames,Score,NumberofFailures"
                    tableName = activityId!+activityId!
                }
            }
            let participantId = Study.currentStudy?.userParticipateState.participantId
            //Get Survey Response from Server
            LabKeyServices().getParticipantResponse(tableName: tableName!,activityId: activityId!, keys: keys!, participantId: participantId!, delegate: self)
        } else {
            
            //save response in database
            
            let responses = StudyDashboard.instance.dashboardResponse
            for  response in responses{
                
                let activityId = response.activityId
                let activity = Study.currentStudy?.activities.filter({$0.actvityId == activityId}).first
                var key = response.key
                if activity?.type == ActivityType.activeTask {
                    
                    if activity?.taskSubType == "fetalKickCounter" || activity?.taskSubType == "towerOfHanoi" {
                        key = activityId!
                    }
                    
                }
                
                let values = response.values
                for value in values {
                    let responseValue = (value["value"] as? Float)!
                    let count = (value["count"] as? Float)!
                    //SetData Format
                    let date = ResponseDataFetch.labkeyDateFormatter.date(from: (value["date"] as? String)!)
                    let localDateAsString = ResponseDataFetch.localDateFormatter.string(from: date!)
                    
                    let localDate = ResponseDataFetch.localDateFormatter.date(from: localDateAsString)
                    //Save Stats to DB
                    DBHandler.saveStatisticsDataFor(activityId: activityId!, key: key!, data: responseValue, fkDuration: Int(count),date: localDate!)
                }
                
            }
            let key = "LabKeyResponse" + (Study.currentStudy?.studyId)!
            UserDefaults.standard.set(true, forKey: key)
            
            let appDelegate = (UIApplication.shared.delegate as? AppDelegate)!
            appDelegate.addAndRemoveProgress(add: false)
            
        }
    }
    
    // MARK: Webservice Delegates
    func startedRequest(_ manager: NetworkManager, requestName: NSString) {
        Logger.sharedInstance.info(" START requestname : \(requestName)")
    }
    func finishedRequest(_ manager: NetworkManager, requestName: NSString, response: AnyObject?) {
        Logger.sharedInstance.info("requestname : \(requestName) Response : \(String(describing:response) )")
        
        if requestName as String == WCPMethods.studyDashboard.method.methodName {
            self.getDataKeysForCurrentStudy()
            
        } else if requestName as String == ResponseMethods.executeSQL.description {
            self.handleExecuteSQLResponse()
        }
        
    }
    func failedRequest(_ manager: NetworkManager, requestName: NSString, error: NSError) {
        Logger.sharedInstance.info("requestname : \(requestName)")
        if requestName as String == ResponseMethods.executeSQL.description {
            self.handleExecuteSQLResponse()
            
        } else {
            //Do Nothing
        }
    }
    
}


