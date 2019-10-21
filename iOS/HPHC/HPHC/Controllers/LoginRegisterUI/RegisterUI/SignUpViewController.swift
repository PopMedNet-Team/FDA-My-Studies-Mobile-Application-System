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

let kVerifyMessageFromSignUp = "An email has been sent to xyz@gmail.com. Please type in the Verification Code received in the email to complete the verification step."

enum SignUpLoadFrom: Int{
    case gatewayOverview
    case login        // from gateway login-> signup
    case menu        // from menu
    case menu_login  //from menu->Login->Signup
    case joinStudy_login //from joinStudy->Login->Signup
}


class SignUpViewController: UIViewController{
    
    var tableViewRowDetails: NSMutableArray?
    var agreedToTerms: Bool = false
    var confirmPassword = ""
    var user: User!
    
    @IBOutlet var tableView: UITableView?
    @IBOutlet var tableViewFooterView: UIView?
    @IBOutlet var buttonSubmit: UIButton?
    
    @IBOutlet var buttonAgree: UIButton?
    @IBOutlet var labelTermsAndConditions: FRHyperLabel?
    @IBOutlet var termsAndCondition: LinkTextView?
    var viewLoadFrom: SignUpLoadFrom = .menu
    var termsPageOpened = false
    override var preferredStatusBarStyle: UIStatusBarStyle{
        return .default
    }

// MARK:- ViewController Lifecycle
    
