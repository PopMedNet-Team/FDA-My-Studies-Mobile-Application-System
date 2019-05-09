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

class StandaloneStudy: NSObject {
    
    
    
    func createStudyForStandalone() {
        
        WCPServices().getStudyBasicInfo(self)//.getStudyList(self)
        
//        let studyDetail = ["category":"Drug Safety",
//                           "logo":"https://hphci-fdama-st-wcp-01.labkey.com/fdaResources/studylogo/STUDY_HT_06072017020809.jpeg?v=1513764405945",
//                           "settings" :             [
//                            "enrolling" : true,
//                            "platform" : "all",
//                            "rejoin" : false
//            ],
//                           "sponsorName" : " FDA",
//                           "status" : "Active",
//                           "studyId" : "TESTSTUDY01",
//                           "studyVersion" : "48.2",
//                           "tagline" : "A study on exposures exposures during Human Eye.",
//                           "title" : "Human Eye"] as [String : Any]
//
//        let study = Study.init(studyDetail: studyDetail)
//        Study.updateCurrentStudy(study: study)
//
//        let studylist:Array<Study> = [study]
//        Gateway.instance.studies = studylist
//
//        Logger.sharedInstance.info("Studies Saving in DB")
//        //save in database
//        DBHandler().saveStudies(studies: studylist)
//
//        self.fetchStudyDashboardInfo()
    }
    
    func setupStandaloneStudy() {
        if User.currentUser.authToken != nil && User.currentUser.authToken.count > 0{
            self.getStudyStates()
        }
        else {
            self.createStudyForStandalone()
        }
       
    }
    
    func getStudyStates() {
        UserServices().getStudyStates(self)
    }
    
    func getStudyDashboardInfo() {
        WCPServices().getStudyInformation(studyId: (Study.currentStudy?.studyId)!, delegate: self)
    }
    
    func fetchStudyDashboardInfo() {
        
    
        DBHandler.loadStudyOverview(studyId: (Study.currentStudy?.studyId)!) { (overview) in
            if overview != nil {
                Study.currentStudy?.overview = overview
                //NotificationCenter.default.post(name: NSNotification.Name(rawValue: "StudySetupCompleted"), object: nil)
                self.getStudyUpdates()
            }
            else {
                self.getStudyDashboardInfo()
            }
        }
    }
    func getStudyUpdates(){
        
        let study = Study.currentStudy
        DBHandler.loadStudyDetailsToUpdate(studyId: (study?.studyId)!, completionHandler: { (success) in
          NotificationCenter.default.post(name: NSNotification.Name(rawValue: "StudySetupCompleted"), object: nil)
            //self.pushToStudyDashboard()
            //self.removeProgressIndicator()
            //self.checkDatabaseForStudyInfo(study: study!)
        })
    }
    func handleStudyListResponse(){
        
        Logger.sharedInstance.info("Study Response Handler")
        
        if (Gateway.instance.studies?.count)! > 0{
            DBHandler.loadStudyListFromDatabase { (studies) in
                if studies.count > 0 {
                    
                    let standaloneStudyId = Utilities.standaloneStudyId()
                    let study = studies.filter({$0.studyId == standaloneStudyId})
                    Gateway.instance.studies = studies
                    Study.updateCurrentStudy(study:study.last!)
                    self.getStudyDashboardInfo()
                }
                else {
                    // no study found send controler back from here
                }
            }
          
        }
       
    }
}

//MARK:- Webservices Delegates
extension StandaloneStudy:NMWebServiceDelegate {
    
    func startedRequest(_ manager: NetworkManager, requestName: NSString) {
        Logger.sharedInstance.info("requestname START : \(requestName)")
        
        //let appdelegate = UIApplication.shared.delegate as! AppDelegate
        //appdelegate.window?.addProgressIndicatorOnWindowFromTop()
        
    }
    
    func finishedRequest(_ manager: NetworkManager, requestName: NSString, response: AnyObject?) {
        Logger.sharedInstance.info("requestname FINISH: \(requestName) : \(String(describing:response))")
        
        
        //let appdelegate = UIApplication.shared.delegate as! AppDelegate
        
        
        if(requestName as String == WCPMethods.study.rawValue){
            self.handleStudyListResponse()
        }
        
        if(requestName as String == WCPMethods.studyInfo.rawValue){
        
            self.fetchStudyDashboardInfo()
        }
        else if (requestName as String == RegistrationMethods.studyState.description){
        
            self.createStudyForStandalone()
        }
        
        
        
    }
    
    func failedRequest(_ manager: NetworkManager, requestName: NSString, error: NSError) {
        Logger.sharedInstance.info("requestname Failed: \(requestName)")
        
        //--
        //self.removeProgressIndicator()
        //--
        let appdelegate = UIApplication.shared.delegate as! AppDelegate
        
        appdelegate.window?.removeProgressIndicatorFromWindow()
        
        if error.code == 403 { //unauthorized
           self.createStudyForStandalone()
        }
        else {
            
            UIUtilities.showAlertWithMessage(alertMessage: error.localizedDescription)
  
        }
    }
}
