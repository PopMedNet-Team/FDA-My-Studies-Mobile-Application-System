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
import RealmSwift


enum StatisticsFormula: String{
   case Summation
   case Average
   case Maximum
   case Minimum 

}

enum ChartTimeRange: String{
    
   case  days_of_week //s,m,t..s   f = daily
   case  days_of_month // 1,2,3,4..31   f = daily
   case  weeks_of_month // w1,w2,w3,w4.. w5   f = weekly
   case  months_of_year //j,f,m..d  f = monthly
   case  runs //   f = sheduled
   case  hours_of_day  // f = withInADay
   
}

class StudyDashboard: NSObject {
    
    var statistics: Array<DashboardStatistics>! = []
    var charts: Array<DashboardCharts>! = []
    var dashboardResponse: Array<DashboardResponse> = []
    static var instance = StudyDashboard()
    
    func saveDashboardResponse(responseList: Array<DashboardResponse>){
        self.dashboardResponse.append(contentsOf: responseList)
    }
}

class DashboardResponse{
    var key: String? //Stats key
    var activityId: String?
    var type: String?
    var values: Array<Dictionary<String,Any>> = []
    var date: String?
    var isPHI: String?
    
    init() {
        
    }
}
class DashboardStatistics {
    
    var statisticsId: String?
    var studyId: String?
    var title: String?
    var displayName: String?
    var unit: String?
    var calculation: String?
    var statType: String?
    var activityId: String?
    var activityVersion: String?
    var dataSourceType: String?
    var dataSourceKey: String?
    var statList = List<DBStatisticsData>()
    
    init() {
    }
    /**
     Initializer with dictionary of properties
    */
    init(detail: Dictionary<String,Any>) {
        
        if Utilities.isValidObject(someObject: detail as AnyObject?){
            
            
            if Utilities.isValidValue(someObject: detail["title"] as AnyObject ){
                self.title = detail["title"] as? String
            }
            else {
                self.title = "123"
            }
            if Utilities.isValidValue(someObject: detail["displayName"] as AnyObject ){
                self.displayName = detail["displayName"] as? String
            }
            if Utilities.isValidValue(someObject: detail["statType"] as AnyObject ){
                self.statType = detail["statType"] as? String
            }
            if Utilities.isValidValue(someObject: detail["unit"] as AnyObject ){
                self.unit = detail["unit"] as? String
            }
            if Utilities.isValidValue(someObject: detail["calculation"] as AnyObject ){
                self.calculation = detail["calculation"] as? String
            }
            
            let datasource = detail["dataSource"] as! Dictionary<String,Any>
            
            if Utilities.isValidValue(someObject: datasource["type"] as AnyObject ){
                self.dataSourceType = datasource["type"] as? String
            }
            if Utilities.isValidValue(someObject: datasource["key"] as AnyObject ){
                self.dataSourceKey = datasource["key"] as? String
            }
            
            let activity = datasource["activity"] as! Dictionary<String,Any>
            if Utilities.isValidValue(someObject: activity[kActivityId] as AnyObject ){
                self.activityId = activity[kActivityId] as? String
            }
            if Utilities.isValidValue(someObject: activity["version"] as AnyObject ){
                self.activityVersion = activity["version"] as? String
            }
            self.studyId = Study.currentStudy?.studyId

            self.statisticsId = self.studyId! + self.title!
        }
    }
    
}

class DashboardCharts {
    
    //basic
    var chartId: String?
    var studyId: String?
    var title: String?
    var displayName: String?
    var chartType: String?
    var scrollable: Bool = true
    
    //datasource
    var activityId: String?
    var activityVersion: String?
    var dataSourceType: String?
    var dataSourceKey: String?
    var dataSourceTimeRange: String?
    var startTime: Date?
    var endTime: Date?
    
    
    //settings
    var barColor: String?
    var numberOfPoints: Int = 0
    var chartSubType: String?
    
    
    var statList = List<DBStatisticsData>()
    
    init() {
    }
    
    /**
     initializer with dictionary of properties
    */
    init(detail: Dictionary<String,Any>) {
    
        if Utilities.isValidObject(someObject: detail as AnyObject?){
            
            
            if Utilities.isValidValue(someObject: detail["title"] as AnyObject ){
                self.title = detail["title"] as? String
            }
            else {
                self.title = "123"
            }
            if Utilities.isValidValue(someObject: detail["displayName"] as AnyObject ){
                self.displayName = detail["displayName"] as? String
            }
            if Utilities.isValidValue(someObject: detail["type"] as AnyObject ){
                self.chartType = detail["type"] as? String
            }
            if Utilities.isValidValue(someObject: detail["scrollable"] as AnyObject ){
                self.scrollable = detail["scrollable"] as! Bool
            }
            
            
            //datasource
            let datasource = detail["dataSource"] as! Dictionary<String,Any>
            
            if Utilities.isValidValue(someObject: datasource["type"] as AnyObject ){
                self.dataSourceType = datasource["type"] as? String
            }
            if Utilities.isValidValue(someObject: datasource["key"] as AnyObject ){
                self.dataSourceKey = datasource["key"] as? String
            }
            if Utilities.isValidValue(someObject: datasource["timeRangeType"] as AnyObject ){
                self.dataSourceTimeRange = datasource["timeRangeType"] as? String
            }

            // activity detail
            let activity = datasource["activity"] as! Dictionary<String,Any>
            if Utilities.isValidValue(someObject: activity[kActivityId] as AnyObject ){
                self.activityId = activity[kActivityId] as? String
            }
            if Utilities.isValidValue(someObject: activity["version"] as AnyObject ){
                self.activityVersion = activity["version"] as? String
            }
            
            //configuration
            let configuration = detail["configuration"] as! Dictionary<String,Any>
            if Utilities.isValidValue(someObject: configuration["subType"] as AnyObject ){
                self.chartSubType = configuration["subType"] as? String
            }
            
            self.studyId = Study.currentStudy?.studyId
          
          self.chartId = self.studyId! + (self.activityId == nil ? "" : self.activityId!) + self.dataSourceKey!
        }
    }
}
