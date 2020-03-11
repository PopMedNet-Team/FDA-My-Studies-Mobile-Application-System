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

/* Contact us field description*/
struct ContactUsFeilds {
    
    static var firstName: String = ""
    static var email: String = ""
    static var subject: String = ""
    static var message: String = ""
    
  init(){
        ContactUsFeilds.firstName = ""
        ContactUsFeilds.email = ""
        ContactUsFeilds.subject = ""
        ContactUsFeilds.message = ""
    }
}

class ContactUsViewController: UIViewController{
    
    var tableViewRowDetails: NSMutableArray?
    
    @IBOutlet var buttonSubmit: UIButton?
    @IBOutlet var tableView: UITableView?
    @IBOutlet var tableViewFooter: UIView?
    @IBOutlet var feedbackTextView: UITextView?
    var previousContentHeight: Double = 0.0
    
    
// MARK:- ViewController LifeCycle
    override func viewDidLoad() {
        super.viewDidLoad()
        
        self.navigationItem.title =  NSLocalizedString("CONTACT US", comment: "")
        
        //Used to set border color for bottom view
        buttonSubmit?.layer.borderColor = kUicolorForButtonBackground
        
        //Automatically takes care  of text field become first responder and scroll of tableview
        // IQKeyboardManager.sharedManager().enable = true
        
        //load plist info
        let plistPath = Bundle.main.path(forResource: "ContactUs", ofType: ".plist", inDirectory: nil)
        tableViewRowDetails = NSMutableArray.init(contentsOfFile: plistPath!)
        
        self.addBackBarButton()
        
        self.tableView?.estimatedRowHeight = 62
        self.tableView?.rowHeight = UITableView.automaticDimension
        
        //Used for background tap dismiss keyboard
        let tapGestureRecognizer: UITapGestureRecognizer = UITapGestureRecognizer.init(target: self, action: #selector(ContactUsViewController.handleTapGesture))
        self.tableView?.addGestureRecognizer(tapGestureRecognizer)
         _ = ContactUsFeilds.init()
    }
    
    override func viewWillAppear(_ animated: Bool) {
        super.viewWillAppear(animated)
       
    }
    
    override func viewDidAppear(_ animated: Bool) {
        super.viewDidAppear(animated)
        
    }
    
    override func viewWillDisappear(_ animated: Bool) {
        super.viewWillDisappear(animated)
        
    }
    
    @objc func handleTapGesture(gesture: UIGestureRecognizer){
        
        let location = gesture.location(in: gesture.view)
        if location.y > 245 {
            let ip = IndexPath.init(row: 3, section: 0)
            let cell = self.tableView?.cellForRow(at: ip) as! TextviewCell
            cell.textView?.becomeFirstResponder()

        }
        
    }
    
// MARK:- Button Actions
    
    /**
     
     Validations after clicking on submit button
     If all the validations satisfy send contact-us request
     
     @param sender  accepts any object
     
     */
    @IBAction func buttonSubmitAciton(_ sender: UIButton){
        print("\(ContactUsFeilds.firstName)")
        
        if (ContactUsFeilds.firstName.isEmpty && ContactUsFeilds.email.isEmpty && ContactUsFeilds.subject.isEmpty && ContactUsFeilds.message.isEmpty){
            
            UIUtilities.showAlertWithMessage(alertMessage: NSLocalizedString(kMessageAllFieldsAreEmpty, comment: ""))
        }
        else if ContactUsFeilds.firstName.isEmpty {
            UIUtilities.showAlertWithMessage(alertMessage: NSLocalizedString(kMessageFirstNameBlank, comment: ""))
        }
        else if ContactUsFeilds.email.isEmpty {
            UIUtilities.showAlertWithMessage(alertMessage: NSLocalizedString(kMessageEmailBlank,comment:""))
        }
        else if ContactUsFeilds.subject.isEmpty {
            UIUtilities.showAlertWithMessage(alertMessage: NSLocalizedString(kMessageSubjectBlankCheck, comment: ""))
        }
        else if ContactUsFeilds.message.isEmpty {
            UIUtilities.showAlertWithMessage(alertMessage: NSLocalizedString(kMessageMessageBlankCheck, comment: ""))
        }
        else if !(Utilities.isValidEmail(testStr: ContactUsFeilds.email)){
            UIUtilities.showAlertWithMessage(alertMessage: NSLocalizedString(kMessageValidEmail, comment: ""))
        }
        else {
            WCPServices().sendUserContactUsRequest(delegate: self)
        }
    }
}


// MARK:- TableView Datasource
extension ContactUsViewController: UITableViewDataSource{
    
    func tableView(_ tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
        return 4
    }
    
