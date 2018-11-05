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
enum ResponseMethods: String {
    //TODO : Write exact name for request method
    case enroll
    case validateEnrollmentToken
    case processResponse
    case withdrawFromStudy
    case getParticipantResponse
    case executeSQL
    
    
    var description: String{
        switch self {
            
        default:
            return self.rawValue+".api"
        }
    }
    
    var method: Method {
        
        switch self {
        case .getParticipantResponse,.validateEnrollmentToken,.executeSQL:
            return Method(methodName: (self.rawValue+".api"), methodType: .httpMethodGet, requestType: .requestTypeHTTP)
        case .withdrawFromStudy:
            return Method(methodName: (self.rawValue+".api"), methodType: .httpMethodPOST, requestType: .requestTypeHTTP)
        default:
            return Method(methodName: (self.rawValue+".api"), methodType: .httpMethodPOST, requestType: .requestTypeJSON)
        }
    }
    
}
struct ResponseServerURLConstants {
    //TODO: Set the server end points
    
    //static let ProductionURL = "https://hphci-fdama-te-ds-01.labkey.com/mobileappstudy-"
    //static let DevelopmentURL = "https://hphci-fdama-te-ds-01.labkey.com/mobileappstudy-"
    
    //Labkey UAT Server
    static let ProductionURL = "https://hphci-fdama-st-ds-01.labkey.com/mobileappstudy-"
    static let DevelopmentURL = "https://hphci-fdama-st-ds-01.labkey.com/mobileappstudy-"
    
    //Labkey Production Server
    //static let ProductionURL = "https://hphci-fdama-pr-ds-01.labkey.com/mobileappstudy-"
    //static let DevelopmentURL = "https://hphci-fdama-pr-ds-01.labkey.com/mobileappstudy-"
    
}
class ResponseServerConfiguration: NetworkConfiguration {
    static let configuration = ResponseServerConfiguration()
    
    
    // MARK:  Delegates
    override func getProductionURL() -> String {
        return ResponseServerURLConstants.ProductionURL
    }
    override func getDevelopmentURL() -> String {
        return ResponseServerURLConstants.DevelopmentURL
    }
    
    override func getDefaultHeaders() -> [String: String] {
        return Dictionary()
    }
    override func getDefaultRequestParameters() -> [String: Any] {
        return Dictionary()
    }
    override func shouldParseErrorMessage() -> Bool {
        return true
    }
    override func parseError(errorResponse: Dictionary<String,Any>)->NSError {
        
        var error = NSError(domain: NSURLErrorDomain, code: 101,userInfo: [NSLocalizedDescriptionKey:"Could not connect to server"])
        
        if let errorMessage =  errorResponse["exception"] {
            
            error = NSError(domain: NSURLErrorDomain, code: 101,userInfo:[NSLocalizedDescriptionKey:errorMessage])
        }
        
        return  error
    }
}
