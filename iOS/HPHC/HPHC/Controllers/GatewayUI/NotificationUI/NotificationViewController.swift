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


class NotificationViewController: UIViewController {
    
    @IBOutlet var tableView: UITableView?
    var notificationArray: Array<Any> = []
    
    
// MARK:- ViewController LifeCycle
    
    override func viewDidLoad() {
        super.viewDidLoad()
        
        self.title = NSLocalizedString(kNotificationsTitleText, comment: "")
        
        self.loadLocalNotification()
        WCPServices().getNotification(skip: 0, delegate: self)
    }
    
    override func viewWillAppear(_ animated: Bool) {
        super.viewWillAppear(animated)
        self.addBackBarButton()
        self.navigationController?.setNavigationBarHidden(false, animated: true)
        
        let ud = UserDefaults.standard
        ud.set(false, forKey: kShowNotification)
        ud.synchronize()
    }
    
    override func viewDidAppear(_ animated: Bool) {
        super.viewDidAppear(animated)
    }

// MARK:- Helper Methods
    
    func handleNotificationListResponse() {
        if (Gateway.instance.notification?.count)! > 0{
            self.loadNotificationFromDatabase()
           // self.tableView?.isHidden = false
            
        }else {
          // self.tableView?.isHidden = true
            self.tableView?.isHidden = false
            self.tableView?.reloadData()
        }
    }
    
    func loadNotificationFromDatabase() {
        
        DBHandler.loadNotificationListFromDatabase(completionHandler: {(notificationList) in
            
            if notificationList.count > 0 {
               // self.tableView?.isHidden = false
                
                for notification in notificationList {
                    
                    //filter notification
                    if notification.type == AppNotification.NotificationType.Study {
                        
                        let study = Gateway.instance.studies?.filter({$0.studyId == notification.studyId}).last
                        if study != nil && self.isUserJoined(study: study!) {
                            self.notificationArray.append(notification)
                        }
                        
                    }else {
                         self.notificationArray.append(notification)
                    }
                }
              
                //Sort Notification according to sort date
              let sorted = self.notificationArray.sorted(by: { (first, second) -> Bool in
                
                let date1: Date!
                let date2: Date!
                if first is AppLocalNotification {
                  date1 = (first as! AppLocalNotification).startDate
                  
                }else {
                  date1 = (first as! AppNotification).date
                }
                
                if second is AppLocalNotification {
                  date2 = (second as! AppLocalNotification).startDate
                    
                }else {
                  date2 = (second as! AppNotification).date
                }
               return date1 > date2
                
              })
              self.notificationArray = sorted
              self.tableView?.isHidden = false
              self.tableView?.reloadData()
                
            }else {
                self.tableView?.isHidden = false
                self.tableView?.reloadData()
            }
        })
    }
    
    func loadLocalNotification(){
        
        DBHandler.getLocalNotification { (notificationList) in
            if notificationList.count > 0 {
                self.notificationArray = notificationList
            }
        }
        
    }

    
    /**
     Used to check the Study State
     @param study    Access the data from Study class
     @return Bool
     */
    class func checkForStudyState(study: Study) -> Bool {
        
        let currentStudy = study
        let participatedStatus = (currentStudy.userParticipateState.status)
        
        switch currentStudy.status {
        case .Active:
            if participatedStatus == .inProgress {
                return true
            }else {
                 UIUtilities.showAlertWithTitleAndMessage(title: "", message: NSLocalizedString("Please join study to go forward.", comment: "") as NSString)
            }
        case .Upcoming:
            UIUtilities.showAlertWithTitleAndMessage(title: "", message: NSLocalizedString(kMessageForStudyUpcomingState, comment: "") as NSString)
            
        case .Paused:
            UIUtilities.showAlertWithTitleAndMessage(title: "", message: NSLocalizedString(kMessageForStudyPausedState, comment: "") as NSString)
            
        case .Closed:
            UIUtilities.showAlertWithTitleAndMessage(title: "", message: NSLocalizedString(kMessageForStudyClosedState, comment: "") as NSString)
            
        }
        
