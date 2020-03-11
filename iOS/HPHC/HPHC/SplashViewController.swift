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

let kDefaultPasscodeString = "Password123"
let kIsIphoneSimulator = "iPhone Simulator"
let kStoryboardIdentifierGateway = "Gateway"
let kStoryboardIdentifierSlideMenuVC = "FDASlideMenuViewController"

class SplashViewController: UIViewController {
    
    var isAppOpenedForFirstTime:Bool? = false
    
    // MARK:- Viewcontroller Lifecycle
    
    override func viewDidLoad() {
        super.viewDidLoad()
    }
    
    override func viewWillAppear(_ animated: Bool) {
        
        DBHandler().initilizeCurrentUser()
        
        self.checkIfAppLaunchedForFirstTime()
        
        // Checks AuthKey, If exists navigate to HomeController else GatewayDashboard
        if Utilities.isStandaloneApp() {
            self.initilizeStudyForStandaloneApp()
        } else {
            
            if User.currentUser.authToken != nil {
                
                let appDelegate = UIApplication.shared.delegate as! AppDelegate
                appDelegate.checkPasscode(viewController: self)
                self.navigateToGatewayDashboard()
                
            }else {
                    /*Gateway App*/
                    self.navigateToHomeController()
                
            }
        }
       
    }

    
    /**
     Navigating to Home Screen and load HomeViewController from Login Storyboard
     */
    func navigateToHomeController(){
        
        let loginStoryboard = UIStoryboard.init(name: kLoginStoryboardIdentifier, bundle:Bundle.main)
        let homeViewController = loginStoryboard.instantiateViewController(withIdentifier:"HomeViewController")
        self.navigationController?.pushViewController(homeViewController, animated: true)
    }
    
    func initilizeStudyForStandaloneApp() {
        
        NotificationCenter.default.addObserver(self, selector: #selector(SplashViewController.studySetupComplete), name: NSNotification.Name(rawValue: "StudySetupCompleted"), object: nil)
        
        StandaloneStudy().setupStandaloneStudy()
        
    }
    
    func initilizeStudyOverview() {
        
        let plistPath = Bundle.main.path(forResource: "StudyOverview", ofType: ".plist", inDirectory:nil)
        let arrayContent = NSMutableArray.init(contentsOfFile: plistPath!)
        
        do {
            
            var listOfOverviews:Array<OverviewSection> = []
            for overview in arrayContent!{
                let overviewObj = OverviewSection(detail: overview as! Dictionary<String, Any>)
                listOfOverviews.append(overviewObj)
            }
            
            //create new Overview object
            let overview = Overview()
            overview.type = .study
            overview.sections = listOfOverviews
            
            Study.currentStudy?.overview = overview
            //assgin to Gateway
            //Gateway.instance.overview = overview
            
            self.navigateToStudyHomeController()
            
            
        } catch {
            print("json error: \(error.localizedDescription)")
        }
    }
    
    func navigateToStudyHomeController() {
        //StudyHomeViewController
        let studyStoryBoard = UIStoryboard.init(name: kStudyStoryboard, bundle: Bundle.main)
        let studyHomeController = studyStoryBoard.instantiateViewController(withIdentifier: String(describing: StudyHomeViewController.classForCoder())) as! StudyHomeViewController
        //studyHomeController.delegate = self
        self.navigationController?.pushViewController(studyHomeController, animated: true)
    }
    
    /**
     Navigate to gateway Dashboard
     */
    func navigateToGatewayDashboard(){
        
        NotificationCenter.default.removeObserver(self, name: NSNotification.Name(rawValue:"StudySetupCompleted"), object: nil)
        self.createMenuView()
        
    }
    
    @objc func studySetupComplete(){
        print("studySetupComplete")
        //self.navigateToGatewayDashboard()
        NotificationCenter.default.removeObserver(self, name: NSNotification.Name(rawValue:"StudySetupCompleted"), object: nil)
        
        if User.currentUser.authToken != nil && User.currentUser.authToken.count > 0{
            
            let appDelegate = UIApplication.shared.delegate as! AppDelegate
            appDelegate.checkPasscode(viewController: self)
            
            //self.navigateToGatewayDashboard()
            
            self.createMenuView()
//            let userStudyStatus =  (Study.currentStudy?.userParticipateState.status)!
//           // let ud = UserDefaults.standard
//            if ud.bool(forKey: "joined")
//                //|| userStudyStatus == .yetToJoin
//            {
//                self.navigateToGatewayDashboard()
//            }
//            else {
//                self.navigateToStudyHomeController()
//            }
        }
        else {
            
            /*Gateway App*/
            //self.navigateToHomeController()
            
            /*Standalone App*/
            //self.navigateToStudyHomeController()
            self.createMenuView()
        }
        
        
    }
    
    /**
     Navigating to Study list and Load FDASlideMenuViewController from Gateway Storyboard
     */
    func createMenuView() {
        
        let storyboard = UIStoryboard(name: kStoryboardIdentifierGateway, bundle: nil)
        let fda = storyboard.instantiateViewController(withIdentifier: kStoryboardIdentifierSlideMenuVC) as! FDASlideMenuViewController
        fda.automaticallyAdjustsScrollViewInsets = true
        self.navigationController?.pushViewController(fda, animated: true)
    }
    
    //Update Encryption Key & IV on first time launch
    func checkIfAppLaunchedForFirstTime(){
        
        if isAppOpenedForFirstTime == false{
            
            let currentDate = "\(Date(timeIntervalSinceNow: 0))"
            let currentIndex = currentDate.index(currentDate.endIndex
                , offsetBy: -13)
            let subStringFromDate = String(currentDate[..<currentIndex])
            let ud = UserDefaults.standard
            
            if User.currentUser.userType == .FDAUser {
                
                let index =  User.currentUser.userId.index(User.currentUser.userId.endIndex
                    , offsetBy: -16)
                let subKey = String(User.currentUser.userId[..<index])
                ud.set("\(subKey + subStringFromDate)", forKey: kEncryptionKey)
            }
            else{
                ud.set(currentDate + kDefaultPasscodeString, forKey: kEncryptionKey)
            }
            
            
            if UIDevice.current.model == kIsIphoneSimulator {
                // simulator
                ud.set(kdefaultIVForEncryption, forKey: kEncryptionIV)
            }
            else{
                // not a simulator
                var udid = UIDevice.current.identifierForVendor?.uuidString
                
                let index =  udid?.index((udid?.endIndex)!
                    , offsetBy: -20)
                udid = String((udid?[..<index!])!)
                ud.set(udid, forKey: kEncryptionIV)
            }
            
            ud.synchronize()
        }
    }
}


// MARK:- ORKTaskViewController Delegate
extension SplashViewController:ORKTaskViewControllerDelegate{
    
    func taskViewControllerSupportsSaveAndRestore(_ taskViewController: ORKTaskViewController) -> Bool {
        return true
    }
    
    public func taskViewController(_ taskViewController: ORKTaskViewController, didFinishWith reason: ORKTaskViewControllerFinishReason, error: Error?) {
        
        switch reason {
            
        case ORKTaskViewControllerFinishReason.completed:
            print("completed")
           
        case ORKTaskViewControllerFinishReason.failed:
            print("failed")
        
        case ORKTaskViewControllerFinishReason.discarded:
            print("discarded")
            
        case ORKTaskViewControllerFinishReason.saved:
            print("saved")
            
        }
        taskViewController.dismiss(animated: true, completion: nil)
    }
    
    func taskViewController(_ taskViewController: ORKTaskViewController, stepViewControllerWillAppear stepViewController: ORKStepViewController) {
        
    }
}


