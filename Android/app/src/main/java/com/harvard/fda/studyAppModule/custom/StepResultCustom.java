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
