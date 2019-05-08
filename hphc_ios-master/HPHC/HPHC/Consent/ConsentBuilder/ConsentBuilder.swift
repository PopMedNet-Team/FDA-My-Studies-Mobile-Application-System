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
import ResearchKit

//Consent Api Constants

let kConsentVisual = "consent"
let kConsentSharing = "sharing"
let kConsentReview = "review"
let kConsentVisualScreens = "visualScreens"

let kConsentVersion = "version"
let kConsentPdfContent = "content"

let kConsentTaskIdentifierText = "ConsentTask"

// SharingConsent Api Constants

let kConsentSharingStepShortDesc = "shortDesc"
let kConsentSharingStepLongDesc = "longDesc"
let kConsentSharingSteplearnMore = "learnMore"
let kConsentSharingStepText = "text"
let kConsentSharingStepTitle = "title"
let kConsentSharingStepAllowWithoutSharing = "allowWithoutSharing"


// ReviewConsent Api Constants

let kConsentReviewStepTitle = "title"
let kConsentReviewStepSignatureTitle = "signatureTitle"
let kConsentReviewStepSignatureContent = "reviewHTML"
let kConsentReviewStepReasonForConsent = "reasonForConsent"


// Comprehension Api Constants

let kConsentComprehension = "comprehension"
let kConsentComprehensionQuestions = "questions"
let kConsentComprehensionCorrectAnswers = "correctAnswers"
let kConsentComprehensionCorrectAnswersKey = "key"
let kConsentComprehensionAnswer = "answer"
let kConsentComprehensionPassScore = "passScore"
let kConsentComprehensionEvaluation = "evaluation"

//

let kConsentSharePdfCompletionStep = "ConsentShareCompletionStep"
let kConsentViewPdfCompletionStep = "ConsentViewPdfCompletionStep"
let kConsentCompletionStepIdentifier = "ConsentFinalCompletionStep"


//Completion Storyboard Segue

let kConsentSharePdfStoryboardId = "ConsentSharePdfStepViewControllerIdentifier"

let kConsentViewPdfStoryboardId = "ConsentPdfViewerStepViewControllerIdentifier"


// Comprehenion Instruction Step Keys

let kConsentComprehensionTestTitle = "Comprehension"
let kConsentComprehensionTestText = "Let's do a quick and simple test of your understanding of this Study."
let kComprehensionInstructionStepIdentifier = "ComprehensionInstructionStep"

// Comprehension Completion Step Keys

let kComprehensionCompletionTitle = "Great Job!"
let kComprehensionCompletionText = "You answered all of the questions correctly. Tap on Next to proceed"
let kComprehensionCompletionStepIdentifier = "ComprehensionCompletionStep"


//Consent Completion

let kConsentCompletionMainTitle = "Thanks for providing consent for this Study"
let kConsentCompletionSubTitle = "You can now start participating in the Study"
let kSignaturePageContentText = "I agree to participate in this research study."

//Signature Page

let kConsentSignaturePageContent = "signaturePageContent"
let kConsentSignaturePageTitle = "Participant"

enum ConsentStatus:String {
    case pending = "pending"
    case completed = "completed"
}




// MARK:ConsentBuilder Class

class ConsentBuilder {
    
    //var consentIdentifier:String? // this id will be used to add to signature
    
    var consentSectionArray: [ORKConsentSection]
    var consentStepArray: [ORKStep]
    
    var reviewConsent: ReviewConsent?
    var sharingConsent: SharingConsent?
    
    var consentDocument: ORKConsentDocument?
    var version: String?
    var consentStatus: ConsentStatus?
    var consentHasVisualStep: Bool?
    static var currentConsent: ConsentBuilder? = nil
    
    var consentResult: ConsentResult?
    
    var comprehension: Comprehension?
    
