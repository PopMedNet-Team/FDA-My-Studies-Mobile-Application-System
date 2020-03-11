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
import ResearchKit
import UIKit

let kEligibilityConsentTask = "EligibilityConsentTask"
let kEligibilityTokenStep = "EligibilityTokenStep"

let kInEligibilityStep = "InEligibilityStep"

let kFetalKickCounterStep = "FetalKickCounter"
let kEligibilityStepViewControllerIdentifier = "EligibilityStepViewController"
let kInEligibilityStepViewControllerIdentifier = "InEligibilityStepViewController"

let kConsentTaskIdentifier = "ConsentTask"
let kStudyDashboardViewControllerIdentifier = "StudyDashboardViewController"
let kStudyDashboardTabbarControllerIdentifier = "StudyDashboardTabbarViewControllerIdentifier"

let kShareConsentFailureAlert = "You can't join study without sharing your data"

protocol StudyHomeViewDontrollerDelegate {
    func studyHomeJoinStudy()
}

enum StudyHomeLoadFrom: Int {
    case resource
    case home
}

class StudyHomeViewController: UIViewController {
    @IBOutlet var container: UIView!
    @IBOutlet var pageControlView: UIPageControl?
    @IBOutlet var buttonBack: UIButton!
    @IBOutlet var buttonStar: UIButton!
    @IBOutlet var buttonJoinStudy: UIButton?
    @IBOutlet var visitWebsiteButtonLeadingConstraint: NSLayoutConstraint?
    @IBOutlet var visitWebsiteButtonTrailingConstraint: NSLayoutConstraint?
    @IBOutlet var buttonVisitWebsite: UIButton?
    @IBOutlet var buttonViewConsent: UIButton?
    @IBOutlet var viewBottombarBg: UIView?
    @IBOutlet var viewSeperater: UIView?

    var isStudyBookMarked = false
    var isGettingJoiningDate = false
    var delegate: StudyHomeViewDontrollerDelegate?
    var hideViewConsentAfterJoining = false
    var loadViewFrom: StudyHomeLoadFrom = .home
    var isUpdatingIneligibility: Bool = false

    var consentRestorationData: Data?

    var pageViewController: PageViewController? {
        didSet {
            pageViewController?.pageViewDelegate = self
        }
    }

    override var preferredStatusBarStyle: UIStatusBarStyle {
        return .lightContent
    }

    func hideSubViews() {
        for subview in view.subviews {
            subview.isHidden = true
        }
    }

    func unHideSubViews() {
        for subview in view.subviews {
            subview.isHidden = false
        }
    }
    
    func studyEnrollmentFinished() {
        NotificationCenter.default.post(name: NSNotification.Name(rawValue: "NotificationStudyEnrollmentCompleted"), object: nil)
    }
    func studyEnrollmentFailed(error:NSError?) {
        
        NotificationCenter.default.post(name:NSNotification.Name(rawValue: "NotificationStudyEnrollmentFailed"), object: error)
        //let message = error.localizedDescription
        //UIUtilities.showAlertWithTitleAndMessage(title: NSLocalizedString(kErrorTitle, comment: "") as NSString, message: message as NSString)
    }
    
    func studyEnrollmentStarted(taskViewController:ORKTaskViewController) {
        
        let consent = ConsentBuilder.currentConsent
        let consentResult = consent?.consentResult
        /* Save Consent Document In Database and Consent Result*/
        consentResult?.consentDocument = ConsentBuilder.currentConsent?.consentDocument
        consentResult?.initWithORKTaskResult(taskResult: taskViewController.result)
        
        // save consent to study
        Study.currentStudy?.signedConsentVersion = consent?.version!
        Study.currentStudy?.signedConsentFilePath = consentResult?.consentPath!
        
        // save also in DB
        DBHandler.saveConsentInformation(study: Study.currentStudy!)
        
        //addProgressIndicator()
        
        // restoring token in case of ineligibility
        if (consentResult?.token) == nil || (consentResult?.token)?.isEmpty == true {
            let appdelegate = (UIApplication.shared.delegate as? AppDelegate)!
            consentResult?.token = appdelegate.consentToken
        }
        
        LabKeyServices().enrollForStudy(studyId: (Study.currentStudy?.studyId)!, token: (ConsentBuilder.currentConsent?.consentResult?.token)!, delegate: self)
    }

    // MARK: - Viewcontroller Lifecycle

