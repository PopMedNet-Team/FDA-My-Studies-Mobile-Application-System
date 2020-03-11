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
import IQKeyboardManagerSwift
import LocalAuthentication
import SlideMenuControllerSwift

let kProfileTableViewCellIdentifier = "ProfileTableViewCell"

let kLeadTimeSelectText = "Select Lead Time"
let kActionSheetDoneButtonTitle = "Done"
let kActionSheetCancelButtonTitle = "Cancel"

let kChangePasswordSegueIdentifier = "changePasswordSegue"
let kErrorTitle = ""
let kProfileAlertTitleText = "Profile"
let kProfileAlertUpdatedText = "Profile updated Successfully."

let signupCellLastIndex = 2

let kProfileTitleText = "My Account"

let kSignOutText = "Sign Out"
let kLabelName = "LabelName"

let kUseTouchIdOrPasscode = "Use Passcode or Touch ID to access app"
let kUseFaceIdOrPasscode = "Use Passcode or Face ID to access app"

let kUsePasscodeToAccessApp = "Use Passcode to access app"

let ktouchid = "touchIdEnabled"
let korkPasscode = "ORKPasscode"


// Cell Toggle Switch Types
enum ToggelSwitchTags: Int{
    case usePasscode = 3
    case useTouchId = 6
    case receivePush = 4
    case receiveStudyActivityReminders = 5
}

class ProfileViewController: UIViewController, SlideMenuControllerDelegate {
    
    var tableViewRowDetails: NSMutableArray?
    var datePickerView: UIDatePicker?
    var isCellEditable: Bool?
    var user = User.currentUser
    var isPasscodeViewPresented: Bool = false
    
    var passcodeStateIsEditing: Bool = false
    
    var isProfileEdited = false
    
