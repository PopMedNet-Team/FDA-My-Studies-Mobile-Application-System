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
import RealmSwift

class DBHandler: NSObject {

    fileprivate class func getRealmObject() -> Realm! {
        
        var realm: Realm!
        do {
            realm = try Realm()
        } catch {
            return nil
        }
        return realm
    }
    
    
    /* Used to save user details like userid, authkey, first name , last name etc*/
    func saveCurrentUser(user: User){
        
        let realm = DBHandler.getRealmObject()!
        let dbUsers = realm.objects(DBUser.self)
        var dbUser = dbUsers.last
        
        if dbUser == nil {
            
            dbUser = DBUser()
            dbUser?.userType = (user.userType?.rawValue)!
            dbUser?.emailId = user.emailId!
            dbUser?.authToken = user.authToken
            dbUser?.userId = user.userId
            //dbUser.firstName = user.firstName
            //dbUser.lastName = user.lastName
            dbUser?.verified = user.verified
            
            dbUser?.refreshToken = user.refreshToken
            
            try? realm.write({
                realm.add(dbUser!, update: true)
                
            })
        }
        else {
            let user = User.currentUser
            do {
                try realm.write({
                    
                    dbUser?.userType = (user.userType?.rawValue)!
                    dbUser?.emailId = user.emailId!
                    dbUser?.authToken = user.authToken
                    
                    //dbUser.firstName = user.firstName
                    //dbUser.lastName = user.lastName
                    dbUser?.verified = user.verified
                    
                    dbUser?.refreshToken = user.refreshToken
                    
                })
            } catch let error {
                print(error)
            }
            
//            try? realm.write({
//
//                dbUser?.userType = (user.userType?.rawValue)!
//                dbUser?.emailId = user.emailId!
//                dbUser?.authToken = user.authToken
//                dbUser?.userId = user.userId
//                //dbUser.firstName = user.firstName
//                //dbUser.lastName = user.lastName
//                dbUser?.verified = user.verified
//
//                dbUser?.refreshToken = user.refreshToken
//
//            })
           
        }
        
        
        
       
//        print("DBPath : varealm.configuration.fileURL)")
//        try? realm.write({
//            realm.add(dbUser!, update: true)
//
//        })
    }
    
    /* Used to initialize the current logged in user*/
    func initilizeCurrentUser(){
        
        let realm = DBHandler.getRealmObject()!
        let dbUsers = realm.objects(DBUser.self)
        let dbUser = dbUsers.last
        
        if dbUser != nil {
            let currentUser = User.currentUser
            currentUser.firstName = dbUser?.firstName
            currentUser.lastName  = dbUser?.lastName
            currentUser.verified = dbUser?.verified
            currentUser.authToken = dbUser?.authToken
            currentUser.userId = dbUser?.userId
            currentUser.emailId = dbUser?.emailId
            currentUser.userType =  (dbUser?.userType).map { UserType(rawValue: $0) }!
            currentUser.refreshToken = dbUser?.refreshToken
          
            let settings = Settings()
            settings.localNotifications = dbUser?.localNotificationEnabled
            settings.passcode = dbUser?.passcodeEnabled
            settings.remoteNotifications = dbUser?.remoteNotificationEnabled
            
            currentUser.settings = settings
        }
        
    }
    
    class func saveUserSettingsToDatabase(){
        
        let realm = DBHandler.getRealmObject()!
        let dbUsers = realm.objects(DBUser.self)
        let dbUser = dbUsers.last
        
        try? realm.write({
            
            let user = User.currentUser
            dbUser?.passcodeEnabled = (user.settings?.passcode)!
            dbUser?.localNotificationEnabled = (user.settings?.localNotifications)!
            dbUser?.remoteNotificationEnabled = (user.settings?.remoteNotifications)!
            
        })
    }
    
    class func resetAuthToken(){
        
        let realm = try! Realm()
        let dbUsers = realm.objects(DBUser.self)
        let dbUser = dbUsers.last
        
        try! realm.write({
            
            dbUser?.authToken = ""
            dbUser?.userType =  UserType.AnonymousUser.rawValue//(user.userType?.rawValue)!
            
        })
    }
    
    /* Used to delete current logged in user*/
    class func deleteCurrentUser(){
        
        let realm = DBHandler.getRealmObject()!
        let dbUsers = realm.objects(DBUser.self)
        let dbUser = dbUsers.last
        try? realm.write {
            realm.delete(dbUser!)
        }
    }
    
     // MARK:Study
    /* Save studies 
     @params: studies - Array
     */
    func saveStudies(studies: Array<Study>){
        
        let realm = DBHandler.getRealmObject()!
        let dbStudiesArray = realm.objects(DBStudy.self)
      
        var dbStudies: Array<DBStudy> = []
        for study in studies {
            
            //some studies are already present in db
            var dbStudy: DBStudy?
            if dbStudiesArray.count > 0 {
                 dbStudy = dbStudiesArray.filter({$0.studyId ==  study.studyId}).last
            }
            
            if dbStudy == nil {
                dbStudy = DBHandler.getDBStudy(study: study)
                dbStudies.append(dbStudy!)
            }else {
                
                try? realm.write({
                    dbStudy?.category = study.category
                    dbStudy?.name = study.name
                    dbStudy?.sponserName = study.sponserName
                    dbStudy?.tagLine = study.description
                    dbStudy?.logoURL = study.logoURL
                    dbStudy?.startDate = study.startDate
                    dbStudy?.endEnd = study.endEnd
                    dbStudy?.status = study.status.rawValue
                    dbStudy?.enrolling = study.studySettings.enrollingAllowed
                    dbStudy?.rejoin = study.studySettings.rejoinStudyAfterWithdrawn
                    dbStudy?.platform = study.studySettings.platform
                    dbStudy?.participatedStatus = study.userParticipateState.status.rawValue
                    dbStudy?.participatedId = study.userParticipateState.participantId
                    dbStudy?.joiningDate = study.userParticipateState.joiningDate
                    dbStudy?.completion = study.userParticipateState.completion
                    dbStudy?.adherence = study.userParticipateState.adherence
                    dbStudy?.bookmarked = study.userParticipateState.bookmarked
                    
                    if dbStudy?.participatedStatus == UserStudyStatus.StudyStatus.inProgress.rawValue {
                        dbStudy?.updatedVersion = study.version
                        
                    }else {
                        dbStudy?.updatedVersion = study.version
                    }
                    
                })
                
            }
           
        }
        
        try? realm.write({
            realm.add(dbStudies, update: true)
            
        })
        
        Logger.sharedInstance.info("Studies Saved in DB")
    }
    
    /**
     creates an instance of DBStudy
    */
    private class func getDBStudy(study: Study) ->DBStudy {
        
        let dbStudy = DBStudy()
        dbStudy.studyId = study.studyId
        dbStudy.category = study.category
        dbStudy.name = study.name
        dbStudy.sponserName = study.sponserName
        dbStudy.tagLine = study.description
        dbStudy.version = study.version
        dbStudy.updatedVersion = study.version
        dbStudy.logoURL = study.logoURL
        dbStudy.startDate = study.startDate
        dbStudy.endEnd = study.endEnd
        dbStudy.enrolling = study.studySettings.enrollingAllowed
        dbStudy.rejoin = study.studySettings.rejoinStudyAfterWithdrawn
        dbStudy.platform = study.studySettings.platform
        dbStudy.status = study.status.rawValue
        dbStudy.participatedStatus = study.userParticipateState.status.rawValue
        dbStudy.participatedId = study.userParticipateState.participantId
        dbStudy.joiningDate = study.userParticipateState.joiningDate
        dbStudy.completion = study.userParticipateState.completion
        dbStudy.adherence = study.userParticipateState.adherence
        dbStudy.bookmarked = study.userParticipateState.bookmarked
        dbStudy.withdrawalConfigrationMessage = study.withdrawalConfigration?.message
        dbStudy.withdrawalConfigrationType = study.withdrawalConfigration?.type?.rawValue
        return dbStudy
        
    }
    
    /**
     Fetches list of Studies from DB
     return completion handler with array of studies
    */
    class func loadStudyListFromDatabase(completionHandler: @escaping (Array<Study>) -> ()) {
        
        let realm = DBHandler.getRealmObject()!
        let dbStudies = realm.objects(DBStudy.self)
        
