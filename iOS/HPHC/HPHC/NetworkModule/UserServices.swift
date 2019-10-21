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

//common keys
let kAppVersion = "appVersion"
let kOSType = "os"

let kDeviceToken = "deviceToken"

// MARK: Registration Server API Constants
let kUserFirstName = "firstName"
let kUserLastName = "lastName"
let kUserEmailId = "emailId"
let kUserSettings = "settings"
let kUserId = "userId"


let kLocale = "locale"
let kParticipantInfo = "participantInfo"

let kUserProfile = "profile"
let kUserInfo = "info"
let kUserOS = "os"
let kUserAppVersion = "appVersion"
let kUserPassword = "password"
let kUserLogoutReason = "reason"
let kBasicInfo = "info"
let kStudyId = "studyId"
let kDeleteData = "deleteData"
let kUserVerified = "verified"
let kUserAuthToken = "auth"
let kStudies = "studies"
let kActivites = "activities"
let kActivityKey = "activity"
let kConsent = "consent"
let kEligibility = "eligibility"
let kUserEligibilityStatus = "eligbibilityStatus"
let kUserConsentStatus =  "consentStatus"
let kUserOldPassword = "currentPassword"
let kUserNewPassword = "newPassword"
let kUserIsTempPassword = "resetPassword"


let kPasscodeIsPending = "PASSCODESETUP"
let kShowNotification = "SHOWNOTIFICATION"



let kConsentpdf = "pdf"

// MARK: Settings Api Constants
let kSettingsRemoteNotifications = "remoteNotifications"
let kSettingsLocalNotifications = "localNotifications"
let kSettingsPassCode = "passcode"
let kSettingsTouchId = "touchId"
let kSettingsLeadTime = "reminderLeadTime"

let kSettingsLocale = "locale"

let kVerifyCode = "code"

//-------------------
let kDeactivateAccountDeleteData = "deleteData"

let kBookmarked = "bookmarked"
let kStatus = "status"
let kActivityId = "activityId"
let kActivityVersion = "activityVersion"
let kActivityRunId = "activityRunId"
let kCompletion = "completion"
let kAdherence = "adherence"

// MARK: Logout Api constants
let kLogoutReason = "reason"
let kLogoutReasonValue = "Logout"

// MARK: Refresh token constants

let kRefreshToken = "refreshToken"

struct FailedUserServices {
    
    var requestParams: [String: Any]? = [:]
    var headerParams: [String: String]? = [:]
    var method: Method!
}



class UserServices: NSObject {
    
    let networkManager = NetworkManager.sharedInstance()
    var delegate: NMWebServiceDelegate! = nil
    var requestParams: Dictionary<String,Any>? = [:]
    var headerParams: Dictionary<String,String>? = [:]
    var method: Method!
    var failedRequestServices = FailedUserServices()
    
    
    // MARK: Requests
    
    /*
    func checkForAppUpdates(delegate: NMWebServiceDelegate){
        
        self.delegate = delegate
        let method = RegistrationMethods.versionInfo.method
        self.sendRequestWith(method: method, params: nil, headers: nil)
    }*/
    
    
    func loginUser(_ delegate: NMWebServiceDelegate){
        
        self.delegate = delegate
        
        let user = User.currentUser
        let params = [kUserEmailId: user.emailId!,
                      kUserPassword: user.password!,
                      "appId":Utilities.getBundleIdentifier()]
        
        let method = RegistrationMethods.login.method
        
        self.sendRequestWith(method: method, params: params, headers: nil)
        
    }
    
    func registerUser(_ delegate: NMWebServiceDelegate){
        
        self.delegate = delegate
        
        let user = User.currentUser
        
        let params = [kUserEmailId: user.emailId!,
                      kUserPassword: user.password!,
                      "appId":Utilities.getBundleIdentifier()
                      ]
        
        let method = RegistrationMethods.register.method
        self.sendRequestWith(method: method, params: params, headers: nil)
        
    }
    
