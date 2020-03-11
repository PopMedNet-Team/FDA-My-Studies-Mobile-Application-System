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

let kScheduleStartTime = "startTime"
let kScheduleEndTime = "endTime"

/**
 Schedule model schedules all the runs for the activities based on the frequency type and calculates the run for each activity
 */
class Schedule{
    
    var frequency: Frequency = .One_Time
    var startTime: Date!
    var endTime: Date?
    var lastRunTime: Date? = nil
    var nextRunTime: Date!
    weak var activity: Activity!
    var activityRuns: Array<ActivityRun>! = []
    var dailyFrequencyTimings: Array<Dictionary<String,Any>> = []
    
    var scheduledTimings: Array<Dictionary<String,Any>> = []
    var currentRunId = 0
    
    var completionHandler: ((Array<ActivityRun>) -> ())? = nil
    
    init(){
    }
    
    /**
     initializer method with dictionary used to initialize Scheduler
     */
    init(detail: Dictionary<String,Any>){
        
        if Utilities.isValidObject(someObject: detail as AnyObject?){
            
            if Utilities.isValidValue(someObject: detail[kActivityStartTime] as AnyObject ){
                self.startTime = Utilities.getDateFromString(dateString: detail[kActivityStartTime] as! String)
            }
            
            if Utilities.isValidValue(someObject: detail[kActivityEndTime] as AnyObject ){
                self.endTime = Utilities.getDateFromString(dateString: detail[kActivityEndTime] as! String)
            }
            
            if Utilities.isValidValue(someObject: detail[kActivityFrequency] as AnyObject ){
                self.frequency = Frequency(rawValue: detail[kActivityFrequency] as! String)!
            }
        } else {
            Logger.sharedInstance.debug("Schedule Dictionary is null:\(detail)")
        }
    }
    
    /**
     isAvailable methods returns boolean, which specifies whether the activity is avialble or not
     */
    func isAvailable() -> Bool{
        
        let currentDate = Date()
        var availablityStatus = true
        
        let result = currentDate.compare(endTime!)
        let startResult = currentDate.compare(startTime)
        if (startResult == .orderedSame || startResult == .orderedAscending) {
            availablityStatus = false
        }
        if result == .orderedDescending{
            availablityStatus = false
        }
        return availablityStatus
    }
    
    /**
     getRunsForActivity returns completion handler with array of ActivityRun
     @param activity, for which runs needed to be calculated
     */
    func getRunsForActivity(activity: Activity,handler: @escaping (Array<ActivityRun>) -> ()){
        
        //get joiningDate
        let studyStatus = User.currentUser.participatedStudies.filter({$0.studyId == activity.studyId}).last
        
        let joiningDate = studyStatus?.joiningDate.utcDate()
        let start = activity.startDate?.utcDate()
        
        self.completionHandler = handler
        var endDateResult: ComparisonResult? = nil
        if activity.endDate != nil {
            let end = activity.endDate?.utcDate()
            endDateResult = (end?.compare(joiningDate!))! as ComparisonResult
        }
        let startDateResult = (start?.compare(joiningDate!))! as ComparisonResult
        
        //check if user joined after activity is ended
        if  endDateResult != nil && endDateResult == .orderedAscending {
            if self.completionHandler != nil {
                self.completionHandler!(self.activityRuns)
            }
        } else {
            //check if user joined before activity is started
            if startDateResult == .orderedDescending {
                self.startTime = start
            } else {
                self.startTime = joiningDate
            }
            
            self.frequency = activity.frequencyType
            self.endTime = activity.endDate?.utcDate()
            self.activity = activity
            self.setActivityRun()
        }
    }
    
    /**
     setActivityRun decides the runType based on the frequency, it sets the completionHandler with the runs calculated
     */
    
    func setActivityRun(){
        
        switch self.frequency {
        case Frequency.One_Time:
            self.setOneTimeRun()
        case Frequency.Daily:
            self.setDailyFrequenyRuns()
        case Frequency.Weekly:
            self.setWeeklyRuns()
        case Frequency.Monthly:
            self.setMonthlyRuns()
        case Frequency.Scheduled:
            self.setScheduledRuns()
        }
        
        if self.completionHandler != nil {
            self.completionHandler!(self.activityRuns)
        }
    }
    
    //One Time Run setter
    func setOneTimeRun(){
        
        let offset = UserDefaults.standard.value(forKey: "offset") as? Int
        let updatedStartTime = startTime.addingTimeInterval(TimeInterval(offset!))
        let updatedEndTime = endTime?.addingTimeInterval(TimeInterval(offset!))
        
        let activityRun = ActivityRun()
        activityRun.runId = 1
        activityRun.startDate = updatedStartTime
        activityRun.endDate = updatedEndTime
        
        activityRuns.append(activityRun)
    }
    