    override func viewDidLoad() {
        super.viewDidLoad()
        
        //Used to set border color for bottom view
        buttonSubmit?.layer.borderColor = kUicolorForButtonBackground
        
        self.title = NSLocalizedString(kSignUpTitleText, comment: "")
        
        //load plist info
        let plistPath = Bundle.main.path(forResource: "SignUpPlist", ofType: ".plist", inDirectory: nil)
        tableViewRowDetails = NSMutableArray.init(contentsOfFile: plistPath!)
        
        //Automatically takes care  of text field become first responder and scroll of tableview
        // IQKeyboardManager.sharedManager().enable = true
        
        //info button
        self.navigationItem.rightBarButtonItem = UIBarButtonItem.init(image: UIImage.init(named: "info"), style: .done, target: self, action: #selector(self.buttonInfoAction(_:)))
       
        
        //Used for background tap dismiss keyboard
        let tapGestureRecognizer: UITapGestureRecognizer = UITapGestureRecognizer.init(target: self, action: #selector(SignUpViewController.dismissKeyboard))
        self.tableView?.addGestureRecognizer(tapGestureRecognizer)
        
        //unhide navigationbar
        self.navigationController?.setNavigationBarHidden(false, animated: true)
        
        //WCPServices().getTermsPolicy(delegate: self)
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
        
        if termsPageOpened {
            termsPageOpened = false
        } else {
            //unhide navigationbar
            self.navigationController?.setNavigationBarHidden(false, animated: true)
            
            User.resetCurrentUser()
            self.user = User.currentUser
            confirmPassword = ""
           
            if viewLoadFrom == .menu{
                self.setNavigationBarItem()
            } else {
                self.addBackBarButton()
            }
 
            setNeedsStatusBarAppearanceUpdate()
            self.tableView?.reloadData()
        }
    }
    
    override func viewDidAppear(_ animated: Bool) {
        super.viewDidAppear(animated)
        
        
    }
    
// MARK:- Utility Methods
    
    /**
     
     Attributed string for Terms & Privacy Policy
     
     */
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
    
    
    /**
     
     Dismiss key board when clicked on Background
     
     */
    @objc func dismissKeyboard(){
        self.view.endEditing(true)
    }
    
    
    /**
     
     All validation checks and Password,Email complexity checks
     
     @return Bool
     
     */
    func validateAllFields() -> Bool{
        //(user.firstName?.isEmpty)! && (user.lastName?.isEmpty)! &&
        if  (self.user.emailId?.isEmpty)! && (self.user.password?.isEmpty)! && confirmPassword.isEmpty {
            self.showAlertMessages(textMessage: kMessageAllFieldsAreEmpty)
            return false
        } else if self.user.emailId == "" {
            self.showAlertMessages(textMessage: kMessageEmailBlank)
            return false
        } else if !(Utilities.isValidEmail(testStr: self.user.emailId!)) {
            self.showAlertMessages(textMessage: kMessageValidEmail)
            return false
        } else if self.user.password == "" {
            self.showAlertMessages(textMessage: kMessagePasswordBlank)
            return false
        } else if Utilities.isPasswordValid(text: (self.user.password)!) == false  {
            self.showAlertMessages(textMessage: kMessageValidatePasswordComplexity)
            return false
        } else if (self.user.password)! == user.emailId {
            self.showAlertMessages(textMessage: kMessagePasswordMatchingToOtherFeilds)
            return false
        } else if confirmPassword == "" {
            self.showAlertMessages(textMessage: kMessageProfileConfirmPasswordBlank)
            return false
        } else if (self.user.password != confirmPassword) {
            self.showAlertMessages(textMessage: kMessageValidatePasswords)
            return false
        }
        return true
    }
    
    
    /**
     
     Used to show the alert using Utility
     @param textMessage     data which we need to display in the alert
     
     */
    func showAlertMessages(textMessage: String){
        UIUtilities.showAlertMessage("", errorMessage: NSLocalizedString(textMessage, comment: ""), errorAlertActionTitle: NSLocalizedString("OK", comment: ""), viewControllerUsed: self)
    }
    
    
    /**
     
     Method to navigate to Verification Controller
     
     */
    func navigateToVerificationController(){
        self.performSegue(withIdentifier: "verificationSegue", sender: nil)
    }
    
    
// MARK:- Button Actions
    
    /**

     Submit button Clicked and Used to check all the validations
     before making a Register webservice call
     
     @param sender  Accepts any object
     
     */
    @IBAction func submitButtonAction(_ sender: Any) {
        
        self.view.endEditing(true)
        
        if self.validateAllFields() == true {
            
            if !(agreedToTerms) {
                self.showAlertMessages(textMessage: kMessageAgreeToTermsAndConditions)
            } else {
                //Call the Webservice
                UserServices().registerUser(self as NMWebServiceDelegate)
            }
        }
    }
    
    
    /**
     
     Agree to terms and conditions button clicked
     
     @param sender  accepts any object
     
     */
    @IBAction func agreeButtonAction(_ sender: Any) {
        if (sender as? UIButton)!.isSelected{
            (sender as? UIButton)!.isSelected = !(sender as? UIButton)!.isSelected
            agreedToTerms = false
        } else {
            agreedToTerms = true
            (sender as? UIButton)!.isSelected = !(sender as? UIButton)!.isSelected
        }
    }
    
    
    /**
     
     Displays alert regarding why the user has to register
     
     @param sender  Accepts any object
     
     */
    @IBAction func buttonInfoAction(_ sender: Any){
        UIUtilities.showAlertWithTitleAndMessage(title: "Why Register?", message: kRegistrationInfoMessage as NSString)
    }

    
// MARK:- Segue Method
    
    override func prepare(for segue: UIStoryboardSegue, sender: Any?) {
        if let verificationController = segue.destination as? VerificationViewController {
            switch viewLoadFrom {
                 case .menu:
                    verificationController.shouldCreateMenu = false
                    verificationController.viewLoadFrom = .signup
                 case .menu_login:
                    verificationController.shouldCreateMenu = false
                    verificationController.viewLoadFrom = .login
                 case .joinStudy_login:
                    verificationController.shouldCreateMenu = false
                    verificationController.viewLoadFrom = .joinStudy
                 case .login:
                    verificationController.shouldCreateMenu = true
                    verificationController.viewLoadFrom = .login
                case .gatewayOverview:
                     verificationController.shouldCreateMenu = true
                    verificationController.viewLoadFrom = .signup
            }
            let message = kVerifyMessageFromSignUp
            let modifiedMessage = message.replacingOccurrences(of: kDefaultEmail, with: User.currentUser.emailId!)
            verificationController.labelMessage = modifiedMessage

        }
    }
}


// MARK:- Gesture Delegate
extension SignUpViewController: UIGestureRecognizerDelegate{
    func gestureRecognizerShouldBegin(_ gestureRecognizer: UIGestureRecognizer) -> Bool {
        if gestureRecognizer.isKind(of: UITapGestureRecognizer.classForCoder()) {
            if gestureRecognizer.numberOfTouches == 2 {
                return false
            }
        }
        return true
    }
}


// MARK:- UITextView Delegate
class LinkTextView: UITextView{
    
    override func canPerformAction(_ action: Selector, withSender sender: Any?) -> Bool {
        return false
    }
    
    override func addGestureRecognizer(_ gestureRecognizer: UIGestureRecognizer) {
        
        if gestureRecognizer.isKind(of: UILongPressGestureRecognizer.classForCoder()){
            // gestureRecognizer.isEnabled = false
        }
        if gestureRecognizer.isKind(of: UITapGestureRecognizer.classForCoder()) {
            //let tap = gestureRecognizer as! UITapGestureRecognizer
            //if tap.numberOfTapsRequired == 2 {
            gestureRecognizer.isEnabled = false
            //}
        }
        super.addGestureRecognizer(gestureRecognizer)
    }
}


// MARK:- Textfield Delegate
extension SignUpViewController: UITextViewDelegate{
    
    func textView(_ textView: UITextView, shouldInteractWith URL: URL, in characterRange: NSRange) -> Bool {
        
        var link: String =   (TermsAndPolicy.currentTermsAndPolicy?.termsURL)! //kTermsAndConditionLink
        var title: String = kNavigationTitleTerms
        if (URL.absoluteString == TermsAndPolicy.currentTermsAndPolicy?.policyURL  && characterRange.length == String("Privacy Policy").count ) {
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


// MARK:- TableView Datasource
extension SignUpViewController: UITableViewDataSource {
    
    func tableView(_ tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
        return tableViewRowDetails!.count
    }
    
    func tableView(_ tableView: UITableView, cellForRowAt indexPath: IndexPath) -> UITableViewCell {
        
        let tableViewData = (tableViewRowDetails?.object(at: indexPath.row) as? NSDictionary)!
        let cell = (tableView.dequeueReusableCell(withIdentifier: kSignUpTableViewCellIdentifier, for: indexPath) as? SignUpTableViewCell)!
        
        
        cell.textFieldValue?.text = ""
        var isSecuredEntry: Bool = false
        
        cell.textFieldValue?.tag = indexPath.row
        
        var keyBoardType: UIKeyboardType? =  UIKeyboardType.default
        let textFieldTag = TextFieldTags(rawValue: indexPath.row)!
        
        //Cell TextField data setup
        switch  textFieldTag {
        /*
        case .FirstNameTag,.LastName:
            cell.textFieldValue?.autocapitalizationType = .sentences
            
            isSecuredEntry = false
        */
        case  .Password:
            
            isSecuredEntry = true
            cell.textFieldValue?.text = self.user.password
        case  .ConfirmPassword:
            isSecuredEntry = true
            cell.textFieldValue?.text = confirmPassword
        case .EmailId :
            keyBoardType = .emailAddress
            isSecuredEntry = false
            cell.textFieldValue?.text = self.user.emailId
        //default: break
        }
        //Cell Data Setup
        cell.populateCellData(data: tableViewData, securedText: isSecuredEntry,keyboardType: keyBoardType)
        
        cell.backgroundColor = UIColor.clear
        return cell
    }
}


// MARK:- TableView Delegates
extension SignUpViewController: UITableViewDelegate{
    
    func tableView(_ tableView: UITableView, didSelectRowAt indexPath: IndexPath) {
        tableView.deselectRow(at: indexPath, animated: true)
        //print(indexPath.row)
    }
}


// MARK:- Textfield Delegate
extension SignUpViewController: UITextFieldDelegate{
    
    func textFieldDidBeginEditing(_ textField: UITextField) {
        print(textField.tag)
        if textField.tag == TextFieldTags.EmailId.rawValue{
            textField.keyboardType = .emailAddress
        }
    }
    
    func textField(_ textField: UITextField, shouldChangeCharactersIn range: NSRange, replacementString string: String) -> Bool {
        
        let tag: TextFieldTags = TextFieldTags(rawValue: textField.tag)!
         let finalString = textField.text! + string
        
        if string == " " {
            return false
        }
        
        if  tag == .EmailId {
            if string == " " || finalString.count > 255{
                return false
            } else {
                return true
            }
        } else if tag == .Password || tag == .ConfirmPassword {
            if finalString.count > 64 {
                return false
            } else {
                if (range.location == textField.text?.count && string == " ") {
                    
                    textField.text = textField.text?.appending("\u{00a0}")
                    return false
                }
                return true
            }
        } else {
            return true
        }
    }
    
    func textFieldDidEndEditing(_ textField: UITextField) {
        print(textField.text!)
        
        textField.text =  textField.text?.trimmingCharacters(in: CharacterSet.whitespacesAndNewlines)
        
        let tag: TextFieldTags = TextFieldTags(rawValue: textField.tag)!
        
        switch tag {
        /*
        case .FirstNameTag:
            self.user.firstName = textField.text!
            break
            
        case .LastName:
            self.user.lastName = textField.text!
            break
        */
        case .EmailId:
            self.user.emailId = textField.text!
            break
            
        case .Password:
            self.user.password = textField.text!
            break
            
        case .ConfirmPassword:
            confirmPassword = textField.text!
            break
            
//        default:
//            print("No Matching data Found")
//            break
        }
    }
}


// MARK:- Webservice delegates
extension SignUpViewController: NMWebServiceDelegate {
    
    func startedRequest(_ manager: NetworkManager, requestName: NSString) {
        
        Logger.sharedInstance.info("requestname : \(requestName)")
         self.addProgressIndicator()
        if requestName .isEqual(to: RegistrationMethods.register.rawValue){
           
        }
    }
    
    func finishedRequest(_ manager: NetworkManager, requestName: NSString, response: AnyObject?) {
        Logger.sharedInstance.info("requestname : \(requestName)")
        
       self.removeProgressIndicator()
        if requestName .isEqual(to: RegistrationMethods.register.description)   {
            
            let delegate = (UIApplication.shared.delegate as? AppDelegate)!
            delegate.calculateTimeZoneChange()
            ORKPasscodeViewController.removePasscodeFromKeychain()
            self.navigateToVerificationController()
        } else {
            self.agreeToTermsAndConditions()
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
