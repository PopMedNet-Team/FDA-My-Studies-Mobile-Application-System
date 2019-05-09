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

class NotificationTableViewCell: UITableViewCell {
    
    @IBOutlet var labelNotificationText: UILabel?
    @IBOutlet var labelNotificationTime: UILabel?
    
    override func awakeFromNib() {
        super.awakeFromNib()
        // Initialization code
    }
    
    override func setSelected(_ selected: Bool, animated: Bool) {
        super.setSelected(selected, animated: animated)
        
    }
    
    /**
     Used to populate cell data
     @param appNotification    Access the data from AppNotification class
     */
    func populateCellWith(notification: Any?) {
        
        if notification is AppNotification {
            let appNotification = notification as! AppNotification
            if appNotification.date != nil {
                self.labelNotificationTime?.text = NotificationTableViewCell.formatter.string(from: (appNotification.date)!)
                
                if appNotification.message != nil{
                    labelNotificationText?.text =  appNotification.message!
                    
                }else {
                    labelNotificationText?.text = ""
                }
            }else {
                
                let appNotification = notification as! AppLocalNotification
                self.labelNotificationTime?.text = NotificationTableViewCell.formatter.string(from: (appNotification.startDate)!)
                
                if appNotification.message != nil{
                    labelNotificationText?.text =  appNotification.message!
                    
                }else {
                    labelNotificationText?.text = ""
                }
            }
            
        }else {
            let appNotification = notification as! AppLocalNotification
            self.labelNotificationTime?.text = NotificationTableViewCell.formatter.string(from: (appNotification.startDate)!)
            
            if Utilities.isValidValue(someObject: appNotification.message! as AnyObject?){
                labelNotificationText?.text =  appNotification.message!
                
            }else {
                labelNotificationText?.text = ""
            }
        }
    }
    
    private static let formatter: DateFormatter = {
        let formatter = DateFormatter()
        formatter.dateFormat = "MMM dd YYYY"
        formatter.timeZone = TimeZone.init(abbreviation: "GMT")
        return formatter
    }()
}
