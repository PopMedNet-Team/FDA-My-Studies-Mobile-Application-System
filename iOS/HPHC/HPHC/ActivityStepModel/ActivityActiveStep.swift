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


let kActivityStepActiveOptions = "options"
let kActivityStepActiveFormat = "format"
let kActivityStepActiveType = "type"
let kActivityStepActiveResultType = "resultType"



//Active task Api constants

//FitnessCheckFormat

let kActiveFitnessCheckWalkDuration = "walkDuration"
let kActiveFitnessCheckRestDuration = "restDuration"



//ShortWalkFormat

let kActiveShortWalkNumberOfStepsPerLeg = "numberOfStepsPerLeg"
let kActiveShortWalkRestDuration = "restDuration"



//AudioFormat

let kActiveAudioSpeechInstruction = "speechInstruction"
let kActiveAudioShortSpeechInstruction = "shortSpeechInstruction"
let kActiveAudioDuration  = "duration"

//TwoFingerTappingIntervalFormat

let kActiveTwoFingerTappingIntervalDuration = "duration"
let kActiveTwoFingerTappingIntervalHandOptions = "handOptions"

//SpatialSpanMemoryFormat

let kActiveSpatialSpanMemoryInitialSpan = "initialSpan"
let kActiveSpatialSpanMemoryMinimumSpan = "minimumSpan"
let kActiveSpatialSpanMemoryMaximumSpan = "maximumSpan"
let kActiveSpatialSpanMemoryPlaySpeed = "playSpeed"
let kActiveSpatialSpanMemoryMaximumTests = "maximumTests"
let kActiveSpatialSpanMemoryMaximumConsecutiveFailures = "maximumConsecutiveFailures"
let kActiveSpatialSpanMemoryCustomTargetImage = "customTargetImage"
let kActiveSpatialSpanMemoryCustomTargetPluralName = "customTargetPluralName"
let kActiveSpatialSpanMemoryRequireReversal = "requireReversal"


//ToneAudiometryFormat
let kActiveToneAudiometrySpeechInstruction = "speechInstruction"
let kActiveToneAudiometryShortSpeechInstruction = "shortSpeechInstruction"
let kActiveToneAudiometryToneDuration = "toneDuration"

//TowerOfHanoiFormat
let kActiveTowerOfHanoiNumberOfDisks = "numberOfDisks"


//TimedWalkFormat

let kActiveTimedWalkTistanceInMeters = "distanceInMeters"
let kActiveTimedWalkTimeLimit = "timeLimit"
let kActiveTimedWalkTurnAroundTimeLimit = "turnAroundTimeLimit"

//PSATFormat

let kActivePSATPresentationMode = "presentationMode"
let kActivePSATInterStimulusInterval = "interStimulusInterval"
let kActivePSATStimulusDuration = "stimulusDuration"
let kActivePSATSeriesLength = "seriesLength"

//TremorTestFormat

let kActiveTremorTestActiveStepDuration = "activeStepDuration"
let kActiveTremorTestActiveTaskOptions = "activeTaskOptions"
let kActiveTremorTestHandOptions = "handOptions"


//HolePegTestFormat

let kActiveHolePegTestDominantHand = "dominantHand"
let kActiveHolePegTestNumberOfPegs = "numberOfPegs"
let kActiveHolePegTestThreshold = "threshold"
let kActiveHolePegTestRotated = "rotated"
let kActiveHolePegTestTimeLimit = "timeLimit"


//FetalKickCounterFormat
let kActiveFetalKickCounterDuration = "duration"
let kActiveFetalKickCounterInstructionText = "text"
let kActiveFetalKickCounterkickCounts = "kickCount"


//Completion Text

let kActiveTaskCompletionStepText = "Tap Done to submit responses. Responses cannot be modified after submission."



