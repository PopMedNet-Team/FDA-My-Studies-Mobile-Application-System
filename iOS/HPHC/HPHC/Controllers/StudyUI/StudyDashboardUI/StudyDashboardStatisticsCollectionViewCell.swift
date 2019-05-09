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

enum StatisticsType: String{
    case  Activity
    case  Sleep
    case  Weight
    case  Nutrition
    case  HeartRate
    case  BloodGlucose
    case  ActiveTask
    case  BabyKicks
    case  Mood
    case  Other
    
    var description: String{
        switch self {
        case  .Activity:
            return "Activity"
        case  .Sleep:
            return "Sleep"
        case  .Weight:
            return "Weight"
        case  .Nutrition:
            return "Nutrition"
        case  .HeartRate:
            return "Heart Rate"
        case  .BloodGlucose:
            return "Blood Glucose"
        case  .ActiveTask:
            return "Active Task"
        case  .BabyKicks:
            return "Baby Kicks"
        case  .Mood:
            return "Mood"
        case  .Other:
            return "Other"
        }
    }
}


class StudyDashboardStatisticsCollectionViewCell: UICollectionViewCell {
    
    //Statistics cell
    @IBOutlet var statisticsImage: UIImageView?
    @IBOutlet var labelStatisticsText: UILabel?
    @IBOutlet var labelStatisticsCount: UILabel?
    @IBOutlet var labelUnit: UILabel?
    var stats: DashboardStatistics!
    
    /**
     Display DashboardStats for selected Tab
     */
    func displayStatisics(data: DashboardStatistics,startDate: Date,endDate: Date?,tab: SelectedTab){
        
        stats = data
        labelStatisticsText?.text = data.displayName
        labelUnit?.text = data.unit?.uppercased()
        
        self.displayStateTypeImage()
        
        switch tab {
        case .Day:
            self.handleForDay(date: startDate)
        case .Week:
            self.handleForWeek(startDate: startDate, endDate: endDate!)
        case .Month:
            self.handleForMonth(date: startDate)
        }
    }
    
    func displayStateTypeImage(){
        
        if stats.statType == nil {
            statisticsImage?.image = UIImage(named: "activity")
            
        } else {
            
            switch stats.statType! {
            case  StatisticsType.Activity.description:
                statisticsImage?.image = UIImage(named: "stat_icn_active_task")
            case  StatisticsType.Sleep.description:
                statisticsImage?.image = UIImage(named: "stat_icn_sleep")
            case  StatisticsType.Weight.description:
                statisticsImage?.image = UIImage(named: "stat_icn_weight")
            case  StatisticsType.Nutrition.description:
                statisticsImage?.image = UIImage(named: "stat_icn_nutrition")
            case  StatisticsType.HeartRate.description:
                statisticsImage?.image = UIImage(named: "stat_icn_heart_rate")
            case  StatisticsType.BloodGlucose.description:
                statisticsImage?.image = UIImage(named: "stat_icn_glucose")
            case  StatisticsType.ActiveTask.description:
                statisticsImage?.image = UIImage(named: "stat_icn_active_task")
            case  StatisticsType.BabyKicks.description:
                statisticsImage?.image = UIImage(named: "stat_icn_baby_kicks")
            case  StatisticsType.Mood.description:
                statisticsImage?.image = UIImage(named: "stat_icn_mood")
            case  StatisticsType.Other.description:
                statisticsImage?.image = UIImage(named: "stat_icn_other")
            default :
                statisticsImage?.image = UIImage(named: "stat_icn_other")
            }
        }
        
        
    }
    
    /**
     Used to display Statistics cell
     @param data    Accepts the data from Dictionary
     */
    func displayStatisics(data: DashboardStatistics){
        
        labelStatisticsText?.text = data.displayName
        let array = data.statList.map{$0.data}
        
        if  data.calculation! == StatisticsFormula.Maximum.rawValue {
            let max = array.max()
            labelStatisticsCount?.text = String(describing: max)
        }
        if  data.calculation! == StatisticsFormula.Minimum.rawValue {
            let min = array.min()
            labelStatisticsCount?.text = String(describing: min)
        }
        if  data.calculation! == StatisticsFormula.Average.rawValue {
            let sumArray = array.reduce(0, +)
            let avgArrayValue = sumArray / Float(array.count)
            labelStatisticsCount?.text = String(describing: avgArrayValue)
        }
        if  data.calculation! == StatisticsFormula.Summation.rawValue {
            let sumArray = array.reduce(0, +)
            labelStatisticsCount?.text = String(describing: sumArray)
        }
    }
    
    
    // MARK:- Date Handlers
    func handleForDay(date: Date){
        
        let dataList: Array<DBStatisticsData> = stats.statList.filter({$0.startDate! >= date.startOfDay && $0.startDate! <= date.endOfDay!})
        
        let array = dataList.map{$0.data}
        
        if array.count == 0 {
            labelStatisticsCount?.text = "NA"
        } else {
            self.calculate(array: array)
        }
    }
    func handleForWeek(startDate: Date,endDate: Date){
        
        let dataList: Array<DBStatisticsData> = stats.statList.filter({$0.startDate! >= startDate && $0.startDate! <= endDate})
        
        let array = dataList.map{$0.data}
        
        if array.count == 0 {
            labelStatisticsCount?.text = "NA"
        } else {
            self.calculate(array: array)
        }
        
    }
    func handleForMonth(date: Date){
        
        let dataList: Array<DBStatisticsData> = stats.statList.filter({$0.startDate! >= date.startOfMonth() && $0.startDate! <= date.endOfMonth()})
        
        let array = dataList.map{$0.data}
        
        if array.count == 0 {
            labelStatisticsCount?.text = "NA"
        } else {
            self.calculate(array: array)
        }
        
    }
    
    /**
     calculates the stats data and Updates Stats Label
     */
    func calculate(array: Array<Float>){
        
        
        let data = self.stats!
        
        if  data.calculation! == StatisticsFormula.Maximum.rawValue {
            let max = array.max()
            let maxValue: String! = String(format:"%.2f", max!)
            labelStatisticsCount?.text = maxValue
        }
        if  data.calculation! == StatisticsFormula.Minimum.rawValue {
            let min = array.min()
            let minValue = String(format:"%.2f", min!)
            labelStatisticsCount?.text = minValue
        }
        if  data.calculation! == StatisticsFormula.Average.rawValue {
            let sumArray = array.reduce(0, +)
            let avgArrayValue = sumArray / Float(array.count)
            let avgValue = String(format:"%.2f", avgArrayValue)
            labelStatisticsCount?.text = avgValue
        }
        if  data.calculation! == StatisticsFormula.Summation.rawValue {
            let sumArray = array.reduce(0, +)
            let sumValue = String(format:"%.2f", sumArray)
            labelStatisticsCount?.text = sumValue
        }
    }
    
}
