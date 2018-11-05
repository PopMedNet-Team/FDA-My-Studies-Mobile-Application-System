/*
 License Agreement for FDA My Studies
 Copyright © 2017-2018 Harvard Pilgrim Health Care Institute (HPHCI) and its Contributors.
 Permission is hereby granted, free of charge, to any person obtaining a copy of this software and
 associated documentation files (the "Software"), to deal in the Software without restriction, including
 without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies
 of the Software, and to permit persons to whom the Software is furnished to do so, subject to the
 following conditions:
 
 The above copyright notice and this permission notice shall be included in all copies or substantial
 portions of the Software.
 
 Funding Source: Food and Drug Administration (“Funding Agency”) effective 18 September 2014 as Contract no. HHSF22320140030I/HHSF22301006T (the “Prime Contract”).
 
 THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 FITNESS FOR A PARTICULAR PURPOSE AND NON-INFRINGEMENT. IN NO EVENT SHALL
 THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR
 OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE,
 ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR
 OTHER DEALINGS IN THE SOFTWARE.
 */

import UIKit
import RealmSwift

// DB instance of Activity Model
class DBActivity: Object {
    
    dynamic var id: String?
    dynamic var type: String?
    dynamic var actvityId: String?
    dynamic var state: String?
    dynamic var studyId: String?
    dynamic var name: String?
    dynamic var shortName: String?
    dynamic var version: String?
    dynamic var lastModified: Date?
    
    dynamic var startDate: Date?
    dynamic var endDate: Date?
    dynamic var branching: Bool = false
    dynamic var randomization: Bool = false
    
    
    dynamic var frequencyRunsData: Data?
    dynamic var frequencyType: String?
    
    dynamic var currentRunId: String?
    dynamic var participationStatus: Int = 0
    dynamic var completedRuns: Int = 0
  
    dynamic var taskSubType: String?
  
    var activityRuns = List<DBActivityRun>()
    
    override static func primaryKey() -> String? {
        return "id"
    }
    
}
class DBActivityMetaData:Object {
    
    dynamic var actvityId: String?
    dynamic var studyId: String?
    dynamic var metaData: Data?
    
}
class DBActivityRun: Object {
    
    dynamic  var startDate: Date!
    dynamic  var endDate: Date!
    dynamic  var complitionDate: Date!
    dynamic  var runId: Int = 1
    dynamic  var studyId: String!
    dynamic  var activityId: String!
    dynamic  var isCompleted: Bool = false
    dynamic  var restortionData: Data?
    dynamic  var toBeSynced: Bool = false
    dynamic  var responseData: Data?
    
}