        return false
    }
    
     func isUserJoined(study: Study) -> Bool {
        
        let currentStudy = study
        let participatedStatus = (currentStudy.userParticipateState.status)
        if participatedStatus == .inProgress {
            return true
        }
        return false
    }
    
    
    class func checkForStudyStateAndParticiapantState(study: Study) -> Bool {
        
        let currentStudy = study
        let participatedStatus = (currentStudy.userParticipateState.status)
        
        switch currentStudy.status {
        case .Active:
            if participatedStatus == .inProgress {
                return true
            }else {
               return false
            }
        case .Upcoming:
           return false
        case .Paused:
            return false
        case .Closed:
            return false
            
        }
        
        //return false
    }
    
    /**
     Used to push the screen to Study Dashboard
     @param type    Access data from AppNotification class and NotificationSubType Enum
     */
    func pushToStudyDashboard(type: AppNotification.NotificationSubType?) {
        
        let viewController: StudyDashboardTabbarViewController?
        let storyboard = UIStoryboard(name: kStudyStoryboard, bundle: nil)
        if type != nil {
            
            self.navigationController?.setNavigationBarHidden(true, animated: true)
            
            viewController = storyboard.instantiateViewController(withIdentifier: kStudyDashboardTabbarControllerIdentifier) as? StudyDashboardTabbarViewController
            
            switch type! as  AppNotification.NotificationSubType{
            case .Study:
                
                viewController?.selectedIndex = 0
                self.navigationController?.pushViewController(viewController!, animated: true)
            case .Resource:
                
                viewController?.selectedIndex = 2
                self.navigationController?.pushViewController(viewController!, animated: true)
            case .Activity:
                
                viewController?.selectedIndex = 0
                self.navigationController?.pushViewController(viewController!, animated: true)
            default: break
                
            }
        }
    }
}

// MARK:- TableView Datasource
extension NotificationViewController: UITableViewDataSource {
    
    func tableView(_ tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
        return notificationArray.count
    }
    
    func tableView(_ tableView: UITableView, cellForRowAt indexPath: IndexPath) -> UITableViewCell {
        var cell: NotificationTableViewCell?
        
        cell = tableView.dequeueReusableCell(withIdentifier: kNotificationTableViewCellIdentifier , for: indexPath) as? NotificationTableViewCell
        
        cell?.populateCellWith(notification: (notificationArray[indexPath.row]))
        cell?.backgroundColor = UIColor.clear
        return cell!
    }
}

// MARK:- TableView Delegates
extension NotificationViewController: UITableViewDelegate{
    
    func tableView(_ tableView: UITableView, didSelectRowAt indexPath: IndexPath) {
        tableView.deselectRow(at: indexPath, animated: true)
      
      
        let appNotification = notificationArray[indexPath.row]
        let appNotif = appNotification as! AppNotification
        if appNotif.type == AppNotification.NotificationType.Study {
        
        if Utilities.isValidValue(someObject: appNotif.studyId as AnyObject?) {
            
            let study = Gateway.instance.studies?.filter({$0.studyId == appNotif.studyId}).last
            
            if self.isUserJoined(study: study!) && study?.status == .Active {
                
                Study.updateCurrentStudy(study: study! )
                self.pushToStudyDashboard(type: appNotif.subType )
            }
        }
        }
    }
}


// MARK:- WebService Delegate
extension NotificationViewController: NMWebServiceDelegate {
    
    func startedRequest(_ manager: NetworkManager, requestName: NSString) {
        Logger.sharedInstance.info("requestname : \(requestName)")
        self.addProgressIndicator()
    }
    
    func finishedRequest(_ manager: NetworkManager, requestName: NSString, response: AnyObject?) {
        self.removeProgressIndicator()
        
        if requestName as String == WCPMethods.notifications.method.methodName {
           self.handleNotificationListResponse()
        }
    }
 
    func failedRequest(_ manager: NetworkManager, requestName: NSString, error: NSError) {
        Logger.sharedInstance.info("requestname : \(requestName)")
        self.removeProgressIndicator()
        
    }
}


