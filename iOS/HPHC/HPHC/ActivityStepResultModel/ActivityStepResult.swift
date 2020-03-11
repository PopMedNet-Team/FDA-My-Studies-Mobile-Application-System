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

let kActivityStepStartTime = "startTime"
let kActivityStepEndTime = "endTime"

let kActivityStepSkipped = "skipped"
let kActivityStepResultValue = "value"

let kActivityActiveKeyResultType = "resultType" // to be used specifically for Active Task

let kActivityActiveStepKey = "key"

let kSpatialSpanMemoryKeyScore = "Score"
let kSpatialSpanMemoryKeyNumberOfGames = "NumberofGames"
let kSpatialSpanMemoryKeyNumberOfFailures = "NumberofFailures"

let kTowerOfHanoiKeyPuzzleWasSolved = "puzzleWasSolved"
let kTowerOfHanoiKeyNumberOfMoves = "numberOfMoves"

let kFetalKickCounterDuration = "duration"
let kFetalKickCounterCount = "count"

enum ActiveStepResultType: String {
    case boolean = "boolean"
    case numeric = "numeric"
}

enum SpatialSpanMemoryType: Int {
    case score = 0
    case numberOfGames = 1
    case numberOfFailures = 2
}

enum TowerOfHanoiResultType:Int {
    case puzzleWasSolved = 0
    case numberOfMoves = 1
}

enum ActvityStepResultType: String {
    case formOrActiveTask = "grouped"
    case questionnaire = "questionnaire"
    
}

class ActivityStepResult {
    
    var type: ActivityStepType?
    var step: ActivityStep?
    var key: String? // Identifier of step
    var startTime: Date?
    var endTime: Date?
    var skipped: Bool?
    
    /** stores the result value of step, it can be of any type
    */
    var value: Any?
    
    var subTypeForForm: String? //Exclusively used for form step to store the formItem type
    
    var questionStep: ActivityQuestionStep?
    
    /* default initializer method
     */
    init() {
        self.step = ActivityStep() //instance of ActivityStep
        self.type = .question //specifies the step type
        self.key = "" //stores the identifier of step
        self.startTime = Date.init(timeIntervalSinceNow: 0)
        self.endTime = Date.init(timeIntervalSinceNow: 0)
        self.skipped = false
        self.value = 0
        self.questionStep = nil
        self.subTypeForForm = ""
        
    }
    // MARK: Utility Method
    
    /*
     initializer method for the class
     @param stepResult  is the ORKStepResult
     @param activityType    holds the activity type
     
     */
    func initWithORKStepResult(stepResult: ORKStepResult,activityType: ActivityType) {
        
        if Utilities.isValidValue(someObject: stepResult.identifier as AnyObject?) {
            self.key = stepResult.identifier
        }
        
        self.startTime = stepResult.startDate
        self.endTime = stepResult.endDate
        
        if (stepResult.results?.count)! > 1 || (self.step != nil && self.step is ActivityFormStep) {
            self.type = .form
        } else {
            if activityType == .activeTask {
                self.type =  .active
            }
        }
        self.setResultValue(stepResult: stepResult ,activityType: activityType )
        
    }
    
    
    /* method create ActivityStepResult by initializing params
     @stepDict:contains all ActivityResultStep properties
     */
    func initWithDict(stepDict: Dictionary<String, Any>) {
        
        if Utilities.isValidObject(someObject: stepDict as AnyObject?) {
            
            if Utilities.isValidValue(someObject: stepDict[kActivityStepType] as AnyObject ) {
                self.type = stepDict[kActivityStepType] as? ActivityStepType
            }
            if Utilities.isValidValue(someObject: stepDict[kActivityStepKey] as AnyObject ) {
                self.key = stepDict[kActivityStepKey] as? String
            }
            if Utilities.isValidValue(someObject: stepDict[kActivityStepStartTime] as AnyObject ) {
                
                if Utilities.isValidValue(someObject: Utilities.getDateFromString(dateString:(stepDict[kActivityStepStartTime] as? String)!) as AnyObject?) {
                    self.startTime =  Utilities.getDateFromString(dateString: (stepDict[kActivityStepStartTime] as? String)!)
                } else {
                    Logger.sharedInstance.debug("Date Conversion is null:\(stepDict)")
                }
            }
            if Utilities.isValidValue(someObject: stepDict[kActivityStepEndTime] as AnyObject ) {
                
                if Utilities.isValidValue(someObject: Utilities.getDateFromString(dateString:(stepDict[kActivityStepEndTime] as? String)!) as AnyObject?) {
                    
                    self.endTime =  Utilities.getDateFromString(dateString: (stepDict[kActivityStepEndTime] as? String)!)
                } else {
                    Logger.sharedInstance.debug("Date Conversion is null:\(stepDict)")
                }
            }
            
            if Utilities.isValidValue(someObject: stepDict[kActivityStepSkipped] as AnyObject ) {
                self.skipped = stepDict[kActivityStepSkipped] as? Bool
            }
            
        } else {
            Logger.sharedInstance.debug("Step Result Dictionary is null:\(stepDict)")
        }
        
    }
    