    override func viewDidLoad() {
        super.viewDidLoad()

        // self.loadTestData()
        automaticallyAdjustsScrollViewInsets = false
        // Added to change next screen
        pageControlView?.addTarget(self, action: #selector(StudyHomeViewController.didChangePageControlValue), for: .valueChanged)

        // pageViewController?.overview = Gateway.instance.overview
        navigationController?.setNavigationBarHidden(true, animated: true)

        if User.currentUser.userType == UserType.AnonymousUser {
            buttonStar.isHidden = true
        } else {
            if User.currentUser.isStudyBookmarked(studyId: (Study.currentStudy?.studyId)!) {
                buttonStar.isSelected = true
            }
        }

        // ConsentToken will be used in case of ineligibility
        let appdelegate = (UIApplication.shared.delegate as? AppDelegate)!
        appdelegate.consentToken = ""

        if Utilities.isStandaloneApp(), loadViewFrom == .home {
            if User.currentUser.authToken != nil, User.currentUser.authToken.count > 0 {
                unwindeToStudyHome(nil)
            }
        }

        let brandingDetail = Utilities.getBrandingDetails()
        let joinStudyTitle = brandingDetail?[BrandingConstant.JoinStudyButtonTitle] as? String
        if joinStudyTitle != nil {
            buttonJoinStudy?.setTitle(joinStudyTitle, for: .normal)
        }

        if let visitWebsite = brandingDetail?[BrandingConstant.VisitWebsiteButtonTitle] as? String {
            buttonVisitWebsite?.setTitle(visitWebsite, for: .normal)
        }
        if let viewConsent = brandingDetail?[BrandingConstant.ViewConsentButtonTitle] as? String {
            buttonViewConsent?.setTitle(viewConsent, for: .normal)
        }
    }

    override func viewWillAppear(_ animated: Bool) {
        super.viewWillAppear(animated)
        // hide navigationbar

        setNeedsStatusBarAppearanceUpdate()
        navigationController?.setNavigationBarHidden(true, animated: true)
        if Utilities.isValidValue(someObject: Study.currentStudy?.overview.websiteLink as AnyObject?) == false {
            // if website link is nil

            buttonVisitWebsite?.isHidden = true
            visitWebsiteButtonLeadingConstraint?.constant = UIScreen.main.bounds.size.width / 2 - 13
            viewSeperater?.isHidden = true
        } else {
            buttonVisitWebsite?.isHidden = false
            visitWebsiteButtonLeadingConstraint?.constant = 0
            viewSeperater?.isHidden = false
        }

        // If coming from Activity Resources
        if hideViewConsentAfterJoining == true {
            if Utilities.isValidValue(someObject: Study.currentStudy?.overview.websiteLink as AnyObject?) {
                buttonVisitWebsite?.isHidden = false
                visitWebsiteButtonLeadingConstraint?.constant = -184
                view.layoutIfNeeded()
                buttonVisitWebsite?.layoutIfNeeded()

                buttonJoinStudy?.isHidden = true
                viewSeperater?.isHidden = true
                buttonViewConsent?.isHidden = true
            } else {
                buttonVisitWebsite?.isHidden = true
                buttonVisitWebsite?.isUserInteractionEnabled = false

                buttonJoinStudy?.isHidden = true
                viewSeperater?.isHidden = true
                buttonViewConsent?.isHidden = true
            }
        }

        // Standalone App Settings
        if Utilities.isStandaloneApp() {
            buttonStar.isHidden = true
            buttonBack.isHidden = true
            if loadViewFrom == .home {
                buttonBack.setImage(UIImage(named: "menu_icn"), for: .normal)
                buttonBack.tag = 200
                slideMenuController()?.leftPanGesture?.isEnabled = false
            }
        }
    }

    override func viewDidAppear(_ animated: Bool) {
        super.viewDidAppear(animated)
    }

    func logout() {
        if User.currentUser.authToken != nil, User.currentUser.authToken.count > 0 {
            // logout

            let user = User.currentUser
            let headerParams = [kUserId: user.userId!]
            let params = [kUserLogoutReason: user.logoutReason.rawValue]

            let method = RegistrationMethods.logout.method
            NetworkManager.sharedInstance().composeRequest(RegistrationServerConfiguration.configuration,
                                                           method: method,
                                                           params: params as NSDictionary,
                                                           headers: headerParams as NSDictionary,
                                                           delegate: self)

            let appDomain = Bundle.main.bundleIdentifier!
            UserDefaults.standard.removePersistentDomain(forName: appDomain)
            UserDefaults.standard.synchronize()

            // Delete from database
            DBHandler.deleteCurrentUser()

            // reset user object
            User.resetCurrentUser()

            // remove passcode
            ORKPasscodeViewController.removePasscodeFromKeychain()
        }
    }

    // MARK: -

    /**

     This method Loads the test data from StudyOverview plist file

     */
    func loadTestData() {
        //        let filePath  = Bundle.main.path(forResource: "GatewayOverview", ofType: "json")
        //        let data = NSData(contentsOfFile: filePath!)

        // load plist info
        let plistPath = Bundle.main.path(forResource: "StudyOverview", ofType: ".plist", inDirectory: nil)
        let arrayContent = NSMutableArray(contentsOfFile: plistPath!)

        do {
            // let response = try JSONSerialization.jsonObject(with: data! as Data, options: []) as? Dictionary<String,Any>

            // let overviewList = response[kOverViewInfo] as! Array<Dictionary<String,Any>>
            var listOfOverviews: [OverviewSection] = []
            for overview in arrayContent! {
                let overviewObj = OverviewSection(detail: (overview as? [String: Any])!)
                listOfOverviews.append(overviewObj)
            }

            // create new Overview object
            let overview = Overview()
            overview.type = .study
            overview.sections = listOfOverviews

            // assgin to Gateway
            Gateway.instance.overview = overview

        } catch {
            print("json error: \(error.localizedDescription)")
        }
    }

    /**

     This Methd creates eligibility Consent Task

     */
    func createEligibilityConsentTask() {
        var eligibilitySteps = EligibilityBuilder.currentEligibility?.getEligibilitySteps()

        let taskViewController: ORKTaskViewController?

        let consentTask: ORKOrderedTask? = ConsentBuilder.currentConsent?.createConsentTask() as! ORKOrderedTask?

        for stepDict in (consentTask?.steps)! { 
            eligibilitySteps?.append(stepDict)
        }

        var orkOrderedTask: ORKTask? = ORKOrderedTask(identifier: kEligibilityConsentTask, steps: eligibilitySteps)

        if EligibilityBuilder.currentEligibility?.type == .test || EligibilityBuilder.currentEligibility?.type == .both {
            orkOrderedTask = ORKNavigableOrderedTask(identifier: kEligibilityConsentTask, steps: eligibilitySteps)

            var i: Int? = 0

            for orkstep in eligibilitySteps! {
                if orkstep.isKind(of: ORKQuestionStep.self), (orkstep as? ORKQuestionStep)?.answerFormat?.questionType == .boolean {
                    var defaultStepIdentifier: String = ""

                    var choicePredicate: [NSPredicate] = [NSPredicate]()

                    var destination: [String]? = [String]()

                    let resultSelector: ORKResultSelector?

                    var predicateRule: ORKPredicateStepNavigationRule?

                    resultSelector = ORKResultSelector(stepIdentifier: orkstep.identifier, resultIdentifier: orkstep.identifier)

                    if i! + 1 < (eligibilitySteps?.count)! {
                        defaultStepIdentifier = (eligibilitySteps?[i! + 1].identifier)!
                    } else {
                        defaultStepIdentifier = "CompletionStep"
                    }

                    var correctAnswerArray: [[String: Any]]? = [[String: Any]]()

                    if (EligibilityBuilder.currentEligibility?.correctAnswers?.count)! > 0 {
                        // getting correct answer dict for current step

                        correctAnswerArray = EligibilityBuilder.currentEligibility?.correctAnswers?.filter { ($0[kEligibilityCorrectAnswerKey] as? String) == orkstep.identifier }
                    } else {
                        // there are no correct answers
                    }

                    if (correctAnswerArray?.count)! > 0 {
                        if (correctAnswerArray?.count)! == 1 {
                            // only for correct answer go to next or else go to ineligible screen

                            let correctAnswerDict = correctAnswerArray?.first

                            let choiceA: Bool! = (correctAnswerDict?[kEligibilityCorrectAnswer] as? Bool)!
                            var predicateQuestionChoiceA: NSPredicate = NSPredicate()
                            predicateQuestionChoiceA = ORKResultPredicate.predicateForBooleanQuestionResult(with: resultSelector!, expectedAnswer: choiceA)
                            let inverseChoiceB = (correctAnswerDict?[kEligibilityCorrectAnswer] as? Bool) == true ? false : true

                            let predicateQuestionChoiceB = ORKResultPredicate.predicateForBooleanQuestionResult(with: resultSelector!, expectedAnswer: inverseChoiceB)

                            destination?.append(kInEligibilityStep) // inEligible completion step
                            choicePredicate.append(predicateQuestionChoiceB)

                            // following is to jump to visual/review step after eligibilitytest

                            if defaultStepIdentifier == kInEligibilityStep {
                                let nextStepAfterIneligibilityIdentifier = (eligibilitySteps?[i! + 2].identifier)!

                                destination?.append(nextStepAfterIneligibilityIdentifier)
                                choicePredicate.append(predicateQuestionChoiceA)
                            }

                        } else if (correctAnswerArray?.count) == 2 {
                            // for both answers pass to next question no need of branching, provided that this is not last question

                            var nextStep: ORKStep?

                            if i! + 1 < (eligibilitySteps?.count)! {
                                nextStep = (eligibilitySteps?[i! + 1])!
                            }

                            if nextStep != nil, nextStep is InEligibilityStep {
                                // need to jump to validated screen

                                let directRule: ORKDirectStepNavigationRule!
                                directRule = ORKDirectStepNavigationRule(destinationStepIdentifier: kEligibilityVerifiedScreen)
                                (orkOrderedTask as? ORKNavigableOrderedTask)!.setNavigationRule(directRule!, forTriggerStepIdentifier: orkstep.identifier)
                            } else {
                                // do nothing assuming that next step is some question step
                            }
                        }
                    } else {
                        // there are no correct answers
                    }

                    if choicePredicate.count > 0, (destination?.count)! > 0 {
                        print("choices in eligibility \(choicePredicate) ")
                        predicateRule = ORKPredicateStepNavigationRule(resultPredicates: choicePredicate, destinationStepIdentifiers: destination!, defaultStepIdentifier: defaultStepIdentifier, validateArrays: true)

                        (orkOrderedTask as? ORKNavigableOrderedTask)!.setNavigationRule(predicateRule!, forTriggerStepIdentifier: orkstep.identifier)
                    }
                }
                i = i! + 1
            }
        }

        if orkOrderedTask is ORKNavigableOrderedTask {
            if consentRestorationData != nil {
                taskViewController = ORKTaskViewController(task: (orkOrderedTask as? ORKNavigableOrderedTask)!, restorationData: consentRestorationData, delegate: self)

            } else {
                taskViewController = ORKTaskViewController(task: (orkOrderedTask as? ORKNavigableOrderedTask)!, taskRun: nil)
                taskViewController?.outputDirectory = FileManager.default.urls(for: .documentDirectory, in: .userDomainMask).first!
            }
        } else {
            if consentRestorationData != nil {
                taskViewController = ORKTaskViewController(task: orkOrderedTask, restorationData: consentRestorationData, delegate: self)

            } else {
                taskViewController = ORKTaskViewController(task: orkOrderedTask, taskRun: nil)
                taskViewController?.outputDirectory = FileManager.default.urls(for: .documentDirectory, in: .userDomainMask).first!
            }
        }

        taskViewController?.delegate = self
        taskViewController?.title = "Activity"
        taskViewController?.navigationItem.title = nil
        taskViewController?.isNavigationBarHidden = false
        UIView.appearance(whenContainedInInstancesOf: [ORKTaskViewController.self]).tintColor = kUIColorForSubmitButtonBackground

        // UIApplication.shared.statusBarStyle = .default
        setNeedsStatusBarAppearanceUpdate()
        taskViewController?.modalPresentationStyle = .fullScreen
        present(taskViewController!, animated: true, completion: {
            let appdelegate: AppDelegate? = UIApplication.shared.delegate as? AppDelegate

            if appdelegate?.retryView?.isHidden == false {
                appdelegate?.retryView?.isHidden = true
                appdelegate?.retryView?.removeFromSuperview()
            }
        })
    }

    /**
     This Method displays consent Document
     */
    func displayConsentDocument() {
        if Study.currentStudy?.consentDocument != nil {
            if Study.currentStudy?.consentDocument?.htmlString != nil {
                navigateToWebView(link: nil, htmlText: (Study.currentStudy?.consentDocument?.htmlString)!, isEmailAvailable: true)
            }
        }
    }

    /**
     This Method is used when the user taps on the pageControl to change its
     current page (Commented as this is not working)
     */
    @objc func didChangePageControlValue() {
        // pageViewController?.scrollToViewController(index: (pageControlView?.currentPage)!)
    }

    /**
     This Method is used to push screen back to Studydashboard tabbar controller
     */
    func pushToStudyDashboard() {
        let studyDashboard = (storyboard?.instantiateViewController(withIdentifier: kStudyDashboardTabbarControllerIdentifier) as? StudyDashboardTabbarViewController)!
        navigationController?.pushViewController(studyDashboard, animated: true)
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
        navigationController?.present(taskViewController, animated: false, completion: nil)
    }

    @objc func updateEligibilityConsentStatus() {
        let notificationName = Notification.Name(kPDFCreationNotificationId)
        // Stop listening notification
        NotificationCenter.default.removeObserver(self, name: notificationName, object: nil)

        UserServices().updateUserEligibilityConsentStatus(eligibilityStatus: true, consentStatus: (ConsentBuilder.currentConsent?.consentStatus)!, delegate: self)
    }

    // MARK: - Button Actions

    /**
     This Method Join Study button clicked
     @param sender  Accepts UIButton object
     */
    @IBAction func buttonActionJoinStudy(_: UIButton) {
        if User.currentUser.userType == UserType.AnonymousUser {
            // let leftController = slideMenuController()?.leftViewController as! LeftMenuViewController
            // leftController.changeViewController(.reachOut_signIn)

            let loginStoryBoard = UIStoryboard(name: kLoginStoryboardIdentifier, bundle: nil)
            let signInController = (loginStoryBoard.instantiateViewController(withIdentifier: String(describing: SignInViewController.classForCoder())) as? SignInViewController)!
            signInController.viewLoadFrom = .joinStudy
            navigationController?.pushViewController(signInController, animated: true)

        } else {
            let currentStudy = Study.currentStudy!
            let participatedStatus = currentStudy.userParticipateState.status

            switch currentStudy.status {
            case .Active:

                if Utilities.isStandaloneApp() {
                    logout()
                }

                if participatedStatus == .yetToJoin || participatedStatus == .notEligible {
                    // check if enrolling is allowed
                    if currentStudy.studySettings.enrollingAllowed {
                        WCPServices().getEligibilityConsentMetadata(studyId: (Study.currentStudy?.studyId)!, delegate: self as NMWebServiceDelegate)
                    } else {
                        UIUtilities.showAlertWithTitleAndMessage(title: "", message: NSLocalizedString(kMessageForStudyEnrollingNotAllowed, comment: "") as NSString)
                    }
                } else if participatedStatus == .Withdrawn {
                    // check if rejoining is allowed after withrdrawn from study
                    if currentStudy.studySettings.rejoinStudyAfterWithdrawn {
                        WCPServices().getEligibilityConsentMetadata(studyId: (Study.currentStudy?.studyId)!, delegate: self as NMWebServiceDelegate)
                    } else {
                        UIUtilities.showAlertWithTitleAndMessage(title: "", message: NSLocalizedString(kMessageForStudyWithdrawnState, comment: "") as NSString)
                    }
                }
            case .Upcoming:
                UIUtilities.showAlertWithTitleAndMessage(title: "", message: NSLocalizedString(kMessageForStudyUpcomingState, comment: "") as NSString)
            case .Paused:
                UIUtilities.showAlertWithTitleAndMessage(title: "", message: NSLocalizedString(kMessageForStudyPausedState, comment: "") as NSString)
            case .Closed:
                UIUtilities.showAlertWithTitleAndMessage(title: "", message: NSLocalizedString(kMessageForStudyClosedState, comment: "") as NSString)
            }
        }
    }

    /**
     This method is used to navigate to previous view controller
     @param sender    Accepts any kind of objects
     */
    @IBAction func backButtonAction(_ sender: Any) {
        let button = sender as! UIButton
        if button.tag == 200 {
            slideMenuController()?.openLeft()
        } else {
            _ = navigationController?.popViewController(animated: true)
        }
    }

    /**
     This method is start button Action
     @param sender    Accepts Any kind of object
     */
    @IBAction func starButtonAction(_ sender: Any) {
        let button = (sender as? UIButton)!
        var userStudyStatus: UserStudyStatus!
        let study = Study.currentStudy
        let user = User.currentUser
        if button.isSelected {
            button.isSelected = false
            userStudyStatus = user.removeBookbarkStudy(studyId: (study?.studyId)!)

        } else {
            button.isSelected = true
            userStudyStatus = user.bookmarkStudy(studyId: (study?.studyId)!)
        }

        isStudyBookMarked = true
        UserServices().updateStudyBookmarkStatus(studyStauts: userStudyStatus, delegate: self)
    }

    /**
     This method is visit website button action
     @param sender    Accepts UIButton object
     */
    @IBAction func visitWebsiteButtonAction(_ sender: UIButton) {
        if sender.tag == 1188 {
            // Visit Website

            navigateToWebView(link: Study.currentStudy?.overview.websiteLink, htmlText: nil, isEmailAvailable: false)

        } else {
            // View Consent

            if Study.currentStudy?.studyId != nil {
                WCPServices().getConsentDocument(studyId: (Study.currentStudy?.studyId)!, delegate: self as NMWebServiceDelegate)
            }
        }
    }

    /**

     This method is unwind to study home
     @param segue    the segue used to connect the View controller
     */
    @IBAction func unwindeToStudyHome(_: UIStoryboardSegue?) {
        hideSubViews()

        if UserDefaults.standard.bool(forKey: kPasscodeIsPending) {
            UserServices().getUserProfile(self as NMWebServiceDelegate)
        } else {
            UserServices().getStudyStates(self)
        }
    }

    // MARK: - Response Handlers

    func handleResponseForStudyState() {
        let currentUser = User.currentUser
        let study = Study.currentStudy!
        if let studyStatus = currentUser.participatedStudies.filter({ $0.studyId == Study.currentStudy?.studyId }).last {
            Study.currentStudy?.userParticipateState = studyStatus

            if study.status == .Active {
                if studyStatus.status == .inProgress {
                    // go to study dashboard
                    removeProgressIndicator()
                    pushToStudyDashboard()
                } else if studyStatus.status == .yetToJoin {
                    // check if enrolling is allowed
                    if study.studySettings.enrollingAllowed {
                        WCPServices().getEligibilityConsentMetadata(studyId: (Study.currentStudy?.studyId)!, delegate: self as NMWebServiceDelegate)
                    } else {
                        // unhide view
                        removeProgressIndicator()
                        unHideSubViews()
                    }

                } else {
                    // unhide view
                    removeProgressIndicator()
                    unHideSubViews()
                }
            } else {
                // unhide view
                removeProgressIndicator()
                unHideSubViews()
                if Utilities.isStandaloneApp() {
                    buttonBack.isHidden = true
                    buttonStar.isHidden = true
                }
            }

        } else {
            if study.status == .Active {
                if study.studySettings.enrollingAllowed {
                    WCPServices().getEligibilityConsentMetadata(studyId: (Study.currentStudy?.studyId)!, delegate: self as NMWebServiceDelegate)
                } else {
                    removeProgressIndicator()
                    unHideSubViews()
                }
            } else {
                removeProgressIndicator()
                unHideSubViews()
            }
        }
    }
    
    func handleStudyEnrollmentResponse(response:[String:Any]) {
        
        let tokenDict = (response["data"] as? [String: Any])!
        if Utilities.isValidObject(someObject: tokenDict as AnyObject?) {
            let apptoken = (tokenDict["appToken"] as? String)!
            
            // update token
            let currentUserStudyStatus = User.currentUser.updateStudyStatus(studyId: (Study.currentStudy?.studyId)!, status: .inProgress)
            currentUserStudyStatus.participantId = apptoken
            Study.currentStudy?.userParticipateState = currentUserStudyStatus
            
            DBHandler.updateStudyParticipationStatus(study: Study.currentStudy!)
            
            ConsentBuilder.currentConsent?.consentStatus = .completed
            UserServices().updateUserParticipatedStatus(studyStauts: currentUserStudyStatus, delegate: self)
        }
        
    }

    // MARK: - Segue Methods

    override func prepare(for segue: UIStoryboardSegue, sender _: Any?) {
        if let pageViewController = segue.destination as? PageViewController {
            pageViewController.pageViewDelegate = self
            pageViewController.overview = Study.currentStudy?.overview
        }
    }

    /**
     This Method navigates to weblink
     @param link        the link to go for desired destination
     @param htmlText    displays the text in the webview
     */
    func navigateToWebView(link: String?, htmlText: String?, isEmailAvailable: Bool?) {
        let loginStoryboard = UIStoryboard(name: "Main", bundle: Bundle.main)
        let webViewController = (loginStoryboard.instantiateViewController(withIdentifier: "WebViewController") as? UINavigationController)!
        let webView = (webViewController.viewControllers[0] as? WebViewController)!
        webView.isEmailAvailable = isEmailAvailable!

        if link != nil {
            webView.requestLink = Study.currentStudy?.overview.websiteLink
        }
        if htmlText != nil {
            webView.htmlString = htmlText
        }
        navigationController?.present(webViewController, animated: true, completion: nil)
    }
}

extension StudyHomeViewController: ComprehensionFailureDelegate {
    func didTapOnRetry() {
        createEligibilityConsentTask()
    }

