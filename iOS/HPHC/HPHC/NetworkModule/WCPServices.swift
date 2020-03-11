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

//Api constants
let kNotificationSkip = "skip"

let kActivity = "activity"

//study
let kStudyTitle = "title"
let kStudyCategory = "category"
let kStudySponserName = "sponsorName"
let kStudyDescription = "description"
let kStudyTagLine = "tagline"
let kStudyVersion = "studyVersion"

let kStudyStatus = "status"
let kActivityStatus = "activityState"
let kStudyLogoURL = "logo"
let kStudySettings = "settings"
let kStudyEnrolling = "enrolling"
let kStudyPlatform = "platform"
let kStudyRejoin = "rejoin"
let kStudyParticipantId = "participantId"
let kStudyEnrolledDate = "enrolledDate"

//resources
let kResources = "resources"


//overview
let kOverViewInfo = "info"
let kOverviewType = "type"
let kOverviewImageLink = "image"
let kOverviewTitle = "title"
let kOverviewText = "text"
let kOverviewMediaLink = "videoLink" // link
let kOverviewWebsiteLink = "website"

//notification
let kNotifications = "notifications"
let kNotificationId = "notificationId"
let kNotificationType = "type"
let kNotificationSubType = "subtype"
let kNotificationAudience = "audience"
let kNotificationTitle = "title"
let kNotificationMessage = "message"
let kNotificationStudyId = "studyId"
let kNotificationActivityId = "activityId"


//feedback
let kFeedbackSubject = "subject"
let kFeedbackBody = "body"

//contactus
let kContactusEmail = "email"
let kContactusFirstname = "firstName"

//studyupdates
let kStudyUpdates = "updates"
let kStudyCurrentVersion = "currentVersion"
let kStudyConsent = "consent"
let kStudyActivities = "activities"
let kStudyResources = "resources"
let kStudyInfo = "info"


//StudyWithdrawalConfigration
let kStudyWithdrawalConfigration = "withdrawalConfig"
let kStudyWithdrawalMessage = "message"
let kStudyWithdrawalType = "type"

//study AnchorDate

let kStudyAnchorDate = "anchorDate"
let kStudyAnchorDateType = "type"
let kStudyAnchorDateActivityId = "activityId"
let kStudyAnchorDateActivityVersion = "activityVersion"
let kStudyAnchorDateQuestionKey = "key"
let kStudyAnchorDateQuestionInfo = "questionInfo"



class WCPServices: NSObject {
    let networkManager = NetworkManager.sharedInstance()
    var delegate: NMWebServiceDelegate! = nil
    var delegateSource: NMWebServiceDelegate?
    
    // MARK:Requests
    
    func checkForAppUpdates(delegate: NMWebServiceDelegate){
        
        self.delegate = delegate
        let method = WCPMethods.versionInfo.method
        self.sendRequestWith(method: method, params: nil, headers: nil)
    }
    
    func getStudyBasicInfo(_ delegate:NMWebServiceDelegate) {
        
        self.delegate = delegate
        
        let method = WCPMethods.study.method
        let params:Dictionary<String, String> = ["studyId":Utilities.standaloneStudyId()]
        self.sendRequestWith(method: method, params: params, headers: nil)
    }
    func getStudyList(_ delegate: NMWebServiceDelegate){
        
        print("StudyList Start \(Date())")
        self.delegate = delegate
        
        let method = WCPMethods.studyList.method
        let params = Dictionary<String, Any>()
        self.sendRequestWith(method: method, params: params, headers: nil)
    }
    
    func getConsentDocument(studyId: String, delegate: NMWebServiceDelegate){
        
        self.delegate = delegate
        
        let header = [kStudyId: studyId,"consentVersion": ""]
        let method = WCPMethods.consentDocument.method
        
        self.sendRequestWith(method: method, params: header, headers: nil)
    }
    
    func getGatewayResources(delegate: NMWebServiceDelegate){
        self.delegate = delegate
        
        let method = WCPMethods.gatewayInfo.method
        let params = Dictionary<String, Any>()
        self.sendRequestWith(method: method, params: params, headers: nil)
    }
    
    func getEligibilityConsentMetadata(studyId: String, delegate: NMWebServiceDelegate){
        
        self.delegate = delegate
        
        let method = WCPMethods.eligibilityConsent.method
        let headerParams = [kStudyId: studyId]
        self.sendRequestWith(method: method, params: headerParams, headers: nil)
    }
    func getResourcesForStudy(studyId: String, delegate: NMWebServiceDelegate){
        
        self.delegate = delegate
        
        let method = WCPMethods.resources.method
        let headerParams = [kStudyId: studyId]
        self.sendRequestWith(method: method, params: headerParams, headers: nil )
    }
    