        User.currentUser.participatedStudies.removeAll()
        var studies: Array<Study> = []
        for dbStudy in dbStudies {

            let study = Study()
            study.studyId = dbStudy.studyId
            study.category = dbStudy.category
            study.name = dbStudy.name
            study.sponserName = dbStudy.sponserName
            study.description = dbStudy.tagLine
            study.version = dbStudy.version
            study.newVersion = dbStudy.updatedVersion
            study.logoURL = dbStudy.logoURL
            study.startDate = dbStudy.startDate
            study.endEnd = dbStudy.endEnd
            study.status = StudyStatus(rawValue: dbStudy.status!)!
            study.signedConsentVersion = dbStudy.signedConsentVersion
            study.signedConsentFilePath = dbStudy.signedConsentFilePath
            study.activitiesLocalNotificationUpdated = dbStudy.activitiesLocalNotificationUpdated
            
            //settings
            let studySettings = StudySettings()
            studySettings.enrollingAllowed = dbStudy.enrolling
            studySettings.rejoinStudyAfterWithdrawn = dbStudy.rejoin
            studySettings.platform = dbStudy.platform!
            study.studySettings = studySettings
            
            //status
            let participatedStatus = UserStudyStatus()
            participatedStatus.status = UserStudyStatus.StudyStatus(rawValue: dbStudy.participatedStatus)!
            participatedStatus.bookmarked = dbStudy.bookmarked
            participatedStatus.studyId = dbStudy.studyId
            participatedStatus.participantId = dbStudy.participatedId
            participatedStatus.adherence = dbStudy.adherence
            participatedStatus.completion = dbStudy.completion
            participatedStatus.joiningDate = dbStudy.joiningDate
            
            study.userParticipateState = participatedStatus
            
            print("status \(dbStudy.participatedStatus)");
            
            //append to user class participatesStudies also
            User.currentUser.participatedStudies.append(participatedStatus)
            
            //anchorDate
            let anchorDate = StudyAnchorDate()
            anchorDate.anchorDateActivityId = dbStudy.anchorDateActivityId
            anchorDate.anchorDateQuestionKey = dbStudy.anchorDateType
            anchorDate.anchorDateActivityVersion = dbStudy.anchorDateActivityVersion
            anchorDate.anchorDateQuestionKey = dbStudy.anchorDateQuestionKey
            anchorDate.anchorDateType = dbStudy.anchorDateType
            anchorDate.date = dbStudy.anchorDate
            
            study.anchorDate = anchorDate
            
            let withdrawalInfo = StudyWithdrawalConfigration()
            withdrawalInfo.message = dbStudy.withdrawalConfigrationMessage
            
            
            if dbStudy.withdrawalConfigrationType != nil {
                 withdrawalInfo.type = StudyWithdrawalConfigrationType(rawValue: dbStudy.withdrawalConfigrationType!)
            }else {
                 withdrawalInfo.type = .notAvailable
            }
            study.withdrawalConfigration = withdrawalInfo
            studies.append(study)
        }
       
        completionHandler(studies)
    }

    /* Save study overview
     @params: overview , String
     */
    class func saveStudyOverview(overview: Overview , studyId: String) {
        
        let realm = DBHandler.getRealmObject()!
        let studies =  realm.objects(DBStudy.self).filter("studyId == %@",studyId)
        let dbStudy = studies.last
        
        //save overview
        let dbStudies = List<DBOverviewSection>()
        for sectionIndex in 0...(overview.sections.count-1) {
            
            let section = overview.sections[sectionIndex]
            let dbOverviewSection = DBOverviewSection()
            dbOverviewSection.title = section.title
            dbOverviewSection.link  = section.link
            dbOverviewSection.imageURL = section.imageURL
            dbOverviewSection.text = section.text
            dbOverviewSection.type = section.type
            dbOverviewSection.studyId = studyId
            dbOverviewSection.sectionId = studyId + "screen\(sectionIndex)"
            dbStudies.append(dbOverviewSection)
        }
        
        try? realm.write({
            
            realm.add(dbStudies,update: true)
            dbStudy?.websiteLink = overview.websiteLink

        })
        
    }
    
    /**
     saves withdrawal configration to DB
     @param withdrawalConfigration, instance of StudyWithdrawalConfigration
     @param studyId, study for which configrations are to be updated
    */
    class func saveWithdrawalConfigration(withdrawalConfigration: StudyWithdrawalConfigration, studyId: String){
        
        let realm = DBHandler.getRealmObject()!
        let studies =  realm.objects(DBStudy.self).filter("studyId == %@",studyId)
        let dbStudy = studies.last
        
        try? realm.write({
            
            dbStudy?.withdrawalConfigrationMessage = withdrawalConfigration.message
            dbStudy?.withdrawalConfigrationType = withdrawalConfigration.type?.rawValue
        })
        
    }

    /**
     saves anchor data to DB
     @param anchorDate, instance of StudyAnchorDate
     @param studyId, study for which anchorDate are to be updated
     */
    class func saveAnchorDateDetail(anchorDate: StudyAnchorDate , studyId: String){
        
        let realm = DBHandler.getRealmObject()!
        let studies =  realm.objects(DBStudy.self).filter("studyId == %@",studyId)
        let dbStudy = studies.last
        
        try? realm.write({
            
            dbStudy?.anchorDateActivityId = anchorDate.anchorDateActivityId
            dbStudy?.anchorDateType = anchorDate.anchorDateType
            dbStudy?.anchorDateActivityVersion = anchorDate.anchorDateActivityVersion
            dbStudy?.anchorDateQuestionKey = anchorDate.anchorDateQuestionKey
        })
    }
    
    /**
     saves anchor data to DB
     @param anchorDate, instance of StudyAnchorDate
     @param studyId, study for which configrations are to be updated
     */
    class func saveAnchorDate(date: Date,studyId: String){
        
        let realm = DBHandler.getRealmObject()!
        let studies =  realm.objects(DBStudy.self).filter("studyId == %@",studyId)
        let dbStudy = studies.last
        
        try? realm.write({
            
            dbStudy?.anchorDate = date
        })

    }
    
    /**
     Fetches overview from DB
     return completion handler with instance of overview
     */
    class func loadStudyOverview(studyId: String,completionHandler: @escaping (Overview?) -> ()){
        
        let realm = DBHandler.getRealmObject()!
        let studies =  realm.objects(DBOverviewSection.self).filter("studyId == %@",studyId)
        let study =  realm.objects(DBStudy.self).filter("studyId == %@",studyId).last
        
        if studies.count > 0 {
            
            // inilize OverviewSection from database
            var overviewSections: Array<OverviewSection> = []
            for dbSection in studies {
                let section = OverviewSection()
                
                section.title = dbSection.title
                section.imageURL = dbSection.imageURL
                section.link = dbSection.link
                section.type = dbSection.type
                section.text = dbSection.text
                overviewSections.append(section)
            }
            
            //Create Overview object  
            let overview = Overview()
            overview.type = .study
            overview.websiteLink = study?.websiteLink
            overview.sections = overviewSections
            completionHandler(overview)
            
        }else {
            completionHandler(nil)
        }
    }
    
    /**
     updates studyUpdates to DB
     @param updateDetails, instance of StudyUpdates
     @param study, study for which configrations are to be updated
     */
    class func updateMetaDataToUpdateForStudy(study: Study , updateDetails: StudyUpdates?) {
        
        let realm = DBHandler.getRealmObject()!
        let studies =  realm.objects(DBStudy.self).filter("studyId == %@",study.studyId)
        let dbStudy = studies.last
        
        try? realm.write({
            
            dbStudy?.updateResources = StudyUpdates.studyResourcesUpdated
            dbStudy?.updateConsent = StudyUpdates.studyConsentUpdated
            dbStudy?.updateActivities = StudyUpdates.studyActivitiesUpdated
            dbStudy?.updateInfo = StudyUpdates.studyInfoUpdated
            if StudyUpdates.studyVersion != nil {
                dbStudy?.version = StudyUpdates.studyVersion
                
            }else {
                dbStudy?.version = dbStudy?.updatedVersion
            }
        })
    }
    
    /**
     Updates StudyParticipation status of user to DB
    */
    class func updateStudyParticipationStatus(study: Study) {
        
        let realm = DBHandler.getRealmObject()!
        let studies =  realm.objects(DBStudy.self).filter("studyId == %@",study.studyId)
        let dbStudy = studies.last
        
        try? realm.write({
            
             dbStudy?.participatedStatus = study.userParticipateState.status.rawValue
             dbStudy?.participatedId = study.userParticipateState.participantId
             dbStudy?.joiningDate = study.userParticipateState.joiningDate
             dbStudy?.completion = study.userParticipateState.completion
             dbStudy?.adherence = study.userParticipateState.adherence
        })
    }
    
    /**
     Fetches StudyDetails from DB
     return completion handler with boolean
     */
    class func loadStudyDetailsToUpdate(studyId: String,completionHandler: @escaping (Bool) -> ()) {
        
        let realm = DBHandler.getRealmObject()!
        let studies =  realm.objects(DBStudy.self).filter("studyId == %@",studyId)
        let dbStudy = studies.last
        
        StudyUpdates.studyActivitiesUpdated = (dbStudy?.updateActivities)!
        StudyUpdates.studyConsentUpdated = (dbStudy?.updateConsent)!
        StudyUpdates.studyResourcesUpdated = (dbStudy?.updateResources)!
        StudyUpdates.studyInfoUpdated = (dbStudy?.updateInfo)!
        completionHandler(true)
    }
    
    /**
     saves study consent Info to DB
    */
    class func saveConsentInformation(study: Study){
        
        let realm = DBHandler.getRealmObject()!
        let studies =  realm.objects(DBStudy.self).filter("studyId == %@",study.studyId)
        let dbStudy = studies.last
        
        try? realm.write({
            
            dbStudy?.signedConsentFilePath = study.signedConsentFilePath
            dbStudy?.signedConsentVersion = study.signedConsentVersion
        })
    }
    
    /**
     updates local Notification status for the study.
    */
    class func updateLocalNotificaitonUpdated(studyId: String,status: Bool) {
        
        let realm = DBHandler.getRealmObject()!
        let study = realm.object(ofType: DBStudy.self, forPrimaryKey: studyId)
        try? realm.write({
            study?.activitiesLocalNotificationUpdated = status
        })
        
    }
    
     // MARK:Activity
    
    /**
     Saves Activities to DB
     @param activityies, is list or Activities
    */
    
    class func saveActivities(activityies: Array<Activity>) {
        
        let realm = DBHandler.getRealmObject()!
        let study = Study.currentStudy
        let dbActivityArray = realm.objects(DBActivity.self).filter({$0.studyId == study?.studyId})
        