    func didTapOnCancel() {
        consentRestorationData = nil
    }
}

// MARK: - PageControl Delegates for handling Counts

extension StudyHomeViewController: PageViewControllerDelegate {
    func pageViewController(pageViewController _: PageViewController, didUpdatePageCount count: Int) {
        pageControlView?.numberOfPages = count
    }

    func pageViewController(pageViewController _: PageViewController, didUpdatePageIndex index: Int) {
        pageControlView?.currentPage = index

        buttonJoinStudy?.layer.borderColor = kUicolorForButtonBackground
        if index == 0 {
            // for First Page

            UIView.animate(withDuration: 0.1, animations: {
                self.buttonJoinStudy?.backgroundColor = kUIColorForSubmitButtonBackground
                self.buttonJoinStudy?.setTitleColor(UIColor.white, for: .normal)

            })
        } else {
            // for All other pages
            UIView.animate(withDuration: 0.1, animations: {
                self.buttonJoinStudy?.backgroundColor = UIColor.white
                self.buttonJoinStudy?.setTitleColor(kUIColorForSubmitButtonBackground, for: .normal)
            })
        }
    }
}

// MARK: - Webservice Delegates

extension StudyHomeViewController: NMWebServiceDelegate {
    func startedRequest(_: NetworkManager, requestName: NSString) {
        Logger.sharedInstance.info("requestname startedRequest : \(requestName)")

        if requestName as String == RegistrationMethods.logout.method.methodName {
        } else {
            addProgressIndicator()
        }
    }

