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


let kMessageForSharingDashboard = "This action will create a shareable image file of the dashboard currently seen in this section. Proceed?"

enum TableViewCells: Int {
    case welcomeCell = 0
    //case studyActivityCell
    case percentageCell
}

class StudyDashboardViewController: UIViewController{
    
    @IBOutlet var tableView: UITableView?
    @IBOutlet var labelStudyTitle: UILabel?
    @IBOutlet var buttonHome:UIButton!
    
    var dataSourceKeysForLabkey: Array<Dictionary<String,String>> = []
    
    var tableViewRowDetails = NSMutableArray()
    var todayActivitiesArray = NSMutableArray()
    var statisticsArray = NSMutableArray()
    override var preferredStatusBarStyle: UIStatusBarStyle{
        return .lightContent
    }
    
    // MARK:- ViewController Lifecycle
    override func viewDidLoad() {
        super.viewDidLoad()
        
        //load plist info
        let plistPath = Bundle.main.path(forResource: "StudyDashboard", ofType: ".plist", inDirectory: nil)
        let tableViewRowDetailsdat = NSMutableArray.init(contentsOfFile: plistPath!)
        
        let tableviewdata = (tableViewRowDetailsdat?[0] as? NSDictionary)!
        
        tableViewRowDetails = (tableviewdata["studyActivity"] as? NSMutableArray)!
        todayActivitiesArray = (tableviewdata["todaysActivity"] as? NSMutableArray)!
        statisticsArray = (tableviewdata["statistics"] as? NSMutableArray)!
        
        labelStudyTitle?.text = Study.currentStudy?.name
        
        //check if consent is udpated
        if StudyUpdates.studyConsentUpdated {
            let appDelegate = (UIApplication.shared.delegate as? AppDelegate)!
            appDelegate.checkConsentStatus(controller: self)
        }
        
    }
    
    override func viewWillAppear(_ animated: Bool) {
        super.viewWillAppear(animated)
        //UIApplication.shared.statusBarStyle = .lightContent
        
        setNeedsStatusBarAppearanceUpdate()
        
        
        //Standalone App Settings
        if Utilities.isStandaloneApp() {
            buttonHome.setImage(UIImage(named: "menu_icn"), for: .normal)
            buttonHome.tag = 200
            self.slideMenuController()?.removeLeftGestures()
            self.slideMenuController()?.removeRightGestures()
        }
        
        //show navigationbar
        self.navigationController?.setNavigationBarHidden(true, animated: true)
        
        self.tableView?.reloadData()
        
        DBHandler.loadStatisticsForStudy(studyId: (Study.currentStudy?.studyId)!) { (statiticsList) in
            
            if statiticsList.count != 0 {
                StudyDashboard.instance.statistics = statiticsList
                self.tableView?.reloadData()
            }
        }
        
    }
    
    override func viewDidAppear(_ animated: Bool) {
        super.viewDidAppear(animated)
        
    }
    
    
    // MARK:- Helper Methods
    
    /**
     Used to Create Eligibility Consent Task
     */
    func createEligibilityConsentTask() {
        
        let taskViewController: ORKTaskViewController?
        
        let consentTask: ORKOrderedTask? = ConsentBuilder.currentConsent?.createConsentTask() as! ORKOrderedTask?
        
        taskViewController = ORKTaskViewController(task: consentTask, taskRun: nil)
        
        taskViewController?.delegate = self
        taskViewController?.outputDirectory = FileManager.default.urls(for: .documentDirectory, in: .userDomainMask).first!
        
        taskViewController?.navigationItem.title = nil
        
        UIView.appearance(whenContainedInInstancesOf: [ORKTaskViewController.self]).tintColor = kUIColorForSubmitButtonBackground
        
        UIApplication.shared.statusBarStyle = .default
        taskViewController?.modalPresentationStyle = .fullScreen
        present(taskViewController!, animated: true, completion: nil)
    }
    
