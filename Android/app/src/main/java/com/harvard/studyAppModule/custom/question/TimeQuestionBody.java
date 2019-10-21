package com.harvard.studyAppModule.custom.question;

import android.app.TimePickerDialog;
import android.content.res.Resources;
import android.support.v7.view.ContextThemeWrapper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TimePicker;

import com.harvard.R;

import org.researchstack.backbone.answerformat.DateAnswerFormat;
import org.researchstack.backbone.result.StepResult;
import org.researchstack.backbone.step.QuestionStep;
import org.researchstack.backbone.step.Step;
import org.researchstack.backbone.ui.step.body.BodyAnswer;
import org.researchstack.backbone.ui.step.body.StepBody;
import org.researchstack.backbone.utils.FormatHelper;

import java.text.DateFormat;
import java.util.Calendar;


/**
 * Created by Naveen Raj on 01/23/2017.
 */


public class TimeQuestionBody implements StepBody {
    //-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*
    // Static Fields
    //-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*
    private static final DateFormat DATE_FORMAT = FormatHelper.getFormat(DateFormat.MEDIUM,
            FormatHelper.NONE);

    //-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*
    // Constructor Fields
    //-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*
    private QuestionStep step;
    private StepResult<String> result;
    private DateAnswerFormat format;
    private Calendar calendar;
    TextView time;
    private boolean hasChosenTime;
    private String savedTime;

    public TimeQuestionBody(Step step, StepResult result) {
        this.step = (QuestionStep) step;
        this.result = result == null ? new StepResult<>(step) : result;
        this.format = (DateAnswerFormat) this.step.getAnswerFormat();
        this.calendar = Calendar.getInstance();

        // First check the result and restore last picked date
        savedTime = this.result.getResult();
        if (savedTime != null) {
            hasChosenTime = true;
        } else {
            hasChosenTime = false;
        }
    }

    @Override
    public View getBodyView(int viewType, LayoutInflater inflater, ViewGroup parent) {
        View view = inflater.inflate(org.researchstack.backbone.R.layout.rsb_item_date_view, parent, false);

        TextView title = (TextView) view.findViewById(org.researchstack.backbone.R.id.label);
        if (viewType == VIEW_TYPE_COMPACT) {
            title.setText(step.getTitle());
        } else {
            title.setVisibility(View.GONE);
        }

        time = (TextView) view.findViewById(org.researchstack.backbone.R.id.value);
        time.setSingleLine(true);
        if (step.getPlaceholder() != null) {
            time.setHint(step.getPlaceholder());
        } else {
            time.setHint(R.string.enter_a_time);
        }

        if (hasChosenTime) {
            time.setText(savedTime);
        }

        time.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    TimeQuestionBody.this.showDialog(time, savedTime);
                }
            }
        });

        time.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (v.isFocused()) {
                    TimeQuestionBody.this.showDialog(time, savedTime);
                }
            }
        });

        Resources res = parent.getResources();
        LinearLayout.MarginLayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutParams.leftMargin = res.getDimensionPixelSize(org.researchstack.backbone.R.dimen.rsb_margin_left);
        layoutParams.rightMargin = res.getDimensionPixelSize(org.researchstack.backbone.R.dimen.rsb_margin_right);
        view.setLayoutParams(layoutParams);

        return view;
    }

    @Override
    public StepResult getStepResult(boolean skipped) {
        if (skipped) {
            result.setResult(null);
        } else {
            result.setResult(time.getText().toString());
        }

        return result;
    }

    /**
     * @return {@link BodyAnswer#VALID} if result date is between min and max (inclusive) date set
     * within the Step.AnswerFormat
     */
    @Override
    public BodyAnswer getBodyAnswerState() {
        /*Date minDate = format.getMinimumDate();
        Date maxDate = format.getMaximumDate();
        Date resultDate = calendar.getTime();

        if (!hasChosenTime) {
            return new BodyAnswer(false, org.researchstack.backbone.R.string.rsb_invalid_answer_default);
        }

        if (minDate != null && resultDate.getTime() < minDate.getTime()) {
            return new BodyAnswer(false, org.researchstack.backbone.R.string.rsb_invalid_answer_date_under);
        }

        if (maxDate != null && resultDate.getTime() > maxDate.getTime()) {
            return new BodyAnswer(false, org.researchstack.backbone.R.string.rsb_invalid_answer_date_over);
        }*/
        if (hasChosenTime)
            return BodyAnswer.VALID;
        else
            return BodyAnswer.INVALID;
    }

    private void showDialog(final TextView tv, String savedTime) {
        ContextThemeWrapper contextWrapper = new ContextThemeWrapper(tv.getContext(),
                org.researchstack.backbone.R.style.Theme_Backbone);
        int hour = 0, minute = 0;

        if (hasChosenTime) {
            String[] hrs_min = time.getText().toString().split(":");
            Calendar calendar = Calendar.getInstance();
            if (hrs_min.length > 1) {
                calendar.set(Calendar.HOUR, Integer.parseInt(hrs_min[0]));
                calendar.set(Calendar.MINUTE, Integer.parseInt(hrs_min[1]));
                hour = calendar.get(Calendar.HOUR_OF_DAY);
                minute = calendar.get(Calendar.MINUTE);
            }
        } else {
            Calendar mcurrentTime = Calendar.getInstance();
            hour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
            minute = mcurrentTime.get(Calendar.MINUTE);
        }
        TimePickerDialog mTimePicker;
        mTimePicker = new TimePickerDialog(contextWrapper, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                if (selectedHour < 10 && selectedMinute < 10)
                    tv.setText("0" + selectedHour + ":0" + selectedMinute + ":00");
                else if (selectedHour < 10)
                    tv.setText("0" + selectedHour + ":" + selectedMinute + ":00");
                else if (selectedMinute < 10)
                    tv.setText(selectedHour + ":0" + selectedMinute + ":00");
                else
                    tv.setText("" + selectedHour + ":" + selectedMinute + ":00");
                hasChosenTime = true;
            }
        }, hour, minute, true);
        mTimePicker.setTitle(contextWrapper.getString(R.string.setelct_time));
        mTimePicker.show();
    }
}