    /* Default Initializer method
     by default sets all params to empty values
     */
    init() {
       
        self.consentSectionArray = []
        
        self.consentStepArray = []
        self.reviewConsent = ReviewConsent()
        self.sharingConsent = SharingConsent()
        self.consentResult = ConsentResult()
        self.consentDocument = ORKConsentDocument()
        self.version = ""
        self.consentHasVisualStep = false
        
        self.comprehension = Comprehension()
    }
    
    
    /* initializer method which initializes all params
     @metaDataDict:contains as Dictionaries for all the properties of Consent Step
     */
    func initWithMetaData(metaDataDict: Dictionary<String, Any>)  {
        
        self.consentStatus = .pending
         self.consentHasVisualStep = false
        
        if Utilities.isValidObject(someObject: metaDataDict as AnyObject?) {
            
            if Utilities.isValidValue(someObject: metaDataDict[kConsentVersion] as AnyObject?) {
                
                self.version =  (metaDataDict[kConsentVersion] as? String)!
            }
            else {
                self.version = "No_Version"
            }
            let visualConsentArray = (metaDataDict[kConsentVisualScreens] as? Array<Dictionary<String,Any>>)!
            
            if  Utilities.isValidObject(someObject: visualConsentArray as AnyObject?) {
                for sectionDict in visualConsentArray {
                    
                    let consentSection: ConsentSectionStep? = ConsentSectionStep()
                    consentSection?.initWithDict(stepDict: sectionDict)
                         consentSectionArray.append((consentSection?.createConsentSection())!)
                    
                    if consentSection?.type != .custom {
                        self.consentHasVisualStep = true
                    }
                    
                }
                
            }
            
            let consentSharingDict = (metaDataDict[kConsentSharing] as? Dictionary<String,Any>)!
            
            if  Utilities.isValidObject(someObject: consentSharingDict as AnyObject?) {
                self.sharingConsent?.initWithSharingDict(dict: consentSharingDict)
                
            }
            let reviewConsentDict = (metaDataDict[kConsentReview] as? Dictionary<String,Any>)!
            
            if  Utilities.isValidObject(someObject: reviewConsentDict as AnyObject?) {
                self.reviewConsent?.initWithReviewDict(dict: reviewConsentDict)
                
            }
            
            let comprehensionDict = (metaDataDict[kConsentComprehension] as? Dictionary<String,Any>)!
            
            if  Utilities.isValidObject(someObject: comprehensionDict as AnyObject?) {
                self.comprehension?.initWithComprehension(dict: comprehensionDict)
                
            }
            
            
        }
    }
    
    
    /* Method to get VisualConsentStep
     @returns an instance of ORKVisualConsentStep
     */
    func getVisualConsentStep() -> VisualConsentStep? {
        
        if self.consentSectionArray.count > 0 {
            
            let invisibleVisualConsents = self.consentSectionArray.filter({$0.type == .onlyInDocument})
            
            if invisibleVisualConsents.count ==  self.consentSectionArray.count {
                return nil
            }
            else {
                //create Visual Consent Step
                let visualConsentStep = VisualConsentStep(identifier: kVisualStepId, document: self.getConsentDocument())
                return visualConsentStep
            }
        }
        else {
            return nil
        }
    }
    
    
    func getConsentDocument() -> ORKConsentDocument {
        
        if self.consentDocument != nil && Utilities.isValidObject(someObject: self.consentDocument?.sections as AnyObject?)   {
            return self.consentDocument!
        }
        else {
            self.consentDocument = self.createConsentDocument()
            return self.consentDocument!
        }
        
        
    }
    
    /* Method to create ConsentDocument
     @returns a ORKConsentDocument for VisualConsentStep and Review Step
     */
    func createConsentDocument() -> ORKConsentDocument? {
        
        let consentDocument = ORKConsentDocument()
        
        if Utilities.isValidValue(someObject: "title" as AnyObject )
            && Utilities.isValidValue(someObject: "signaturePageTitle" as AnyObject )
            && Utilities.isValidValue(someObject: kConsentSignaturePageContent as AnyObject ) {
            
            
            consentDocument.title = Study.currentStudy?.name
            consentDocument.signaturePageTitle = kConsentSignaturePageTitle
            consentDocument.signaturePageContent = kConsentSignaturePageContent
            
            consentDocument.sections = [ORKConsentSection]()
            
            if self.consentSectionArray.count > 0 {
               consentDocument.sections?.append(contentsOf: self.consentSectionArray)
            }
            
            let investigatorSignature = ORKConsentSignature(forPersonWithTitle: kConsentSignaturePageTitle, dateFormatString: "MM/dd/YYYY", identifier: "Signature")
            
            consentDocument.addSignature(investigatorSignature)
            return consentDocument
            
        }
        else {
            
            Logger.sharedInstance.debug("consent Step has null values:")
            return nil
        }
        
    }
    
