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


let resourcesDownloadPath = AKUtility.baseFilePath + "/Resources"

class ResourcesDetailViewController: UIViewController {
    
    
    @IBOutlet var webView: UIWebView?
    @IBOutlet var progressBar: UIProgressView?
    
    var activityIndicator: UIActivityIndicatorView!
    var requestLink: String?
    var type: String?
    var htmlString: String?
    var resource: Resource?
    var isEmailComposerPresented: Bool?
    
    override func viewDidLoad() {
        super.viewDidLoad()
        self.hidesBottomBarWhenPushed = true
        self.addBackBarButton()
        self.isEmailComposerPresented = false
        self.title = resource?.title
        
    }
    
    override func viewWillAppear(_ animated: Bool) {
        super.viewWillAppear(animated)
        
        UIApplication.shared.statusBarStyle = .default
        
        if self.isEmailComposerPresented == false{
        
        
        if self.resource?.file?.link != nil {
            
            activityIndicator = UIActivityIndicatorView(style: .gray)
            activityIndicator.center = CGPoint(x: self.view.frame.midX, y: self.view.frame.midY-100)
            
           
            self.view.addSubview(activityIndicator)
            
            
            activityIndicator.startAnimating()
            
            if self.resource?.file?.mimeType == .pdf{
                
                if self.resource?.file?.localPath != nil {
                    
                    if self.resource?.file?.localPath == "BundlePath" {
                        
                        let path = Bundle.main.path(forResource: self.resource?.file?.link!, ofType: ".pdf")
                        self.loadWebViewWithPath(path: path!)
                    } else {
                        let path = resourcesDownloadPath + "/" + (self.resource?.file?.localPath)!
                        let pdfData = FileDownloadManager.decrytFile(pathURL: URL(string: path))
                        self.loadWebViewWithData(data: pdfData!)

                    }
                    
                                      //self.loadWebViewWithPath(path: (self.resource?.file?.localPath)!)
                } else {
                   //let path = resourcesDownloadPath + "/PDF_linking.pdf"
                    self.startDownloadingfile()
                    //let pdfData = FileDownloadManager.decrytFile(pathURL:URL(string:path))
                    //self.loadWebViewWithData(data: pdfData!)
                }
                
                
            } else {
                webView?.loadHTMLString(self.requestLink!, baseURL: nil)
            }
        } else {
            
        }
       // webView?.scalesPageToFit = true
        webView?.delegate = self
        self.webView?.scalesPageToFit = true
        }
        
        UIApplication.shared.statusBarStyle = .default
    }
    
    override func viewWillDisappear(_ animated: Bool) {
        // self.tabBar.isHidden = false
    }
    
    func loadWebViewWithPath(path: String) {
        
        let url: URL? = URL.init(string:path.addingPercentEncoding(withAllowedCharacters: .urlPathAllowed)!)
        let urlRequest = URLRequest(url: url!)
        webView?.loadRequest(urlRequest)
    }
    func loadWebViewWithData(data: Data) {
       
        self.webView?.load(data, mimeType: "application/pdf", textEncodingName: "UTF-8", baseURL: URL.init(fileURLWithPath: "") )
        
//        let url:URL? = URL.init(string:path.addingPercentEncoding(withAllowedCharacters: .urlPathAllowed)!)
//        let urlRequest = URLRequest(url: url!)
//        webView?.loadRequest(urlRequest)
    }
    
    func startDownloadingfile() {
        
        if !FileManager.default.fileExists(atPath: resourcesDownloadPath) {
            try! FileManager.default.createDirectory(atPath: resourcesDownloadPath, withIntermediateDirectories: true, attributes: nil)
        }
        debugPrint("custom download path: \(resourcesDownloadPath)")
        
       
        
        let fileURL =  (self.resource?.file?.link)!
        
        let url = URL(string: fileURL)
        
        var fileName: NSString = url!.lastPathComponent as NSString
        
        fileName = AKUtility.getUniqueFileNameWithPath((resourcesDownloadPath as NSString).appendingPathComponent(fileName as String) as NSString)
        
        let fdm = FileDownloadManager()
        fdm.delegate = self
        guard let encodedUrl = fileURL.addingPercentEncoding(withAllowedCharacters: .urlHostAllowed) else {
            return
        } // Tush
        
        fdm.downloadFile(fileName as String, fileURL: encodedUrl , destinationPath: resourcesDownloadPath)
    }
    
    
    // MARK:Button Actions
    