    func getDataKeysForCurrentStudy() {
        
        DBHandler.getDataSourceKeyForActivity(studyId: (Study.currentStudy?.studyId)!) { (activityKeys) in
            print(activityKeys)
            if activityKeys.count > 0 {
                self.dataSourceKeysForLabkey = activityKeys
                self.sendRequestToGetDashboardResponse()
            }
        }
        
    }
    
    
    /**
     Used to send Request To Get Dashboard Info
     */
    func sendRequestToGetDashboardInfo(){
        WCPServices().getStudyDashboardInfo(studyId: (Study.currentStudy?.studyId)!, delegate: self)
    }
    
    func sendRequestToGetDashboardResponse(){
        
        if self.dataSourceKeysForLabkey.count != 0 {
            let details = self.dataSourceKeysForLabkey.first
            let activityId = details?["activityId"]
            var tableName = activityId
            let activity = Study.currentStudy?.activities.filter({$0.actvityId == activityId}).first
            var keys = details?["keys"]
            
            if activity?.type == ActivityType.activeTask {
                
                if activity?.taskSubType == "fetalKickCounter" {
                    keys = "\"count\",duration"
                    tableName = activityId!+activityId!
                    
                } else if activity?.taskSubType == "towerOfHanoi" {
                    keys = "numberOfMoves"
                    tableName = activityId!+activityId!
                    
                } else if activity?.taskSubType == "spatialSpanMemory" {
                    keys = "NumberofGames,Score,NumberofFailures"
                    tableName = activityId!+activityId!
                }
            }
            let participantId = Study.currentStudy?.userParticipateState.participantId
            //Get stats from Server
            LabKeyServices().getParticipantResponse(tableName: tableName!,activityId: activityId!, keys: keys!, participantId: participantId!, delegate: self)
            
        } else {
            self.removeProgressIndicator()
            
            //save response in database
            let responses = StudyDashboard.instance.dashboardResponse
            for  response in responses{
                
                let activityId = response.activityId
                let activity = Study.currentStudy?.activities.filter({$0.actvityId == activityId}).first
                var key = response.key
                if activity?.type == ActivityType.activeTask {
                    
                    if activity?.taskSubType == "fetalKickCounter" || activity?.taskSubType == "towerOfHanoi"{
                        key = activityId!
                    }
                    
                }
                
                
                let values = response.values
                for value in values{
                    let responseValue = (value["value"] as? Float)!
                    let count = (value["count"] as? Float)!
                    let date = StudyDashboardViewController.labkeyDateFormatter.date(from: (value["date"] as? String)!)
                    let localDateAsString = StudyDashboardViewController.localDateFormatter.string(from: date!)
                    
                    let localDate = StudyDashboardViewController.localDateFormatter.date(from: localDateAsString)
                    DBHandler.saveStatisticsDataFor(activityId: activityId!, key: key!, data: responseValue, fkDuration: Int(count),date: localDate!)
                }
                
                
            }
            let key = "LabKeyResponse" + (Study.currentStudy?.studyId)!
            UserDefaults.standard.set(true, forKey: key)
        }
    }
    
    
    func handleExecuteSQLResponse(){
        
        self.dataSourceKeysForLabkey.removeFirst()
        self.sendRequestToGetDashboardResponse()
    }
    
    // MARK:- Button Actions
    
    /**
     Home button clicked
     @param sender    Accepts any kind of object
     */
    @IBAction func homeButtonAction(_ sender: AnyObject){
        let button = sender as! UIButton
        if button.tag == 200 {
            self.slideMenuController()?.openLeft()
        }
        else {
            self.performSegue(withIdentifier: unwindToStudyListDashboard, sender: self)
        }
    }
    
    
    /**
     Share to others button clicked
     @param sender    Accepts any kind of object
     */
    @IBAction func shareButtonAction(_ sender: AnyObject){
        
        
        UIUtilities.showAlertMessageWithTwoActionsAndHandler(NSLocalizedString(kTitleMessage, comment: ""), errorMessage: NSLocalizedString(kMessageForSharingDashboard, comment: ""), errorAlertActionTitle: NSLocalizedString(kTitleOK, comment: ""),
                                                             errorAlertActionTitle2: NSLocalizedString(kTitleCancel, comment: ""), viewControllerUsed: self,
                                                             action1: {
                                                                
                                                                self.shareScreenShotByMail()
        },
                                                             
                                                             action2: {
                                                                
                                                                
        })
        
        
        
    }
    
