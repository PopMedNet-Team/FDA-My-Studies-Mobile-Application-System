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

import XCTest
@testable import HPHC

class ActivityTest: XCTestCase {

    let activity = Activity()
    override func setUp() {
        // Put setup code here. This method is called before the invocation of each test method in the class.
    }

    override func tearDown() {
        // Put teardown code here. This method is called after the invocation of each test method in the class.
    }

    func testExample() {
        // This is an example of a functional test case.
        // Use XCTAssert and related functions to verify your tests produce the correct results.
    }
    
//    func testStartDateEndDateForEmpty() {
//        let schedule:[String:Any] = [
//            "startTime": "",
//            "endTime": "",
//            "anchorDate": [
//                "sourceType": "EnrollmentDate",
//                "sourceActivityId": "anct",
//                "sourceKey": "anc",
//                "start": [
//                    "days": 10,
//                    "time": "13:00:00"
//                ],
//                "end": [
//                    "days": -10,
//                    "time": "13:00:00"
//                ]
//            ]
//        ]
//
//        activity.setActivityAvailability(schedule)
//
//        XCTAssertNil(activity.startDate)
//        XCTAssertNil(activity.endDate)
//    }
//
//    func testStartDateEndDateForNonEmpty() {
//        let schedule:[String:Any] = [
//            "startTime": "2019-03-12T00:00:00.000-0700",
//            "endTime": "2019-03-12T00:00:00.000-0700",
//            "anchorDate": [
//                "sourceType": "EnrollmentDate",
//                "sourceActivityId": "anct",
//                "sourceKey": "anc",
//                "start": [
//                    "days": 10,
//                    "time": "13:00:00"
//                ],
//                "end": [
//                    "days": -10,
//                    "time": "13:00:00"
//                ]
//            ]
//        ]
//
//        activity.setActivityAvailability(schedule)
//
//        XCTAssertNotNil(activity.startDate)
//        XCTAssertNotNil(activity.endDate)
//    }
//
//    func testAnchorDateNotAvailable() {
//
//        let schedule:[String:Any] = [
//            "startTime": "2019-03-12T00:00:00.000-0700",
//            "endTime": "2019-03-12T00:00:00.000-0700",
//        ]
//
//        activity.setActivityAvailability(schedule)
//        XCTAssertNil(activity.anchorDate)
//    }
//
//    func testAnchorDateAvailable() {
//        let schedule:[String:Any] = [
//            "startTime": "",
//            "endTime": "",
//            "anchorDate": [
//                "sourceType": "EnrollmentDate",
//                "sourceActivityId": "anct",
//                "sourceKey": "anc",
//                "start": [
//                    "days": 10,
//                    "time": "13:00:00"
//                ],
//                "end": [
//                    "days": -10,
//                    "time": "13:00:00"
//                ]
//            ]
//        ]
//
//        activity.setActivityAvailability(schedule)
//        XCTAssertNotNil(activity.anchorDate)
//    }
//    func testAnchorDateStartDetailsAvailable() {
//        let schedule:[String:Any] = [
//            "startTime": "",
//            "endTime": "",
//            "anchorDate": [
//                "sourceType": "EnrollmentDate",
//                "sourceActivityId": "anct",
//                "sourceKey": "anc",
//                "start": [
//                    "days": 10,
//                    "time": "13:00:00"
//                ],
//                "end": [
//                    "days": -10,
//                    "time": "13:00:00"
//                ]
//            ]
//        ]
//
//        activity.setActivityAvailability(schedule["anchorDate"] as! [String : Any])
//        XCTAssertNotNil(activity.anchorDate?.startTime)
//        XCTAssertEqual(10, activity.anchorDate?.startDays)
//    }
    func testAnchorDateStartDetailsNotAvailable() {
        let schedule:[String:Any] = [
            "startTime": "",
            "endTime": "",
            "anchorDate": [
                "sourceType": "EnrollmentDate",
                "sourceActivityId": "anct",
                "sourceKey": "anc",
                "end": [
                    "days": -10,
                    "time": "13:00:00"
                ]
            ]
        ]
        
        activity.setActivityAvailability(schedule["anchorDate"] as! [String : Any])
        XCTAssertNil(activity.anchorDate?.startTime)
        XCTAssertEqual(0, activity.anchorDate?.startDays)
    }
    func testAnchorDateEndDetailsAvailable() {
        let schedule:[String:Any] = [
            "startTime": "",
            "endTime": "",
            "anchorDate": [
                "sourceType": "EnrollmentDate",
                "sourceActivityId": "anct",
                "sourceKey": "anc",
                "start": [
                    "days": 10,
                    "time": "13:00:00"
                ],
                "end": [
                    "days": -10,
                    "time": "13:00:00"
                ]
            ]
        ]
        
        activity.setActivityAvailability(schedule["anchorDate"] as! [String : Any])
        XCTAssertNotNil(activity.anchorDate?.endTime)
        XCTAssertEqual(-10, activity.anchorDate?.endDays)
    }
    func testAnchorDateEndDetailsNotAvailable() {
        let schedule:[String:Any] = [
            "startTime": "",
            "endTime": "",
            "anchorDate": [
                "sourceType": "EnrollmentDate",
                "sourceActivityId": "anct",
                "sourceKey": "anc",
                "start": [
                    "days": 10,
                    "time": "13:00:00"
                ]
            ]
        ]
        
        activity.setActivityAvailability(schedule["anchorDate"] as! [String : Any])
        XCTAssertNil(activity.anchorDate?.endTime)
        XCTAssertEqual(0, activity.anchorDate?.endDays)
    }
    
