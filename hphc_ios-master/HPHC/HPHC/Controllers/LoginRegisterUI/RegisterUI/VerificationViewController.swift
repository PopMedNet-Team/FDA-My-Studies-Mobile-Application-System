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

let kDefaultEmail = "xyz@gmail.com"
let kSignupCompletionSegue = "signupCompletionSegue"
let kAlertMessageText = "Message"
let kAlertMessageVerifyEmail = "Please verify your email address."
let kAlertMessageResendEmail = "An email verification code has been sent to your registered email."

let kChangePasswordViewControllerIdentifier = "ChangePasswordViewController"

let kNotificationRegistrationIsPending = "NotificationRegistrationIsPending"

enum VerificationLoadFrom: Int{
    case forgotPassword
    case login
    case signup
    case joinStudy
}

class VerificationViewController: UIViewController{
    
    @IBOutlet var buttonContinue: UIButton?
    @IBOutlet var buttonResendEmail: UIButton?
    @IBOutlet var labelVerificationMessage: UILabel?
    @IBOutlet var textFieldEmail: UITextField?
    @IBOutlet var textFieldVerificationCode: UITextField?
    
    var labelMessage: String?
    var isFromForgotPassword: Bool =  false
    var emailId: String?
    var shouldCreateMenu: Bool = true
    var viewLoadFrom: VerificationLoadFrom = .signup
    
    
// MARK:- View Controllere Lifecycle
    override func viewDidLoad() {
        super.viewDidLoad()
        
        //Used to set border color for bottom view
        buttonContinue?.layer.borderColor = kUicolorForButtonBackground
        self.title = NSLocalizedString("", comment: "")
        
        if labelMessage != nil {
            labelVerificationMessage?.text = labelMessage
        }
        
        textFieldEmail?.text = self.emailId!
        
        //hide navigationbar
        self.navigationController?.setNavigationBarHidden(true, animated: true)
       
    }
    
    override func viewWillAppear(_ animated: Bool) {
        super.viewWillAppear(animated)
        
        
    }
    
    override func viewDidAppear(_ animated: Bool) {
        super.viewDidAppear(animated)
        
        
    }
    
    
// MARK:- Button Actions
 
    /**
     
     Navigate to previous screen 
     
     @param sender  accepts any object
     
     */
    @IBAction func buttonActionBack(_ sender: UIButton){
        _ = self.navigationController?.popViewController(animated: true)
    }
    
    
    /**
     
     Used to send the verification mail to regestered mail id
     
     @param sender  accepts anyy object
     
     */
    @IBAction func continueTwoButtonAction( _ sender: UIButton) {
        
        self.view.endEditing(true)
        
        if self.textFieldVerificationCode?.text == "" {
            self.showAlertMessages(textMessage: kMessageVerificationCodeEmpty)
            
        } else {
            print("Call the webservice")
            
            UserServices().verifyEmail(emailId: self.emailId!,  verificationCode:(self.textFieldVerificationCode?.text)! , delegate: self)
            
        }
    }
    
    
    /**
     
     Send the verification mail id to regestered 
     
     @param sender  accepts any object
     
     */
    @IBAction func continueButtonAction(_ sender: Any) {
        
        if (textFieldVerificationCode?.text?.count)! > 0 {
             UserServices().verifyEmail(emailId: User.currentUser.emailId!,  verificationCode:(self.textFieldVerificationCode?.text)! , delegate: self)
        } else {
             self.showAlertMessages(textMessage: kMessageVerificationCodeEmpty)
        }
    }
    
    
    /**
     
     Resend the verification code to regestered mail id 
     
     @param sender  accepts UIButton object
     
     */
    @IBAction func resendEmailButtonAction(_ sender: UIButton){
        
        var finalEmail: String = User.currentUser.emailId!
        
        if viewLoadFrom == .forgotPassword {
            finalEmail = self.emailId!
        }
       
        if (finalEmail.isEmpty) || !(Utilities.isValidEmail(testStr: finalEmail)) {
             self.showAlertMessages(textMessage: kMessageValidEmail)
        } else {
             UserServices().resendEmailConfirmation(emailId: finalEmail, delegate: self)
        }
    }

    
// MARK:- Segue Methods
    override func prepare(for segue: UIStoryboardSegue, sender: Any?) {
        
        if let singupCompletion = segue.destination as? SignUpCompleteViewController {
           
            singupCompletion.shouldCreateMenu = self.shouldCreateMenu
            if self.viewLoadFrom == .joinStudy{
                singupCompletion.viewLoadFrom = .joinStudy
            }
            
        }
    }
    
    
// MARK:- Utility Methods

