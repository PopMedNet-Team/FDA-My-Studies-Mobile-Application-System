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



enum SelectedTab: String{
    case Day
    case Week
    case Month
}
class StudyDashboardStatisticsTableViewCell: UITableViewCell {
    
    
    
    
    //Fifth cell Outlets
    @IBOutlet var statisticsCollectionView: UICollectionView?
    @IBOutlet var buttonDay: UIButton?
    @IBOutlet var buttonWeek: UIButton?
    @IBOutlet var buttonMonth: UIButton?
    @IBOutlet var buttonForward: UIButton?
    @IBOutlet var buttonBackward: UIButton?
    @IBOutlet var labelDateValue: UILabel?
    @IBOutlet var labelNoData: UILabel?
    
    var statisticsArrayData: NSMutableArray?
    var selectedTab: SelectedTab = .Day
    var todaysDate = Date()
    var startDateOfWeek: Date?
    var endDateOfWeek: Date?
    
    override func awakeFromNib() {
        super.awakeFromNib()
    }
    
    override func setSelected(_ selected: Bool, animated: Bool) {
        super.setSelected(selected, animated: animated)
        
    }
    
    func displayData(){
        
        let stringDate = StudyDashboardStatisticsTableViewCell.formatter.string(from: todaysDate)
        let color = Utilities.getUIColorFromHex(0x007CBA)
        
        let attributedStartDate: NSMutableAttributedString = NSMutableAttributedString(string: stringDate)
        attributedStartDate.addAttribute(NSAttributedString.Key.foregroundColor, value: color, range: NSMakeRange(0, 2))
        labelDateValue?.attributedText = attributedStartDate
        self.buttonForward?.isEnabled = false
        
        if StudyDashboard.instance.statistics.count == 0  {
            labelNoData?.isHidden = false
            self.statisticsCollectionView?.isHidden = true
        }
    }
    
    func getWeeklyAttributedText()-> NSAttributedString{
        
        var startDate = startDateOfWeek
        startDate = startDate?.addingTimeInterval(24*3600)
        let stringStartDate = StudyDashboardStatisticsTableViewCell.formatter.string(from: startDate!) + " - "
        let stringEndDate = StudyDashboardStatisticsTableViewCell.formatter.string(from: endDateOfWeek!)
        
        let color = Utilities.getUIColorFromHex(0x007CBA)
        
        let attributedStartDate: NSMutableAttributedString = NSMutableAttributedString(string: stringStartDate)
        attributedStartDate.addAttribute(NSAttributedString.Key.foregroundColor, value: color, range: NSMakeRange(0, 2))
        
        let attributedEndDate: NSMutableAttributedString = NSMutableAttributedString(string: stringEndDate)
        attributedEndDate.addAttribute(NSAttributedString.Key.foregroundColor, value: color, range: NSMakeRange(0, 2))
        
        
        attributedStartDate.append(attributedEndDate)
        
        return attributedStartDate
    }
    
    // MARK:- Button action
    
