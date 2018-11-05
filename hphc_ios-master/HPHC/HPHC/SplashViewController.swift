/*
 License Agreement for FDA My Studies
 Copyright © 2017-2018 Harvard Pilgrim Health Care Institute (HPHCI) and its Contributors.
 Permission is hereby granted, free of charge, to any person obtaining a copy of this software and
 associated documentation files (the "Software"), to deal in the Software without restriction, including
 without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies
 of the Software, and to permit persons to whom the Software is furnished to do so, subject to the
 following conditions:
 
 The above copyright notice and this permission notice shall be included in all copies or substantial
 portions of the Software.
 
 Funding Source: Food and Drug Administration (“Funding Agency”) effective 18 September 2014 as Contract no. HHSF22320140030I/HHSF22301006T (the “Prime Contract”).
 
 THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 FITNESS FOR A PARTICULAR PURPOSE AND NON-INFRINGEMENT. IN NO EVENT SHALL
 THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR
 OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE,
 ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR
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
        if User.currentUser.authToken != nil {
            
            let appDelegate = UIApplication.shared.delegate as! AppDelegate
            appDelegate.checkPasscode(viewController: self)
            self.navigateToGatewayDashboard()
            
        }else {
            self.navigateToHomeController()
        }
    }
    
    override func viewDidDisappear(_ animated: Bool) {
    }
    
    override func didReceiveMemoryWarning() {
        super.didReceiveMemoryWarning()
    }
    
    
    /**
     Navigating to Home Screen and load HomeViewController from Login Storyboard
     */
    func navigateToHomeController(){
        
        let loginStoryboard = UIStoryboard.init(name: kLoginStoryboardIdentifier, bundle:Bundle.main)
        let homeViewController = loginStoryboard.instantiateViewController(withIdentifier:"HomeViewController")
        self.navigationController?.pushViewController(homeViewController, animated: true)
    }
    
    
    /**
     Navigate to gateway Dashboard
     */
    func navigateToGatewayDashboard(){
        self.createMenuView()
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
            let subStringFromDate = currentDate.substring(to: currentIndex)
            
            let ud = UserDefaults.standard
            
            if User.currentUser.userType == .FDAUser {
                
                let index =  User.currentUser.userId.index(User.currentUser.userId.endIndex
                    , offsetBy: -16)
                let subKey = User.currentUser.userId.substring(to:index )
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
                udid = udid?.substring(to: index!)
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