enum ActiveStepType:String{
    // Active Steps.
    case audioStep = "audio"
    case fitnessStep = "fitnessCheck"
    case holePegTestStep = "holePegTest"
    case psatStep = "psat"
    case reactionTime
    case shortWalkStep = "shortWalk"
    case spatialSpanMemoryStep = "spatialSpanMemory"
    case timedWalkStep = "timedWalk"
    case timedWalkWithTurnAroundStep
    case toneAudiometryStep = "toneAudiometry"
    case towerOfHanoi = "towerOfHanoi"
    case tremorTestStep = "tremorTest"
    case twoFingerTappingIntervalStep = "twoFingerTappingInterval"
    case walkBackAndForthStep
    case kneeRangeOfMotion
    case shoulderRangeOfMotion
    case fetalKickCounter = "fetalKickCounter"
}

class ActivityActiveStep: ActivityStep {
    
    var options: ORKPredefinedTaskOption? // predefined task options
    
    var formatDict: Dictionary<String, Any>? // format dict holds data specific to question type
    
    var activeType: ActiveStepType? // specifies type of activeType
    
    override init() {
        /* default Initializer Method */
        
        super.init()
        options = nil
        formatDict = Dictionary<String, Any>()
        activeType = .audioStep
    }
    
    /* Setter method to set Activity Active Steps
     @ stepDict should  should contains all params for ActivityStep
     */
    override func initWithDict(stepDict: Dictionary<String, Any>) {
        
        if Utilities.isValidObject(someObject: stepDict as AnyObject?){
            
            super.initWithDict(stepDict: stepDict)
            
            if Utilities.isValidObject(someObject: stepDict[kActivityStepActiveOptions] as AnyObject ) {
                
                for  option: Int in (stepDict[kActivityStepActiveOptions] as? [Int])! {
                    
                    self.options?.insert(ORKPredefinedTaskOption(rawValue: UInt(option)))
                }
            } else {
                self.options = []
            }
            
            if Utilities.isValidValue(someObject: stepDict[kActivityStepActiveResultType] as AnyObject?) {
                self.activeType = ActiveStepType(rawValue: (stepDict[kActivityStepActiveResultType] as? String)!)
            }
            
            if Utilities.isValidObject(someObject: stepDict[kActivityStepActiveFormat] as AnyObject ) {
                self.formatDict = (stepDict[kActivityStepActiveFormat] as? Dictionary)!
            }
        } else {
            Logger.sharedInstance.debug("Question Step Dictionary is null:\(stepDict)")
        }
        
    }
    
