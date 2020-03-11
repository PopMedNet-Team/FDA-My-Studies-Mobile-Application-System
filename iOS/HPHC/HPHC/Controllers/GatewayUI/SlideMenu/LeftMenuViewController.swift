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
import SlideMenuControllerSwift

let kLeftMenuSubtitle = "subTitle"
let kLeftMenuTitle = "menuTitle"
let kLeftMenuIconName = "iconName"
let kLeftMenuCellTitleHome = "Home"
let kLeftMenuCellTitleResources = "Resources"
let kLeftMenuCellTitleProfile = "My Account"
let kLeftMenuCellTitleSignIn = "Sign In"
let kLeftMenuCellTitleNewUser = "New User?"
let kLeftMenuCellSubTitleValue = "Sign up"
let kAlertMessageReachoutText = "This feature will be available in the next sprint."

let kAlertMessageForSignOut = "Are you sure you want to sign out?"
let kAlertMessageSignOutSync = "Are you sure you want to sign out? Incomplete activities and activities completed while offline must be re-started when you next sign in."

let kAlertSignOutLaterTitle = "Sign Out later"

// MARK:Segue Identifiers
let kLoginStoryboardIdentifier = "Login"


enum LeftMenu: Int {
    case studyList = 0
    case resources
    case profile_reachOut
    case reachOut_signIn
    case signup
}


protocol LeftMenuProtocol: class {
    func changeViewController(_ menu: LeftMenu)
}

class LeftMenuViewController: UIViewController, LeftMenuProtocol {
    
    @IBOutlet weak var tableView: UITableView!
    @IBOutlet weak var labelVersion: UILabel!
    @IBOutlet weak var labelProductName: UILabel!
    @IBOutlet weak var tableHeaderView: UIView!
    @IBOutlet weak var tableFooterView: UIView!
    @IBOutlet weak var buttonSignOut: UIButton?
    
    var menus: [[String: Any]] = [ ["menuTitle": "Home",
                   "iconName": "home_menu1-1",
                   "menuType": LeftMenu.studyList],
                  
                  ["menuTitle": "Resources",
                   "iconName": "resources_menu1",
                   "menuType": LeftMenu.resources],
    ]
    // standalone
    var studyTabBarController: UITabBarController!
    var studyHomeViewController: UINavigationController!
    
    // Gateway & standalone
    var studyListViewController: UINavigationController!
    var notificationController: UIViewController!
    var resourcesViewController: UINavigationController!
    var profileviewController: UIViewController!
    var nonMenuViewController: UIViewController!
    var reachoutViewController: UINavigationController!
    var signInViewController: UINavigationController!
    var signUpViewController: UINavigationController!
    
    required init?(coder aDecoder: NSCoder) {
        super.init(coder: aDecoder)
    }
    
// MARK:- ViewController Lifecycle
    override func viewDidLoad() {
        super.viewDidLoad()
        
        self.view.isHidden = true
        self.createLeftmenuItems()
        
        var infoDict: NSDictionary?
        if let path = Bundle.main.path(forResource: "Info", ofType: "plist") {
            infoDict = NSDictionary(contentsOfFile: path)
        }
        let navTitle = infoDict!["ProductTitleName"] as! String
        labelProductName.text = navTitle
        
        self.tableView.separatorColor = UIColor(red: 224/255, green: 224/255, blue: 224/255, alpha: 1.0)
        
        if Utilities.isStandaloneApp() {
            setupStandaloneMenu()
        } else {
            setupGatewayMenu()
        }
        
        self.labelVersion.text = "V" + "\(Utilities.getAppVersion())"
    }
    
    override func viewWillAppear(_ animated: Bool) {
    }
    
    override func viewDidAppear(_ animated: Bool) {
        super.viewDidAppear(animated)
        self.view.isHidden = false
    }
    
    
    final private func setupGatewayMenu(){
        
        let storyboard = UIStoryboard(name: kStoryboardIdentifierGateway, bundle: nil)
        
        self.studyListViewController = (storyboard.instantiateViewController(withIdentifier: String(describing: StudyListViewController.classForCoder())) as? UINavigationController)!
        
        self.notificationController = (storyboard.instantiateViewController(withIdentifier:  String(describing: NotificationViewController.classForCoder())) as? UINavigationController)!
        
        self.resourcesViewController = (storyboard.instantiateViewController(withIdentifier:  String(describing: GatewayResourcesListViewController.classForCoder())) as? UINavigationController)!
        
        self.profileviewController = (storyboard.instantiateViewController(withIdentifier:  String(describing: ProfileViewController.classForCoder())) as? UINavigationController)!
        
        self.reachoutViewController = (storyboard.instantiateViewController(withIdentifier:  String(describing: ReachoutOptionsViewController.classForCoder())) as? UINavigationController)!
        
    }
    
