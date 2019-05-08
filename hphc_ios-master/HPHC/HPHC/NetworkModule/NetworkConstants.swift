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
fileprivate func < <T : Comparable>(lhs: T?, rhs: T?) -> Bool {
  switch (lhs, rhs) {
  case let (l?, r?):
    return l < r
  case (nil, _?):
    return true
  default:
    return false
  }
}

fileprivate func > <T : Comparable>(lhs: T?, rhs: T?) -> Bool {
  switch (lhs, rhs) {
  case let (l?, r?):
    return l > r
  default:
    return rhs < lhs
  }
}



// MARK: WebRequestMethods
//let kSM




struct NetworkConnectionConstants {
    static let ConnectionTimeoutInterval = 30.0
    static let NoOfRequestRetry  = 3
    static let EnableRequestRetry = false
}

struct TrustedHosts {
    static let TrustedHost1 = ""
    static let TrustedHost2 = ""
    static let TrustedHost3 = ""
}

struct HTTPHeaderKeys {
    static let SetCookie = "Set-Cookie"
    static let ContentType = "Content-Type"
}

struct HTTPHeaderValues {
    static  let ContentTypeJson = "application/json"
}

struct NetworkURLConstants {
    //TODO: Set the server end points
    
    static let ProductionURL = ""
    static let DevelopmentURL = ""
    
}

class NetworkConstants: NSObject {
    //TODO: Configure common parameters for requests here.
    class func getCommonRequestParameters()-> NSDictionary? {
        return nil
    }
    
    class func getCommonHeaderParameters() -> NSDictionary? {
        //let cookie = UserDefaults.standard.value(forKey: "cookies")
        let headers : NSDictionary? =  nil
        /*
        if (cookie != nil && (cookie as AnyObject).length > 0){
            headers = ["cookie" : cookie!]
        }
         */
        return headers
    }
    
    fileprivate func getTrustedHosts()-> NSArray{
        let array = [TrustedHosts.TrustedHost1,TrustedHosts.TrustedHost2,TrustedHosts.TrustedHost3]
        return array as NSArray
    }
    
    class func checkResponseHeaders(_ response : URLResponse)-> (NSInteger,String){
        let httpResponse = response as? HTTPURLResponse
       
        let headers = httpResponse!.allHeaderFields as NSDictionary
        let statusCode = httpResponse!.statusCode
        var statusMessage = ""
        
        if let message = headers["StatusMessage"] {
            // now val is not nil and the Optional has been unwrapped, so use it
            statusMessage = message as! String
        }
        
        return (statusCode,statusMessage)
    }
}