    func shareScreenShotByMail() {
        
        //Create the UIImage
        UIGraphicsBeginImageContextWithOptions(view.bounds.size, self.view.isOpaque, 0.0)
        view.layer.render(in: UIGraphicsGetCurrentContext()!)
        let image = UIGraphicsGetImageFromCurrentImageContext()
        UIGraphicsEndImageContext()
        
        (self.tabBarController as? StudyDashboardTabbarViewController)!.shareScreenshotByEmail(image: image,subject: kEmailSubjectDashboard, fileName: kEmailSubjectDashboard)
        
        
    }
    
    
    private static let labkeyDateFormatter: DateFormatter = {
        //2017/06/13 18:12:13
        let formatter = DateFormatter()
        formatter.timeZone = TimeZone.init(identifier: "America/New_York")
        formatter.dateFormat = "YYYY/MM/dd HH:mm:ss"
        
        return formatter
    }()
    
    private static let localDateFormatter: DateFormatter = {
        //2017/06/13 18:12:13
        let formatter = DateFormatter()
        formatter.timeZone = TimeZone.current
        formatter.dateFormat = "YYYY/MM/dd HH:mm:ss"
        
        return formatter
    }()
}


// MARK:- TableView Datasource
extension StudyDashboardViewController : UITableViewDataSource {
    
    func tableView(_ tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
        return 1
    }
    
    func numberOfSections(in tableView: UITableView) -> Int {
        return tableViewRowDetails.count + 1
    }
    
    func tableView(_ tableView: UITableView, heightForRowAt indexPath: IndexPath) -> CGFloat {
        
        //Used for the last cell Height (trends cell)
        if indexPath.section == tableViewRowDetails.count{
            return 50
        }
        
        let data = (self.tableViewRowDetails[indexPath.section] as? NSDictionary)!
        
        var heightValue: CGFloat = 0
        if (data["isTableViewCell"] as? String)! == "YES" {
            
            //Used for Table view Height in a cell
            switch indexPath.section {
            case TableViewCells.welcomeCell.rawValue:
                heightValue = 70
            case TableViewCells.percentageCell.rawValue:
                heightValue = 200
            default:
                return 0
            }
            
        } else {
            //Used for Collection View Height in a cell
            if (data["isStudy"] as? String)! == "YES" {
                heightValue = 130
            } else {
                heightValue = 210
            }
        }
        return heightValue
    }
    
    func tableView(_ tableView: UITableView, cellForRowAt indexPath: IndexPath) -> UITableViewCell {
        
        var cell: UITableViewCell?
        
        //Used to display the last cell trends
        if indexPath.section == tableViewRowDetails.count{
            
            cell = (tableView.dequeueReusableCell(withIdentifier: kTrendTableViewCell, for: indexPath) as? StudyDashboardTrendsTableViewCell)!
            return cell!
        }
        
        let tableViewData = (tableViewRowDetails.object(at: indexPath.section) as? NSDictionary)!
        
        if (tableViewData["isTableViewCell"] as? String)! == "YES" {
            
            //Used for Table view Cell
            switch indexPath.section {
            case TableViewCells.welcomeCell.rawValue:
                cell = (tableView.dequeueReusableCell(withIdentifier: kWelcomeTableViewCell, for: indexPath) as? StudyDashboardWelcomeTableViewCell)!
                (cell as? StudyDashboardWelcomeTableViewCell)!.displayFirstCelldata(data: tableViewData)
                
            case TableViewCells.percentageCell.rawValue:
                cell = (tableView.dequeueReusableCell(withIdentifier: kPercentageTableViewCell, for: indexPath) as? StudyDashboardStudyPercentageTableViewCell)!
                (cell as? StudyDashboardStudyPercentageTableViewCell)!.displayThirdCellData(data: tableViewData)
                
            default:
                return cell!
            }
            
        } else {
            cell = (tableView.dequeueReusableCell(withIdentifier: kStatisticsTableViewCell, for: indexPath) as? StudyDashboardStatisticsTableViewCell)!
            (cell as? StudyDashboardStatisticsTableViewCell)!.displayData()
            (cell as? StudyDashboardStatisticsTableViewCell)!.buttonDay?.setTitle("  DAY  ", for: UIControl.State.normal)
            (cell as? StudyDashboardStatisticsTableViewCell)!.statisticsCollectionView?.reloadData()
        }
        return cell!
    }
}