    final private func setupStandaloneMenu(){
        
        let studyStoryBoard = UIStoryboard.init(name: kStudyStoryboard, bundle: Bundle.main)
        /*for standalone*/
        self.studyTabBarController = studyStoryBoard.instantiateViewController(withIdentifier: kStudyDashboardTabbarControllerIdentifier) as! StudyDashboardTabbarViewController
        
        let storyboard = UIStoryboard(name: kStoryboardIdentifierGateway, bundle: nil)
        
        self.studyListViewController = storyboard.instantiateViewController(withIdentifier: String(describing: StudyListViewController.classForCoder())) as? UINavigationController
        
        
        self.studyHomeViewController = studyStoryBoard.instantiateViewController(withIdentifier: String(describing: "StudyHomeNavigationController")) as? UINavigationController //for standalone
        
        self.notificationController = storyboard.instantiateViewController(withIdentifier:  String(describing: NotificationViewController.classForCoder())) as? UINavigationController
        
        self.resourcesViewController = storyboard.instantiateViewController(withIdentifier:  String(describing: GatewayResourcesListViewController.classForCoder())) as? UINavigationController
        
        self.profileviewController = storyboard.instantiateViewController(withIdentifier:  String(describing: ProfileViewController.classForCoder())) as? UINavigationController
        
        self.reachoutViewController = storyboard.instantiateViewController(withIdentifier:  String(describing: ReachoutOptionsViewController.classForCoder())) as? UINavigationController
        
    }
    
    
    
    /**
     Used to create Login controller for new user using SignInViewController
     and SignUpViewController
     */
    func createControllersForAnonymousUser(){
        
        let loginStoryBoard = UIStoryboard(name: kLoginStoryboardIdentifier, bundle: nil)
        let signInController = (loginStoryBoard.instantiateViewController(withIdentifier:  String(describing: SignInViewController.classForCoder())) as? SignInViewController)!
        self.signInViewController = UINavigationController(rootViewController: signInController)
        self.signInViewController.navigationBar.barStyle = .default
         self.signInViewController.navigationBar.isTranslucent = false
        
        let signUpController = (loginStoryBoard.instantiateViewController(withIdentifier:  String(describing: SignUpViewController.classForCoder())) as? SignUpViewController)!
        self.signUpViewController = UINavigationController(rootViewController: signUpController)
        self.signUpViewController.navigationBar.barStyle = .default
        self.signUpViewController.navigationBar.isTranslucent = false
    }
    

    /**
     Used to create Left menu items
     */
    func createLeftmenuItems(){
        
        self.createControllersForAnonymousUser()
        
        let user = User.currentUser
        
        menus = [ ["menuTitle": "Home",
                   "iconName": "home_menu1-1",
            "menuType": LeftMenu.studyList]
        ]
        
        if !Utilities.isStandaloneApp() {
            
            menus.append(["menuTitle": "Resources",
             "iconName": "resources_menu1",
                "menuType": LeftMenu.resources])
        }
        
        if user.userType == .FDAUser {
            menus.append(["menuTitle": "My Account",
                          "iconName": "profile_menu1",
                "menuType": LeftMenu.profile_reachOut])
            menus.append(["menuTitle": "Reach Out",
                          "iconName": "reachout_menu1",
                "menuType": LeftMenu.reachOut_signIn])
           
            self.buttonSignOut?.isHidden = false
        }
        else{
            menus.append(["menuTitle": "Reach Out",
                          "iconName": "reachout_menu1",
                "menuType": LeftMenu.profile_reachOut])

            menus.append(["menuTitle": "Sign In",
                          "iconName": "signin_menu1",
                "menuType": LeftMenu.reachOut_signIn])
            
            menus.append(["menuTitle": "New User?",
                          "iconName":"newuser_menu1",
                          "subTitle": "Sign up",
                "menuType": LeftMenu.signup])
             self.buttonSignOut?.isHidden = true
        }
        
        // Setting proportion height of the header and footer view
        var height: CGFloat? = 0.0
        height =  (UIScreen.main.bounds.size.height -  CGFloat(menus.count * 70))/2
        
        self.tableHeaderView.frame.size = CGSize(width: self.tableHeaderView!.frame.size.width, height: height!)
        self.tableFooterView.frame.size = CGSize(width: self.tableFooterView!.frame.size.width, height: height!)
        self.tableView.frame.size = CGSize(width: self.tableView.frame.width, height: UIScreen.main.bounds.size.height  )
        
        self.tableView.reloadData()
        
    }
    