    //DailyRun setter
    func setDailyRuns(){
        
        let numberOfDays = self.getNumberOfDaysBetween(startDate: startTime, endDate: endTime!)
        print("numberOfDays \(numberOfDays)")
        var runStartDate: Date? = startTime
        var runEndDate: Date? = nil
        let calendar = Calendar.currentUTC()
        for day in 1...numberOfDays {
            
            runStartDate =  calendar.date(byAdding:.day, value: day, to: startTime)
            runEndDate =  calendar.date(byAdding:.second, value:86399, to: runStartDate!)
            
            //appent in activity
            let activityRun = ActivityRun()
            activityRun.runId = day
            activityRun.startDate = runStartDate
            activityRun.endDate = runEndDate
            activityRuns.append(activityRun)
        }
    }
    
    //WeeklyRun Setter
    func setWeeklyRuns() {
        
        let offset = UserDefaults.standard.value(forKey: "offset") as? Int
        let updatedStartTime = startTime.addingTimeInterval(TimeInterval(offset!))
        let updatedEndTime = endTime?.addingTimeInterval(TimeInterval(offset!))
        
        
        let dayOfWeek = self.getCurrentWeekDay(date: updatedStartTime)
        let calendar = Calendar.currentUTC()
        let targetDay = self.getCurrentWeekDay(date: activity.startDate!) //server configurable
        
        //first day
        var runStartDate = calendar.date(byAdding: .weekday, value: (targetDay - dayOfWeek), to: updatedStartTime)
        var runId = 1
        while runStartDate?.compare(updatedEndTime!) == .orderedAscending {
            var runEndDate =  calendar.date(byAdding: .second, value:((7*86400) - 1), to: runStartDate!)
            if runEndDate?.compare(updatedEndTime!) == .orderedDescending {
                runEndDate = updatedEndTime
            }
            
            //appent in activity
            let activityRun = ActivityRun()
            activityRun.runId = runId
            activityRun.startDate = runStartDate
            activityRun.endDate = runEndDate
            activityRuns.append(activityRun)
            
            //save range
            runStartDate = calendar.date(byAdding: .second, value: 1, to: runEndDate!)
            runId += 1
        }
        
    }
    
    //MonthlyRun Setter
    func setMonthlyRuns(){
        
        let calendar = Calendar.currentUTC()
        let offset = UserDefaults.standard.value(forKey: "offset") as? Int
        let updatedStartTime = startTime.addingTimeInterval(TimeInterval(offset!))
        let updatedEndTime = endTime?.addingTimeInterval(TimeInterval(offset!))
        var runStartDate = updatedStartTime
        var runId = 1
        while runStartDate.compare(updatedEndTime!) == .orderedAscending {
            let nextRunStartDate =  calendar.date(byAdding: .month, value: 1*runId, to: updatedStartTime)
            var runEndDate = calendar.date(byAdding: .second, value: -1, to: nextRunStartDate!)
            //save range
            if runEndDate?.compare(updatedEndTime!) == .orderedDescending {
                runEndDate = updatedEndTime
            }
            
            //appent in activity
            let activityRun = ActivityRun()
            activityRun.runId = runId
            activityRun.startDate = runStartDate
            activityRun.endDate = runEndDate
            activityRuns.append(activityRun)
            
            runStartDate = nextRunStartDate!
            runId += 1
        }
    }
    