    func tableView(_ tableView: UITableView, cellForRowAt indexPath: IndexPath) -> UITableViewCell {
       
        let cell = UITableViewCell()
        if indexPath.row == 3 {
            
            let cell = tableView.dequeueReusableCell(withIdentifier: "textviewCell", for: indexPath) as! TextviewCell
            return cell
        }
        else {
            
            let tableViewData = tableViewRowDetails?.object(at: indexPath.row) as! NSDictionary
            
            let cell = tableView.dequeueReusableCell(withIdentifier: kContactUsTableViewCellIdentifier, for: indexPath) as! ContactUsTableViewCell
            
            cell.textFieldValue?.tag = indexPath.row
            
            var keyBoardType: UIKeyboardType? =  UIKeyboardType.default
            let textFieldTag = ContactTextFieldTags(rawValue: indexPath.row)!
            
            //Cell ContactTextField data setup
            switch  textFieldTag {
            case .FirstName , .Subject:
                keyBoardType = .default
            case .Email :
                cell.textFieldValue?.text = User.currentUser.emailId!
                ContactUsFeilds.email = User.currentUser.emailId!
                keyBoardType = .emailAddress
            }
            
            //Cell Data Setup
            cell.populateCellData(data: tableViewData,keyboardType: keyBoardType)
            
            cell.backgroundColor = UIColor.clear
            return cell
        }
        //return cell
    }
}


// MARK:- TableView Delegates
extension ContactUsViewController: UITableViewDelegate{
    
    func tableView(_ tableView: UITableView, didSelectRowAt indexPath: IndexPath) {
        tableView.deselectRow(at: indexPath, animated: true)
    }
}


// MARK:- TextView Delegates
extension ContactUsViewController: UITextViewDelegate {

    func textViewDidChange(_ textView: UITextView) {
        let currentOffset = tableView?.contentOffset
        UIView.setAnimationsEnabled(false)
        tableView?.beginUpdates()
        tableView?.endUpdates()
        UIView.setAnimationsEnabled(true)
        tableView?.setContentOffset(currentOffset!, animated: false)
    }
    func textViewDidEndEditing(_ textView: UITextView) {
        print("textViewDidEndEditing")
        if textView.tag == 101 && textView.text.count == 0 {
            textView.text = kMessageTextViewPlaceHolder
            textView.textColor = UIColor.lightGray
            textView.tag = 100
        }
        else {
            ContactUsFeilds.message = textView.text!
        }
    }
    func textViewDidBeginEditing(_ textView: UITextView) {
         print("textViewDidBeginEditing")
        
        if textView.tag == 100 {
            textView.text = ""
            textView.textColor = UIColor.black
            textView.tag = 101
        }
    }
}


// MARK:- Textfield Delegate
extension ContactUsViewController: UITextFieldDelegate{
    
    func textFieldDidBeginEditing(_ textField: UITextField) {
        
        
    }
    
    func textField(_ textField: UITextField, shouldChangeCharactersIn range: NSRange, replacementString string: String) -> Bool {
        
        let tag: ContactTextFieldTags = ContactTextFieldTags(rawValue: textField.tag)!
        let finalString = textField.text! + string
      
        
        if string == " " {
            return false
        }
        
        if  tag == .Email {
            if string == " " || finalString.count > 255{
                return false
            }
            else{
                return true
            }
        }
        return true
    }
    
    func textFieldDidEndEditing(_ textField: UITextField) {
        print(textField.text!)
        
        textField.text =  textField.text?.trimmingCharacters(in: CharacterSet.whitespacesAndNewlines)
        
        let tag: ContactTextFieldTags = ContactTextFieldTags(rawValue: textField.tag)!
        
        switch tag {
           
        case .Email:
            ContactUsFeilds.email = textField.text!
            break
            
        case .FirstName:
            ContactUsFeilds.firstName = textField.text!
            break
            
        case .Subject:
            ContactUsFeilds.subject = textField.text!
            break
            
//        default:
//            print("No Matching data Found")
//            break
        }
    }
}


// MARK:- Tableview cell class initialization
class TextviewCell: UITableViewCell{
    
    @IBOutlet var labelTitle: UILabel?
    @IBOutlet var textView : UITextView?
    
    
    override func awakeFromNib() {
        super.awakeFromNib()
        // Initialization code
    }
    
    override func setSelected(_ selected: Bool, animated: Bool) {
        super.setSelected(selected, animated: animated)
        // Configure the view for the selected state
    }
}


// MARK:- Webservice Delegates
extension ContactUsViewController: NMWebServiceDelegate {
    func startedRequest(_ manager: NetworkManager, requestName: NSString) {
        
        Logger.sharedInstance.info("requestname : \(requestName)")
        self.addProgressIndicator()
    }
    func finishedRequest(_ manager: NetworkManager, requestName: NSString, response: AnyObject?) {
        Logger.sharedInstance.info("requestname : \(requestName)")
        self.removeProgressIndicator()
        
        UIUtilities.showAlertMessageWithActionHandler("", message: NSLocalizedString(kMessageContactedSuccessfuly, comment: ""), buttonTitle: kTitleOk, viewControllerUsed: self) {
            _ = self.navigationController?.popViewController(animated: true)
        }
        
    }
    func failedRequest(_ manager: NetworkManager, requestName: NSString, error: NSError) {
        
        self.removeProgressIndicator()
        Logger.sharedInstance.info("requestname : \(requestName)")
        UIUtilities.showAlertWithTitleAndMessage(title: NSLocalizedString("Error", comment: "") as NSString, message: error.localizedDescription as NSString)
    }
}

