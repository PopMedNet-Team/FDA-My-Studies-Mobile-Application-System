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


let kLabelText = "LabelName"
let kToggleValue = "ToggleValue"

enum ProfileTableViewCellType: Int {
    case usePasscode = 3
    case useTouchId = 6
    case receivePushNotifications = 4
    case receiveStudyActivityReminder = 5
}


class ProfileTableViewCell: UITableViewCell {
    
    @IBOutlet var labelName: UILabel?
    @IBOutlet var switchToggle: UISwitch?
   
    
    override func awakeFromNib() {
        super.awakeFromNib()
        // Initialization code
    }
    
    @IBAction func switchValueChanged(sender: UISwitch) {
        
    }
    
    
    /**
     Used to set default values
     @param dict    holds the dictionary of default values
     */
    func setCellData(dict: NSDictionary ){
        
        self.labelName?.text = NSLocalizedString((dict[kLabelText] as? String)!, comment: "")
        
    }
    

    /**
     Used to set toggle value for switch
     @param toggleValue    switch Value
     */
    func setToggleValue(indexValue: Int)  {
        
        let user = User.currentUser
        switch ProfileTableViewCellType(rawValue: indexValue)! as ProfileTableViewCellType{
        case .usePasscode:
            if Utilities.isValidValue(someObject: user.settings?.passcode as AnyObject?){
                self.switchToggle?.isOn = (user.settings?.passcode)!
                
            }
            else{
                 self.switchToggle?.isOn =  false
                
            }
        case .useTouchId:
            if Utilities.isValidValue(someObject: user.settings?.touchId as AnyObject?){
                self.switchToggle?.isOn = (user.settings?.touchId)!
            }
            else{
                self.switchToggle?.isOn =  false
            }
        case .receivePushNotifications:
            if Utilities.isValidValue(someObject: user.settings?.remoteNotifications as AnyObject?){
                self.switchToggle?.isOn = (user.settings?.remoteNotifications)!
            }
            else{
                self.switchToggle?.isOn =  false
            }
        case .receiveStudyActivityReminder:
            if Utilities.isValidValue(someObject: user.settings?.localNotifications as AnyObject?){
                self.switchToggle?.isOn = (user.settings?.localNotifications)!
            }
            else{
                self.switchToggle?.isOn =  false
            }
        }
    }
    
    override func setSelected(_ selected: Bool, animated: Bool) {
        super.setSelected(selected, animated: animated)
        
    }
}