    /**
     Day, Week and Month Button clicked
     @param sender    Accepts Any object
     */
    @IBAction func dayWeekMonthButtonAction(_ sender: AnyObject){
        
        if sender.tag == 11{
            //Day clicked
            buttonDay?.setTitle(kDaySpaces, for: UIControl.State.normal)
            buttonWeek?.setTitle(kWeek, for: UIControl.State.normal)
            buttonMonth?.setTitle(kMonth, for: UIControl.State.normal)
            
            buttonDay?.setTitleColor(UIColor.white, for: UIControl.State.normal)
            buttonWeek?.setTitleColor(kGreyColor, for: UIControl.State.normal)
            buttonMonth?.setTitleColor(kGreyColor, for: UIControl.State.normal)
            
            buttonDay?.backgroundColor = kDarkBlueColor
            buttonWeek?.backgroundColor = UIColor.white
            buttonMonth?.backgroundColor = UIColor.white
            
            self.selectedTab = .Day
            todaysDate = Date()
            
            let stringDate = StudyDashboardStatisticsTableViewCell.formatter.string(from: Date())
            let color = Utilities.getUIColorFromHex(0x007CBA)
            
            let attributedStartDate: NSMutableAttributedString = NSMutableAttributedString(string: stringDate)
            attributedStartDate.addAttribute(NSAttributedString.Key.foregroundColor, value: color, range: NSMakeRange(0, 2))
            labelDateValue?.attributedText = attributedStartDate
            self.buttonForward?.isEnabled = false
            
        } else if sender.tag == 12 {
            //Week clicked
            buttonWeek?.setTitle(kWeekSpaces, for: UIControl.State.normal)
            buttonDay?.setTitle(kDay, for: UIControl.State.normal)
            buttonMonth?.setTitle(kMonth, for: UIControl.State.normal)
            
            buttonWeek?.setTitleColor(UIColor.white, for: UIControl.State.normal)
            buttonDay?.setTitleColor(kGreyColor, for: UIControl.State.normal)
            buttonMonth?.setTitleColor(kGreyColor, for: UIControl.State.normal)
            
            buttonWeek?.backgroundColor = kDarkBlueColor
            buttonDay?.backgroundColor = UIColor.white
            buttonMonth?.backgroundColor = UIColor.white
            
            self.selectedTab = .Week
            todaysDate = Date()
            
            startDateOfWeek = todaysDate.startOfWeek
            endDateOfWeek = todaysDate.endOfWeek
            
            let attributedText = self.getWeeklyAttributedText()
            labelDateValue?.attributedText = attributedText//stringStartDate + " - " + stringEndDate
            self.buttonForward?.isEnabled = false
            
        } else if sender.tag == 13 {
            
            //Months clicked
            buttonMonth?.setTitle(kMonthSpaces, for: UIControl.State.normal)
            buttonDay?.setTitle(kDay, for: UIControl.State.normal)
            buttonWeek?.setTitle(kWeek, for: UIControl.State.normal)
            
            buttonMonth?.setTitleColor(UIColor.white, for: UIControl.State.normal)
            buttonDay?.setTitleColor(kGreyColor, for: UIControl.State.normal)
            buttonWeek?.setTitleColor(kGreyColor, for: UIControl.State.normal)
            
            buttonMonth?.backgroundColor = kDarkBlueColor
            buttonDay?.backgroundColor = UIColor.white
            buttonWeek?.backgroundColor = UIColor.white
            
            self.selectedTab = .Month
            todaysDate = Date()
            
            let stringDate = StudyDashboardStatisticsTableViewCell.monthFormatter.string(from: todaysDate)
            self.labelDateValue?.text = stringDate
            self.buttonForward?.isEnabled = false
        }
        
        self.statisticsCollectionView?.reloadData()
    }
    
    @IBAction func buttonForwardClicked(_ sender: UIButton){
        
        let calendar = Calendar.current
        
        switch self.selectedTab {
        case .Day:
            
            self.buttonForward?.isEnabled = true
            self.buttonBackward?.isEnabled = true
            todaysDate = calendar.date(byAdding: .day, value: 1, to: todaysDate)!
            let stringDate = StudyDashboardStatisticsTableViewCell.formatter.string(from: todaysDate)
            let color = Utilities.getUIColorFromHex(0x007CBA)
            
            let attributedStartDate: NSMutableAttributedString = NSMutableAttributedString(string: stringDate)
            attributedStartDate.addAttribute(NSAttributedString.Key.foregroundColor, value: color, range: NSMakeRange(0, 2))
            labelDateValue?.attributedText = attributedStartDate
            
            
            
            let result = todaysDate.compare(Date())
            if result == .orderedSame || result == .orderedDescending{
                self.buttonForward?.isEnabled = false
            }
            
        case .Week:
            
            self.buttonForward?.isEnabled = true
            self.buttonBackward?.isEnabled = true
            
            startDateOfWeek = calendar.date(byAdding: .day, value: 7, to: startDateOfWeek!)
            endDateOfWeek = calendar.date(byAdding: .day, value: 7, to: endDateOfWeek!)
            
            let attributedText = self.getWeeklyAttributedText()
            labelDateValue?.attributedText = attributedText//stringStartDate + " - " + stringEndDate
            
            let result = todaysDate.compare(Date())
            if result == .orderedSame || result == .orderedDescending{
                self.buttonForward?.isEnabled = false
            }
            
        case .Month:
            
            self.buttonForward?.isEnabled = true
            self.buttonBackward?.isEnabled = true
            todaysDate = calendar.date(byAdding: .month, value: 1, to: todaysDate)!
            let stringDate = StudyDashboardStatisticsTableViewCell.monthFormatter.string(from: todaysDate)
            labelDateValue?.text = stringDate
            
            let result = todaysDate.compare(Date())
            if result == .orderedSame || result == .orderedDescending{
                self.buttonForward?.isEnabled = false
            }
            
        }
        self.statisticsCollectionView?.reloadData()
    }
    
