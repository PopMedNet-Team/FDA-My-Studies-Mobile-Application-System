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


enum WCPMethods: String {
    
    //TODO : Write exact name for request method
    case gatewayInfo
    case studyList
    case eligibilityConsent
    case resources
    case studyInfo
    case activityList
    case activity
    case studyDashboard
    case termsPolicy
    case notifications
    case consentDocument
    case feedback
    case contactUs
    case studyUpdates
    case appUpdates
    
    var method: Method{
        switch self {
        case .feedback,.contactUs:
            return Method(methodName: self.rawValue, methodType: .httpMethodPOST, requestType: .requestTypeJSON)
        default:
            return Method(methodName: self.rawValue, methodType: .httpMethodGet, requestType: .requestTypeHTTP)
            
        }
    }
}


struct WCPServerURLConstants {
    //TODO: Set the server end points
    
    //LabKey
    //static let ProductionURL =  "https://hphci-fdama-te-wcp-01.labkey.com/StudyMetaData/"
    //static let DevelopmentURL = "https://hphci-fdama-te-wcp-01.labkey.com/StudyMetaData/"
    
    //Lab UAT Server
    static let ProductionURL =  "https://hphci-fdama-st-wcp-01.labkey.com/StudyMetaData/"
    static let DevelopmentURL = "https://hphci-fdama-st-wcp-01.labkey.com/StudyMetaData/"
    
    //Lab Production Server
    //static let ProductionURL =  "https://hphci-fdama-pr-wcp-01.labkey.com/StudyMetaData/"
    //static let DevelopmentURL = "https://hphci-fdama-pr-wcp-01.labkey.com/StudyMetaData/"
    
    
    //UAT
    //static let ProductionURL = "http://23.89.199.27:8080/StudyMetaData/"
    //static let DevelopmentURL = "http://23.89.199.27:8080/StudyMetaData/"
    
    
    //New SerVerForDevelopment
    //static let ProductionURL = "http://192.168.0.26:8080/StudyMetaData/"
    //static let DevelopmentURL = "http://192.168.0.26:8080/StudyMetaData/"
    
    
    
    //Production
    //static let ProductionURL = "http://192.168.0.50:8080/StudyMetaData/"
    //static let DevelopmentURL = "http://192.168.0.50:8080/StudyMetaData/"
    
    
    //Dev
    //static let ProductionURL = "http://192.168.0.50:8080/StudyMetaData-DEV/"
    //static let DevelopmentURL = "http://192.168.0.50:8080/StudyMetaData-DEV/"
    
    
    //local
    //static let ProductionURL = "http://192.168.0.32:8080/StudyMetaData/"
    //static let DevelopmentURL = "http://192.168.0.32:8080/StudyMetaData/"
    
    //local 2
    //static let ProductionURL = "http://172.246.126.44:8080/StudyMetaData/"
    //static let DevelopmentURL = "http://172.246.126.44:8080/StudyMetaData/"
}

class WCPConfiguration: NetworkConfiguration {
    
    static let configuration = WCPConfiguration()
    
    // MARK:  Delegates
    override func getProductionURL() -> String {
        return WCPServerURLConstants.ProductionURL
    }
    override func getDevelopmentURL() -> String {
        return WCPServerURLConstants.DevelopmentURL
    }
    
    override func getDefaultHeaders() -> [String : String] {
        
        let token = Utilities.getBundleIdentifier() + ":" + "ee91a4f6-d9c4-4ee9-a0e2-5682c5b1c916"
        let base64token = "Basic " + token.toBase64()
        
        let headers = ["Authorization": base64token]
        return headers
    }
    override func getDefaultRequestParameters() -> [String: Any] {
        return Dictionary()
    }
    override func shouldParseErrorMessage() -> Bool {
        return false
    }
    
}