        var dbActivities: Array<DBActivity> = []
        var activityUpdated = false
        for activity in activityies {
          
            var dbActivity: DBActivity?
            if dbActivityArray.count != 0 {
                dbActivity = dbActivityArray.filter({$0.actvityId == activity.actvityId!}).last
                
                if dbActivity == nil { //newly added activity
                    dbActivity = DBHandler.getDBActivity(activity: activity)
                    dbActivities.append(dbActivity!)
                    activityUpdated  = true
                }
                else {
                    
                    //check if version is updated
                    if dbActivity?.version != activity.version {
                        
                        try? realm.write({
                            realm.delete((dbActivity?.activityRuns)!)
                            realm.delete(dbActivity!)
                        })
                        
                        let updatedActivity = DBHandler.getDBActivity(activity: activity)
                        dbActivities.append(updatedActivity)
                        DBHandler.deleteMetaDataForActivity(activityId: activity.actvityId!, studyId: activity.studyId!)
                        activityUpdated = true
                        
                    }else {
                         try? realm.write({
                            
                            dbActivity?.currentRunId = activity.userParticipationStatus.activityRunId
                            dbActivity?.participationStatus = activity.userParticipationStatus.status.rawValue
                            dbActivity?.completedRuns = activity.userParticipationStatus.compeltedRuns
                            dbActivity?.state = activity.state
                         })
                    }
                }
                
            }else {
                dbActivity = DBHandler.getDBActivity(activity: activity)
                dbActivities.append(dbActivity!)
                activityUpdated = true
            }
        }
        
        //keys for alerts
        if activityUpdated {
            let ud = UserDefaults.standard
            let halfCompletionKey = "50pcShown"  + (Study.currentStudy?.studyId)!
            let fullCompletionKey = "100pcShown"  + (Study.currentStudy?.studyId)!
            ud.set(false, forKey: halfCompletionKey)
            ud.set(false, forKey: fullCompletionKey)
        }
        
  
        
        if dbActivities.count > 0 {
            try? realm.write({
                realm.add(dbActivities, update: true)
            })
        }
        Logger.sharedInstance.info("Activities Saved in DB")
    }
    
    /**
     fetches DBActivity from DB
     return an instance of DBActivity
    */
    private class func getDBActivity(activity: Activity)->DBActivity{
        
        let dbActivity = DBActivity()
        
        dbActivity.studyId = activity.studyId
        dbActivity.actvityId = activity.actvityId
        dbActivity.type = activity.type?.rawValue
        dbActivity.name = activity.name
        dbActivity.startDate = activity.startDate
        dbActivity.endDate = activity.endDate
        dbActivity.version = activity.version
        dbActivity.state = activity.state
        
        dbActivity.branching = activity.branching!
        dbActivity.frequencyType = activity.frequencyType.rawValue
        dbActivity.schedulingType = activity.schedulingType.rawValue
        dbActivity.currentRunId = activity.userParticipationStatus.activityRunId
        dbActivity.participationStatus = activity.userParticipationStatus.status.rawValue
        dbActivity.completedRuns = activity.userParticipationStatus.compeltedRuns
        dbActivity.id = activity.studyId! + activity.actvityId!
        dbActivity.taskSubType = activity.taskSubType
      
        do {
            let json = ["data": activity.frequencyRuns]
            let data =  try JSONSerialization.data(withJSONObject: json, options: JSONSerialization.WritingOptions.prettyPrinted)
            dbActivity.frequencyRunsData = data
            
        }catch {
        }
        
        do {
            let json = ["data": activity.anchorRuns]
            let data =  try JSONSerialization.data(withJSONObject: json, options: JSONSerialization.WritingOptions.prettyPrinted)
            dbActivity.anchorRunsData = data
            
        }catch {
        }
        
        
        
        //save overview
        let dbActivityRuns = List<DBActivityRun>()
        for activityRun in activity.activityRuns {
            
            let dbActivityRun = DBActivityRun()
            dbActivityRun.startDate = activityRun.startDate
            dbActivityRun.endDate = activityRun.endDate
            dbActivityRun.activityId = activity.actvityId
            dbActivityRun.studyId = activity.studyId
            dbActivityRun.runId = activityRun.runId
            dbActivityRun.isCompleted = activityRun.isCompleted
            dbActivityRuns.append(dbActivityRun)
        }
        
        //save anchor date
        dbActivity.sourceType = activity.anchorDate?.sourceType
        dbActivity.sourceActivityId = activity.anchorDate?.sourceActivityId
        dbActivity.sourceKey = activity.anchorDate?.sourceKey
        dbActivity.sourceFormKey = activity.anchorDate?.sourceFormKey
        dbActivity.startDays = activity.anchorDate?.startDays ?? 0
        dbActivity.startTime = activity.anchorDate?.startTime
        dbActivity.endDays = activity.anchorDate?.endDays ?? 0
        dbActivity.repeatInterval = activity.anchorDate?.repeatInterval ?? 0
        dbActivity.endTime = activity.anchorDate?.endTime
        
        dbActivity.activityRuns.append(objectsIn: dbActivityRuns)
        return dbActivity
    }
    
    /**
     updates Activity restortion data for the questionare
     @param activity, activity in which restortion data to be updated
     @param studyId, study which contains activity
     @param restortionData, restortion data for the questionare
    */
    class func updateActivityRestortionDataFor(activity: Activity,studyId: String,restortionData: Data?){
        
        let realm = DBHandler.getRealmObject()!
        let dbActivities = realm.objects(DBActivityRun.self).filter({$0.activityId == activity.actvityId && $0.studyId == studyId && $0.runId == activity.currentRun.runId})
        let dbActivity = dbActivities.last
        
        try? realm.write({
            dbActivity?.restortionData = restortionData
        })
    }
    
    class func updateTargetActivityAnchorDateDetail(studyId:String, activityId:String, response:[String:Any]) -> Bool {
        
        let realm = DBHandler.getRealmObject()!
        let dbActivities = realm.objects(DBActivity.self).filter({$0.sourceActivityId == activityId && $0.studyId == studyId && $0.startDate == nil})
        
        guard let dbActivity = dbActivities.last else {
            return false
        }
        
        //get source question value and key
        let results = response["results"] as! Array<Dictionary<String,Any>>
        var quesStepKey:String
        var dictionary:[String:Any] = [:]
        let sourceKey = (dbActivity.sourceKey)!
        if dbActivity.sourceFormKey != nil && dbActivity.sourceFormKey!.count > 0{
            quesStepKey = dbActivity.sourceFormKey!
            let quesResults = results.filter({$0["key"] as! String == quesStepKey}).first
            let resultsArray =  ((quesResults!["value"] as? [[Any]])?.first) as? [[String:Any]]
            dictionary = resultsArray!.filter({$0["key"] as! String == sourceKey}).first!
            
        }
        else {
            dictionary = results.filter({$0["key"] as! String == sourceKey}).first!
        }
        
        //let dictionary = results.filter({$0["key"] as! String == sourceKey}).first
        
        guard let userInputDate = dictionary["value"] as? String else{
            return false
        }
        
        
        let date = Utilities.getDateFromString(dateString: userInputDate)
        
        for activity in dbActivities {
            self.updateActivityLifeTimeFor(activity, anchorDate: date!)
        }
        
        DBHandler.updateResourceLifeTime(studyId, activityId: activityId, questionKey: sourceKey, anchorDateValue: date!)
        
        return true
    }
    
    class func updateActivityLifeTimeFor(_ dbActivity:DBActivity, anchorDate:Date) {
        
        var date = anchorDate
        let realm = DBHandler.getRealmObject()!
        
        //update start date
        var startDateStringEnrollment =  Utilities.formatterShort?.string(from: date)
        let startTimeEnrollment =  "00:00:00"
        startDateStringEnrollment = (startDateStringEnrollment ?? "") + " " + startTimeEnrollment
        date = Utilities.findDateFromString(dateString: startDateStringEnrollment ?? "")!
        
        let frequency = Frequency(rawValue: (dbActivity.frequencyType)!)!
        let lifeTime = DBHandler.getLifeTime(date,
                                             frequency:frequency,
                                             startDays: (dbActivity.startDays),
                                             endDays: (dbActivity.endDays),
                                             repeatInterval: (dbActivity.repeatInterval))
        
        
        //update start date
        var startDateString =  Utilities.formatterShort?.string(from: lifeTime.0!)
        let startTime =  (dbActivity.startTime == nil) ? "00:00:00" : (dbActivity.startTime)!
        startDateString = (startDateString ?? "") + " " + startTime
        let startdate = Utilities.findDateFromString(dateString: startDateString ?? "")
        
        //update end date
        var endDateString =  Utilities.formatterShort?.string(from: lifeTime.1!)
        let endTime =  (dbActivity.endTime == nil) ? "23:59:59" : (dbActivity.endTime)!
        endDateString = (endDateString ?? "") + " " + endTime
        let endDate = Utilities.findDateFromString(dateString: endDateString ?? "")
        
        if startdate != nil && endDate != nil {
            //calcuate runs for activity
            let currentDate = DBHandler.getCurrentDateWithTimeDifference()
            
            let activity = DBHandler.getActivityFromDBActivity(dbActivity, runDate: currentDate)
            activity.startDate = startdate
            activity.endDate = endDate
            activity.anchorDate?.anchorDateValue = date
            Schedule().getRunsForActivity(activity: activity, handler: { (runs) in
                //if runs.count > 0 {
                    activity.activityRuns = runs
                    
                    //save overview
                    let dbActivityRuns = List<DBActivityRun>()
                    for activityRun in activity.activityRuns {
                        
                        let dbActivityRun = DBActivityRun()
                        dbActivityRun.startDate = activityRun.startDate
                        dbActivityRun.endDate = activityRun.endDate
                        dbActivityRun.activityId = activity.actvityId
                        dbActivityRun.studyId = activity.studyId
                        dbActivityRun.runId = activityRun.runId
                        dbActivityRun.isCompleted = activityRun.isCompleted
                        dbActivityRuns.append(dbActivityRun)
                    }
                    
                    
                    try? realm.write({
                        dbActivity.activityRuns.append(objectsIn: dbActivityRuns)
                        dbActivity.startDate = startdate
                        dbActivity.endDate = endDate
                        dbActivity.anchorDateValue = date
                    })
                    
               // }
               
            })
            
        }
    }
    
