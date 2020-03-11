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

let kOverViewWebsiteLink = "studyWebsite"

class Overview {
    enum OverviewType: Int {
        case gateway
        case study
    }
    var sections: Array<OverviewSection>!
    var type: OverviewType = .gateway
    var websiteLink: String?
}

class OverviewSection {
    
    var title: String?           //title of overview
    var type: String?            //type of overview video/image
    var imageURL: String?        //download url of image
    var text: String?            //short description
    var link: String?            //used for media link
    var websiteLink: String?     //used for Website link
    
    init(){
    }
    
    /**
     Initializer with Dictionary of properties
    */
    init(detail: Dictionary<String, Any>){
        
        /* Checks the Object (Array or Dictionary) is valid or not */
        if Utilities.isValidObject(someObject: detail as AnyObject?){
            
            /* Checks the Title value (Null, Nil, empty etc) is valid or not */
            if Utilities.isValidValue(someObject: detail[kOverviewTitle] as AnyObject ){
                self.title = detail[kOverviewTitle] as? String
            }
            /* Checks the Type value (Null, Nil, empty etc) is valid or not */
            if Utilities.isValidValue(someObject: detail[kOverviewType] as AnyObject ){
                self.type = detail[kOverviewType] as? String
            }
            /* Checks the Text value (Null, Nil, empty etc) is valid or not */
            if Utilities.isValidValue(someObject: detail[kOverviewText] as AnyObject ){
                self.text = detail[kOverviewText] as? String
            }
            /* Checks the Link value (Null, Nil, empty etc) is valid or not */
            if Utilities.isValidValue(someObject: detail[kOverviewImageLink] as AnyObject ){
                self.imageURL = detail[kOverviewImageLink] as? String
            }
            /* Checks the MediaLink value (Null, Nil, empty etc) is valid or not */
            if Utilities.isValidValue(someObject: detail[kOverviewMediaLink] as AnyObject ) {
                self.link = detail[kOverviewMediaLink] as? String
            }
            /* Checks the WebsiteLink value (Null, Nil, empty etc) is valid or not */
            if Utilities.isValidValue(someObject: detail[kOverviewWebsiteLink] as AnyObject ) {
                self.websiteLink = detail[kOverviewWebsiteLink] as? String
            }
        } else {
            Logger.sharedInstance.debug("Overview Dictionary is null:\(detail)")
        }
    }
}
