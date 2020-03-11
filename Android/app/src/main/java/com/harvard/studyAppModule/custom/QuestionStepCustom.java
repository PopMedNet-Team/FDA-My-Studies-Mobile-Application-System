package com.harvard.studyAppModule.custom;

import org.researchstack.backbone.step.QuestionStep;
import org.researchstack.backbone.ui.step.layout.SurveyStepLayout;

/**
 * Created by Naveen Raj on 11/14/2016.
 */
public class QuestionStepCustom extends QuestionStep {
    public AnswerFormatCustom answerFormat;
    public String placeholder;
    private String identifier;
    private boolean addable;
    private String addmoretitle;
    private String TimeIntevalDuration;

    /**
     * Returns a new question step that includes the specified identifier.
     *
     * @param identifier The identifier of the step (a step identifier should be unique within the
     *                   task).
     */
    public QuestionStepCustom(String identifier) {
        super(identifier);
        this.identifier = identifier;
    }

    /**
     * Returns a new question step that includes the specified identifier, and title.
     *
     * @param identifier The identifier of the step (a step identifier should be unique within the
     *                   task).
     * @param title      A localized string that represents the primary text of the question.
     */
    public QuestionStepCustom(String identifier, String title) {
        super(identifier, title);
        this.identifier = identifier;
    }

    /**
     * Returns a new question step that includes the specified identifier, title, and answer
     * format.
     *
     * @param identifier The identifier of the step (a step identifier should be unique within the
     *                   task).
     * @param title      A localized string that represents the primary text of the question.
     * @param format     The format in which the answer is expected.
     */
    public QuestionStepCustom(String identifier, String title, AnswerFormatCustom format) {
        super(identifier, title);
        this.answerFormat = format;
        this.identifier = identifier;
    }

    public void setidentifier(String identifier) {
        this.identifier = identifier;
    }

    /**
     * Returns a special {@link org.researchstack.backbone.ui.step.layout.StepLayout} that is used
     * for all question steps.
     * <p>
     * This step layout uses the {@link #getStepBodyClass()} to fill in the user interaction portion
     * of the layout's UI.
     *
     * @return the StepLayout to be used for general QuestionSteps
     */
    @Override
    public Class getStepLayoutClass() {
        return SurveyStepLayout.class;
    }

    /**
     * Returns a subclass of {@link org.researchstack.backbone.ui.step.body.StepBody} responsible
     * for creating the ui for answering the question, base on the AnswerFormat.
     * <p>
     * This class is used by {@link SurveyStepLayout} to create the part of the layout where the
     * user answers the question. For example, a StepBody for a simple text question would be
     * responsible for creating an EditText for the SurveyStepLayout to place inside of its layout.
     * <p>
     * Override this method with your own StepBody implementation if you create a custom
     * QuestionStep.
     *
     * @return the StepBody implementation for this question step.
     */
    public Class<?> getStepBodyClass() {
        return answerFormat.getQuestionType().getStepBodyClass();
    }

    /**
     * Returns the format of the answer.
     * <p>
     * For example, the answer format might include the type of data to collect, the constraints to
     * place on the answer, or a list of available choices (in the case of single or multiple select
     * questions). It also provides the default {@link org.researchstack.backbone.ui.step.body.StepBody}
     * for questions of its type.
     *
     * @return the answer format for this question step
     */
    public AnswerFormatCustom getAnswerFormat1() {
        return answerFormat;
    }

    /**
     * Sets the answer format for this question step.
     *
     * @param answerFormat the answer format for this question step
     * @see #getAnswerFormat()
     */
    public void setAnswerFormat1(AnswerFormatCustom answerFormat) {
        this.answerFormat = answerFormat;
    }

    /**
     * Returns a localized string that represents the placeholder text displayed before an answer
     * has been entered.
     * <p>
     * For numeric and text-based answers, the placeholder content is displayed in the text field or
     * text area when an answer has not yet been entered.
     *
     * @return the placeholder string
     */
    public String getPlaceholder() {
        return placeholder;
    }

    /**
     * Sets a localized string that represents the placeholder text displayed before an answer has
     * been entered.
     * <p>
     * For numeric and text-based answers, the placeholder content is displayed in the text field or
     * text area when an answer has not yet been entered.
     *
     * @param placeholder the placeholder string
     */
    public void setPlaceholder(String placeholder) {
        this.placeholder = placeholder;
    }

    public String getAddmoretitle() {
        return addmoretitle;
    }

    public void setAddMoreTitle(String addMoreTitle) {
        this.addmoretitle = addMoreTitle;
    }



}