    private class func getLifeTime(_ date:Date,
                           frequency:Frequency,
                           startDays:Int,
                           endDays:Int,
                           repeatInterval:Int) -> (Date?,Date?) {
        
        
        var startDate:Date!
        var endDate:Date!
   
        
        switch frequency {
        case .One_Time:
            
            let startDateInterval = TimeInterval(60*60*24*(startDays))
            let endDateInterval = TimeInterval(60*60*24*(endDays))
            
            startDate = date.addingTimeInterval(startDateInterval)
            endDate = date.addingTimeInterval(endDateInterval)
            
        case .Daily:
            
            let startDateInterval = TimeInterval(60*60*24*(startDays))
            let endDateInterval = TimeInterval(60*60*24*(repeatInterval))
            startDate = date.addingTimeInterval(startDateInterval)
            endDate = startDate.addingTimeInterval(endDateInterval)
            
        case .Weekly:
            
            let startDateInterval = TimeInterval(60*60*24*(startDays))
            let endDateInterval = TimeInterval(60*60*24*7*(repeatInterval))
            startDate = date.addingTimeInterval(startDateInterval)
            endDate = startDate.addingTimeInterval(endDateInterval)
        case .Monthly:
            
            let startDateInterval = TimeInterval(60*60*24*(startDays))
            startDate = date.addingTimeInterval(startDateInterval)
            
            //let endDateInterval = TimeInterval(-1)
            let calender = Calendar.current
            endDate = calender.date(byAdding: .month, value: (repeatInterval), to: startDate)
            //endDate = endDate.addingTimeInterval(endDateInterval)
        case .Scheduled:
            
            let startDateInterval = TimeInterval(60*60*24*(startDays))
            let endDateInterval = TimeInterval(60*60*24*(endDays))
            
            startDate = date.addingTimeInterval(startDateInterval)
            endDate = date.addingTimeInterval(endDateInterval)
            
        }
        
        return (startDate,endDate)
    }
    
    class func getActivitiesWithEmptyAnchorDateValue(_ studyId:String) -> [DBActivity] {
        let realm = DBHandler.getRealmObject()!
        let dbActivities:Array<DBActivity> = realm.objects(DBActivity.self).filter({$0.studyId == studyId && $0.anchorDateValue == nil && $0.sourceType == "ActivityResponse"})
        return dbActivities
    }
    
    /**
     updates activity meta data to DB
    */
    class func updateActivityMetaData(activity: Activity){
        
        let realm = DBHandler.getRealmObject()!
        let dbActivities = realm.objects(DBActivity.self).filter({$0.actvityId == activity.actvityId && $0.studyId == activity.studyId})
        let dbActivity = dbActivities.last
        
        try? realm.write({
            dbActivity?.shortName = activity.shortName
            
        })
        
    }
    
    /**
     loads activity list from DB for the study provided
     return completion handler with array of activities
    */
    class func loadActivityListFromDatabase(studyId: String,completionHandler: @escaping (Array<Activity>) -> ()){
        
        let realm = DBHandler.getRealmObject()!
        //let dbActivities = realm.objects(DBActivity.self).filter("studyId == %@",studyId)
        let dbActivities = realm.objects(DBActivity.self).filter({$0.studyId == studyId && $0.startDate != nil})
    
        let date = DBHandler.getCurrentDateWithTimeDifference()
        var activities: Array<Activity> = []
        for dbActivity in dbActivities {
            
            let activity = DBHandler.getActivityFromDBActivity(dbActivity,runDate: date)
            activities.append(activity)
        }
        completionHandler(activities)
    }
    
    private class func getCurrentDateWithTimeDifference() -> Date {
        
        var date = Date().utcDate()
        
        let difference = UserDefaults.standard.value(forKey: "offset") as? Int
        if difference != nil {
            date = date.addingTimeInterval(TimeInterval(difference!))
        }
        
        return date
    }
    
    private class func getActivityFromDBActivity(_ dbActivity:DBActivity, runDate date:Date) -> Activity {
        
        //create activity instance
        let activity = Activity()
        activity.actvityId  = dbActivity.actvityId
        activity.studyId    = dbActivity.studyId
        activity.name       = dbActivity.name
        activity.startDate  = dbActivity.startDate
        activity.endDate    = dbActivity.endDate
        activity.type       = ActivityType(rawValue: dbActivity.type!)
        activity.frequencyType = Frequency(rawValue: dbActivity.frequencyType!)!
        activity.schedulingType = ActivityScheduleType(rawValue: dbActivity.schedulingType!)!
        activity.totalRuns = dbActivity.activityRuns.count
        activity.version = dbActivity.version
        activity.branching = dbActivity.branching
        activity.state = dbActivity.state
        activity.taskSubType = dbActivity.taskSubType
        do {
            let frequencyRuns = try JSONSerialization.jsonObject(with: dbActivity.frequencyRunsData!, options: []) as! [String: Any]
            activity.frequencyRuns = frequencyRuns["data"] as! Array<Dictionary<String, Any>>?
            
        }catch {
        }
        
        do {
            let frequencyRuns = try JSONSerialization.jsonObject(with: dbActivity.anchorRunsData!, options: []) as! [String: Any]
            activity.anchorRuns = frequencyRuns["data"] as! Array<Dictionary<String, Any>>?
            
        }catch {
        }
        
        if activity.totalRuns != 0 {
            
            //create activity run
            var runs: Array<ActivityRun> = []
            for dbRun in dbActivity.activityRuns {
                let run = ActivityRun()
                run .activityId = dbRun.activityId
                run.complitionDate = dbRun.complitionDate
                run.startDate = dbRun.startDate
                run.endDate = dbRun.endDate
                run.runId = dbRun.runId
                run.studyId = dbRun.studyId
                run.isCompleted = dbRun.isCompleted
                run.restortionData = dbRun.restortionData
                runs.append(run)
            }
            activity.activityRuns = runs
            
            var runsBeforeToday: Array<ActivityRun>! = []
            var run: ActivityRun!
            
            if activity.frequencyType == Frequency.One_Time && activity.endDate == nil {
                run = runs.last
                
            }else {
                
                runsBeforeToday = runs.filter({$0.endDate <= date})
                run = runs.filter({$0.startDate <= date && $0.endDate > date}).first //current run
            }
            
            let completedRuns = runs.filter({$0.isCompleted == true})
            activity.compeltedRuns = completedRuns.count
            activity.currentRunId =  (run != nil) ? (run?.runId)!: runsBeforeToday.count
            activity.currentRun = run
            activity.compeltedRuns = dbActivity.completedRuns
        }
        let userStatus = UserActivityStatus()
        userStatus.activityId = dbActivity.actvityId
        userStatus.activityRunId = String(activity.currentRunId)
        userStatus.studyId = dbActivity.studyId
        
        if String(activity.currentRunId) == dbActivity.currentRunId {
            userStatus.status = UserActivityStatus.ActivityStatus(rawValue: dbActivity.participationStatus)!
        }
        
        userStatus.compeltedRuns = activity.compeltedRuns
        userStatus.incompletedRuns = activity.incompletedRuns
        userStatus.totalRuns = activity.totalRuns
        
        let incompleteRuns = activity.currentRunId - activity.compeltedRuns
        activity.incompletedRuns = ((incompleteRuns < 0) || activity.totalRuns == 0) ? 0 :incompleteRuns
        
        if activity.currentRun == nil {
            userStatus.status = UserActivityStatus.ActivityStatus.abandoned
            
        }else {
            
            if userStatus.status != UserActivityStatus.ActivityStatus.completed {
                
                var incompleteRuns = activity.currentRunId - activity.compeltedRuns
                incompleteRuns -= 1
                activity.incompletedRuns = (incompleteRuns < 0) ? 0 :incompleteRuns
            }
            
        }
        activity.userParticipationStatus = userStatus
        
        //append to user class participatesStudies also
        let activityStatus = User.currentUser.participatedActivites.filter({$0.activityId == activity.actvityId && $0.studyId == activity.studyId}).first
        let index = User.currentUser.participatedActivites.index(where: {$0.activityId == activity.actvityId && $0.studyId == activity.studyId })
        if activityStatus != nil {
            User.currentUser.participatedActivites[index!] = userStatus
            
        }else {
            User.currentUser.participatedActivites.append(userStatus)
        }
        
        
        //save anchor date
        let anchorDate = AnchorDate()
        anchorDate.sourceType = dbActivity.sourceType
        anchorDate.sourceActivityId = dbActivity.sourceActivityId
        anchorDate.sourceKey = dbActivity.sourceKey
        anchorDate.sourceFormKey = dbActivity.sourceFormKey
        anchorDate.startDays = dbActivity.startDays
        anchorDate.startTime = dbActivity.startTime
        anchorDate.endDays = dbActivity.endDays
        anchorDate.repeatInterval = dbActivity.repeatInterval
        anchorDate.endTime = dbActivity.endTime
        activity.anchorDate = anchorDate
        
        return activity
    }
    
    /**
     loads studyRuns and returns completion and adherence for the runs
    */
    class func loadAllStudyRuns(studyId: String,completionHandler: @escaping (_ completion: Int,_ adherence: Int) -> ()){
        
        let date = Date()
        let realm = DBHandler.getRealmObject()!
        let studyRuns = realm.objects(DBActivityRun.self).filter("studyId == %@",studyId)
        let completedRuns = studyRuns.filter({$0.isCompleted == true})
        let runsBeforeToday = studyRuns.filter({($0.endDate == nil) || ($0.endDate <= date)})
        var incompleteRuns = runsBeforeToday.count - completedRuns.count
        
        if incompleteRuns < 0 {
            incompleteRuns = 0
        }
        
