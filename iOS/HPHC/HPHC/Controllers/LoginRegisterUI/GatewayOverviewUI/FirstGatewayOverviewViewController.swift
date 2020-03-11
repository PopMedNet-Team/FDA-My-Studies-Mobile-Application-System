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
import AVKit

class FirstGatewayOverviewViewController: UIViewController{
    
    var tableViewRowDetails: NSMutableArray!
    
    @IBOutlet var imageViewBackgroundImage: UIImageView?
    @IBOutlet var buttonWatchVideo: UIButton?
    @IBOutlet var buttonGetStarted: UIButton?
    @IBOutlet var labelDescriptionText: UILabel?
    @IBOutlet var labelTitleText: UILabel?
    
    var pageIndex: Int!
    var overviewSectionDetail: OverviewSection!
    var moviePlayer: MPMoviePlayerViewController!
    var playerViewController: AVPlayerViewController!
    override var preferredStatusBarStyle: UIStatusBarStyle {
        return .lightContent
    }
    
// MARK:- View Controller Lifecycle
    override func viewDidLoad() {
        super.viewDidLoad()
        
        if overviewSectionDetail.link == nil {
            buttonWatchVideo?.isHidden = true
        }
        
    }
    
    override func viewWillAppear(_ animated: Bool) {
        super.viewWillAppear(animated)
        
        labelTitleText?.text = overviewSectionDetail.title
        labelDescriptionText?.text = overviewSectionDetail.text
        imageViewBackgroundImage?.image = UIImage.init(named: overviewSectionDetail.imageURL!)
        
        //UIApplication.shared.statusBarStyle = .lightContent
        setNeedsStatusBarAppearanceUpdate()

    }
    
    override func viewDidDisappear(_ animated: Bool) {
       
    }
    
    override func viewDidAppear(_ animated: Bool) {
        super.viewDidAppear(animated)
        
        
    }
    
// MARK:- Movie Player methods
    
    /**
     
     Movie player completion method
     
     @param notification    receives the paticular video completion notification

     */
    func moviePlayBackDidFinish(notification: NSNotification) {
        //  println("moviePlayBackDidFinish:")
        moviePlayer.moviePlayer.stop()
        NotificationCenter.default.removeObserver(self, name: NSNotification.Name.MPMoviePlayerPlaybackDidFinish, object: nil)
        moviePlayer.dismiss(animated: true, completion: nil)
    }
    func playerDidFinishPlaying(note: NSNotification) {
        NotificationCenter.default.removeObserver(self, name: NSNotification.Name.AVPlayerItemDidPlayToEndTime, object: nil)
        self.playerViewController.dismiss(animated: true, completion: nil)
    }
    
    
// MARK:- Button Action
    
    /**
     
     Watch video button clicked
     
     @param sender    accepts any kind of object

     */
    @IBAction func watchVideoButtonClicked(_ sender: Any){
        let urlString = overviewSectionDetail.link!
        if urlString.contains("youtube"){
            let url = URL.init(string: urlString)
            UIApplication.shared.openURL(url!)
        } else {
            
            let url = URL.init(string: urlString)
            
            let player = AVPlayer(url: url!)
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
     
     This method is used to create FDASlideMenuViewController and Gateway storyboard
     
     @param sender    accepts any kind of object

     */
    @IBAction func getStartedButtonClicked(_ sender: Any){
        self.createMenuView()
    }
    
    
    /**
     
     Create the menu view using FDASlideMenuViewController and Gateway storyboard

     */
    func createMenuView() {
        
        let storyboard = UIStoryboard(name: kStoryboardIdentifierGateway, bundle: nil)
        
        let fda = storyboard.instantiateViewController(withIdentifier: kStoryboardIdentifierSlideMenuVC) as! FDASlideMenuViewController
             fda.automaticallyAdjustsScrollViewInsets = true
        self.navigationController?.pushViewController(fda, animated: true)

    }
}

