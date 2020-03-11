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

enum RequestType: NSInteger {
    case requestTypeJSON
    case requestTypeHTTP
}

enum HTTPMethod : NSInteger {
    case httpMethodGet
    case httpMethodPUT
    case httpMethodPOST
    case httpMethodDELETE
    
    var methodTypeAsString:String {
        switch self {
        case .httpMethodGet:
            return "GET"
        case .httpMethodPOST:
            return "POST"
        case .httpMethodPUT:
            return "PUT"
        case .httpMethodDELETE:
            return "DELETE"
        }
    }
 
}

struct DefaultHeaders {
    static let DefaultHeaderKey = NSDictionary.init(object: HTTPHeaderValues.ContentTypeJson, forKey: HTTPHeaderKeys.ContentType as NSCopying)
}

class NetworkWebServiceHandler: NSObject, URLSessionDelegate {
    
    var shouldRetryRequest = NetworkConnectionConstants.EnableRequestRetry
    var maxRequestRetryCount = NetworkConnectionConstants.NoOfRequestRetry
    var connectionTimeoutInterval : Double = NetworkConnectionConstants.ConnectionTimeoutInterval
   
    
    var delegate: NMWebServiceDelegate? = nil
    var challengeDelegate: NMAuthChallengeDelegate? = nil
    
    weak var networkManager: NetworkManager? = nil
    var configuration:NetworkConfiguration!
    
    init(delegate :  NMWebServiceDelegate, challengeDelegate:NMAuthChallengeDelegate?){
        self.delegate = delegate
        self.challengeDelegate = challengeDelegate
    }
    
    func getServerURLString() -> NSString {
        
        let ud:Bool = UserDefaults.standard.bool(forKey: kIsStagingUser)
        
        #if DEBUG
            if ud {
                 return self.configuration.getDevelopmentURL() as NSString
                
            }
            return self.configuration.getProductionURL() as NSString
        #else
            if ud {
                return self.configuration.getDevelopmentURL() as NSString
            }
            return self.configuration.getProductionURL() as NSString
        #endif
    }
    
    fileprivate func getBaseURLString(_ requestName : NSString) -> NSString {
        
        let serverPath = self .getServerURLString()
        return NSString.init(format: "%@%@", serverPath,requestName)
    }
    
    fileprivate func getCombinedWithCommonParams(_ params : NSDictionary?) -> NSDictionary? {
        
        let commonParams  = self.configuration.getDefaultRequestParameters() as NSDictionary?
        var mParams : NSMutableDictionary? = nil
        if commonParams != nil{
            mParams = NSMutableDictionary.init(dictionary: commonParams!)
        }
        if  params != nil {
            if mParams != nil {
                mParams!.addEntries(from:params! as! [AnyHashable : Any])
            } else {
                mParams = NSMutableDictionary.init(dictionary: params!)
            }
        }
        return mParams!
    }
    
    fileprivate func getCombinedHeaders(_ userHeaders : NSDictionary?, defaultHeaders : NSDictionary?)-> NSDictionary? {
        
        let commonParams: NSDictionary? = self.configuration.getDefaultHeaders() as NSDictionary?
        
        var mParams : NSMutableDictionary? = nil
        if commonParams != nil{
            mParams = NSMutableDictionary.init(dictionary: commonParams!)
        }
        
        
        if  defaultHeaders != nil {
            if mParams != nil {
                mParams!.addEntries(from: defaultHeaders as! [AnyHashable: Any])
            } else {
                mParams = NSMutableDictionary.init(dictionary: defaultHeaders!)
            }
        }
        if userHeaders != nil && userHeaders!.count > 0  {
            if mParams != nil {
                mParams!.addEntries(from: userHeaders as! [AnyHashable: Any])
            } else {
                mParams = NSMutableDictionary.init(dictionary: userHeaders!)
            }
        }
        return mParams
    }
    
    func getRequestMethod(_ methods : HTTPMethod)-> NSString{
        switch methods {
        case .httpMethodGet:
            return "GET"
        case .httpMethodPOST:
            return "POST"
        case .httpMethodPUT:
            return "PUT"
        case .httpMethodDELETE:
            return "DELETE"
        }
    }
    