    /**
     
     Used to show the alert using Utility
     
     @param textMessage     The message which is used to display in alert
     
     */
    func showAlertMessages(textMessage: String){
        UIUtilities.showAlertMessage("", errorMessage: NSLocalizedString(textMessage, comment: ""), errorAlertActionTitle: NSLocalizedString("OK", comment: ""), viewControllerUsed: self)
    }

    
    /**
     
     Navigate to Sign up completion screen 
     
     */
    func navigateToSignUpCompletionStep(){
        self.performSegue(withIdentifier: kSignupCompletionSegue, sender: nil)
    }
    
    
    /**
     
     Navigate to change password screen 
     
     */
    func navigateToChangePasswordViewController(){
        
        let storyboard = UIStoryboard(name: kStoryboardIdentifierGateway, bundle: nil)
        
        let fda = storyboard.instantiateViewController(withIdentifier: kChangePasswordViewControllerIdentifier) as! ChangePasswordViewController
        
        if shouldCreateMenu {
            fda.viewLoadFrom = .login
        } else {
            fda.viewLoadFrom = .menu_login
        }
        
        self.navigationController?.pushViewController(fda, animated: true)
    }
}


// MARK:- TextField Delegates
extension VerificationViewController:UITextFieldDelegate{
    
    func textField(_ textField: UITextField, shouldChangeCharactersIn range: NSRange, replacementString string: String) -> Bool {
        
        let finalString = textField.text! + string
        
        if string == " " || finalString.count > 255 {
            return false
        } else {
            return true
        }
    }
    
    func textFieldDidEndEditing(_ textField: UITextField) {
        print(textField.text!)
       
        if textField == textFieldEmail {
             User.currentUser.emailId = textField.text
            
        } else {
            
        }
    }
}


// MARK:- Webservice Delegates
extension VerificationViewController: NMWebServiceDelegate {
    
    func startedRequest(_ manager: NetworkManager, requestName: NSString) {
        
        self.addProgressIndicator()
        Logger.sharedInstance.info("requestname : \(requestName)")
    }
    
    func finishedRequest(_ manager: NetworkManager, requestName: NSString, response: AnyObject?) {
        Logger.sharedInstance.info("requestname : \(requestName)")
        
        self.removeProgressIndicator()
        
        if User.currentUser.verified == true{
            
            let ud = UserDefaults.standard
            ud.set(true, forKey: kNotificationRegistrationIsPending)
            ud.synchronize()
            
            if viewLoadFrom == .forgotPassword{
                //pop to login
                
                self.performSegue(withIdentifier: "signInUnwindSegue", sender: self)
            } else if viewLoadFrom == .joinStudy {
                
                let appDelegate = (UIApplication.shared.delegate as? AppDelegate)!
                appDelegate.checkPasscode(viewController: self)
                
                self.navigateToSignUpCompletionStep()
            } else {
                let appDelegate = (UIApplication.shared.delegate as? AppDelegate)!
                appDelegate.checkPasscode(viewController: self)
                
                self.navigateToSignUpCompletionStep()
            }
        } else {
            
            if requestName as String == RegistrationMethods.resendConfirmation.description {
                UIUtilities.showAlertWithTitleAndMessage(title: NSLocalizedString(kAlertMessageText, comment: "") as NSString, message:NSLocalizedString(kAlertMessageResendEmail, comment: "") as NSString)
            } else {
                UIUtilities.showAlertWithTitleAndMessage(title: NSLocalizedString(kAlertMessageText, comment: "") as NSString, message: NSLocalizedString(kAlertMessageVerifyEmail, comment: "") as NSString)
            }
        }
    }
    
    func failedRequest(_ manager: NetworkManager, requestName: NSString, error: NSError) {
        Logger.sharedInstance.info("requestname : \(requestName)")
        
        self.removeProgressIndicator()
        if error.code == 403 { //unauthorized
            UIUtilities.showAlertMessageWithActionHandler(kErrorTitle, message: error.localizedDescription, buttonTitle: kTitleOk, viewControllerUsed: self, action: {
                _ = self.navigationController?.popToRootViewController(animated: true)
            })
        } else {
            
            UIUtilities.showAlertWithTitleAndMessage(title: NSLocalizedString(kErrorTitle, comment: "") as NSString, message: error.localizedDescription as NSString)
        }
    }
}

// MARK:- ORKTaskViewController Delegate
extension VerificationViewController: ORKTaskViewControllerDelegate{
    
    func taskViewControllerSupportsSaveAndRestore(_ taskViewController: ORKTaskViewController) -> Bool {
        return true
    }
    
    public func taskViewController(_ taskViewController: ORKTaskViewController, didFinishWith reason: ORKTaskViewControllerFinishReason, error: Error?) {
        
        //var taskResult: Any?
        
        switch reason {
            
        case ORKTaskViewControllerFinishReason.completed:
            print("completed")
            //taskResult = taskViewController.result
        case ORKTaskViewControllerFinishReason.failed:
            print("failed")
            //taskResult = taskViewController.result
        case ORKTaskViewControllerFinishReason.discarded:
            print("discarded")
            
            //taskResult = taskViewController.result
        case ORKTaskViewControllerFinishReason.saved:
            print("saved")
            //taskResult = taskViewController.restorationData
            
        }
        taskViewController.dismiss(animated: true, completion: nil)
    }
    
    func taskViewController(_ taskViewController: ORKTaskViewController, stepViewControllerWillAppear stepViewController: ORKStepViewController) {
        
    }
}