    func confirmUserRegistration(_ delegate: NMWebServiceDelegate){
        
        self.delegate = delegate
        
        let user = User.currentUser
        let headerParams = [kUserId: user.userId!]
        let method = RegistrationMethods.confirmRegistration.method
        self.sendRequestWith(method: method, params: nil, headers: headerParams)
        
    }
    
    
    func verifyEmail(emailId: String,verificationCode: String, delegate: NMWebServiceDelegate){
        
        self.delegate = delegate
        
        //let user = User.currentUser
        
        let param = [kVerifyCode: verificationCode,
                     kUserEmailId: emailId]
        let method = RegistrationMethods.verify.method
        self.sendRequestWith(method: method, params: param, headers: nil)
        
    }
    
    
    func resendEmailConfirmation(emailId: String, delegate: NMWebServiceDelegate){
        
        self.delegate = delegate
        let params = [kUserEmailId: emailId]
        let method = RegistrationMethods.resendConfirmation.method
        self.sendRequestWith(method: method, params: params, headers: nil)
        
    }
    
    
    func logoutUser(_ delegate: NMWebServiceDelegate){
        
        self.delegate = delegate
        
        let user = User.currentUser
        let headerParams = [kUserId: user.userId!]
        let params = [kUserLogoutReason: user.logoutReason.rawValue]
        
        let method = RegistrationMethods.logout.method
        self.sendRequestWith(method: method, params: params, headers: headerParams)
        
    }
    
    func deleteAccount(_ delegate: NMWebServiceDelegate)  {
        
        self.delegate = delegate
        
        let user = User.currentUser
        let headerParams = [kUserAuthToken: user.authToken] as Dictionary<String,String>
        let method = RegistrationMethods.deleteAccount.method
        self.sendRequestWith(method: method, params: nil, headers: headerParams)
    }
    
    func deActivateAccount(listOfStudyIds: Array<String> , delegate: NMWebServiceDelegate)  {
        
        self.delegate = delegate
        
        let user = User.currentUser
        let headerParams = [kUserAuthToken: user.authToken,
                            kUserId: user.userId!] as Dictionary<String,String>
        
        let params = [kDeactivateAccountDeleteData: listOfStudyIds]
        
        let method = RegistrationMethods.deactivate.method
        self.sendRequestWith(method: method, params: params, headers: headerParams)
    }
    
    
    func forgotPassword(email: String, delegate: NMWebServiceDelegate){
        
        self.delegate = delegate
        
       // let user = User.currentUser
        let params = [kUserEmailId: email]
        let method = RegistrationMethods.forgotPassword.method
        
        self.sendRequestWith(method: method, params: params, headers: nil )
    }
    
    func changePassword(oldPassword: String,newPassword: String,delegate: NMWebServiceDelegate){
        
        self.delegate = delegate
        
        let user = User.currentUser
        let headerParams = [kUserId: user.userId!]
        let params = [kUserOldPassword: oldPassword,
                      kUserNewPassword: newPassword]
        
        let method = RegistrationMethods.changePassword.method
        self.sendRequestWith(method: method, params: params, headers: headerParams)
    }
    
    func getUserProfile(_ delegate: NMWebServiceDelegate){
        
        self.delegate = delegate
        
        let user = User.currentUser
        
        let headerParams = [kUserId: user.userId!]
        let method = RegistrationMethods.userProfile.method
        self.sendRequestWith(method: method, params: nil, headers: headerParams)
    }
    
    func updateUserProfile(_ delegate: NMWebServiceDelegate){
        
        self.delegate = delegate
        
        let user = User.currentUser
        let headerParams = [kUserId: user.userId!]
        
        let settings = [kSettingsRemoteNotifications: (user.settings?.remoteNotifications)! as Bool,
                        kSettingsTouchId: (user.settings?.touchId)! as Bool,
                        kSettingsPassCode: (user.settings?.passcode)! as Bool,
                        kSettingsLocalNotifications: (user.settings?.localNotifications)! as Bool,
                        kSettingsLeadTime: (user.settings?.leadTime)! as String,
                        kSettingsLocale: (user.settings?.locale)! as String
            ] as [String: Any]
        
        let version = Utilities.getAppVersion()
        let token = Utilities.getBundleIdentifier()
        let info = [kAppVersion: version,
                    kOSType: "ios",
                    kDeviceToken: token
        ]
        
        let params = [
            kUserSettings: settings,
            kBasicInfo: info,
            kParticipantInfo: []] as [String: Any]
        
        let method = RegistrationMethods.updateUserProfile.method
        
        self.sendRequestWith(method: method, params: params, headers: headerParams)
    }
    
