
//  SurveyTask.swift
//  Survey-Demo
//
//  Created by Tushar on 6/14/18.
//  Copyright © 2018 Tushar. All rights reserved.
//

import Foundation
import ResearchKit

/*
public var textChoiceQuestionTask: ORKOrderedTask = {
    
    let textChoiceOneText = NSLocalizedString("Chandigarh", comment: "")
    let textChoiceTwoText = NSLocalizedString("New Delhi", comment: "")
    let textChoiceThreeText = NSLocalizedString("Bangalore", comment: "")
    
    // The text to display can be separate from the value coded for each choice:
    let textChoices = [
        ORKTextChoice(text: textChoiceOneText, value: "choice_1" as NSCoding & NSCopying & NSObjectProtocol),
        ORKTextChoice(text: textChoiceTwoText, value: "choice_2" as NSCoding & NSCopying & NSObjectProtocol),
        ORKTextChoice(text: textChoiceThreeText, value: "choice_3" as NSCoding & NSCopying & NSObjectProtocol)
    ]
   
    let answerFormat = ORKAnswerFormat.choiceAnswerFormat(with: .multipleChoice, textChoices: textChoices)
    
    //=========//
    let questionStep = ORKQuestionStep(identifier: "questionStep", title: NSLocalizedString("Text Choice", comment: ""), question: NSLocalizedString("Your question goes here.", comment: ""), answer: answerFormat)
    
    questionStep.text = NSLocalizedString("Additional text can go here.", comment: "")

    //=========//
    let newQuestionStep = ORKQuestionStep(identifier: "newQuestionStep", title: NSLocalizedString("Text Choice", comment: ""), question: NSLocalizedString("Which city you belong to?\nWhich city you belong to?", comment: ""), answer: answerFormat)
    
    newQuestionStep.text = NSLocalizedString("Additional text can go here.Additional text can go here", comment: "")

    //=========//
    let otherThirdChoice = OtherChoice(isShowOtherCell: true, isShowOtherField: true, otherTitle: "Other Title goes here", placeholder: "other things",isMandatory: false)
    
    let thirdQuestionStep = QuestionStep(identifier: "thirdQuestionStep", title: NSLocalizedString("Text Choice", comment: ""), question: NSLocalizedString("Your question goes here.", comment: ""), answer: answerFormat,otherChoice: otherThirdChoice)
    
    thirdQuestionStep.text = NSLocalizedString("Additional text can go here.", comment: "")

    //=========//
    let otherForthChoice = OtherChoice(isShowOtherCell: true, isShowOtherField: true, otherTitle: "Other Title goes here", placeholder: "other things")
    
    let forthQuestionStep = QuestionStep(identifier: "forthQuestionStep", title: NSLocalizedString("Text Choice", comment: ""), question: NSLocalizedString("Your question goes here.", comment: ""), answer: answerFormat,otherChoice: otherForthChoice)
    
    thirdQuestionStep.text = NSLocalizedString("Additional text can go here.", comment: "")
    
    return ORKOrderedTask(identifier: "textChoiceQuestionTask", steps: [questionStep, thirdQuestionStep, newQuestionStep, forthQuestionStep])
}()
*/