    fileprivate func getHttpRequest(_ requestName : NSString , parameters : NSDictionary?)-> NSString {
        
        // get from uptiiq
        var url : String = ""
        if !(parameters == nil || parameters?.count == 0){
            let allKeys = parameters?.allKeys
            for key in allKeys! {
                url = (url as String) + String(format: "%@=%@&",String(describing: key), parameters?[key as! String] as! CVarArg )
            }
            let length = url.count-1
            let index = url.index(url.startIndex, offsetBy: length)
            url =  String(url[..<index])//url.substring(to: index) //url.substring(to: length)
        }
        return url as NSString
        
    }
    
    func composeRequestFor(_ requestName: NSString, requestType : RequestType , method : HTTPMethod , params : NSDictionary?, headers : NSDictionary?){
        
        if ((delegate?.startedRequest) != nil) {
            delegate?.startedRequest(networkManager!, requestName: requestName)
        }
        
        var requestParams:NSDictionary? = nil
        if params != nil{
            requestParams = self.getCombinedWithCommonParams(params)
        }
        switch requestType {
        case .requestTypeHTTP:
            self.generateHTTPRequest(requestName, method: method, params: requestParams, headers: headers!)
            break
        case .requestTypeJSON:
            self.generateJSONRequest(requestName, method: method, params: requestParams, headers: headers)
            break
        }
    }
    
    func composeRequest(_ configuration:NetworkConfiguration, method: Method, params : NSDictionary?, headers : NSDictionary?){
        
        self.configuration = configuration
        
        if ((delegate?.startedRequest) != nil) {
            delegate?.startedRequest(networkManager!, requestName: method.methodName as NSString)
        }
        
        var requestParams:NSDictionary? = nil
        if params != nil {
            requestParams = self.getCombinedWithCommonParams(params)
        }
        switch method.requestType {
        case .requestTypeHTTP:
            self.generateHTTPRequest(method.methodName as NSString, method: method.methodType, params: requestParams, headers: headers)
            break
        case .requestTypeJSON:
            self.generateJSONRequest(method.methodName as NSString, method: method.methodType, params: requestParams, headers: headers)
            break
        }
    }
    
    fileprivate func generateHTTPRequest(_ requestName : NSString ,method : HTTPMethod , params : NSDictionary?, headers : NSDictionary?){
        
        let httpHeaders : NSDictionary? = self.getCombinedHeaders(headers, defaultHeaders: nil)
        let baseURLString  : NSString = self.getBaseURLString(requestName)
        let httpRequestString  : NSString? = self.getHttpRequest(requestName, parameters: params)
        var requestString  : NSString!
        
        if (httpRequestString?.length == 0) {
            requestString = baseURLString
        } else {
            requestString = String(format:"%@?%@",baseURLString,httpRequestString!) as NSString?
        }
        
        if #available(iOS 9, *) {
            requestString = requestString.addingPercentEncoding(withAllowedCharacters: CharacterSet.urlQueryAllowed) as NSString?
        } else {
            requestString = requestString.addingPercentEscapes(using: String.Encoding.utf8.rawValue) as NSString?
        }
        