    func finishedRequest(_: NetworkManager, requestName: NSString, response: AnyObject?) {
        Logger.sharedInstance.info("requestname : \(requestName)")

        if requestName as String == WCPMethods.eligibilityConsent.method.methodName {
            removeProgressIndicator()
            createEligibilityConsentTask()
        }

        if requestName as String == RegistrationMethods.updateStudyState.method.methodName {
            if isStudyBookMarked || isUpdatingIneligibility {
                removeProgressIndicator()

                if isUpdatingIneligibility {
                    isUpdatingIneligibility = false
                }

            } else {
                if ConsentBuilder.currentConsent?.consentResult?.consentPdfData?.count == 0 {
                    // Define identifier
                    let notificationName = Notification.Name(kPDFCreationNotificationId)

                    // Register to receive notification
                    NotificationCenter.default.addObserver(self, selector: #selector(updateEligibilityConsentStatus), name: notificationName, object: nil)

                } else {
                    UserServices().updateUserEligibilityConsentStatus(eligibilityStatus: true, consentStatus: (ConsentBuilder.currentConsent?.consentStatus)!, delegate: self)
                }
            }
        }

        if requestName as String == ResponseMethods.enroll.description {
            if Utilities.isValidObject(someObject: response) {
                handleStudyEnrollmentResponse(response: response as! [String:Any])
            }
        }

        if requestName as String == RegistrationMethods.updateEligibilityConsentStatus.method.methodName {
            if User.currentUser.getStudyStatus(studyId: (Study.currentStudy?.studyId)!) == UserStudyStatus.StudyStatus.inProgress {
                isGettingJoiningDate = true
                UserServices().getStudyStates(self)
            }
        }

        if requestName as String == WCPMethods.consentDocument.method.methodName {
            removeProgressIndicator()
            displayConsentDocument()
        }

        if requestName as String == RegistrationMethods.studyState.description {
            if isGettingJoiningDate {
                // update in Study
                let currentUser = User.currentUser
                if let userStudyStatus = currentUser.participatedStudies.filter({ $0.studyId == Study.currentStudy?.studyId }).last {
                    Study.currentStudy?.userParticipateState = userStudyStatus
                }
                //pushToStudyDashboard()
                isGettingJoiningDate = false
                self.studyEnrollmentFinished()
                //removeProgressIndicator()
                
            } else {
                handleResponseForStudyState()
            }
        }

        if requestName as String == RegistrationMethods.userProfile.description {
            if User.currentUser.settings?.passcode == true {
                setPassCode()
            } else {
                UserServices().getStudyStates(self)
            }
        }
    }

    func failedRequest(_: NetworkManager, requestName: NSString, error: NSError) {
        Logger.sharedInstance.info("requestname : \(requestName)")
        removeProgressIndicator()

        if error.code == 403 { // unauthorized Access
            let appdelegate = (UIApplication.shared.delegate as? AppDelegate)!
            appdelegate.window?.removeProgressIndicatorFromWindow()
            UIUtilities.showAlertMessageWithActionHandler(kErrorTitle, message: error.localizedDescription, buttonTitle: kTitleOk, viewControllerUsed: self, action: {
                self.fdaSlideMenuController()?.navigateToHomeAfterUnauthorizedAccess()
            })
        } else {
            if requestName as String == WCPMethods.consentDocument.method.methodName {
                // self.removeProgressIndicator()
            }

            else if requestName as String == ResponseMethods.enroll.description {
                unHideSubViews()
                
                self.studyEnrollmentFailed(error: error)
                
               
            }
            else if requestName as String == RegistrationMethods.updateStudyState.method.methodName
                || requestName as String == RegistrationMethods.updateEligibilityConsentStatus.method.methodName {
                unHideSubViews()

                let message = error.localizedDescription as NSString
                if message.length != 0 {
                    UIUtilities.showAlertWithTitleAndMessage(title: NSLocalizedString(kErrorTitle, comment: "") as NSString, message: error.localizedDescription as NSString)
                } else {
                    UIUtilities.showAlertMessageWithActionHandler(kErrorTitle, message: "Unknown error occurred. Please try after some time.", buttonTitle: kTitleOk, viewControllerUsed: self, action: {
                        self.navigationController?.popViewController(animated: true)
                    })
                }
            }
            else {
                 UIUtilities.showAlertWithTitleAndMessage(title: NSLocalizedString(kErrorTitle, comment: "") as NSString, message: error.localizedDescription as NSString)
            }
        }
    }
}

// MARK: - ORKTaskViewController Delegate

extension StudyHomeViewController: ORKTaskViewControllerDelegate {
    func taskViewControllerSupportsSaveAndRestore(_: ORKTaskViewController) -> Bool {
        return true
    }