    func updateUserProfile(deviceToken: String, delegate: NMWebServiceDelegate){
        
        self.delegate = delegate
        
        let user = User.currentUser
        let headerParams = [kUserId: user.userId!]
        
//        let settings = [kSettingsRemoteNotifications: (user.settings?.remoteNotifications)! as Bool,
//                        kSettingsTouchId: (user.settings?.touchId)! as Bool,
//                        kSettingsPassCode: (user.settings?.passcode)! as Bool,
//                        kSettingsLocalNotifications: (user.settings?.localNotifications)! as Bool,
//                        kSettingsLeadTime: (user.settings?.leadTime)! as String,
//                        kSettingsLocale: (user.settings?.locale)! as String
//            ] as [String: Any]
        
        let version = Utilities.getAppVersion()
        let info = [kAppVersion: version,
                    kOSType: "ios",
                    kDeviceToken: deviceToken
        ]
        
        let params = [
            
            kBasicInfo: info,
            kParticipantInfo: []] as [String: Any]
        
        let method = RegistrationMethods.updateUserProfile.method
        
        self.sendRequestWith(method: method, params: params, headers: headerParams)
    }
    
    func getUserPreference(_ delegate: NMWebServiceDelegate){
        
        self.delegate = delegate
        
        let user = User.currentUser
        let headerParams = [kUserId: user.userId!,
                            kUserAuthToken: user.authToken] as Dictionary<String, String>
        
        let method = RegistrationMethods.userPreferences.method
        
        self.sendRequestWith(method: method, params: nil, headers: headerParams)
    }
    
    
    func getStudyStates(_ delegate: NMWebServiceDelegate){
        
        self.delegate = delegate
        
        let user = User.currentUser
        let headerParams = [kUserId: user.userId!] as Dictionary<String, String>
        let method = RegistrationMethods.studyState.method
        
        self.sendRequestWith(method: method, params: nil, headers: headerParams)
    }
    
    func updateCompletionAdherence(studyStauts: UserStudyStatus , delegate: NMWebServiceDelegate){
        
        self.delegate = delegate
        
        let user = User.currentUser
        let headerParams = [kUserId: user.userId!]
        
        let params = [kStudies: [studyStauts.getCompletionAdherence()]] as [String: Any]
        let method = RegistrationMethods.updateStudyState.method
        
        self.sendRequestWith(method: method, params: params, headers: headerParams)
    }
    
    func updateStudyBookmarkStatus(studyStauts: UserStudyStatus , delegate: NMWebServiceDelegate){
        self.delegate = delegate
        
        let user = User.currentUser
        let headerParams = [kUserId: user.userId!]
        
        let params = [kStudies: [studyStauts.getBookmarkUserStudyStatus()]] as [String: Any]
        let method = RegistrationMethods.updateStudyState.method
        
        self.sendRequestWith(method: method, params: params, headers: headerParams)
    }
    
    func updateActivityBookmarkStatus(activityStauts: UserActivityStatus , delegate: NMWebServiceDelegate){
        self.delegate = delegate
        
        let user = User.currentUser
        let headerParams = [kUserId: user.userId] as Dictionary<String, String>
        
        let params = [kActivites: [activityStauts.getBookmarkUserActivityStatus()]] as [String : Any]
        let method = RegistrationMethods.updateActivityState.method
        
        self.sendRequestWith(method: method, params: params, headers: headerParams)
    }
    