        let requestUrl = URL(string: requestString as String)!
      print("Request URL:  \(requestUrl)")
         var request = URLRequest.init(url: requestUrl, cachePolicy: URLRequest.CachePolicy.reloadIgnoringLocalCacheData, timeoutInterval: self.connectionTimeoutInterval)
        request.httpMethod = self.getRequestMethod(method) as String
        if httpHeaders != nil && (httpHeaders?.count)! > 0{
            request.allHTTPHeaderFields = httpHeaders as? [String : String]
        }
        self.fireRequest(request, requestName: requestName)
    }
    
    fileprivate func generateJSONRequest(_ requestName : NSString , method : HTTPMethod , params : NSDictionary? , headers : NSDictionary?) {
        var defaultheaders : NSDictionary? = DefaultHeaders.DefaultHeaderKey
        if params == nil || params?.count == 0 {
            defaultheaders = nil
        }
        let httpHeaders : NSDictionary?
      
//      if requestName as String == RegistrationMethods.refreshToken.method.methodName{
//         httpHeaders = defaultheaders
//
//      } else {
         httpHeaders = self.getCombinedHeaders(headers, defaultHeaders: defaultheaders)
      //}
      
        let baseURLString : NSString = self.getBaseURLString(requestName)
        let requestUrl = URL(string: baseURLString as String)
        do{
            
            var request = URLRequest.init(url: requestUrl!, cachePolicy: URLRequest.CachePolicy.reloadIgnoringLocalCacheData, timeoutInterval: self.connectionTimeoutInterval)
          
            if params != nil && (params?.count)! > 0{
                let data = try JSONSerialization.data(withJSONObject: params!, options: JSONSerialization.WritingOptions.prettyPrinted)
                request.httpBody = data
            }
            
            request.httpMethod=self.getRequestMethod(method) as String
            if httpHeaders != nil {
                request.allHTTPHeaderFields = httpHeaders! as? [String : String]
            }
            self.fireRequest(request, requestName: requestName)
            
        }catch {
            print("Serilization error")
        }
    }
    
    fileprivate func fireRequest (_ request : URLRequest? , requestName : NSString?) {
        
        if NetworkManager.isNetworkAvailable() {

            let config = URLSessionConfiguration.default
            let session = Foundation.URLSession.init(configuration: config, delegate: self , delegateQueue: nil)
            
            session.dataTask(with: request!) {(data, response, error) -> Void in
                if let data = data {
                    DispatchQueue.main.async {
                        self.handleResponse(data, response: response, requestName: requestName, error: error as NSError?)
                    }
                } else {
                    DispatchQueue.main.async {
                        self.delegate?.failedRequest(self.networkManager!, requestName: requestName!,error: error! as NSError)
                    }
                }
                }.resume()
            
        } else {
            if ((delegate?.failedRequest) != nil) {
        
                let error1 = NSError(domain: NSURLErrorDomain, code:NoNetworkErrorCode,userInfo:[NSLocalizedDescriptionKey:"You seems to be offline. Please connect to a network to proceed with this action."])
                delegate?.failedRequest(networkManager!, requestName: requestName!,error: error1)
            }
        }
    }
    
     func handleResponse(_ data : Data? , response : URLResponse?, requestName : NSString?,error : NSError?){
       
        if (error != nil) {
            if shouldRetryRequest && maxRequestRetryCount > 0  {
                maxRequestRetryCount -= 1
            } else {
                
                if error?.code == -1001 { //Could not connect to the server.
                }
                if ((delegate?.failedRequest) != nil) {
                    delegate?.failedRequest(networkManager!, requestName: requestName!,error: error!)
                }

            }
        } else {
            let status = NetworkConstants.checkResponseHeaders(response!)
            let statusCode = status.0
            var error1 : NSError?
            if statusCode == 200 || statusCode == 0 {
                var responseDict: NSDictionary? = nil
                
                do{
                    //NSJSONReadingOptions.MutableContainers
                    responseDict = try JSONSerialization.jsonObject(with: data!, options: []) as? NSDictionary

                }catch {
                    print("Serilization error")
                }
                
                if ((delegate?.finishedRequest) != nil) {
                    
                    if (responseDict != nil) {
                        delegate?.finishedRequest(networkManager!, requestName: requestName!,response: responseDict!)
                        
                    } else {
                        
                        error1 = NSError(domain: NSURLErrorDomain, code: 300,userInfo:[NSLocalizedDescriptionKey:"Could not connect to server. Please try again later."])
                        
                        if ((delegate?.failedRequest) != nil) {
                            delegate?.failedRequest(networkManager!, requestName: requestName!,error:error1!)
                        }
                    }
                }
            } else {
                
                if self.configuration.shouldParseErrorMessage() {
                  
                    var responseDict: [String:Any]? = nil
                    do {
                        
                      responseDict = try JSONSerialization.jsonObject(with: data!, options:.allowFragments) as? [String:Any]
                        
                    }catch {
                        print("Serilization error")
                    }
                    error1 = self.configuration.parseError(errorResponse: responseDict!)
                } else {
                    
                    error1 = NSError(domain: NSURLErrorDomain, code:statusCode,userInfo:[NSLocalizedDescriptionKey: status.1])
                }

                if ((delegate?.failedRequest) != nil) {
                    delegate?.failedRequest(networkManager!, requestName: requestName!,error:error1!)
                }
            }
        }
    }
    
    func urlSession(_ session: URLSession, didReceive challenge: URLAuthenticationChallenge, completionHandler: @escaping (URLSession.AuthChallengeDisposition, URLCredential?) -> Void) {
        var credential : URLCredential!
        
        if (challengeDelegate?.networkCredential) != nil {
            credential = challengeDelegate?.networkCredential(networkManager!, challenge: challenge)
        }
        
        if (credential != nil) {
            var challengeDisposition : Foundation.URLSession.AuthChallengeDisposition!
            if (challengeDelegate?.networkChallengeDisposition) != nil {
                challengeDisposition = challengeDelegate?.networkChallengeDisposition(networkManager!, challenge: challenge)
            }
            completionHandler(challengeDisposition,credential)
        }
    }
}
