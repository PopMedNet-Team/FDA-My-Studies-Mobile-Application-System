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
import QuickLook

let kResourceName = "fda_preload"

extension UINavigationController {
    open override var childForStatusBarStyle: UIViewController? {
        return visibleViewController
    }
}
extension UIViewController {
    
    func topMostViewController() -> UIViewController {
        
            if self.presentedViewController == nil {
                return self
            }
            if let navigation = self.presentedViewController as? UINavigationController {
                return navigation.visibleViewController!.topMostViewController()
            }
            if let tab = self.presentedViewController as? UITabBarController {
                if let selectedTab = tab.selectedViewController {
                    return selectedTab.topMostViewController()
                }
                return tab.topMostViewController()
            }
            return self.presentedViewController!.topMostViewController()
    }
    
    func setNavigationBarItem() {
        
        self.addLeftBarButtonWithImage(UIImage(named: "menu_icn")!)
        self.slideMenuController()?.removeLeftGestures()
        self.slideMenuController()?.removeRightGestures()
        self.slideMenuController()?.addLeftGestures()
        self.slideMenuController()?.addRightGestures()
    }
    
    func removeNavigationBarItem() {
        self.navigationItem.leftBarButtonItem = nil
        self.navigationItem.rightBarButtonItem = nil
        self.slideMenuController()?.removeLeftGestures()
        self.slideMenuController()?.removeRightGestures()
    }
    
    func showAlert(title: String,message: String){
        
          let alert = UIAlertController(title: title,message: message,preferredStyle: UIAlertController.Style.alert)
          alert.addAction(UIAlertAction(title: "OK", style: .default, handler: nil))
         
          self.present(alert, animated: true, completion: nil)
    }
    
    func addProgressIndicator(){
        
        self.navigationItem.leftBarButtonItem?.isEnabled = false
        self.navigationItem.rightBarButtonItem?.isEnabled = true
        self.navigationItem.backBarButtonItem?.isEnabled = false
        slideMenuController()?.removeLeftGestures()
        slideMenuController()?.view.isUserInteractionEnabled = false
        
        self.navigationController?.navigationBar.isUserInteractionEnabled = false
        
        var progressView = self.view.viewWithTag(5000)
        if progressView == nil {
            
            progressView = UINib(nibName: kNewProgressViewNIB, bundle: nil).instantiate(withOwner: nil, options: nil)[0] as? UIView
            
            let url = Bundle.main.url(forResource: kResourceName, withExtension: "gif")!
           
            let webView =  progressView?.subviews.first as! UIWebView
            webView.loadRequest(URLRequest(url: url))
            
            UI: do {
                webView.scalesPageToFit = true
                webView.contentMode = UIView.ContentMode.scaleAspectFit
                progressView!.alpha = 0
                progressView!.tag = 5000
            }
            
            layout: do {
                self.view.addSubview(progressView!)
                progressView!.translatesAutoresizingMaskIntoConstraints = false
                
                NSLayoutConstraint.activate([
                    progressView!.leadingAnchor.constraint(equalTo: self.view.leadingAnchor),
                    progressView!.trailingAnchor.constraint(equalTo: self.view.trailingAnchor),
                    progressView!.widthAnchor.constraint(equalToConstant: UIScreen.main.bounds.size.width),
                    progressView!.heightAnchor.constraint(equalToConstant: UIScreen.main.bounds.size.height)
                    //progressView!.bottomAnchor.constraint(equalTo: self.view.bottomAnchor),
                    //progressView!.topAnchor.constraint(equalTo: self.view.topAnchor)
                    ])
            }
            
            

            UIView.animate(withDuration: 0.3) {
                progressView!.alpha = 1
            }
        }
    }
    
    func removeProgressIndicator(){
        
        self.navigationItem.leftBarButtonItem?.isEnabled = true
        self.navigationItem.rightBarButtonItem?.isEnabled = true
        self.navigationItem.backBarButtonItem?.isEnabled = true
        
         self.navigationController?.navigationBar.isUserInteractionEnabled = true
        
        let view = self.view.viewWithTag(5000) //as UIView
        
        slideMenuController()?.view.isUserInteractionEnabled = true
        slideMenuController()?.addLeftGestures()
        
        UIView.animate(withDuration: 0.2, animations: {
            view?.alpha = 0
        }) { (completed) in
            view?.removeFromSuperview()
        }
    }
}