// MARK:- TableView Delegates
extension StudyDashboardViewController : UITableViewDelegate{
    func tableView(_ tableView: UITableView, didSelectRowAt indexPath: IndexPath) {
        
        tableView.deselectRow(at: indexPath, animated: true)
        
        if indexPath.section == tableViewRowDetails.count {
            self.performSegue(withIdentifier: "chartSegue", sender: nil)
        }
    }
}


// MARK:- Webservice Delegates
extension StudyDashboardViewController:NMWebServiceDelegate {
    
    func startedRequest(_ manager: NetworkManager, requestName: NSString) {
        Logger.sharedInstance.info("requestname : \(requestName)")
        self.addProgressIndicator()
    }
    
    func finishedRequest(_ manager: NetworkManager, requestName: NSString, response: AnyObject?) {
        
        if requestName as String == WCPMethods.eligibilityConsent.method.methodName {
            self.removeProgressIndicator()
            self.createEligibilityConsentTask()
        }
        else if requestName as String == WCPMethods.studyDashboard.method.methodName {
            self.removeProgressIndicator()
            self.tableView?.reloadData()
        }
        else if requestName as String == ResponseMethods.executeSQL.description{
            self.handleExecuteSQLResponse()
        }
    }
    
    func failedRequest(_ manager: NetworkManager, requestName: NSString, error: NSError) {
        Logger.sharedInstance.info("requestname : \(requestName)")
        
        
        if requestName as String == WCPMethods.consentDocument.method.methodName {
            self.removeProgressIndicator()
        }
        else if requestName as String == ResponseMethods.executeSQL.description{
            self.handleExecuteSQLResponse()
        }
        else {
            self.removeProgressIndicator()
        }
    }
}


// MARK:- ORKTaskViewController Delegate
extension StudyDashboardViewController: ORKTaskViewControllerDelegate{
    
    func taskViewControllerSupportsSaveAndRestore(_ taskViewController: ORKTaskViewController) -> Bool {
        return true
    }
    