    /* method creates a ActivityStepDictionary from step instance
     returns ResultDictionary for storing data to Api/Local
     */
    func getActivityStepResultDict() -> Dictionary<String, Any>? {
        
        var stepDict:Dictionary<String,Any>? = Dictionary<String,Any>()
        
        switch self.type! as ActivityStepType {
            
        case .instruction: stepDict?[kActivityStepResultType] = "null"
            
        case .question:  stepDict?[kActivityStepResultType] = self.step?.resultType
            
        case .form: stepDict?[kActivityStepResultType] = ActvityStepResultType.formOrActiveTask.rawValue
            
        case .active:
            
            if self.step?.resultType != nil {
            
            stepDict?[kActivityStepResultType] =  (self.step?.resultType as? String)! == "fetalKickCounter" ? "grouped" : (self.step?.resultType)
                
            } else {
              stepDict?[kActivityStepResultType] = "grouped"
          }
        default: break
            
        }
      
        if Utilities.isValidValue(someObject: self.key as AnyObject?) {
            
            stepDict?[kActivityStepKey] = self.key!
        }
        if self.startTime != nil && (Utilities.getStringFromDate(date: self.startTime!) != nil) {
            
            stepDict?[kActivityStartTime] = Utilities.getStringFromDate(date: self.startTime!)
        }
        if self.endTime != nil && (Utilities.getStringFromDate(date: self.endTime!) != nil) {
            
            stepDict?[kActivityEndTime] = Utilities.getStringFromDate(date: self.endTime!)
        }
        
        if self.value != nil {
            stepDict?[kActivityStepResultValue] = self.value
            // checking if step is skippable
            if self.value is Array<Any> || self.value is Dictionary<String, Any> {
                
                if Utilities.isValidObject(someObject: self.value as AnyObject ) {
                    stepDict?[kActivityStepSkipped] = false
                    
                } else {
                    stepDict?[kActivityStepSkipped] = true
                }
                
            } else {
                if Utilities.isValidValue(someObject: self.value as AnyObject) {
                    stepDict?[kActivityStepSkipped] = false
                    
                } else {
                    stepDict?[kActivityStepSkipped] = true
                }
            }
        } else {
            stepDict?[kActivityStepSkipped] = self.skipped!
        }
        return stepDict
    }
    