    func updateUserParticipatedStatus(studyStauts: UserStudyStatus, delegate: NMWebServiceDelegate){
        
        self.delegate = delegate
        
        let user = User.currentUser
        let headerParams = [kUserId: user.userId] as Dictionary<String, String>
        let params = [kStudies: [studyStauts.getParticipatedUserStudyStatus()]] as [String: Any]
        let method = RegistrationMethods.updateStudyState.method
        
        self.sendRequestWith(method: method, params: params, headers: headerParams)
    }
    
    func updateUserActivityParticipatedStatus(studyId: String, activityStatus: UserActivityStatus, delegate: NMWebServiceDelegate){
        
        self.delegate = delegate
        
        let user = User.currentUser
        let headerParams = [kUserId: user.userId] as Dictionary<String, String>
        let params = [kStudyId: studyId,
                      kActivity: [activityStatus.getParticipatedUserActivityStatus()]] as [String: Any]
        let method = RegistrationMethods.updateActivityState.method
        self.sendRequestWith(method: method, params: params, headers: headerParams)
    }
    
    func updateUserEligibilityConsentStatus(eligibilityStatus: Bool,consentStatus: ConsentStatus, delegate: NMWebServiceDelegate){
        
        
        self.delegate = delegate
        
       
        let user = User.currentUser
        let headerParams = [kUserId: user.userId! as String,
                            kUserAuthToken: user.authToken! as String]
        
        let consentVersion: String?
        if (ConsentBuilder.currentConsent?.version?.count)! > 0 {
            consentVersion = ConsentBuilder.currentConsent?.version!
        } else {
            consentVersion = "1"
        }
        
        let base64data = ConsentBuilder.currentConsent?.consentResult?.consentPdfData!.base64EncodedString()
        
        let consent = [ kConsentDocumentVersion: consentVersion! as String,
                        kStatus: consentStatus.rawValue,
                        kConsentpdf: "\(base64data!)" as Any] as [String: Any]
        
        
        let params = [kStudyId: (Study.currentStudy?.studyId!)! as String,
                      kEligibility: eligibilityStatus,
                      kConsent: consent,
                      kConsentSharing: ""] as [String : Any]
        let method = RegistrationMethods.updateEligibilityConsentStatus.method
        
        //print(" doc == \(ConsentBuilder.currentConsent?.consentResult?.consentPdfData)")
        self.sendRequestWith(method: method, params: params, headers: headerParams)
    }
    
    func getConsentPDFForStudy(studyId: String , delegate: NMWebServiceDelegate) {
        
        self.delegate = delegate
        
        let user = User.currentUser
        let params = [kStudyId: studyId,
                      "consentVersion": ""]
        
        let headerParams = [kUserId: user.userId!]
        let method = RegistrationMethods.consentPDF.method
        
        self.sendRequestWith(method: method, params: params, headers: headerParams)
    }
    
    func updateUserActivityState(_ delegate: NMWebServiceDelegate) {
        
        self.delegate = delegate
        
        //INCOMPLETE
        //let method = RegistrationMethods.updateActivityState.method
    }
    
    func getUserActivityState(studyId: String , delegate: NMWebServiceDelegate) {
        
        self.delegate = delegate
        
        let user = User.currentUser
        let params = [kStudyId: studyId]
        let headerParams = [kUserId: user.userId!]
        let method = RegistrationMethods.activityState.method
        self.sendRequestWith(method: method, params: params, headers: headerParams)
    }
    
    func withdrawFromStudy(studyId: String ,shouldDeleteData: Bool, delegate: NMWebServiceDelegate){
        
        
        self.delegate = delegate
        let user = User.currentUser
        let headerParams = [kUserId: user.userId! as String]
        
        let params = [
            kStudyId: studyId,
            kDeleteData: shouldDeleteData] as [String: Any]
        
        let method = RegistrationMethods.withdraw.method
        
        self.sendRequestWith(method: method, params: params, headers: headerParams)
    }
    
    func updateToken(){
        
        let user = User.currentUser
        
        let param = [kRefreshToken: user.refreshToken!]
        let method = RegistrationMethods.refreshToken.method
        self.sendRequestWith(method: method, params: param, headers: nil)
        
    }
    
