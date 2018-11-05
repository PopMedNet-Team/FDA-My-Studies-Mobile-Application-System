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

import Foundation

enum ResourceLevel: String{
    
    case gateway = "gateway"
    case study = "study"
}

let kResourceLevel = "level"
let kResourceKey = "key"
let kResourceType = "type"
let kResourceFile = "file"
let kResourceConfigration = "availability"
let kResourceTitle = "title"
let kResourceId = "resourcesId"
let kResourceAudience = "audience"

/**
 Resource model stores the resource of any Study or Gateway. Each resource has a unique id and have file and anchor data
 */

class Resource {
    var level: ResourceLevel?
    var key: String?
    var type: String?
    var notificationMessage: String?
    var file: File?
    var audience: Audience?
    var resourcesId: String?
    var configration: Dictionary<String, Any>?
    var startDate: Date?
    var endDate: Date?
    var anchorDateStartDays: Int?
    var anchorDateEndDays: Int?
    var title: String?
    var povAvailable: Bool = false
    
    /**
     Default Initializer
     */
    init() {
        self.level = ResourceLevel.gateway
        self.key = ""
        self.type  = ""
        self.file = File()
        self.configration = Dictionary()
        self.title = ""
    }
    /**
     initializer method with ditionary of properties
     */
    init(detail: Dictionary<String, Any>) {
        
        if Utilities.isValidObject(someObject: detail as AnyObject?){
            
            if (Utilities.isValidValue(someObject: (detail[kResourceId]) as AnyObject)) {
                self.resourcesId = detail[kResourceId] as? String
            }
            if (Utilities.isValidValue(someObject: (detail[kResourceAudience]) as AnyObject)) {
                self.audience = Audience(rawValue:detail[kResourceAudience] as! String)
            }
            if (Utilities.isValidValue(someObject: (detail[kResourceLevel]) as AnyObject)) {
                self.level = detail[kResourceLevel] as? ResourceLevel
            }
            if (Utilities.isValidValue(someObject: (detail[kResourceKey]) as AnyObject)){
                self.key = detail[kResourceKey] as? String
            }
            if (Utilities.isValidValue(someObject: (detail[kResourceType]) as AnyObject)) {
                self.type = detail[kResourceType] as? String
            }
            if (Utilities.isValidValue(someObject: (detail[kResourceFile]) as AnyObject)) {
                self.file?.setFile(dict: detail[kResourceFile] as! NSDictionary)
            }
            if (Utilities.isValidValue(someObject: (detail["notificationText"]) as AnyObject)){
                self.notificationMessage = detail["notificationText"] as? String
            }
            
            //Setting the configuration if any
            if (Utilities.isValidObject(someObject: detail[kResourceConfigration] as AnyObject)) {
                let configuration = detail[kResourceConfigration] as! Dictionary<String,Any>
                self.povAvailable = true
                if (Utilities.isValidValue(someObject: (configuration["availableDate"]) as AnyObject)) {
                    self.startDate = Utilities.getDateFromStringWithFormat("YYYY-MM-dd", resultDate: configuration["availableDate"] as! String)
                }
                if (Utilities.isValidValue(someObject: (configuration["expiryDate"]) as AnyObject)) {
                    self.startDate = Utilities.getDateFromStringWithFormat("YYYY-MM-dd", resultDate: configuration["expiryDate"] as! String)
                }
                
                self.anchorDateStartDays = configuration["startDays"] as? Int
                self.anchorDateEndDays = configuration["endDays"] as? Int
                
            } else {
                self.povAvailable = false
            }
            
            if (Utilities.isValidValue(someObject: (detail[kResourceTitle]) as AnyObject)) {
                self.title = detail[kResourceTitle] as? String
            }
            self.file = File()
            self.file?.setFileForStudy(dict: detail as NSDictionary)
        } else {
            Logger.sharedInstance.debug("Resource Dictionary is null:\(detail)")
        }
    }
    
    /**
     Setter method for resource
     @param dict, dictionary of properties of resource
     */
    func setResource(dict: NSDictionary) {
        
        if Utilities.isValidObject(someObject: dict){
            
            if (Utilities.isValidValue(someObject: (dict[kResourceId]) as AnyObject)) {
                self.resourcesId = dict[kResourceId] as? String
            }
            if (Utilities.isValidValue(someObject: (dict[kResourceAudience]) as AnyObject)) {
                self.audience = Audience(rawValue:dict[kResourceAudience] as! String)
            }
            if (Utilities.isValidValue(someObject: (dict[kResourceLevel]) as AnyObject)) {
                self.level = dict[kResourceLevel] as? ResourceLevel
            }
            if (Utilities.isValidValue(someObject: (dict[kResourceKey]) as AnyObject)){
                self.key = dict[kResourceKey] as? String
            }
            if (Utilities.isValidValue(someObject: (dict[kResourceType]) as AnyObject)) {
                self.type = dict[kResourceType] as? String
            }
            if (Utilities.isValidValue(someObject: (dict["notificationText"]) as AnyObject)){
                self.notificationMessage = dict["notificationText"] as? String
            }
            
            if self.level != nil {
                
                if self.level == ResourceLevel.study {
                    // Study Level
                    self.file?.setFileForStudy(dict:dict)
                    
                } else if self.level == ResourceLevel.gateway {
                    // Gateway Level
                    if (Utilities.isValidValue(someObject: (dict[kResourceFile]) as AnyObject)) {
                        self.file?.setFile(dict: dict[kResourceFile] as! NSDictionary)
                    }
                }
            }
            
            //Setting the configurations
            if (Utilities.isValidObject(someObject: dict[kResourceConfigration] as AnyObject)) {
                let configuration = dict[kResourceConfigration] as! Dictionary<String,Any>
                self.povAvailable = true
                if (Utilities.isValidValue(someObject: (configuration["availableDate"]) as AnyObject)) {
                    self.startDate = Utilities.getDateFromStringWithFormat("YYYY-MM-dd", resultDate: configuration["availableDate"] as! String)
                }
                if (Utilities.isValidValue(someObject: (configuration["expiryDate"]) as AnyObject)) {
                    self.endDate = Utilities.getDateFromStringWithFormat("YYYY-MM-dd", resultDate: configuration["expiryDate"] as! String)
                }
                self.anchorDateStartDays = configuration["startDays"] as? Int
                self.anchorDateEndDays = configuration["endDays"] as? Int
                
            } else {
                self.povAvailable = false
            }
            if (Utilities.isValidValue(someObject: (dict[kResourceTitle]) as AnyObject)) {
                self.title = dict[kResourceTitle] as? String
            }
        } else {
            Logger.sharedInstance.debug("Resource Dictionary is null:\(dict)")
        }
    }
}