    func testActivityLifeTimeForOneTimeFrequency() {
        
        let schedule:[String:Any] = [
            "startTime": "",
            "endTime": "",
            "anchorDate": [
                "sourceType": "EnrollmentDate",
                "sourceActivityId": "anct",
                "sourceKey": "anc",
                "start": [
                    "days": 10,
                    "time": "13:00:00"
                ],
                "end": [
                    "days": -10,
                    "time": "13:00:00"
                ]
            ]
        ]
        
        activity.setActivityAvailability(schedule["anchorDate"] as! [String : Any])
        let date = Date()
        activity.anchorDate?.anchorDateValue = date
        let frequency:Frequency = Frequency.One_Time
        
        let expectedStartDate = date.addingTimeInterval(TimeInterval(60*60*24*10))
        let expectedEndDate = date.addingTimeInterval(TimeInterval(60*60*24*(-10)))
        
        let lifeTime = activity.updateLifeTime(activity.anchorDate!, frequency: frequency)
        
        XCTAssertEqual(expectedStartDate.timeIntervalSinceReferenceDate, lifeTime.0!.timeIntervalSinceReferenceDate)
        XCTAssertEqual(expectedEndDate.timeIntervalSinceReferenceDate, lifeTime.1!.timeIntervalSinceReferenceDate)
    }
    
    func testActivityLifeTimeForDailyFrequency() {
        
        let schedule:[String:Any] = [
            "startTime": "",
            "endTime": "",
            "anchorDate": [
                "sourceType": "EnrollmentDate",
                "sourceActivityId": "anct",
                "sourceKey": "anc",
                "start": [
                    "anchorDays": 10,
                    "time": "13:00:00"
                ],
                "end": [
                    "anchorDays": 0,
                    "repeatInterval":5,
                    "time": "13:00:00"
                ]
            ]
        ]
        
        activity.setActivityAvailability(schedule["anchorDate"] as! [String : Any])
        let date = Date()
        activity.anchorDate?.anchorDateValue = date
        let frequency:Frequency = Frequency.Daily
        
        let expectedStartDate = date.addingTimeInterval(TimeInterval(60*60*24*10))
        let expectedEndDate = expectedStartDate.addingTimeInterval(TimeInterval(60*60*24*(5)))
        
        let lifeTime = activity.updateLifeTime(activity.anchorDate!, frequency: frequency)
        XCTAssertNotNil(lifeTime.0)
        XCTAssertNotNil(lifeTime.1)
        XCTAssertEqual(expectedStartDate.timeIntervalSinceReferenceDate, lifeTime.0?.timeIntervalSinceReferenceDate)
        XCTAssertEqual(expectedEndDate.timeIntervalSinceReferenceDate, lifeTime.1?.timeIntervalSinceReferenceDate)
    }
    
    func testActivityLifeTimeForWeeklyFrequency() {
        
        let schedule:[String:Any] = [
            "startTime": "",
            "endTime": "",
            "anchorDate": [
                "sourceType": "EnrollmentDate",
                "sourceActivityId": "anct",
                "sourceKey": "anc",
                "start": [
                    "anchorDays": 10,
                    "time": "13:00:00"
                ],
                "end": [
                    "anchorDays": 0,
                    "repeatInterval":7,
                    "time": "13:00:00"
                ]
            ]
        ]
        
        activity.setActivityAvailability(schedule["anchorDate"] as! [String : Any])
        let date = Date()
        activity.anchorDate?.anchorDateValue = date
        let frequency:Frequency = Frequency.Weekly
        
        let expectedStartDate = date.addingTimeInterval(TimeInterval(60*60*24*10))
        var expectedEndDate =  expectedStartDate.addingTimeInterval(TimeInterval((7*604800) - 1))
        
        
        let lifeTime = activity.updateLifeTime(activity.anchorDate!, frequency: frequency)
        XCTAssertNotNil(lifeTime.0)
        XCTAssertNotNil(lifeTime.1)
        XCTAssertEqual(expectedStartDate.timeIntervalSinceReferenceDate, lifeTime.0?.timeIntervalSinceReferenceDate)
        XCTAssertEqual(expectedEndDate.timeIntervalSinceReferenceDate, lifeTime.1?.timeIntervalSinceReferenceDate)
    }
    
