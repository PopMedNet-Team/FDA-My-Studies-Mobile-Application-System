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

import IQKeyboardManagerSwift
import UIKit

let kHelperTextForFilteredStudiesNotFound = "Sorry, no Studies found. Please try different Filter Options"
let kHelperTextForSearchedStudiesNotFound = "Sorry, no Studies found. Please check the spelling or try a different search."

let kHelperTextForOffline = "Sorry, no studies available right now. Please remain signed in to get notified when there are new studies available."

let kNotificationViewControllerIdentifier = "NotificationViewControllerIdentifier"

class StudyListViewController: UIViewController {
    
    // MARK:- Outlets
    
    @IBOutlet var tableView: UITableView?
    @IBOutlet var labelHelperText: UILabel!

    var refreshControl: UIRefreshControl? // for refreshing studylist

    var studyListRequestFailed = false
    var searchView: SearchBarView?

    var isComingFromFilterScreen: Bool = false
    var studiesList: [Study] = []

    var previousStudyList: [Study] = []

    var allStudyList: [Study] = [] // Gatewaystudylist
    override var preferredStatusBarStyle: UIStatusBarStyle {
        return .default
    }

    // MARK: - Viewcontroller lifecycle

    override func viewDidLoad() {
        super.viewDidLoad()

        var infoDict: NSDictionary?
        if let path = Bundle.main.path(forResource: "Info", ofType: "plist") {
            infoDict = NSDictionary(contentsOfFile: path)
        }
        let navTitle = infoDict!["ProductTitleName"] as! String
        let titleLabel = UILabel()
        titleLabel.text = NSLocalizedString(navTitle, comment: "")
        titleLabel.font = UIFont(name: "HelveticaNeue-Medium", size: 18)
        titleLabel.textAlignment = .left
        titleLabel.textColor = Utilities.getUIColorFromHex(0x007CBA)
        titleLabel.frame = CGRect(x: 0, y: 0, width: 300, height: 44)

        navigationItem.titleView = titleLabel

        // self.loadTestData()
        // get Profile data to check for passcode
        // Condition missing

        let appDelegate = (UIApplication.shared.delegate as? AppDelegate)!

        
        if User.currentUser.userType == .FDAUser, User.currentUser.settings?.localNotifications == true {
            /* Will come next next phase */
            //appDelegate.checkForAppReopenNotification()
        }

        isComingFromFilterScreen = false
        // IQKeyboardManager.sharedManager().enable = true

        refreshControl = UIRefreshControl()
        refreshControl?.attributedTitle = NSAttributedString(string: "Pull to refresh")
        refreshControl?.addTarget(self, action: #selector(refresh(sender:)), for: UIControl.Event.valueChanged)
        tableView?.addSubview(refreshControl!)
    }

    override func viewWillAppear(_: Bool) {
        if isComingFromFilterScreen {
            isComingFromFilterScreen = false
            return
        }
        addRightNavigationItem()
        let appdelegate = (UIApplication.shared.delegate as? AppDelegate)!

        Study.currentStudy = nil

        let ud = UserDefaults.standard
        var ispasscodePending: Bool? = false

        // Checking if User has missed out setting the passcode/TouchId
        if ud.value(forKey: kPasscodeIsPending) != nil {
            ispasscodePending = (ud.value(forKey: kPasscodeIsPending) as? Bool)!
        }

        if ispasscodePending == true {
            if User.currentUser.userType == .FDAUser {
                tableView?.isHidden = true
                // Fetch the User Profile
                UserServices().getUserProfile(self as NMWebServiceDelegate)
            }
        }

        labelHelperText.isHidden = true
        setNavigationBarItem()
        navigationController?.setNavigationBarHidden(false, animated: true)
        navigationController?.navigationBar.isHidden = false

        if User.currentUser.userType == .FDAUser { // For LoggedIn User
            tableView?.estimatedRowHeight = 145
            tableView?.rowHeight = UITableView.automaticDimension

            if (fdaSlideMenuController()?.isLeftOpen())! {
                // Do Nothing
            } else {
                // Fetch User Preferences
                sendRequestToGetUserPreference()
            }
        } else { // For ananomous User
            tableView?.estimatedRowHeight = 140
            tableView?.rowHeight = UITableView.automaticDimension
            // Fetch StudyList
            sendRequestToGetStudyList()
        }

        // UIApplication.shared.statusBarStyle = .default
        setNeedsStatusBarAppearanceUpdate()
        // Checking if registering notification is pending
        if ud.value(forKey: kNotificationRegistrationIsPending) != nil, ud.bool(forKey: kNotificationRegistrationIsPending) == true {
            appdelegate.askForNotification()
        }

        // Handling StudyList Request Failure condition
        if studyListRequestFailed {
            labelHelperText.isHidden = false
            labelHelperText.text = kHelperTextForOffline
        }
    }

    override func didReceiveMemoryWarning() {
        super.didReceiveMemoryWarning()
        // Dispose of any resources that can be recreated.
    }

    // MARK: - Helper Methods

    /**
     addRightNavigationItem method updates the navigation bar items for current controller , by adding Notification Button, Notification Indicator & Filter Button
     */
    func addRightNavigationItem() {
        let view = UIView(frame: CGRect(x: 0, y: 4, width: 110, height: 40))

        // Notification Button
        let button = addNotificationButton()
        view.addSubview(button)
        button.isExclusiveTouch = true

        // notification Indicator
        let label = addNotificationIndication()
        view.addSubview(label)

        let ud = UserDefaults.standard
        let showNotification = ud.bool(forKey: kShowNotification)

        if showNotification {
            label.isHidden = false
        } else {
            label.isHidden = true
        }

        //  filter Button
        let filterButton = addFilterButton()
        view.addSubview(filterButton)
        filterButton.isExclusiveTouch = true

        //  Search Button
        let searchButton = addSearchButton()
        view.addSubview(searchButton)
        searchButton.isExclusiveTouch = true

        let barButton = UIBarButtonItem(customView: view)
        navigationItem.rightBarButtonItems = [barButton]
    }

    func addSearchButton() -> UIButton {
        let searchButton = UIButton(type: .custom)
        searchButton.setImage(#imageLiteral(resourceName: "search_small"), for: UIControl.State.normal)
        searchButton.addTarget(self, action: #selector(searchButtonAction(_:)), for: .touchUpInside)
        searchButton.frame = CGRect(x: 0, y: 4, width: 30, height: 30)
        return searchButton
    }

    func addFilterButton() -> UIButton {
        let filterButton = UIButton(type: .custom)
        filterButton.setImage(#imageLiteral(resourceName: "filterIcon"), for: UIControl.State.normal)
        filterButton.addTarget(self, action: #selector(filterAction(_:)), for: .touchUpInside)
        filterButton.frame = CGRect(x: 40, y: 4, width: 30, height: 30)
        return filterButton
    }

    func addNotificationButton() -> UIButton {
        let button = UIButton(type: .custom)

        button.setImage(#imageLiteral(resourceName: "notification_grey"), for: UIControl.State.normal)
        button.addTarget(self, action: #selector(buttonActionNotification(_:)), for: .touchUpInside)
        button.frame = CGRect(x: 80, y: 4, width: 30, height: 30)
        return button
    }

    func addNotificationIndication() -> UILabel {
        let label = UILabel(frame: CGRect(x: 100, y: 4, width: 10, height: 10))
        label.font = UIFont.systemFont(ofSize: 10)
        label.textColor = UIColor.white

        label.textAlignment = NSTextAlignment.center
        label.backgroundColor = kUIColorForSubmitButtonBackground
        label.layer.cornerRadius = 5
        label.clipsToBounds = true
        label.text = ""
        return label
    }

    func checkIfNotificationEnabled() {
        var notificationEnabledFromAppSettings = false

        // checking the app settings
        let notificationType = UIApplication.shared.currentUserNotificationSettings!.types
        if notificationType == [] {
            print("notifications are NOT enabled")
        } else {
            print("notifications are enabled")
            notificationEnabledFromAppSettings = true
        }

        if (User.currentUser.settings?.remoteNotifications)!,
            (User.currentUser.settings?.localNotifications)!,
            notificationEnabledFromAppSettings { // Notifications are enabled
            // Do Nothing
        } else { // Notification is Disabled
            let ud = UserDefaults.standard
            let previousDate = ud.object(forKey: "NotificationRemainder") as? Date
            let todayDate = Date()
            var daysLastSeen = 0
            if previousDate != nil {
                daysLastSeen = Schedule().getNumberOfDaysBetween(startDate: previousDate!, endDate: todayDate)
            }

            if daysLastSeen >= 7 { // Notification is disabled for 7 or more Days
                UIUtilities.showAlertWithTitleAndMessage(title: NSLocalizedString("FDA My Studies", comment: "") as NSString, message: NSLocalizedString(kMessageAppNotificationOffRemainder, comment: "") as NSString)

                ud.set(Date(), forKey: "NotificationRemainder")
                ud.synchronize()
            }
        }
    }

    /**
     Used to load the test data from Studylist of type json
     */
    func loadTestData() {
        let filePath = Bundle.main.path(forResource: "StudyList", ofType: "json")
        let data = NSData(contentsOfFile: filePath!)

        do {
            let response = try JSONSerialization.jsonObject(with: data! as Data, options: []) as? [String: Any]

            let studies = (response?[kStudies] as? [[String: Any]])!
            var listOfStudies: [Study] = []
            for study in studies {
                let studyModelObj = Study(studyDetail: study)
                listOfStudies.append(studyModelObj)
            }

            // assgin to Gateway
            Gateway.instance.studies = listOfStudies

        } catch {
            print("json error: \(error.localizedDescription)")
        }
    }

    /**
     checkIfFetelKickCountRunning method verifies whether if FetalKick Task is Still running and calculate the time difference.
     */
    func checkIfFetelKickCountRunning() {
        let ud = UserDefaults.standard

        if ud.bool(forKey: "FKC"), ud.object(forKey: kFetalKickStartTimeStamp) != nil {
            let studyId = (ud.object(forKey: kFetalkickStudyId) as? String)!
            let study = Gateway.instance.studies?.filter { $0.studyId == studyId }.last

            if study?.userParticipateState.status == .inProgress, study?.status == .Active {
                Study.updateCurrentStudy(study: study!)
                pushToStudyDashboard(animated: false)
            }
        } else {
            checkIfNotificationEnabled()
            if NotificationHandler.instance.studyId.count > 0 {
                let studyId = NotificationHandler.instance.studyId
                let study = Gateway.instance.studies?.filter { $0.studyId == studyId }.first
                Study.updateCurrentStudy(study: study!)

                NotificationHandler.instance.studyId = ""
                performTaskBasedOnStudyStatus()
            }
        }
    }

    /**
     Navigate to notification screen
     */
    func navigateToNotifications() {
        let gatewayStoryBoard = UIStoryboard(name: kStoryboardIdentifierGateway, bundle: Bundle.main)
        let notificationController = (gatewayStoryBoard.instantiateViewController(withIdentifier: kNotificationViewControllerIdentifier) as? NotificationViewController)!
        navigationController?.pushViewController(notificationController, animated: true)
    }

    /**
     Navigate to StudyHomeViewController screen
     */
    func navigateToStudyHome() {
        let studyStoryBoard = UIStoryboard(name: kStudyStoryboard, bundle: Bundle.main)
        let studyHomeController = (studyStoryBoard.instantiateViewController(withIdentifier: String(describing: StudyHomeViewController.classForCoder())) as? StudyHomeViewController)!
        studyHomeController.delegate = self
        navigationController?.pushViewController(studyHomeController, animated: true)
    }

    /**
     Navigate the screen to Study Dashboard tabbar viewcontroller screen
     */
    func pushToStudyDashboard(animated: Bool = true) {
        let studyStoryBoard = UIStoryboard(name: kStudyStoryboard, bundle: Bundle.main)

        let studyDashboard = (studyStoryBoard.instantiateViewController(withIdentifier: kStudyDashboardTabbarControllerIdentifier) as? StudyDashboardTabbarViewController)!

        navigationController?.navigationBar.isHidden = true
        navigationController?.pushViewController(studyDashboard, animated: animated)
    }

    /**
     Method to display taskViewController for passcode setup if
     passcode setup is enabled,called only once after signin.
     */
    func setPassCode() {
        // Remove Passcode if already exist
        ORKPasscodeViewController.removePasscodeFromKeychain()

        let passcodeStep = ORKPasscodeStep(identifier: kPasscodeStepIdentifier)
        passcodeStep.passcodeType = .type4Digit

        let task = ORKOrderedTask(identifier: kPasscodeTaskIdentifier, steps: [passcodeStep])
        let taskViewController = ORKTaskViewController(task: task, taskRun: nil)
        taskViewController.delegate = self
        taskViewController.isNavigationBarHidden = true
        taskViewController.modalPresentationStyle = .fullScreen
        navigationController?.present(taskViewController, animated: false, completion: {
            self.tableView?.isHidden = false
        })
    }

    /**
     Load the study data from Database
     */
    func loadStudiesFromDatabase() {
        Logger.sharedInstance.info("Fetching Studies From DB")
        DBHandler.loadStudyListFromDatabase { studies in
            if studies.count > 0 {
                self.tableView?.isHidden = false
                
//                var sortedstudies2 = studies.sorted(by: { $0.name!.lowercased() < $1.name!.lowercased() })
            
                
//               Logger.sharedInstance.info("Sorting Studies")
//                sortedstudies2 = sortedstudies2.sorted(by: { (study1: Study, study2: Study) -> Bool in
//                    // sorting based on UserParticipation status
//                    if study1.status == study2.status {
//                        return (study1.userParticipateState.status.sortIndex < study2.userParticipateState.status.sortIndex)
//                    }
//                    return (study1.status.sortIndex < study2.status.sortIndex)
//                })
                self.studiesList = studies
                self.tableView?.reloadData()
                Logger.sharedInstance.info("Studies displayed to user")

                self.previousStudyList = studies
                self.allStudyList = studies
                Gateway.instance.studies = studies

                // Applying Filters
                if StudyFilterHandler.instance.previousAppliedFilters.count > 0 {
                    let previousCollectionData = StudyFilterHandler.instance.previousAppliedFilters

                    if User.currentUser.userType == .FDAUser {
                        self.appliedFilter(studyStatus: previousCollectionData.first!, pariticipationsStatus: previousCollectionData[2], categories: previousCollectionData[3], searchText: "", bookmarked: previousCollectionData[1].count > 0 ? true : false)
                    } else {
                        self.appliedFilter(studyStatus: previousCollectionData.first!, pariticipationsStatus: [], categories: previousCollectionData[1], searchText: "", bookmarked: false)
                    }
                } else {
                    let appDelegate = (UIApplication.shared.delegate as? AppDelegate)!

                    appDelegate.setDefaultFilters(previousCollectionData: [])

                    // using default Filters
                    let filterStrings = appDelegate.getDefaultFilterStrings()

                    self.appliedFilter(studyStatus: filterStrings.studyStatus, pariticipationsStatus: filterStrings.pariticipationsStatus, categories: filterStrings.categories, searchText: filterStrings.searchText, bookmarked: filterStrings.bookmark)
                }
                self.checkIfFetelKickCountRunning()
            } else {
                if !self.studyListRequestFailed {
                    self.labelHelperText.isHidden = true
                    self.tableView?.isHidden = false
                    self.studyListRequestFailed = false

                    self.sendRequestToGetStudyList()
                } else {
                    self.tableView?.isHidden = true
                    self.labelHelperText.isHidden = false
                    self.labelHelperText.text = kHelperTextForOffline
                }
            }
        }
    }

    /**
     Sort Studies based on the Study Status
     */
    func getSortedStudies(studies: [Study]) -> [Study] {
        var sortedstudies2 = studies.sorted(by: { $0.name!.lowercased() < $1.name!.lowercased() })
        sortedstudies2 = sortedstudies2.sorted(by: { (study1: Study, study2: Study) -> Bool in

            if study1.status == study2.status {
                return (study1.userParticipateState.status.sortIndex < study2.userParticipateState.status.sortIndex)
            }
            return (study1.status.sortIndex < study2.status.sortIndex)
        })
        return sortedstudies2
    }

    // MARK: - Button Actions

    /**
     Navigate to notification screen on button clicked
     @param sender    accepts UIBarButtonItem in sender
     */
    @IBAction func buttonActionNotification(_: UIBarButtonItem) {
        navigateToNotifications()
    }

    @objc func refresh(sender _: AnyObject) {
        sendRequestToGetStudyList()
    }

    /**
     Navigate to StudyFilter screen on button clicked
     @param sender    accepts UIBarButtonItem in sender
     */
    @IBAction func filterAction(_: UIBarButtonItem) {
        isComingFromFilterScreen = true
        performSegue(withIdentifier: filterListSegue, sender: nil)
    }

    @IBAction func searchButtonAction(_: UIBarButtonItem) {
        searchView = SearchBarView.instanceFromNib(frame: CGRect(x: 0, y: -200, width: view.frame.size.width, height: 64.0), detail: nil)

        UIView.animate(withDuration: 0.2,
                       delay: 0.0,
                       options: UIView.AnimationOptions.preferredFramesPerSecond60,
                       animations: { () -> Void in

                           let y: CGFloat = DeviceType.IS_IPHONE_X_OR_HIGH ? 20.0 : 0.0

                           self.searchView?.frame = CGRect(x: 0, y: y, width: self.view.frame.size.width, height: 64.0)

                           self.searchView?.textFieldSearch?.becomeFirstResponder()
                           self.searchView?.delegate = self

                           self.slideMenuController()?.leftPanGesture?.isEnabled = false

                           self.navigationController?.view.addSubview(self.searchView!)

                           if StudyFilterHandler.instance.searchText.count > 0 {
                               self.searchView?.textFieldSearch?.text = StudyFilterHandler.instance.searchText
                           }

                       }, completion: { (_) -> Void in

        })
    }

    // MARK: - Custom Bar Buttons

    /**
     Used to add left bar button item
     */
    func addLeftBarButton() {
        let button = UIButton(type: .custom)
        button.setTitle("FDA LISTENS!", for: .normal)
        button.titleLabel?.font = UIFont(name: "HelveticaNeue-Medium", size: 18)
        button.frame = CGRect(x: 0, y: 0, width: 120, height: 30)
        button.contentHorizontalAlignment = .left
        button.setTitleColor(Utilities.getUIColorFromHex(0x007CBA), for: .normal)

        let barItem = UIBarButtonItem(customView: button)

        navigationItem.setLeftBarButton(barItem, animated: true)
    }

    /**
     Used to add right bar button item
     */
    func addRightBarButton() {
        let button = UIButton(type: .custom)
        button.setImage(#imageLiteral(resourceName: "filter_icn"), for: .normal)

        button.frame = CGRect(x: 0, y: 0, width: 19, height: 22.5)
        let barItem = UIBarButtonItem(customView: button)

        navigationItem.setRightBarButton(barItem, animated: true)
    }

    // MARK: - Segue Methods

    override func prepare(for segue: UIStoryboardSegue, sender _: Any?) {
        // Get the new view controller using segue.destinationViewController.
        // Pass the selected object to the new view controller.

        if segue.identifier == filterListSegue {
            let filterVc = (segue.destination as? StudyFilterViewController)!
           
            if StudyFilterHandler.instance.previousAppliedFilters.count > 0 {
                filterVc.previousCollectionData = StudyFilterHandler.instance.previousAppliedFilters
            }
            filterVc.delegate = self
        }
    }

    @IBAction func unwindToStudyList(_: UIStoryboardSegue) {
        // unwindStudyListSegue
    }

    // MARK: - Database Methods

    func checkDatabaseForStudyInfo(study: Study) {
        DBHandler.loadStudyOverview(studyId: (study.studyId)!) { overview in
            if overview != nil {
                study.overview = overview
                // self.navigateBasedOnUserStatus()
                self.navigateToStudyHome()
            } else {
                self.sendRequestToGetStudyInfo(study: study)
            }
        }
    }

    // MARK: - Webservice Requests

    /**
     Send the webservice request to get Study List
     */
    func sendRequestToGetStudyList() {
        WCPServices().getStudyList(self)
    }

    /**
     Send the webservice request to get Study Info
     @param study    Access the data from the study class
     */
    func sendRequestToGetStudyInfo(study: Study) {
        WCPServices().getStudyInformation(studyId: study.studyId, delegate: self)
    }

    /**
     Send the webservice request to get UserPreferences
     */
    func sendRequestToGetUserPreference() {
        UserServices().getStudyStates(self)
    }

    /**
     Send the webservice request to Update BookMarkStatus
     @param userStudyStatus    Access the data from UserStudyStatus
     */
    func sendRequestToUpdateBookMarkStatus(userStudyStatus: UserStudyStatus) {
        UserServices().updateStudyBookmarkStatus(studyStauts: userStudyStatus, delegate: self)
    }

    // MARK: - Webservice Responses

    /**
     Handle the Study list webservice response
     */
    func handleStudyListResponse() {
        Logger.sharedInstance.info("Study Response Handler")

        if (Gateway.instance.studies?.count)! > 0 {
            loadStudiesFromDatabase()
            let appDelegate = (UIApplication.shared.delegate as? AppDelegate)!

            if appDelegate.notificationDetails != nil, User.currentUser.userType == .FDAUser {
                appDelegate.handleLocalAndRemoteNotification(userInfoDetails: appDelegate.notificationDetails!)
            }
        } else {
            tableView?.isHidden = true
            labelHelperText.text = kHelperTextForOffline
            labelHelperText.isHidden = false
        }
    }

    /**
     save information for study which feilds need to be updated
     */
    func handleStudyUpdatedInformation() {
        let currentStudy = Study.currentStudy
        if currentStudy?.userParticipateState.status == UserStudyStatus.StudyStatus.yetToJoin {
            StudyUpdates.studyConsentUpdated = false
            StudyUpdates.studyActivitiesUpdated = false
            StudyUpdates.studyResourcesUpdated = false

            currentStudy?.version = StudyUpdates.studyVersion
            currentStudy?.newVersion = StudyUpdates.studyVersion
        }

        DBHandler.updateMetaDataToUpdateForStudy(study: Study.currentStudy!, updateDetails: nil)
        if StudyUpdates.studyInfoUpdated {
            sendRequestToGetStudyInfo(study: Study.currentStudy!)
        } else {
            navigateBasedOnUserStatus()
        }
    }

    /**
     navigateBasedOnUserStatus method navigates to StudyDashBoard or StudyHome based on UserParticipationStatus.
     */
    func navigateBasedOnUserStatus() {
        if User.currentUser.userType == UserType.FDAUser {
            if Study.currentStudy?.status == .Active {
                // handle accoring to UserStatus
                let userStudyStatus = (Study.currentStudy?.userParticipateState.status)!

                if userStudyStatus == .completed || userStudyStatus == .inProgress {
                    pushToStudyDashboard()
                } else {
                    checkDatabaseForStudyInfo(study: Study.currentStudy!)
                }
            } else {
                checkDatabaseForStudyInfo(study: Study.currentStudy!)
            }
        } else {
            checkDatabaseForStudyInfo(study: Study.currentStudy!)
        }
    }

    func performTaskBasedOnStudyStatus() {
        let study = Study.currentStudy

        if User.currentUser.userType == UserType.FDAUser {
            if Study.currentStudy?.status == .Active {
                let userStudyStatus = (Study.currentStudy?.userParticipateState.status)!

                if userStudyStatus == .completed || userStudyStatus == .inProgress {
                    // check if study version is udpated
                    if study?.version != study?.newVersion {
                        WCPServices().getStudyUpdates(study: study!, delegate: self)
                    } else {
                        // let appDelegate = (UIApplication.shared.delegate as? AppDelegate)!
                        addProgressIndicator()
                        perform(#selector(loadStudyDetails), with: self, afterDelay: 1)
                    }
                } else {
                    checkForStudyUpdate(study: study)
                }
            } else if Study.currentStudy?.status == .Paused {
                let userStudyStatus = (Study.currentStudy?.userParticipateState.status)!

                if userStudyStatus == .completed || userStudyStatus == .inProgress {
                    UIUtilities.showAlertWithTitleAndMessage(title: "", message: NSLocalizedString(kMessageForStudyPausedAfterJoiningState, comment: "") as NSString)
                } else {
                    checkForStudyUpdate(study: study)
                }
            } else {
                checkForStudyUpdate(study: study)
            }
        } else {
            checkForStudyUpdate(study: study)
        }
    }

    @objc func loadStudyDetails() {
        let study = Study.currentStudy
        DBHandler.loadStudyDetailsToUpdate(studyId: (study?.studyId)!, completionHandler: { _ in

            self.pushToStudyDashboard()
            self.removeProgressIndicator()
        })
    }

    func checkForStudyUpdate(study: Study?) {
        if study?.version != study?.newVersion {
            WCPServices().getStudyUpdates(study: study!, delegate: self)
        } else {
            checkDatabaseForStudyInfo(study: study!)
        }
    }
}

// MARK: - Applied filter delegate

extension StudyListViewController: StudyFilterDelegates {
    // Based on applied filter call WS
    func appliedFilter(studyStatus: [String], pariticipationsStatus: [String], categories: [String], searchText: String, bookmarked: Bool) {
        var previousCollectionData: [[String]] = []

        previousCollectionData.append(studyStatus)

        if User.currentUser.userType == .FDAUser {
            previousCollectionData.append(bookmarked == true ? ["Bookmarked"] : [])
            previousCollectionData.append(pariticipationsStatus)
        }

        previousCollectionData.append(categories.count == 0 ? [] : categories)

        StudyFilterHandler.instance.previousAppliedFilters = previousCollectionData

        StudyFilterHandler.instance.searchText = ""

        // filter by study category
        var categoryFilteredStudies: [Study]! = []
        if categories.count > 0 {
            categoryFilteredStudies = allStudyList.filter { categories.contains($0.category!) }
        }

        // filter by study status
        var statusFilteredStudies: [Study]! = []
        if studyStatus.count > 0 {
            statusFilteredStudies = allStudyList.filter { studyStatus.contains($0.status.rawValue) }
        }

        // filter by study status
        var pariticipationsStatusFilteredStudies: [Study]! = []
        if pariticipationsStatus.count > 0 {
            pariticipationsStatusFilteredStudies = allStudyList.filter { pariticipationsStatus.contains($0.userParticipateState.status.description) }
        }

        // filter by bookmark
        var bookmarkedStudies: [Study]! = []

        if bookmarked {
            bookmarkedStudies = allStudyList.filter { $0.userParticipateState.bookmarked == bookmarked }
        }

        // filter by searched Text
        var searchTextFilteredStudies: [Study]! = []
        if searchText.count > 0 {
            searchTextFilteredStudies = allStudyList.filter {
                ($0.name?.containsIgnoringCase(searchText))! || ($0.category?.containsIgnoringCase(searchText))! || ($0.description?.containsIgnoringCase(searchText))! || ($0.sponserName?.containsIgnoringCase(searchText))!
            }
        }

        // Intersection
        let setStudyStatus = Set<Study>(statusFilteredStudies)

        let setpariticipationsStatus = Set<Study>(pariticipationsStatusFilteredStudies)

        var statusFilteredSet = Set<Study>()

        var allFilteredSet = Set<Study>()

        // (setStudyStatus) ^ (setpariticipationsStatus)

        if setStudyStatus.count > 0, setpariticipationsStatus.count > 0 {
            statusFilteredSet = setStudyStatus.intersection(setpariticipationsStatus)
        } else {
            if setStudyStatus.count > 0 {
                statusFilteredSet = setStudyStatus

            } else if setpariticipationsStatus.count > 0 {
                statusFilteredSet = setpariticipationsStatus
            }
        }

        var bookMarkAndCategorySet = Set<Study>()

        let setCategories = Set<Study>(categoryFilteredStudies)

        let setBookmarkedStudies = Set<Study>(bookmarkedStudies)

        // (setCategories) ^ (setBookmarkedStudies)
        if setCategories.count > 0, setBookmarkedStudies.count > 0 {
            bookMarkAndCategorySet = setCategories.intersection(setBookmarkedStudies)
        } else {
            if setCategories.count > 0 {
                bookMarkAndCategorySet = setCategories
            } else if setBookmarkedStudies.count > 0 {
                bookMarkAndCategorySet = setBookmarkedStudies
            }
        }

        // (statusFilteredSet) ^ (bookMarkAndCategorySet)

        if statusFilteredSet.count > 0, bookMarkAndCategorySet.count > 0 {
            allFilteredSet = statusFilteredSet.intersection(bookMarkAndCategorySet)
        } else {
            if (statusFilteredSet.count > 0 && (bookmarked == true || categories.count > 0)) || (bookMarkAndCategorySet.count > 0 && (pariticipationsStatus.count > 0 || studyStatus.count > 0)) {
                allFilteredSet = bookMarkAndCategorySet.intersection(statusFilteredSet)
            } else {
                allFilteredSet = statusFilteredSet.union(bookMarkAndCategorySet)
            }
        }

        // (studystatus ^ participantstatus ^ bookmarked ^ category) ^ (searchTextResult)
        let setSearchedTextStudies = Set<Study>(searchTextFilteredStudies)

        if allFilteredSet.count > 0, setSearchedTextStudies.count > 0 {
            allFilteredSet = allFilteredSet.intersection(setSearchedTextStudies)
        } else {
            if setSearchedTextStudies.count > 0 {
                allFilteredSet = setSearchedTextStudies
            }
        }

        // Assigning Filtered result to Studlist
        let allStudiesArray: [Study] = Array(allFilteredSet)

        if searchText.count == 0, bookmarked == false, studyStatus.count == 0,
            pariticipationsStatus.count == 0, categories.count == 0 {
            studiesList = getSortedStudies(studies: allStudyList)
        } else {
            studiesList = getSortedStudies(studies: allStudiesArray)
        }

        previousStudyList = studiesList
        tableView?.reloadData()

        if studiesList.count == 0 {
            tableView?.isHidden = true
            labelHelperText.isHidden = false
            if studyListRequestFailed {
                labelHelperText.text = kHelperTextForOffline

            } else if searchText == "" {
                labelHelperText.text = kHelperTextForFilteredStudiesNotFound
            } else {
                labelHelperText.text = kHelperTextForSearchedStudiesNotFound
            }
        } else {
            tableView?.isHidden = false
            labelHelperText.isHidden = true
        }
    }

    func didCancelFilter(_: Bool) {
        // Do Nothing
    }
}

// MARK: - TableView Data source

extension StudyListViewController: UITableViewDataSource {
    func tableView(_: UITableView, numberOfRowsInSection _: Int) -> Int {
        return studiesList.count
    }

    func tableView(_ tableView: UITableView, cellForRowAt indexPath: IndexPath) -> UITableViewCell {
        var cellIdentifier = "studyCell"

        // check if current user is anonymous
        let user = User.currentUser
        if user.userType == .AnonymousUser {
            cellIdentifier = "anonymousStudyCell"
        }

        let cell = (tableView.dequeueReusableCell(withIdentifier: cellIdentifier, for: indexPath) as? StudyListCell)!

        cell.populateCellWith(study: studiesList[indexPath.row])
        cell.delegate = self

        return cell
    }
}

// MARK: - TableView Delegates

extension StudyListViewController: UITableViewDelegate {
    func tableView(_ tableView: UITableView, didSelectRowAt indexPath: IndexPath) {
        tableView.deselectRow(at: indexPath, animated: true)

        if searchView != nil {
            searchView?.removeFromSuperview()
            slideMenuController()?.leftPanGesture?.isEnabled = true
        }

        let study = studiesList[indexPath.row]
        Study.updateCurrentStudy(study: study)

        performTaskBasedOnStudyStatus()
    }
}

// MARK: - StudyList Delegates

extension StudyListViewController: StudyListDelegates {
    func studyBookmarked(_: StudyListCell, bookmarked: Bool, forStudy study: Study) {
        let user = User.currentUser
        var userStudyStatus: UserStudyStatus!
        if bookmarked {
            userStudyStatus = user.bookmarkStudy(studyId: study.studyId!)
        } else {
            userStudyStatus = user.removeBookbarkStudy(studyId: study.studyId!)
        }
        sendRequestToUpdateBookMarkStatus(userStudyStatus: userStudyStatus)
    }
}

// MARK: SearchBarDelegate

extension StudyListViewController: searchBarDelegate {
    func didTapOnCancel() {
        slideMenuController()?.leftPanGesture?.isEnabled = true

        if studiesList.count == 0, previousStudyList.count > 0 {
            studiesList = previousStudyList
        } else if searchView?.textFieldSearch?.text?.count == 0 {
            if StudyFilterHandler.instance.previousAppliedFilters.count > 0 {
                let previousCollectionData = StudyFilterHandler.instance.previousAppliedFilters

                // Apply Filters
                if User.currentUser.userType == .FDAUser {
                    appliedFilter(studyStatus: previousCollectionData.first!, pariticipationsStatus: previousCollectionData[2], categories: previousCollectionData[3], searchText: "", bookmarked: previousCollectionData[1].count > 0 ? true : false)
                } else {
                    appliedFilter(studyStatus: previousCollectionData.first!, pariticipationsStatus: [], categories: previousCollectionData[1], searchText: "", bookmarked: false)
                }
            } else {
                let appDelegate = (UIApplication.shared.delegate as? AppDelegate)!

                if StudyFilterHandler.instance.filterOptions.count > 0 {
                    // setting default Filters
                    let filterStrings = appDelegate.getDefaultFilterStrings()
                    appliedFilter(studyStatus: filterStrings.studyStatus, pariticipationsStatus: filterStrings.pariticipationsStatus, categories: filterStrings.categories, searchText: filterStrings.searchText, bookmarked: filterStrings.bookmark)
                }
            }
        }

        if searchView != nil {
            searchView?.removeFromSuperview()
        }

        tableView?.reloadData()

        if studiesList.count == 0 {
            labelHelperText.text = kHelperTextForSearchedStudiesNotFound
            tableView?.isHidden = true
            labelHelperText.isHidden = false
        } else {
            tableView?.isHidden = false
            labelHelperText.isHidden = true
        }
    }

    /** Searches for the text provided in the filtered StudyList
     @text: serachText irrespective of case
     */
    func search(text: String) {
        if studiesList.count == 0 {
            let appDelegate = (UIApplication.shared.delegate as? AppDelegate)!

            if StudyFilterHandler.instance.filterOptions.count > 0 {
                let filterStrings = appDelegate.getDefaultFilterStrings()

                appliedFilter(studyStatus: filterStrings.studyStatus, pariticipationsStatus: filterStrings.pariticipationsStatus, categories: filterStrings.categories, searchText: filterStrings.searchText, bookmarked: filterStrings.bookmark)
            }
        }

        // filter by searched Text
        var searchTextFilteredStudies: [Study]! = []
        if text.count > 0 {
            searchTextFilteredStudies = allStudyList.filter {
                ($0.name?.containsIgnoringCase(text))! || ($0.category?.containsIgnoringCase(text))! || ($0.description?.containsIgnoringCase(text))! || ($0.sponserName?.containsIgnoringCase(text))!
            }

            StudyFilterHandler.instance.searchText = text

            previousStudyList = studiesList
            studiesList = getSortedStudies(studies: searchTextFilteredStudies)

            if studiesList.count == 0 {
                labelHelperText.text = kHelperTextForSearchedStudiesNotFound
                tableView?.isHidden = true
                labelHelperText.isHidden = false
            }
        } else {
            StudyFilterHandler.instance.searchText = ""
        }

        tableView?.reloadData()

        if studiesList.count > 0 {
            if searchView != nil {
                searchView?.removeFromSuperview()
                slideMenuController()?.leftPanGesture?.isEnabled = true
            }
        }
    }
}

// MARK: - Webservices Delegates

extension StudyListViewController: NMWebServiceDelegate {
    func startedRequest(_: NetworkManager, requestName: NSString) {
        Logger.sharedInstance.info("requestname START : \(requestName)")

        let appdelegate = (UIApplication.shared.delegate as? AppDelegate)!
        appdelegate.window?.addProgressIndicatorOnWindowFromTop()
    }

    func finishedRequest(_: NetworkManager, requestName: NSString, response: AnyObject?) {
        Logger.sharedInstance.info("requestname FINISH: \(requestName) : \(String(describing: response))")

        let appdelegate = (UIApplication.shared.delegate as? AppDelegate)!

        if requestName as String == WCPMethods.studyList.rawValue {
            // let responseDict = (response as? NSDictionary)!

            if refreshControl != nil, (refreshControl?.isRefreshing)! {
                refreshControl?.endRefreshing()
            }

            handleStudyListResponse()
            appdelegate.window?.removeProgressIndicatorFromWindow()

        } else if requestName as String == WCPMethods.studyInfo.rawValue {
            appdelegate.window?.removeProgressIndicatorFromWindow()
            navigateBasedOnUserStatus()

        } else if requestName as String == RegistrationMethods.studyState.description {
            appdelegate.window?.removeProgressIndicatorFromWindow()
            sendRequestToGetStudyList()

        } else if requestName as String == WCPMethods.studyUpdates.rawValue {
            appdelegate.window?.removeProgressIndicatorFromWindow()
            handleStudyUpdatedInformation()

        } else if requestName as String == RegistrationMethods.userProfile.description {
            appdelegate.window?.removeProgressIndicatorFromWindow()
            if User.currentUser.settings?.passcode == true {
                setPassCode()

            } else {
                UserDefaults.standard.set(false, forKey: kPasscodeIsPending)
                UserDefaults.standard.synchronize()
            }

        } else if requestName as String == RegistrationMethods.updateStudyState.description {
            appdelegate.window?.removeProgressIndicatorFromWindow()
        }
    }

    func failedRequest(_: NetworkManager, requestName: NSString, error: NSError) {
        Logger.sharedInstance.info("requestname Failed: \(requestName)")
        let appdelegate = (UIApplication.shared.delegate as? AppDelegate)!
        appdelegate.window?.removeProgressIndicatorFromWindow()

        if error.code == 403 { // unauthorized Access
            UIUtilities.showAlertMessageWithActionHandler(kErrorTitle, message: error.localizedDescription, buttonTitle: kTitleOk, viewControllerUsed: self, action: {
                self.fdaSlideMenuController()?.navigateToHomeAfterUnauthorizedAccess()
            })
        } else {
            if requestName as String == RegistrationMethods.studyState.description {
                sendRequestToGetStudyList()

            } else if requestName as String == WCPMethods.studyList.rawValue {
                studyListRequestFailed = true
                loadStudiesFromDatabase()

            } else {
                if requestName as String == RegistrationMethods.userProfile.description {}

                if refreshControl != nil, (refreshControl?.isRefreshing)! {
                    refreshControl?.endRefreshing()
                }
                UIUtilities.showAlertWithTitleAndMessage(title: NSLocalizedString(kErrorTitle, comment: "") as NSString, message: error.localizedDescription as NSString)
            }
        }
    }
}

// MARK: - StudyHomeViewDontroller Delegate

extension StudyListViewController: StudyHomeViewDontrollerDelegate {
    func studyHomeJoinStudy() {
        DispatchQueue.main.asyncAfter(deadline: .now() + 0.5) {
            let appdelegate = (UIApplication.shared.delegate as? AppDelegate)!
            appdelegate.window?.removeProgressIndicatorFromWindow()

            let leftController = (self.slideMenuController()?.leftViewController as? LeftMenuViewController)!
            leftController.changeViewController(.reachOut_signIn)
        }
    }
}

// MARK: - ORKTaskViewController Delegate

extension StudyListViewController: ORKTaskViewControllerDelegate {
    func taskViewControllerSupportsSaveAndRestore(_: ORKTaskViewController) -> Bool {
        return true
    }

    public func taskViewController(_ taskViewController: ORKTaskViewController, didFinishWith reason: ORKTaskViewControllerFinishReason, error _: Error?) {
        var taskResult: Any?
        switch reason {
        case ORKTaskViewControllerFinishReason.completed:
            print("completed")
            taskResult = taskViewController.result
            let ud = UserDefaults.standard
            ud.set(false, forKey: kPasscodeIsPending)
            ud.synchronize()

        case ORKTaskViewControllerFinishReason.failed:
            print("failed")
            taskResult = taskViewController.result

        case ORKTaskViewControllerFinishReason.discarded:
            print("discarded")
            taskResult = taskViewController.result

        case ORKTaskViewControllerFinishReason.saved:
            print("saved")
            taskResult = taskViewController.restorationData
        }

        perform(#selector(dismisscontroller), with: self, afterDelay: 1.5)
    }

    @objc func dismisscontroller() {
        dismiss(animated: true, completion: nil)
    }

    func taskViewController(_: ORKTaskViewController, stepViewControllerWillAppear _: ORKStepViewController) {}
}
