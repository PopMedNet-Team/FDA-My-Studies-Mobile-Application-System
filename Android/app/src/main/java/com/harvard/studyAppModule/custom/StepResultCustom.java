package com.harvard.studyAppModule.custom;

import org.researchstack.backbone.answerformat.AnswerFormat;
import org.researchstack.backbone.result.Result;
import org.researchstack.backbone.result.StepResult;
import org.researchstack.backbone.step.QuestionStep;
import org.researchstack.backbone.step.Step;

import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Created by Naveen Raj on 07/17/2017.
 */

public class StepResultCustom <T> extends Result
{
    /**
     * When StepResult only has a single value, pair that value with the following key
     */
    public static final String DEFAULT_KEY = "answer";

    private LinkedHashMap<String, StepResult> results;

    private AnswerFormat answerFormat;

    /**
     * Creates a StepResult from a {@link Step}.
     * <p>
     * Using this constructor ensures that the StepResult has the correct identifier and answer
     * format for the corresponding step.
     *
     * @param step the step from which to create the StepResult
     */
    public StepResultCustom(Step step)
    {
        super(step.getIdentifier());
        this.results = new LinkedHashMap<>();

        if(step instanceof QuestionStep)
        {
            answerFormat = ((QuestionStep) step).getAnswerFormat();
        }
        setStartDate(new Date());
        // this will be updated when the result is set
        setEndDate(new Date());
    }

    public Map<String, StepResult> getResults()
    {
        return results;
    }

    public void setResults(LinkedHashMap<String, StepResult> results)
    {
        this.results = results;
    }

    /**
     * Returns the result stored using {@link #setResult}.
     *
     * @return the result with the default identifier
     */
    public StepResult getResult()
    {
        return getResultForIdentifier(DEFAULT_KEY);
    }

    /**
     * Sets the result using the default key, useful when there is only a single result.
     *
     * @param result the result to save with the default key
     */
    public void setResult(StepResult result)
    {
        setResultForIdentifier(DEFAULT_KEY, result);
        setEndDate(new Date());
    }

    /**
     * Returns the result for the given identifier, use this when there are multiple results for the
     * step.
     *
     * @param identifier the identifier used as the key for storing this result
     * @return the result for the given identifier
     */
    public StepResult getResultForIdentifier(String identifier)
    {
        return results.get(identifier);
    }

    /**
     * Sets the result for the given identifier, use when there are multiple results for the step.
     * <p>
     * If there is only one result, use the {@link #setResult} convenience method instead.
     *
     * @param identifier the identifier for the result
     * @param result     the result to save
     */
    public void setResultForIdentifier(String identifier, StepResult result)
    {
        results.put(identifier, result);
    }

    /**
     * Gets the {@link AnswerFormat} for this step result. May be useful when processing the
     * result.
     *
     * @return the answer format associated with the step
     */
    public AnswerFormat getAnswerFormat()
    {
        return answerFormat;
    }
}