    func getStudyInformation(studyId: String, delegate: NMWebServiceDelegate){
        
        self.delegate = delegate
        
        let method = WCPMethods.studyInfo.method
        let params = [kStudyId: studyId]
        self.sendRequestWith(method: method, params: params, headers: nil)
    }
    
    func getStudyActivityList(studyId: String, delegate: NMWebServiceDelegate){
        
        self.delegate = delegate
        
        let method = WCPMethods.activityList.method
        let headerParams = [kStudyId: studyId]
        self.sendRequestWith(method: method, params: headerParams, headers: nil)
    }
    
    func getStudyActivityMetadata(studyId: String, activityId: String,activityVersion: String, delegate: NMWebServiceDelegate){
        
        self.delegate = delegate
        
        
        let method = WCPMethods.activity.method
        let headerParams = [kStudyId: studyId,
                            kActivityId: activityId,
                            kActivityVersion: activityVersion]
        self.sendRequestWith(method: method, params: headerParams, headers: nil)
    }
    
    func getStudyDashboardInfo(studyId: String, delegate: NMWebServiceDelegate){
        
        self.delegate = delegate
        let method = WCPMethods.studyDashboard.method
        let params = [kStudyId: studyId]
        self.sendRequestWith(method: method, params: params, headers: nil)
    }
    
    func getTermsPolicy(studyId:String, delegate:NMWebServiceDelegate){
        
        self.delegate = delegate
        
        let method = WCPMethods.termsPolicy.method
        let params = [kStudyId: studyId]
        self.sendRequestWith(method: method, params: params, headers: nil)
    }
    
    func getTermsPolicy(delegate: NMWebServiceDelegate){
        
        self.delegate = delegate
        let method = WCPMethods.termsPolicy.method
        self.sendRequestWith(method: method, params: nil, headers: nil)
    }
    
    func getNotification(skip: Int, delegate: NMWebServiceDelegate){
        
        self.delegate = delegate
        let method = WCPMethods.notifications.method
        let headerParams = [kNotificationSkip: "\(skip)"]
        self.sendRequestWith(method: method, params: headerParams, headers: nil)
        
    }
    
    func sendUserFeedback(delegate: NMWebServiceDelegate){
        
        self.delegate = delegate
        
        let method = WCPMethods.feedback.method
        let params = [kFeedbackBody: FeedbackDetail.feedback,
                      kFeedbackSubject: FeedbackDetail.subject]
        self.sendRequestWith(method: method, params: params, headers: nil)
        
    }
    
    func sendUserContactUsRequest(delegate: NMWebServiceDelegate){
        
        self.delegate = delegate
        
        let method = WCPMethods.contactUs.method
        let params = [kFeedbackBody:ContactUsFeilds.message,
                      kFeedbackSubject:ContactUsFeilds.subject,
                      kContactusEmail:ContactUsFeilds.email,
                      kContactusFirstname:ContactUsFeilds.firstName]
        self.sendRequestWith(method: method, params: params, headers: nil)
        
    }
    
    func getStudyUpdates(study: Study,delegate: NMWebServiceDelegate){
        self.delegate = delegate
        
        let method = WCPMethods.studyUpdates.method
        let headerParams = [kStudyId: study.studyId!,
                            kStudyVersion: study.version!]
        self.sendRequestWith(method: method, params: headerParams, headers: nil)
        
    }
    
    /*
    func checkForAppUpdates(delegate: NMWebServiceDelegate){
        
        self.delegate = delegate
        let method = WCPMethods.appUpdates.method
        let headerParams = [kAppVersion: Utilities.getAppVersion(),
                            kOSType: "ios"]
        self.sendRequestWith(method: method, params: headerParams, headers: nil)
    }
     */
    