    /* Method to get Active Tasks
     @returns a ORTask for active step
     */
    func getActiveTask() -> ORKTask? {
        
        if Utilities.isValidObject(someObject: self.formatDict as AnyObject?) {
            switch self.activeType! as ActiveStepType {
                
            case .audioStep : // creates an Audio step
                
                if  Utilities.isValidValue(someObject: formatDict?[kActiveAudioSpeechInstruction] as AnyObject?)
                    && Utilities.isValidValue(someObject: formatDict?[kActiveAudioShortSpeechInstruction] as AnyObject?)
                    && Utilities.isValidValue(someObject: formatDict?[kActiveAudioDuration] as AnyObject?) {
                    
                    return ORKOrderedTask.audioTask(withIdentifier: key!,
                                                    intendedUseDescription: title!,
                                                    speechInstruction: (formatDict?[kActiveAudioSpeechInstruction] as? String)!,
                                                    shortSpeechInstruction: (formatDict?[kActiveAudioShortSpeechInstruction] as? String)!,
                                                    duration: (formatDict?[kActiveAudioDuration] as? TimeInterval)!,
                                                    recordingSettings: nil,
                                                    checkAudioLevel: true, options: self.options!)
                    
                    
                } else {
                    Logger.sharedInstance.debug("audioStep:formatDict has null values:\(String(describing: formatDict))")
                    return nil
                }
                
            case .fitnessStep : // creates an fitness check step
                
                if  Utilities.isValidValue(someObject: formatDict?[kActiveFitnessCheckWalkDuration] as AnyObject?)
                    && Utilities.isValidValue(someObject: formatDict?[kActiveFitnessCheckRestDuration] as AnyObject?) {
                    
                    return ORKOrderedTask.fitnessCheck(withIdentifier: key!,
                                                       intendedUseDescription: title!,
                                                       walkDuration: (formatDict?[kActiveFitnessCheckWalkDuration] as? TimeInterval)!,
                                                       restDuration: (formatDict?[kActiveFitnessCheckRestDuration] as? TimeInterval)!,
                                                       options: self.options!)
                } else {
                    Logger.sharedInstance.debug("fitnessStep:formatDict has null values:\(String(describing: formatDict))")
                    return nil
                }
            case .holePegTestStep : // creates an hole peg test step
                
                if  Utilities.isValidValue(someObject: formatDict?[kActiveHolePegTestDominantHand] as AnyObject?)
                    && Utilities.isValidValue(someObject: formatDict?[kActiveHolePegTestNumberOfPegs] as AnyObject?)
                    && Utilities.isValidValue(someObject: formatDict?[kActiveHolePegTestThreshold] as AnyObject?)
                    && Utilities.isValidValue(someObject: formatDict?[kActiveHolePegTestRotated] as AnyObject?)
                    && Utilities.isValidValue(someObject: formatDict?[kActiveHolePegTestTimeLimit] as AnyObject?) {
                    
                    return ORKNavigableOrderedTask.holePegTest(withIdentifier: key!, intendedUseDescription: title!,
                                                               dominantHand: ORKBodySagittal(rawValue: (formatDict?[kActiveHolePegTestDominantHand] as? Int)!)!,
                                                               numberOfPegs: (formatDict?[kActiveHolePegTestNumberOfPegs] as? Int32)!,
                                                               threshold: (formatDict?[kActiveHolePegTestThreshold] as? Double)!,
                                                               rotated: ((formatDict?[kActiveHolePegTestRotated]) != nil),
                                                               timeLimit: (formatDict?[kActiveHolePegTestTimeLimit] as? TimeInterval)!,
                                                               options: self.options!)
                } else {
                    Logger.sharedInstance.debug("holePegTestStep:formatDict has null values:\(String(describing: formatDict))")
                    return nil
                }
            case .psatStep : // creates an PSAT Step
                
                if  Utilities.isValidValue(someObject: formatDict?[kActivePSATPresentationMode] as AnyObject?)
                    && Utilities.isValidValue(someObject: formatDict?[kActivePSATInterStimulusInterval] as AnyObject?)
                    &&  Utilities.isValidValue(someObject: formatDict?[kActivePSATStimulusDuration] as AnyObject?)
                    &&  Utilities.isValidValue(someObject: formatDict?[kActivePSATSeriesLength] as AnyObject?) {
                    
                    return ORKOrderedTask.psatTask(withIdentifier: key!,
                                                   intendedUseDescription: title!,
                                                   presentationMode: ORKPSATPresentationMode(rawValue:(formatDict?[kActivePSATPresentationMode] as? Int)!),
                                                   interStimulusInterval: (formatDict?[kActivePSATInterStimulusInterval] as? TimeInterval)!,
                                                   stimulusDuration: (formatDict?[kActivePSATStimulusDuration] as? TimeInterval)!,
                                                   seriesLength: (formatDict?[kActivePSATSeriesLength]  as? Int)!,
                                                   options: self.options!)
                    
                    
                } else {
                    Logger.sharedInstance.debug("psatStep:formatDict has null values:\(String(describing: formatDict))")
                    return nil
                }
            case .shortWalkStep: // creates an short walk step
                
                if  Utilities.isValidValue(someObject: formatDict?[kActiveShortWalkNumberOfStepsPerLeg] as AnyObject?)
                    && Utilities.isValidValue(someObject: formatDict?[kActiveShortWalkRestDuration] as AnyObject?)
                {
                    
                    return ORKOrderedTask.shortWalk(withIdentifier: key!, intendedUseDescription: title!, numberOfStepsPerLeg: (formatDict?[kActiveShortWalkNumberOfStepsPerLeg] as? Int)!, restDuration: (formatDict?[kActiveShortWalkRestDuration] as? TimeInterval)! , options: self.options!)
                    
                } else {
                    Logger.sharedInstance.debug("shortWalkStep:formatDict has null values:\(String(describing: formatDict))")
                    return nil
                }
            case .spatialSpanMemoryStep : // creates a spatial span memory step
                
                if  Utilities.isValidValue(someObject: formatDict?[kActiveSpatialSpanMemoryInitialSpan] as AnyObject?)
                    && Utilities.isValidValue(someObject: formatDict?[kActiveSpatialSpanMemoryMinimumSpan] as AnyObject?)
                    && Utilities.isValidValue(someObject: formatDict?[kActiveSpatialSpanMemoryMaximumSpan] as AnyObject?)
                    && Utilities.isValidValue(someObject: formatDict?[kActiveSpatialSpanMemoryPlaySpeed] as AnyObject?)
                    && Utilities.isValidValue(someObject: formatDict?[kActiveSpatialSpanMemoryMaximumTests] as AnyObject?)
                    && Utilities.isValidValue(someObject: formatDict?[kActiveSpatialSpanMemoryMaximumConsecutiveFailures] as AnyObject?)
                    && Utilities.isValidValue(someObject: formatDict?[kActiveSpatialSpanMemoryRequireReversal] as AnyObject?) {
                    
                    let image = UIImage(named: (formatDict?[kActiveSpatialSpanMemoryCustomTargetImage] as? String)!)
                    
                    let initialSpan = (formatDict?[kActiveSpatialSpanMemoryInitialSpan] as? Int)
                    let minimumSpan = (formatDict?[kActiveSpatialSpanMemoryMinimumSpan] as? Int)
                    let maximumSpan = (formatDict?[kActiveSpatialSpanMemoryMaximumSpan] as? Int)
                    let playSpeed = (formatDict?[kActiveSpatialSpanMemoryPlaySpeed] as? Float)
                    let maximumTest = (formatDict?[kActiveSpatialSpanMemoryMaximumTests] as? Int)
                    let maximumConsecutiveFailures = (formatDict?[kActiveSpatialSpanMemoryMaximumConsecutiveFailures] as? Int)
                  
                  var customPluralName:String? = "flowers" //Default Flowers are used
                  
                  if Utilities.isValidValue(someObject: formatDict?[kActiveSpatialSpanMemoryCustomTargetPluralName]  as AnyObject) {
                    
                    customPluralName = (formatDict?[kActiveSpatialSpanMemoryCustomTargetPluralName] as? String)!
                    
                  }
                    //Initilize Spatial Span Memory task only if matches the following criteria
                    if initialSpan! >= 2
                        && initialSpan! >= minimumSpan!
                        && initialSpan! <= maximumSpan!
                        && maximumSpan! <= 20
                        && playSpeed! > 0.4
                        && playSpeed! < 21.0
                        && maximumTest! >= 1
                        && maximumConsecutiveFailures! >= 1 {
                
                    let orderedTask =  ORKOrderedTask.spatialSpanMemoryTask(withIdentifier:  key!, intendedUseDescription:
                        self.text!,
                                                                initialSpan: (formatDict?[kActiveSpatialSpanMemoryInitialSpan] as? Int)!,
                                                                minimumSpan: (formatDict?[kActiveSpatialSpanMemoryMinimumSpan] as? Int)!,
                                                                maximumSpan: (formatDict?[kActiveSpatialSpanMemoryMaximumSpan] as? Int)!,
                                                                playSpeed: (formatDict?[kActiveSpatialSpanMemoryPlaySpeed] as? TimeInterval)!,
                                                                maximumTests: (formatDict?[kActiveSpatialSpanMemoryMaximumTests] as? Int)!,
                                                                maximumConsecutiveFailures: (formatDict?[kActiveSpatialSpanMemoryMaximumConsecutiveFailures] as? Int)!,
                                                                customTargetImage: image ,
                                                                customTargetPluralName: customPluralName,
                                                                requireReversal: ((formatDict?[kActiveSpatialSpanMemoryRequireReversal]) as? Bool)! ,
                                                                options: self.options!)
                      
                      (orderedTask.steps.last as? ORKCompletionStep)!.text = NSLocalizedString(kActiveTaskCompletionStepText, comment: "")
                      return orderedTask
                        
                    } else {
                        return nil
                    }
                    
                } else {
                    Logger.sharedInstance.debug("spatialSpanMemoryStep:formatDict has null values:\(String(describing: formatDict))")
                    return nil
                }
            case .timedWalkStep : // creates a timed walk step
                
                if  Utilities.isValidValue(someObject: formatDict?[kActiveTimedWalkTistanceInMeters] as AnyObject?)
                    && Utilities.isValidValue(someObject: formatDict?[kActiveTimedWalkTimeLimit] as AnyObject?)
                    &&  Utilities.isValidValue(someObject: formatDict?[kActiveTimedWalkTurnAroundTimeLimit] as AnyObject?) {
                    // includeAssistiveDeviceForm set to false by default
                    
                    return ORKOrderedTask.timedWalk(withIdentifier: key!,
                                                    intendedUseDescription: title!,
                                                    distanceInMeters: (formatDict?[kActiveTimedWalkTistanceInMeters] as? Double)!,
                                                    timeLimit: (formatDict?[kActiveTimedWalkTimeLimit] as? TimeInterval)!,
                                                    turnAroundTimeLimit: (formatDict?[kActiveTimedWalkTurnAroundTimeLimit] as? TimeInterval)!,
                                                    includeAssistiveDeviceForm: false,
                                                    options: self.options!)
                    
                } else {
                    Logger.sharedInstance.debug("timedWalkStep:formatDict has null values:\(String(describing: formatDict))")
                    return nil
                }
            case .toneAudiometryStep : // creates a tone audiometry step
                
                if  Utilities.isValidValue(someObject: formatDict?[kActiveToneAudiometrySpeechInstruction] as AnyObject?)
                    && Utilities.isValidValue(someObject: formatDict?[kActiveToneAudiometryShortSpeechInstruction] as AnyObject?)
                    &&  Utilities.isValidValue(someObject: formatDict?[kActiveToneAudiometryToneDuration] as AnyObject?) {
                    
                    return ORKOrderedTask.toneAudiometryTask(withIdentifier:  key!,
                                                             intendedUseDescription: title!,
                                                             speechInstruction: (formatDict?[kActiveToneAudiometrySpeechInstruction] as? String)! ,
                                                             shortSpeechInstruction: (formatDict?[kActiveToneAudiometryShortSpeechInstruction] as? String)!,
                                                             toneDuration: (formatDict?[kActiveToneAudiometryToneDuration] as? TimeInterval)!,
                                                             options: self.options!)
                    
                } else {
                    Logger.sharedInstance.debug("toneAudiometryStep:formatDict has null values:\(String(describing: formatDict))")
                    return nil
                }
            case .towerOfHanoi : // creates a tower of honoi step
                
                if  Utilities.isValidValue(someObject: formatDict?[kActiveTowerOfHanoiNumberOfDisks] as AnyObject?) {
                    let orderedTask =  ORKOrderedTask.towerOfHanoiTask(withIdentifier: key!,
                                                           intendedUseDescription: self.text!,
                                                           numberOfDisks: (formatDict?[kActiveTowerOfHanoiNumberOfDisks] as? UInt)! ,
                                                           options: self.options!)
                  (orderedTask.steps.last as? ORKCompletionStep)!.text = NSLocalizedString(kActiveTaskCompletionStepText, comment: "")
                  return orderedTask
                    
                } else {
                    Logger.sharedInstance.debug("towerOfHanoi:formatDict has null values:\(String(describing: formatDict))")
                    return nil
                }
            case .twoFingerTappingIntervalStep : // creates a two finger tapping step
                
                if  Utilities.isValidValue(someObject: formatDict?[kActiveTwoFingerTappingIntervalDuration] as AnyObject?)
                    &&  Utilities.isValidValue(someObject: formatDict?[kActiveTwoFingerTappingIntervalHandOptions] as AnyObject?) {
                    return ORKOrderedTask.twoFingerTappingIntervalTask(withIdentifier: key!,
                                                                       intendedUseDescription: title!,
                                                                       duration: (formatDict?[kActiveTwoFingerTappingIntervalDuration] as? TimeInterval)!,
                                                                       handOptions: ORKPredefinedTaskHandOption(rawValue:(formatDict?[kActiveTwoFingerTappingIntervalHandOptions] as? UInt)!),
                                                                       options: self.options!)
                    
                } else {
                    Logger.sharedInstance.debug("twoFingerTappingIntervalStep:formatDict has null values:\(String(describing: formatDict))")
                    return nil
                }
                
            case .tremorTestStep : // creates a tremor test step
                if  Utilities.isValidValue(someObject: formatDict?[kActiveTremorTestActiveStepDuration] as AnyObject?)
                    &&  Utilities.isValidValue(someObject: formatDict?[kActiveTremorTestHandOptions] as AnyObject?) {
                    
                    var activeOptions:ORKTremorActiveTaskOption?
                    if Utilities.isValidObject(someObject: formatDict?[kActivityStepActiveOptions] as AnyObject ){
                        activeOptions = nil
                        for  option:Int in (formatDict?[kActivityStepActiveOptions] as? [Int])! {
                            
                            activeOptions?.insert(ORKTremorActiveTaskOption(rawValue: UInt(option)))
                            
                        }
                        
                    } else {
                        activeOptions = []
                    }
                    
                    return ORKOrderedTask.tremorTest(withIdentifier: key!,
                                                     intendedUseDescription: title!,
                                                     activeStepDuration: (formatDict?[kActiveTremorTestActiveStepDuration] as? TimeInterval)!,
                                                     activeTaskOptions: activeOptions! ,
                                                     handOptions: ORKPredefinedTaskHandOption(rawValue: (formatDict?[kActiveTremorTestHandOptions] as? UInt)!),
                                                     options: self.options!)
                } else {
                    Logger.sharedInstance.debug("twoFingerTappingIntervalStep:formatDict has null values:\(String(describing: formatDict))")
                    return nil
                    
                }
                
            case .fetalKickCounter : // creates a fetal kick counter step
                
                if  Utilities.isValidValue(someObject: formatDict?[kActiveFetalKickCounterDuration] as AnyObject?)
{
                    let instructionText = self.text!
                    
                    let fetalKickTask:FetalKickCounterTask? = FetalKickCounterTask()
                    fetalKickTask?.initWithFormat(duration: Float((formatDict?[kActiveFetalKickCounterDuration] as? Int)!), identifier: self.key!, instructionText: instructionText as? String)
                    
                    if Utilities.isValidValue(someObject: formatDict?[kActiveFetalKickCounterkickCounts] as AnyObject ) {
                        fetalKickTask?.maxKickCounts =  (formatDict?[kActiveFetalKickCounterkickCounts] as? Int)!
                        
                    } else {
                    //Default Fetal Kicks
                      fetalKickTask?.maxKickCounts = 200
                  }
                    return fetalKickTask?.getTask()
                } else {
                    Logger.sharedInstance.debug("fetalKickCounter:formatDict has null values:\(String(describing: formatDict))")
                    return nil
                }
            
            default:
                Logger.sharedInstance.debug("Case Mismatch:Default Executed null values:\(String(describing: formatDict))")
                return nil
                
            }
        } else {
            Logger.sharedInstance.debug("Format Dict have null values:\(String(describing: formatDict))")
            return nil
        }
    }
}
