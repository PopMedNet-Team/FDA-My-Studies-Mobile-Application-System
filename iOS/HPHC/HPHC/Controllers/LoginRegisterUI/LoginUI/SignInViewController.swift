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
import IQKeyboardManagerSwift
import SlideMenuControllerSwift

let kVerifyMessageFromSignIn = "Your registered email is pending verification. Please type in the Verification Code received in the email to complete this step and proceed to using the app."


enum SignInLoadFrom: Int{
    case gatewayOverview
    case joinStudy
    case menu
}

class SignInViewController: UIViewController{
    
    @IBOutlet var tableView: UITableView?
    @IBOutlet var buttonSignIn: UIButton?
    @IBOutlet var buttonSignUp: UIButton?
    @IBOutlet var termsAndCondition: LinkTextView?
    var viewLoadFrom: SignInLoadFrom = .menu
    var tableViewRowDetails: NSMutableArray?
    var user = User.currentUser
    var termsPageOpened = false
    override var preferredStatusBarStyle: UIStatusBarStyle {
        return .default
    }
    
// MARK:- ViewController Lifecycle
    
    override func viewDidLoad() {
        super.viewDidLoad()
        
        //Used to set border color for bottom view
        buttonSignIn?.layer.borderColor = kUicolorForButtonBackground
        self.title = NSLocalizedString(kSignInTitleText, comment: "")
        
        //load plist info
        let plistPath = Bundle.main.path(forResource: "SignInPlist", ofType: ".plist", inDirectory: nil)
        tableViewRowDetails = NSMutableArray.init(contentsOfFile: plistPath!)
        
        //Automatically takes care  of text field become first responder and scroll of tableview
        // IQKeyboardManager.sharedManager().enable = true
        
        //Used for background tap dismiss keyboard
        let gestureRecognizer: UITapGestureRecognizer = UITapGestureRecognizer.init(target: self, action: #selector(SignInViewController.dismissKeyboard))
        self.tableView?.addGestureRecognizer(gestureRecognizer)
        
        
        //info button
        self.navigationItem.rightBarButtonItem = UIBarButtonItem.init(image: UIImage.init(named: "info"), style: .done, target: self, action: #selector(self.buttonInfoAction(_:)))
        
        
        //unhide navigationbar
        self.navigationController?.setNavigationBarHidden(false, animated: true)
        
        //WCPServices().getTermsPolicy(delegate: self)
        
        if let attributedTitle = buttonSignUp?.attributedTitle(for: .normal) {
            let mutableAttributedTitle = NSMutableAttributedString(attributedString: attributedTitle)
            
            mutableAttributedTitle.addAttribute(NSAttributedString.Key.foregroundColor, value: #colorLiteral(red: 0, green: 0.4862745098, blue: 0.7294117647, alpha: 1) , range: NSRange(location: 10,length: 7))
            
            buttonSignUp?.setAttributedTitle(mutableAttributedTitle, for: .normal)
        }
        
        let brandingDetail = Utilities.getBrandingDetails()
        TermsAndPolicy.currentTermsAndPolicy =  TermsAndPolicy()
        guard let policyURL = brandingDetail?[BrandingConstant.PrivacyPolicyURL] as? String,let terms = brandingDetail?[BrandingConstant.TermsAndConditionURL] as? String else {
           return
        }
        TermsAndPolicy.currentTermsAndPolicy?.initWith(terms: terms, policy: policyURL)
        self.agreeToTermsAndConditions()
        
       
       
    }
    
    override func viewWillAppear(_ animated: Bool) {
        super.viewWillAppear(animated)
        
        //unhide navigationbar
       
        self.navigationController?.setNavigationBarHidden(false, animated: true)
        
        User.resetCurrentUser()
        user = User.currentUser
        
        if viewLoadFrom == .gatewayOverview || viewLoadFrom == .joinStudy {
            self.addBackBarButton()
        } else {
            self.setNavigationBarItem()
        }
        
        
        if termsPageOpened {
            termsPageOpened = false
        }
        
        
       // self.perform(#selector(SignInViewController.setInitialDate), with: self, afterDelay: 1)
        
        self.tableView?.reloadData()
        
        //UIApplication.shared.statusBarStyle = .default
        setNeedsStatusBarAppearanceUpdate()
    }
    
    override func viewDidAppear(_ animated: Bool) {
        super.viewDidAppear(animated)
       
    }
    
    override func viewWillDisappear(_ animated: Bool) {
        //hide navigationbar
        if viewLoadFrom == .gatewayOverview || Utilities.isStandaloneApp(){
            self.navigationController?.setNavigationBarHidden(true, animated: true)
        }
    }
    
    
// MARK:- Button Action
    
    /**
     
     Sign In Button Clicked and is used to check all the validations 
     before making a login webservice call
     
     @param sender  accepts any object
     
     */
    @IBAction func signInButtonAction(_ sender: Any) {
    
        self.view.endEditing(true)
        if (user.emailId?.isEmpty)! && (user.password?.isEmpty)! {
            self.showAlertMessages(textMessage: kMessageAllFieldsAreEmpty)
        } else if user.emailId == "" {
            self.showAlertMessages(textMessage: kMessageEmailBlank)

        } else if !(Utilities.isValidEmail(testStr: user.emailId!)) {
            self.showAlertMessages(textMessage: kMessageValidEmail)

        } else if user.password == "" {
            self.showAlertMessages(textMessage: kMessagePasswordBlank)

        } else {

            let ud = UserDefaults.standard
            if user.emailId == kStagingUserEmailId {
                ud.set(true, forKey: kIsStagingUser)
            } else {
                ud.set(false, forKey: kIsStagingUser)
            }
            ud.synchronize()

            UserServices().loginUser(self)
        }
    }
    
    
    /**
     
     To Display registration information
     
     @param sender    accepts any object

     */
    @IBAction func buttonInfoAction(_ sender: Any){
        UIUtilities.showAlertWithTitleAndMessage(title: "Why Register?", message: kRegistrationInfoMessage as NSString)
    }
    

// MARK:-
    
    /**
     
     Initial Data Setup which displays email and password
     
     */
    func setInitialDate()  {
        
        // if textfield have data then we are updating same to model object
        
        var selectedCell: SignInTableViewCell =  (tableView!.cellForRow(at: IndexPath(row: 0, section: 0)) as? SignInTableViewCell)!
        
        let emailTextFieldValue = selectedCell.textFieldValue?.text
        selectedCell = (tableView!.cellForRow(at: IndexPath(row: 1, section: 0)) as? SignInTableViewCell)!
        
        let passwordTextFieldValue = selectedCell.textFieldValue?.text
        
        
        if emailTextFieldValue?.isEmpty == false &&  (emailTextFieldValue?.count)! > 0{
            user.emailId = emailTextFieldValue
        }
        if passwordTextFieldValue?.isEmpty == false && (passwordTextFieldValue?.count)! > 0{
            user.password = passwordTextFieldValue
        }
        self.tableView?.reloadData()
    }
    
    
    /**
     
     Used to show the alert using Utility
     
     @param textMessage     Used to display the alert text
     
     */
    func showAlertMessages(textMessage: String){
        UIUtilities.showAlertMessage("", errorMessage: NSLocalizedString(textMessage, comment: ""), errorAlertActionTitle: NSLocalizedString("OK", comment: ""), viewControllerUsed: self)
    }
    
    
    /**
     
     Dismiss key board when clicked on Background
     
     */
    @objc func dismissKeyboard(){
        self.view.endEditing(true)
    }
    
    
    /**
     
     creatingMenuView before Navigating to DashBoard
     
     */
    func navigateToGatewayDashboard(){
        self.createMenuView()
    }
    
    
    /**
     
     Used to Naviagate Changepassword view controller using gateway storyboard
     
     */
    func navigateToChangePassword(){
        
        let storyboard = UIStoryboard(name: kStoryboardIdentifierGateway, bundle: nil)
        
        let changePassword = (storyboard.instantiateViewController(withIdentifier: "ChangePasswordViewController") as? ChangePasswordViewController)!
        if viewLoadFrom == .menu {
            changePassword.viewLoadFrom = .menu_login
        } else if viewLoadFrom == .joinStudy {
            changePassword.viewLoadFrom = .joinStudy
        } else {
            changePassword.viewLoadFrom = .login
        }
        changePassword.temporaryPassword = User.currentUser.password!
        self.navigationController?.pushViewController(changePassword, animated: true)
    }
    
    
    /**
     
     Used to navigate to Verification controller
     
     */
    func navigateToVerifyController(){
        self.performSegue(withIdentifier: "verificationSegue", sender: nil)
    }
    
    
    /**
     
     Method to update Left Menu using FDASlideMenuViewController and Gateway dashboard
     
     */
    func createMenuView() {
        
        let storyboard = UIStoryboard(name: kStoryboardIdentifierGateway, bundle: nil)
        
        let fda = (storyboard.instantiateViewController(withIdentifier: kStoryboardIdentifierSlideMenuVC) as? FDASlideMenuViewController)!
        fda.automaticallyAdjustsScrollViewInsets = true
        self.navigationController?.pushViewController(fda, animated: true)
    }
    
    
    func agreeToTermsAndConditions(){
        
        self.termsAndCondition?.delegate = self
        let attributedString =  (termsAndCondition?.attributedText.mutableCopy() as? NSMutableAttributedString)!
        
        var foundRange = attributedString.mutableString.range(of: "Terms")
        attributedString.addAttribute(NSAttributedString.Key.link, value:(TermsAndPolicy.currentTermsAndPolicy?.termsURL!)! as String, range: foundRange)
        
        foundRange = attributedString.mutableString.range(of: "Privacy Policy")
        attributedString.addAttribute(NSAttributedString.Key.link, value:(TermsAndPolicy.currentTermsAndPolicy?.policyURL!)! as String  , range: foundRange)
        
        termsAndCondition?.attributedText = attributedString
        
        termsAndCondition?.linkTextAttributes = convertToOptionalNSAttributedStringKeyDictionary([NSAttributedString.Key.foregroundColor.rawValue: Utilities.getUIColorFromHex(0x007CBA)])
        
    }
    
    
// MARK:- Segue Methods
    
    @IBAction func unwindFromVerification(_ segue: UIStoryboardSegue){
        
    }
    
    override func prepare(for segue: UIStoryboardSegue, sender: Any?) {
        
        if let signUpController = segue.destination as? SignUpViewController {
            if viewLoadFrom == .menu {
                signUpController.viewLoadFrom = .menu_login
            } else if(viewLoadFrom == .joinStudy) {
                signUpController.viewLoadFrom = .joinStudy_login
            } else {
                signUpController.viewLoadFrom = .login
            }
            
        }
        if let verificationController = segue.destination as? VerificationViewController {
            
            if viewLoadFrom == .menu {
                verificationController.shouldCreateMenu = false
                verificationController.viewLoadFrom = .login
            } else if viewLoadFrom == .joinStudy {
                verificationController.viewLoadFrom = .joinStudy
                verificationController.shouldCreateMenu = false
            } else if viewLoadFrom == .gatewayOverview {
                
                verificationController.viewLoadFrom = .login
                verificationController.shouldCreateMenu = true
            }
            
            verificationController.labelMessage = kVerifyMessageFromSignIn
        }
    }
}


// MARK:- TableView Data source
extension SignInViewController : UITableViewDataSource {
    
    func tableView(_ tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
        return 2
    }
    
    func tableView(_ tableView: UITableView, cellForRowAt indexPath: IndexPath) -> UITableViewCell {
        let tableViewData = (tableViewRowDetails?.object(at: indexPath.row) as? NSDictionary)!
        
        let cell = (tableView.dequeueReusableCell(withIdentifier: kSignInTableViewCellIdentifier, for: indexPath) as? SignInTableViewCell)!
        
        cell.textFieldValue?.text = ""
        var isSecuredEntry: Bool = false
        if indexPath.row == SignInTableViewTags.Password.rawValue {
            isSecuredEntry = true
        } else {
            cell.textFieldValue?.keyboardType = .emailAddress
        }
        
        cell.textFieldValue?.tag = indexPath.row
        cell.populateCellData(data: tableViewData, securedText: isSecuredEntry)
        
        return cell
    }
}


// MARK:- TableView Delegates
extension SignInViewController:  UITableViewDelegate {
    
    func tableView(_ tableView: UITableView, didSelectRowAt indexPath: IndexPath) {
        tableView.deselectRow(at: indexPath, animated: true)
        
    }
}


// MARK:- Textfield Delegate
extension SignInViewController: UITextFieldDelegate{
    
    func textFieldDidBeginEditing(_ textField: UITextField) {
        print(textField.tag)
        
    }
    
    func textField(_ textField: UITextField, shouldChangeCharactersIn range: NSRange, replacementString string: String) -> Bool {
        
        let tag: SignInTableViewTags = SignInTableViewTags(rawValue: textField.tag)!
        
        if tag == .EmailId {
            if string == " " {
                return false
            } else {
                return true
            }
        } else {
            if (range.location == textField.text?.count && string == " ") {
                
                textField.text = textField.text?.appending("\u{00a0}")
                return false
            }
            return true
        }
    }
    
    func textFieldDidEndEditing(_ textField: UITextField) {
        print(textField.text!)
        switch textField.tag {
        case SignInTableViewTags.EmailId.rawValue:
            user.emailId = textField.text
            break
            
        case SignInTableViewTags.Password.rawValue:
            user.password = textField.text
            break
            
        default:
            print("No Matching data Found")
            break
        }
    }
}

extension SignInViewController: UIGestureRecognizerDelegate{
    func gestureRecognizerShouldBegin(_ gestureRecognizer: UIGestureRecognizer) -> Bool {
        if gestureRecognizer.isKind(of: UITapGestureRecognizer.classForCoder()) {
            if gestureRecognizer.numberOfTouches == 2 {
                return false
            }
        }
        return true
    }
}



extension SignInViewController: UITextViewDelegate{
    
    func textView(_ textView: UITextView, shouldInteractWith URL: URL, in characterRange: NSRange) -> Bool {
        
        print(characterRange.description)
        
        var link: String =   (TermsAndPolicy.currentTermsAndPolicy?.termsURL)! //kTermsAndConditionLink
        var title: String = kNavigationTitleTerms
        if (URL.absoluteString == TermsAndPolicy.currentTermsAndPolicy?.policyURL && characterRange.length == String("Privacy Policy").count) {
            //kPrivacyPolicyLink
            print("terms")
            link =  (TermsAndPolicy.currentTermsAndPolicy?.policyURL)! // kPrivacyPolicyLink
            title = kNavigationTitlePrivacyPolicy
            
        }
        let loginStoryboard = UIStoryboard.init(name: "Main", bundle: Bundle.main)
        let webViewController = (loginStoryboard.instantiateViewController(withIdentifier: "WebViewController") as? UINavigationController)!
        let webview = (webViewController.viewControllers[0] as? WebViewController)!
        webview.requestLink = link
        webview.title = title
        self.navigationController?.present(webViewController, animated: true, completion: nil)
        
        termsPageOpened = true
        
        return false
    }
    
    func gestureRecognizer(_ gestureRecognizer: UIGestureRecognizer, shouldReceive press: UIPress) -> Bool {
        return false
    }
    
    func textViewDidChangeSelection(_ textView: UITextView) {
        if(!NSEqualRanges(textView.selectedRange, NSMakeRange(0, 0))) {
            textView.selectedRange = NSMakeRange(0, 0);
        }
    }
}




// MARK:- Webservices Delegate
extension SignInViewController: NMWebServiceDelegate {
    
    func startedRequest(_ manager: NetworkManager, requestName: NSString) {
        Logger.sharedInstance.info("requestname : \(requestName)")
        self.addProgressIndicator()
    }
    
    func finishedRequest(_ manager: NetworkManager, requestName: NSString, response: AnyObject?) {
        Logger.sharedInstance.info("requestname : \(requestName)")
        self.removeProgressIndicator()
        
        if requestName as String == WCPMethods.termsPolicy.method.methodName {
            self.agreeToTermsAndConditions()
        } else {
            let delegate = (UIApplication.shared.delegate as? AppDelegate)!
            delegate.calculateTimeZoneChange()
            if User.currentUser.verified == true {
                
                let ud = UserDefaults.standard
                ud.set(true, forKey: kNotificationRegistrationIsPending)
                ud.synchronize()
                
                ORKPasscodeViewController.removePasscodeFromKeychain()
                
                if User.currentUser.isLoginWithTempPassword {
                    self.navigateToChangePassword()
                } else {
                    
                    if viewLoadFrom == .gatewayOverview {
                        self.navigateToGatewayDashboard()
                        
                    } else if viewLoadFrom == .joinStudy {
                        
                        let leftController = (slideMenuController()?.leftViewController as? LeftMenuViewController)!
                        leftController.createLeftmenuItems()
                        self.performSegue(withIdentifier: "unwindStudyHomeSegue", sender: self)
                        
                    } else {
                        
                        let leftController = (slideMenuController()?.leftViewController as? LeftMenuViewController)!
                        leftController.createLeftmenuItems()
                        leftController.changeViewController(.studyList)
                    }
                }
            } else {
                
                self.navigateToVerifyController()
            }
        }
    }
    
    func failedRequest(_ manager: NetworkManager, requestName: NSString, error: NSError) {
        Logger.sharedInstance.info("requestname : \(requestName)")
        
        self.removeProgressIndicator()
        
        UIUtilities.showAlertWithTitleAndMessage(title: NSLocalizedString(kErrorTitle, comment: "") as NSString, message: error.localizedDescription as NSString)
    }
}



// Helper function inserted by Swift 4.2 migrator.
fileprivate func convertToOptionalNSAttributedStringKeyDictionary(_ input: [String: Any]?) -> [NSAttributedString.Key: Any]? {
	guard let input = input else { return nil }
	return Dictionary(uniqueKeysWithValues: input.map { key, value in (NSAttributedString.Key(rawValue: key), value)})
}