    /**
     Used to set the initial data for new user
     */
    func setInitialData()  {
        
        let user = User.currentUser
        if user.userType == .FDAUser {
            menus.append(["menuTitle": "My Account",
                          "iconName": "profile_menu1"])
            self.tableView.tableFooterView?.isHidden = false
        }
        else{
            menus.append(["menuTitle": "Sign In",
                          "iconName": "signin_menu1"])
            
            menus.append(["menuTitle": "New User?",
                          "iconName": "newuser_menu1",
                          "subTitle": "Sign up"])
            self.tableView.tableFooterView?.isHidden = true
        }
        
        // Setting proportion height of the header and footer view
        let height = UIScreen.main.bounds.size.height  * (220.0 / 667.0) //calculate new height
        self.tableView.tableHeaderView?.frame.size = CGSize(width: self.tableView.tableHeaderView!.frame.size.width, height: height)
        self.tableView.tableFooterView?.frame.size = CGSize(width: self.tableView.tableFooterView!.frame.size.width, height: height)
        self.tableView.frame.size = CGSize(width: self.tableView.frame.width, height: UIScreen.main.bounds.size.height)
        self.tableView.reloadData()
    }
    
    
    /**
     Used to change the view controller when clicked from the left menu
     @param menu    Accepts the data from enum LeftMenu
     */
    func changeViewController(_ menu: LeftMenu) {
        
        let isStandalone = Utilities.isStandaloneApp()
        
        switch menu {
        case .studyList:
            
            if isStandalone {
                if Study.currentStudy?.userParticipateState.status == .inProgress {
                     self.slideMenuController()?.changeMainViewController(self.studyTabBarController, close: true)
                }
                else {
                    self.slideMenuController()?.changeMainViewController(self.studyHomeViewController, close: true)
                }
                
               
            } else {
                self.slideMenuController()?.changeMainViewController(self.studyListViewController, close: true)
            }
            
            
        case .resources:
            self.slideMenuController()?.changeMainViewController(self.resourcesViewController, close: true)
            
        case .profile_reachOut:
            
            if User.currentUser.userType == .FDAUser {
                self.slideMenuController()?.changeMainViewController(self.profileviewController, close: true)
                
            }else {
                // go to ReachOut screen
                self.slideMenuController()?.changeMainViewController(self.reachoutViewController, close: true)
            }
            
        case .reachOut_signIn:
            if User.currentUser.userType == .FDAUser {
                // go to reach out
                self.slideMenuController()?.changeMainViewController(self.reachoutViewController, close: true)
                
            }else {
                
                // go sign in
                self.slideMenuController()?.changeMainViewController(self.signInViewController, close: true)
            }
        case .signup:
            self.slideMenuController()?.changeMainViewController(self.signUpViewController, close: true)
            
            
        }
    }
    
    
// MARK:- Button Action
    
    /**
     Signout button clicked
     @param sender    Accepts UIButton Object
     */
    @IBAction func buttonActionSignOut(_ sender: UIButton) {
        
     
        
        DBHandler.isDataAvailableToSync { (available) in
            if(available){
                
                UIUtilities.showAlertMessageWithTwoActionsAndHandler(NSLocalizedString(kSignOutText, comment: ""), errorMessage: NSLocalizedString(kAlertMessageSignOutSync, comment: ""), errorAlertActionTitle: NSLocalizedString(kSignOutText, comment: ""),
                                                                     errorAlertActionTitle2: NSLocalizedString(kAlertSignOutLaterTitle, comment: ""), viewControllerUsed: self,
                                                                     action1: {
                                                                        
                                                                        self.sendRequestToSignOut()
                                                                        
                },
                                                                     action2: {
                })
            }
            else {
                
                UIUtilities.showAlertMessageWithTwoActionsAndHandler(NSLocalizedString(kSignOutText, comment: ""), errorMessage: NSLocalizedString(kAlertMessageForSignOut, comment: ""), errorAlertActionTitle: NSLocalizedString(kSignOutText, comment: ""),
                                                                     errorAlertActionTitle2: NSLocalizedString(kTitleCancel, comment: ""), viewControllerUsed: self,
                                                                     action1: {
                                                                        
                                                                        self.sendRequestToSignOut()
                                                                        
                                                                        
                },
                                                                     action2: {
                                                                        
                })
            }
        }
        
       
        
    }

    
    /**
     Send the webservice request to Signout
     */
    func sendRequestToSignOut() {
        
        
        UserServices().logoutUser(self as NMWebServiceDelegate)
        
        
    }
    
    
    /**
     As the user is Signed out Remove passcode from the keychain
     */
    func signout(){
      
        ORKPasscodeViewController.removePasscodeFromKeychain()
        
        let ud = UserDefaults.standard
        ud.set(false, forKey: kPasscodeIsPending)
        ud.set(false, forKey: kShowNotification)
        ud.synchronize()
        
        StudyDashboard.instance.dashboardResponse = []
        let appDelegate = UIApplication.shared.delegate as! AppDelegate
        appDelegate.updateKeyAndInitializationVector()
        
        if !Utilities.isStandaloneApp() {
            self.changeViewController(.studyList)
            self.createLeftmenuItems()
        }
        else {
            UIApplication.shared.keyWindow?.removeProgressIndicatorFromWindow()
            self.navigationController?.popToRootViewController(animated: true)
        }
        
       
    }
}


