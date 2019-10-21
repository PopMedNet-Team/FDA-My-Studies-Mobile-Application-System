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


enum WCPMethods: String {
    
    //TODO : Write exact name for request method
    case study
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
    case versionInfo
    
    var method: Method{
        switch self {
        case .feedback,.contactUs:
            return Method(methodName: self.rawValue, methodType: .httpMethodPOST, requestType: .requestTypeJSON)
        default:
            return Method(methodName: self.rawValue, methodType: .httpMethodGet, requestType: .requestTypeHTTP)
        }
    }
}


struct Credentials {
    
    static let username: String = "username"
    static let authToken: String = "authToken"
    
    static var token: String {
        return username + ":" + authToken
    }
}


struct WCPServerURLConstants {
    
    //TODO: Set the server end points
    
    //Staging
    static let ProductionURL = "Your production server URL"
    static let DevelopmentURL = "Your development server URL"
    
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
        
        var infoDict: NSDictionary?
        if let path = Bundle.main.path(forResource: "Info", ofType: "plist") {
            infoDict = NSDictionary(contentsOfFile: path)
        }
        let appId = infoDict!["ApplicationID"] as! String
        let orgId = infoDict!["OrganizationID"] as! String
        
        let token = Credentials.token
  
        let base64token = "Basic " + token.toBase64()
        
        let headers = ["Authorization": base64token,
                       "applicationId": appId,
                       "orgId": orgId]
        
        return headers
    }
    
    override func getDefaultRequestParameters() -> [String: Any] {
        return Dictionary()
    }
    
    override func shouldParseErrorMessage() -> Bool {
        return false
    }
    
}