    func syncOfflineSavedData(method: Method, params: Dictionary<String, Any>?,headers: Dictionary<String, String>? , delegate: NMWebServiceDelegate){
        
        self.delegate = delegate
        self.sendRequestWith(method: method, params: params, headers: headers)
    }
    
    
    // MARK:Parsers
    func handleUserLoginResponse(response: Dictionary<String, Any>){
        
        let user = User.currentUser
        user.userId     = (response[kUserId] as? String)!
        user.verified   = (response[kUserVerified] as? Bool)!
        user.authToken  = (response[kUserAuthToken] as? String)!
        if let refreshToken = response[kRefreshToken] as? String {
            user.refreshToken = refreshToken
            
        }
        
        if let isTempPassword = response[kUserIsTempPassword] as? Bool {
            user.isLoginWithTempPassword = isTempPassword
        }
        
        if user.verified! && !user.isLoginWithTempPassword {
            
            //set user type & save current user to DB
            user.userType = UserType.FDAUser
            DBHandler().saveCurrentUser(user: user)
            
            //Updating Key & Vector
            let appDelegate = (UIApplication.shared.delegate as? AppDelegate)!
            appDelegate.updateKeyAndInitializationVector()
            
            
            //TEMP : Need to save these values in Realm
            let ud = UserDefaults.standard
            ud.set(user.authToken, forKey: kUserAuthToken)
            ud.set(user.userId!, forKey: kUserId)
            ud.set(true, forKey: kPasscodeIsPending)
            ud.synchronize()
            
            StudyFilterHandler.instance.previousAppliedFilters = []
            
        }
        
    }
    
    func handleUserRegistrationResponse(response: Dictionary<String, Any>){
        
        let user = User.currentUser
        user.userId     = (response[kUserId] as? String)!
        user.verified   = (response[kUserVerified] as? Bool)!
        user.authToken  = (response[kUserAuthToken] as? String)!
        
        user.refreshToken = (response[kRefreshToken] as? String)!
        StudyFilterHandler.instance.previousAppliedFilters = []
        
    }
    
    func handleConfirmRegistrationResponse(response: Dictionary<String, Any>){
        
        let user = User.currentUser
        if let varified = response[kUserVerified] as? Bool {
            
            user.verified = varified
            if user.verified! {
                
                user.userType = UserType.FDAUser
                
                //TEMP : Need to save these values in Realm
                let ud = UserDefaults.standard
                ud.set(user.authToken, forKey: kUserAuthToken)
                ud.set(user.userId!, forKey: kUserId)
                ud.synchronize()
                //Save Current User to DB
                DBHandler().saveCurrentUser(user: user)
                StudyFilterHandler.instance.previousAppliedFilters = []
            }
        }
    }
    
    func handleEmailVerifyResponse(response: Dictionary<String, Any>){
        
        let user = User.currentUser
        user.verified = true
        
        if user.verified! {
            
            if user.authToken != nil {
                
                user.userType = UserType.FDAUser
                
                //TEMP : Need to save these values in Realm
                let ud = UserDefaults.standard
                ud.set(user.authToken, forKey: kUserAuthToken)
                ud.set(user.userId!, forKey: kUserId)
                ud.set(true, forKey: kPasscodeIsPending)
                ud.synchronize()
                
                DBHandler().saveCurrentUser(user: user)
            }
        }
    }
    
    func handleGetUserProfileResponse(response: Dictionary<String, Any>) {
        
        let user = User.currentUser
        
        //settings
        let settings = (response[kUserSettings] as? Dictionary<String, Any>)!
        let userSettings = Settings()
        userSettings.setSettings(dict: settings as NSDictionary)
        user.settings = userSettings
        
        DBHandler.saveUserSettingsToDatabase()
        
        //profile
        let profile = (response[kUserProfile] as? Dictionary<String, Any>)!
        user.emailId = profile[kUserEmailId] as? String
        user.firstName = profile[kUserFirstName] as? String
        user.lastName = profile[kUserLastName] as? String
    }
    