    public func taskViewController(_ taskViewController: ORKTaskViewController, didFinishWith reason: ORKTaskViewControllerFinishReason, error error: Error?) {
        consentRestorationData = nil

        if taskViewController.task?.identifier == kPasscodeTaskIdentifier {
            let ud = UserDefaults.standard
            ud.set(false, forKey: kPasscodeIsPending)
            ud.synchronize()

            taskViewController.dismiss(animated: true, completion: {
                // call api to get all study stats
                UserServices().getStudyStates(self)
            })
            return
        }

        var taskResult: Any?
        switch reason {
        case ORKTaskViewControllerFinishReason.completed:
            print("completed")
            taskResult = taskViewController.result
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

        let lastStepResultIdentifier: String?

        lastStepResultIdentifier = (taskViewController.result.results?.last as? ORKStepResult)!.identifier

        if taskViewController.task?.identifier == kEligibilityConsentTask, reason == ORKTaskViewControllerFinishReason.completed, lastStepResultIdentifier != kInEligibilityStep {
            hideSubViews()

//            let consent = ConsentBuilder.currentConsent
//            let consentResult = consent?.consentResult
//            /* Save Consent Document In Database and Consent Result*/
//            consentResult?.consentDocument = ConsentBuilder.currentConsent?.consentDocument
//            consentResult?.initWithORKTaskResult(taskResult: taskViewController.result)
//
//            // save consent to study
//            Study.currentStudy?.signedConsentVersion = consent?.version!
//            Study.currentStudy?.signedConsentFilePath = consentResult?.consentPath!
//
//            // save also in DB
//            DBHandler.saveConsentInformation(study: Study.currentStudy!)
//
//            addProgressIndicator()
//
//            // restoring token in case of ineligibility
//            if (consentResult?.token) == nil || (consentResult?.token)?.isEmpty == true {
//                let appdelegate = (UIApplication.shared.delegate as? AppDelegate)!
//                consentResult?.token = appdelegate.consentToken
//            }

            //LabKeyServices().enrollForStudy(studyId: (Study.currentStudy?.studyId)!, token: (ConsentBuilder.currentConsent?.consentResult?.token)!, delegate: self)
            addProgressIndicator()
            taskViewController.dismiss(animated: true) {
                self.pushToStudyDashboard()
                self.removeProgressIndicator()
            }

        } else {
            // activityBuilder?.actvityResult?.initWithORKTaskResult(taskResult: taskViewController.result)

            if reason == ORKTaskViewControllerFinishReason.discarded {
                unHideSubViews()
                // _ = self.navigationController?.popViewController(animated: true)

                if Study.currentStudy?.userParticipateState.status == .notEligible {
                    // checking if validated or verified screen is present in results so status can be reverted back to yet To join
                    let results = taskViewController.result.results?.contains(where: { $0.identifier == kEligibilityVerifiedScreen || $0.identifier == kEligibilityValidateScreen
                    })

                    if results! {
                        let currentUserStudyStatus = User.currentUser.updateStudyStatus(studyId: (Study.currentStudy?.studyId)!, status: .yetToJoin)

                        Study.currentStudy?.userParticipateState = currentUserStudyStatus

                        DBHandler.updateStudyParticipationStatus(study: Study.currentStudy!)

                        isUpdatingIneligibility = true

                        UserServices().updateUserParticipatedStatus(studyStauts: currentUserStudyStatus, delegate: self)
                    }
                }
            }
            taskViewController.dismiss(animated: true) {
                if reason == ORKTaskViewControllerFinishReason.failed {
                    let messge = error == nil ? "Something went wrong please try again later." : error?.localizedDescription
                    UIUtilities.showAlertWithTitleAndMessage(title: NSLocalizedString(kErrorTitle, comment: "") as NSString, message:messge! as NSString)
                }
            }
        }
    }

    func taskViewController(_ taskViewController: ORKTaskViewController, stepViewControllerWillAppear stepViewController: ORKStepViewController) {
        if (taskViewController.result.results?.count)! > 1 {
            if activityBuilder?.actvityResult?.result?.count == taskViewController.result.results?.count {
                // Removing the dummy result:Currentstep result which not presented yet
                activityBuilder?.actvityResult?.result?.removeLast()
            } else {}
        }

        
        // For Verified Step , Completion Step, Visual Step, Review Step, Share Pdf Step
        let stepIndentifer = stepViewController.step?.identifier
        if stepIndentifer == kEligibilityVerifiedScreen
            || stepIndentifer == kConsentCompletionStepIdentifier
            || stepIndentifer == kVisualStepId
            || stepIndentifer == kConsentSharePdfCompletionStep
            || stepIndentifer == kInEligibilityStep
            || stepIndentifer == kEligibilityValidateScreen
            || stepIndentifer == kConsentSharing
            || stepIndentifer == kReviewTitle
            || stepIndentifer == kComprehensionInstructionStepIdentifier {
            
            

            if stepIndentifer == kEligibilityVerifiedScreen {
                stepViewController.continueButtonTitle = kContinueButtonTitle
            }

            if stepIndentifer == kVisualStepId {
                consentRestorationData = Data(count: 0)

                if taskViewController.restorationData != nil {
                    consentRestorationData = taskViewController.restorationData
                }

            } else if stepIndentifer == kComprehensionInstructionStepIdentifier {
                let insvisibleConsents = ConsentBuilder.currentConsent?.getVisualConsentStep()

                if insvisibleConsents == nil {
                    consentRestorationData = Data()

                    if taskViewController.restorationData != nil {
                        consentRestorationData = taskViewController.restorationData
                    }
                }
            }

            stepViewController.backButtonItem = nil
            
        }
        // checking if currentstep is View Pdf Step
        else if stepIndentifer == kConsentViewPdfCompletionStep {
            // Back button is enabled
             stepViewController.backButtonItem = nil

            let orkStepResult: ORKStepResult? = taskViewController.result.results?[(taskViewController.result.results?.count)! - 2] as! ORKStepResult?

            let consentSignatureResult: ConsentCompletionTaskResult? = orkStepResult?.results?.first as? ConsentCompletionTaskResult

            // Checking if Signature is consented after Review Step

            if consentSignatureResult?.didTapOnViewPdf == false {
                // Directly moving to completion step by skipping Intermediate PDF viewer screen
                stepViewController.goForward()
            } else {}
            
        } else {
            // Back button is enabled

            if taskViewController.task?.identifier == kEligibilityConsentTask {
                stepViewController.backButtonItem = nil
            } else {
                stepViewController.backButtonItem?.isEnabled = true
            }
        }
    }

    // MARK: - StepViewController Delegate

    public func stepViewController(_: ORKStepViewController, didFinishWith _: ORKStepViewControllerNavigationDirection) {}

    public func stepViewControllerResultDidChange(_: ORKStepViewController) {}

    public func stepViewControllerDidFail(_: ORKStepViewController, withError _: Error?) {}

    func taskViewController(_ taskViewController: ORKTaskViewController, viewControllerFor step: ORKStep) -> ORKStepViewController? {
        // CurrentStep is TokenStep

        if step.identifier == kEligibilityTokenStep {
            let gatewayStoryboard = UIStoryboard(name: kFetalKickCounterStep, bundle: nil)

            let ttController = (gatewayStoryboard.instantiateViewController(withIdentifier: kEligibilityStepViewControllerIdentifier) as? EligibilityStepViewController)!

            ttController.descriptionText = step.text
            ttController.step = step

            return ttController
        } else if step.identifier == kConsentSharePdfCompletionStep {
            var totalResults = taskViewController.result.results
            let reviewStep: ORKStepResult?

            totalResults = totalResults?.filter { $0.identifier == kReviewTitle }

            reviewStep = totalResults?.first as! ORKStepResult?
            
//            DispatchQueue.main.asyncAfter(deadline: .now() + 2) {
//                //self.studyEnrollmentFailed(error: nil)
//                self.studyEnrollmentFinished()
//            }
           
            if (reviewStep?.identifier)! == kReviewTitle, (reviewStep?.results?.count)! > 0 {
                let consentSignatureResult: ORKConsentSignatureResult? = reviewStep?.results?.first as? ORKConsentSignatureResult

                if consentSignatureResult?.consented == false {
                    taskViewController.dismiss(animated: true,
                                               completion: nil)
                    _ = navigationController?.popViewController(animated: true)
                    return nil

                } else {
                    let documentCopy: ORKConsentDocument = ((ConsentBuilder.currentConsent?.consentDocument)!.copy() as? ORKConsentDocument)!
                    consentSignatureResult?.apply(to: documentCopy)

                    let gatewayStoryboard = UIStoryboard(name: kFetalKickCounterStep, bundle: nil)
                    let ttController = (gatewayStoryboard.instantiateViewController(withIdentifier: kConsentSharePdfStoryboardId) as? ConsentSharePdfStepViewController)!
                    ttController.step = step
                    ttController.consentDocument = documentCopy
                    
                    //start enrollment process
                    if Study.currentStudy?.userParticipateState.status == UserStudyStatus.StudyStatus.yetToJoin {
                        DispatchQueue.main.asyncAfter(deadline: .now() + 1) {
                            self.studyEnrollmentStarted(taskViewController: taskViewController)
                        }
                    }
                    
                    return ttController
                }
            } else {
                return nil
            }
        } else if step.identifier == kConsentViewPdfCompletionStep {
            let reviewSharePdfStep: ORKStepResult? = taskViewController.result.results?.last as! ORKStepResult?

            let result = (reviewSharePdfStep?.results?.first as? ConsentCompletionTaskResult)

            if (result?.didTapOnViewPdf)! {
                let gatewayStoryboard = UIStoryboard(name: kFetalKickCounterStep, bundle: nil)

                let ttController = (gatewayStoryboard.instantiateViewController(withIdentifier: kConsentViewPdfStoryboardId) as? ConsentPdfViewerStepViewController)!
                ttController.step = step

                ttController.pdfData = result?.pdfData

                return ttController
            } else {
                //taskViewController.goForward()
                return nil
            }
        } else if step.identifier == kInEligibilityStep {
            let gatewayStoryboard = UIStoryboard(name: kFetalKickCounterStep, bundle: nil)

            let ttController = (gatewayStoryboard.instantiateViewController(withIdentifier: kInEligibilityStepViewControllerIdentifier) as? InEligibilityStepViewController)!

            ttController.step = step

            return ttController
        } else if step.identifier == kEligibilityVerifiedScreen {
            let lastStepResultIdentifier: String?
            lastStepResultIdentifier = (taskViewController.result.results?.last as? ORKStepResult)!.identifier

            if lastStepResultIdentifier == kInEligibilityStep {
                let currentUserStudyStatus = User.currentUser.updateStudyStatus(studyId: (Study.currentStudy?.studyId)!, status: .notEligible)

                Study.currentStudy?.userParticipateState = currentUserStudyStatus

                DBHandler.updateStudyParticipationStatus(study: Study.currentStudy!)

                unHideSubViews()
                dismiss(animated: true, completion: {
                    self.isUpdatingIneligibility = true

                    UserServices().updateUserParticipatedStatus(studyStauts: currentUserStudyStatus, delegate: self)
                })
                return nil
            } else {
                return nil
            }
        } else if step.identifier == kComprehensionCompletionStepIdentifier {
            // previous step was consent sharing and validation of answers(if comprehension test exists) is already done

            // comprehension test is available
            if (ConsentBuilder.currentConsent?.comprehension?.questions?.count)! > 0 {
                let visualStepIndex: Int = (taskViewController.result.results?.index(where: { $0.identifier == kComprehensionInstructionStepIdentifier }))!

                if visualStepIndex >= 0 {
                    var i = visualStepIndex + 1 // holds the index of  question
                    var j = 0 // holds the index of correct answer

                    var userScore = 0

                    while i < (taskViewController.result.results?.count)! {
                        let textChoiceResult: ORKChoiceQuestionResult = (((taskViewController.result.results?[i] as? ORKStepResult)!.results?.first) as? ORKChoiceQuestionResult)!

                        let correctAnswerDict: [String: Any]? = ConsentBuilder.currentConsent?.comprehension?.correctAnswers?[j]

                        let answerArray: [String] = (correctAnswerDict?[kConsentComprehensionAnswer] as? [String])!

                        let evaluationType: Evaluation? = Evaluation(rawValue: (correctAnswerDict?[kConsentComprehensionEvaluation] as? String)!)

                        let answeredSet = Set((textChoiceResult.choiceAnswers! as? [String])!)

                        let correctAnswerSet = Set(answerArray)

                        switch evaluationType! {
                        case .any:

                            if answeredSet.isSubset(of: correctAnswerSet) {
                                userScore = userScore + 1
                            }

                        case .all:

                            if answeredSet == correctAnswerSet {
                                userScore = userScore + 1
                            }

                            // default: break
                        }

                        j += 1
                        i += 1
                    }

                    if userScore >= (ConsentBuilder.currentConsent?.comprehension?.passScore)! {
                        return nil
                    } else {
                        let appdelegate: AppDelegate? = UIApplication.shared.delegate as? AppDelegate
                        appdelegate?.isComprehensionFailed = true
                        appdelegate?.addRetryScreen(viewController: self)

                        dismiss(animated: true, completion: nil)
                    }

                } else {
                    // if by chance we didnt get visualStepIndex i.e there no visual step
                    // logically should never occur
                }

                return nil
            } else {
                // comprehension test is not available
                return nil
            }

        } else if step.identifier == kReviewTitle {
            // if sharing step exists && allowWithoutSharing is set

            let shareStep: ORKStepResult? = taskViewController.result.results?.last as! ORKStepResult?

            // ConsentBuilder.currentConsent?.sharingConsent?.allowWithoutSharing = false

            if shareStep?.identifier == kConsentSharing, ConsentBuilder.currentConsent?.sharingConsent != nil, (ConsentBuilder.currentConsent?.sharingConsent?.allowWithoutSharing)! == false {
                let result = (shareStep?.results?.first as? ORKChoiceQuestionResult)

                if (result?.choiceAnswers?.first as? Bool)! == true {
                    return nil
                } else {
                    dismiss(animated: true, completion: {
                        self.navigationController?.popViewController(animated: true)

                        UIUtilities.showAlertWithTitleAndMessage(title: "Message", message: NSLocalizedString(kShareConsentFailureAlert, comment: "") as NSString)
                    })
                    return nil
                }

            } else {
                return nil
            }
        } else {
            return nil
        }
    }
}
