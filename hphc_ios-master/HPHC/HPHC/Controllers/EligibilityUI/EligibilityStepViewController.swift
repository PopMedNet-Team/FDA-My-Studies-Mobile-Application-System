/*
 License Agreement for FDA My Studies
 Copyright © 2017-2018 Harvard Pilgrim Health Care Institute (HPHCI) and its Contributors.
 Permission is hereby granted, free of charge, to any person obtaining a copy of this software and
 associated documentation files (the "Software"), to deal in the Software without restriction, including
 without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies
 of the Software, and to permit persons to whom the Software is furnished to do so, subject to the
 following conditions:
 
 The above copyright notice and this permission notice shall be included in all copies or substantial
 portions of the Software.
 
 Funding Source: Food and Drug Administration (“Funding Agency”) effective 18 September 2014 as Contract no. HHSF22320140030I/HHSF22301006T (the “Prime Contract”).
 
 THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 FITNESS FOR A PARTICULAR PURPOSE AND NON-INFRINGEMENT. IN NO EVENT SHALL
 THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR
 OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE,
 ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR
 OTHER DEALINGS IN THE SOFTWARE.
 */

import UIKit
import ResearchKit
import IQKeyboardManagerSwift

let kStudyWithStudyId = "Study with StudyId"
let kTitleOK = "OK"

class EligibilityStep: ORKStep {
    var type: String?
    
    func showsProgress() -> Bool {
        return false
    }
}


class EligibilityStepViewController: ORKStepViewController {
    
    @IBOutlet weak var tokenTextField: UITextField!
    @IBOutlet weak var buttonSubmit: UIButton?
    @IBOutlet weak var labelDescription: UILabel?
    var descriptionText: String?
    
    var taskResult: EligibilityTokenTaskResult = EligibilityTokenTaskResult(identifier: kFetalKickCounterStepDefaultIdentifier)
    
    // MARK: ORKStepViewController Intitialization Methods
    
    override init(step: ORKStep?) {
        super.init(step: step)
    }
    
    required init?(coder aDecoder: NSCoder) {
        super.init(coder: aDecoder)
    }
    
    override func hasNextStep() -> Bool {
        super.hasNextStep()
        return true
    }
    
    override func goForward(){
        
        super.goForward()
        
    }
    
    override var result: ORKStepResult? {
        
        let orkResult = super.result
        orkResult?.results = [self.taskResult]
        return orkResult
        
    }
    
    override func viewDidLoad() {
        super.viewDidLoad()
        
        buttonSubmit?.layer.borderColor =   kUicolorForButtonBackground
        
        if (self.descriptionText?.characters.count)! > 0 {
            labelDescription?.text = self.descriptionText
        }
        
        if let step = step as? EligibilityStep {
            step.type = "token"
        }
        
        IQKeyboardManager.sharedManager().enable = true
    }
    
    // MARK: Methods and Button Actions
    
    func showAlert(message: String) {
        let alert = UIAlertController(title: kErrorTitle as String,message: message as String,preferredStyle: UIAlertControllerStyle.alert)
        alert.addAction(UIAlertAction(title: NSLocalizedString(kTitleOK, comment: ""), style: .default, handler: nil))
        
        self.navigationController?.present(alert, animated: true, completion: nil)
    }
    
    @IBAction func buttonActionSubmit(sender: UIButton?) {
        
        self.view.endEditing(true)
        let token = tokenTextField.text
        
        if (token?.isEmpty) == false {
            
            LabKeyServices().verifyEnrollmentToken(studyId: (Study.currentStudy?.studyId)!, token: token!, delegate: self)
        }else {
            self.showAlert(title: kTitleMessage, message: kMessageValidToken )
            
        }
    }
    
    override func didReceiveMemoryWarning() {
        super.didReceiveMemoryWarning()
        // Dispose of any resources that can be recreated.
    }
}

// MARK: TextField Delegates
extension EligibilityStepViewController: UITextFieldDelegate {
    func textFieldDidEndEditing(_ textField: UITextField) {
    }
    func textFieldShouldBeginEditing(_ textField: UITextField) -> Bool {
        return true
    }
    
    func textField(_ textField: UITextField, shouldChangeCharactersIn range: NSRange, replacementString string: String) -> Bool {
        
        if string == " "{
            return false
            
        }else {
            return true
        }
    }
    
    func textFieldDidBeginEditing(_ textField: UITextField) {
    }
    
}

// MARK: Webservice Delegates
extension EligibilityStepViewController: NMWebServiceDelegate {
    func startedRequest(_ manager: NetworkManager, requestName: NSString) {
        Logger.sharedInstance.info("requestname : \(requestName)")
        
        self.addProgressIndicator()
        
    }
    
    func finishedRequest(_ manager: NetworkManager, requestName: NSString, response: AnyObject?) {
        Logger.sharedInstance.info("requestname : \(requestName)")
        
        self.removeProgressIndicator()
        
        if (tokenTextField.text?.isEmpty) == false {
            self.taskResult.enrollmentToken = tokenTextField.text!
            //Storing token so that it can be used in case of ineligibility
            let appdelegate = UIApplication.shared.delegate as! AppDelegate
            appdelegate.consentToken = tokenTextField.text!
            
        }else {
            self.taskResult.enrollmentToken = ""
        }
        
        
        self.goForward()
    }
    
    func failedRequest(_ manager: NetworkManager, requestName: NSString, error: NSError) {
        Logger.sharedInstance.info("requestname : \(requestName)")
        
        self.removeProgressIndicator()
        if error.localizedDescription.localizedCaseInsensitiveContains(tokenTextField.text!) {
            
            self.showAlert(message: kMessageInvalidTokenOrIfStudyDoesNotExist) //kMessageForInvalidToken
            
        }else {
            if error.localizedDescription.localizedCaseInsensitiveContains(kStudyWithStudyId) {
                
                self.showAlert(message: kMessageInvalidTokenOrIfStudyDoesNotExist) //kMessageForMissingStudyId
                
            }else {
                self.showAlert(message: error.localizedDescription)
            }
        }
    }
}

// MARK: ORKResult overriding

open class EligibilityTokenTaskResult: ORKResult {
    open var enrollmentToken: String = ""
    
    override open var description: String {
        get {
            return "enrollmentToken:\(enrollmentToken)"
        }
    }
    
    override open var debugDescription: String {
        get {
            return "enrollmentToken:\(enrollmentToken)"
        }
    }
}

