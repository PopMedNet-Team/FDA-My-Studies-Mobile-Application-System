package com.harvard.studyAppModule.custom.question;

import android.util.Log;

import com.harvard.R;
import com.harvard.studyAppModule.custom.AnswerFormatCustom;

import org.researchstack.backbone.ui.step.body.BodyAnswer;
import org.researchstack.backbone.utils.FormatHelper;

import java.text.SimpleDateFormat;
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

    //Before used FormatHelper.SIMPLE_FORMAT_DATE Now FormatHelper.DEFAULT_FORMAT
    public BodyAnswer validateAnswer(Date resultDate) {
        SimpleDateFormat simpleDateFormat;
        if (style == DateAnswerStyle.Date)
            simpleDateFormat = FormatHelper.SIMPLE_FORMAT_DATE;
        else
            simpleDateFormat = FormatHelper.DEFAULT_FORMAT;

        if (dateRange.equalsIgnoreCase("") || dateRange.equalsIgnoreCase("custom")) {
            if (minimumDate != null && resultDate.getTime() < minimumDate.getTime()) {
                return new BodyAnswer(false,
                        R.string.rsb_invalid_answer_date_under,
                        simpleDateFormat.format(minimumDate));
            }

            if (maximumDate != null && resultDate.getTime() > maximumDate.getTime()) {
                return new BodyAnswer(false,
                        R.string.rsb_invalid_answer_date_over,
                        simpleDateFormat.format(maximumDate));
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
                        simpleDateFormat.format(currentCalendar.getTime()));
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
                        simpleDateFormat.format(currentCalendar.getTime()));
            }
        }

        return BodyAnswer.VALID;
    }

}