    /* Method to get ComprehensionSteps
     @returns an array of ORKSteps
     */
    func getComprehensionSteps() -> [ORKStep]? {

        if (self.comprehension?.questions?.count)! > 0 {
            var stepsArray: [ORKStep]? = [ORKStep]()
            for stepDict in (self.comprehension?.questions!)! {
                //create questionSteps
                let questionStep: ActivityQuestionStep? = ActivityQuestionStep()
                questionStep?.initWithDict(stepDict: stepDict )
                
                questionStep?.skippable = false
                stepsArray?.append((questionStep?.getQuestionStep())!)
            }
            return stepsArray
        }
        else {
            return nil
        }
    }
    
    /* Method to get ReviewConsentStep
     @returns an instance of ORKConsentReviewStep
     */
    func getReviewConsentStep() -> ConsentReviewStep? {
    
        let reviewConsentStep: ConsentReviewStep?

        if  Utilities.isValidValue(someObject: self.reviewConsent?.signatureContent as AnyObject ) {
            
            //create Consent Document
            let consentDocument: ORKConsentDocument? = (self.getConsentDocument() as ORKConsentDocument)
            consentDocument?.htmlReviewContent = self.reviewConsent?.signatureContent
            consentDocument?.signaturePageContent = NSLocalizedString(kSignaturePageContentText, comment: "")
            
             //Initialize review Step
             reviewConsentStep = ConsentReviewStep(identifier: kReviewTitle, signature: (self.getConsentDocument() as ORKConsentDocument).signatures?[0], in: self.getConsentDocument())
            
        }else {
            //create Consent Document
            let consentDocument: ORKConsentDocument? = (self.getConsentDocument() as ORKConsentDocument)
            consentDocument?.signaturePageContent = NSLocalizedString(kSignaturePageContentText, comment: "")
            
            //Initialize review Step
             reviewConsentStep = ConsentReviewStep(identifier: kReviewTitle, signature: consentDocument?.signatures?[0], in: consentDocument!)
        }
        
        if Utilities.isValidValue(someObject: self.reviewConsent?.signatureTitle as AnyObject ) {
            reviewConsentStep?.text = self.reviewConsent?.title
        } else {
            reviewConsentStep?.text = ""
        }
        
        if Utilities.isValidValue(someObject: self.reviewConsent?.reasonForConsent as AnyObject ) {
            reviewConsentStep?.reasonForConsent = (self.reviewConsent?.reasonForConsent)!
        } else {
            reviewConsentStep?.reasonForConsent = ""
        }
        
        return reviewConsentStep
        
    }
    
    /* Method to get ConsentSharingStep
     @returns an instance of ORKConsentSharingStep
     */
    func getConsentSharingStep() -> ConsentSharingStep? {
        
        if Utilities.isValidValue(someObject: self.sharingConsent?.shortDesc as AnyObject )
            && Utilities.isValidValue(someObject: self.sharingConsent?.longDesc as AnyObject )
            && Utilities.isValidValue(someObject: self.sharingConsent?.learnMore as AnyObject ) {
            
            //create shareStep
            let sharingConsentStep = ConsentSharingStep(identifier: kConsentSharing, investigatorShortDescription: (self.sharingConsent?.shortDesc)!, investigatorLongDescription: (self.sharingConsent?.longDesc)!, localizedLearnMoreHTMLContent: (self.sharingConsent?.learnMore)!)
            
            if Utilities.isValidValue(someObject: self.sharingConsent?.text as AnyObject ) {
                sharingConsentStep.text = self.sharingConsent?.text
            }
           
            if Utilities.isValidValue(someObject: self.sharingConsent?.title as AnyObject ) {
                sharingConsentStep.title = self.sharingConsent?.title
            }
            return sharingConsentStep
        }else {
            Logger.sharedInstance.debug("consent Step has null values:")
            return nil
        }
    }
    