    //DailyFrequencyRun Setter
    func setDailyFrequenyRuns(){
        
        dailyFrequencyTimings = activity.frequencyRuns!
        
        var numberOfDays = self.getNumberOfDaysBetween(startDate: startTime, endDate: endTime!)
        let calendar = Calendar.currentUTC()
        var runId = 1
        let startDateString =  Schedule.formatter?.string(from: startTime)
        var startDateShortStyle = Schedule.formatter2?.date(from: startDateString!)
        
        if numberOfDays <= 0 {
            numberOfDays = 1;
        }
        
        for _ in 0...numberOfDays {
            
            let startDate = startDateShortStyle
            
            for timing in dailyFrequencyTimings{
                
                //run start time creation
                let dailyStartTime = timing[kScheduleStartTime] as! String
                var hoursAndMins = dailyStartTime.components(separatedBy: ":")
                var hour = Int((hoursAndMins[0]))
                var minutes = Int((hoursAndMins[1]))
                var second = Int((hoursAndMins[2]))
                
                var runStartDate =  calendar.date(byAdding: .hour, value: hour!, to: startDate!)
                runStartDate = calendar.date(byAdding: .minute, value: minutes!, to: runStartDate!)
                runStartDate = calendar.date(byAdding: .second, value: second!, to: runStartDate!)
                
                
                //run end time creation
                let dailyEndTime = timing[kScheduleEndTime] as! String
                hoursAndMins = dailyEndTime.components(separatedBy: ":")
                hour = Int((hoursAndMins[0]))
                minutes = Int((hoursAndMins[1]))
                second = Int((hoursAndMins[2]))
                
                var runEndDate =  calendar.date(byAdding: .hour, value: hour!, to: startDate!)
                runEndDate = calendar.date(byAdding: .minute, value: minutes!, to: runEndDate!)
                runEndDate = calendar.date(byAdding: .second, value: second!, to: runEndDate!)
                
                runStartDate = runStartDate?.utcDate()
                
                runEndDate = runEndDate?.utcDate()
                let offset = UserDefaults.standard.value(forKey: "offset") as? Int
                let updatedStartTime = runStartDate?.addingTimeInterval(TimeInterval(offset!))
                let updatedEndTime = runEndDate?.addingTimeInterval(TimeInterval(offset!))
                
                if !(updatedEndTime! < startTime) {
                    
                    //appent in activityRun array
                    let activityRun = ActivityRun()
                    activityRun.runId = runId
                    activityRun.startDate = updatedStartTime
                    activityRun.endDate = updatedEndTime
                    activityRuns.append(activityRun)
                    
                    runId += 1
                }
            }
            startDateShortStyle =  calendar.date(byAdding: .day, value: 1, to: startDateShortStyle!)
        }
    }
    
    //ScheduledRuns Setter
    func setScheduledRuns(){
        
        
        let offset = UserDefaults.standard.value(forKey: "offset") as? Int
        let activityEndTime = endTime?.addingTimeInterval(TimeInterval(offset!))
        var runId = 1
        
        let schedulingType = activity.schedulingType
        if schedulingType == .anchorDate {
            scheduledTimings = activity.anchorRuns!
        }
        else {
            scheduledTimings = activity.frequencyRuns!
        }
        
        //scheduledTimings = activity.frequencyRuns!
        
       
        for timing in scheduledTimings {
            
            
            var runStartDate:Date?
            var runEndDate:Date?
            
            if schedulingType == .anchorDate {
                let startDays = timing["startDays"] as? Int ?? 0
                let endDays = timing["endDays"] as? Int ?? 0
                _ = timing["time"] as? String ?? "00:00:00"
                
                let anchorDate = activity.anchorDate?.anchorDateValue
                
                let startDateInterval = TimeInterval(60*60*24*(startDays))
                let endDateInterval = TimeInterval(60*60*24*(endDays))
                
                runStartDate = anchorDate?.addingTimeInterval(startDateInterval)
                runEndDate = anchorDate?.addingTimeInterval(endDateInterval)
                
                //update start date
                var startDateString =  Utilities.formatterShort?.string(from: runStartDate!)
                let startTime =  timing["time"] as? String ?? "00:00:00"
                startDateString = (startDateString ?? "") + " " + startTime
                let startdate = Utilities.findDateFromString(dateString: startDateString ?? "")
                
                //update end date
                var endDateString =  Utilities.formatterShort?.string(from: runEndDate!)
                let endTime =  timing["time"] as? String ?? "23:59:59"
                endDateString = (endDateString ?? "") + " " + endTime
                let endDate = Utilities.findDateFromString(dateString: endDateString ?? "")
                
                runStartDate = startdate//getDateAfterAddingTimeComponent(time, date: runStartDate!)
                runEndDate = endDate//getDateAfterAddingTimeComponent(time, date: runEndDate!)
                
            }
            else {
                
                //run start time creation
                let scheduledStartTime = timing[kScheduleStartTime]
                 runStartDate =  Utilities.getDateFromStringWithOutTimezone(dateString: scheduledStartTime! as! String)
                
                //run end time creation
                let scheduledEndTime = timing[kScheduleEndTime]
                 runEndDate = Utilities.getDateFromStringWithOutTimezone(dateString: scheduledEndTime! as! String)
            }
            
            print("start date \(runStartDate!) , end date \(runEndDate!)")
            
            let offset = UserDefaults.standard.value(forKey: "offset") as? Int
            let updatedStartTime = runStartDate?.addingTimeInterval(TimeInterval(offset!))
            
            if activityEndTime! > updatedStartTime! {
                
                let updatedEndTime = runEndDate?.addingTimeInterval(TimeInterval(offset!))
                if !(updatedEndTime! < startTime) {
                    //appent in activityRun array
                    let activityRun = ActivityRun()
                    activityRun.runId = runId
                    activityRun.startDate = updatedStartTime
                    activityRun.endDate = updatedEndTime
                    activityRuns.append(activityRun)
                    
                    runId += 1
                }
            }
        }
    }
    