    /* method saves the result of Current Step
     @stepResult: stepResult which can be result of Questionstep/InstructionStep/ActiveTask
     */
    func setResultValue(stepResult: ORKStepResult, activityType: ActivityType)  {
        
        if((stepResult.results?.count)! > 0) {
            
            if  activityType == .Questionnaire {
                // for question Step
                
                if stepResult.results?.count == 1 && self.type != .form {
                    
                    if  let questionstepResult: ORKQuestionResult? = stepResult.results?.first as? ORKQuestionResult? {
                        self.setValue(questionstepResult:questionstepResult! )
                        
                    } else {
                        
                        // for consent step result we are storing the ORKConsentSignatureResult
                        let consentStepResult: ORKConsentSignatureResult? = (stepResult.results?.first as? ORKConsentSignatureResult?)!
                        self.value = consentStepResult;
                        
                    }
                } else {
                    // for form step result
                    
                    self.value  = [ActivityStepResult]()
                    var formResultArray:[Any] = [Any]()
                    var i: Int! = 0
                    var j: Int! = 0
                    var isAddMore: Bool? =  false
                    

                    if (stepResult.results?.count)! > (self.step as? ActivityFormStep)!.itemsArray.count {
                        isAddMore = true
                    }
                    var localArray: [Dictionary< String,Any>] = [Dictionary< String,Any>]()
                    
                    for result in stepResult.results! {
                        
                        let activityStepResult: ActivityStepResult? = ActivityStepResult()
                        activityStepResult?.startTime = self.startTime
                        activityStepResult?.endTime = self.endTime
                        activityStepResult?.skipped = self.skipped
                        
                        let activityStep = ActivityStep()
                        activityStepResult?.step = activityStep
                        
                        j = (i == 0 ? 0 : i % (self.step as? ActivityFormStep)!.itemsArray.count)
                        
                        //Checking if formStep is RepeatableFormStep
                        if isAddMore! {
                            if j  == 0 {
                                localArray.removeAll()
                                localArray = [Dictionary< String,Any>]()
                            }
                            
                            let stepDict = (((self.step as? ActivityFormStep)!.itemsArray) as [Dictionary<String,Any>])[j]
                            
                             activityStepResult?.key = stepDict["key"] as! String?
                            
                        } else {
                             activityStepResult?.key = result.identifier
                        }
                        let itemDict = (self.step as? ActivityFormStep)!.itemsArray[j] as Dictionary<String, Any>
                        activityStepResult?.step?.resultType = (itemDict["resultType"] as? String)!
                        if ((result as? ORKQuestionResult) != nil) {
                            
                            let questionResult: ORKQuestionResult? = (result as? ORKQuestionResult)
                            
                            if  Utilities.isValidValue(someObject: (activityStepResult?.step?.resultType as? String as AnyObject)) {
                                self.subTypeForForm = activityStepResult?.step?.resultType as? String
                                
                            } else {
                                self.subTypeForForm = ""
                            }
                            
                            self.setValue(questionstepResult: questionResult!)
                            
                            activityStepResult?.value = self.value
                            localArray.append((activityStepResult?.getActivityStepResultDict()!)!)
                            
                            //checking if more steps added in RepeatableFormStep
                            if isAddMore! {
                                if j + 1 == (self.step as? ActivityFormStep)!.itemsArray.count {
                                    if localArray.count > 0 {
                                        formResultArray.append(localArray)
                                    }
                                }
                            }
                        }
                        i = i + 1
                    }
                    
                    if isAddMore! {
                        self.value = formResultArray
                        
                    } else {
                        if localArray.count > 0 {
                            formResultArray.append(localArray)
                        }
                        self.value = formResultArray
                    }
                }
                
            } else if (activityType == .activeTask) { //For Active task like Fetal Kick, Spatial Span Memory & Towers of Honoi
                
                let activityResult: ORKResult? = stepResult.results?.first
                var resultArray: Array<Dictionary<String, Any>>? =  Array()
                
                if (activityResult as? ORKSpatialSpanMemoryResult) != nil { // Result Handling for Spatial Span Memory
                    
                    let stepTypeResult: ORKSpatialSpanMemoryResult? = activityResult as? ORKSpatialSpanMemoryResult
                    
                    if Utilities.isValidValue(someObject: stepTypeResult?.score as AnyObject?)
                        && Utilities.isValidValue(someObject: stepTypeResult?.numberOfGames as AnyObject?)
                        && Utilities.isValidValue(someObject: stepTypeResult?.numberOfFailures as AnyObject?) {
                        
                        for i in 0..<3 {
                            var resultDict: Dictionary<String, Any>? =  Dictionary()
                            
                            resultDict?[kActivityActiveKeyResultType] = ActiveStepResultType.numeric.rawValue
                            
                            switch SpatialSpanMemoryType(rawValue: i)! as SpatialSpanMemoryType {
                            case .score: // score
                                resultDict?[kActivityActiveStepKey] = kSpatialSpanMemoryKeyScore
                                resultDict?[kActivityStepResultValue] = stepTypeResult?.score
                                
                            case .numberOfGames: //numberOfGames
                                resultDict?[kActivityActiveStepKey] = kSpatialSpanMemoryKeyNumberOfGames
                                resultDict?[kActivityStepResultValue] = stepTypeResult?.numberOfGames
                                
                            case .numberOfFailures: // numberOfFailures
                                resultDict?[kActivityActiveStepKey] = kSpatialSpanMemoryKeyNumberOfFailures
                                resultDict?[kActivityStepResultValue] = stepTypeResult?.numberOfFailures
                            }
                            
                            if self.startTime != nil && (Utilities.getStringFromDate(date: self.startTime!) != nil) {
                                
                                resultDict?[kActivityStepStartTime] = Utilities.getStringFromDate(date: self.startTime!)
                            } else {
                                let currentDate = Date()
                                let dateString =  Utilities.getStringFromDate(date: currentDate)

                                resultDict?[kActivityStepStartTime] = dateString
                            }
                            if self.endTime != nil && (Utilities.getStringFromDate(date: self.endTime!) != nil) {
                                
                                resultDict?[kActivityStepEndTime] = Utilities.getStringFromDate(date: self.endTime!)
                            } else {
                                let currentDate = Date()
                                let dateString =  Utilities.getStringFromDate(date: currentDate)
                                resultDict?[kActivityStepEndTime] = dateString
                            }
                            resultDict?[kActivityStepSkipped] =  self.skipped
                            resultArray?.append(resultDict!)
                        }
                        self.key = Study.currentActivity?.actvityId
                        self.value = resultArray
                    } else {
                        self.value = 0
                    }
                }
                else if (activityResult as? ORKTowerOfHanoiResult) != nil { //Result Handling for Towers of Honoi
                    let stepTypeResult:ORKTowerOfHanoiResult? = activityResult as? ORKTowerOfHanoiResult
                    
                    for i in 0..<2 {
                        var resultDict: Dictionary<String, Any>? =  Dictionary()
                        //Saving puzzleWasSolved & numberOfMoves
                        if  TowerOfHanoiResultType(rawValue: i) == .puzzleWasSolved { //puzzleWasSolved
                            
                            resultDict?[kActivityActiveStepKey] = kTowerOfHanoiKeyPuzzleWasSolved
                            resultDict?[kActivityStepResultValue] = stepTypeResult?.puzzleWasSolved
                          resultDict?[kActivityActiveKeyResultType] = ActiveStepResultType.boolean.rawValue
                            
                        } else { // numberOfMoves
                            resultDict?[kActivityActiveStepKey] = kTowerOfHanoiKeyNumberOfMoves
                            resultDict?[kActivityStepResultValue] = stepTypeResult?.moves?.count
                          resultDict?[kActivityActiveKeyResultType] = ActiveStepResultType.numeric.rawValue
                        }
                        
                        if self.startTime != nil && (Utilities.getStringFromDate(date: self.startTime!) != nil) {
                            
                            resultDict?[kActivityStepStartTime] = Utilities.getStringFromDate(date: self.startTime!)
                        } else {
                            let currentDate = Date()
                            let dateString =  Utilities.getStringFromDate(date: currentDate)
                            
                            resultDict?[kActivityStepStartTime] = dateString
                        }
                        
                        //Saving Start & End Time of Step
                        if self.endTime != nil && (Utilities.getStringFromDate(date: self.endTime!) != nil) {
                            
                            resultDict?[kActivityStepEndTime] = Utilities.getStringFromDate(date: self.endTime!)
                            
                        } else {
                            let currentDate = Date()
                            let dateString =  Utilities.getStringFromDate(date: currentDate)
                            
                            resultDict?[kActivityStepEndTime] = dateString
                        }
                        resultDict?[kActivityStepSkipped] =  self.skipped
                        resultArray?.append(resultDict!)
                        
                    }
                    self.key = Study.currentActivity?.actvityId
                    self.value = resultArray
                    
                }
                else if (activityResult as? FetalKickCounterTaskResult) != nil { //Result handling for FetalKickCounter
                    let stepTypeResult: FetalKickCounterTaskResult? = activityResult as? FetalKickCounterTaskResult
                    
                    for i in 0..<2 {
                        var resultDict: Dictionary<String, Any>? =  Dictionary()
                        
                        resultDict?[kActivityActiveKeyResultType] = ActiveStepResultType.numeric.rawValue
                        
                        //Saving Duration & Kick Counts
                        if i == 0 { //Duration
                            resultDict?[kActivityActiveStepKey] = kFetalKickCounterDuration
                            resultDict?[kActivityStepResultValue] = Double((stepTypeResult?.duration) == nil ? 0 : (stepTypeResult?.duration)!)
                            
                        } else { // Kick Count
                            resultDict?[kActivityActiveStepKey] = kFetalKickCounterCount
                            resultDict?[kActivityStepResultValue] = Double((stepTypeResult?.totalKickCount) == nil ? 0 : (stepTypeResult?.totalKickCount)!)
                        }
                        
                        //Saving Start & End Time of Step
                        if self.startTime != nil && (Utilities.getStringFromDate(date: self.startTime!) != nil) {
                             resultDict?[kActivityStepStartTime] = Utilities.getStringFromDate(date: self.startTime!)
                        } else {
                            let currentDate = Date()
                            let dateString =  Utilities.getStringFromDate(date: currentDate)
                            resultDict?[kActivityStepStartTime] = dateString
                        }
                        if self.endTime != nil && (Utilities.getStringFromDate(date: self.endTime!) != nil) {
                            
                           resultDict?[kActivityStepEndTime] = Utilities.getStringFromDate(date: self.endTime!)
                        } else {
                            let currentDate = Date()
                            let dateString =  Utilities.getStringFromDate(date: currentDate)
                            
                            resultDict?[kActivityStepEndTime] = dateString
                        }
                        resultDict?[kActivityStepSkipped] =  self.skipped
                        resultArray?.append(resultDict!)
                    }
                    self.value = resultArray
                }
            } else {
                // Do Nothing
            }
        } else {
           //Do Nothing
        }
    }
    
