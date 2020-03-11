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

let kStudyListViewControllerIdentifier = "StudyListViewController"
let kStudyHomeViewControllerIdentifier = "StudyHomeNavigationController"
let kLeftMenuViewControllerIdentifier = "LeftMenuViewController"

open class FDASlideMenuViewController: SlideMenuController {
    
    override open var preferredStatusBarStyle: UIStatusBarStyle{
        return .default
    }
    override open func awakeFromNib() {
        
       
        SlideMenuOptions.leftViewWidth = UIScreen.main.bounds.size.width * 0.81
        let storyboard = UIStoryboard(name: kStoryboardIdentifierGateway, bundle: nil)
        
        if Utilities.isStandaloneApp() {
            let studyStoryBoard = UIStoryboard.init(name: kStudyStoryboard, bundle: Bundle.main)
            
            if Study.currentStudy?.userParticipateState.status == .inProgress {
                let studyTabBarController = studyStoryBoard.instantiateViewController(withIdentifier: kStudyDashboardTabbarControllerIdentifier) as! StudyDashboardTabbarViewController
                self.mainViewController = studyTabBarController
            }
            else {
                let studyHomeViewController = studyStoryBoard.instantiateViewController(withIdentifier: String(describing: kStudyHomeViewControllerIdentifier)) as! UINavigationController
                self.mainViewController = studyHomeViewController
            }
            
        } else {
            
            //Gateway- Studylist
            let controller = storyboard.instantiateViewController(withIdentifier: kStudyListViewControllerIdentifier)
            self.mainViewController = controller
        }
        
        let controller2 = storyboard.instantiateViewController(withIdentifier: kLeftMenuViewControllerIdentifier)
        self.leftViewController = controller2
        super.awakeFromNib()

    }

    override open func isTagetViewController() -> Bool {
        
        if let vc = UIApplication.topViewController() {
            
            if Utilities.isStandaloneApp() {
                if vc is StudyHomeViewController ||
                    vc is NotificationViewController ||
                    vc is GatewayResourcesListViewController ||
                    vc is ProfileViewController {
                    return true
                }
            } else {
                if vc is StudyListViewController ||
                    vc is NotificationViewController ||
                    vc is GatewayResourcesListViewController ||
                    vc is ProfileViewController {
                    return true
                }
            }
        }
        return false
    }
    
    
    override open func track(_ trackAction: TrackAction) {
        switch trackAction {
        case .leftTapOpen:
            print("TrackAction: left tap open.")
        case .leftTapClose:
            print("TrackAction: left tap close.")
        case .leftFlickOpen:
            print("TrackAction: left flick open.")
        case .leftFlickClose:
            print("TrackAction: left flick close.")
        case .rightTapOpen:
            print("TrackAction: right tap open.")
        case .rightTapClose:
            print("TrackAction: right tap close.")
        case .rightFlickOpen:
            print("TrackAction: right flick open.")
        case .rightFlickClose:
            print("TrackAction: right flick close.")
        }
    }
    
    func navigateToHomeAfterSingout(){
        
        self.leftViewController?.view.isHidden = true
        _ = self.navigationController?.popToRootViewController(animated: true)
        
    
        let navVC: UINavigationController = UIApplication.shared.keyWindow?.rootViewController as! UINavigationController
        
        if navVC.viewControllers.count > 0 {
            let splashVC: SplashViewController = navVC.viewControllers.first as! SplashViewController
            
            splashVC.navigateToGatewayDashboard()
        }
    }
    
    func navigateToHomeAfterUnauthorizedAccess(){
        
        User.resetCurrentUser()
        
        //Delete from database
        DBHandler.deleteAll()
        
        if ORKPasscodeViewController.isPasscodeStoredInKeychain() {
            ORKPasscodeViewController.removePasscodeFromKeychain()
        }
        
        //cancel all local notification
        LocalNotification.cancelAllLocalNotification()

        let ud = UserDefaults.standard
        ud.removeObject(forKey: kUserAuthToken)
        ud.removeObject(forKey: kUserId)
        ud.synchronize()
        
        let appDomain = Bundle.main.bundleIdentifier!
        UserDefaults.standard.removePersistentDomain(forName: appDomain)
        UserDefaults.standard.synchronize()

        self.leftViewController?.view.isHidden = true
        _ = self.navigationController?.popToRootViewController(animated: true)
    }
    
    func navigateToHomeControllerForSignin(){
        
        self.performSegue(withIdentifier: "unwindToHomeSignin", sender: self)
    }
    func navigateToHomeControllerForRegister(){
        
        self.performSegue(withIdentifier: "unwindToHomeRegister", sender: self)
    }
}

extension UIViewController {
    
    public func fdaSlideMenuController() -> FDASlideMenuViewController? {
        var viewController: UIViewController? = self
        while viewController != nil {
            if viewController is FDASlideMenuViewController {
                return viewController as? FDASlideMenuViewController
            }
            viewController = viewController?.parent
        }
        return nil;
    }
}
