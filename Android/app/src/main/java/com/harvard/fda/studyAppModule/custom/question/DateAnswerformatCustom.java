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

package com.harvard.fda.studyAppModule.custom.question;

import android.util.Log;

import com.harvard.fda.R;
import com.harvard.fda.studyAppModule.custom.AnswerFormatCustom;

import org.researchstack.backbone.ui.step.body.BodyAnswer;
import org.researchstack.backbone.utils.FormatHelper;

import java.util.Calendar;
import java.util.Date;

/**
 * Created by Naveen Raj on 06/28/2017.
 */

public class DateAnswerformatCustom extends AnswerFormatCustom {

    private DateAnswerStyle style;

    private Date defaultDate;

    private Date minimumDate;

    private Date maximumDate;

    private String dateRange = "custom";

    public DateAnswerformatCustom(DateAnswerStyle style) {
        this.style = style;
    }

    public DateAnswerformatCustom(DateAnswerStyle style, Date defaultDate, Date minimumDate, Date maximumDate, String dateRange) {
        this.style = style;
        this.defaultDate = defaultDate;
        this.minimumDate = minimumDate;
        this.maximumDate = maximumDate;
        if (dateRange != null)
            this.dateRange = dateRange;
    }

    /**
     * Returns the style of date entry.
     *
     * @return the style of the date entry
     */
    public DateAnswerStyle getStyle() {
        return style;
    }

    /**
     * Returns the date to use as the default.
     * <p>
     * The date is displayed in the user's time zone. When the value of this property is
     * <code>null</code>, the current time is used as the default.
     *
     * @return the default date for the date picker presented to the user, or null
     */
    public Date getDefaultDate() {
        return defaultDate;
    }

    /**
     * Returns the minimum allowed date.
     * <p>
     * When the value of this property is <code>null</code>, there is no minimum.
     *
     * @return returns the minimum allowed date, or null
     */
    public Date getMinimumDate() {
        return minimumDate;
    }

    /**
     * The maximum allowed date.
     * <p>
     * When the value of this property is <code>null</code>, there is no maximum.
     *
     * @return returns the maximum allowed date, or null
     */
    public Date getMaximumDate() {
        return maximumDate;
    }

    @Override
    public AnswerFormatCustom.QuestionType getQuestionType() {
        if (style == DateAnswerStyle.Date) return Type.Date;
        if (style == DateAnswerStyle.DateAndTime) return Type.DateAndTime;
        if (style == DateAnswerStyle.TimeOfDay) return Type.TimeOfDay;

        return Type.None;
    }

    public BodyAnswer validateAnswer(Date resultDate) {
        if (dateRange.equalsIgnoreCase("") || dateRange.equalsIgnoreCase("custom")) {
            if (minimumDate != null && resultDate.getTime() < minimumDate.getTime()) {
                return new BodyAnswer(false,
                        R.string.rsb_invalid_answer_date_under,
                        FormatHelper.SIMPLE_FORMAT_DATE.format(minimumDate));
            }

            if (maximumDate != null && resultDate.getTime() > maximumDate.getTime()) {
                return new BodyAnswer(false,
                        R.string.rsb_invalid_answer_date_over,
                        FormatHelper.SIMPLE_FORMAT_DATE.format(maximumDate));
            }
        } else if (dateRange.equalsIgnoreCase("untilCurrent")) {
            Calendar resultsCalendar = Calendar.getInstance();
            Calendar currentCalendar = Calendar.getInstance();
            if (style == DateAnswerStyle.Date) {
                resultsCalendar.setTime(resultDate);
                resultsCalendar.set(Calendar.HOUR_OF_DAY, 0);
                resultsCalendar.set(Calendar.MINUTE, 0);
                resultsCalendar.set(Calendar.SECOND, 0);
                resultsCalendar.set(Calendar.MILLISECOND, 0);

                currentCalendar.set(Calendar.HOUR_OF_DAY, 0);
                currentCalendar.set(Calendar.MINUTE, 0);
                currentCalendar.set(Calendar.SECOND, 0);
                currentCalendar.set(Calendar.MILLISECOND, 0);
            } else if (style == DateAnswerStyle.DateAndTime) {
                resultsCalendar.setTime(resultDate);
            }

            if (resultsCalendar.getTime().equals(currentCalendar.getTime()) || resultsCalendar.getTime().before(currentCalendar.getTime())) {
                return BodyAnswer.VALID;
            } else {
                return new BodyAnswer(false,
                        R.string.rsb_invalid_answer_date_over,
                        FormatHelper.SIMPLE_FORMAT_DATE.format(currentCalendar.getTime()));
            }
        } else if (dateRange.equalsIgnoreCase("afterCurrent")) {
            Calendar resultsCalendar = Calendar.getInstance();
            Calendar currentCalendar = Calendar.getInstance();

            if (style == DateAnswerStyle.Date) {
                resultsCalendar.setTime(resultDate);
                resultsCalendar.set(Calendar.HOUR_OF_DAY, 0);
                resultsCalendar.set(Calendar.MINUTE, 0);
                resultsCalendar.set(Calendar.SECOND, 0);
                resultsCalendar.set(Calendar.MILLISECOND, 0);

                currentCalendar.set(Calendar.HOUR_OF_DAY, 0);
                currentCalendar.set(Calendar.MINUTE, 0);
                currentCalendar.set(Calendar.SECOND, 0);
                currentCalendar.set(Calendar.MILLISECOND, 0);
            } else if (style == DateAnswerStyle.DateAndTime) {
                resultsCalendar.setTime(resultDate);
            }

            if (resultsCalendar.getTime().after(currentCalendar.getTime())) {
                return BodyAnswer.VALID;
            } else {
                return new BodyAnswer(false,
                        R.string.rsb_invalid_answer_date_under,
                        FormatHelper.SIMPLE_FORMAT_DATE.format(currentCalendar.getTime()));
            }
        }

        return BodyAnswer.VALID;
    }

}