    /**
     setValue method sets the questionStepResult value based on the QuestionStepType
     @param questionstepResult is instance of type ORKQuestionResult
    */
    func setValue(questionstepResult: ORKQuestionResult) {
        switch questionstepResult.questionType.rawValue {
            
        case  ORKQuestionType.scale.rawValue : //scale and continuos scale
            
            if ((questionstepResult as? ORKScaleQuestionResult) != nil) {
                let stepTypeResult = (questionstepResult as? ORKScaleQuestionResult)!
                
                if Utilities.isValidValue(someObject: stepTypeResult.scaleAnswer as AnyObject?) {
                    
                    if self.step != nil && (self.step as? ActivityQuestionStep) != nil && ((self.step as? ActivityQuestionStep)?.resultType as? String)! == "continuousScale" {
                        let formatDict: Dictionary<String, Any>
                        
                        formatDict = ((self.step as? ActivityQuestionStep)?.formatDict)!
                        let maxFractionDigit = formatDict[kStepQuestionContinuosScaleMaxFractionDigits]
                        
                        if (maxFractionDigit as? Int)! == 0 {
                            self.value = round((stepTypeResult.scaleAnswer as? Double)!)
                            
                        } else if (maxFractionDigit as? Int)! == 1 {
                            let v = (stepTypeResult.scaleAnswer as? Double)!
                            self.value = Double(round(10 * v)/10)
                            
                        } else if (maxFractionDigit as? Int)! == 2 {
                            let v = (stepTypeResult.scaleAnswer as? Double)!
                            self.value = Double(round(100 * v)/100)
                            
                        } else if (maxFractionDigit as? Int)! == 3 {
                            let v = (stepTypeResult.scaleAnswer as? Double)!
                            self.value = Double(round(1000 * v)/1000)
                            
                        } else if (maxFractionDigit as? Int)! == 4 {
                            let v = (stepTypeResult.scaleAnswer as? Double)!
                            self.value = Double(round(10000 * v)/10000)
                            
                        } else {
                            self.value = (stepTypeResult.scaleAnswer as? Double)!
                        }
                    } else {
                        self.value = (stepTypeResult.scaleAnswer as? Double)!
                    }
                } else {
                    self.value = 0.0
                }
            } else {
                let stepTypeResult = (questionstepResult as? ORKChoiceQuestionResult)!
                if Utilities.isValidObject(someObject: stepTypeResult.choiceAnswers as AnyObject?) {
                    
                    if (stepTypeResult.choiceAnswers?.count)! > 0 {
                        self.value = stepTypeResult.choiceAnswers?.first
                        
                    } else {
                        self.value = ""
                    }
                } else {
                    self.value = ""
                }
            }
            
        case ORKQuestionType.singleChoice.rawValue: //textchoice + value picker + imageChoice
            
            let stepTypeResult = (questionstepResult as? ORKChoiceQuestionResult)!
            var resultType: String? = (self.step?.resultType as? String)!
            
            // for form we have to assign the step type of each form item
            if resultType == "grouped" {
                resultType = self.subTypeForForm
            }
            
            if Utilities.isValidObject(someObject: stepTypeResult.choiceAnswers as AnyObject?) {
                if (stepTypeResult.choiceAnswers?.count)! > 0 {
                    
                    if resultType ==  QuestionStepType.imageChoice.rawValue ||  resultType == QuestionStepType.valuePicker.rawValue {
                        
                        //for image choice and valuepicker
                        
                        let resultValue: String! = "\(stepTypeResult.choiceAnswers!.first!)"
                        
                        self.value = (resultValue == nil ? "" : resultValue)
                        
                    } else {
                        // for text choice
                        var resultValue: [Any] = []
                        let selectedValue = stepTypeResult.choiceAnswers?.first
                        
                        if let stringValue = selectedValue as? String {
                            resultValue.append(stringValue)
                        } else if let otherDict = selectedValue as? [String:Any] {
                            resultValue.append(otherDict)
                        } else {
                            resultValue.append(selectedValue as Any)
                        }
                
                        self.value = resultValue
                    }
                    
                } else {
                    if resultType ==  QuestionStepType.imageChoice.rawValue ||  resultType == QuestionStepType.valuePicker.rawValue {
                        self.value = ""
                        
                    } else {
                        self.value = []
                    }
                }
            } else {
                if resultType ==  QuestionStepType.imageChoice.rawValue ||  resultType == QuestionStepType.valuePicker.rawValue {
                    self.value = ""
                    
                } else {
                    self.value = []
                }
            }
        case ORKQuestionType.multipleChoice.rawValue: //textchoice + imageChoice
            
            let stepTypeResult = (questionstepResult as? ORKChoiceQuestionResult)!
            
            if let answers = stepTypeResult.choiceAnswers {
                
                var resultArray: [Any] = []
        
                for value in answers {
                    
                    if let stringValue = value as? String {
                        resultArray.append(stringValue)
                    } else if let otherDict = value as? [String:Any] {
                        resultArray.append(otherDict)
                    } else {
                        resultArray.append(value)
                    }
                    
                }
                self.value = resultArray
                
            } else {
                self.value = []
            }
            
            /*
            if Utilities.isValidObject(someObject: stepTypeResult.choiceAnswers as AnyObject?) {
                if (stepTypeResult.choiceAnswers?.count)! > 1 {
                    
             
                    
                } else {
                    
                    let resultValue: String! = "\(stepTypeResult.choiceAnswers!.first!)"
                    let resultArray: Array<String>? = ["\(resultValue == nil ? "" : resultValue!)"]
                    self.value = resultArray
                }
                
            } else {
                self.value = []
            }
             */
            
        case ORKQuestionType.boolean.rawValue:
            
            let stepTypeResult = (questionstepResult as? ORKBooleanQuestionResult)!
            
            if Utilities.isValidValue(someObject: stepTypeResult.booleanAnswer as AnyObject?) {
                self.value =  stepTypeResult.booleanAnswer! == 1 ? true : false
                
            } else {
                self.value = false
            }
            
        case ORKQuestionType.integer.rawValue: // numeric type
            let stepTypeResult = (questionstepResult as? ORKNumericQuestionResult)!
            
            if Utilities.isValidValue(someObject: stepTypeResult.numericAnswer as AnyObject?) {
                self.value =  Double(truncating:stepTypeResult.numericAnswer!)
                
            } else {
                self.value = 0.0
            }
            
        case ORKQuestionType.decimal.rawValue: // numeric type
            let stepTypeResult = (questionstepResult as? ORKNumericQuestionResult)!
            
            if Utilities.isValidValue(someObject: stepTypeResult.numericAnswer as AnyObject?) {
                self.value = Double(truncating:stepTypeResult.numericAnswer!)
                
            } else {
                self.value = 0.0
            }
            
        case  ORKQuestionType.timeOfDay.rawValue:
            let stepTypeResult = (questionstepResult as? ORKTimeOfDayQuestionResult)!
            
            if stepTypeResult.dateComponentsAnswer != nil {
                
                let hour: Int? = (stepTypeResult.dateComponentsAnswer?.hour == nil ? 0 : stepTypeResult.dateComponentsAnswer?.hour)
                let minute: Int? = (stepTypeResult.dateComponentsAnswer?.minute == nil ? 0 : stepTypeResult.dateComponentsAnswer?.minute)
                let seconds: Int? = (stepTypeResult.dateComponentsAnswer?.second == nil ? 0 : stepTypeResult.dateComponentsAnswer?.second)
                
                self.value = (( hour! < 10 ? ("0" + "\(hour!)") : "\(hour!)") + ":" + ( minute! < 10 ? ("0" + "\(minute!)") : "\(minute!)") + ":" + ( seconds! < 10 ? ("0" + "\(seconds!)") : "\(seconds!)"))
                
            } else {
                self.value = "00:00:00"
            }
            
        case ORKQuestionType.date.rawValue:
            let stepTypeResult = (questionstepResult as? ORKDateQuestionResult)!
            
            if Utilities.isValidValue(someObject: stepTypeResult.dateAnswer as AnyObject?) {
                self.value =  Utilities.getStringFromDate(date: stepTypeResult.dateAnswer!)
            } else {
                self.value = "0000-00-00'T'00:00:00"
            }
        case ORKQuestionType.dateAndTime.rawValue:
            let stepTypeResult = (questionstepResult as? ORKDateQuestionResult)!
            
            if Utilities.isValidValue(someObject: stepTypeResult.dateAnswer as AnyObject?) {
                self.value =  Utilities.getStringFromDate(date: stepTypeResult.dateAnswer! )
                
            } else {
                self.value = "0000-00-00'T'00:00:00"
            }
        case ORKQuestionType.text.rawValue: // text + email
            
            let stepTypeResult = (questionstepResult as? ORKTextQuestionResult)!
            
            if Utilities.isValidValue(someObject: stepTypeResult.answer as AnyObject?) {
                self.value = (stepTypeResult.answer as? String)!
                
            } else {
                self.value = ""
            }
            
        case ORKQuestionType.timeInterval.rawValue:
            
            let stepTypeResult = (questionstepResult as? ORKTimeIntervalQuestionResult)!
            
            if Utilities.isValidValue(someObject: stepTypeResult.intervalAnswer as AnyObject?) {
                self.value = Double(truncating:stepTypeResult.intervalAnswer!)/3600
                
            } else {
                self.value = 0.0
            }
            
        case ORKQuestionType.height.rawValue:
            
            let stepTypeResult = (questionstepResult as? ORKNumericQuestionResult)!
            if Utilities.isValidValue(someObject: stepTypeResult.numericAnswer as AnyObject?) {
                self.value = Double(truncating:stepTypeResult.numericAnswer!)
                
            } else {
                self.value = 0.0
            }
            
        case ORKQuestionType.location.rawValue:
            let stepTypeResult = (questionstepResult as? ORKLocationQuestionResult)!
            
            if stepTypeResult.locationAnswer != nil && CLLocationCoordinate2DIsValid((stepTypeResult.locationAnswer?.coordinate)!) {
                
                let lat = stepTypeResult.locationAnswer?.coordinate.latitude
                let long = stepTypeResult.locationAnswer?.coordinate.longitude
                
                self.value = "\(lat!)" + "," + "\(long!)"
                
            } else {
                self.value = "0.0,0.0"
            }
        default:break
        }
    }
    
    
    // MARK: Setter & Getter methods for Step
    
    /* Method to Initialize step
     @step:ActivityStep instance with all properties initialized priorly
     */
    
    func setStep(step: ActivityStep)  {
        
        
        self.step = step
    }
    
    /* Method to get ActivityStep
     returns current step
     */
    
    func getStep()-> ActivityStep {
        return self.step!
    }
}

