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
import RealmSwift

class SyncUpdate{
    
    var isReachabilityChanged: Bool
    static var currentSyncUpdate: SyncUpdate? = nil
    var selectedRun: DBActivityRun?
   
    init() {
        self.isReachabilityChanged = false
    }
    
    @objc func updateData() {
       
        if (NetworkManager.sharedInstance().reachability?.isReachable)! {
            print("*******************update Data***************")
            
            DispatchQueue.main.asyncAfter(deadline: .now() + 10.0) {
                self.syncDataToServer()
            }
        }else {
             print("not available")
        }
    }
    
    /**
     SyncData to server, called to sync responses stored in offline mode to server
    */
    func syncDataToServer() {
        
        let realm = try! Realm()
        let toBeSyncedData = realm.objects(DBDataOfflineSync.self).sorted(byKeyPath: "date", ascending: true).first
        if toBeSyncedData != nil {
            
            //request params
            var params: Dictionary<String, Any>? = nil
            if toBeSyncedData?.requestParams != nil {
                
                do {
                    params = try JSONSerialization.jsonObject(with: (toBeSyncedData?.requestParams)!, options: []) as? Dictionary<String, Any>
                    
                }catch {
                    
                }
            }
            
            //header params
            var headers: Dictionary<String, String>? = nil
            if toBeSyncedData?.headerParams != nil {
                
                do {
                    headers = try JSONSerialization.jsonObject(with: (toBeSyncedData?.headerParams)!, options: []) as? Dictionary<String, String>
                    
                }catch {
                    
                }
            }
            
            
            let methodString = toBeSyncedData?.method!
            let server = toBeSyncedData?.server!
            
            if server == "registration" {
                
                let methodName = methodString?.components(separatedBy: ".").first
                let registrationMethod = RegistrationMethods(rawValue: methodName!)
                let method = registrationMethod?.method
                UserServices().syncOfflineSavedData(method: method!, params: params, headers: headers ,delegate: self)
                
            }else if server == "wcp" {
                //Do Nothing
            }else if server == "response" {
                
                let methodName = methodString?.components(separatedBy: ".").first
                let registrationMethod = ResponseMethods(rawValue: methodName!)
                let method = registrationMethod?.method
                LabKeyServices().syncOfflineSavedData(method: method!, params: params, headers: headers ,delegate: self)
            }
            
            //delete current database object
            try! realm.write {
               realm.delete(toBeSyncedData!)
            }
        }
    }
    
    /**
     updates run information to server
    */
    func updateRunInformationToServer(){
        
        let realm = try! Realm()
        let dbRuns = realm.objects(DBActivityRun.self).filter({$0.toBeSynced == true})
        
        if dbRuns.count > 0 {
            
            //get last run
            let run = dbRuns.last
            self.selectedRun = run
            let activity = realm.object(ofType: DBActivity.self, forPrimaryKey: run?.activityId!)
            
            if activity == nil {
                self.findNextRunToSync()
                
            }else if activity != nil && activity?.shortName == nil {
                self.findNextRunToSync()
                
            }else {
                
                let currentUser = User.currentUser
                if let userStudyStatus = currentUser.participatedStudies.filter({$0.studyId == run?.studyId}).first {
                    
                    let studyId =  run?.studyId
                    let activiyId =  run?.activityId
                    let activityName =  activity?.shortName
                    let activityVersion = activity?.version!
                    let currentRunId = run?.runId
                    
                    let info =  [kStudyId: studyId! ,
                                 kActivityId: activiyId! ,
                                 kActivityName: activityName! ,
                                 "version": activityVersion! ,
                                 kActivityRunId: "\(currentRunId!)"
                        ] as [String: String]
                    
                    let ActivityType = activity?.type
                    
                    let participationid = userStudyStatus.participantId
                    
                    var data: Dictionary<String, Any> = [:]
                    do {
                        let responseData = try JSONSerialization.jsonObject(with: (run?.responseData)!, options: []) as! [String: Any]
                        data = (responseData["data"] as! Dictionary<String, Any>?)!
                    }catch {
                    }
                    //save to server
                    LabKeyServices().processResponse(metaData: info, activityType: ActivityType!, responseData: data, participantId: participationid!, delegate: self)
                }
            }
        }else {
            self.selectedRun = nil
        }
    }
    
    func findNextRunToSync(){
        if selectedRun != nil {
            
            let realm = try! Realm()
            try! realm.write {
                self.selectedRun?.toBeSynced = false
                self.selectedRun?.responseData = nil
            }
            
            self.updateRunInformationToServer()
        }
    }
    
    
}

// MARK:- Webservices Delegates
extension SyncUpdate: NMWebServiceDelegate {
    
    func startedRequest(_ manager: NetworkManager, requestName: NSString) {
        Logger.sharedInstance.info("requestname : \(requestName)")
    }
    
    func finishedRequest(_ manager: NetworkManager, requestName: NSString, response: AnyObject?) {
        Logger.sharedInstance.info("requestname : \(requestName) : \(String(describing:response))")
        self.syncDataToServer()
    }
    
    func failedRequest(_ manager: NetworkManager, requestName: NSString, error: NSError) {
        Logger.sharedInstance.info("requestname : \(requestName)")
        
    }
}
