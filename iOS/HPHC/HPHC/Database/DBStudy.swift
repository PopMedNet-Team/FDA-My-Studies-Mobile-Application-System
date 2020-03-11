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

/**
 DB instance of Study Model
 */
class DBStudy: Object {
    
    @objc dynamic var studyId: String!
    @objc dynamic var name: String?
    @objc dynamic var version: String?
    @objc dynamic var updatedVersion: String?
    @objc dynamic var identifer: String?
    @objc dynamic var category: String?
    @objc dynamic var startDate: String?
    @objc dynamic var endEnd: String?
    @objc dynamic var status: String?
    @objc dynamic var sponserName: String?
    @objc dynamic var tagLine: String?
    @objc dynamic var brandingConfiguration: String?
    @objc dynamic var logoURL: String?
    @objc dynamic var websiteLink: String?
    @objc dynamic var bookmarked: Bool = false
    @objc dynamic var updateResources: Bool = false
    @objc dynamic var updateActivities: Bool = false
    @objc dynamic var updateConsent: Bool = false
    @objc dynamic var updateInfo: Bool = false
    @objc dynamic var enrolling: Bool = false
    @objc dynamic var platform: String?
    @objc dynamic var rejoin: Bool = false
    
    @objc dynamic var signedConsentVersion: String?
    @objc dynamic var signedConsentFilePath: String?
    
    //study state info
    @objc dynamic var participatedStatus: Int = 0
    @objc dynamic var participatedId: String?
    @objc dynamic var joiningDate: Date?
    @objc dynamic var completion: Int = 0
    @objc dynamic var adherence: Int = 0
    
    //anchor date values
    @objc dynamic var anchorDate: Date?
    @objc dynamic var anchorDateType: String?
    @objc dynamic var anchorDateActivityId: String?
    @objc dynamic var anchorDateActivityVersion: String?
    @objc dynamic var anchorDateQuestionKey: String?
    @objc dynamic var activitiesLocalNotificationUpdated = false
    
    
    //withdrawalConfigration
    
    @objc dynamic var withdrawalConfigrationMessage: String?
    @objc dynamic var withdrawalConfigrationType: String?
    
    var sections = List<DBOverviewSection>()
    
    override static func primaryKey() -> String? {
        return "studyId"
    }
    
}

class DBOverviewSection: Object {
    
    @objc dynamic  var title: String?
    @objc dynamic  var type: String?
    @objc dynamic  var imageURL: String?
    @objc dynamic  var text: String?
    @objc dynamic  var link: String?
    @objc dynamic var  studyId: String!
    @objc dynamic var  sectionId: String!
    
    override static func primaryKey() -> String? {
        return "sectionId"
    }
}

class DBStatistics : Object {
    
    @objc dynamic var  studyId: String!
    @objc dynamic var  statisticsId: String!
    @objc dynamic var title: String?
    @objc dynamic var displayName: String?
    @objc dynamic var unit: String?
    @objc dynamic var calculation: String?
    @objc dynamic var statType: String?
    @objc dynamic var activityId: String?
    @objc dynamic var activityVersion: String?
    @objc dynamic var dataSourceType: String?
    @objc dynamic var dataSourceKey: String?
    var statisticsData = List<DBStatisticsData>()
    
    override static func primaryKey() -> String? {
        return "statisticsId"
    }
    
}
class DBCharts :Object {
    
    //basic
    @objc dynamic  var chartId: String?
    @objc dynamic var studyId: String?
    @objc dynamic var title: String?
    @objc dynamic var displayName: String?
    @objc dynamic var chartType: String?
    @objc dynamic var scrollable: Bool = true
    
    //datasource
    @objc dynamic var activityId: String?
    @objc dynamic var activityVersion: String?
    @objc dynamic var dataSourceType: String?
    @objc dynamic var dataSourceKey: String?
    @objc dynamic var dataSourceTimeRange: String?
    @objc dynamic var startTime: Date?
    @objc dynamic var endTime: Date?
    
    
    //settings
    @objc dynamic var barColor: String?
    @objc dynamic var numberOfPoints: Int = 0
    @objc dynamic var chartSubType: String?
    
    var statisticsData = List<DBStatisticsData>()
    
    override static func primaryKey() -> String? {
        return "chartId"
    }

}
class DBStatisticsData: Object {
    @objc dynamic var startDate: Date?
    @objc dynamic var data: Float = 0.0
    @objc dynamic var fkDuration: Int = 0
}

class DBResources:Object {
    
    @objc dynamic  var studyId: String?
    @objc dynamic  var level: String?
    @objc dynamic  var key: String?
    @objc dynamic  var type: String?
    @objc dynamic  var audience: String?
    @objc dynamic  var resourceId: String?
    @objc dynamic  var notificationMessage: String?
    @objc dynamic  var startDate: Date?
    @objc dynamic  var endDate: Date?
    @objc dynamic  var anchorDateStartDays: Int = 0
    @objc dynamic  var anchorDateEndDays: Int = 0
    @objc dynamic  var title: String?
    @objc dynamic  var serverUrl: String?
    @objc dynamic  var localPath: String?
    @objc dynamic  var povAvailable: Bool = false
    
    @objc dynamic  var availabilityType: String?
    @objc dynamic  var sourceType: String?
    @objc dynamic  var sourceActivityId: String?
    @objc dynamic  var sourceKey: String?
    @objc dynamic  var sourceFormKey: String?
    
    @objc dynamic  var startTime: String?
    @objc dynamic  var endTime: String?
    
    override static func primaryKey() -> String? {
        return "resourceId"
    }
}


class DBDataOfflineSync:Object{
    
    @objc dynamic var requestParams: Data?
    @objc dynamic var headerParams: Data?
    @objc dynamic var method: String?
    @objc dynamic var server: String?
    @objc dynamic var date: Date?
}