    // MARK:Parsers
    func handleStudyBasicInfo(response: Dictionary<String, Any>){
        print("handleStudyBasicInfo")
        
        let studies = response[kStudies] as! Array<Dictionary<String,Any>>
        var listOfStudies: Array<Study> = []
        for study in studies{
            let studyModelObj = Study(studyDetail: study)
            listOfStudies.append(studyModelObj)
        }
        Logger.sharedInstance.info("Studies Parsing Finished")
        //assgin to Gateway
        Gateway.instance.studies = listOfStudies
        
        Logger.sharedInstance.info("Studies Saving in DB")
        //save in database
        DBHandler().saveStudies(studies: listOfStudies)
    }
    func handleStudyList(response: Dictionary<String, Any>){
        
        print("StudyList Parsing Start \(Date().timeIntervalSince1970)")
        Logger.sharedInstance.info("Studies Parsing Start")
        
        let studies = response[kStudies] as! Array<Dictionary<String,Any>>
        var listOfStudies: Array<Study> = []
        for study in studies{
            let studyModelObj = Study(studyDetail: study)
            listOfStudies.append(studyModelObj)
        }
        Logger.sharedInstance.info("Studies Parsing Finished")
        //assgin to Gateway
        Gateway.instance.studies = listOfStudies
        
        Logger.sharedInstance.info("Studies Saving in DB")
        //save in database
        DBHandler().saveStudies(studies: listOfStudies)
        print("StudyList Parsing Finished \(Date().timeIntervalSince1970)")
    }
    
    func handleEligibilityConsentMetaData(response: Dictionary<String, Any>){
        let consent = response[kConsent] as! Dictionary<String, Any>
        let eligibility = response[kEligibility] as! Dictionary<String, Any>
        
        if Utilities.isValidObject(someObject: consent as AnyObject?){
            ConsentBuilder.currentConsent = ConsentBuilder()
            ConsentBuilder.currentConsent?.initWithMetaData(metaDataDict: consent)
        }
        
        if Utilities.isValidObject(someObject: eligibility as AnyObject?){
            EligibilityBuilder.currentEligibility = EligibilityBuilder()
            EligibilityBuilder.currentEligibility?.initEligibilityWithDict(eligibilityDict: eligibility )
        }
        
    }
    
    
    func handleResourceListForGateway(response: Dictionary<String, Any>) {
        
        let resources = response[kResources] as! Array<Dictionary<String,Any>>
        var listOfResources: Array<Resource>! = []
        for resource in resources{
            let resourceObj = Resource(detail: resource)
            listOfResources.append(resourceObj)
        }
        
        //assgin to Gateway
        Gateway.instance.resources = listOfResources
    }
    
    func handleResourceForStudy(response: Dictionary<String, Any>){
        
        //Testing
//        let filePath  = Bundle.main.path(forResource: "ResourceList", ofType: "json")
//        let data = NSData(contentsOfFile: filePath!)
//
//        var resources:Array<Dictionary<String,Any>> = []
//        do {
//            let res = try JSONSerialization.jsonObject(with: data! as Data, options: []) as? Dictionary<String,Any>
//
//            resources = res?[kResources] as! Array<Dictionary<String,Any>>
//        }
//        catch {
//            print("json error: \(error.localizedDescription)")
//        }
        
        //Actual
        let resources = response[kResources] as! Array<Dictionary<String,Any>>
        var listOfResources: Array<Resource>! = []
        for resource in resources{
            let resourceObj = Resource()
            resourceObj.level = ResourceLevel.study
            resourceObj.setResource(dict: resource as NSDictionary)
            
            listOfResources.append(resourceObj)
        }
        
        
        //save in database
        DBHandler.saveResourcesForStudy(studyId: (Study.currentStudy?.studyId)!, resources: listOfResources)
        
        //assign to Gateway
        Study.currentStudy?.resources = listOfResources
        
    }
    
    func handleStudyDashboard(response: Dictionary<String, Any>){
        
        guard let dashboard = response["dashboard"] as? Dictionary<String, Any> else { return }
        
        if Utilities.isValidObject(someObject: dashboard as AnyObject?){
            
            if Study.currentStudy != nil {
                
                //stats
                let statsList = dashboard["statistics"] as! Array<Dictionary<String,Any>>
                var listOfStats: Array<DashboardStatistics>! = []
                for stat in statsList{
                    
                    let dashboardStat = DashboardStatistics.init(detail: stat)
                    listOfStats.append(dashboardStat)
                }
                
                StudyDashboard.instance.statistics = listOfStats
                //save stats in database
                DBHandler.saveDashBoardStatistics(studyId: (Study.currentStudy?.studyId)!, statistics: listOfStats)
                
                //charts
                let chartList = dashboard["charts"] as! Array<Dictionary<String,Any>>
                var listOfCharts: Array<DashboardCharts>! = []
                for chart in chartList{
                    
                    let dashboardChart = DashboardCharts.init(detail: chart)
                    listOfCharts.append(dashboardChart)
                }
                
                StudyDashboard.instance.charts = listOfCharts
                
                //save charts in database
                DBHandler.saveDashBoardCharts(studyId: (Study.currentStudy?.studyId)!, charts: listOfCharts)
            }
        }
    }
    