    @IBAction func buttonBackwardClicked(_ sender: UIButton){
        
        let calendar = Calendar.current
        
        switch self.selectedTab {
        case .Day:
            
            self.buttonForward?.isEnabled = true
            self.buttonBackward?.isEnabled = true
            todaysDate = calendar.date(byAdding: .day, value: -1, to: todaysDate)!
            let stringDate = StudyDashboardStatisticsTableViewCell.formatter.string(from: todaysDate)
            let color = Utilities.getUIColorFromHex(0x007CBA)
            
            let attributedStartDate: NSMutableAttributedString = NSMutableAttributedString(string: stringDate)
            attributedStartDate.addAttribute(NSAttributedString.Key.foregroundColor, value: color, range: NSMakeRange(0, 2))
            labelDateValue?.attributedText = attributedStartDate
            
        case .Week:
            
            self.buttonForward?.isEnabled = true
            self.buttonBackward?.isEnabled = true
            
            startDateOfWeek = calendar.date(byAdding: .day, value: -7, to: startDateOfWeek!)
            endDateOfWeek = calendar.date(byAdding: .day, value: -7, to: endDateOfWeek!)
            let stringStartDate = StudyDashboardStatisticsTableViewCell.formatter.string(from: startDateOfWeek!) + " - "
            let stringEndDate = StudyDashboardStatisticsTableViewCell.formatter.string(from: endDateOfWeek!)
            
            
            let color = Utilities.getUIColorFromHex(0x007CBA)
            
            let attributedStartDate:NSMutableAttributedString = NSMutableAttributedString(string: stringStartDate)
            attributedStartDate.addAttribute(NSAttributedString.Key.foregroundColor, value: color, range: NSMakeRange(0, 2))
            
            let attributedEndDate: NSMutableAttributedString = NSMutableAttributedString(string: stringEndDate)
            attributedEndDate.addAttribute(NSAttributedString.Key.foregroundColor, value: color, range: NSMakeRange(0, 2))
            attributedStartDate.append(attributedEndDate)
            
            labelDateValue?.attributedText = attributedStartDate //stringStartDate + " - " + stringEndDate
            
        case .Month:
            
            self.buttonForward?.isEnabled = true
            self.buttonBackward?.isEnabled = true
            todaysDate = calendar.date(byAdding: .month, value: -1, to: todaysDate)!
            let stringDate = StudyDashboardStatisticsTableViewCell.monthFormatter.string(from: todaysDate)
            labelDateValue?.text = stringDate
            
        }
        
        self.statisticsCollectionView?.reloadData()
    }
    
    
    
    // MARK: - FORMATERS
    private static let formatter: DateFormatter = {
        let formatter = DateFormatter()
        formatter.dateFormat = "dd, MMM YYYY"
        formatter.timeZone = TimeZone.init(abbreviation: "GMT")
        return formatter
    }()
    private static let yearFormatter: DateFormatter = {
        let formatter = DateFormatter()
        formatter.dateFormat = "YYYY"
        formatter.timeZone = TimeZone.init(abbreviation: "GMT")
        return formatter
    }()
    private static let monthFormatter: DateFormatter = {
        let formatter = DateFormatter()
        formatter.dateFormat = "MMM YYYY"
        formatter.timeZone = TimeZone.init(abbreviation: "GMT")
        return formatter
    }()
}

// MARK:- Collection delegates
extension StudyDashboardStatisticsTableViewCell: UICollectionViewDelegate, UICollectionViewDataSource {
    
    func collectionView(_ collectionView: UICollectionView, numberOfItemsInSection section: Int) -> Int {
        return StudyDashboard.instance.statistics.count
    }
    
    func collectionView(_ collectionView: UICollectionView, cellForItemAt indexPath: IndexPath) -> UICollectionViewCell {
        
        let cell = collectionView.dequeueReusableCell(withReuseIdentifier: kStatisticsCollectionViewCell, for: indexPath) as! StudyDashboardStatisticsCollectionViewCell
        let stats = StudyDashboard.instance.statistics[indexPath.row]
        
        if selectedTab == .Week {
            cell.displayStatisics(data: stats, startDate: startDateOfWeek!, endDate: endDateOfWeek, tab: selectedTab)
            
        } else {
            cell.displayStatisics(data: stats, startDate: todaysDate, endDate: nil, tab: selectedTab)
        }
        
        return cell
    }
    
    func collectionView(_ collectionView: UICollectionView, didSelectItemAt indexPath: IndexPath) {
        
        
        
    }
}


