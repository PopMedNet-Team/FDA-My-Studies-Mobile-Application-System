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


let kFetalKickInstructionStepIdentifier = "Instruction"
let kFetalKickInstructionStepTitle = "Fetal Kick Counter"
let kFetalKickInstructionStepText = "This task needs you to record the number of times you experience fetal kicks in a given duration of time.Also called as the Fetal Kick Counter task, this will help assess the activity of the baby within."


let kFetalKickIntroductionStepIdentifier = "FetalKickIntroduction"
let kFetalKickIntroductionStepTitle = ""
let kFetalKickIntroductionStepText = "This task needs you to record the number of times you experience fetal kicks in a given duration of time.Also called as the Fetal Kick Counter task, this will help assess the activity of the baby within."


let kFetalKickCounterStepIdentifier = "FetalKickCounterStep"

let kFetalKickCounterStepCompletionTitle = "CompletionStep"
let kFetalKickCounterStepCompletionText = "Thank you for your time!"

let kFetalKickCounterTaskIdentifier = "FetalKickCounterTask"


class FetalKickCounterTask {
    
    var duration: Float?       // task run time
    var identifier: String?
    var steps: [ORKStep]?    // steps involved in fetal kick
    var instructionText: String?
    var maxKickCounts: Int?
    /*
     Default Initializer method
     */
    init() {
        self.steps =  [ORKStep]()
        self.identifier = kFetalKickCounterStepIdentifier
        self.duration = 0
        self.instructionText = ""
        self.maxKickCounts = 0
    }
    
    
    /*
     Initalizer method to create instance
     @param duration    is task run time in hours
     */
    func initWithFormat(duration: Float,identifier: String,instructionText: String?)  {
        
        self.identifier = identifier
        self.steps =  [ORKStep]()
        if duration > 0.0{
            self.duration =  duration
            
        } else {
            self.duration = 50
            Logger.sharedInstance.warn("Duration is null:\(duration)")
        }
        
        if Utilities.isValidValue(someObject: instructionText as AnyObject?) {
             self.instructionText = instructionText
            
        } else {
             self.instructionText = ""
        }
        self.maxKickCounts = 0
    }
    
    /* setter method to set totalKickCounts
    */
    func setMaxKicks(maxKicks: Int) {
        self.maxKickCounts = maxKicks
    }
    
    
    /*
     Getter method to create fetalKickTask
     @returns OrkorderedTask    containing steps
     */
    func getTask() -> ORKOrderedTask {
        
        //create a Intro step
        let introStep = FetalKickIntroStep(identifier: kFetalKickIntroductionStepIdentifier)
        introStep.introTitle =  NSLocalizedString(kFetalKickInstructionStepTitle, comment: "")
      
      if (self.instructionText?.count)! > 0 {
        introStep.subTitle = NSLocalizedString(self.instructionText!, comment: "")
       
      } else {
         introStep.subTitle = NSLocalizedString(kFetalKickInstructionStepText, comment: "")
      }
      
        introStep.displayImage = #imageLiteral(resourceName: "task_img1")
        
        steps?.append(introStep)
      
        //create a Fetal Kick Counter Step
        let kickStep = FetalKickCounterStep(identifier: self.identifier!)
        kickStep.counDownTimer = Int(self.duration! )
        
        kickStep.totalCounts = self.maxKickCounts
        kickStep.stepDuration = 30
        kickStep.shouldShowDefaultTimer = false
        kickStep.shouldStartTimerAutomatically = true
        kickStep.shouldContinueOnFinish = true
        kickStep.shouldUseNextAsSkipButton = false
        
        steps?.append(kickStep)
        
        //create a Completion Step
        let summaryStep = ORKCompletionStep(identifier: kFetalKickCounterStepCompletionTitle)
        summaryStep.title = "Activity Completed"
        summaryStep.image = #imageLiteral(resourceName: "successBlueBig")
        summaryStep.detailText = "Thank you for your time!\n\nTap Done to submit responses. Responses cannot be modified after submission."
       
        steps?.append(summaryStep)
        
        return ORKOrderedTask(identifier: kFetalKickCounterTaskIdentifier, steps: steps)
    }
    
}
