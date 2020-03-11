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
import SlideMenuControllerSwift

class HomeViewController: UIViewController{
    
    @IBOutlet weak var container: UIView!
    @IBOutlet var pageControlView: UIPageControl?
    @IBOutlet var buttonLink: UIButton!
    @IBOutlet var buttonSignin: UIButton!
    @IBOutlet var buttonRegister: UIButton!
    @IBOutlet var buttonGetStarted: UIButton?
    var websiteName:String!
    
// MARK:- ViewController Lifecycle
    
    override func loadView() {
        super.loadView()
        self.loadGatewayUI()
    }
    
    override func viewDidLoad() {
        super.viewDidLoad()
        
        // self.loadTestData()
        self.automaticallyAdjustsScrollViewInsets = false
        //Added to change next screen
        pageControlView?.addTarget(self, action: #selector(HomeViewController.didChangePageControlValue), for: .valueChanged)
        
       
        let infoDict = Utilities.getBrandingDetails()
        websiteName = infoDict?[BrandingConstant.WebsiteLink] as! String
        let title = infoDict?[BrandingConstant.WebsiteButtonTitle] as? String
        
        buttonLink.setTitle(title != nil ? title : websiteName, for: .normal)
    }
    
    override func viewWillAppear(_ animated: Bool) {
        super.viewWillAppear(animated)
        
        //hide navigationbar
        self.navigationController?.setNavigationBarHidden(true, animated: true)
        
        // Ask for network configuration in Debug mode
        #if DEBUG
            //HPHCSettings().setupDefaultURLs()
            //HPHCSettings().showSettings(self){}
        #endif
        
    }
    
    override func viewDidAppear(_ animated: Bool) {
        super.viewDidAppear(animated)
        
        
    }
    
// MARK:- 
    
    /**
 
       This method is used to load Initial data setup
    
     */
    
    
    func loadGatewayUI(){
       
        //load plist info
        let plistPath = Bundle.main.path(forResource: "GatewayOverview", ofType: ".plist", inDirectory: nil)
        let arrayContent = NSMutableArray.init(contentsOfFile: plistPath!)
        
        do {
           
            var listOfOverviews: Array<OverviewSection> = []
            for overview in arrayContent!{
                let overviewObj = OverviewSection(detail: overview as! Dictionary<String, Any>)
                listOfOverviews.append(overviewObj)
            }
            
            //create new Overview object
            let overview = Overview()
            overview.type = .gateway
            overview.sections = listOfOverviews
            
            //assgin to Gateway
            Gateway.instance.overview = overview
            
        } catch let error{
            print("json error: \(error.localizedDescription)")
        }
    }
    
    
// MARK:- Segue Methods
    
    override func prepare(for segue: UIStoryboardSegue, sender: Any?) {
        if let pageViewController = segue.destination as? PageViewController {
            pageViewController.pageViewDelegate = self
            pageViewController.overview = Gateway.instance.overview!
        }
        
        if let signInController = segue.destination as? SignInViewController {
            signInController.viewLoadFrom = .gatewayOverview
            
        }
        
        if let signUpController = segue.destination as? SignUpViewController {
            signUpController.viewLoadFrom = .gatewayOverview
            
        }
    }
    
    
    /**
 
     This method is triggered when the user taps on the pageControl to 
     change its current page (Commented as this is not working)
 
     */
    @objc func didChangePageControlValue() {
        //pageViewController?.scrollToViewController(index: (pageControlView?.currentPage)!)
    }
    
    
// MARK:- Button Actions
    
    /**
     
     This method takes the users action and calls menu view
     
     @param sender  Accepts any kind of object
     
     */
    @IBAction func getStartedButtonClicked(_ sender: UIButton){
        self.createMenuView()
    }
    
    
    /**
     
     This methos is used to Create a menu view 
     
     */
    func createMenuView() {
        
        let storyboard = UIStoryboard(name: kStoryboardIdentifierGateway, bundle: nil)
        let fda = storyboard.instantiateViewController(withIdentifier: kStoryboardIdentifierSlideMenuVC) as! FDASlideMenuViewController
        fda.automaticallyAdjustsScrollViewInsets = true
        self.navigationController?.pushViewController(fda, animated: true)
    }
    
    
    /**
     
     This method is used to initialize WebViewController using 
     Main storyboard
     
     @param sender  Access any kind of objects
     
     */
    @IBAction func linkButtonAction(_ sender: Any) {
        
        let loginStoryboard = UIStoryboard.init(name: "Main", bundle:Bundle.main)
        let webViewController = loginStoryboard.instantiateViewController(withIdentifier: "WebViewController") as! UINavigationController
        let webView = webViewController.viewControllers[0] as! WebViewController
        webView.requestLink = "https://" + websiteName
        self.navigationController?.present(webViewController, animated: true, completion: nil)
    }
    
    
    /**
     
     This method is used to perform unwind operation
     
     @param segue   the segue which is connected to 1 controller to another
     
     */
    @IBAction func unwindForLogout(_ segue: UIStoryboardSegue){
        
        
    }
    
    
    /**
     
     This method is used to Unwind segue 
     
     @param segue   the segue which is connected to 1 controller to another
     
     */
    @IBAction func unwindForRegister(_ segue: UIStoryboardSegue){
        DispatchQueue.main.asyncAfter(deadline: .now()) {
            // your code here
            self.buttonRegister.sendActions(for: .touchUpInside)
        }
    }
    
    
    /**
     
     This method is used to send the screen back to Signin
     
     @param segue   the segue which is connected to 1 controller to another
     
     */
    @IBAction func unwindForSignIn(_ segue: UIStoryboardSegue){
        
        DispatchQueue.main.asyncAfter(deadline: .now()) {
            // your code here
            self.buttonSignin.sendActions(for: .touchUpInside)
        }
    }
}


// MARK:- Page Control Delegates for handling Counts
extension HomeViewController: PageViewControllerDelegate {
    
    func pageViewController(pageViewController: PageViewController, didUpdatePageCount count: Int){
        pageControlView?.numberOfPages = count
    }
    
    func pageViewController(pageViewController: PageViewController, didUpdatePageIndex index: Int) {
        pageControlView?.currentPage = index
       
        buttonGetStarted?.layer.borderColor = kUicolorForButtonBackground
        
        if index == 0 {
            // for First Page
            UIView.animate(withDuration: 0.1, animations: {
                self.buttonGetStarted?.backgroundColor = kUIColorForSubmitButtonBackground
                self.buttonGetStarted?.setTitleColor(UIColor.white, for: .normal)
            })
        } else {
            
            UIView.animate(withDuration: 0.1, animations: {
                // for All other pages
                self.buttonGetStarted?.backgroundColor = UIColor.white
                self.buttonGetStarted?.setTitleColor(kUIColorForSubmitButtonBackground, for: .normal)
            })
        }
    }
}

