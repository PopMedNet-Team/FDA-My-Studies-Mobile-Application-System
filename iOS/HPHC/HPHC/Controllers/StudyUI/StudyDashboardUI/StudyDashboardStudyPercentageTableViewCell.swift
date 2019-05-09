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

class StudyDashboardStudyPercentageTableViewCell: UITableViewCell {
    
    //Third cell Outlets
    @IBOutlet var labelStudyCompletion: UILabel?
    @IBOutlet var labelStudyAdherence: UILabel?
    
    @IBOutlet var studyPercentagePie: KDCircularProgress?
    @IBOutlet var completedPercentagePie: KDCircularProgress?
    
    let blueColor = UIColor.init(red: 56/255, green: 124/255, blue: 186/255, alpha: 1)
    let greyColor = UIColor.init(red: 216/255, green: 227/255, blue: 230/255, alpha: 1)
    
    
    override func awakeFromNib() {
        super.awakeFromNib()
    }
    
    override func setSelected(_ selected: Bool, animated: Bool) {
        super.setSelected(selected, animated: animated)
        
    }
    
    
    /**
     Used to display Study Percentage cell
     @param data    Accepts the data from Dictionary
     */
    func displayThirdCellData(data: NSDictionary){
        let currentUser = User.currentUser
        let study = Study.currentStudy
        
        studyPercentagePie?.startAngle = -90
        studyPercentagePie?.progressThickness = 0.25
        studyPercentagePie?.trackThickness = 0.25
        studyPercentagePie?.trackColor = greyColor
        studyPercentagePie?.clockwise = true
        studyPercentagePie?.gradientRotateSpeed = 2
        studyPercentagePie?.roundedCorners = false
        studyPercentagePie?.glowMode = .forward
        studyPercentagePie?.glowAmount = 0.1
        studyPercentagePie?.set(colors: blueColor)
        
        completedPercentagePie?.startAngle = -90
        completedPercentagePie?.progressThickness = 0.25
        completedPercentagePie?.trackThickness = 0.25
        completedPercentagePie?.trackColor = greyColor
        completedPercentagePie?.clockwise = true
        completedPercentagePie?.gradientRotateSpeed = 2
        completedPercentagePie?.roundedCorners = false
        completedPercentagePie?.glowMode = .forward
        completedPercentagePie?.glowAmount = 0.1
        completedPercentagePie?.set(colors: blueColor)
        
        if let userStudyStatus = currentUser.participatedStudies.filter({$0.studyId == study?.studyId}).first {
            //update completion %
            
            if ( Study.currentStudy?.totalIncompleteRuns == 0
                && Study.currentStudy?.totalCompleteRuns ==  0){
                
                self.labelStudyCompletion?.text = "--"
                self.labelStudyAdherence?.text = "--"
                
                studyPercentagePie?.angle = 0
                completedPercentagePie?.angle = 0
            } else {
                
                self.labelStudyCompletion?.text = String(userStudyStatus.adherence)
                self.labelStudyAdherence?.text = String(userStudyStatus.completion)
                
                studyPercentagePie?.angle = Double(userStudyStatus.completion)/0.27777778
                completedPercentagePie?.angle = Double(userStudyStatus.adherence)/0.27777778
            }
            
        }else {
            self.labelStudyCompletion?.text = "0"
            self.labelStudyAdherence?.text = "0"
            
            studyPercentagePie?.angle = 0
            completedPercentagePie?.angle = 0
        }
    }
}



