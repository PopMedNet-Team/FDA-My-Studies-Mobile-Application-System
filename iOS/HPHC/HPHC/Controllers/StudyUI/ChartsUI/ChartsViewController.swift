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


let kMessageForSharingCharts = "This action will create a shareable image file of the charts currently seen in this section. Proceed?"

class ChartsViewController: UIViewController {

    @IBOutlet weak var tableView: UITableView!
    @IBOutlet weak var backButton: UIButton!
    
    override func viewDidLoad() {
        super.viewDidLoad()

        // Do any additional setup after loading the view.
        
        self.title = NSLocalizedString("TRENDS", comment: "")
        //self.addBackBarButton()
        
        //unhide navigationbar
        //self.navigationController?.setNavigationBarHidden(false, animated: true)
        
    

    }
    override func viewWillAppear(_ animated: Bool) {
        super.viewWillAppear(animated)
       
        //UINavigationBar.appearance().tintColor = UIColor.red
        
        
        
        DBHandler.loadChartsForStudy(studyId: (Study.currentStudy?.studyId)!) { (chartList) in
            
            if chartList.count != 0 {
                StudyDashboard.instance.charts = chartList
                self.tableView?.reloadData()
            }
        }
    }

    override func didReceiveMemoryWarning() {
        super.didReceiveMemoryWarning()
        // Dispose of any resources that can be recreated.
    }
    
    @IBAction func backButtonAction(_ sender: UIButton){
        self.navigationController?.popViewController(animated: true)
    }
    
    @IBAction func shareButtonAction(_ sender: AnyObject){
        
        if StudyDashboard.instance.charts.count > 0{
            
            UIUtilities.showAlertMessageWithTwoActionsAndHandler(NSLocalizedString(kTitleMessage, comment: ""), errorMessage: NSLocalizedString(kMessageForSharingCharts, comment: ""), errorAlertActionTitle: NSLocalizedString(kTitleOK, comment: ""),
                                                                 errorAlertActionTitle2: NSLocalizedString(kTitleCancel, comment: ""), viewControllerUsed: self,
                                                                 action1: {
                                                                    
                                                                   self.shareScreenShotByMail()
                                                                    },
            
                                                                 action2: {
                                                                    
                                                                    
            })

            
            
            
            
        }
        
        
    }

    func shareScreenShotByMail() {
        
        //Create the UIImage
        
        let savedContentOffset = self.tableView.contentOffset
        let savedFrame = tableView.frame
        
        
        UIGraphicsBeginImageContextWithOptions(tableView.contentSize, self.view.isOpaque, 0.0)
        tableView.contentOffset = .zero
        tableView.frame = CGRect(x: 0, y: 0, width: tableView.contentSize.width, height: tableView.contentSize.height)
        tableView.layer.render(in: UIGraphicsGetCurrentContext()!)
        let image = UIGraphicsGetImageFromCurrentImageContext()
        UIGraphicsEndImageContext();
        
        tableView.contentOffset = savedContentOffset
        tableView.frame = savedFrame
        
        (self.tabBarController as! StudyDashboardTabbarViewController).shareScreenshotByEmail(image: image, subject: kEmailSubjectCharts,fileName: kEmailSubjectCharts)
    
    }
    
    /*
    // MARK: - Navigation

    // In a storyboard-based application, you will often want to do a little preparation before navigation
    override func prepare(for segue: UIStoryboardSegue, sender: Any?) {
        // Get the new view controller using segue.destinationViewController.
        // Pass the selected object to the new view controller.
    }
    */

}

extension ChartsViewController: UITableViewDelegate{
    
}
extension ChartsViewController: UITableViewDataSource{
    
    func tableView(_ tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
        return 1
    }
    func numberOfSections(in tableView: UITableView) -> Int {
        return StudyDashboard.instance.charts.count
    }
    func tableView(_ tableView: UITableView, cellForRowAt indexPath: IndexPath) -> UITableViewCell{
        
       
        
        let chart = StudyDashboard.instance.charts[indexPath.section]
        
        if chart.chartType == "line-chart"{
            
            let cell = tableView.dequeueReusableCell(withIdentifier: "lineChart") as! LineChartCell
            cell.setupLineChart(chart: chart)
            return cell
        } else {
            let cell = UITableViewCell()
            return cell
        }
        
       
    }
}