    func handleConsentDocument(response: Dictionary<String, Any>){
        
        let consentDict = response[kConsent] as! Dictionary<String, Any>
        
        if Utilities.isValidObject(someObject: consentDict as AnyObject?) {
            
            Study.currentStudy?.consentDocument = ConsentDocument()
            Study.currentStudy?.consentDocument?.initData(consentDoucumentdict: consentDict)
        }
        
    }
    
    
    func handleTermsAndPolicy(response: Dictionary<String, Any>){
        
        TermsAndPolicy.currentTermsAndPolicy =  TermsAndPolicy()
        TermsAndPolicy.currentTermsAndPolicy?.initWithDict(dict: response)
        
    }
    
    
    func handleStudyInfo(response: Dictionary<String, Any>){
        
        if Study.currentStudy != nil {
            
            let overviewList = response[kOverViewInfo] as! Array<Dictionary<String,Any>>
            var listOfOverviews: Array<OverviewSection> = []
            for overview in overviewList{
                let overviewObj = OverviewSection(detail: overview)
                listOfOverviews.append(overviewObj)
            }
            
            //create new Overview object
            let overview = Overview()
            overview.type = .study
            overview.sections = listOfOverviews
            overview.websiteLink = response[kOverViewWebsiteLink] as? String
            
            
            //update overview object to current study
            Study.currentStudy?.overview = overview
            
            //anchorDate
            if Utilities.isValidObject(someObject: response[kStudyAnchorDate] as AnyObject?){
                
                let studyAndhorDate = StudyAnchorDate.init(detail: response[kStudyAnchorDate] as! Dictionary<String,Any>)
                
                //update anchorDate to current study
                Study.currentStudy?.anchorDate = studyAndhorDate
                
                DBHandler.saveAnchorDateDetail(anchorDate: studyAndhorDate, studyId: (Study.currentStudy?.studyId)!)
            }
            
            //WithdrawalConfigration
            if Utilities.isValidObject(someObject: response[kStudyWithdrawalConfigration] as AnyObject?){
                
                let studyWithdrawalConfig = StudyWithdrawalConfigration.init(withdrawalConfigration: response[kStudyWithdrawalConfigration] as! Dictionary<String,Any>)
                
                
                //update anchorDate to current study
                Study.currentStudy?.withdrawalConfigration = studyWithdrawalConfig
                DBHandler.saveWithdrawalConfigration(withdrawalConfigration:studyWithdrawalConfig ,studyId: (Study.currentStudy?.studyId)!)
            }
            
            //save in database
            DBHandler.saveStudyOverview(overview: overview, studyId: (Study.currentStudy?.studyId)!)
        }
        
    }
    
    func handleStudyActivityList(response: Dictionary<String, Any>){
        
        Logger.sharedInstance.info("Activities Parsing Start")
        
//        //Testing
//        let filePath  = Bundle.main.path(forResource: "Activitylist", ofType: "json")
//        let data = NSData(contentsOfFile: filePath!)
//
//        var activities:Array<Dictionary<String,Any>> = []
//        do {
//            let res = try JSONSerialization.jsonObject(with: data! as Data, options: []) as? Dictionary<String,Any>
//
//            activities = res?[kActivites] as! Array<Dictionary<String, Any>>
//        }
//        catch {
//            print("json error: \(error.localizedDescription)")
//        }
        
        //Actual
        let activities = response[kActivites] as! Array<Dictionary<String,Any>>
        
        if Utilities.isValidObject(someObject: activities as AnyObject? ) {
            
            if Study.currentStudy != nil {
                var activityList: Array<Activity> = []
                for activityDict in activities{
                    
                    let activity = Activity.init(studyId: (Study.currentStudy?.studyId)!, infoDict: activityDict)
                    activityList.append(activity)
                }
                
                Logger.sharedInstance.info("Activities Parsing Finished")
                //save to current study object
                Study.currentStudy?.activities = activityList
                Logger.sharedInstance.info("Activities Saving in DB")
                //save in database
                DBHandler.saveActivities(activityies: (Study.currentStudy?.activities)!)
            }
        } else {
            Logger.sharedInstance.debug("activities is null:\(activities)")
        }
        
    }
    