    func handleUpdateUserProfileResponse(response: Dictionary<String, Any>) {
    }
    
    func handleResendEmailConfirmationResponse(response: Dictionary<String, Any>) {
    }
    
    
    func handleChangePasswordResponse(response: Dictionary<String, Any>) {
        
        let user = User.currentUser
        if user.verified! {
            
            user.userType = UserType.FDAUser
            
            DBHandler().saveCurrentUser(user: user)
            
            //TEMP : Need to save these values in Realm
            let ud = UserDefaults.standard
            ud.set(user.authToken, forKey: kUserAuthToken)
            ud.set(user.userId!, forKey: kUserId)
            ud.synchronize()
        }
        
    }
    
    
    func handleGetPreferenceResponse(response: Dictionary<String, Any>){
        
        let user = User.currentUser

        //studies
        if let studies = response[kStudies] as? Array<Dictionary<String, Any>> {
            
            for study in studies {
                let participatedStudy = UserStudyStatus(detail: study)
                user.participatedStudies.append(participatedStudy)
            }
        }
        
        //activities
//        if let activites = response[kActivites]  as? Array<Dictionary<String, Any>> {
//            for _ in activites {
//                // let participatedActivity = UserActivityStatus(detail: activity)
//                // user.participatedActivites.append(participatedActivity)
//            }
//        }
    }
    
    func handleGetStudyStatesResponse(response: Dictionary<String, Any>){
        let user = User.currentUser
        //studies
        user.participatedStudies.removeAll()
        if let studies = response[kStudies] as? Array<Dictionary<String, Any>> {
            
            for study in studies {
                let participatedStudy = UserStudyStatus(detail: study)
                user.participatedStudies.append(participatedStudy)
            }
        }
    }
    func handleGetActivityStatesResponse(response: Dictionary<String, Any>){
        let user = User.currentUser
        //activities
        if let activites = response[kActivites]  as? Array<Dictionary<String, Any>> {
            if Study.currentStudy != nil {
                for activity in activites {
                    let participatedActivity = UserActivityStatus(detail: activity,studyId:(Study.currentStudy?.studyId)!)
                    user.participatedActivites.append(participatedActivity)
                }
            }
        }
    }
    func handleUpdateEligibilityConsentStatusResponse(response: Dictionary<String, Any>){
        
    }
    
    func handleGetConsentPDFResponse(response: Dictionary<String, Any>){
        
        //let user = User.currentUser
        if Utilities.isValidValue(someObject: response[kConsent] as AnyObject?) {
            //Do nothing
        }
    }
    
    func handleUpdateActivityStateResponse(response: Dictionary<String, Any>){
        
    }
    
    func handleGetActivityStateResponse(response: Dictionary<String, Any>){
        
        //let user = User.currentUser
        
        //activities
        _ = (response[kActivites]  as? Array<Dictionary<String, Any>>)!
//        for activity in activites {
//
//            //let participatedActivity = UserActivityStatus(detail: activity)
//            // user.participatedActivites.append(participatedActivity)
//        }
    }
    
    func handleWithdrawFromStudyResponse(response: Dictionary<String, Any>){
    }
    
    func handleLogoutResponse(response: Dictionary<String, Any>)  {
        
        //TEMP
        let ud = UserDefaults.standard
        ud.removeObject(forKey: kUserAuthToken)
        ud.removeObject(forKey: kUserId)
        ud.synchronize()
        
        let appDomain = Bundle.main.bundleIdentifier!
        UserDefaults.standard.removePersistentDomain(forName: appDomain)
        UserDefaults.standard.synchronize()
        
        //Delete from database
        DBHandler.deleteCurrentUser()
        
        //reset user object
        User.resetCurrentUser()
        
        //delete complete database
        DBHandler.deleteAll()
        
        //cancel all local notification
        LocalNotification.cancelAllLocalNotification()
        
        //reset Filters
        StudyFilterHandler.instance.previousAppliedFilters = []
        StudyFilterHandler.instance.searchText = ""
        
    }
    