        let completion = ceil( Double(self.divide(lhs: (completedRuns.count + incompleteRuns)*100, rhs: studyRuns.count)) )
        let adherence = ceil (Double(self.divide(lhs: (completedRuns.count)*100, rhs: (completedRuns.count + incompleteRuns))))
       
        completionHandler(Int(completion),Int(adherence))
        print("complete: \(completedRuns.count) , incomplete: \(incompleteRuns)")
        
    }
    static func divide(lhs: Int, rhs: Int) -> Int {
        if rhs == 0 {
            return 0
        }
        return lhs/rhs
    }
    
    /**
     saves ActivityRuns to DB
     @param activity, activity in which ActivityRun to be updated
     @param studyId, study which contains activity
     @param runs, contains list of ActvityRun
    */
    class func saveActivityRuns(activityId: String,studyId: String,runs: Array<ActivityRun>){
        
        let realm = DBHandler.getRealmObject()!
        let dbActivities = realm.objects(DBActivity.self).filter("studyId == %@ && actvityId == %@",studyId,activityId)
        let dbActivity = dbActivities.last
        
        //save overview
        let dbActivityRuns = List<DBActivityRun>()
        for sectionIndex in 0...(runs.count-1) {
            
            let activityRun = runs[sectionIndex]
            let dbActivityRun = DBActivityRun()
            dbActivityRun.startDate = activityRun.startDate
            dbActivityRun.endDate = activityRun.endDate
            dbActivityRun.activityId = activityId
            dbActivityRun.studyId = studyId
            dbActivityRun.runId = activityRun.runId
            dbActivityRun.isCompleted = activityRun.isCompleted
            
            dbActivityRuns.append(dbActivityRun)
        }
        
        try? realm.write({
            dbActivity?.activityRuns.append(objectsIn: dbActivityRuns)
        })
        
    }
    
    class func updateRunToComplete(runId:Int,activityId: String,studyId: String){
        
        let realm = DBHandler.getRealmObject()!
        let dbRuns = realm.objects(DBActivityRun.self).filter("studyId == %@ && activityId == %@ && runId == %d",studyId,activityId,runId)
        let dbRun = dbRuns.last
        
        try? realm.write({
           dbRun?.isCompleted = true
        })
        
    }
    
    class func updateActivityParticipationStatus(activity: Activity){
        
        let realm = DBHandler.getRealmObject()!
        let studies =  realm.objects(DBActivity.self).filter({$0.actvityId == activity.actvityId && $0.studyId == activity.studyId})
        let dbActivity = studies.last
        
        try? realm.write({
            
            dbActivity?.currentRunId = activity.userParticipationStatus.activityRunId
            dbActivity?.participationStatus = activity.userParticipationStatus.status.rawValue
            dbActivity?.completedRuns = activity.compeltedRuns
        })
    }
    
    /**
     saves response data for activity and sets the flag to be synched with server
    */
    class func saveResponseDataFor(activity: Activity,toBeSynced: Bool,data: Dictionary<String,Any>){
        
        let realm = DBHandler.getRealmObject()!
        let currentRun = activity.currentRun
        let dbRuns = realm.objects(DBActivityRun.self).filter({$0.studyId == currentRun?.studyId && $0.activityId == activity.actvityId && $0.runId == currentRun?.runId})
        let dbRun = dbRuns.last
        
        try? realm.write({
            
            dbRun?.toBeSynced = true
            do {
                let json = ["data": data]
                let jsonData =  try JSONSerialization.data(withJSONObject: json, options: JSONSerialization.WritingOptions.prettyPrinted)
                dbRun?.responseData = jsonData
            }catch {
            }
        })
    }
    
    /**
     saves the requestInformation for the missed requests due to network failure for offline support.
     @param params, request params
     @param headers, request Headers
     @param method, method type
     @param server, server string
    */
    class func saveRequestInformation(params: Dictionary<String,Any>?,headers: Dictionary<String,Any>?,method: String,server: String) {
        
        let realm = DBHandler.getRealmObject()!
        let dataSync = DBDataOfflineSync()
        
        if params != nil {
            do {
                let paramData =  try JSONSerialization.data(withJSONObject: params!, options: JSONSerialization.WritingOptions.prettyPrinted)
                dataSync.requestParams = paramData
            }catch {
            }
        }
        
        if headers != nil {
            do {
                let headerData =  try JSONSerialization.data(withJSONObject: headers!, options: JSONSerialization.WritingOptions.prettyPrinted)
                dataSync.headerParams = headerData
            }catch {
            }
        }
        dataSync.method = method
        dataSync.server = server
        dataSync.date = Date()
        
        try? realm.write({
            realm.add(dataSync)
        })
        
    }
    
    class func isDataAvailableToSync(completionHandler: @escaping (Bool) -> ()){
        
        let realm = DBHandler.getRealmObject()!
        let dbRuns = realm.objects(DBDataOfflineSync.self)
        
        if dbRuns.count > 0{
            completionHandler(true)
        }else {
            completionHandler(false)
        }
    }
    
    /**
      fetches completion and adherence for the Study Id from DB
     */
    class func getCompletion(studyId: String ,completionHandler: @escaping (_ completion: Int,_ adherence: Int) -> ()){
        
        let realm = DBHandler.getRealmObject()!
        let dbActivities = realm.objects(DBActivity.self).filter("studyId == %@",studyId)
        
        if dbActivities.count <= 0 {
            completionHandler(-1,-1)
            return
        }
        
        var date = Date().utcDate()
        let difference = UserDefaults.standard.value(forKey: "offset") as? Int
        if difference != nil {
            date = date.addingTimeInterval(TimeInterval(difference!))
        }
        
        
        var totalStudyRuns = 0
        var totalCompletedRuns = 0
        var totalIncompletedRuns = 0
        
        for dbActivity in dbActivities {
            
            let runs =  dbActivity.activityRuns
            var run: DBActivityRun!
            var runsBeforeToday: Array<DBActivityRun>! = []
            
            if dbActivity.endDate == nil {
                
                run = runs.last
            }else {
                
                runsBeforeToday = runs.filter({$0.endDate <= date})
                let firstRun: [DBActivityRun] = runs.filter({$0.startDate <= date && $0.endDate > date})
                run = firstRun.first
                //run = runs.filter({$0.startDate <= date && $0.endDate > date}).first //current run
            }
            
            let currentRunId =  (run != nil) ? (run?.runId)! : runsBeforeToday.count
          
            let completedRuns = dbActivity.completedRuns
            var incompleteRuns = currentRunId - completedRuns
            incompleteRuns = (incompleteRuns < 0) ? 0 : incompleteRuns
            
            var participationStatus: UserActivityStatus.ActivityStatus = .yetToJoin
            if String(currentRunId) == dbActivity.currentRunId {
                participationStatus = UserActivityStatus.ActivityStatus(rawValue: dbActivity.participationStatus)!
            }
            
            
            if participationStatus != UserActivityStatus.ActivityStatus.completed && run != nil {
                incompleteRuns = currentRunId - completedRuns
                incompleteRuns -= 1
                incompleteRuns = (incompleteRuns < 0) ? 0 :incompleteRuns
            }
            let totalRuns = runs.count
            
            //update values
            totalStudyRuns += totalRuns
            totalIncompletedRuns += incompleteRuns
            totalCompletedRuns += completedRuns
        }
        
        let completion = ceil( Double(self.divide(lhs: (totalCompletedRuns + totalIncompletedRuns)*100, rhs: totalStudyRuns)) )
        completionHandler(Int(completion),0)
        
    }
    
    // MARK:-  Activity MetaData
    
    /**
     saves activity meta data to DB
     */
    class func saveActivityMetaData(activity: Activity, data: Dictionary<String,Any>){
        
        let realm = DBHandler.getRealmObject()!
        let metaData = DBActivityMetaData()
        metaData.actvityId = activity.actvityId;
        metaData.studyId = activity.studyId;
        
        do {
            let json = data
            let data =  try JSONSerialization.data(withJSONObject: json, options: JSONSerialization.WritingOptions.prettyPrinted)
            metaData.metaData = data
        }catch {
        }
        
        try? realm.write({
            
            realm.add(metaData)
        })
    }
    
    /**
     fetches activityMeta data from DB, updates the activityBuilder instance and returns a bool
    */
    class func loadActivityMetaData(activity: Activity,completionHandler: @escaping (Bool) -> ()) {
        
        let realm = DBHandler.getRealmObject()!
        let dbMetaDataList = realm.objects(DBActivityMetaData.self).filter({$0.actvityId == activity.actvityId && $0.studyId == activity.studyId})
        
        if dbMetaDataList.count != 0 {
            let metaData = dbMetaDataList.last
            
            do {
                let response = try JSONSerialization.jsonObject(with: (metaData?.metaData)!, options: []) as! [String: Any]
                
                Study.currentActivity?.setActivityMetaData(activityDict: response[kActivity] as! Dictionary<String, Any>)
                
                if Utilities.isValidObject(someObject: Study.currentActivity?.steps as AnyObject?) {
                    
                    ActivityBuilder.currentActivityBuilder = ActivityBuilder()
                    ActivityBuilder.currentActivityBuilder.initWithActivity(activity: Study.currentActivity! )
                }
                completionHandler(true)
                
            }catch {
                completionHandler(false)
            }
        }else {
             completionHandler(false)
        }
    }
    
    /**
     deletes meta data for activity provided from DB
    */
    class func deleteMetaDataForActivity(activityId: String,studyId: String) {
        
        let realm = DBHandler.getRealmObject()!
        let dbMetaDataList = realm.objects(DBActivityMetaData.self).filter({$0.actvityId == activityId && $0.studyId == studyId})
        
