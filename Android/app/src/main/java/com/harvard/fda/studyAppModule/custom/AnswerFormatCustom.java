/*
 * Copyright © 2017-2019 Harvard Pilgrim Health Care Institute (HPHCI) and its Contributors.
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and
 * associated documentation files (the "Software"), to deal in the Software without restriction, including
 * without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial
 * portions of the Software.
 *
 * Funding Source: Food and Drug Administration (“Funding Agency”) effective 18 September 2014 as Contract no. HHSF22320140030I/HHSF22301006T (the “Prime Contract”).
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NON-INFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package com.harvard.fda.studyAppModule.custom;

import com.harvard.fda.studyAppModule.custom.activeTask.StepCountClass;
import com.harvard.fda.studyAppModule.custom.activeTask.Tappingactivity;
import com.harvard.fda.studyAppModule.custom.question.AudioQuestionbody;
import com.harvard.fda.studyAppModule.custom.question.ContinuousScaleQuestion;
import com.harvard.fda.studyAppModule.custom.question.CustomDateQuestionBody;
import com.harvard.fda.studyAppModule.custom.question.DecimalUnitQuestionBody;
import com.harvard.fda.studyAppModule.custom.question.EmailQuestion;
import com.harvard.fda.studyAppModule.custom.question.FormBodyCustom;
import com.harvard.fda.studyAppModule.custom.question.HeightQuestion;
import com.harvard.fda.studyAppModule.custom.question.IntegerUnitQuestionBody;
import com.harvard.fda.studyAppModule.custom.question.LocationQuestion;
import com.harvard.fda.studyAppModule.custom.question.MultiChoiceImageQuestionBody;
import com.harvard.fda.studyAppModule.custom.question.MultiChoiceTextQuestionBody;
import com.harvard.fda.studyAppModule.custom.question.ScaleQuestion;
import com.harvard.fda.studyAppModule.custom.question.ScaleTextQuestion;
import com.harvard.fda.studyAppModule.custom.question.SingleChoiceTextQuestionBody;
import com.harvard.fda.studyAppModule.custom.question.TaskInstructionStep;
import com.harvard.fda.studyAppModule.custom.question.TaskIntroductionStep;
import com.harvard.fda.studyAppModule.custom.question.TextQuestionRegexBody;
import com.harvard.fda.studyAppModule.custom.question.TimeIntervalQuestion;
import com.harvard.fda.studyAppModule.custom.question.valuePickerQuestion;

import org.researchstack.backbone.answerformat.AnswerFormat;
import org.researchstack.backbone.ui.step.body.NotImplementedStepBody;

import java.io.Serializable;

/**
 * Created by Naveen Raj on 10/26/2016.
 */
public class AnswerFormatCustom implements Serializable {
    /**
     * Default constructor. The appropriate subclass of AnswerFormat should be used instead of this
     * directly.
     */
    public AnswerFormatCustom() {
    }

    /**
     * Returns the QuestionType for this answer format. Implement this in your subclass.
     *
     * @return the question type
     */
    public AnswerFormatCustom.QuestionType getQuestionType() {
        return Type.None;
    }

    /**
     * Interface that {@link AnswerFormat.Type} implements. Since you cannot add a value to an existing enum, you
     * may implement this interface instead to provide your own QuestionType that provides a {@link
     * org.researchstack.backbone.ui.step.body.StepBody} class.
     */
    public interface QuestionType {
        Class<?> getStepBodyClass();
    }

    /**
     * The type of question. (read-only)
     * <p>
     * The type provides a default {@link org.researchstack.backbone.ui.step.body.StepBody} for that
     * type of question. A custom StepLayout implementation may provide it's own StepBody rather
     * than using the default provided by this AnswerFormat.
     */
    public enum Type implements QuestionType {
        None(NotImplementedStepBody.class),
        MultipleImageChoice(MultiChoiceImageQuestionBody.class),
        MultipleTextChoice(MultiChoiceTextQuestionBody.class),
        SingleTextChoice(SingleChoiceTextQuestionBody.class),
        Audio(AudioQuestionbody.class),
        stepcount(StepCountClass.class),
        Tapping(Tappingactivity.class),
        spatialSpanMemory(NotImplementedStepBody.class),
        valuePicker(valuePickerQuestion.class),
        Scale(ScaleQuestion.class),
        ScaleText(ScaleTextQuestion.class),
        Location(LocationQuestion.class),
        ContinousScale(ContinuousScaleQuestion.class),
        Height(HeightQuestion.class),
        TimeInterval(TimeIntervalQuestion.class),
        TextRegex(TextQuestionRegexBody.class),
        Email(EmailQuestion.class),
        TaskIntroStep(TaskIntroductionStep.class),
        TaskinstructionStep(TaskInstructionStep.class),
        Integer(IntegerUnitQuestionBody.class),
        Decimal(DecimalUnitQuestionBody.class),
        Date(CustomDateQuestionBody.class),
        DateAndTime(CustomDateQuestionBody.class),
        TimeOfDay(CustomDateQuestionBody.class),
        Form(FormBodyCustom.class);

        private Class<?> stepBodyClass;

        Type(Class<?> stepBodyClass) {
            this.stepBodyClass = stepBodyClass;
        }

        @Override
        public Class<?> getStepBodyClass() {
            return stepBodyClass;
        }

    }

    /**
     * The style of the question (that is, single or multiple choice).
     */
    public enum CustomAnswerStyle {
        MultipleImageChoice,
        MultipleTextChoice,
        SingleTextChoice,
        Audio,
        Tapping,
        stepcount,
        valuePicker,
        Scale,
        ScaleText,
        ContinousScale,
        TimeofDay,
        TimeInterval,
        Height,
        Location,
        spatialSpanMemory,
        TextRegex,
        Email,
        TaskIntroStep,
        TaskinstructionStep,
        Integer,
        Decimal,
        Form;
    }

    public enum DateAnswerStyle
    {
        DateAndTime,
        Date,
        TimeOfDay
    }

}