    func handleGetStudyActivityMetadata(response: Dictionary<String, Any>){
        
        /*
        let filePath  = Bundle.main.path(forResource: "Activity_Metadata_Other", ofType: "json")
        let data = NSData(contentsOfFile: filePath!)
        
        var activities: [String:Any] = [:]
        
        do {
            let res = try JSONSerialization.jsonObject(with: data! as Data, options: []) as? Dictionary<String,Any>
            
            activities = res?[kActivity] as! [String: Any]
        }
        catch {
            print("json error: \(error.localizedDescription)")
        }
        */
       // Study.currentActivity?.setActivityMetaData(activityDict: activities)
        
        Study.currentActivity?.setActivityMetaData(activityDict: response[kActivity] as! Dictionary<String, Any>)
        
        if Utilities.isValidObject(someObject: Study.currentActivity?.steps as AnyObject?){
            
            ActivityBuilder.currentActivityBuilder = ActivityBuilder()
            ActivityBuilder.currentActivityBuilder.initWithActivity(activity:Study.currentActivity! )
        }
        
        //Save and Update activity meta data
        DBHandler.saveActivityMetaData(activity: Study.currentActivity!, data: response)
        DBHandler.updateActivityMetaData(activity: Study.currentActivity!)
        
    }
    
    func handleGetNotification(response: Dictionary<String, Any>){
        
        let notifications = response[kNotifications] as! Array<Dictionary<String,Any>>
        var listOfNotifications: Array<AppNotification>! = []
        for notification in notifications{
            let overviewObj = AppNotification(detail: notification)
            listOfNotifications.append(overviewObj)
        }
        
        Gateway.instance.notification = listOfNotifications
        
        //save in database
        DBHandler().saveNotifications(notifications: listOfNotifications )
        
    }
    
    func handleContactUsAndFeedback(response: Dictionary<String, Any>){
    }
    
    func handleStudyUpdates(response: Dictionary<String, Any>){
        
        if Utilities.isValidObject(someObject: response as AnyObject?){
            _ = StudyUpdates(detail: response)
        }
    }
    
    private func sendRequestWith(method: Method, params: Dictionary<String, Any>?,headers: Dictionary<String, String>?){
        
        networkManager.composeRequest(WCPConfiguration.configuration,
                                      method: method,
                                      params: params as NSDictionary?,
                                      headers: headers as NSDictionary?,
                                      delegate: delegateSource != nil ? delegateSource! : self)
    }
    
    
}
extension WCPServices:NMWebServiceDelegate{
    func startedRequest(_ manager: NetworkManager, requestName: NSString) {
        Logger.sharedInstance.info("WCP Request Called: \(requestName)")
        if delegate != nil {
            delegate.startedRequest(manager, requestName: requestName)
        }
    }
    func finishedRequest(_ manager: NetworkManager, requestName: NSString, response: AnyObject?) {
       
        print("StudyList Finished \(Date())")
        Logger.sharedInstance.info("WCP Received Data: \(requestName)")
        let methodName = WCPMethods(rawValue: requestName as String)!
        
        switch methodName {
        case .gatewayInfo:
            self.handleResourceListForGateway(response: response as! Dictionary<String, Any>)
        case .study:
            self.handleStudyBasicInfo(response: response as! Dictionary<String, Any>)
        case .studyList:
            self.handleStudyList(response: response as! Dictionary<String, Any>)
        case .eligibilityConsent:
            self.handleEligibilityConsentMetaData(response: response as! Dictionary<String, Any>)
        case .resources:
            self.handleResourceForStudy(response: response as! Dictionary<String, Any>)
        case .consentDocument:
            self.handleConsentDocument(response: response as! Dictionary<String, Any>)
        case .studyInfo:
            self.handleStudyInfo(response: response as! Dictionary<String, Any>)
        case .activityList:
            self.handleStudyActivityList(response: response as! Dictionary<String, Any>)
        case .activity:
            self.handleGetStudyActivityMetadata(response: response as! Dictionary<String, Any>)
        case .studyDashboard:
            self.handleStudyDashboard(response: response as! Dictionary<String, Any>)
        case .termsPolicy:
            self.handleTermsAndPolicy(response:response as! Dictionary<String, Any> )
        case .notifications:
            self.handleGetNotification(response:response as! Dictionary<String, Any> )
        case .contactUs,.feedback:
            self.handleContactUsAndFeedback(response:response as! Dictionary<String, Any> )
        case .studyUpdates:
            self.handleStudyUpdates(response: response as! Dictionary<String, Any>)
        case .appUpdates: break
            
        default: break
        }
        
        if delegate != nil {
            delegate.finishedRequest(manager, requestName: requestName, response: response)
        }
        
    }
    func failedRequest(_ manager: NetworkManager, requestName: NSString, error: NSError) {
        if delegate != nil {
            delegate.failedRequest(manager, requestName: requestName, error: error)
        }
    }
}