    func getDateAfterAddingTimeComponent(_ time:String, date: Date) -> Date? {
        
        var datetime:Date! = date
        let calendar = Calendar.currentUTC()
        let hoursAndMins = time.components(separatedBy: ":")
        let hour = Int((hoursAndMins[0]))
        let minutes = Int((hoursAndMins[1]))
        let second = Int((hoursAndMins[2]))
        
        datetime =  calendar.date(byAdding: .hour, value: hour!, to: datetime)
        datetime = calendar.date(byAdding: .minute, value: minutes!, to: datetime)
        datetime = calendar.date(byAdding: .second, value: second!, to: datetime)
        
        return datetime
    }
    
    // MARK:Utility Methods
    
    public static var _formatter: DateFormatter?
    public static var formatter: DateFormatter! {
        get {
            if let f = _formatter {
                return f
            } else {
                let formatter = DateFormatter()
                formatter.dateFormat = "YYYY-mm-dd"
                formatter.dateStyle = .short
                formatter.timeZone = TimeZone.init(abbreviation: "UTC")
                _formatter = formatter
                return formatter
            }
        }
        set(newValue) {
            _formatter = newValue
        }
    }
    
    public static var _formatter2: DateFormatter?
    public static var formatter2: DateFormatter! {
        
        get{
            
            if let f = _formatter2 {
                return f
            } else {
                let formatter = DateFormatter()
                formatter.dateFormat = "YYYY-mm-dd"
                formatter.dateStyle = .short
                formatter.timeZone = TimeZone.current // TimeZone.init(abbreviation:"IST")
                _formatter2 = formatter
                return formatter
            }
        }
        set(newValue){
            _formatter2 = newValue
        }
    }
    
    // MARK:-
    func getCurrentWeekDay(date: Date) -> Int{
        
        let calendar = Calendar.currentUTC()
        let component = calendar.dateComponents([.weekday], from: date)
        print(component.weekday! as Int)
        let dayOfWeek = component.weekday! as Int
        return dayOfWeek
    }
    
    func endOfDay(date: Date) -> (Date) {
        let calendar = Calendar.currentUTC()
        let endDate = calendar.date(byAdding: .day, value: 1, to: date)
        return (endDate!)
    }
    
    func getNumberOfWeeksBetween(startDate: Date,endDate: Date) -> Int {
        let calendar = Calendar.currentUTC()
        let date1 = calendar.startOfDay(for: startDate)
        let date2 = calendar.startOfDay(for: endDate) //calendar.startOfDay(for: endDate)
        
        let components = calendar.dateComponents([Calendar.Component.weekOfYear], from: date1, to: date2)
        
        print(components.weekOfYear! as Int)
        return components.weekOfYear! as Int
    }
    
    func getNumberOfDaysBetween(startDate: Date,endDate: Date) -> Int {
        
        let calendar = Calendar.currentUTC()
        
        // Replace the hour (time) of both dates with 00:00
        let date1 = startDate.startOfDay //calendar.startOfDay(for: startDate)
        let date2 = endDate.endOfDay//calendar.startOfDay(for: endDate)
        
        let components = calendar.dateComponents([Calendar.Component.day], from: date1, to: date2!)
        
        print(components.day! as Int)
        return components.day! as Int
    }
    
}

class ActivityRun {
    
    var startDate: Date!
    var endDate: Date!
    var complitionDate: Date!
    var runId: Int = 1
    var studyId: String!
    var activityId: String!
    var isCompleted: Bool = false
    var restortionData: Data?
    var toBeSynced: Bool = false
    var responseData: Data?
    
}

extension Calendar{
    
    static func currentUTC() -> Calendar {
        var calender = Calendar.current
        calender.timeZone = TimeZone(abbreviation: "UTC")!
        return calender
    }
}

extension Date{
    
    public static var todayUTC: Date{
        let timezone = TimeZone(abbreviation: "UTC")!
        var dateComponents = Calendar.current.dateComponents(in: timezone, from: Date())
        dateComponents.calendar = Calendar.currentUTC()
        let date = dateComponents.date
        return date!
    }
    
    public func utcDate()->Date{
        
        let timezone = TimeZone(abbreviation: "UTC")!
        var dateComponents = Calendar.current.dateComponents(in: timezone, from: self)
        dateComponents.calendar = Calendar.currentUTC()
        let date = dateComponents.date
        return date!
        
    }
}