    func testActivityLifeTimeForWeeklyFrequencyAsValue() {
       let dateValue = "2019-05-25 12:00:00"
        
        let schedule:[String:Any] = [
            "startTime": "",
            "endTime": "",
            "anchorDate": [
                "sourceType": "EnrollmentDate",
                "sourceActivityId": "anct",
                "sourceKey": "anc",
                "start": [
                    "anchorDays": -10,
                    "time": "13:00:00"
                ],
                "end": [
                    "anchorDays": 0,
                    "repeatInterval":2,
                    "time": "13:00:00"
                ]
            ]
        ]
        
        activity.setActivityAvailability(schedule["anchorDate"] as! [String : Any])
        
        let date = Utilities.findDateFromString(dateString: dateValue)
        activity.anchorDate?.anchorDateValue = date
        let frequency:Frequency = Frequency.Weekly
        
        let expectedStartDate = Utilities.findDateFromString(dateString: "2019-05-15 12:00:00")
        let expectedEndDate = Utilities.findDateFromString(dateString: "2019-05-29 11:59:59")

        let lifeTime = activity.updateLifeTime(activity.anchorDate!, frequency: frequency)
        XCTAssertNotNil(lifeTime.0)
        XCTAssertNotNil(lifeTime.1)
        XCTAssertEqual(expectedStartDate?.timeIntervalSinceReferenceDate, lifeTime.0?.timeIntervalSinceReferenceDate)
        XCTAssertEqual(expectedEndDate?.timeIntervalSinceReferenceDate, lifeTime.1?.timeIntervalSinceReferenceDate)
        
    }
    
    func testActivityLifeTimeForMonthlyFrequencyAsValue() {
        let dateValue = "2019-01-31 12:00:00"
        
        let schedule:[String:Any] = [
            "startTime": "",
            "endTime": "",
            "anchorDate": [
                "sourceType": "EnrollmentDate",
                "sourceActivityId": "anct",
                "sourceKey": "anc",
                "start": [
                    "anchorDays": 0,
                    "time": "13:00:00"
                ],
                "end": [
                    "anchorDays": 0,
                    "repeatInterval":1,
                    "time": "13:00:00"
                ]
            ]
        ]
        
        activity.setActivityAvailability(schedule["anchorDate"] as! [String : Any])
        
        let date = Utilities.findDateFromString(dateString: dateValue)
        activity.anchorDate?.anchorDateValue = date
        let frequency:Frequency = Frequency.Monthly
        
        let expectedStartDate = Utilities.findDateFromString(dateString: "2019-01-31 12:00:00")
        let expectedEndDate = Utilities.findDateFromString(dateString: "2019-02-28 11:59:59")
        
        let lifeTime = activity.updateLifeTime(activity.anchorDate!, frequency: frequency)
        XCTAssertNotNil(lifeTime.0)
        XCTAssertNotNil(lifeTime.1)
        XCTAssertEqual(expectedStartDate?.timeIntervalSinceReferenceDate, lifeTime.0?.timeIntervalSinceReferenceDate)
        XCTAssertEqual(expectedEndDate?.timeIntervalSinceReferenceDate, lifeTime.1?.timeIntervalSinceReferenceDate)
        
    }
    
    func testActivityLifeTimeForScheduledFrequencyAsValue() {
        let dateValue = "2019-01-31 12:00:00"
        
        let schedule:[String:Any] = [
            "startTime": "",
            "endTime": "",
            "anchorDate": [
                "sourceType": "EnrollmentDate",
                "sourceActivityId": "anct",
                "sourceKey": "anc",
                "start": [
                    "anchorDays": -10,
                    "time": "13:00:00"
                ],
                "end": [
                    "anchorDays": 10,
                    "repeatInterval":1,
                    "time": "13:00:00"
                ]
            ]
        ]
        
        activity.setActivityAvailability(schedule["anchorDate"] as! [String : Any])
        
        let date = Utilities.findDateFromString(dateString: dateValue)
        activity.anchorDate?.anchorDateValue = date
        let frequency:Frequency = Frequency.Scheduled
        
        let expectedStartDate = Utilities.findDateFromString(dateString: "2019-01-16 12:00:00")
        let expectedEndDate = Utilities.findDateFromString(dateString: "2019-02-10 11:59:59")
        
        let lifeTime = activity.updateLifeTime(activity.anchorDate!, frequency: frequency)
        XCTAssertNotNil(lifeTime.0)
        XCTAssertNotNil(lifeTime.1)
        XCTAssertEqual(expectedStartDate?.timeIntervalSinceReferenceDate, lifeTime.0?.timeIntervalSinceReferenceDate)
        XCTAssertEqual(expectedEndDate?.timeIntervalSinceReferenceDate, lifeTime.1?.timeIntervalSinceReferenceDate)
        
    }

    func testPerformanceExample() {
        // This is an example of a performance test case.
        self.measure {
            // Put the code you want to measure the time of here.
        }
    }

}