        if dbMetaDataList.count != 0 {
            
            let metaData = dbMetaDataList.last
            try? realm.write({
                realm.delete(metaData!)
            })
        }
    }

    // MARK:- Dashboard - Statistics
    
    /**
     Saves the dashboard Stats to Database.
     @param statistics, array of statistics  which is to be updated
     @param studyId, study which contains activity
    */
    class func saveDashBoardStatistics(studyId: String,statistics: Array<DashboardStatistics>) {
        
        let realm = DBHandler.getRealmObject()!
        let dbStatisticsArray = realm.objects(DBStatistics.self).filter({$0.studyId == studyId})
        
        var dbStatisticsList: Array<DBStatistics> = []
        for stats in statistics {
            
            var dbStatistics: DBStatistics?
            if dbStatisticsArray.count != 0 {
                dbStatistics = dbStatisticsArray.filter({$0.activityId == stats.activityId!}).last
                
                if dbStatistics == nil {
                    
                    dbStatistics = DBHandler.getDBStatistics(stats: stats)
                    dbStatisticsList.append(dbStatistics!)
                }else {
                    
                    try? realm.write({
                        dbStatistics?.activityId = stats.activityId
                        dbStatistics?.activityVersion = stats.activityVersion
                        dbStatistics?.calculation = stats.calculation
                        dbStatistics?.dataSourceKey = stats.dataSourceKey
                        dbStatistics?.dataSourceType = stats.dataSourceType
                        dbStatistics?.displayName = stats.displayName
                        dbStatistics?.title = stats.title
                        dbStatistics?.statType = stats.statType
                        dbStatistics?.studyId = stats.studyId
                        dbStatistics?.unit = stats.unit
                    })
                }
            }else {
                
                dbStatistics = DBHandler.getDBStatistics(stats: stats)
                dbStatisticsList.append(dbStatistics!)
            }
        }
        
        if dbStatisticsList.count > 0 {
            try? realm.write({
                realm.add(dbStatisticsList, update: true)
                
            })
        }
    }
    
    /**
     creates an instance of DBStatistics from DashboardStatistics
    */
    private class func getDBStatistics(stats: DashboardStatistics)->DBStatistics{
        
        let dbStatistics = DBStatistics()
        dbStatistics.activityId = stats.activityId
        dbStatistics.activityVersion = stats.activityVersion
        dbStatistics.calculation = stats.calculation
        dbStatistics.dataSourceKey = stats.dataSourceKey
        dbStatistics.dataSourceType = stats.dataSourceType
        dbStatistics.displayName = stats.displayName
        dbStatistics.title = stats.title
        dbStatistics.statType = stats.statType
        dbStatistics.studyId = stats.studyId
        dbStatistics.unit = stats.unit
        dbStatistics.statisticsId = stats.studyId! + stats.title!
        
        return dbStatistics
        
    }
    
    /**
     fetches stats for the study provided and returns array of DashboardStatistics
    */
    class func loadStatisticsForStudy(studyId: String,completionHandler: @escaping (Array<DashboardStatistics>) -> ()) {
        
        let realm = DBHandler.getRealmObject()!
        let dbStatisticsList = realm.objects(DBStatistics.self).filter("studyId == %@",studyId)
        
        var statsList: Array<DashboardStatistics> = []
        for dbStatistics in dbStatisticsList {
            
            let stats = DashboardStatistics()
            stats.activityId =  dbStatistics.activityId
            stats.activityVersion  = dbStatistics.activityVersion
            stats.calculation = dbStatistics.calculation
            stats.dataSourceKey = dbStatistics.dataSourceKey
            stats.dataSourceType = dbStatistics.dataSourceType
            stats.displayName = dbStatistics.displayName
            stats.title = dbStatistics.title
            stats.statType = dbStatistics.statType
            stats.studyId = dbStatistics.studyId
            stats.unit = dbStatistics.unit
            stats.statList = dbStatistics.statisticsData
            
            statsList.append(stats)
        }
        completionHandler(statsList)
        
    }
    
    // MARK:- Dashboard - Charts
    
    /**
     saves Charts for the study in DB
    */
    class func saveDashBoardCharts(studyId: String,charts: Array<DashboardCharts>){
        
        let realm = DBHandler.getRealmObject()!
        let dbChartsArray = realm.objects(DBCharts.self).filter({$0.studyId == studyId})
        
        var dbChartsList: Array<DBCharts> = []
        for chart in charts {
            
            var dbChart: DBCharts?
            if dbChartsArray.count != 0 {
                dbChart = dbChartsArray.filter({$0.activityId == chart.activityId!}).last
                
                if dbChart == nil {
                    
                    dbChart = DBHandler.getDBChart(chart: chart)
                    dbChartsList.append(dbChart!)
                }else {
                    
                    try? realm.write({
                        
                        dbChart?.activityId = chart.activityId
                        dbChart?.activityVersion = chart.activityVersion
                        dbChart?.chartType = chart.chartType
                        dbChart?.chartSubType = chart.chartSubType
                        dbChart?.dataSourceTimeRange = chart.dataSourceTimeRange
                        dbChart?.dataSourceKey = chart.dataSourceKey
                        dbChart?.dataSourceType = chart.dataSourceType
                        dbChart?.displayName = chart.displayName
                        dbChart?.title = chart.title
                        dbChart?.scrollable = chart.scrollable
                        dbChart?.studyId = chart.studyId
                        
                    })
                    
                }
            }else {
                
                dbChart = DBHandler.getDBChart(chart: chart)
                dbChartsList.append(dbChart!)
            }
        }
        
        if dbChartsList.count > 0 {
            try? realm.write({
                realm.add(dbChartsList, update: true)
                
            })
        }
    }
    
    /**
     creates an instance of DBCharts from DashboardCharts
    */
    private class func getDBChart(chart: DashboardCharts)->DBCharts{
        
        let dbChart = DBCharts()
        dbChart.activityId = chart.activityId
        dbChart.activityVersion = chart.activityVersion
        dbChart.chartType = chart.chartType
        dbChart.chartSubType = chart.chartSubType
        dbChart.dataSourceTimeRange = chart.dataSourceTimeRange
        dbChart.dataSourceKey = chart.dataSourceKey
        dbChart.dataSourceType = chart.dataSourceType
        dbChart.displayName = chart.displayName
        dbChart.title = chart.title
        dbChart.scrollable = chart.scrollable
        
        dbChart.studyId = chart.studyId
      dbChart.chartId = chart.studyId! + (chart.activityId == nil ? "": chart.activityId! )  + chart.dataSourceKey!
      
        return dbChart
    }
    
    /**
     loads DashboardCharts for Study from DB
    */
    class func loadChartsForStudy(studyId: String,completionHandler: @escaping (Array<DashboardCharts>) -> ()){
        
        let realm = DBHandler.getRealmObject()!
        let dbChartList = realm.objects(DBCharts.self).filter("studyId == %@",studyId)
        
        var chartList: Array<DashboardCharts> = []
        for dbChart in dbChartList {
            
            let chart = DashboardCharts()
            chart.activityId =  dbChart.activityId
            chart.activityVersion  = dbChart.activityVersion
            chart.chartType = dbChart.chartType
            chart.chartSubType = dbChart.chartSubType
            chart.dataSourceTimeRange = dbChart.dataSourceTimeRange
            chart.dataSourceKey = dbChart.dataSourceKey
            chart.dataSourceType = dbChart.dataSourceType
            chart.displayName = dbChart.displayName
            chart.title = dbChart.title
            chart.scrollable = dbChart.scrollable
          
            chart.studyId = dbChart.studyId
            chart.statList = dbChart.statisticsData
            chartList.append(chart)
        }
        completionHandler(chartList)
    }
    
    /**
     saves Statistics data for actvity into DB
     @param activityId, activity for which stats to be saved
     @param key, for which the stats to be saved
     @param data, value to be stored
     @param fkDuration, exclusively to be used for fetal kick task
     @param date, date of submission of response
    */
    class func saveStatisticsDataFor(activityId: String,key: String,data: Float,fkDuration: Int,date: Date){
        
        let realm = DBHandler.getRealmObject()!
        let dbStatisticsList = realm.objects(DBStatistics.self).filter("activityId == %@ && dataSourceKey == %@",activityId,key)
        
        let dbChartsList = realm.objects(DBCharts.self).filter("activityId == %@ && dataSourceKey == %@",activityId,key)
        
        let dbStatistics = dbStatisticsList.last
        let dbChart = dbChartsList.last
        
        //save data
        let statData = DBStatisticsData()
        statData.startDate = date
        statData.data = data
        statData.fkDuration = fkDuration
      
        try? realm.write({
            if dbStatistics != nil  {
                dbStatistics?.statisticsData.append(statData)
            }
            if dbChart != nil {
                dbChart?.statisticsData.append(statData)
            }
        })
     }
    
    /**
     fetches data source keys needed to get DBstats from Server
    */
    class func getDataSourceKeyForActivity(studyId: String, completionHandler: @escaping (Array<Dictionary<String,String>>) -> ()) {
        
        let realm = DBHandler.getRealmObject()!
        let dbStatisticsList = realm.objects(DBStatistics.self).filter({$0.studyId == studyId})
        let dbChartsList = realm.objects(DBCharts.self).filter({$0.studyId == studyId})
        
        let statActivities: Array<String> = dbStatisticsList.map({$0.activityId!})
        let chartActivities: Array<String> = dbChartsList.map({$0.activityId!})
        
        let set1 = Set(statActivities)
        let set2 = Set(chartActivities)
        
        let allActivities = set1.union(set2)
        
        var activityAndQuestionKeys: Array<Dictionary<String,String>> = []
        
