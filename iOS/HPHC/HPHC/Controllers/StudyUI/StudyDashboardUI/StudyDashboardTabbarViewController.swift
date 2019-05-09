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


class StudyDashboardTabbarViewController: UITabBarController {
    
    override func viewDidLoad() {
        super.viewDidLoad()
        
        // Do any additional setup after loading the view.
    }
    override func viewWillAppear(_ animated: Bool) {
        super.viewWillAppear(true)
        self.navigationController?.interactivePopGestureRecognizer?.isEnabled = false
    }
    
    override func didReceiveMemoryWarning() {
        super.didReceiveMemoryWarning()
        // Dispose of any resources that can be recreated.
    }
    
    
    public func shareScreenshotByEmail(image: UIImage!, subject: String!,fileName: String!){
        
        let imageData = image.pngData()
        let mailComposerVC = MFMailComposeViewController()
        mailComposerVC.mailComposeDelegate = self as MFMailComposeViewControllerDelegate
        let finalSubject = "\((Study.currentStudy?.name)!)" + " " + subject
        
        mailComposerVC.setSubject(finalSubject)
        mailComposerVC.setMessageBody("", isHTML: false)
        
        let Filename =   "\((Study.currentStudy?.name)!)" + "_" + "\(fileName!)"  + ".png"
        
        mailComposerVC.addAttachmentData(imageData!, mimeType: "image/png", fileName: Filename)
        
        if MFMailComposeViewController.canSendMail()
        {
            self.present(mailComposerVC, animated: true, completion: nil)
        } else {
            
            let alert = UIAlertController(title: NSLocalizedString(kTitleError, comment: ""),message: "",preferredStyle: UIAlertController.Style.alert)
            
            alert.addAction(UIAlertAction.init(title: NSLocalizedString(kTitleOk, comment: ""), style: .default, handler: { (action) in
                
                self.dismiss(animated: true, completion: nil)
                
            }))
        }
    }
}

extension StudyDashboardTabbarViewController: MFMailComposeViewControllerDelegate{
    func mailComposeController(_ controller: MFMailComposeViewController, didFinishWith result: MFMailComposeResult, error: Error?) {
        controller.dismiss(animated: true, completion: nil)
    }
}

