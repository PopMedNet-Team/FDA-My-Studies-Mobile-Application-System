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

// DB instance of Activity Model
class DBActivity: Object {
    
    @objc dynamic var id: String?
    @objc dynamic var type: String?
    @objc dynamic var actvityId: String?
    @objc dynamic var state: String?
    @objc dynamic var studyId: String?
    @objc dynamic var name: String?
    @objc dynamic var shortName: String?
    @objc dynamic var version: String?
    @objc dynamic var lastModified: Date?
    
    @objc dynamic var startDate: Date?
    @objc dynamic var endDate: Date?
    @objc dynamic var branching: Bool = false
    @objc dynamic var randomization: Bool = false
    
    
    @objc dynamic var frequencyRunsData: Data?
    @objc dynamic var anchorRunsData: Data?
    @objc dynamic var frequencyType: String?
    @objc dynamic var schedulingType:String?
    
    @objc dynamic var currentRunId: String?
    @objc dynamic var participationStatus: Int = 0
    @objc dynamic var completedRuns: Int = 0
    
    @objc dynamic var sourceType:String?
    @objc dynamic var sourceActivityId:String?
    @objc dynamic var sourceKey:String?
    @objc dynamic var sourceFormKey:String?
    @objc dynamic var startDays:Int = 0
    @objc dynamic var startTime:String?
    @objc dynamic var endDays:Int = 0
    @objc dynamic var repeatInterval:Int = 0
    @objc dynamic var endTime:String?
    @objc dynamic var anchorDateValue:Date?
  
    @objc dynamic var taskSubType: String?
  
    var activityRuns = List<DBActivityRun>()
    
    override static func primaryKey() -> String? {
        return "id"
    }
    
}
class DBActivityMetaData:Object {
    
    @objc dynamic var actvityId: String?
    @objc dynamic var studyId: String?
    @objc dynamic var metaData: Data?
    
}
class DBActivityRun: Object {
    
    @objc dynamic  var startDate: Date!
    @objc dynamic  var endDate: Date!
    @objc dynamic  var complitionDate: Date!
    @objc dynamic  var runId: Int = 1
    @objc dynamic  var studyId: String!
    @objc dynamic  var activityId: String!
    @objc dynamic  var isCompleted: Bool = false
    @objc dynamic  var restortionData: Data?
    @objc dynamic  var toBeSynced: Bool = false
    @objc dynamic  var responseData: Data?
    
}


