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
import MessageUI

class WebViewController : UIViewController{
    
    @IBOutlet var webView : UIWebView?
    @IBOutlet var barItemShare : UIBarButtonItem?
    
    var activityIndicator:UIActivityIndicatorView!
    var requestLink:String?
    
    var pdfData:Data?
    
    var isEmailAvailable:Bool? = false
    
    var htmlString: String?
    override var preferredStatusBarStyle: UIStatusBarStyle{
        return .default
    }
    
    override func viewDidLoad() {
        super.viewDidLoad()
        
        
    }
    
    override func viewWillAppear(_ animated: Bool) {
        super.viewWillAppear(animated)
        
        
        
        //Used to add a loader
        activityIndicator = UIActivityIndicatorView(style: .gray)
        activityIndicator.center = CGPoint(x: self.view.frame.midX, y: self.view.frame.midY-100)
        self.view.addSubview(activityIndicator)
        activityIndicator.startAnimating()
        
        
        
        if self.isEmailAvailable == false{
            
        barItemShare?.isEnabled = false
        self.navigationItem.rightBarButtonItem = nil
            
            barItemShare = nil
        }
        
        
        if self.requestLink != nil && (self.requestLink?.count)! > 0 {
            let url = URL.init(string:self.requestLink!)
            let urlRequest = URLRequest.init(url: url!)
            
            webView?.loadRequest(urlRequest)
            
        }
        else if self.htmlString != nil {
            
            webView?.loadHTMLString(self.htmlString!, baseURL: nil)
            
            
        }
        else if self.pdfData != nil {
            
            self.webView?.load(pdfData!, mimeType: "application/pdf", textEncodingName: "UTF-8", baseURL:URL.init(fileURLWithPath: "") )
            
        }
        else{
            //VisitWebsite
            
            self.activityIndicator.stopAnimating()
            self.activityIndicator.removeFromSuperview()
            
            
        }
        
        webView?.delegate = self
        webView?.scalesPageToFit = true
        //UIApplication.shared.statusBarStyle = .default
        setNeedsStatusBarAppearanceUpdate()
    }
    
    override func viewDidAppear(_ animated: Bool) {
        super.viewDidAppear(animated)
        
        
    }
    
    @IBAction func cancelButtonClicked(_ sender : Any){
        self.dismiss(animated: true, completion: nil)
    }
    
    @IBAction func buttonActionShare(_ sender : UIBarButtonItem){
        self.sendConsentByMail()
    }
    
   
    func sendConsentByMail() {
        let mailComposerVC = MFMailComposeViewController()
        mailComposerVC.mailComposeDelegate = self
        
        
        mailComposerVC.setSubject("Consent")
       
         //let data:Data?
       
        
        if self.pdfData != nil{
           
            let consentName:String! = (Study.currentStudy?.name!)! + "_SignedConsent"
            
            mailComposerVC.addAttachmentData(self.pdfData!, mimeType: "application/pdf", fileName:consentName)
            
            mailComposerVC.setMessageBody("", isHTML: false)
        }
        else if self.htmlString != nil {
            mailComposerVC.setMessageBody(self.htmlString!, isHTML: true)
           
        }
        else{
        }
        
        if MFMailComposeViewController.canSendMail()
        {
            self.present(mailComposerVC, animated: true, completion: nil)
        }
        else{
            let alert = UIAlertController(title:NSLocalizedString(kTitleError, comment: ""),message:"",preferredStyle: UIAlertController.Style.alert)
            
            alert.addAction(UIAlertAction.init(title:NSLocalizedString("OK", comment: ""), style: .default, handler: { (action) in
                
                self.dismiss(animated: true, completion: nil)
                
            }))
        }
        
        
    }
    
}

extension WebViewController:MFMailComposeViewControllerDelegate{
    func mailComposeController(_ controller: MFMailComposeViewController, didFinishWith result: MFMailComposeResult, error: Error?) {
        controller.dismiss(animated: true, completion: nil)
    }
}
extension WebViewController:UIWebViewDelegate{
    
    func webViewDidFinishLoad(_ webView: UIWebView) {
        self.activityIndicator.stopAnimating()
        self.activityIndicator.removeFromSuperview()
    }
    func webView(_ webView: UIWebView, didFailLoadWithError error: Error) {
        self.activityIndicator.stopAnimating()
        self.activityIndicator.removeFromSuperview()
        debugPrint("\(error.localizedDescription)")
        
//        let buttonTitleOK = NSLocalizedString("OK", comment: "")
//        let alert = UIAlertController(title:NSLocalizedString(kTitleError, comment: ""),message:error.localizedDescription,preferredStyle: UIAlertControllerStyle.alert)
//        
//        alert.addAction(UIAlertAction.init(title:buttonTitleOK, style: .default, handler: { (action) in
//            
//            self.dismiss(animated: true, completion: nil)
//            
//        }))
//        
//        
//        self.present(alert, animated: true, completion: nil)
        
        
    }
}