        for activityId in allActivities {
            
            let statList = dbStatisticsList.filter({$0.activityId == activityId})
            let chatList = dbChartsList.filter({$0.activityId == activityId})
            
            let statsKeys = statList.map({$0.dataSourceKey})
            let chartKeys = chatList.map({$0.dataSourceKey})
            
            var keys: Array<String> = []
            for key in statsKeys{
                if !keys.contains(key!){
                    keys.append(key!)
                }
            }
            
            for key in chartKeys{
                if !keys.contains(key!){
                    keys.append(key!)
                }
            }
            
            let keyString = keys.joined(separator: ",")
            let dict = ["activityId": activityId,
                        "keys": keyString] as Dictionary<String,String>
            
            activityAndQuestionKeys.append(dict)
            
        }
        return completionHandler(activityAndQuestionKeys)
    }
    
    // MARK:- RESOURCES
    
    /**
     Saves Resources for Study To DB
    */
    class func saveResourcesForStudy(studyId: String,resources: Array<Resource>){
        
        let realm = DBHandler.getRealmObject()!
        let dbResourcesArray = realm.objects(DBResources.self).filter({$0.studyId == studyId})
        
        var dbResourcesList: Array<DBResources> = []
        for resource in resources {
            
            var dbResource: DBResources?
            if dbResourcesArray.count != 0 {
                dbResource = dbResourcesArray.filter({$0.resourceId == resource.resourcesId}).last
                
                if dbResource == nil {
                    
                    dbResource = DBHandler.getDBResource(resource: resource)
                    dbResource?.studyId = studyId
                    dbResourcesList.append(dbResource!)
                }else {
                    
                    try? realm.write({
                      
                        dbResource?.title = resource.title
                       
                        dbResource?.audience = resource.audience?.rawValue
                        dbResource?.endDate = resource.endDate
                        dbResource?.startDate = resource.startDate
                        dbResource?.key = resource.key
                        dbResource?.povAvailable = resource.povAvailable
                        dbResource?.serverUrl = resource.file?.link
                        dbResource?.level = resource.level?.rawValue
                        dbResource?.notificationMessage = resource.notificationMessage
                        
                        if resource.povAvailable {
                            dbResource?.anchorDateEndDays = resource.anchorDateEndDays!
                            dbResource?.anchorDateStartDays = resource.anchorDateStartDays!
                        }
                    })
                }
            }else {
                
                dbResource = DBHandler.getDBResource(resource: resource)
                dbResource?.studyId = studyId
                dbResourcesList.append(dbResource!)
            }
        }
        
        let newlist = resources
        let dbResourceIds: Array<String> = dbResourcesArray.map({$0.resourceId!})
        let resourceIds: Array<String>  = newlist.map({$0.resourcesId!})
        let dbset: Set<String> = Set(dbResourceIds)
        let set: Set<String> = Set(resourceIds)
        
        let toBeDelete = dbset.subtracting(set)
        for aId in toBeDelete {
            let dbResource = dbResourcesArray.filter({$0.resourceId == aId}).last
            
            try? realm.write({
               
                realm.delete(dbResource!)
            })
     
        }

        if dbResourcesList.count > 0 {
            try? realm.write({
                realm.add(dbResourcesList, update: true)
                
            })
        }
    }
    
    private class func getDBResource(resource: Resource)->DBResources {
        
        let dbResource = DBResources()
        dbResource.resourceId = resource.resourcesId
        dbResource.title = resource.title
        dbResource.audience = resource.audience?.rawValue
        dbResource.endDate = resource.endDate
        dbResource.startDate = resource.startDate
        dbResource.key = resource.key
        dbResource.povAvailable = resource.povAvailable
        dbResource.serverUrl = resource.file?.link
        dbResource.level = resource.level?.rawValue
        dbResource.type = resource.type
        dbResource.notificationMessage = resource.notificationMessage
        
        if resource.povAvailable {
            dbResource.anchorDateEndDays = resource.anchorDateEndDays!
            dbResource.anchorDateStartDays = resource.anchorDateStartDays!
        }
        
        dbResource.availabilityType = resource.availabilityType.rawValue
        dbResource.sourceType = resource.sourceType?.rawValue
        dbResource.sourceActivityId = resource.sourceActivityId
        dbResource.sourceKey = resource.sourceKey
        dbResource.sourceFormKey = resource.sourceFormKey
        dbResource.startTime = resource.startTime
        dbResource.endTime = resource.endTime
        
        return dbResource
    }
    
    class func getResourceWithEmptyAnchorDateValue(_ studyId:String) -> [DBResources] {
        let realm = DBHandler.getRealmObject()!
        let dbResources:Array<DBResources> = realm.objects(DBResources.self).filter({$0.studyId == studyId && $0.startDate == nil && $0.sourceType == "ActivityResponse"})
        return dbResources
    }
    
    class func isActivitiesEmpty(_ studyId:String)->Bool {
        let realm = DBHandler.getRealmObject()!
        return realm.objects(DBActivity.self).filter({$0.studyId == studyId}).count == 0
    }
    class func isResourcesEmpty(_ studyId:String)->Bool {
         let realm = DBHandler.getRealmObject()!
         return realm.objects(DBResources.self).filter({$0.studyId == studyId}).count == 0
    }
    
    /**
     loads resources for study from DB
    */
    class func loadResourcesForStudy(studyId: String,completionHandler: @escaping (Array<Resource>) -> ()) {
        
        let realm = DBHandler.getRealmObject()!
        let dbResourceList = realm.objects(DBResources.self).filter({$0.studyId == studyId && ($0.povAvailable == false || $0.startDate != nil)})
        
        var resourceList: Array<Resource> = []
        for dbResource in dbResourceList {
            
            let resource = Resource()
            resource.resourcesId = dbResource.resourceId
            resource.title = dbResource.title
            resource.anchorDateEndDays = dbResource.anchorDateEndDays
            resource.anchorDateStartDays = dbResource.anchorDateStartDays
            resource.audience = Audience(rawValue: dbResource.audience!)
            resource.endDate  = dbResource.endDate
            resource.startDate = dbResource.startDate
            resource.key = dbResource.key
            resource.povAvailable = dbResource.povAvailable
            resource.notificationMessage = dbResource.notificationMessage
            resource.level = ResourceLevel(rawValue: dbResource.level!)
            
            resource.availabilityType = ResourceAvailabilityType(rawValue:dbResource.availabilityType!)!
            resource.sourceType = (dbResource.sourceType != nil) ? AnchorDateSourceType(rawValue:dbResource.sourceType!)! : nil
            resource.sourceActivityId = dbResource.sourceActivityId
            resource.sourceKey = dbResource.sourceKey
            resource.sourceFormKey = dbResource.sourceFormKey
            resource.startTime = dbResource.startTime
            resource.endTime = dbResource.endTime
            
            let file = File()
            file.link = dbResource.serverUrl
            file.localPath = dbResource.localPath
            file.mimeType = MimeType(rawValue: dbResource.type!)
            file.name = dbResource.title
            
            resource.file = file
            resourceList.append(resource)
        }
        completionHandler(resourceList)
    }
    
    class func getResourcesWithAnchorDateAvailable(studyId: String,completionHandler: @escaping (Array<DBResources>) -> ()){
        
        let realm = DBHandler.getRealmObject()!
        let dbResourceList: Array<DBResources> = realm.objects(DBResources.self).filter({$0.studyId == studyId && $0.povAvailable == true})
        completionHandler(dbResourceList)
    }
    
    class func resourceListFor(_ studyId:String, activityId:String?, questionKey:String?) -> Array<DBResources> {
        
        let realm = DBHandler.getRealmObject()!
        var dbResourceList: Array<DBResources> = realm.objects(DBResources.self).filter({$0.studyId == studyId && $0.povAvailable == true && $0.startDate == nil})
        
        if activityId != nil {
          dbResourceList = dbResourceList.filter({$0.sourceActivityId == activityId})
        }
        if questionKey != nil {
            dbResourceList = dbResourceList.filter({$0.sourceKey == questionKey})
        }
        
        return dbResourceList
    }
    
    @discardableResult
    class func  updateResourceLifeTime(_ studyId:String, activityId:String?, questionKey:String?, anchorDateValue:Date) -> (Bool) {
        
      
        let resourceList = DBHandler.resourceListFor(studyId,activityId: activityId, questionKey: questionKey)
        
        var startDateStringEnrollment =  Utilities.formatterShort?.string(from: anchorDateValue)
        let startTimeEnrollment =  "00:00:00"
        startDateStringEnrollment = (startDateStringEnrollment ?? "") + " " + startTimeEnrollment
        let anchorDate = Utilities.findDateFromString(dateString: startDateStringEnrollment ?? "")
        
        var resourceUpdatedStatus = false
        for resource in resourceList {
            
            resourceUpdatedStatus = true
            
            self.saveLifeTimeFor(resource: resource, anchorDate: anchorDate!)
          
        }
        return resourceUpdatedStatus
        
    }
    
    class func saveLifeTimeFor(resource:DBResources, anchorDate:Date) {
        
        let realm = DBHandler.getRealmObject()!
        let startDateInterval = TimeInterval(60*60*24*(resource.anchorDateStartDays)) //start of day
        let endDateInterval = TimeInterval(60*60*24*(resource.anchorDateEndDays+1) - 1) // end of day
        
        var startDate = anchorDate.addingTimeInterval(startDateInterval)
        var endDate = anchorDate.addingTimeInterval(endDateInterval)
        
        
        //update start date
        var startDateString =  Utilities.formatterShort?.string(from: startDate)
        let startTime =  (resource.startTime == nil) ? "00:00:00" : (resource.startTime)!
        startDateString = (startDateString ?? "") + " " + startTime
        startDate = Utilities.findDateFromString(dateString: startDateString ?? "")!
        