    func handleDeleteAccountResponse(response: Dictionary<String, Any>) {
        let ud = UserDefaults.standard
        ud.removeObject(forKey: kUserAuthToken)
        ud.removeObject(forKey: kUserId)
        ud.synchronize()
        
        let appDomain = Bundle.main.bundleIdentifier!
        UserDefaults.standard.removePersistentDomain(forName: appDomain)
        UserDefaults.standard.synchronize()
        
        //Delete from database
        DBHandler.deleteCurrentUser()
        
        //reset user object
        User.resetCurrentUser()
        
        //delete complete database
        DBHandler.deleteAll()
        
        //cancel all local notification
        LocalNotification.cancelAllLocalNotification()
        
        //reset Filters
        StudyFilterHandler.instance.previousAppliedFilters = []
        StudyFilterHandler.instance.searchText = ""
    }
    
    func handleDeActivateAccountResponse(response: Dictionary<String, Any>) {
        let ud = UserDefaults.standard
        ud.removeObject(forKey: kUserAuthToken)
        ud.removeObject(forKey: kUserId)
        ud.synchronize()
        
        let appDomain = Bundle.main.bundleIdentifier!
        UserDefaults.standard.removePersistentDomain(forName: appDomain)
        UserDefaults.standard.synchronize()
        
        //Delete from database
        DBHandler.deleteCurrentUser()
        
        //reset user object
        User.resetCurrentUser()
        
        //delete complete database
        DBHandler.deleteAll()
        
        //cancel all local notification
        LocalNotification.cancelAllLocalNotification()
        
        //reset Filters
        StudyFilterHandler.instance.previousAppliedFilters = []
        StudyFilterHandler.instance.searchText = ""
    }
    