    /** Method to get ORKTask i.e ConsentTask
     @returns an instance of ORKTask
     */
    func createConsentTask() -> ORKTask? {
        
        let visualConsentStep: VisualConsentStep? = self.getVisualConsentStep()
        let sharingConsentStep: ConsentSharingStep? = self.getConsentSharingStep()
        let reviewConsentStep: ConsentReviewStep? = self.getReviewConsentStep()
        
        let comprehensionSteps: [ORKStep]? = self.getComprehensionSteps()
        
        var stepArray: Array<ORKStep>? = Array()
        
        if visualConsentStep != nil {
            
            if  self.consentHasVisualStep! {
                stepArray?.append(visualConsentStep!)
            }else {
                //  means all steps are custom only included in documents and there are no Visual Step
                // Do Nothing
            }
        }
        
        // comprehension steps
        if comprehensionSteps != nil && (comprehensionSteps?.count)! > 0 {
            
            // adding Instruction Step for Comprehenion
            let comprehensionTestInstructionStep = customInstructionStep(identifier: kComprehensionInstructionStepIdentifier)
            comprehensionTestInstructionStep.text = kConsentComprehensionTestText
            
            comprehensionTestInstructionStep.title = kConsentComprehensionTestTitle
            stepArray?.append(comprehensionTestInstructionStep)
            
            // adding questionary
            for step in comprehensionSteps! {
                stepArray?.append(step)
            }
            
            //adding Completion Step
            let comprehensionCompletionStep = customInstructionStep(identifier: kComprehensionCompletionStepIdentifier)
            comprehensionCompletionStep.text = kComprehensionCompletionText
            
            comprehensionCompletionStep.title = kComprehensionCompletionTitle
            comprehensionCompletionStep.image =  #imageLiteral(resourceName: "successBlueBig")
            stepArray?.append(comprehensionCompletionStep)

        }
        
        
        if sharingConsentStep != nil {
            stepArray?.append(sharingConsentStep!)
        }
        if reviewConsentStep != nil {
            stepArray?.append(reviewConsentStep!)
        }
        
        //ConsentCompletionStep
        let consentCompletionStep = ConsentCompletionStep(identifier: kConsentSharePdfCompletionStep)
        
        consentCompletionStep.mainTitle = kConsentCompletionMainTitle
        consentCompletionStep.subTitle =  kConsentCompletionSubTitle
        
        let consentViewPdfStep = ConsentPdfViewerStep(identifier: kConsentViewPdfCompletionStep )
        
        //create Custom completion step
        let completionStep = CustomCompletionStep(identifier: kConsentCompletionStepIdentifier)
        completionStep.detailText = NSLocalizedString(kConsentCompletionMainTitle, comment: "")
       
        
        if (stepArray?.count)! > 0 {
            
             stepArray?.append(consentCompletionStep)
             stepArray?.append(consentViewPdfStep)
            stepArray?.append(completionStep)
            
            let task = ORKNavigableOrderedTask(identifier: kConsentTaskIdentifierText, steps: stepArray)
            return task
        }
        else {
            return nil
        }
    }
}


// MARK:SharingConsent Struct

struct SharingConsent {
    
    var shortDesc: String?
    var longDesc: String?
    var learnMore: String?
    var allowWithoutSharing: Bool?
    var text: String?
    var title: String?
   
    /** Default Initializer
    */
    init() {
        self.shortDesc = ""
        self.longDesc =  ""
        self.learnMore =  ""
        self.allowWithoutSharing =  false
        self.text = ""
        self.title = ""
    }
    
    /* initializer method which initializes all params
     @dict:contains as key:Value pair for all the properties of SharingConsent Step
     */
    public mutating func initWithSharingDict(dict: Dictionary<String, Any>)
    {
        
        if Utilities.isValidObject(someObject: dict as AnyObject?) {
            
            if Utilities.isValidValue(someObject: dict[kConsentSharingStepShortDesc] as AnyObject ) {
                self.shortDesc =  (dict[kConsentSharingStepShortDesc] as? String)!
            }
            
            if Utilities.isValidValue(someObject: dict[kConsentSharingStepLongDesc] as AnyObject ) {
                self.longDesc = dict[kConsentSharingStepLongDesc] as? String
            }
            if Utilities.isValidValue(someObject: dict[kConsentSharingSteplearnMore] as AnyObject ) {
                self.learnMore = dict[kConsentSharingSteplearnMore] as? String
            }
            
            if Utilities.isValidValue(someObject: dict[kConsentSharingStepText] as AnyObject ) {
                self.text = dict[kConsentSharingStepText] as? String
            }
            if Utilities.isValidValue(someObject: dict[kConsentSharingStepTitle] as AnyObject ) {
                self.title = dict[kConsentSharingStepTitle] as? String
            }
            
//            if Utilities.isValidValue(someObject: dict[kConsentSharingStepAllowWithoutSharing] as AnyObject ){
//                self.allowWithoutSharing = dict[kConsentSharingStepAllowWithoutSharing] as? Bool
//            }
          
           self.allowWithoutSharing = true
          
        }else {
            Logger.sharedInstance.debug("ConsentDocument Step Dictionary is null:\(dict)")
        }
        
    }
    
}