    @IBAction func cancelButtonClicked(_ sender: Any){
        self.dismiss(animated: true, completion: nil)
    }
    
    @IBAction func buttonActionForward(_ sender: UIBarButtonItem){
        
        self.sendEmail()
    }
    
    override func didReceiveMemoryWarning() {
        super.didReceiveMemoryWarning()
        // Dispose of any resources that can be recreated.
    }
}

extension ResourcesDetailViewController: UIWebViewDelegate{
    
    func webViewDidFinishLoad(_ webView: UIWebView) {
        self.activityIndicator.stopAnimating()
        self.activityIndicator.removeFromSuperview()
    }
    func webView(_ webView: UIWebView, didFailLoadWithError error: Error) {
        self.activityIndicator.stopAnimating()
        self.activityIndicator.removeFromSuperview()
        
        let buttonTitleOK = NSLocalizedString("OK", comment: "")
        let alert = UIAlertController(title: NSLocalizedString(kTitleError, comment: ""),message: error.localizedDescription,preferredStyle: UIAlertController.Style.alert)
        
        alert.addAction(UIAlertAction.init(title: buttonTitleOK, style: .default, handler: { (action) in
            
            self.dismiss(animated: true, completion: nil)
            
        }))
        
        
        self.present(alert, animated: true, completion: nil)
        
        
    }
}

extension ResourcesDetailViewController: MFMailComposeViewControllerDelegate{
    
    func sendEmail() {
        let composeVC = MFMailComposeViewController()
        composeVC.mailComposeDelegate = self
        // Configure the fields of the interface.
        
        composeVC.setSubject("Resources")
        
        if resource?.file?.localPath != nil {
            
            if self.resource?.file?.localPath == "BundlePath"{
                
                do {
                    let file = Bundle.main.url(forResource: self.resource?.file?.link!, withExtension: "pdf")
                    let data = try Data(contentsOf: file!)
                    composeVC.addAttachmentData(data, mimeType: "application/pdf", fileName: (resource?.file?.name)!)
                }
                catch{
                    
                }
            } else {
                
                do {
                    
                    let fullPath = resourcesDownloadPath + "/" + (self.resource?.file?.localPath)!
                    
                    let data = FileDownloadManager.decrytFile(pathURL: URL.init(string: fullPath))
                    
                    composeVC.addAttachmentData(data!, mimeType: "application/pdf", fileName: (resource?.file?.name)!)
                }
                catch let error as NSError{
                    print("error \(error)")
                }
            }
            
            
            
        } else {
            composeVC.setMessageBody((resource?.file?.link)!, isHTML: true)
        }
        
        
        if MFMailComposeViewController.canSendMail()
        {
            self.present(composeVC, animated: true, completion: nil)
            
        } else {
            let alert = UIAlertController(title: NSLocalizedString(kTitleError, comment: ""),message: "",preferredStyle: UIAlertController.Style.alert)
            
            alert.addAction(UIAlertAction.init(title: NSLocalizedString("OK", comment: ""), style: .default, handler: { (action) in
                
                self.dismiss(animated: true, completion: nil)
                
            }))
        }
    }
    
    func mailComposeController(_ controller: MFMailComposeViewController,
                               didFinishWith result: MFMailComposeResult, error: Error?) {
        
         self.isEmailComposerPresented = true
        
        controller.dismiss(animated: true, completion: nil)
    }
    
}


extension ResourcesDetailViewController: FileDownloadManagerDelegates{
    
    func download(manager: FileDownloadManager, didUpdateProgress progress: Float) {
        
        self.progressBar?.progress = progress
    }
    func download(manager: FileDownloadManager, didFinishDownloadingAtPath path: String) {
        
        
         let fullPath = resourcesDownloadPath + "/" + path
        
        
        let data = FileDownloadManager.decrytFile(pathURL: URL.init(string: fullPath))
        
        if data != nil {
            self.resource?.file?.localPath = path
            // self.loadWebViewWithPath(path: path)
            
            let mimeType = "application/" + "\((self.resource?.file?.mimeType?.rawValue)!)"
            
            self.webView?.load(data!, mimeType: mimeType, textEncodingName: "UTF-8", baseURL: URL.init(fileURLWithPath: "") )
        }
        
    }
    func download(manager: FileDownloadManager, didFailedWithError error: Error) {
        
    }
    
    
}
