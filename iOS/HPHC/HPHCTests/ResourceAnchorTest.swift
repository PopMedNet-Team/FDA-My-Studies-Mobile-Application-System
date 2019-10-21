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
import Mockingjay


class ResourceAnchorTest: XCTestCase {

    var delegate:WCPDelegate!
    override func setUp() {
        // Put setup code here. This method is called before the invocation of each test method in the class.
        delegate = WCPDelegate()
    }

    override func tearDown() {
        // Put teardown code here. This method is called after the invocation of each test method in the class.
    }

    func testExample() {
        // This is an example of a functional test case.
        // Use XCTAssert and related functions to verify your tests produce the correct results.
    }
    func testFetchingResourceList() {
//        let rlist =  DBHandler.resourceListFor(nil, questionKey: nil)
//        XCTAssertEqual(rlist.count, 1)
    }
    
    func testResourcesLifeTimeWhenAnchorDateIsAvailable() {
//        DBHandler.updateResourceLifeTime(nil, questionKey: nil, anchorDateValue: Date())
//        let rlist =  DBHandler.resourceListFor(nil, questionKey: nil)
//        XCTAssertEqual(rlist.count, 0)
    }
    
    func testResourceIsEmpty() {
       // let empty = DBHandler.isResourcesEmpty()
        //XCTAssert(empty)
    }
    
    func testResourcesLifeTimeWhenAnchorDateIsNotAvailable() {
        
    }
    
    func testWCP_GetStudies_Failure() {
        
        //let delegate = WCPDelegate()
        
        let expection = expectation(description: "StudyList api call")
        delegate.asyncExpectation = expection
        
        _ = [ "user": "Kyle" ]
        let url = "https://hpwcp-stage.lkcompliant.net/StudyMetaData/studyList"
        
        let error = NSError.init(domain: "mocking", code: 500, userInfo: nil)
        stub(uri(url), failure(error))
        
        let services = WCPServices()
        services.delegateSource = delegate
        services.getStudyList(delegate)
        
        waitForExpectations(timeout: 3.0) { (error) in
           
            if let error = error {
                XCTFail("waitForExpectationsWithTimeout errored: \(error)")
            }
            
            guard let result = self.delegate.delegateAsyncResult else {
                XCTFail("Expected delegate to be called")
                return
            }
            let data = self.delegate.apiResponse
            let error = self.delegate.apiError
            
            XCTAssertEqual(error?.code, 500)
            XCTAssertNotEqual(WCPDelegate.State.none, result)
            //XCTAssertNotEqual(WCPDelegate.State.start, result)
            XCTAssertEqual(WCPDelegate.State.failed, result)
            XCTAssertNil(data)
           
        }
    }
    
    func testWCP_GetStudies_Sucess() {
        
        let expection = expectation(description: "StudyList api call")
        delegate.asyncExpectation = expection
        
        let path = Bundle(for: type(of: self)).url(forResource: "StudyList", withExtension: "json")!
        let data = try! Data(contentsOf: path)
        
        let url = "https://hpwcp-stage.lkcompliant.net/StudyMetaData/studyList"
        stub(http(.get,uri: url),jsonData(data))
        
        let services = WCPServices()
        services.delegateSource = delegate
        services.getStudyList(delegate)
        
        waitForExpectations(timeout: 2.0) { (error) in
            
            if let error = error {
                XCTFail("waitForExpectationsWithTimeout errored: \(error)")
            }
            
            guard let result = self.delegate.delegateAsyncResult else {
                XCTFail("Expected delegate to be called")
                return
            }
            let data = self.delegate.apiResponse
            let error = self.delegate.apiError
            
            
            XCTAssertNotEqual(WCPDelegate.State.none, result)
            //XCTAssertNotEqual(WCPDelegate.State.start, result)
            XCTAssertEqual(WCPDelegate.State.finished, result)
            XCTAssertNotNil(data)
            XCTAssertEqual(Gateway.instance.studies?.count, 2)
            
        }
    }
    

    func testPerformanceExample() {
        // This is an example of a performance test case.
        self.measure {
            // Put the code you want to measure the time of here.
        }
    }
    
    let studyResponse = Data("{\n    \"message\": \"SUCCESS\",\n    \"studies\": [\n        {\n            \"studyId\": \"Study02\",\n            \"studyVersion\": \"1.3\",\n            \"title\": \"Food Nutrition\",\n            \"category\": \"Food Safety\",\n            \"sponsorName\": \" FDA\",\n            \"tagline\": \"Path to improved health\",\n            \"status\": \"Closed\",\n            \"logo\": \"https:\\hpwcp-stage.lkcompliant.net/fdaResources/studylogo/STUDY_FS_05282018040302.jpeg?v=1564127523483\",\n            \"settings\": {\n                \"enrolling\": true,\n                \"platform\": \"all\",\n                \"rejoin\": true\n            }\n        }\n   ]\n}".utf8)
        
    

}

class WCPDelegate:NMWebServiceDelegate {
    
    enum State:String {
        case none
        case start
        case finished
        case failed
    }
    
    var delegateAsyncResult: State? = .none
    var apiResponse:[String:Any]?
    var apiError:NSError?
    var asyncExpectation: XCTestExpectation?
    
    func failedRequest(_ manager: NetworkManager, requestName: NSString, error: NSError) {
        guard let expectation = asyncExpectation else {
            XCTFail("Delegate was not setup correctly. Missing XCTExpectation reference")
            return
        }
        
        apiError = error
        delegateAsyncResult = .failed
        expectation.fulfill()
    }
    func startedRequest(_ manager: NetworkManager, requestName: NSString) {
        
        guard asyncExpectation != nil else {
            XCTFail("Delegate was not setup correctly. Missing XCTExpectation reference")
            return
        }
        
        delegateAsyncResult = .start
        //expectation.fulfill()
        
    }
    func finishedRequest(_ manager: NetworkManager, requestName: NSString, response: AnyObject?) {
        
        guard let expectation = asyncExpectation else {
            XCTFail("Delegate was not setup correctly. Missing XCTExpectation reference")
            return
        }
        
        apiResponse = response as? [String:Any]
        delegateAsyncResult = .finished
        expectation.fulfill()
    }
}