// MARK:ReviewConsent Struct

struct ReviewConsent {
    
    var title: String?
    var signatureTitle: String?
    var signatureContent: String?
    var reasonForConsent: String?
    
    init() {
        self.title = ""
        self.signatureTitle =  ""
        self.signatureContent =  ""
        self.reasonForConsent = ""
    }
    
    /* initializer method which initializes all params
     @dict:contains as key:Value pair for all the properties of ReviewConsent Step
     */
    mutating func initWithReviewDict(dict: Dictionary<String, Any>) {
        
        if Utilities.isValidObject(someObject: dict as AnyObject?) {
            
            if Utilities.isValidValue(someObject: dict[kConsentReviewStepTitle] as AnyObject ) {
                self.title =  (dict[kConsentReviewStepTitle] as? String)!
            }
            
            if Utilities.isValidValue(someObject: dict[kConsentReviewStepSignatureTitle] as AnyObject ) {
                self.signatureTitle = dict[kConsentReviewStepSignatureTitle] as? String
            }
            if Utilities.isValidValue(someObject: dict[kConsentReviewStepSignatureContent] as AnyObject ) {
                self.signatureContent = dict[kConsentReviewStepSignatureContent] as? String
            }
            if Utilities.isValidValue(someObject: dict[kConsentReviewStepReasonForConsent] as AnyObject ) {
                self.reasonForConsent = dict[kConsentReviewStepReasonForConsent] as? String
            }
        }
        else {
            Logger.sharedInstance.debug("ConsentDocument Step Dictionary is null:\(dict)")
        }
        
    }
}

// MARK:Comprehension Struct

enum Evaluation: String {
    case any = "any"
    case all = "all"
}


struct Comprehension {
    
    var passScore: Int?
    var questions: Array<Dictionary<String,Any>>?
    var correctAnswers: Array<Dictionary<String,Any>>?
    
    
    
    init() {
        self.passScore = 0
        self.questions =  []
        self.correctAnswers =  []
    }
    
    /* initializer method which initializes all params
     @dict:contains as key:Value pair for all the properties of Comprehension Step
     */
    mutating func initWithComprehension(dict: Dictionary<String, Any>) {
        
        if Utilities.isValidObject(someObject: dict as AnyObject?) {
            
            if Utilities.isValidValue(someObject: dict[kConsentComprehensionPassScore] as AnyObject ) {
                self.passScore =  (dict[kConsentComprehensionPassScore] as? Int)!
            }
            
            if Utilities.isValidObject(someObject: dict[kConsentComprehensionQuestions] as AnyObject ) {
                self.questions = dict[kConsentComprehensionQuestions] as? Array<Dictionary<String,Any>>
            }
            if Utilities.isValidObject(someObject: dict[kConsentComprehensionCorrectAnswers] as AnyObject ) {
                self.correctAnswers = dict[kConsentComprehensionCorrectAnswers] as?  Array<Dictionary<String,Any>>
            }
        }
        else{
            Logger.sharedInstance.debug("Comprehension Step Dictionary is null:\(dict)")
        }
        
    }
}

// MARK: Custom Clases

class ConsentReviewStep: ORKConsentReviewStep {
    
   /* func showsProgress() -> Bool {
        return true
    }
    */
}

class VisualConsentStep: ORKVisualConsentStep {
    
   /* func showsProgress() -> Bool {
        return true
    }
    */
}

class ConsentSharingStep: ORKConsentSharingStep {
    
   /* func showsProgress() -> Bool {
        return true
    }
   */
}

class CustomCompletionStep: ORKCompletionStep {
    
    func showsProgress() -> Bool {
     return true
     }
    
}


