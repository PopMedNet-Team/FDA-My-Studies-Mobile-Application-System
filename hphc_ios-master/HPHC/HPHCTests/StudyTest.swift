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

class StudyTest: XCTestCase {
    
    
    func testStudyList(){
        
        let response: [String: Any] = [
            "message": "SUCCESS",
            "studies": [
                [
                    "studyId": "PreganancyStudy",
                    "studyVersion": "1.5",
                    "title": "A Study for Women's Fitness.",
                    "category": "Drug Safety",
                    "sponsorName": " FDA",
                    "tagline": "Understanding factors that influence women's fitness",
                    "status": "Closed",
                    "logo": "http://172.246.126.44:8080/fdaResources/studylogo/STUDY_AP_09142018053627.jpeg?v=1550642300197",
                    "settings": [
                        "enrolling": true,
                        "platform": "ios",
                        "rejoin": true
                    ]
                ],
                [
                    "studyId": "Study001",
                    "studyVersion": "2.3",
                    "title": "A Study on the Human Eye",
                    "category": "Public Health",
                    "sponsorName": " FDA",
                    "tagline": "Gathering insights on eye-friendly lifestyles",
                    "status": "Active",
                    "logo": "http://172.246.126.44:8080/fdaResources/studylogo/STUDY_SS_05212018053722.jpg?v=1550642300203",
                    "settings": [
                        "enrolling": true,
                        "platform": "ios",
                        "rejoin": true
                    ]
                ],
                
            ]
        ]
        
        
        
    }
    
    
}