    @IBOutlet var tableViewProfile: UITableView?
    @IBOutlet var tableViewFooterViewProfile: UIView?
    @IBOutlet var buttonLeadTime: UIButton?
    @IBOutlet var editBarButtonItem: UIBarButtonItem?
    @IBOutlet var tableTopConstraint: NSLayoutConstraint?
    
    
// MARK:- ViewController Lifecycle
    override func viewDidLoad() {
        super.viewDidLoad()
        
        //First responder handler for textfields
        // IQKeyboardManager.sharedManager().enable = true
        
        //Load plist info
        let plistPath = Bundle.main.path(forResource: "Profile", ofType: ".plist", inDirectory:nil)
        
        tableViewRowDetails = NSMutableArray.init(contentsOfFile: plistPath!)
        
        //Resigning First Responder on outside tap
        let gestureRecognizer: UITapGestureRecognizer = UITapGestureRecognizer.init(target: self, action: #selector(ProfileViewController.dismissKeyboard))
        self.tableViewProfile?.addGestureRecognizer(gestureRecognizer)
        
        //Initial data setup
        self.setInitialDate()
        
        self.fdaSlideMenuController()?.delegate = self
        
    }
    
    override func viewWillAppear(_ animated: Bool) { 
        super.viewWillAppear(animated)
        user = User.currentUser
        
        if isPasscodeViewPresented == false{
            UserServices().getUserProfile(self as NMWebServiceDelegate)
        }
        self.setNavigationBarItem()
        
        UIApplication.shared.statusBarStyle = .default
        self.tableViewProfile?.reloadData()
    }
    
    override func viewDidAppear(_ animated: Bool) {
        super.viewDidAppear(animated)
        
       
        
    }
    
    override func viewWillDisappear(_ animated: Bool) {
        print("Profile View will disapper")
        
        if isProfileEdited {
            isProfileEdited = false
            UserServices().updateUserProfile(self)
        }
    }
    
    func leftDidClose() {
        print("Left menu is closed")
    }
    
// MARK:- Button Actions
    
    /**
     
     Change password button clicked
     
     @param sender    Accepts UIButton object

     */
    
    
    @IBAction func buttonActionChangePassCode(_ sender: UIButton){
        
        let passcodeViewController = ORKPasscodeViewController.passcodeEditingViewController(withText: "", delegate: self, passcodeType: .type4Digit)
        passcodeStateIsEditing = true
        
        self.navigationController?.present(passcodeViewController, animated: false, completion: {})
    }
    
    
    /**
     
     Edit Profile button clicked
     
     @param sender    Accepts UIbarButtonItem

     */
    @IBAction func editBarButtonAction(_ sender: UIBarButtonItem){
        
        if self.isCellEditable! == false  {
            self.isCellEditable =  true
            
            self.buttonLeadTime?.isUserInteractionEnabled =  true
            
            self.editBarButtonItem?.title = "Save"
            self.editBarButtonItem?.tintColor = UIColor.black
        }
        else{
            self.view.endEditing(true)
            
            if self.validateAllFields() {
                UserServices().updateUserProfile(self)
            }
        }
        self.tableViewProfile?.reloadData()
    }
    
    
    /**
     
     Button action for LeadtimeButton, CancelButton & DoneButton
     
     @param sender  Accepts UIButton object
     
     */
    @IBAction func buttonActionLeadTime(_ sender: UIButton) {
        
        
        let alertView = UIAlertController(title: kLeadTimeSelectText, message: "\n\n\n\n\n\n\n\n\n\n\n", preferredStyle: UIAlertController.Style.actionSheet);
        
        
        datePickerView = UIDatePicker.init(frame: CGRect(x: 10, y: 30, width: alertView.view.frame.size.width - 40, height: 216) )
        
        datePickerView?.datePickerMode = .countDownTimer
        
        
        let dateFormatter = DateFormatter()
        dateFormatter.dateFormat = "HH:mm"
        
        datePickerView?.date = dateFormatter.date(from: "00:00")!
        
        alertView.view.addSubview(datePickerView!)
        
        let action =   UIAlertAction(title: kActionSheetDoneButtonTitle, style: UIAlertAction.Style.default, handler: {
            action in
            
            let calender:Calendar? = Calendar.current
            
            if Utilities.isValidValue(someObject: self.datePickerView?.date as AnyObject? )  {
                
                let dateComponent = calender?.dateComponents([.hour, .minute], from: (self.datePickerView?.date)!)
                
                
                // title =  hour : minute,  if hour < 10, hour =  "0" + hour ,if minute < 10, minute =  "0" + minute
                
                let title: String! = (((dateComponent?.hour)! as Int) < 10 ? "0\((dateComponent?.hour)! as Int)" : "\((dateComponent?.hour)! as Int)") + ":"
                    
                    + (((dateComponent?.minute)! as Int) < 10 ? "0\((dateComponent?.minute)! as Int)" : "\((dateComponent?.minute)! as Int)")
                
                self.buttonLeadTime?.setTitle(title!, for: .normal)
                
                self.user.settings?.leadTime = title
            }
            
        })
        let actionCancel =   UIAlertAction(title: kActionSheetCancelButtonTitle, style: UIAlertAction.Style.default, handler: {
            action in
            
        })
        
        alertView.addAction(action)
        alertView.addAction(actionCancel)
        present(alertView, animated: true, completion: nil)
        
    }
    
    
    /**
     
     Signout Button Clicked
     
     @param sender  Accepts UIButton Object
     
     */
    @IBAction func buttonActionSignOut(_ sender: UIButton) {
        
        UIUtilities.showAlertMessageWithTwoActionsAndHandler(NSLocalizedString(kSignOutText, comment: ""), errorMessage: NSLocalizedString(kAlertMessageForSignOut, comment: ""), errorAlertActionTitle: NSLocalizedString(kSignOutText, comment: ""),
                                                             errorAlertActionTitle2: NSLocalizedString(kTitleCancel, comment: ""), viewControllerUsed: self,
                                                             action1: {
                                                                
                                                                self.sendRequestToSignOut()
                                                                
                                                                
        },
                                                             action2: {
                                                                
        })
        
    }
    
    
    /**
     
     Delete Account clicked
     
     @param sender  Accepts UIButton Object
     
     */
    @IBAction func buttonActionDeleteAccount(_ sender: UIButton) {
      
        if (Gateway.instance.studies?.count)! > 0 {
            let studies = Gateway.instance.studies
            var joinedStudies:[Study] = []
            if Utilities.isStandaloneApp() {
                let standaloneStudyId = Utilities.standaloneStudyId()
                joinedStudies = studies?.filter({($0.userParticipateState.status == .inProgress || $0.userParticipateState.status == .completed) && ($0.studyId == standaloneStudyId)}) ?? []
            }
            else {
                joinedStudies = studies?.filter({$0.userParticipateState.status == .inProgress || $0.userParticipateState.status == .completed}) ?? []
            }

            if joinedStudies.count != 0 {
                self.performSegue(withIdentifier: "confirmationSegue", sender: joinedStudies)
            }
            else {
                
                var infoDict: NSDictionary?
                if let path = Bundle.main.path(forResource: "Info", ofType: "plist") {
                    infoDict = NSDictionary(contentsOfFile: path)
                }
                let navTitle = infoDict!["ProductTitleName"] as! String
                
                var descriptionText =  kDeleteAccountConfirmationMessage
                descriptionText = descriptionText.replacingOccurrences(of: "#APPNAME#", with: navTitle)
                
                UIUtilities.showAlertMessageWithTwoActionsAndHandler(NSLocalizedString(kTitleDeleteAccount, comment: ""), errorMessage: NSLocalizedString(descriptionText, comment: ""), errorAlertActionTitle: NSLocalizedString(kTitleDeleteAccount, comment: ""),
                                                                     errorAlertActionTitle2: NSLocalizedString(kTitleCancel, comment: ""), viewControllerUsed: self,
                                                                     action1: {

                                                                        self.sendRequestToDeleteAccount()


                },
                                                                     action2: {

                })
            }
        }
        
        
        
    }
    
    
// MARK:- Utility Methods
    
    /**
     
     Dismiss key board when clicked on Background
     
     */
    @objc func dismissKeyboard(){
        self.view.endEditing(true)
    }
    
    
    /**
     
     Api Call to SignOut
     
     */
    func sendRequestToSignOut() {
        UserServices().logoutUser(self)
    }
    
    
    /**
 
     Api call to delete account
     
     */
    func sendRequestToDeleteAccount(){
        let studies: Array<String> = []
        UserServices().deActivateAccount(listOfStudyIds: studies, delegate: self)
    }
    
    
    /**
     
     SignOut Response handler for slider menu setup
     
     */
    func handleSignoutResponse(){
        debugPrint("singout")
        
        if ORKPasscodeViewController.isPasscodeStoredInKeychain(){
            ORKPasscodeViewController.removePasscodeFromKeychain()
        }
        
        let ud = UserDefaults.standard
        ud.set(false, forKey: kPasscodeIsPending)
         ud.set(false, forKey: kShowNotification)
        ud.synchronize()
        
        let appDelegate = (UIApplication.shared.delegate as? AppDelegate)!
        appDelegate.updateKeyAndInitializationVector()

        //fdaSlideMenuController()?.navigateToHomeAfterSingout()
        let leftController = (slideMenuController()?.leftViewController as? LeftMenuViewController)!
        leftController.changeViewController(.studyList)
        leftController.createLeftmenuItems()
        
    }
    
    
    /**
     
     DeleteAccount Response handler
     
     */
    func handleDeleteAccountResponse(){
        // fdaSlideMenuController()?.navigateToHomeAfterSingout()
        
        ORKPasscodeViewController.removePasscodeFromKeychain()
        
        let appDelegate = (UIApplication.shared.delegate as? AppDelegate)!
        appDelegate.updateKeyAndInitializationVector()
        
        
        UIUtilities.showAlertMessageWithActionHandler(NSLocalizedString(kTitleMessage, comment: ""), message: NSLocalizedString(kMessageAccountDeletedSuccess, comment: ""), buttonTitle: NSLocalizedString(kTitleOk, comment: ""), viewControllerUsed: self) {
            
            
            if Utilities.isStandaloneApp() {
                
                UIApplication.shared.keyWindow?.addProgressIndicatorOnWindowFromTop()
                Study.currentStudy = nil
                self.slideMenuController()?.leftViewController?.navigationController?.popToRootViewController(animated: true)
                DispatchQueue.main.asyncAfter(deadline: .now()+1) {
                    UIApplication.shared.keyWindow?.removeProgressIndicatorFromWindow()
                }
            }
            else {
                let leftController = (self.slideMenuController()?.leftViewController as? LeftMenuViewController)!
                leftController.changeViewController(.studyList)
                leftController.createLeftmenuItems()
            }
            
        }
    }
    
    
    /**
     
     SetInitialData sets lead Time
     
     */
    func setInitialDate()  {
        
        if user.settings != nil &&  Utilities.isValidValue(someObject: user.settings?.leadTime as AnyObject?) {
            self.buttonLeadTime?.setTitle(user.settings?.leadTime, for: .normal)
        }
        else{
            Logger.sharedInstance.debug("settings/LeadTime is null")
        }
        self.title = NSLocalizedString(kProfileTitleText, comment: "")
        self.isCellEditable =  true
        
        self.buttonLeadTime?.isUserInteractionEnabled =  false
        
        
        var touchIdEnabled: Bool? = true
        
        
        // 1. Create a authentication context
        let authenticationContext = LAContext()
        var error: NSError?
        
        // 2. Check if the device has a fingerprint sensor
        // If not, show the user an alert view and bail out!
        guard authenticationContext.canEvaluatePolicy(.deviceOwnerAuthenticationWithBiometrics, error: &error) else {
            touchIdEnabled = false
            return
        }
        
        var passcodeDict: Dictionary<String,Any> =  (tableViewRowDetails?[3] as? Dictionary<String, Any>)!
        
    
        guard let keychainPasscodeDict = try? ORKKeychainWrapper.object(forKey: korkPasscode) as? [String : Any] else {
            return
        }
        

        var istouchIdEnabled: Bool =  false
        if keychainPasscodeDict.count > 0 {
            istouchIdEnabled = keychainPasscodeDict[ktouchid] as? Bool ?? false
        }
        
        
        var touchLabelText = kUsePasscodeToAccessApp
        if istouchIdEnabled {
            if authenticationContext.biometryType == .faceID {
                touchLabelText = kUseFaceIdOrPasscode
            }
            else if authenticationContext.biometryType == .touchID{
                touchLabelText = kUseTouchIdOrPasscode
            }
        }
        passcodeDict[kLabelName] =  touchLabelText
        tableViewRowDetails?.replaceObject(at: 3, with: passcodeDict)
        
        
    }
    
    
    /**
     
     Toggle Value change  method for cell Togges
     
     @param Sender  has to be a UISwitch
     
     */
    @objc func toggleValueChanged(_ sender: UISwitch)  {
        
        isProfileEdited = true
        
        let toggle: UISwitch? = sender as UISwitch
        
        if  user.settings != nil {
            
            switch ToggelSwitchTags(rawValue: sender.tag)! as ToggelSwitchTags{
            case .usePasscode:
                user.settings?.passcode = toggle?.isOn
                
                if toggle?.isOn == true{
                    
                    if ORKPasscodeViewController.isPasscodeStoredInKeychain(){
                        ORKPasscodeViewController.removePasscodeFromKeychain()
                    }
                }
                
                self.checkPasscode()
                
            case .useTouchId:
                user.settings?.touchId = toggle?.isOn
                
            case .receivePush:
                user.settings?.remoteNotifications = toggle?.isOn
            case .receiveStudyActivityReminders:
                user.settings?.localNotifications = toggle?.isOn
                
                if (user.settings?.localNotifications)! {
                    //on
                    print("on")
                  self.addProgressIndicator()
                      
                  self.perform(#selector(self.registerLocalNotification), with: self, afterDelay: 1.0)
                }
                else {
                    print("false")
                   self.addProgressIndicator()
                  self.perform(#selector(self.cancelAllLocalNotifications), with: self, afterDelay: 1.0)
                  
                }
            }
            self.editBarButtonItem?.tintColor = UIColor.black
        }
        else{
            Logger.sharedInstance.debug("settings is null")
        }
    }
  
  @objc func cancelAllLocalNotifications(){
    
    UIApplication.shared.cancelAllLocalNotifications()
    let application = UIApplication.shared
    let scheduledNotifications = application.scheduledLocalNotifications!
    print("notification  \(scheduledNotifications)")
    
     self.removeProgressIndicator()
  }
  
 
 
  @objc func registerLocalNotification(){
    LocalNotification.refreshAllLocalNotification()
    self.removeProgressIndicator()
  }
 

    /**
     
     Button action for Change password button
     
     @param sender  Accepts UIbutton Object
     
     */
    @objc func pushToChangePassword(_ sender: UIButton)  {
        self.performSegue(withIdentifier: kChangePasswordSegueIdentifier, sender: nil)
    }
    
    
    /**
     
     Validation to check entered email is valid or not
    
     @return Bool
     
     */
    func validateAllFields() -> Bool{
        
        //(user.firstName?.isEmpty)! && (user.lastName?.isEmpty)! &&
        
        if (user.emailId?.isEmpty)! {
            self.showAlertMessages(textMessage: kMessageAllFieldsAreEmpty)
            return false
        }  else if user.emailId == "" {
            self.showAlertMessages(textMessage: kMessageEmailBlank)
            return false
        }else if !(Utilities.isValidEmail(testStr: user.emailId!)){
            self.showAlertMessages(textMessage: kMessageValidEmail)
            return false
        }
        return true
    }

    
    /**
     
     Method to show the alert using Utility
     
     @param textMessage    message to be displayed
     
     */
    func showAlertMessages(textMessage: String){
        UIUtilities.showAlertMessage("", errorMessage: NSLocalizedString(textMessage, comment: ""), errorAlertActionTitle: NSLocalizedString("OK", comment: ""), viewControllerUsed: self)
    }
    
    
    /**
     
     Used to check weather the user id FDA user or not
     
     */
    func checkPasscode() {
        if User.currentUser.userType == .FDAUser {
            //FDA user
            
            if  ORKPasscodeViewController.isPasscodeStoredInKeychain() == false{
                let passcodeStep = ORKPasscodeStep(identifier: kPasscodeStepIdentifier)
                passcodeStep.passcodeType = .type4Digit
                // passcodeStep.text = kPasscodeSetUpText
                let task = ORKOrderedTask(identifier: kPasscodeTaskIdentifier, steps: [passcodeStep])
                let taskViewController = ORKTaskViewController.init(task: task, taskRun: nil)
                taskViewController.delegate = self
                taskViewController.isNavigationBarHidden = true
                taskViewController.modalPresentationStyle = .fullScreen
                self.navigationController?.present(taskViewController, animated: false, completion: nil)
            }
            else{
                let passcodeViewController = ORKPasscodeViewController.passcodeAuthenticationViewController(withText: "", delegate: self)
                
                self.navigationController?.present(passcodeViewController, animated: false, completion: nil)
            }
        }
        else{
            //Anonomous user
            //ORKPasscodeViewController.removePasscodeFromKeychain()
        }
    }
    
    override func didReceiveMemoryWarning() {
        super.didReceiveMemoryWarning()
        // Dispose of any resources that can be recreated.
    }
    
    
// MARK:- Segue Method
    
    /**
     
     Segue Delegate method for Navigation based on segue connected
     
     */
    override func prepare(for segue: UIStoryboardSegue, sender: Any?) {
        
        if let changePassword = segue.destination as? ChangePasswordViewController {
            changePassword.viewLoadFrom = .profile
            
        }
        else if let confirmDelete = segue.destination as? ConfirmationViewController {
            confirmDelete.joinedStudies = (sender as? Array<Study>)!
            
        }
    }
}


// MARK:- TableView Data source
extension ProfileViewController: UITableViewDataSource {
    
    func tableView(_ tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
        return tableViewRowDetails!.count
    }
    
    func tableView(_ tableView: UITableView, cellForRowAt indexPath: IndexPath) -> UITableViewCell {
        
        let tableViewData = (tableViewRowDetails?.object(at: indexPath.row) as? NSDictionary)!
        
        if indexPath.row <= signupCellLastIndex {
            // for SignUp Cell data
            
            let  cell = (tableView.dequeueReusableCell(withIdentifier: "CommonDetailsCell", for: indexPath) as? SignUpTableViewCell)!
            cell.textFieldValue?.text = ""
            
            var isSecuredEntry: Bool = false
            cell.isUserInteractionEnabled = self.isCellEditable!
            
            cell.textFieldValue?.tag = indexPath.row
            cell.textFieldValue?.delegate = self
            
            var keyBoardType: UIKeyboardType? =  UIKeyboardType.default
            let textFieldTag = TextFieldTags(rawValue: indexPath.row)!
            
            // TextField properties set up according to index
            switch  textFieldTag {
            case  .Password:
                
                cell.buttonChangePassword?.isUserInteractionEnabled =  true
                cell.buttonChangePassword?.isHidden =  false
                cell.buttonChangePassword?.addTarget(self, action:#selector(pushToChangePassword), for: .touchUpInside)
                 cell.buttonChangePassword?.setTitleColor(kUIColorForSubmitButtonBackground, for: .normal)
                cell.textFieldValue?.isHidden = true
                cell.isUserInteractionEnabled = true
                
                isSecuredEntry = true
            case .EmailId:
                keyBoardType = .emailAddress
                isSecuredEntry = false
                
            case .ConfirmPassword: //ChangePasscode
                
                cell.textFieldValue?.isHidden = true
                cell.buttonChangePassword?.isHidden =  false
                cell.buttonChangePassword?.setTitle("Change Passcode", for: .normal)
                
                if User.currentUser.settings?.passcode == true {
                    cell.buttonChangePassword?.isUserInteractionEnabled =  true
                    
                    cell.buttonChangePassword?.setTitleColor(kUIColorForSubmitButtonBackground, for: .normal)
                    
                    cell.isUserInteractionEnabled = true
                }
                else{
                    cell.buttonChangePassword?.isUserInteractionEnabled =  false
                    cell.buttonChangePassword?.setTitleColor(UIColor.gray, for: .normal)
                    
                    cell.isUserInteractionEnabled = false
                }
                
                cell.buttonChangePassword?.addTarget(self, action:#selector(buttonActionChangePassCode), for: .touchUpInside)
                
            //default: break
            }
            //Cell data setup
            cell.populateCellData(data: tableViewData, securedText: isSecuredEntry,keyboardType: keyBoardType)
            
            cell.backgroundColor = UIColor.clear
            
            
            cell.setCellData(tag: TextFieldTags(rawValue: indexPath.row)!)
            
            
            if TextFieldTags(rawValue: indexPath.row) ==  .EmailId{
                cell.textFieldValue?.isUserInteractionEnabled = false
            }
            
            return cell
        }
        else{
            // for ProfileTableViewCell data
            
            let cell = (tableView.dequeueReusableCell(withIdentifier: kProfileTableViewCellIdentifier, for: indexPath) as? ProfileTableViewCell)!
            cell.setCellData(dict: tableViewData)
            
            
            //TODO: handle toggle Value based on user settings
            
            if (user.settings != nil) {
                cell.setToggleValue(indexValue: indexPath.row)
            }
            cell.switchToggle?.tag =  indexPath.row
            // Toggle button Action
            cell.switchToggle?.addTarget(self, action: #selector(ProfileViewController.toggleValueChanged), for: .valueChanged)
            
            cell.isUserInteractionEnabled = self.isCellEditable!
            
            
            return cell
        }
        
    }
}

// MARK:- TableView Delegates
extension ProfileViewController: UITableViewDelegate{
    
    func tableView(_ tableView: UITableView, didSelectRowAt indexPath: IndexPath) {
        tableView.deselectRow(at: indexPath, animated: true)
        //print(indexPath.row)
    }
}

// MARK:- Textfield Delegate
extension ProfileViewController: UITextFieldDelegate{
    
    func textFieldDidBeginEditing(_ textField: UITextField) {
        print(textField.tag)
        
        
    }
    
    func textField(_ textField: UITextField, shouldChangeCharactersIn range: NSRange, replacementString string: String) -> Bool {
        
        let tag: TextFieldTags = TextFieldTags(rawValue: textField.tag)!
        // Disabling space editing
        
        let finalString = textField.text! + string
        
       
        if  tag == .EmailId {
            if string == " " || finalString.count > 255{
                return false
            }
            else{
                return true
            }
        }
        else{
            return true
        }
        
    }
    
    func textFieldDidEndEditing(_ textField: UITextField) {
        print(textField.text!)
        
        // trimming white spaces
        textField.text =  textField.text?.trimmingCharacters(in: CharacterSet.whitespacesAndNewlines)
        
        switch textField.tag {
           
        case TextFieldTags.EmailId.rawValue:
            user.emailId! = textField.text!
            
            break
            
        case TextFieldTags.Password.rawValue:
            
            user.password! = textField.text!
            break
            
        default:
            print("No Matching data Found")
            break
        }
        
    }
}


// MARK:- UserService Response handler
extension ProfileViewController: NMWebServiceDelegate {
    
    func startedRequest(_ manager: NetworkManager, requestName: NSString) {
        Logger.sharedInstance.info("requestname : \(requestName)")
        
        if requestName as String !=  RegistrationMethods.updateUserProfile.description {
             self.addProgressIndicator()
        }
       
    }
    
    func finishedRequest(_ manager: NetworkManager, requestName: NSString, response: AnyObject?) {
        Logger.sharedInstance.info("requestname : \(requestName)")
        
        self.removeProgressIndicator()
        if requestName as String ==  RegistrationMethods.logout.description {
            
            self.handleSignoutResponse()
        }
        else if requestName as String ==  RegistrationMethods.userProfile.description {
            self.tableViewProfile?.reloadData()
            
            if (user.settings?.leadTime?.count)! > 0 {
                self.buttonLeadTime?.setTitle(user.settings?.leadTime, for: .normal)
            }
            
        }
        else if requestName as String ==  RegistrationMethods.updateUserProfile.description {
            
            self.isCellEditable = true
            self.editBarButtonItem?.title = "Edit"
            self.tableViewProfile?.reloadData()
            self.buttonLeadTime?.isUserInteractionEnabled = self.isCellEditable!
             DBHandler.saveUserSettingsToDatabase()
            
        }
        else if requestName as String == RegistrationMethods.deactivate.description{
            self.handleDeleteAccountResponse()
        }
    }
    
    func failedRequest(_ manager: NetworkManager, requestName: NSString, error: NSError) {
        Logger.sharedInstance.info("requestname : \(requestName)")
        self.removeProgressIndicator()
        
        if error.code == 403 { //unauthorized
          
            UIUtilities.showAlertMessageWithActionHandler(kErrorTitle, message: error.localizedDescription, buttonTitle: kTitleOk, viewControllerUsed: self, action: {
                self.fdaSlideMenuController()?.navigateToHomeAfterUnauthorizedAccess()
            })
        }
        else {
            
            UIUtilities.showAlertWithTitleAndMessage(title: NSLocalizedString(kErrorTitle, comment: "") as NSString, message: error.localizedDescription as NSString)
        }
    }
}


// MARK:- ORKPasscode Delegate
extension ProfileViewController: ORKPasscodeDelegate {
    
    func passcodeViewControllerDidFinish(withSuccess viewController: UIViewController) {
        
        UserServices().updateUserProfile(self)
        self.isPasscodeViewPresented = true
       
        //Recent Changes
        let appDelegate = (UIApplication.shared.delegate as? AppDelegate)!
        appDelegate.appIsResignedButDidNotEnteredBackground = false

        self.perform(#selector(dismissTaskViewController), with: self, afterDelay: 1)
         self.setInitialDate()
    }
    
    func passcodeViewControllerDidFailAuthentication(_ viewController: UIViewController) {
        
    }
    
    
    func passcodeViewControllerDidCancel(_ viewController: UIViewController){
        
        if passcodeStateIsEditing{
            viewController.dismiss(animated: true, completion: {
                self.passcodeStateIsEditing = false
            })
        }
        
    }
    
}




// MARK:- ORKTaskViewController Delegate
extension ProfileViewController: ORKTaskViewControllerDelegate{
    
    func taskViewControllerSupportsSaveAndRestore(_ taskViewController: ORKTaskViewController) -> Bool {
        return true
    }
    
    public func taskViewController(_ taskViewController: ORKTaskViewController, didFinishWith reason: ORKTaskViewControllerFinishReason, error: Error?) {
        
        var taskResult: Any?
        
        switch reason {
            
        case ORKTaskViewControllerFinishReason.completed:
            print("completed")
            taskResult = taskViewController.result
            
         //   let passcodeDict:NSDictionary? =  ORKKeychainWrapper.object(forKey: "ORKPasscode", error:nil) as? NSDictionary
            
         //   ORKPasscodeViewController.forcePasscode(passcodeDict?.object(forKey: "passcode") as! String, withTouchIdEnabled: false)
            
            
            
            //Following will be executed only when passcode is setted for first time
            
            if taskViewController.task?.identifier != "ChangePassCodeTask"{
                UserServices().updateUserProfile(self)
                self.isPasscodeViewPresented = true
            }
            
            let ud = UserDefaults.standard
            ud.set(false, forKey: kPasscodeIsPending)
            ud.synchronize()
            
        case ORKTaskViewControllerFinishReason.failed:
            print("failed")
            taskResult = taskViewController.result
        case ORKTaskViewControllerFinishReason.discarded:
            print("discarded")
            
            if taskViewController.task?.identifier != "ChangePassCodeTask"{
                user.settings?.passcode = user.settings?.passcode == true ? false: true
            }
            
            taskResult = taskViewController.result
        case ORKTaskViewControllerFinishReason.saved:
            print("saved")
            taskResult = taskViewController.restorationData
            
        }
        
        let appDelegate = (UIApplication.shared.delegate as? AppDelegate)!
        appDelegate.appIsResignedButDidNotEnteredBackground = false
      
      
      self.perform(#selector(dismissTaskViewController), with: self, afterDelay: 1)
      
//        let localTaskViewcontroller =  taskViewController
//        self.dismiss(animated: true, completion: {
//          localTaskViewcontroller.view.endEditing(true)
//        })
    }
  
  @objc func dismissTaskViewController() {
    self.dismiss(animated: true, completion: {
 })
  }
  
  
    func taskViewController(_ taskViewController: ORKTaskViewController, stepViewControllerWillAppear stepViewController: ORKStepViewController) {
        
    }
}