// MARK:- UITableView Delegate
extension LeftMenuViewController: UITableViewDelegate {
    
    func tableView(_ tableView: UITableView, heightForRowAt indexPath: IndexPath) -> CGFloat {
        
        if let menu = LeftMenu(rawValue: indexPath.row) {
            switch menu {
            case .studyList, .resources, .profile_reachOut,.reachOut_signIn, .signup:
                return 70.0
            }
        }
        return 0
    }
    
    func tableView(_ tableView: UITableView, didSelectRowAt indexPath: IndexPath) {
        
        tableView.deselectRow(at: indexPath, animated: true)
        
        if let menu = menus[indexPath.row]["menuType"] as? LeftMenu {
            self.changeViewController(menu)
        }
    }
    
    func scrollViewDidScroll(_ scrollView: UIScrollView) {
        if self.tableView == scrollView {
            
        }
    }
}


// MARK:- UITableView DataSource
extension LeftMenuViewController: UITableViewDataSource {
    
    func tableView(_ tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
        return menus.count
    }
    
    func tableView(_ tableView: UITableView, cellForRowAt indexPath: IndexPath) -> UITableViewCell {
        
        let dict: Dictionary<String,Any>? = menus[indexPath.row]
        
        if dict?["subTitle"] != nil {
            
            var cell: LeftMenuCell?
            cell = tableView.dequeueReusableCell(withIdentifier: "cell" , for: indexPath) as? LeftMenuCell
            cell?.populateCellData(data: menus[indexPath.row])
            return cell!
            
        }else {
            var cell: LeftMenuResourceTableViewCell?
            
            cell = tableView.dequeueReusableCell(withIdentifier: "LeftMenuResourceCell" , for: indexPath) as? LeftMenuResourceTableViewCell
            cell?.populateCellData(data: menus[indexPath.row])
            return cell!
        }
    }
}


// MARK:- UserService Response handler
extension LeftMenuViewController: NMWebServiceDelegate {
    
    func startedRequest(_ manager: NetworkManager, requestName: NSString) {
        Logger.sharedInstance.info("requestname : \(requestName)")
        UIApplication.shared.keyWindow?.addProgressIndicatorOnWindowFromTop()
    }
    
    func finishedRequest(_ manager: NetworkManager, requestName: NSString, response: AnyObject?) {
        Logger.sharedInstance.info("requestname : \(requestName)")
        
        if requestName as String ==  RegistrationMethods.logout.description {
            self.signout()
        }
        UIApplication.shared.keyWindow?.addProgressIndicatorOnWindowFromTop()
    }
    
    func failedRequest(_ manager: NetworkManager, requestName: NSString, error: NSError) {
        Logger.sharedInstance.info("requestname : \(requestName)")
        UIApplication.shared.keyWindow?.addProgressIndicatorOnWindowFromTop()
        
        if error.code == 403 { //unauthorized
            UIUtilities.showAlertMessageWithActionHandler(kErrorTitle, message: error.localizedDescription, buttonTitle: kTitleOk, viewControllerUsed: self, action: {
                self.fdaSlideMenuController()?.navigateToHomeAfterUnauthorizedAccess()
            })
        }else {
            UIUtilities.showAlertWithTitleAndMessage(title: NSLocalizedString(kErrorTitle, comment: "") as NSString, message: error.localizedDescription as NSString)
        }
    }
}




