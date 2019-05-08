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
import MediaPlayer
import SDWebImage
import ResearchKit
import AVKit
class StudyOverviewViewControllerFirst: UIViewController {
    
    @IBOutlet var buttonJoinStudy: UIButton?
    @IBOutlet var buttonWatchVideo: UIButton?
    @IBOutlet var buttonVisitWebsite: UIButton?
    @IBOutlet var labelTitle: UILabel?
    @IBOutlet var labelDescription: UILabel?
    @IBOutlet var imageViewStudy: UIImageView?
    
    var pageIndex: Int!
    var overViewWebsiteLink: String?
    var overviewSectionDetail: OverviewSection!
    var moviePlayer: MPMoviePlayerViewController!
    var playerViewController: AVPlayerViewController!
    //var player:AVPlayer!
    override var preferredStatusBarStyle: UIStatusBarStyle{
        return .lightContent
    }
// MARK:- Viewcontroller Lifecycle
    override func viewDidLoad() {
        super.viewDidLoad()
        
        //Used to set border color for bottom view
        buttonJoinStudy?.layer.borderColor = kUicolorForButtonBackground
        if overviewSectionDetail.imageURL != nil {
            let url = URL.init(string: overviewSectionDetail.imageURL!)
            imageViewStudy?.sd_setImage(with: url, placeholderImage: nil)
        }
        
        if overviewSectionDetail.link != nil {
            buttonWatchVideo?.isHidden = false
        } else {
             buttonWatchVideo?.isHidden =  true
        }
    }
    
    override func viewWillDisappear(_ animated: Bool) {
       
    }
    
    override func viewWillAppear(_ animated: Bool) {
        super.viewWillAppear(animated)
        
         labelTitle?.text = overviewSectionDetail.title
        
        var fontSize = 18.0
        if DeviceType.IS_IPAD || DeviceType.IS_IPHONE_4_OR_LESS {
            fontSize = 13.0
        } else if DeviceType.IS_IPHONE_5 {
            fontSize = 14.0
        }
        
        let attrStr = try! NSAttributedString(
            data: (overviewSectionDetail.text?.data(using: String.Encoding.unicode, allowLossyConversion: true)!)!,
            options: [ NSAttributedString.DocumentReadingOptionKey.documentType: NSAttributedString.DocumentType.html],
            documentAttributes: nil)
        
        let attributedText: NSMutableAttributedString = NSMutableAttributedString(attributedString: attrStr)
        attributedText.addAttributes([NSAttributedString.Key.font:UIFont(
            name: "HelveticaNeue",
            size: CGFloat(fontSize))!], range:(attrStr.string as NSString).range(of: attrStr.string))
        attributedText.addAttribute(NSAttributedString.Key.foregroundColor, value: UIColor.white, range: (attrStr.string as NSString).range(of: attrStr.string))
        
        if Utilities.isValidValue(someObject: attrStr.string as AnyObject?) {
             self.labelDescription?.attributedText = attributedText
        } else {
             self.labelDescription?.text = ""
        }
       self.labelDescription?.textAlignment = .center
        
        //UIApplication.shared.statusBarStyle = .lightContent
        setNeedsStatusBarAppearanceUpdate()
        
        //self.labelDescription?.text = overviewSectionDetail.text!
        
    }
    
    override func viewDidAppear(_ animated: Bool) {
        super.viewDidAppear(animated)
        
    }
    
    override func viewDidDisappear(_ animated: Bool) {
      super.viewDidDisappear(animated)
        
    }
    
    
// MARK:-
    
    /** 
     
     This method is used for Video player completed the video notification 
     
     @param notification    used to access the different notifications
     
     */
    func moviePlayBackDidFinish(notification: NSNotification) {
        //  println("moviePlayBackDidFinish:")
        moviePlayer.moviePlayer.stop()
        NotificationCenter.default.removeObserver(self, name: NSNotification.Name.MPMoviePlayerPlaybackDidFinish, object: nil)
        moviePlayer.dismiss(animated: true, completion: nil)
    }
    
    @objc func playerDidFinishPlaying(note: NSNotification) {
        NotificationCenter.default.removeObserver(self, name: NSNotification.Name.AVPlayerItemDidPlayToEndTime, object: nil)
        self.playerViewController.dismiss(animated: true, completion: nil)
    }
    
   
// MARK:- Button Actions
    
    /**
     
     This method is used to Watch Video 
     
     @param sender  Access any kind of object
     
     */
    @IBAction func watchVideoButtonAction(_ sender: Any) {
        
        let urlString = overviewSectionDetail.link!
        let url = URL.init(string: urlString)
        let extenstion = url?.pathExtension
    
        if  extenstion == nil || extenstion?.count == 0 {
            UIApplication.shared.openURL(url!)
        } else {
            
            do{
                try AVAudioSession.sharedInstance().setMode(.moviePlayback)
            } catch {
                //Didn't work
            }
            
            let url: NSURL = NSURL(string: overviewSectionDetail.link!)!

            let player = AVPlayer(url: url as URL)
            NotificationCenter.default.addObserver(self, selector:#selector(StudyOverviewViewControllerFirst.playerDidFinishPlaying(note:)),
                                                   name: NSNotification.Name.AVPlayerItemDidPlayToEndTime, object: player.currentItem)
            playerViewController = AVPlayerViewController()
            playerViewController.player = player
            self.present(playerViewController, animated: true) {
                self.playerViewController.player!.play()
            }

        }
    }
    
    
    /** 
     
     This method is used to Join Study
     
     @param sender  Access any kind of object
     
     */
    @IBAction func buttonActionJoinStudy(_ sender: Any){
        
        if User.currentUser.userType == UserType.AnonymousUser{
            let leftController = slideMenuController()?.leftViewController as! LeftMenuViewController
            leftController.changeViewController(.reachOut_signIn)
        }
    }
    

    /**
     
     This method is used to Visit website 
     
     @param sender  Access any kind of object
     
     */
    @IBAction func visitWebsiteButtonAction(_ sender: Any) {
        
        if overViewWebsiteLink != nil {
            
            let loginStoryboard = UIStoryboard.init(name: "Main", bundle: Bundle.main)
            let webViewController = loginStoryboard.instantiateViewController(withIdentifier:"WebViewController") as! UINavigationController
            let webView = webViewController.viewControllers[0] as! WebViewController
            
            webView.requestLink = overViewWebsiteLink!
            self.navigationController?.present(webViewController, animated: true, completion: nil)
        }
    }
}


// MARK:- Webservice Delegates
extension StudyOverviewViewControllerFirst: NMWebServiceDelegate {
    
    func startedRequest(_ manager: NetworkManager, requestName: NSString) {
        Logger.sharedInstance.info("requestname : \(requestName)")
        self.addProgressIndicator()
    }
    
    func finishedRequest(_ manager: NetworkManager, requestName: NSString, response: AnyObject?) {
        Logger.sharedInstance.info("requestname : \(requestName)")
        
        self.removeProgressIndicator()
        if requestName as String ==  RegistrationMethods.logout.description {
            
            
        }
    }
    
    func failedRequest(_ manager: NetworkManager, requestName: NSString, error: NSError) {
        Logger.sharedInstance.info("requestname : \(requestName)")
        self.removeProgressIndicator()
        
    }
}


// Helper function inserted by Swift 4.2 migrator.
fileprivate func convertFromAVAudioSessionCategory(_ input: AVAudioSession.Category) -> String {
	return input.rawValue
}
