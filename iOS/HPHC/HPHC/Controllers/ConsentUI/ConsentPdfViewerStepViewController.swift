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
import MessageUI
import ResearchKit

let kPdfMimeType = "application/pdf"
let kUTF8Encoding = "UTF-8"
let kEmailSubject = "Signed Consent"
let kEmailSubjectDashboard = "Dashboard"
let kEmailSubjectCharts = "Charts"
let kConsentFileName = "Consent"
let kConsentFormat = ".pdf"

class ConsentPdfViewerStep: ORKStep {
    
    func showsProgress() -> Bool {
        return false
    }
}

/*
 Displays Signed Consent Pdf and provides option to share by Email
 */
class ConsentPdfViewerStepViewController: ORKStepViewController {
    
    @IBOutlet var webView: UIWebView?
    var pdfData: Data?
    
    @IBOutlet weak var buttonEmailPdf: UIBarButtonItem?
    
    @IBOutlet weak var buttonNext: UIButton?
    
    
    // MARK:ORKstepView Controller Init methods
    override init(step: ORKStep?) {
        super.init(step: step)
    }
    
    required init?(coder aDecoder: NSCoder) {
        super.init(coder: aDecoder)
    }
    override func hasNextStep() -> Bool {
        super.hasNextStep()
        return true
    }
    
    override func goForward(){
        super.goForward()
    }
    
    // MARK:Button Actions
    
    @IBAction func buttonActionNext(sender: UIBarButtonItem?) {
        
        self.goForward()
    }
    
    @IBAction func buttonActionEmailPdf(sender: UIBarButtonItem?) {
        
        self.sendConsentByMail()
        
    }
    
    // MARK:View controller delegates
    
    override func viewDidLoad() {
        super.viewDidLoad()
        
        self.webView?.load(pdfData!, mimeType: "application/pdf", textEncodingName: "UTF-8", baseURL: URL.init(fileURLWithPath: "") )
        webView?.delegate = self
        webView?.scalesPageToFit = true
    }
    
    /*
     sendConsentByMail used for sharing the Consent
     */
    func sendConsentByMail() {
        
        let mailComposerVC = MFMailComposeViewController()
        mailComposerVC.mailComposeDelegate = self
        
        mailComposerVC.setSubject(kEmailSubject)
        mailComposerVC.setMessageBody("", isHTML: false)
        
        let Filename =   "\((Study.currentStudy?.name)!)" + "_SignedConsent"   + ".pdf"
        
        mailComposerVC.addAttachmentData(pdfData!, mimeType: "application/pdf", fileName: Filename)
        
        if MFMailComposeViewController.canSendMail() {
            self.present(mailComposerVC, animated: true, completion: nil)
            
        }else {
            
            let alert = UIAlertController(title: NSLocalizedString(kTitleError, comment: ""),message: "",preferredStyle: UIAlertController.Style.alert)
            
            alert.addAction(UIAlertAction.init(title: NSLocalizedString(kTitleOk, comment: ""), style: .default, handler: { (action) in
                
                self.dismiss(animated: true, completion: nil)
            }))
        }
    }
    
    
    override func didReceiveMemoryWarning() {
        super.didReceiveMemoryWarning()
        // Dispose of any resources that can be recreated.
    }
    
}

// MARK: MailComposer Delegates
extension ConsentPdfViewerStepViewController: MFMailComposeViewControllerDelegate{
    func mailComposeController(_ controller: MFMailComposeViewController, didFinishWith result: MFMailComposeResult, error: Error?) {
        controller.dismiss(animated: true, completion: nil)
    }
}

// MARK: WebView Delegate
extension ConsentPdfViewerStepViewController: UIWebViewDelegate{
    
    func webViewDidFinishLoad(_ webView: UIWebView) {
        self.removeProgressIndicator()
    }
    
    func webView(_ webView: UIWebView, didFailLoadWithError error: Error) {
        
        self.removeProgressIndicator()
        
        let buttonTitleOK = NSLocalizedString("OK", comment: "")
        let alert = UIAlertController(title: NSLocalizedString(kTitleError, comment: ""),message: error.localizedDescription,preferredStyle: UIAlertController.Style.alert)
        
        alert.addAction(UIAlertAction.init(title: buttonTitleOK, style: .default, handler: { (action) in
            self.dismiss(animated: true, completion: nil)
            
        }))
        
        self.present(alert, animated: true, completion: nil)
    }
}