        //update end date
        var endDateString =  Utilities.formatterShort?.string(from: endDate)
        let endTime =  (resource.endTime == nil) ? "23:59:59" : (resource.endTime)!
        endDateString = (endDateString ?? "") + " " + endTime
        endDate = Utilities.findDateFromString(dateString: endDateString ?? "")!
        
        try? realm.write({
            resource.startDate = startDate
            resource.endDate = endDate
        })
    }
   // class func loadResourcesForStudy(studyId: String,completionHandler: @escaping (Array<Resource>) -> ())
    
    class func activitiesWithAnchorDateAvailable(studyId:String, completionHandler: @escaping (Bool) -> ()) {
       
        let realm = DBHandler.getRealmObject()!
        let dbActivities = realm.objects(DBActivity.self).filter({$0.studyId == studyId && $0.startDate != nil && $0.anchorDateValue != nil})
        
        var anchorDateAvailable = false
        for activity in dbActivities {
          anchorDateAvailable = DBHandler.updateResourceLifeTime(studyId, activityId: activity.sourceActivityId, questionKey: activity.sourceKey, anchorDateValue: activity.anchorDateValue!)
        }
        print("Completed")
        completionHandler(anchorDateAvailable)
    }
    
    class func updateResourceLocalPath(resourceId: String,path: String) {
        let realm = DBHandler.getRealmObject()!
        let dbResource = realm.objects(DBResources.self).filter("resourcesId == %@",resourceId).last!
        try? realm.write({
            dbResource.localPath = path
            
        })
    }
    
     // MARK:- NOTIFICATION
    /**
     saves notification to DB
    */
    func saveNotifications(notifications: Array<AppNotification>) {
        
        let realm = DBHandler.getRealmObject()!
        
        var dbNotificationList: Array<DBNotification> = []
        for notification in notifications {
            
            let dbNotification = DBNotification()
            dbNotification.id = notification.id!
            dbNotification.title = notification.title
            dbNotification.message = notification.message
            
            if notification.studyId != nil {
                dbNotification.studyId = notification.studyId!
                
            }else{
                dbNotification.studyId = ""
            }
            
            if notification.activityId != nil {
                dbNotification.activityId = notification.activityId!
                
            }else{
                dbNotification.activityId = ""
            }
            
            dbNotification.isRead = notification.read!
            dbNotification.notificationType = notification.type.rawValue
            dbNotification.subType = notification.subType.rawValue
            dbNotification.audience = notification.audience!.rawValue
            dbNotification.date = notification.date!
            dbNotificationList.append(dbNotification)
            
        }
        
        try? realm.write({
            realm.add(dbNotificationList, update: true)
            
        })
    }
    
    /**
     loads notification list from DB
    */
    class func loadNotificationListFromDatabase(completionHandler: @escaping (Array<AppNotification>) -> ()){
        
        let realm = DBHandler.getRealmObject()!
        let dbNotifications = realm.objects(DBNotification.self).sorted(byKeyPath: "date", ascending: false)
        
        var notificationList: Array<AppNotification> = []
        for dbnotification in dbNotifications {
            
            let notification = AppNotification()
            
            notification.id = dbnotification.id
            notification.title = dbnotification.title
            notification.message = dbnotification.message
            notification.studyId = dbnotification.studyId
            notification.activityId = dbnotification.activityId
            notification.type =    AppNotification.NotificationType(rawValue: dbnotification.notificationType!)!
            notification.subType = AppNotification.NotificationSubType(rawValue: dbnotification.subType!)!
            notification.audience = Audience(rawValue: dbnotification.audience!)!
            notification.date =  dbnotification.date
            notification.read = dbnotification.isRead
            notificationList.append(notification)
            
        }
        completionHandler(notificationList)
    }
    
    class func saveRegisteredLocaNotification(notificationList: Array<AppLocalNotification>) {
        
        for notification in notificationList {
            DBHandler.saveLocalNotification(notification: notification)
        }
    }
    
    /**
     Saves local Notification to DB
    */
    class func saveLocalNotification(notification: AppLocalNotification) {
        
        let realm = DBHandler.getRealmObject()!
        let dbNotification = DBLocalNotification()
        dbNotification.id = notification.id
        dbNotification.title = notification.title
        dbNotification.message = notification.message
        
        
        if notification.studyId != nil {
            dbNotification.studyId = notification.studyId
            
        }else{
            dbNotification.studyId = ""
        }
        
        if notification.activityId != nil {
            dbNotification.activityId = notification.activityId
            
        }else{
            dbNotification.activityId = ""
        }
        
        dbNotification.isRead = notification.read!
        dbNotification.notificationType = notification.type.rawValue
        dbNotification.subType = notification.subType.rawValue
        dbNotification.audience = notification.audience!.rawValue
        dbNotification.startDate = notification.startDate
        dbNotification.endDate = notification.endDate
        
        try? realm.write({
            realm.add(dbNotification, update: true)
            
        })
    }
    
    /**
     fetches list of local notifications from DB
     return array of localnotifications in completion handler
     */
    class func getLocalNotification(completionHandler: @escaping (Array<AppLocalNotification>) -> ()) {
        
        let realm = DBHandler.getRealmObject()!
        let todayDate = Date()
        let dbNotifications = realm.objects(DBLocalNotification.self).sorted(byKeyPath: "startDate", ascending: false).filter({$0.startDate! <= todayDate && $0.endDate! >= todayDate})
        
        var notificationList: Array<AppLocalNotification> = []
        for dbnotification in dbNotifications {
            
            let notification = AppLocalNotification()
            notification.id = dbnotification.id
            notification.title = dbnotification.title
            notification.message = dbnotification.message
            notification.studyId = dbnotification.studyId
            notification.activityId = dbnotification.activityId
            notification.type =    AppNotification.NotificationType(rawValue: dbnotification.notificationType!)!
            
            notification.subType = AppNotification.NotificationSubType(rawValue: dbnotification.subType!)!
            notification.audience = Audience(rawValue: dbnotification.audience!)!
            
            notification.read = dbnotification.isRead
            notification.startDate = dbnotification.startDate
            notification.endDate = dbnotification.endDate
            notificationList.append(notification)
        }
        
        completionHandler(notificationList)
    }
  
    /**
     fetches the sorted top most 50 local notification from DB
     return array of localnotification in completion handler
    */
  class func getRecentLocalNotification(completionHandler: @escaping (Array<AppLocalNotification>) -> ()){
    
    let realm = DBHandler.getRealmObject()!
    let todayDate = Date()
    let dbNotifications = realm.objects(DBLocalNotification.self).sorted(byKeyPath: "startDate", ascending: true).filter({$0.startDate! >= todayDate})
    
    var notificationList: Array<AppLocalNotification> = []
    

    for (i,dbnotification) in dbNotifications.enumerated() {
        
      if i == 50{
        break
      }
      //1test001dfsgNotification01PFJ3M
      let notification = AppLocalNotification()
      
      notification.id = dbnotification.id
      notification.title = dbnotification.title
      notification.message = dbnotification.message
      notification.studyId = dbnotification.studyId
      notification.activityId = dbnotification.activityId
      notification.type =    AppNotification.NotificationType(rawValue: dbnotification.notificationType!)!
      
      notification.subType = AppNotification.NotificationSubType(rawValue: dbnotification.subType!)!
      notification.audience = Audience(rawValue: dbnotification.audience!)!
      
      notification.read = dbnotification.isRead
      notification.startDate = dbnotification.startDate
      notification.endDate = dbnotification.endDate
      notificationList.append(notification)
      
    }
    completionHandler(notificationList)
  }
  
    class func isNotificationSetFor(notification: String,completionHandler: @escaping (Bool) -> ()){
        let realm = DBHandler.getRealmObject()!
       
        let dbNotifications = realm.object(ofType: DBLocalNotification.self, forPrimaryKey: notification)
        
        if dbNotifications == nil{
            completionHandler(false)
        }
        completionHandler(true)
        
    }

    // MARK:- DELETE
    class func deleteAll(){
        
        let realm = DBHandler.getRealmObject()!
        try? realm.write {
            realm.deleteAll()
        }
    }
    
    /**
     deletes StudyData
    */
    class func deleteStudyData(studyId: String){
        
        let realm = DBHandler.getRealmObject()!
        
        //delete activites and its metadata
        let dbActivities = realm.objects(DBActivity.self).filter("studyId == %@",studyId)
        for dbActivity in dbActivities{
            
            DBHandler.deleteMetaDataForActivity(activityId: (dbActivity.actvityId)!, studyId: (dbActivity.studyId)!)
            
            try? realm.write({
                realm.delete((dbActivity.activityRuns))
                realm.delete(dbActivity)
            })
        }
        
        //delete stats
        let dbStatisticsArray = realm.objects(DBStatistics.self).filter({$0.studyId == studyId})
        for stat in dbStatisticsArray{
            try? realm.write({
                realm.delete((stat.statisticsData))
                realm.delete(stat)
            })
        }
        
        //delete chart
        let dbChartsArray = realm.objects(DBCharts.self).filter({$0.studyId == studyId})
        for chart in dbChartsArray{
            try? realm.write({
                realm.delete((chart.statisticsData))
                realm.delete(chart)
            })
        }

        //delete resource
        let dbResourcesArray = realm.objects(DBResources.self).filter({$0.studyId == studyId})
        try? realm.write({
            realm.delete(dbResourcesArray)
        })
        
    }
  
  /**
     deletes DBlocalNotification
    */
  class func deleteDBLocalNotification(activityId: String,studyId: String){
    
    let realm = DBHandler.getRealmObject()!
    
    let dbNotifications = realm.objects(DBNotification.self).filter("activityId == %@ && studyId == %@",activityId,studyId)
    if dbNotifications.count > 0 {
      
      for dbNotification in dbNotifications{
        
      try? realm.write({
        realm.delete(dbNotification)
      })
        
      }
    }
  }

}