    public func taskViewController(_ taskViewController: ORKTaskViewController, didFinishWith reason: ORKTaskViewControllerFinishReason, error: Error?) {
        
        var taskResult:Any?
        switch reason {
            
        case ORKTaskViewControllerFinishReason.completed:
            print("completed")
            taskResult = taskViewController.result
            
            ConsentBuilder.currentConsent?.consentResult?.consentDocument =   ConsentBuilder.currentConsent?.consentDocument
            ConsentBuilder.currentConsent?.consentResult?.initWithORKTaskResult(taskResult: taskViewController.result )
            
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
        taskViewController.dismiss(animated: true, completion: nil)
    }
    
    func taskViewController(_ taskViewController: ORKTaskViewController, stepViewControllerWillAppear stepViewController: ORKStepViewController) {
        
        if (taskViewController.result.results?.count)! > 1{
            
            if activityBuilder?.actvityResult?.result?.count == taskViewController.result.results?.count{
                //Removing the dummy result:Currentstep result which not presented yet
                activityBuilder?.actvityResult?.result?.removeLast()
                
            } else {
                
            }
        }
        
        //Handling show and hide of Back Button
        
        //For Verified Step , Completion Step, Visual Step, Review Step, Share Pdf Step
        
        if  stepViewController.step?.identifier == kConsentCompletionStepIdentifier || stepViewController.step?.identifier == kVisualStepId || stepViewController.step?.identifier == kReviewTitle || stepViewController.step?.identifier == kConsentSharePdfCompletionStep{
            
            
            if stepViewController.step?.identifier == kEligibilityVerifiedScreen {
                stepViewController.continueButtonTitle = kContinueButtonTitle
            }
            stepViewController.backButtonItem = nil
        }
            //checking if currentstep is View Pdf Step
        else if stepViewController.step?.identifier == kConsentViewPdfCompletionStep {
            
            //Back button is enabled
            stepViewController.backButtonItem?.isEnabled = true
            
            let orkStepResult: ORKStepResult? = taskViewController.result.results?[(taskViewController.result.results?.count)! - 2] as! ORKStepResult?
            
            let consentSignatureResult: ConsentCompletionTaskResult? = orkStepResult?.results?.first as? ConsentCompletionTaskResult
            
            //Checking if Signature is consented after Review Step
            
            if  consentSignatureResult?.didTapOnViewPdf == false{
                //Directly moving to completion step by skipping Intermediate PDF viewer screen
                stepViewController.goForward()
                
            } else {
                
            }
        } else {
            //Back button is enabled
            stepViewController.backButtonItem?.isEnabled = true
            
        }
    }
    
    
    // MARK:- StepViewController Delegate
    
    public func stepViewController(_ stepViewController: ORKStepViewController, didFinishWith direction: ORKStepViewControllerNavigationDirection){
    }
    
    public func stepViewControllerResultDidChange(_ stepViewController: ORKStepViewController){
    }
    
    public func stepViewControllerDidFail(_ stepViewController: ORKStepViewController, withError error: Error?){
    }
    
    func taskViewController(_ taskViewController: ORKTaskViewController, viewControllerFor step: ORKStep) -> ORKStepViewController? {
        
        //CurrentStep is TokenStep
        
        if step.identifier == kEligibilityTokenStep { //For EligibilityToken Step
            
            let gatewayStoryboard = UIStoryboard(name: kFetalKickCounterStep, bundle: nil)
            
            let ttController = (gatewayStoryboard.instantiateViewController(withIdentifier: kEligibilityStepViewControllerIdentifier) as? EligibilityStepViewController)!
            ttController.descriptionText = step.text
            ttController.step = step
            
            return ttController
        } else if step.identifier == kConsentSharePdfCompletionStep { // For SharePdfCompletion Step
            
            var totalResults =  taskViewController.result.results
            let reviewStep: ORKStepResult?
            totalResults = totalResults?.filter({$0.identifier == kReviewTitle})
            reviewStep = totalResults?.first as! ORKStepResult?
            
            if (reviewStep?.identifier)! == kReviewTitle && (reviewStep?.results?.count)! > 0 {
                let consentSignatureResult: ORKConsentSignatureResult? = reviewStep?.results?.first as? ORKConsentSignatureResult
                
                if  consentSignatureResult?.consented == false { //User disagreed on Consent
                    taskViewController.dismiss(animated: true
                        , completion: nil)
                    _ = self.navigationController?.popViewController(animated: true)
                    return nil
                    
                } else { //User consented
                    
                    let documentCopy: ORKConsentDocument = ((ConsentBuilder.currentConsent?.consentDocument)!.copy() as? ORKConsentDocument)!
                    
                    consentSignatureResult?.apply(to: documentCopy)
                    let gatewayStoryboard = UIStoryboard(name: kFetalKickCounterStep, bundle: nil)
                    let ttController = (gatewayStoryboard.instantiateViewController(withIdentifier: kConsentSharePdfStoryboardId) as? ConsentSharePdfStepViewController)!
                    ttController.step = step
                    ttController.consentDocument =  documentCopy
                    return ttController
                }
            } else {
                return nil
            }
        } else if step.identifier == kConsentViewPdfCompletionStep { //For Pdf Completion Step
            
            let reviewSharePdfStep: ORKStepResult? = taskViewController.result.results?.last as! ORKStepResult?
            
            let result = (reviewSharePdfStep?.results?.first as? ConsentCompletionTaskResult)
            
            if (result?.didTapOnViewPdf)!{
                let gatewayStoryboard = UIStoryboard(name: kFetalKickCounterStep, bundle: nil)
                
                let ttController = (gatewayStoryboard.instantiateViewController(withIdentifier: kConsentViewPdfStoryboardId) as? ConsentPdfViewerStepViewController)!
                ttController.step = step
                ttController.pdfData = result?.pdfData
                return ttController
                
            } else {
                return nil
            }
        } else {
            
            return nil
        }
    }
}