    func handleUpdateTokenResponse(response: Dictionary<String, Any>){
        
        let user = User.currentUser
        user.authToken  = (response[kUserAuthToken] as? String)!
        //user.refreshToken = response[kRefreshToken] as! String
        //self.failedRequestServices.headerParams![kUserAuthToken] = user.accessToken
        
        DBHandler().saveCurrentUser(user: user)
        //re-send request which failed due to session expired
        
       // let requestParams = self.failedRequestServices.requestParams == nil ? nil : self.failedRequestServices.requestParams
        
        let headerParams = self.failedRequestServices.headerParams == nil ? [:] : self.failedRequestServices.headerParams
//
        self.sendRequestWith(method: self.failedRequestServices.method, params: (self.requestParams == nil ?  nil : self.requestParams) , headers: headerParams)
        
    }
    
    
    private func sendRequestWith(method: Method, params: Dictionary<String, Any>?,headers: Dictionary<String, String>?){
        
        self.requestParams = params
        self.headerParams = headers
        self.method = method
        networkManager.composeRequest(RegistrationServerConfiguration.configuration,
                                      method: method,
                                      params: params as NSDictionary?,
                                      headers: headers as NSDictionary?,
                                      delegate: self)
    }
    
}
extension UserServices: NMWebServiceDelegate{
    func startedRequest(_ manager: NetworkManager, requestName: NSString) {
        //  Logger.sharedInstance.info("RUS Request Called: \(requestName)")
        if delegate != nil {
            delegate.startedRequest(manager, requestName: requestName)
        }
    }
    func finishedRequest(_ manager: NetworkManager, requestName: NSString, response: AnyObject?) {
        Logger.sharedInstance.info("RUS Received Data: \(requestName), \(String(describing: response))")
        switch requestName {
        case RegistrationMethods.login.description as String:
            
            self.handleUserLoginResponse(response: (response as? Dictionary<String, Any>)!)
            
        case RegistrationMethods.register.description as String:
            
            self.handleUserRegistrationResponse(response: (response as? Dictionary<String, Any>)!)
            
        case RegistrationMethods.confirmRegistration.description as String:
            
            self.handleConfirmRegistrationResponse(response: (response as? Dictionary<String, Any>)!)
            
        case RegistrationMethods.verify.description as String:
            
            self.handleEmailVerifyResponse(response: (response as? Dictionary<String, Any>)!)
            
        case RegistrationMethods.userProfile.description as String:
            
            self.handleGetUserProfileResponse(response: (response as? Dictionary<String, Any>)!)
            
        case RegistrationMethods.updateUserProfile.description as String:
            
            self.handleUpdateUserProfileResponse(response: (response as? Dictionary<String, Any>)!)
            
        case RegistrationMethods.userPreferences.description as String:
            
            
            self.handleGetPreferenceResponse(response: (response as? Dictionary<String, Any>)!)
        case RegistrationMethods.changePassword.description as String:
            
            self.handleChangePasswordResponse(response: (response as? Dictionary<String, Any>)!)
            
        case RegistrationMethods.updatePreferences.description as String: break //did not handled response
            
        case RegistrationMethods.updateEligibilityConsentStatus.description as String: break
        case RegistrationMethods.consentPDF.description as String: break
        case RegistrationMethods.studyState.description as String:
            self.handleGetStudyStatesResponse(response:  (response as? Dictionary<String, Any>)!)
        case RegistrationMethods.updateStudyState.description as String: break
        case RegistrationMethods.updateActivityState.description as String: break
        case RegistrationMethods.activityState.description as String:
            self.handleGetActivityStatesResponse(response:  (response as? Dictionary<String, Any>)!)
        case RegistrationMethods.withdraw.description as String: break
        case RegistrationMethods.forgotPassword.description as String: break
            
        case RegistrationMethods.logout.description as String:
            self.handleLogoutResponse(response: (response as? Dictionary<String, Any>)!)
            
        case RegistrationMethods.deleteAccount.description as String:
            self.handleDeleteAccountResponse(response: (response as? Dictionary<String, Any>)!)
            
        case RegistrationMethods.deactivate.description as String:
            self.handleDeActivateAccountResponse(response: (response as? Dictionary<String, Any>)!)
        case RegistrationMethods.refreshToken.description as String:
            self.handleUpdateTokenResponse(response: (response as? Dictionary<String, Any>)!)
        default : break
        }
        
        if delegate != nil {
            delegate.finishedRequest(manager, requestName: requestName, response: response)
        }
    }
    
    
    func failedRequest(_ manager: NetworkManager, requestName: NSString, error: NSError) {
        
        if error.code == 401 {
            
            self.failedRequestServices.headerParams = self.headerParams
            self.failedRequestServices.requestParams = self.requestParams
            self.failedRequestServices.method = self.method
            
            print("Failed: Refresh token Expired")
            
            if User.currentUser.refreshToken == "" && requestName as String != RegistrationMethods.login.description {
                //Unauthorized Access
                
                //error.localizedDescription = "Your Session is Expired"
                
                let errorInfo = ["NSLocalizedDescription": "Your Session is Expired"]
                
                let localError  = NSError.init(domain: error.domain, code: 403, userInfo: errorInfo)
                
                if delegate != nil {
                    delegate.failedRequest(manager, requestName: requestName, error: localError)
                }
                
            } else {
                //Update Refresh Token
               
                    self.updateToken()
                
                
            }
            
        } else {
            
            if delegate != nil {
                
                var errorInfo = error.userInfo
                var localError = error
                if error.code == 403{
                    errorInfo = ["NSLocalizedDescription": "Your Session is Expired"]
                    localError  = NSError.init(domain: error.domain, code: 403, userInfo: errorInfo)
                }
                
                delegate.failedRequest(manager, requestName: requestName, error: localError)
            }
            
            //handle failed request due to network connectivity
            if requestName as String == RegistrationMethods.updateStudyState.description ||
                requestName as String == RegistrationMethods.updateActivityState.description {
                
                if (error.code == NoNetworkErrorCode) {
                    //save in database
                    print("save in database")
                    DBHandler.saveRequestInformation(params: self.requestParams, headers: self.headerParams, method: requestName as String, server: "registration")
                }
            }
        }
    }
}
